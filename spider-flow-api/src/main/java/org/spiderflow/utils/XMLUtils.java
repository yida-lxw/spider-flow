package org.spiderflow.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.spiderflow.core.utils.JacksonUtils;
import org.spiderflow.core.utils.StringUtils;

import java.util.Map;

/**
 * @author yida
 * @package org.spiderflow.utils
 * @date 2024-08-26 21:31
 * @description Type your description over here.
 */
public class XMLUtils {
	public static String updateXML(String flowId, String xmlString) {
		Document document = Jsoup.parse(xmlString, "", Parser.xmlParser().settings(ParseSettings.preserveCase));
		document.outputSettings(new Document.OutputSettings()
				.prettyPrint(false)
				.syntax(Document.OutputSettings.Syntax.xml));
		Element mxGraphModel = document.getElementsByTag("mxGraphModel").get(0);
		Element root = document.getElementsByTag("root").get(0);
		Element firstMaxCell = root.firstElementChild();
		if(null != firstMaxCell) {
			Element jsonProperty = firstMaxCell.firstElementChild();
			if(null != jsonProperty) {
				String jsonPropertyData = jsonProperty.text();
				if(StringUtils.isNotEmpty(jsonPropertyData) && jsonPropertyData.startsWith("{") && jsonPropertyData.endsWith("}")) {
					if(jsonPropertyData.contains("&quot;")) {
						jsonPropertyData = jsonPropertyData.replace("&quot;", "\"");
					}
					Map<String, Object> jsonPropertyMap = JacksonUtils.json2Map(jsonPropertyData);
					jsonPropertyMap.putIfAbsent("flowId", flowId);
					jsonPropertyData = JacksonUtils.toJSONString(jsonPropertyMap);
					//jsonPropertyData = jsonPropertyData.replace("\"", "&quot;");
					jsonProperty.text(jsonPropertyData);
				}
			}
		}
		return mxGraphModel.outerHtml();
	}

	public static String getFlowIdFromXML(String xmlString) {
		Document document = Jsoup.parse(xmlString, "", Parser.xmlParser().settings(ParseSettings.preserveCase));
		document.outputSettings(new Document.OutputSettings()
				.prettyPrint(false)
				.syntax(Document.OutputSettings.Syntax.xml));
		Element root = document.getElementsByTag("root").get(0);
		Element firstMaxCell = root.firstElementChild();
		String flowId = null;
		if(null != firstMaxCell) {
			Element jsonProperty = firstMaxCell.firstElementChild();
			if(null != jsonProperty) {
				String jsonPropertyData = jsonProperty.text();
				if(StringUtils.isNotEmpty(jsonPropertyData) && jsonPropertyData.startsWith("{") && jsonPropertyData.endsWith("}")) {
					if(jsonPropertyData.contains("&quot;")) {
						jsonPropertyData = jsonPropertyData.replace("&quot;", "\"");
					}
					Map<String, Object> jsonPropertyMap = JacksonUtils.json2Map(jsonPropertyData);
					Object flowIdObj = jsonPropertyMap.get("flowId");
					if(null != flowIdObj) {
						flowId = flowIdObj.toString();
					}
				}
			}
		}
		return flowId;
	}

