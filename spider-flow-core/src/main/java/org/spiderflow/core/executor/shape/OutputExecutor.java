package org.spiderflow.core.executor.shape;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.executor.ShapeExecutor;
import org.spiderflow.core.http.SpiderResponse;
import org.spiderflow.core.listener.SpiderListener;
import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.model.SpiderOutput;
import org.spiderflow.core.utils.DataSourceUtils;
import org.spiderflow.core.utils.ExpressionUtils;
import org.spiderflow.core.utils.JacksonUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 输出执行器
 *
 * @author Administrator
 */
@Component
public class OutputExecutor implements ShapeExecutor, SpiderListener {
	private static Logger logger = LoggerFactory.getLogger(OutputExecutor.class);

	public static final String OUTPUT_ALL = "output-all";

	public static final String OUTPUT_NAME = "output-name";

	public static final String OUTPUT_VALUE = "output-value";

	public static final String DATASOURCE_ID = "datasourceId";

	public static final String OUTPUT_DATABASE = "output-database";

	public static final String OUTPUT_CSV = "output-csv";

	public static final String TABLE_NAME = "tableName";

	public static final String CSV_NAME = "csvName";

	public static final String CSV_ENCODING = "csvEncoding";

	/**
	 * 输出CSVPrinter节点变量
	 */
	private Map<String, CSVPrinter> cachePrinter = new HashMap<>();

	@Override
	public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
		SpiderOutput output = new SpiderOutput();
		output.setNodeName(node.getNodeName());
		output.setNodeId(node.getNodeId());
		boolean outputAll = "1".equals(node.getStringJsonValue(OUTPUT_ALL));
		boolean databaseFlag = "1".equals(node.getStringJsonValue(OUTPUT_DATABASE));
		boolean csvFlag = "1".equals(node.getStringJsonValue(OUTPUT_CSV));
		if (outputAll) {
			outputAll(output, variables);
		}
		List<Map<String, String>> outputs = node.getListJsonValue(OUTPUT_NAME, OUTPUT_VALUE);
		Map<String, Object> outputData = null;
		if (databaseFlag || csvFlag) {
			outputData = new HashMap<>(outputs.size());
		}
		for (Map<String, String> item : outputs) {
			Object value = null;
			String outputValue = item.get(OUTPUT_VALUE);
			String outputName = item.get(OUTPUT_NAME);
			try {
				value = ExpressionUtils.execute(outputValue, variables);
				context.pause(node.getNodeId(), "common", outputName, value);
				logger.debug("输出{}={}", outputName, value);
			} catch (Exception e) {
				logger.error("输出{}出错，异常信息：{}", outputName, e);
			}
			output.addOutput(outputName, value);
			if ((databaseFlag || csvFlag) && value != null) {
				outputData.put(outputName, value.toString());
			}
		}
		if (databaseFlag) {
			String dsId = node.getStringJsonValue(DATASOURCE_ID);
			String tableName = node.getStringJsonValue(TABLE_NAME);
			if (StringUtils.isBlank(dsId)) {
				logger.warn("数据源ID为空！");
			} else if (StringUtils.isBlank(tableName)) {
				logger.warn("表名为空！");
			} else {
				outputDB(dsId, tableName, outputData);
			}
		}
		if (csvFlag) {
			String csvName = node.getStringJsonValue(CSV_NAME);
			outputCSV(node, context, csvName, outputData);
		}
		context.addOutput(output);
	}

	/**
	 * 输出所有参数
	 *
	 * @param output
	 * @param variables
	 */
	private void outputAll(SpiderOutput output, Map<String, Object> variables) {
		for (Map.Entry<String, Object> item : variables.entrySet()) {
			Object value = item.getValue();
			if (value instanceof SpiderResponse) {
				SpiderResponse resp = (SpiderResponse) value;
				output.addOutput(item.getKey() + ".html", resp.getHtml());
				continue;
			}
			//去除不输出的信息
			if ("ex".equals(item.getKey())) {
				continue;
			}
			//去除不能序列化的参数
			try {
				JacksonUtils.toJSONString(value);
			} catch (Exception e) {
				logger.error("Parsing the value:{} to JSON String for validing occur exception:\n{}.", value, e.getMessage());
				continue;
			}
			//输出信息
			output.addOutput(item.getKey(), item.getValue());
		}
	}

	private void outputDB(String databaseId, String tableName, Map<String, Object> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		JdbcTemplate template = new JdbcTemplate(DataSourceUtils.getDataSource(databaseId));
		Set<String> keySet = data.keySet();
		Object[] params = new Object[data.size()];
		SQL sql = new SQL();
		//设置表名
		sql.INSERT_INTO(tableName);
		int index = 0;
		//设置字段名
		for (String key : keySet) {
			sql.VALUES(key, "?");
			params[index] = data.get(key);
			index++;
		}
		try {
			//执行sql
			template.update(sql.toString(), params);
		} catch (Exception e) {
			logger.error("执行sql出错,异常信息:{}", e.getMessage(), e);
			ExceptionUtils.wrapAndThrow(e);
		}
	}

	private void outputCSV(SpiderNode node, SpiderContext context, String csvName, Map<String, Object> data) {
		if (data == null || data.isEmpty()) {
			return;
		}
		String key = context.getId() + "-" + node.getNodeId();
		CSVPrinter printer = cachePrinter.get(key);
		List<String> records = new ArrayList<>(data.size());
		String[] headers = data.keySet().toArray(new String[data.size()]);
		try {
			if (printer == null) {
				synchronized (cachePrinter) {
					printer = cachePrinter.get(key);
					if (printer == null) {
						CSVFormat format = CSVFormat.DEFAULT.withHeader(headers);
						FileOutputStream os = new FileOutputStream(csvName);
						String csvEncoding = node.getStringJsonValue(CSV_ENCODING);
						if ("UTF-8BOM".equals(csvEncoding)) {
							csvEncoding = csvEncoding.substring(0, 5);
							byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
							os.write(bom);
							os.flush();
						}
						OutputStreamWriter osw = new OutputStreamWriter(os, csvEncoding);
						printer = new CSVPrinter(osw, format);
						cachePrinter.put(key, printer);
					}
				}
			}
			for (int i = 0; i < headers.length; i++) {
				records.add(data.get(headers[i]).toString());
			}
			synchronized (cachePrinter) {
				printer.printRecord(records);
			}
		} catch (IOException e) {
			logger.error("文件输出错误,异常信息:{}", e.getMessage(), e);
			ExceptionUtils.wrapAndThrow(e);
		}
	}

	@Override
	public String supportShape() {
		return "output";
	}

	@Override
	public void beforeStart(SpiderContext context) {

	}

	@Override
	public void afterEnd(SpiderContext context) {
		this.releasePrinters();
	}

	private void releasePrinters() {
		for (Iterator<Map.Entry<String, CSVPrinter>> iterator = this.cachePrinter.entrySet().iterator(); iterator.hasNext(); ) {
			Map.Entry<String, CSVPrinter> entry = iterator.next();
			CSVPrinter printer = entry.getValue();
			if (printer != null) {
				try {
					printer.flush();
					this.cachePrinter.remove(entry.getKey());
				} catch (IOException e) {
					logger.error("文件输出错误,异常信息:{}", e.getMessage(), e);
					ExceptionUtils.wrapAndThrow(e);
				} finally {
					try {
						printer.close();
					} catch (Exception e) {
						logger.error("As closing the printer occur exception:\n{}.", e.getMessage());
					}
				}
			}
		}
	}
}
