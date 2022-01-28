package com.ykren.fastdfs;

import com.ykren.fastdfs.model.fdfs.FileInfo;
import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.FileInfoRequest;
import com.ykren.fastdfs.model.MetaDataInfoRequest;
import com.ykren.fastdfs.model.UploadFileRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
    protected FastDFS fastDFS;

    protected static Logger LOGGER = LoggerFactory.getLogger(BaseClientTest.class);
    public static final String localFilePath = "/file/test.jpg";
    public static final List<String> TRACKER_LIST = new ArrayList<>();

    static {
        TRACKER_LIST.add("192.168.24.129:22122");
    }

    @Before
    public void initClient() {
        fastDFS = new FastDFSClientBuilder().build(TRACKER_LIST);
        trackerClient = fastDFS.trackerClient();
        LOGGER.info("初始化tracker={}", trackerClient.getTrackerList());
        LOGGER.info("fastDFSClient={}", fastDFS);


    }

    @After
    public void closeClient() {
        fastDFS.close();
    }

    public StorePath uploadRandomFile() {
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("上传文件 result={}", storePath);
        return storePath;
    }


    protected Set<MetaData> getMetaData(StorePath storePath) {
        MetaDataInfoRequest metaDataInfoRequest = MetaDataInfoRequest.builder()
                .group(storePath.getGroup()).path(storePath.getPath()).build();
        return fastDFS.getMetadata(metaDataInfoRequest);
    }

    protected void delete(StorePath storePath) {
        FileInfoRequest fileInfoRequest = FileInfoRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        fastDFS.deleteFile(fileInfoRequest);
    }

    protected FileInfo queryFile(StorePath storePath) {
        FileInfoRequest fileInfoRequest = FileInfoRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        return fastDFS.queryFileInfo(fileInfoRequest);
    }

    protected File getFile() {
        URL path = BaseClientTest.class.getResource(localFilePath);
        return new File(path.getPath());
    }
}
