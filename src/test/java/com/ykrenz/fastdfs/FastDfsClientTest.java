package com.ykrenz.fastdfs;


import com.ykrenz.fastdfs.event.UploadProgressListener;
import com.ykrenz.fastdfs.model.DownloadFileRequest;
import com.ykrenz.fastdfs.model.MetaDataRequest;
import com.ykrenz.fastdfs.model.ThumbImage;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.UploadImageRequest;
import com.ykrenz.fastdfs.model.UploadSalveFileRequest;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.ImageStorePath;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.proto.storage.DownloadByteArray;
import com.ykrenz.fastdfs.model.proto.storage.DownloadFileWriter;
import com.ykrenz.fastdfs.model.proto.storage.DownloadOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 文件基础操作测试演示
 *
 * @author tobato
 */
public class FastDfsClientTest extends BaseClientTest {

    protected static Logger LOGGER = LoggerFactory.getLogger(FastDfsClientTest.class);

    /**
     * 基本文件上传操作测试
     *
     * @throws IOException
     */
    @Test
    public void uploadFile() throws IOException {
        LOGGER.debug("##上传文件..##");

        StorePath storePath = fastDFS.uploadFile(getFile());
        delete(storePath);

        storePath = fastDFS.uploadFile("group1", getFile());
        delete(storePath);

        RandomTextFile file = new RandomTextFile();

        storePath = fastDFS.uploadFile(file.getInputStream(), file.getFileSize(), file.getFileExtName());
        delete(storePath);

        storePath = fastDFS.uploadFile("group1", file.getInputStream(), file.getFileSize(), file.getFileExtName());
        delete(storePath);

//        File file = new File(testFilePath);
//        FileInputStream stream = new FileInputStream(file);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .stream(stream, file.length(), "exe")
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
        storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.info("上传文件 result={}", storePath);
        delete(storePath);
        assertNull(queryFile(storePath));
    }

    @Test
    public void uploadSalveFile() throws IOException {
        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
//        File file = new File(testFilePath);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .file(file)
                .metaData("key1", "value1")
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);

        UploadSalveFileRequest salveFileRequest = UploadSalveFileRequest.builder()
                .groupName(storePath.getGroup())
                .masterPath(storePath.getPath())
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .file(file)
                .prefix("aaa")
                .metaData("salvekey", "salvevalue")
                .build();
        StorePath slaveFile = fastDFS.uploadSlaveFile(salveFileRequest);
        assertNotNull(storePath);
        assertNotNull(slaveFile);
        LOGGER.info("上传文件 result={} slaveFile={}", storePath, slaveFile);

        StorePath slaveFilePath = fastDFS.uploadSlaveFile(storePath.getGroup(), storePath.getPath(), "_file_", getFile());
        LOGGER.info("上传文件 result={} slaveFilePath={}", storePath, slaveFilePath);
        Assert.assertNotNull(slaveFilePath);
        delete(slaveFilePath);
        assertNull(queryFile(slaveFilePath));

        StorePath slaveFilePath2 = fastDFS.uploadSlaveFile(storePath.getGroup(), storePath.getPath(), "_is_",
                file.getInputStream(), file.getFileSize(), file.getFileExtName());
        LOGGER.info("上传文件 result={} slaveFilePath={}", storePath, slaveFilePath2);
        Assert.assertNotNull(slaveFilePath2);
        delete(slaveFilePath2);
        assertNull(queryFile(slaveFilePath2));

