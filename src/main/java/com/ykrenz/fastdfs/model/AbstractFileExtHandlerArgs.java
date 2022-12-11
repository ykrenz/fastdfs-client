package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.common.FastDfsUtils;
import com.ykrenz.fastdfs.model.proto.OtherConstants;

/**
 * 文件后缀名处理抽象参数
 *
 * @author ykren
 * @date 2022/12/09
 */
public abstract class AbstractFileExtHandlerArgs extends AbstractFileArgs {

    /**
     * 文件扩展名
     */
    protected String fileExtName;

    public String fileExtName() {
        return fileExtName;
    }

    /**
     * 参数构建类
     */
    public abstract static class Builder<B extends AbstractFileExtHandlerArgs.Builder<B, A>, A extends AbstractFileExtHandlerArgs>
            extends AbstractFileArgs.Builder<B, A> {

        @Override
        protected void validate(A args) {
            super.validate(args);
            if (args.fileExtName != null && args.fileExtName.length() > OtherConstants.FDFS_FILE_EXT_NAME_MAX_LEN) {
                String msg = String.format("fileExtName length > %d", OtherConstants.FDFS_FILE_EXT_NAME_MAX_LEN);
                LOGGER.warn(msg);
            }
        }

        /**
         * 文件后缀名
         *
         * @param fileExtName
         * @return
         */
        protected void fileExtName(String fileExtName) {
            String handlerFileExtName = FastDfsUtils.handlerFilename(fileExtName);
            operations.add(args -> args.fileExtName = handlerFileExtName);
        }

    }
}
