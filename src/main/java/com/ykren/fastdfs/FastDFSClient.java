package com.ykren.fastdfs;

import com.ykren.fastdfs.conn.FdfsConnectionPool;
import com.ykren.fastdfs.conn.TrackerConnectionManager;
import com.ykren.fastdfs.exception.FdfsIOException;
import com.ykren.fastdfs.model.*;
import com.ykren.fastdfs.model.fdfs.*;
import com.ykren.fastdfs.model.proto.OtherConstants;
import com.ykren.fastdfs.model.proto.storage.*;
import com.ykren.fastdfs.model.proto.storage.enums.StorageMetadataSetType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    protected static final Logger LOGGER = LoggerFactory.getLogger(FastDFSClient.class);

    private final TrackerClient trackerClient;

    private final TrackerConnectionManager trackerConnectionManager;

    /**
     * 分组group 优先级大于参数 ${@link FastDFSClient#getGroup(GroupArgs)}
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
        InputStream is = getInputStream(request.file(), request.inputStream());
        String groupName = getGroup(request);
        LOGGER.debug("获取到上传的group={}", groupName);
        // 上传文件
        StorePath storePath = uploadFileAndMetaData(groupName, is,
                request.fileSize(), request.fileExtName(), request.metaData(), false);
        //关闭流信息
        autoClose(is, request.autoClose());
        return storePath;
    }

    @Override
    public StorePath uploadSlaveFile(UploadSalveFileRequest request) {
        String groupName = getGroup(request);
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        //获取上传流
        InputStream is = getInputStream(request.file(), request.inputStream());
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, request.masterFilePath());
        //上传从文件
        StorePath storePath = uploadSalveFileAndMetaData(client, request.masterFilePath(),
                is, request.fileSize(), request.prefix(), request.fileExtName(), request.metaData());
        //关闭流信息
        autoClose(is, request.autoClose());
        return storePath;
    }

    /**
     * 上传文件和元数据
     *
     * @param groupName
     * @param inputStream
     * @param fileSize
     * @param fileExtName
     * @param metaDataSet
     * @return
     */
    protected StorePath uploadFileAndMetaData(String groupName, InputStream inputStream, long fileSize,
                                              String fileExtName, Set<MetaData> metaDataSet, boolean isAppenderFile) {
        // 获取存储节点
        StorageNode client = trackerClient.getStoreStorage(groupName);
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
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageGetMetadataCommand command = new StorageGetMetadataCommand(groupName, path);
        return trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public void overwriteMetadata(MetaDataRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        uploadMetaData(client, groupName, path, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE, request.metaData());
    }

    @Override
    public void mergeMetadata(MetaDataRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
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
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageQueryFileInfoCommand command = new StorageQueryFileInfoCommand(groupName, path);
        return trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public void deleteFile(FileInfoRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageDeleteFileCommand command = new StorageDeleteFileCommand(groupName, path);
        trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    @Override
    public <T> T downloadFile(DownloadFileRequest<T> request) {
        String groupName = getGroup(request);
        String path = request.path();
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageDownloadCommand<T> command = new StorageDownloadCommand<>(groupName, path,
                request.offset(), request.fileSize(), request.callback());
        return trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    // region appender file

    @Override
    public StorePath uploadAppenderFile(UploadFileRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        // 获取上传流
        InputStream inputStream = getInputStream(request.file(), request.inputStream());
        // 上传追加文件
        StorePath storePath = uploadFileAndMetaData(groupName, inputStream,
                request.fileSize(), request.fileExtName(), request.metaData(), true);
        // 关闭流
        autoClose(inputStream, request.autoClose());
        return storePath;
    }

    @Override
    public void appendFile(AppendFileRequest request) {
        // 获取分组
        String groupName = getGroup(request);
        String path = request.path();
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        // 获取上传流
        InputStream inputStream = getInputStream(request.file(), request.inputStream());
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageAppendFileCommand command = new StorageAppendFileCommand(inputStream, request.fileSize(), path);
        trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        // 上传metadata
        uploadMetaData(client, groupName, path, request.metaType(), request.metaData());
        // 关闭流
        autoClose(inputStream, request.autoClose());
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
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        // 获取上传流
        InputStream inputStream = getInputStream(request.file(), request.inputStream());
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
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
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
        CodeUtils.validateNotBlankString(groupName, OtherConstants.GROUP);
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageRegenerateAppendFileCommand command = new StorageRegenerateAppendFileCommand(path);
        return trackerConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    // endregion appender file

    // region multipart     //TODO 待实现

    @Override
    public StorePath initMultipartUpload(InitMultipartUploadRequest request) {
        return null;
    }


    @Override
    public void uploadPart(UploadMultipartPartRequest request) {

    }

    @Override
    public StorePath completeMultipartUpload(CompleteMultipartRequest request) {
        return null;
    }

    @Override
    public void abortMultipartUpload(AbortMultipartRequest request) {

    }

    // endregion multipart

    /**
     * 获取分组
     *
     * @param args
     * @return
     */
    private String getGroup(GroupArgs args) {
        return StringUtils.isNotBlank(group) ? group : args.group();
    }

    /**
     * 获取文件流
     *
     * @return
     */
    private InputStream getInputStream(File file, InputStream inputStream) {
        if (file != null) {
            try {
                return FileUtils.openInputStream(file);
            } catch (IOException e) {
                throw new FdfsIOException("open local file io exception", e);
            }
        }
        return inputStream;
    }

    /**
     * 自动关闭流
     *
     * @param is
     * @param autoClose
     */
    private void autoClose(InputStream is, boolean autoClose) {
        if (autoClose) {
            IOUtils.closeQuietly(is);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("上传流关闭={}", is);
            }
        }
    }
}
