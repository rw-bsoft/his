package ctd.print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import ctd.controller.exception.ControllerException;
import ctd.controller.support.AbstractConfigurableLoader;
import ctd.resource.ResourceCenter;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.converter.ConversionUtils;
import ctd.util.xml.XMLHelper;

public class PrintLocalLoader extends AbstractConfigurableLoader<Print> {
	private String listFileName = "prints.xml";
	private static Map<String, Document> listDocMap = new ConcurrentHashMap<String, Document>();;

	public PrintLocalLoader() {
		postfix = ".jrxml";
	}

	public void setListFileName(String listFileName) {
		this.listFileName = listFileName;
	}

	@Override
	public Print load(String id) throws ControllerException {
		if (id.indexOf(".") == -1) {
			throw new ControllerException("print id " + id + " is illegal");
		}
		String domain = S.substringBefore(id, ".");
		Document listDoc = listDocMap.get(domain);
		if (listDoc == null) {
			try {
				Resource r = ResourceCenter.load(
						ResourceUtils.CLASSPATH_URL_PREFIX,
						S.join(domain, "/", listFileName));
				listDoc = XMLHelper.getDocument(r.getInputStream());
				listDocMap.put(domain, listDoc);
			} catch (Exception e) {
				throw new ControllerException(e, listFileName + " init failed.");
			}
		}
		return createInstanceFormDoc(id, listDoc, -1);
	}

	@Override
	public Print createInstanceFormDoc(String id, Document doc, long lastModi)
			throws ControllerException {
		Element root = doc.getRootElement();
		if (root == null) {
			throw new ControllerException(ControllerException.PARSE_ERROR,
					"root element missing.");
		}
		Element el = (Element) root.selectSingleNode("print[@id='" + id + "']");
		if (el == null) {
			throw new ControllerException(ControllerException.IO_ERROR,
					"print element missing.");
		}
		try {
			Print print = ConversionUtils.convert(el, Print.class);
			print.setId(id);
			print.setLastModify(lastModi);
			// add by yangl 动态列报表不去加载jrxml文件
			if (AppContextHolder.getBean(print.getBean()) instanceof DynamicPrint) {
				return print;
			}
			List<Element> templates = el.elements("template");
			if (templates.size() > 0) {
				for (Element e : templates) {
					String path = e.getTextTrim().replaceAll("\\.", "/")
							+ postfix;
					Resource pr = ResourceCenter.load(
							ResourceUtils.CLASSPATH_URL_PREFIX, path);
					JasperDesign jd = PrintUtil.getJasperDesign(pr.getFile());
					addDefaultStyle(jd, StyleManager.getBaseStyle()); // base
																		// style
					jd.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
					if (!e.attributeValue("scriptlet", "").equals("")) {
						jd.setScriptletClass(e.attributeValue("scriptlet", ""));
					}
					jd.setProperty("jrxmlName", id);
					fixMissingParamAndField(jd);
					print.addJasperDesign(jd);
					if (pr.lastModified() > print.getlastModify()) {
						print.setLastModify(pr.lastModified());
					}
				}
			} else {
				String path = id.replaceAll("\\.", "/") + postfix;
				Resource pr = ResourceCenter.load(
						ResourceUtils.CLASSPATH_URL_PREFIX, path);
				JasperDesign jd = PrintUtil.getJasperDesign(pr.getFile());
				addDefaultStyle(jd, StyleManager.getBaseStyle()); // base style
				jd.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
				if (!el.attributeValue("scriptlet", "").equals("")) {
					jd.setScriptletClass(el.attributeValue("scriptlet", ""));
				}
				jd.setProperty("jrxmlName", id);
				fixMissingParamAndField(jd);
				print.addJasperDesign(jd);
				print.setLastModify(pr.lastModified());
			}

			return print;
		} catch (Exception e) {
			throw new ControllerException(e, ControllerException.PARSE_ERROR,
					"print[" + id + "] init unknow error:" + e.getMessage());
		}
	}

	private void addDefaultStyle(JasperDesign design, JRDesignStyle style)
			throws JRException {
		HashMap<String, JRStyle> map = (HashMap<String, JRStyle>) design
				.getStylesMap();
		if (!map.containsKey(style.getName())) {
			design.addStyle(style);
		}
	}

