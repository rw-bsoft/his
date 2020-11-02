/**
 * @(#)EmpiLoader.java Created on Mar 16, 2010 7:58:30 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.pix.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import phis.application.pub.source.PublicModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.PersistentDataOperationException;
import phis.source.utils.EHRUtil;
import phis.source.utils.SchemaUtil;

import com.alibaba.fastjson.JSONException;

import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.DBService;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class EmpiLoader extends DBService implements Service, BSPHISEntryNames {

	private static final Log logger = LogFactory.getLog(EmpiLoader.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ctd.service.core.Service#execute(java.util.Map, java.util.Map,
	 * ctd.util.context.Context)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		Map<String, Object> empiData;
		try {
			empiData = load(empiId, ctx);
			empiData = SchemaUtil.setDictionaryMassageForList(empiData,
					BSPHISEntryNames.MPI_DemographicInfo_CIC);
			//查询公卫责任医生和家庭档案，如果有健康档案并且存在家庭档案则查询家庭地址
			if(reqBody.containsKey("qygwxt") && "1".equals((String)reqBody.get("qygwxt")))
			{
				Map<String, Object> chisData=getChisInfo(empiId,ctx);
				if(chisData.get("familyAddr")!=null)
				{
					empiData.put("address", chisData.get("familyAddr"));
				}
				empiData.put("manaDoctorId_text", chisData.get("manaDoctorId_text")==null?"":chisData.get("manaDoctorId_text"));
				if(chisData.get("phrId")!=null)
				{
					empiData.put("phrId", chisData.get("phrId"));	
				}
			}else
			{
				empiData.put("manaDoctorId_text","");
			}
			Date birthday = (Date) empiData.get("birthday");
			int age = EHRUtil.calculateAge(birthday, new Date());
			reqBody.put("age", age);
			empiData.put("age", age);
			res.put("empiData", empiData);
			res.put("ids", reqBody);
		} catch (PersistentDataOperationException e) {
			logger.error("Get empi data failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取个人基本信息失败！");
			throw new ServiceException(e);
		}
	}
	
	
	/**
	 * @param empiId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getChisInfo(String empiId, Context ctx)
			throws PersistentDataOperationException {
		Map<String, Object> chisData = new HashMap<String, Object>();
		try {
			BaseDAO baseDAO = new BaseDAO();
			Map<String, Object> params1 = new HashMap<String, Object>();
			Map<String, Object> params2 = new HashMap<String, Object>();
			params1.put("empiId", empiId);
			
			List<Map<String, Object>> chisDataList=baseDAO.doSqlQuery("select phrId as phrId,manaDoctorId as manaDoctorId,familyId as familyId from EHR_HealthRecord where empiId=:empiId",
					params1);
			if(chisDataList.size()>0)
			{
				chisData=(Map<String, Object>)chisDataList.get(0);
				chisData.put("phrId",chisData.get("PHRID"));
				if(chisData.get("MANADOCTORID")!=null)
					chisData.put("manaDoctorId",chisData.get("MANADOCTORID"));
				chisData = SchemaUtil.setDictionaryMassageForList(chisData,
						BSPHISEntryNames.EHR_HealthRecordChis);
				if(chisData.get("FAMILYID")!=null)//存在家庭档案，查询家庭档案地址
				{
					params2.put("familyId", chisData.get("FAMILYID"));
					List<Map<String, Object>> addrDataList=baseDAO.doSqlQuery("select familyAddr as familyAddr,familyId as familyId from EHR_FamilyRecord where familyId=:familyId",
							params2);
					Map addrDataMap=(Map<String, Object>)addrDataList.get(0);
					addrDataMap = SchemaUtil.setDictionaryMassageForList(addrDataMap,
							BSPHISEntryNames.EHR_FamilyRecordChis);	
					if(addrDataList.size()>0)
					{
						if(addrDataMap.get("FAMILYADDR")!=null)
							chisData.put("familyAddr", addrDataMap.get("FAMILYADDR"));
					}
					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			return chisData;
		}
	}

	/**
	 * @param empiId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> load(String empiId, Context ctx)
			throws PersistentDataOperationException {
		try {
			BaseDAO baseDAO = new BaseDAO();
			EmpiModel model = new EmpiModel(baseDAO);
			Map<String, Object> empiData = model.getEmpiInfoByEmpiid(empiId);
			String idCard = (String) empiData.get("idCard");
			if (idCard == null) {
				empiData.put("idCard", "");
			}
			// @@ 设置这个属性对应到EHRView 头上Template的temp参数，以强制浏览器去刷新。
			empiData.put("temp", new Date().getTime());
			//makeEmpiResponse(empiData, baseDAO);
			return empiData;
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * @param result
	 * @param sc
	 * @param session
	 * @return
	 * @throws JSONException
	 */
	private void makeEmpiResponse(Map<String, Object> result, BaseDAO baseDAO) {
		Date birthday = (Date) result.get("birthday");
		if (birthday == null) {
			result.put("lifeCycle", "unknown");
			result.put("lifeCycle_text", "unknown");
			return;
		}

		try {
			PublicModel publicModel = new PublicModel(baseDAO);
			Map<String, Object> cycle = publicModel
					.getLifeCycle(birthday, null);
			if (cycle == null) {
				result.put("lifeCycle", "unknown");
				result.put("lifeCycle_text", "unknown");
			} else {
				result.put("lifeCycle", cycle.get("cycleId"));
				result.put("lifeCycle_text", cycle.get("cycleName"));
				result.put("age", cycle.get("age"));
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Get life cycle failed.", e);
			result.put("lifeCycle", "unknown");
			result.put("lifeCycle_text", "unknown");
		}
	}
}
