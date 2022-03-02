package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.model.FileInfoRequest;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author ykren
 * @date 2022/2/10
 */
public class ThreadTest extends BaseClientTest {
    String testFilePath = "D:\\Users\\ykren\\Downloads\\Git-2.34.1-64-bit.exe";

    @Test
    public void uploadThreadTest() throws InterruptedException, ExecutionException {
        for (int count = 0; count < 10; count++) {
            int thread = 100;
            ExecutorService service = Executors.newFixedThreadPool(thread);
            FastDfs fastDFS = new FastDfsClientBuilder().build(TRACKER_LIST);
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < thread; i++) {
                tasks.add(new uploadTask(fastDFS));
            }
            List<Future<Void>> futures = service.invokeAll(tasks);
            for (Future f : futures) {
                f.get();
            }
            TimeUnit.SECONDS.sleep(10);
            fastDFS.shutdown();
        }
    }


    class uploadTask implements Callable<Void> {

        FastDfs fastDFS;

        public uploadTask(FastDfs fastDFS) {
            this.fastDFS = fastDFS;
        }

        @Override
        public Void call() throws Exception {
            StorePath storePath = null;
            try {
//                File file = new File(testFilePath);
//                UploadFileRequest fileRequest = UploadFileRequest.builder()
//                        .stream(new FileInputStream(file), file.length(), "exe")
//                        .build();

            String data = UUID.randomUUID().toString();
            UploadFileRequest fileRequest = UploadFileRequest.builder()
                    .stream(new ByteArrayInputStream(data.getBytes()), data.length(), "txt")
                    .build();
                storePath = fastDFS.uploadFile(fileRequest);
                LOGGER.info("上传成功={}", storePath);
            } finally {
                if (storePath != null) {
                    FileInfoRequest fileInfoRequest = FileInfoRequest.builder()
                            .groupName(storePath.getGroup())
                            .path(storePath.getPath())
                            .build();
                    fastDFS.deleteFile(fileInfoRequest);
                }
            }
            return null;
        }
    }
}
