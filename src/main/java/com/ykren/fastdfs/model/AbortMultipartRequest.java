package com.ykren.fastdfs.model;

/**
 * 终止分片上传参数
 *
 * @author ykren
 * @date 2022/1/25
 */
public class AbortMultipartRequest extends GroupPathArgs {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, AbortMultipartRequest> {
    }
}
