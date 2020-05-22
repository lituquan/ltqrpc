package com.ltq.rpc.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceContainer {
	private static final Map<String, Class<?>> serviceRegistry = new ConcurrentHashMap<String, Class<?>>();

    public static void register(Class serviceInterface, Class impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }
    public static Class get(String serviceName){
    	  Class serviceClass = serviceRegistry.get(serviceName);
    	  return serviceClass;
    }
}
