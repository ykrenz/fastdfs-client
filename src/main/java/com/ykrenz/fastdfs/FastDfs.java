package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.model.*;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.ImageStorePath;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.proto.storage.DownloadCallback;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * FastDFS文件存储客户端
 *
 * @author ykren
 */
public interface FastDfs extends TrackerClient, HttpServerClient {

    /**
     * 关闭客户端
     */
    void shutdown();

    /**
     * 上传一般文件
     *
     * @param file
     * @return
     */
    StorePath uploadFile(File file);

    /**
     * 上传一般文件到分组 file
     *
     * @param groupName
     * @param file
     * @return
     */
    StorePath uploadFile(String groupName, File file);

    /**
     * 上传一般文件 文件流
     *
     * @param stream
     * @param fileSize
     * @param fileExtName
     * @return
     */
    StorePath uploadFile(InputStream stream, long fileSize, String fileExtName);

    /**
     * 上传一般文件到分组 文件流
     *
     * @param groupName
     * @param stream
     * @param fileSize
     * @param fileExtName
     * @return
     */
    StorePath uploadFile(String groupName, InputStream stream, long fileSize, String fileExtName);

    /**
     * 上传一般文件
     *
     * @param request
     * @return
     */
    StorePath uploadFile(UploadFileRequest request);

    /**
     * 上传从文件
     *
     * @param groupName
     * @param masterFilePath
     * @param prefix
     * @param file
     * @return
     */
    StorePath uploadSlaveFile(String groupName, String masterFilePath, String prefix, File file);

    /**
     * 上传从文件
     *
     * @param groupName
     * @param masterFilePath
     * @param prefix
     * @param stream
     * @param fileSize
     * @param fileExtName
     * @return
     */
    StorePath uploadSlaveFile(String groupName, String masterFilePath, String prefix,
                              InputStream stream, long fileSize, String fileExtName);

    /**
     * 上传从文件
     *
     * @param request
     * @return
     */
    StorePath uploadSlaveFile(UploadSalveFileRequest request);

    /**
     * 上传图片并生成对应规格的缩略图
     * <pre>
     * thumbImage是否生成缩略图
     *   1 根据指定尺寸生成缩略图
     *   2 根据指定比例生成缩略图
     * <pre/>
     *
     * @param request 上传文件配置
     * @return
     */
    ImageStorePath uploadImage(UploadImageRequest request);

    /**
     * 生成缩略图片 不上传原图
     * <pre>
     * thumbImage是否生成缩略图
     *   1 根据指定尺寸生成缩略图
     *   2 根据指定比例生成缩略图
     * <pre/>
     *
     * @param request 上传文件配置
     * @return
     */
    StorePath createThumbImage(UploadImageRequest request);

    /**
     * 批量生成缩略图片 不上传原图
     * <pre>
     * thumbImage是否生成缩略图
     *   1 根据指定尺寸生成缩略图
     *   2 根据指定比例生成缩略图
     * <pre/>
     *
     * @param request 上传文件配置
     * @return
     */
    List<StorePath> createThumbImages(UploadImageRequest request);

    /**
     * 获取文件元信息
     *
     * @param groupName
     * @param path
     * @return
     */
    Set<MetaData> getMetadata(String groupName, String path);

    /**
     * 获取文件元信息
     *
     * @param request
     * @return
     */
    Set<MetaData> getMetadata(MetaDataInfoRequest request);

    /**
     * 修改文件元信息（覆盖）
     *
     * @param groupName
     * @param path
     * @param metaData
     */
    void overwriteMetadata(String groupName, String path, Set<MetaData> metaData);

    /**
     * 修改文件元信息（覆盖）
     *
     * @param request
     */
    void overwriteMetadata(MetaDataRequest request);

    /**
     * 修改文件元信息（覆盖）
     *
     * @param groupName
     * @param path
     * @param metaData
     */
    void mergeMetadata(String groupName, String path, Set<MetaData> metaData);

    /**
     * 修改文件元信息（合并）
     *
     * @param request
     */
    void mergeMetadata(MetaDataRequest request);

    /**
     * 删除文件元信息
     *
     * @param groupName
     * @param path
     */
    void deleteMetadata(String groupName, String path);

    /**
     * 查看文件的信息
     *
     * @param groupName
     * @param path
     * @return
     */
    FileInfo queryFileInfo(String groupName, String path);

    /**
     * 查看文件的信息
     *
     * @param request
     * @return
     */
    FileInfo queryFileInfo(FileInfoRequest request);

    /**
     * 删除文件
     *
     * @param groupName
     * @param path
     */
    void deleteFile(String groupName, String path);

    /**
     * 删除文件
     *
     * @param request
     */
    void deleteFile(FileInfoRequest request);

    /**
     * 下载文件
     *
     * @param groupName
     * @param path
     * @param callback
     * @param <T>
     * @return
     */
    <T> T downloadFile(String groupName, String path, DownloadCallback<T> callback);

