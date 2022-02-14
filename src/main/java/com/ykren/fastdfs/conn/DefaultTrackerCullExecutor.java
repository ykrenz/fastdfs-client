package com.ykren.fastdfs.conn;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认剔除tracker策略
 */
public class DefaultTrackerCullExecutor implements TrackerCullExecutor<InetSocketAddress> {

    private final int retry;

    private final Map<InetSocketAddress, AtomicInteger> map = new HashMap<>();

    public DefaultTrackerCullExecutor(int retry) {
        this.retry = retry;
    }

    @Override
    public void addTracker(InetSocketAddress trackerServer) {
        map.put(trackerServer, new AtomicInteger(0));
    }

    @Override
    public boolean cullTracker(InetSocketAddress trackerServer) {
        return map.get(trackerServer).incrementAndGet() >= retry;
    }

    @Override
    public void recoveryTracker(InetSocketAddress trackerServer) {
        map.get(trackerServer).set(0);
    }

    @Override
    public void removeTracker(InetSocketAddress trackerServer) {
        map.remove(trackerServer);
    }
}