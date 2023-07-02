package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.model.fdfs.GroupState;
import com.ykrenz.fastdfs.model.fdfs.StorageNode;
import com.ykrenz.fastdfs.model.fdfs.StorageNodeInfo;
import com.ykrenz.fastdfs.model.fdfs.StorageState;

import java.util.List;


/**
 * 目录服务(Tracker)客户端接口
 *
 * @author tobato
 */
public interface TrackerClient {

    /**
     * 关闭客户端
     */
    void shutdown();

    /**
     * author: ykren
     * 获取tracker节点 get TrackerList
     *
     * @return
     */
    List<String> getTrackerServers();

    /**
     * 添加tracker
     *
     * @param trackerServer
     */
    void addTrackerServer(String trackerServer);

    /**
     * 移除tracker
     *
     * @param trackerServer
     */
    void removeTrackerServer(String trackerServer);

    /**
     * 获取存储节点 get the StoreStorage Client
     *
     * @return
     */
    StorageNode getStoreStorage();

    /**
     * 按组获取存储节点 get the StoreStorage Client by group
     *
     * @param groupName
     * @return
     */
    StorageNode getStoreStorage(String groupName);

    /**
     * 获取读取存储节点 get the fetchStorage Client by group and filename
     *
     * @param groupName
     * @param filename
     * @return
     */
    StorageNodeInfo getFetchStorage(String groupName, String filename);

    /**
     * 获取更新节点 get the updateStorage Client by group and filename
     *
     * @param groupName
     * @param filename
     * @return
     */
    StorageNodeInfo getUpdateStorage(String groupName, String filename);

    /**
     * 获取组状态list groups
     *
     * @return
     */
    List<GroupState> listGroups();

    /**
     * 按组名获取存储节点状态list storages by groupName
     *
     * @param groupName
     * @return
     */
    List<StorageState> listStorages(String groupName);

    /**
     * 获取存储状态 list storages by groupName and storageIpAddr
     *
     * @param groupName
     * @param storageIpAddr
     * @return
     */
    List<StorageState> listStorages(String groupName, String storageIpAddr);

    /**
     * 删除存储节点 delete storage from TrackerServer
     *
     * @param groupName
     * @param storageIpAddr
     */
    void deleteStorage(String groupName, String storageIpAddr);

}
