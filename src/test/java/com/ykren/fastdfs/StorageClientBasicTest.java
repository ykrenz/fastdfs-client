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
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("上传文件 result={}", storePath);
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
