package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.cache.MultipartUploadAttachment;
import com.ykrenz.fastdfs.cache.DefaultMultipartAttachmentAccessor;
import com.ykrenz.fastdfs.cache.MultipartAttachmentAccessor;
import com.ykrenz.fastdfs.common.CodeUtils;
import com.ykrenz.fastdfs.config.FastDfsConfiguration;
import com.ykrenz.fastdfs.conn.FdfsConnectionManager;
import com.ykrenz.fastdfs.conn.FdfsConnectionPool;
import com.ykrenz.fastdfs.conn.TrackerConnectionManager;
import com.ykrenz.fastdfs.event.ProgressInputStream;
import com.ykrenz.fastdfs.event.ProgressListener;
import com.ykrenz.fastdfs.exception.FdfsClientException;
import com.ykrenz.fastdfs.exception.FdfsUploadImageException;
import com.ykrenz.fastdfs.model.*;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.ImageStorePath;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.fdfs.StorageNode;
import com.ykrenz.fastdfs.model.fdfs.StorageNodeInfo;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.proto.storage.DownloadCallback;
import com.ykrenz.fastdfs.model.proto.storage.StorageAppendFileCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageDeleteFileCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageDownloadCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageGetMetadataCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageModifyCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageQueryFileInfoCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageRegenerateAppendFileCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageSetMetadataCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageTruncateCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageUploadFileCommand;
import com.ykrenz.fastdfs.model.proto.storage.StorageUploadSlaveFileCommand;
import com.ykrenz.fastdfs.model.proto.storage.enums.StorageMetadataSetType;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * FastDFSClient默认客户端
 *
 * @author ykren
 * @date 2022/1/21
 */
