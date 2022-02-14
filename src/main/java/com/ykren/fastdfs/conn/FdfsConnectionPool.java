package com.ykren.fastdfs.conn;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

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

    private ConnectionConfig connection;

    private GenericKeyedObjectPoolConfig pool;

    /**
     * 默认构造函数
     *
     * @param connection
     * @param pool
     */
    public FdfsConnectionPool(ConnectionConfig connection, GenericKeyedObjectPoolConfig pool) {
        super(new PooledConnectionFactory(connection), pool);
        this.connection = connection;
        this.pool = pool;
    }

    public ConnectionConfig getConnection() {
        return connection;
    }

    public void setConnection(ConnectionConfig connection) {
        this.connection = connection;
    }

    public GenericKeyedObjectPoolConfig getPool() {
        return pool;
    }

    public void setPool(GenericKeyedObjectPoolConfig pool) {
        this.pool = pool;
    }
}
