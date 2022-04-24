package com.time.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName JedisUtil.java
 * @Description Jedis工具类
 * @createTime 2022年03月26日 16:17:00
 */
@Slf4j
@Component
@ConditionalOnBean(Jedis.class)
public class JedisUtil implements IJedisTemplate {

    @PostConstruct
    public void init() {
        log.info("JedisUtil is created");
    }

    @Autowired(required = false)
    private Jedis jedis;

    /* ------------------------------------- key相关操作 -------------------------------------------*/

    /**
     * 用于删除已存在的键,不存在的 key 会被忽略
     */
    @Override
    public Long delete(String key) {
        return jedis.del(key);
    }

    /**
     * 用于序列化给定 key ,并返回被序列化的值
     */
    @Override
    public byte[] dump(String key) {
        return jedis.dump(key);
    }

    /**
     * 用于检查给定 key 是否存在
     */
    @Override
    public Boolean exists(String key) {
        return jedis.exists(key);
    }

    /**
     * 用于设置 key 的过期时间，key 过期后将不再可用。单位以秒计
     *
     * @param seconds 过期时间-秒
     */
    @Override
    public Long expire(String key, Integer seconds) {
        return jedis.expire(key, seconds);
    }

    /**
     * 用于以 UNIX 时间戳(unix timestamp)格式设置 key 的过期时间。key 过期后将不再可用
     *
     * @param unixTime 秒时间戳：1611567518
     */
    @Override
    public Long expireAt(String key, Long unixTime) {
        return jedis.expireAt(key, unixTime);
    }

    /**
     * 以毫秒为单位设置 key 的生效时间
     *
     * @param milliseconds 毫秒
     */
    @Override
    public Long pexpire(String key, Integer milliseconds) {
        return jedis.pexpire(key, milliseconds);
    }

    /**
     * 以毫秒为单位设置 key 的生效时间
     *
     * @param milliseconds 毫秒时间戳：1611567518000
     */
    @Override
    public Long pexpireAt(String key, Long milliseconds) {
        return jedis.pexpireAt(key, milliseconds);
    }

    /**
     * 用于查找所有符合给定模式 pattern 的 key
     *
     * @param pattern *
     */
    @Override
    public Set<String> keys(String pattern) {
        return jedis.keys(pattern);
    }

    /**
     * 移除 key 的过期时间，key 将持久保持
     */
    @Override
    public Long persist(String key) {
        return jedis.persist(key);
    }

    /**
     * Pttl 命令以毫秒为单位返回 key 的剩余过期时间
     */
    @Override
    public Long pttl(String key) {
        return jedis.pttl(key);
    }

    /**
     * Pttl 命令以秒为单位返回 key 的剩余过期时间
     */
    @Override
    public Long ttl(String key) {
        return jedis.ttl(key);
    }

    /**
     * 从当前数据库中随机返回一个 key
     */
    public String randomKey() {
        return jedis.randomKey();
    }

    /**
     * 修改 key 的名称
     *
     * @return 改名成功时提示 OK ，失败时候返回一个错误。
     */
    @Override
    public String rename(String oldKey, String newKey) {
        return jedis.rename(oldKey, newKey);
    }

    /**
     * 用于在新的 key 不存在时修改 key 的名称
     *
     * @return 修改成功时，返回 1 。 如果 NEW_KEY_NAME 已经存在，返回 0
     */
    @Override
    public Long renameIfAbsent(String oldKey, String newKey) {
        return jedis.renamenx(oldKey, newKey);
    }

    /**
     * 用于返回 key 所储存的值的类型
     *
     * @return 返回 key 的数据类型，数据类型有: none (key不存在)、string (字符串)、list (列表)、set (集合)、zset (有序集)、hash (哈希表)
     */
    @Override
    public String type(String key) {
        return jedis.type(key);
    }


    /* ------------------------------------- String 相关操作 -------------------------------------------*/

    /**
     * 用于设置给定 key 的值。如果 key 已经存储其他值， SET 就覆写旧值，且无视类型
     */
    @Override
    public String set(String key, String value) {
        return jedis.set(key, value);
    }

