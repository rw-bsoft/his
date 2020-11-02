package phis.application.sto.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
/**
 * 财务验收model
 * @author caijy
 *
 */
public class StorehouseFinancialAcceptanceModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseFinancialAcceptanceModel.class);

	public StorehouseFinancialAcceptanceModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 财务验收审核前判断下有没未审核的明细
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationFinancialAcceptanceNum(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("XTSB=:xtsb and RKDH=:rkdh and RKFS=:rkfs and YSDH=0");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("XTSB")));
		map_par.put("rkdh", MedicineUtils.parseInt(body.get("RKDH")));
		map_par.put("rkfs", MedicineUtils.parseInt(body.get("RKFS")));
		try {
			long l = dao.doCount("YK_RK02", hql.toString(),
					map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("该单子已经全部审核完,已刷新页面");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 已验收入库单查询
	 * @updateInfo
	 * @param body
	 * @param cnd
	 * @param map_par
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> financialAcceptanceDataQuery(
			Map<String, Object> body, List<Object> cnd,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select distinct a.JGID as JGID,a.XTSB as XTSB,a.RKFS as RKFS,a.RKDH as RKDH,c.YSDH as YSDH,to_char(c.YSRQ,'yyyy-mm-dd hh24:mi:ss') as YSRQ,b.DWMC as DWMC from YK_RK01 a,YK_JHDW b,YK_RK02  c where a.XTSB=:yksb and  a.DWXH=b.DWXH and a.XTSB=c.XTSB and a.RKDH=c.RKDH and a.RKFS=c.RKFS and ")
					.append(ExpressionProcessor.instance().toString(cnd).replaceAll("str", "to_char"))
					.append(" order by to_char(c.YSRQ,'yyyy-mm-dd hh24:mi:ss') desc");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("yksb", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")));
			MedicineCommonModel model=new MedicineCommonModel(dao);
			map_ret=model.getPageInfoRecord(req, map_par, hql.toString(), null);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "已验收入库单查询失败", e);
		} 
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 财务验收
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveFinancialAcceptance(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_rk01 = (Map<String, Object>) body
				.get("YK_RK01");
		List<Map<String, Object>> list_rk02_temp = (List<Map<String, Object>>) body
				.get("YK_RK02");
		List<Map<String, Object>> list_rk02 = new ArrayList<Map<String, Object>>();
		StringBuffer hql_cdxx = new StringBuffer();// 查询药品价格信息
		hql_cdxx.append("select JHJE as JHJE, LSJE as  LSJE from YK_CDXX where JGID=:jgid and YPXH=:ypxh and YPCD=:ypcd ");
		StringBuffer hql_cdxx_update = new StringBuffer();// 更新药品价格信息
		hql_cdxx_update
		.append("update YK_CDXX set JHJE=:jhje,LSJE=:lsje,KCSL=KCSL+:kcsl,JHJG=:jhjg where JGID=:jgid and YPXH=:ypxh and YPCD=:ypcd");
		StringBuffer hql_ysdh = new StringBuffer();// 查询验收单号
		hql_ysdh.append("select YSDH as YSDH from YK_RKFS where RKFS=:rkfs");
		StringBuffer hql_ysdh_update = new StringBuffer();// 更新验收单号
		hql_ysdh_update.append("update YK_RKFS set YSDH=YSDH+1 where  RKFS=:rkfs");
		StringBuffer hql_sfqbys = new StringBuffer();// 查询明细是否全部验收
		hql_sfqbys
		.append(" RKFS=:rkfs and RKDH=:rkdh and XTSB=:xtsb and YSDH=0");
		StringBuffer hql_rk01_update = new StringBuffer();// 明细全部验收后rk01更新CWPB
		hql_rk01_update
		.append("update YK_RK01 set CWPB=1 where RKFS=:rkfs and RKDH=:rkdh and XTSB=:xtsb");
		StringBuffer hql_rk02_acceptanced = new StringBuffer();// 保存前判断明细是否已经被验收掉了
		hql_rk02_acceptanced.append("YSDH=0 and SBXH=:sbxh");
		try {
			// 保存前判断明细是否已经被验收掉了
			Map<String, Object> map_par_acceptanced = new HashMap<String, Object>();
			for (Map<String, Object> map_rk02 : list_rk02_temp) {
				map_par_acceptanced
						.put("sbxh", MedicineUtils.parseLong(map_rk02.get("SBXH")));
				long l = dao.doCount("YK_RK02",
						hql_rk02_acceptanced.toString(), map_par_acceptanced);
				if (l > 0) {
					list_rk02.add(map_rk02);
				}
			}
			if (list_rk02.size() == 0) {
				return;
			}
			Map<String, Object> map_par_ysdh = new HashMap<String, Object>();
			map_par_ysdh.put("rkfs", MedicineUtils.parseInt(map_rk01.get("RKFS")));
			Map<String, Object> map_ysdh = dao.doLoad(hql_ysdh.toString(),
					map_par_ysdh);
			if (map_ysdh == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "获取验收单号失败");
			}
			dao.doUpdate(hql_ysdh_update.toString(), map_par_ysdh);
			int ysdh = MedicineUtils.parseInt(map_ysdh.get("YSDH"));// 验收单号
			Date nowDate = new Date();// 当前时间
			UserRoleToken user = UserRoleToken.getCurrent();
			String userid = user.getUserId();// 用户ID
			List<Map<String,Object>> list_pz02=new ArrayList<Map<String,Object>>();
			for (Map<String, Object> map_rk02 : list_rk02) {
//				long ypxh = ;
//				String jgid =;
//				long ypcd = ;
				double rksl = MedicineUtils.parseDouble(map_rk02.get("RKSL"));
				Map<String, Object> map_par_cdxx = new HashMap<String, Object>();
				map_par_cdxx.put("jgid", MedicineUtils.parseString(map_rk02.get("JGID")));
				map_par_cdxx.put("ypxh", MedicineUtils.parseLong(map_rk02.get("YPXH")));
				map_par_cdxx.put("ypcd", MedicineUtils.parseLong(map_rk02.get("YPCD")));
				Map<String, Object> map_cdxx = dao.doLoad(hql_cdxx.toString(),
						map_par_cdxx);
				if (map_cdxx == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "未找到相应的价格记录");
				}
				map_par_cdxx.put("kcsl", rksl);
				map_par_cdxx.put("jhje", MedicineUtils.parseDouble(map_cdxx.get("JHJE"))
						+ MedicineUtils.parseDouble(map_rk02.get("JHHJ")));
				map_par_cdxx.put("lsje", MedicineUtils.parseDouble(map_cdxx.get("LSJE"))
						+ MedicineUtils.parseDouble(map_rk02.get("LSJE")));
				map_par_cdxx.put("jhjg", MedicineUtils.parseDouble(map_rk02.get("JHJG")));
				map_rk02.put("YSDH", ysdh);
				map_rk02.put("YSRQ", nowDate);
				map_rk02.put("YSGH", userid);
				Map<String, Object> map_rk02_db = dao.doLoad(
						BSPHISEntryNames.YK_RK02,
						MedicineUtils.parseLong(map_rk02.get("SBXH")));// 查询数据库中该入库明细数据
				if (map_rk02_db == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "入库明细记录查询失败");
				}
				// 如果进货价格未改变,则不进行其他数据的保存
				if (MedicineUtils.parseDouble(map_rk02_db.get("JHJG")) == MedicineUtils.parseDouble(map_rk02
						.get("JHJG"))) {
					dao.doUpdate(hql_cdxx_update.toString(), map_par_cdxx);
					dao.doSave("update", BSPHISEntryNames.YK_RK02, map_rk02,
							false);
					dao.doSave("update", BSPHISEntryNames.YK_RK01, map_rk01,
							false);
					continue;
				}
				double pzsl = 0, xjhe = 0, xpfe = 0, xlse = 0, ylse = 0, yjhe = 0, ypfe = 0;
				if (MedicineUtils.parseDouble(map_rk02.get("RKSL")) < 0) {
					pzsl = Math.abs(MedicineUtils.parseDouble(map_rk02.get("RKSL")));
					yjhe = Math.abs(MedicineUtils.parseDouble(map_rk02.get("JHHJ")));
					ypfe = Math.abs(MedicineUtils.parseDouble(map_rk02.get("PFJE")));
					ylse = Math.abs(MedicineUtils.parseDouble(map_rk02.get("LSJE")));
					xjhe = Math.abs(MedicineUtils.parseDouble(map_rk02_db.get("JHHJ")));
					xpfe = Math.abs(MedicineUtils.parseDouble(map_rk02_db.get("PFJE")));
					xlse = Math.abs(MedicineUtils.parseDouble(map_rk02_db.get("LSJE")));
				} else {
					Map<String, Double> pzcl_ret = wf_pzcl(map_rk02);
					pzsl = pzcl_ret.get("pzsl");
					if (pzsl == 0) {
						dao.doUpdate(hql_cdxx_update.toString(), map_par_cdxx);
						dao.doSave("update", BSPHISEntryNames.YK_RK02,
								map_rk02, false);
						dao.doSave("update", BSPHISEntryNames.YK_RK01,
								map_rk01, false);
						continue;
					}
					xjhe = pzcl_ret.get("xjhe");
					xpfe = pzcl_ret.get("xpfe");
					xlse = pzcl_ret.get("xlse");
					ylse = pzcl_ret.get("ylse");
					yjhe = pzcl_ret.get("yjhe");
					ypfe = pzcl_ret.get("ypfe");
				}
				Map<String, Object> map_pz02 = new HashMap<String, Object>();
				//map_pz02.put("PZID", pzid);
				map_pz02.put("XTSB", MedicineUtils.parseLong(map_rk02.get("XTSB")));
				map_pz02.put("JGID", map_rk02.get("JGID"));
				map_pz02.put("SBXH", MedicineUtils.parseLong(map_rk02.get("SBXH")));
				map_pz02.put("YPXH", MedicineUtils.parseLong(map_rk02.get("YPXH")));
				map_pz02.put("YPCD", MedicineUtils.parseLong(map_rk02.get("YPCD")));
				map_pz02.put("YPPH", map_rk02.get("YPPH") + "");
				map_pz02.put("YPXQ", map_rk02.get("YPXQ"));
				map_pz02.put("PZSL", pzsl);
				if (MedicineUtils.parseDouble(map_rk02.get("RKSL")) < 0) {
					map_pz02.put("YJHJ", MedicineUtils.parseDouble(map_rk02.get("JHJG")));
					map_pz02.put("YPFJ", MedicineUtils.parseDouble(map_rk02.get("PFJG")));
					map_pz02.put("YLSJ", MedicineUtils.parseDouble(map_rk02.get("LSJG")));
					map_pz02.put("XJHJ", MedicineUtils.parseDouble(map_rk02_db.get("JHJG")));
					map_pz02.put("XPFJ", MedicineUtils.parseDouble(map_rk02_db.get("PFJG")));
					map_pz02.put("XLSJ", MedicineUtils.parseDouble(map_rk02_db.get("LSJG")));
				} else {
					map_pz02.put("YJHJ", MedicineUtils.parseDouble(map_rk02_db.get("JHJG")));
					map_pz02.put("YPFJ", MedicineUtils.parseDouble(map_rk02_db.get("PFJG")));
					map_pz02.put("YLSJ", MedicineUtils.parseDouble(map_rk02_db.get("LSJG")));
					map_pz02.put("XJHJ", MedicineUtils.parseDouble(map_rk02.get("JHJG")));
					map_pz02.put("XPFJ", MedicineUtils.parseDouble(map_rk02.get("PFJG")));
					map_pz02.put("XLSJ", MedicineUtils.parseDouble(map_rk02.get("LSJG")));
				}
				map_pz02.put("YJHE", yjhe);
				map_pz02.put("YPFE", ypfe);
				map_pz02.put("YLSE", ylse);
				map_pz02.put("XJHE", xjhe);
				map_pz02.put("XPFE", xpfe);
				map_pz02.put("XLSE", xlse);
				//dao.doSave("create", BSPHISEntryNames.YK_PZ02, map_pz02, false);
				list_pz02.add(map_pz02);
				map_par_cdxx.put("jhje", MedicineUtils.parseDouble(map_par_cdxx.get("JHJE"))
						+ yjhe - xjhe);
				dao.doUpdate(hql_cdxx_update.toString(), map_par_cdxx);
				dao.doSave("update", BSPHISEntryNames.YK_RK02, map_rk02, false);
				dao.doSave("update", BSPHISEntryNames.YK_RK01, map_rk01, false);
			}
			// 此处增加平账处理新增记录
			if(list_pz02.size()>0){
				Map<String, Object> map_pz01 = new HashMap<String, Object>();
				map_pz01.put("JGID", MedicineUtils.parseString(map_rk01.get("JGID")));
				map_pz01.put("PZLX", 1);
				map_pz01.put("XTSB", MedicineUtils.parseLong(map_rk01.get("XTSB")));
				map_pz01.put("RCFS", 1);
				map_pz01.put("RCDH", MedicineUtils.parseInt(map_rk01.get("RKDH")));
				map_pz01.put("PZRQ", map_rk01.get("JGID"));
				map_pz01.put("PZRQ", nowDate);
				map_pz01.put("PZGH", userid);
				map_pz01.put("PZYY", "");
				map_pz01 = dao.doSave("create", BSPHISEntryNames.YK_PZ01, map_pz01,
						false);
				long pzid = MedicineUtils.parseLong(map_pz01.get("PZID"));
				for(Map<String,Object> map_pz02:list_pz02){
					map_pz02.put("PZID", pzid);
					dao.doSave("create", BSPHISEntryNames.YK_PZ02, map_pz02, false);
				}
			}
			// 此处判断明细是否全部验收 并更新01表的cwpb
			Map<String, Object> map_par_sfqbys = new HashMap<String, Object>();
			map_par_sfqbys.put("xtsb", MedicineUtils.parseLong(map_rk01.get("XTSB")));
			map_par_sfqbys.put("rkdh", MedicineUtils.parseInt(map_rk01.get("RKDH")));
			map_par_sfqbys.put("rkfs", MedicineUtils.parseInt(map_rk01.get("RKFS")));
			Long l = dao.doCount("YK_RK02",
					hql_sfqbys.toString(), map_par_sfqbys);
			if (l == 0) {
				dao.doUpdate(hql_rk01_update.toString(), map_par_sfqbys);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "财务验收失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "财务验收失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 平账策略..由于页面唯一能改的价格只有进货价格 所以略去了设计里面的批发价格和零售价格的判断
	 * @updateInfo
	 * @param map_rk02
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Double> wf_pzcl(Map<String, Object> map_rk02)
			throws ModelDataOperationException {
		Map<String, Double> ret = new HashMap<String, Double>();
		long kcsb = MedicineUtils.parseLong(map_rk02.get("KCSB"));
		double xjhe_ret = 0, xlse_ret = 0, xpfe_ret = 0, yjhe_ret = 0, ypfe_ret = 0, ylse_ret = 0, pzsl_ret = 0;
		try {
			List<?> cnd_kcsb = CNDHelper.toListCnd(CNDHelper
					.toStringCnd(CNDHelper.createSimpleCnd("eq", "KCSB", "s",
							kcsb)));
			List<Map<String, Object>> list_kcfl = dao.doList(cnd_kcsb, null,
					"YK_KCFL");
			if (list_kcfl == null) {
				// 没有库存分裂 报错(采购入库出现问题 库存分裂没保存进去)
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "库存分裂数据错误");
			}
			double sum = 0;
			for (int i = list_kcfl.size() - 1; i >= 0; i--) {
				Map<String, Object> map_kcfl = list_kcfl.get(i);
				if (i == 0) {
					map_kcfl.put("XJHE", MedicineUtils.parseDouble(map_rk02.get("JHHJ"))
							- sum);
				} else {
					double xjhe_kcfl = MedicineUtils.formatDouble(2,
							MedicineUtils.parseDouble(map_rk02.get("JHJG"))
									* MedicineUtils.parseDouble(map_kcfl.get("KCSL")));
					sum += xjhe_kcfl;
					map_kcfl.put("XJHE", xjhe_kcfl);
				}
				Map<String, Object> map_kcmx = dao.doLoad(
						BSPHISEntryNames.YK_KCMX,
						MedicineUtils.parseLong(map_kcfl.get("FLSB")));
				if (map_kcmx != null) {
					map_kcmx.put("JHJG", map_rk02.get("JHJG"));
					double pzsl = MedicineUtils.formatDouble(2,
							MedicineUtils.parseDouble(map_kcfl.get("KCSL")))
							- MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(map_kcmx.get("KCSL")));
					if (pzsl == 0) {// 药品尚未出库不需要平账，只需修改KCMX中的金额
						map_kcmx.put("JHJE", map_kcfl.get("XJHE"));
					} else {// 已有部分药品出库，出库部分药品需要平账
						double xjhe = MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_kcmx.get("KCSL"))
										* MedicineUtils.parseDouble(map_rk02.get("JHJG")));
						map_kcmx.put("JHJE", xjhe);
						xjhe_ret += xjhe;
						xlse_ret += MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_kcfl.get("LSJE"))
										- MedicineUtils.parseDouble(map_kcmx.get("LSJE")));
						xpfe_ret += MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_kcfl.get("PFJE"))
										- MedicineUtils.parseDouble(map_kcmx.get("PFJE")));
						yjhe_ret += MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_kcfl.get("JHJE"))
										- MedicineUtils.parseDouble(map_kcmx.get("JHJE")));
						ypfe_ret += MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_kcfl.get("PFJE"))
										- MedicineUtils.parseDouble(map_kcmx.get("PFJE")));
						ylse_ret += MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_kcfl.get("LSJE"))
										- MedicineUtils.parseDouble(map_kcmx.get("LSJE")));
						pzsl_ret += pzsl;
					}
					dao.doSave("update", BSPHISEntryNames.YK_KCMX, map_kcmx,
							false);// 更新库存
				} else {// 当前批次药品已全部出库
					pzsl_ret += MedicineUtils.parseDouble(map_kcfl.get("KCSL"));
					xjhe_ret += MedicineUtils.parseDouble(map_kcfl.get("XJHE"));
					xlse_ret += 0;
					xpfe_ret += 0;
					ypfe_ret += MedicineUtils.parseDouble(map_kcfl.get("PFJE"));
					ylse_ret += MedicineUtils.parseDouble(map_kcfl.get("LSJE"));
					yjhe_ret += MedicineUtils.parseDouble(map_kcfl.get("JHJE"));
				}
			}
			ret.put("pzsl", pzsl_ret);
			ret.put("xjhe", xjhe_ret);
			ret.put("xlse", xlse_ret);
			ret.put("xpfe", xpfe_ret);
			ret.put("ypfe", ypfe_ret);
			ret.put("ylse", ylse_ret);
			ret.put("yjhe", yjhe_ret);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "平账出错", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "平账出错", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "平账出错", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 查询药库财务验收药品扣率
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public double queryYPKL(Context ctx) throws ModelDataOperationException {
		double ypkl=1;
		try{
		 ypkl= MedicineUtils.parseDouble(ParameterUtil.getParameter(UserRoleToken.getCurrent().getManageUnit().getId(),
				BSPHISSystemArgument.YPKL_YK, ctx));
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.YPKL_YK, e);
		}
		 return ypkl;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 入库单验收前判断是否已经验收和当月是否已经月结
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationFinancialAcceptance(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			StringBuffer hql_yspb = new StringBuffer();// 验收查询
			StringBuffer hql_sfyj = new StringBuffer();// 是否月结
			hql_yspb.append(" XTSB=:xtsb and RKDH=:rkdh and RKFS=:rkfs and CWPB=1");
			hql_sfyj.append("select count(1) as num from YK_JZJL having max(ZZSJ)>sysdate and XTSB=:xtsb group by XTSB");
			Map<String, Object> map_par_yspb = new HashMap<String, Object>();
			map_par_yspb.put("xtsb", MedicineUtils.parseLong(body.get("XTSB")));
			map_par_yspb.put("rkdh", MedicineUtils.parseInt(body.get("RKDH")));
			map_par_yspb.put("rkfs", MedicineUtils.parseInt(body.get("RKFS")));
			long l = dao.doCount("YK_RK01", hql_yspb.toString(),
					map_par_yspb);
			if (l > 0) {
				return MedicineUtils.getRetMap("已做财务验收,不能重复操作!已刷新窗口!");
			}
			Map<String, Object> map_par_jzjl = new HashMap<String, Object>();
			map_par_jzjl.put("xtsb", MedicineUtils.parseLong(body.get("XTSB")));
			List<Map<String, Object>> list_jzjl = dao.doSqlQuery(
					hql_sfyj.toString(), map_par_jzjl);
			if (list_jzjl != null && list_jzjl.size() != 0) {
				return MedicineUtils.getRetMap("本月已结账,不能把入库单确认在本月!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "入库单验收前判断失败", e);
		}
		return MedicineUtils.getRetMap();
	}

}
