package org.spiderflow.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.spiderflow.core.enums.FlowNoticeType;
import org.spiderflow.core.model.FlowNotice;
import org.spiderflow.core.model.SpiderFlow;

/**
 * @author yida
 * @package org.spiderflow.core.service
 * @date 2024-08-21 16:57
 * @description Type your description over here.
 */
public interface FlowNoticeService extends IService<FlowNotice> {
	boolean saveOrUpdate(FlowNotice entity);

	void sendFlowNotice(SpiderFlow spiderFlow, FlowNoticeType type);
}
