package com.ykren.fastdfs.model;

import java.util.Objects;

import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;

/**
 * 截取文件信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class TruncateFileRequest extends AbstractGroupPathArgs {

    protected TruncateFileRequest() {
    }

    /**
     * 文件大小
     */
    protected long fileSize;

    public static Builder builder() {
        return new TruncateFileRequest.Builder();
    }

    /**
     * 参数构建类
     */
    public static class Builder extends AbstractGroupPathBuilder<TruncateFileRequest.Builder, TruncateFileRequest> {
        @Override
        protected void validate(TruncateFileRequest args) {
            validateNotBlankString(args.path, "path");
        }

        public Builder fileSize(long fileSize) {
            operations.add(args -> args.fileSize = fileSize);
            return this;
        }
    }

    public long fileSize() {
        return fileSize;
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
