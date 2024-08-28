/*
 Navicat MySQL Data Transfer

 Source Server         : Local-MySQL
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : spiderflow

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 28/08/2024 20:09:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS spiderflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE spiderflow;

-- ----------------------------
-- Table structure for sp_datasource
-- ----------------------------
DROP TABLE IF EXISTS `sp_datasource`;
CREATE TABLE `sp_datasource` (
  `id` varchar(32) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `driver_class_name` varchar(255) DEFAULT NULL,
  `jdbc_url` varchar(255) DEFAULT NULL,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(32) DEFAULT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `connection_pool_config` text COMMENT 'JDBC连接池配置(JSON格式)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for sp_flow
-- ----------------------------
DROP TABLE IF EXISTS `sp_flow`;
CREATE TABLE `sp_flow` (
  `id` varchar(32) NOT NULL,
  `name` varchar(64) DEFAULT NULL COMMENT '任务名字',
  `xml` longtext COMMENT 'xml表达式',
  `cron` varchar(255) DEFAULT NULL COMMENT 'corn表达式',
  `enabled` char(1) DEFAULT '0' COMMENT '任务是否启动,默认未启动',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_execute_time` datetime DEFAULT NULL COMMENT '上一次执行时间',
  `next_execute_time` datetime DEFAULT NULL COMMENT '下一次执行时间',
  `execute_count` int(8) DEFAULT NULL COMMENT '定时执行的已执行次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='爬虫任务表';

-- ----------------------------
-- Records of sp_flow
-- ----------------------------
BEGIN;
INSERT INTO `sp_flow` VALUES ('663aaa5e36a84c9594ef3cfd6738e9a7', '百度热点', '<mxGraphModel>\n  <root>\n    <mxCell id=\"0\">\n      <JsonProperty as=\"data\">\n        {&quot;spiderName&quot;:&quot;百度热点&quot;,&quot;threadCount&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"1\" parent=\"0\"/>\n    <mxCell id=\"2\" value=\"开始\" style=\"start\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"80\" y=\"80\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;shape&quot;:&quot;start&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"3\" value=\"开始抓取\" style=\"request\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"220\" y=\"80\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;开始抓取&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;sleep&quot;:&quot;&quot;,&quot;timeout&quot;:&quot;&quot;,&quot;response-charset&quot;:&quot;gbk&quot;,&quot;method&quot;:&quot;GET&quot;,&quot;body-type&quot;:&quot;none&quot;,&quot;body-content-type&quot;:&quot;text/plain&quot;,&quot;loopCount&quot;:&quot;&quot;,&quot;url&quot;:&quot;https://top.baidu.com/buzz?b=1&amp;fr=topindex&quot;,&quot;proxy&quot;:&quot;&quot;,&quot;request-body&quot;:&quot;&quot;,&quot;follow-redirect&quot;:&quot;1&quot;,&quot;tls-validate&quot;:&quot;1&quot;,&quot;shape&quot;:&quot;request&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"4\" value=\"定义变量\" style=\"variable\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"360\" y=\"80\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;定义变量&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;variable-name&quot;:[&quot;elementbd&quot;],&quot;loopCount&quot;:&quot;&quot;,&quot;variable-value&quot;:[&quot;${resp.xpaths(&#39;//*[@id=\\&quot;main\\&quot;]/div[2]/div/table/tbody/tr&#39;)}&quot;],&quot;shape&quot;:&quot;variable&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"5\" value=\"输出\" style=\"output\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"480\" y=\"80\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;输出&quot;,&quot;loopVariableName&quot;:&quot;i&quot;,&quot;output-name&quot;:[&quot;名称&quot;,&quot;地址&quot;,&quot;百度指数&quot;,&quot;2&quot;],&quot;loopCount&quot;:&quot;${elementbd.size()-1}&quot;,&quot;output-value&quot;:[&quot;${elementbd[i+1].xpath(&#39;//td[2]/a[1]/text()&#39;)}&quot;,&quot;${elementbd[i+1].xpath(&#39;//td[2]/a[1]/@href&#39;)}&quot;,&quot;${elementbd[i+1].xpath(&#39;//td[4]/span/text()&#39;)}&quot;,&quot;${elementbd[i+1].xpath(&#39;//td[3]/a[2]/text()&#39;)}&quot;],&quot;shape&quot;:&quot;output&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"6\" value=\"\" parent=\"1\" source=\"2\" target=\"3\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"7\" value=\"\" parent=\"1\" source=\"3\" target=\"4\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"8\" value=\"\" parent=\"1\" source=\"4\" target=\"5\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n  </root>\n</mxGraphModel>\n', '0/50 0 * * * ? *', '1', '2019-10-20 17:24:21', '2024-08-13 07:04:26', '2024-08-28 20:00:00', 51);
INSERT INTO `sp_flow` VALUES ('b4430885ba8349588d1220d37eac831d', '爬取开源中国动弹', '<mxGraphModel>\n  <root>\n    <mxCell id=\"0\">\n      <JsonProperty as=\"data\">\n        {&quot;spiderName&quot;:&quot;爬取开源中国动弹&quot;,&quot;threadCount&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"1\" parent=\"0\"/>\n    <mxCell id=\"2\" value=\"开始\" style=\"start\" vertex=\"1\" parent=\"1\">\n      <mxGeometry x=\"80\" y=\"80\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;shape&quot;:&quot;start&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"3\" value=\"爬取动弹\" style=\"request\" vertex=\"1\" parent=\"1\">\n      <mxGeometry x=\"220\" y=\"80\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;爬取动弹&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;sleep&quot;:&quot;&quot;,&quot;timeout&quot;:&quot;&quot;,&quot;response-charset&quot;:&quot;&quot;,&quot;method&quot;:&quot;GET&quot;,&quot;parameter-name&quot;:[&quot;type&quot;,&quot;lastLogId&quot;],&quot;body-type&quot;:&quot;none&quot;,&quot;body-content-type&quot;:&quot;text/plain&quot;,&quot;loopCount&quot;:&quot;&quot;,&quot;url&quot;:&quot;https://www.oschina.net/tweets/widgets/_tweet_index_list &quot;,&quot;proxy&quot;:&quot;&quot;,&quot;parameter-value&quot;:[&quot;ajax&quot;,&quot;${lastLogId}&quot;],&quot;request-body&quot;:&quot;&quot;,&quot;follow-redirect&quot;:&quot;1&quot;,&quot;tls-validate&quot;:&quot;1&quot;,&quot;shape&quot;:&quot;request&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"4\" value=\"\" edge=\"1\" parent=\"1\" source=\"2\" target=\"3\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"5\" value=\"提取lastLogId以及tweets\" style=\"variable\" vertex=\"1\" parent=\"1\">\n      <mxGeometry x=\"340\" y=\"80\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;提取lastLogId以及tweets&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;variable-name&quot;:[&quot;lastLogId&quot;,&quot;tweets&quot;,&quot;fetchCount&quot;],&quot;loopCount&quot;:&quot;&quot;,&quot;variable-value&quot;:[&quot;${resp.selector(&#39;.tweet-item:last-child&#39;).attr(&#39;data-tweet-id&#39;)}&quot;,&quot;${resp.selectors(&#39;.tweet-item[data-tweet-id]&#39;)}&quot;,&quot;${fetchCount == null ? 0 : fetchCount + 1}&quot;],&quot;shape&quot;:&quot;variable&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"6\" value=\"\" edge=\"1\" parent=\"1\" source=\"3\" target=\"5\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"7\" value=\"循环\" style=\"loop\" vertex=\"1\" parent=\"1\">\n      <mxGeometry x=\"340\" y=\"250\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;循环&quot;,&quot;loopVariableName&quot;:&quot;index&quot;,&quot;loopCount&quot;:&quot;${list.length(tweets)}&quot;,&quot;shape&quot;:&quot;loop&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"8\" value=\"\" edge=\"1\" parent=\"1\" source=\"5\" target=\"7\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"9\" value=\"提取详细信息\" style=\"variable\" vertex=\"1\" parent=\"1\">\n      <mxGeometry x=\"340\" y=\"340\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;提取详细信息&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;variable-name&quot;:[&quot;content&quot;,&quot;author&quot;,&quot;like&quot;,&quot;reply&quot;,&quot;publishTime&quot;],&quot;loopCount&quot;:&quot;&quot;,&quot;variable-value&quot;:[&quot;${tweets[index].selector(&#39;.text&#39;).text()}&quot;,&quot;${tweets[index].selector(&#39;.user&#39;).text()}&quot;,&quot;${tweets[index].selector(&#39;.like span&#39;).text()}&quot;,&quot;${tweets[index].selector(&#39;.reply span&#39;).text()}&quot;,&quot;${tweets[index].selector(&#39;.date&#39;).regx(&#39;(.*?)&amp;nbsp&#39;)}&quot;],&quot;shape&quot;:&quot;variable&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"10\" value=\"\" edge=\"1\" parent=\"1\" source=\"7\" target=\"9\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"11\" value=\"输出\" style=\"output\" vertex=\"1\" parent=\"1\">\n      <mxGeometry x=\"340\" y=\"430\" width=\"32\" height=\"32\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;输出&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;output-name&quot;:[&quot;作者&quot;,&quot;内容&quot;,&quot;点赞数&quot;,&quot;评论数&quot;,&quot;发布时间&quot;],&quot;loopCount&quot;:&quot;&quot;,&quot;output-value&quot;:[&quot;${author}&quot;,&quot;${content}&quot;,&quot;${like}&quot;,&quot;${reply}&quot;,&quot;${publishTime}&quot;],&quot;shape&quot;:&quot;output&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"12\" value=\"\" edge=\"1\" parent=\"1\" source=\"9\" target=\"11\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"13\" value=\"爬取3页\" edge=\"1\" parent=\"1\" source=\"5\" target=\"3\">\n      <mxGeometry x=\"-0.0312\" y=\"-20\" relative=\"1\" as=\"geometry\">\n        <Array as=\"points\">\n          <mxPoint x=\"356\" y=\"180\"/>\n          <mxPoint x=\"236\" y=\"180\"/>\n        </Array>\n        <mxPoint as=\"offset\"/>\n      </mxGeometry>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;爬取5页&quot;,&quot;condition&quot;:&quot;${fetchCount &lt; 3}&quot;}\n      </JsonProperty>\n    </mxCell>\n  </root>\n</mxGraphModel>\n', '', '0', '2019-11-03 17:02:49', '2024-08-13 06:20:27', NULL, 5);
INSERT INTO `sp_flow` VALUES ('b45fb98d2a564c23ba623a377d5e12e9', '爬取码云GVP', '<mxGraphModel>\n  <root>\n    <mxCell id=\"0\">\n      <JsonProperty as=\"data\">{\"flowId\":\"b45fb98d2a564c23ba623a377d5e12e9\",\"spiderName\":\"爬取码云GVP\",\"submit-strategy\":\"parent\",\"threadCount\":\"\"}</JsonProperty>\n    </mxCell>\n    <mxCell id=\"1\" parent=\"0\" />\n    <mxCell id=\"2\" value=\"开始\" style=\"start\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"80\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"shape\":\"start\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"5\" value=\"抓取首页\" style=\"request\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"180\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"抓取首页\",\"loopVariableName\":\"\",\"method\":\"GET\",\"sleep\":\"\",\"timeout\":\"\",\"response-charset\":\"\",\"retryCount\":\"\",\"retryInterval\":\"\",\"body-type\":\"none\",\"body-content-type\":\"application/json\",\"loopCount\":\"\",\"url\":\"https://gitee.com/gvp/all\",\"proxy\":\"\",\"request-body\":\"\",\"follow-redirect\":\"1\",\"tls-validate\":\"1\",\"cookie-auto-set\":\"1\",\"auto-deduplicate\":\"0\",\"shape\":\"request\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"6\" value=\"\" parent=\"1\" source=\"2\" target=\"5\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"\",\"condition\":\"\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"7\" value=\"提取项目名、地址\" style=\"variable\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"330\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"提取项目名、地址\",\"loopVariableName\":\"\",\"variable-name\":[\"projectUrls\",\"projectNames\"],\"loopCount\":\"\",\"variable-value\":[\"${extract.selectors(resp.html,\'.categorical-project-card a\',\'attr\',\'href\')}\",\"${extract.selectors(resp.html,\'.project-name\')}\"],\"shape\":\"variable\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"8\" value=\"\" parent=\"1\" source=\"5\" target=\"7\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"\",\"condition\":\"\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"9\" value=\"抓取详情页\" style=\"request\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"450.16668701171875\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"抓取详情页\",\"loopVariableName\":\"projectIndex\",\"sleep\":\"\",\"timeout\":\"\",\"response-charset\":\"\",\"method\":\"GET\",\"body-type\":\"none\",\"body-content-type\":\"text/plain\",\"loopCount\":\"10\",\"url\":\"https://gitee.com/${projectUrls[projectIndex]}\",\"proxy\":\"\",\"request-body\":[\"\"],\"follow-redirect\":\"1\",\"shape\":\"request\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"10\" value=\"\" parent=\"1\" source=\"7\" target=\"9\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"\",\"condition\":\"\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"12\" value=\"提取项目描述\" style=\"variable\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"550\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"提取项目描述\",\"loopVariableName\":\"\",\"variable-name\":[\"projectDesc\"],\"loopCount\":\"\",\"variable-value\":[\"${extract.selector(resp.html,\'.git-project-desc-text\')}\"],\"shape\":\"variable\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"13\" value=\"\" parent=\"1\" source=\"9\" target=\"12\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"\",\"condition\":\"\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"14\" value=\"输出\" style=\"output\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"660.1666870117188\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"输出\",\"output-name\":[\"项目名\",\"项目地址\",\"项目描述\"],\"output-value\":[\"${projectNames[projectIndex]}\",\"https://gitee.com${projectUrls[projectIndex]}\",\"${projectDesc}\"],\"shape\":\"output\"}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"15\" value=\"\" parent=\"1\" source=\"12\" target=\"14\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\" />\n      <JsonProperty as=\"data\">\n        {\"value\":\"\",\"condition\":\"\"}\n      </JsonProperty>\n    </mxCell>\n  </root>\n</mxGraphModel>', NULL, '0', '2019-08-22 13:46:54', '2024-08-23 19:35:30', NULL, 8);
INSERT INTO `sp_flow` VALUES ('f0a67f17ee1a498a9b2f4ca30556f3c3', '抓取每日菜价', '<mxGraphModel>\n  <root>\n    <mxCell id=\"0\">\n      <JsonProperty as=\"data\">\n        {&quot;spiderName&quot;:&quot;抓取每日菜价&quot;,&quot;threadCount&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"1\" parent=\"0\"/>\n    <mxCell id=\"2\" value=\"开始\" style=\"start\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"80\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;shape&quot;:&quot;start&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"3\" value=\"开始抓取\" style=\"request\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"219.83334350585938\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;开始抓取&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;sleep&quot;:&quot;&quot;,&quot;timeout&quot;:&quot;&quot;,&quot;response-charset&quot;:&quot;&quot;,&quot;method&quot;:&quot;GET&quot;,&quot;body-type&quot;:&quot;none&quot;,&quot;body-content-type&quot;:&quot;text/plain&quot;,&quot;loopCount&quot;:&quot;&quot;,&quot;url&quot;:&quot;http://www.beijingprice.cn:8086/price/priceToday/PageLoad/LoadPrice?jsoncallback=1&quot;,&quot;proxy&quot;:&quot;&quot;,&quot;request-body&quot;:[&quot;&quot;],&quot;follow-redirect&quot;:&quot;1&quot;,&quot;shape&quot;:&quot;request&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"4\" value=\"\" parent=\"1\" source=\"2\" target=\"3\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"5\" value=\"解析JSON\" style=\"variable\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"350\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;解析JSON&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;variable-name&quot;:[&quot;jsonstr&quot;,&quot;jsondata&quot;,&quot;data&quot;],&quot;loopCount&quot;:&quot;&quot;,&quot;variable-value&quot;:[&quot;${string.substring(resp.html,2,resp.html.length()-1)}&quot;,&quot;${json.parse(jsonstr)}&quot;,&quot;${extract.jsonpath(jsondata[0],&#39;data&#39;)}&quot;],&quot;shape&quot;:&quot;variable&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"6\" value=\"\" parent=\"1\" source=\"3\" target=\"5\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"7\" value=\"输出\" style=\"output\" parent=\"1\" vertex=\"1\">\n      <mxGeometry x=\"480.16668701171875\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;输出&quot;,&quot;loopVariableName&quot;:&quot;i&quot;,&quot;output-name&quot;:[&quot;菜名&quot;,&quot;菜价&quot;,&quot;单位&quot;],&quot;loopCount&quot;:&quot;${list.length(data)}&quot;,&quot;output-value&quot;:[&quot;${data[i].ItemName}&quot;,&quot;${data[i].Price04}&quot;,&quot;${data[i].ItemUnit}&quot;],&quot;shape&quot;:&quot;output&quot;}\n      </JsonProperty>\n    </mxCell>\n    <mxCell id=\"8\" value=\"\" parent=\"1\" source=\"5\" target=\"7\" edge=\"1\">\n      <mxGeometry relative=\"1\" as=\"geometry\"/>\n      <JsonProperty as=\"data\">\n        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n      </JsonProperty>\n    </mxCell>\n  </root>\n</mxGraphModel>\n', NULL, '0', '2019-08-22 13:48:22', '2024-08-12 20:06:03', NULL, 1);
COMMIT;

-- ----------------------------
-- Table structure for sp_flow_notice
-- ----------------------------
DROP TABLE IF EXISTS `sp_flow_notice`;
CREATE TABLE `sp_flow_notice` (
  `id` varchar(32) NOT NULL,
  `recipients` varchar(200) DEFAULT NULL COMMENT '收件人',
  `notice_way` char(10) DEFAULT NULL COMMENT '通知方式',
  `start_notice` char(1) DEFAULT '0' COMMENT '流程开始通知:1:开启通知,0:关闭通知',
  `exception_notice` char(1) DEFAULT '0' COMMENT '流程异常通知:1:开启通知,0:关闭通知',
  `end_notice` char(1) DEFAULT '0' COMMENT '流程结束通知:1:开启通知,0:关闭通知',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='爬虫任务通知表';

-- ----------------------------
-- Table structure for sp_function
-- ----------------------------
DROP TABLE IF EXISTS `sp_function`;
CREATE TABLE `sp_function` (
  `id` varchar(32) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '函数名',
  `parameter` varchar(512) DEFAULT NULL COMMENT '参数',
  `script` text COMMENT 'js脚本',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for sp_job_history
-- ----------------------------
DROP TABLE IF EXISTS `sp_job_history`;
CREATE TABLE `sp_job_history` (
  `id` varchar(32) NOT NULL,
  `flow_id` varchar(32) NOT NULL,
  `start_execution_time` datetime DEFAULT NULL,
  `end_execution_time` datetime DEFAULT NULL,
  `execution_status` tinyint(255) DEFAULT NULL COMMENT '爬虫任务执行状态,0=未开始,1=运行中,2=已完成,3=异常中断',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sp_task
-- ----------------------------
DROP TABLE IF EXISTS `sp_task`;
CREATE TABLE `sp_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `flow_id` varchar(32) NOT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of sp_task
-- ----------------------------
BEGIN;
INSERT INTO `sp_task` VALUES (7, '663aaa5e36a84c9594ef3cfd6738e9a7', '2024-08-12 20:00:00', '2024-08-12 20:00:02');
INSERT INTO `sp_task` VALUES (8, '663aaa5e36a84c9594ef3cfd6738e9a7', '2024-08-12 20:00:50', '2024-08-12 20:00:50');
INSERT INTO `sp_task` VALUES (9, '663aaa5e36a84c9594ef3cfd6738e9a7', '2024-08-12 20:03:11', '2024-08-12 20:03:11');
INSERT INTO `sp_task` VALUES (10, 'b4430885ba8349588d1220d37eac831d', '2024-08-12 20:05:43', '2024-08-12 20:05:44');
INSERT INTO `sp_task` VALUES (11, 'f0a67f17ee1a498a9b2f4ca30556f3c3', '2024-08-12 20:06:03', '2024-08-12 20:06:04');
INSERT INTO `sp_task` VALUES (12, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-12 20:08:27', '2024-08-12 20:08:34');
INSERT INTO `sp_task` VALUES (13, '663aaa5e36a84c9594ef3cfd6738e9a7', '2024-08-12 20:12:27', '2024-08-12 20:12:28');
INSERT INTO `sp_task` VALUES (14, '663aaa5e36a84c9594ef3cfd6738e9a7', '2024-08-13 06:19:06', '2024-08-13 06:19:09');
INSERT INTO `sp_task` VALUES (15, 'b4430885ba8349588d1220d37eac831d', '2024-08-13 06:20:27', '2024-08-13 06:20:30');
INSERT INTO `sp_task` VALUES (16, '663aaa5e36a84c9594ef3cfd6738e9a7', '2024-08-13 07:04:26', '2024-08-13 07:04:27');
INSERT INTO `sp_task` VALUES (17, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-20 23:01:06', '2024-08-20 23:01:09');
INSERT INTO `sp_task` VALUES (18, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-20 23:09:26', '2024-08-20 23:09:29');
INSERT INTO `sp_task` VALUES (19, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-21 03:48:14', '2024-08-21 03:48:19');
INSERT INTO `sp_task` VALUES (20, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-21 06:28:59', '2024-08-21 06:29:03');
INSERT INTO `sp_task` VALUES (21, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-21 07:08:43', '2024-08-21 07:08:44');
INSERT INTO `sp_task` VALUES (22, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-21 07:10:05', '2024-08-21 07:10:05');
INSERT INTO `sp_task` VALUES (23, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-21 07:25:31', '2024-08-21 07:25:35');
INSERT INTO `sp_task` VALUES (24, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-23 19:34:50', '2024-08-23 19:34:53');
INSERT INTO `sp_task` VALUES (25, 'b45fb98d2a564c23ba623a377d5e12e9', '2024-08-23 19:35:30', '2024-08-23 19:35:33');
COMMIT;

-- ----------------------------
-- Table structure for sp_variable
-- ----------------------------
DROP TABLE IF EXISTS `sp_variable`;
CREATE TABLE `sp_variable` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL COMMENT '变量名',
  `value` varchar(512) DEFAULT NULL COMMENT '变量值',
  `description` varchar(255) DEFAULT NULL COMMENT '变量描述',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
