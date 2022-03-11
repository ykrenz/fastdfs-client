package com.ykrenz.fastdfs.monitor;

import com.ykrenz.fastdfs.TrackerClient;
import com.ykrenz.fastdfs.exception.FdfsUnavailableException;
import com.ykrenz.fastdfs.model.fdfs.GroupState;
import com.ykrenz.fastdfs.model.fdfs.StorageState;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Storage监控
 *
 * @author ykren
 * @date 2022/2/18
 */
public class StorageMonitor implements FastDfsMonitor {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageMonitor.class);

    private final TrackerClient trackerClient;

    /**
     * 详见:
     * tracker.conf
     * reserved_storage_space
     */
    private final String reservedStorageSpace;

    public StorageMonitor(TrackerClient trackerClient, String reservedStorageSpace) {
        this.trackerClient = trackerClient;
        this.reservedStorageSpace = reservedStorageSpace;
    }

    @Override
    public void monitor() {
        try {
            List<String> trackerServers = trackerClient.getTrackerServers();
            LOGGER.debug("trackerServers::{}", trackerServers);
            List<GroupState> groupStates = trackerClient.listGroups();
            if (CollectionUtils.isEmpty(groupStates)) {
                monitorException(new FdfsUnavailableException("No available group found in tracker service" + trackerServers));
            }
            for (GroupState groupState : groupStates) {
                LOGGER.debug("group stage::{}", groupState);
                String groupName = groupState.getGroupName();
                List<StorageState> storageStates = trackerClient.listStorages(groupName);
                for (StorageState storageState : storageStates) {
                    LOGGER.debug("group name::{} storage stage::{}", groupName, storageState);
                    monitorStorage(groupState, storageState);
                }
            }
        } catch (Exception e) {
            monitorException(e);
        }
    }

    protected void monitorException(Exception ex) {
        LOGGER.error("storage monitor errorMessage={}", ex.getMessage());
    }

    protected void monitorStorage(GroupState groupState, StorageState storageState) {
        long totalMb = storageState.getTotalMB();
        long freeMb = storageState.getFreeMB();
        ReservedMode mode = this.getMode(reservedStorageSpace);
        String number = reservedStorageSpace.substring(0, reservedStorageSpace.length() - 1);
        BigDecimal reservedSpace = new BigDecimal(number);
        boolean insufficientSpace = false;
        switch (mode) {
            case NONE:
                LOGGER.warn("illegal arg not ends with G/g M/m K/k B/b %");
                break;
            case RATIO:
                BigDecimal freeRatio = BigDecimal.valueOf((float) freeMb * 100 / totalMb).setScale(2, BigDecimal.ROUND_UP);
                insufficientSpace = freeRatio.compareTo(reservedSpace) <= 0;
                break;
            case B:
                long freeBytes = freeMb * 1024L * 1024L;
                insufficientSpace = freeBytes <= reservedSpace.longValue();
                break;
            case K:
                long freeKb = freeMb * 1024L;
                insufficientSpace = freeKb <= reservedSpace.longValue();
                break;
            case M:
                insufficientSpace = freeMb <= reservedSpace.longValue();
                break;
            case G:
                BigDecimal freeGb = BigDecimal.valueOf((float) freeMb / 1024L).setScale(2, BigDecimal.ROUND_UP);
                insufficientSpace = freeGb.compareTo(reservedSpace) <= 0;
                break;
            default:
                break;
        }
        if (insufficientSpace) {
            insufficientSpace(groupState, storageState);
        }
    }

    protected void insufficientSpace(GroupState groupState, StorageState storageState) {
        long totalMb = storageState.getTotalMB();
        long freeMb = storageState.getFreeMB();
        LOGGER.error("Waring!!! Storage Insufficient Space [ip:{} totalMB:{} freeMB:{} ReservedSpace:{}]",
                storageState.getIpAddr(), totalMb, freeMb, reservedStorageSpace);
    }

    private ReservedMode getMode(String reservedStorageSpace) {
        if (NumberUtils.isNumber(reservedStorageSpace)) {
            return ReservedMode.B;
        }
        ReservedMode[] modes = ReservedMode.values();
        for (ReservedMode mode : modes) {
            String lastStr = reservedStorageSpace.substring(reservedStorageSpace.length() - 1);
            if (mode.getKey().equalsIgnoreCase(lastStr)) {
                return mode;
            }
        }
        return ReservedMode.NONE;
    }

    /**
     * 预留类型
     */
    enum ReservedMode {
        /**
         * 未知
         */
        NONE(""),
        /**
         * %
         */
        RATIO("%"),
        /**
         * GB
         */
        G("G"),
        /**
         * MB
         */
        M("M"),
        /**
         * KB
         */
        K("K"),
        /**
         * B
         */
        B("B");
        private String key;

        ReservedMode(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
