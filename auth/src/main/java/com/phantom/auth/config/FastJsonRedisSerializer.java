package com.phantom.auth.config;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import org.springframework.data.redis.serializer.RedisSerializer;


/**
 * redis 序列化配置
 *
 * @author lei.tan
 * @version 1.0
 * @date 2022/9/6 17:41
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {


    /**
     * 按需加上需要支持自动类型的类名前缀，范围越小越安全
     */
    static final Filter AUTO_TYPE_FILTER = JSONReader.autoTypeFilter(
            "org.springframework.", "com.phantom."
    );

    private final Class<T> clazz;

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONBytes(t, JSONWriter.Feature.WriteClassName);
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return JSON.parseObject(bytes, clazz, AUTO_TYPE_FILTER);
    }


}
