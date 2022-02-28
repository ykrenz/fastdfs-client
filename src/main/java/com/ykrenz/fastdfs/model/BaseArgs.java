package com.ykrenz.fastdfs.model;


import com.ykrenz.fastdfs.common.CodeUtils;
import org.apache.commons.io.FilenameUtils;
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
     * Base builder which builds arguments.
     */
    public abstract static class Builder<B extends BaseArgs.Builder<B, A>, A extends BaseArgs> {

        /**
         * 日志
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

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
            validate(args);
            logDebugArgs(args);
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

        private void logDebugArgs(A a) {
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
