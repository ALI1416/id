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
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.IdTest -- ID 105306160215621632
    }

    /**
     * 手动初始化
     */
    // @Test
    void test01Init() {
        Id.init(22);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 22 ；1ms最多生成ID 4194304 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2090-09-07 23:47:35.551 ，大约可使用 69 年
        // INFO cn.z.id.IdTest -- ID 421225011492159488
    }

    /**
     * 初始化多次
     */
    // @Test
    void test02InitMore() {
        Id.init(21);
        Id.init(22);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 21 ；1ms最多生成ID 2097152 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2160-05-15 15:35:11.103 ，大约可使用 139 年
        // WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
        // INFO cn.z.id.IdTest -- ID 210612668099198977
    }

    /**
     * 初始化晚了
     */
    // @Test
    void test03InitLate() {
        log.info("ID {}", Id.next());
        Id.init(22);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.IdTest -- ID 105306373240127489
        // WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
        // INFO cn.z.id.IdTest -- ID 105306373240127490
    }

    /**
     * 初始化异常
     */
    // @Test
    void test04InitException() {
        Id.init(100);
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 100 ；1ms最多生成ID 68719476736 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2021-01-02 21:16:57.727 ，大约可使用 0 年
        // ERROR cn.z.id.Id -- 当前时间 2024-03-08 16:39:58.921 无效！应为 [1970-01-01 08:00:00.0,2021-01-02 21:16:57.727]
        // cn.z.id.IdException: 序列号位数SEQUENCE_BITS 100 无效！应为 [0,64]
    }

    /**
     * 阻塞
     */
    // @Test
    void test05Block() {
        // 使用较小的序列号位数初始化，可以复现阻塞
        Id.init(1);
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        log.info("ID {}", Id.next());
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 1 ；1ms最多生成ID 2 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 146140533-04-25 23:36:27.903 ，大约可使用 146235604 年
        // INFO cn.z.id.IdTest -- ID 200856138949
        // WARN cn.z.id.Id -- 检测到阻塞，时间 2024-03-08 16:41:09.474 ，最大序列号 1
        // INFO cn.z.id.IdTest -- ID 200856138974
        // INFO cn.z.id.IdTest -- ID 200856138975
        // WARN cn.z.id.Id -- 检测到阻塞，时间 2024-03-08 16:41:09.487 ，最大序列号 1
        // INFO cn.z.id.IdTest -- ID 200856139004
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
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.IdTest -- ID 105306532567056385
        // INFO cn.z.id.IdTest -- ID 105306543069593601
        // WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2024-03-08 16:40:25.247 ，上一个生成的时间 2024-03-08 16:42:25.475
        // INFO cn.z.id.IdTest -- ID 105306543070642177
        // INFO cn.z.id.IdTest -- ID 105306553560596480
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
            log.info("ID {}", Id.next());
            log.info("时钟正拨时间为：{}毫秒", Id.reset());
        }
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.IdTest -- ID 105307024018898945
        // INFO cn.z.id.IdTest -- ID 105307025071669248
        // INFO cn.z.id.Id -- 重置开始时间戳，时钟正拨 0 毫秒
        // INFO cn.z.id.IdTest -- 时钟正拨时间为：0毫秒
        // WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2024-03-08 16:48:06.833 ，上一个生成的时间 2024-03-08 16:50:08.157
        // INFO cn.z.id.IdTest -- ID 105307028227883009
        // INFO cn.z.id.Id -- 重置开始时间戳，时钟正拨 121325 毫秒
        // INFO cn.z.id.IdTest -- 时钟正拨时间为：121325毫秒
    }

    /**
     * 比较
     */
    // @Test
    void test08Compare() {
        // 使用较大的序列号位数初始化，避免阻塞
        Id.init(32);
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 32 ；1ms最多生成ID 4294967296 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2021-01-26 04:31:23.647 ，大约可使用 0 年
        /* 100万次 */
        compare(1000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用1000000次使用时间为：16毫秒
        // INFO cn.z.id.IdTest -- Random调用1000000次使用时间为：31毫秒
        // INFO cn.z.id.IdTest -- UUID调用1000000次使用时间为：1184毫秒
        // INFO cn.z.id.IdTest -- 调用1000000次，高性能雪花Id生成器比UUID性能高74.0倍
        /* 1000万次 */
        compare(10000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用10000000次使用时间为：96毫秒
        // INFO cn.z.id.IdTest -- Random调用10000000次使用时间为：191毫秒
        // INFO cn.z.id.IdTest -- UUID调用10000000次使用时间为：8926毫秒
        // INFO cn.z.id.IdTest -- 调用10000000次，高性能雪花Id生成器比UUID性能高93.0倍
        /* 1亿次 */
        compare(100000000);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用100000000次使用时间为：830毫秒
        // INFO cn.z.id.IdTest -- Random调用100000000次使用时间为：793毫秒
        // INFO cn.z.id.IdTest -- UUID调用100000000次使用时间为：83830毫秒
        // INFO cn.z.id.IdTest -- 调用100000000次，高性能雪花Id生成器比UUID性能高101.0倍
        /* 21亿次 */
        compare(Integer.MAX_VALUE);
        // INFO cn.z.id.IdTest -- 高性能雪花Id生成器调用2147483647次使用时间为：1991毫秒
        // INFO cn.z.id.IdTest -- Random调用2147483647次使用时间为：36886毫秒
        // INFO cn.z.id.IdTest -- UUID调用2147483647次使用时间为：7916216毫秒
        // INFO cn.z.id.IdTest -- 调用2147483647次，高性能雪花Id生成器比UUID性能高3976.0倍
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
        Id.init(5);
        // INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
        // INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 5 ；1ms最多生成ID 32 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 9135678-01-08 07:28:31.743 ，大约可使用 9139725 年

        /* 获取配置参数 */
        log.info(String.valueOf(Id.param()));
        // INFO cn.z.id.IdTest -- 5
        long id = Id.next();
        log.info(String.valueOf(id));
        // INFO cn.z.id.IdTest -- 3213748448577

        /* 根据配置参数解析id */
        long[] parse = Id.parse(id);
        log.info(Arrays.toString(parse));
        // INFO cn.z.id.IdTest -- [1709888839018, 1]

        /* 根据配置参数构造id(序列号为0) */
        log.info(String.valueOf(Id.format(parse[0])));
        // INFO cn.z.id.IdTest -- 3213748448576

        /* 根据配置参数构造id */
        log.info(String.valueOf(Id.format(parse[0], 1L)));
        // INFO cn.z.id.IdTest -- 3213748448577

        /* 解析id */
        long[] parse2 = Id.parse(20L, id);
        log.info(Arrays.toString(parse2));
        // INFO cn.z.id.IdTest -- [1609462264869, 372033]

        /* 构造id */
        log.info(String.valueOf(Id.format(20L, parse2[0], parse2[1])));
        // INFO cn.z.id.IdTest -- 3213748448577

        /* 获取id的时间戳 */
        log.info(String.valueOf(Id.newTimestamp(id)));
        // INFO cn.z.id.IdTest -- 2024-03-08 17:07:19.018
    }

}
