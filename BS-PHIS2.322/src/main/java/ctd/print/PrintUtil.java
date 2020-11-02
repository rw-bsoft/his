package ctd.print;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRTemplate;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlTemplateLoader;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;

import org.apache.commons.lang.StringUtils;

public class PrintUtil {
	
	public final static int REPORT_TYPE_OF_PDF = 0;
	public final static int REPORT_TYPE_OF_HTML = 1;
	public final static int REPORT_TYPE_OF_WORD = 2;
	public final static int REPORT_TYPE_OF_EXCEL = 3;
	public final static int REPORT_TYPE_OF_XML = 4;
	
	private final static String ContentTypeOfHtml = "text/html;charset=UTF-8";
	private final static String ContentTypeOfWord = "application/msword;charset=UTF-8";
	private final static String ContentTypeOfExcel = "application/vnd.ms-excel;charset=UTF-8";
	private final static String ContentTypeOfPdf = "application/pdf;charset=UTF-8";
	private final static String[] postfix = {".pdf",".html",".doc",".xls",".xml"};
	
	public static String LOAD = "simpleLoad";
	public static String QUERY = "simpleQuery";
	
	// get JRTemplate
	public static JRTemplate getJRTemplate(String fileName) throws JRException{
		return JRXmlTemplateLoader.load(fileName);
	}
	
	// get JasperDesign
	public static JasperDesign getJasperDesign(String fileName) throws JRException{
		return JRXmlLoader.load(fileName);
	}
	
	public static JasperDesign getJasperDesign(File file) throws JRException{
		return JRXmlLoader.load(file);
	}
	
	public static JasperDesign getJasperDesign(InputStream is) throws JRException{
		return JRXmlLoader.load(is);
	}
	
	// get JasperReport
	public static JasperReport getJasperReport(String fileName) throws JRException {
		return JasperCompileManager.compileReport(fileName);
	}
	
	public static JasperReport getJasperReport(InputStream is) throws JRException {
		return JasperCompileManager.compileReport(is);
	}
	
	public static JasperReport getJasperReport(JasperDesign jd) throws JRException {
		return JasperCompileManager.compileReport(jd);
	}

	// get JasperPrint
	public static JasperPrint getJasperPrint(String fileName, Map<String, Object> parameters) throws JRException{
		return getJasperPrint(getJasperReport(fileName), parameters);
	}
	
	public static JasperPrint getJasperPrint(String fileName, Map<String, Object> parameters, Connection con) throws JRException{
		return getJasperPrint(getJasperReport(fileName), parameters,con);
	}
	
	public static JasperPrint getJasperPrint(JasperReport jr, Map<String, Object> parameters) throws JRException{
		return JasperFillManager.fillReport(jr, parameters);
	}
	
	public static JasperPrint getJasperPrint(JasperReport jr, Map<String, Object> parameters, Connection con) throws JRException{
		return JasperFillManager.fillReport(jr, parameters,con);
	}
	
	public static JasperPrint getJasperPrint(JasperReport jr, Map<String, Object> parameters, JRDataSource dataSource) throws JRException{
		return JasperFillManager.fillReport(jr, parameters,dataSource);
	}
	