	public static void main(String[] args) {
		String xmlString = "<mxGraphModel>\n" +
				"  <root>\n" +
				"    <mxCell id=\"0\">\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;spiderName&quot;:&quot;爬取码云GVP&quot;,&quot;submit-strategy&quot;:&quot;random&quot;,&quot;threadCount&quot;:&quot;&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"1\" parent=\"0\"/>\n" +
				"    <mxCell id=\"2\" value=\"开始\" style=\"start\" parent=\"1\" vertex=\"1\">\n" +
				"      <mxGeometry x=\"80\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;shape&quot;:&quot;start&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"5\" value=\"抓取首页\" style=\"request\" parent=\"1\" vertex=\"1\">\n" +
				"      <mxGeometry x=\"180\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;抓取首页&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;method&quot;:&quot;GET&quot;,&quot;sleep&quot;:&quot;&quot;,&quot;timeout&quot;:&quot;&quot;,&quot;response-charset&quot;:&quot;&quot;,&quot;retryCount&quot;:&quot;&quot;,&quot;retryInterval&quot;:&quot;&quot;,&quot;body-type&quot;:&quot;none&quot;,&quot;body-content-type&quot;:&quot;application/json&quot;,&quot;loopCount&quot;:&quot;&quot;,&quot;url&quot;:&quot;https://gitee.com/gvp/all&quot;,&quot;proxy&quot;:&quot;&quot;,&quot;request-body&quot;:&quot;&quot;,&quot;follow-redirect&quot;:&quot;1&quot;,&quot;tls-validate&quot;:&quot;1&quot;,&quot;cookie-auto-set&quot;:&quot;1&quot;,&quot;auto-deduplicate&quot;:&quot;0&quot;,&quot;shape&quot;:&quot;request&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"6\" value=\"\" parent=\"1\" source=\"2\" target=\"5\" edge=\"1\">\n" +
				"      <mxGeometry relative=\"1\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"7\" value=\"提取项目名、地址\" style=\"variable\" parent=\"1\" vertex=\"1\">\n" +
				"      <mxGeometry x=\"330\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;提取项目名、地址&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;variable-name&quot;:[&quot;projectUrls&quot;,&quot;projectNames&quot;],&quot;loopCount&quot;:&quot;&quot;,&quot;variable-value&quot;:[&quot;${extract.selectors(resp.html,&#39;.categorical-project-card a&#39;,&#39;attr&#39;,&#39;href&#39;)}&quot;,&quot;${extract.selectors(resp.html,&#39;.project-name&#39;)}&quot;],&quot;shape&quot;:&quot;variable&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"8\" value=\"\" parent=\"1\" source=\"5\" target=\"7\" edge=\"1\">\n" +
				"      <mxGeometry relative=\"1\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"9\" value=\"抓取详情页\" style=\"request\" parent=\"1\" vertex=\"1\">\n" +
				"      <mxGeometry x=\"450.16668701171875\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;抓取详情页&quot;,&quot;loopVariableName&quot;:&quot;projectIndex&quot;,&quot;sleep&quot;:&quot;&quot;,&quot;timeout&quot;:&quot;&quot;,&quot;response-charset&quot;:&quot;&quot;,&quot;method&quot;:&quot;GET&quot;,&quot;body-type&quot;:&quot;none&quot;,&quot;body-content-type&quot;:&quot;text/plain&quot;,&quot;loopCount&quot;:&quot;10&quot;,&quot;url&quot;:&quot;https://gitee.com/${projectUrls[projectIndex]}&quot;,&quot;proxy&quot;:&quot;&quot;,&quot;request-body&quot;:[&quot;&quot;],&quot;follow-redirect&quot;:&quot;1&quot;,&quot;shape&quot;:&quot;request&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"10\" value=\"\" parent=\"1\" source=\"7\" target=\"9\" edge=\"1\">\n" +
				"      <mxGeometry relative=\"1\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"12\" value=\"提取项目描述\" style=\"variable\" parent=\"1\" vertex=\"1\">\n" +
				"      <mxGeometry x=\"550\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;提取项目描述&quot;,&quot;loopVariableName&quot;:&quot;&quot;,&quot;variable-name&quot;:[&quot;projectDesc&quot;],&quot;loopCount&quot;:&quot;&quot;,&quot;variable-value&quot;:[&quot;${extract.selector(resp.html,&#39;.git-project-desc-text&#39;)}&quot;],&quot;shape&quot;:&quot;variable&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"13\" value=\"\" parent=\"1\" source=\"9\" target=\"12\" edge=\"1\">\n" +
				"      <mxGeometry relative=\"1\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"14\" value=\"输出\" style=\"output\" parent=\"1\" vertex=\"1\">\n" +
				"      <mxGeometry x=\"660.1666870117188\" y=\"80\" width=\"24\" height=\"24\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;输出&quot;,&quot;output-name&quot;:[&quot;项目名&quot;,&quot;项目地址&quot;,&quot;项目描述&quot;],&quot;output-value&quot;:[&quot;${projectNames[projectIndex]}&quot;,&quot;https://gitee.com${projectUrls[projectIndex]}&quot;,&quot;${projectDesc}&quot;],&quot;shape&quot;:&quot;output&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"    <mxCell id=\"15\" value=\"\" parent=\"1\" source=\"12\" target=\"14\" edge=\"1\">\n" +
				"      <mxGeometry relative=\"1\" as=\"geometry\"/>\n" +
				"      <JsonProperty as=\"data\">\n" +
				"        {&quot;value&quot;:&quot;&quot;,&quot;condition&quot;:&quot;&quot;}\n" +
				"      </JsonProperty>\n" +
				"    </mxCell>\n" +
				"  </root>\n" +
				"</mxGraphModel>\n";
		String flowId = "b45fb98d2a564c23ba623a377d5e12e9";
		String xml = XMLUtils.updateXML(flowId, xmlString);
		System.out.println(xml);
	}
}
