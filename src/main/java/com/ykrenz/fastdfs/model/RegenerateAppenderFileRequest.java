package com.ykrenz.fastdfs.model;

import java.util.Objects;

/**
 * appender文件改普通文件信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class RegenerateAppenderFileRequest extends GroupPathArgs {
    /**
     * crc32校验码
     */
    protected long crc32;

    public long crc32() {
        return crc32;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, RegenerateAppenderFileRequest> {
        /**
         * cr32校验
         *
         * @param crc32
         * @return
         */
        public Builder crc32(long crc32) {
            operations.add(args -> args.crc32 = crc32);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RegenerateAppenderFileRequest that = (RegenerateAppenderFileRequest) o;
        return crc32 == that.crc32;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), crc32);
    }
}
