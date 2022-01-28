package com.ykren.fastdfs;

import com.ykren.fastdfs.event.UploadProgressListener;
import com.ykren.fastdfs.model.UploadFileRequest;
import com.ykren.fastdfs.model.fdfs.StorePath;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author ykren
 * @date 2022/1/29
 */
public class ProgressTest extends BaseClientTest {

    @Test
    public void progressUpload() {
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .listener(new UploadProgressListener())
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.debug("上传文件 result={}", storePath);
    }
}
