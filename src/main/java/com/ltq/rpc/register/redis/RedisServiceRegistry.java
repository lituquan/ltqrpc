package com.ltq.rpc.register.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltq.rpc.register.ServiceRegistry;
import com.ltq.rpc.register.zookeeper.Constant;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 基于 Redis 的服务注册接口实现
 *
 * @author huangyong
 * @since 1.0.0
 */
public class RedisServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceRegistry.class);

    private JedisPool jedisPool;

    public RedisServiceRegistry(String zkAddress) {
        // 创建 ZooKeeper 客户端
        try  {
            jedisPool = new JedisPool(zkAddress);
        }catch (Exception e){
            e.printStackTrace();
        }
        LOGGER.debug("connect zookeeper");
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        Jedis jedisClient=jedisPool.getResource();
        if (!jedisClient.exists(servicePath)) {
            jedisClient.set(servicePath,serviceAddress);
            LOGGER.debug("create service node: {}", servicePath);
        }
        jedisClient.close();
    }
    public static void main(String[] args) {
    	new RedisServiceRegistry("192.168.6.31:31089");
	}

    @Override
    public String discover(String name) {
        try {
            // 获取 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + name;
            Jedis jedisClient=jedisPool.getResource();
            if (!jedisClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            // 获取 address 节点
            String address = jedisClient.get(name);
            return  address;
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            jedisPool.close();
        }
        return "";
    }
}