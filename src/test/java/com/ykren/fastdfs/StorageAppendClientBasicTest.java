package com.ykren.fastdfs;

import com.ykren.fastdfs.model.AppendFileRequest;
import com.ykren.fastdfs.model.ModifyFileRequest;
import com.ykren.fastdfs.model.RegenerateAppenderFileRequest;
import com.ykren.fastdfs.model.TruncateFileRequest;
import com.ykren.fastdfs.model.UploadFileRequest;
import com.ykren.fastdfs.model.fdfs.FileInfo;
import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.proto.storage.enums.StorageMetadataSetType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * 文件基础操作测试演示
 *
 * @author tobato
 */
public class StorageAppendClientBasicTest extends BaseClientTest {

    protected static Logger LOGGER = LoggerFactory.getLogger(StorageAppendClientBasicTest.class);

    /**
     * appender文件上传操作测试
     *
     * @throws IOException
     */
    @Test
    public void uploadAppendFile() throws IOException {
        LOGGER.debug("##append上传文件..##");
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .build();
        StorePath storePath = fastDFS.uploadAppenderFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("append上传文件 result={}", storePath);

        Set<MetaData> metaData = getMetaData(storePath);
        assertTrue(metaData.isEmpty());

        metaData.add(new MetaData("key1", "value1"));
        metaData.add(new MetaData("key2", "value2"));
        AppendFileRequest appendFileRequest = AppendFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize())
                .path(storePath.getPath())
                .group(storePath.getGroup())
                .metaData(metaData, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE)
                .build();
        fastDFS.appendFile(appendFileRequest);
        metaData = getMetaData(storePath);
        assertEquals(2, metaData.size());

        metaData.clear();
        MetaData newdata = new MetaData("newkey", "newvalue");
        metaData.add(newdata);
        appendFileRequest = AppendFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize())
                .path(storePath.getPath())
                .group(storePath.getGroup())
                .metaData(metaData, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE)
                .build();
        fastDFS.appendFile(appendFileRequest);
        assertEquals(1, getMetaData(storePath).size());
        assertTrue(getMetaData(storePath).contains(newdata));


        metaData.clear();
        MetaData newdata2 = new MetaData("newkey", "newvalue2");
        metaData.add(newdata2);
        appendFileRequest = AppendFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize())
                .path(storePath.getPath())
                .group(storePath.getGroup())
                .metaData(metaData, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_MERGE)
                .build();
        fastDFS.appendFile(appendFileRequest);
        assertEquals(1, getMetaData(storePath).size());
        assertTrue(getMetaData(storePath).contains(newdata2));

        appendFileRequest = AppendFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize())
                .path(storePath.getPath())
                .group(storePath.getGroup())
                .metaData(new HashSet<>(), StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE)
                .build();
        fastDFS.appendFile(appendFileRequest);
        assertTrue(getMetaData(storePath).isEmpty());

        delete(storePath);
    }

    /**
     * appender文件上传操作测试
     *
     * @throws IOException
     */
    @Test
    public void modifyAppendFile() throws IOException {
        LOGGER.debug("##append上传文件..##");
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .metaData("key1", "value1")
                .metaData("key2", "value2")
                .build();
        StorePath storePath = fastDFS.uploadAppenderFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("append上传文件 result={} text={}", storePath, file.getText());

        Set<MetaData> metaData = getMetaData(storePath);
        assertEquals(2, metaData.size());
        assertTrue(metaData.contains(new MetaData("key1", "value1")));
        assertTrue(metaData.contains(new MetaData("key2", "value2")));

        RandomTextFile modifyFile = new RandomTextFile();
        ModifyFileRequest modifyFileRequest = ModifyFileRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .stream(modifyFile.getInputStream(), modifyFile.getFileSize(), 0)
                .build();
        fastDFS.modifyFile(modifyFileRequest);
        LOGGER.debug("modify上传文件 result={} text={}", storePath, modifyFile.getText());

        String text = "123";
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        modifyFileRequest = ModifyFileRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .stream(inputStream, text.length(), 0)
                .build();
        fastDFS.modifyFile(modifyFileRequest);
        LOGGER.debug("modify上传文件 result={} text={}", storePath, modifyFile.getText());

        metaData.clear();
        MetaData overMeta = new MetaData("newkey", "newvalue");
        metaData.add(overMeta);
        modifyFileRequest = ModifyFileRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .stream(modifyFile.getInputStream(), modifyFile.getFileSize(), 0)
                .metaData(metaData, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE)
                .build();

        fastDFS.modifyFile(modifyFileRequest);
        LOGGER.debug("modify上传文件 result={} text={}", storePath, modifyFile.getText());
        metaData = getMetaData(storePath);
        assertEquals(1, metaData.size());
        assertTrue(metaData.contains(overMeta));


        metaData.clear();
        MetaData newdata = new MetaData("newkey", "newvalue1");
        metaData.add(newdata);
        modifyFileRequest = ModifyFileRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .stream(modifyFile.getInputStream(), modifyFile.getFileSize(), 0)
                .metaData(metaData, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_MERGE)
                .build();

        fastDFS.modifyFile(modifyFileRequest);
        LOGGER.debug("modify上传文件 result={} text={}", storePath, modifyFile.getText());
        metaData = getMetaData(storePath);
        assertEquals(1, metaData.size());
        assertTrue(metaData.contains(newdata));


        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                RandomTextFile modifyFile2 = new RandomTextFile();
                ModifyFileRequest modifyFileRequest2 = ModifyFileRequest.builder()
                        .group(storePath.getGroup())
                        .path(storePath.getPath())
                        .stream(modifyFile2.getInputStream(), modifyFile2.getFileSize(), 0)
                        .build();
                LOGGER.info("start==========" + Thread.currentThread().getName());
                fastDFS.modifyFile(modifyFileRequest2);
                LOGGER.info("modify上传文件 size={} result={} text={}", storePath, modifyFile2.getFileSize() / 1024 / 1024,
                        StringUtils.substring(modifyFile2.getText(), modifyFile2.getText().length() - 10));
                LOGGER.info("end==========" + Thread.currentThread().getName());
            });
        }
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        delete(storePath);
    }

    @Test
    public void truncateFile() {
        LOGGER.debug("##append上传文件..##");
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .metaData("key1", "value1")
                .metaData("key2", "value2")
                .build();
        StorePath storePath = fastDFS.uploadAppenderFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("append上传文件 result={} text={}", storePath, file.getText());

        TruncateFileRequest request = TruncateFileRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        fastDFS.truncateFile(request);

        FileInfo fileInfo = queryFile(storePath);
        assertEquals(0, fileInfo.getFileSize());

        long size = 100;
        request = TruncateFileRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .fileSize(size)
                .build();
        fastDFS.truncateFile(request);
        fileInfo = queryFile(storePath);
        assertEquals(size, fileInfo.getFileSize());

        delete(storePath);
    }

    @Test
    public void regenerateAppenderFile() {
        LOGGER.debug("##append上传文件..##");
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .metaData("key1", "value1")
                .metaData("key2", "value2")
                .build();
        StorePath storePath = fastDFS.uploadAppenderFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("append上传文件 result={} text={}", storePath, file.getText());

        RegenerateAppenderFileRequest request = RegenerateAppenderFileRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        StorePath reStorePath = fastDFS.regenerateAppenderFile(request);
        LOGGER.debug("append文件改为普通文件 result={} text={}", reStorePath, file.getText());
        assertNotNull(reStorePath);
        assertNull(queryFile(storePath));
        delete(reStorePath);
    }
}
