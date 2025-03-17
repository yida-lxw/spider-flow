package org.spiderflow.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-09-24 10:55
 * @description PDF操作工具类
 */
public class PDFUtils {
	private static final Logger log = LoggerFactory.getLogger(PDFUtils.class);
	private static final String WKHTML_TO_PDF_COMMAND_TEMPLATE = "${wkhtml2pdf_basepath}bin/${wkhtml2pdf_command_name} " +
			"--encoding utf8 --page-size A4 --enable-local-file-access " +
			"\"${src}\" \"${dest}\"";

	public static boolean convertHtmlToPDF(String sourceHtmlFilePath, String targetPdfFilePath,
										   String wkhtml2pdfBasepath, String wkhtml2pdfCommandName) {

		wkhtml2pdfBasepath = StringUtils.replaceBackSlash(wkhtml2pdfBasepath);
		if (!wkhtml2pdfBasepath.endsWith("/")) {
			wkhtml2pdfBasepath = wkhtml2pdfBasepath + "/";
		}
		String osType = OSUtils.getOSType();
		if ("windows".equals(osType)) {
			if (!wkhtml2pdfCommandName.endsWith(".exe")) {
				wkhtml2pdfCommandName = wkhtml2pdfCommandName + ".exe";
			}
		} else if ("linux".equals(osType) || "mac".equals(osType)) {
			if (wkhtml2pdfCommandName.endsWith(".exe")) {
				wkhtml2pdfCommandName = wkhtml2pdfCommandName.substring(0, wkhtml2pdfCommandName.lastIndexOf("."));
			}
		}
		String command = WKHTML_TO_PDF_COMMAND_TEMPLATE.replace("${wkhtml2pdf_basepath}", wkhtml2pdfBasepath)
				.replace("${wkhtml2pdf_command_name}", wkhtml2pdfCommandName)
				.replace("${src}", sourceHtmlFilePath)
				.replace("${dest}", targetPdfFilePath);
		log.info("command:{}", command);
		return CommandExecuteUtils.execute(command, "wkhtml2pdf_converter");
	}
}