	// export to httpservletresponse
	public static void exportToHttpServletResponse(int type, JasperPrint jp, HttpServletRequest request, HttpServletResponse response, String title) throws IOException, JRException{
		JRExporter exporter = getJRExporter(type, request, response, title);
		response.setContentType(getContentType(exporter));
		request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jp);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
		exporter.exportReport();
	}
	
	public static void exportToHttpServletResponse(int type, List<JasperPrint> list, HttpServletRequest request, HttpServletResponse response, String title) throws IOException, JRException{
		JRExporter exporter = getJRExporter(type, request, response, title);
		response.setContentType(getContentType(exporter));
		request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_LIST_SESSION_ATTRIBUTE, list);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, list);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
		exporter.exportReport();
	}
	
	// export type
	private static JRExporter getJRExporter(int type, HttpServletRequest request, HttpServletResponse response, String filename) {
		JRExporter exporter = null;
		if(StringUtils.isEmpty(filename)){
			filename = String.valueOf(new Date().getTime());
		}
		try {
			filename = new String(filename.getBytes("GBK"),"iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		switch (type) {
		case REPORT_TYPE_OF_HTML:
			String isLandscape = request.getParameter("landscape");
			boolean landscape = false;
			if(!StringUtils.isEmpty(isLandscape) && "1".equals(isLandscape)){
				landscape = true;
			}
			boolean silentPrint = false;
			String isSilentPrint = request.getParameter("silentPrint");
			if(!StringUtils.isEmpty(isSilentPrint) && "1".equals(isSilentPrint)){
				silentPrint = true;
			}
			exporter = new JRHtmlExporter();
			StringBuffer sb = new StringBuffer("</td><td width=\"50%\">&nbsp;</td></tr>\n</table>\n")
//			.append("<OBJECT classid='CLSID:8856F961-340A-11D0-A96B-00C04FD705A2' id='wb' width='0' height='0'></OBJECT>\n")
			.append("<script type='text/javascript'>\n")
			.append("if(document.all){\n")
//			.append("var hkey_root='HKEY_CURRENT_USER', hkey_path='\\\\Software\\\\Microsoft\\\\Internet Explorer\\\\PageSetup', hkey_key;\n")
//			.append("var RegWsh = new ActiveXObject('WScript.Shell');\n")
//			.append("hkey_key='\\\\header';RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, '');\n")
//			.append("hkey_key='\\\\footer';RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, '');\n")
//			.append("hkey_key='\\\\margin_bottom';RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, '0.158330');\n")
//			.append("hkey_key='\\\\margin_left';RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, '0.156670');\n")
//			.append("hkey_key='\\\\margin_right';RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, '0.111670');\n")
//			.append("hkey_key='\\\\margin_top';RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, '0.156670');\n")
//			.append("hkey_key='\\\\Shrink_To_Fit';RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, 'no');\n")
//			.append("document.all.wb.execwb(7, 1);\n")
			.append("}\n")
			.append("if(jsPrintSetup){\n");
			if(landscape){
				sb.append("jsPrintSetup.setOption('orientation',jsPrintSetup.kLandscapeOrientation);\n");
			}
			sb.append("jsPrintSetup.setOption('marginTop',0);\n")
			.append("jsPrintSetup.setOption('marginBottom',0);\n")
			.append("jsPrintSetup.setOption('marginLeft',0);\n")
			.append("jsPrintSetup.setOption('marginRight',0);\n")
			.append("jsPrintSetup.setSilentPrint("+silentPrint+");\n")
			.append("jsPrintSetup.print();\n")
			.append("}\n")
			.append("</script>\n")
			.append("</body>\n</html>\n");
			exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, sb.toString());
			exporter.setParameter(JRHtmlExporterParameter.ZOOM_RATIO, 1.25f);
			exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "print_image?image=");
			exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML,"<div style='page-break-before:always;'></div>");
			break;
		case REPORT_TYPE_OF_WORD:
			exporter = new JRRtfExporter();
			response.setHeader("Content-Disposition", "inline; filename=\""+filename+postfix[REPORT_TYPE_OF_WORD]+"\""); 
			break;
		case REPORT_TYPE_OF_EXCEL:
			 exporter = new JRXlsExporter();
//			exporter = new JExcelApiExporter();
			response.setHeader("Content-Disposition", "inline; filename=\""+filename+postfix[REPORT_TYPE_OF_EXCEL]+"\""); 
			break;
		case REPORT_TYPE_OF_XML:
			exporter = new JRXmlExporter();
			response.setHeader("Content-Disposition", "inline; filename=\""+filename+postfix[REPORT_TYPE_OF_XML]+"\"");  
			break;
		default:
			exporter = new JRPdfExporter();
			response.setHeader("Content-Disposition", "inline; filename=\""+filename+postfix[REPORT_TYPE_OF_PDF]+"\"");  
			break;
		}
		return exporter;
	}

	// response contenttype
	private static String getContentType(JRExporter exporter) {
		String responseContentType = ContentTypeOfHtml;
		if (exporter instanceof JRRtfExporter) {
			responseContentType = ContentTypeOfWord;
		} else if (exporter instanceof JRXlsExporter || exporter instanceof JExcelApiExporter) {
			responseContentType = ContentTypeOfExcel;
		} else if (exporter instanceof JRPdfExporter) {
			responseContentType = ContentTypeOfPdf;
		}
		return responseContentType;
	}
	
	public static JRDataSource ds(Collection col){
		return new JRMapCollectionDataSource(col);
	}

	public static String getLOAD() {
		return LOAD;
	}

	public static void setLOAD(String lOAD) {
		LOAD = lOAD;
	}

	public static String getQUERY() {
		return QUERY;
	}

	public static void setQUERY(String qUERY) {
		QUERY = qUERY;
	}

}
