package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.monitor.StorageMonitor;
import org.junit.Test;

/**
 * @author ykren
 * @date 2022/2/18
 */
public class MonitorTest extends BaseClientTest {

    @Test
    public void storageSpaceTest() {
        new StorageMonitor(fastDFS.trackerClient(), "85%").monitor();
        new StorageMonitor(fastDFS.trackerClient(), "36.72G").monitor();
        new StorageMonitor(fastDFS.trackerClient(), "36.73g").monitor();
        new StorageMonitor(fastDFS.trackerClient(), "37597M").monitor();

        long b = 1024 * 1024 * 1000L * 38;
        new StorageMonitor(fastDFS.trackerClient(), b + "b").monitor();
        new StorageMonitor(fastDFS.trackerClient(), b + "").monitor();
    }

}
