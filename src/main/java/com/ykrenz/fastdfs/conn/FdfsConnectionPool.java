package com.ykrenz.fastdfs.conn;

import com.ykrenz.fastdfs.config.ConnectionConfiguration;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import java.net.InetSocketAddress;

/**
 * 定义Fdfs连接池对象
 * <p>
 * <pre>
 * 定义了对象池要实现的功能,对一个地址进行池化Map Pool
 * </pre>
 *
 * @author tobato
 */
public class FdfsConnectionPool extends GenericKeyedObjectPool<InetSocketAddress, Connection> {

    private ConnectionConfiguration connection;

    /**
     * 默认构造函数
     *
     * @param connection
     */
    public FdfsConnectionPool(ConnectionConfiguration connection) {
        super(new PooledConnectionFactory(connection), connection.getPool());
        this.connection = connection;
    }

    public ConnectionConfiguration getConnection() {
        return connection;
    }

    public void setConnection(ConnectionConfiguration connection) {
        this.connection = connection;
    }

}
