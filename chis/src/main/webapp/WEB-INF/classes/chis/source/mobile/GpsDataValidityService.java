/**
 * @(#)FamilyContractModel.java Created on 2012-11-19 下午03:01:03
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.SchemaUtil;

import ctd.app.ApplicationController;
import ctd.net.rpc.Client;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:wangk@bsoft.com.cn">王侃</a>
 */
public class GpsDataValidityService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(GpsDataValidityService.class);

	private static double EARTH_RADIUS = 6378.137;
	/**
	 * 坐标比对容许的误差值
	 */
	//private static final double wucha = 50;

	@SuppressWarnings("unchecked")
	public void doSaveGpsData(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String validityFlag = (String) body.get("validityFlag");
		if (null == validityFlag) {
			body.put("validityFlag", "0");
		}
		try {
			Map<String, Object> gpsData = dao.doInsert(PUB_DataValidity, body,
					true);
			if (gpsData != null) {
				jsonRes.put("body", gpsData);
			}
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doAutoMatch(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> param = new HashMap<String, Object>();
		String cndValue = (String) body.get("cndValue");
		String beginTime = (String) body.get("beginTime");
		String endTime = (String) body.get("endTime");
		String hql = " from PUB_DataValidity where (1=1)";
		hql += " and serviceDate>=to_date(:beginTime,'yyyy-MM-dd') and  serviceDate <=to_date(:endTime,'yyyy-MM-dd') and validityFlag = '0' and GPS is not null and GPS!='4.9E-324,4.9E-324'";
		param.put("beginTime", beginTime);
		param.put("endTime", endTime);
		if (!StringUtils.isEmpty(cndValue)) {
			hql += " and serviceBusiness=:serviceBusiness";
			param.put("serviceBusiness", cndValue);
		}
		try {
			List<Map<String, Object>> result = dao.doQuery(hql, param);
			processJob(result, dao);
			response.put("msg", "处理完成...");
			jsonRes.put("body", response);
		} catch (Exception e) {
			logger.error("doAutoMatch error...");
			throw new ServiceException(e);
		}
	}

	public void doQualityMatch(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> response = new HashMap<String, Object>();
			String op = (String) jsonReq.get("ops");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("registerId", (String) jsonReq.get("registerId"));
			if ("yes".equals(op)) {
				dao.doUpdate(
						"update PUB_DataValidity set validityFlag='1' where registerId=:registerId",
						param);
			} else {
				dao.doUpdate(
						"update PUB_DataValidity set validityFlag='2' where registerId=:registerId",
						param);
			}
			response.put("msg", "处理完成...");
			jsonRes.put("body", response);
		} catch (PersistentDataOperationException e) {
			logger.error("doQualityMatch error...");
			throw new ServiceException(e);
		}
	}

	public void doAllMatch(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> response = new HashMap<String, Object>();
		String hql = " from PUB_DataValidity where (1=1)";
		hql += "and validityFlag = '0' and GPS is not null";
		try {
			List<Map<String, Object>> result = dao.doQuery(hql, null);
			processJob(result, dao);
			response.put("msg", "处理完成...");
			jsonRes.put("body", response);
		} catch (Exception e) {
			logger.error("doAutoMatch error...");
			throw new ServiceException(e);
		}
	}

	public void processJob(List<Map<String, Object>> result, BaseDAO dao)
			throws Exception {
		System.out.println(result);
		SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
		int wucha=Integer.parseInt(scmm.getSystemConfigData("cercle"))/2;
		for (Map<String, Object> entry : result) {
			if (entry.get("GPS") != null) {
				String gps = (String) entry.get("GPS");
				String[] jwd = gps.split(",");// 处理
				String hql2 = "select b.GPS as gps from  EHR_HealthRecord a, "
						+ " EHR_AreaGrid b ,PUB_DataValidity c"
						+ " where a.empiId = c.empiId and a.regionCode = b.regionCode "
						+ " and a.empiId = :empiId ";
				Map<String, Object> param2 = new HashMap<String, Object>();
				param2.put("empiId", entry.get("empiId"));
				List<Map<String, Object>> r = dao.doQuery(hql2, param2);
				if (r.size() > 0) {
					String gps_areagrid = (String) r.get(0).get("gps"); // 社区areaGird
																		// gps
																		// 字段
					if (gps_areagrid != null && !gps_areagrid.equals("")) {
						String[] jwd2 = gps_areagrid.split(",");
						boolean pass = false;
						double deviation = GetDistance(
								Double.parseDouble(jwd[0]),
								Double.parseDouble(jwd[1]),
								Double.parseDouble(jwd2[0]),
								Double.parseDouble(jwd2[1]));
						deviation *= 1000;
						if (deviation <= wucha) { // 符合坐标,打标记
							pass = true;
						}
						param2 = new HashMap<String, Object>();
						param2.put("registerId", entry.get("registerId"));
						// 在上面写jwd判断逻辑
						if (pass) {
							dao.doUpdate(
									"update PUB_DataValidity set validityFlag='1' where registerId=:registerId",
									param2);
						} else {
							dao.doUpdate(
									"update PUB_DataValidity set validityFlag='2' where registerId=:registerId",
									param2);
						}
					}
				}
			}
		}
	}

	public void doSaveValidityCercle(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		if (body == null) {
			logger.error("body  is missed!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		String cercle = body.get("cercle").toString();
		String validityDays = body.get("validityDays").toString();
		try {
			saveSystemConfigData("cercle", cercle);
			saveSystemConfigData("validityDays", validityDays);
			ApplicationController.instance().reload(Constants.UTIL_APP_ID);
		} catch (Exception e) {
			res.put(RES_CODE, ServiceCode.CODE_IO_EXCEPTION);
			res.put(RES_MESSAGE, "spring.xml配置文件解析失败。");
			return;
		}
	}

	public void doGetValidityCercle(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		try {
			Map<String, Object> body = new HashMap<String, Object>();
			SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
			String cercle = scmm.getSystemConfigData("cercle");
			int value=Integer.parseInt(cercle);
			int validityDays=Integer.parseInt(scmm.getSystemConfigData("validityDays"));
			body.put("cercle",value);
			body.put("validityDays",validityDays);
			res.put("body", body);
		} catch (Exception e) {
			e.printStackTrace();
			res.put(RES_CODE, ServiceCode.CODE_IO_EXCEPTION);
			res.put(RES_MESSAGE, "配置文件解析失败。");
			return;
		}

	}
	
	protected void saveSystemConfigData(String configName, String configValue)
			throws Exception {
		if (configName == null || configName.equals("")) {
			return;
		}
		Document doc = (Document) Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "load", new Object[]{Constants.UTIL_APP_ID,".app"});
		Document define = DocumentHelper.parseText(doc.asXML());
		Element e = (Element) define.getRootElement().selectSingleNode(
				"//properties//p[@name='" + configName + "']");
		e.setText(configValue);
		Client.rpcInvoke(AppContextHolder.getConfigServiceId("xmlRemoteLoader"), "save", new Object[]{define,Constants.UTIL_APP_ID,".app"});
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2,
			double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS * 10000;
		s = Math.round(s);
		s = s / 10;
		return s;
	}

	public static void main(String args[]) {
		double a = 30.281405;
		double b = 120.143135;

		double a1 = 30.28301;
		double b1 = 120.144494;
		System.out.println(GetDistance(a, b, a1, b1));

	}
}
