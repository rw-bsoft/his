/**
 * @(#)IdrReportService.java Created on 2014-6-8 下午1:38:19
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.idr;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class IdrReportService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(IdrReportService.class);

	/**
	 * 
	 * @Description:保存传染病报告卡
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-6-8 下午1:39:37
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveIdrReport(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		Long RecordID = Long.parseLong(reqBodyMap.get("RecordID")!=null?(reqBodyMap.get("RecordID")+""):"1");
		IdrReportModel irModel = new IdrReportModel(dao);
		if(S.isEmpty(phrId)){
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> hrMap = null;
			try {
				hrMap = hrModel.getHealthRecordListByEmpiId(empiId);
			} catch (ModelDataOperationException e1) {
				logger.error("Get health record by empiId failure..", e1);
				throw new ServiceException(e1);
			}
			if(hrMap != null && hrMap.size() > 0){
				phrId = (String) hrMap.get("phrId");
				reqBodyMap.put("phrId", phrId);
			}
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = irModel.saveIdrReport(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save IDR_Report failure.", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op) && rsMap != null) {
			RecordID = Long.parseLong(rsMap.get("RecordID")+"");
		}
		vLogService.saveVindicateLog(IDR_Report, op, String.valueOf(RecordID), dao, empiId);
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:获取门诊传染病诊断列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-5-26 上午11:34:20
	 * @Modify:
	 */
	public void doGetClinicDiagnosisList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		long BRID = Long.parseLong(req.get("BRID") + "");
		//long JZXH = Long.parseLong(req.get("JZXH") + "");
		List<Map<String, Object>> rsList = null;
		if (BRID > 0) {
			IdrReportModel idrModel = new IdrReportModel(dao);
			try {
				rsList = idrModel.getClinicDiagnosisList(BRID);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Get catching cilinc diagnosis of the patient failure.",
						e);
				throw new ServiceException(e);
			}
		}
//		if(rsList != null && rsList.size() > 0){
//			for(int i=0,len=rsList.size();i < len; i++){
//				rsList.get(i).put("NUM", (i+1));
//			}
//		}
		res.put("body", rsList);
	}
}
