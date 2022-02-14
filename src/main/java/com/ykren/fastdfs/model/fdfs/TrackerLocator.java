package com.ykren.fastdfs.model.fdfs;

import com.ykren.fastdfs.conn.TrackerCullExecutor;
import com.ykren.fastdfs.exception.FdfsUnavailableException;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 表示Tracker服务器位置
 * <p>
 * <pre>
 * 支持负载均衡对IP轮询
 * </pre>
 *
 * @author tobato
 */
public class TrackerLocator {

    /**
     * tracker服务配置地址列表
     */
    private List<String> trackerServers;
    /**
     * 目录服务地址-为了加速处理，增加了一个map
     */
    private final Map<InetSocketAddress, TrackerAddressHolder> trackerAddressMap = new HashMap<>();
    /**
     * 轮询圈
     */
    private final CircularList<TrackerAddressHolder> trackerAddressCircular = new CircularList<>();
    /**
     * 剔除圈
     */
    private final List<TrackerAddressHolder> cullTrackerAddressCircular = new ArrayList<>();
    /**
     * 连接中断以后经过N秒重试
     * -1不重试
     */
    private int retryAfterSecond;
    /**
     * tracker剔除
     */
    private TrackerCullExecutor<InetSocketAddress> cullExecutor;

    /**
     * 初始化Tracker服务器地址
     * 配置方式为 ip:port 如 192.168.1.2:21000
     *
     * @param trackerServers
     */
    public TrackerLocator(List<String> trackerServers, TrackerCullExecutor<InetSocketAddress> cullExecutor) {
        super();
        this.trackerServers = trackerServers;
        this.cullExecutor = cullExecutor;
        buildTrackerAddresses();
    }

    public TrackerCullExecutor<InetSocketAddress> getCullExecutor() {
        return cullExecutor;
    }

    public void setCullExecutor(TrackerCullExecutor<InetSocketAddress> cullExecutor) {
        this.cullExecutor = cullExecutor;
    }

    /**
     * 分析TrackerAddress
     */
    private void buildTrackerAddresses() {
        for (String item : trackerServers) {
            initTrackerServer(item);
        }
    }

    /**
     * 初始化tracker
     *
     * @param trackerServer
     * @return
     */
    private void initTrackerServer(String trackerServer) {
        if (StringUtils.isBlank(trackerServer)) {
            return;
        }
        InetSocketAddress address = getInetSocketAddress(trackerServer);
        // 放到轮询圈
        TrackerAddressHolder holder = new TrackerAddressHolder(address);
        trackerAddressCircular.add(holder);
        trackerAddressMap.put(address, holder);
        cullExecutor.addTracker(address);
    }

