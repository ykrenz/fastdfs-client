package com.ykren.fastdfs.conn;

import com.ykren.fastdfs.config.ConnectionConfiguration;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * pooled FdfsSocket factory
 * <p>
 * <pre>
 * 定义了被池化的对象的创建，初始化，激活，钝化以及销毁功能
 * </per>
 *
 * @author tobato
 */
public class PooledConnectionFactory extends BaseKeyedPooledObjectFactory<InetSocketAddress, Connection> {
    /**
     * 读取时间
     */
    private int soTimeout;
    /**
     * 连接超时时间
     */
    private int connectTimeout;
    /**
     * 字符集
     */
    private Charset charset;

    public PooledConnectionFactory(ConnectionConfiguration connection) {
        this.soTimeout = connection.getSocketTimeout();
        this.connectTimeout = connection.getConnectTimeout();
        this.charset = connection.getCharset() == null ? StandardCharsets.UTF_8 : Charset.forName(connection.getCharset());
    }

    /**
     * 创建连接
     */
    @Override
    public Connection create(InetSocketAddress address) {
        return new DefaultConnection(address, soTimeout, connectTimeout, charset);
    }

    /**
     * 将对象池化pooledObject
     */
    @Override
    public PooledObject<Connection> wrap(Connection conn) {
        return new DefaultPooledObject<Connection>(conn);
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * 从池中移出
     *
     * @param key
     * @param p
     */
    @Override
    public void destroyObject(InetSocketAddress key, PooledObject<Connection> p) {
        p.getObject().close();
    }

    /***
     * 验证池中对象是否可用
     * @param key
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(InetSocketAddress key, PooledObject<Connection> p) {
        return p.getObject().isValid();
    }

}
