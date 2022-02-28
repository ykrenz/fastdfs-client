package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.model.AppendFileRequest;
import com.ykrenz.fastdfs.model.CompleteMultipartRequest;
import com.ykrenz.fastdfs.model.UploadImageRequest;
import com.ykrenz.fastdfs.model.DownloadFileRequest;
import com.ykrenz.fastdfs.model.FileInfoRequest;
import com.ykrenz.fastdfs.model.InitMultipartUploadRequest;
import com.ykrenz.fastdfs.model.MetaDataInfoRequest;
import com.ykrenz.fastdfs.model.MetaDataRequest;
import com.ykrenz.fastdfs.model.ModifyFileRequest;
import com.ykrenz.fastdfs.model.RegenerateAppenderFileRequest;
import com.ykrenz.fastdfs.model.TruncateFileRequest;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.UploadMultipartPartRequest;
import com.ykrenz.fastdfs.model.UploadSalveFileRequest;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.ImageStorePath;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.proto.storage.DownloadCallback;

import java.util.List;
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
    void shutdown();

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
