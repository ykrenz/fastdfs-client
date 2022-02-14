package com.ykren.fastdfs;


import com.ykren.fastdfs.event.UploadProgressListener;
import com.ykren.fastdfs.model.DownloadFileRequest;
import com.ykren.fastdfs.model.MetaDataRequest;
import com.ykren.fastdfs.model.ThumbImage;
import com.ykren.fastdfs.model.UploadFileRequest;
import com.ykren.fastdfs.model.UploadImageRequest;
import com.ykren.fastdfs.model.UploadSalveFileRequest;
import com.ykren.fastdfs.model.fdfs.FileInfo;
import com.ykren.fastdfs.model.fdfs.ImageStorePath;
import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.proto.storage.DownloadFileWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 文件基础操作测试演示
 *
 * @author tobato
 */
public class StorageClientBasicTest extends BaseClientTest {

    protected static Logger LOGGER = LoggerFactory.getLogger(StorageClientBasicTest.class);

    /**
     * 基本文件上传操作测试
     *
     * @throws IOException
     */
    @Test
    public void uploadFile() throws IOException {
        LOGGER.debug("##上传文件..##");
        StorePath storePath = uploadRandomFile();

        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .build();
        storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("上传文件 result={}", storePath);

        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        MetaData metaData1 = new MetaData(key1, value1);
        MetaData metaData2 = new MetaData(key2, value2);

        File localFile = getFile();
        fileRequest = UploadFileRequest.builder()
                .file(localFile)
                .metaData(key1, value1)
                .metaData(key2, value2)
                .build();
        storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        Set<MetaData> metaData = getMetaData(storePath);
        LOGGER.debug("上传文件带元数据 result={} meta = {}", storePath, metaData);
        assertEquals(2, metaData.size());
        assertTrue(metaData.contains(metaData1));
        assertTrue(metaData.contains(metaData2));

        Set<MetaData> metaDataSet = new HashSet<>();
        metaDataSet.add(metaData1);
        metaDataSet.add(metaData2);
        fileRequest = UploadFileRequest.builder()
                .file(localFile)
                .metaData(metaDataSet)
                .build();
        storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("上传文件MetaData result={}", storePath);
        assertEquals(2, metaData.size());
        assertTrue(metaData.contains(metaData1));
        assertTrue(metaData.contains(metaData2));

        delete(storePath);

        FileInfo fileInfo = queryFile(storePath);
        assertNull(fileInfo);
    }


    @Test
    public void uploadSlaveFile() {
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), "jpg")
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        LOGGER.debug("上传主文件 result={}", storePath);
        assertNotNull(storePath);

        UploadSalveFileRequest salveFileRequest = UploadSalveFileRequest.builder()
                .group(storePath.getGroup())
                .masterPath(storePath.getPath())
                .prefix("_")
                .metaData("salveKey", "salveValue")
//                .file(getFile())
                .stream(file.getInputStream(), file.getFileSize(), "jpg")
                .build();
        LOGGER.debug("##上传从文件..##");
        StorePath slavePath = fastDFS.uploadSlaveFile(salveFileRequest);
        LOGGER.debug("上传从文件 result={}", slavePath);
    }

    @Test
    public void metadataTest() {
        String key1 = "key1";
        String value1 = "value1";
        String key2 = "key2";
        String value2 = "value2";
        MetaData metaData1 = new MetaData(key1, value1);
        MetaData metaData2 = new MetaData(key2, value2);

        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .file(getFile())
                .metaData(key1, value1)
                .metaData(key2, value2)
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        Set<MetaData> metaData = getMetaData(storePath);
        LOGGER.debug("result={} meta = {}", storePath, metaData);
        assertEquals(2, metaData.size());
        assertTrue(metaData.contains(metaData1));
        assertTrue(metaData.contains(metaData2));

        MetaDataRequest metaDataRequest = MetaDataRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .metaData(key1, value1)
                .build();

        fastDFS.mergeMetadata(metaDataRequest);
        metaData = getMetaData(storePath);
        LOGGER.debug("merge MetaData result={}", metaData);
        assertEquals(2, metaData.size());
        assertTrue(metaData.contains(metaData1));
        assertTrue(metaData.contains(metaData2));

        String key3 = "key3";
        String value3 = "value3";
        MetaData metaData3 = new MetaData(key3, value3);
        metaDataRequest = MetaDataRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .metaData(key3, value3)
                .build();

        fastDFS.mergeMetadata(metaDataRequest);
        metaData = getMetaData(storePath);
        LOGGER.debug("merge MetaData result={}", metaData);
        assertEquals(3, metaData.size());
        assertTrue(metaData.contains(metaData1));
        assertTrue(metaData.contains(metaData2));
        assertTrue(metaData.contains(metaData3));

        String newkey = "newkey";
        String newvalue = "newvalue";
        MetaData newMetaData = new MetaData(newkey, newvalue);
        metaDataRequest = MetaDataRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .metaData(newkey, newvalue)
                .build();
        fastDFS.overwriteMetadata(metaDataRequest);
        metaData = getMetaData(storePath);
        assertEquals(1, metaData.size());
        assertTrue(metaData.contains(newMetaData));

        delete(storePath);
        assertNull(queryFile(storePath));
    }

    @Test
    public void queryFileInfo() {
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .file(getFile())
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        FileInfo fileInfo = queryFile(storePath);
        assertNotNull(fileInfo);

        delete(storePath);
        assertNull(queryFile(storePath));
    }

    @Test
    public void deleteFileInfo() {
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .file(getFile())
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        delete(storePath);
        delete(storePath);
        assertNull(queryFile(storePath));
    }

    @Test
    public void downLoadTest() {
        StorePath storePath = uploadRandomFile();
        DownloadFileRequest request = DownloadFileRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .fileSize(2)
                .build();
        fastDFS.downloadFile(request, new DownloadFileWriter("tmp/tmp1.txt"));

        delete(storePath);
    }

    @Test
    public void uploadImageTest() {
        File file = getFile();
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("a", "a"));
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
                .listener(new UploadProgressListener() {
                    @Override
                    public void start() {

                    }

                    @Override
                    public void uploading() {
                        System.out.println(percent());
                    }

                    @Override
                    public void completed() {

                    }

                    @Override
                    public void failed() {

                    }
                })
                .build();
        ImageStorePath imageStorePath = fastDFS.uploadImage(request);
        LOGGER.info("imgPath={}", imageStorePath.getImg().getWebPath());
        LOGGER.info("imgPath={}", imageStorePath.getImg().getDownLoadPath("1.jpg"));
        LOGGER.info("thumbPath={}", imageStorePath.getThumbs());
//        delete(imageStorePath.getImg());
//        for (StorePath path : imageStorePath.getThumbs()) {
//            delete(path);
//        }
    }

    @Test
    public void createThumbImageTest() {
        File file = getFile();
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("a", "a"));
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
                .thumbImage(new ThumbImage(150, 150), metaData)
                .build();
        StorePath thumbImage = fastDFS.createThumbImage(request);
        LOGGER.info("thumbImage={}", thumbImage);
        delete(thumbImage);

        UploadImageRequest request2 = UploadImageRequest.builder()
                .file(file)
                .thumbImage(new ThumbImage(150, 150), metaData)
                .thumbImage(new ThumbImage(100, 100), metaData)
                .thumbImage(new ThumbImage(0.5), metaData)
                .build();
        List<StorePath> thumbImages = fastDFS.createThumbImages(request2);
        LOGGER.info("thumbImages={}", thumbImages);
        for (StorePath path : thumbImages) {
            delete(path);
        }
    }
}
