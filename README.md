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

本项目根据[Twitter的雪花ID生成器](https://github.com/twitter-archive/snowflake)重构，并加上了手动设置参数、时钟回拨处理等，以及支持[SpringBoot自动配置](https://github.com/ALI1416/id-spring-boot-autoconfigure)

## 依赖导入

```xml
<dependency>
  <groupId>cn.404z</groupId>
  <artifactId>id</artifactId>
  <version>2.7.0</version>
</dependency>
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.4.8</version>
</dependency>
```

## 使用方法

### 直接调用

代码

```java
log.info("ID {}", Id.next());
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.IdTest -- ID 84916544812875776
```

### 手动初始化

代码

```java
Id.init(0, 8, 14);
log.info("ID {}", Id.next());
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.Id -- 手动初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 14
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 16384 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 69 年，失效时间 2090-09-07 23:47:35.551
INFO cn.z.id.IdTest -- ID 339667266901639168
```

## 异常处理

### 初始化多次

代码

```java
Id.init(0, 8, 13);
Id.init(0, 8, 15);
log.info("ID {}", Id.next());
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.Id -- 手动初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 13
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 8192 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 139 年，失效时间 2160-05-15 15:35:11.103
WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
INFO cn.z.id.IdTest -- ID 169833687997743104
```

### 初始化晚了

代码

```java
log.info("ID {}", Id.next());
Id.init(0, 8, 12);
log.info("ID {}", Id.next());
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.IdTest -- ID 84916883292160000
WARN cn.z.id.Id -- 已经初始化过了，不可重复初始化！
INFO cn.z.id.IdTest -- ID 84916883292160001
```

### 初始化异常

代码

```java
Id.init(1000, 8, 12);
log.info("ID {}", Id.next());
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.Id -- 手动初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 1000 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
cn.z.id.IdException: 机器码MACHINE_ID 1000 无效！应为 [0,255]
```

### 阻塞

代码

```java
// 初始化，复现阻塞
Id.init(0, 0, 0);
log.info("ID {}", Id.next());
log.info("ID {}", Id.next());
log.info("ID {}", Id.next());
log.info("ID {}", Id.next());
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.Id -- 手动初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 0 ，序列号位数SEQUENCE_BITS 0
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 0 ，1ms内最多生成ID数量 1 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 292471208 年，失效时间 292269004-12-03 00:47:04.191
INFO cn.z.id.IdTest -- ID 80983144609
WARN cn.z.id.Id -- 检测到阻塞，时间 2023-07-27 15:19:04.609 ，最大序列号 0
INFO cn.z.id.IdTest -- ID 80983144625
WARN cn.z.id.Id -- 检测到阻塞，时间 2023-07-27 15:19:04.625 ，最大序列号 0
INFO cn.z.id.IdTest -- ID 80983144626
WARN cn.z.id.Id -- 检测到阻塞，时间 2023-07-27 15:19:04.626 ，最大序列号 0
INFO cn.z.id.IdTest -- ID 80983144641
```

### 时钟回拨(需要在1分钟内手动回拨时钟)

代码

```java
for (int i = 0; i < 60; i++) {
    log.info("ID {}", Id.next());
    try {
        Thread.sleep(1000);
    } catch (Exception ignore) {
        Thread.currentThread().interrupt();
    }
}
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.IdTest -- ID 84917057323270144
INFO cn.z.id.IdTest -- ID 84917058381283328
WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2023-07-27 15:17:59.316 ，上一个生成的时间 2023-07-27 15:20:17.603
INFO cn.z.id.IdTest -- ID 84917058382331904
INFO cn.z.id.IdTest -- ID 84917059429859328
```

### 重置初始时间戳(需要在1分钟内手动回拨时钟)

代码

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
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.IdTest -- ID 84917178671824896
INFO cn.z.id.Id -- 重置开始时间戳，时钟总共回拨 0 毫秒
INFO cn.z.id.IdTest -- 总共回拨时间为：0毫秒
WARN cn.z.id.Id -- 监测到系统时钟发生了回拨。回拨时间 2023-07-27 15:20:05.313 ，上一个生成的时间 2023-07-27 15:22:15.352
INFO cn.z.id.IdTest -- ID 84917181851107328
INFO cn.z.id.Id -- 重置开始时间戳，时钟总共回拨 130040 毫秒
INFO cn.z.id.IdTest -- 总共回拨时间为：130040毫秒
```

## 工具测试

代码

```java
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
```

结果

```txt
INFO cn.z.id.Id -- 预初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 0 ，机器码位数MACHINE_BITS 8 ，序列号位数SEQUENCE_BITS 12
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 255 ，1ms内最多生成ID数量 4096 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 278 年，失效时间 2299-09-27 23:10:22.207
INFO cn.z.id.Id -- 手动初始化...
INFO cn.z.id.Id -- 机器码MACHINE_ID 3 ，机器码位数MACHINE_BITS 4 ，序列号位数SEQUENCE_BITS 5
INFO cn.z.id.Id -- 最大机器码MACHINE_ID 15 ，1ms内最多生成ID数量 32 ，时钟最早回拨到 2021-01-01 08:00:00.0 ，可使用时间大约 571232 年，失效时间 572874-07-26 01:58:01.983
INFO cn.z.id.IdTest -- [3, 4, 5]
INFO cn.z.id.IdTest -- 20448571222112
INFO cn.z.id.IdTest -- [1649397815668, 3, 0]
INFO cn.z.id.IdTest -- 20448571222112
INFO cn.z.id.IdTest -- 20448571222113
INFO cn.z.id.IdTest -- [1609478701277, 46, 2144]
INFO cn.z.id.IdTest -- 20448571222112
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