    /**
     * 用于获取指定 key 的值。如果 key 不存在，返回 nil 。如果key 储存的值不是字符串类型，返回一个错误
     */
    @Override
    public String get(String key) {
        return jedis.get(key);
    }

    /**
     * 返回所有(一个或多个)给定 key 的值。 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil
     */
    @Override
    public List<String> mget(String... key) {
        return jedis.mget(key);
    }

    /**
     * 用于同时设置一个或多个 key-value 对
     *
     * @param keysValues MSET key1 value1 key2 value2 .. keyN valueN
     */
    @Override
    public String mset(String... keysValues) {
        return jedis.mset(keysValues);
    }

    /**
     * 用于所有给定 key 都不存在时，同时设置一个或多个 key-value
     */
    @Override
    public Long msetnx(String... keysValues) {
        return jedis.msetnx(keysValues);
    }

    /**
     * 用于获取存储在指定 key 中字符串的子字符串。字符串的截取范围由 start 和 end 两个偏移量决定(包括 start 和 end 在内!)
     */
    @Override
    public String getRange(String key, Long start, Long end) {
        return jedis.getrange(key, start, end);
    }

    /**
     * 为指定的 key 设置值及其过期时间。如果 key 已经存在， SETEX 命令将会替换旧的值和过期时间
     *
     * @param key     指定 key
     * @param seconds 过期时间-秒
     * @param value   值
     */
    @Override
    public String setex(String key, Integer seconds, String value) {
        return jedis.setex(key, seconds, value);
    }

    /**
     * 命令以毫秒 milliseconds 为单位设置 key 的生存时间
     */
    @Override
    public String psetxx(String key, Long milliseconds, String value) {
        return jedis.psetex(key, milliseconds, value);
    }

    /**
     * 在指定的 key 不存在时，为 key 设置指定的值.否则设置无效
     */
    @Override
    public Long setnx(String key, String value) {
        return jedis.setnx(key, value);
    }

    /**
     * 用指定的字符串覆盖给定 key 所储存的字符串值，覆盖的位置从偏移量 offset 开始
     */
    @Override
    public Long setRange(String key, Long offset, String value) {
        return jedis.setrange(key, offset, value);
    }

    /**
     * 用于获取指定 key 所储存的字符串值的长度。当 key 储存的不是字符串值时，返回一个错误
     */
    @Override
    public Long strlen(String key) {
        return jedis.strlen(key);
    }

    /**
     * Redis Incr 命令将 key 中储存的数字值增一。
     * !:如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
     * !:如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * !:本操作的值限制在 64 位(bit)有符号数字表示之内。
     */
    @Override
    public Long incr(String key) {
        return jedis.incr(key);
    }

    /**
     * Redis Incr 命令将 key 中储存的数字值减一。
     * !:如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
     * !:如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * !:本操作的值限制在 64 位(bit)有符号数字表示之内。
     */
    @Override
    public Long decr(String key) {
        return jedis.decr(key);
    }

    /**
     * 用于为指定的 key 追加值。
     * :如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。
     * :如果 key 不存在， APPEND 就简单地将给定 key 设为 value ，就像执行 SET key value 一样
     */
    @Override
    public Object append(String key, String value) {
        return jedis.append(key, value);
    }


    /* ------------------------------------- hash 相关操作 -------------------------------------------*/

    /**
     * 用于删除哈希表 key 中的一个或多个指定字段，不存在的字段将被忽略
     */
    @Override
    public Long hdel(String key, String... fields) {
        return jedis.hdel(key, fields);
    }

    /**
     * 用于返回哈希表中指定字段的值
     */
    @Override
    public Object hget(String key, String field) {
        return jedis.hget(key, field);
    }

    /**
     * 用于返回哈希表中，所有的字段和值
     */
    @Override
    public Map<String, String> hgetAll(String key) {
        return jedis.hgetAll(key);
    }

    /**
     * 用于返回哈希表中，一个或多个给定字段的值
     * :如果指定的字段不存在于哈希表，那么返回一个 nil 值
     */
    @Override
    public Object hmget(String key, String... fields) {
        return jedis.hmget(key, fields);
    }

