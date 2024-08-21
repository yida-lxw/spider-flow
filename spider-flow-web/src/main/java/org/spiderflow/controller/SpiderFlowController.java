package org.spiderflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.annotation.Comment;
import org.spiderflow.core.executor.FunctionExecutor;
import org.spiderflow.core.executor.FunctionExtension;
import org.spiderflow.core.executor.PluginConfig;
import org.spiderflow.core.expression.Grammerable;
import org.spiderflow.core.model.Grammer;
import org.spiderflow.core.model.Plugin;
import org.spiderflow.core.model.Shape;
import org.spiderflow.core.model.SpiderFlow;
import org.spiderflow.core.utils.ExecutorsUtils;
import org.spiderflow.io.Line;
import org.spiderflow.io.RandomAccessFileReader;
import org.spiderflow.model.JsonBean;
import org.spiderflow.service.SpiderFlowServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 爬虫Controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/spider")
public class SpiderFlowController {

	@Autowired
	private List<FunctionExecutor> functionExecutors;

	@Autowired
	private List<FunctionExtension> functionExtensions;

	@Autowired
	private List<Grammerable> grammerables;

	@Autowired
	private SpiderFlowServiceImpl spiderFlowService;

	@Autowired(required = false)
	private List<PluginConfig> pluginConfigs;

	@Value("${spider.workspace}")
	private String workspace;

	private final List<Grammer> grammers = new ArrayList<Grammer>();

	private static Logger logger = LoggerFactory.getLogger(SpiderFlowController.class);

	@PostConstruct
	private void init() {
		for (FunctionExecutor executor : functionExecutors) {
			String function = executor.getFunctionPrefix();
			grammers.addAll(Grammer.findGrammers(executor.getClass(), function, function, true));
			Comment comment = executor.getClass().getAnnotation(Comment.class);
			Grammer grammer = new Grammer();
			if (comment != null) {
				grammer.setComment(comment.value());
			}
			grammer.setFunction(function);
			grammers.add(grammer);
		}

		for (FunctionExtension extension : functionExtensions) {
			String owner = extension.support().getSimpleName();
			grammers.addAll(Grammer.findGrammers(extension.getClass(), null, owner, true));
		}
		for (Grammerable grammerable : grammerables) {
			grammers.addAll(grammerable.grammers());
		}
	}

	/**
	 * 爬虫列表
	 *
	 * @param page 页数
	 * @param size 每页显示条数
	 * @return Page<SpiderFlow> 所有爬虫的列表页
	 */
	@RequestMapping("/list")
	public IPage<SpiderFlow> list(@RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "limit", defaultValue = "1") Integer size, @RequestParam(name = "name", defaultValue = "") String name) {
		return spiderFlowService.selectSpiderPage(new Page<>(page, size), name);
	}

	@RequestMapping("/save")
	public String save(SpiderFlow spiderFlow) {
		spiderFlowService.save(spiderFlow);
		return spiderFlow.getId();
	}

	@RequestMapping("/history")
	public JsonBean<?> history(String id, String timestamp) {
		if (StringUtils.isNotBlank(timestamp)) {
			return new JsonBean<>(spiderFlowService.readHistory(id, timestamp));
		} else {
			return new JsonBean<>(spiderFlowService.historyList(id));
		}
	}

	@RequestMapping("/get")
	public SpiderFlow get(String id) {
		return spiderFlowService.getById(id);
	}

	@RequestMapping("/other")
	public List<SpiderFlow> other(String id) {
		if (StringUtils.isBlank(id)) {
			return spiderFlowService.selectFlows();
		}
		return spiderFlowService.selectOtherFlows(id);
	}

	@RequestMapping("/remove")
	public void remove(String id) {
		spiderFlowService.remove(id);
	}

	@RequestMapping("/start")
	public void start(String id) {
		spiderFlowService.start(id);
	}

	@RequestMapping("/stop")
	public void stop(String id) {
		spiderFlowService.stop(id);
	}

	@RequestMapping("/copy")
	public void copy(String id) {
		spiderFlowService.copy(id);
	}

	@RequestMapping("/run")
	public void run(String id) {
		spiderFlowService.run(id);
	}

	@RequestMapping("/cron")
	public void cron(String id, String cron) {
		spiderFlowService.resetCornExpression(id, cron);
	}

	@RequestMapping("/xml")
	public String xml(String id) {
		return spiderFlowService.getById(id).getXml();
	}

	@RequestMapping("/log/download")
	public ResponseEntity<FileSystemResource> download(String id, String taskId) {
		if (StringUtils.isBlank(taskId) || NumberUtils.toInt(taskId, 0) == 0) {
			Integer maxId = spiderFlowService.getFlowMaxTaskId(id);
			taskId = maxId == null ? "" : maxId.toString();
		}
		File file = new File(workspace, id + File.separator + "logs" + File.separator + taskId + ".log");
		return ResponseEntity.ok()
				.header("Content-Disposition", "attachment; filename=spider.log")
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new FileSystemResource(file));
	}

	@RequestMapping("/log")
	public JsonBean<List<Line>> log(String id, String taskId, String keywords, Long index, Integer count, Boolean reversed, Boolean matchcase, Boolean regx) {
		if (StringUtils.isBlank(taskId)) {
			Integer maxId = spiderFlowService.getFlowMaxTaskId(id);
			taskId = maxId == null ? "" : maxId.toString();
		}
		File logFile = new File(workspace, id + File.separator + "logs" + File.separator + taskId + ".log");
		try (RandomAccessFileReader reader = new RandomAccessFileReader(new RandomAccessFile(logFile, "r"), index == null ? -1 : index, reversed == null || reversed)) {
			return new JsonBean<>(reader.readLine(count == null ? 10 : count, keywords, matchcase != null && matchcase, regx != null && regx));
		} catch (FileNotFoundException e) {
			return new JsonBean<>(0, "日志文件不存在");
		} catch (IOException e) {
			logger.error("读取日志文件出错", e);
			return new JsonBean<>(-1, "读取日志文件出错");
		}
	}

	@RequestMapping("/shapes")
	public List<Shape> shapes() {
		return ExecutorsUtils.shapes();
	}

	@RequestMapping("/pluginConfigs")
	public List<Plugin> pluginConfigs() {
		return null == pluginConfigs ? Collections.emptyList() : pluginConfigs.stream().filter(e -> e.plugin() != null).map(plugin -> plugin.plugin()).collect(Collectors.toList());
	}

	@RequestMapping("/grammers")
	public JsonBean<List<Grammer>> grammers() {
		return new JsonBean<>(this.grammers);
	}

	@GetMapping("/recent5TriggerTime")
	public List<String> getRecent5TriggerTime(String cron) {
		return spiderFlowService.getRecentTriggerTime(cron, 5);
	}
}