    /**
     * 下载文件
     *
     * @param groupName
     * @param path
     * @param fileOffset
     * @param downLoadSize
     * @param callback
     * @param <T>
     * @return
     */
    <T> T downloadFile(String groupName, String path, long fileOffset, long downLoadSize, DownloadCallback<T> callback);

    /**
     * 下载文件
     *
     * @param request
     * @param callback
     * @return
     */
    <T> T downloadFile(DownloadFileRequest request, DownloadCallback<T> callback);

    /**
     * 上传支持断点续传的文件
     *
     * @param file
     * @return
     */
    StorePath uploadAppenderFile(File file);

    /**
     * 上传支持断点续传的文件
     *
     * @param groupName
     * @param file
     * @return
     */
    StorePath uploadAppenderFile(String groupName, File file);

    /**
     * 上传支持断点续传的文件
     *
     * @param stream
     * @param fileSize
     * @param fileExtName
     * @return
     */
    StorePath uploadAppenderFile(InputStream stream, long fileSize, String fileExtName);

    /**
     * 上传支持断点续传的文件
     *
     * @param groupName
     * @param stream
     * @param fileSize
     * @param fileExtName
     * @return
     */
    StorePath uploadAppenderFile(String groupName, InputStream stream, long fileSize, String fileExtName);

    /**
     * 上传支持断点续传的文件
     *
     * @param request
     * @return
     */
    StorePath uploadAppenderFile(UploadAppendFileRequest request);

    /**
     * 断点续传文件
     *
     * @param groupName
     * @param path
     * @param file
     */
    void appendFile(String groupName, String path, File file);

    /**
     * 断点续传文件
     *
     * @param groupName
     * @param path
     * @param stream
     * @param fileSize
     */
    void appendFile(String groupName, String path, InputStream stream, long fileSize);

    /**
     * 断点续传文件
     *
     * @param request
     */
    void appendFile(AppendFileRequest request);

    /**
     * 修改续传文件的内容
     *
     * @param groupName
     * @param path
     * @param stream
     * @param fileSize
     * @param offset
     */
    void modifyFile(String groupName, String path, InputStream stream, long fileSize, long offset);

    /**
     * 修改续传文件的内容
     *
     * @param request
     */
    void modifyFile(ModifyFileRequest request);

    /**
     * 清除续传类型文件的内容
     *
     * @param groupName
     * @param path
     */
    void truncateFile(String groupName, String path);

    /**
     * 清除续传类型文件的内容
     *
     * @param groupName
     * @param path
     * @param size
     */
    void truncateFile(String groupName, String path, long size);

    /**
     * 清除续传类型文件的内容
     *
     * @param request
     */
    void truncateFile(TruncateFileRequest request);

    /**
     * appender类型文件改名为普通文件 V6.02及以上版本
     * since V6.02, rename appender file to normal file
     *
     * @param groupName
     * @param path
     * @return
     */
    StorePath regenerateAppenderFile(String groupName, String path);

    /**
     * appender类型文件改名为普通文件 V6.02及以上版本
     * since V6.02, rename appender file to normal file
     *
     * @param request
     * @return
     */
    StorePath regenerateAppenderFile(RegenerateAppenderFileRequest request);

    /**
     * 初始化分片上传
     *
     * @param fileSize    文件大小
     * @param fileExtName 文件后缀
     * @param partSize    分片大小
     * @return StorePath
     */
    StorePath initMultipartUpload(long fileSize, long partSize, String fileExtName);

    /**
     * 初始化分片上传
     *
     * @param request
     * @return StorePath
     */
    StorePath initMultipartUpload(InitMultipartUploadRequest request);

    /**
     * 上传分片
     *
     * @param groupName  组名称
     * @param path       路径
     * @param part       分片文件 (size<=partSize)
     * @param partNumber 分片索引 (start=1)
     */
    void uploadMultipart(String groupName, String path, File part, int partNumber);

    /**
     * 上传分片
     *
     * @param groupName  组名称
     * @param path       路径
     * @param part       分片文件流 (size<=partSize)
     * @param partNumber 分片索引 (start=1)
     */
    void uploadMultipart(String groupName, String path, InputStream part, int partNumber);

    /**
     * 上传分片
     *
     * @param request
     */
    void uploadMultipart(UploadMultipartRequest request);

    /**
     * 完成分片上传
     *
     * @param groupName
     * @param path
     * @param regenerate 是否改为普通文件 true条件 version>=6.02
     * @return regenerate=false原文件路径 true regenerate文件路径
     */
    StorePath completeMultipartUpload(String groupName, String path, boolean regenerate);

    /**
     * 完成分片上传
     * version<6.02 regenerate = false
     *
     * @param request
     * @return regenerate=false原文件路径 true regenerate文件路径
     */
    StorePath completeMultipartUpload(CompleteMultipartRequest request);

}
