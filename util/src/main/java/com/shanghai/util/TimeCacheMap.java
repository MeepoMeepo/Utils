package com.shanghai.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 本地缓存
 * @author jin.lv
 *
 * @param <K>
 * @param <V>
 */
public class TimeCacheMap<K, V> {
	// this default ensures things expire at most 50% past the expiration time
	private static final int DEFAULT_NUMbuckets = 3;

	public static interface ExpiredCallback<K, V> {
		public void expire(K key, V val);
	}

	private LinkedList<HashMap<K, V>> buckets;

	private final Object lock = new Object();
	private Thread cleaner;
	private ExpiredCallback<K, V> callback;

	public TimeCacheMap(int expirationSecs, int numBuckets, ExpiredCallback<K, V> expiredCallback) {
		if (numBuckets < 2) {
			throw new IllegalArgumentException("numBuckets must be >= 2");
		}
		buckets = new LinkedList<HashMap<K, V>>();
		for (int i = 0; i < numBuckets; i++) {
			buckets.add(new HashMap<K, V>());
		}

		callback = expiredCallback;
		final long expirationMillis = expirationSecs * 1000L;
		final long sleepTime = expirationMillis / (numBuckets - 1);
		cleaner = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Map<K, V> dead = null;
						Thread.sleep(sleepTime);
						synchronized (lock) {
							dead = buckets.removeLast();
							buckets.addFirst(new HashMap<K, V>());
						}
						if (callback != null) {
							for (Entry<K, V> entry : dead.entrySet()) {
								callback.expire(entry.getKey(),
										entry.getValue());
							}
						}
					}
				} catch (InterruptedException ex) {

				}
			}
		});
		cleaner.setDaemon(true);
		cleaner.start();
	}

	public TimeCacheMap(int expirationSecs, ExpiredCallback<K, V> callback) {
		this(expirationSecs, DEFAULT_NUMbuckets, callback);
	}

	public TimeCacheMap(int expirationSecs) {
		this(expirationSecs, DEFAULT_NUMbuckets);
	}

	public TimeCacheMap(int expirationSecs, int numBuckets) {
		this(expirationSecs, numBuckets, null);
	}

	public boolean containsKey(K key) {
		synchronized (lock) {
			for (HashMap<K, V> bucket : buckets) {
				if (bucket.containsKey(key)) {
					return true;
				}
			}
			return false;
		}
	}

	public V get(K key) {
		synchronized (lock) {
			for (HashMap<K, V> bucket : buckets) {
				if (bucket.containsKey(key)) {
					return bucket.get(key);
				}
			}
			return null;
		}
	}

	public void put(K key, V value) {
		synchronized (lock) {
			Iterator<HashMap<K, V>> it = buckets.iterator();
			HashMap<K, V> bucket = it.next();
			bucket.put(key, value);
			while (it.hasNext()) {
				bucket = it.next();
				bucket.remove(key);
			}
		}
	}

	public Object remove(K key) {
		synchronized (lock) {
			for (HashMap<K, V> bucket : buckets) {
				if (bucket.containsKey(key)) {
					return bucket.remove(key);
				}
			}
			return null;
		}
	}

	public int size() {
		synchronized (lock) {
			int size = 0;
			for (HashMap<K, V> bucket : buckets) {
				size += bucket.size();
			}
			return size;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			cleaner.interrupt();
		} finally {
			super.finalize();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		TimeCacheMap<Object, Object> map = new TimeCacheMap<>(10);
		map.put("dd", "ddd");
		System.out.println(map.get("dd"));
		Thread.sleep(20000);
		System.out.println(map.get("dd"));
	}
}
