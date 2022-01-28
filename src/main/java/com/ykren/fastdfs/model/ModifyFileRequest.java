package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.proto.storage.enums.StorageMetadataSetType;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;

import static com.ykren.fastdfs.common.CodeUtils.validateGreaterZero;
import static com.ykren.fastdfs.common.CodeUtils.validateNotBlankString;
import static com.ykren.fastdfs.common.CodeUtils.validateNotNull;

/**
 * 修改文件请求参数
 *
 * @author ykren
 * @date 2022/1/21
 */
public class ModifyFileRequest extends AbstractFileArgs {
    /**
     * 文件修改起始值
     */
    protected long fileOffset;
    /**
     * 文件元数据类型
     */
    protected StorageMetadataSetType metaType;

    public String path() {
        return path;
    }

    public long offset() {
        return fileOffset < 0 ? 0 : fileOffset;
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
    public static final class Builder extends AbstractFileArgs.Builder<Builder, ModifyFileRequest> {

        @Override
        protected void validate(ModifyFileRequest args) {
            super.validate(args);
            validateNotBlankString(args.path, "path");
            validateGreaterZero(args.fileOffset, "fileOffset");
            if (args.metaData != null && !args.metaData.isEmpty()) {
                validateNotNull(args.metaType, "metadata type");
            }
        }

        /**
         * 修改文件path
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
         * @param fileOffset
         * @return
         */
        public Builder file(String filePath, long fileOffset) {
            return file(new File(filePath), fileOffset);
        }

        /**
         * 上传文件
         *
         * @param file
         * @param fileOffset
         * @return
         */
        public Builder file(File file, long fileOffset) {
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            operations.add(args -> args.fileOffset = fileOffset);
            return this;
        }


        /**
         * 追加文件流
         *
         * @param stream
         * @param fileSize
         * @return
         */
        public Builder stream(InputStream stream, long fileSize, long fileOffset) {
            operations.add(args -> args.stream = stream);
            operations.add(args -> args.fileSize = fileSize);
            operations.add(args -> args.fileOffset = fileOffset);
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
            operations.add(args -> args.metaData.addAll(metaData));
            operations.add(args -> args.metaType = type);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModifyFileRequest that = (ModifyFileRequest) o;
        return fileOffset == that.fileOffset &&
                metaType == that.metaType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileOffset, metaType);
    }
}