public class FastDfsClient implements FastDfs {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FastDfsClient.class);

    /**
     * TrackerClient
     */
    private final TrackerClient trackerClient;

    /**
     * 连接管理器
     */
    private final FdfsConnectionManager fdfsConnectionManager;

    /**
     * 默认 group
     */
    private final String defaultGroup;

    /**
     * FastDfs配置
     */
    private final FastDfsConfiguration fastDfsConfiguration;

    /**
     * web客户端
     */
    private final FastDfsWebClient fastDfsWebClient;

    public FastDfsClient(final List<String> trackerServers, final FastDfsConfiguration configuration) {
        checkClient(trackerServers, configuration);
        FdfsConnectionPool pool = new FdfsConnectionPool(configuration.getConnection());
        this.trackerClient = new DefaultTrackerClient(new TrackerConnectionManager(trackerServers, pool));
        this.fastDfsWebClient = new DefaultFastDfsWebClient(configuration.getHttp());
        this.fdfsConnectionManager = new FdfsConnectionManager(pool);
        this.defaultGroup = configuration.getDefaultGroup();
        this.fastDfsConfiguration = configuration;
    }

    private void checkClient(List<String> trackerServers, FastDfsConfiguration configuration) {
        if (trackerServers == null || trackerServers.isEmpty()) {
            throw new IllegalArgumentException("trackerServers should not be null or empty.");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("configuration should not be null.");
        }
    }

    public FdfsConnectionManager getConnectionManager() {
        return fdfsConnectionManager;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public FastDfsConfiguration getFastDfsConfiguration() {
        return fastDfsConfiguration;
    }

    public FastDfsWebClient getFastDfsWebClient() {
        return fastDfsWebClient;
    }

    @Override
    public TrackerClient getTrackerClient() {
        return trackerClient;
    }

    @Override
    public void shutdown() {
        fdfsConnectionManager.getPool().close();
    }

    @Override
    public String accessUrl(String groupName, String path) {
        return fastDfsWebClient.accessUrl(groupName, path);
    }

    @Override
    public String downLoadUrl(String groupName, String path, String downloadFileName) {
        return fastDfsWebClient.downLoadUrl(groupName, path, downloadFileName);
    }

    @Override
    public String downLoadUrl(String groupName, String path, String urlArgName, String downloadFileName) {
        return fastDfsWebClient.downLoadUrl(groupName, path, urlArgName, downloadFileName);
    }

    @Override
    public StorePath uploadFile(File file) {
        return this.uploadFile(UploadFileRequest.builder().file(file).build());
    }

    @Override
    public StorePath uploadFile(String groupName, File file) {
        return this.uploadFile(UploadFileRequest.builder().groupName(groupName).file(file).build());
    }

    @Override
    public StorePath uploadFile(InputStream stream, long fileSize, String fileExtName) {
        return this.uploadFile(UploadFileRequest.builder().stream(stream, fileSize, fileExtName).build());
    }

    @Override
    public StorePath uploadFile(String groupName, InputStream stream, long fileSize, String fileExtName) {
        return this.uploadFile(UploadFileRequest.builder()
                .groupName(groupName).stream(stream, fileSize, fileExtName).build()
        );
    }

    @Override
    public StorePath uploadFile(UploadFileRequest request) {
        String groupName = getGroupName(request);
        InputStream stream = getInputStream(request);
        StorageNode client = trackerClient.getStoreStorage(groupName);
        return uploadFileAndMetaData(client, stream, request.fileSize(),
                request.fileExtName(), request.metaData(), false);
    }

    /**
     * 上传文件和元数据
     *
     * @param client
     * @param inputStream
     * @param fileSize
     * @param fileExtName
     * @param metaDataSet
     * @return
     */
    protected StorePath uploadFileAndMetaData(StorageNode client, InputStream inputStream, long fileSize,
                                              String fileExtName, Set<MetaData> metaDataSet, boolean isAppenderFile) {
        // 上传文件
        StorageUploadFileCommand command = new StorageUploadFileCommand(client.getStoreIndex(), inputStream,
                fileExtName, fileSize, isAppenderFile);
        StorePath path = fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        // 上传metadata
        if (hasMetaData(metaDataSet)) {
            StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(path.getGroup(), path.getPath(),
                    metaDataSet, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
            fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
        }
        return path;
    }

    @Override
    public StorePath uploadSlaveFile(String groupName, String masterFilePath, String prefix, File file) {
        return this.uploadSlaveFile(
                UploadSalveFileRequest.builder().groupName(groupName)
                        .masterPath(masterFilePath).prefix(prefix)
                        .file(file).build()
        );
    }

    @Override
    public StorePath uploadSlaveFile(String groupName, String masterFilePath, String prefix,
                                     InputStream stream, long fileSize, String fileExtName) {
        return this.uploadSlaveFile(
                UploadSalveFileRequest.builder().groupName(groupName)
                        .masterPath(masterFilePath).prefix(prefix)
                        .stream(stream, fileSize, fileExtName).build()
        );
    }

    @Override
    public StorePath uploadSlaveFile(UploadSalveFileRequest request) {
        String groupName = request.groupName();
        InputStream stream = getInputStream(request);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, request.masterPath());

        StorageUploadSlaveFileCommand command = new StorageUploadSlaveFileCommand(stream,
                request.fileSize(), request.masterPath(), request.prefix(), request.fileExtName());
        StorePath path = fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);

        Set<MetaData> metaData = request.metaData();
        // 上传metadata
        if (hasMetaData(metaData)) {
            StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(path.getGroup(), path.getPath(),
                    metaData, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
            fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
        }
        return path;

    }

    @Override
    public ImageStorePath uploadImage(UploadImageRequest request) {
        ImageStorePath imageStorePath = new ImageStorePath();
        //获取上传流
        byte[] bytes;
        try (InputStream stream = getStream(request.stream(), request.file())) {
            bytes = inputStreamToByte(stream);

            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                    .groupName(request.groupName())
                    .listener(request.listener())
                    .stream(new ByteArrayInputStream(bytes), request.fileSize(), request.fileExtName())
                    .metaData(request.metaData())
                    .build();
            StorePath img = uploadFile(uploadFileRequest);
            imageStorePath.setImg(img);
            LOGGER.debug("upload image success img {}", img);

            Set<UploadImageRequest.ThumbImageRequest> thumbImageRequests = request.thumbImages();
            List<StorePath> paths = new ArrayList<>(thumbImageRequests.size());
            // TODO 多线程优化
            for (UploadImageRequest.ThumbImageRequest thumbImageRequest : thumbImageRequests) {
                ThumbImage thumbImage = thumbImageRequest.thumbImage();
                Set<MetaData> metaData = thumbImageRequest.thumbMetaData();
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                     ByteArrayInputStream thumbImageStream = generateThumbImageStream(inputStream, thumbImage)) {
                    // 获取文件大小
                    long fileSize = thumbImageStream.available();
                    // 获取配置缩略图前缀
                    String prefixName = thumbImage.getPrefixName();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("获取到缩略图前缀{}", prefixName);
                    }
                    UploadSalveFileRequest uploadSalveFileRequest = UploadSalveFileRequest.builder()
                            .stream(thumbImageStream, fileSize, request.fileExtName())
                            .groupName(img.getGroup())
                            .listener(request.listener())
                            .masterPath(img.getPath())
                            .prefix(prefixName)
                            .metaData(metaData)
                            .build();

                    StorePath thumbStorePath = this.uploadSlaveFile(uploadSalveFileRequest);
                    LOGGER.debug("upload thumb image success thumbImage={}", thumbImage);
                    paths.add(thumbStorePath);
                }
            }
            imageStorePath.setThumbs(paths);
            return imageStorePath;
        } catch (IOException e) {
            throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
        }
    }

    @Override
    public StorePath createThumbImage(UploadImageRequest request) {
        return createThumbImages(request).get(0);
    }

    @Override
    public List<StorePath> createThumbImages(UploadImageRequest request) {
        Set<UploadImageRequest.ThumbImageRequest> thumbImageRequests = request.thumbImages();
        CodeUtils.validateCollectionNotEmpty(thumbImageRequests, "thumbImage");
        List<StorePath> paths = new ArrayList<>(thumbImageRequests.size());

        String groupName = getGroupName(request);
        StorageNode client = trackerClient.getStoreStorage(groupName);

        for (UploadImageRequest.ThumbImageRequest thumbImageRequest : thumbImageRequests) {
            ThumbImage thumbImage = thumbImageRequest.thumbImage();
            try (InputStream stream = getStream(request.stream(), request.file());
                 ByteArrayInputStream thumbImageStream = generateThumbImageStream(stream, thumbImage)) {
                Set<MetaData> metaDataSet = thumbImageRequest.thumbMetaData();

                StorePath storePath = uploadFileAndMetaData(client,
                        progressStream(request.listener(), thumbImageStream),
                        thumbImageStream.available(),
                        request.fileExtName(), metaDataSet, false);
                paths.add(storePath);
            } catch (IOException e) {
                throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
            }
        }
        return paths;
    }

    /**
     * 获取byte流
     *
     * @param inputStream
     * @return
     */
    private byte[] inputStreamToByte(InputStream inputStream) {
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
        }
    }

    /**
     * 生成缩略图
     *
     * @param inputStream
     * @param thumbImage
     * @return
     * @throws IOException
     */
    private ByteArrayInputStream generateThumbImageStream(InputStream inputStream,
                                                          ThumbImage thumbImage) throws IOException {
        //根据传入配置生成缩略图
        if (thumbImage.getPercent() != 0) {
            return generateThumbImageByPercent(inputStream, thumbImage);
        } else {
            return generateThumbImageBySize(inputStream, thumbImage);
        }
    }

    /**
     * 根据传入比例生成缩略图
     *
     * @param inputStream
     * @param thumbImage
     * @return
     * @throws IOException
     */
    private ByteArrayInputStream generateThumbImageByPercent(InputStream inputStream,
                                                             ThumbImage thumbImage) throws IOException {
        LOGGER.debug("根据传入比例生成缩略图");
        // 在内存当中生成缩略图
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //@formatter:off
        Thumbnails
                .of(inputStream)
                .scale(thumbImage.getPercent())
                .imageType(BufferedImage.TYPE_INT_ARGB)
                .toOutputStream(out);
        //@formatter:on
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * 根据传入尺寸生成缩略图
     *
     * @param inputStream
     * @param thumbImage
     * @return
     * @throws IOException
     */
    private ByteArrayInputStream generateThumbImageBySize(InputStream inputStream,
                                                          ThumbImage thumbImage) throws IOException {
        LOGGER.debug("根据传入尺寸生成缩略图");
        // 在内存当中生成缩略图
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //@formatter:off
        Thumbnails
                .of(inputStream)
                .size(thumbImage.getWidth(), thumbImage.getHeight())
                .imageType(BufferedImage.TYPE_INT_ARGB)
                .toOutputStream(out);
        //@formatter:on
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * 检查是否有MetaData
     *
     * @param metaDataSet
     * @return
     */
    protected boolean hasMetaData(Set<MetaData> metaDataSet) {
        return null != metaDataSet && !metaDataSet.isEmpty();
    }

    @Override
    public Set<MetaData> getMetadata(String groupName, String path) {
        return this.getMetadata(MetaDataInfoRequest.builder()
                .groupName(groupName).path(path).build());
    }

    @Override
    public Set<MetaData> getMetadata(MetaDataInfoRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageGetMetadataCommand command = new StorageGetMetadataCommand(groupName, path);
        return fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    private void uploadMetaData(InetSocketAddress address, String groupName, String path,
                                StorageMetadataSetType type, Set<MetaData> metaData) {
        if (type == null) {
            return;
        }
        StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(groupName, path, metaData, type);
        if (type == StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE) {
            fdfsConnectionManager.executeFdfsCmd(address, setMDCommand);
        }
        if (type == StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_MERGE) {
            fdfsConnectionManager.executeFdfsCmd(address, setMDCommand);
        }
    }

    @Override
    public void overwriteMetadata(String groupName, String path, Set<MetaData> metaData) {
        this.overwriteMetadata(MetaDataRequest.builder()
                .groupName(groupName).path(path)
                .metaData(metaData).build());
    }

    @Override
    public void overwriteMetadata(MetaDataRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        uploadMetaData(client.getInetSocketAddress(), groupName, path,
                StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE, request.metaData());
    }

    @Override
    public void mergeMetadata(String groupName, String path, Set<MetaData> metaData) {
        this.mergeMetadata(MetaDataRequest.builder()
                .groupName(groupName).path(path).metaData(metaData).build());
    }

    @Override
    public void mergeMetadata(MetaDataRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        uploadMetaData(client.getInetSocketAddress(), groupName, path,
                StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_MERGE, request.metaData());
    }

    @Override
    public void deleteMetadata(String groupName, String path) {
        this.overwriteMetadata(MetaDataRequest.builder()
                .groupName(groupName).path(path).build());
    }

    @Override
    public FileInfo queryFileInfo(String groupName, String path) {
        return this.queryFileInfo(FileInfoRequest.builder()
                .groupName(groupName).path(path).build());
    }

    @Override
    public FileInfo queryFileInfo(FileInfoRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageQueryFileInfoCommand command = new StorageQueryFileInfoCommand(groupName, path);
        return fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public void deleteFile(String groupName, String path) {
        this.deleteFile(FileInfoRequest.builder()
                .groupName(groupName).path(path).build());
    }

    @Override
    public void deleteFile(FileInfoRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageDeleteFileCommand command = new StorageDeleteFileCommand(groupName, path);
        fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public <T> T downloadFile(String groupName, String path, DownloadCallback<T> callback) {
        return this.downloadFile(DownloadFileRequest.builder()
                .groupName(groupName).path(path).build(), callback);
    }

    @Override
    public <T> T downloadFile(String groupName, String path, long fileOffset, long downLoadSize, DownloadCallback<T> callback) {
        return this.downloadFile(DownloadFileRequest.builder()
                .groupName(groupName).path(path)
                .offset(fileOffset).fileSize(downLoadSize).build(), callback);
    }

    @Override
    public <T> T downloadFile(DownloadFileRequest request, DownloadCallback<T> callback) {
        String groupName = request.groupName();
        String path = request.path();
        CodeUtils.validateNotNull(callback, "callback");
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageDownloadCommand<T> command = new StorageDownloadCommand<>(groupName, path,
                request.offset(), request.fileSize(), callback);
        return fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    // region appender

    @Override
    public StorePath uploadAppenderFile(File file) {
        return this.uploadAppenderFile(UploadAppendFileRequest.builder().file(file).build());
    }

    @Override
    public StorePath uploadAppenderFile(String groupName, File file) {
        return this.uploadAppenderFile(
                UploadAppendFileRequest.builder().groupName(groupName).file(file).build()
        );
    }

    @Override
    public StorePath uploadAppenderFile(InputStream stream, long fileSize, String fileExtName) {
        return this.uploadAppenderFile(
                UploadAppendFileRequest.builder().stream(stream, fileSize, fileExtName).build()
        );
    }

    @Override
    public StorePath uploadAppenderFile(String groupName, InputStream stream, long fileSize, String fileExtName) {
        return this.uploadAppenderFile(
                UploadAppendFileRequest.builder().groupName(groupName).stream(stream, fileSize, fileExtName).build()
        );
    }

    @Override
    public StorePath uploadAppenderFile(UploadAppendFileRequest request) {
        String groupName = getGroupName(request);
        InputStream stream = getInputStream(request);
        StorageNode client = trackerClient.getStoreStorage(groupName);
        return uploadFileAndMetaData(client, stream, request.fileSize(),
                request.fileExtName(), request.metaData(), true);
    }

    @Override
    public void appendFile(String groupName, String path, File file) {
        this.appendFile(AppendFileRequest.builder()
                .groupName(groupName).path(path).file(file).build());
    }

    @Override
    public void appendFile(String groupName, String path, InputStream stream, long fileSize) {
        this.appendFile(AppendFileRequest.builder()
                .groupName(groupName).path(path).stream(stream, fileSize).build());
    }

    @Override
    public void appendFile(AppendFileRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        InputStream stream = getInputStream(request);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageAppendFileCommand command = new StorageAppendFileCommand(stream, request.fileSize(), path);
        fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        uploadMetaData(client.getInetSocketAddress(), groupName, path, request.metaType(), request.metaData());
    }

    @Override
    public void modifyFile(String groupName, String path, InputStream stream, long fileSize, long offset) {
        this.modifyFile(ModifyFileRequest.builder()
                .groupName(groupName).path(path)
                .stream(stream, fileSize, offset).build());
    }

    @Override
    public void modifyFile(ModifyFileRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        InputStream stream = getInputStream(request);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageModifyCommand command = new StorageModifyCommand(path, stream, request.fileSize(), request.offset());
        fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        uploadMetaData(client.getInetSocketAddress(), groupName, path, request.metaType(), request.metaData());
    }

    @Override
    public void truncateFile(String groupName, String path) {
        this.truncateFile(TruncateFileRequest.builder()
                .groupName(groupName).path(path).fileSize(0).build());
    }

    @Override
    public void truncateFile(String groupName, String path, long size) {
        this.truncateFile(TruncateFileRequest.builder()
                .groupName(groupName).path(path).fileSize(size).build());
    }

    @Override
    public void truncateFile(TruncateFileRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageTruncateCommand command = new StorageTruncateCommand(path, request.fileSize());
        fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public StorePath regenerateAppenderFile(String groupName, String path) {
        return this.regenerateAppenderFile(RegenerateAppenderFileRequest.builder()
                .groupName(groupName).path(path).build());
    }

    @Override
    public StorePath regenerateAppenderFile(RegenerateAppenderFileRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageRegenerateAppendFileCommand command = new StorageRegenerateAppendFileCommand(path);
        return fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    // endregion appender

    // region multipart

    private final MultipartAttachmentAccessor multipartAttachment = new DefaultMultipartAttachmentAccessor(this);

    @Override
    public StorePath initMultipartUpload(long fileSize, long partSize, String fileExtName) {
        return this.initMultipartUpload(InitMultipartUploadRequest.builder()
                .fileSize(fileSize).partSize(partSize).fileExtName(fileExtName).build());
    }

    @Override
    public StorePath initMultipartUpload(InitMultipartUploadRequest request) {
        String groupName = getGroupName(request);
        long fileSize = request.fileSize();
        UploadAppendFileRequest uploadFileRequest = UploadAppendFileRequest.builder()
                .groupName(groupName)
                .stream(new ByteArrayInputStream(new byte[0]), 0, request.fileExtName())
                .build();
        StorePath storePath = uploadAppenderFile(uploadFileRequest);
        String group = storePath.getGroup();
        String path = storePath.getPath();
        if (fileSize > 0) {
            this.truncateFile(group, path, fileSize);
        }
        // save attachment
        multipartAttachment.put(group, path, new MultipartUploadAttachment(fileSize, request.partSize()));
        return storePath;
    }

    @Override
    public void uploadMultipart(String groupName, String path, File part, int partNumber) {
        this.uploadMultipart(UploadMultipartRequest.builder()
                .groupName(groupName).path(path).file(part, partNumber).build());
    }

    @Override
    public void uploadMultipart(String groupName, String path, InputStream part, int partNumber) {
        this.uploadMultipart(UploadMultipartRequest.builder()
                .groupName(groupName).path(path).stream(part, partNumber).build());
    }

    @Override
    public void uploadMultipart(UploadMultipartRequest request) {
        String groupName = request.groupName();
        String path = request.path();
        int partNumber = request.partNumber();
        MultipartUploadAttachment attachment = multipartAttachment.get(groupName, path);
        if (attachment == null) {
            throw new FdfsClientException("Not found multipart attachment.");
        }
        long partCount = attachment.getPartCount();
        if (partCount <= 0) {
            throw new FdfsClientException("No part need to be uploaded please check your file size or part size.");
        }
        if (partNumber > partCount) {
            throw new FdfsClientException("Incorrect partNumber it should be in the range of 1-" + partCount);
        }

        if (attachment.getFileSize() == 0) {
            // size=0 则只有一个size为0的分片 不需要在上传了
            return;
        }

        long[] att = attachment.offset(partNumber);
        long offset = att[0], size = att[1];
        ModifyFileRequest modifyFileRequest;
        ModifyFileRequest.Builder builder = ModifyFileRequest.builder()
                .groupName(groupName).path(path);
        if (request.file() != null) {
            modifyFileRequest = builder.file(request.file(), offset).build();
        } else {
            modifyFileRequest = builder.stream(request.stream(), size, offset).build();
        }
        this.modifyFile(modifyFileRequest);
    }

    @Override
    public StorePath completeMultipartUpload(String groupName, String path, boolean regenerate) {
        return this.completeMultipartUpload(CompleteMultipartRequest.builder()
                .groupName(groupName).path(path).regenerate(regenerate).build());
    }

    @Override
    public StorePath completeMultipartUpload(CompleteMultipartRequest request) {
        StorePath storePath = new StorePath(request.groupName(), request.path());
        if (request.regenerate()) {
            storePath = this.regenerateAppenderFile(storePath.getGroup(), storePath.getPath());
        }
        String group = storePath.getGroup();
        String path = storePath.getPath();
        // 重置metadata
        this.mergeMetadata(group, path, request.metaData());
        //remove attachment
        multipartAttachment.remove(group, path);
        return storePath;
    }

    // endregion multipart

    /**
     * 获取分组
     *
     * @param args
     * @return
     */
    private String getGroupName(GroupArgs args) {
        String groupName = args.groupName();
        if (StringUtils.isNotBlank(groupName)) {
            return groupName;
        }
        return StringUtils.isBlank(defaultGroup) ? null : defaultGroup;
    }

    /**
     * 获取文件流
     *
     * @param args
     * @return
     */
    private InputStream getInputStream(final AbstractFileArgs args) {
        final InputStream stream = getStream(args.stream(), args.file());
        return progressStream(args.listener(), stream);
    }

    private InputStream progressStream(final ProgressListener listener, final InputStream stream) {
        return (listener == null || listener == ProgressListener.NOOP) ?
                stream : ProgressInputStream.inputStreamForRequest(stream, listener);
    }

    private InputStream getStream(final InputStream origin, final File file) {
        InputStream input = origin;
        if (file == null) {
            if (input == null) {
                throw new IllegalArgumentException("upload content cannot be empty. ");
            }
            input = InputStreamWrapper.wrap(input);
        } else {
            input = InputStreamWrapper.wrap(file);
        }
        return input;
    }

}
