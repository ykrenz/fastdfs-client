package com.ykren.fastdfs.model;

import java.util.Objects;

/**
 * path参数抽象类
 *
 * @author ykren
 * @date 2022/1/22
 */
public abstract class AbstractGroupPathArgs extends GroupArgs {

    /**
     * 文件路径 path
     */
    protected String path;

    public String path() {
        return path;
    }

    /**
     * group path builder
     *
     * @param <B>
     * @param <A>
     */
    public abstract static class AbstractGroupPathBuilder<B extends AbstractGroupPathArgs.AbstractGroupPathBuilder<B, A>, A extends AbstractGroupPathArgs>
            extends GroupArgs.Builder<B, A> {
        @SuppressWarnings("unchecked")
        public B path(String path) {
            operations.add(args -> args.path = path);
            return (B) this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractGroupPathArgs that = (AbstractGroupPathArgs) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), path);
    }
}
