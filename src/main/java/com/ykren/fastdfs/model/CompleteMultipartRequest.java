package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.fdfs.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.ykren.fastdfs.model.proto.OtherConstants.FDFS_MAX_META_NAME_LEN;
import static com.ykren.fastdfs.model.proto.OtherConstants.FDFS_MAX_META_VALUE_LEN;

/**
 * 完成分片上传参数
 *
 * @author ykren
 * @date 2022/1/25
 */
public class CompleteMultipartRequest extends GroupPathArgs {
    /**
     * 是否改为普通文件 默认为true V6.02版本以下请设置为false
     */
    protected boolean regenerate = true;
    /**
     * 文件元数据
     */
    protected Set<MetaData> metaData;

    public boolean regenerate() {
        return regenerate;
    }

    public Set<MetaData> metaData() {
        return metaData;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 一般文件上传参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, CompleteMultipartRequest> {
        /**
         * 日志
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

        @Override
        protected void validate(CompleteMultipartRequest args) {
            super.validate(args);
            if (args.metaData != null && !args.metaData.isEmpty()) {
                for (MetaData metadata : args.metaData) {
                    String name = metadata.getName();
                    String value = metadata.getValue();
                    if (name != null && name.length() > FDFS_MAX_META_NAME_LEN) {
                        String msg = String.format("metadata name length > %d ", FDFS_MAX_META_NAME_LEN);
                        LOGGER.warn(msg);
                    }
                    if (value != null && value.length() > FDFS_MAX_META_VALUE_LEN) {
                        String msg = String.format("metadata value length > %d ", FDFS_MAX_META_VALUE_LEN);
                        LOGGER.warn(msg);
                    }
                }
            }
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

        /**
         * 是否改为普通文件 默认为true V6.02版本以下请设置为false
         *
         * @param regenerate
         * @return
         */
        public Builder regenerate(boolean regenerate) {
            operations.add(args -> args.regenerate = regenerate);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CompleteMultipartRequest that = (CompleteMultipartRequest) o;
        return regenerate == that.regenerate &&
                Objects.equals(metaData, that.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), regenerate, metaData);
    }
}
