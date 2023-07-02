package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.config.FastDfsConfiguration;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.GroupState;
import com.ykrenz.fastdfs.model.fdfs.StorageNode;
import com.ykrenz.fastdfs.model.fdfs.StorageNodeInfo;
import com.ykrenz.fastdfs.model.fdfs.StorageState;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TrackerTest {
    protected static Logger LOGGER = LoggerFactory.getLogger(TrackerTest.class);

    @Test
    public void testTrackerApi() {
        String tracker1 = "192.168.100.200:22122";
        FastDfs fastDfs = new FastDfsClientBuilder().build(tracker1);
        FastDfsConfiguration configuration = new FastDfsConfiguration();
        configuration.getHttp().getWebServers().add("http://192.168.100.200:8888");
        configuration.getHttp().setUrlHaveGroup(true);
        configuration.getHttp().setHttpAntiStealToken(true);
        configuration.getHttp().setSecretKey("FastDFS1234567890");


        try {
            //1
            List<String> servers = fastDfs.getTrackerServers();
            Assert.assertNotNull(servers);
            Assert.assertEquals(1, servers.size());
            Assert.assertEquals(tracker1, servers.get(0));

            String tracker2 = "192.168.100.201:22122";
            fastDfs.addTrackerServer(tracker2);
            servers = fastDfs.getTrackerServers();
            Assert.assertNotNull(servers);
            Assert.assertEquals(2, servers.size());
            Assert.assertEquals(tracker1, servers.get(0));
            Assert.assertEquals(tracker2, servers.get(1));

            fastDfs.removeTrackerServer(tracker1);
            servers = fastDfs.getTrackerServers();
            Assert.assertNotNull(servers);
            Assert.assertEquals(1, servers.size());
            Assert.assertEquals(tracker2, servers.get(0));

            StorageNode storeStorage = fastDfs.getStoreStorage();
            LOGGER.info("获取一个storage {}", storeStorage);
            List<GroupState> groupStates = fastDfs.listGroups();
            for (GroupState groupState : groupStates) {
                LOGGER.info("groupState={}", groupState);
//                LOGGER.info("groupState={}", fastDfs.getStoreStorage(groupState.getGroupName()));
                List<StorageState> storageStates = fastDfs.listStorages(groupState.getGroupName());
                LOGGER.info("group = {} storages ={}", groupState.getGroupName(), storageStates);
            }

            RandomTextFile file = new RandomTextFile(100);
            StorePath storePath = fastDfs.uploadFile(file.getInputStream(), 100, file.getFileExtName());

            FileInfo fileInfo = fastDfs.queryFileInfo(storePath.getGroup(), storePath.getPath());
            LOGGER.info("file info ={}", fileInfo);
            StorageNodeInfo fetchStorage = fastDfs.getFetchStorage(storePath.getGroup(), storePath.getPath());
            LOGGER.info("fetchStorage={}", fetchStorage);

            StorageNodeInfo updateStorage = fastDfs.getUpdateStorage(storePath.getGroup(), storePath.getPath());
            LOGGER.info("updateStorage={}", updateStorage);

            fastDfs.deleteFile(storePath.getGroup(), storePath.getPath());
        } finally {
            fastDfs.shutdown();
        }


    }
}
