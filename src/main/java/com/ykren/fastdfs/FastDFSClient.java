package com.ykren.fastdfs;

import com.ykren.fastdfs.conn.FdfsConnectionPool;
import com.ykren.fastdfs.conn.TrackerConnectionManager;
import com.ykren.fastdfs.event.ProgressInputStream;
import com.ykren.fastdfs.event.ProgressListener;
import com.ykren.fastdfs.exception.FdfsIOException;
import com.ykren.fastdfs.exception.FdfsUploadImageException;
import com.ykren.fastdfs.model.AbstractFileArgs;
import com.ykren.fastdfs.model.AppendFileRequest;
import com.ykren.fastdfs.model.CompleteMultipartRequest;
import com.ykren.fastdfs.model.DownloadFileRequest;
import com.ykren.fastdfs.model.FileInfoRequest;
import com.ykren.fastdfs.model.GroupArgs;
import com.ykren.fastdfs.model.InitMultipartUploadRequest;
import com.ykren.fastdfs.model.MetaDataInfoRequest;
import com.ykren.fastdfs.model.MetaDataRequest;
import com.ykren.fastdfs.model.ModifyFileRequest;
import com.ykren.fastdfs.model.RegenerateAppenderFileRequest;
import com.ykren.fastdfs.model.ThumbImage;
import com.ykren.fastdfs.model.TruncateFileRequest;
import com.ykren.fastdfs.model.UploadFileRequest;
import com.ykren.fastdfs.model.UploadImageRequest;
import com.ykren.fastdfs.model.UploadMultipartPartRequest;
import com.ykren.fastdfs.model.UploadSalveFileRequest;
import com.ykren.fastdfs.model.fdfs.FileInfo;
import com.ykren.fastdfs.model.fdfs.ImageStorePath;
import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.fdfs.StorageNode;
import com.ykren.fastdfs.model.fdfs.StorageNodeInfo;
import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.proto.storage.DownloadCallback;
import com.ykren.fastdfs.model.proto.storage.StorageAppendFileCommand;
import com.ykren.fastdfs.model.proto.storage.StorageDeleteFileCommand;
import com.ykren.fastdfs.model.proto.storage.StorageDownloadCommand;
import com.ykren.fastdfs.model.proto.storage.StorageGetMetadataCommand;
import com.ykren.fastdfs.model.proto.storage.StorageModifyCommand;
import com.ykren.fastdfs.model.proto.storage.StorageQueryFileInfoCommand;
import com.ykren.fastdfs.model.proto.storage.StorageRegenerateAppendFileCommand;
import com.ykren.fastdfs.model.proto.storage.StorageSetMetadataCommand;
import com.ykren.fastdfs.model.proto.storage.StorageTruncateCommand;
import com.ykren.fastdfs.model.proto.storage.StorageUploadFileCommand;
import com.ykren.fastdfs.model.proto.storage.StorageUploadSlaveFileCommand;
import com.ykren.fastdfs.model.proto.storage.enums.StorageMetadataSetType;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.ykren.fastdfs.common.CodeUtils.validateNotBlankString;
import static com.ykren.fastdfs.common.CodeUtils.validateNotNull;
import static com.ykren.fastdfs.common.FastDFSUtils.handlerFilename;
import static com.ykren.fastdfs.model.proto.OtherConstants.DEFAULT_STREAM_BUFFER_SIZE;

/**
 * FastDFSClient默认客户端
 *
 * @author ykren
 * @date 2022/1/21
 */
