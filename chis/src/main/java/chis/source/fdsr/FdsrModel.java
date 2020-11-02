/**
 * @(#)HIVSScreeningService.java Created on 2012-1-5 上午11:37:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.fdsr;

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
public class FdsrModel extends AbstractActionService implements
		DAOSupportable {
	protected BaseDAO dao;
	private static final Logger logger = LoggerFactory
			.getLogger(FdsrModel.class);			
	public FdsrModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * 2020-09-11 renwei 家医服务记录保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void saveJYFW(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) jsonReq.get("op");
		System.out.println("----------->"+op);
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> params = new HashMap<String, Object>();	
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = null;
//		try {
//			date = simpleDateFormat.parse(body.get("recodeDate").toString());
//		} catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		params.put("empiId", body.get("empiId"));
		params.put("phrId", body.get("phrId"));
		params.put("name", body.get("name")); //姓名
		params.put("sex", body.get("sex")); //性别
		params.put("age", body.get("age")); //年龄
		params.put("telephone", body.get("telephone")); //电话
		params.put("jkgl", body.get("jkgl")); //健康管理服务
		params.put("jksj",body.get("jksj")); //健康数据监测
		params.put("hlyy",body.get("hlyy")); //合理用药指导
		params.put("psyz",body.get("psyz")); //配送医嘱内药品
		params.put("lxdc", body.get("lxdc")); //联系代采购药品
		params.put("fyjk", body.get("fyjk")); //妇幼健康项目咨询服务
		params.put("kzmx", body.get("kzmx")); //开展慢性病等重点人群自我管理小组活动
		params.put("zyyj", body.get("zyyj")); //中医药健康管理
		params.put("byzd", body.get("byzd")); //避孕指导、药具发放
		params.put("zzyy",body.get("zzyy")); //转诊、预约就诊等联络
		params.put("yytj",body.get("yytj")); //预约体检时间
		params.put("jdtj",body.get("jdtj")); //解读体检报告
		params.put("xlgh",body.get("xlgh")); //心理关怀
		params.put("kzjy",body.get("kzjy")); //开展家医签约
		params.put("qt",body.get("qt")); //其他
		

		params.put("isjkgl", body.get("isjkgl")); //健康管理服务
		params.put("isjksj",body.get("isjksj")); //健康数据监测
		params.put("ishlyy",body.get("ishlyy")); //合理用药指导
		params.put("ispsyz",body.get("ispsyz")); //配送医嘱内药品
		params.put("islxdc", body.get("islxdc")); //联系代采购药品
		params.put("isfyjk", body.get("isfyjk")); //妇幼健康项目咨询服务
		params.put("iskzmx", body.get("iskzmx")); //开展慢性病等重点人群自我管理小组活动
		params.put("iszyyj", body.get("iszyyj")); //中医药健康管理
		params.put("isbyzd", body.get("isbyzd")); //避孕指导、药具发放
		params.put("iszzyy",body.get("iszzyy")); //转诊、预约就诊等联络
		params.put("isyytj",body.get("isyytj")); //预约体检时间
		params.put("isjdtj",body.get("isjdtj")); //解读体检报告
		params.put("isxlgh",body.get("isxlgh")); //心理关怀
		params.put("iskzjy",body.get("iskzjy")); //开展家医签约
		params.put("isqt",body.get("isqt")); //其他
				
//		params.put("recodeDate",date); //记录日期
		
		try {
			if(op.equals("create")){
				dao.doSave("create", FDSR, params, false);
			}else if(op.equals("update")){
				params.put("recodeId", body.get("id"));
				dao.doSave("update", FDSR, params, false);
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected Map<String, Object> queryJYFW(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> res = new ArrayList();
		params.put("empiId",body.get("empiId"));
		String empiId = (String) body.get("empiId");
		try {
			res = dao.doQuery("empiId", empiId, FDSR);
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
	
//	@SuppressWarnings("unchecked")
//	protected void checkJYFWRecord(Map<String, Object> jsonReq,
//			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
//		Map<String, Object> params = new HashMap<String, Object>();
//		Map<String, Object> rsMap = null;
////		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
////        Date date = null;
////		try {
////			date = simpleDateFormat.parse(body.get("recodeDate").toString());
////		} catch (ParseException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
////		params.put("recodeDate",date);
//		params.put("empiId",body.get("empiId"));
//		params.put("recodeId",body.get("recodeId"));
//		String hql = new StringBuffer("select count(*) as ct from ")
//						.append(FDSR)
//						.append(" where empiId=:empiId and recodeId <> :recodeId")
//						.toString();
//		try {
//			rsMap = dao.doLoad(hql, params);
//		} catch (PersistentDataOperationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		long count = rsMap == null ? 0 : (Long) rsMap.get("ct");
//		jsonRes.put("count", count);
//	}
//	
//	protected void updateCheckflag(Map<String, Object> jsonReq,
//			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
//			throws ServiceException {
//		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
//		Map<String, Object> params = new HashMap<String, Object>();	
//		params.put("checkFlag",body.get("checkFlag"));
//		params.put("recodeId", body.get("id"));
//		try {
//			dao.doSave("update", FDSR, params, false);
//		} catch (PersistentDataOperationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
