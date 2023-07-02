package com.ykrenz.fastdfs.cache;

public interface FdfsCache<K, V> {

    void put(K key, V value);

    V get(K key);

    V remove(K key);

    void clear();
}
