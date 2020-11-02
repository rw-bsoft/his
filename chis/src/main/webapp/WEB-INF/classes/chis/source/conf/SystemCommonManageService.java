/**
 * @(#)SystemConfigManage.java Created on 2011-2-15 上午09:49:59
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.conf;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.SendDictionaryReloadSynMsg;

import com.alibaba.fastjson.JSONException;

import ctd.account.user.UserController;
import ctd.dictionary.DictionaryController;
import ctd.net.rpc.Client;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * 
 * @description：公共设置服务
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */
public class SystemCommonManageService extends AbstractActionService implements
		DAOSupportable {
	private static Logger logger = LoggerFactory
			.getLogger(SystemCommonManageService.class);
	private static final String PRE_XPATH = "/dic/@rootKey";

	// private static final String REGION_XPATH =
	// "chis/dictionary/manageUnit.xml";
	// private static final String TYPE_PATH = "chis/dictionary/unitType.xml";

	/**
	 * 保存公共设置
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param ctx
	 * @throws URISyntaxException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public void doSaveConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		if (body == null) {
			logger.error("body  is missed!");
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "数据不能为空！");
			return;
		}
		// 更新areaGrid
		String province = StringUtils
				.trimToEmpty((String) body.get("province"));
		String city = StringUtils.trimToEmpty((String) body.get("city"));
		String region = StringUtils.trimToEmpty((String) body.get("region"));
		String root = "";
		String root_text = "";
		if (region != null && !region.equals("")) {
			root = region;
			root_text = "region_text";
		} else if (city != null && !city.equals("")) {
			root = city;
			root_text = "city_text";
		} else if (province != null && !province.equals("")) {
			root = province;
			root_text = "province_text";
		}
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		Document areaDoc = null;
		Element dic = null;
		Document doc;
		try {
			doc = (Document) Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "load", new Object[]{"chis.dictionary.areaGrid",".dic"});
			areaDoc = DocumentHelper.parseText(doc.asXML());
		} catch (Exception e1) {
			logger.error("Failed to load chis.dictionary.areaGrid.", e1);
			throw new ServiceException("Failed to load chis.dictionary.areaGrid.",e1);
		}
		dic = (Element) areaDoc.getRootElement().selectSingleNode("//dic");

		Attribute rootKey = dic.attribute("rootKey");
		Attribute codeRule = dic.attribute("codeRule");
		String coderule = null;
		String areaRule = null;
		String manageRule = null;
		Boolean zxs = false;
		String pre = null;
		pre = root.substring(0, 2);
		if (pre.equals("11") || pre.equals("12") || pre.equals("31")
				|| pre.equals("50"))
			zxs = true;
		// 直辖市处理
		if (zxs) {
			coderule = "2,4,3,3,3,3,3,3";
			areaRule = "[2,6, 9, 12, 15, 18, 21, 24]";
			manageRule = "2,4,3,3";
		} else {
			coderule = "4,2,3,3,3,3,3,3";
			areaRule = "[2,4,6, 9, 12, 15, 18, 21, 24]";
			manageRule = "4,2,3,3";
			// 设置到省的情况
			if (root.length() == 2) {
				coderule = "2,2,2,3,3,3,3,3,3";
				manageRule = "2,2,2,3,3";
			}
		}

		// 设置到县
		if (root.length() == 6) {
			coderule = "6,3,3,3,3,3,3";
			// manageRule = "6,3,3";
		}
		// 设置codeRule
		if (codeRule != null)
			codeRule.setText(coderule);
		else {
			dic.addAttribute("codeRule", coderule);
		}
		// 设置rootKey
		if (rootKey != null)
			rootKey.setText(root);
		else {
			dic.addAttribute("rootKey", root);
		}
		try {
			Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "save", new Object[]{areaDoc,"chis.dictionary.areaGrid",".dic"});
		} catch (Exception e1) {
			logger.error("Failed to save chis.dictionary.areaGrid.", e1);
			throw new ServiceException("Failed to save chis.dictionary.areaGrid.",e1);
		}
		// 重新加载areaGrid字典
		DictionaryController.instance().reload("chis.dictionary.areaGrid");
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid");
		// ** 更新家庭档案网格地址
		Document areaDoc_fam = null;
		Element dic_fam = null;
		Document doc_fam;
		try {
			doc_fam = (Document) Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "load", new Object[]{"chis.dictionary.areaGrid_family",".dic"});
			areaDoc_fam = DocumentHelper.parseText(doc_fam.asXML());
		} catch (Exception e1) {
			logger.error("Failed to load chis.dictionary.areaGrid_family.", e1);
			throw new ServiceException("Failed to load chis.dictionary.areaGrid_family.",e1);
		}
		dic_fam = (Element) areaDoc_fam.getRootElement().selectSingleNode(
				"//dic");
		Attribute rootKey_fam = dic_fam.attribute("rootKey");
		Attribute codeRule_fam = dic_fam.attribute("codeRule");
		// 设置codeRule
		if (codeRule_fam != null) {
			codeRule_fam.setText(coderule);
		} else {
			dic_fam.addAttribute("codeRule", coderule);
		}
		// 设置rootKey
		if (rootKey_fam != null) {
			rootKey_fam.setText(root);
		} else {
			dic_fam.addAttribute("rootKey", root);
		}
		try {
			Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "save", new Object[]{areaDoc_fam,"chis.dictionary.areaGrid_family",".dic"});
		} catch (Exception e1) {
			logger.error("Failed to save chis.dictionary.areaGrid_family.", e1);
			throw new ServiceException("Failed to save chis.dictionary.areaGrid_family.",e1);
		}
		// 重新加载areaGrid字典
		DictionaryController.instance().reload("chis.dictionary.areaGrid_family");
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid_family");
		// ** 更新网格地址（组）

		Document areaDoc_gro = null;
		Element dic_gro = null;
		Document doc_gro;
		try {
			doc_gro = (Document) Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "load", new Object[]{"chis.dictionary.areaGrid_group",".dic"});
			areaDoc_gro = DocumentHelper.parseText(doc_gro.asXML());
		} catch (Exception e1) {
			logger.error("Failed to load chis.dictionary.areaGrid_group.", e1);
			throw new ServiceException("Failed to load chis.dictionary.areaGrid_group.",e1);
		}
		dic_gro = (Element) areaDoc_fam.getRootElement().selectSingleNode(
				"//dic");
		Attribute rootKey_gro = dic_gro.attribute("rootKey");
		Attribute codeRule_gro = dic_gro.attribute("codeRule");
		// 设置codeRule
		if (codeRule_gro != null) {
			codeRule_gro.setText(coderule);
		} else {
			dic_gro.addAttribute("codeRule", coderule);
		}
		// 设置rootKey
		if (rootKey_gro != null) {
			rootKey_gro.setText(root);
		} else {
			dic_gro.addAttribute("rootKey", root);
		}
		try {
			Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "save", new Object[]{areaDoc_gro,"chis.dictionary.areaGrid_group",".dic"});
		} catch (Exception e1) {
			logger.error("Failed to save chis.dictionary.areaGrid_group.", e1);
			throw new ServiceException("Failed to save chis.dictionary.areaGrid_group.",e1);
		}
		// 重新加载areaGrid字典
		DictionaryController.instance().reload("chis.dictionary.areaGrid_group");
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid_group");
		try {
//			 smm.createRegionBase(body, manageRule, REGION_XPATH, TYPE_PATH);
			// // ** 重载管理单元字典
			// DictionaryController.instance().reload("chis.@manageUnit");
			// chb 重载与用户相关的数据字典
			DictionaryController.instance().reload("chis.dictionary.user");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.user");
			DictionaryController.instance().reload("chis.dictionary.user01");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.user01");
			DictionaryController.instance().reload("chis.dictionary.user02");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.user02");
			DictionaryController.instance().reload("chis.dictionary.user03");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.user03");
			DictionaryController.instance().reload("chis.dictionary.user04");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.user04");
			DictionaryController.instance().reload("chis.dictionary.user14");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.user14");
			DictionaryController.instance().reload("chis.dictionary.user16");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.user16");
		} catch (Exception e) {
			logger.error("Failed to create region base of dictionary manaUnit!");
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "创建管辖机构基础字典失败！");
			throw new ServiceException(e);
		}

		// 更新system用户 相关字段
		UserController ctl = UserController.instance();
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("manageUnitId", root);
			map.put("regionCode", root);
			String cnds = "['and',['eq',['$','userId'],['s','system']],['eq',['$','roleId'],['s','chis.system']]]";
			Client.rpcInvoke(AppContextHolder.getConfigServiceId("userLoader"), "updateUser", new Object[] {
					map, CNDHelper.toListCnd(cnds.toString())});
		} catch (Exception e) {
			logger.error("Failed to change system in database!");
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "更新system用户相关字段失败！");
			throw new ServiceException(e);
		}
		ctl.reload("system");
		res.put("body", body);
	}

	/**
	 * 获得配置信息
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 * @throws JSONException
	 */
	public void doGetConfig(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		Document doc = null;
		try {
			doc = (Document) Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "load", new Object[]{"chis.dictionary.areaGrid",".dic"});
		} catch (Exception e) {
			logger.error("Failed to load chis.dictionary.areaGrid.", e);
			throw new ServiceException("Failed to load chis.dictionary.areaGrid.",e);
		}
		if(doc != null){
			Attribute rootKey = (Attribute) doc.getRootElement().selectSingleNode(PRE_XPATH);
			if (rootKey != null) {
				String value = rootKey.getText();
				
				Map<String, Object> body = new HashMap<String, Object>();
				switch (value.length()) {
				case 6:
					body.put("region", value);
				case 4:
					body.put("city", value.substring(0, 4));
				case 2:
					body.put("province", value.substring(0, 2));
				}
				// 对直辖市特殊处理
				String pre = value.substring(0, 2);
				Map<String, Object> retbody = SchemaUtil
						.setDictionaryMessageForForm(body, SYS_CommonConfig);
				if (pre.equals("50") || pre.equals("11") || pre.equals("12")
						|| pre.equals("31")) {
					retbody.put("city", retbody.get("province"));
				}
				res.put("body", retbody);
			}
		}
	}
	/**
	 * 获得配置参数
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void doGetSystemConfigValue(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx){ 
		String reqValue=  req.get("body")+"";
		String resValue="";
		try {
			SystemCofigManageModel sys=new SystemCofigManageModel(null);
			resValue=sys.getSystemConfigData(reqValue);
			res.put("body", resValue);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void doGetSystemConfigValues(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx){ 
		String reqValues =  req.get("body")+"";
		String[] valueList = reqValues.split(",");
		Map<String, Object> resValue= new HashMap<String, Object>();
		try {
			SystemCofigManageModel sys=new SystemCofigManageModel(null);
			for (int i = 0; i < valueList.length; i++) {
				String key = valueList[i];
				resValue.put(key, sys.getSystemConfigData(key));
			}
			res.put("body", resValue);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
