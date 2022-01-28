package com.ykren.fastdfs.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * FastDFS工具类
 *
 * @author ykren
 * @date 2022/1/28
 */
public final class FastDFSUtils {
    private FastDFSUtils() {
    }

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FastDFSUtils.class);
    /**
     * 文件名或者文件前缀校验正则
     */
    private static final String FILENAME_REGEX = "^[A-Za-z0-9_\\-.]+$";
    /**
     * Pattern
     */
    public static final Pattern FDFS_FILENAME_PATTEN = Pattern.compile(FILENAME_REGEX);

    /**
     * 发现fastdfs会对文件名称和文件前缀合法校验 其实文件可以支持其以外的字符 会导致正常文件传输不上去
     * 这里做特殊处理 过滤非法字符替换为空 只有一个字符替换为.
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

}
