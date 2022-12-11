package com.ykrenz.fastdfs.model;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import static com.ykrenz.fastdfs.common.CodeUtils.validateNotBlankString;

/**
 * @author ykren
 * @date 2022/12/09
 */
public class UploadMultipartRequest extends AbstractFileArgs {

    /**
     * 分片索引 start=1
     */
    protected int partNumber;

    /**
     * 初始化文件path
     */
    public String path() {
        return path;
    }

    public int partNumber() {
        return partNumber;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends AbstractFileArgs.Builder<Builder, UploadMultipartRequest> {

        @Override
        protected void validate(UploadMultipartRequest args) {
            super.validate(args);
            validateNotBlankString(args.groupName, "groupName");
            validateNotBlankString(args.path, "path");
        }

        /**
         * 初始化文件path
         *
         * @param path
         * @return
         */
        public Builder path(String path) {
            operations.add(args -> args.path = path);
            return this;
        }

        public Builder file(String partFilePath, int partNumber) {
            return file(new File(partFilePath), partNumber);
        }

        public Builder file(File part, int partNumber) {
            operations.add(args -> args.file = part);
            operations.add(args -> args.partNumber = partNumber);
            return this;
        }

        public Builder stream(InputStream partStream, int partNumber) {
            operations.add(args -> args.stream = partStream);
            operations.add(args -> args.partNumber = partNumber);
            return this;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UploadMultipartRequest that = (UploadMultipartRequest) o;
        return partNumber == that.partNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), partNumber);
    }
}
