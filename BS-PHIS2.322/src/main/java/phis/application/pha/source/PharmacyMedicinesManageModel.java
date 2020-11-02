package phis.application.pha.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

/**
 * 药房药品信息维护model
 * 
 * @author caijy
 * 
 */
public class PharmacyMedicinesManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyMedicinesManageModel.class);

	public PharmacyMedicinesManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 药品私用信息导入
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int savePharmacyMedicinesInformations(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		if (!MedicineUtils.verificationPharmacyId(ctx)) {
			return 0;
		}
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		StringBuffer qxSql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("yfsb", yfsb);
		qxSql.append("select XYQX as XYQX,ZYQX as ZYQX,CYQX as CYQX,BZLB as BZLB from YF_YFLB where YFSB=:yfsb ");
		List<Map<String, Object>> qxList = null;
		try {
			qxList = dao.doQuery(qxSql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取药房药品权限失败", e);
		}
		StringBuffer qxWhere = new StringBuffer("(");
		Map<String, Object> qx;
		if (qxList == null || qxList.size() == 0) {
			return -1;
		} else {
			qx = qxList.get(0);
			List<String> list = new ArrayList<String>();
			if ("1".equals(qx.get("XYQX") + "")) {
				list.add("1");
			}
			if ("1".equals(qx.get("ZYQX") + "")) {
				list.add("2");
			}
			if ("1".equals(qx.get("CYQX") + "")) {
				list.add("3");
			}
			if (list.size() == 0) {
				return 0;
			} else {
				for (int i = 0; i < list.size(); i++) {
					if (i == 0) {
						qxWhere.append(list.get(i));
					} else {
						qxWhere.append(",").append(list.get(i));
					}
				}
				qxWhere.append(")");
			}
		}
		StringBuffer sql = new StringBuffer();
		String date = "sysdate";
		sql.append("insert into YF_YPXX (JGID,YFSB,YPXH,YFGG,YFDW,YFBZ,YFGC,YFDC,YFZF,KWBM,QZCL,DRSJ,XGSJ) ( ");
		if ("1".equals(qx.get("BZLB") + "")) {
			sql.append(
					"select yk.JGID,"
							+ yfsb
							+ ",yk.YPXH,yp.YFGG,yp.YFDW,yp.YFBZ,0,0,0,yk.KWBM,yp.QZCL,"
							+ date + "," + date).append(" from ");
		} else {
			sql.append(
					"select yk.JGID,"
							+ yfsb
							+ ",yk.YPXH,yp.BFGG,yp.BFDW,yp.BFBZ,0,0,0,yk.KWBM,yp.QZCL,"
							+ date + "," + date).append(" from ");
		}
		sql.append(
				" YK_YPXX yk,YK_TYPK yp  where yk.JGID=:jgid and yk.YKZF!=1  and yk.YPXH not in (select YPXH from YF_YPXX yf where  yf.YFSB="
						+ yfsb + " )  and  yk.YPXH=yp.YPXH  and yp.TYPE in ")
				.append(qxWhere.toString()).append(")");
		parameters.clear();
		parameters.put("jgid", jgid);
		int i = 0;
		try {
			i = dao.doSqlUpdate(sql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房药品信息导入失败", e);
		}
		return i;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 药房药品作废和取消作废
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> validationPharmacyMedicinesInvalid(
			Map<String, Object> body) throws ModelDataOperationException {
		long ypxh = MedicineUtils.parseLong(body.get("ypxh"));
		int yfzf = MedicineUtils.parseInt(body.get("yfzf")) == 1 ? 0 : 1;
		long yfsb = MedicineUtils.parseLong(body.get("yfsb"));
		String jgid = MedicineUtils.parseString(body.get("jgid"));
		Long l;
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("ypxh", ypxh);
		parameter.put("yfsb", yfsb);
		parameters.put("ypxh", ypxh);
		parameters.put("yfsb", yfsb);
		parameters.put("jgid", jgid);
		StringBuffer hql = new StringBuffer();
		String retMsg = "";
		if (yfzf == 0) {
			hql.append("update YF_YPXX set YFZF=:yfzf where YPXH=:ypxh and YFSB=:yfsb");
			parameter.put("yfzf", yfzf);
			try {
				dao.doUpdate(hql.toString(), parameter);
				retMsg = "取消作废成功";
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "药房药品取消作废失败", e);
			}
		} else {
			try {
				hql.append(" YPXH =:ypxh And YFSB =:yfsb  AND JGID =:jgid");
				l = dao.doCount("YF_KCMX", hql.toString(), parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("药房有库存,不能作废!");
				}
				StringBuffer tableNames = new StringBuffer();
				hql = new StringBuffer();
				tableNames.append(" YF_DB02 a,YF_DB01  b ");
				hql.append(" b.SQDH = a.SQDH AND b.SQYF = a.SQYF AND b.JGID = a.JGID AND	b.CKBZ = 1 AND	b.RKBZ = 0 AND	b.SQYF =:yfsb  AND a.YPXH =:ypxh AND   a.JGID =:jgid ");
				l = dao.doCount(tableNames.toString(), hql.toString(),
						parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("调拨药品未入库,不能作废!");
				}
				hql = new StringBuffer();
				hql.append(" b.SQDH = a.SQDH AND b.SQYF = a.SQYF AND	b.JGID = a.JGID AND	b.CKBZ = 0 AND	b.RKBZ = 1 AND	b.TYPB = 1 AND	b.MBYF =:yfsb AND a.YPXH =:ypxh AND a.JGID=:jgid ");
				l = dao.doCount(tableNames.toString(), hql.toString(),
						parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("调拨药品未出库,不能作废!");
				}
				hql = new StringBuffer();
				tableNames = new StringBuffer();
				tableNames.append("YK_CK02 a,YK_CK01 b ");
				parameters.remove("jgid");
				hql.append(" b.CKDH = a.CKDH AND b.CKFS = a.CKFS AND b.XTSB = a.XTSB AND	b.JGID = a.JGID AND	b.LYPB = 0 AND	b.CKPB = 1 AND	b.YFSB =:yfsb AND a.YPXH =:ypxh  ");
				l = dao.doCount(tableNames.toString(), hql.toString(),
						parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("药品申领未领药,不能作废!");
				}
				hql = new StringBuffer();
				hql.append("update YF_YPXX  set YFZF=:yfzf where YPXH=:ypxh and YFSB=:yfsb");
				parameter.put("yfzf", yfzf);
				dao.doUpdate(hql.toString(), parameter);
				retMsg = "作废成功";
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "药房药品作废失败", e);
			}
		}
		return MedicineUtils.getRetMap(retMsg, 200);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 获取当前修改药房药品信息
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPharmacyMedicinesInformation(
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			long ypxh = MedicineUtils.parseLong(body.get("ypxh"));
			long yfsb = MedicineUtils.parseLong(body.get("yfsb"));
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT a.KWBM as KWBM,b.YPMC as YPMC,b.YPGG as YPGG, b.YPDW as YPDW, b.ZXBZ as ZXBZ,a.JGID as JGID,a.YPXH as YPXH,a.YFGG as YFGG,a.YFZF as YFZF,a.YFDW as YFDW,a.YFBZ as YFBZ,a.YFGC as YFGC,a.YFDC as YFDC,a.YFSB as YFSB,a.DRSJ as DRSJ,a.QZCL as QZCL FROM YK_TYPK b,YF_YPXX a WHERE a.YPXH=b.YPXH AND a.YPXH=:ypxh and a.YFSB=:yfsb");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("yfsb", yfsb);
			parameters.put("ypxh", ypxh);
			ret = dao.doLoad(hql.toString(), parameters);
			Map<String, Object> qzcl = new HashMap<String, Object>();
			qzcl.put("key", MedicineUtils.parseInt(ret.get("QZCL")));
			switch (MedicineUtils.parseInt(ret.get("QZCL"))) {
			case 0:
				qzcl.put("text", "每次发药数量取整");
				break;
			case 1:
				qzcl.put("text", "每天发药数量取整");
				break;
			default:
				qzcl.put("text", "不取整");
				break;
			}
			ret.put("QZCL", qzcl);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取药房药品信息失败", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 保存药房药品信息(修改包装)
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void savePharmacyMedicinesInformation(Map<String, Object> body)
			throws ModelDataOperationException {
		double oldYfbz = MedicineUtils.parseDouble(body.get("OLDYFBZ"));
		double yfbz = MedicineUtils.parseDouble(body.get("YFBZ"));
		StringBuffer hql_kcmx_update = new StringBuffer();
		hql_kcmx_update
				.append("update YF_KCMX  set YPSL=(YPSL*:oldYfbz)/:yfbz,LSJG=(LSJG*:yfbz)/:oldYfbz,JHJG=(JHJG*:yfbz)/:oldYfbz,PFJG=(PFJG*:yfbz)/:oldYfbz where YPXH=:ypxh and YFSB=:yfsb and JGID=:jgid");
		Map<String, Object> map_par_kcmx = new HashMap<String, Object>();
		map_par_kcmx.put("oldYfbz", oldYfbz);
		map_par_kcmx.put("yfbz", yfbz);
		map_par_kcmx.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		map_par_kcmx.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		map_par_kcmx.put("jgid", MedicineUtils.parseString(body.get("JGID")));
		try {
			dao.doUpdate(hql_kcmx_update.toString(), map_par_kcmx);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "数量/价格超过最大上限", e);
		}
		try {
			dao.doSave("update", BSPHISEntryNames.YF_YPXX, body, false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "药房药品信息保存验证失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房药品信息保存失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 验证药房药品是否有发生业务(发生业务后药房包装不能修改)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> validationPharmacyMedicinesPackage(
			Map<String, Object> body) throws ModelDataOperationException {
		List<Object> list = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		long yfsb = MedicineUtils.parseLong(body.get("yfsb"));
		long ypxh = MedicineUtils.parseLong(body.get("ypxh"));
		String jgid = MedicineUtils.parseString(body.get("jgid"));
		StringBuffer hqlWhere = new StringBuffer();
		StringBuffer tableNames = new StringBuffer();
		Long l;
		try {
			hqlWhere.append(" a.JGID = b.JGID AND a.YFSB = b.YFSB AND a.YFSB=:yfsb and b.RKFS = a.RKFS AND b.RKDH = a.RKDH AND b.YPXH =:ypxh  AND a.RKPB=0");
			tableNames.append(" YF_RK01 a,YF_RK02 b ");
			parameters.put("yfsb", yfsb);
			parameters.put("ypxh", ypxh);
			l = dao.doCount(tableNames.toString(), hqlWhere.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("尚有该药品的入库单没有确认入库,不能修改!",
						ServiceCode.CODE_RECORD_USING);
			}
			hqlWhere = new StringBuffer();
			tableNames = new StringBuffer();
			hqlWhere.append(" a.JGID = b.JGID AND a.YFSB = b.YFSB AND b.CKFS = a.CKFS AND b.CKDH = a.CKDH AND b.YPXH =:ypxh  AND b.YFSB =:yfsb   AND a.LYPB = 0 AND a.CKPB = 0");
			tableNames.append(" YF_CK01 a,YF_CK02 b ");
			l = dao.doCount(tableNames.toString(), hqlWhere.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("尚有该药品的出库单或申领单没有确认,不能修改!",
						ServiceCode.CODE_RECORD_USING);
			}
			hqlWhere = new StringBuffer();
			tableNames = new StringBuffer();
			hqlWhere.append(" a.JGID = b.JGID and a.XTSB = b.YKSB and a.TJFS = b.TJFS and a.TJDH = b.TJDH and  b.YPXH =:ypxh AND a.JGID =:jgid and a.ZXRQ is null and b.TJSL!=0");
			tableNames.append(" YK_TJ01 a,YF_TJJL b ");
			parameters = new HashMap<String, Object>();
			parameters.put("jgid", jgid);
			parameters.put("ypxh", ypxh);
			l = dao.doCount(tableNames.toString(), hqlWhere.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("尚有该药品的调价没有执行,不能修改!",
						ServiceCode.CODE_RECORD_USING);
			}
			hqlWhere = new StringBuffer();
			tableNames = new StringBuffer();
			hqlWhere.append(" a.CFSB = b.CFSB AND a.JGID = b.JGID  AND b.YPXH =:ypxh  AND a.YFSB =:yfsb  AND a.ZFPB = 0 AND a.FYBZ = 0 and a.MZXH > 0");
			tableNames.append(" MS_CF01 a,MS_CF02  b ");
			parameters = new HashMap<String, Object>();
			parameters.put("yfsb", yfsb);
			parameters.put("ypxh", ypxh);
			l = dao.doCount(tableNames.toString(), hqlWhere.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("尚有该药品的处方单没有发药,不能修改!",
						ServiceCode.CODE_RECORD_USING);
			}
			hqlWhere = new StringBuffer();
			tableNames = new StringBuffer();
			hqlWhere.append(" a.XTSB = b.XTSB AND a.CKFS = b.CKFS AND a.CKDH = b.CKDH AND b.YPXH =:ypxh AND a.YFSB =:yfsb  AND a.LYPB = 0");
			tableNames.append(" YK_CK01 a,YK_CK02  b ");
			l = dao.doCount(tableNames.toString(), hqlWhere.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("尚有该药品的申领单没有确认,不能修改!",
						ServiceCode.CODE_RECORD_USING);
			}
			hqlWhere = new StringBuffer();
			tableNames = new StringBuffer();
			hqlWhere.append(" b.SQDH = a.SQDH AND a.JGID = b.JGID AND a.SQYF = b.SQYF AND b.YPXH =:ypxh AND (a.SQYF =:yfsb OR a.MBYF=:yfsb ) AND b.JGID =:jgid  AND (a.CKBZ = 0 OR a.RKBZ = 0)");
			tableNames.append(" YF_DB01 a, YF_DB02  b ");
			parameters.put("jgid", jgid);
			l = dao.doCount(tableNames.toString(), hqlWhere.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("尚有该药品的调拨单没有确认,不能修改!",
						ServiceCode.CODE_RECORD_USING);
			}
			hqlWhere = new StringBuffer();
			tableNames = new StringBuffer();
			hqlWhere.append(" b.PDDH = a.PDDH AND a.JGID = b.JGID AND a.YFSB = b.YFSB AND a.CKBH = b.CKBH  AND b.YPXH =:ypxh AND b.YFSB =:yfsb  AND b.JGID =:jgid AND a.PDWC = 0");
			tableNames.append(" YF_YK01 a,YF_YK02 b ");
			l = dao.doCount(tableNames.toString(), hqlWhere.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("尚有该药品的盘点单没有完成,不能修改!",
						ServiceCode.CODE_RECORD_USING);
			}
			hqlWhere = new StringBuffer();
			tableNames = new StringBuffer();
			hqlWhere.append(" a.TJXH = b.TJXH AND a.JGID = b.JGID  AND b.YPXH =:ypxh  AND a.TJYF =:yfsb AND a.JGID =:jgid AND b.FYBZ = 0");
			tableNames.append(" BQ_TJ01 a,BQ_TJ02  b ");
			l = dao.doCount(tableNames.toString(), hqlWhere.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("尚有该药品的医嘱没有发药,不能修改!",
						ServiceCode.CODE_RECORD_USING);
			}
			list = new ArrayList<Object>();
			list.add(ServiceCode.CODE_OK);
			list.add("可以修改");
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "验证包装失败", e);
		}
		return MedicineUtils.getRetMap("可以修改", ServiceCode.CODE_OK);
	}

}
