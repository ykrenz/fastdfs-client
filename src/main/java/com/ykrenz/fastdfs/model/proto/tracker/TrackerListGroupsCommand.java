package com.ykrenz.fastdfs.model.proto.tracker;

import com.ykrenz.fastdfs.model.fdfs.GroupState;
import com.ykrenz.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykrenz.fastdfs.model.proto.tracker.internal.TrackerListGroupsRequest;
import com.ykrenz.fastdfs.model.proto.tracker.internal.TrackerListGroupsResponse;

import java.util.List;

/**
 * 列出组命令
 *
 * @author tobato
 */
public class TrackerListGroupsCommand extends AbstractFdfsCommand<List<GroupState>> {

    public TrackerListGroupsCommand() {
        super.request = new TrackerListGroupsRequest();
        super.response = new TrackerListGroupsResponse();
    }

}