public class FastDFSClient implements FastDFS {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FastDFSClient.class);

    private static final String GROUP = "group";

    private final TrackerClient trackerClient;

    private final TrackerConnectionManager trackerConnectionManager;

    /**
     * 分组group 优先级大于参数 ${@link #getGroup(GroupArgs)}
     * 优点是可以固定分组上传 不用每次都设置group
     */
    private String group;

    public FastDFSClient(final List<String> trackerServers) {
        this(trackerServers, null);
    }

    public FastDFSClient(final List<String> trackerServers, String group) {
        this(trackerServers, group, new FastDFSConfiguration());
    }

    public FastDFSClient(final List<String> trackerServers, String group, final FastDFSConfiguration configuration) {
        this.trackerConnectionManager = new TrackerConnectionManager(trackerServers, new FdfsConnectionPool(configuration));
        this.trackerClient = new DefaultTrackerClient(trackerConnectionManager);
        this.group = group;
    }

    public TrackerConnectionManager getConnectionManager() {
        return trackerConnectionManager;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public TrackerClient trackerClient() {
        return trackerClient;
    }

    @Override
    public void close() {
        trackerConnectionManager.getPool().close();
    }

    @Override
    public StorePath uploadFile(UploadFileRequest request) {
        //获取上传流
        InputStream is = wrapperStream(request);
        String groupName = getGroup(request);
        // 获取存储节点
        StorageNode client = trackerClient.getStoreStorage(groupName);
        // 上传文件
        return uploadFileAndMetaData(client, is,
                request.fileSize(), handlerFilename(request.fileExtName()), request.metaData(), false);
    }

    @Override
    public StorePath uploadSlaveFile(UploadSalveFileRequest request) {
        String groupName = getGroup(request);
        validateNotBlankString(groupName, GROUP);
        //获取上传流
        InputStream is = wrapperStream(request);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, request.masterPath());
        //上传从文件
        String handlerPrefix = handlerFilename(request.prefix());
        String prefix = handlerPrefix.isEmpty() ? "." : handlerPrefix;
        return uploadSalveFileAndMetaData(client, request.masterPath(),
                is, request.fileSize(), prefix, handlerFilename(request.fileExtName()), request.metaData());
    }

    @Override
    public ImageStorePath uploadImage(UploadImageRequest request) {
        if (request.onlyThumb()) {
            LOGGER.debug("only upload thumb image imgPath return null");
            StorePath storePath = uploadThumbImage(request);
            return new ImageStorePath(null, storePath);
        }
        return uploadImageAndThumb(request);
    }

    private StorePath uploadThumbImage(UploadImageRequest request) {
        ThumbImage thumbImage = request.thumbImage();
        try (InputStream stream = getStream(request);
             ByteArrayInputStream thumbImageStream = generateThumbImageStream(stream, thumbImage)) {
            String groupName = getGroup(request);
            // 获取存储节点
            StorageNode client = trackerClient.getStoreStorage(groupName);
            String fileExtName = handlerFilename(request.fileExtName());
            Set<MetaData> metaDataSet = request.thumbMetaData();
            return uploadFileAndMetaData(client, progressStream(request.listener(), thumbImageStream), thumbImageStream.available(),
                    fileExtName, metaDataSet, false);
        } catch (IOException e) {
            throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
        }
    }

    private ImageStorePath uploadImageAndThumb(UploadImageRequest request) {
        //获取上传流
        byte[] bytes = null;
        try (InputStream is = getStream(request)) {
            bytes = inputStreamToByte(is);
            String groupName = getGroup(request);
            // 上传文件
            String fileExtName = handlerFilename(request.fileExtName());
            // 获取存储节点
            StorageNode client = trackerClient.getStoreStorage(groupName);
            StorePath imgPath = uploadFileAndMetaData(client, progressStream(request.listener(), new ByteArrayInputStream(bytes)),
                    request.fileSize(), fileExtName, request.metaData(), false);
            LOGGER.debug("upload image success");

            //如果设置了需要上传缩略图
            ThumbImage thumbImage = request.thumbImage();
            StorePath thumbPath = null;
            if (null != thumbImage) {
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                     ByteArrayInputStream thumbImageStream = generateThumbImageStream(inputStream, thumbImage)) {
                    // 获取文件大小
                    long fileSize = thumbImageStream.available();
                    // 获取配置缩略图前缀
                    String prefixName = thumbImage.getPrefixName();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("获取到缩略图前缀{}", prefixName);
                    }
                    StorageNodeInfo storageNodeInfo = new StorageNodeInfo(client.getIp(), client.getPort());
                    thumbPath = uploadSalveFileAndMetaData(storageNodeInfo, imgPath.getPath(),
                            progressStream(request.listener(), thumbImageStream), fileSize, prefixName, fileExtName, request.thumbMetaData());
                    LOGGER.debug("upload thumb image success thumbImage={}", thumbImage);
                }
            }
            return new ImageStorePath(imgPath, thumbPath);
        } catch (IOException e) {
            throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
        } finally {
            bytes = null;
        }
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
        StorePath path = trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        // 上传metadata
        if (hasMetaData(metaDataSet)) {
            StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(path.getGroup(), path.getPath(),
                    metaDataSet, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
            trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
        }
        return path;
    }

    /**
     * 上传从文件和元数据
     *
     * @param client
     * @param inputStream
     * @param fileSize
     * @param fileExtName
     * @param metaDataSet
     * @return
     */
    protected StorePath uploadSalveFileAndMetaData(StorageNodeInfo client, String masterPath,
                                                   InputStream inputStream, long fileSize,
                                                   String prefix, String fileExtName, Set<MetaData> metaDataSet) {
        StorageUploadSlaveFileCommand command = new StorageUploadSlaveFileCommand(inputStream, fileSize, masterPath,
                prefix, fileExtName);
        StorePath path = trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        // 上传metadata
        if (hasMetaData(metaDataSet)) {
            StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(path.getGroup(), path.getPath(),
                    metaDataSet, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
            trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
        }
        return path;
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
    public Set<MetaData> getMetadata(MetaDataInfoRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageGetMetadataCommand command = new StorageGetMetadataCommand(groupName, path);
        return trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public void overwriteMetadata(MetaDataRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        uploadMetaData(client, groupName, path, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE, request.metaData());
    }

    @Override
    public void mergeMetadata(MetaDataRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        Set<MetaData> metaDataSet = (request.metaData() == null ? Collections.emptySet() : request.metaData());
        if (metaDataSet.isEmpty()) {
            LOGGER.warn("metadata is empty not merge");
            return;
        }
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        uploadMetaData(client, groupName, path, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_MERGE, request.metaData());
    }

    @Override
    public FileInfo queryFileInfo(FileInfoRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageQueryFileInfoCommand command = new StorageQueryFileInfoCommand(groupName, path);
        return trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public void deleteFile(FileInfoRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageDeleteFileCommand command = new StorageDeleteFileCommand(groupName, path);
        trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public <T> T downloadFile(DownloadFileRequest request, DownloadCallback<T> callback) {
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        validateNotNull(callback, "callback");
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageDownloadCommand<T> command = new StorageDownloadCommand<>(groupName, path,
                request.offset(), request.fileSize(), callback);
        return trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    // region appender

    @Override
    public StorePath uploadAppenderFile(UploadFileRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        // 获取上传流
        InputStream inputStream = wrapperStream(request);
        // 获取存储节点
        StorageNode client = trackerClient.getStoreStorage(groupName);
        // 上传追加文件
        return uploadFileAndMetaData(client, inputStream,
                request.fileSize(), handlerFilename(request.fileExtName()), request.metaData(), true);
    }

    @Override
    public void appendFile(AppendFileRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        // 获取上传流
        InputStream inputStream = wrapperStream(request);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageAppendFileCommand command = new StorageAppendFileCommand(inputStream, request.fileSize(), path);
        trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        // 上传metadata
        uploadMetaData(client, groupName, path, request.metaType(), request.metaData());
    }

    private void uploadMetaData(StorageNodeInfo client, String groupName, String path, StorageMetadataSetType type, Set<MetaData> metaData) {
        if (type == null || client == null || StringUtils.isBlank(groupName) || StringUtils.isBlank(path)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("args is null upload metadata fail");
            }
            return;
        }
        metaData = (metaData == null ? Collections.emptySet() : metaData);
        StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(groupName, path,
                metaData, type);
        if (type == StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE) {
            trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
        }
        if (type == StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_MERGE) {
            if (metaData.isEmpty()) {
                LOGGER.warn("metadata is empty merge failed");
                return;
            }
            trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
        }

    }

    @Override
    public void modifyFile(ModifyFileRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        // 获取上传流
        InputStream inputStream = wrapperStream(request);
        // 获取存储节点
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageModifyCommand command = new StorageModifyCommand(path, inputStream, request.fileSize(), request.offset());
        // 修改文件
        trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        uploadMetaData(client, groupName, path, request.metaType(), request.metaData());
    }

    @Override
    public void truncateFile(TruncateFileRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        // 获取存储节点
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageTruncateCommand command = new StorageTruncateCommand(path, request.fileSize());
        // 清除文件
        trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public StorePath regenerateAppenderFile(RegenerateAppenderFileRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageRegenerateAppendFileCommand command = new StorageRegenerateAppendFileCommand(path);
        return trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    // endregion appender

    // region multipart

    @Override
    public StorePath initMultipartUpload(InitMultipartUploadRequest request) {
        String groupName = getGroup(request);
        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                .group(groupName)
                .stream(new ByteArrayInputStream(new byte[]{}), 0, handlerFilename(request.fileExtName()))
                .build();
        StorePath storePath = uploadAppenderFile(uploadFileRequest);
        if (request.fileSize() > 0) {
            TruncateFileRequest truncateFileRequest = TruncateFileRequest.builder()
                    .group(storePath.getGroup())
                    .path(storePath.getPath())
                    .fileSize(request.fileSize())
                    .build();
            truncateFile(truncateFileRequest);
        }
        return storePath;
    }


    @Override
    public void uploadMultipart(UploadMultipartPartRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        long fileSize = request.fileSize();
        long partSize = request.partSize();
        int partNumber = request.partNumber();
        // 计算offset
        long offset = 0;
        if (partNumber > 1) {
            offset = (partNumber - 1) * partSize;
        }
        ModifyFileRequest modifyFileRequest = ModifyFileRequest.builder()
                .group(groupName)
                .path(path)
                .stream(request.stream(), fileSize, offset)
                .file(request.file(), offset).build();
        modifyFile(modifyFileRequest);
    }

    @Override
    public StorePath completeMultipartUpload(CompleteMultipartRequest request) {
        String groupName = getGroup(request);
        String path = request.path();
        validateNotBlankString(groupName, GROUP);
        StorePath storePath = new StorePath(groupName, path);
        if (request.regenerate()) {
            RegenerateAppenderFileRequest reRequest = RegenerateAppenderFileRequest.builder()
                    .group(storePath.getGroup())
                    .path(storePath.getPath())
                    .build();
            storePath = regenerateAppenderFile(reRequest);
        }
        // 重置metadata
        MetaDataRequest metaDataRequest = MetaDataRequest.builder()
                .group(storePath.getGroup())
                .path(storePath.getPath())
                .metaData(request.metaData())
                .build();
        overwriteMetadata(metaDataRequest);
        return storePath;
    }

    // endregion multipart

    /**
     * 获取分组
     *
     * @param args
     * @return
     */
    private String getGroup(GroupArgs args) {
        String groupName = StringUtils.isNotBlank(group) ? group : args.group();
        LOGGER.debug("获取到上传的group={}", groupName);
        return groupName;
    }

    /**
     * 装饰流
     *
     * @param args
     * @return
     */
    private InputStream wrapperStream(AbstractFileArgs args) {
        InputStream is = getStream(args);
        return progressStream(args.listener(), is);
    }

    private InputStream progressStream(ProgressListener listener, InputStream is) {
        return (listener == null || listener == ProgressListener.NOOP) ?
                is : ProgressInputStream.inputStreamForRequest(is, listener);
    }

    /**
     * 获取上传流
     */
    private InputStream getStream(AbstractFileArgs args) {
        Objects.requireNonNull(args);
        File file = args.file();
        InputStream is = args.stream();
        if (file != null) {
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new FdfsIOException(e);
            }
        } else {
            is = new BufferedInputStream(is, DEFAULT_STREAM_BUFFER_SIZE);
        }
        return is;
    }

}
