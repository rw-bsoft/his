package phis.application.sto.source;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

import phis.application.mds.source.MedicineManageModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
/**
 * 药库药品信息管理
 * @author caijy
 *
 */
public class StorehouseMedicineManageModel {
	public static Double progressValue = 0.0;// 进度值，需要进度条显示时设置
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseMedicineManageModel.class);

	public StorehouseMedicineManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 药品私用信息作废和取消作废
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> invalidPrivateMedicines(Map<String, Object> body)
			throws ModelDataOperationException {
		long ypxh = MedicineUtils.parseLong(body.get("ypxh"));
		int op = MedicineUtils.parseInt(body.get("op"));
		String jgid = MedicineUtils.parseString(body.get("jgid"));
		long yksb = MedicineUtils.parseLong(body.get("yksb"));
		int ykzf;
		if (op == 0) {
			ykzf = 1;
		} else {
			ykzf = 0;
		}
		Long l;
		String hqlWhere = "";
		StringBuffer sql = new StringBuffer();
		StringBuffer tableNames = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("ypxh", ypxh);
		parameter.put("jgid", jgid);
		parameters.put("ypxh", ypxh);
		parameters.put("jgid", jgid);
		parameters.put("yksb", yksb);
		sql.append("update YK_YPXX set YKZF=:ykzf where YPXH=:ypxh and JGID=:jgid");
		if (ykzf == 0) {
			parameter.put("ykzf", ykzf);
			try {
				dao.doUpdate(sql.toString(), parameter);
				return MedicineUtils.getRetMap();
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "药品信息取消作废失败.", e);
			}
		} else {
			try {
				// 注释掉的是因为功能未做表没创建,以后功能完善后取消注释
				hqlWhere = " YPXH =:ypxh and JGID=:jgid";
				l = dao.doCount("YK_KCMX", hqlWhere, parameter);
				if (l > 0) {
					return MedicineUtils.getRetMap("药库有库存不能作废");
				}
				hqlWhere = " a.XTSB = b.XTSB AND a.RKFS = b.RKFS AND a.RKDH = b.RKDH  AND a.RKPB = 0  AND b.YPXH =:ypxh and a.JGID=:jgid";
				tableNames.append("YK_RK01 a,YK_RK02 b ");
				l = dao.doCount(tableNames.toString(), hqlWhere, parameter);
				if (l > 0) {
					return MedicineUtils.getRetMap("有入库单未实物入库不能作废");
				}
				hqlWhere = "a.XTSB = b.XTSB AND a.CKFS = b.CKFS AND a.CKDH = b.CKDH AND b.XTSB =:yksb AND b.CKFS = 99 AND b.LYPB = 1  AND b.SQTJ = 1  AND b.CKPB = 0  AND a.YPXH =:ypxh AND b.JGID =:jgid";
				tableNames = new StringBuffer();
				tableNames.append("YK_CK02  a,YK_CK01 b");
				l = dao.doCount(tableNames.toString(), hqlWhere, parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("有药房已退药而药库未确认数据不能作废");
				}
				// hqlWhere =
				// "a.XTSB = b.XTSB AND a.JHDH = b.JHDH AND b.XTSB =:yksb AND b.JHZT <= 1 AND a.YPXH =:ypxh AND b.JGID = a.JGID AND b.JGID =:jgid";
				// tableNames = new StringBuffer();
				// tableNames.append(BSPHISTableNames.YK_JH02).append(" a,")
				// .append(BSPHISTableNames.YK_JH01).append(" b ");
				// l = dao.doCount(tableNames.toString(), hqlWhere, parameter);
				// if (l > 0) {
				// list.add(ServiceCode.CODE_RECORD_USING);
				// list.add("有计划单未审批或未执行不能作废");
				// return list;
				// }
				hqlWhere = "a.XTSB = b.XTSB AND a.TJFS = b.TJFS AND a.TJDH = b.TJDH AND b.ZYPB = 0 AND a.YPXH =:ypxh 	AND b.JGID =:jgid";
				tableNames = new StringBuffer();
				tableNames.append("YK_TJ02 a,YK_TJ01 b ");
				l = dao.doCount(tableNames.toString(), hqlWhere, parameter);
				if (l > 0) {
					return MedicineUtils.getRetMap("有调价单未执行不能作废");
				}
				parameter.put("ykzf", ykzf);
				dao.doUpdate(sql.toString(), parameter);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "药品信息作废失败.", e);
			}
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 药品私用信息保存(包括价格保存)
	 * @updateInfo
	 * @param op
	 * @param body
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveMedicinesPrivateInformation(String op,
			Map<String, Object> body) throws ModelDataOperationException {
		try {
			dao.doSave(op, "phis.application.sto.schemas.YK_YPXX_SAVE", body,
					false);// 保存基础信息YP_TYPK
			long ypxh = MedicineUtils.parseLong(body.get("YPXH"));
			String jgid = MedicineUtils.parseString(body.get("JGID"));
			MedicineManageModel mmm = new MedicineManageModel(dao);
			mmm.updatePharmacyMedicinesUpdateTime(ypxh, jgid);// 更新药房药品信息的修改时间
			ArrayList<Map<String, Object>> priceInfos = (ArrayList<Map<String, Object>>) body
					.get("priceTab");
			StringBuffer hql_ypcd_count = new StringBuffer();// 查询yk_ypcd是否有该产地
			StringBuffer hql_ypcd_update = new StringBuffer();// 更新yk_ypcd
			hql_ypcd_count
					.append("select LSJG as LSJG,JHJG as JHJG from YK_YPCD where YPXH=:ypxh and YPCD=:ypcd");
			hql_ypcd_update
					.append("update YK_YPCD set LSJG=:LSJG,JHJG=:JHJG where YPXH=:ypxh and YPCD=:ypcd");
			if (priceInfos != null && priceInfos.size() > 0) {
				for (Map<String, Object> alias : priceInfos) {
					if ("create".equals(alias.get("_opStatus"))) {
						alias.put("YPXH", ypxh);
						alias.put("JGID", jgid);
						// 同步yk_ypcd
						Map<String, Object> map_par = new HashMap<String, Object>();
						map_par.put("ypxh", ypxh);
						map_par.put("ypcd",
								MedicineUtils.parseLong(alias.get("YPCD")));
						Map<String, Object> map_ypcd = dao.doLoad(
								hql_ypcd_count.toString(), map_par);
						if (map_ypcd != null && map_ypcd.size() > 0) {
							map_ypcd.put("ypxh", ypxh);
							map_ypcd.put("ypcd",
									MedicineUtils.parseLong(alias.get("YPCD")));
							if (MedicineUtils.parseDouble(alias.get("LSJG")) > MedicineUtils
									.parseDouble(map_ypcd.get("LSJG"))) {
								map_ypcd.put("LSJG", MedicineUtils
										.parseDouble(alias.get("LSJG")));
							}
							if (MedicineUtils.parseDouble(alias.get("JHJG")) > MedicineUtils
									.parseDouble(map_ypcd.get("JHJG"))) {
								map_ypcd.put("JHJG", MedicineUtils
										.parseDouble(alias.get("JHJG")));
							}
							dao.doUpdate(hql_ypcd_update.toString(), map_ypcd);
						} else {
							dao.doSave("create", BSPHISEntryNames.YK_YPCD,
									alias, false);
						}
						dao.doSave("create", BSPHISEntryNames.YK_CDXX, alias,
								false);
					} else {
						dao.doSave("update", BSPHISEntryNames.YK_CDXX, alias,
								false);// 如果需要以后改成只更新修改过的数据
					}
				}

			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品信息保存失败.", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "药品信息保存失败.", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 药品私用信息修改界面数据 查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadPirvateMedicinesInformation(
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> m = new HashMap<String, Object>();
		try {
			String jgid = MedicineUtils.parseString(body.get("JGID"));
			long ypxh = MedicineUtils.parseLong(body.get("YPXH"));
			StringBuffer hql = new StringBuffer();
			StringBuffer selectHql = new StringBuffer();
			hql.append("select ");
			Schema sc = SchemaController.instance().get(
					"phis.application.sto.schemas.YK_YPXX");
			for (SchemaItem si : sc.getItems()) {
				if (si.isVirtual()) {
					continue;
				}
				String f = "";
				if (si.isRef()) {
					f = MedicineUtils
							.parseString(si.getProperties().get("ref"));
					selectHql.append(",").append(f).append(" as ")
							.append(f.substring(2, f.length()));
				} else {
					f = si.getId();
					selectHql.append(",a.").append(f).append(" as ").append(f);
				}
			}
			hql.append(selectHql.substring(1))
					.append(" from YK_YPXX a,YK_TYPK b where a.YPXH=b.YPXH and a.YPXH=:ypxh and a.JGID=:jgid");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ypxh", ypxh);
			parameters.put("jgid", jgid);
			m = dao.doLoad(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品私用信息查询失败.", e);
		} catch (ControllerException e) {
			MedicineUtils.throwsException(logger, "药品私用信息查询失败.", e);
		}
		return SchemaUtil.setDictionaryMassageForForm(m,
				"phis.application.sto.schemas.YK_YPXX");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 价格注销和恢复
	 * @updateInfo
	 * @param record
	 * @param op
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> reomovePriceInformation(
			Map<String, Object> record, int op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ypxh", MedicineUtils.parseLong(record.get("YPXH")));
		parameters.put("ypcd", MedicineUtils.parseLong(record.get("YPCD")));
		parameters.put("jgid", MedicineUtils.parseString(record.get("JGID")));
		long l = 0;
		StringBuffer hql_update = new StringBuffer();// 注销或恢复
		hql_update
				.append("update YK_CDXX set ZFPB=:zfpb where JGID=:jgid and YPCD=:ypcd and YPXH=:ypxh");
		StringBuffer hql_count = new StringBuffer();
		hql_count.append(" YPCD=:ypcd and JGID=:jgid and YPXH=:ypxh ");// 统计是否有库存
		StringBuffer hql_count_tj = new StringBuffer();// 统计是否有调价
		hql_count_tj
				.append(" a.XTSB =b.XTSB and a.TJFS=b.TJFS and a.TJDH=b.TJDH and a.ZYPB =0 and b.YPXH=:ypxh and b.YPCD=:ypcd and a.JGID=b.JGID and a.JGID=:jgid");
		StringBuffer tableName = new StringBuffer();
		tableName.append("YK_TJ01  a,YK_TJ02 b ");
		try {
			if (op != 0) {
				l = dao.doCount("YF_KCMX", hql_count.toString(), parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("药房有库存,不能注销产地");
				}
				l = dao.doCount(tableName.toString(), hql_count_tj.toString(),
						parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("有调价单未执行不能注销产地");
				}
				StringBuffer hql = new StringBuffer();
				hql.append("YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
				l = dao.doCount("YK_KCMX", hql.toString(), parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("药库有库存不能产地注销");
				}
				hql.append(" and KCSB = 0");
				l = dao.doCount("YK_RK02", hql.toString(), parameters);
				if (l > 0) {
					return MedicineUtils.getRetMap("有入库单未实物入库不能产地注销");
				}
				tableName = new StringBuffer();
				tableName.append("YK_CK01  a,YK_CK02 b ");
				hql = new StringBuffer();
				hql.append(" a.XTSB=b.XTSB and a.CKDH=b.CKDH and a.CKFS=b.CKFS and a.LYPB=0 and a.YFSB>0  and b.YPXH=:ypxh and b.YPCD=:ypcd and a.JGID=:jgid");
				if (l > 0) {
					return MedicineUtils.getRetMap("药库出库确认，但药房没有入库确认,不能注销产地");
				}
				hql = new StringBuffer();
				hql.append(" a.XTSB=:xtsb and a.CKFS=99 and a.LYPB=1 and a.SQTJ=1 and a.CKPB=0 and b.YPXH=:ypxh and b.YPCD=:ypcd and a.JGID=:jgid and a.XTSB=b.XTSB and a.CKDH=b.CKDH and a.CKFS=b.CKFS");
				parameters.put("xtsb", yksb);
				l = dao.doCount(tableName.toString(), hql.toString(),
						parameters);
				if (l > 0) {
					return MedicineUtils
							.getRetMap("有药房已确认退药而药库没有确认退药单存在,不能注销产地");
				}
			}
			parameters.remove("xtsb");
			parameters.put("zfpb", op);
			dao.doUpdate(hql_update.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			if (op == 0) {
				MedicineUtils.throwsException(logger, "价格恢复失败.", e);
			} else {
				MedicineUtils.throwsException(logger, "价格注销失败.", e);
			}
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 查询是否中心控制价格
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int queryControlPrices(Context ctx)
			throws ModelDataOperationException {
		int zxkzjg=0;
		try{
		zxkzjg=MedicineUtils
				.parseInt(ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(),
						BSPHISSystemArgument.ZXKZJG, ctx));
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.ZXKZJG, e);
		}
		return zxkzjg;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 私用药品修改价格时检查下是否有库存,出入库单,调价单
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryMedicinesStock(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> map_par_count = new HashMap<String, Object>();
		map_par_count.put("jgid", UserRoleToken.getCurrent().getManageUnit()
				.getId());
		map_par_count.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
		map_par_count.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		StringBuffer hql_count = new StringBuffer();// 库存校验
		hql_count.append(" YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
		StringBuffer hql_rk = new StringBuffer();// 入库单校验
		StringBuffer tableName_rk = new StringBuffer();
		hql_rk.append(" a.RKFS=b.RKFS and a.RKDH=b.RKDH and a.JGID=b.JGID and YPXH=:ypxh and YPCD=:ypcd and a.JGID=:jgid ");
		tableName_rk.append("YF_RK01 a,YF_RK02 b ");
		StringBuffer hql_ck = new StringBuffer();// 出库单校验
		StringBuffer hql_tj=new StringBuffer();
		hql_tj.append("  YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid and TJSL>0");
		StringBuffer tableName_ck = new StringBuffer();
		hql_ck.append(" a.CKFS=b.CKFS and a.CKDH=b.CKDH and a.JGID=b.JGID and YPXH=:ypxh and YPCD=:ypcd and a.JGID=:jgid ");
		tableName_ck.append("YF_CK01  a,YF_CK02 b ");
		try {
			long l = dao
					.doCount("YF_KCMX", hql_count.toString(), map_par_count);
			if (l > 0) {
				return MedicineUtils.getRetMap("已有库存,不能修改!");
			}
			l = dao.doCount(tableName_rk.toString(), hql_rk.toString(),
					map_par_count);
			if (l > 0) {
				return MedicineUtils.getRetMap("已有入库单,不能修改!");
			}
			l = dao.doCount(tableName_ck.toString(), hql_ck.toString(),
					map_par_count);
			if (l > 0) {
				return MedicineUtils.getRetMap("已有出库单,不能修改!");
			}
			l = dao.doCount("YF_TJJL", hql_tj.toString(),
					map_par_count);
			if (l > 0) {
				return MedicineUtils.getRetMap("已有调价单,不能修改!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询是否有库存失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 查询批零加成
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public double queryPljc(Context ctx) throws ModelDataOperationException {
		double pljc=1;
		try{
		 pljc=MedicineUtils.parseDouble(ParameterUtil.getParameter(
				ParameterUtil.getTopUnitId(), BSPHISSystemArgument.PLJC, ctx));
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.PLJC, e);
		}
		return pljc;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 药品私用信息调入
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveMedicinesPrivateImportInformation(
			List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
//		StringBuffer hql_delete = new StringBuffer();
//		hql_delete
//				.append("delete from YK_YPXX where YPXH=:ypxh and JGID=:jgid");
		StringBuffer hql_yp=new StringBuffer();//判断下该药库有无该药品
		hql_yp.append(" YPXH=:ypxh and YKSB=:yksb");
		int size = body.size();
		try {
			StringBuffer hql_allJg=new StringBuffer();//查询当前药库下面所有产地信息
			hql_allJg.append("select YPCD as YPCD,YPXH as YPXH from YK_CDXX where JGID=:jgid and YPXH in (select YPXH as YPXH from YK_YPXX where YKSB=:yksb)");
			Map<String,Object> map_par_cd=new HashMap<String,Object>();
			map_par_cd.put("jgid", jgid);
			map_par_cd.put("yksb", yksb);
			List<Map<String,Object>> list_allCdxx=dao.doSqlQuery(hql_allJg.toString(), map_par_cd);
			for (int i = 0; i < size; i++) {
			DecimalFormat df = new DecimalFormat("#.00");
			progressValue = MedicineUtils
					.parseDouble(df.format((i + 1) / size));
			Map<String, Object> data = (Map<String, Object>) body.get(i);
			if (data != null) {
				data.put("JGID", jgid);
				data.put("YKSB", yksb);
				data.put("GCSL", 0);// 高储数量
				data.put("DCSL", 0);// 低储数量
				data.put("YKZF", 0);// 药库作废
					Map<String,Object> map_par_yp=new HashMap<String,Object>();
					map_par_yp.put("ypxh", MedicineUtils.parseLong(data.get("YPXH")));
					map_par_yp.put("yksb", yksb);
					long l=dao.doCount("YK_YPXX", hql_yp.toString(), map_par_yp);
					if(l==0){
						// 保存药品信息
						dao.doSave("create",
								"phis.application.sto.schemas.YK_YPXX_SAVE", data,
								false);
					}
					// 保存价格信息
					List<Map<String, Object>> list_price = dao.doList(
							CNDHelper.toListCnd("['eq',['$','YPXH'],['i',"
									+ MedicineUtils.parseLong(data.get("YPXH"))
									+ "]]"), null, BSPHISEntryNames.YK_YPCD);
					for (Map<String, Object> map_price : list_price) {
						int isCz=0;//判断价格是否存在
						for(Map<String,Object> map_ykcd:list_allCdxx){
							if(MedicineUtils.parseLong(map_ykcd.get("YPXH"))==MedicineUtils.parseLong(map_price.get("YPXH"))&&MedicineUtils.parseLong(map_ykcd.get("YPCD"))==MedicineUtils.parseLong(map_price.get("YPCD"))){
								isCz=1;
								break;
							}
						}
						if(isCz==1){
							continue;
						}
						map_price.put("JGID", jgid);
						map_price.put("PFJG", Double.parseDouble("0"));
						map_price.put("GYJJ", map_price.get("JHJG"));
						map_price.put("GYLJ", map_price.get("LSJG"));
						dao.doSave("create", BSPHISEntryNames.YK_CDXX,
								map_price, false);
					}
			}
		}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "导入药品信息失败.", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "导入药品信息失败.", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "导入药品信息失败.", e);
		}

		//将调入的药品 调入要机构下所有药房中
		List<Map<String,Object>> list_jgid=new ArrayList<Map<String,Object>>();
		Map<String,Object> map_jgid=new HashMap<String,Object>();
		map_jgid.put("JGID", jgid);
		list_jgid.add(map_jgid);
		MedicineManageModel mmm=new MedicineManageModel(dao);
		mmm.saveYpxxForYf(list_jgid,body);
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 需要导入的药品信息列表查询
	 * @updateInfo
	 * @param cnds
	 * @param parameters
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> medicinesPrivateInformationList(List<Object> cnds,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> inofList=new ArrayList<Map<String,Object>>();
		int total=0;
		try {
			if (cnds == null) {
				cnds = (List<Object>) CNDHelper
						.toListCnd("['eq',['s','1'],['s','1']]");
			}
			StringBuffer hql = new StringBuffer();
			Map<String, Object> map_par = new HashMap<String, Object>();
			String jgid=UserRoleToken.getCurrent().getManageUnit().getId();
			long yksb= MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
			map_par.put("jgid", jgid);
			map_par.put("yksb", yksb);
			hql.append(
					"select yp.YPXH as YPXH,yp.YPMC as YPMC,yp.YPGG as YPGG,yp.YPDW as YPDW,yp.YPSX as YPSX,jx.SXMC as YPSX_text,yp.PYDM as PYDM,yp.TYPE as CFLX from YK_TYPK yp,YK_YPSX jx where yp.YPSX=jx.YPSX and ")
					.append(ExpressionProcessor.instance().toString(cnds).replaceAll("str", "to_char"))
					.append(" and (YPXH not in (select YPXH from YK_YPXX  where JGID=:jgid) or YPXH in (select YPXH from YK_YPXX  where YKSB=:yksb) ) and ZFPB!=1")
					.append(" and yp.GRADE in (select grade from SYS_Organization where organizCode=:jgid)")
					;
//			StringBuffer countHql = new StringBuffer();
//			countHql.append(ExpressionProcessor.instance().toString(cnds))
//					.append(" and YPXH not in (select YPXH from YK_YPXX where JGID=:jgid) and ZFPB!=1");
//			total = dao.doCount("YK_TYPK yp",
//					countHql.toString(), map_par);
//			if(total==0){
//				return MedicineUtils.getRetMap(total, inofList);
//			}
//			MedicineUtils.getPageInfo(req, map_par);
//			inofList = dao.doQuery(hql.toString(),
//					map_par);
			List<Map<String,Object>> list_allRecord=new ArrayList<Map<String,Object>>();
			list_allRecord=dao.doSqlQuery(hql.toString(), map_par);
			if(list_allRecord==null||list_allRecord.size()==0){
				return MedicineUtils.getRetMap(total, inofList);
			}
			StringBuffer hql_ypjg=new StringBuffer();
			hql_ypjg.append("select count(*) as NUM,b.YPXH as YPXH from YK_TYPK b left outer join YK_YPCD a on a.YPXH=b.YPXH  left outer join YK_YPXX c on b.YPXH=c.YPXH and c.YKSB=:yksb left outer join SYS_Organization d on d.organizCode=:jgid and b.grade = d.grade where  (c.YPXH is null or (a.YPCD is not null and a.YPCD not in (select YPCD from YK_CDXX d where d.YPXH=b.YPXH and JGID=:jgid))) group by b.YPXH");
			List<Map<String,Object>> list_record=new ArrayList<Map<String,Object>>();
			Map<String,Object> map_par_gl=new HashMap<String,Object>();
			map_par_gl.put("jgid", jgid);
			map_par_gl.put("yksb", yksb);
			List<Map<String,Object>> l=dao.doSqlQuery(hql_ypjg.toString(), map_par_gl);
			for(Map<String,Object> map_record:list_allRecord){
				Map<String,Object> m=MedicineUtils.getRecord(l, new String[]{"YPXH"}, map_record, new String[]{"YPXH"});
				if(m!=null){
					long num=MedicineUtils.parseLong(m.get("NUM"));
					if(num>0){
						list_record.add(map_record);
					}
				}
			}
			total=list_record.size();
			Map<String,Object> map_temp=new HashMap<String,Object>();
			MedicineUtils.getPageInfo(req, map_temp);
			if(map_temp.size()==0){
				throw new ModelDataOperationException("分页信息获取失败");
			}
			int first=MedicineUtils.parseInt(map_temp.get("first"));
			int max=MedicineUtils.parseInt(map_temp.get("max"));
			int to=0;
			if(total>=(first+max)){
				to=(first+max);
			}else if(total>first&&total<(first+max)){
				to=total;
			}else{
				return MedicineUtils.getRetMap(total, inofList);	
			}
			for(int i=first;i<to;i++){
				inofList.add(list_record.get(i));
			}
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "获取导入药品信息失败.", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取导入药品信息失败.", e);
		}
		return MedicineUtils.getRetMap(total, inofList);
	}
	
	
}
