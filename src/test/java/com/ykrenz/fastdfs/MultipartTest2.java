package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.common.Crc32;
import com.ykrenz.fastdfs.model.CompleteMultipartRequest;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author ykren
 * @date 2022/1/26
 */
public class MultipartTest2 extends BaseClientTest {

    @Test
    public void multipartTest() throws IOException {
        int length = 1024 * 1024 * 33;
        RandomTextFile file = new RandomTextFile(length);

        File sampleFile = new File("tmp", "sampleFile.txt");
        FileUtils.copyToFile(file.getInputStream(), sampleFile);

        final long partSize = 5 * 1024 * 1024L;   // 5MB
        long fileSize = sampleFile.length();
        long partCount = fileSize > 0 ? (long) Math.ceil((double) fileSize / partSize) : 1;

        StorePath storePath = fastDFS.initMultipartUpload(fileSize, partSize, "txt");
        LOGGER.info("初始化分片成功 path={}", storePath);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= partCount; i++) {
            long startPos = (i - 1) * partSize;
            InputStream ins = new FileInputStream(sampleFile);
            ins.skip(startPos);
            int partNumber = i;
            executorService.execute(() -> {
                fastDFS.uploadMultipart(storePath.getGroup(), storePath.getPath(), ins, partNumber);
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
                .metaData("key", "Complete")
                .build();
        StorePath path = fastDFS.completeMultipartUpload(completeRequest);

        Set<MetaData> metaData = fastDFS.getMetadata(storePath.getGroup(), storePath.getPath());
        assertEquals(1, metaData.size());
        assertTrue(metaData.contains(new MetaData("key", "Complete")));

        // crc32校验
        FileInfo fileInfo = queryFile(path);
        Assert.assertEquals(crc32, Crc32.convertUnsigned(fileInfo.getCrc32()));
        LOGGER.info("上传成功 path={} crc32={}", path, crc32);
        delete(storePath);
    }

}
