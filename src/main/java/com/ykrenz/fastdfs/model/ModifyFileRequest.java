package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.proto.storage.enums.StorageMetadataSetType;
import com.ykrenz.fastdfs.common.CodeUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.ykrenz.fastdfs.common.CodeUtils.validateNotBlankString;
import static com.ykrenz.fastdfs.common.CodeUtils.validateNotLessZero;

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
    protected long offset;
    /**
     * 文件元数据类型
     */
    protected StorageMetadataSetType metaType;

    public String path() {
        return path;
    }

    public long offset() {
        return offset;
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
            validateNotBlankString(args.groupName, "groupName");
            validateNotBlankString(args.path, "path");
            validateNotLessZero(args.offset, "offset");
            if (args.metaData != null && !args.metaData.isEmpty()) {
                CodeUtils.validateNotNull(args.metaType, "metadata type");
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
         * 修改文件
         *
         * @param file
         * @param offset
         * @return
         */
        public Builder file(File file, long offset) {
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            operations.add(args -> args.offset = offset);
            return this;
        }


        /**
         * 修改文件流
         *
         * @param stream
         * @param fileSize
         * @return
         */
        public Builder stream(InputStream stream, long fileSize, long offset) {
            operations.add(args -> args.stream = stream);
            operations.add(args -> args.fileSize = fileSize);
            operations.add(args -> args.offset = offset);
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
        ModifyFileRequest that = (ModifyFileRequest) o;
        return offset == that.offset &&
                metaType == that.metaType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), offset, metaType);
    }
}
