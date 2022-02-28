package com.ykren.fastdfs;


import com.ykren.fastdfs.event.UploadProgressListener;
import com.ykren.fastdfs.model.DownloadFileRequest;
import com.ykren.fastdfs.model.ThumbImage;
import com.ykren.fastdfs.model.UploadFileRequest;
import com.ykren.fastdfs.model.UploadImageRequest;
import com.ykren.fastdfs.model.UploadSalveFileRequest;
import com.ykren.fastdfs.model.fdfs.ImageStorePath;
import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.proto.storage.DownloadByteArray;
import com.ykren.fastdfs.model.proto.storage.DownloadFileWriter;
import com.ykren.fastdfs.model.proto.storage.DownloadOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 文件基础操作测试演示
 *
 * @author tobato
 */
public class FastDFSClientTest extends BaseClientTest {

    protected static Logger LOGGER = LoggerFactory.getLogger(FastDFSClientTest.class);
    String testFilePath = "D:\\Users\\ykren\\Downloads\\Git-2.34.1-64-bit.exe";

    /**
     * 基本文件上传操作测试
     *
     * @throws IOException
     */
    @Test
    public void uploadFile() throws IOException {
        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
//        File file = new File(testFilePath);
//        FileInputStream stream = new FileInputStream(file);
//        long crc32 = FileUtils.checksumCRC32(file);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .stream(stream, file.length(), "exe")
//                .crc32(crc32)
                .metaData("key1", "value1")
                .metaData("key2", "value2")
                .listener(new UploadProgressListener() {
                    @Override
                    public void start() {
                        LOGGER.debug("开始上传...文件总大小={}", totalBytes);
                    }

                    @Override
                    public void uploading() {
                        LOGGER.debug("上传中 上传进度为" + percent());
                    }

                    @Override
                    public void completed() {
                        LOGGER.debug("上传完成...");
                    }

                    @Override
                    public void failed() {
                        LOGGER.debug("上传失败...已经上传的字节数={}", bytesWritten);
                    }
                })
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.info("上传文件 result={}", storePath);
        LOGGER.info("上传文件 webPath={}", storePath.getWebPath());
        LOGGER.info("上传文件 downLoadPath={}", storePath.getDownLoadPath("1.txt"));
        LOGGER.info("上传文件 downLoadPath2={}", storePath.getDownLoadPath("name", "1.txt"));
        delete(storePath);
        assertNull(queryFile(storePath));
    }

    @Test
    public void uploadSalveFile() throws IOException {
        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
//        File file = new File(testFilePath);
//        long crc32 = FileUtils.checksumCRC32(file);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .file(file)
//                .crc32(crc32)
                .metaData("key1", "value1")
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);

        UploadSalveFileRequest salveFileRequest = UploadSalveFileRequest.builder()
                .masterPath(storePath.getPath())
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .file(file)
                .prefix("aaa")
//                .crc32(crc32)
                .metaData("salvekey", "salvevalue")
                .build();
        StorePath slaveFile = fastDFS.uploadSlaveFile(salveFileRequest);
        assertNotNull(storePath);
        assertNotNull(slaveFile);
        LOGGER.info("上传文件 result={} slaveFile={}", storePath, slaveFile);
        LOGGER.info("上传文件 webPath={} webPath={}", storePath.getWebPath(), slaveFile.getWebPath());
        LOGGER.info("上传文件 downLoadPath={} downLoadPath={}", storePath.getDownLoadPath("1.txt"), slaveFile.getDownLoadPath("1.txt"));
        LOGGER.info("上传文件 downLoadPath2={} downLoadPath2={}",
                storePath.getDownLoadPath("name", "1.txt"),
                slaveFile.getDownLoadPath("name", "1.txt"));
        delete(storePath);
        delete(slaveFile);
        assertNull(queryFile(storePath));
        assertNull(queryFile(slaveFile));
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
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
//                .fileSize(2)
                .build();
        fastDFS.downloadFile(request, new DownloadFileWriter("tmp/tmp1.txt"));
        byte[] bytes = fastDFS.downloadFile(request, new DownloadByteArray());
        try {
            LOGGER.info(IOUtils.toString(bytes, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (OutputStream ous = FileUtils.openOutputStream(new File("tmp/test.txt"))) {
            fastDFS.downloadFile(request, new DownloadOutputStream(ous));
        } catch (IOException e) {
            e.printStackTrace();
        }
        delete(storePath);
    }

    @Test
    public void uploadImageTest() {
        File file = getFile();
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("a", "a"));
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
                .thumbImage(new ThumbImage(150, 150))
                .build();
        ImageStorePath imageStorePath = fastDFS.uploadImage(request);
        LOGGER.info("img={}", imageStorePath.getImg());
        LOGGER.info("thumbs={}", imageStorePath.getThumbs());
        delete(imageStorePath.getImg());
        for (StorePath path : imageStorePath.getThumbs()) {
            delete(path);
        }
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
