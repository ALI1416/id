package cn.z.id;

import static cn.z.id.Id.*;

/**
 * <h1>ID工具类</h1>
 *
 * <p>
 * createDate 2022/04/07 15:52:18
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 2.4.0
 **/
public class IdUtil {

    private IdUtil() {
    }

    /**
     * 获取配置参数
     *
     * @return long[]<br>
     * 0 machineId    机器码<br>
     * 1 machineBits  机器码位数<br>
     * 2 sequenceBits 序列号位数
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
     */
    public static long format(long machineId, long machineBits, long sequenceBits, long timestamp, long sequence) {
        return ((timestamp - INITIAL_TIMESTAMP) << (machineBits + sequenceBits)) // 时间戳的差
                | (machineId << sequenceBits) // 机器码
                | sequence; // 序列号
    }

    /**
     * 根据配置参数构造id(序列号为0)
     *
     * @param timestamp 时间戳
     * @return id
     */
    public static long format(long timestamp) {
        return format(MACHINE_ID, MACHINE_BITS, SEQUENCE_BITS, timestamp, 0L);
    }

    /**
     * 根据配置参数构造id
     *
     * @param timestamp 时间戳
     * @param sequence  序列号
     * @return id
     */
    public static long format(long timestamp, long sequence) {
        return format(MACHINE_ID, MACHINE_BITS, SEQUENCE_BITS, timestamp, sequence);
    }

    /**
     * 解析id
     *
     * @param machineBits  机器码位数
     * @param sequenceBits 序列号位数
     * @param id           id
     * @return long[]<br>
     * 0 timestamp 时间戳<br>
     * 1 machineId 机器码<br>
     * 2 sequence  序列号
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
     * @return long[]<br>
     * 0 timestamp 时间戳<br>
     * 1 machineId 机器码<br>
     * 2 sequence  序列号
     */
    public static long[] parse(long id) {
        return parse(MACHINE_BITS, SEQUENCE_BITS, id);
    }

}
