package com.ykrenz.fastdfs.model;

import java.util.Objects;

/**
 * 完成分片上传参数
 *
 * @author ykren
 * @date 2022/1/25
 */
public class CompleteMultipartRequest extends GroupPathArgs {
    /**
     * 是否改为普通文件 默认为true V6.02版本以上可设置为true
     */
    protected boolean regenerate;

    public boolean regenerate() {
        return regenerate;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 一般文件上传参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, CompleteMultipartRequest> {

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
        return regenerate == that.regenerate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), regenerate);
    }
}
