package com.ltq.rpc.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceContainer {
	private static final Map<String,Object> serviceRegistry = new ConcurrentHashMap();

    public static void register(Class serviceInterface, Class impl) {
        try {
            serviceRegistry.put(serviceInterface.getName(), impl.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public static Object get(String serviceName){
    	  Object serviceClass = serviceRegistry.get(serviceName);
    	  return serviceClass;
    }
}
