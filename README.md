***基于tobato 1.2.72开发***

feat:

 1.大文件上传 分片上传功能

2.升级fastdfs为6.07最新版本 加入6.0.2版本新增的rename接口 支持appender文件改为普通文件

3.批量生成缩略图 单独上传缩略图功能

4.token防盗链功能添加

5.集成nginx 可获取web访问的路径和下载文件的路径 使用更方便

optimize:

tracker高可用 tracker集群情况 一台宕机出错可切换到另一台正常工作。

tracker宕机重试优化 默认调整为30s重试 可配置

查询文件接口 获取medata 删除文件接口 文件不存在返回空值 不会抛出异常

上传进度功能 可获取上传的进度

crc32校验 保证文件传输正确性 可结合断点续传和分片上传保证传输可靠性

流上传优化 根据需要的size传输相应流 size<0则传输所有流 不是必须传输所有流

其他细节优化处理

fix: truncate file bug -- truncate file size不为0时报错修复

refactor: 不依赖spring环境 可用于任何框架的项目

全新参数api 更丰富的api体验



代码示例：

客户端FastDFS构建：

```java
// 默认配置构建
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
trackerServers.add("192.168.24.131:22122");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers);
// 配置构建
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
trackerServers.add("192.168.24.131:22122");
FastDFSConfiguration configuration = new FastDFSConfiguration();
configuration.setGroup("group1");
configuration.getHttp().setWebServerUrl("http://192.168.24.130:8888");
configuration.getHttp().setWebServerUrlHasGroup(true);
configuration.getHttp().setHttpAntiStealToken(true);
configuration.getHttp().setSecretKey("FastDFS1234567890");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers, configuration);
```

备注：

**FastDFS关闭客户端 此客户端关闭后则不可使用 此客户端是线程安全的 可以支持多线程 建议单例模式使用 不建议每次新建和关闭客户端**

```java
fastDFS.close();
```

FastDFSConfiguration配置详解：

| 参数                          | 含义                        | 默认值            | 备注                                                         |
| ----------------------------- | --------------------------- | ----------------- | ------------------------------------------------------------ |
| group                         | 上传到固定组                | 无                | 优先级大于参数                                               |
|                               |                             |                   |                                                              |
| HttpConfiguration             | http相关配置                |                   | 1.Token防盗链<br/> 2.获取预览地址 StorePath.getWebPath() <br/> 3.获取下载地址StorePath.getDownLoadPath(String) |
| webServerUrl                  | web访问路径                 | 无                | eg: nginx地址 配合fastdfs-nginx-module使用<br> 例如图片等可直接返回预览地址 下载时传入文件名即可返回下载地址 自带token防盗链 |
| webServerUrlHasGroup          | web路径是否包含Group        | false             | 关联服务器配置mod_fastdfs.conf url_have_group_name<br> 配和StorePath.getWebPath StorePath.getDownLoadPath使用 |
| httpAntiStealToken            | 是否开启http防盗链          | false             | 关联服务器配置http.config http.anti_steal.check_token        |
| secretKey                     | http防盗链密钥              | FastDFS1234567890 | 关联服务器配置http.config http.anti_steal.secret_key         |
| charset                       | 字符集                      | UTF-8             |                                                              |
|                               |                             |                   |                                                              |
| ConnectionConfiguration       | 连接配置                    |                   |                                                              |
| socketTimeout                 | 读取时间                    | 30s               |                                                              |
| connectTimeout                | 连接超时时间                | 2s                |                                                              |
| charset                       | 字符集                      | UTF-8             |                                                              |
| retryAfterSecond              | tracker不可用后多少秒后重试 | 30s               |                                                              |
|                               |                             |                   |                                                              |
| GenericKeyedObjectPoolConfig  | 连接池配置                  |                   | 这里只列举默认设置了哪些值 具体配置参考apache common2 pool   |
| maxWaitMillis                 | 获取连接时的最大等待毫秒数  | 5s                |                                                              |
| maxTotalPerKey                | 每个key最大连接数           | 500               |                                                              |
| maxIdlePerKey                 | 每个key最大空闲连接数       | 100               |                                                              |
| minIdlePerKey                 | 每个key最小空闲连接数       | 10                |                                                              |
| minEvictableIdleTimeMillis    | 空闲连接存活时长            | 30min             |                                                              |
| timeBetweenEvictionRunsMillis | 清理空闲连接任务时长        | 1min              |                                                              |
| testOnCreate                  | 创建新连接检测              | true              |                                                              |
| testOnBorrow                  | 连接池中获取连接检测        | true              |                                                              |

