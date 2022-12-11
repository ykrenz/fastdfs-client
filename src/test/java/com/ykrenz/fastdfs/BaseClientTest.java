package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.config.FastDfsConfiguration;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.FileInfoRequest;
import com.ykrenz.fastdfs.model.MetaDataInfoRequest;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;


/**
 * @author ykren
 * @date 2022/1/20
 */
public class BaseClientTest {

    protected TrackerClient trackerClient;
    protected FastDfs fastDFS;

    protected static Logger LOGGER = LoggerFactory.getLogger(BaseClientTest.class);
    public static final List<String> TRACKER_LIST = new ArrayList<>();

    static {
        TRACKER_LIST.add("192.168.100.200:22122");
//        TRACKER_LIST.add("192.168.24.131:22122");
//        TRACKER_LIST.add("192.168.24.132:22122");
    }

    @Before
    public void initClient() {
        FastDfsConfiguration configuration = new FastDfsConfiguration();
        configuration.setDefaultGroup("group1");
        configuration.getHttp().getWebServers().add("http://192.168.24.130:8888");
        configuration.getHttp().setUrlHaveGroup(true);
        configuration.getHttp().setHttpAntiStealToken(true);
        configuration.getHttp().setSecretKey("FastDFS1234567890");
        fastDFS = new FastDfsClientBuilder().build(TRACKER_LIST, configuration);
        trackerClient = fastDFS.getTrackerClient();
        LOGGER.info("初始化tracker={}", trackerClient.getTrackerServers());
        LOGGER.info("fastDFSClient={}", fastDFS);
    }

    @After
    public void closeClient() {
        fastDFS.shutdown();
    }

    public StorePath uploadRandomFile() {
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.info("上传文件 text={} result={}", file.getText(), storePath);
        return storePath;
    }


    protected Set<MetaData> getMetaData(StorePath storePath) {
        MetaDataInfoRequest metaDataInfoRequest = MetaDataInfoRequest.builder()
                .groupName(storePath.getGroup()).path(storePath.getPath()).build();
        return fastDFS.getMetadata(metaDataInfoRequest);
    }

    protected void delete(StorePath storePath) {
        FileInfoRequest fileInfoRequest = FileInfoRequest.builder()
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        fastDFS.deleteFile(fileInfoRequest);
        LOGGER.info("删除文件成功 path={}", storePath);
    }

    protected FileInfo queryFile(StorePath storePath) {
        FileInfoRequest fileInfoRequest = FileInfoRequest.builder()
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        return fastDFS.queryFileInfo(fileInfoRequest);
    }

    protected File getFile() {
        return new File("src/test/resources/file/test.jpg");
    }
}
