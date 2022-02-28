package com.ykrenz.fastdfs.model;

/**
 * 文件元数据信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class MetaDataInfoRequest extends GroupPathArgs {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 一般文件上传参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, MetaDataInfoRequest> {
    }
}
