# High Performance Snowflake ID Generator (Single Version) 高性能雪花ID生成器(单机版)

[![License](https://img.shields.io/github/license/ALI1416/id?label=License)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Java Support](https://img.shields.io/badge/Java-8+-green)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/maven-central/v/cn.404z/id?label=Maven%20Central)](https://mvnrepository.com/artifact/cn.404z/id)
[![Tag](https://img.shields.io/github/v/tag/ALI1416/id?label=Tag)](https://github.com/ALI1416/id/tags)
[![Repo Size](https://img.shields.io/github/repo-size/ALI1416/id?label=Repo%20Size&color=success)](https://github.com/ALI1416/id/archive/refs/heads/master.zip)

[![Java CI](https://github.com/ALI1416/id/actions/workflows/ci.yml/badge.svg)](https://github.com/ALI1416/id/actions/workflows/ci.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_id&metric=coverage)
![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_id&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_id&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_id&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_id)

## 简介

本项目根据[Twitter的雪花ID生成器](https://github.com/twitter-archive/snowflake)重构，并加上了手动设置参数、时钟回拨处理，偶数问题解决等，以及支持[SpringBoot自动配置](https://github.com/ALI1416/id-spring-boot-autoconfigure)

### 支持版本

- [主线版本](https://github.com/ALI1416/id/tree/master)
- [单机版](https://github.com/ALI1416/id/tree/single) 去除`机器码`、`机器码位数`字段，不支持分布式

## 依赖导入

```xml
<dependency>
  <groupId>cn.404z</groupId>
  <artifactId>id</artifactId>
  <version>3.2.0.single</version>
</dependency>
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.5.0</version>
</dependency>
```

## 使用方法

### 直接调用

```java
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.IdTest -- ID 105306160215621632
```

### 手动初始化

```java
Id.init(22);
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 22 ；1ms最多生成ID 4194304 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2090-09-07 23:47:35.551 ，大约可使用 69 年
// INFO cn.z.id.IdTest -- ID 421225011492159488
```

## 异常处理

### 初始化多次

```java
Id.init(21);
Id.init(22);
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 21 ；1ms最多生成ID 2097152 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2160-05-15 15:35:11.103 ，大约可使用 139 年
// WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
// INFO cn.z.id.IdTest -- ID 210612668099198977
```

### 初始化晚了

```java
log.info("ID {}", Id.next());
Id.init(22);
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.IdTest -- ID 105306373240127489
// WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
// INFO cn.z.id.IdTest -- ID 105306373240127490
```

### 初始化异常

```java
Id.init(100);
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：序列号位数SEQUENCE_BITS 20 ；1ms最多生成ID 1048576 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：序列号位数SEQUENCE_BITS 100 ；1ms最多生成ID 68719476736 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2021-01-02 21:16:57.727 ，大约可使用 0 年
// ERROR cn.z.id.Id -- 当前时间 2024-03-08 16:39:58.921 无效！应为 [1970-01-01 08:00:00.0,2021-01-02 21:16:57.727]
// cn.z.id.IdException: 序列号位数SEQUENCE_BITS 100 无效！应为 [0,64]
```

### 阻塞

```java
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
```

### 时钟回拨(需要在1分钟内手动回拨时钟)

```java
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
```

### 重置初始时间戳(需要在1分钟内手动回拨时钟)

```java
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
```

## 工具

```java
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
```

更多请见[测试](./src/test)

## 性能比较

| 次数   | random.nextLong()耗时 | Id.next()耗时 | UUID.randomUUID()耗时 | 倍数       |
| ------ |---------------------|-------------|---------------------|----------|
| 100万  | 16毫秒                | 31毫秒        | 1184毫秒              | 74.0倍    |
| 1000万 | 96毫秒                | 191毫秒       | 8926毫秒              | 93.0倍    |
| 1亿    | 830毫秒               | 793毫秒       | 83830毫秒             | 101.0倍   |
| 21亿   | 1991毫秒              | 36886毫秒     | 7916216毫秒           | 3976.0倍  |

## 更新日志

[点击查看](./CHANGELOG.md)

## 关于

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://www.404z.cn/images/about.dark.svg">
  <img alt="About" src="https://www.404z.cn/images/about.light.svg">
</picture>
