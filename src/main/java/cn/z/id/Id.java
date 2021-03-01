package cn.z.id;

import cn.z.clock.Clock;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>高性能Id生成器</h1>
 *
 * <p>
 * createDate 2021/02/24 20:36:27
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class Id {

    /**
     * 日志实例
     */
    private final static Logger log = LoggerFactory.getLogger(Id.class);
    /**
     * 初始时间戳(如果发生回拨，这个值会减少)<br>
     * 1609459200000<br>
     * 格林尼治时间为2021-01-01 00:00:00 GMT+0<br>
     * 北京时间为2021-01-01 08:00:00 GMT+8
     **/
    private static long startTimestamp = 1609459200000L;
    /**
     * 上一次生成的时间戳
     */
    private static long lastTimestamp = -1L;
    /**
     * 序列号
     */
    private static long sequence = 0L;
    /**
     * 机器码(默认0)
     */
    private static long MACHINE_ID;
    /**
     * 机器码位数(默认8)
     **/
    private static long MACHINE_BITS;
    /**
     * 序列号位数(默认12)
     **/
    private static long SEQUENCE_BITS;
    /**
     * 最大机器码
     **/
    private static long MACHINE_MAX;
    /**
     * 最大序列号
     **/
    private static long SEQUENCE_MAX;
    /**
     * 机器码左移量
     */
    private static long MACHINE_LEFT_SHIFT;
    /**
     * 时间戳左移量(其中二进制头部占1位为0来保证生成的id是正数)
     */
    private static long TIMESTAMP_LEFT_SHIFT;

    static {
        initialization(0, 8, 12);
        log.info("自动初始化，MACHINE_ID为{}，MACHINE_BITS为{}，SEQUENCE_BITS为{}", MACHINE_ID, MACHINE_BITS, SEQUENCE_BITS);
        available();
    }

    /**
     * 禁止构造
     */
    private Id() {

    }

    /**
     * 初始化
     *
     * @param machineId    机器码
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     */
    private static void initialization(long machineId, long machineBits, long sequenceBits) {
        MACHINE_ID = machineId;
        MACHINE_BITS = machineBits;
        SEQUENCE_BITS = sequenceBits;
        MACHINE_MAX = ~(-1L << MACHINE_BITS);
        SEQUENCE_MAX = ~(-1L << SEQUENCE_BITS);
        MACHINE_LEFT_SHIFT = SEQUENCE_BITS;
        TIMESTAMP_LEFT_SHIFT = (MACHINE_BITS + SEQUENCE_BITS == 0 ? 0 : MACHINE_BITS + SEQUENCE_BITS - 1);
    }

    /**
     * 手动初始化
     *
     * @param machineId    机器码
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     */
    public static synchronized void init(long machineId, long machineBits, long sequenceBits) {
        if (lastTimestamp == -1) {
            synchronized (Id.class) {
                if (lastTimestamp == -1) {
                    lastTimestamp = -2;
                    initialization(machineId, machineBits, sequenceBits);
                    log.info("手动初始化，MACHINE_ID为{}，MACHINE_BITS为{}，SEQUENCE_BITS为{}", MACHINE_ID, MACHINE_BITS,
                            SEQUENCE_BITS);
                    valid();
                    available();
                } else {
                    log.warn("已经初始化过了，不可重复初始化！");
                }
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 判断取值是否合理
     */
    private static void valid() {
        // 机器码
        if (MACHINE_ID > MACHINE_MAX || MACHINE_ID < 0) {
            log.error("机器码MACHINE_ID需要>=0并且<=" + MACHINE_MAX + "。当前为" + MACHINE_ID, new IllegalArgumentException());
        }
        // 机器码位数
        if (MACHINE_BITS < 0 || MACHINE_BITS > 64) {
            log.error("机器码位数MACHINE_BITS需要>=0并且<=64。当前为" + MACHINE_BITS, new IllegalArgumentException());
        }
        // 序列号位数
        if (SEQUENCE_BITS < 0 || SEQUENCE_BITS > 64) {
            log.error("序列号位数SEQUENCE_BITS需要>=0并且<=64。当前为" + SEQUENCE_BITS, new IllegalArgumentException());
        }
    }

    /**
     * 可用性检查
     */
    private static void available() {
        long TIMESTAMP_MAX = ~(-1L << (64 - TIMESTAMP_LEFT_SHIFT));
        log.info("最大机器码为{}，1ms内最大序列号为{}，最早时钟回拨到{}，可使用时间为{}，失效日期为{}", MACHINE_MAX, SEQUENCE_MAX,
                new Timestamp(startTimestamp), new Timestamp(TIMESTAMP_MAX),
                new Timestamp(startTimestamp + TIMESTAMP_MAX));
    }

    /**
     * 获取下一个序列号
     */
    public static synchronized long next() {
        // 当前时间戳
        long currentTimestamp = Clock.now();
        // 判断当前时间戳 和 上一次时间戳的大小关系
        if (lastTimestamp == currentTimestamp) {
            /* 同一毫秒 */
            // 序列号自增
            sequence += 1;
            // 判断是否大于最大序列号
            if (sequence > SEQUENCE_MAX) {
                log.warn("检测到阻塞，时间戳为{}，最大序列号为{}。请考虑增加SEQUENCE_BITS。", currentTimestamp, SEQUENCE_MAX - 1);
                /* 阻塞当前这一毫秒 */
                while (lastTimestamp == currentTimestamp) {
                    currentTimestamp = Clock.now();
                }
                /* 阻塞结束后 */
                // 序列号归零
                sequence = 0;
                // 更新上一个时间戳 为 当前时间戳
                lastTimestamp = currentTimestamp;
            }
        } else if (lastTimestamp < currentTimestamp) {
            /* 当前时间戳增加了 */
            // 序列号归零
            sequence = 0;
            // 更新上一个时间戳 为 当前时间戳
            lastTimestamp = currentTimestamp;
        } else {
            /* 时间回拨(当前时间戳减少了) */
            log.warn("监测到系统时钟发生了回拨。时间为{}，上一个生成的时间为{}。", new Timestamp(currentTimestamp), new Timestamp(lastTimestamp));
            // 修改初始时间戳 为 初始时间戳-(上一个时间戳-当前时间戳+1)
            startTimestamp -= (lastTimestamp - currentTimestamp + 1);
            // 序列号归零
            sequence = 0;
            // 更新上一个时间戳 为 当前时间戳
            lastTimestamp = currentTimestamp;
        }
        // 返回 按位或 后的数值
        return ((currentTimestamp - startTimestamp) << TIMESTAMP_LEFT_SHIFT) // 时间戳的差
                | (MACHINE_ID << MACHINE_LEFT_SHIFT) // 机器码
                | sequence; // 序列号
    }

}