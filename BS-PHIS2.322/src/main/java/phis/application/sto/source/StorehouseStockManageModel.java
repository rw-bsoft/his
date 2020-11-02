package phis.application.sto.source;

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
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
/**
 * 药库库存管理
 * @author caijy
 *
 */
public class StorehouseStockManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseStockManageModel.class);

	public StorehouseStockManageModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 处理药库库存主键问题,由于0库存会删记录,重启服务器会重新查询主键 导致YK_KCMX主键和YK_KCFL主键冲突
	 * @updateInfo
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void synchronousPrimaryKey(Context ctx)
			throws ModelDataOperationException {
		long kc_key = 0;
		long kcls_key = 0;
		StringBuffer hql = new StringBuffer();
		hql.append("select max(SBXH) as SBXH from YK_KCMX_LS");
		try {
			Schema sc = SchemaController.instance().get(BSPHISEntryNames.YK_KCMX);
			SchemaItem item_yk_kc = sc.getKeyItem();
			kc_key = MedicineUtils.parseLong(KeyManager.getKeyByName(
					"YK_KCMX",
					item_yk_kc.getKeyRules(), item_yk_kc.getId(), ctx));
			Map<String, Object> map_par = new HashMap<String, Object>();
			Map<String, Object> map_ls_key = dao
					.doLoad(hql.toString(), map_par);
			kcls_key = MedicineUtils.parseLong(map_ls_key.get("SBXH"));
			if (kcls_key > kc_key) {
				for (int i = 0; i < kcls_key - kc_key; i++) {
					KeyManager.getKeyByName(
							"YK_KCMX",
							item_yk_kc.getKeyRules(), item_yk_kc.getId(), ctx);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "处理主键失败", e);
		} catch (KeyManagerException e) {
			MedicineUtils.throwsException(logger, "处理主键失败", e);
		} catch (ControllerException e) {
			MedicineUtils.throwsException(logger, "处理主键失败", e);
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 账册库存查询
	 * @updateInfo
	 * @param body
	 * @param cnd
	 * @param parameters
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> stockSearch(Map<String, Object> body,List<Object> cnd, Map<String, Object> req,
			Context ctx) throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(d.KCSL) as SWKC,c.KCSL as KCSL,c.JHJE as JHJE,c.PFJE as PFJE,c.LSJE as LSJE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,a.YPDM as YPDM,e.KWBM as KWBM,c.YPXH as YPXH,c.YPCD as YPCD,e.YKZF as YKZF,a.ZBLB as ZBLB,c.ZFPB as ZFPB FROM YK_CDDZ b,YK_TYPK a,YK_CDXX c left join YK_KCMX  d on (c.JGID = d.JGID and c.YPXH = d.YPXH and c.YPCD = d.YPCD),YK_YPXX e WHERE e.YKSB = :YKSB and c.YPCD = b.YPCD and a.YPXH = c.YPXH and a.YPXH = e.YPXH and e.JGID = c.JGID and e.JGID = :JGID ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = " and " + ExpressionProcessor.instance().toString(cnd);
					hql.append(where);
				}
			}
			if (!(Boolean) body.get("showZero")) {
				hql.append(" and c.KCSL > 0 ");
			}
			hql.append(" GROUP BY a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.KCSL,c.JHJE,c.PFJE,c.LSJE,a.PYDM,a.WBDM,a.JXDM,a.QTDM,a.YPDM,e.KWBM,c.YPXH,c.YPCD,e.YKZF,a.ZBLB,c.ZFPB");
			if (!(Boolean) body.get("showZero")) {
				hql.append(" having sum(d.KCSL) > 0");
			}
			Map<String, Object> map_par = new HashMap<String, Object>();
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			long yksb =MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
			map_par.put("YKSB", yksb);
			map_par.put("JGID", jgid);
			hql.append(" order by c.YPXH");
			MedicineCommonModel model=new MedicineCommonModel(dao);
			map_ret=model.getPageInfoRecord(req, map_par, hql.toString(),null);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "账册库存查询失败!", e);
		} 
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 查询实物库存
	 * @updateInfo
	 * @param cnd
	 * @param parameters
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> physicalDetails(List<Object> cnd, Map<String, Object> req,
			Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT c.LSJG as LSJG,c.JHJG as JHJG,a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(c.KCSL) as KCSL,c.YPPH as YPPH,c.YPXQ as YPXQ,c.TYPE as TYPE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,c.YPXH as YPXH,c.YPCD as YPCD,a.YPDM as YPDM,d.KWBM as KWBM,d.YKZF as YKZF,a.ZBLB as ZBLB FROM YK_CDDZ b,YK_KCMX c,YK_TYPK a,YK_YPXX d WHERE ( b.YPCD = c.YPCD ) and  c.JGID = d.JGID and ( c.YPXH = a.YPXH ) and ( a.YPXH = d.YPXH ) and  ( c.JGID = d.JGID ) and ( d.YKSB = :YKSB ) and ( d.JGID = :JGID ) ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = " and " + ExpressionProcessor.instance().toString(cnd);
					hql.append(where);
				}
			}
			hql.append(" GROUP BY a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.YPPH,c.YPXQ,c.TYPE,a.PYDM,a.WBDM,a.JXDM,   a.QTDM,c.YPXH,c.YPCD,a.YPDM,d.KWBM,d.YKZF,a.ZBLB,c.LSJG,c.JHJG");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("YKSB", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")));
			map_par.put("JGID", UserRoleToken.getCurrent().getManageUnit().getId());
			hql.append(" order by c.YPXH");
			MedicineCommonModel model=new MedicineCommonModel(dao);
			map_ret=model.getPageInfoRecord(req, map_par, hql.toString(),"phis.application.sto.schemas.YK_SWMX");
		}  catch (ExpException e) {
			MedicineUtils.throwsException(logger, "实物明细查询失败!", e);
		}
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 药房库存明细查询
	 * @updateInfo
	 * @param cnd
	 * @param parameters
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> pharmacyStockSearch(List<Object> cnd, Map<String, Object> req,
			Context ctx) throws ModelDataOperationException {
			Map<String,Object> map_ret=new HashMap<String,Object>();
			try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			long yksb =MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
			String sql_query = "select e.yfmc as yfmc, a.YPMC as YPMC,c.YFGG as YPGG,c.YFDW as YPDW,d.CDMC as CDMC,a1.YPPH as YPPH,a1.YPXQ as YPXQ,a1.LSJG as LSJG,a1.YPSL as KCSL,1 as TYPE, a1.YPCD as YPCD,a1.LSJE as LSJE,a1.JHJG as JHJG,a1.JHJE as JHJE,a1.PFJG as PFJG,a1.PFJE as PFJE,a1.JGID as JGID,a1.SBXH as SBXH,a1.CKBH as CKBH,a1.YPXH as YPXH,a1.JYBZ as JYBZ  " +
					"from YF_KCMX a1 left join yf_yflb e on a1.yfsb=e.yfsb left join yk_ypxx f on a1.ypxh=f.ypxh, YK_TYPK a, YF_YPXX c, YK_CDDZ d " +
					"where a.YPXH = a1.YPXH and a1.YPXH = c.YPXH  and a1.YFSB = c.YFSB and a1.YPCD = d.YPCD  and e.jgid = " +jgid + " and f.yksb ="+yksb;
			if (cnd != null&&cnd.size() > 0) {
				sql_query+=" and " + ExpressionProcessor.instance().toString(cnd);
			}
			Map<String,Object> map_par=new HashMap<String,Object>();
			MedicineCommonModel model=new MedicineCommonModel(dao);
			map_ret=model.getPageInfoRecord(req,map_par , sql_query,null);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "药房库存明细查询失败!", e);
		}
		return map_ret;
	}
	
	
	
}
