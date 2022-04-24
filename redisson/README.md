# Springboot1.x使用Redisson访问redis服务（适用于自搭建redis、AWS ElastiCache等服务，支持单节点、集群、加密、SSL传输认证），并可根据环境自动创建集群或单节点模式客户端。

### 1、由于springboot1.x默认内置jedis作为redis客户端，spring-boot-autoconfigure默认只对jedis实现了自动装配，因此springboot1.x项目中要使用redisson客户端，需要手动注册RedissonClient。
</br>

### 2、取消spring-boot-starter-data-redis的依赖，更换成spring-data-redis(目的是避免系统自动依赖jedis包)，在pom.xml中添加或更新依赖。
```java
<!--spring-boot-starter-data-redis-->
<!--<dependency>-->
<!--  <groupId>org.springframework.boot</groupId>-->
<!--  <artifactId>spring-boot-starter-data-redis</artifactId>-->
<!--</dependency>-->

<!-- https://mvnrepository.com/artifact/org.springframework.data/spring-data-redis -->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
</dependency>


<!--Redisson-->
<dependency>
	<groupId>org.redisson</groupId>
	<artifactId>redisson</artifactId>
	<version>3.17.0</version>
</dependency>
```
</br>

### 3、application.yml

> 3.1、单节点配置
```java
spring:
  redis:
    database: 0
    cluster:
      #redis客户端名称,jedis、redisson有该属性，lettuce没有
      clientName: 'redis-client-name'
      #Redis服务器访问密码
      password: 123456
      #集群模式下，逗号分隔的键值对（主机：端口）形式的服务器列表
      nodes: 127.0.0.1:6379
      #连接超时，指的是连接一个url的连接等待时间
      connectionTimeout: 1000
      #读取数据超时，指的是连接上一个url，获取response的返回等待时间
      soTimeOut: 1000
      #连接失败重试次数
      maxAttempts: 2
      #集群模式下，集群最大转发的数量
      max-redirects: 3
      #是否启用SSL连接，AWS rds服务要开启SSL才可以正常访问
      ssl: false
      #是否开启集群模式，按环境加载对应的客户端模式
      clusterEnable: false
    pool:
      max-active: 50  # 连接池最大连接数（使用负值表示没有限制）
      max-wait: -1  # 连接池最大阻塞等待时间（使用负值表示没有限制）
      max-idle: 10  # 连接池中的最大空闲连接
      min-idle: 5 # 连接池中的最小空闲连接
```
</br>

> 3.2、集群配置
```java
spring:
  redis:
    database: 0
    cluster:
      #redis客户端名称,jedis、redisson有该属性，lettuce没有
      clientName: 'redis-client-name'
      #Redis服务器访问密码
      password: 123456
      #集群模式下，逗号分隔的键值对（主机：端口）形式的服务器列表
      nodes: xxxxxx:6379
      #连接超时，指的是连接一个url的连接等待时间
      connectionTimeout: 1000
      #读取数据超时，指的是连接上一个url，获取response的返回等待时间
      soTimeOut: 1000
      #连接失败重试次数
      maxAttempts: 2
      #集群模式下，集群最大转发的数量
      max-redirects: 3
      #是否启用SSL连接，AWS rds服务要开启SSL才可以正常访问
      ssl: true
      #是否开启集群模式，按环境加载对应的客户端模式
      clusterEnable: true
    pool:
      max-active: 50  # 连接池最大连接数（使用负值表示没有限制）
      max-wait: -1  # 连接池最大阻塞等待时间（使用负值表示没有限制）
      max-idle: 10  # 连接池中的最大空闲连接
      min-idle: 5 # 连接池中的最小空闲连接
```
</br>

### 4、编写redis属性配置类
```java
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    private Pool pool;

    private Cluster cluster;

    @Getter
    @Setter
    @ToString
    public static class Cluster {
        /**
         * 集群模式下，逗号分隔的键值对（主机：端口）形式的服务器列表
         */
        List<String> nodes;

        /**
         * 连接超时，指的是连接一个url的连接等待时间
         */
        Integer connectionTimeout;

        /**
         * 读取数据超时，指的是连接上一个url，获取response的返回等待时间
         */
        Integer soTimeOut;

        /**
         * 连接失败重试次数
         */
        Integer maxAttempts;

        /**
         * 集群模式下，集群最大转发的数量
         */
        Integer maxRedirects;

        /**
         * jedis客户端名称 3.x版本之后的新参数
         */
        String clientName;

        /**
         * redis访问密码
         */
        String password;

        /**
         * 是否启用SSL连接，AWS rds服务要开启SSL才可以正常访问
         */
        boolean ssl;

        /**
         * 是否开启集群模式，不用环境使用不同的模式
         */
        boolean clusterEnable;
    }

    @Getter
    @Setter
    @ToString
    public static class Pool {

        /**
         * 连接池中的最大空闲连接
         */
        Integer maxIdle;

        /**
         * 连接池中的最小空闲连接
         */
        Integer minIdle;

        /**
         * 连接池最大连接数（使用负值表示没有限制）
         */
        Integer maxActive;

        /**
         * 连接池最大阻塞等待时间（使用负值表示没有限制）
         */
        Integer maxWait;
    }
}
```
</br>

