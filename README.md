# High Performance Snowflake ID Generator 高性能雪花ID生成器

[![License](https://img.shields.io/github/license/ali1416/id?label=License)](https://opensource.org/licenses/BSD-3-Clause)
[![Java Support](https://img.shields.io/badge/Java-8+-green)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/maven-central/v/cn.404z/id?label=Maven%20Central)](https://mvnrepository.com/artifact/cn.404z/id)
[![Tag](https://img.shields.io/github/v/tag/ali1416/id?label=Tag)](https://github.com/ALI1416/id/tags)
[![Repo Size](https://img.shields.io/github/repo-size/ali1416/id?label=Repo%20Size&color=success)](https://github.com/ALI1416/id)

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
    <version>2.5.0</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.6</version>
</dependency>
```

## 使用方法

### 直接调用

代码

```java
log.info("ID为：{}", Id.next());
```

结果

```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.IdTest - ID为：5483442415337472
```

### 手动初始化

代码

```java
Id.init(0, 8, 14);
log.info("ID为：{}", Id.next());
```

结果

```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.Id - 手动初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为14
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为16384，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为69年，失效日期为2090-09-07 23:47:35.551
[main] INFO cn.z.id.IdTest - ID为：21934128022683648
```

## 异常处理

### 初始化多次

代码

```java
Id.init(0, 8, 13);
Id.init(0, 8, 15);
log.info("ID为：{}", Id.next());
```

结果

```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.Id - 手动初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为13
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为8192，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为139年，失效日期为2160-05-15 15:35:11.103
[main] WARN cn.z.id.Id - 已经初始化过了，不可重复初始化！
[main] INFO cn.z.id.IdTest - ID为：10967292061941760
```

### 初始化晚了

代码

```java
log.info("ID为：{}", Id.next());
Id.init(0, 8, 12);
log.info("ID为：{}", Id.next());
```

结果

```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] WARN cn.z.id.Id - 已经初始化过了，不可重复初始化！
[main] INFO cn.z.id.IdTest - ID为：5483684734959616
[main] INFO cn.z.id.IdTest - ID为：5483684734959617
```

### 初始化异常

代码

```java
Id.init(1000, 8, 12);
log.info("ID为：{}", Id.next());
```

结果

```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.Id - 手动初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为1000，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] ERROR cn.z.id.Id - 机器码MACHINE_ID需要>=0并且<=255。当前为1000
java.lang.Exception: 机器码无效
[main] ERROR cn.z.id.Id - 重置初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.IdTest - ID为：5483719912587264
```

### 阻塞

代码

```java
// 初始化，复现阻塞
Id.init(0, 0, 0);
log.info("ID为：{}", Id.next());
log.info("ID为：{}", Id.next());
log.info("ID为：{}", Id.next());
log.info("ID为：{}", Id.next());
```

结果

```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.Id - 手动初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为0，SEQUENCE_BITS为0
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为0，1ms内最多生成Id数量为1，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为292471208年，失效日期为292269004-12-03 00:47:04.191
[main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.469，最大序列号为0
[main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.485，最大序列号为0
[main] INFO cn.z.id.IdTest - ID为：5229847469
[main] INFO cn.z.id.IdTest - ID为：5229847485
[main] INFO cn.z.id.IdTest - ID为：5229847500
[main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.5，最大序列号为0
[main] INFO cn.z.id.IdTest - ID为：5229847516
```

### 时钟回拨(需要在1分钟内手动回拨时钟)

代码

```java
for (int i = 0; i < 60; i++) {
    log.info("ID为：{}", Id.next());
    try {
        Thread.sleep(1000);
    } catch (Exception ignore) {
        Thread.currentThread().interrupt();
    }
}
```

结果

```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.IdTest - ID为：5483989976481792
[main] WARN cn.z.id.Id - 监测到系统时钟发生了回拨。回拨时间为2021-03-02 19:45:33.249，上一个生成的时间为2021-03-02 20:45:40.392
[main] INFO cn.z.id.IdTest - ID为：5483989977530368
```

### 重置初始时间戳(需要在1分钟内手动回拨时钟)

代码

```java
for (int i = 0; i < 60; i++) {
    log.info("ID为：{}", Id.next());
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
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.IdTest - ID为：23564520900263936
[main] INFO cn.z.id.Id - 重置初始时间戳，时钟总共回拨0毫秒
[main] INFO cn.z.id.IdTest - 总共回拨时间为：0毫秒
[main] WARN cn.z.id.Id - 监测到系统时钟发生了回拨。回拨时间为2021-09-18 10:25:55.498，上一个生成的时间为2021-09-18 10:27:58.361
[main] INFO cn.z.id.IdTest - ID为：23564520901312512
[main] INFO cn.z.id.Id - 重置初始时间戳，时钟总共回拨122864毫秒
[main] INFO cn.z.id.IdTest - 总共回拨时间为：122864毫秒
[main] INFO cn.z.id.IdTest - ID为：23564393127084032
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
14:03:35.661 [main] INFO cn.z.id.Id - 预初始化...
14:03:35.665 [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
14:03:35.667 [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
14:03:35.672 [main] INFO cn.z.id.Id - 手动初始化...
14:03:35.672 [main] INFO cn.z.id.Id - 初始化，MACHINE_ID为3，MACHINE_BITS为4，SEQUENCE_BITS为5
14:03:35.672 [main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为15，1ms内最多生成Id数量为32，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为571232年，失效日期为572874-07-26 01:58:01.983
[main] INFO cn.z.id.IdTest - [3, 4, 5]
[main] INFO cn.z.id.IdTest - 20448571222112
[main] INFO cn.z.id.IdTest - [1649397815668, 3, 0]
[main] INFO cn.z.id.IdTest - 20448571222112
[main] INFO cn.z.id.IdTest - 20448571222113
[main] INFO cn.z.id.IdTest - [1609478701277, 46, 2144]
[main] INFO cn.z.id.IdTest - 20448571222112
```

## 性能比较

| 次数   | random.nextLong()耗时 | Id.next()耗时 | UUID.randomUUID()耗时 | 倍数    |
| ------ | --------------------- | ------------- | --------------------- | ------- |
| 100万  | 15毫秒                | 47毫秒        | 1175毫秒              | 25.0倍  |
| 1000万 | 173毫秒               | 227毫秒       | 8853毫秒              | 39.0倍  |
| 1亿    | 793毫秒               | 909毫秒       | 83628毫秒             | 92.0倍  |
| 21亿   | 36886毫秒             | 37871毫秒     | 7915039毫秒           | 209.0倍 |

## 交流

QQ：1416978277  
微信：1416978277  
支付宝：1416978277@qq.com  
![交流](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/contact.png)

## 赞助

![赞助](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/donate.png)
