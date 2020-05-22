package com.ltq.rpc.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ltq.rpc.register.zookeeper.Constant;

/**
 * 基于 Redis 的服务注册接口实现
 *
 * @author huangyong
 * @since 1.0.0
 */
public class SimpleMapRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMapRegistry.class);
    private static final Map<String, String> serviceRegistry = new ConcurrentHashMap();
    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!serviceRegistry.containsKey(servicePath)) {
        	serviceRegistry.put(servicePath,serviceAddress);
            LOGGER.debug("create service node: {}", servicePath);
        }
    }

    @Override
    public String discover(String name) {
        try {
            // 获取 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + name;
            if (!serviceRegistry.containsKey(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            // 获取 address 节点
            String address = serviceRegistry.get(name);
            return  address;
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}