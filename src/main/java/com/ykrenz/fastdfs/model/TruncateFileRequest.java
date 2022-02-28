package com.ykrenz.fastdfs.model;

import java.util.Objects;

/**
 * 截取文件信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class TruncateFileRequest extends GroupPathArgs {
    /**
     * 文件大小
     */
    protected long fileSize;

    public long fileSize() {
        return fileSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, TruncateFileRequest> {
        /**
         * 截取文件大小
         *
         * @param fileSize
         * @return
         */
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
        TruncateFileRequest that = (TruncateFileRequest) o;
        return fileSize == that.fileSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileSize);
    }
}
