/**
 * @(#)PortalCommonAspectLogon.java Created on 2013-11-27 下午4:36:34
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package platform.source.service.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.account.organ.Organ;
import ctd.account.organ.OrganController;
import ctd.app.Application;
import ctd.app.ApplicationController;
import ctd.controller.exception.ControllerException;
import ctd.domain.DomainBean;
import ctd.domain.DomainUtil;
import ctd.mvc.controller.support.logon.CommonAspectLogon;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PlatFormCommonAspectLogon extends CommonAspectLogon {
	Logger logger = LoggerFactory.getLogger(PlatFormCommonAspectLogon.class);
	// **公共app文件
	public static final String UTIL_APP_ID="chis.application.util.UTIL";
	
	@Override
	public void afterLogon(Map<String, Object> body) {
		//** 公用参数****
		body.put("serverDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		
		//**下面是 分社区 医疗**==========-s-==========
		String chisStatus = "0";
		String phisStatus = "0";
		Map<String,DomainBean> allDomainMap = DomainUtil.getAllDomains();
		DomainBean chisDomain = allDomainMap.get("chis");
		if(chisDomain != null){
			chisStatus = chisDomain.getStatus();
		}
		DomainBean phisDomain = allDomainMap.get("phis");
		if(phisDomain != null){
			phisStatus = phisDomain.getStatus();
		}
		//**@社区参数
		if("1".equals(chisStatus)){
			Application app = null;
			try {
				app = PlatFormCommonAspectLogon.getApplication(UTIL_APP_ID);
			} catch (ControllerException e) {
				logger.error(e.getMessage(), e);
			}
			body.put("childrenRegisterAge", Integer.parseInt((String) app
					.getProperty("childrenRegisterAge")));
			body.put("childrenDieAge",
					Integer.parseInt((String) app.getProperty("childrenDieAge")));
			body.put("oldPeopleAge",
					Integer.parseInt((String) app.getProperty("oldPeopleAge")));
			body.put(
					"oldPeopleMode",
					Integer.parseInt((String) app.getProperty(BusinessType.LNR
							+ "_planMode")));
			body.put(
					"hypertensionMode",
					Integer.parseInt((String) app.getProperty(BusinessType.GXY
							+ "_planMode")));
			body.put(
					"diabetesMode",
					Integer.parseInt((String) app.getProperty(BusinessType.TNB
							+ "_planMode")));
			body.put(
					"diabetesPrecedeDays",
					Integer.parseInt((String) app.getProperty(BusinessType.TNB
							+ "_precedeDays")));
			body.put(
					"diabetesDelayDays",
					Integer.parseInt((String) app.getProperty(BusinessType.TNB
							+ "_delayDays")));
			body.put(
					"pregnantMode",
					Integer.parseInt((String) app.getProperty(BusinessType.MATERNAL
							+ "_planMode")));
		}
		//**@医疗参数
//		if("1".equals(phisStatus)){
//			// add by yangl 根据用户ID，找到GY_YGDM表中的YGDM
//			UserRoleToken token = UserRoleToken.getCurrent();
//			BaseDAO dao = new BaseDAO();
//			try {
//				Map<String, Object> parameters = new HashMap<String, Object>();
//				parameters.put("YGBH", token.getUserId());
//
//				List<Map<String, Object>> list = dao.doQuery(
//						"select YGDM as YGDM from GY_YGDM where YGBH=:YGBH",
//						parameters);
//
//				if (list == null || list.size() == 0) {
//					logger.error("获取员工信息错误！");
//					return;
//				} else if (list.size() > 1) {
//					logger.error("登录名：" + token.getUserId() + "存在多个员工信息！");
//					return;
//				}
//				Map<String, Object> gy_ygdm = list.get(0);
//				token.setProperty("staffId", gy_ygdm.get("YGDM"));// 框架登录用户对应HIS员工的YGDM
//				body.put("staffId", gy_ygdm.get("YGDM"));// 返回到前台缓存
//				//ContextUtils.put("user.prop.staffId", gy_ygdm.get("YGDM"));// 支持表达式中的用户
//				//增加顶级节点 
//				body.put("topUnitId", this.getTopUnitId());// 返回到前台缓存
//
//			} catch (PersistentDataOperationException e) {
//				logger.error("获取员工信息错误！", e);
//			}
//		}
		//**下面是 分社区 医疗**==========-e-==========
		
		//** 下面也是公用参数============
		body.put("chisStatus", chisStatus);
		body.put("phisStatus", phisStatus);
		try {
			UserRoleToken token = UserRoleToken.getCurrent();
			Organ organ = OrganController.instance().get(token.getOrganId());
			Object instantExtractMSG = organ.getProperty("instantExtractMSG",Boolean.class);
			body.put("instantExtractMSG", instantExtractMSG==null?false:instantExtractMSG);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取系统顶级节点ID
	 * 
	 * @return
	 */
	private String getTopUnitId() {
		String organId = UserRoleToken.getCurrent().getOrganId();
		if (organId != null && organId.indexOf(".") >= 0) {
			return organId.substring(organId.indexOf(".")+1);
		}
		return organId;
	}
	/**
	 * 获取application文件
	 * 
	 * @param appName
	 * @return
	 * @throws ControllerException
	 */
	public static Application getApplication(String appName)
			throws ControllerException {
		ApplicationController acr = ApplicationController.instance();
		return acr.get(appName);
	}
	
}
