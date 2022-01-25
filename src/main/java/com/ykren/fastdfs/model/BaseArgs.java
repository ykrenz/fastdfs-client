package com.ykren.fastdfs.model;


import com.ykren.fastdfs.model.proto.OtherConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 参数基础类 Base argument class
 *
 * @author ykren
 * @date 2022/1/21
 */
public class BaseArgs {
    protected BaseArgs() {
    }

    /**
     * 日志
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseArgs.class);

    /**
     * Base builder which builds arguments.
     */
    public abstract static class Builder<B extends BaseArgs.Builder<B, A>, A extends BaseArgs> {

        protected List<Consumer<A>> operations;

        protected Builder() {
            this.operations = new ArrayList<>();
        }

        /**
         * 校验参数
         *
         * @param args
         */
        protected abstract void validate(A args);

        /**
         * 发现fastdfs会对文件名称和文件前缀合法校验 其实文件可以支持其以外的字符 会导致正常文件传输不上去
         * 这里做特殊处理 过滤非法字符替换为空 只有一个字符替换为.
         * 校验文件名合法性 参阅fastdfs源码 tracker_proto.c fdfs_validate_filename(const char *filename)
         *
         * @param filename
         * @return
         */
        protected String handlerFilename(String filename) {
            //包含不符合FDFS的字符
            if (StringUtils.isNotBlank(filename) && !OtherConstants.FDFS_FILENAME_PATTEN.matcher(filename).matches()) {
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
                String msg = String.format("文件名包含FDFS不允许字符 处理完毕 name=%s realName=%s", filename, result);
                LOGGER.warn(msg);
                return result;
            }
            return filename;
        }

        /**
         * 获取文件后缀
         *
         * @param fileName
         * @return
         */
        protected String getExtension(String fileName) {
            return FilenameUtils.getExtension(fileName);
        }

        public A build() throws IllegalArgumentException {
            A args = newInstance();
            operations.forEach(operation -> operation.accept(args));
            logDebugArgs(args);
            validate(args);
            return args;
        }

        private A newInstance() {
            try {
                for (Constructor<?> constructor :
                        this.getClass().getEnclosingClass().getDeclaredConstructors()) {
                    if (constructor.getParameterCount() == 0) {
                        return (A) constructor.newInstance();
                    }
                }
                throw new RuntimeException(
                        this.getClass().getEnclosingClass() + " must have no argument constructor");
            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | SecurityException e) {
                // Args class must have no argument constructor with at least protected access.
                throw new RuntimeException(e);
            }
        }

        protected void logDebugArgs(A a) {
            if (LOGGER.isDebugEnabled()) {
                List<Field> fields = CodeUtils.getAllFieldList(a.getClass());
                if (!fields.isEmpty()) {
                    LOGGER.debug("args===============");
                    fields.forEach(c -> {
                        try {
                            String name = c.getName();
                            Object value = c.get(a);
                            LOGGER.debug(String.format("%s : %s", name, value));
                        } catch (Exception e) {
                            LOGGER.error("debug format args error", e);
                        }
                    });
                    LOGGER.debug("args===============");
                } else {
                    LOGGER.debug("no args");
                }
            }
        }
    }


}
