package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.config.FastDFSConfiguration;

import java.util.List;

/**
 * FastDFS构建类
 *
 * @author ykren
 * @date 2022/1/24
 */
public final class FastDfsClientBuilder implements FastDfsBuilder {
    /**
     * 构建FastDFS
     *
     * @param trackerServers
     * @return
     */
    @Override
    public FastDfs build(List<String> trackerServers) {
        return build(trackerServers, getFastDFSConfiguration());
    }

    /**
     * 构建FastDFS
     *
     * @param trackerServers
     * @return
     */
    @Override
    public FastDfs build(List<String> trackerServers, FastDFSConfiguration configuration) {
        return new FastDfsClient(trackerServers, getFastDFSConfiguration(configuration));
    }

    /**
     * 获取config
     *
     * @return
     */
    private static FastDFSConfiguration getFastDFSConfiguration() {
        return new FastDFSConfiguration();
    }

    private static FastDFSConfiguration getFastDFSConfiguration(FastDFSConfiguration config) {
        if (config == null) {
            config = new FastDFSConfiguration();
        }
        return config;
    }
}