    /**
     * 用于查看哈希表的指定字段是否存在
     */
    @Override
    public Boolean hexists(String key, String field) {
        return jedis.hexists(key, field);
    }

    /**
     * 用于获取哈希表中的所有域（field）
     */
    @Override
    public Set<String> hkeys(String pattern) {
        return jedis.keys(pattern);
    }

    /**
     * 返回哈希表所有域(field)的值
     */
    @Override
    public List<String> hvalues(String key) {
        return jedis.hvals(key);
    }

    /**
     * 用于获取哈希表中字段的数量
     */
    @Override
    public Long hlen(String key) {
        return jedis.hlen(key);
    }

    /**
     * 用于为哈希表中的字段赋值
     */
    @Override
    public Long hset(String key, String fields, String value) {
        return jedis.hset(key, fields, value);
    }

    /**
     * 用于为哈希表中的字段赋值
     */
    @Override
    public Long hset(String key, Map<String, String> map) {
        return jedis.hset(key, map);
    }

    /**
     * 用于同时将多个 field-value (字段-值)对设置到哈希表中
     */
    @Override
    public String hmset(String key, Map<String, String> hash) {
        return jedis.hmset(key, hash);
    }

    /**
     * 用于为哈希表中不存在的的字段赋值
     */
    @Override
    public Object hsetnx(String key, String field, String value) {
        return jedis.hsetnx(key, field, value);
    }

    /**
     * 设置缓存对象
     *
     * @param key 缓存key
     * @param obj 缓存value
     */
    @Override
    public <T> void setObject(String key, T obj, int expireTime) {
        jedis.setex(key, expireTime, JSON.toJSONString(obj));
    }

    /**
     * 获取指定key的缓存
     *
     * @param key---JSON.parseObject(value, User.class);
     */
    @Override
    public String getObject(String key) {
        return jedis.get(key);
    }


    /* ------------------------------------- List 相关操作 -------------------------------------------*/

    /**
     * 命令移出并获取多个指定 key 列表第一个元素，如果元素为空返回nil
     */
    @Override
    public List<String> blpop(String... key) {
        return jedis.blpop(key);
    }

    /**
     * 命令移出并获取多个指定 key 列表指定的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     */
    @Override
    public List<String> blpop(Integer timeout, String... key) {
        return jedis.blpop(timeout, key);
    }

    /**
     * 命令移出并获取指定 key 列表第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     */
    @Override
    public List<String> blpop(Integer timeout, String key) {
        return jedis.blpop(timeout, key);
    }

    /**
     * 命令移出并获取多个指定 key 列表最后一个元素，如果元素为空返回nil
     */
    @Override
    public List<String> brpop(String... args) {
        return jedis.brpop(args);
    }

    /**
     * 命令移出并获取多个指定 key 列表指定的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     */
    @Override
    public List<String> brpop(Integer timeout, String... key) {
        return jedis.brpop(timeout, key);
    }

    /**
     * 命令移出并获取指定 key 列表最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     */
    @Override
    public List<String> brpop(Integer timeout, String key) {
        return jedis.brpop(timeout, key);
    }

    /**
     * 用于通过索引获取列表中的元素
     *
     * @param index 0 or -1
     */
    @Override
    public String lindex(String key, Long index) {
        return jedis.lindex(key, index);
    }

    /**
     * 用于在列表的元素前或者后插入元素。当指定元素不存在于列表中时，不执行任何操作
     *
     * @param where ListPosition.BEFORE  ListPosition.AFTER
     * @param pivot 以那个元素为中心点
     */
    @Override
    public Long linsert(String key, ListPosition where, String pivot, String value) {
        return jedis.linsert(key, where, pivot, value);
    }

    /**
     * 用于返回列表的长度
     */
    @Override
    public Long llen(String key) {
        return jedis.llen(key);
    }

    /**
     * 移出并获取列表的第一个元素
     */
    @Override
    public String lpop(String key) {
        return jedis.lpop(key);
    }

