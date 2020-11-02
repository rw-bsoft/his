/**
 * @(#)SystemCofigManageModel.java Created on 2012-2-15 下午4:00:18
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.conf;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.FixType;
import chis.source.dic.PlanStatus;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SendDictionaryReloadSynMsg;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import com.alibaba.fastjson.JSONException;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dao.support.QueryContext;
import ctd.dao.support.QueryResult;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.support.XMLDictionary;
import ctd.net.rpc.Client;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.PyConverter;
import ctd.util.exp.ExpException;
import ctd.util.xml.XMLHelper;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class SystemCofigManageModel implements BSCHISEntryNames {
	private static Logger logger = LoggerFactory
			.getLogger(SystemCofigManageModel.class);
	protected static String PLANMODE_EXPRESS = "['eq', ['s', 'planMode'], ['s', '2']]";
	protected static String PLANMODE_PLANTYPE = "15";
	protected static String VISIT_RESULT_EXPRESS = "['eq', ['s', 'visitResult'], ['s', '2']]";
	protected static String VISIT_RESULT_PLANTYPE = "02";

	// $$ 孕妇童档案参数设置配置信息
	public static final String PREGNANT_VISITINTERVALSAME = "pregnantVisitIntervalSame";

	private BaseDAO dao;

	public SystemCofigManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据文件路径获取文件对象
	 * 
	 * @param filePath
	 * @return
	 * @throws ModelDataOperationException
	 */
	public File getFile(String filePath) throws ModelDataOperationException {
		String path;
		try {
			path = BSCHISUtil.getPath(filePath);
		} catch (IOException e) {
			throw new ModelDataOperationException(e);
		}
		return new File(path);
	}

	/**
	 * 读取文件内容
	 * 
	 * @param f
	 * @param nameSpacePrefix
	 * @param nameSpaceUrl
	 * @return
	 * @throws ServiceException
	 */
	protected Document getFileDoc(File f, String nameSpacePrefix,
			String nameSpaceUrl) throws ServiceException {
		SAXReader saxReader = new SAXReader();
		if ((nameSpacePrefix != null && !nameSpacePrefix.equals(""))
				&& (nameSpaceUrl != null && !nameSpaceUrl.equals(""))) {
			Map<String, Object> nameSpaceMap = new HashMap<String, Object>();
			nameSpaceMap.put(nameSpacePrefix, nameSpaceUrl);
			DocumentFactory factory = saxReader.getDocumentFactory();
			factory.setXPathNamespaceURIs(nameSpaceMap);
		}
		Document defineDoc;
		try {
			defineDoc = saxReader.read(f);
		} catch (DocumentException e) {
			throw new ServiceException(e);
		}
		if (defineDoc == null) {
			logger.error("init failed,can't get config file from:"
					+ f.getName());
			throw new ServiceException(
					"init failed,can't get config file from:" + f.getName());
		}
		return defineDoc;

	}

	/**
	 * 修改UTIL.app中系统配置参数
	 * 
	 * @param contexName
	 *            配置参数名
	 * @param configValue
	 *            所要保存的值
	 * @throws ControllerException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	protected void saveSystemConfigData(String configName, String configValue)
			throws Exception {
		if (configName == null || configName.equals("")) {
			return;
		}
		Document doc = (Document) Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "load", new Object[]{Constants.UTIL_APP_ID,".app"});
		Document define = DocumentHelper.parseText(doc.asXML());
		Element e = (Element) define.getRootElement().selectSingleNode(
				"//properties//p[@name='" + configName + "']");
		if(e != null){
			e.setText(configValue);
		}else{
			Element properties = (Element) define.getRootElement().selectSingleNode("properties");
			e = properties.addElement("p").addAttribute("name", configName);
			e.setText(configValue);
		}
		Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "save", new Object[]{define,Constants.UTIL_APP_ID,".app"});
	}

	/**
	 * 修改UTIL.app中系统配置参数
	 * 
	 * @param configMap
	 *            配置参数集合(key:配置参数名,value:参数值}
	 * @throws ControllerException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	protected void saveSystemConfigData(Map<String, Object> configMap)
			throws Exception {
		if (configMap == null || configMap.isEmpty()) {
			return;
		}
		Document doc = (Document) Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "load", new Object[]{Constants.UTIL_APP_ID,".app"});
		Document define = DocumentHelper.parseText(doc.asXML());
		for (Iterator<String> configKey = configMap.keySet().iterator(); configKey
				.hasNext();) {
			String configName = (String) configKey.next();
			Element e = (Element) define.getRootElement().selectSingleNode(
					"//properties//p[@name='" + configName + "']");
			if(e == null){
				Element properties = (Element) define.getRootElement().selectSingleNode("properties");
				e = properties.addElement("p").addAttribute("name", configName);
			}
			e.setText(configMap.get(configName).toString());

		}
		Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "save", new Object[]{define,Constants.UTIL_APP_ID,".app"});
	}

	/**
	 * 从UTIL.app获取系统配置参数
	 * 
	 * @param configName
	 *            参数名
	 * @return 参数值
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	public String getSystemConfigData(String configName)
			throws ModelDataOperationException {
		if (configName == null || configName.equals("")) {
			return null;
		}
		Application app;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e);
		}
		String configValue = (String) app.getProperty(configName);
		return configValue;
	}

	/**
	 * 从UTIL.app中获取系统配置参数,并存放到指定对象中
	 * 
	 * @param configMap
	 *            配置参数 (key:Application.xml文件中参数名字,value:存放的参数名)
	 * @return 参数值 (key:现存参数名 ,value:参数值)
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws JSONException
	 */
	protected Map<String, Object> getSystemConfigData(
			Map<String, String> configMap) throws ModelDataOperationException {
		if (configMap == null || configMap.size() < 1) {
			return null;
		}
		Map<String, Object> object = new HashMap<String, Object>();
		Application app;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e);
		}
		for (Iterator<String> configKey = configMap.keySet().iterator(); configKey
				.hasNext();) {
			String configName = configKey.next();
			String configValue = (String) app.getProperty(configName);
			object.put(configMap.get(configName), configValue);
		}
		return object;
	}

	/**
	 * 构建表达式多个元素
	 * 
	 * @param expType
	 * @param expList
	 * @return
	 */
	protected String getExpMulti(String expType, List<String> expList) {
		int listSize = expList.size();
		if (listSize == 1) {
			return expList.get(0);
		}
		StringBuilder sb = new StringBuilder("['");
		sb.append(expType);
		sb.append("',");
		for (int i = 0; i < listSize; i++) {
			String exp = expList.get(i);
			sb.append(exp);
			if (i != expList.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 构建表达式两个元素
	 * 
	 * @param expType
	 * @param expOne
	 * @param expTwo
	 * @return
	 */
	protected String getExpDouble(String expType, String expOne, String expTwo) {
		StringBuilder sb = new StringBuilder("['");
		sb.append(expType);
		sb.append("',");
		sb.append(expOne);
		sb.append(",");
		sb.append(expTwo);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 构建表达式单个元素
	 * 
	 * @param expType
	 * @param fieldName
	 * @param fieldType
	 * @param value
	 * @return
	 */
	protected String getExpSingle(String expType, String fieldName,
			String fieldType, String value) {
		StringBuilder sb = new StringBuilder("['");
		sb.append(expType);
		sb.append("',['");
		sb.append(fieldType);
		sb.append("','");
		sb.append(fieldName);
		sb.append("'],['");
		sb.append(fieldType);
		sb.append("','");
		sb.append(value);
		sb.append("']]");
		return sb.toString();
	}

	protected void changeSchema(String planMode, String schema)
			throws Exception {
		// 更新随访schema
		Document doc = (Document) Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "load", new Object[]{schema,".sc"});
		Document define = DocumentHelper.parseText(doc.asXML());
		Element visitDate = (Element) define.getRootElement().selectSingleNode(
				"//entry/item[@id='nextDate']");
		if (planMode.equals("2")) {
			visitDate.addAttribute("not-null", "1");
		} else {
			visitDate.addAttribute("not-null", "0");
		}
		 Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "save", new Object[]{doc,schema,".sc"});
		SchemaController.instance().reload(schema);
	}

	/**
	 * 构造结果数据为前台initFormData()支持的数据
	 * 
	 * @param result
	 * @param sc
	 * @return
	 * @throws ModelDataOperationException
	 * @throws JSONException
	 */
	protected Map<String, Object> makeFormResBody(Map<String, Object> result,
			String entryName) throws ModelDataOperationException {
		Schema sc;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e);
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		for (String key : result.keySet()) {
			if (key == null || key.equals("")) {
				continue;
			}
			Object value = result.get(key);
			if (value == null) {
				continue;
			}
			SchemaItem si = sc.getItem(key);
			if (si.isCodedValue()) {
				Map<String, Object> v = new HashMap<String, Object>();
				v.put("key", value);
				v.put("text", si.toDisplayValue(value));
				resBody.put(key, v);
			} else {
				resBody.put(key, result.get(key));
			}
		}
		return resBody;
	}

	/**
	 * 构造结果数据为前台list()支持的数据
	 * 
	 * @param result
	 * @param sc
	 * @return
	 * @throws ModelDataOperationException
	 * @throws JSONException
	 */
	protected Map<String, Object> makeListResBody(Map<String, Object> result,
			String entryName) throws ModelDataOperationException {
		Schema sc;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e);
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		for (String key : result.keySet()) {
			if (key == null || key.equals("")) {
				continue;
			}
			Object value = result.get(key);
			if (value == null) {
				continue;
			}
			SchemaItem si = sc.getItem(key);
			if (si.isCodedValue()) {
				resBody.put(key, result.get(key));
				resBody.put(key + "_text", si.toDisplayValue(value));
			} else {
				resBody.put(key, result.get(key));
			}
		}
		return resBody;
	}

	/**
	 * 更新manaunit.xml字典文件
	 * 
	 * @param json
	 * @param manageRule
	 * @param path
	 * @param typePath
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws IOException
	 */
	protected void createRegionBase(Map<String, Object> json,
			String manageRule, String path, String typePath)
			throws ModelDataOperationException, ServiceException, IOException {
		File file = this.getFile(path);
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("dic");
		root.addAttribute("manageRule", manageRule);
		root.addAttribute("searchKey", "pyCode");
		root.addAttribute("lastModi", (new Date()).toString());
		root.addAttribute("class", "XMLDictionary");
		root.addAttribute("filename", file.getAbsolutePath());
		// 省
		String province = StringUtils
				.trimToEmpty((String) json.get("province"));
		if (province != null && !province.equals("")) {
			String region = StringUtils
					.trimToEmpty((String) json.get("region"));
			String city = StringUtils.trimToEmpty((String) json.get("city"));
			if (region.length() != 0) {
				Element re = root.addElement("item");
				String type = getTypeByRegion(region, manageRule);
				String regiontext = StringUtils.trimToEmpty((String) json
						.get("region_text"));
				re.addAttribute("key", region);
				re.addAttribute("text", regiontext + "卫生局");
				re.addAttribute("type", type);
				re.addAttribute("pyCode",
						PyConverter.getFirstLetter(regiontext + "卫生局"));
				try {
					addSystemToType(type, typePath);
				} catch (ControllerException e) {
					throw new ServiceException(e);
				}
				if (region.length() == 2) {
					re.addAttribute("isMunicipality", Boolean.TRUE.toString());
				} else {
					re.addAttribute("isMunicipality", Boolean.FALSE.toString());
				}
			} else if (city != null && city.length() > 0) {
				Element cy = root.addElement("item");
				String text = StringUtils.trimToEmpty((String) json
						.get("city_text"));
				cy.addAttribute("key", city);
				cy.addAttribute("text", text + "卫生局");
				cy.addAttribute("type", "b");
				cy.addAttribute("pyCode",
						PyConverter.getFirstLetter(text + "卫生局"));
				// 市辖区
				List<Map<String, Object>> list = getChildren(city, 6l);
				for (Map<String, Object> map : list) {
					Element el = cy.addElement("item");
					el.addAttribute("key", (String) map.get("regionCode"));
					el.addAttribute("type", "c");
					el.addAttribute("text", map.get("regionName") + "卫生局");
					el.addAttribute(
							"pyCode",
							PyConverter.getFirstLetter(map.get("regionName")
									+ "卫生局"));
				}
			} else {// 设置到省的情况
				Element cy = root.addElement("item");
				String text = StringUtils.trimToEmpty((String) json
						.get("province_text"));
				cy.addAttribute("key", province);
				cy.addAttribute("text", text + "卫生厅");
				cy.addAttribute("type", "a");
				cy.addAttribute("pyCode",
						PyConverter.getFirstLetter(text + "卫生厅"));
				// 市辖区
				List<Map<String, Object>> list = getChildren(province, 4l);
				for (Map<String, Object> map : list) {
					Element el = cy.addElement("item");
					String regionCode = (String) map.get("regionCode");
					el.addAttribute("key", regionCode);
					el.addAttribute("type", "b");
					el.addAttribute("text", map.get("regionName") + "卫生局");
					el.addAttribute(
							"pyCode",
							PyConverter.getFirstLetter(map.get("regionName")
									+ "卫生局"));
					List<Map<String, Object>> regionList = getChildren(
							regionCode, 6l);
					// 区县
					for (Map<String, Object> regionMap : regionList) {
						Element regionEl = el.addElement("item");
						regionEl.addAttribute("key",
								(String) regionMap.get("regionCode"));
						regionEl.addAttribute("type", "c");
						regionEl.addAttribute("text",
								regionMap.get("regionName") + "卫生局");
						regionEl.addAttribute(
								"pyCode",
								PyConverter.getFirstLetter(regionMap
										.get("regionName") + "卫生局"));
					}
				}
			}
		}
		XMLHelper.putDocument(file, doc);
		DictionaryController.instance().reload("chis.@manageUnit");
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.@manageUnit");
	}

	/**
	 * 查询子节点
	 * 
	 * @param parent
	 * @param childLength
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getChildren(String parent,
			Long childLength) throws ModelDataOperationException {
		StringBuffer cnd = new StringBuffer(
				"['like',['$','regionCode'],['concat',['s','").append(
						parent).append("'],['s','%']]]");
		cnd.insert(0, "['and',")
		.append(",['eq',['len','regioncode'],['d','").append(childLength)
		.append("']]]");
		try {
			HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
			Object[] paras = new Object[] { "area",
					CNDHelper.toListCnd(cnd.toString()), new QueryContext() };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(AppContextHolder.getConfigServiceId("daoService"), "find", paras,header);
			return qr.getItems();
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC查询网格地址子节点失败", e);
		}
	}

	private void addSystemToType(String type, String typePath)
			throws ModelDataOperationException, IOException,
			ControllerException {
		File typeFile = getFile(typePath);
		XMLDictionary dic = (XMLDictionary) DictionaryController.instance()
				.get("unitType");
		Document unitType = dic.getDefineDoc();
		Element root = (Element) unitType.selectSingleNode("//dic");
		Iterator<?> items = root.elements().iterator();
		while (items.hasNext()) {
			Element it = (Element) items.next();
			Attribute key = it.attribute("key");
			Attribute roles = it.attribute("roles");
			if (key.getText().equals(type)
					&& roles.getText().indexOf("system") == -1) {
				roles.setText(roles.getText() + ",system");
			}
		}
		XMLHelper.putDocument(typeFile, unitType);
		DictionaryController.instance().reload("unitType");
		SendDictionaryReloadSynMsg.instance().sendSynMsg("unitType");
	}

	private String getTypeByRegion(String region, String manageRule) {
		String[] rules = manageRule.split(",");
		int len = 0;
		int i = 2;
		for (String s : rules) {
			int r = Integer.parseInt(s);
			len += r;
			if (region.length() == len) {
				char type = (char) (96 + i);
				return String.valueOf(type);
			}
			i++;
		}
		return "a";
	}

	/**
	 * 儿童首次随访天数修改后处理计划业务数据
	 * 
	 * @param formBody
	 * @param session
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void updatebChildrenBusinessData(Map<String, Object> formBody,
			String config) throws ModelDataOperationException {
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(e1);
		}
		String perFirst = (String) app.getProperty(config);
		if (perFirst != null && !perFirst.equals("")) {
			int firstDay = Integer.parseInt(perFirst);
			int nowFirst = (Integer) formBody.get(config);
			if (firstDay != nowFirst) {
				int btwDay = nowFirst - firstDay;
				String hql = new StringBuilder(" update ")
						.append(PUB_VisitPlan)
						.append(" set beginDate = sum_day(beginDate+ ")
						.append(btwDay)
						.append(") ,endDate = sum_day(endDate+ ")
						.append(btwDay)
						.append(") ,beginVisitDate = sum_day(beginVisitDate+ ")
						.append(btwDay)
						.append(") ,planDate = sum_day(planDate+ ")
						.append(btwDay)
						.append(") where sn=1 and planStatus= :planStatus and ")
						.append("(businessType=:plantype1 or businessType= :plantype2)")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("planStatus", PlanStatus.NEED_VISIT);
				parameters.put("plantype1", BusinessType.CD_IQ);
				parameters.put("plantype2", BusinessType.CD_CU);
				try {
					dao.doUpdate(hql, parameters);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException("修改儿童业务数据失败！", e);
				}
			}
		}
	}

	/**
	 * 判断随访配置是否改变(businessType 随访类型)
	 * 
	 * @param formBody
	 * @param jsonRes
	 * @param session
	 * @param sc
	 * @param ctx
	 * @return
	 * @throws JSONException
	 * @throws ServiceException
	 */
	public Map<String, Boolean> checkConfigUpdate(Map<String, Object> formBody,
			String entryName, String businessType)
			throws ModelDataOperationException {
		Map<String, Boolean> planTypeChanged = null;
		Schema sc = null;
		VisitPlanModel vpm = new VisitPlanModel(dao);
		List<PlanInstance> preList;
		try {
			sc = SchemaController.instance().get(entryName);
			preList = vpm.getPlanInstanceExpressions(businessType,
					dao.getContext());
		} catch (Exception e) {
			throw new ModelDataOperationException(
					"Failed to check config update", e);
		}
		for (PlanInstance map : preList) {
			String exp = map.getExpression();
			String preCode = map.getPlanTypeCode();
			String nowCode = null;
			String groupCode = null;
			List<SchemaItem> items = sc.getItems();
			for (SchemaItem si : items) {
				String name = si.getId();
				if (name.startsWith("planType")) {
					String express = si.getProperty("expression").toString()
							.replace("'", "\"");
					if (exp.equalsIgnoreCase(express)) {
						nowCode = (String) formBody.get(name);
						List<?> cnd;
						try {
							cnd = CNDHelper.toListCnd(exp);
							groupCode = (String) ((List<?>) ((List<?>) cnd
									.get(1)).get(2)).get(1);
						} catch (ExpException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
			if (!preCode.equals(nowCode)) {
				if (planTypeChanged == null) {
					planTypeChanged = new HashMap<String, Boolean>();
				}
				if (groupCode != null) {
					planTypeChanged.put(groupCode, true);
				}
				continue;
			}
		}
		return planTypeChanged;
	}

	/**
	 * 判断用户提交计划生成方式与正在使用的是否相同，不同则需处理数据
	 * 
	 * @param planTypeChanged
	 * @param data
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void updatebDiabetesBusinessData(
			Map<String, Boolean> planTypeChanged, Map<String, Object> data,
			VisitPlanCreator vpc) throws ModelDataOperationException {
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(e1);
		}
		String preMode = (String) app.getProperty(BusinessType.TNB
				+ "_planMode");
		String nowMode = (String) data.get("planMode");
		Boolean change = nowMode.equals(preMode);
		for (Iterator<String> iterator = planTypeChanged.keySet().iterator(); iterator
				.hasNext();) {
			String groupCode = iterator.next();
			StringBuilder qPlan = new StringBuilder(
					"select phrId as recordId ,diabetesGroup as groupCode,")
					.append("empiId as empiId, fixDate as fixGroupDate from ")
					.append(MDC_DiabetesFixGroup)
					.append(" where fixId in (select max(fixId) from ")
					.append(MDC_DiabetesFixGroup)
					.append(" where diabetesGroup != '99' and diabetesGroup = :groupCode  group by phrId )");
			Map<String, Object> parm = new HashMap<String, Object>();
			parm.put("groupCode", groupCode);
			List<Map<String, Object>> result = null;
			try {
				result = dao.doQuery(qPlan.toString(), parm);
				if (result == null || result.size() < 1) {
					continue;
				}

				for (Map<String, Object> planRecord : result) {
					String recordId = planRecord.get("recordId").toString();
					int groupNum = Integer.parseInt(groupCode);
					StringBuilder sb = new StringBuilder("update ")
							.append(MDC_DiabetesRecord);
					sb.append(" set planTypeCode = :planTypeCode where phrId = :phrId");
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("planTypeCode", data.get("planType" + groupNum));
					param.put("phrId", recordId);
					dao.doUpdate(sb.toString(), param);
					if (change) { // plan
						Map<String, Object> body = new HashMap<String, Object>(
								planRecord);
						body.put("businessType", BusinessType.TNB);
						body.put("fixType", FixType.NON_FIX_DATE);
						Date date = new Date();
						Calendar c = Calendar.getInstance();
						c.setTime(date);
						c.set(Calendar.DAY_OF_MONTH, 1);
						body.put("lastVisitDate",
								BSCHISUtil.toString(c.getTime(), null));
						body.put("$deleteUndoPlan", false);
						vpc.create(body, dao.getContext());
					}
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * 保存糖尿病计划方案数据
	 * 
	 * @param body
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	public void saveDiabetesPlanInstance(Map<String, Object> body)
			throws ModelDataOperationException {
		String planMode = (String) body.get("planMode");
		String express = VISIT_RESULT_EXPRESS;
		String planType = VISIT_RESULT_PLANTYPE;
		try {
			Schema sc = SchemaController.instance().get(ADMIN_DiabetesConfig);
			if (planMode.equals("1")) {
				List<SchemaItem> items = sc.getItems();
				for (SchemaItem si : items) {
					String name = si.getId();
					if (name.startsWith("planType")
							&& !name.equals("planTypeCode")) {
						if (!(StringUtils.trimToEmpty((String) body.get(si
								.getId()))).equals("")) {
							Map<String, Object> saveBody = new HashMap<String, Object>();
							saveBody.put("instanceType", BusinessType.TNB);
							saveBody.put("expression",
									si.getProperty("expression"));
							saveBody.put("planTypeCode", body.get(si.getId()));
							dao.doSave("create", PUB_PlanInstance, saveBody,
									true);
						}
					}
				}
			} else if (planMode.equals("2")) {
				express = PLANMODE_EXPRESS;
				planType = PLANMODE_PLANTYPE;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("instanceType", BusinessType.TNB);
			map.put("expression", express);
			map.put("planTypeCode", planType);
			dao.doSave("create", PUB_PlanInstance, map, true);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"save Diabetes PlanInstance failed", e);
		}
	}
	
	/**
	 * 保存糖尿病高危人群计划方案数据
	 * 
	 * @param body
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	public void saveDiabetesRiskPlanInstance(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			Schema sc = SchemaController.instance().get(ADMIN_DiabetesRiskConfig);
			List<SchemaItem> items = sc.getItems();
			for (SchemaItem si : items) {
				String name = si.getId();
				if (name.startsWith("planType")
						&& !name.equals("planTypeCode")) {
					if (!(StringUtils.trimToEmpty((String) body.get(si
							.getId()))).equals("")) {
						Map<String, Object> saveBody = new HashMap<String, Object>();
						saveBody.put("instanceType", BusinessType.DiabetesRisk);
						saveBody.put("planTypeCode", body.get(si.getId()));
						saveBody.put("expression", "['eq', ['s', 'visitResult'], ['s', '1']]");
						dao.doSave("create", PUB_PlanInstance, saveBody,
								false);
					}
				}
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"save Diabetes PlanInstance failed", e);
		}
	}
	
	/**
	 * 保存糖尿病高危人群计划方案数据
	 * 
	 * @param body
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	public void saveHypertensionRiskPlanInstance(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			Schema sc = SchemaController.instance().get(ADMIN_HypertensionRiskConfig);
			List<SchemaItem> items = sc.getItems();
			for (SchemaItem si : items) {
				String name = si.getId();
				if (name.startsWith("planType")
						&& !name.equals("planTypeCode")) {
					if (!(StringUtils.trimToEmpty((String) body.get(si
							.getId()))).equals("")) {
						Map<String, Object> saveBody = new HashMap<String, Object>();
						saveBody.put("instanceType", BusinessType.HypertensionRisk);
						saveBody.put("planTypeCode", body.get(si.getId()));
						saveBody.put("expression", "['eq', ['s', 'visitResult'], ['s', '1']]");
						dao.doSave("create", PUB_PlanInstance, saveBody,
								false);
					}
				}
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"save Diabetes PlanInstance failed", e);
		}
	}

	/**
	 * 判断用户提交计划生成方式与正在使用的是否相同，不同则需处理数据
	 * 
	 * @param planTypeChanged
	 * @param formBody
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	public void updatebHypertensionBusinessData(
			Map<String, Boolean> planTypeChanged, Map<String, Object> formBody,
			VisitPlanCreator vpc) throws ServiceException,
			ModelDataOperationException {
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(e1);
		}
		String preMode = (String) app.getProperty(BusinessType.GXY
				+ "_planMode");
		String nowMode = (String) formBody.get("planMode");
		Boolean change = nowMode.equals(preMode);
		for (Iterator<String> iterator = planTypeChanged.keySet().iterator(); iterator
				.hasNext();) {
			String groupCode = iterator.next();
			StringBuilder qPlan = new StringBuilder(
					"select phrId as recordId ,hypertensionGroup as groupCode,");
			qPlan.append("empiId as empiId, fixDate as fixGroupDate from ");
			qPlan.append(MDC_HypertensionFixGroup);
			qPlan.append(" where fixId in (select max(fixId) from ");
			qPlan.append(MDC_HypertensionFixGroup)
					.append(" where hypertensionGroup != '99' and hypertensionGroup = :groupCode  group by phrId )");
			Map<String, Object> parm = new HashMap<String, Object>();
			parm.put("groupCode", groupCode);
			List<Map<String, Object>> result = null;
			try {
				result = dao.doQuery(qPlan.toString(), parm);

				if (result == null || result.size() < 1) {
					continue;
				}

				for (Map<String, Object> planRecord : result) {
					String recordId = planRecord.get("recordId").toString();
					int groupNum = Integer.parseInt(groupCode);
					StringBuilder sb = new StringBuilder("update ")
							.append(MDC_HypertensionRecord);
					sb.append(" set planTypeCode = :planTypeCode where phrId = :phrId");
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("planTypeCode",
							formBody.get("planType" + groupNum));
					param.put("phrId", recordId);
					dao.doUpdate(sb.toString(), param);
					if (change) {
						HashMap<String, Object> body = new HashMap<String, Object>(
								planRecord);
						body.put("instanceType", BusinessType.GXY);
						body.put("fixType", FixType.CREATE);
						Date date = new Date();
						Calendar c = Calendar.getInstance();
						c.setTime(date);
						c.set(Calendar.DAY_OF_MONTH, 1);
						body.put("lastVisitDate",
								BSCHISUtil.toString(c.getTime(), null));
						body.put("$deleteUndoPlan", false);
						vpc.create(body, dao.getContext());
					}
				}
			} catch (Exception e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,
						"Failed to update Hypertension Business Data", e);
			}
		}
	}

	/**
	 * 保存高血压计划方案配置信息
	 * 
	 * @param listBody
	 * @param jsonRes
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws JSONException
	 * @throws ServiceException
	 */
	public void saveFixGroupDictionary(List<Map<String, Object>> listBody,
			String fixGroupType) throws ModelDataOperationException {
		for (int i = 0; i < listBody.size(); i++) {
			Map<String, Object> groupData = listBody.get(i);
			groupData.put("dicType", fixGroupType);
			try {
				dao.doSave("update", MDC_EstimateDictionary, groupData, true);
			} catch (Exception e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "保存高血压计划方案配置信息失败！", e);
			}
		}
	}

	/**
	 * @param formBody
	 * @param otherPlanTypeCode
	 * @param otherExpression
	 * @throws ModelDataOperationException
	 */
	public void saveHypertensionPlanInstance(Map<String, Object> formBody,
			String otherPlanTypeCode, String otherExpression)
			throws ModelDataOperationException {
		String planMode = (String) formBody.get("planMode");
		String express = VISIT_RESULT_EXPRESS;
		String planType = VISIT_RESULT_PLANTYPE;
		try {
			Schema sc = SchemaController.instance().get(
					ADMIN_HypertensionConfig);
			if (planMode.equals("1")) {
				List<SchemaItem> items = sc.getItems();
				for (SchemaItem si : items) {
					String name = si.getId();
					if (name.startsWith("planType")) {
						Map<String, Object> saveBody = new HashMap<String, Object>();
						saveBody.put("instanceType", BusinessType.GXY);
						saveBody.put("expression", si.getProperty("expression"));
						saveBody.put("planTypeCode", formBody.get(si.getId()));
						dao.doSave("create", PUB_PlanInstance, saveBody, true);
					}
				}
			} else if (planMode.equals("2")) {
				express = PLANMODE_EXPRESS;
				planType = PLANMODE_PLANTYPE;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("instanceType", BusinessType.GXY);
			map.put("expression", express);
			map.put("planTypeCode", planType);
			dao.doSave("create", PUB_PlanInstance, map, true);
			Application app = null;
			try {
				app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			} catch (ControllerException e1) {
				throw new ModelDataOperationException(e1);
			}
			String otherCode = (String) app.getProperty(otherPlanTypeCode);
			if (otherCode == null || otherCode.equals("")) {
				return;
			}
			Map<String, Object> otherBody = new HashMap<String, Object>();
			otherBody.put("instanceType", BusinessType.GXY);
			otherBody.put("expression", otherExpression);
			otherBody.put("planTypeCode", otherCode);
			dao.doSave("create", PUB_PlanInstance, otherBody, true);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"Failed to insert Hypertension plan instance records", e);
		}
	}

	public List<Map<String, Object>> getPlanInstanceForOld(String businessType,
			Map<String, Object> condition) throws ModelDataOperationException {
		String hql = new StringBuilder("select planTypeCode as planTypeCode, ")
				.append("expression as expression from ")
				.append(PUB_PlanInstance)
				.append(" where instanceType=:instanceType and planTypeCode<>'15'")
				.toString();
		Map<String, Object> map = new HashMap<String, Object>();
		if (condition != null) {
			map.putAll(condition);
		}
		map.put("instanceType", businessType);
		List<Map<String, Object>> res = null;
		try {
			res = dao.doQuery(hql, map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"Cannot get plan instance exceptions.", e);
		}
		return res;
	}

}
