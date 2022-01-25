package com.ykren.fastdfs.model;

/**
 * 一般文件上传参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class UploadFileRequest extends AbstractFileArgs {
    protected UploadFileRequest() {
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static class Builder extends AbstractUploadBuilder<Builder, UploadFileRequest> {
    }
}
