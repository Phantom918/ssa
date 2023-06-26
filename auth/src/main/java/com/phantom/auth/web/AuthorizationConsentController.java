/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phantom.auth.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

/**
 * @author Daniel Garnier-Moiroux
 */
@Controller
public class AuthorizationConsentController {

    private final RegisteredClientRepository registeredClientRepository;

    private final OAuth2AuthorizationConsentService authorizationConsentService;

    public AuthorizationConsentController(RegisteredClientRepository registeredClientRepository,
                                          OAuth2AuthorizationConsentService authorizationConsentService) {
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationConsentService = authorizationConsentService;
    }

    /**
     * 自定义登录界面
     *
     * @return 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    /**
     * 退出登录
     * 注销策略:
     *  用户登录后，会在认证服务器和客户端都保存session信息。注销时需要把两个地方的都清除，包括安全上下文，仅清除客户端或认证服务器是不彻底的。
     *
     * security的退出操作是 [/logout], 但是这里的退出，仅仅清除了客户端的登录信息。在认证服务器中，用户还是登录状态。浏览器不关闭时，客户端与认证服务器间的JSESSIONID是不变的。
     * 用不变的JSESSIONID，向认证服务器发起请求，认证服务器中用户是登录状态，保存有与JSESSIONID对应的信息，这时会直接返回用户请求的信息，当然就不会再登录/授权了。
     *
     * @param request
     */
    @GetMapping("/out")
    public String logout(HttpServletRequest request) {
        // ========== 清理客户端 ===========
        // 清理客户端session
        request.getSession().invalidate();
        // 清理客户端安全上下文
        SecurityContextHolder.clearContext();
        // ========== 清理认证中心 ===========
        // 跳转至认证中心退出页面
        // 重定向: 使用 "redirect"，注意：类的注解不能使用@RestController，要用@Controller, 因为@RestController内含@ResponseBody，解析返回的是json串。不是跳转页面
        // 请求转发: 使用 "forward"，注意：类的注解不能使用@RestController 要用@Controller
        return "redirect:/logout";
    }

    /**
     * 自定义授权界面数据准备
     *
     * @param principal
     * @param model
     * @param clientId
     * @param scope
     * @param state
     * @return
     */
    @GetMapping(value = "/oauth2/consent")
    public String consent(Principal principal, Model model,
                          @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                          @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                          @RequestParam(OAuth2ParameterNames.STATE) String state) {

        // Remove scopes that were already approved
        Set<String> scopesToApprove = new HashSet<>();
        Set<String> previouslyApprovedScopes = new HashSet<>();
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        assert registeredClient != null;
        OAuth2AuthorizationConsent currentAuthorizationConsent = this.authorizationConsentService.findById(registeredClient.getId(), principal.getName());
        Set<String> authorizedScopes;
        if (currentAuthorizationConsent != null) {
            authorizedScopes = currentAuthorizationConsent.getScopes();
        } else {
            authorizedScopes = Collections.emptySet();
        }
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (OidcScopes.OPENID.equals(requestedScope)) {
                continue;
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", withDescription(scopesToApprove));
        model.addAttribute("previouslyApprovedScopes", withDescription(previouslyApprovedScopes));
        model.addAttribute("principalName", principal.getName());

        return "consent";
    }

    private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
        Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
        for (String scope : scopes) {
            scopeWithDescriptions.add(new ScopeWithDescription(scope));

        }
        return scopeWithDescriptions;
    }

    public static class ScopeWithDescription {
        private static final String DEFAULT_DESCRIPTION = "UNKNOWN SCOPE - We cannot provide information about this permission, use caution when granting this.";
        private static final Map<String, String> scopeDescriptions = new HashMap<>();

        static {
            scopeDescriptions.put(OidcScopes.PROFILE, "此应用将能够读取您的个人资料信息");
            scopeDescriptions.put("message.read", "此应用将能够读取您的消息");
            scopeDescriptions.put("message.write", "此应用将能够添加新消息，同时也能编辑或者删除消息");
            scopeDescriptions.put("other.scope", "这是范围描述的另一个范围示例.");
        }

        public final String scope;
        public final String description;

        ScopeWithDescription(String scope) {
            this.scope = scope;
            this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
        }
    }
}
