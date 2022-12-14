package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.config.FastDfsConfiguration;

import java.util.List;

/**
 * FastDFS构建类
 *
 * @author ykren
 * @date 2022/1/25
 */
public interface FastDfsBuilder {

    /**
     * 根据trackerServer构建 使用默认的连接池
     *
     * @param trackerServers
     * @return
     */
    FastDfs build(String... trackerServers);

    /**
     * 根据trackerServers构建 使用默认的连接池
     *
     * @param trackerServers
     * @return
     */
    FastDfs build(List<String> trackerServers);

    /**
     * 根据配置构建
     *
     * @param trackerServers
     * @param configuration
     * @return
     */
    FastDfs build(List<String> trackerServers, FastDfsConfiguration configuration);
}
