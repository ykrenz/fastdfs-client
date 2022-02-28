package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.common.CodeUtils;

import java.util.Objects;

/**
 * 下载文件信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class DownloadFileRequest extends GroupPathArgs {
    /**
     * 下载部分文件起始值
     */
    protected long fileOffset;
    /**
     * 下载文件大小
     */
    protected long fileSize;

    public long offset() {
        return fileOffset < 0 ? 0 : fileOffset;
    }

    public long fileSize() {
        return fileSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, DownloadFileRequest> {

        @Override
        protected void validate(DownloadFileRequest args) {
            super.validate(args);
            CodeUtils.validateNotLessZero(args.fileOffset, "fileOffset");
            CodeUtils.validateNotLessZero(args.fileSize, "fileSize");
        }

        public Builder offset(long fileOffset) {
            operations.add(args -> args.fileOffset = fileOffset);
            return this;
        }

        public Builder fileSize(long fileSize) {
            operations.add(args -> args.fileSize = fileSize);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DownloadFileRequest that = (DownloadFileRequest) o;
        return fileOffset == that.fileOffset &&
                fileSize == that.fileSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileOffset, fileSize);
    }
}
