package phis.application.sto.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
/**
 * 月底过账
 * @author caijy
 *
 */
public class StorehouseMonthlyModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseMonthlyModel.class);

	public StorehouseMonthlyModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 月终过账
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveStorehouseMonthly(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb =MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int year = MedicineUtils.parseInt(body.get("year"));
		int month = MedicineUtils.parseInt(body.get("month"));
		int op =MedicineUtils.parseInt(body.get("op"));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DATE, 10);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Map<String,Object> ret=MedicineUtils.getRetMap("月结成功",200);
		try {
			Date cwyf = sdf.parse(sdf.format(c.getTime()));
			if (op == 1) {
				StringBuffer hql_sfyj = new StringBuffer();// 查询当前月是否月结
				hql_sfyj.append(" CWYF=:cwyf and  XTSB=:xtsb and JGID=:jgid");
				Map<String, Object> map_par_sfyj = new HashMap<String, Object>();
				map_par_sfyj.put("cwyf", cwyf);
				map_par_sfyj.put("xtsb", yksb);
				map_par_sfyj.put("jgid", jgid);
				long l = dao.doCount("YK_JZJL",
						hql_sfyj.toString(), map_par_sfyj);
				if (l > 0) {
					return MedicineUtils.getRetMap("本月已过账，是否取消本月过账?",9001);
				}
				// 查询上个月是否月结
				c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
				map_par_sfyj.put("cwyf", sdf.parse(sdf.format(c.getTime())));
				l = dao.doCount("YK_JZJL", hql_sfyj.toString(),
						map_par_sfyj);
				if (l == 0) {
					return MedicineUtils.getRetMap("上月未结账,本月过账无法进行,请先做上一月的结账处理!",9000);
				}
				StorehouseCheckInOutModel model=new StorehouseCheckInOutModel(dao);
				List<String> list_qszzsj = model.dateQuery(body, ctx);
				if (sdf.parse(list_qszzsj.get(1)).getTime() > new Date()
						.getTime()) {
					return MedicineUtils.getRetMap("设定月结截止日为" + list_qszzsj.get(1) + " 提前月结将使"
							+ sdf.format(new Date()) + "之后的业务做账到下个月, 是否继续?",9002);
				}
				yjcl(cwyf, sdf.parse(list_qszzsj.get(0)),
						sdf.parse(list_qszzsj.get(1)), c.getTime(), jgid, yksb);
			} else if (op == 2) {// 取消月结
				qxyj(cwyf, ret, yksb);
				return ret;
			} else {
				StorehouseCheckInOutModel model=new StorehouseCheckInOutModel(dao);
				List<String> list_qszzsj = model.dateQuery(body, ctx);
				c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
				yjcl(cwyf, sdf.parse(list_qszzsj.get(0)),
						sdf.parse(list_qszzsj.get(1)),
						sdf.parse(sdf.format(c.getTime())), jgid, yksb);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "月结失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "月结失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 月结处理
	 * @updateInfo
	 * @param cwyf
	 * @param kssj
	 * @param zzsj
	 * @param precwyf
	 * @param jgid
	 * @param yksb
	 * @throws ModelDataOperationException
	 */
	public void yjcl(Date cwyf, Date kssj, Date zzsj, Date precwyf,
			String jgid, long yksb) throws ModelDataOperationException {
		StringBuffer hql_cdxx = new StringBuffer();// 查询产地
		hql_cdxx.append(
				"select a.YPXH as YPXH,a.YPCD as YPCD,a.JHJG as JHJG,a.PFJG as PFJG,a.LSJG as LSJG from YK_CDXX a,YK_YPXX b where a.YPXH=b.YPXH and a.JGID=b.JGID and b.YKSB=:yksb ");
		StringBuffer hql_syyjjg = new StringBuffer();// 查询上月月结结果
		hql_syyjjg
		.append("select KCSL as KCSL,JHJE as JHJE,PFJE as PFJE,LSJE as LSJE from YK_YJJG where XTSB=:xtsb and CWYF=:cwyf and YPXH=:ypxh and YPCD=:ypcd");
		StringBuffer hql_rk = new StringBuffer();// 入库数据查询
		hql_rk.append(
				"select sum(JHHJ) as JHJE,sum(PFJE) as PFJE,sum(LSJE) as LSJE,sum(RKSL) as RKSL,YPXH as YPXH,YPCD as YPCD from YK_RK02 where YPXH=:ypxh and YPCD=:ypcd and XTSB=:xtsb and YSRQ>=:begin and YSRQ<=:end group by YPXH,YPCD");
		StringBuffer hql_ck = new StringBuffer();// 出库数据查询
		hql_ck.append(
				"select sum(b.JHJE) as JHJE,sum(b.PFJE) as PFJE,sum(b.LSJE) as LSJE,sum(b.SFSL) as SFSL,b.YPXH as YPXH,b.YPCD as YPCD from YK_CK01 a,YK_CK02 b where b.YPXH=:ypxh and b.YPCD=:ypcd and b.XTSB=:xtsb and a.CKRQ>=:begin and a.CKRQ<=:end and a.XTSB=b.XTSB and a.CKDH=b.CKDH and a.CKFS=b.CKFS and a.CKPB=1 group by b.YPXH,b.YPCD");
		StringBuffer hql_tj = new StringBuffer();// 调价数据查询
		hql_tj.append(
				"select sum(b.PFZZ-b.PFJZ) as PFJE,sum(b.LSZZ-b.LSJZ) as LSJE,b.YPXH as YPXH,b.YPCD as YPCD from YK_TJ01 a,YK_TJ02 b where b.YPXH=:ypxh and b.YPCD=:ypcd and b.XTSB=:xtsb and a.ZXRQ>=:begin and a.ZXRQ<=:end and a.XTSB=b.XTSB and a.TJDH=b.TJDH and a.TJFS=b.TJFS and a.ZYPB=1 group by b.YPXH,b.YPCD");
		StringBuffer hql_pz = new StringBuffer();// 平账数据查询
		hql_pz.append(
				"select b.YPXH as YPXH,b.YPCD as YPCD,sum(b.XJHE-b.YJHE) as JHJE,sum(b.XPFE-b.YPFE) as PFJE,sum(b.XLSE-b.YLSE) as LSJE from YK_PZ01 a,YK_PZ02 b where a.PZID=b.PZID and a.XTSB=:xtsb and a.PZRQ>=:begin and a.PZRQ<=:end and b.YPXH=:ypxh and b.YPCD=:ypcd group by b.YPXH,b.YPCD");
		Map<String, Object> map_par_cdxx = new HashMap<String, Object>();
		map_par_cdxx.put("yksb", yksb);
		try {
			List<Map<String, Object>> list_cdxx = dao.doQuery(
					hql_cdxx.toString(), map_par_cdxx);
			for (Map<String, Object> map_cdxx : list_cdxx) {
				boolean insert = false;
				long ypxh = MedicineUtils.parseLong(map_cdxx.get("YPXH"));
				long ypcd = MedicineUtils.parseLong(map_cdxx.get("YPCD"));
				double jcsl = 0;
				double jcje_jh = 0;
				double jcje_ls = 0;
				double jcje_pf = 0;
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("ypxh", ypxh);
				map_par.put("ypcd", ypcd);
				map_par.put("xtsb", yksb);
				map_par.put("cwyf", precwyf);
				Map<String, Object> map_syyjjg = dao.doLoad(
						hql_syyjjg.toString(), map_par);
				if (map_syyjjg != null) {
					insert = true;
					jcsl += MedicineUtils.parseDouble(map_syyjjg.get("KCSL"));
					jcje_jh += MedicineUtils.parseDouble(map_syyjjg.get("JHJE"));
					jcje_ls += MedicineUtils.parseDouble(map_syyjjg.get("LSJE"));
					jcje_pf += MedicineUtils.parseDouble(map_syyjjg.get("PFJE"));
				}
				map_par.remove("cwyf");
				map_par.put("begin", kssj);
				map_par.put("end", zzsj);
				List<Map<String, Object>> list_rk = dao.doSqlQuery(
						hql_rk.toString(), map_par);
				if (list_rk != null && list_rk.size() > 0) {
					Map<String, Object> map_rk = list_rk.get(0);
					insert = true;
					jcsl += MedicineUtils.parseDouble(map_rk.get("RKSL"));
					jcje_jh += MedicineUtils.parseDouble(map_rk.get("JHJE"));
					jcje_ls += MedicineUtils.parseDouble(map_rk.get("LSJE"));
					jcje_pf += MedicineUtils.parseDouble(map_rk.get("PFJE"));
				}
				List<Map<String, Object>> list_ck = dao.doSqlQuery(
						hql_ck.toString(), map_par);
				if (list_ck != null && list_ck.size() > 0) {
					Map<String, Object> map_ck = list_ck.get(0);
					insert = true;
					jcsl -= MedicineUtils.parseDouble(map_ck.get("SFSL"));
					jcje_jh -= MedicineUtils.parseDouble(map_ck.get("JHJE"));
					jcje_ls -= MedicineUtils.parseDouble(map_ck.get("LSJE"));
					jcje_pf -= MedicineUtils.parseDouble(map_ck.get("PFJE"));
				}
				if (insert) {
					Map<String, Object> map_swyj = new HashMap<String, Object>();
					map_swyj.put("JGID", jgid);
					map_swyj.put("XTSB", yksb);
					map_swyj.put("CWYF", cwyf);
					map_swyj.put("YPXH", ypxh);
					map_swyj.put("YPCD", ypcd);
					map_swyj.put("LSJE", jcje_ls);
					map_swyj.put("KCSL", jcsl);
					map_swyj.put("PFJE", jcje_pf);
					map_swyj.put("JHJE", jcje_jh);
					dao.doSave("create", BSPHISEntryNames.YK_SWYJ, map_swyj,
							false);
				}
				List<Map<String, Object>> list_tj = dao.doSqlQuery(
						hql_tj.toString(), map_par);
				if (list_tj != null && list_tj.size() > 0) {
					Map<String, Object> map_tj = list_tj.get(0);
					insert = true;
					jcje_ls += MedicineUtils.parseDouble(map_tj.get("LSJE"));
					jcje_pf += MedicineUtils.parseDouble(map_tj.get("PFJE"));
				}
				List<Map<String, Object>> list_pz = dao.doSqlQuery(
						hql_pz.toString(), map_par);
				if (list_pz != null && list_pz.size() > 0) {
					Map<String, Object> map_pz = list_pz.get(0);
					insert = true;
					jcje_ls -= MedicineUtils.parseDouble(map_pz.get("LSJE"));
					jcje_pf -= MedicineUtils.parseDouble(map_pz.get("PFJE"));
					jcje_jh -= MedicineUtils.parseDouble(map_pz.get("JHJE"));
				}
				if (insert) {
					Map<String, Object> map_yjjg = new HashMap<String, Object>();
					map_yjjg.put("JGID", jgid);
					map_yjjg.put("XTSB", yksb);
					map_yjjg.put("CWYF", cwyf);
					map_yjjg.put("YPXH", ypxh);
					map_yjjg.put("YPCD", ypcd);
					map_yjjg.put("LSJE", jcje_ls);
					map_yjjg.put("KCSL", jcsl);
					map_yjjg.put("PFJE", jcje_pf);
					map_yjjg.put("JHJE", jcje_jh);
					map_yjjg.put("LSJG", MedicineUtils.parseDouble(map_cdxx.get("LSJG")));
					map_yjjg.put("JHJG", MedicineUtils.parseDouble(map_cdxx.get("JHJG")));
					map_yjjg.put("PFJG", MedicineUtils.parseDouble(map_cdxx.get("PFJG")));
					dao.doSave("create", BSPHISEntryNames.YK_YJJG, map_yjjg,
							false);
				}
			}
			Map<String, Object> map_jzjl = new HashMap<String, Object>();
			map_jzjl.put("JGID", jgid);
			map_jzjl.put("XTSB", yksb);
			map_jzjl.put("CWYF", cwyf);
			map_jzjl.put("QSSJ", kssj);
			map_jzjl.put("ZZSJ", zzsj);
			dao.doSave("create", BSPHISEntryNames.YK_JZJL, map_jzjl, false);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "月结失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "月结失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description  取消月结
	 * @updateInfo
	 * @param cwyf
	 * @param ret
	 * @param yksb
	 * @throws ModelDataOperationException
	 */
	public void qxyj(Date cwyf, Map<String, Object> ret, long yksb)
			throws ModelDataOperationException {
		StringBuffer hql_yjyz = new StringBuffer();
		hql_yjyz.append(" XTSB=:xtsb");
		Map<String, Object> map_par_yjyz = new HashMap<String, Object>();
		map_par_yjyz.put("xtsb", yksb);
		try {
			long l = dao.doCount("YK_JZJL", hql_yjyz.toString(),
					map_par_yjyz);
			if (l < 2) {
				ret=MedicineUtils.getRetMap("本月账册为初始账册,不能取消!",9000);
				return;
			}
			hql_yjyz.append(" and CWYF>:cwyf");
			map_par_yjyz.put("cwyf", cwyf);
			l = dao.doCount("YK_JZJL", hql_yjyz.toString(),
					map_par_yjyz);
			if (l > 0) {
				ret=MedicineUtils.getRetMap("只能取消最后一个月的账册!",9000);
				return;
			}
			StringBuffer hql_swyj_delete = new StringBuffer();// 删除实物月结
			hql_swyj_delete.append("delete from YK_SWYJ where CWYF=:cwyf and XTSB=:xtsb");
			StringBuffer hql_yjjg_delete = new StringBuffer();// 删除月结结果
			hql_yjjg_delete.append("delete from YK_YJJG where CWYF=:cwyf and XTSB=:xtsb");
			StringBuffer hql_jzjl_delete = new StringBuffer();// 删除结账记录
			hql_jzjl_delete.append("delete from YK_JZJL where CWYF=:cwyf and XTSB=:xtsb");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("cwyf", cwyf);
			map_par.put("xtsb", yksb);
			dao.doUpdate(hql_swyj_delete.toString(), map_par);
			dao.doUpdate(hql_yjjg_delete.toString(), map_par);
			dao.doUpdate(hql_jzjl_delete.toString(), map_par);
			ret.put("msg", "取消月结成功");
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "取消月结失败", e);
		}
	}
}
