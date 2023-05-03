package com.phantom.auth.config;

import cn.hutool.core.lang.Singleton;

/**
 * <p>雪花算法工具类</p>
 * <p>雪花算法是由 Twitter 公司开源的可在分布式系统中产生一个全局唯一 ID 的算法。最初 Twitter 把存储系统从 MySQL 迁移到 Cassandra，
 * 因为 Cassandra 没有顺序ID生成机制，所以开发了这样一套全局唯一ID生成服务。</p>
 * <p>SnowFlake 算法生成的 ID 是一个 64 位的整数，它的结构如下图所示：</p>
 * <a href="https://img2022.cnblogs.com/blog/1734261/202202/1734261-20220210190314569-21637841.png">🔗</a>
 * <ul>
 *     <li>第一部分：1bit 符号位，由于都是生成 ID 都是正数，所以第一位统一为0；</li>
 *     <li>第二部分：41 bit 时间戳，单位是毫秒，41 位可以表示的数字多达 2^41 - 1，换算成年就是 69 年；</li>
 *     <li>第三部分：5 bit 机房 ID，代表最多支持 32 个机房；</li>
 *     <li>第四部分：5 bit 机器 ID，代表每个机房最多支持 32 台机器；</li>
 *     <li>第五部分：12 bit，记录同一时间（毫秒）内产生的不同 id，也就是说同一毫秒内可以产生4096个不同 id。</li>
 * </ul>
 * <p>SnowFlake 生成的 ID 整体上按照时间自增排序，并且整个分布式系统不会产生 ID 碰撞（由 DataCenterID 和 WorkerID 区分），并且效率较高</p>
 *
 * <p>在实现 SnowFlake 基本功能的基础上，增加部分拓展功能：</p>
 * <p>
 * 定义开始时间戳，默认为 2020/01/01 08:00:00，如果使用默认的时间戳位数，那么该程序生成 ID 大概可以使用到 2089 年；
 * 机房 ID 、机器 ID 和 序列 ID 三个数据段长度可以自定义，通过构造函数传入。
 * </p>
 * 方法介绍:
 * <ul>
 *     <li>timeGen: 生成当前毫秒时间戳，相对于 2020年1月1日 8:00:00, 返回值: 当前毫秒时间戳</li>
 *     <li>tilNextMillis: 阻塞直到下一毫秒, 返回值: 下一毫秒时间戳</li>
 *     <li>nextId: 生成一个新的 ID, 返回值: 新ID</li>
 * </ul>
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/2/9 11:37
 */
public class SnowFlakeUtil {

    private static final long START_STAMP = 1420041600000L;
    private static final long SEQUENCE_BIT = 9L;
    private static final long MACHINE_BIT = 2L;
    private static final long DATACENTER_BIT = 2L;
    private static final long MAX_SEQUENCE = 511L;
    private static final long MAX_MACHINE_NUM = 3L;
    private static final long MAX_DATACENTER_NUM = 3L;
    private static final long MACHINE_LEFT = 9L;
    private static final long DATACENTER_LEFT = 11L;
    private static final long TIMESTAMP_LEFT = 13L;
    private final long datacenterId;
    private final long machineId;
    private long sequence = 0L;
    private long lastStamp = -1L;

    public SnowFlakeUtil(long datacenterId, long machineId) {
        if (datacenterId <= 3L && datacenterId >= 0L) {
            if (machineId <= 3L && machineId >= 0L) {
                this.datacenterId = datacenterId;
                this.machineId = machineId;
            } else {
                throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
            }
        } else {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
    }

    public synchronized long nextId() {
        long currStmp = this.getNowMillis();
        if (currStmp < this.lastStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        } else {
            if (currStmp == this.lastStamp) {
                this.sequence = this.sequence + 1L & 511L;
                if (this.sequence == 0L) {
                    currStmp = this.getNextMill();
                }
            } else {
                this.sequence = 0L;
            }

            this.lastStamp = currStmp;
            return currStmp - 1420041600000L << 13 | this.datacenterId << 11 | this.machineId << 9 | this.sequence;
        }
    }

    private long getNextMill() {
        long mill;
        for (mill = this.getNowMillis(); mill <= this.lastStamp; mill = this.getNowMillis()) {
        }

        return mill;
    }

    private long getNowMillis() {
        return System.currentTimeMillis();
    }

    public static Long getDefaultSnowFlakeId() {
        return Singleton.get(SnowFlakeUtil.class, 1L, 1L).nextId();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; ++i) {
            System.out.println(getDefaultSnowFlakeId());
            System.out.println(getDefaultSnowFlakeId().toString().length());
        }

    }

}
