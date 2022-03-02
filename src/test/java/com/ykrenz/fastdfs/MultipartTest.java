package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.common.Crc32;
import com.ykrenz.fastdfs.model.CompleteMultipartRequest;
import com.ykrenz.fastdfs.model.InitMultipartUploadRequest;
import com.ykrenz.fastdfs.model.UploadMultipartPartRequest;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author ykren
 * @date 2022/1/26
 */
public class MultipartTest extends BaseClientTest {

    @Test
    public void multipartTest() throws IOException {
        File sampleFile = new File("D:\\Users\\ykren\\Downloads\\Git-2.34.1-64-bit.exe");
        final long partSize = 5 * 1024 * 1024L;   // 5MB
        long fileSize = sampleFile.length();
        long partCount = fileSize > 0 ? (long) Math.ceil((double) fileSize / partSize) : 1;

        InitMultipartUploadRequest initRequest = InitMultipartUploadRequest.builder()
                .fileSize(sampleFile.length())
                .fileExtName(FilenameUtils.getExtension(sampleFile.getName()))
                .build();
        StorePath storePath = fastDFS.initMultipartUpload(initRequest);
        LOGGER.info("初始化分片成功 path={}", storePath);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= partCount; i++) {
            long startPos = (i - 1) * partSize;
            long curPartSize = (i == partCount) ? (fileSize - startPos) : partSize;
            InputStream ins = new FileInputStream(sampleFile);
            ins.skip(startPos);
            int partNumber = i;
            executorService.execute(() -> {
                // offset方式
//                UploadMultipartPartRequest offsetPartRequest = UploadMultipartPartRequest.builder()
//                        .streamOffset(ins, curPartSize, startPos)
//                        .groupName(storePath.getGroup())
//                        .path(storePath.getPath())
//                        .build();
//                fastDFS.uploadMultipart(offsetPartRequest);
                // partSize方式
                UploadMultipartPartRequest partRequest = UploadMultipartPartRequest.builder()
                        .streamPartSize(ins, curPartSize, partNumber, partSize)
                        .groupName(storePath.getGroup())
                        .path(storePath.getPath())
                        .build();
                fastDFS.uploadMultipart(partRequest);
            });
        }
        /*
         * Waiting for all parts finished
         */
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long crc32 = Crc32.file(sampleFile);
        CompleteMultipartRequest completeRequest = CompleteMultipartRequest.builder()
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
                .crc32(crc32)
                .build();
        StorePath path = fastDFS.completeMultipartUpload(completeRequest);
        LOGGER.info("上传成功 path={}", path);
    }

}
