package com.ykren.fastdfs.model;

import java.util.Objects;

import static com.ykren.fastdfs.common.CodeUtils.validateNotBlankString;

/**
 * path参数抽象类
 *
 * @author ykren
 * @date 2022/1/22
 */
public class GroupPathArgs extends GroupArgs {
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
    public static class Builder<B extends Builder<B, A>, A extends GroupPathArgs>
            extends GroupArgs.Builder<B, A> {

        @Override
        protected void validate(A args) {
            validateNotBlankString(args.path, "path");
        }

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
        GroupPathArgs that = (GroupPathArgs) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), path);
    }
}
