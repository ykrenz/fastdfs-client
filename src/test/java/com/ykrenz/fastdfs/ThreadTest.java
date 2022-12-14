package com.ykrenz.fastdfs;

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
import java.util.concurrent.TimeUnit;

/**
 * @author ykren
 * @date 2022/2/10
 */
public class ThreadTest extends BaseClientTest {

    @Test
    public void uploadThreadTest() throws InterruptedException, ExecutionException {
        for (int count = 0; count < 5; count++) {
            int thread = 100;
            ExecutorService service = Executors.newFixedThreadPool(thread);
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < thread; i++) {
                tasks.add(new uploadTask(fastDFS));
            }
            service.invokeAll(tasks);
            service.shutdown();
            while (!service.isTerminated()) {
                service.awaitTermination(5, TimeUnit.SECONDS);
            }

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
                String data = UUID.randomUUID().toString();
                UploadFileRequest fileRequest = UploadFileRequest.builder()
                        .stream(new ByteArrayInputStream(data.getBytes()), data.length(), "txt")
                        .build();
                storePath = fastDFS.uploadFile(fileRequest);
                LOGGER.info("上传成功={}", storePath);
            } finally {
                if (storePath != null) {
                    delete(storePath);
                }
            }
            return null;
        }
    }
}
