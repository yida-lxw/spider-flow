package org.spiderflow.core.constants;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

/**
 * @author yida
 * @package org.spiderflow.core.constants
 * @date 2024-08-21 15:59
 * @description 常量类
 */
public class Constants {
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String DEFAULT_USER_TIMEZONE = "Asia/Shanghai";
	/**
	 * 默认项目运行环境:dev
	 */
	public static final String DEFAULT_ENV = "dev";

	public static final String BOOLEAN_TRUE = "true";
	public static final String BOOLEAN_FALSE = "false";
	public static final String EMPTY_STRING = "";
	public static final String ASTERISK = "*";
	public static final String UNDERSCORE = "_";
	public static final String POUNG_SIGN = "#";
	public static final String PERCENTAGE_SIGN = "%";
	public static final String POUNG_SIGN_UNICODE = "%23";

	//正斜杠
	public static final String FORWARD_SLASH = "/";
	//反斜杠
	public static final String BACK_SLASH = "\\";
	//反斜杠(用于Java正则表达式中)
	public static final String BACK_SLASH_IN_REGEX = "\\\\";

	public static final String ID_FIELD_NAME_DEFAULT = "id";

	//2G文件大小(单位:bytes)
	public static final long DEFAULT_2G_FILE_SIZE = 2147483648L;

	public static final long DEFAULT_KEEP_ALIVE_TIME = 60000L;
	public static final int DEFAULT_CAPACITY = 1024;
	public static final int DEFAULT_MAX_RETRY_TIMES = 3;
	public static final int DEFAULT_RETRY_TIMES_MAP_SIZE = 1024;
	public static final String DEFAULT_THREAD_POOL_PREFFIX = "ThreadPool-";
	public static final String DEFAULT_THREAD_POOL_TAG = "default";

	/**
	 * 当前进程ID
	 */
	public static final String CURRENT_PROCESS_ID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

	/**
	 * 获取当前项目的根目录
	 */
	public static final String CURRENT_PROJECT_BASE_PATH = System.getProperty("user.dir").replace("\\", "/") + "/";

	/**
	 * Maven项目源码的存放包路径
	 */
	public static final String SOURCE_CODE_ROOT_MAVEN = "src/main/java";
	/**
	 * Maven项目资源文件的存放包路径
	 */
	public static final String RESOURCE_FILE_ROOT_MAVEN = "src/main/resources";

	/**
	 * 一个批次从Redis加载id的个数
	 */
	public static final int DEFAULT_ID_BATCH_LOAD_SIZE = 10000;

	//每G的字节大小
	public static final long G_SIZE = 1024L * 1024L * 1024L;
	//每M的字节大小
	public static final long M_SIZE = 1024L * 1024L;

	/**
	 * 1KB的字节数
	 */
	public static final long ONE_KB_BYTES = 1024L;

	/**
	 * 1MB的字节数
	 */
	public static final long ONE_MB_BYTES = 1024L * 1024L;

	/**
	 * 1GB的字节数
	 */
	public static final long ONE_GB_BYTES = 1024L * 1024L * 1024L;

	/**
	 * 1TB的字节数
	 */
	public static final long ONE_TB_BYTES = 1024L * 1024L * 1024L * 1024L;

	/**
	 * 1PB的字节数
	 */
	public static final long ONE_PB_BYTES = 1024L * 1024L * 1024L * 1024L * 1024L;

	public static final BigDecimal ONE_UNIT = new BigDecimal("1024");
	public static final BigDecimal TWO_UNIT = new BigDecimal(String.valueOf(1024L * 1024L));
	public static final BigDecimal THREE_UNIT = new BigDecimal(String.valueOf(1024L * 1024L * 1024L));
	public static final BigDecimal FOUR_UNIT = new BigDecimal(String.valueOf(1024L * 1024L * 1024L * 1024L));
	public static final BigDecimal FIVE_UNIT = new BigDecimal(String.valueOf(1024L * 1024L * 1024L * 1024L * 1024L));

	/**
	 * ThreadGroup
	 */
	public static final ThreadGroup SPIDER_FLOW_THREAD_GROUP = new ThreadGroup("spider-flow-group");

	/**
	 * 线程名称前缀
	 */
	public static final String THREAD_POOL_NAME_PREFIX = "spider-flow-";

	/**
	 * 连续多久没有接收到心跳包，则判定Websocket Client已经断线,默认为10秒(单位:毫秒)
	 */
	public static final long MAX_INTERVAL_OF_NOT_RECIEVING_HEARTBEAT_PACKET = 10000;
}
