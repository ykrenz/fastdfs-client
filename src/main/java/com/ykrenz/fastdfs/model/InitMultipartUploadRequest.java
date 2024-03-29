package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.common.FastDfsUtils;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.proto.OtherConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.ykrenz.fastdfs.common.CodeUtils.validateGreaterZero;
import static com.ykrenz.fastdfs.common.CodeUtils.validateNotLessZero;

/**
 * 初始化分片上传
 *
 * @author ykren
 * @date 2022/1/25
 */
public class InitMultipartUploadRequest extends GroupArgs {
    /**
     * 文件大小
     */
    protected long fileSize;
    /**
     * 分片大小
     */
    protected long partSize;
    /**
     * 文件后缀
     */
    protected String fileExtName;

    /**
     * 文件元数据
     */
    protected Set<MetaData> metaData = new HashSet<>();

    public Set<MetaData> metaData() {
        return metaData;
    }

    public String fileExtName() {
        return fileExtName;
    }

    public long fileSize() {
        return fileSize;
    }

    public long partSize() {
        return partSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends GroupArgs.Builder<InitMultipartUploadRequest.Builder, InitMultipartUploadRequest> {
        private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

        @Override
        protected void validate(InitMultipartUploadRequest args) {
            validateNotLessZero(args.fileSize, "fileSize");
            validateGreaterZero(args.partSize, "partSize");

            if (args.metaData != null && !args.metaData.isEmpty()) {
                for (MetaData metadata : args.metaData) {
                    String name = metadata.getName();
                    String value = metadata.getValue();
                    if (name != null && name.length() > OtherConstants.FDFS_MAX_META_NAME_LEN) {
                        String msg = String.format("metadata name length > %d ", OtherConstants.FDFS_MAX_META_NAME_LEN);
                        LOGGER.warn(msg);
                    }
                    if (value != null && value.length() > OtherConstants.FDFS_MAX_META_VALUE_LEN) {
                        String msg = String.format("metadata value length > %d ", OtherConstants.FDFS_MAX_META_VALUE_LEN);
                        LOGGER.warn(msg);
                    }
                }
            }
        }

        /**
         * 文件大小
         *
         * @param fileSize 文件大小
         * @return
         */
        public Builder fileSize(long fileSize) {
            operations.add(args -> args.fileSize = fileSize);
            return this;
        }

        /**
         * 分片大小
         *
         * @param partSize 分片大小
         * @return
         */
        public Builder partSize(long partSize) {
            operations.add(args -> args.partSize = partSize);
            return this;
        }

        /**
         * 文件后缀名
         *
         * @param fileExtName
         * @return
         */
        public Builder fileExtName(String fileExtName) {
            String handlerFileExtName = FastDfsUtils.handlerFilename(fileExtName);
            operations.add(args -> args.fileExtName = handlerFileExtName);
            return this;
        }

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
        InitMultipartUploadRequest that = (InitMultipartUploadRequest) o;
        return fileSize == that.fileSize && partSize == that.partSize && Objects.equals(fileExtName, that.fileExtName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileSize, partSize, fileExtName);
    }
}
