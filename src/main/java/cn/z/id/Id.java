package cn.z.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.z.Clock;

/**
 * <h1>高性能Id生成器</h1>
 *
 * <p>
 * createDate 2020/12/23 15:11:35
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
    private static long MACHINE_ID = 0;
    /**
     * 机器码位数(默认8)
     **/
    private static long MACHINE_BITS = 8;
    /**
     * 序列号位数(默认14)
     **/
    private static long SEQUENCE_BITS = 14;
    /**
     * 最大机器码
     **/
    private final static long MACHINE_MAX = ~(-1L << MACHINE_BITS);
    /**
     * 最大序列号
     **/
    private final static long SEQUENCE_MAX = ~(-1L << SEQUENCE_BITS);
    /**
     * 机器码左移量
     */
    private final static long MACHINE_LEFT_SHIFT = SEQUENCE_BITS;
    /**
     * 时间戳左移量(其中二进制头部占1位为0来保证生成的id是正数)
     */
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_BITS - 1;

    /**
     * 禁止构造
     */
    private Id() {

    }

    /**
     * 判断取值是否合理
     */
    private static void vaild() {
        // 机器码位数过大或过小
        if (MACHINE_BITS < 0 || MACHINE_BITS > 64) {
            log.error("机器码位数MACHINE_BITS需要>=0并且<=64。当前为" + MACHINE_BITS, new IllegalArgumentException());
        }
        // 序列号位数过大或过小
        if (SEQUENCE_BITS < 0 || SEQUENCE_BITS > 64) {
            log.error("序列号位数SEQUENCE_BITS需要>=0并且<=64。当前为" + SEQUENCE_BITS, new IllegalArgumentException());
        }
        // 机器码过大或过小
        if (MACHINE_ID > MACHINE_MAX || MACHINE_ID < 0) {
            log.error("机器码MACHINE_ID需要>=0并且<=" + MACHINE_MAX + "。当前为" + MACHINE_ID, new IllegalArgumentException());
        }
        // 时间戳位数过小(需要留给时间戳35位才能使用1年，其中二进制头部占1位为0来保证生成的id是正数)
        // 28 = 64 - 35 - 1
        if (SEQUENCE_BITS + MACHINE_BITS > 28) {
            log.error("时间戳分配的位数过小，需要SEQUENCE_BITS+MACHINE_BITS<=28。当前为" + (SEQUENCE_BITS + MACHINE_BITS),
                    new IllegalArgumentException());
        }
    }

    /**
     * 初始化
     * 
     * @param machineId    机器码
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     */
    public static void init(long machineId, long machineBits, long sequenceBits) {
        MACHINE_ID = machineId;
        MACHINE_BITS = machineBits;
        SEQUENCE_BITS = sequenceBits;
        vaild();
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
                log.warn("检测到阻塞，时间戳为{}，最大序列号为{}。请考虑增加SEQUENCE_BITS。", currentTimestamp, sequence - 1);
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
            log.warn("监测到系统时钟发生了回拨。时间戳为{}，上一个生成的时间戳为{}。", currentTimestamp, lastTimestamp);
            // 修改初始时间戳 为 初始时间戳-(上一个时间戳-当前时间戳+1)
            startTimestamp -= (lastTimestamp - currentTimestamp + 1);
            // 序列号归零
            sequence = 0;
            // 更新上一个时间戳 为 当前时间戳
            lastTimestamp = currentTimestamp;
        }
        // 返回按位拼接后的数值
        return ((currentTimestamp - startTimestamp) << TIMESTAMP_LEFT_SHIFT) // 时间戳的差
                | (MACHINE_ID << MACHINE_LEFT_SHIFT)// 机器码
                | sequence;// 序列号
    }

}