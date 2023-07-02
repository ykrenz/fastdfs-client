package com.ykrenz.fastdfs.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * FastDFS工具类
 *
 * @author ykren
 * @date 2022/1/28
 */
public final class FastDfsUtils {
    private FastDfsUtils() {
    }

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FastDfsUtils.class);
    /**
     * 文件名或者文件前缀校验正则
     */
    private static final String FILENAME_REGEX = "^[A-Za-z0-9_\\-.]+$";
    /**
     * Pattern
     */
    public static final Pattern FDFS_FILENAME_PATTEN = Pattern.compile(FILENAME_REGEX);

    /**
     * fastdfs会对文件后缀名或slave文件前缀名合法校验 导致正常文件上传失败
     * 这里做特殊处理 过滤非法字符替换为空
     * 校验文件名合法性 参阅fastdfs源码 tracker_proto.c fdfs_validate_filename(const char *filename)
     *
     * @param filename
     * @return
     */
    public static String handlerFilename(String filename) {
        if (StringUtils.isBlank(filename)) {
            return filename;
        }
        if (!FDFS_FILENAME_PATTEN.matcher(filename).matches()) {
            StringBuilder charBuilder = new StringBuilder();
            char[] chars = filename.toCharArray();
            for (char c : chars) {
                boolean letter = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
                boolean number = (c >= '0' && c <= '9');
                boolean other = (c == '-') || (c == '_') || (c == '.');
                if ((letter || number || other)) {
                    charBuilder.append(c);
                }
            }
            String result = charBuilder.toString();
            String msg = String.format("文件名包含FastDfs不允许字符 处理完毕 name=%s realName=%s", filename, result);
            LOGGER.warn(msg);
            return result;
        }
        return filename;
    }

    private static final String DEFAULT_PREFIX = "_";

    /**
     * 处理前缀名
     *
     * @param prefix
     * @return
     */
    public static String handlerPrefix(String prefix) {
        String handlerPrefix = handlerFilename(prefix);
        return StringUtils.isBlank(handlerPrefix) ? DEFAULT_PREFIX : handlerPrefix;
    }

    /**
     * 防盗链获取token
     *
     * @param path      文件path 不加group
     * @param ts        时间戳
     * @param secretKey 密钥
     * @param charset   字符集
     * @return
     */
    public static String getToken(String path, int ts, String secretKey, Charset charset) {
        byte[] bsFilename = charset == null ? path.getBytes() : path.getBytes(charset);
        byte[] bsKey = charset == null ? secretKey.getBytes() : secretKey.getBytes(charset);
        byte[] bsTimestamp = charset == null ? Integer.toString(ts).getBytes() : Integer.toString(ts).getBytes(charset);

        byte[] buff = new byte[bsFilename.length + bsKey.length + bsTimestamp.length];
        System.arraycopy(bsFilename, 0, buff, 0, bsFilename.length);
        System.arraycopy(bsKey, 0, buff, bsFilename.length, bsKey.length);
        System.arraycopy(bsTimestamp, 0, buff, bsFilename.length + bsKey.length, bsTimestamp.length);

        return DigestUtils.md5DigestAsHex(buff);
    }
}
