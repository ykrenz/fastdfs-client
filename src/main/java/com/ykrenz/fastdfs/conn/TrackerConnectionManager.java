package com.ykrenz.fastdfs.conn;

import com.ykrenz.fastdfs.exception.FdfsClientException;
import com.ykrenz.fastdfs.exception.FdfsException;
import com.ykrenz.fastdfs.exception.FdfsUnavailableException;
import com.ykrenz.fastdfs.model.fdfs.TrackerLocator;
import com.ykrenz.fastdfs.model.proto.FdfsCommand;

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
    public TrackerConnectionManager(List<String> trackerServers, FdfsConnectionPool pool) {
        super(pool);
        this.trackerLocator = new TrackerLocator(trackerServers);
        this.trackerLocator.setRetryAfterSecond(pool.getConnection().getRetryAfterSecond());
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
        InetSocketAddress address = null;
        while (true) {
            try {
                // 获取连接
                address = trackerLocator.getTrackerAddress();
                LOGGER.debug("获取到Tracker连接地址{}", address);
                Connection conn = getConnection(address);
                trackerLocator.setActive(address);
                // 执行交易
                return execute(address, conn, command);
            } catch (FdfsUnavailableException e) {
                throw e;
            } catch (FdfsException e) {
                LOGGER.error("execute tracker cmd error", e);
                trackerLocator.setInActive(address);
            } catch (Exception e) {
                throw new FdfsClientException("execute cmd error", e);
            }
        }
    }
}
