package phis.application.sto.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

public class StorehouseBasicInfomationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseBasicInfomationModel.class);

	public StorehouseBasicInfomationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 药库删除前验证
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verifiedUsing_yklb(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql_yp = new StringBuffer();// 查询有没导入药品
		StringBuffer hql_qx = new StringBuffer();// 查询业务权限维护是否已经维护
		Map<String, Object> map_par = new HashMap<String, Object>();
		hql_yp.append(" YKSB=:yksb and JGID=:jgid");
		hql_qx.append(" YWLB='5' and KSDM=:yksb and JGID=:jgid");
		map_par.put("yksb", MedicineUtils.parseLong(body.get("yksb")));
		map_par.put("jgid", MedicineUtils.parseString(body.get("jgid")));
		long l = 0;
		try {
			l = dao.doCount("YK_YPXX", hql_yp.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已经调入药品，不能删除!");
			}
			l = dao.doCount("GY_QXKZ", hql_qx.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("药库权限已有员工使用，不能删除!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药库删除验证失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 保存前判断名称是否重复
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> repeatInspection(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hqlWhere = new StringBuffer();
		Map<String, Object> map_par = new HashMap<String, Object>();
		long pkey = MedicineUtils.parseLong(body.get("pkey"));
		String pb = MedicineUtils.parseString(body.get("pb"));
		map_par.put("mc", MedicineUtils.parseString(body.get("mc")));
		try {
			if ("rk".equals(pb)) {
				map_par.put("yksb", MedicineUtils.parseLong(body.get("yksb")));
				hqlWhere.append(" FSMC=:mc and XTSB=:yksb");
				if (pkey != 0) {
					hqlWhere.append(" and RKFS!=:pkey");
					map_par.put("pkey", MedicineUtils.parseInt(pkey));
				}
				long l = dao.doCount("YK_RKFS", hqlWhere.toString(), map_par);
				if (l > 0) {
					return MedicineUtils.getRetMap("方式名称已经存在!");
				}
				if (pkey != 0) {
					hqlWhere = new StringBuffer();
					hqlWhere.append(" a.RKFS=b.RKFS and a.RKFS=:rkfs and a.DYFS!=:dyfs");
					map_par.clear();
					map_par.put("rkfs", MedicineUtils.parseInt(pkey));
					map_par.put("dyfs",
							MedicineUtils.parseInt(body.get("dyfs")));
					l = dao.doCount("YK_RKFS a,YK_RK01 b", hqlWhere.toString(),
							map_par);
					if (l > 0) {
						return MedicineUtils.getRetMap("已产生业务,不能修改是否采购方式!");
					}
				}

			} else if ("yk".equals(pb)) {
				Long yksb = MedicineUtils.parseLong(body.get("pkey"));
				map_par.put("jgid", MedicineUtils.parseString(body.get("jgid")));
				hqlWhere.append(" YKMC=:mc and JGID=:jgid");
				if (yksb != 0) {
					hqlWhere.append(" and YKSB!=:yksb");
					map_par.put("yksb", yksb);
				}
				long l = dao.doCount("YK_YKLB", hqlWhere.toString(), map_par);
				if (l > 0) {
					return MedicineUtils.getRetMap("药库名称已经存在!");
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询名称重复失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 查询药库是否已经初始化
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> querySystemInit(Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql_count = new StringBuffer();
		hql_count.append(" GROUPID=2 and OFFICEID=:yksb and INIT=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")));
		try {
			long l = dao.doCount("GY_CSH", hql_count.toString(),
					map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("药库未初始化",ServiceCode.CODE_RECORD_NOT_FOUND);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询初始化失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 查询药库是否已经转账
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> initialQuery(Context ctx) throws ModelDataOperationException {
		Map<String,Object> body=new HashMap<String,Object>();
		body.put("keyName", "XTSB");
		body.put("keyValue", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")));
		body.put("tableName", "YK_JZJL");
		try{
			MedicineCommonModel model=new MedicineCommonModel(dao);
		if(model.repeatVerification(body)){
			return MedicineUtils.getRetMap();
		}
		}catch(Exception e){
			MedicineUtils.throwsException(logger, "是否初始转账查询失败", e);
		}
		return MedicineUtils.getRetMap("药库未初始转账");
	}

}