### 5、注册redissonClient Bean，根据clusterEnable设置构造集群或单节点redisson客户端。
```java
@Configuration
public class RedisConfig {
    @Autowired
    protected RedisProperties redisProperties;

    private final static String COLLECTION_PREFIX = "redis://";
    private final static String COLLECTION_SSL_PREFIX = "rediss://";

    /**
     * 注册RedissonClient
     * config.setTransportMode(TransportMode.EPOLL);
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redissonClient() {
        log.info("creating redissonClient");
        // 1、创建配置
        Config config = new Config();

        // 2、配置模式
        if (redisProperties.getCluster().isClusterEnable()) {
            log.info("creating redissonClientCluster");
            ClusterServersConfig cluster = config.useClusterServers();
            cluster.setMasterConnectionMinimumIdleSize(redisProperties.getPool().getMaxIdle());
            cluster.setConnectTimeout(redisProperties.getCluster().getConnectionTimeout());
            cluster.setClientName(redisProperties.getCluster().getClientName()).setPassword(redisProperties.getCluster().getPassword());
            List<String> nodes = redisProperties.getCluster().getNodes();
            nodes.forEach(item -> cluster.addNodeAddress((redisProperties.getCluster().isSsl() ? COLLECTION_SSL_PREFIX : COLLECTION_PREFIX) + item));
        } else {
            log.info("creating redissonClient");
            SingleServerConfig single = config.useSingleServer();
            single.setConnectionMinimumIdleSize(redisProperties.getPool().getMaxIdle());
            single.setConnectTimeout(redisProperties.getCluster().getConnectionTimeout());
            single.setClientName(redisProperties.getCluster().getClientName()).setPassword(redisProperties.getCluster().getPassword());
            single.setAddress(redisProperties.getCluster().isSsl() ? COLLECTION_SSL_PREFIX : COLLECTION_PREFIX + redisProperties.getCluster().getNodes().get(0));
        }
        log.info("creating redissonClient success");
        return Redisson.create(config);
    }
}
```
</br>

### 6、测试
> [RMap set 测试](http://localhost:8080/redisson/map/set)　　
> [RMap get 测试](http://localhost:8080/redisson/map/get/1000)　　
> [Set测试](http://localhost:8080/redisson/set/test)　　
> [Queue测试](http://localhost:8080/redisson/queue/test)
```java
@Slf4j
@RestController
@RequestMapping("/redisson")
public class RedissonTest {

    @Autowired
    RedissonClient redisson;

    private List<UserInfo> userlist;

    {
        userlist = generateUser();
    }

    /**
     * 测试RMap,put方法的时候就会同步到redis中 ,在redis中创建了一张userMap表
     *
     * @return
     */
    @GetMapping("/map/set")
    public RMap<Object, UserInfo> mapSet() {
        RMap<Integer, UserInfo> map = redisson.getMap("userMap");
        userlist.forEach(u -> map.put(u.getId(), u));
        return redisson.getMap("userMap");
    }

    /**
     * 测试RMap,put方法的时候就会同步到redis中 ,在redis中创建了一张userMap表
     *
     * @param id
     * @return
     */
    @GetMapping("/map/get/{id}")
    public UserInfo mapGet(@PathVariable Integer id) {
        RMap<Integer, UserInfo> map = redisson.getMap("userMap");
        return map.get(id);
    }

    /**
     * 测试Set集合 ,创建userSet的set集合表
     *
     * @return
     */
    @GetMapping("/set/test")
    public Set<UserInfo> setTest() {
        Set<UserInfo> userSet = redisson.getSet("userSet");
        userlist.forEach(u -> userSet.add(u));
        return redisson.getSet("userSet");

    }

    /**
     * 测试Queue队列
     *
     * @return
     */
    @GetMapping("/queue/test")
    public Queue<UserInfo> queueTest() {
        Queue<UserInfo> userQueue = redisson.getQueue("userQueue");
        userlist.forEach(u -> userQueue.add(u));
        return redisson.getQueue("userQueue");
    }


    /**
     * 生成测试用户数据
     *
     * @return
     */
    private List<UserInfo> generateUser() {
        List<UserInfo> userlist = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserInfo user = new UserInfo();
            user.setId(1000 + i);
            user.setUsername("tang" + i);
            user.setAge(20 + i);
            user.setPassword("123456");
            user.setEmail("test" + i + "@126.com");
            user.setSex(i % 2 == 0 ? 1 : 2);
            user.setPhone("1888888888" + i);
            userlist.add(user);
        }
        return userlist;
    }
}
```