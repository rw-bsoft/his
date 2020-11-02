package phis.application.pha.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
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
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;

/**
 * 药房库存管理Model
 * 
 * @author caijy
 * 
 */
public class PharmacyInventoryManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyInventoryManageModel.class);

	public PharmacyInventoryManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 查询库存记录
	 * @updateInfo
	 * @param cnd
	 * @param req
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryInventory(List<Object> cnd,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
			StringBuffer sql_query = new StringBuffer(
					"select distinct a.YPDW as YPDW,a.PYDM as PYDM,a.WBDM as WBDM,a.YPMC as YPMC,a.YFGG as YFGG,a.YFDW as YFDW, a.CDMC as CDMC,a.YPPH as YPPH,a.YPXQ as YPXQ,a.LSJG as LSJG,a.YPSL as YPSL,nvl(sum(e.KCSL), 0) as KCSL,a.TYPE as TYPE,a.YPCD as YPCD,a.LSJE as LSJE,a.JHJG as JHJG,a.JHJE as JHJE,a.PFJG as PFJG,a.PFJE as PFJE, a.JGID as JGID,a.SBXH as SBXH,a.YFSB as YFSB,a.CKBH as CKBH,a.YPXH as YPXH,a.JYBZ as JYBZ from (select b.YPDW as YPDW,b.PYDM as PYDM,b.WBDM as WBDM,b.YPMC as YPMC, c.YFGG as YFGG,c.YFDW as YFDW, d.CDMC as CDMC,a.YPPH as YPPH,a.YPXQ as YPXQ,a.LSJG as LSJG,a.YPSL as YPSL,b.TYPE as TYPE,a.YPCD as YPCD, a.LSJE as LSJE,a.JHJG as JHJG,a.JHJE as JHJE,a.PFJG as PFJG,a.PFJE as PFJE,a.JGID as JGID,a.SBXH as SBXH,a.YFSB as YFSB,a.CKBH as CKBH,a.YPXH as YPXH,a.JYBZ as JYBZ,(c.YFBZ / b.ZXBZ) as BL from  YK_TYPK b, YF_KCMX a, YF_YPXX c, YK_CDDZ d where b.YPXH = a.YPXH and a.YPXH = c.YPXH and a.YFSB = c.YFSB and a.YPCD = d.YPCD and a.YFSB =:yfsb ");
			if (cnd != null) {
				sql_query.append(" and "
						+ ExpressionProcessor.instance().toString(cnd));
			}
			sql_query
					.append(" ) a left join YK_KCMX e on a.YPXH=e.YPXH and a.YPCD=e.YPCD and a.JGID=e.JGID and (a.YPPH = e.YPPH or (a.YPPH is null and e.YPPH is null)) and (a.YPXQ = e.YPXQ or (a.YPXQ is null and e.YPXQ is null)) and a.LSJG =round(e.LSJG * a.BL , 4) and a.JHJG =round(e.JHJG * a.BL, 4) group by a.YPDW,a.PYDM, a.WBDM, a.YPMC, a.YFGG, a.YFDW, a.CDMC, a.YPPH, a.YPXQ, a.LSJG, a.YPSL, a.TYPE, a.YPCD, a.LSJE, a.JHJG, a.JHJE, a.PFJG, a.PFJE, a.JGID, a.SBXH, a.YFSB, a.CKBH, a.YPXH, a.JYBZ order by a.PYDM");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("yfsb", yfsb);
			MedicineCommonModel model=new MedicineCommonModel(dao);
			map_ret = model.getPageInfoRecord(req, map_par,
					sql_query.toString(),
					"phis.application.pha.schemas.YF_KCMX_JY");
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "库存查询失败!", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 禁用或者取消禁用库存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveDisableInventory(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql_update = new StringBuffer();
		hql_update.append("update YF_KCMX  set JYBZ=:jybz where SBXH=:kcsb");
		Map<String, Object> map_par_update = new HashMap<String, Object>();
		map_par_update.put("kcsb", MedicineUtils.parseLong(body.get("SBXH")));
		map_par_update.put("jybz", MedicineUtils.parseInt(body.get("JYBZ")));
		try {
			dao.doUpdate(hql_update.toString(), map_par_update);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存禁用(取消禁用)失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 同步yf_kcmx主键值,用于解决某个库存(主键是当前库存最大值),数量减到0,存到YF_KCMX_LS表
	 *              重启服务器再录库存导致SBXH重复的问题
	 * @updateInfo
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void synchronousPrimaryKey(Context ctx)
			throws ModelDataOperationException {
		long kc_key = 0;
		long kcls_key = 0;
		StringBuffer hql = new StringBuffer();
		hql.append("select max(SBXH) as SBXH from YF_KCMX_LS");
		try {
			Schema sc = SchemaController.instance().get(
					BSPHISEntryNames.YF_KCMX_CSH);
			SchemaItem item_yk_kc = sc.getKeyItem();
			kc_key = MedicineUtils.parseLong(KeyManager.getKeyByName("YF_KCMX",
					item_yk_kc.getKeyRules(), item_yk_kc.getId(), ctx));
			Map<String, Object> map_par = new HashMap<String, Object>();
			Map<String, Object> map_ls_key = dao
					.doLoad(hql.toString(), map_par);
			kcls_key = MedicineUtils.parseLong(map_ls_key.get("SBXH"));
			if (kcls_key > kc_key) {
				for (int i = 0; i < kcls_key - kc_key; i++) {
					KeyManager.getKeyByName("YF_KCMX",
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
	 * @createDate 2014-2-24
	 * @description 减库存(有库存识别)
	 * @updateInfo
	 * @param kc
	 *            存有库存识别和药品数量的map的集合,map里面一定要有{"KCSB":,"YPSL":}
	 * @param ctx
	 * @return map code200减库存成功,9000减库存失败 msg错误信息,ypmc药品名称,ypxh药品序号(库存不够时才有)
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> lessInventory(List<Map<String, Object>> kc,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> map = new HashMap<String, Object>();// 返回的map信息
																// code200减库存成功,9000减库存失败
																// msg错误信息,ypmc药品名称,ypxh药品序号(库存不够时才有)
		map.put("code", 9000);
		map.put("msg", "记录为空");
		if (kc == null || kc.size() == 0) {
			return map;
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		StringBuffer kcHql = new StringBuffer();// 查询该药品的当前库存
		kcHql.append("select YPSL as YPSL,YPXH as YPXH,YPCD as YPCD from YF_KCMX  where SBXH=:kcsb");
		StringBuffer updateKcHql = new StringBuffer();// 更新库存
		updateKcHql
				.append("update YF_KCMX  set YPSL=:ypsl,LSJE=LSJG*:ypsl,JHJE=JHJG*:ypsl,PFJE=PFJG*:ypsl where SBXH=:kcsb");
		StringBuffer ypHql = new StringBuffer();// 查询库存不足的药品名称
		ypHql.append("select YPMC as YPMC from YK_TYPK where YPXH=:ypxh");
		StringBuffer sql_kc_ls_insert = new StringBuffer();
		sql_kc_ls_insert
				.append("insert into YF_KCMX_LS  select * from YF_KCMX  where YPSL=0 and SBXH=:kcsb");
		StringBuffer hql_kc_update = new StringBuffer();
		hql_kc_update
				.append("delete from YF_KCMX  where YPSL=0 and SBXH=:kcsb");
		try {
			int SFQYYFYFY=0;
			double KCDJTS=0;
			try{
			SFQYYFYFY= MedicineUtils.parseInt(ParameterUtil
					.getParameter(jgid, BSPHISSystemArgument.SFQYYFYFY, ctx));
			KCDJTS= MedicineUtils.parseInt(ParameterUtil
					.getParameter(jgid, BSPHISSystemArgument.KCDJTS, ctx));
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.SFQYYFYFY, e);
			}
//			if(SFQYYFYFY==1){//如果启用库存冻结
//				//先删除过期的冻结库存
//				MedicineCommonModel model=new MedicineCommonModel(dao);
//				model.deleteKCDJ(jgid, ctx);
//			}
			StringBuffer hql_kcdj=new StringBuffer();//查询冻结数量
			hql_kcdj.append("select YPSL as YPSL,YFBZ as YFBZ from YF_KCDJ where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and sysdate-DJSJ<=:kcdjts");
			StringBuffer hql_yfbz=new StringBuffer();//查询药房包装,用于计算冻结的实际数量
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh ");
			StringBuffer hql_kcsl_sum=new StringBuffer();//查询总的库存数量,用于减掉冻结的和当前要发的比较
			hql_kcsl_sum.append("select sum(YPSL) as KCSL from YF_KCMX where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd");
			for (int i = 0; i < kc.size(); i++) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				Map<String, Object> dqyp = kc.get(i);
				if (dqyp == null) {
					return map;
				}
				parameters.put("kcsb",
						MedicineUtils.parseLong(dqyp.get("KCSB")));
				Map<String, Object> map_kc = dao.doLoad(kcHql.toString(),
						parameters);
				if (map_kc == null) {
					map.put("msg", "库存不够");
					if (dqyp.containsKey("YPXH")) {
						parameters.remove("kcsb");
						long ypxh = MedicineUtils.parseLong(dqyp.get("YPXH"));
						parameters.put("ypxh", ypxh);
						String ypmc = (String) dao.doLoad(ypHql.toString(),
								parameters).get("YPMC");
						map.put("ypmc", ypmc);
						map.put("ypxh", ypxh);
					}
					Session session = (Session) ctx.get(Context.DB_SESSION);
					// 库存不够 数据回滚
					session.getTransaction().rollback();
					return map;
				}
				Double ypsl_kc = (Double) map_kc.get("YPSL");
				Double ypsl_dq = MedicineUtils.parseDouble(dqyp.get("YPSL"));
				if(SFQYYFYFY==1){//库存冻结
					long ypxh = MedicineUtils.parseLong(map_kc.get("YPXH"));
					long ypcd = MedicineUtils.parseLong(map_kc.get("YPCD"));
					//long cfsb=MedicineUtils.parseLong(dqyp.get("JLXH"));//有传就是发药,不传就是0
					double djsl=0;//冻结的总数量
					double kcsl=0;//总的库存数量
					Map<String,Object> map_par_kcdj=new HashMap<String,Object>();
					map_par_kcdj.put("ypxh", ypxh);
					map_par_kcdj.put("ypcd", ypcd);
					map_par_kcdj.put("yfsb", yfsb);
					//map_par_kcdj.put("cfsb", cfsb);
					map_par_kcdj.put("kcdjts", KCDJTS);
					List<Map<String,Object>> list_kcdj=dao.doQuery(hql_kcdj.toString(), map_par_kcdj);
					//map_par_kcdj.remove("cfsb");
					map_par_kcdj.remove("kcdjts");
					List<Map<String,Object>> list_kcsl=dao.doSqlQuery(hql_kcsl_sum.toString(), map_par_kcdj);
					if(list_kcsl!=null&&list_kcsl.size()>0&&list_kcsl.get(0)!=null){
						kcsl=MedicineUtils.parseDouble(list_kcsl.get(0).get("KCSL"));
					}
					if(list_kcdj!=null&&list_kcdj.size()>0){
						Map<String,Object> map_par_yfbz=new HashMap<String,Object>();
						map_par_yfbz.put("yfsb", yfsb);
						map_par_yfbz.put("ypxh", ypxh);
						Map<String,Object> map_yfbz=dao.doLoad(hql_yfbz.toString(), map_par_yfbz);
						int yfbz=MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
						for(Map<String,Object> map_kcdj:list_kcdj){
							djsl+=MedicineUtils.formatDouble(2, MedicineUtils.simpleMultiply(2, map_kcdj.get("YPSL"), map_kcdj.get("YFBZ"))/yfbz);
						}
					}
					if(kcsl-djsl<ypsl_dq){//如果库存不够
						map.put("msg", "库存 不够");
						parameters.remove("kcsb");
						parameters.put("ypxh", ypxh);
						String ypmc = (String) dao.doLoad(ypHql.toString(),
								parameters).get("YPMC");
						map.put("ypmc", ypmc);
						map.put("ypxh", ypxh);
						Session session = (Session) ctx.get(Context.DB_SESSION);
						// 库存不够 数据回滚
						session.getTransaction().rollback();
						return map;
					}
				}
				// 库存 不够
				if (ypsl_kc < ypsl_dq) {
					map.put("msg", "库存 不够");
					parameters.remove("kcsb");
					long ypxh = MedicineUtils.parseLong(map_kc.get("YPXH"));
					parameters.put("ypxh", ypxh);
					String ypmc = (String) dao.doLoad(ypHql.toString(),
							parameters).get("YPMC");
					map.put("ypmc", ypmc);
					map.put("ypxh", ypxh);
					Session session = (Session) ctx.get(Context.DB_SESSION);
					// 库存不够 数据回滚
					session.getTransaction().rollback();
					return map;
				}
				// 库存足够
				parameters.put("ypsl", ypsl_kc - ypsl_dq);
				dao.doUpdate(updateKcHql.toString(), parameters);
				// 完后删除库存是0的记录
				if (ypsl_kc - ypsl_dq == 0) {
					Map<String, Object> map_ypsl_par = new HashMap<String, Object>();
					map_ypsl_par.put("kcsb",
							MedicineUtils.parseLong(dqyp.get("KCSB")));
					dao.doSqlUpdate(sql_kc_ls_insert.toString(), map_ypsl_par);
					dao.doUpdate(hql_kc_update.toString(), map_ypsl_par);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "减库存失败", e);
		}
		map.put("code", 200);
		map.put("msg", "库存更新成功");
		return map;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 减库存(无kcsb)
	 * @updateInfo
	 * @param body
	 *            集合里的Map必须包含"YPXH"(药品序号)"YFSB"(药房识别)"YPCD"(药品产地)"YPDJ"(药品单价||
	 *            药房单价)"YPSL"(要减掉的药品数量)
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryAndLessInventory(
			List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		if (body == null || body.size() == 0) {
			return null;
		}
		List<Map<String, Object>> list_kc_update_ret = new ArrayList<Map<String, Object>>();// 需要更新的库存集合(返回用)
		Session session = (Session) ctx.get(Context.DB_SESSION);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		try {
			//库存冻结代码
			int SFQYYFYFY=0;
			double KCDJTS=0;
			try{
			SFQYYFYFY= MedicineUtils.parseInt(ParameterUtil
					.getParameter(jgid, BSPHISSystemArgument.SFQYYFYFY, ctx));
			KCDJTS= MedicineUtils.parseDouble(ParameterUtil
					.getParameter(jgid, BSPHISSystemArgument.KCDJTS, ctx));
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.SFQYYFYFY, e);
			}
//			if(SFQYYFYFY==1){//如果启用库存冻结
//				//先删除过期的冻结库存
//				MedicineCommonModel model=new MedicineCommonModel(dao);
//				model.deleteKCDJ(jgid, ctx);
//			}
			
			// 出库顺序
			List<String> list_order = new ArrayList<String>();
			int cksx_ypxq_yf=0;
			try{
			cksx_ypxq_yf = MedicineUtils
					.parseInt(ParameterUtil.getParameter(jgid,
							BSPHISSystemArgument.CKSX_YPXQ_YF, ctx));// 是否按效期排序
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CKSX_YPXQ_YF, e);
			}
			if (cksx_ypxq_yf > 0) {
				String cksx_ypxq_order_yf = MedicineUtils
						.parseString(ParameterUtil.getParameter(jgid,
								BSPHISSystemArgument.CKSX_YPXQ_ORDER_YF, ctx));
				if ("A".equals(cksx_ypxq_order_yf)) {
					list_order.add("YPXQ");
				} else {
					list_order.add("YPXQ desc");
				}
			}
			int cksx_kcsl_yf=0;
			try{
			 cksx_kcsl_yf = MedicineUtils
					.parseInt(ParameterUtil.getParameter(jgid,
							BSPHISSystemArgument.CKSX_KCSL_YF, ctx));// 是否按库存排序
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CKSX_KCSL_YF, e);
			}
			if (cksx_kcsl_yf > 0) {
				String cksx_kcsl_order_yf = MedicineUtils
						.parseString(ParameterUtil.getParameter(jgid,
								BSPHISSystemArgument.CKSX_KCSL_ORDER_YF, ctx));
				if ("A".equals(cksx_kcsl_order_yf)) {
					if (list_order.size() > 0) {
						list_order.add(",YPSL");
					} else {
						list_order.add("YPSL");
					}
				} else {
					if (list_order.size() > 0) {
						list_order.add(",YPSL desc");
					} else {
						list_order.add("YPSL desc");
					}
				}
			}
			long yfsb = 0;
			StringBuffer hql_kcdj=new StringBuffer();//查询冻结数量
			hql_kcdj.append("select YPSL as YPSL,YFBZ as YFBZ from YF_KCDJ where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and sysdate-DJSJ<=:kcdjts");
			StringBuffer hql_yfbz=new StringBuffer();//查询药房包装,用于计算冻结的实际数量
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh ");
			StringBuffer hql_kcsl_sum=new StringBuffer();//查询总的库存数量,用于减掉冻结的和当前要发的比较
			hql_kcsl_sum.append("select sum(YPSL) as KCSL from YF_KCMX where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd");
			for (int i = 0; i < body.size(); i++) {
				List<Map<String, Object>> list_kc_update = new ArrayList<Map<String, Object>>();// 需要更新的库存集合
				Map<String, Object> map_ypkc = body.get(i);

				if (map_ypkc.get("YPXH") == null
						|| map_ypkc.get("YPCD") == null
						|| map_ypkc.get("YFSB") == null
						|| map_ypkc.get("YPDJ") == null
						|| map_ypkc.get("YPSL") == null) {
					return null;
				}
				long ypxh = MedicineUtils.parseLong(map_ypkc.get("YPXH"));
				long ypcd = MedicineUtils.parseLong(map_ypkc.get("YPCD"));
				yfsb = MedicineUtils.parseLong(map_ypkc.get("YFSB"));
				// 增加以下代码 用于增加站点发药减掉中心的库存
				if (map_ypkc.containsKey("SJFYBZ")
						&& MedicineUtils.parseInt(map_ypkc.get("SJFYBZ")) == 1) {
					yfsb = MedicineUtils.parseLong(map_ypkc.get("SJYF"));
				}
				double ypdj = MedicineUtils.parseDouble(map_ypkc.get("YPDJ"));
				double ypsl = 0;
				if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
					ypsl = MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_ypkc.get("YPSL"))
									* MedicineUtils.parseInt(map_ypkc
											.get("CFTS")));
				} else {
					ypsl = MedicineUtils.parseDouble(map_ypkc.get("YPSL"));
				}
				//库存冻结代码
				if(SFQYYFYFY==1){
					//long cfsb=MedicineUtils.parseLong(map_ypkc.get("CFSB"));//有传就是发药,不传就是0
					double djsl=0;//冻结的总数量
					double kcsl=0;//总的库存数量
					Map<String,Object> map_par_kcdj=new HashMap<String,Object>();
					map_par_kcdj.put("ypxh", ypxh);
					map_par_kcdj.put("ypcd", ypcd);
					map_par_kcdj.put("yfsb", yfsb);
					//map_par_kcdj.put("cfsb", cfsb);
					map_par_kcdj.put("kcdjts", KCDJTS);
					List<Map<String,Object>> list_kcdj=dao.doQuery(hql_kcdj.toString(), map_par_kcdj);
					//map_par_kcdj.remove("cfsb");
					map_par_kcdj.remove("kcdjts");
					List<Map<String,Object>> list_kcsl=dao.doSqlQuery(hql_kcsl_sum.toString(), map_par_kcdj);
					if(list_kcsl!=null&&list_kcsl.size()>0&&list_kcsl.get(0)!=null){
						kcsl=MedicineUtils.parseDouble(list_kcsl.get(0).get("KCSL"));
					}
					if(list_kcdj!=null&&list_kcdj.size()>0){
						Map<String,Object> map_par_yfbz=new HashMap<String,Object>();
						map_par_yfbz.put("yfsb", yfsb);
						map_par_yfbz.put("ypxh", ypxh);
						Map<String,Object> map_yfbz=dao.doLoad(hql_yfbz.toString(), map_par_yfbz);
						int yfbz=MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
						for(Map<String,Object> map_kcdj:list_kcdj){
							djsl+=MedicineUtils.formatDouble(2, MedicineUtils.simpleMultiply(2, map_kcdj.get("YPSL"), map_kcdj.get("YFBZ"))/yfbz);
						}
					}
					if(kcsl-djsl<ypsl){//如果库存不够
						session.getTransaction().rollback();
						List<Map<String, Object>> list_no = new ArrayList<Map<String, Object>>();
						Map<String, Object> map_no = new HashMap<String, Object>();
						map_no.put("ypxh", ypxh);
						if(map_ypkc.containsKey("YZMC")){
							map_no.put("ypmc", map_ypkc.get("YZMC"));
						}
						list_no.add(map_no);
						return list_no;
					}
				}
				StringBuffer hql_kc_xtjg = new StringBuffer();// 相同价格的药品库存查询
				hql_kc_xtjg
						.append("select YPSL as YPSL,SBXH as KCSB,JGID as JGID,YFSB as YFSB,YPXH as YPXH,YPCD as YPCD,YPPH as YPPH,YPXQ as YPXQ,LSJG as LSJG ,PFJG as PFJG,JHJG as JHJG,YKJH as YKJH from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and LSJG=:ypdj and JYBZ!=1");
				if (list_order.size() > 0) {
					StringBuffer order = new StringBuffer();
					order.append(" order by ");
					for (String o : list_order) {
						order.append(o);
					}
					hql_kc_xtjg.append(order.toString());
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ypxh", ypxh);
				parameters.put("ypcd", ypcd);
				parameters.put("yfsb", yfsb);
				parameters.put("ypdj", ypdj);
				List<Map<String, Object>> list_kc_xtjg = dao.doQuery(
						hql_kc_xtjg.toString(), parameters);
				// 有相同价格的库存
				if (list_kc_xtjg != null) {
					for (int j = 0; j < list_kc_xtjg.size(); j++) {
						Map<String, Object> map_kc_update = new HashMap<String, Object>();// 存要更新的库存
						Map<String, Object> map_kc_xtjg = list_kc_xtjg.get(j);
						double ypsl_kc_xtjg = MedicineUtils
								.parseDouble(map_kc_xtjg.get("YPSL"));// 相同价格的库存数量
						long kcsb_kc_xtjg = MedicineUtils.parseLong(map_kc_xtjg
								.get("KCSB"));// 相同价格的库存识别
						for (String key : map_ypkc.keySet()) {
							if(map_kc_xtjg.containsKey(key)){
								continue;
							}
							map_kc_xtjg.put(key, map_ypkc.get(key));
						}
						if (ypsl_kc_xtjg < ypsl) {
							map_kc_update.put("kcsb", kcsb_kc_xtjg);
							map_kc_update.put("ypsl", 0);
							list_kc_update.add(map_kc_update);
							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
								map_kc_xtjg
										.put("YPSL",
												MedicineUtils
														.formatDouble(
																2,
																ypsl_kc_xtjg
																		/ MedicineUtils
																				.parseInt(map_ypkc
																						.get("CFTS"))));
							} else {
								map_kc_xtjg.put("YPSL", ypsl_kc_xtjg);
							}
							list_kc_update_ret.add(map_kc_xtjg);
							ypsl = ypsl - ypsl_kc_xtjg;
						} else {
							map_kc_update.put("kcsb", kcsb_kc_xtjg);
							map_kc_update.put("ypsl", ypsl_kc_xtjg - ypsl);
							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
								map_kc_xtjg
										.put("YPSL",
												MedicineUtils.formatDouble(
														2,
														ypsl
																/ MedicineUtils
																		.parseInt(map_ypkc
																				.get("CFTS"))));
							} else {
								map_kc_xtjg.put("YPSL", ypsl);
							}
							list_kc_update.add(map_kc_update);
							list_kc_update_ret.add(map_kc_xtjg);
							ypsl = 0;
							break;
						}
					}
				}
				// 相同价格的药 数量不够
//				if (ypsl > 0) {
//					StringBuffer hql_kc_btjg = new StringBuffer();// 不同价格的药品库存查询
//					hql_kc_btjg
//							.append("select SBXH as KCSB,YPSL as YPSL,JGID as JGID,YFSB as YFSB,YPXH as YPXH,YPCD as YPCD,YPPH as YPPH,YPXQ as YPXQ,LSJG as LSJG ,PFJG as PFJG,JHJG as JHJG,YKJH as YKJH from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and LSJG!=:ypdj and JYBZ!=1 ");
//					if (list_order.size() > 0) {
//						StringBuffer order = new StringBuffer();
//						order.append(" order by ");
//						for (String o : list_order) {
//							order.append(o);
//						}
//						hql_kc_btjg.append(order.toString());
//					}
//					List<Map<String, Object>> list_kc_btjg = dao.doQuery(
//							hql_kc_btjg.toString(), parameters);
//					if (list_kc_btjg == null) {
//						session.getTransaction().rollback();
//						return null;
//					}
//					for (int k = 0; k < list_kc_btjg.size(); k++) {
//						Map<String, Object> map_kc_btjg = list_kc_btjg.get(k);
//						double ypsl_kc_btjg = MedicineUtils
//								.parseDouble(map_kc_btjg.get("YPSL"));// 不同价格的库存数量
//						long kcsb_kc_btjg = MedicineUtils.parseLong(map_kc_btjg
//								.get("KCSB"));// 不同价格的库存识别
//						Map<String, Object> map_kc_btjg_update = new HashMap<String, Object>();// 存要更新的库存
//						for (String key : map_ypkc.keySet()) {
//							map_kc_btjg.put(key, map_ypkc.get(key));
//						}
//						// 如果这个库存小于要减的ypsl,该库存数量变0,要减的数量减去这个库存数量
//						if (ypsl_kc_btjg < ypsl) {
//							map_kc_btjg_update.put("kcsb", kcsb_kc_btjg);
//							map_kc_btjg_update.put("ypsl", 0);
//							list_kc_update.add(map_kc_btjg_update);
//							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
//								map_kc_btjg
//										.put("YPSL",
//												MedicineUtils
//														.formatDouble(
//																2,
//																ypsl_kc_btjg
//																		/ MedicineUtils
//																				.parseInt(map_ypkc
//																						.get("CFTS"))));
//							} else {
//								map_kc_btjg.put("YPSL", ypsl_kc_btjg);
//							}
//							list_kc_update_ret.add(map_kc_btjg);
//							ypsl = ypsl - ypsl_kc_btjg;
//						} else {
//							map_kc_btjg_update.put("kcsb", kcsb_kc_btjg);
//							map_kc_btjg_update.put("ypsl", ypsl_kc_btjg - ypsl);
//							list_kc_update.add(map_kc_btjg_update);
//							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
//								map_kc_btjg
//										.put("YPSL",
//												MedicineUtils.formatDouble(
//														2,
//														ypsl
//																/ MedicineUtils
//																		.parseInt(map_ypkc
//																				.get("CFTS"))));
//							} else {
//								map_kc_btjg.put("YPSL", ypsl);
//							}
//							list_kc_update_ret.add(map_kc_btjg);
//							ypsl = 0;
//							break;
//						}
//					}
//				}
				// 库存不够
				if (ypsl > 0) {
					session.getTransaction().rollback();
					List<Map<String, Object>> list_no = new ArrayList<Map<String, Object>>();
					Map<String, Object> map_no = new HashMap<String, Object>();
					map_no.put("ypxh", ypxh);//这个key不要随便改,住院药房发药根据有没ypxh(小写)来判断是否库存不够
					if(map_ypkc.containsKey("YZMC")){
						map_no.put("ypmc", map_ypkc.get("YZMC"));
					}
					list_no.add(map_no);
					return list_no;
				}
				StringBuffer hql_kc_update = new StringBuffer();// 库存更新
				hql_kc_update
						.append("update YF_KCMX set YPSL=:ypsl,JHJE=JHJG*:ypsl,PFJE=PFJG*:ypsl,LSJE=LSJG*:ypsl where SBXH=:sbxh");
				for (int x = 0; x < list_kc_update.size(); x++) {
					Map<String, Object> map_kc_update = list_kc_update.get(x);
					Map<String, Object> parameter = new HashMap<String, Object>();
					parameter.put("ypsl", MedicineUtils
							.parseDouble(map_kc_update.get("ypsl")));
					parameter.put("sbxh", map_kc_update.get("kcsb"));
					dao.doUpdate(hql_kc_update.toString(), parameter);
				}
			}
			Map<String, Object> map_kcsl_par = new HashMap<String, Object>();
			map_kcsl_par.put("yfsb", yfsb);
			StringBuffer sql_kc_ls_insert = new StringBuffer();
			sql_kc_ls_insert
					.append("insert into YF_KCMX_LS  select * from YF_KCMX  where YPSL=0 and YFSB=:yfsb");
			dao.doSqlUpdate(sql_kc_ls_insert.toString(), map_kcsl_par);
			StringBuffer hql_kc_delete = new StringBuffer();
			hql_kc_delete
					.append("delete from YF_KCMX  where YPSL=0 and YFSB=:yfsb");
			dao.doUpdate(hql_kc_delete.toString(), map_kcsl_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "减库存失败", e);
		}
		return list_kc_update_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-4-29
	 * @description 药房高低储查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryYFGDC(String pydm,Context ctx) throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		//hql.append("select e.CDMC as CDMC,b.YFGG as YFGG,b.YFDW as YFDW,a.LSJG as LSJG, a.YPSL as KCSL ,a.SBXH as SBXH,c.YPMC as YPMC,b.YFGC as GCSL,b.YFDC as DCSL from YF_KCMX a,YF_YPXX b,YK_TYPK c,YK_YPXX d,YK_CDDZ e where a.YPCD=e.YPCD and a.YPXH=b.YPXH and a.YPXH=c.YPXH and a.YPXH=d.YPXH and a.JGID=d.JGID and d.YKZF=0 and c.ZFPB=0 and b.YFZF=0 and b.YFSB=:yfsb and a.YFSB=b.YFSB and (( a.YPSL>=b.YFGC and b.YFGC is not null and b.YFGC!=0) or (a.YPSL<=b.YFDC and b.YFDC is not null and b.YFDC!=0))");
		hql.append("select c.YPMC as YPMC,b.YFGG as YFGG,b.YFDW as YFDW,b.YFGC as GCSL,b.YFDC as DCSL,nvl(a.KCSL,0) as KCSL from YF_YPXX b,YK_TYPK c left outer join (select sum(YPSL) as KCSL,YPXH as YPXH from YF_KCMX where YFSB=:yfsb group by YPXH) a on a.YPXH=c.YPXH  where b.YPXH=c.YPXH and b.YFSB=:yfsb and (( nvl(a.KCSL,0)>b.YFGC and b.YFGC is not null and b.YFGC!=0) or (nvl(a.KCSL,0)<b.YFDC and b.YFDC is not null and b.YFDC!=0)) and c.PYDM like :pydm");
		Map<String,Object> map_par=new HashMap<String,Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		map_par.put("yfsb", yfsb);
		map_par.put("pydm", pydm+"%");
		List<Map<String,Object>> ret=new ArrayList<Map<String,Object>>();
		try {
			ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房高低储提示查询失败", e);
		}
		return ret;
	}
}
