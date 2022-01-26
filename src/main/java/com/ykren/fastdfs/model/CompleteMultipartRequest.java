package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.fdfs.MetaData;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;
import static com.ykren.fastdfs.model.CodeUtils.validateNotNull;

/**
 * 完成分片上传参数
 *
 * @author ykren
 * @date 2022/1/25
 */
public class CompleteMultipartRequest extends AbstractGroupPathArgs {
    protected CompleteMultipartRequest() {
    }

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
    public static class Builder extends AbstractGroupPathBuilder<CompleteMultipartRequest.Builder, CompleteMultipartRequest> {
        @Override
        protected void validate(CompleteMultipartRequest args) {
            validateNotBlankString(args.path, "path");
        }

        public Builder metaData(String name, String value) {
            validateNotNull(name, "metaData name");
            validateNotNull(value, "metaData value");
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
