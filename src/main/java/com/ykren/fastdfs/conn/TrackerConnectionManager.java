package com.ykren.fastdfs.conn;

import com.ykren.fastdfs.exception.FdfsConnectException;
import com.ykren.fastdfs.model.fdfs.TrackerLocator;
import com.ykren.fastdfs.model.proto.FdfsCommand;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 管理TrackerClient连接池分配
 *
 * @author tobato
 */
public class TrackerConnectionManager extends FdfsConnectionManager {

    /**
     * Tracker定位
     */
    private TrackerLocator trackerLocator;

    /**
     * 构造函数
     */
    public TrackerConnectionManager(List<String> trackerList, FdfsConnectionPool pool) {
        super(pool);
        trackerLocator = new TrackerLocator(trackerList);
    }

    public TrackerLocator getTrackerLocator() {
        return trackerLocator;
    }

    public void setTrackerLocator(TrackerLocator trackerLocator) {
        this.trackerLocator = trackerLocator;
    }

    /**
     * 获取连接并执行交易
     *
     * @param command
     * @return
     */
    public <T> T executeFdfsTrackerCmd(FdfsCommand<T> command) {
        Connection conn;
        InetSocketAddress address = null;
        // 获取连接
        try {
            address = trackerLocator.getTrackerAddress();
            LOGGER.debug("获取到Tracker连接地址{}", address);
            conn = getConnection(address);
            trackerLocator.setActive(address);
        } catch (FdfsConnectException e) {
            trackerLocator.setInActive(address);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unable to borrow buffer from pool", e);
            throw new RuntimeException("Unable to borrow buffer from pool", e);
        }
        // 执行交易
        return execute(address, conn, command);
    }

    public List<String> getTrackerList() {
        return trackerLocator.getTrackerList();
    }
}
