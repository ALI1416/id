# High Performance Snowflake ID Generator 高性能雪花ID生成器

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

## 依赖导入

```xml
<dependency>
  <groupId>cn.404z</groupId>
  <artifactId>id</artifactId>
  <version>3.2.0</version>
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
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.IdTest -- ID 90441966306721793
```

### 手动初始化

```java
Id.init(0, 8, 14);
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 14 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 16384 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2090-09-07 23:47:35.551 ，大约可使用 69 年
// INFO cn.z.id.IdTest -- ID 361768291288481792
```

## 异常处理

### 初始化多次

```java
Id.init(0, 8, 13);
Id.init(0, 8, 15);
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 13 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 8192 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2160-05-15 15:35:11.103 ，大约可使用 139 年
// WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
// INFO cn.z.id.IdTest -- ID 180884221449994241
```

### 初始化晚了

```java
log.info("ID {}", Id.next());
Id.init(0, 8, 12);
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.IdTest -- ID 90442134147039232
// WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
// INFO cn.z.id.IdTest -- ID 90442134147039233
```

### 初始化异常

```java
Id.init(1000, 8, 12);
log.info("ID {}", Id.next());
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.Id -- 高性能雪花ID生成器初始化：机器码MACHINE_ID 1000 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// cn.z.id.IdException: 机器码MACHINE_ID 1000 无效！应为 [0,255]
```

### 阻塞

```java
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
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.IdTest -- ID 90442379897602049
// INFO cn.z.id.IdTest -- ID 90442380959809536
// WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2023-09-26 14:02:32.663 ，上一个生成的时间 2023-09-26 15:02:55.836
// INFO cn.z.id.IdTest -- ID 90442380960858113
// INFO cn.z.id.IdTest -- ID 90442382019919872
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
    log.info("总共回拨时间为：{}毫秒", Id.reset());
}
// INFO cn.z.id.Id -- 高性能雪花ID生成器预初始化：机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12 ，最大机器码MACHINE_ID 255 ；1ms最多生成ID 4096 个，起始时间 2021-01-01 08:00:00.0 ，失效时间 2299-09-27 23:10:22.207 ，大约可使用 278 年
// INFO cn.z.id.IdTest -- ID 90442426204815360
// INFO cn.z.id.Id -- 重置开始时间戳，时钟正拨 0 毫秒
// INFO cn.z.id.IdTest -- 时钟正拨时间为：0毫秒
// WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2023-09-26 14:03:30.023 ，上一个生成的时间 2023-09-26 15:03:38.985
// INFO cn.z.id.IdTest -- ID 90442426205863936
// INFO cn.z.id.Id -- 重置开始时间戳，时钟正拨 3608963 毫秒
// INFO cn.z.id.IdTest -- 时钟正拨时间为：3608963毫秒
```

## 工具

```java
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
log.info(String.valueOf(Id.newTimestamp(id)));
// INFO cn.z.id.IdTest -- 2023-12-23 15:13:04.144
```

更多请见[测试](./src/test)

## 性能比较

| 次数   | random.nextLong()耗时 | Id.next()耗时 | UUID.randomUUID()耗时 | 倍数    |
| ------ | --------------------- | ------------- | --------------------- | ------- |
| 100万  | 15毫秒                | 47毫秒        | 1175毫秒              | 25.0倍  |
| 1000万 | 173毫秒               | 227毫秒       | 8853毫秒              | 39.0倍  |
| 1亿    | 793毫秒               | 909毫秒       | 83628毫秒             | 92.0倍  |
| 21亿   | 36886毫秒             | 37871毫秒     | 7915039毫秒           | 209.0倍 |

## 更新日志

[点击查看](./CHANGELOG.md)

## 关于

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://www.404z.cn/images/about.dark.svg">
  <img alt="About" src="https://www.404z.cn/images/about.light.svg">
</picture>