上传本地文件

```java
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers);
UploadFileRequest fileRequest = UploadFileRequest.builder()
        .filePath("test.txt")
        .build();
StorePath storePath = fastDFS.uploadFile(fileRequest);
fastDFS.close();
System.out.println("上传文件成功" + storePath);
```

上传文件流

```java
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers);
String str = "123";
UploadFileRequest fileRequest = UploadFileRequest.builder()
    .stream(new ByteArrayInputStream(str.getBytes()), str.length(), "")
    .build();
StorePath storePath = fastDFS.uploadFile(fileRequest);
System.out.println("上传文件成功" + storePath);
fastDFS.close();
```

上传文件带元数据

```java
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers);
UploadFileRequest fileRequest = UploadFileRequest.builder()
        .file(new File("test.txt"))
        .metaData("MetaKey", "MetaValue")
        .build();
StorePath storePath = fastDFS.uploadFile(fileRequest);
System.out.println("上传文件成功" + storePath);
fastDFS.close();
```

上传文件带进度条

```java
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers);
UploadFileRequest fileRequest = UploadFileRequest.builder()
        .file(new File("test.mp4"))
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
fastDFS.close();
```

上传带crc32校验

```java
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers);
File file = new File("test.txt");
long crc32 = Crc32.file(file);
UploadFileRequest fileRequest = UploadFileRequest.builder()
        .file(file)
        .crc32(crc32)
        .build();
StorePath storePath = fastDFS.uploadFile(fileRequest);
System.out.println("上传文件成功" + storePath);
fastDFS.close();
```

断点续传示例：

```java
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers);
StorePath storePath = null;
for (int i = 1; i < 10; i++) {
    String appendStr = String.valueOf(i);
    if (i == 1) {
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(new ByteArrayInputStream(appendStr.getBytes()), appendStr.length(), "txt")
                .build();
        storePath = fastDFS.uploadAppenderFile(fileRequest);
    } else {
        AppendFileRequest appendRequest = AppendFileRequest.builder()
                .stream(new ByteArrayInputStream(appendStr.getBytes()), appendStr.length())
                .path(storePath.getPath())
                .group(storePath.getGroup())
                .build();
        fastDFS.appendFile(appendRequest);
    }
}
// 修改为普通文件 6.0.2版本以上支持该特性
RegenerateAppenderFileRequest request = RegenerateAppenderFileRequest.builder()
        .group(storePath.getGroup())
        .path(storePath.getPath())
        .build();
StorePath reStorePath = fastDFS.regenerateAppenderFile(request);
System.out.println("上传文件成功" + reStorePath);
fastDFS.close();
```

分片上传：

```java
List<String> trackerServers = new ArrayList<>();
trackerServers.add("192.168.24.130:22122");
FastDFS fastDFS = new FastDFSClientBuilder().build(trackerServers);
File sampleFile = new File("test.mp4");
final long partSize = 5 * 1024 * 1024L;   // 5MB
long fileSize = sampleFile.length();
long partCount = fileSize > 0 ? (long) Math.ceil((double) fileSize / partSize) : 1;

InitMultipartUploadRequest initRequest = InitMultipartUploadRequest.builder()
        .fileSize(sampleFile.length())
        .fileExtName(FilenameUtils.getExtension(sampleFile.getName()))
        .build();
StorePath storePath = fastDFS.initMultipartUpload(initRequest);
System.out.println("初始化分片成功" + storePath);
ExecutorService executorService = Executors.newFixedThreadPool(3);
for (int i = 1; i <= partCount; i++) {
    long startPos = (i - 1) * partSize;
    long curPartSize = (i == partCount) ? (fileSize - startPos) : partSize;
    InputStream ins = new FileInputStream(sampleFile);
    ins.skip(startPos);
    int partNumber = i;
    executorService.execute(() -> {
        UploadMultipartPartRequest multipartPartRequest = UploadMultipartPartRequest.builder()
                .stream(ins, partNumber, curPartSize)
                .partSize(partSize)
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        fastDFS.uploadMultipart(multipartPartRequest);
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
    .group(storePath.getGroup())
    .path(storePath.getPath())
    .crc32(crc32)
    // 6.0.2版本以上支持该特性
    .regenerate(true)
    .build();
StorePath path = fastDFS.completeMultipartUpload(completeRequest);
System.out.println("上传文件成功" + path);
fastDFS.close();
```
