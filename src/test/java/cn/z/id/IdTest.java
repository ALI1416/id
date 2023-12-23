package cn.z.id;

import cn.z.clock.Clock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * <h1>高性能雪花ID生成器测试</h1>
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
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.IdTest -- ID 90441966306721793
    }

    /**
     * 手动初始化
     */
    // @Test
    void test01Init() {
        Id.init(0, 8, 14);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 14 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 16384 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2090-09-07 23:47:35.551 ，大约可使用 69 年
        // INFO cn.z.id.IdTest -- ID 361768291288481792
    }

    /**
     * 初始化多次
     */
    // @Test
    void test02InitMore() {
        Id.init(0, 8, 13);
        Id.init(0, 8, 15);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 13 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 8192 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2160-05-15 15:35:11.103 ，大约可使用 139 年
        // WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
        // INFO cn.z.id.IdTest -- ID 180884221449994241
    }

    /**
     * 初始化晚了
     */
    // @Test
    void test03InitLate() {
        log.info("ID {}", Id.next());
        Id.init(0, 8, 12);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.IdTest -- ID 90442134147039232
        // WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
        // INFO cn.z.id.IdTest -- ID 90442134147039233
    }

    /**
     * 初始化异常
     */
    // @Test
    void test04InitException() {
        Id.init(1000, 8, 12);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 1000 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // cn.z.id.IdException: 机器码MACHINE_ID 1000 无效！应为 [0,255]
    }

    /**
     * 阻塞
     */
    // @Test
    void test05Block() {
        // 使用较小的序列号位数初始化，可以复现阻塞
        Id.init(0, 0, 1);
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 0 ，序列号位数SEQUENCE_BITS 1 ，最大机器码MACHINE_ID 0 ；1ms最多生成ID 2 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 146140533-04-25 23:36:27.903 ，大约可使用 146235604 年
        // INFO cn.z.id.IdTest -- ID 172504939970
        // INFO cn.z.id.IdTest -- ID 172504939971
        // WARN cn.z.id.Id -- 检测到阻塞，时间 2023-09-26 15:01:09.985 ，最大序列号 1
        // INFO cn.z.id.IdTest -- ID 172504939981
        // WARN cn.z.id.Id -- 检测到阻塞，时间 2023-09-26 15:01:09.99 ，最大序列号 1
        // INFO cn.z.id.IdTest -- ID 172504940012
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
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.IdTest -- ID 90442379897602049
        // INFO cn.z.id.IdTest -- ID 90442380959809536
        // WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2023-09-26 14:02:32.663 ，上一个生成的时间 2023-09-26 15:02:55.836
        // INFO cn.z.id.IdTest -- ID 90442380960858113
        // INFO cn.z.id.IdTest -- ID 90442382019919872
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
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.IdTest -- ID 90442426204815360
        // INFO cn.z.id.Id -- 重置开始时间戳，时钟总共回拨 0 毫秒
        // INFO cn.z.id.IdTest -- 总共回拨时间为：0毫秒
        // WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2023-09-26 14:03:30.023 ，上一个生成的时间 2023-09-26 15:03:38.985
        // INFO cn.z.id.IdTest -- ID 90442426205863936
        // INFO cn.z.id.Id -- 重置开始时间戳，时钟总共回拨 3608963 毫秒
        // INFO cn.z.id.IdTest -- 总共回拨时间为：3608963毫秒
    }

    /**
     * 比较
     */
    // @Test
    void test08Compare() {
        // 使用较大的序列号位数初始化，避免阻塞
        Id.init(0, 0, 26);
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 0 ，序列号位数SEQUENCE_BITS 26 ，最大机器码MACHINE_ID 0 ；1ms最多生成ID 67108864 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2025-05-11 01:29:13.471 ，大约可使用 4 年
        /* 100万次 */
        compare(1000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用1000000次使用时间为：47毫秒
        // INFO cn.z.id.IdTest -- Random调用1000000次使用时间为：15毫秒
        // INFO cn.z.id.IdTest -- UUID调用1000000次使用时间为：1175毫秒
        // INFO cn.z.id.IdTest -- 调用1000000次，高性能雪花Id生成器比UUID性能高25.0倍
        /* 1000万次 */
        compare(10000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用10000000次使用时间为：227毫秒
        // INFO cn.z.id.IdTest -- Random调用10000000次使用时间为：173毫秒
        // INFO cn.z.id.IdTest -- UUID调用10000000次使用时间为：8853毫秒
        // INFO cn.z.id.IdTest -- 调用10000000次，高性能雪花Id生成器比UUID性能高39.0倍
        /* 1亿次 */
        compare(100000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用100000000次使用时间为：909毫秒
        // INFO cn.z.id.IdTest -- Random调用100000000次使用时间为：793毫秒
        // INFO cn.z.id.IdTest -- UUID调用100000000次使用时间为：83628毫秒
        // INFO cn.z.id.IdTest -- 调用100000000次，高性能雪花Id生成器比UUID性能高92.0倍
        /* 21亿次 */
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
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 3 ，机器码位数MACHINE_BITS 4 ，序列号位数SEQUENCE_BITS 5 ，最大机器码MACHINE_ID 15 ；1ms最多生成ID 32 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 572874-07-26 01:58:01.983 ，大约可使用 571232 年

        /* 获取配置参数 */
        log.info(Arrays.toString(Id.param()));
        // INFO cn.z.id.IdTest -- [3, 4, 5]
        long id = Id.next();
        log.info(String.valueOf(id));
        // INFO cn.z.id.IdTest -- 44161594381921

        /* 根据配置参数解析id */
        long[] parse = Id.parse(id);
        log.info(Arrays.toString(parse));
        // INFO cn.z.id.IdTest -- [1695712314027, 3, 1]

        /* 根据配置参数构造id(序列号为0) */
        log.info(String.valueOf(Id.format(parse[0])));
        // INFO cn.z.id.IdTest -- 44161594381920

        /* 根据配置参数构造id */
        log.info(String.valueOf(Id.format(parse[0], 1L)));
        // INFO cn.z.id.IdTest -- 44161594381921

        /* 解析id */
        long[] parse2 = Id.parse(8L, 12L, id);
        log.info(Arrays.toString(parse2));
        // INFO cn.z.id.IdTest -- [1609501315778, 85, 1633]

        /* 构造id */
        log.info(String.valueOf(Id.format(parse2[1], 8L, 12L, parse2[0], parse2[2])));
        // INFO cn.z.id.IdTest -- 44161594381921

        /* 获取id的时间戳 */
        log.info(String.valueOf(new Timestamp(Id.timestamp(id))));
        // INFO cn.z.id.IdTest -- 2023-12-23 15:13:04.144
    }

}
