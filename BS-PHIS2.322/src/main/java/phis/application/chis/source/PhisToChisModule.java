/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.chis.source;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.ServiceCode;
import ctd.util.context.Context;

/**
 * @description 处方组套维护
 * 
 * @author zhangyq 2012.05.28
 */
public class PhisToChisModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PhisToChisModule.class);

	public PhisToChisModule(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 根据病人id查询最近开处方时间
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doGetLastCfDate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, String> body = (Map<String, String>) req.get("body");
		String brid=body.get("brid");
		try {
			String hql="select kfrq as KFRQ from MS_CF01 where brid=:brid order by kfrq desc";
			Map param=new HashMap();
			param.put("brid", 11111);
			List<Map<String,Object>> list=(List<Map<String,Object>>)dao.doSqlQuery(hql, param); 
			DateFormat df=DateFormat.getDateInstance();
			if(list.size()<1)
			{
				String kfrq=df.format(new Date());				
				res.put("kfrq", kfrq);
			}else
			{
				Map<String,Object> kfrqMap=list.get(0);
				String kfrq=df.format((Date)kfrqMap.get("KFRQ"));
				res.put("kfrq", kfrq);	
			}
		} catch (Exception e) {
			logger.error("Qyery failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询处方信息失败.");
		}
	}

//	/**
//	 * 处方组套明细删除
//	 * 
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 */
//	public void doRemovePrescriptionDetails(Map<String, Object> body,
//			long pkey, String schemaList, String schemaDetailsList,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ModelDataOperationException {
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		String sql = "ZTBH=:ZTBH";
//		String sql1 = "ZTBH=:ZTBH and SFQY=1";// 判断是否启用的sql
//		try {
//			dao.doRemove(pkey, "phis.application.fsb.schemas.JC_ZL_ZT02");
//			parameters.put("ZTBH", Long.parseLong(body.get("ZTBH") + ""));
//			Long l = dao.doCount(schemaDetailsList, sql, parameters);
//			if (l == 0) {
//				Long l1 = dao.doCount(schemaList, sql1, parameters);
//				if (l1 > 0) {// 如果启用了 才取消启动
//					dao.doUpdate(
//							"update JC_ZL_ZT01 set SFQY=0 where ZTBH=:ZTBH",
//							parameters);
//					res.put(Service.RES_CODE, 604);
//					res.put(Service.RES_MESSAGE, "该组套明细为空，自动取消启用");
//				}
//			}
//		} catch (PersistentDataOperationException e) {
//			logger.error("remove failed.", e);
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "删除失败.");
//		}
//
//	}
//
//	/*
//	 * 病历模板启用功能
//	 */
//	public void saveMedicalTemplateExecute(Map<String, Object> body,
//			String schemaList, String schemaDetailsList,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ModelDataOperationException {
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		String QYBZ = body.get("QYBZ") + "";
//		String sql = "JLXH=:JLXH and ZSXX is not null and XBS is not null";
//		try {
//			parameters.put("JLXH", body.get("JLXH"));
//			Long l = dao.doCount(schemaDetailsList, sql, parameters);
//			// 如果明细不为空
//			if (l > 0) {
//				// 没有启用
//				if ("0".equalsIgnoreCase(QYBZ)) {
//					// 启用
//					parameters.put("QYBZ", 0);
//					dao.doUpdate("update " + schemaList
//							+ " set QYBZ=:QYBZ where JLXH=:JLXH", parameters);
//					res.put(Service.RES_CODE, 605);
//					res.put(Service.RES_MESSAGE, "取消启用成功");
//				} else {
//					// 取消启用
//					parameters.put("QYBZ", 1);
//					dao.doUpdate("update " + schemaList
//							+ " set QYBZ=:QYBZ WHERE JLXH=:JLXH", parameters);
//					res.put(Service.RES_CODE, 605);
//					res.put(Service.RES_MESSAGE, "启用成功");
//				}
//			} else {
//				res.put(Service.RES_CODE, 607);
//				res.put(Service.RES_MESSAGE, "明细为空，不能启用");
//			}
//		} catch (PersistentDataOperationException e) {
//			logger.error("Save failed.", e);
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
//		}
//	}

//	/*
//	 * 诊疗方案启用功能
//	 */
//	public void saveTherapeuticRegimenExecute(Map<String, Object> body,
//			String schemaList, String schemaDetailsList,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ModelDataOperationException {
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		String QYBZ = body.get("QYBZ") + "";
//		String sql = "ZLXH=:ZLXH and (BLMBBH is not null or CFZTBH is not null or XMZTBH is not null or JBXH is not null)";
//		try {
//			parameters.put("ZLXH", body.get("ZLXH"));
//			Long l = dao.doCount(schemaDetailsList, sql, parameters);
//			// 如果明细不为空
//			if (l > 0) {
//				// 没有启用
//				if ("0".equalsIgnoreCase(QYBZ)) {
//					// 启用
//					parameters.put("QYBZ", 0);
//					dao.doUpdate("update " + schemaList
//							+ " set QYBZ=:QYBZ where ZLXH=:ZLXH", parameters);
//					res.put(Service.RES_CODE, 605);
//					res.put(Service.RES_MESSAGE, "取消启用成功");
//				} else {
//					// 取消启用
//					parameters.put("QYBZ", 1);
//					dao.doUpdate("update " + schemaList
//							+ " set QYBZ=:QYBZ WHERE ZLXH=:ZLXH", parameters);
//					res.put(Service.RES_CODE, 605);
//					res.put(Service.RES_MESSAGE, "启用成功");
//				}
//			} else {
//				res.put(Service.RES_CODE, 607);
//				res.put(Service.RES_MESSAGE, "明细为空，不能启用");
//			}
//		} catch (PersistentDataOperationException e) {
//			logger.error("Save failed.", e);
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
//		}
//	}
//
//	// 删除组套时判断组套明细是否有值 如果有 提示不能删
//	public void removePrescriptionDel(Map<String, Object> body,
//			Map<String, Object> res, String op, BaseDAO dao, Context ctx)
//			throws ModelDataOperationException {
//		String sql = "ZTBH=:ZTBH";
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put("ZTBH", Long.parseLong(body.get("ZTBH") + ""));
//		try {
//			Long l = dao.doCount("YS_MZ_ZT02", sql, parameters);
//			if (l > 0) {
//				res.put(Service.RES_CODE, 613);
//				res.put(Service.RES_MESSAGE, "组套明细已存在，无法删除");
//			}
//		} catch (PersistentDataOperationException e) {
//			logger.error("Save failed.", e);
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "组套明细校验失败");
//		}
//	}

}
