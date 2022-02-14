package com.ykren.fastdfs.conn;

/**
 * tracker 剔除策略
 *
 * @author ykren
 * @date 2022/2/11
 */
public interface TrackerCullExecutor<T> {

    /**
     * 添加
     *
     * @param trackerServer
     */
    void addTracker(T trackerServer);

    /**
     * 剔除
     *
     * @param trackerServer
     * @return
     */
    boolean cullTracker(T trackerServer);

    /**
     * 恢复
     *
     * @param trackerServer
     */
    void recoveryTracker(T trackerServer);

    /**
     * 恢复
     *
     * @param trackerServer
     */
    void removeTracker(T trackerServer);
}
