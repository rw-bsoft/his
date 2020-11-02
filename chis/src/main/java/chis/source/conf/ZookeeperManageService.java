/**
 * @(#)ZookeeperManageService.java Created on 2012-7-18 上午09:17:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.conf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import ctd.app.ApplicationController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class ZookeeperManageService extends AbstractActionService implements
		DAOSupportable {
	private static Log logger = LogFactory.getLog(ZookeeperManageService.class);

	@SuppressWarnings({ "unchecked" })
	protected void doSaveZkConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		if (body == null) {
			logger.error("body  is missed!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
		String ifNeedPix = (String) body.get("ifNeedPix");
		String phisActiveYW = (String) body.get("phisActiveYW");
		String KLX = (String) body.get("KLX");
		try {
			if ("y".equals(ifNeedPix)) {
				String beforeData = scmm.getSystemConfigData("ifNeedPix");
				if (!"true".equals(beforeData)) {
					scmm.saveSystemConfigData("ifNeedPix", "true");
					modifySpringConfig(true);
				}
			} else {
				String beforeData = scmm.getSystemConfigData("ifNeedPix");
				if (!"false".equals(beforeData)) {
					scmm.saveSystemConfigData("ifNeedPix", "false");
					modifySpringConfig(false);
				}
			}
			if ("y".equals(phisActiveYW)) {
				scmm.saveSystemConfigData("phisActiveYW", "true");
			} else {
				scmm.saveSystemConfigData("phisActiveYW", "false");
			}
			scmm.saveSystemConfigData("KLX", KLX);
			ApplicationController.instance().reload(Constants.UTIL_APP_ID);
		} catch (Exception e) {
			res.put(RES_CODE, ServiceCode.CODE_IO_EXCEPTION);
			res.put(RES_MESSAGE, "spring.xml配置文件解析失败。");
			return;
		}
	}

	@SuppressWarnings("rawtypes")
	private void modifySpringConfig(boolean ifNeedPix)
			throws DocumentException, IOException {
		String url = BSCHISUtil.getPath("spring/spring.xml");
		SAXReader reader = new SAXReader();
		Document document;
		File file = new File(url);
		document = reader.read(file);
		List beans = document.selectNodes("/beans");
		Iterator it1 = beans.iterator();
		if (ifNeedPix) {
			Element bean = (Element) it1.next();
			Element imp = bean.addElement("import");
			imp.addAttribute("resource", "spring-mpi.xml");
		} else {
			while (it1.hasNext()) {
				Element bean = (Element) it1.next();
				Iterator imports = bean.elementIterator("import");
				while (imports.hasNext()) {
					Element imp = (Element) imports.next();
					Attribute attribute = imp.attribute("resource");
					String value = attribute.getValue();
					if ("spring-mpi.xml".equals(value)) {
						bean.remove(imp);
						break;
					}
				}
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		XMLWriter writer;
		writer = new XMLWriter(new FileWriter(file), format);
		writer.write(document);
		writer.close();
	}

	@SuppressWarnings("unchecked")
	public void doTestConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String zkAddress = (String) body.get("zkAddress");
		int zkPort = (Integer) body.get("zkPort");
		try {
			Socket client = new Socket(zkAddress, zkPort);
			client.close();
		} catch (Exception e) {
			throw new ServiceException("连接失败。");
		}
	}

	protected void doGetZkConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		try {
			Map<String, Object> body = new HashMap<String, Object>();
			SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
			String ifNeedPix = scmm.getSystemConfigData("ifNeedPix");
			String phisActiveYW = scmm.getSystemConfigData("phisActiveYW");
			String KLX = scmm.getSystemConfigData("KLX");
			if ("true".equals(ifNeedPix)) {
				body.put("ifNeedPix", "y");
			} else {
				body.put("ifNeedPix", "n");
			}
			if ("true".equals(phisActiveYW)) {
				body.put("phisActiveYW", "y");
			} else {
				body.put("phisActiveYW", "n");
			}
			body.put("KLX", KLX);
			body = SchemaUtil.setDictionaryMessageForForm(body,
					SYS_ZookeeperConfig);
			res.put("body", body);
		} catch (Exception e) {
			e.printStackTrace();
			res.put(RES_CODE, ServiceCode.CODE_IO_EXCEPTION);
			res.put(RES_MESSAGE, "配置文件解析失败。");
			return;
		}

	}

}
