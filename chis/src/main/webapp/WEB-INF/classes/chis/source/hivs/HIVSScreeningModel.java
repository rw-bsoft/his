/**
 * @(#)HIVSScreeningService.java Created on 2012-1-5 上午11:37:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.hivs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.admin.AreaGridModel;
import chis.source.admin.SystemUserModel;
import chis.source.cdh.ChildrenBaseModel;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.cdh.DebilityChildrenModel;
import chis.source.control.ControlRunner;
import chis.source.dc.RabiesRecordModel;
import chis.source.def.DefLimbModel;
import chis.source.dic.Gender;
import chis.source.dic.IsFamily;
import chis.source.dic.Maritals;
import chis.source.dic.RelatedCode;
import chis.source.dic.RolesList;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiModel;
import chis.source.fhr.FamilyRecordModule;
import chis.source.idr.IdrReportModel;
import chis.source.log.VindicateLogService;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mdc.HypertensionRiskModel;
import chis.source.mhc.PregnantRecordModel;
import chis.source.ohr.OldPeopleRecordModel;
import chis.source.psy.PsychosisRecordModel;
import chis.source.rvc.RetiredVeteranCadresModel;
import chis.source.sch.SchistospmaModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.tr.TumourConfirmedModel;
import chis.source.tr.TumourHighRiskModel;
import chis.source.tr.TumourPatientReportCardModel;
import chis.source.tr.TumourQuestionnaireModel;
import chis.source.tr.TumourScreeningModel;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanModel;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 个人健康档案服务
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">tianj</a>
 */
public class HIVSScreeningModel extends AbstractActionService implements
		DAOSupportable {
	protected BaseDAO dao;
	private static final Logger logger = LoggerFactory
			.getLogger(HIVSScreeningModel.class);			
	public HIVSScreeningModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * 2020-05-26 XiaHeng HIV人群筛查保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void saveHIVSScreening(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) jsonReq.get("op");
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> params = new HashMap<String, Object>();	
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
		try {
			date = simpleDateFormat.parse(body.get("screeningDate").toString());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		params.put("empiId", body.get("empiId"));
		params.put("phrId", body.get("phrId"));
		params.put("outHistory", body.get("outHistory"));
		params.put("operation", body.get("operation"));
		params.put("transfusion", body.get("transfusion"));
		params.put("seperationTM", body.get("seperationTM"));
		params.put("widowedHY", body.get("widowedHY"));
		params.put("other",body.get("other"));
		params.put("screeningResult",body.get("screeningResult"));
		params.put("checkFlag",body.get("checkFlag"));
		params.put("screeningDate",date);
		
		try {
			if(op.equals("create")){
				dao.doSave("create", HIVS_Screening, params, false);
			}else if(op.equals("update")){
				params.put("screenId", body.get("id"));
				dao.doSave("update", HIVS_Screening, params, false);
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected Map<String, Object> queryHIVSScreening(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> res = new ArrayList();
		params.put("empiId",body.get("empiId"));
		String empiId = (String) body.get("empiId");
		try {
			res = dao.doQuery("empiId", empiId, HIVS_Screening);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(res.size() > 0){
			return res.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void checkHIVSRecord(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> rsMap = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
		try {
			date = simpleDateFormat.parse(body.get("screeningDate").toString());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		params.put("screeningDate",date);
		params.put("empiId",body.get("empiId"));
		params.put("screenId",body.get("screenId"));
		String hql = new StringBuffer("select count(*) as ct from ")
						.append(HIVS_Screening)
						.append(" where SCREENINGDATE=:screeningDate and empiId=:empiId and screenId <> :screenId")
						.toString();
		try {
			rsMap = dao.doLoad(hql, params);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long count = rsMap == null ? 0 : (Long) rsMap.get("ct");
		jsonRes.put("count", count);
	}
	
	protected void updateCheckflag(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> params = new HashMap<String, Object>();	
		params.put("checkFlag",body.get("checkFlag"));
		params.put("screenId", body.get("id"));
		try {
			dao.doSave("update", HIVS_Screening, params, false);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
