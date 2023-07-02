package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.common.Crc32;
import com.ykrenz.fastdfs.model.CompleteMultipartRequest;
import com.ykrenz.fastdfs.model.InitMultipartUploadRequest;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import sun.security.krb5.internal.crypto.crc32;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
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
public class MultipartUploadTest extends BaseClientTest {

    @Test
    public void multipartTest() throws IOException, InterruptedException {
        int length = 1024 * 1024 * 33;
        RandomTextFile file = new RandomTextFile(length);

        File sampleFile = new File("tmp", "sampleFile.txt");
        FileUtils.copyToFile(file.getInputStream(), sampleFile);

        final long partSize = 5 * 1024 * 1024L;   // 5MB
        long fileSize = sampleFile.length();
        long partCount = fileSize > 0 ? (long) Math.ceil((double) fileSize / partSize) : 1;


        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("test_key", "test_value"));

        InitMultipartUploadRequest uploadRequest = InitMultipartUploadRequest.builder()
                .fileSize(fileSize).partSize(partSize).fileExtName("txt")
                .metaData(metaData).build();
        StorePath storePath = fastDFS.initMultipartUpload(uploadRequest);
        LOGGER.info("初始化分片成功 path={}", storePath);

        Set<MetaData> meta = fastDFS.getMetadata(storePath.getGroup(), storePath.getPath());
        assertEquals(metaData.size(), meta.size());
        assertTrue(metaData.containsAll(meta));

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
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }

        long crc32 = Crc32.file(sampleFile);
        CompleteMultipartRequest completeRequest = CompleteMultipartRequest.builder()
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
                .regenerate(true)
                .build();
        StorePath resultPath = fastDFS.completeMultipartUpload(completeRequest);

        Set<MetaData> metaC = fastDFS.getMetadata(resultPath.getGroup(), resultPath.getPath());
        assertEquals(metaData.size(), metaC.size());
        assertTrue(metaData.containsAll(metaC));

        // crc32校验
        FileInfo fileInfo = queryFile(resultPath);
        Assert.assertEquals(crc32, Crc32.convertUnsigned(fileInfo.getCrc32()));
        LOGGER.info("上传成功 path={} crc32={}", resultPath, crc32);
        delete(resultPath);
    }

}
