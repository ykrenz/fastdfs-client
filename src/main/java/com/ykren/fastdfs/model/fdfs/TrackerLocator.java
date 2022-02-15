package com.ykren.fastdfs.model.fdfs;

import com.ykren.fastdfs.exception.FdfsUnavailableException;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
     * 连接中断以后经过N秒重试
     */
    private int retryAfterSecond;

    /**
     * 初始化Tracker服务器地址
     * 配置方式为 ip:port 如 192.168.1.2:21000
     *
     * @param trackerServers
     */
    public TrackerLocator(List<String> trackerServers) {
        super();
        this.trackerServers = trackerServers;
        buildTrackerAddresses();
    }

    /**
     * 分析TrackerAddress
     */
    private synchronized void buildTrackerAddresses() {
        Set<String> addressSet = new HashSet<>(trackerServers);
        for (String server : addressSet) {
            initTrackerServer(server);
        }
    }

    /**
     * 初始化tracker
     *
     * @param trackerServer
     * @return
     */
    private boolean initTrackerServer(String trackerServer) {
        if (StringUtils.isBlank(trackerServer)) {
            return false;
        }
        InetSocketAddress address = getInetSocketAddress(trackerServer);
        // 放到轮询圈
        TrackerAddressHolder holder = new TrackerAddressHolder(address);
        trackerAddressMap.put(address, holder);
        return trackerAddressCircular.add(holder);
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
        return Collections.unmodifiableList(trackerServers);
    }

    public void setTrackerServers(List<String> trackerServers) {
        this.trackerServers = trackerServers;
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
        TrackerAddressHolder holder;
        // 遍历连接地址,抓取当前有效的地址
        for (int i = 0; i < trackerAddressCircular.size(); i++) {
            holder = trackerAddressCircular.next();
            if (holder.canTryToConnect(retryAfterSecond)) {
                return holder.getAddress();
            }
        }
        throw new FdfsUnavailableException("找不到可用的tracker " + getTrackerAddressConfigString());
    }

    /**
     * 获取配置地址列表
     *
     * @return trackerAddressConfig
     */
    private String getTrackerAddressConfigString() {
        StringBuilder config = new StringBuilder();
        for (int i = 0; i < trackerAddressCircular.size(); i++) {
            TrackerAddressHolder holder = trackerAddressCircular.next();
            InetSocketAddress address = holder.getAddress();
            config.append(address.toString()).append(",");
        }
        return new String(config);
    }

    /**
     * 设置连接有效
     *
     * @param address
     */
    public void setActive(InetSocketAddress address) {
        TrackerAddressHolder holder = trackerAddressMap.get(address);
        holder.setActive();
    }

    /**
     * 设置连接无效
     *
     * @param address
     */
    public void setInActive(InetSocketAddress address) {
        TrackerAddressHolder holder = trackerAddressMap.get(address);
        holder.setInActive();
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

                return initTrackerServer(trackerServer) && trackerServers.add(trackerServer);
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
                return trackerAddressCircular.remove(holder) && trackerServers.remove(trackerServer);
            }
            return false;
        } finally {
            TRACKER_LOCK.unlock();
        }
    }

}
