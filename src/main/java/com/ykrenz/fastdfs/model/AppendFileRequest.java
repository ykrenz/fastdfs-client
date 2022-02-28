package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.proto.storage.enums.StorageMetadataSetType;
import com.ykrenz.fastdfs.common.CodeUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * 追加文件请求参数
 *
 * @author ykren
 * @date 2022/1/21
 */
public class AppendFileRequest extends AbstractFileArgs {
    /**
     * 文件元数据类型
     */
    protected StorageMetadataSetType metaType;

    public String path() {
        return path;
    }

    public Set<MetaData> metaData() {
        return metaData;
    }

    public StorageMetadataSetType metaType() {
        return metaType;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends AbstractFileArgs.Builder<Builder, AppendFileRequest> {

        @Override
        protected void validate(AppendFileRequest args) {
            super.validate(args);
            CodeUtils.validateNotBlankString(args.path, "path");
            if (args.metaData != null && !args.metaData.isEmpty()) {
                CodeUtils.validateNotNull(args.metaType, "metadata type");
            }
        }

        /**
         * 追加文件path
         *
         * @param path
         * @return
         */
        public Builder path(String path) {
            operations.add(args -> args.path = path);
            return this;
        }

        /**
         * 上传文件
         *
         * @param filePath
         * @return
         */
        public Builder file(String filePath) {
            return file(new File(filePath));
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
            return this;
        }

        /**
         * 追加文件流
         *
         * @param stream
         * @param fileSize
         * @return
         */
        public Builder stream(InputStream stream, long fileSize) {
            operations.add(args -> args.stream = stream);
            operations.add(args -> args.fileSize = fileSize);
            return this;
        }


        /**
         * 元数据信息
         *
         * @return
         */
        public Builder metaData(String name, String value, StorageMetadataSetType type) {
            operations.add(args -> args.metaData.add(new MetaData(name, value)));
            operations.add(args -> args.metaType = type);
            return this;
        }

        /**
         * 元数据信息
         *
         * @param metaData
         * @param type
         * @return
         */
        public Builder metaData(Set<MetaData> metaData, StorageMetadataSetType type) {
            operations.add(args -> args.metaData.addAll(metaData == null ? Collections.emptySet() : metaData));
            operations.add(args -> args.metaType = type);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AppendFileRequest that = (AppendFileRequest) o;
        return metaType == that.metaType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), metaType);
    }
}
