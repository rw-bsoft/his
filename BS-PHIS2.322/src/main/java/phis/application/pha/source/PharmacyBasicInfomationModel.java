package phis.application.pha.source;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.account.organ.OrganController;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

/**
 * 药房基本信息维护model
 * 
 * @author caijy
 * 
 */
public class PharmacyBasicInfomationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyBasicInfomationModel.class);

	public PharmacyBasicInfomationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 药房是否系统初始化查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> querySystemInit(Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql_count = new StringBuffer();
		hql_count.append(" GROUPID=3 and OFFICEID=:yfsb and INIT=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put(
				"yfsb",
				MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty(
						"pharmacyId")));
		try {
			long l = dao.doCount("GY_CSH", hql_count.toString(), map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("药房未初始化",
						ServiceCode.CODE_RECORD_NOT_FOUND);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询初始化失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 领药方式查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryReceiveWay(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		long il_yfsb = MedicineUtils.parseLong(body.get("YFSB"));
		int ll_lyfs = MedicineUtils.parseInt(body.get("LYFS"));
		long yksb = MedicineUtils.parseLong(body.get("YKSB"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("YFSB", il_yfsb);
		parameters.put("CKFS", ll_lyfs);
		parameters.put("YKSB", yksb);
		try {
			long count = dao.doCount("YK_CK01",
					"XTSB = :YKSB  AND YFSB = :YFSB AND CKFS = :CKFS ",
					parameters);
			if (count > 0) {
				return MedicineUtils.getRetMap("已发生过业务数据,不能修改申领方式!");
			}
			Map<String, Object> parameters1 = new HashMap<String, Object>();
			parameters1.put("YKSB", yksb);
			List<Map<String, Object>> reList = dao
					.doSqlQuery(
							"SELECT distinct a.CKFS,a.FSMC FROM YK_CKFS a WHERE a.KSPB = 0 and a.DYFS!=6 and a.DYFS!=4 and a.XTSB = :YKSB AND a.CKFS NOT IN (SELECT LYFS FROM YF_LYFS WHERE YKSB = :YKSB )",
							parameters1);
			if (reList.size() == 0) {
				return MedicineUtils.getRetMap("没有可选择领用方式!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药方式查询失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 查询领药方式
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> listReceiveWay(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		long yksb = MedicineUtils.parseLong(body.get("YKSB"));// 用户的药库识别
		String jgid = body.get("JGID") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		parameters.put("YKSB", yksb);
		List<Map<String, Object>> reList = null;
		try {
			reList = dao
					.doSqlQuery(
							"SELECT distinct a.CKFS as CKFS,a.FSMC as FSMC FROM YK_CKFS a WHERE a.KSPB = 0 and a.DYFS!=6 and a.DYFS!=4 and a.JGID = :JGID AND a.XTSB = :YKSB AND a.CKFS NOT IN (SELECT LYFS FROM YF_LYFS WHERE YKSB = :YKSB AND JGID =:JGID)",
							parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药库领用方式查询失败!", e);
		}
		return reList;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 保存领药方式
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveReceiveWay(List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		try {
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> YF_LYFS = body.get(i);
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("JGID", YF_LYFS.get("JGID"));
				parameters.put("YFSB",
						MedicineUtils.parseLong(YF_LYFS.get("YFSB")));
				parameters.put("YKSB",
						MedicineUtils.parseLong(YF_LYFS.get("YKSB")));
				dao.doUpdate(
						"delete from YF_LYFS where JGID=:JGID and YFSB=:YFSB and YKSB=:YKSB",
						parameters);
				dao.doSave("create", BSPHISEntryNames.YF_LYFS, YF_LYFS, false);
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "领药方式保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药方式保存失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 重置领药方式
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> updateReceiveWay(List<Map<String, Object>> body,
			Context ctx) throws ModelDataOperationException {
		try {
			String msg = "";
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> YF_LYFS = body.get(i);
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("YKSB",
						MedicineUtils.parseLong(YF_LYFS.get("YKSB")));
				parameters.put("YFSB",
						MedicineUtils.parseLong(YF_LYFS.get("YFSB")));
				parameters.put("CKFS",
						MedicineUtils.parseInt(YF_LYFS.get("LYFS")));
				parameters.put("JGID", YF_LYFS.get("JGID"));
				long count = dao
						.doCount(
								"YK_CK01",
								"XTSB = :YKSB  AND YFSB = :YFSB AND CKFS = :CKFS And JGID = :JGID",
								parameters);
				if (count > 0) {
					if (msg.length() > 0) {
						msg += "," + YF_LYFS.get("YKMC");
					} else {
						msg += YF_LYFS.get("YKMC");
					}
				} else {
					dao.doUpdate(
							"delete from YF_LYFS where JGID=:JGID and YFSB=:YFSB and YKSB=:YKSB and LYFS=:CKFS",
							parameters);
				}
			}
			if (msg.length() > 0) {
				msg += "所选的申领方式已发生过业务数据,不能修改申领方式!";
				return MedicineUtils.getRetMap(msg);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "重置领药方式失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 领药方式维护查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryReceiveWayMaintain(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String type = user.getManageUnit().getType();
		String parentJGID = user.getManageUnit().getParent().getId();
		String organId = UserRoleToken.getCurrent().getOrganId();
		long yfsb = MedicineUtils.parseLong(body.get("YFSB"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("YFSB", yfsb);
		parameters.put("JGID", jgid);
		StringBuffer hql = new StringBuffer();
		if ("D".equals(type)) {
			hql.append("SELECT * FROM (SELECT a.YFSB as YFSB,a.YKSB as YKSB,b.YKMC as YKMC,a.LYFS as LYFS,a.JGID as JGID FROM YF_LYFS a,YK_YKLB b where ( a.YKSB = b.YKSB )  and ( a.YFSB = :YFSB )  union all select "
					+ yfsb
					+ " as YFSB,a.YKSB as YKSB,a.YKMC as YKMC,0 as LYFS,a.JGID as JGID from  YK_YKLB  a where (a.JGID=:JGID or a.JGID=:parentJGID) and a.yksb not in (select YKSB from YF_LYFS where  YFSB=:YFSB) ) ORDER BY JGID,YKSB ");
			parameters.put("parentJGID", parentJGID);
		} else {
			hql.append("SELECT * FROM (SELECT a.YFSB as YFSB,a.YKSB as YKSB,b.YKMC as YKMC,a.LYFS as LYFS,a.JGID as JGID FROM YF_LYFS a,YK_YKLB  b where ( a.YKSB = b.YKSB )  and ( a.YFSB = :YFSB )  union all select "
					+ yfsb
					+ "  as YFSB,a.YKSB as YKSB,a.YKMC as YKMC,0 as LYFS,a.JGID as JGID from YK_YKLB  a where (a.JGID=:JGID) and a.yksb not in (select YKSB from YF_LYFS where  YFSB=:YFSB) ) ORDER BY  JGID,YKSB");
		}
		List<Map<String, Object>> reList = null;
		try {
			reList = dao.doSqlQuery(hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(reList,
					"phis.application.sto.schemas.YK_YKLB_LYFS");
			for (Map<String, Object> map : reList) {
				map.put("JGMC",
						// OrganController.instance().get(parseString(map.get("JGID"))).getName()
						OrganController.lookupManageUnit(
								MedicineUtils.parseString(map.get("JGID")),
								organId).getName());
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药库领用方式查询失败!", e);
		} catch (ControllerException e) {
			MedicineUtils.throwsException(logger, "药库领用方式查询失败!", e);
		}
		return reList;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 检验新增时药房窗口编号是否重复
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> windowNumberRepeatInspection(
			Map<String, Object> body) throws ModelDataOperationException {
		int ckbh = MedicineUtils.parseInt(body.get("ckbh"));
		long yfsb = MedicineUtils.parseLong(body.get("yfsb"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ckbh", ckbh);
		parameters.put("yfsb", yfsb);
		StringBuffer hqlWhere = new StringBuffer();
		hqlWhere.append("CKBH=:ckbh and YFSB=:yfsb");
		try {
			long l = dao.doCount("YF_CKBH", hqlWhere.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("窗口编号重复");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "窗口编号重复查询失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 更新窗口信息(双主键并且主键值可以修改故需要特别写方法处理)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> updateWindowInformation(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hqlWhere = new StringBuffer();
		hqlWhere.append("  CKBH=:ckbh and YFSB=:yfsb");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("ckbh", MedicineUtils.parseInt(body.get("CKBH")));
			parameters.put("yfsb", MedicineUtils.parseLong(body.get("yfsb")));
			Long l = dao.doCount("YF_CKBH", hqlWhere.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("窗口编号已存在");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "窗口编号重复查询失败", e);
		}
		parameters = new HashMap<String, Object>();
		parameters.put("oldCkbh", MedicineUtils.parseInt(body.get("oldCkbh")));
		StringBuffer updateHql = new StringBuffer();
		updateHql.append("update YF_CKBH  set ");
		try {
			Schema sc = SchemaController.instance().get(
					"phis.application.pha.schemas.YF_CKBH");
			List<SchemaItem> items = sc.getItems();
			for (SchemaItem it : items) {
				if ("false".equals(it.getProperty("update"))) {
					continue;
				}
				updateHql.append(" ").append(it.getId()).append("=:")
						.append(it.getId()).append(",");
				parameters.put(it.getId(),
						it.toPersistValue(body.get(it.getId())));
			}
			updateHql.deleteCharAt(updateHql.lastIndexOf(","));
			updateHql.append(" where CKBH=:oldCkbh and YFSB=:YFSB");
			dao.doUpdate(updateHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "窗口更新失败", e);
		} catch (ControllerException e) {
			MedicineUtils.throwsException(logger, "窗口更新失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 药房注销
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> pharmacyCancellation(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameter = new HashMap<String, Object>();
		StringBuffer hql_update = new StringBuffer();// 更新sql
		hql_update.append("update YF_YFLB  set ZXBZ=:zxbz where YFSB=:yfsb");
		int zxbz = MedicineUtils.parseInt(body.get("op")) == 0 ? 1 : 0;
		Long yfsb = MedicineUtils.parseLong(body.get("yfsb"));
		parameter.put("zxbz", zxbz);
		parameter.put("yfsb", yfsb);
		try {
			if (zxbz == 0) {
				dao.doUpdate(hql_update.toString(), parameter);
				return MedicineUtils.getRetMap("取消注销成功", 200);
			}
			StringBuffer hql_zx_kc = new StringBuffer();// 库存查询
			hql_zx_kc.append(" YFSB=:yfsb");
			parameters.put("yfsb", yfsb);
			Long l = dao.doCount("YF_KCMX", hql_zx_kc.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("该药房有库存，不能注销!");
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			int cfxq=1;
			try{
			cfxq = MedicineUtils.parseInt(ParameterUtil.getParameter(
					jgid, BSPHISSystemArgument.CFXQ, ctx));
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CFXQ, e);
			}
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -cfxq);
			Date d = c.getTime();
			StringBuffer hql_wfy = new StringBuffer();// 未发药处方查询
			hql_wfy.append(" YFSB=:yfsb and FYBZ =0 and KFRQ >:rkrq");
			parameters.put("rkrq", d);
			l = dao.doCount("MS_CF01", hql_wfy.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("该药房有未发药处方单，不能注销!");
			}
			StringBuffer hql_rk = new StringBuffer();// 入库单查询
			hql_rk.append(" YFSB=:yfsb and RKPB=0");
			parameters.remove("rkrq");
			l = dao.doCount("YF_RK01", hql_rk.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("该药房有入库单没确认,不能注销!");
			}
			StringBuffer hql_ck = new StringBuffer();// 出库单查询
			hql_ck.append(" YFSB=:yfsb and CKPB=0");
			l = dao.doCount("YF_CK01", hql_ck.toString(), parameters);
			if (l > 0) {
				MedicineUtils.getRetMap("该药房有出库单没确认,不能注销!");
			}
			dao.doUpdate(hql_update.toString(), parameter);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房注销失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 药房界面 查询是否有领药科室
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> departmentSearch(Context ctx)
			throws ModelDataOperationException {
		String jgid = UserRoleToken.getCurrent().getManageUnit().getRef();// 获取JGID
		StringBuffer hql = new StringBuffer();
		hql.append("select count(1) as TOTAL from SYS_OFFICE  where  ORGANIZCODE=:jgid ");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", jgid);
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					map_par);
			if (list == null || list.get(0) == null
					|| MedicineUtils.parseLong(list.get(0).get("TOTAL")) == 0) {
				return MedicineUtils.getRetMap("无科室");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "病区查询失败", e);
		}
		return MedicineUtils.getRetMap();
	}
}
