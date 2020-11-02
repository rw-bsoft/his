package phis.application.fsb.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class FamilySickBedizationCheckoutModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedizationCheckoutModel.class);

	public FamilySickBedizationCheckoutModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-21
	 * @description 判断本日是否已做过结帐
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Object> isreckon(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = (String) user.getUserId();
		String jgid = user.getManageUnit().getId();
		String jzrq = body.get("jzrq") + "";
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] s = jzrq.split("-| |:");
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, parseInt(s[0]));
		a.set(Calendar.MONTH, parseInt(s[1]) - 1);
		a.set(Calendar.DATE, parseInt(s[2]));
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		Date begin = a.getTime();
		a.add(Calendar.DAY_OF_YEAR, 1);
		Date end = a.getTime();
		List<Object> ret = new ArrayList<Object>();
		StringBuffer hql_count = new StringBuffer();// 查询有没结账过
		hql_count
				.append("JZRQ >=:begin and JZRQ <:end and  CZGH=:czgh and JGID=:jgid");
		StringBuffer hql_max = new StringBuffer();// 查询最后结账记录
		hql_max.append("select MAX(JZRQ) as JZRQ from ")
				.append("JC_JZXX")
				.append(" where CZGH=:czgh and JGID=:jgid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("czgh", userid);
		map_par.put("jgid", jgid);
		map_par.put("begin", begin);
		map_par.put("end", end);
		try {
			long l = dao.doCount("JC_JZXX",
					hql_count.toString(), map_par);
			if (l > 0) {
				ret.add(true);
			} else {
				ret.add(false);
			}
			map_par.remove("begin");
			map_par.remove("end");
			Map<String, Object> map_max = dao.doLoad(hql_max.toString(),
					map_par);
			if (map_max.get("JZRQ") != null) {
				ret.add(map_max.get("JZRQ"));
			}
			return ret;
		} catch (PersistentDataOperationException e) {
			logger.error("Has done Checkout query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "是否已做过结帐查询失败");
		}

	}

	/**
	 * 
	 * @author
	 * @createDate 2012-11-26
	 * @description 结算日报
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doCreate_jzrb(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = (String) user.getUserId();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> iws_pjxx1 = new HashMap<String, Object>();
		Map<String, Object> iws_pjxx2 = new HashMap<String, Object>();
		Map<String, Object> iws_pjxx3 = new HashMap<String, Object>();
		long iws_pjxx1_pjzs = 0;
		long iws_pjxx2_pjzs = 0;
		long iws_pjxx3_pjzs = 0;
		String  qtysFb="";
		String currentDay = sdf.format(new Date());
		String adt_jzrq = body.get("jzrq") + "";
		String[] s = adt_jzrq.split(" |-|:");
		Calendar a = Calendar.getInstance();
		a.set(parseInt(s[0]), parseInt(s[1]) - 1, parseInt(s[2]),
				parseInt(s[3]), parseInt(s[4]), parseInt(s[5]));
		Date adt_jzrq_Date = a.getTime();
		Date ldt_End = a.getTime();
		if (!currentDay.equals(adt_jzrq.split(" ")[0])) {
			a.set(parseInt(s[0]), parseInt(s[1]) - 1, parseInt(s[2]), 0, 0, 0);
			ldt_End = a.getTime();
		}
		// 住院结算表单
		String ids_jsfp_hql = " SELECT a.ZYH as ZYH,a.JSCS as JSCS,a.JSLX as JSLX,a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.JKHJ as JKHJ,a.FPHM as FPHM,a.ZFPB as ZFPB,a.TPHM as TPHM FROM JC_JCJS a,JC_BRRY b  WHERE a.JSLX<>4 and a.ZYH=b.ZYH and  a.CZGH =:czgh AND a.JGID = :jgid AND  a.JZRQ IS NULL AND a.JSRQ <  :adt_jzrq";
		// 住院缴款表单
		String ids_jksj_hql = "SELECT a.JKXH as JKXH,a.JKJE as JKJE,a.JKFS as JKFS,a.SJHM as SJHM,a.ZFPB as ZFPB,a.CZGH as CZGH  FROM JC_TBKK a WHERE a.CZGH = :czgh AND a.JGID = :jgid AND a.JZRQ IS NULL AND a.JKRQ < :adt_jzrq";
		// 住院结算作废表单
		String ids_jszf_hql = " SELECT a.ZYH as ZYH,a.JSCS as JSCS,a.JSLX as JSLX,(-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,(-1*a.JKHJ) as JKHJ,a.FPHM as FPHM,a.ZFPB as ZFPB,a.TPHM as TPHM FROM JC_JCJS a ,JC_JSZF  b,JC_BRRY d WHERE a.JSLX<>4 and (a.ZYH=d.ZYH ) and ( a.ZYH  = b.ZYH  ) AND ( a.JSCS = b.JSCS ) AND b.ZFGH = :czgh AND b.JGID =:jgid AND  b.JZRQ IS NULL AND b.ZFRQ <  :adt_jzrq";
		// 住院缴款作废表单
		String ids_jkzf_hql = " SELECT a.JKXH as JKXH, (-1*a.JKJE) as JKJE ,a.JKFS as JKFS,a.SJHM as SJHM,a.ZFPB as ZFPB,a.CZGH as CZGH FROM JC_TBKK a ,JC_JKZF b WHERE ( b.JKXH = a.JKXH )   AND   b.ZFGH = :czgh AND b.JGID = :jgid AND  b.JZRQ IS NULL AND b.ZFRQ <  :adt_jzrq";
		// 住院付款表单
		String ids_jsfp_fk_hql = "select a.ZYH as ZYH, a.JSCS as JSCS, a.FKFS as FKFS, a.FKJE as FKJE, a.FKHM as FKHM from JC_FKXX a, JC_JCJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.CZGH = :czgh  AND b.JGID = :jgid AND b.JZRQ IS NULL AND b.JSRQ < :adt_jzrq";
		// 住院付款作废表单
		String ids_jszf_fk_hql = "select a.ZYH as ZYH, a.JSCS as JSCS, a.FKFS as FKFS, a.FKJE as FKJE, a.FKHM as FKHM from JC_FKXX a, JC_JSZF b where a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.ZFGH = :czgh  AND b.JGID = :jgid AND b.JZRQ IS NULL AND b.ZFRQ < :adt_jzrq";
//		// 保险判别为“0”时的 收费统计
//		String ids_dbpb_hql = "select sum (FKJE) as FKJE from(" +
//				" select sum(-1*a.JKJE) as FKJE  from JC_TBKK a left join JC_JKZF b on b.JKXH = a.JKXH where b.ZFGH = :czgh AND b.JGID = :jgid  AND b.JZRQ IS NULL  AND b.ZFRQ < :adt_jzrq " +
//				" UNION all " +
//				" select  sum(a.JKJE) as JKJE  from JC_TBKK a  where a.CZGH = :czgh  AND a.JGID = :jgid AND a.JZRQ IS NULL AND a.JKRQ < :adt_jzrq " +
//				" UNION all" +
//				"  select -sum( a.FKJE ) as FKJE from JC_FKXX a  left join JC_JSZF b  on a.ZYH = b.ZYH  and a.JSCS = b.JSCS  where b.ZFGH= :czgh  AND b.JGID = :jgid  AND b.JZRQ IS NULL  AND b.ZFRQ< :adt_jzrq " +
//				") ";
//		// 保险判别为“0”时的 付款方式统计
//		String ids_fkfs_hql="  select g.FKJE,g.FKFS,f.FKMC from (select sum(b.FKJE) as FKJE,b.fkfs as FKFS  from JC_JCJS a   left join JC_FKXX b  on a.ZYH = b.ZYH   and a.JSCS = b.JSCS left join GY_BRXZ c  on a.BRXZ = c.BRXZ  left join  GY_FKFS d  on b.FKFS=d.FKFS where a.JSLX <> 4   AND a.CZGH= :czgh AND a.JGID = :jgid AND a.JZRQ IS NULL  AND a.JSRQ< :adt_jzrq AND c.DBPB='0' group by b.fkfs  ) g left join GY_FKFS f on g.FKFS=f.FKFS ";
//		// 保险判别为不“0”时的 收费统计
//		String ids_brxz_hql = " select f.BRXZ as BRXZ,f.FKJE as FKJE ,g.XZMC as XZMC  from ( select a.BRXZ BRXZ,sum(b.FKJE) as FKJE   from JC_JCJS a left join JC_FKXX b  on a.ZYH=b.ZYH   and a.JSCS = b.JSCS   left join GY_BRXZ c on a.BRXZ=c.BRXZ where a.JSLX <> 4   AND a.CZGH= :czgh AND a.JGID = :jgid AND a.JZRQ IS NULL  AND a.JSRQ< :adt_jzrq AND c.DBPB !='0' group by a.BRXZ )f left join GY_BRXZ g on f.BRXZ=g.BRXZ";
		String ids_fkfs_hql = "select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from ("+
		"select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from JC_FKXX a, JC_JSZF b where a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.ZFGH = :czgh  AND b.JGID = :jgid AND b.JZRQ IS NULL AND b.ZFRQ < :adt_jzrq "+
		" union all "+
		"select a.FKFS as FKFS, a.FKJE as FKJE from JC_FKXX a, JC_JCJS b where b.JSLX<>4 and a.ZYH = b.ZYH and a.JSCS = b.JSCS AND b.CZGH = :czgh  AND b.JGID = :jgid AND b.JZRQ IS NULL AND b.JSRQ < :adt_jzrq"+
		" union all "+
		"SELECT a.JKFS as FKFS,a.JKJE as FKJE FROM JC_TBKK a WHERE a.CZGH = :czgh AND a.JGID = :jgid AND a.JZRQ IS NULL AND a.JKRQ < :adt_jzrq"+
		" union all "+
		"SELECT a.JKFS as FKFS,(-1*a.JKJE) as FKJE FROM JC_TBKK a ,JC_JKZF b WHERE ( b.JKXH = a.JKXH ) AND b.ZFGH = :czgh AND b.JGID = :jgid AND  b.JZRQ IS NULL AND b.ZFRQ <  :adt_jzrq "+
		") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC";
		String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("+
		"SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a WHERE a.JSLX<>4 and a.CZGH =:czgh AND a.JGID = :jgid AND  a.JZRQ IS NULL AND a.JSRQ <  :adt_jzrq and a.FYHJ<>a.ZFHJ"+
		" union all "+
		"SELECT (-1*a.FYHJ) as FYHJ,(-1*a.ZFHJ) as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a ,JC_JSZF b WHERE a.JSLX<>4 and ( a.ZYH  = b.ZYH  ) AND ( a.JSCS = b.JSCS ) AND b.ZFGH = :czgh AND b.JGID =:jgid AND  b.JZRQ IS NULL AND b.ZFRQ <  :adt_jzrq and a.FYHJ<>a.ZFHJ"+
		") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("czgh", userid);
		map_par.put("jgid", jgid);
		map_par.put("adt_jzrq", adt_jzrq_Date);
		try {
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,map_par);// bu为“0”时的 收费统计
			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(ids_fkfs_hql,map_par);// bu为“0”时的 收费统计
//			List<Map<String, Object>> ids_dbpb = dao.doSqlQuery(ids_dbpb_hql,map_par);// 保险判别为“0”时的 收费统计
			List<Map<String, Object>> ids_jsfp = dao.doSqlQuery(ids_jsfp_hql,
					map_par);// 住院结算表单
			List<Map<String, Object>> ids_jksj = dao.doQuery(ids_jksj_hql,
					map_par);// 住院缴款表单
			List<Map<String, Object>> ids_jszf = dao.doSqlQuery(ids_jszf_hql,
					map_par);// 住院结算作废表单
			List<Map<String, Object>> ids_jkzf = dao.doQuery(ids_jkzf_hql,
					map_par);// 住院缴款作废表单
			List<Map<String, Object>> ids_jsfp_fk = dao.doQuery(
					ids_jsfp_fk_hql, map_par);// 住院付款表单
			List<Map<String, Object>> ids_jszf_fk = dao.doQuery(
					ids_jszf_fk_hql, map_par);// 住院付款作废表单
			if (ids_jsfp.size() + ids_jksj.size() + ids_jszf.size()
					+ ids_jkzf.size() + ids_jsfp_fk.size() + ids_jszf_fk.size() == 0) {
				return null;
			}
			double lc_tyjj = 0d; // 退预缴金
			double lc_cysr = 0d; // 出院收入
			int ll_fpzs = 0; // 发票张数
			double lc_xjje = 0d; // 现金
			double lc_srje = 0d; // 货币误差
			double lc_yjxj = 0d; // 预缴金额
			double lc_qtje = 0d; // 其他金额
			double syb = 0.00;
			double sybzf = 0.00;
			for (Map<String, Object> map_ids_jsfp : ids_jsfp) {
				String ll_zyh = map_ids_jsfp.get("ZYH") + "";
				Map<String, Object> sybPar = new HashMap<String, Object>();
				sybPar.put("ZYH", Long.parseLong(ll_zyh));
//				Map<String, Object> sybMap = dao
//						.doLoad("select sum(YLFZE-GRZFJE) as SYB from YB_ZYJS where ZYH=:ZYH",
//								sybPar);
//				if (sybMap != null && sybMap.size() > 0) {
//					if (sybMap.get("SYB") != null) {
//						syb += Double.parseDouble(sybMap.get("SYB") + "");
//					}
//				}
				String ll_jscs = map_ids_jsfp.get("JSCS") + "";
				lc_tyjj = lc_tyjj
						+ (((map_ids_jsfp.get("JKHJ") != null) ? Double
								.parseDouble(map_ids_jsfp.get("JKHJ") + "")
								: 0d));
				lc_cysr = lc_cysr
						+ (((map_ids_jsfp.get("FYHJ") != null) ? Double
								.parseDouble(map_ids_jsfp.get("FYHJ") + "")
								: 0d));
				ll_fpzs++;
				List<Map<String, Object>> ids_jsfp_fk_AfterFilter = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> map_ids_jsfp_fk : ids_jsfp_fk) {
					if ((map_ids_jsfp_fk.get("ZYH") + "").equals(ll_zyh)
							&& (ll_jscs
									.equals(map_ids_jsfp_fk.get("JSCS") + ""))) {
						ids_jsfp_fk_AfterFilter.add(map_ids_jsfp_fk);
					}
				}
				for (Map<String, Object> map_ids_jsfp_fk_AfterFilter : ids_jsfp_fk_AfterFilter) {
					long fklb = wf_get_fklb(Long
							.parseLong(map_ids_jsfp_fk_AfterFilter.get("FKFS")
									+ ""));
					if (fklb == 1) {
						lc_xjje = lc_xjje
								+ parseDouble(map_ids_jsfp_fk_AfterFilter
										.get("FKJE") + "");
					} else if (fklb == 4) {
						lc_srje = lc_srje
								+ parseDouble(map_ids_jsfp_fk_AfterFilter
										.get("FKJE") + "");
					} else {
						lc_qtje = lc_qtje
								+ parseDouble(map_ids_jsfp_fk_AfterFilter
										.get("FKJE") + "");
					}
				}
			}

			for (Map<String, Object> map_ids_jszf : ids_jszf) {
				String ll_zyh = map_ids_jszf.get("ZYH") + "";
				Map<String, Object> sybPar = new HashMap<String, Object>();
				sybPar.put("ZYH", Long.parseLong(ll_zyh));
//				Map<String, Object> sybMap = dao
//						.doLoad("select sum(YLFZE-GRZFJE) as SYB from YB_ZYJS where ZYH=:ZYH",
//								sybPar);
//				if (sybMap != null && sybMap.size() > 0) {
//					if (sybMap.get("SYB") != null) {
//						sybzf += Double.parseDouble(sybMap.get("SYB") + "");
//					}
//				}
				String ll_jscs = map_ids_jszf.get("JSCS") + "";
				// 累加作废发票数量，保存作废发票号码
				iws_pjxx1_pjzs++;
				iws_pjxx1.put(iws_pjxx1_pjzs + "", map_ids_jszf.get("FPHM")
						+ "");
				// 取结算作废退预缴金并将其累加
				lc_tyjj = lc_tyjj + parseDouble(map_ids_jszf.get("JKHJ") + ""); // ids_jszf.jkhj[ll_Row]
				// 累加出院收入
				lc_cysr = lc_cysr + parseDouble(map_ids_jszf.get("FYHJ") + ""); // ids_jszf.zfhj
				List<Map<String, Object>> ids_jszf_fk_AfterFilter = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> map : ids_jszf_fk) {
					if ((map.get("ZYH") + "").equals(ll_zyh)
							&& (ll_jscs.equals(map.get("JSCS") + ""))) {
						ids_jszf_fk_AfterFilter.add(map);
					}
				}
				for (Map<String, Object> map : ids_jszf_fk_AfterFilter) {
					long fklb = wf_get_fklb(Long
							.parseLong(map.get("FKFS") + ""));
					if (fklb == 1) {
						lc_xjje = lc_xjje - parseDouble(map.get("FKJE") + "");
					} else if (fklb == 4) {
						lc_srje = lc_srje - parseDouble(map.get("FKJE") + "");
					} else {
						lc_qtje = lc_qtje - parseDouble(map.get("FKJE") + "");
					}
				}
			}

			Long ll_sjzs = 0L;
			double lc_qtjk = 0;
			double lc_jkhj = 0;
			for (Map<String, Object> map_ids_jksj : ids_jksj) {
				long fklb = wf_get_fklb(parseLong(map_ids_jksj.get("JKFS") + ""));
				if (fklb != 3) {
					ll_sjzs++;
				}
				if (fklb == 1) {
					lc_yjxj = lc_yjxj
							+ parseDouble(map_ids_jksj.get("JKJE") + "");
				} else {
					lc_qtjk = lc_qtjk
							+ parseDouble(map_ids_jksj.get("JKJE") + "");
				}
				lc_jkhj = lc_jkhj + parseDouble(map_ids_jksj.get("JKJE") + "");
			}
			for (Map<String, Object> map : ids_jkzf) {
				long fklb = wf_get_fklb(parseLong(map.get("JKFS") + ""));
				iws_pjxx2_pjzs++;
				String pjzs = iws_pjxx2_pjzs + "";
				iws_pjxx2.put(pjzs, map.get("SJHM") + "");
				if (fklb == 1) {
					lc_yjxj = lc_yjxj + parseDouble(map.get("JKJE") + "");
				} else {
					lc_qtjk = lc_qtjk + parseDouble(map.get("JKJE") + "");
				}
				lc_jkhj = lc_jkhj + parseDouble(map.get("JKJE") + "");
			}

			// 算结算票据和缴款收据 票据序列
			ids_jsfp_hql += " and a.FPHM is not null order by a.FPHM asc";
			ids_jsfp = dao.doSqlQuery(ids_jsfp_hql, map_par);
			String ls_pjxl_fp = "";
			if (ids_jsfp.size() > 0) {
				String ls_begin_pjhm = ids_jsfp.get(0).get("FPHM") + "";
				String ls_curr_pjhm = ids_jsfp.get(0).get("FPHM") + "";
				String ls_end_pjhm = "";
				for (int i = 0; i < ids_jsfp.size(); i++) {
					if (!(ids_jsfp.get(i).get("FPHM") + "")
							.equals(ls_curr_pjhm)) {
						ls_end_pjhm = (ids_jsfp.get(i - 1).get("FPHM") + "");
						ls_pjxl_fp = wf_addtopjxl(ls_pjxl_fp, ls_begin_pjhm,
								ls_end_pjhm);
						ls_begin_pjhm = ids_jsfp.get(i).get("FPHM") + "";
						ls_curr_pjhm = ids_jsfp.get(i).get("FPHM") + "";
					}
					ls_curr_pjhm = wf_addpjhm(ls_curr_pjhm); // 票据号码加1
				}
				ls_end_pjhm = ids_jsfp.get(ids_jsfp.size() - 1).get("FPHM")
						+ "";
				ls_pjxl_fp = wf_addtopjxl(ls_pjxl_fp, ls_begin_pjhm,
						ls_end_pjhm);
			}

			ids_jksj_hql += " and a.SJHM is not null order by a.SJHM asc";
			ids_jksj = dao.doQuery(ids_jksj_hql, map_par);
			String ls_pjxl_sj = "";
			if (ids_jksj.size() > 0) {
				String ls_begin_pjhm = ids_jksj.get(0).get("SJHM") + "";
				String ls_curr_pjhm = ids_jksj.get(0).get("SJHM") + "";
				String ls_end_pjhm = "";
				for (int i = 0; i < ids_jksj.size(); i++) {
					if (!(ids_jksj.get(i).get("SJHM") + "")
							.equals(ls_curr_pjhm)) {
						ls_end_pjhm = (ids_jksj.get(i - 1).get("SJHM") + "");
						ls_pjxl_sj = wf_addtopjxl(ls_pjxl_sj, ls_begin_pjhm,
								ls_end_pjhm);
						ls_begin_pjhm = ids_jksj.get(i).get("SJHM") + "";
						ls_curr_pjhm = ids_jksj.get(i).get("SJHM") + "";
					}
					;
					ls_curr_pjhm = wf_addpjhm(ls_curr_pjhm); // 票据号码加1
				}
				ls_end_pjhm = ids_jksj.get(ids_jksj.size() - 1).get("SJHM")
						+ "";
				ls_pjxl_sj = wf_addtopjxl(ls_pjxl_sj, ls_begin_pjhm,
						ls_end_pjhm);
			}
			// 取退预缴款收据张数
			map_par.put("ldt_End", ldt_End);
			map_par.remove("adt_jzrq");
			String ll_tjks_hql = "SELECT COUNT(*) as TJZS  FROM JC_TBKK a,JC_JCJS b WHERE b.JSLX<>4 and (a.ZYH = b.ZYH)AND(a.JSCS = b.JSCS)AND(a.ZFPB = 0)AND(b.ZFPB = 0)AND(b.CZGH = :czgh)AND(b.JZRQ IS NULL)AND(b.JSRQ < :ldt_End)AND (a.JGID = b.JGID) AND (a.JGID = :jgid)";
			Map<String, Object> map_ll_tjks = dao.doLoad(ll_tjks_hql, map_par);
			long ll_tjks = parseLong(map_ll_tjks.get("TJZS") + "");

			// 将数据写入日终结帐表单
			Map<String, Object> pjxx = new HashMap<String, Object>();
			pjxx.put("JZRQ", adt_jzrq); // 日终结帐表单JZRQ[1] = adt_jzrq // 结帐日期
			pjxx.put("CZGH", userid); // 日终结帐表单CZGH[1] = 当前操作工号 // 操作工号
			pjxx.put("CYSR", lc_cysr); // 日终结帐表单cysr[1] = lc_cysr // 出院收入
			pjxx.put("YJJE", lc_jkhj); // 日终结帐表单yjje[1] = lc_yjxj// 预缴金额
			pjxx.put("YJXJ", lc_yjxj); // 日终结帐表单yjxj[1] = lc_yjxj // 预缴现金
			pjxx.put("YJZP", 0); // 日终结帐表单yjzp[1] = 0 // 预缴支票
			pjxx.put("YJQT", lc_qtjk); // 日终结帐表单yjqt[1] = 0 // 预缴其它
			pjxx.put("YJYHK", 0); // 日终结帐表单yjyhk[1] = 0 // 预缴银行卡
			pjxx.put("KBJE", 0); // 日终结帐表单kbje[1] = 0 // 空白支票金额
			pjxx.put("QZPJ", ls_pjxl_fp); // 日终结帐表单qzpj[1] = ls_pjxl_fp //
											// 起止发票序列
			pjxx.put("QZSJ", ls_pjxl_sj); // 日终结帐表单qzsj[1] = ls_pjxl_sj //
											// 起止收据序列
			pjxx.put("FPZS", ll_fpzs); // 日终结帐表单fpzs[1] = ll_fpzs // 发票张数
			pjxx.put("SJZS", ll_sjzs); // 日终结帐表单sjzs[1] = ll_sjzs
			pjxx.put("YSJE",lc_cysr+lc_jkhj-lc_tyjj);
//			pjxx.put("YSJE", lc_xjje + lc_jkhj + lc_srje + lc_qtje); // 日终结帐表单ysje[1]
																		// =
																		// lc_xjje
																		// +
																		// lc_srje
																		// //
																		// 应收金额
			pjxx.put("YSXJ", lc_xjje + lc_yjxj); // 日终结帐表单ysxj[1] = lc_xjje +
													// lc_yjxj // 应收现金
			pjxx.put("YSZP", 0); // 日终结帐表单yszp[1] = 0 // 应收支票
			pjxx.put("YSQT", lc_qtje + lc_qtjk); // 日终结帐表单ysqt[1] = 0 // 应收其它
			pjxx.put("YSYH", 0); // 日终结帐表单ysyh[1] = 0 // 应收优惠
			pjxx.put("YSYHK", 0); // 日终结帐表单ysyhk[1] = 0 // 应收银行卡
			pjxx.put("SRJE", lc_srje); // 日终结帐表单srje[1] = lc_srje // 舍入金额
			pjxx.put("ZPZS", 0); // 日终结帐表单zpzs[1] = ll_zpzs // 支票张数
			pjxx.put("QTZS", 0); // 日终结帐表单qtzs[1] = 0 // 其它张数
			pjxx.put("TYJJ", lc_tyjj); // 日终结帐表单tyjj[1] = lc_tyjj // 退预缴金
			pjxx.put("TJKS", ll_tjks); // 日终结帐表单tjks[1] = ll_tjks // 退预缴款张数
			pjxx.put("KBZP", 0); // 日终结帐表单kbzp[1] = 0 // 空白支票张数
			pjxx.put("JGID", jgid); // 日终结帐表单jgid[1] = gl_jgid
			pjxx.put("TPJE", 0); // 日终结帐表单TPJE[1] = 0
//			double xjje=0.0;//现金金额
//			double zsje=0.0;//总收金额
//			double jzje=0.0;//记账金额
//			xjje=lc_xjje + lc_yjxj;
//			qtysFb="现金:" +xjje+" ";
			String jzjeSt="0.00";
			
//			//保险判别不为“0”付款方式
			if (ids_fkfs  != null && ids_fkfs .size() != 0) {
				 for(int i=0;i<ids_fkfs.size();i++){
//					 if("现金".equals(ids_fkfs.get(i).get("FKMC")+"")){
//						 qtysFb = qtysFb +ids_fkfs.get(i).get("FKMC")+ ":"
//									+ String.format("%1$.2f",Double.parseDouble(ids_fkfs.get(i).get("FKJE")+"")+xjje)
//									+ " ";
//					 }else{
						 qtysFb = qtysFb +ids_fkfs.get(i).get("FKMC")+ ":"
								+ String.format("%1$.2f",ids_fkfs.get(i).get("FKJE"))
								+ " ";
//					 }
				 }
			}
//			//保险判别不为“0”
			if (ids_brxz  != null && ids_brxz .size() != 0) {
				 for(int i=0;i<ids_brxz.size();i++){
					 if(Integer.parseInt(ids_brxz.get(i).get("DBPB")+"")==0){
						 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +(parseDouble(ids_brxz.get(i).get("FYHJ")+ "")-parseDouble(ids_brxz.get(i).get("ZFHJ")+ "")));
						
//						 jzjeSt=String.format("%1$.2f", jzje);
					 }else{
						 qtysFb = qtysFb +ids_brxz.get(i).get("XZMC")+ ":"
								+ String.format("%1$.2f",(parseDouble(ids_brxz.get(i).get("FYHJ")+ "")-parseDouble(ids_brxz.get(i).get("ZFHJ")+ "")))
								+ " ";
					 }
				 }
				 qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
			}
//			//保险判别为“0”
//			if (ids_dbpb  != null && ids_dbpb .size() != 0) {
//				 for(int i=0;i<ids_dbpb.size();i++){
//					 zsje=parseDouble(ids_dbpb.get(0).get("FKJE")+ "");
//					 jzje=zsje-xjje;
//					 jzjeSt=String.format("%1$.2f", jzje);
//				 }
//				 qtysFb = qtysFb+" " + "记账 :"
//							+ jzjeSt
//							+ " ";
//			}
			pjxx.put("qtysFb", qtysFb);
			
			
			// 写票据信息
			Map<String, Object> zfpj = wf_WriteBillInfo(iws_pjxx1_pjzs,
					iws_pjxx1, iws_pjxx2_pjzs, iws_pjxx2, iws_pjxx3_pjzs,
					iws_pjxx3);
			pjxx.putAll(zfpj);
			pjxx.put("SYB", (syb - sybzf));
			ret.put("JZRQ", adt_jzrq_Date);
			ret.put("jzxx", pjxx);
			ret.put("iws_pjxx1", iws_pjxx1);
			ret.put("iws_pjxx2", iws_pjxx2);
			ret.put("iws_pjxx3", iws_pjxx3);
			ret.put("iws_pjxx1_pjzs", iws_pjxx1_pjzs);
			ret.put("iws_pjxx2_pjzs", iws_pjxx2_pjzs);
			ret.put("iws_pjxx3_pjzs", iws_pjxx3_pjzs);
			ret.put("SYB", (syb - sybzf));
		
		} catch (PersistentDataOperationException e) {
			logger.error("Has done Checkout query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "表单查询失败");
		}
//		System.out.println("-----33-------ret="+ret.toString());
		return ret;
		

	}

	private Map<String, Object> wf_WriteBillInfo(long iws_pjxx1_pjzs,
			Map<String, Object> iws_pjxx1, long iws_pjxx2_pjzs,
			Map<String, Object> iws_pjxx2, long iws_pjxx3_pjzs,
			Map<String, Object> iws_pjxx3) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Long i;
		String ls_zffp = ""; // 作废发票号码
		String ls_zfsj = ""; // 作废收据号码
		// String ls_tphm = ""; // 退票号码
		String ls_Temp = "";

		// 作废发票(aws_pjxx[1])
		for (i = 1L; i <= iws_pjxx1_pjzs; i++) {
			if ("".equals(ls_Temp)) {
				ls_Temp = iws_pjxx1.get(i + "") + "";
				ls_zffp += iws_pjxx1.get(i + "") + "";
			} else if ((ls_Temp + " " + iws_pjxx1.get(i + "") + "").length() > 17) {
				ls_Temp = iws_pjxx1.get(i + "") + "";
				ls_zffp += " " + iws_pjxx1.get(i + "") + "";
			} else {
				ls_Temp += " " + iws_pjxx1.get(i + "") + "";
				ls_zffp += " " + iws_pjxx1.get(i + "") + "";
			}
		}

		// 作废收据(aws_pjxx[2])
		ls_Temp = "";
		for (i = 1L; i <= iws_pjxx2_pjzs; i++) {
			if ("".equals(ls_Temp)) {
				ls_Temp = iws_pjxx2.get(i + "") + "";
				ls_zfsj += iws_pjxx2.get(i + "") + "";
			} else if ((ls_Temp + " " + iws_pjxx2.get(i + "") + "").length() > 17) {
				ls_Temp = iws_pjxx2.get(i + "") + "";
				ls_zfsj += " " + iws_pjxx2.get(i + "") + "";
			} else {
				ls_Temp += " " + iws_pjxx2.get(i + "") + "";
				ls_zfsj += " " + iws_pjxx2.get(i + "") + "";
			}
		}

		String jsfp = ls_zffp; // 日终结帐表单中结算发票的内容 = ls_zffp
		String jksj = ls_zfsj; // 日终结帐表单中缴款收据的内容 = ls_zfsj
		ret.put("JSFP", jsfp);
		ret.put("JKSJ", jksj);
		return ret;
	}

	private String wf_addtopjxl(String as_pjxl, String as_begin, String as_end) {
		if (as_begin.equals(as_end)) {
			as_pjxl += " " + as_begin;
		} else {
			as_pjxl += " " + as_begin + "-" + as_end;
		}
		return as_pjxl;
	}

	private String wf_addpjhm(String as_pjhm) {
		String ls_string = "0000000000000000000000000000000";
		String ls_char = "";
		String ls_pjhm = "";
		int ls_i = 0;
		int fphm_length = 0;
		for (int i = as_pjhm.length() - 1; i >= 0; i--) {
			if (!(as_pjhm.charAt(i) >= '0' && as_pjhm.charAt(i) <= '9')) {
				ls_i = i + 1;
				break;
			}
		}
		fphm_length = as_pjhm.length() - ls_i; // 发票号码数字部分的长度
		ls_char = as_pjhm.substring(0, ls_i);
		ls_pjhm = as_pjhm.substring(ls_i);
		ls_pjhm = String.valueOf(Long.valueOf(ls_pjhm) + 1);
		ls_pjhm = ls_char
				+ ls_string.substring(0, Math.abs(fphm_length - ls_pjhm.length()))
				+ ls_pjhm;
		return ls_pjhm.trim();
	}

	/**
	 * 获取付款类别
	 * 
	 * @param al_fkfs
	 * @return
	 * @throws ModelDataOperationException
	 */
	private long wf_get_fklb(long al_fkfs) throws ModelDataOperationException {
		long ll_fklb = 0;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_fkfs", al_fkfs);
		String hql = "SELECT a.FKLB as FKLB FROM GY_FKFS a WHERE a.FKFS = :al_fkfs AND a.SYLX = 2";
		try {
			Map<String, Object> ret = dao.doLoad(hql, parameters);
			if (ret == null || "".equals(ret.get("FKLB") + "")
					|| "null".equals(ret.get("FKLB") + "")) {
				ll_fklb = 0;
			} else {
				ll_fklb = parseLong(ret.get("FKLB") + "");
			}
			if (ll_fklb == 0) {
				ll_fklb = 1;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Has done Checkout query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "表单查询失败");
		}
		return ll_fklb;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-21
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null || "null".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-21
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null || "null".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-21
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null || "null".equals(o)) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-21
	 * @description double型数据转换
	 * @updateInfo
	 * @param number
	 *            保留小数点后几位
	 * @param data
	 *            数据
	 * @return
	 */
	public double formatDouble(int number, double data) {
//		if (number > 8) {
//			return 0;
//		}
//		double x = Math.pow(10, number);
//		return Math.round(data * x) / x;
		return BSPHISUtil.getDouble(data,number);
	}

	public boolean doWf_Check(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = (String) user.getUserId();
		String jgid = user.getManageUnit().getId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		long ll_Count = 0;
		String ls_idt_jzrq = body.get("idt_jzrq") + "";
		String[] s = ls_idt_jzrq.split("-| |:");
		Calendar cal = Calendar.getInstance();
		cal.set(parseInt(s[0]), parseInt(s[1]) - 1, parseInt(s[2]),
				parseInt(s[3]), parseInt(s[4]), parseInt(s[5]));
		Date idt_jzrq = cal.getTime();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_UserId", userid);
		map.put("idt_jzrq", idt_jzrq);
		map.put("gl_jgid", jgid);
		if (ls_idt_jzrq.split(" ")[0].equals(sdf.format(new Date()))) {
			try {
				String hql = "CZGH = :is_UserId AND JZRQ IS NULL AND JKRQ > :idt_jzrq and JGID = :gl_jgid";
				ll_Count = dao.doCount("JC_TBKK", hql, map);
				if (ll_Count > 0) {
					return false;
				}
				hql = "ZFGH = :is_UserId AND JZRQ IS NULL AND ZFRQ > :idt_jzrq and JGID = :gl_jgid ";
				ll_Count = dao.doCount("JC_JKZF", hql, map);
				if (ll_Count > 0) {
					return false;
				}
				hql = "CZGH = :is_UserId AND JZRQ IS NULL AND JSRQ > :idt_jzrq and JGID = :gl_jgid";
				ll_Count = dao.doCount("JC_JCJS", hql, map);
				if (ll_Count > 0) {
					return false;
				}
				hql = "ZFGH = :is_UserId AND JZRQ IS NULL AND ZFRQ > :idt_jzrq and JGID = :gl_jgid ";
				ll_Count = dao.doCount("JC_JSZF", hql, map);
				if (ll_Count > 0) {
					return false;
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Has done Checkout query failed", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "表单查询失败");
			}
		}
		return true;
	}

	/**
	 * 结账
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void doSave_jzrb(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException, ValidateException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = (String) user.getUserId();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String ls_idt_jzrq = body.get("idt_jzrq") + "";
		Date idt_jzrq = BSHISUtil.toDate(ls_idt_jzrq);
		parameters.put("idt_jzrq", idt_jzrq);
		parameters.put("userid", userid);
		parameters.put("gl_jgid", jgid);
		try {
			// 日终结帐表单保存
			Map<String, Object> jzxx = (Map<String, Object>) body.get("jzxx");
			jzxx.put("SRJE", parseDouble(jzxx.get("SRJE")));
			jzxx.put("SYB", parseDouble(jzxx.get("SYB")));
//			Date jzrq = BSHISUtil.toDate((jzxx.get("JZRQ") + ""));
			jzxx.put("JZRQ", idt_jzrq);
			jzxx.put("QTYSFB", jzxx.get("qtysFb") + "");
			dao.doSave("create", BSPHISEntryNames.JC_JZXX, jzxx, false);
			// 将作废发票1、作废收据2及退票号码3写入JC_ZFPJ
			for (int i = 1; i < 4; i++) {
				int ll_pjlb = i;
				Map<String, Object> pjhms = (Map<String, Object>) body
						.get("iws_pjxx" + i);
				Map<String, Object> zfpj = new HashMap<String, Object>();
				zfpj.put("JZRQ", idt_jzrq);
				zfpj.put("CZGH", userid);
				zfpj.put("PJLB", ll_pjlb);
				zfpj.put("JGID", jgid);
				for (int j = 1; j <= pjhms.size(); j++) {
					zfpj.put("PJHM", pjhms.get(j + ""));
					// hql =
					// "INSERT INTO JC_ZFPJ(JZRQ, CZGH , PJLB ,PJHM,JGID) values( :idt_jzrq, :userid, :ll_pjlb, :pjhm, :gl_jgid)";
					dao.doSave("create", BSPHISEntryNames.JC_ZFPJ, zfpj, false);
				}
			}

			// 将结帐日期写入JC_TBKK
			long ll_Count = 0;
//			String ls_idt_End = body.get("idt_End") + "";
			Date idt_End = BSHISUtil.toDate(ls_idt_jzrq);
			parameters.put("ldt_End", idt_End);
			parameters.remove("idt_jzrq");
			String hqlWhere = "CZGH = :userid AND JZRQ IS NULL AND JKRQ < :ldt_End  and JGID = :gl_jgid";
			ll_Count = dao.doCount("JC_TBKK", hqlWhere,
					parameters);
			String hql_update = "";
			if (ll_Count > 0) {
				parameters.put("idt_jzrq", idt_jzrq);
				hql_update = "UPDATE JC_TBKK SET JZRQ = :idt_jzrq WHERE CZGH =:userid AND JZRQ IS NULL AND JKRQ < :ldt_End  and JGID = :gl_jgid ";
				dao.doUpdate(hql_update, parameters);
			}

			// 将结帐日期写入JC_JKZF
			parameters.remove("idt_jzrq");
			ll_Count = 0;
			hqlWhere = "ZFGH = :userid AND JZRQ IS NULL AND ZFRQ < :ldt_End  and JGID = :gl_jgid";
			ll_Count = dao.doCount("JC_JKZF", hqlWhere,
					parameters);
			if (ll_Count > 0) {
				parameters.put("idt_jzrq", idt_jzrq);
				hql_update = "UPDATE JC_JKZF SET JZRQ = :idt_jzrq WHERE ZFGH = :userid AND JZRQ IS NULL AND ZFRQ < :ldt_End and JGID = :gl_jgid";
				dao.doUpdate(hql_update, parameters);
			}

			// 将结帐日期写入JC_JCJS
			parameters.remove("idt_jzrq");
			ll_Count = 0;
			hqlWhere = "CZGH = :userid AND JZRQ IS NULL AND JSRQ < :ldt_End  and JGID = :gl_jgid";
			ll_Count = dao.doCount("JC_JCJS", hqlWhere,
					parameters);
			if (ll_Count > 0) {
				parameters.put("idt_jzrq", idt_jzrq);
				hql_update = "UPDATE JC_JCJS SET JZRQ = :idt_jzrq WHERE CZGH = :userid AND JZRQ IS NULL AND JSRQ < :ldt_End and JGID = :gl_jgid";
				dao.doUpdate(hql_update, parameters);
			}

			// 将结帐日期写入JC_JSZF
			parameters.remove("idt_jzrq");
			ll_Count = 0;
			hqlWhere = "ZFGH = :userid AND JZRQ IS NULL AND ZFRQ < :ldt_End  and JGID = :gl_jgid";
			ll_Count = dao.doCount("JC_JSZF", hqlWhere,
					parameters);
			if (ll_Count > 0) {
				parameters.put("idt_jzrq", idt_jzrq);
				hql_update = "UPDATE JC_JSZF SET JZRQ = :idt_jzrq WHERE ZFGH = :userid AND JZRQ IS NULL AND ZFRQ < :ldt_End  and JGID = :gl_jgid";
				dao.doUpdate(hql_update, parameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Has done Checkout query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "表单操作失败");
		}
	}

	// 日终结帐表单检索数据，参数是：adt_clrq_b、adt_clrq_e、当前工号、gl_jgid
	public Map<String, Object> doQuery_ZY_JZXX(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> jzxx = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = (String) user.getUserId();
		String jgid = user.getManageUnit().getId();
		String ls_adt_clrq_b = body.get("adt_clrq_b") + "";
//		String ls_adt_clrq_e = body.get("adt_clrq_e") + "";
		String jzrq = body.get("jzrq") + "";
		Date adt_clrq_b = BSHISUtil.toDate(ls_adt_clrq_b);
//		Date adt_clrq_e = BSHISUtil.toDate(ls_adt_clrq_e);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", jgid);
		parameters.put("as_czgh", userid);
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		parameters.put("adt_jzrq_s", sdftime.format(adt_clrq_b));
//		parameters.put("adt_jzrq_e", adt_clrq_e);
		String hql = "SELECT  a.QTYSFB as QTYSFB,a.JZRQ as JZRQ, a.CZGH as CZGH,a.CYSR as CYSR,a.YJJE as YJJE,a.YJXJ as YJXJ, a.YJZP as YJZP,a.QZPJ as QZPJ,a.QZSJ as QZSJ,a.FPZS as FPZS, a.SJZS as SJZS,a.YSJE as YSJE,a.YSXJ as YSXJ,a.YSZP as YSZP,a.ZPZS as ZPZS, a.TYJJ as TYJJ,a.TJKS as TJKS,a.KBJE as KBJE,a.KBZP as KBZP, a.YSQT as YSQT,a.QTZS as QTZS,a.SRJE as SRJE,a.YJYHK as YJYHK, a.YSYHK as YSYHK, a.YSYH  as YSYH, a.JGID  as JGID FROM JC_JZXX  a WHERE ( to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') = :adt_jzrq_s ) AND  ( a.CZGH = :as_czgh ) AND   a.JGID = :al_jgid order by a.JZRQ";
		if (body.get("jzrq") != null) {
			// Date jzrq = BSHISUtil.toDate(ls_jzrq);
			parameters.put("jzrq", jzrq);
			parameters.remove("adt_jzrq_s");
//			parameters.remove("adt_jzrq_e");
			hql = "SELECT  a.QTYSFB as QTYSFB,a.JZRQ as JZRQ, a.CZGH as CZGH,a.CYSR as CYSR,a.YJJE as YJJE,a.YJXJ as YJXJ, a.YJZP as YJZP,a.QZPJ as QZPJ,a.QZSJ as QZSJ,a.FPZS as FPZS, a.SJZS as SJZS,a.YSJE as YSJE,a.YSXJ as YSXJ,a.YSZP as YSZP,a.ZPZS as ZPZS, a.TYJJ as TYJJ,a.TJKS as TJKS,a.KBJE as KBJE,a.KBZP as KBZP, a.YSQT as YSQT,a.QTZS as QTZS,a.SRJE as SRJE,a.YJYHK as YJYHK, a.YSYHK as YSYHK, a.YSYH  as YSYH, a.JGID  as JGID FROM JC_JZXX  a WHERE ( to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') = :jzrq ) AND  ( a.CZGH = :as_czgh ) AND   a.JGID = :al_jgid ";
		}
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = dao.doQuery(hql, parameters);
			// 若记录数大于一(多次结帐)，将第一条以后的记录合并到第一条中
			if (list.size() > 0) {
				double CYSR = 0;
				double YJJE = 0;
				double YJXJ = 0;
				int FPZS = 0;
				int SJZS = 0;
				double SYB = 0;
				double YSJE = 0;
				double YSXJ = 0;
				double YSQT = 0;
				int ZPZS = 0;
				double TYJJ = 0;
				int TJKS = 0;
				double SRJE = 0;
				String qtysFb="";
				StringBuffer QZPJ = new StringBuffer("");
				StringBuffer QZSJ = new StringBuffer("");
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < list.size(); i++) {
					map = list.get(i);
					SRJE = SRJE + parseDouble(map.get("SRJE") + "");

					CYSR = CYSR + parseDouble(map.get("CYSR") + ""); // 日终结帐表单.cysr[1]
																		// =
																		// 日终结帐表单.cysr[1]
																		// +
																		// 日终结帐表单.cysr[ll_Row]
					YJJE = YJJE + parseDouble(map.get("YJJE") + ""); // 日终结帐表单.yjje[1]
																		// =
																		// 日终结帐表单.yjje[1]
																		// +
																		// 日终结帐表单.yjje[ll_Row]
					YJXJ = YJXJ + parseDouble(map.get("YJXJ") + ""); // 日终结帐表单.yjxj[1]
																		// =
																		// 日终结帐表单.yjxj[1]
																		// +
																		// 日终结帐表单.yjxj[ll_Row]
					FPZS = FPZS + parseInt(map.get("FPZS") + ""); // 日终结帐表单.fpzs[1]
																	// =
																	// 日终结帐表单.fpzs[1]
																	// +
																	// 日终结帐表单.fpzs[ll_Row]
					SJZS = SJZS + parseInt(map.get("SJZS") + ""); // 日终结帐表单.sjzs[1]
																	// =
																	// 日终结帐表单.sjzs[1]
																	// +
																	// 日终结帐表单.sjzs[ll_Row]
					YSJE = YSJE + parseDouble(map.get("YSJE") + ""); // 日终结帐表单.ysje[1]
																		// =
																		// 日终结帐表单.ysje[1]
																		// +
																		// 日终结帐表单.ysje[ll_Row]
					YSXJ = YSXJ + parseDouble(map.get("YSXJ") + ""); // 日终结帐表单.ysxj[1]
																		// =
																		// 日终结帐表单.ysxj[1]
																		// +
																		// 日终结帐表单.ysxj[ll_Row]
					YSQT = YSQT + parseDouble(map.get("YSQT") + ""); // 日终结帐表单.ysqt[1]
																		// =
																		// 日终结帐表单.ysqt[1]
																		// +
																		// 日终结帐表单.ysqt[ll_Row]
					ZPZS = ZPZS + parseInt(map.get("ZPZS") + ""); // 日终结帐表单.zpzs[1]
																	// =
																	// 日终结帐表单.zpzs[1]
																	// +
																	// 日终结帐表单.zpzs[ll_Row]
					TYJJ = TYJJ + parseDouble(map.get("TYJJ") + ""); // 日终结帐表单.tyjj[1]
																		// =
																		// 日终结帐表单.tyjj[1]
																		// +
																		// 日终结帐表单.tyjj[ll_Row]
					TJKS = TJKS + parseInt(map.get("TJKS") + ""); // 日终结帐表单.tjks[1]
																	// =
																	// 日终结帐表单.tjks[1]
																	// +
																	// 日终结帐表单.tjks[ll_Row]
																	// =
																	// 日终结帐表单.srje[1]
																	// +
																	// 日终结帐表单.srje[ll_Row]
					SYB = SYB + parseDouble(map.get("SYB") + "");
					qtysFb = qtysFb + map.get("QTYSFB") + " ";
					if (map.get("QZPJ") != null) {
						QZPJ.append(map.get("QZPJ") + " ");
					}
					if (map.get("QZSJ") != null) {
						QZSJ.append(map.get("QZSJ") + " ");
					}
				}
				//如果没有参数jzrq（表示点击打印按钮，然后进入本方法），重新整理算结算发票和缴款收据 票据序列
				if (body.get("jzrq") == null){
					Map<String,Object> para = new HashMap<String, Object>();
					para.put("al_jgid", jgid);
					para.put("as_czgh", userid);
					para.put("adt_jzrq_s", sdftime.format(adt_clrq_b));
//					para.put("adt_jzrq_e", adt_clrq_e);
					String ids_jsfp_hql = " SELECT a.FPHM as FPHM FROM JC_JCJS a,JC_BRRY b  WHERE a.ZYH=b.ZYH and  a.CZGH =:as_czgh AND a.JGID = :al_jgid AND  a.JZRQ IS not NULL AND to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') =:adt_jzrq_s and a.FPHM is not null order by a.FPHM asc";
					List<Map<String, Object>> ids_jsfp = dao.doSqlQuery(ids_jsfp_hql, para);
					String ls_pjxl_fp = "";
					if (ids_jsfp.size() > 0) {
						String ls_begin_pjhm = ids_jsfp.get(0).get("FPHM") + "";
						String ls_curr_pjhm = ids_jsfp.get(0).get("FPHM") + "";
						String ls_end_pjhm = "";
						for (int i = 0; i < ids_jsfp.size(); i++) {
							if (!(ids_jsfp.get(i).get("FPHM") + "")
									.equals(ls_curr_pjhm)) {
								ls_end_pjhm = (ids_jsfp.get(i - 1).get("FPHM") + "");
								ls_pjxl_fp = wf_addtopjxl(ls_pjxl_fp, ls_begin_pjhm,
										ls_end_pjhm);
								ls_begin_pjhm = ids_jsfp.get(i).get("FPHM") + "";
								ls_curr_pjhm = ids_jsfp.get(i).get("FPHM") + "";
							}
							ls_curr_pjhm = wf_addpjhm(ls_curr_pjhm); // 票据号码加1
						}
						ls_end_pjhm = ids_jsfp.get(ids_jsfp.size() - 1).get("FPHM")
								+ "";
						ls_pjxl_fp = wf_addtopjxl(ls_pjxl_fp, ls_begin_pjhm,
								ls_end_pjhm);
					}
					QZPJ= new StringBuffer("");
					QZPJ.append(ls_pjxl_fp);

					String ids_jksj_hql = " SELECT a.SJHM as SJHM from JC_TBKK a WHERE a.CZGH = :as_czgh AND a.JGID = :al_jgid AND a.JZRQ IS not NULL AND to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss')=:adt_jzrq_s and a.SJHM is not null order by a.SJHM asc";
					List<Map<String, Object>>ids_jksj = dao.doQuery(ids_jksj_hql, para);
					String ls_pjxl_sj = "";
					if (ids_jksj.size() > 0) {
						String ls_begin_pjhm = ids_jksj.get(0).get("SJHM") + "";
						String ls_curr_pjhm = ids_jksj.get(0).get("SJHM") + "";
						String ls_end_pjhm = "";
						for (int i = 0; i < ids_jksj.size(); i++) {
							if (!(ids_jksj.get(i).get("SJHM") + "")
									.equals(ls_curr_pjhm)) {
								ls_end_pjhm = (ids_jksj.get(i - 1).get("SJHM") + "");
								ls_pjxl_sj = wf_addtopjxl(ls_pjxl_sj, ls_begin_pjhm,
										ls_end_pjhm);
								ls_begin_pjhm = ids_jksj.get(i).get("SJHM") + "";
								ls_curr_pjhm = ids_jksj.get(i).get("SJHM") + "";
							}
							;
							ls_curr_pjhm = wf_addpjhm(ls_curr_pjhm); // 票据号码加1
						}
						ls_end_pjhm = ids_jksj.get(ids_jksj.size() - 1).get("SJHM")
								+ "";
						ls_pjxl_sj = wf_addtopjxl(ls_pjxl_sj, ls_begin_pjhm,
								ls_end_pjhm);
					}
					QZSJ= new StringBuffer("");
					QZSJ.append(ls_pjxl_sj);
				}
				jzxx.put("JZRQ", map.get("JZRQ"));
				jzxx.put("CZGH", map.get("CZGH") + "");
				jzxx.put("CYSR", CYSR + "");
				jzxx.put("YJJE", YJJE + "");
				jzxx.put("YJXJ", YJXJ + "");
				jzxx.put("FPZS", FPZS + "");
				jzxx.put("SJZS", SJZS + "");
				jzxx.put("YSJE", YSJE + "");
				jzxx.put("YSXJ", YSXJ + "");
				jzxx.put("YSQT", YSQT + "");
				jzxx.put("ZPZS", ZPZS + "");
				jzxx.put("TYJJ", TYJJ + "");
				jzxx.put("TJKS", TJKS + "");
				jzxx.put("SRJE", SRJE + "");
				jzxx.put("QZPJ", QZPJ.toString());
				jzxx.put("QZSJ", QZSJ.toString());
				jzxx.put("CZGH", userid + "");
				jzxx.put("SYB", SYB + "");
				jzxx.put("qtysFb", qtysFb + "");
			}
			// 获取作废票据数据
			Map<String, Object> iws_pjxx1 = new HashMap<String, Object>();
			Map<String, Object> iws_pjxx2 = new HashMap<String, Object>();
			long iws_pjxx1_pjzs = 0;
			long iws_pjxx2_pjzs = 0;
			hql = "SELECT a.PJLB as PJLB,a.PJHM as PJHM FROM JC_ZFPJ a WHERE  a.CZGH = :as_czgh AND a.JGID = :al_jgid AND  to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') = :adt_jzrq_s";
			if (body.get("jzrq") != null) {
				hql = "SELECT a.PJLB as PJLB,a.PJHM as PJHM FROM JC_ZFPJ a WHERE  a.CZGH = :as_czgh AND a.JGID = :al_jgid AND  to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') = :jzrq";
			}
			List<Map<String, Object>> ret_a = dao.doQuery(hql, parameters);
			for (Map<String, Object> map : ret_a) {
				if ("1".equals(map.get("PJLB") + "")) {
					iws_pjxx1_pjzs++;
					iws_pjxx1.put(iws_pjxx1_pjzs + "", map.get("PJHM") + "");
				}
				if ("2".equals(map.get("PJLB") + "")) {
					iws_pjxx2_pjzs++;
					iws_pjxx2.put(iws_pjxx2_pjzs + "", map.get("PJHM") + "");
				}
			}
			Map<String, Object> zfpj = wf_WriteBillInfo(iws_pjxx1_pjzs,
					iws_pjxx1, iws_pjxx2_pjzs, iws_pjxx2, 0, null);
			jzxx.putAll(zfpj);
			return jzxx;
		} catch (PersistentDataOperationException e) {
			logger.error("Has done Checkout query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "表单操作失败！");
		}
	}
	
	
	// 取消汇总查询
		public void doQueryCancelCommit(Map<String, Object> req,
				Map<String, Object> res, BaseDAO dao, Context ctx)
				throws ModelDataOperationException {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();// 用户的机构名称
			String userId = user.getUserId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", jgid);
			parameters.put("czgh", userId);
			try {
				List<Map<String, Object>> list = dao
						.doSqlQuery(
								"select distinct to_char(JZRQ,'yyyy-mm-dd hh24:mi:ss') as JZRQ,CZGH as CZGH from JC_JZXX where CZGH=:czgh and JGID=:jgid and HZRQ is null order by JZRQ desc",
								parameters);
				if (list.size() > 0) {
					res.put("body", list);
				}
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "查询日报信息出错!");
			}

		}

		// 取消汇总查询
		public void doCancelCommit(Map<String, Object> req,
				Map<String, Object> res, BaseDAO dao, Context ctx)
				throws ModelDataOperationException {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();// 用户的机构名称
			String userId = user.getUserId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			String jzrq = req.get("JZRQ") + "";
			SimpleDateFormat sdfdatetime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date jzdate = null;
			try {
				jzdate = sdfdatetime.parse(jzrq);
			} catch (ParseException e1) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "取消日报信息出错!");
			}
			parameters.put("jgid", jgid);
			parameters.put("czgh", userId);
			parameters.put("jzrq", jzdate);
			try {
				List<?> list = dao.doSqlQuery("SELECT JZRQ as JZRQ FROM JC_JZXX WHERE CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq and HZRQ is null", parameters);
				if (list.size() ==0){
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前记录已汇总不能取消");
				}
				Map<String, Object> jzmaxparameters = new HashMap<String, Object>();
				jzmaxparameters.put("jgid", jgid);
				jzmaxparameters.put("czgh", userId);
				List<Map<String, Object>> list1 = dao
						.doSqlQuery(
								"select  to_char(max(JZRQ),'yyyy-mm-dd hh24:mi:ss') as JZRQ from JC_JZXX where CZGH=:czgh and JGID=:jgid and HZRQ is null",
								jzmaxparameters);
				if (list1.size() >0){
					if(!jzrq.equals(list1.get(0).get("JZRQ")+"")){
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前记录不是最后一条结账信息,不能取消!");
					}
				}
				dao.doUpdate("delete from JC_ZFPJ where JGID=:jgid and CZGH=:czgh and JZRQ=:jzrq", parameters);
				dao.doUpdate("delete from JC_JZXX where JGID=:jgid and CZGH=:czgh and JZRQ=:jzrq", parameters);
				dao.doUpdate("update JC_TBKK set JZRQ = null where CZGH =:czgh  and JZRQ=:jzrq and JGID = :jgid", parameters);
				dao.doUpdate("update JC_JKZF set JZRQ = null where ZFGH = :czgh  and JZRQ=:jzrq and JGID = :jgid", parameters);
				dao.doUpdate("update JC_JCJS set JZRQ = null where CZGH = :czgh  and JZRQ=:jzrq and JGID = :jgid", parameters);
				dao.doUpdate("update JC_JSZF SET JZRQ = null where ZFGH = :czgh  and JZRQ=:jzrq and JGID = :jgid", parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "取消日报信息出错!");
			}

		}
		
		@SuppressWarnings("unchecked")
		public void doQuerySQL(Map<String, Object> req, Map<String, Object> res,
				BaseDAO dao, Context ctx) throws ModelDataOperationException {
			UserRoleToken user = UserRoleToken.getCurrent();
			String uid = user.getUserId();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("czgh", uid);
			parameters.put("jgid", jgid);
			StringBuffer hql1 = new StringBuffer();
			try {
				if (req.containsKey("cnd")) {
					List<Object> listCND = (List<Object>) req.get("cnd");
					String cnd = ExpressionProcessor.instance().toString(listCND);
					cnd = cnd.replaceAll("str", "to_char");
					hql1.append(" SELECT JZRQ as JZRQ FROM JC_JZXX ")
							.append(" WHERE CZGH =:czgh and JGID =:jgid and ")
							.append(cnd)
							.append(" order by JZRQ desc");
				}
				List<Map<String, Object>> listSQL1 = dao.doQuery(hql1.toString(),
						parameters);
				res.put("body", listSQL1);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			} catch (ExpException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * \ 查询最后一次汇总时间
		 * 
		 * @param req
		 * @param res
		 * @param dao
		 * @param ctx
		 * @throws ModelDataOperationException
		 */
		public void doGetLastHZRQ(Map<String, Object> req, Map<String, Object> res,
				BaseDAO dao, Context ctx) throws ModelDataOperationException {
			SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				if(sdftime.parse(req.get("jzrq")+"").getTime()>new java.util.Date().getTime()){
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "结账时间不能大于当前时间!");
				}
				UserRoleToken user = UserRoleToken.getCurrent();
				String uid = user.getUserId()+"";
				String jgid = user.getManageUnitId();// 用户的机构ID
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("jgid", jgid);
				parameters.put("czgh", uid);
				String sql = "select to_char(max(jzrq),'yyyy-mm-dd hh24:mi:ss') as jzrq from JC_JZXX where JGID=:jgid and CZGH = :czgh";
				List<Map<String, Object>> map = dao.doSqlQuery(sql, parameters);
				if (map != null && map.get(0) != null) {
//					String jzrq = map.get(0).get("JZRQ")+"";
					if (map.get(0).get("JZRQ") == null) {
						res.put("body", "");
					} else {
//						SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd");
						res.put("body", map.get(0).get("JZRQ")+"");
					}
				}
				FamilySickBedizationCheckoutService cck = FamilySickBedizationCheckoutService
						.getInstance();
					cck.doIsreckon(req, res,dao, ctx);
					List<Object> rebody = (List<Object>) res.get("body");
				if(!(Boolean)rebody.get(0)){
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "没有需要结账的信息!");
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