    private InetSocketAddress getInetSocketAddress(String trackerServer) {
        String[] parts = StringUtils.split(trackerServer, ":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "the value of item \"tracker_server\" is invalid, the correct format is host:port");
        }
        return new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
    }


    public List<String> getTrackerServers() {
        return trackerServers;
    }

    public void setTrackerServers(List<String> trackerServers) {
        this.trackerServers = trackerServers;
    }

    public int getRetryAfterSecond() {
        return retryAfterSecond;
    }

    public void setRetryAfterSecond(int retryAfterSecond) {
        this.retryAfterSecond = retryAfterSecond;
    }

    /**
     * 获取Tracker服务器地址
     *
     * @return trackerAddress
     */
    public InetSocketAddress getTrackerAddress() {
        // 剔除圈重新加入轮询圈 保证高可用
        checkCullTracker();
        if (!trackerAddressCircular.isEmpty()) {
            return trackerAddressCircular.next().getAddress();
        }
        throw new FdfsUnavailableException("找不到可用的tracker " + getTrackerAddressConfigString());
    }

    private void checkCullTracker() {
        if (cullTrackerAddressCircular.isEmpty()) {
            return;
        }
        if (retryAfterSecond > 0) {
            List<TrackerAddressHolder> removeList = new ArrayList<>();
            for (TrackerAddressHolder holder : cullTrackerAddressCircular) {
                if (holder.canTryToConnect(retryAfterSecond)) {
                    removeList.add(holder);
                }
            }
            for (TrackerAddressHolder holder : removeList) {
                recoveryTracker(holder.getAddress());
            }
        }
    }

    /**
     * 获取配置地址列表
     *
     * @return trackerAddressConfig
     */
    private String getTrackerAddressConfigString() {
        return StringUtils.join(trackerServers, ",");
    }

    /**
     * 尝试获取其他可用tracker
     *
     * @param address
     * @return
     */
    public InetSocketAddress tryOtherTrackerAddress(InetSocketAddress address) {
        TrackerAddressHolder holder;
        // 遍历连接地址,抓取当前有效的地址
        for (int i = 0; i < trackerAddressCircular.size(); i++) {
            holder = trackerAddressCircular.next();
            InetSocketAddress holderAddress = holder.getAddress();
            if (!holderAddress.equals(address)) {
                return holderAddress;
            }
        }
        return address;
    }

    /**
     * TRACKER_LOCK
     */
    private static final Lock TRACKER_LOCK = new ReentrantLock();

    /**
     * 添加tracker
     *
     * @param trackerServer
     */
    public boolean addTracker(String trackerServer) {
        try {
            if (TRACKER_LOCK.tryLock()) {
                if (StringUtils.isBlank(trackerServer)) {
                    return false;
                }

                if (trackerServers.contains(trackerServer)) {
                    return true;
                }

                initTrackerServer(trackerServer);
                trackerServers.add(trackerServer);
                return true;
            }
            return false;
        } finally {
            TRACKER_LOCK.unlock();
        }
    }

    /**
     * 移除tracker
     * 如果remove一个被剔除正在恢复的tracker可能会不成功
     *
     * @param trackerServer
     */
    public boolean removeTracker(String trackerServer) {
        try {
            if (TRACKER_LOCK.tryLock()) {
                if (StringUtils.isBlank(trackerServer)) {
                    return false;
                }

                if (!trackerServers.contains(trackerServer)) {
                    return true;
                }

                InetSocketAddress address = getInetSocketAddress(trackerServer);
                TrackerAddressHolder holder = trackerAddressMap.remove(address);
                trackerAddressCircular.remove(holder);
                cullTrackerAddressCircular.remove(holder);
                cullExecutor.removeTracker(address);
                trackerServers.remove(trackerServer);
                return true;
            }
            return false;
        } finally {
            TRACKER_LOCK.unlock();
        }
    }

    /**
     * 剔除tracker
     *
     * @param address
     */
    public boolean cullTracker(InetSocketAddress address) {
        try {
            if (TRACKER_LOCK.tryLock()) {
                TrackerAddressHolder holder = trackerAddressMap.get(address);
                // is remove
                if (holder == null) {
                    return true;
                }
                // is cull
                if (cullTrackerAddressCircular.contains(holder)) {
                    return true;
                }
                if (cullExecutor.cullTracker(address)) {
                    holder.setInActive();
                    trackerAddressCircular.remove(holder);
                    cullTrackerAddressCircular.add(holder);
                    cullExecutor.recoveryTracker(address);
                    return true;
                }
            }
            return false;
        } finally {
            TRACKER_LOCK.unlock();
        }
    }

    /**
     * 恢复tracker
     *
     * @param address
     */
    public boolean recoveryTracker(InetSocketAddress address) {
        try {
            if (TRACKER_LOCK.tryLock()) {
                TrackerAddressHolder holder = trackerAddressMap.get(address);
                // is remove
                if (holder == null) {
                    return false;
                }
                cullExecutor.recoveryTracker(address);
                // is recovery
                if (!cullTrackerAddressCircular.contains(holder)) {
                    return true;
                }
                trackerAddressCircular.add(holder);
                cullTrackerAddressCircular.remove(holder);
                return true;
            }
            return false;
        } finally {
            TRACKER_LOCK.unlock();
        }
    }
}
