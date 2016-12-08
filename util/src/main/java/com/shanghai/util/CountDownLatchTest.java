package com.shanghai.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchTest {
	
	private static ExecutorService executor = Executors.newFixedThreadPool(20);
	
	public static void test(){
		CountDownLatch countDownLatch = new CountDownLatch(5);
		for (int i=0; i<5;i++) {
			executor.submit(new Runnable() {
	
				@Override
				public void run() {
					try {
						//do something...
						System.out.println(1111);
					} catch (Exception e) {
	
					} finally {
						//count -1
						countDownLatch.countDown();
					}
	
				}
	
			});
		}
		//调用此方法会阻塞，直到count = 0
		try {
			countDownLatch.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
	}
	public static void main(String[] args) {
		test();
		System.exit(0);
	}
}
