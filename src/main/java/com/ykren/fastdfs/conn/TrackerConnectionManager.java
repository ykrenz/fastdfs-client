package com.ykren.fastdfs.conn;

import com.ykren.fastdfs.exception.FdfsClientException;
import com.ykren.fastdfs.exception.FdfsException;
import com.ykren.fastdfs.exception.FdfsUnavailableException;
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
     * 重试
     */
    private RetryStrategy retryStrategy;

    /**
     * 构造函数
     */
    public TrackerConnectionManager(List<String> trackerServers, FdfsConnectionPool pool) {
        super(pool);
        TrackerCullExecutor<InetSocketAddress> trackerCullExecutor =
                new DefaultTrackerCullExecutor(pool.getConnection().getCullAfterCount());
        this.trackerLocator = new TrackerLocator(trackerServers, trackerCullExecutor);
        this.trackerLocator.setRetryAfterSecond(pool.getConnection().getRetryAfterSecond());
        this.retryStrategy = new DefaultTrackerRetryStrategy();
    }

    public TrackerLocator getTrackerLocator() {
        return trackerLocator;
    }

    public void setTrackerLocator(TrackerLocator trackerLocator) {
        this.trackerLocator = trackerLocator;
    }

    public RetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    public void setRetryStrategy(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    /**
     * 获取连接并执行交易
     *
     * @param command
     * @return
     */
    public <T> T executeFdfsTrackerCmd(FdfsCommand<T> command) {
        int retries = 0;
        InetSocketAddress address = null;
        boolean isException = false;
        while (true) {
            // 获取连接
            try {
                if (retries > 0) {
                    int delay = getPool().getConnection().getRetryTimeMills();
                    LOGGER.debug("An retriable error request will be retried after " + delay + "(ms) with attempt times: " + retries);
                    retryStrategy.delay(delay);
                }
                address = trackerLocator.getTrackerAddress();
                LOGGER.debug("获取到Tracker连接地址{}", address);
                Connection conn = getConnection(address);
                // 执行交易
                T result = execute(address, conn, command);
                isException = false;
                return result;
            } catch (FdfsException e) {
                LOGGER.error("execute cmd error", e);
                if (!retryStrategy.shouldRetry(e, address, command, retries)) {
                    throw e;
                }
                isException = true;
            } catch (Exception e) {
                LOGGER.error("client error", e);
                throw new FdfsClientException(e);
            } finally {
                if (isException) {
                    // 剔除tracker
                    boolean cullTracker = trackerLocator.cullTracker(address);
                    LOGGER.debug("cull tracker cull={}", cullTracker);
                    // 尝试其他tracker 保证高可用
                    address = trackerLocator.tryOtherTrackerAddress(address);
                    LOGGER.debug("tryOtherTracker tracker address={}", address);
                }
                // tracker正常了 恢复tracker
                if (retries > 0 && !isException) {
                    trackerLocator.recoveryTracker(address);
                    LOGGER.debug("tracker is recovery address={}", address);
                }
                retries++;
            }
        }
    }

    class DefaultTrackerRetryStrategy extends RetryStrategy {
        @Override
        public <T> boolean shouldRetry(Exception e, InetSocketAddress address, FdfsCommand<T> command, int retries) {
            if (e instanceof FdfsUnavailableException) {
                return false;
            }
            return retries < trackerLocator.getTrackerServers().size() - 1;
        }
    }
}
