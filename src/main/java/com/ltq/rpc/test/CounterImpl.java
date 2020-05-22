package com.ltq.rpc.test;

import java.util.concurrent.atomic.AtomicInteger;

//https://blog.csdn.net/javazejian/article/details/72772470#%E6%97%A0%E9%94%81%E7%9A%84%E6%A6%82%E5%BF%B5
public class CounterImpl implements Counter {
	private AtomicInteger atomic=new AtomicInteger(0);
	/*
	 *atomic 原子类 ==>Unsafe ==>CAS 机制
	 *乐观锁{
	 *	内存中V=A , 此时修改A->B
	 *	比较V是否还是A,是才修改。 【compare and swap==>cpu 指令】	
	 *  
	 *  "谁能成功写进去谁就获取到锁,否则空循环==>自旋锁"
	 *
	 *  ABA问题：
	 *  增加版本号字段
	 *  AtomicStampedReference【使用时间戳维护版本】
	 *}
	 *数据库{
	 *	查询value=A,
	 *	uplate t set value=B where id=xx and value=A 
	 *  
	 *  引入版本,避免ABA问题,每次操作都有版本号：
	 *  查询value=A,version(id)=x
	 *  uplate t set value=B where id=xx and version=x
	 *  
	 *} 
	 * 
	 */
	
	public int count() {		
		return atomic.addAndGet(1);
	}
	
}
