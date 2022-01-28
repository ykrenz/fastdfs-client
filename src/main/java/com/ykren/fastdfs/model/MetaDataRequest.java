package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.fdfs.MetaData;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 文件元数据参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class MetaDataRequest extends GroupPathArgs {

    /**
     * 文件元数据
     */
    protected Set<MetaData> metaData = new HashSet<>();

    public Set<MetaData> metaData() {
        return metaData;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, MetaDataRequest> {
        /**
         * 元数据信息
         *
         * @param name
         * @param value
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
            operations.add(args -> args.metaData.addAll(metaData));
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MetaDataRequest that = (MetaDataRequest) o;
        return Objects.equals(metaData, that.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), metaData);
    }
}
