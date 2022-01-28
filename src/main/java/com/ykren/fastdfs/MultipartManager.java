//package com.ykren.fastdfs;
//
//import com.ykren.fastdfs.model.fdfs.StorePath;
//
//import java.util.List;
//
///**
// * @author ykren
// * @date 2022/1/27
// */
//public abstract class MultipartManager {
//
//    /**
//     * 保存分片任务
//     */
//    abstract void saveUpload(StorePath path);
//
//    /**
//     * 获取已经上传的分片
//     *
//     * @param path
//     * @return
//     */
//    abstract List<Object> listParts(StorePath path);
//
//    /**
//     * 获取上传任务
//     *
//     * @return
//     */
//    abstract List<StorePath> listMultipartUploads(String group, int page, int pageSize);
//
//    //TODO 加入分片过期清理
//
//    void clear() {
//
//    }
//}
