# Springboot1.x 使用Jedis、Lettuce、Redisson连接redis（适用于自搭建redis、AWS ElastiCache等服务，支持单节点、集群、加密、SSL传输认证），并可根据环境自动创建集群或单节点模式客户端。

<font size=5>
　　Springboot1.x默认内置jedis作为redis客户端，SpringBoot1.5.x项目如果要访问AWS ElastiCache加密服务则必须要开启SSL传输加密。
</br>
</br>
　　以1.5.9.RELEASE为例：spring-boot-starter-data-redis默认依赖的jedis版本为2.9.0，查询jedis官方Release Notes可以发现早在2016.9.11发布的2.9.0版本就已经开始支持单节点SSL，而直到2019.7.23 3.1.0版本的发布才开始支持集群模式SSL，因此当项目仅需要访问单机版的加密AWS ElastiCache时，可以直接使用2.9.0客户端，如果要使用集群模式的加密AWS ElastiCache则必须要更换到至少3.1.0版本及以上或更换其他redis客户端连接工具。
</font>
</br>
</br>

```java
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-dependencies</artifactId>
<version>1.5.9.RELEASE</version>
<packaging>pom</packaging>
<name>Spring Boot Dependencies</name>
<description>Spring Boot Dependencies</description>
<url>http://projects.spring.io/spring-boot/</url>
<organization>
   <name>Pivotal Software, Inc.</name>
   <url>http://www.spring.io</url>
</organization>

<properties>
	<!-- Dependency versions -->
	<activemq.version>5.14.5</activemq.version>
	<antlr2.version>2.7.7</antlr2.version>
	
	...
	
	<javax-transaction.version>1.2</javax-transaction.version>
	<javax-validation.version>1.1.0.Final</javax-validation.version>
	<jaxen.version>1.1.6</jaxen.version>
	<jaybird.version>2.2.13</jaybird.version>
	<jboss-logging.version>3.3.1.Final</jboss-logging.version>
	<jboss-transaction-spi.version>7.6.0.Final</jboss-transaction-spi.version>
	<jdom2.version>2.0.6</jdom2.version>
	<jedis.version>2.9.0</jedis.version>
	
	...
</properties>
...
```
</br>

## 本文共提供了5中方式，分别是：四合一共存版、JedisTemplate版、Jedis redisTemplate版、Lettuce redisTemplate版、Redisson版。

### 1、四合一共存版:即项目中同时存在四种redis客户端访问自搭建redis或AWS ElastiCache，且支持单节点、集群、加密、SSL，详情请参照本项目中的fourInOne工程。

### 2、JedisTemplate版：在springboot1.x环境下自己创建jedis单节点客户端或JedisCluster几集群客户端访问自搭建redis或AWS ElastiCache，且支持单节点、集群、加密、SSL，详情请参照本项目中的jedis-jedisTemplate工程。

### 3、Jedis redisTemplate版：在springboot1.x环境下，使用JedisConnectionFactory构建RedisTemplate访问自搭建redis或AWS ElastiCache，且支持单节点、集群、加密、SSL，详情请参照本项目中的jedis-redisTemplate工程。

### 4、Lettuce redisTemplate版：在springboot1.x环境下，使用LettuceConnectionFactory构建RedisTemplate访问自搭建redis或AWS ElastiCache，且支持单节点、集群、加密、SSL，详情请参照本项目中的lettuce工程。

### 5、Redisson版：在springboot1.x环境下，使用Redisson Client访问自搭建redis或AWS ElastiCache，且支持单节点、集群、加密、SSL，详情请参照本项目中的redisson工程。