    /**
     * 将一个或多个值插入到列表头部。 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误
     */
    @Override
    public Long lpush(String key, String... values) {
        return jedis.lpush(key, values);
    }

    /**
     * 将一个值插入到已存在的列表头部，列表不存在时操作无效
     */
    @Override
    public Long lpushx(String key, String... values) {
        return jedis.lpushx(key, values);
    }

    /**
     * 返回列表中指定区间内的元素，区间以偏移量 START 和 END 指定
     */
    @Override
    public List<String> lrange(String key, Long start, Long end) {
        return jedis.lrange(key, start, end);
    }

    /**
     * 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值
     */
    @Override
    public Long lrem(String key, Long count, String value) {
        return jedis.lrem(key, count, value);
    }

    /**
     * 通过索引来设置元素的值
     */
    @Override
    public String lset(String key, Long index, String value) {
        return jedis.lset(key, index, value);
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
     */
    @Override
    public String ltrim(String key, Long start, Long end) {
        return jedis.ltrim(key, start, end);
    }

    /**
     * 用于移除列表的最后一个元素，返回值为移除的元素
     */
    @Override
    public String rpop(String key) {
        return jedis.rpop(key);
    }

    /**
     * 用于将一个或多个值插入到列表的尾部(最右边)
     */
    @Override
    public Long rpush(String key, String... values) {
        return jedis.rpush(key, values);
    }

    /**
     * 用于将一个值插入到已存在的列表尾部(最右边)。如果列表不存在，操作无效
     */
    @Override
    public Long rpushx(String key, String... values) {
        return jedis.rpushx(key, values);
    }


    /* ------------------------------------- Set 相关操作 -------------------------------------------*/

    /**
     * 将一个或多个成员元素加入到集合中，已经存在于集合or重复的成员元素将被忽略。
     * 假如集合 key 不存在，则创建一个只包含添加的元素作成员的集合。
     * 当集合 key 不是集合类型时，返回一个错误。
     */
    @Override
    public Long sadd(String key, String... members) {
        return jedis.sadd(key, members);
    }

    /**
     * 返回集合中元素的数量
     */
    @Override
    public Long scard(String key) {
        return jedis.scard(key);
    }

    /**
     * 返回给定所有给定集合的交集。 不存在的集合 key 被视为空集。 当给定集合当中有一个空集时，结果也为空集(根据集合运算定律)
     */
    @Override
    public Set<String> sinter(String... keys) {
        return jedis.sinter(keys);
    }

    /**
     * 判断成员元素是否是集合的成员
     */
    @Override
    public Boolean sismember(String key, String member) {
        return jedis.sismember(key, member);
    }

    /**
     * 返回集合中的所有的成员。 不存在的集合 key 被视为空集合
     */
    @Override
    public Set<String> smembers(String key) {
        return jedis.smembers(key);
    }

    /**
     * 命令将指定成员 member 元素从 source 集合移动到 destination 集合
     * 当 destination 集合已经包含 member 元素时， SMOVE 命令只是简单地将 source 集合中的 member 元素删除。
     * 当 source 或 destination 不是集合类型时，返回一个错误
     *
     * @param srckey set1
     * @param dstkey set2
     * @param member 移动的值
     */
    @Override
    public Long smove(String srckey, String dstkey, String member) {
        return jedis.smove(srckey, dstkey, member);
    }

    /**
     * 用于移除集合中的指定 key 的一个随机元素，移除后会返回移除的元素
     */
    @Override
    public String spop(String key) {
        return jedis.spop(key);
    }

    /**
     * 用于移除集合中的指定 key 的多个随机元素，移除后会返回移除的元素
     */
    @Override
    public Set<String> spop(String key, Long count) {
        return jedis.spop(key, count);
    }

    /**
     * 用于返回集合中的一个随机元素。
     */
    @Override
    public String srandmember(String key) {
        return jedis.srandmember(key);
    }

    /**
     * 用于返回集合中指定count数量的随机元素
     * 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
     * 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值
     */
    @Override
    public List<String> srandmember(String key, Integer count) {
        return jedis.srandmember(key, count);
    }

    /**
     * 用于移除集合中的一个或多个成员元素，不存在的成员元素会被忽略
     */
    @Override
    public Long srem(String key, String... members) {
        return jedis.srem(key, members);
    }

    /**
     * 返回给定集合的并集。不存在的集合 key 被视为空集
     */
    @Override
    public Set<String> sunion(String... keys) {
        return jedis.sunion(keys);
    }

    /**
     * 将给定集合的并集存储在指定的集合 destination 中。如果 destination 已经存在，则将其覆盖
     */
    @Override
    public Long sunionstore(String dstkey, String... keys) {
        return jedis.sunionstore(dstkey, keys);
    }


    /* ------------------------------------- sortedset 相关操作 -------------------------------------------*/

    /**
     * Redis Zadd 命令用于将一个或多个成员元素及其分数值加入到有序集当中。
     * 如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证该成员在正确的位置上。
     * 分数值可以是整数值或双精度浮点数。
     * 如果有序集合 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     */
    @Override
    public Long zadd(String key, Double score, String member) {
        return jedis.zadd(key, score, member);
    }

    /**
     * Redis Zadd 命令用于将一个或多个成员元素及其分数值加入到有序集当中。
     * 如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证该成员在正确的位置上。
     * 分数值可以是整数值或双精度浮点数。
     * 如果有序集合 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     * 注意： 在 Redis 2.4 版本以前， ZADD 每次只能添加一个元素。
     */
    @Override
    public Long zadd(String key, Map<String, Double> params) {
        return jedis.zadd(key, params);
    }

    /**
     * 用于计算集合中元素的数量
     */
    @Override
    public Long zcard(String key) {
        return jedis.zcard(key);
    }

    /**
     * 用于计算有序集合中指定分数区间的成员数量
     */
    @Override
    public Long zcount(String key, Double min, Double max) {
        return jedis.zcount(key, min, max);
    }

    /**
     * 用于计算有序集合中指定分数区间的成员数量
     */
    @Override
    public Long zcount(String key, String min, String max) {
        return jedis.zcount(key, min, max);
    }

    /**
     * 在计算有序集合中指定字典区间内成员数量
     */
    @Override
    public Long zlexcount(String key, String min, String max) {
        return jedis.zlexcount(key, min, max);
    }

    /**
     * Redis Zrange 返回有序集中，指定区间内的成员。
     * 其中成员的位置按分数值递增(从小到大)来排序。
     * 具有相同分数值的成员按字典序(lexicographical order )来排列
     */
    @Override
    public Set<String> zrange(String key, Long start, Long end) {
        return jedis.zrange(key, start, end);
    }

    /**
     * 返回有序集中指定成员的排名。其中有序集成员按分数值递增(从小到大)顺序排列
     */
    @Override
    public Long zrank(String key, String member) {
        return jedis.zrank(key, member);
    }

    /**
     * 用于移除有序集中的一个或多个成员，不存在的成员将被忽略
     */
    @Override
    public Long zrem(String key, String... members) {
        return jedis.zrem(key, members);
    }

    /**
     * 返回有序集中，成员的分数值。 如果成员元素不是有序集 key 的成员，或 key 不存在，返回 nil
     */
    @Override
    public Double zsocre(String key, String member) {
        return jedis.zscore(key, member);
    }

    /* ------------------------------------- HyperLogLog 相关操作 -------------------------------------------*/

    /**
     * 将所有元素参数添加到 HyperLogLog 数据结构中
     */
    @Override
    public Long pfadd(String key, String... elements) {
        return jedis.pfadd(key, elements);
    }

    /**
     * 返回给定 HyperLogLog 的基数估算值,已存在的值将会被忽略
     */
    @Override
    public long pfcount(String keys) {
        return jedis.pfcount(keys);
    }

    /**
     * 将多个 HyperLogLog 合并为一个 HyperLogLog ，合并后的 HyperLogLog 的基数估算值是通过对所有 给定 HyperLogLog 进行并集计算得出的
     */
    @Override
    public String pfmerge(String destkey, String... sourcekeys) {
        return jedis.pfmerge(destkey, sourcekeys);
    }
}