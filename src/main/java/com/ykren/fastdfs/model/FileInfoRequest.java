package com.ykren.fastdfs.model;

import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;

/**
 * 文件信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class FileInfoRequest extends AbstractGroupPathArgs {

    protected FileInfoRequest() {
    }

    public static Builder builder() {
        return new FileInfoRequest.Builder();
    }

    /**
     * 参数构建类
     */
    public static class Builder extends AbstractGroupPathBuilder<FileInfoRequest.Builder, FileInfoRequest> {
        @Override
        protected void validate(FileInfoRequest args) {
            validateNotBlankString(args.path, "path");
        }
    }
}
