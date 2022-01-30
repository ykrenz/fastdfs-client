package com.ykren.fastdfs;

import com.ykren.fastdfs.event.UploadProgressListener;
import com.ykren.fastdfs.model.UploadFileRequest;
import com.ykren.fastdfs.model.fdfs.StorePath;
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
                    public void start(long totalBytes) {
                        LOGGER.info("开始上传...");
                    }

                    @Override
                    public void uploading(long totalBytes, long bytesWritten) {
                        if (totalBytes > 0) {
                            int percent = (int) (bytesWritten * 100.0 / totalBytes);
                            LOGGER.info("上传中 上传进度为" + percent);
                        } else {
                            LOGGER.info("上传中 已经上传字节数为" + bytesWritten);
                        }
                    }

                    @Override
                    public void completed(long totalBytes, long bytesWritten) {
                        LOGGER.info("上传完成...");
                    }

                    @Override
                    public void failed(long totalBytes, long bytesWritten) {
                        LOGGER.info("开始失败...");
                    }
                })
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("上传文件 result={}", storePath);
    }
}
