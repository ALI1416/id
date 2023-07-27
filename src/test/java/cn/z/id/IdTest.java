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
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.IdTest -- ID 84916544812875776
    }

    /**
     * 手动初始化
     */
    // @Test
    void test01Init() {
        Id.init(0, 8, 14);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.Id -- 手动初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 14
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 16384 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 69 年，失效时间 2090-09-07 23:47:35.551
        // INFO cn.z.id.IdTest -- ID 339667266901639168
    }

    /**
     * 初始化多次
     */
    // @Test
    void test02InitMore() {
        Id.init(0, 8, 13);
        Id.init(0, 8, 15);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.Id -- 手动初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 13
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 8192 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 139 年，失效时间 2160-05-15 15:35:11.103
        // WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
        // INFO cn.z.id.IdTest -- ID 169833687997743104
    }

    /**
     * 初始化晚了
     */
    // @Test
    void test03InitLate() {
        log.info("ID {}", Id.next());
        Id.init(0, 8, 12);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.IdTest -- ID 84916883292160000
        // WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
        // INFO cn.z.id.IdTest -- ID 84916883292160001
    }

    /**
     * 初始化异常
     */
    // @Test
    void test04InitException() {
        Id.init(1000, 8, 12);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.Id -- 手动初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 1000 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // cn.z.id.IdException: 机器码MACHINE_ID 1000 无效！应为 [0,255]
    }

    /**
     * 阻塞
     */
    // @Test
    void test05Block() {
        // 初始化，复现阻塞
        Id.init(0, 0, 0);
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.Id -- 手动初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 0 ，序列号位数SEQUENCE_BITS 0
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 0 ，1ms内最多生成ID数量 1 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 292471208 年，失效时间 292269004-12-03 00:47:04.191
        // INFO cn.z.id.IdTest -- ID 80983144609
        // WARN cn.z.id.Id -- 检测到阻塞，时间 2023-07-27 15:19:04.609 ，最大序列号 0
        // INFO cn.z.id.IdTest -- ID 80983144625
        // WARN cn.z.id.Id -- 检测到阻塞，时间 2023-07-27 15:19:04.625 ，最大序列号 0
        // INFO cn.z.id.IdTest -- ID 80983144626
        // WARN cn.z.id.Id -- 检测到阻塞，时间 2023-07-27 15:19:04.626 ，最大序列号 0
        // INFO cn.z.id.IdTest -- ID 80983144641
    }

    /**
     * 时钟回拨(需要在1分钟内手动回拨时钟)
     */
    // @Test
    void test06Back() {
        for (int i = 0; i < 60; i++) {
            log.info("ID {}", Id.next());
            try {
                Thread.sleep(1000);
            } catch (Exception ignore) {
                Thread.currentThread().interrupt();
            }
        }
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.IdTest -- ID 84917057323270144
        // INFO cn.z.id.IdTest -- ID 84917058381283328
        // WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2023-07-27 15:17:59.316 ，上一个生成的时间 2023-07-27 15:20:17.603
        // INFO cn.z.id.IdTest -- ID 84917058382331904
        // INFO cn.z.id.IdTest -- ID 84917059429859328
    }

    /**
     * 重置初始时间戳(需要在1分钟内手动回拨时钟)
     */
    // @Test
    void test07Reset() {
        for (int i = 0; i < 60; i++) {
            log.info("ID {}", Id.next());
            try {
                Thread.sleep(1000);
            } catch (Exception ignore) {
                Thread.currentThread().interrupt();
            }
            log.info("总共回拨时间为：{}毫秒", Id.reset());
        }
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.IdTest -- ID 84917178671824896
        // INFO cn.z.id.Id -- 重置开始时间戳，时钟总共回拨 0 毫秒
        // INFO cn.z.id.IdTest -- 总共回拨时间为：0毫秒
        // WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2023-07-27 15:20:05.313 ，上一个生成的时间 2023-07-27 15:22:15.352
        // INFO cn.z.id.IdTest -- ID 84917181851107328
        // INFO cn.z.id.Id -- 重置开始时间戳，时钟总共回拨 130040 毫秒
        // INFO cn.z.id.IdTest -- 总共回拨时间为：130040毫秒
    }

    /**
     * 比较
     */
    // @Test
    void test08Compare() {
        // 初始化，避免阻塞
        Id.init(0, 0, 26);
        // INFO cn.z.id.Id -- 预初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
        // INFO cn.z.id.Id -- 手动初始化...
        // INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 0 ，序列号位数SEQUENCE_BITS 26
        // INFO cn.z.id.Id -- 最大机器码MACHINE_ID 0 ，1ms内最多生成ID数量 67108864 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 4 年，失效时间 2025-05-11 01:29:13.471
        /*100万次*/
        compare(1000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用1000000次使用时间为：47毫秒
        // INFO cn.z.id.IdTest -- Random调用1000000次使用时间为：15毫秒
        // INFO cn.z.id.IdTest -- UUID调用1000000次使用时间为：1175毫秒
        // INFO cn.z.id.IdTest -- 调用1000000次，高性能雪花Id生成器比UUID性能高25.0倍
        /*1000万次*/
        compare(10000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用10000000次使用时间为：227毫秒
        // INFO cn.z.id.IdTest -- Random调用10000000次使用时间为：173毫秒
        // INFO cn.z.id.IdTest -- UUID调用10000000次使用时间为：8853毫秒
        // INFO cn.z.id.IdTest -- 调用10000000次，高性能雪花Id生成器比UUID性能高39.0倍
        /*1亿次*/
        compare(100000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用100000000次使用时间为：909毫秒
        // INFO cn.z.id.IdTest -- Random调用100000000次使用时间为：793毫秒
        // INFO cn.z.id.IdTest -- UUID调用100000000次使用时间为：83628毫秒
        // INFO cn.z.id.IdTest -- 调用100000000次，高性能雪花Id生成器比UUID性能高92.0倍
        /*21亿次*/
        compare(Integer.MAX_VALUE);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用2147483647次使用时间为：37871毫秒
        // INFO cn.z.id.IdTest -- Random调用2147483647次使用时间为：36886毫秒
        // INFO cn.z.id.IdTest -- UUID调用2147483647次使用时间为：7915039毫秒
        // INFO cn.z.id.IdTest -- 调用2147483647次，高性能雪花Id生成器比UUID性能高209.0倍
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