	private void fixMissingParamAndField(JasperDesign design)
			throws JRException {
		Map<String, JRParameter> params = design.getParametersMap(); // parameters
		Map<String, JRField> fields = design.getFieldsMap(); // fields
		List<JRChild> els = getDesignElements(design);
		JRDesignStyle base = (JRDesignStyle) design.getStylesMap().get(
				StyleManager.baseStyleName);
		for (JRChild child : els) {
			if (!(child instanceof JRDesignElement)) {
				continue;
			}
			JRDesignElement e = (JRDesignElement) child;
			e.setStyle(base); // 设置base样式
			if (!(e instanceof JRDesignTextField)) {
				if (e instanceof JRDesignStaticText) {
					fixPDFFontEncoding((JRDesignStaticText) e, base);
				}
				continue;
			}
			JRDesignTextField textField = (JRDesignTextField) e;
			fixPDFFontEncoding(textField, base);
			if (textField.getExpression() == null
					|| textField.getExpression().getText().length() < 5) {
				continue;
			}
			String exp = textField.getExpression().getText();
			String name = getExpressionValue(exp);
			if (exp.charAt(1) == 'P') {
				if (params.containsKey(name)) {
					continue;
				}
				JRDesignParameter p = new JRDesignParameter();
				p.setName(name);
				Class<?> clazz = textField.getExpression().getValueClass();
				p.setValueClass(clazz);
				design.addParameter(p);
			} else if (exp.charAt(1) == 'F') {
				if (fields.containsKey(name)) {
					continue;
				}
				JRDesignField f = new JRDesignField();
				f.setName(name);
				Class<?> clazz = textField.getExpression().getValueClass();
				f.setValueClass(clazz);
				design.addField(f);
			}
		}
	}

	private List<JRChild> getDesignElements(JasperDesign design) {
		List<JRChild> els = new ArrayList<JRChild>();
		if (design.getBackground() != null) {
			List<JRChild> els_background = ((JRDesignBand) design
					.getBackground()).getChildren();
			els.addAll(els_background);
		}
		if (design.getTitle() != null) {
			List<JRChild> els_title = ((JRDesignBand) design.getTitle())
					.getChildren();
			els.addAll(els_title);
		}
		if (design.getPageHeader() != null) {
			List<JRChild> els_pageHeader = ((JRDesignBand) design
					.getPageHeader()).getChildren();
			els.addAll(els_pageHeader);
		}
		if (design.getColumnHeader() != null) {
			List<JRChild> els_columnHeader = ((JRDesignBand) design
					.getColumnHeader()).getChildren();
			els.addAll(els_columnHeader);
		}
		if (design.getDetailSection().getBands().length > 0
				&& design.getDetailSection().getBands()[0] != null) {
			List<JRChild> els_detail = ((JRDesignBand) design
					.getDetailSection().getBands()[0]).getChildren();
			els.addAll(els_detail);
		}
		if (design.getColumnFooter() != null) {
			List<JRChild> els_columnFooter = ((JRDesignBand) design
					.getColumnFooter()).getChildren();
			els.addAll(els_columnFooter);
		}
		if (design.getPageFooter() != null) {
			List<JRChild> els_pageFooter = ((JRDesignBand) design
					.getPageFooter()).getChildren();
			els.addAll(els_pageFooter);
		}
		if (design.getLastPageFooter() != null) {
			List<JRChild> els_lastPageFooter = ((JRDesignBand) design
					.getLastPageFooter()).getChildren();
			els.addAll(els_lastPageFooter);
		}
		if (design.getSummary() != null) {
			List<JRChild> els_summary = ((JRDesignBand) design.getSummary())
					.getChildren();
			els.addAll(els_summary);
		}
		return els;
	}

	private void fixPDFFontEncoding(JRDesignStaticText t, JRDesignStyle s) {
		t.setFontName(s.getFontName());
		t.setPdfFontName(s.getPdfFontName());
		t.setPdfEncoding(s.getPdfEncoding());
	}

	private void fixPDFFontEncoding(JRDesignTextField t, JRDesignStyle s) {
		t.setFontName(s.getFontName());
		t.setPdfFontName(s.getPdfFontName());
		t.setPdfEncoding(s.getPdfEncoding());
	}

	private String getExpressionValue(String str) {
		return str.substring(3, str.length() - 1);
	}

}
