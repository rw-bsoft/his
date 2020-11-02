/**
 * @(#)IdrReport.java Created on 2012-11-15 上午9:42:02
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.idr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.validator.ValidateException;

/**
 * @description 传染病档案
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class IdrReportModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public IdrReportModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 注销传染病档案
	 * 
	 * @param whereField
	 * @param whereValue
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @throws ModelDataOperationException
	 */
	public void logOutIdrReport(String whereField, String whereValue,
			String cancellationReason, String deadReason)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ").append("IDR_Report")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ").append(" where ")
				.append(whereField).append(" = :whereValue")
				.append(" and  status = :normal");

		String userId = UserRoleToken.getCurrent().getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("whereValue", whereValue);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销传染病档案信息失败!", e);
		}
	}

	/**
	 * 恢复传染病档案
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void revertIdrReport(String empiId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("IDR_Report")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where empiId = :empiId ")
				.append(" and status = :status1").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationReason", "");
		parameters.put("deadReason", "");
		parameters.put("empiId", empiId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复传染病档案失败！", e);
		}
	}

	/**
	 * 
	 * @Description:保存传染病报告卡信息
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-6-8 下午1:44:37
	 * @Modify:
	 */
	public Map<String, Object> saveIdrReport(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, IDR_Report, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存传染病报告卡信息数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存传染病报告卡信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:查询病人的门诊传染病诊断
	 * @param BRID
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-5-26 下午2:35:10
	 * @Modify: 
	 */
	public List<Map<String, Object>> getClinicDiagnosisList(long BRID)
			throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("BRID", BRID);
		StringBuffer sql_list = new StringBuffer(
				"select a.JLBH as JLBH,a.BRID as BRID,a.JGID as JGID,a.JZXH as JZXH,a.DEEP as DEEP,a.SJZD as SJZD,a.ZDMC as ZDMC,a.ZZBZ as ZZBZ,a.ZDXH as ZDXH,a.PLXH as PLXH,a.ICD10 as ICD10,");
		sql_list.append("a.ZDBW as ZDBW,a.ZDYS as ZDYS,to_char(a.ZDSJ,'yyyy-mm-dd hh24:mi:ss') as ZDSJ,a.ZDLB as ZDLB,a.ZGQK as ZGQK,a.ZGSJ as ZGSJ,a.FZBZ as FZBZ,to_char(a.FBRQ,'yyyy-mm-dd') as FBRQ,b.ZHMC as ZHMC,a.CFLX as CFLX,a.JBPB as JBPB ");
		sql_list.append("from MS_BRZD a left join EMR_ZYZH b on a.ZDBW = b.ZHBS left join GY_JBBM c on a.ZDXH=c.JBXH where c.JBBGK='06' and a.BRID =:BRID  order by a.PLXH ASC");
		List<Map<String, Object>> inofList = null;
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载病人诊断信息出错！");
		}
		for (int i = 0; i < inofList.size(); i++) {
			if (inofList.get(i).get("CFLX") != null) {
				inofList.get(i).put("ZXLB", inofList.get(i).get("CFLX"));
			}
		}
		for (int i = 0; i < inofList.size(); i++) {
			if (inofList.get(i).get("ZHMC") != null) {
				inofList.get(i).put("MC", inofList.get(i).get("ZHMC") + "");
			} else {
				try {
					inofList.get(i).put(
							"MC",
							DictionaryController.instance()
									.get("phis.dictionary.position")
									.getText(inofList.get(i).get("ZDBW") + ""));
				} catch (ControllerException e) {
					e.printStackTrace();
				}
			}
		}
		rsList = SchemaUtil.setDictionaryMessageForList(inofList,
				"chis.application.idr.schemas.MS_BRZD_IDR");
		return rsList;
	}
}