        delete(storePath);
        delete(slaveFile);
        assertNull(queryFile(storePath));
        assertNull(queryFile(slaveFile));
    }

    @Test
    public void deleteFileInfo() throws IOException {
        StorePath storePath = uploadRandomFile();
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
    public void uploadImageTest() throws IOException {
        File file = getFile();
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("a", "a"));
        UploadImageRequest request = UploadImageRequest.builder()
                .listener(new UploadProgressListener() {
                    @Override
                    public void start() {

                    }

                    @Override
                    public void uploading() {
                        System.out.println(this.percent());
                    }

                    @Override
                    public void completed() {

                    }

                    @Override
                    public void failed() {

                    }
                })
                .file(file)
                .metaData(metaData)
                .thumbImage(new ThumbImage(150, 150))
                .build();
        ImageStorePath imageStorePath = fastDFS.uploadImage(request);
        Assert.assertNotNull(imageStorePath);
        Assert.assertNotNull(imageStorePath.getImg());
        Assert.assertEquals(1, imageStorePath.getThumbs().size());

        metaData = getMetaData(imageStorePath.getImg());
        Assert.assertEquals(1, metaData.size());

        Assert.assertTrue(metaData.contains(new MetaData("a", "a")));

        LOGGER.info("img={}", imageStorePath.getImg());
        LOGGER.info("thumbs={}", imageStorePath.getThumbs());
        delete(imageStorePath.getImg());
        for (StorePath path : imageStorePath.getThumbs()) {
            delete(path);
        }
    }

    @Test
    public void createThumbImageTest() throws IOException {
        File file = getFile();
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("a", "a"));
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
                .thumbImage(new ThumbImage(150, 150), metaData)
                .build();
        StorePath thumbImage = fastDFS.createThumbImage(request);
        LOGGER.info("thumbImage={}", thumbImage);
        assertNotNull(thumbImage);
        delete(thumbImage);

        UploadImageRequest request2 = UploadImageRequest.builder()
                .file(file)
                .thumbImage(new ThumbImage(150, 150), metaData)
                .thumbImage(new ThumbImage(100, 100), metaData)
                .thumbImage(new ThumbImage(0.5), metaData)
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
        List<StorePath> thumbImages = fastDFS.createThumbImages(request2);
        assertEquals(3, thumbImages.size());
        LOGGER.info("thumbImages={}", thumbImages);
        for (StorePath path : thumbImages) {
            delete(path);
        }
    }


    @Test
    public void uploadMetadata() throws IOException {
        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .metaData("key1", "value1")
                .metaData("key2", "value2")
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);

        UploadSalveFileRequest salveFileRequest = UploadSalveFileRequest.builder()
                .groupName(storePath.getGroup())
                .masterPath(storePath.getPath())
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .prefix("aaa")
                .metaData("salvekey1", "salvevalue1")
                .metaData("salvekey2", "salvevalue2")
                .build();
        StorePath slaveFile = fastDFS.uploadSlaveFile(salveFileRequest);
        assertNotNull(storePath);
        assertNotNull(slaveFile);

        Set<MetaData> metaData = getMetaData(storePath);
        assertEquals(2, metaData.size());
        assertTrue(metaData.contains(new MetaData("key1", "value1")));
        assertTrue(metaData.contains(new MetaData("key2", "value2")));

        Set<MetaData> metaData2 = getMetaData(slaveFile);
        assertEquals(2, metaData2.size());
        assertTrue(metaData2.contains(new MetaData("salvekey1", "salvevalue1")));
        assertTrue(metaData2.contains(new MetaData("salvekey2", "salvevalue2")));

        MetaDataRequest metaDataRequest = MetaDataRequest.builder()
                .metaData("key1", "newvalue1")
                .metaData("key2", "newvalue2")
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        fastDFS.mergeMetadata(metaDataRequest);

        Set<MetaData> newMeta = getMetaData(storePath);
        assertEquals(2, newMeta.size());
        assertTrue(newMeta.contains(new MetaData("key1", "newvalue1")));
        assertTrue(newMeta.contains(new MetaData("key2", "newvalue2")));

        Set<MetaData> mms = new HashSet<>();
        mms.add(new MetaData("key1", "mmsvalue"));
        fastDFS.mergeMetadata(storePath.getGroup(), storePath.getPath(), mms);
        mms = getMetaData(storePath);
        assertEquals(2, mms.size());
        assertTrue(mms.contains(new MetaData("key1", "mmsvalue")));
        assertTrue(mms.contains(new MetaData("key2", "newvalue2")));

        MetaDataRequest metaDataRequesto = MetaDataRequest.builder()
                .metaData("keyo", "valueo")
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        fastDFS.overwriteMetadata(metaDataRequesto);

        Set<MetaData> oMeta = getMetaData(storePath);
        assertEquals(1, oMeta.size());
        assertTrue(oMeta.contains(new MetaData("keyo", "valueo")));


        MetaDataRequest metaDataRequesto2 = MetaDataRequest.builder()
                .metaData("skeyo", "svalueo")
                .groupName(slaveFile.getGroup())
                .path(slaveFile.getPath())
                .build();
        fastDFS.overwriteMetadata(metaDataRequesto2);

        Set<MetaData> soMeta = getMetaData(slaveFile);
        assertEquals(1, soMeta.size());
        assertTrue(soMeta.contains(new MetaData("skeyo", "svalueo")));

        Set<MetaData> os = new HashSet<>();
        os.add(new MetaData("os", "os"));
        fastDFS.overwriteMetadata(slaveFile.getGroup(), slaveFile.getPath(), os);
        os = getMetaData(slaveFile);
        assertEquals(1, os.size());
        assertTrue(os.contains(new MetaData("os", "os")));

        fastDFS.deleteMetadata(storePath.getGroup(), storePath.getPath());
        assertEquals(0, getMetaData(storePath).size());

        delete(storePath);
        delete(slaveFile);
        assertNull(queryFile(storePath));
        assertNull(queryFile(slaveFile));
    }

    @Test
    public void queryFileTest() {
        StorePath storePath = uploadRandomFile();
        FileInfo fileInfo = fastDFS.queryFileInfo(storePath.getGroup(), storePath.getPath());
        assertNotNull(fileInfo);

        delete(storePath);
        fileInfo = fastDFS.queryFileInfo(storePath.getGroup(), storePath.getPath());
        assertNull(fileInfo);

    }

    @Test
    public void nullFileTest() throws IOException {
        RandomTextFile file = new RandomTextFile(0);
        File sampleFile = new File("tmp", "sampleFile.txt");
        FileUtils.copyToFile(file.getInputStream(), sampleFile);
        StorePath storePath = fastDFS.uploadFile(sampleFile);
        LOGGER.info("上传完成 path {}", storePath);
        delete(storePath);
    }
}
