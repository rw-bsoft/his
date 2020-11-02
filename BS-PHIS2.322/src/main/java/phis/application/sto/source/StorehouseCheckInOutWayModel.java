package phis.application.sto.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
/**
 * 药库出入库方式model
 * @author caijy
 *
 */
public class StorehouseCheckInOutWayModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseCheckInOutWayModel.class);

	public StorehouseCheckInOutWayModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-31
	 * @description 验证出入库方式是否已经被使用
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verifiedUsing(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hqlWhere = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		long yksb = MedicineUtils.parseLong(body.get("yksb"));
		int keyValue = MedicineUtils.parseInt(body.get("keyValue"));
		String tableName;
		String fspb = (String) body.get("fspb");
		parameters.put("yksb", yksb);
		parameters.put("keyValue", keyValue);
		if ("rk".equals(fspb)) {
			tableName = "YK_RK01";
			hqlWhere.append(" RKFS=:keyValue and XTSB=:yksb");
		} else {
			tableName = "YK_CK01";
			hqlWhere.append(" CKFS=:keyValue and XTSB=:yksb");
		}
		try {
			long l=dao.doCount(tableName, hqlWhere.toString(), parameters);
			if(l>0){
				return MedicineUtils.getRetMap("已使用");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "判断是否使用失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-31
	 * @description
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> repeatInspection_ckfs(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		StringBuffer hql_lyfs = new StringBuffer();// 防止科室领用方式和药房的申领方式产生冲突
		hql_lyfs.append(" select a.FSMC as FSMC from YK_CKFS a,YF_LYFS b where a.JGID=b.JGID and b.LYFS=a.CKFS and a.XTSB=:yksb and a.JGID=:jgid and a.CKFS=:ckfs");
		Map<String, Object> map_par_lyfs = new HashMap<String, Object>();
		StringBuffer hql_kspb = new StringBuffer();// 判断同一个药库有没有科室领用的方式
		hql_kspb.append(" KSPB=1 and XTSB=:yksb");
		Map<String, Object> map_par_kspb = new HashMap<String, Object>();
		StringBuffer hql_fsmc = new StringBuffer();
		hql_fsmc.append(" XTSB=:yksb and FSMC=:fsmc");
		Map<String, Object> map_par_fsmc = new HashMap<String, Object>();
		String fsmc = body.get("fsmc") + "";// 方式名称
		int kspb = MedicineUtils.parseInt(body.get("kspb"));// 一个药库只能有一个科室领用
		long yksb = MedicineUtils.parseLong(body.get("yksb"));// 药库识别
		int ckfs = 0;
		int dyfs = MedicineUtils.parseInt(body.get("dyfs") == null ? 0 : body
				.get("dyfs"));
		String op = body.get("op") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		map_par_lyfs.put("yksb", yksb);
		map_par_lyfs.put("jgid", jgid);
		map_par_kspb.put("yksb", yksb);
		map_par_fsmc.put("yksb", yksb);
		map_par_fsmc.put("fsmc", fsmc);
		try {
			if ("update".equals(op)) {
				ckfs = MedicineUtils.parseInt(body.get("ckfs"));// 出库方式
				// 已经发生业务数据的不能修改对应方式
				StringBuffer hql_fsyw = new StringBuffer();
				hql_fsyw.append("select count(*) as NUM from YK_CK01  a,YK_CKFS  b where a.CKFS=b.CKFS and a.XTSB=b.XTSB and b.DYFS!=:dyfs and b.CKFS=:ckfs and b.XTSB=:yksb and b.JGID=:jgid");
				Map<String, Object> map_par_fsyw = new HashMap<String, Object>();
				map_par_fsyw.put("dyfs", dyfs);
				map_par_fsyw.put("ckfs", ckfs);
				map_par_fsyw.put("yksb", yksb);
				map_par_fsyw.put("jgid", jgid);
				Map<String, Object> map_fsyw = dao.doLoad(hql_fsyw.toString(),
						map_par_fsyw);
				if (map_fsyw != null
						&& MedicineUtils.parseInt(map_fsyw.get("NUM")) != 0) {
					return MedicineUtils.getRetMap("已产生业务数据，不能修改对应方式！");
				}
				hql_fsmc.append(" and CKFS!=:ckfs");
				hql_kspb.append(" and CKFS!=:ckfs ");
				map_par_fsmc.put("ckfs", ckfs);
				map_par_kspb.put("ckfs", ckfs);
				map_par_lyfs.put("ckfs", ckfs);
				if (kspb == 1) {
					// 判断方式是否为药房申领方式
					Map<String, Object> map_lyfs = dao.doLoad(
							hql_lyfs.toString(), map_par_lyfs);
					if (map_lyfs != null && map_lyfs.get("FSMC") != null) {
						return MedicineUtils
								.getRetMap("已作为药房申领方式，不能再维护它为科室领用！");
					}
					StringBuffer hql_ksly = new StringBuffer();
					hql_ksly.append("select count(*) as NUM from YK_CK01 a,YK_CKFS b where a.CKFS=b.CKFS and a.XTSB=b.XTSB and b.KSPB!=1 and b.CKFS=:ckfs and b.XTSB=:yksb and b.JGID=:jgid");
					map_lyfs = dao.doLoad(hql_ksly.toString(), map_par_lyfs);
					if (map_lyfs != null
							&& MedicineUtils.parseInt(map_lyfs.get("NUM")) != 0) {
						return MedicineUtils.getRetMap("已产生业务数据，不能再维护它为科室领用！");
					}
				} else {
					// 如果发生业务数据的科室领用记录 不能改为非科室领用
					StringBuffer hql_ksly = new StringBuffer();
					hql_ksly.append("select count(*) as NUM from YK_CK01 a,YK_CKFS b where a.CKFS=b.CKFS and a.XTSB=b.XTSB and b.KSPB=1 and b.CKFS=:ckfs and b.XTSB=:yksb and b.JGID=:jgid");
					Map<String, Object> map_lyfs = dao.doLoad(
							hql_ksly.toString(), map_par_lyfs);
					if (map_lyfs != null
							&& MedicineUtils.parseInt(map_lyfs.get("NUM")) != 0) {
						return MedicineUtils.getRetMap("已产生业务数据，不能改为非科室领用！");
					}
				}
			}

			long l = 0;
			// 判断科室领用的方式是否存在
			if (kspb == 1) {
				l = dao.doCount("YK_CKFS", hql_kspb.toString(), map_par_kspb);
				if (l > 0) {
					return MedicineUtils.getRetMap("科室领用方式已经存在！");
				}
			}
			l = dao.doCount("YK_CKFS", hql_fsmc.toString(),
					map_par_fsmc);
			if (l > 0) {
				return MedicineUtils.getRetMap(" 方式名称已经存在！");
			}
			// 判断当前方式是否是申领退药,如果发生业务的申领退药 不能修改对应方式
			if ("update".equals(op)) {
				StringBuffer hql_slty = new StringBuffer();
				hql_slty.append(" a.XTSB=b.XTSB and a.CKFS=b.CKFS and b.DYFS=6 and b.XTSB=:xtsb and  b.CKFS=:ckfs");
				Map<String, Object> map_par_slty = new HashMap<String, Object>();
				map_par_slty.put("xtsb", yksb);
				map_par_slty.put("ckfs", ckfs);
				StringBuffer tableName = new StringBuffer();
				tableName.append("YK_CK01  a,YK_CKFS b");
				l = dao.doCount(tableName.toString(), hql_slty.toString(),
						map_par_slty);
				if (l > 0) {
					if (dyfs != 6) {
						return MedicineUtils
								.getRetMap("已发生业务的申领退药方式 不能修改对应方式!");
					}
				}
			}
			// 判断对应方式是6的情况
			if (dyfs == 6) {
				StringBuffer hql_dyfs = new StringBuffer();
				hql_dyfs.append(" DYFS=6 and XTSB=:yksb ");
				Map<String, Object> map_par_dyfs = new HashMap<String, Object>();
				map_par_dyfs.put("yksb", yksb);
				if ("create".equals(op)) {
					l = dao.doCount("YK_CKFS", hql_dyfs.toString(),
							map_par_dyfs);
					if (l > 0) {
						return MedicineUtils.getRetMap("一个药库申领退药方式只能维护一个");
					}
				} else {
					hql_dyfs.append(" and CKFS!=:ckfs ");
					map_par_dyfs.put("ckfs", ckfs);
					l = dao.doCount("YK_CKFS", hql_dyfs.toString(),
							map_par_dyfs);
					if (l > 0) {
						return MedicineUtils.getRetMap("一个药库申领退药方式只能维护一个");
					}
					hql_dyfs = new StringBuffer();
					hql_dyfs.append("YKSB=:yksb and LYFS=:ckfs");
					l = dao.doCount("YF_LYFS", hql_dyfs.toString(),
							map_par_dyfs);
					if (l > 0) {
						return MedicineUtils.getRetMap("已维护成药房领用的方式不能维护成申领退药");
					}
				}
			}
			// 判断盘亏的情况
			if (dyfs == 4) {
				if ("update".equals(op)) {
					Map<String, Object> map_lyfs = dao.doLoad(
							hql_lyfs.toString(), map_par_lyfs);
					if (map_lyfs != null && map_lyfs.get("FSMC") != null) {
						return MedicineUtils.getRetMap(" 已作为药房申领方式，不能再维护它为盘亏！");
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询名称重复失败", e);
		}
		return MedicineUtils.getRetMap();
	}

}
