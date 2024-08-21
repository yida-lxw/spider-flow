package org.spiderflow.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.enums.FlowNoticeType;
import org.spiderflow.core.enums.FlowNoticeWay;
import org.spiderflow.core.model.FlowNotice;
import org.spiderflow.core.model.SpiderFlow;
import org.spiderflow.core.service.FlowNoticeService;
import org.spiderflow.core.utils.EmailUtils;
import org.spiderflow.core.utils.ExpressionUtils;
import org.spiderflow.mapper.FlowNoticeMapper;
import org.spiderflow.mapper.SpiderFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FlowNoticeServiceImpl extends ServiceImpl<FlowNoticeMapper, FlowNotice> implements FlowNoticeService {
	private static final Logger logger = LoggerFactory.getLogger(FlowNoticeServiceImpl.class);
	@Autowired
	private SpiderFlowMapper spiderFlowMapper;
	@Autowired
	private EmailUtils emailUtils;
	@Value("${spider.notice.subject:spider-flow流程通知}")
	private String subject;
	@Value("${spider.notice.content.start}")
	private String startContext;
	@Value("${spider.notice.content.end}")
	private String endContext;
	@Value("${spider.notice.content.exception}")
	private String exceptionContext;

	@Override
	public boolean saveOrUpdate(FlowNotice entity) {
		if (spiderFlowMapper.getCountById(entity.getId()) == 0) {
			throw new RuntimeException("没有找到对应的流程");
		}
		return super.saveOrUpdate(entity);
	}

	/**
	 * 发送对应的流程通知
	 *
	 * @param spiderFlow 流程信息
	 * @param type       通知类型
	 * @author BillDowney
	 * @date 2020年4月4日 上午1:37:50
	 */
	@Override
	public void sendFlowNotice(SpiderFlow spiderFlow, FlowNoticeType type) {
		FlowNotice notice = baseMapper.selectById(spiderFlow.getId());
		if (notice != null && !StringUtils.isEmpty(notice.getRecipients())
				&& !StringUtils.isEmpty(notice.getNoticeWay())) {
			String content = null;
			String sendSubject = this.subject;
			switch (type) {
				case startNotice:
					if (notice.judgeStartNotice()) {
						content = startContext;
						sendSubject += " - 流程开始执行";
					}
					break;
				case endNotice:
					if (notice.judgeEndNotice()) {
						content = endContext;
						sendSubject += " - 流程执行完毕";
					}
					break;
				case exceptionNotice:
					if (notice.judgeExceptionNotice()) {
						content = exceptionContext;
						sendSubject += " - 流程发生异常";
					}
					break;
			}
			if (StringUtils.isEmpty(content)) {
				return;
			}
			// 定义一个上下文变量
			Map<String, Object> variables = new HashMap<String, Object>();
			// 放入流程信息
			BeanMap beanMap = BeanMap.create(spiderFlow);
			for (Object key : beanMap.keySet()) {
				variables.put(key + "", beanMap.get(key));
			}
			// 放入当前时间
			variables.put("currentDate", this.getCurrentDate());
			content = ExpressionUtils.execute(content.replaceAll("[{]", "\\${"), variables) + "";
			// 整理收件人
			String recipients = notice.getRecipients();
			for (String recipient : recipients.split(",")) {
				String noticeWay = notice.getNoticeWay();
				String people = recipient;
				// 如果含有":"证明单独配置了发送方式
				if (recipient.contains(":")) {
					String[] strs = recipient.split(":");
					noticeWay = strs[0];
					people = strs[1];
				}
				FlowNoticeWay way = FlowNoticeWay.email;
				try {
					way = FlowNoticeWay.valueOf(noticeWay);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				switch (way) {
					case email:
						emailUtils.sendSimpleMail(sendSubject, content, people);
						break;
				}
			}
		}
	}

	private String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}

}
