package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.model.fdfs.TrackerLocator;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TrackerLocatorTest {

    List<String> init() {
        List<String> trackerServers = new ArrayList<>();
        String tracker1 = "10.10.10.110:22122";
        String tracker2 = "10.10.10.111:22122";
        String tracker3 = "10.10.10.112:22122";
        trackerServers.add(tracker1);
        trackerServers.add(tracker2);
        trackerServers.add(tracker3);
        return trackerServers;
    }

    @Test
    public void initTest() {
        List<String> trackerServers = init();
        TrackerLocator trackerLocator = new TrackerLocator(trackerServers);
        int size = trackerLocator.getTrackerServers().size();
        Assert.assertEquals(size, trackerServers.size());

        Set<InetSocketAddress> result = new HashSet<>();
        for (int i = 0; i < size; i++) {
            InetSocketAddress trackerAddress = trackerLocator.getTrackerAddress();
            result.add(trackerAddress);
        }
        Assert.assertEquals(size, result.size());
    }

    @Test
    public void addTest() {
        List<String> trackerServers = init();
        TrackerLocator trackerLocator = new TrackerLocator(trackerServers);
        int size = trackerLocator.getTrackerServers().size();
        Assert.assertEquals(size, trackerServers.size());

        String tracker4 = "10.10.10.113:22122";
        trackerLocator.addTracker(tracker4);
        Set<InetSocketAddress> result = new HashSet<>();
        for (int i = 0; i < size + 1; i++) {
            InetSocketAddress trackerAddress = trackerLocator.getTrackerAddress();
            result.add(trackerAddress);
        }
        Assert.assertEquals(size + 1, result.size());
        Assert.assertEquals(size + 1, trackerLocator.getTrackerServers().size());
    }


    @Test
    public void removeTest() {
        List<String> trackerServers = init();
        TrackerLocator trackerLocator = new TrackerLocator(trackerServers);
        int size = trackerLocator.getTrackerServers().size();
        Assert.assertEquals(size, trackerServers.size());

        trackerLocator.removeTracker(trackerServers.get(0));
        Set<InetSocketAddress> result = new HashSet<>();
        for (int i = 0; i < size; i++) {
            InetSocketAddress trackerAddress = trackerLocator.getTrackerAddress();
            result.add(trackerAddress);
        }
        Assert.assertEquals(size - 1, result.size());
        Assert.assertEquals(size - 1, trackerLocator.getTrackerServers().size());
    }


    @Test
    public void threadTest() throws InterruptedException {
        List<String> trackerServers = init();
        TrackerLocator trackerLocator = new TrackerLocator(trackerServers);
        int size = trackerLocator.getTrackerServers().size();
        Assert.assertEquals(size, trackerServers.size());

        ExecutorService service = Executors.newFixedThreadPool(10);
        service.submit(() -> {
            trackerLocator.addTracker("10.10.10.113:22122");
        });

        service.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertEquals(size + 1, trackerLocator.getTrackerServers().size());

        size = trackerLocator.getTrackerServers().size();
        Set<InetSocketAddress> result = new HashSet<>();
        for (int i = 0; i < size; i++) {
            InetSocketAddress trackerAddress = trackerLocator.getTrackerAddress();
            result.add(trackerAddress);
        }
        Assert.assertEquals(size, result.size());

        service.shutdown();
    }
}
