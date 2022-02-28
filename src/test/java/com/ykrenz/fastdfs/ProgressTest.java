package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.event.UploadProgressListener;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

/**
 * @author ykren
 * @date 2022/1/29
 */
public class ProgressTest extends BaseClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressTest.class);

    @Test
    public void progressUpload() {
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .listener(new UploadProgressListener() {
                    @Override
                    public void start() {
                        LOGGER.info("开始上传...文件总大小={}", totalBytes);
                    }

                    @Override
                    public void uploading() {
                        LOGGER.info("上传中 上传进度为" + percent());
                    }

                    @Override
                    public void completed() {
                        LOGGER.info("上传完成...");
                    }

                    @Override
                    public void failed() {
                        LOGGER.info("上传失败...已经上传的字节数={}", bytesWritten);
                    }
                })
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("上传文件 result={}", storePath);
    }
}
