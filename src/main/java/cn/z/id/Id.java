package cn.z.id;

import cn.z.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * <h1>高性能雪花ID生成器</h1>
 *
 * <p>雪花ID结构：0|时间戳的差|机器码|序列号</p>
 *
 * <p>例如：</p>
 *
 * <p>
 * 正数1位，时间戳差43位，机器码8位，序列号12位的二进制形式为<br>
 * 0|1111111 11111111 11111111 11111111<br>
 * 11111111 1111|1111 1111|1111 11111111<br>
 * <br>
 * 在有效时间戳差内，二进制形式为<br>
 * 00000000 00000000 00000|111 11111111<br>
 * 11111111 11111111 11111111 11111111<br>
 * 左移8+12=20位(机器码位数+序列号位数)变成<br>
 * 0|1111111 11111111 11111111 11111111<br>
 * 11111111 1111|0000 00000000 00000000<br>
 * <br>
 * 在最大机器码内，二进制形式为<br>
 * 00000000 00000000 00000000 00000000<br>
 * 00000000 00000000 00000000 11111111<br>
 * 左移12位(序列号位数)变成<br>
 * 00000000 00000000 00000000 00000000<br>
 * 00000000 0000|1111 1111|0000 00000000<br>
 * <br>
 * 在最大序列号内，二进制形式为<br>
 * 00000000 00000000 00000000 00000000<br>
 * 00000000 00000000 0000|1111 11111111<br>
 * <br>
 * 三者通过"位或"运算得到ID
 * </p>
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
    private static final Logger log = LoggerFactory.getLogger(Id.class);
    /**
     * 初始时间戳{@value}<br>
     * 格林尼治时间为2021-01-01 00:00:00 GMT+0<br>
     * 北京时间为2021-01-01 08:00:00 GMT+8
     **/
    protected static final long INITIAL_TIMESTAMP = 1609459200000L;
    /**
     * 开始时间戳(如果发生回拨，这个值会减少)
     **/
    private static long startTimestamp = INITIAL_TIMESTAMP;
    /**
     * 上一次生成的时间戳
     */
    private static volatile long lastTimestamp = -1L;
    /**
     * 序列号
     */
    private static long sequence = 0L;
    /**
     * 机器码(默认0)
     */
    protected static long MACHINE_ID;
    /**
     * 机器码位数(默认8)
     **/
    protected static long MACHINE_BITS;
    /**
     * 序列号位数(默认12)
     **/
    protected static long SEQUENCE_BITS;
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
     * 时间戳差左移量
     */
    private static long DIFFERENCE_OF_TIMESTAMP_LEFT_SHIFT;

    static {
        log.info("预初始化...");
        initInner(0, 8, 12);
    }

    private Id() {
    }

    /**
     * 内部初始化
     *
     * @param machineId    机器码
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     */
    private static void initInner(long machineId, long machineBits, long sequenceBits) {
        MACHINE_ID = machineId;
        MACHINE_BITS = machineBits;
        SEQUENCE_BITS = sequenceBits;
        MACHINE_MAX = ~(-1L << MACHINE_BITS);
        SEQUENCE_MAX = ~(-1L << SEQUENCE_BITS);
        MACHINE_LEFT_SHIFT = SEQUENCE_BITS;
        DIFFERENCE_OF_TIMESTAMP_LEFT_SHIFT = MACHINE_BITS + SEQUENCE_BITS;
        // 最大时间戳差
        long differenceOfTimestampMax = ~(-1L << (63 - DIFFERENCE_OF_TIMESTAMP_LEFT_SHIFT));
        log.info("机器码MACHINE_ID {} ，机器码位数MACHINE_BITS {} ，序列号位数SEQUENCE_BITS {}", MACHINE_ID, MACHINE_BITS, SEQUENCE_BITS);
        log.info("最大机器码MACHINE_ID {} ，1ms内最多生成ID数量 {} ，时钟最早回拨到 {} ，可使用时间大约 {} 年，失效时间 {}", MACHINE_MAX, SEQUENCE_MAX + 1,
                new Timestamp(startTimestamp), differenceOfTimestampMax / (365 * 24 * 60 * 60 * 1000L),
                new Timestamp(startTimestamp + differenceOfTimestampMax));
        long currentTimestamp = Clock.now();
        // 当前时间
        if (currentTimestamp - startTimestamp > differenceOfTimestampMax) {
            log.error("当前时间 {} 无效！应为 [{},{}]", new Timestamp(currentTimestamp), new Timestamp(0), new Timestamp(startTimestamp + differenceOfTimestampMax));
        }
        // 机器码
        if (MACHINE_ID > MACHINE_MAX || MACHINE_ID < 0) {
            throw new IdException("机器码MACHINE_ID " + MACHINE_ID + " 无效！应为 [0," + MACHINE_MAX + "]");
        }
        // 机器码位数
        if (MACHINE_BITS < 0 || MACHINE_BITS > 64) {
            throw new IdException("机器码位数MACHINE_BITS " + MACHINE_BITS + " 无效！应为 [0,64]");
        }
        // 序列号位数
        if (SEQUENCE_BITS < 0 || SEQUENCE_BITS > 64) {
            throw new IdException("序列号位数SEQUENCE_BITS " + SEQUENCE_BITS + " 无效！应为 [0,64]");
        }
    }

    /**
     * 初始化
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
                    log.info("初始化...");
                    initInner(machineId, machineBits, sequenceBits);
                } else {
                    log.warn("已经初始化过了，不可重复初始化！");
                }
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 获取下一个id
     *
     * @return id
     */
    public static synchronized long next() {
        // 当前时间戳
        long currentTimestamp = Clock.now();
        // 判断"当前时间戳"和"上一次时间戳"的大小关系
        if (lastTimestamp == currentTimestamp) {
            /* 同一毫秒 */
            // "序列号"自增
            sequence += 1;
            // 判断是否大于"最大序列号"
            if (sequence > SEQUENCE_MAX) {
                log.warn("检测到阻塞，时间 {} ，最大序列号 {}", new Timestamp(currentTimestamp), SEQUENCE_MAX);
                /* 阻塞当前这一毫秒 */
                while (lastTimestamp == currentTimestamp) {
                    currentTimestamp = Clock.now();
                }
                /* 阻塞结束后 */
                // 序列号归零
                sequence = 0;
                // 更新"上一个时间戳"为"当前时间戳"
                lastTimestamp = currentTimestamp;
            }
        } else if (lastTimestamp < currentTimestamp) {
            /* 当前时间戳增加了 */
            // "序列号"归零
            sequence = 0;
            // 更新"上一个时间戳"为"当前时间戳"
            lastTimestamp = currentTimestamp;
        } else {
            /* 时间回拨(当前时间戳减少了) */
            log.warn("监测到系统时钟发生了回拨。回拨时间 {} ，上一个生成的时间 {}", new Timestamp(currentTimestamp), new Timestamp(lastTimestamp));
            // 修改"开始时间戳"为"开始时间戳"-(上一个时间戳-当前时间戳+1)
            startTimestamp -= (lastTimestamp - currentTimestamp + 1);
            // "序列号"归零
            sequence = 0;
            // 更新"上一个时间戳"为"当前时间戳"
            lastTimestamp = currentTimestamp;
        }
        // 返回"位或"后的数值(ID)
        return ((currentTimestamp - startTimestamp) << DIFFERENCE_OF_TIMESTAMP_LEFT_SHIFT) // 时间戳的差
                | (MACHINE_ID << MACHINE_LEFT_SHIFT) // 机器码
                | sequence; // 序列号
    }

    /**
     * 重置开始时间戳<br>
     * 如果时钟有回拨，"开始时间戳"会减少，此操作可以使"开始时间戳"恢复到"初始时间戳"
     *
     * @return 时钟回拨的毫秒数
     * @since 2.3.0
     */
    public static long reset() {
        long difference = INITIAL_TIMESTAMP - startTimestamp;
        startTimestamp = INITIAL_TIMESTAMP;
        log.info("重置开始时间戳，时钟总共回拨 {} 毫秒", difference);
        return difference;
    }

}
