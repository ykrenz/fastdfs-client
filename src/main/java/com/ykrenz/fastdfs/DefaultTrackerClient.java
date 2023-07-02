package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.conn.TrackerConnectionManager;
import com.ykrenz.fastdfs.model.fdfs.GroupState;
import com.ykrenz.fastdfs.model.fdfs.StorageNode;
import com.ykrenz.fastdfs.model.fdfs.StorageNodeInfo;
import com.ykrenz.fastdfs.model.fdfs.StorageState;
import com.ykrenz.fastdfs.model.proto.tracker.TrackerDeleteStorageCommand;
import com.ykrenz.fastdfs.model.proto.tracker.TrackerGetFetchStorageCommand;
import com.ykrenz.fastdfs.model.proto.tracker.TrackerGetStoreStorageCommand;
import com.ykrenz.fastdfs.model.proto.tracker.TrackerListGroupsCommand;
import com.ykrenz.fastdfs.model.proto.tracker.TrackerListStoragesCommand;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 目录服务客户端默认实现
 *
 * @author tobato
 */
public class DefaultTrackerClient implements TrackerClient {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTrackerClient.class);

    private TrackerConnectionManager trackerConnectionManager;

    public DefaultTrackerClient(TrackerConnectionManager trackerConnectionManager) {
        this.trackerConnectionManager = trackerConnectionManager;
    }

    public TrackerConnectionManager getTrackerConnectionManager() {
        return trackerConnectionManager;
    }

    public void setTrackerConnectionManager(TrackerConnectionManager trackerConnectionManager) {
        this.trackerConnectionManager = trackerConnectionManager;
    }

    @Override
    public void shutdown() {
        trackerConnectionManager.getPool().close();
        LOGGER.debug("fastdfs tracker server shutdown");
    }

    @Override
    public List<String> getTrackerServers() {
        return trackerConnectionManager.getTrackerLocator().getTrackerServers();
    }

    @Override
    public void addTrackerServer(String trackerServer) {
        trackerConnectionManager.getTrackerLocator().addTracker(trackerServer);
    }

    @Override
    public void removeTrackerServer(String trackerServer) {
        trackerConnectionManager.getTrackerLocator().removeTracker(trackerServer);
    }

    /**
     * 获取存储节点
     */
    @Override
    public StorageNode getStoreStorage() {
        TrackerGetStoreStorageCommand command = new TrackerGetStoreStorageCommand();
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 按组获取存储节点
     */
    @Override
    public StorageNode getStoreStorage(String groupName) {
        TrackerGetStoreStorageCommand command;
        if (StringUtils.isBlank(groupName)) {
            command = new TrackerGetStoreStorageCommand();
        } else {
            command = new TrackerGetStoreStorageCommand(groupName);
        }

        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 获取源服务器
     */
    @Override
    public StorageNodeInfo getFetchStorage(String groupName, String filename) {
        TrackerGetFetchStorageCommand command = new TrackerGetFetchStorageCommand(groupName, filename, false);
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 获取更新服务器
     */
    @Override
    public StorageNodeInfo getUpdateStorage(String groupName, String filename) {
        TrackerGetFetchStorageCommand command = new TrackerGetFetchStorageCommand(groupName, filename, true);
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 列出组
     */
    @Override
    public List<GroupState> listGroups() {
        TrackerListGroupsCommand command = new TrackerListGroupsCommand();
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 按组列出存储状态
     */
    @Override
    public List<StorageState> listStorages(String groupName) {
        TrackerListStoragesCommand command = new TrackerListStoragesCommand(groupName);
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 按ip列出存储状态
     */
    @Override
    public List<StorageState> listStorages(String groupName, String storageIpAddr) {
        TrackerListStoragesCommand command = new TrackerListStoragesCommand(groupName, storageIpAddr);
        return trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

    /**
     * 删除存储节点
     */
    @Override
    public void deleteStorage(String groupName, String storageIpAddr) {
        TrackerDeleteStorageCommand command = new TrackerDeleteStorageCommand(groupName, storageIpAddr);
        trackerConnectionManager.executeFdfsTrackerCmd(command);
    }

}
