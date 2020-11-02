package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineManageModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.service.core.Service;
import ctd.util.context.Context;

public class ConfigChargingProjectsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChargingProjectsModel.class);

	public ConfigChargingProjectsModel(BaseDAO dao) {
		this.dao = dao;
	}

	// 判断项目名称是否已经存在
	public void chargingProjectsVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		if ("create".equals(op)) {
			String sql = "SFMC=:SFMC";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SFMC", body.get("SFMC"));
			try {
				Long l = dao.doCount(schemaDetailsList, sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "项目名称已经存在");
				}
				if(body.get("PLSX")!=null&&(body.get("PLSX")+"").length()>0){
					String PLSXsql = "PLSX=:PLSX";
					Map<String, Object> PLSXparameters = new HashMap<String, Object>();
					PLSXparameters.put("PLSX", body.get("PLSX"));
					Long PLSXl = dao.doCount(schemaDetailsList, PLSXsql, PLSXparameters);
					if (PLSXl > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "顺序号已经存在");
					}
				}
				if(body.get("ZYPL")!=null&&(body.get("ZYPL")+"").length()>0){
					String PLSXsql = "ZYPL=:ZYPL";
					Map<String, Object> PLSXparameters = new HashMap<String, Object>();
					PLSXparameters.put("ZYPL", body.get("ZYPL"));
					Long PLSXl = dao.doCount(schemaDetailsList, PLSXsql, PLSXparameters);
					if (PLSXl > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "住院顺序号已经存在");
					}
				}
				if(body.get("MZPL")!=null&&(body.get("MZPL")+"").length()>0){
					String PLSXsql = "MZPL=:MZPL";
					Map<String, Object> PLSXparameters = new HashMap<String, Object>();
					PLSXparameters.put("MZPL", body.get("MZPL"));
					Long PLSXl = dao.doCount(schemaDetailsList, PLSXsql, PLSXparameters);
					if (PLSXl > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "门诊顺序号已经存在");
					}
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "项目名称校验失败");
			}
		} else {
			String sql = "SFMC=:SFMC and SFXM<>:SFXM";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SFMC", body.get("SFMC"));
			parameters.put("SFXM", Long.parseLong(body.get("SFXM")+""));
			try {
				Long l = dao.doCount(schemaDetailsList, sql, parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 613);
					res.put(Service.RES_MESSAGE, "项目名称已经存在");
				}
				if(body.get("ZBLB")!=null&&Long.parseLong(body.get("ZBLB")+"")==0){
					Map<String, Object> YK_TYPKparameters = new HashMap<String, Object>();
					YK_TYPKparameters.put("ZBLB", Long.parseLong(body.get("SFXM")+""));
					long YK_TYPKl = dao.doCount("YK_TYPK", "ZBLB=:ZBLB", YK_TYPKparameters);
					if(YK_TYPKl>0){
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "当前项目账簿类别已被使用。");
					}
				}
				if(body.get("PLSX")!=null&&(body.get("PLSX")+"").length()>0){
					String PLSXsql = "PLSX=:PLSX and SFXM<>:SFXM";
					Map<String, Object> PLSXparameters = new HashMap<String, Object>();
					PLSXparameters.put("PLSX", body.get("PLSX"));
					PLSXparameters.put("SFXM", Long.parseLong(body.get("SFXM")+""));
					Long PLSXl = dao.doCount(schemaDetailsList, PLSXsql, PLSXparameters);
					if (PLSXl > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "顺序号已经存在");
					}
				}
				if(body.get("ZYPL")!=null&&(body.get("ZYPL")+"").length()>0){
					String PLSXsql = "ZYPL=:ZYPL and SFXM<>:SFXM";
					Map<String, Object> PLSXparameters = new HashMap<String, Object>();
					PLSXparameters.put("ZYPL", body.get("ZYPL"));
					PLSXparameters.put("SFXM", Long.parseLong(body.get("SFXM")+""));
					Long PLSXl = dao.doCount(schemaDetailsList, PLSXsql, PLSXparameters);
					if (PLSXl > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "住院顺序号已经存在");
					}
				}
				if(body.get("MZPL")!=null&&(body.get("MZPL")+"").length()>0){
					String PLSXsql = "MZPL=:MZPL and SFXM<>:SFXM";
					Map<String, Object> PLSXparameters = new HashMap<String, Object>();
					PLSXparameters.put("MZPL", body.get("MZPL"));
					PLSXparameters.put("SFXM", Long.parseLong(body.get("SFXM")+""));
					Long PLSXl = dao.doCount(schemaDetailsList, PLSXsql, PLSXparameters);
					if (PLSXl > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "门诊顺序号已经存在");
					}
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Save failed.", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "项目名称校验失败");
			}
		}
	}

	public int logoutMedicalItems(long ypxh, int zfpb)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fyxh", ypxh);
		parameters.put("zfpb", zfpb);
		hql.append("update ").append(" GY_YLSF ")
				.append(" set ZFPB=:zfpb where FYXH=:fyxh");
		try {
			dao.doUpdate(hql.toString(), parameters);
			MedicineManageModel mmd = new MedicineManageModel(
					dao);
			mmd.updatePharmacyMedicinesUpdateTime(ypxh, null);// 更新药房药品信息的修改时间
			return ServiceCode.CODE_OK;
		} catch (PersistentDataOperationException e) {
			if (zfpb == 0) {
				logger.error("取消注销失败", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "医疗项目明细取消注销失败.");
			} else {
				logger.error("注销失败", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "医疗项目明细注销失败.");
			}
		}

	}

}
