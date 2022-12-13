package com.ykrenz.fastdfs.model.fdfs;

import java.net.InetSocketAddress;

/**
 * 管理TrackerAddress当前状态
 *
 * @author tobato
 */
public class TrackerAddressHolder {
    /**
     * 连接地址
     */
    private InetSocketAddress address;
    /**
     * 当前是否有效
     */
    private boolean available;
    /**
     * 上次无效时间
     */
    private long lastUnavailableTime;

    /**
     * 构造函数
     *
     * @param address
     */
    public TrackerAddressHolder(InetSocketAddress address) {
        super();
        this.address = address;
        // 默认Tracker有效
        this.available = true;
    }

    /**
     * 有效
     */
    public void setActive() {
        this.available = true;
    }

    /**
     * 无效
     */
    public void setInActive() {
        this.available = false;
        this.lastUnavailableTime = System.currentTimeMillis();
    }

    public boolean isAvailable() {
        return available;
    }

    public long getLastUnavailableTime() {
        return lastUnavailableTime;
    }

    /**
     * 是否可以尝试连接
     *
     * @param retryAfterSecond 在n秒后重试
     * @return
     */
    public boolean canTryToConnect(int retryAfterSecond) {
        // 如果是有效连接
        if (this.available) {
            return true;
        }
        // 如果连接无效，并且达到重试时间
        return retryAfterSecond > 0 && (System.currentTimeMillis() - lastUnavailableTime) > retryAfterSecond * 1000L;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

}
