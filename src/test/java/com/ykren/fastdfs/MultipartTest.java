package com.ykren.fastdfs;

import com.ykren.fastdfs.model.CompleteMultipartRequest;
import com.ykren.fastdfs.model.InitMultipartUploadRequest;
import com.ykren.fastdfs.model.UploadMultipartPartRequest;
import com.ykren.fastdfs.model.fdfs.StorePath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author ykren
 * @date 2022/1/26
 */
public class MultipartTest extends BaseClientTest {

    String testFilePath = "D:\\Users\\ykren\\Downloads\\Git-2.34.1-64-bit.exe";
    int partSize = 1024 * 1024 * 5;
    String chunkPath = "chunks";

    @Test
    public void uploadSimpleTest() throws IOException {
        List<File> chunks = LocalFileOperation.chunkFile(testFilePath, chunkPath, partSize);
        chunks.sort(Comparator.comparingInt(c -> Integer.parseInt(c.getName())));
        InitMultipartUploadRequest initRequest = InitMultipartUploadRequest.builder()
                .fileSize(new File(testFilePath).length())
                .fileExtName(FilenameUtils.getExtension(testFilePath))
                .build();
        StorePath storePath = fastDFS.initMultipartUpload(initRequest);
        LOGGER.info("初始化完毕StorePath={}", storePath);

        long start = System.currentTimeMillis();
        for (File file : chunks) {
            int partNumber = Integer.parseInt(file.getName());
            LOGGER.info("开始上传分片 partNumber={}", partNumber);
            UploadMultipartPartRequest multipartPartRequest = UploadMultipartPartRequest.builder()
                    .file(file, partNumber)
                    .partSize(partSize)
                    .group(storePath.getGroup())
                    .path(storePath.getPath())
                    .build();
            fastDFS.uploadMultipart(multipartPartRequest);
            LOGGER.info("上传分片完毕 partNumber={}", partNumber);
        }

        CompleteMultipartRequest completeRequest = CompleteMultipartRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        StorePath finalPath = fastDFS.completeMultipartUpload(completeRequest);

        if (completeRequest.regenerate()) {
            Assert.assertNull(queryFile(storePath));
            Assert.assertNotNull(queryFile(finalPath));
        } else {
            Assert.assertEquals(storePath, finalPath);
            Assert.assertNotNull(queryFile(finalPath));
        }
        String md5 = null;
        try (FileInputStream inputStream = new FileInputStream(new File(testFilePath))) {
            md5 = DigestUtils.md5DigestAsHex(inputStream);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("上传完毕StorePath={} 源文件md5={} 耗时={}", finalPath, md5, System.currentTimeMillis() - start);
        delete(finalPath);
        FileUtils.deleteDirectory(new File(chunkPath));
    }


    @Test
    public void uploadThreadTest() throws IOException, InterruptedException, ExecutionException {
        List<File> chunks = LocalFileOperation.chunkFile(testFilePath, chunkPath, partSize);
        chunks.sort(Comparator.comparingInt(c -> Integer.parseInt(c.getName())));
        InitMultipartUploadRequest initRequest = InitMultipartUploadRequest.builder()
                .fileSize(new File(testFilePath).length())
                .fileExtName(FilenameUtils.getExtension(testFilePath))
                .build();
        StorePath storePath = fastDFS.initMultipartUpload(initRequest);
        LOGGER.info("初始化完毕StorePath={}", storePath);

        int threadCount = 3;
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        long start = System.currentTimeMillis();
        List<Callable<Void>> taskList = new ArrayList<>();
        for (File file : chunks) {
            taskList.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int partNumber = Integer.parseInt(file.getName());
                    LOGGER.info("开始上传分片 partNumber={}", partNumber);
                    UploadMultipartPartRequest multipartPartRequest = UploadMultipartPartRequest.builder()
                            .file(file, partNumber)
                            .partSize(partSize)
                            .group(storePath.getGroup())
                            .path(storePath.getPath())
                            .build();
                    fastDFS.uploadMultipart(multipartPartRequest);
                    LOGGER.info("上传分片完毕 partNumber={}", partNumber);
                    return null;
                }
            });
        }
        List<Future<Void>> futures = service.invokeAll(taskList);
        for (Future<Void> future : futures) {
            future.get();
        }
        service.shutdown();
        LOGGER.info("线程数={} 耗时==========={}", threadCount, System.currentTimeMillis() - start);
        CompleteMultipartRequest completeRequest = CompleteMultipartRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        StorePath finalPath = fastDFS.completeMultipartUpload(completeRequest);

        if (completeRequest.regenerate()) {
            Assert.assertNull(queryFile(storePath));
            Assert.assertNotNull(queryFile(finalPath));
        } else {
            Assert.assertEquals(storePath, finalPath);
            Assert.assertNotNull(queryFile(finalPath));
        }
        String md5 = null;
        try (FileInputStream inputStream = new FileInputStream(new File(testFilePath))) {
            md5 = DigestUtils.md5DigestAsHex(inputStream);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("上传完毕StorePath={} 源文件md5={} 可查看服务器md5进行对比", finalPath, md5);
        delete(finalPath);
        FileUtils.deleteDirectory(new File(chunkPath));
    }

}
