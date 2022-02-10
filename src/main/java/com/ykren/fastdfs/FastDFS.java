package com.ykren.fastdfs;

import com.ykren.fastdfs.model.AppendFileRequest;
import com.ykren.fastdfs.model.CompleteMultipartRequest;
import com.ykren.fastdfs.model.DownloadFileRequest;
import com.ykren.fastdfs.model.FileInfoRequest;
import com.ykren.fastdfs.model.InitMultipartUploadRequest;
import com.ykren.fastdfs.model.MetaDataInfoRequest;
import com.ykren.fastdfs.model.MetaDataRequest;
import com.ykren.fastdfs.model.ModifyFileRequest;
import com.ykren.fastdfs.model.RegenerateAppenderFileRequest;
import com.ykren.fastdfs.model.TruncateFileRequest;
import com.ykren.fastdfs.model.UploadFileRequest;
import com.ykren.fastdfs.model.UploadImageRequest;
import com.ykren.fastdfs.model.UploadMultipartPartRequest;
import com.ykren.fastdfs.model.UploadSalveFileRequest;
import com.ykren.fastdfs.model.fdfs.FileInfo;
import com.ykren.fastdfs.model.fdfs.ImageStorePath;
import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.proto.storage.DownloadCallback;

import java.util.Set;

/**
 * FastDFS文件存储客户端
 *
 * @author ykren
 */
public interface FastDFS {

    /**
     * 获取trackerClient
     *
     * @return
     */
    TrackerClient trackerClient();

    /**
     * 关闭客户端
     */
    void close();

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
     * @param request
     * @return
     */
    StorePath uploadSlaveFile(UploadSalveFileRequest request);

    /**
     * 上传图片
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
     * 获取文件元信息
     *
     * @param request
     * @return
     */
    Set<MetaData> getMetadata(MetaDataInfoRequest request);

    /**
     * 修改文件元信息（覆盖）
     *
     * @param request
     */
    void overwriteMetadata(MetaDataRequest request);

    /**
     * 修改文件元信息（合并）
     *
     * @param request
     */
    void mergeMetadata(MetaDataRequest request);

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
     * @param request
     */
    void deleteFile(FileInfoRequest request);

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
     * @param request
     * @return
     */
    StorePath uploadAppenderFile(UploadFileRequest request);

    /**
     * 断点续传文件
     *
     * @param request
     */
    void appendFile(AppendFileRequest request);

    /**
     * 修改续传文件的内容
     *
     * @param request
     */
    void modifyFile(ModifyFileRequest request);

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
     * @param request
     * @return
     */
    StorePath regenerateAppenderFile(RegenerateAppenderFileRequest request);

    /**
     * 初始化分片上传
     *
     * @param request group
     * @return StorePath
     */
    StorePath initMultipartUpload(InitMultipartUploadRequest request);

    /**
     * 上传分片
     *
     * @param request
     */
    void uploadMultipart(UploadMultipartPartRequest request);

    /**
     * 完成分片上传
     * version<6.02 regenerate = false
     *
     * @param request
     * @return 最终文件路径
     */
    StorePath completeMultipartUpload(CompleteMultipartRequest request);

}
