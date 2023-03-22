package cn.z.id;

import cn.z.clock.Clock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * <h1>高性能雪花Id生成器测试</h1>
 *
 * <p>
 * createDate 2021/02/25 08:52:28
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
class IdTest {

    /**
     * 直接调用
     */
    // @Test
    void test00Normal() {
        log.info("ID为：{}", Id.next());
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.IdTest - ID为：5483442415337472
    }

    /**
     * 手动初始化
     */
    // @Test
    void test01Init() {
        Id.init(0, 8, 14);
        log.info("ID为：{}", Id.next());
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.Id - 手动初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为14
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为16384，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为69年，失效日期为2090-09-07 23:47:35.551
        // [main] INFO cn.z.id.IdTest - ID为：21934128022683648
    }

    /**
     * 初始化多次
     */
    // @Test
    void test02InitMore() {
        Id.init(0, 8, 13);
        Id.init(0, 8, 15);
        log.info("ID为：{}", Id.next());
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.Id - 手动初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为13
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为8192，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为139年，失效日期为2160-05-15 15:35:11.103
        // [main] WARN cn.z.id.Id - 已经初始化过了，不可重复初始化！
        // [main] INFO cn.z.id.IdTest - ID为：10967292061941760
    }

    /**
     * 初始化晚了
     */
    // @Test
    void test03InitLate() {
        log.info("ID为：{}", Id.next());
        Id.init(0, 8, 12);
        log.info("ID为：{}", Id.next());
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] WARN cn.z.id.Id - 已经初始化过了，不可重复初始化！
        // [main] INFO cn.z.id.IdTest - ID为：5483684734959616
        // [main] INFO cn.z.id.IdTest - ID为：5483684734959617
    }

    /**
     * 初始化异常
     */
    // @Test
    void test04InitException() {
        Id.init(1000, 8, 12);
        log.info("ID为：{}", Id.next());
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.Id - 手动初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为1000，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] ERROR cn.z.id.Id - 机器码MACHINE_ID需要>=0并且<=255。当前为1000
        // java.lang.Exception: 机器码无效
        // [main] ERROR cn.z.id.Id - 重置初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.IdTest - ID为：5483719912587264
    }

    /**
     * 阻塞
     */
    // @Test
    void test05Block() {
        // 初始化，复现阻塞
        Id.init(0, 0, 0);
        log.info("ID为：{}", Id.next());
        log.info("ID为：{}", Id.next());
        log.info("ID为：{}", Id.next());
        log.info("ID为：{}", Id.next());
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.Id - 手动初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为0，SEQUENCE_BITS为0
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为0，1ms内最多生成Id数量为1，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为292471208年，失效日期为292269004-12-03 00:47:04.191
        // [main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.469，最大序列号为0
        // [main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.485，最大序列号为0
        // [main] INFO cn.z.id.IdTest - ID为：5229847469
        // [main] INFO cn.z.id.IdTest - ID为：5229847485
        // [main] INFO cn.z.id.IdTest - ID为：5229847500
        // [main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.5，最大序列号为0
        // [main] INFO cn.z.id.IdTest - ID为：5229847516
    }

    /**
     * 时钟回拨(需要在1分钟内手动回拨时钟)
     */
    // @Test
    void test06Back() {
        for (int i = 0; i < 60; i++) {
            log.info("ID为：{}", Id.next());
            try {
                Thread.sleep(1000);
            } catch (Exception ignore) {
                Thread.currentThread().interrupt();
            }
        }
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.IdTest - ID为：5483989976481792
        // [main] WARN cn.z.id.Id - 监测到系统时钟发生了回拨。回拨时间为2021-03-02 19:45:33.249，上一个生成的时间为2021-03-02 20:45:40.392
        // [main] INFO cn.z.id.IdTest - ID为：5483989977530368
    }

    /**
     * 重置初始时间戳(需要在1分钟内手动回拨时钟)
     */
    // @Test
    void test07Reset() {
        for (int i = 0; i < 60; i++) {
            log.info("ID为：{}", Id.next());
            try {
                Thread.sleep(1000);
            } catch (Exception ignore) {
                Thread.currentThread().interrupt();
            }
            log.info("总共回拨时间为：{}毫秒", Id.reset());
        }
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.IdTest - ID为：23564520900263936
        // [main] INFO cn.z.id.Id - 重置初始时间戳，时钟总共回拨0毫秒
        // [main] INFO cn.z.id.IdTest - 总共回拨时间为：0毫秒
        // [main] WARN cn.z.id.Id - 监测到系统时钟发生了回拨。回拨时间为2021-09-18 10:25:55.498，上一个生成的时间为2021-09-18 10:27:58.361
        // [main] INFO cn.z.id.IdTest - ID为：23564520901312512
        // [main] INFO cn.z.id.Id - 重置初始时间戳，时钟总共回拨122864毫秒
        // [main] INFO cn.z.id.IdTest - 总共回拨时间为：122864毫秒
        // [main] INFO cn.z.id.IdTest - ID为：23564393127084032
    }

    /**
     * 比较
     */
    // @Test
    void test08Compare() {
        // 初始化，避免阻塞
        Id.init(0, 0, 26);
        // [main] INFO cn.z.id.Id - 预初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
        // [main] INFO cn.z.id.Id - 手动初始化...
        // [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为0，SEQUENCE_BITS为28
        // [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为0，1ms内最多生成Id数量为268435456，时钟最早回拨到2021-01-01 08:00:00
        // .0，可使用时间大约为1年，失效日期为2022-02-03 00:22:18.367
        /*100万次*/
        compare(1000000);
        // [main] INFO cn.z.id.IdTest - 高性能雪花Id生成器调用1000000次使用时间为：47毫秒
        // [main] INFO cn.z.id.IdTest - Random调用1000000次使用时间为：15毫秒
        // [main] INFO cn.z.id.IdTest - UUID调用1000000次使用时间为：1175毫秒
        // [main] INFO cn.z.id.IdTest - 调用1000000次，高性能雪花Id生成器比UUID性能高25.0倍
        /*1000万次*/
        compare(10000000);
        // [main] INFO cn.z.id.IdTest - 高性能雪花Id生成器调用10000000次使用时间为：227毫秒
        // [main] INFO cn.z.id.IdTest - Random调用10000000次使用时间为：173毫秒
        // [main] INFO cn.z.id.IdTest - UUID调用10000000次使用时间为：8853毫秒
        // [main] INFO cn.z.id.IdTest - 调用10000000次，高性能雪花Id生成器比UUID性能高39.0倍
        /*1亿次*/
        compare(100000000);
        // [main] INFO cn.z.id.IdTest - 高性能雪花Id生成器调用100000000次使用时间为：909毫秒
        // [main] INFO cn.z.id.IdTest - Random调用100000000次使用时间为：793毫秒
        // [main] INFO cn.z.id.IdTest - UUID调用100000000次使用时间为：83628毫秒
        // [main] INFO cn.z.id.IdTest - 调用100000000次，高性能雪花Id生成器比UUID性能高92.0倍
        // /*21亿次*/
        compare(Integer.MAX_VALUE);
        // [main] INFO cn.z.id.IdTest - 高性能雪花Id生成器调用2147483647次使用时间为：37871毫秒
        // [main] INFO cn.z.id.IdTest - Random调用2147483647次使用时间为：36886毫秒
        // [main] INFO cn.z.id.IdTest - UUID调用2147483647次使用时间为：7915039毫秒
        // [main] INFO cn.z.id.IdTest - 调用2147483647次，高性能雪花Id生成器比UUID性能高209.0倍
    }

    /**
     * 比较
     *
     * @param count 次数
     */
    void compare(int count) {
        /* 初始化 */
        Random random = new Random();
        Id.next();
        random.nextLong();
        UUID.randomUUID();
        /* 高性能雪花Id */
        long a = Clock.now();
        for (int i = 0; i < count; i++) {
            Id.next();
        }
        long b = Clock.now();
        long ba = b - a;
        log.info("高性能雪花Id生成器调用{}次使用时间为：{}毫秒", count, ba);
        /* Random */
        long c = Clock.now();
        for (int i = 0; i < count; i++) {
            random.nextLong();
        }
        long d = Clock.now();
        long dc = d - c;
        log.info("Random调用{}次使用时间为：{}毫秒", count, dc);
        /* UUID */
        long e = Clock.now();
        for (int i = 0; i < count; i++) {
            UUID.randomUUID();
        }
        long f = Clock.now();
        long fe = f - e;
        log.info("UUID调用{}次使用时间为：{}毫秒", count, fe);
        /* 比较 */
        log.info("调用{}次，高性能雪花Id生成器比UUID性能高{}倍", count, fe / (double) ba);
    }

    /**
     * 工具
     */
    @Test
    void test09Util() {
        Id.init(3, 4, 5);
        // 获取配置参数
        log.info(Arrays.toString(IdUtil.param())); // [3, 4, 5]
        long id = Id.next();
        log.info(String.valueOf(id)); // 20448571222112
        // 根据配置参数解析id
        long[] parse = IdUtil.parse(id);
        log.info(Arrays.toString(parse)); // [1649397815668, 3, 0]
        // 根据配置参数构造id(序列号为0)
        log.info(String.valueOf(IdUtil.format(parse[0]))); // 20448571222112
        // 根据配置参数构造id
        log.info(String.valueOf(IdUtil.format(parse[0], 1L))); // 20448571222113
        // 解析id
        long[] parse2 = IdUtil.parse(8L, 12L, id);
        log.info(Arrays.toString(parse2)); // [1609478701277, 46, 2144]
        // 构造id
        log.info(String.valueOf(IdUtil.format(parse2[1], 8L, 12L, parse2[0], parse2[2]))); // 20448571222112
    }

}
