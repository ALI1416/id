package cn.z.id;

import cn.z.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom;

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
    private static final long INITIAL_TIMESTAMP = 1609459200000L;
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
     * 时间戳差左移量
     */
    private static long DIFFERENCE_OF_TIMESTAMP_LEFT_SHIFT;

    static {
        initInner("预初始化", 0, 8, 12);
    }

    private Id() {
    }

    /**
     * 内部初始化
     *
     * @param msg          消息
     * @param machineId    机器码
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     */
    private static void initInner(String msg, long machineId, long machineBits, long sequenceBits) {
        MACHINE_ID = machineId;
        MACHINE_BITS = machineBits;
        SEQUENCE_BITS = sequenceBits;
        MACHINE_MAX = ~(-1L << MACHINE_BITS);
        SEQUENCE_MAX = ~(-1L << SEQUENCE_BITS);
        MACHINE_LEFT_SHIFT = SEQUENCE_BITS;
        DIFFERENCE_OF_TIMESTAMP_LEFT_SHIFT = MACHINE_BITS + SEQUENCE_BITS;
        // 最大时间戳差
        long differenceOfTimestampMax = ~(-1L << (63 - DIFFERENCE_OF_TIMESTAMP_LEFT_SHIFT));
        log.info("高性能雪花ID生成器{}：机器码MACHINE_ID {} ，机器码位数MACHINE_BITS {} ，序列号位数SEQUENCE_BITS {} ，最大机器码MACHINE_ID {} ；1ms最多生成ID {} 个，起始时间 {} ，失效时间 {} ，大约可使用 {} 年",
                msg, MACHINE_ID, MACHINE_BITS, SEQUENCE_BITS, MACHINE_MAX, SEQUENCE_MAX + 1,
                new Timestamp(startTimestamp), new Timestamp(startTimestamp + differenceOfTimestampMax), differenceOfTimestampMax / (365 * 24 * 60 * 60 * 1000L));
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
                    initInner("初始化", machineId, machineBits, sequenceBits);
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
                // "序列号"归零
                sequence = getRandomInitSequence();
                // 更新"上一个时间戳"为"当前时间戳"
                lastTimestamp = currentTimestamp;
            }
        } else if (lastTimestamp < currentTimestamp) {
            /* 当前时间戳增加了 */
            // "序列号"归零
            sequence = getRandomInitSequence();
            // 更新"上一个时间戳"为"当前时间戳"
            lastTimestamp = currentTimestamp;
        } else {
            /* 时间回拨(当前时间戳减少了) */
            log.warn("监测到系统时钟发生了回拨。回拨时间 {} ，上一个生成的时间 {}", new Timestamp(currentTimestamp), new Timestamp(lastTimestamp));
            // 修改"开始时间戳"为"开始时间戳"-(上一个时间戳-当前时间戳+1)
            startTimestamp -= (lastTimestamp - currentTimestamp + 1);
            // "序列号"归零
            sequence = getRandomInitSequence();
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

    /**
     * 获取随机初始序列号<br>
     * 低频率使用下，序列号一直都是0，会导致id都为偶数<br>
     * 当id作为分库分表的分片键时会出现严重的数据倾斜问题
     *
     * @return 随机初始序列号
     * @since 2.8.0
     */
    private static long getRandomInitSequence() {
        return SEQUENCE_MAX == 0 ? 0 : ThreadLocalRandom.current().nextLong(2);
    }

    /**
     * 获取配置参数
     *
     * @return [0] machineId 机器码<br>
     * [1] machineBits 机器码位数<br>
     * [2] sequenceBits 序列号位数
     * @since 3.0.0
     */
    public static long[] param() {
        return new long[]{MACHINE_ID, MACHINE_BITS, SEQUENCE_BITS};
    }

    /**
     * 构造id
     *
     * @param machineId    机器码
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     * @param timestamp    时间戳
     * @param sequence     序列号
     * @return id
     * @since 3.0.0
     */
    public static long format(long machineId, long machineBits, long sequenceBits, long timestamp, long sequence) {
        return ((timestamp - INITIAL_TIMESTAMP) << (machineBits + sequenceBits)) // 时间戳的差
                | (machineId << sequenceBits) // 机器码
                | sequence; // 序列号
    }

    /**
     * 根据配置参数构造id
     *
     * @param timestamp 时间戳
     * @param sequence  序列号
     * @return id
     * @since 3.0.0
     */
    public static long format(long timestamp, long sequence) {
        return format(MACHINE_ID, MACHINE_BITS, SEQUENCE_BITS, timestamp, sequence);
    }

    /**
     * 根据配置参数构造id(序列号为0)
     *
     * @param timestamp 时间戳
     * @return id
     * @since 3.0.0
     */
    public static long format(long timestamp) {
        return format(MACHINE_ID, MACHINE_BITS, SEQUENCE_BITS, timestamp, 0L);
    }

    /**
     * 解析id
     *
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     * @param id           id
     * @return [0] timestamp 时间戳<br>
     * [1] machineId 机器码<br>
     * [2] sequence  序列号
     * @since 3.0.0
     */
    public static long[] parse(long machineBits, long sequenceBits, long id) {
        return new long[]{ //
                (id >> (machineBits + sequenceBits)) + INITIAL_TIMESTAMP, // 时间戳
                (id >> sequenceBits) & (~(-1L << machineBits)), // 机器码
                id & (~(-1L << sequenceBits)) // 序列号
        };
    }

    /**
     * 根据配置参数解析id
     *
     * @param id id
     * @return [0] timestamp 时间戳<br>
     * [1] machineId 机器码<br>
     * [2] sequence  序列号
     * @since 3.0.0
     */
    public static long[] parse(long id) {
        return parse(MACHINE_BITS, SEQUENCE_BITS, id);
    }

    /**
     * 获取id的时间戳
     *
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     * @param id           id
     * @return 时间戳
     * @since 3.1.0
     */
    public static long timestamp(long machineBits, long sequenceBits, long id) {
        return (id >> (machineBits + sequenceBits)) + INITIAL_TIMESTAMP;
    }

    /**
     * 根据配置参数获取id的时间戳
     *
     * @param id id
     * @return 时间戳
     * @since 3.1.0
     */
    public static long timestamp(long id) {
        return timestamp(MACHINE_BITS, SEQUENCE_BITS, id);
    }

}
