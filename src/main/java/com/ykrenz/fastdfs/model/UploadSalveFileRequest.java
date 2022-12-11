package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.common.FastDfsUtils;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.proto.OtherConstants;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.ykrenz.fastdfs.common.CodeUtils.validateNotBlankString;

/**
 * 上传从文件参数
 *
 * @author ykren
 * @date 2022/1/21
 */
public class UploadSalveFileRequest extends AbstractFileExtHandlerArgs {

    /**
     * 从文件前缀
     */
    protected String prefix;
    /**
     * 主文件 path
     */
    public String masterPath() {
        return path;
    }

    public String prefix() {
        return prefix;
    }

    public Set<MetaData> metaData() {
        return metaData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends AbstractFileExtHandlerArgs.Builder<Builder, UploadSalveFileRequest> {
        /**
         * 默认前缀名
         */
        private static final String DEFAULT_PREFIX = "_";

        @Override
        protected void validate(UploadSalveFileRequest args) {
            super.validate(args);
            validateNotBlankString(args.groupName, "master file groupName");
            validateNotBlankString(args.path, "master file path");
            validateNotBlankString(args.prefix, "salve prefix");
            if (args.prefix.length() > OtherConstants.FDFS_FILE_PREFIX_MAX_LEN) {
                String msg = String.format("salve prefix length > %d", OtherConstants.FDFS_FILE_PREFIX_MAX_LEN);
                LOGGER.warn(msg);
            }
        }

        public Builder masterPath(String masterFilePath) {
            operations.add(args -> args.path = masterFilePath);
            return this;
        }

        public Builder prefix(String prefix) {
            String handlerPrefix = FastDfsUtils.handlerPrefix(prefix);
            operations.add(args -> args.prefix = handlerPrefix);
            return this;
        }

        public Builder filePath(String filePath) {
            File file = new File(filePath);
            return file(file);
        }

        /**
         * 上传文件
         *
         * @param file
         * @return
         */
        public Builder file(File file) {
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            super.fileExtName(getExtension(file.getName()));
            return this;
        }

        /**
         * 上传文件流
         *
         * @param stream
         * @param fileSize
         * @param fileExtName
         * @return
         */
        public Builder stream(InputStream stream, long fileSize, String fileExtName) {
            operations.add(args -> args.stream = stream);
            operations.add(args -> args.fileSize = fileSize);
            super.fileExtName(fileExtName);
            return this;
        }

        /**
         * 元数据信息
         *
         * @return
         */
        public Builder metaData(String name, String value) {
            operations.add(args -> args.metaData.add(new MetaData(name, value)));
            return this;
        }

        /**
         * 元数据信息
         *
         * @param metaData
         * @return
         */
        public Builder metaData(Set<MetaData> metaData) {
            operations.add(args -> args.metaData.addAll(metaData == null ? Collections.emptySet() : metaData));
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UploadSalveFileRequest that = (UploadSalveFileRequest) o;
        return Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), prefix);
    }
}
