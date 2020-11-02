/**
 * @(#)HIVSScreeningService.java Created on 2012-1-5 上午11:37:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.hivs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.hivs.HIVSScreeningModel;
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
public class HIVSScreeningService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(HIVSScreeningService.class);

	/**
	 * HIV筛查保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveHIVSScreening(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		HIVSScreeningModel hsm = new HIVSScreeningModel(dao);
		hsm.saveHIVSScreening(jsonReq, jsonRes, dao, ctx);
	}
	
	/**
	 * HIV筛查保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doUpdateCheckflag(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		HIVSScreeningModel hsm = new HIVSScreeningModel(dao);
		hsm.updateCheckflag(jsonReq, jsonRes, dao, ctx);
	}
	
	/**
	 * HIV筛查查询根据EMPIID
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	protected void doQueryHIVSScreening(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		HIVSScreeningModel hsm = new HIVSScreeningModel(dao);
		Map<String,Object> res = new HashMap<String,Object>();
		res = hsm.queryHIVSScreening(jsonReq, jsonRes, dao, ctx);	
		jsonRes.put("data", res);
	}
	
	protected void doCheckHIVSRecord(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		HIVSScreeningModel hsm = new HIVSScreeningModel(dao);
		hsm.checkHIVSRecord(jsonReq, jsonRes, dao, ctx);
	}
}