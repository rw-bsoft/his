package phis.application.pha.source;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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

/**
 * 药房对账model
 * 
 * @author caijy
 * 
 */
public class PharmacyBalanceModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyBalanceModel.class);

	public PharmacyBalanceModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 查询药房药品明细账单
	 * @updateInfo
	 * @param body
	 * @param req
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryBalanceDetailInfo(Map<String, Object> body,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		int zxbz = MedicineUtils.parseInt(body.get("ZXBZ")); // 最小包装
		int yk_yfbz = MedicineUtils.parseInt(body.get("YK_YFBZ")); // 药库包装
		StringBuffer hql_lymx = new StringBuffer(); // 领药明细
		hql_lymx.append("select b.YPXH as YPXH, b.YPCD as YPCD, a.LYRQ as DAY, c.FSMC || '(' || a.CKDH || ')' as REMARK, ((case when b.SQSL > 0 then b.SFSL else b.SQSL end) * "
				+ zxbz
				+ "/"
				+ yk_yfbz
				+ ") as YPSL, (b.LSJG * "
				+ yk_yfbz
				+ "/"
				+ zxbz
				+ ") as LSJG, b.LSJE as LSJE from YK_CK01 a,YK_CK02 b,YK_CKFS c where a.XTSB = b.XTSB and a.CKFS = b.CKFS and a.CKFS = c.CKFS and a.CKDH = b.CKDH and a.LYPB = 1 and a.YFSB = :yfsb and a.LYRQ >:begin and a.LYRQ <=:end and b.YPXH = :ypxh and b.YPCD = :ypcd");
		StringBuffer hql_rkmx = new StringBuffer(); // 入库明细
		hql_rkmx.append("select b.YPXH as YPXH, b.YPCD as YPCD, a.RKRQ as DAY, c.FSMC || '(' ||a.RKDH ||')' as REMARK, (b.RKSL * b.YFBZ / "
				+ yk_yfbz
				+ ") as YPSL, (b.LSJG * "
				+ yk_yfbz
				+ " / b.YFBZ) as LSJG, b.LSJE as LSJE from YF_RK01 a,YF_RK02 b,YF_RKFS c where a.YFSB = c.YFSB and a.RKFS = c.RKFS and a.YFSB = b.YFSB and a.RKFS = b.RKFS and a.RKDH = b.RKDH and a.RKPB = 1 and a.YFSB = :yfsb and a.RKRQ > :begin and a.RKRQ <=:end and b.YPXH = :ypxh and b.YPCD = :ypcd");
		StringBuffer hql_ckmx = new StringBuffer(); // 出库明细
		hql_ckmx.append("select  b.YPXH as YPXH, b.YPCD as YPCD, a.LYRQ as DAY, c.FSMC || '(' || a.CKDH || ')' as REMARK, (b.CKSL * b.YFBZ / "
				+ yk_yfbz
				+ " ) as YPSL, (b.LSJG * "
				+ yk_yfbz
				+ " / b.YFBZ) as LSJG, b.LSJE as LSJE from YF_CK01 a,YF_CK02 b,YF_CKFS c  where a.YFSB = b.YFSB and a.YFSB = c.YFSB and a.CKDH = b.CKDH and a.CKFS = b.CKFS and a.CKFS = c.CKFS and a.CKPB = 1 and a.YFSB = :yfsb and a.LYRQ >:begin and a.LYRQ <=:end and b.YPXH = :ypxh and b.YPCD = :ypcd");
		StringBuffer hql_mz_fymx = new StringBuffer(); // 门诊发药明细
		hql_mz_fymx
				.append("select a.YPXH as YPXH, a.YPCD as YPCD, a.FYRQ as DAY, (case when a.YPSL > 0 then '门诊发药' else '门诊取消发药' end) as REMARK, (a.YPSL * a.YFBZ / "
						+ yk_yfbz
						+ " ) as YPSL, (a.LSJG * "
						+ yk_yfbz
						+ " / a.YFBZ) as LSJG, a.LSJE as LSJE from YF_MZFYMX a where a.YFSB = :yfsb and a.FYRQ > :begin and a.FYRQ <=:end and a.YPXH = :ypxh and a.YPCD = :ypcd");
		StringBuffer hql_zy_fymx = new StringBuffer(); // 住院发药明细
		hql_zy_fymx
				.append("select a.YPXH as YPXH, a.YPCD as YPCD, b.FYSJ as DAY, c.FSMC as REMARK, sum(a.YPSL * a.YFBZ / "
						+ yk_yfbz
						+ " ) as YPSL, (a.LSJG * "
						+ yk_yfbz
						+ " / a.YFBZ) as LSJG, sum(a.LSJE) as LSJE from YF_ZYFYMX a,YF_FYJL b,ZY_FYFS c where a.YPSL>0 and a.JLID = b.JLID and a.YFSB = b.YFSB and a.YFSB = :yfsb and b.FYFS = c.FYFS and b.FYSJ > :begin and b.FYSJ <=:end and a.YPXH = :ypxh and a.YPCD = :ypcd group by a.YFBZ, a.YPXH, a.YPCD, a.LSJG, a.LSJG, b.FYSJ, c.FSMC");
		StringBuffer hql_zy_tymx = new StringBuffer(); // 住院退药明细
		hql_zy_tymx
				.append("select a.YPXH as YPXH, a.YPCD as YPCD, a.FYRQ as DAY, '住院病区退药' as REMARK, sum(a.YPSL * a.YFBZ / "
						+ yk_yfbz
						+ " ) as YPSL, (a.LSJG * "
						+ yk_yfbz
						+ " / a.YFBZ) as LSJG, sum(a.LSJE) as LSJE  from YF_ZYFYMX  a left join BQ_TYMX b on a.TYXH = b.JLXH and a.YFSB = b.YFSB   where a.YPSL<0 and a.YFSB = :yfsb and a.FYRQ > :begin and a.FYRQ <=:end and a.YPXH = :ypxh and a.YPCD = :ypcd group by a.YPXH, a.YPCD, a.LSJG, a.YFBZ, a.LSJG, b.TYRQ ,a.FYRQ ");
		StringBuffer hql_jc_fymx = new StringBuffer(); // 家床发药明细
		hql_jc_fymx
				.append("select a.YPXH as YPXH, a.YPCD as YPCD, b.FYSJ as DAY, '家床发药' as REMARK, sum(a.YPSL * a.YFBZ / "
						+ yk_yfbz
						+ " ) as YPSL, (a.LSJG * "
						+ yk_yfbz
						+ " / a.YFBZ) as LSJG, sum(a.LSJE) as LSJE from YF_JCFYMX a,JC_FYJL b where a.YPSL>0 and a.JLID = b.JLID and a.YFSB = b.YFSB and a.YFSB = :yfsb  and b.FYSJ > :begin and b.FYSJ <=:end and a.YPXH = :ypxh and a.YPCD = :ypcd group by a.YFBZ, a.YPXH, a.YPCD, a.LSJG, a.LSJG, b.FYSJ");
		StringBuffer hql_jc_tymx = new StringBuffer(); // 住院退药明细
		hql_jc_tymx
				.append("select a.YPXH as YPXH, a.YPCD as YPCD, a.FYRQ as DAY, '家床退药' as REMARK, sum(a.YPSL * a.YFBZ / "
						+ yk_yfbz
						+ " ) as YPSL, (a.LSJG * "
						+ yk_yfbz
						+ " / a.YFBZ) as LSJG, sum(a.LSJE) as LSJE  from YF_JCFYMX  a left join JC_TYMX b on a.TYXH = b.JLXH and a.YFSB = b.YFSB   where a.YPSL<0 and a.YFSB = :yfsb and a.FYRQ > :begin and a.FYRQ <=:end and a.YPXH = :ypxh and a.YPCD = :ypcd group by a.YPXH, a.YPCD, a.LSJG, a.YFBZ, a.LSJG, b.TYRQ ,a.FYRQ ");
		StringBuffer hql_tjmx = new StringBuffer(); // 调价明细
		hql_tjmx.append("select a.YPXH as YPXH, a.YPCD as YPCD, a.TJRQ as DAY, (case when a.TJWH is null or a.TJWH = '' then '调价' else '调价('|| a.TJWH ||')'end) as REMARK,  (a.TJSL * a.YFBZ / "
				+ yk_yfbz
				+ " ) as YPSL, ((a.XLSJ) * "
				+ yk_yfbz
				+ " / a.YFBZ) as LSJG, a.XLSE-a.YLSE as LSJE from YF_TJJL a where a.YFSB = :yfsb and a.TJRQ > :begin and a.TJRQ <=:end and a.YPXH = :ypxh and a.YPCD = :ypcd");
		StringBuffer hql_dbckmx = new StringBuffer(); // 调拨出库明细
		hql_dbckmx
				.append("select  b.YPXH as YPXH, b.YPCD as YPCD, a.CKRQ as DAY, (case when b.QRSL > 0 then '调拨到' || (select YFMC from YF_YFLB where YFSB = b.SQYF)|| '(' || b.SQDH || ')' else '由' || (select YFMC from YF_YFLB where YFSB = b.SQYF) || '调拨(' || b.SQDH || ')' end ) as REMARK,(b.QRSL * b.QRBZ / "
						+ yk_yfbz
						+ " ) as YPSL, (b.LSJG * "
						+ yk_yfbz
						+ "/ b.YFBZ) as LSJG, b.LSJE as LSJE from YF_DB01 a,YF_DB02 b where a.SQYF = b.SQYF and a.SQDH = b.SQDH and a.CKBZ = 1 and a.MBYF = :yfsb and a.CKRQ > :begin and a.CKRQ <= :end and b.YPXH = :ypxh and b.YPCD = :ypcd");
		StringBuffer hql_dbrkmx = new StringBuffer(); // 调拨入库明细
		hql_dbrkmx
				.append("select b.YPXH as YPXH, b.YPCD as YPCD, a.CKRQ as DAY, (case when b.QRSL > 0 then '由' || (select YFMC from YF_YFLB where YFSB = a.MBYF)|| '调拨(' || b.SQDH || ')' else '调拨到' || (select YFMC from YF_YFLB where YFSB = a.MBYF) || '(' || b.SQDH || ')' end ) as REMARK,(b.QRSL * b.QRBZ / "
						+ yk_yfbz
						+ " ) as YPSL, (b.LSJG * "
						+ yk_yfbz
						+ " / b.YFBZ) as LSJG, b.LSJE as LSJE from YF_DB01 a,YF_DB02 b where a.SQYF = b.SQYF and a.SQDH = b.SQDH and a.RKBZ = 1 and a.SQYF = :yfsb and a.RKRQ > :begin and a.RKRQ <= :end and b.YPXH = :ypxh and b.YPCD = :ypcd");
		StringBuffer hql_pdmx = new StringBuffer(); // 盘点明细
		hql_pdmx.append("select b.YPXH as YPXH, b.YPCD as YPCD, a.PDRQ as DAY, '盘存(' || a.PDDH || ')' as REMARK, ((b.SPSL - b.PQSL) * b.YFBZ / "
				+ yk_yfbz
				+ ") as YPSL, (b.LSJG * "
				+ yk_yfbz
				+ " / b.YFBZ) as LSJG, b.XLSE - b.YLSE as LSJE from YF_YK01 a,YF_YK02 b where a.YFSB = b.YFSB and a.PDDH = b.PDDH and a.PDWC = 1 and a.YFSB = :yfsb and a.PDRQ > :begin and a.PDRQ <= :end and b.YPXH = :ypxh and b.YPCD = :ypcd");
		StringBuffer hql_union_temp = new StringBuffer(); // 出入库对账明细合并
		hql_union_temp
				.append("select YPXH as YPXH, YPCD as YPCD, to_char(DAY,'yyyy-MM-dd hh24:mi:ss') DAY, REMARK as REMARK, ")
				.append("YPSL as YPSL, LSJG as LSJG, LSJE as LSJE FROM (")
				.append(hql_rkmx).append(" union ").append(hql_lymx)
				.append(" union ").append(hql_ckmx).append(" union ")
				.append(hql_mz_fymx).append(" union ").append(hql_zy_fymx)
				.append(" union ").append(hql_zy_tymx).append(" union ").append(hql_jc_fymx)
				.append(" union ").append(hql_jc_tymx).append(" union ")
				.append(hql_tjmx).append(" union ").append(hql_dbckmx)
				.append(" union ").append(hql_dbrkmx).append(" union ")
				.append(hql_pdmx).append(")")
				// .append(" where LSJG =:lsjg")
				.append(" order by DAY");
		Object dateBegin = body.get("date_begin");
		Object dateEnd = body.get("date_end");
		Date date_begin = null;
		Date date_end = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (dateBegin != null) {
				date_begin = sdf.parse((String) dateBegin);
			}
			if (dateEnd != null) {
				date_end = sdf.parse((String) dateEnd);
			}
		} catch (ParseException e1) {
			MedicineUtils.throwsException(logger, "日期转换失败", e1);
		}
		// int yfbz = parseInt(body.get("YFBZ")); // 药房包装
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("begin", date_begin);
		parameters.put("end", date_end);
		parameters.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		parameters.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		parameters.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
		// parameters.put("lsjg",
		// this.getLsjgByCal(parseDouble(body.get("LSJG")),yk_yfbz, yfbz));
		MedicineCommonModel model = new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, parameters,
				hql_union_temp.toString(), null);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 查询对账明细中药品信息
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryBalanceDetailInit(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		long ypxh = MedicineUtils.parseLong(body.get("YPXH"));
		long yfsb = MedicineUtils.parseLong(body.get("YFSB"));
		String hql_bzlb = "select BZLB as BZLB  from YF_YFLB where YFSB = :YFSB";// 查询包装类别
		Map<String, Object> map_par_bzlb = new HashMap<String, Object>();
		map_par_bzlb.put("YFSB", yfsb);
		StringBuffer hql_typk = new StringBuffer();// 查询药品信息
		Map<String, Object> ret = null;
		try {
			Map<String, Object> map_bzlb = dao.doLoad(hql_bzlb, map_par_bzlb);
			int bzlb = MedicineUtils.parseInt(map_bzlb.get("BZLB"));
			if (bzlb == 1) {// 门诊包装
				hql_typk.append("select YPMC as YPMC, YFGG as YFGG, YFDW as YFDW, YFBZ as YK_YFBZ, ZXBZ as ZXBZ from YK_TYPK where YPXH =:ypxh");
			} else {// 住院包装
				hql_typk.append("select YPMC as YPMC, YFGG as YFGG, YFDW as YFDW, BFBZ as YK_YFBZ, ZXBZ as ZXBZ from YK_TYPK where YPXH =:ypxh");
			}
			Map<String, Object> map_par_ypxx = new HashMap<String, Object>();
			map_par_ypxx.put("ypxh", ypxh);
			ret = dao.doLoad(hql_typk.toString(), map_par_ypxx);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取药品信息出错!", e);
		}
		return ret;
	}

	public Map<String, Object> queryBalanceSummaryInfo(
			Map<String, Object> body, Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date_begin = null;
		Date date_end = null;
		Date cwyf = null;
		Object dateBegin = body.get("date_begin");
		Object dateEnd = body.get("date_end");
		Object type = body.get("type");
		Object pydm = body.get("pydm");
		try {
			if (dateBegin != null) {
				date_begin = sdf.parse((String) dateBegin);
			}
			if (dateEnd != null) {
				date_end = sdf.parse((String) dateEnd);
			}
			if (body.get("cwyf") != null) {
				cwyf = sdf.parse((String) body.get("cwyf"));
			}
		} catch (ParseException e1) {
			MedicineUtils.throwsException(logger, "日期转换失败", e1);
		}
		StringBuffer hql_yfyp = new StringBuffer(); // 药房药品信息
		hql_yfyp.append("select  b.YPXH as YPXH, b.YPCD as YPCD, c.YPMC as YPMC, a.YFGG as YFGG, a.YFDW as YFDW, c.ZXBZ as ZXBZ, c.TYPE as TYPE, a.YFBZ as YFBZ, c.PYDM as PYDM, c.WBDM as WBDM, c.JXDM as JXDM, d.CDMC as CDMC, 0.00 as QCSL, 0.0000 as QCJE, 0.00 as RKSL, 0.0000 as RKJE, 0.00 as CKSL, 0.0000 as CKJE, sum(e.YPSL) as YPSL, sum(e.LSJE) as LSJE, e.YFSB as YFSB, e.LSJG as LSJG, b.LSJG * a.YFBZ/c.ZXBZ as CDLSJG,'' as cwxx");
		StringBuffer conditionHql = new StringBuffer(); // 药房信息条件
		conditionHql
				.append(" from YF_YPXX a,YK_YPCD b,YK_TYPK c,YK_CDDZ d,YF_KCMX e  where b.YPCD = d.YPCD and a.YPXH = c.YPXH and b.YPXH = c.YPXH and a.YFSB =:YFSB and a.YFSB = e.YFSB and c.YPXH = e.YPXH and b.YPCD = e.YPCD");
		StringBuffer hql_yfyp_count = new StringBuffer(); // 药房信息数量
		StringBuffer hql_syjc = new StringBuffer(); // 统计上月结存
		hql_syjc.append("select a.YPXH as YPXH, a.YPCD as YPCD, a.KCSL as KCSL, a.LSJG as LSJG, a.YFBZ as YFBZ, a.JHJE as JHJE, a.LSJE as LSJE, a.PFJE as PFJE from YF_YJJG a where a.CWYF=:cwyf and a.YFSB=:yfsb and a.YPCD=:ypcd and a.YPXH=:ypxh");
		StringBuffer hql_lysl = new StringBuffer(); // 统计领药
		hql_lysl.append("select sum((case when b.SQSL > 0 then b.SFSL else b.SQSL end)) as SFSL, sum(b.LSJE) as LSJE, sum(b.PFJE) as PFJE, sum(b.JHJE) as JHJE, b.LSJG as LSJG from YK_CK01 a,YK_CK02 b where a.XTSB = b.XTSB and a.CKFS = b.CKFS and a.CKDH = b.CKDH and a.LYPB = 1 and a.LYRQ > :begin and a.LYRQ <= :end and a.YFSB=:yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd group by b.YPXH, b.YPCD, b.LSJG");
		StringBuffer hql_rksl = new StringBuffer(); // 统计入库数量金额
		hql_rksl.append("select sum(b.RKSL) as RKSL, sum(b.LSJE) as LSJE, sum(b.PFJE) as PFJE, sum(b.JHJE) as JHJE, b.LSJG as LSJG, b.YFBZ as YFBZ from YF_RK01 a,YF_RK02 b  where a.YFSB=b.YFSB and a.RKFS=b.RKFS and a.RKDH = b.RKDH and a.RKPB=1 and a.RKRQ >:begin and a.RKRQ <=:end and a.YFSB=:yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd group by b.YPXH, b.YPCD, b.LSJG,b.YFBZ");
		StringBuffer hql_cksl = new StringBuffer(); // 统计出库数量金额
		hql_cksl.append("select sum(b.CKSL) as CKSL,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE,sum(b.JHJE) as JHJE,b.LSJG as LSJG,b.YFBZ as YFBZ from YF_CK01 a,YF_CK02 b  where a.YFSB =b.YFSB and a.CKFS =b.CKFS and a.CKDH =b.CKDH and a.CKPB =1 and a.CKRQ >:begin and a.CKRQ <=:end and a.YFSB=:yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd group by b.YPXH, b.YPCD, b.LSJG,b.YFBZ");
		StringBuffer hql_mz_fysl = new StringBuffer(); // 统计门诊发药数量金额
		hql_mz_fysl
				.append("select sum(YPSL) as YPSL,YFBZ as YFBZ,LSJG as LSJG,PFJG as PFJG,JHJG as JHJG,sum(LSJE) as LSJE ,sum(PFJE) as PFJE,sum(JHJE) as JHJE from YF_MZFYMX where YFSB=:yfsb and FYRQ >:begin and FYRQ <=:end and YPXH=:ypxh and YPCD=:ypcd group by YPXH,YPCD,LSJG,PFJG,YFBZ,JHJG");
		StringBuffer hql_zy_fysl = new StringBuffer(); // 统计住院发药数量金额
		hql_zy_fysl
				.append("select sum(YPSL) as YPSL,YFBZ as YFBZ,LSJG as LSJG,PFJG as PFJG,JHJG as JHJG,sum(LSJE) as LSJE ,sum(PFJE) as PFJE,sum(JHJE) as JHJE from YF_ZYFYMX where YFSB=:yfsb and FYRQ >:begin and FYRQ <=:end and YPXH=:ypxh and YPCD=:ypcd group by YPXH,YPCD,LSJG,PFJG,YFBZ,JHJG");
		//update by caijy at 2015-3-11 for 增加家床发药部分
		StringBuffer hql_jc_fysl = new StringBuffer(); // 统计家床发药数量金额
		hql_jc_fysl
				.append("select sum(YPSL) as YPSL,YFBZ as YFBZ,LSJG as LSJG,PFJG as PFJG,JHJG as JHJG,sum(LSJE) as LSJE ,sum(PFJE) as PFJE,sum(JHJE) as JHJE from YF_JCFYMX where YFSB=:yfsb and FYRQ >:begin and FYRQ <=:end and YPXH=:ypxh and YPCD=:ypcd group by YPXH,YPCD,LSJG,PFJG,YFBZ,JHJG");
		StringBuffer hql_tjje = new StringBuffer(); // 统计调价金额
		hql_tjje.append("select XLSE-YLSE as LSJE, XPFE-YPFE as PFJE, XJHE-YJHE as JHJE, XLSJ as XLSJ, YFBZ as YFBZ from YF_TJJL where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJRQ>:begin and TJRQ<=:end");
		StringBuffer hql_dbrksl = new StringBuffer(); // 统计调拨入库数量金额
		hql_dbrksl
				.append("select sum(b.QRSL) as QRSL, b.QRBZ as QRBZ, b.LSJG as LSJG,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE, sum(b.JHJE) as JHJE from YF_DB01 a,YF_DB02 b  where a.SQYF = b.SQYF and a.SQDH = b.SQDH and a.RKBZ = 1 and a.SQYF =:yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd and a.RKRQ >:begin and a.RKRQ <=:end group by b.YPXH, b.YPCD, b.LSJG, b.QRBZ");
		StringBuffer hql_dbcksl = new StringBuffer(); // 统计调拨入库数量金额
		hql_dbcksl
				.append("select sum(b.QRSL) as QRSL, b.QRBZ as QRBZ, b.LSJG as LSJG,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE, sum(b.JHJE) as JHJE from YF_DB01 a,YF_DB02 b  where a.SQYF = b.SQYF and a.SQDH = b.SQDH and a.CKBZ = 1 and a.MBYF =:yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd and a.CKRQ >:begin and a.CKRQ <=:end group by b.YPXH, b.YPCD, b.LSJG, b.QRBZ");
		StringBuffer hql_pdsl = new StringBuffer(); // 统计盘点数量金额
		hql_pdsl.append("select b.SPSL - b.PQSL as PCSL, b.YFBZ as YFBZ, b.LSJG as LSJG, b.XLSE - b.YLSE as LSJE, b.XPFE - b.YPFE as PFJE, b.XJHE - b.YJHE as JHJE from YF_YK01 a,YF_YK02 b  where a.YFSB = b.YFSB and a.PDDH = b.PDDH and a.PDWC = 1 and a.YFSB = :yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd and a.WCRQ >:begin and a.WCRQ <=:end");
		DecimalFormat df_2 = new DecimalFormat("###.00");
		DecimalFormat df_4 = new DecimalFormat("###.0000");
		List<Map<String, Object>> list_yp;
		List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("YFSB", yfsb);
		if (type != null && StringUtils.isNotBlank((String) type)) {
			int tp = MedicineUtils.parseInt(type);
			if (tp > 0) {
				parameters.put("type", tp);
				conditionHql.append(" and c.TYPE =:type");
			}
		}
		if (pydm != null && StringUtils.isNotBlank((String) pydm)) {
			conditionHql.append(" and upper(c.PYDM) like '")
					.append(((String) pydm).toUpperCase()).append("'");
		}
		conditionHql
				.append(" group by b.YPXH,b.YPCD,c.YPMC,a.YFGG,a.YFDW,c.ZXBZ,c.TYPE,a.YFBZ,c.PYDM,c.WBDM,c.JXDM,d.CDMC,b.LSJG,e.YFSB,e.LSJG");
		hql_yfyp.append(conditionHql).append(" order by c.PYDM");
		hql_yfyp_count.append("select count(*) as NUM").append(" from (")
				.append(hql_yfyp).append(")");
		try {
			List<Map<String, Object>> count = dao.doSqlQuery(
					hql_yfyp_count.toString(), parameters);
			int total = Integer.parseInt(count.get(0).get("NUM") + "");
			MedicineUtils.getPageInfo(req, parameters);
			list_yp = dao.doQuery(hql_yfyp.toString(), parameters);
			// 遍历药品
			for (Map<String, Object> map_yp : list_yp) {
				int yfbz = MedicineUtils.parseInt(map_yp.get("YFBZ")); // 药品药房包装
				int zxbz = MedicineUtils.parseInt(map_yp.get("ZXBZ")); // 药品最小包装
				double kc_lsjg = MedicineUtils.parseDouble(map_yp.get("LSJG")); // 药品最小包装
				double qcsl = MedicineUtils.parseDouble(map_yp.get("QCSL")); // 期初数量
				double qcje = MedicineUtils.parseDouble(map_yp.get("QCJE")); // 期初金额
				double rksl = MedicineUtils.parseDouble(map_yp.get("RKSL")); // 入库数量
				double rkje = MedicineUtils.parseDouble(map_yp.get("RKJE")); // 入库金额
				double cksl = MedicineUtils.parseDouble(map_yp.get("CKSL")); // 出库数量
				double ckje = MedicineUtils.parseDouble(map_yp.get("CKJE")); // 出库金额

				Map<String, Object> map_par_syjc = new HashMap<String, Object>();
				map_par_syjc.put("ypcd",
						MedicineUtils.parseLong(map_yp.get("YPCD")));
				map_par_syjc.put("ypxh",
						MedicineUtils.parseLong(map_yp.get("YPXH")));
				map_par_syjc.put("cwyf", cwyf);
				map_par_syjc.put("yfsb", yfsb);

				Map<String, Object> map_common_par = new HashMap<String, Object>();
				map_common_par.put("begin", date_begin);
				map_common_par.put("end", date_end);
				map_common_par.put("yfsb", yfsb);
				map_common_par.put("ypxh",
						MedicineUtils.parseLong(map_yp.get("YPXH")));
				map_common_par.put("ypcd",
						MedicineUtils.parseLong(map_yp.get("YPCD")));

				Map<String, Object> map_syjc = dao.doLoad(hql_syjc.toString(),
						map_par_syjc); // 上月结存数据
				if (map_syjc != null && map_syjc.size() != 0) {
					double _lsjg = MedicineUtils.parseDouble(map_syjc
							.get("LSJG"));
					int _yfbz = MedicineUtils.parseInt(map_syjc.get("YFBZ"));
					double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, _yfbz);
					if (kc_lsjg == cal_lsjg) {
						qcsl = MedicineUtils.parseDouble(df_2
								.format(MedicineUtils.parseDouble(map_syjc
										.get("KCSL")) * _yfbz / yfbz));
						qcje = MedicineUtils.parseDouble(df_4.format(map_syjc
								.get("LSJE")));
					}
				}
				// 药房领药hql_lysl
				List<Map<String, Object>> list_lysl = dao.doQuery(
						hql_lysl.toString(), map_common_par);// 领药数量数据
				if (list_lysl != null && list_lysl.size() > 0) {
					for (Map<String, Object> map_lysl : list_lysl) {
						double _lsjg = MedicineUtils.parseDouble(map_lysl
								.get("LSJG"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, zxbz);
						if (kc_lsjg == cal_lsjg) {
							rksl = rksl
									+ MedicineUtils.parseDouble(df_2
											.format(MedicineUtils
													.parseDouble(map_lysl
															.get("SFSL"))
													* zxbz / yfbz));
							rkje = rkje
									+ MedicineUtils.parseDouble(map_lysl
											.get("LSJE"));
						}
					}
				}
				// 入库：数量、金额影响
				List<Map<String, Object>> list_rksl = dao.doQuery(
						hql_rksl.toString(), map_common_par);
				if (list_rksl != null && list_rksl.size() > 0) {
					for (Map<String, Object> map_rksl : list_rksl) {
						double _lsjg = MedicineUtils.parseDouble(map_rksl
								.get("LSJG"));
						int _yfbz = MedicineUtils
								.parseInt(map_rksl.get("YFBZ"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, _yfbz);
						if (kc_lsjg == cal_lsjg) {
							rksl = rksl
									+ MedicineUtils.parseDouble(df_2
											.format(MedicineUtils
													.parseDouble(map_rksl
															.get("RKSL"))
													* _yfbz / yfbz));
							rkje = rkje
									+ MedicineUtils.parseDouble(map_rksl
											.get("LSJE"));
						}
					}
				}
				// 出库：数量、金额影响
				List<Map<String, Object>> list_cksl = dao.doQuery(
						hql_cksl.toString(), map_common_par);
				if (list_cksl != null && list_cksl.size() > 0) {
					for (Map<String, Object> map_cksl : list_cksl) {
						double _lsjg = MedicineUtils.parseDouble(map_cksl
								.get("LSJG"));
						int _yfbz = MedicineUtils
								.parseInt(map_cksl.get("YFBZ"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, _yfbz);
						if (kc_lsjg == cal_lsjg) {
							cksl = cksl
									+ MedicineUtils.parseDouble(df_2
											.format(MedicineUtils
													.parseDouble(map_cksl
															.get("CKSL"))
													* _yfbz / yfbz));
							ckje = ckje
									+ MedicineUtils.parseDouble(map_cksl
											.get("LSJE"));
						}
					}
				}
				// 门诊发药退药：数量、金额影响
				List<Map<String, Object>> list_mz_fysl = dao.doQuery(
						hql_mz_fysl.toString(), map_common_par);
				if (list_mz_fysl != null && list_mz_fysl.size() > 0) {
					for (Map<String, Object> map_mz_fysl : list_mz_fysl) {
						double _lsjg = MedicineUtils.parseDouble(map_mz_fysl
								.get("LSJG"));
						int _yfbz = MedicineUtils.parseInt(map_mz_fysl
								.get("YFBZ"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, _yfbz);
						if (kc_lsjg == cal_lsjg) {
							cksl = cksl
									+ MedicineUtils.parseDouble(df_2
											.format(MedicineUtils
													.parseDouble(map_mz_fysl
															.get("YPSL"))
													* _yfbz / yfbz));
							ckje = ckje
									+ MedicineUtils.parseDouble(map_mz_fysl
											.get("LSJE"));
						}
					}
				}
				// 住院发药退药：数量、金额影响
				List<Map<String, Object>> list_zy_fysl = dao.doQuery(
						hql_zy_fysl.toString(), map_common_par);
				if (list_zy_fysl != null && list_zy_fysl.size() > 0) {
					for (Map<String, Object> map_zy_fysl : list_zy_fysl) {
						double _lsjg = MedicineUtils.parseDouble(map_zy_fysl
								.get("LSJG"));
						int _yfbz = MedicineUtils.parseInt(map_zy_fysl
								.get("YFBZ"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, _yfbz);
						if (kc_lsjg == cal_lsjg) {
							cksl = cksl
									+ MedicineUtils.parseDouble(df_2
											.format(MedicineUtils
													.parseDouble(map_zy_fysl
															.get("YPSL"))
													* _yfbz / yfbz));
							ckje = ckje
									+ MedicineUtils.parseDouble(map_zy_fysl
											.get("LSJE"));
						}
					}
				}
				//家床发药退药：数量、金额影响
				List<Map<String, Object>> list_jc_fysl = dao.doQuery(
						hql_jc_fysl.toString(), map_common_par);
				if (list_jc_fysl != null && list_jc_fysl.size() > 0) {
					for (Map<String, Object> map_jc_fysl : list_jc_fysl) {
						double _lsjg = MedicineUtils.parseDouble(map_jc_fysl
								.get("LSJG"));
						int _yfbz = MedicineUtils.parseInt(map_jc_fysl
								.get("YFBZ"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, _yfbz);
						if (kc_lsjg == cal_lsjg) {
							cksl = cksl
									+ MedicineUtils.parseDouble(df_2
											.format(MedicineUtils
													.parseDouble(map_jc_fysl
															.get("YPSL"))
													* _yfbz / yfbz));
							ckje = ckje
									+ MedicineUtils.parseDouble(map_jc_fysl
											.get("LSJE"));
						}
					}
				}
				// 调价：金额影响
				List<Map<String, Object>> list_tjje = dao.doQuery(
						hql_tjje.toString(), map_common_par);
				if (list_tjje != null && list_tjje.size() > 0) {
					for (Map<String, Object> map_tjje : list_tjje) {
						double _lsjg = MedicineUtils.parseDouble(map_tjje
								.get("XLSJ"));
						int _yfbz = MedicineUtils
								.parseInt(map_tjje.get("YFBZ"));
						double tj_lsje = MedicineUtils.parseDouble(map_tjje
								.get("LSJE"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, _yfbz);
						if (kc_lsjg == cal_lsjg) {
							if (tj_lsje > 0) {// 调升
								rkje = rkje + tj_lsje * cksl;// rkje = rkje +
																// tj_lsje;
							} else if (tj_lsje < 0) {
								ckje = ckje + tj_lsje * cksl;// ckje = ckje +
																// tj_lsje;
							}
						}
					}
				}
				// 调拨入库：数量、金额影响
				List<Map<String, Object>> list_dbrksl = dao.doQuery(
						hql_dbrksl.toString(), map_common_par);
				if (list_dbrksl != null && list_dbrksl.size() > 0) {
					for (Map<String, Object> map_dbsl : list_dbrksl) {
						double qrsl = MedicineUtils.parseDouble(map_dbsl
								.get("QRSL"));
						double _lsjg = MedicineUtils.parseDouble(map_dbsl
								.get("LSJG"));
						int qrbz = MedicineUtils.parseInt(map_dbsl.get("QRBZ"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, qrbz);
						if (kc_lsjg == cal_lsjg) {
							rksl = rksl
									+ MedicineUtils.parseDouble(df_2
											.format(qrsl * qrbz / yfbz));
							rkje = rkje
									+ MedicineUtils.parseDouble(map_dbsl
											.get("LSJE"));
						}
					}
				}
				// 调拨出库：数量、金额影响
				List<Map<String, Object>> list_dbcksl = dao.doQuery(
						hql_dbcksl.toString(), map_common_par);
				if (list_dbcksl != null && list_dbcksl.size() > 0) {
					for (Map<String, Object> map_dbsl : list_dbcksl) {
						double qrsl = MedicineUtils.parseDouble(map_dbsl
								.get("QRSL"));
						double _lsjg = MedicineUtils.parseDouble(map_dbsl
								.get("LSJG"));
						int qrbz = MedicineUtils.parseInt(map_dbsl.get("QRBZ"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, qrbz);
						if (kc_lsjg == cal_lsjg) {
							cksl = cksl
									+ MedicineUtils
											.parseDouble(df_2.format(qrsl
													* MedicineUtils
															.parseDouble(map_dbsl
																	.get("QRBZ"))
													/ yfbz));
							ckje = ckje
									+ MedicineUtils.parseDouble(map_dbsl
											.get("LSJE"));
						}
					}
				}
				// 盘点：数量金额影响
				List<Map<String, Object>> list_pdsl = dao.doQuery(
						hql_pdsl.toString(), map_common_par);
				if (list_pdsl != null && list_pdsl.size() > 0) {
					for (Map<String, Object> map_pdsl : list_pdsl) {
						double pcsl = MedicineUtils.parseDouble(map_pdsl
								.get("PCSL"));
						double _lsjg = MedicineUtils.parseDouble(map_pdsl
								.get("LSJG"));
						int _yfbz = MedicineUtils
								.parseInt(map_pdsl.get("YFBZ"));
						double cal_lsjg = this.getLsjgByCal(_lsjg, yfbz, _yfbz);
						if (kc_lsjg == cal_lsjg) {
							if (pcsl > 0) {// 盘盈
								rksl = rksl
										+ MedicineUtils
												.parseDouble(df_2.format(pcsl
														* MedicineUtils
																.parseDouble(map_pdsl
																		.get("YFBZ"))
														/ yfbz));
								rkje = rkje
										+ MedicineUtils.parseDouble(map_pdsl
												.get("LSJE"));
							} else if (pcsl < 0) {// 盘亏
								cksl = cksl
										- MedicineUtils
												.parseDouble(df_2.format(pcsl
														* MedicineUtils
																.parseDouble(map_pdsl
																		.get("YFBZ"))
														/ yfbz));
								ckje = ckje
										- MedicineUtils.parseDouble(map_pdsl
												.get("LSJE"));
							}
						}
					}
				}

				map_yp.put("QCSL", qcsl);
				map_yp.put("QCJE", qcje);
				map_yp.put("RKSL", rksl);
				map_yp.put("RKJE", rkje);
				map_yp.put("CKSL", cksl);
				map_yp.put("CKJE", ckje);

				map_yp.put("JGID", jgid);
				map_yp.put("YFSB", yfsb);
				map_yp.put("CKBH", 0);
				map_yp.put("QMSL", df_2.format(qcsl + rksl - cksl));// 期末库存数量
				map_yp.put("QMJE", df_4.format(qcje + rkje - ckje));// 期末库存价格
				list_ret.add(map_yp);
			}
			ret.put("body", list_ret);
			ret.put("totalCount", total);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取药房药品信息失败!", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 零售金额换算公式
	 * @updateInfo
	 * @param d
	 * @param i1
	 * @param i2
	 * @return
	 */
	private double getLsjgByCal(double d, int i1, int i2) {
		BigDecimal b1 = new BigDecimal(String.valueOf(d));
		BigDecimal b2 = new BigDecimal(String.valueOf(i1));
		BigDecimal b3 = new BigDecimal(String.valueOf(i2));
		return b1.multiply(b2).divide(b3,10,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 药房对账进入时初始化操作，含：1.检测药房初始建账2.结账时间判断3.返回起止时间和首次财务月份时间
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<String> queryBalanceInitialization(Context ctx)
			throws ModelDataOperationException {
		List<String> dates = new ArrayList<String>();
		StringBuffer hql_count = new StringBuffer();
		StringBuffer hql_fcwyf = new StringBuffer();
		SimpleDateFormat ldt_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		hql_count.append(" YFSB=:yfsb");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put(
				"yfsb",
				MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty(
						"pharmacyId")));// 用户的药房识别
		// Map<String, Object> map_ret = new HashMap<String, Object>();
		// map_ret.put("code", ServiceCode.CODE_OK);
		// map_ret.put("msg", "OK");
		Date first_cwyf = null;// BSPHISTableNames
		try {
			long l = dao.doCount("YF_JZJL", hql_count.toString(), map_par);
			if (l == 0) {
				dates.add(ServiceCode.CODE_RECORD_NOT_FOUND + "");
				dates.add("该药房还未初始建账");
				return dates;
			}
			hql_fcwyf
					.append("select CWYF as CWYF from YF_JZJL where YFSB =:yfsb and ZZSJ = ( select Max(ZZSJ) from YF_JZJL where YFSB =:yfsb )");
			Map<String, Object> map_fcwyf = dao.doLoad(hql_fcwyf.toString(),
					map_par);
			if (map_fcwyf.get("CWYF") == null) {
				dates.add(ServiceCode.CODE_RECORD_NOT_FOUND + "");
				dates.add("药房结帐记录中时间有误!");
				return dates;
			} else {
				first_cwyf = ldt_sdf.parse(ldt_sdf.format(new Date(
						((Timestamp) (map_fcwyf.get("CWYF"))).getTime())));
			}
			dates.addAll(this.getBalanceDates(first_cwyf, ctx));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询初始化失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期类型转换失败", e);
		}
		return dates;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 获取对账的起止时间范围
	 * @updateInfo
	 * @param cwyf
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<String> getBalanceDates(Date cwyf, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat ldt_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat year_sdf = new SimpleDateFormat("yyyy");
		SimpleDateFormat month_sdf = new SimpleDateFormat("MM");
		List<String> dates = new ArrayList<String>();
		String qssj = null; // 起始时间
		String zzsj = null; // 终止时间
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		// String jgid = user.get("manageUnit.id");// 用户的机构ID
		try {
			int year = Integer.parseInt(year_sdf.format(cwyf));
			int month = Integer.parseInt(month_sdf.format(cwyf));
			qssj = this
					.getBalanceAccountDates(year, month, 0, true, yfsb, jgid);
			if (null == qssj) {
				if (month == 1) {
					qssj = this.getBalanceAccountDates(year - 1, 12, 1, true,
							yfsb, jgid);
				} else {
					qssj = this.getBalanceAccountDates(year, month - 1, 1,
							true, yfsb, jgid);
				}
			}
			if (null != qssj) {
				dates.add(qssj);
				StringBuffer cwyf_hql = new StringBuffer();
				Map<String, Object> cwyf_par = new HashMap<String, Object>();
				cwyf_hql.append("select CWYF as CWYF from YF_JZJL where ZZSJ = :qssj and YFSB = :yfsb ");
				cwyf_par.put("qssj", ldt_sdf.parse(qssj));
				cwyf_par.put("yfsb", yfsb);
				Map<String, Object> cwyf_map = dao.doLoad(cwyf_hql.toString(),
						cwyf_par);
				if (cwyf_map.get("CWYF") != null) {
					dates.add(ldt_sdf.format(new Date(((Timestamp) (cwyf_map
							.get("CWYF"))).getTime())));
				} else {
					List<String> error = new ArrayList<String>();
					error.add("900");
					error.add("该月未月结,不能查询该月报表!");
					return error;
				}
			} else {
				List<String> error = new ArrayList<String>();
				error.add("900");
				error.add("该月未月结.不能查询该月报表!");
				return error;
			}
			zzsj = this.getBalanceAccountDates(year, month, 1, false, yfsb,
					jgid);
			dates.add(zzsj);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期类型转换失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "日期查询失败", e);
		}
		return dates;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 获取会计时间
	 * @updateInfo
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param mode
	 * @param isBegin
	 *            计算时间类型 true：起始时间 false：终止时间
	 * @param yfsb
	 * @param jgid
	 * @return
	 * @throws ModelDataOperationException
	 */
	private String getBalanceAccountDates(int year, int month, int mode,
			boolean isBegin, long yfsb, String jgid)
			throws ModelDataOperationException {
		try {
			SimpleDateFormat ldt_sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Calendar beginCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			Calendar priorCal = Calendar.getInstance();
			beginCal.set(year, month - 1, 1, 0, 0, 0);
			endCal.set(year, month, 1, 0, 0, 0);
			priorCal.set(year, month - 2, 1, 0, 0, 0);
			Date begin_date = beginCal.getTime();
			Date end_date = endCal.getTime();
			Date prior_begin = priorCal.getTime();
			String date = null;
			if (mode == 0) {
				StringBuffer qssj_sb = new StringBuffer();
				qssj_sb.append("select max(QSSJ) as QSSJ  from YF_JZJL where YFSB =:yfsb and JGID =:jgid and CWYF >=:begin_date and CWYF <:end_date");
				Map<String, Object> be_par = new HashMap<String, Object>();
				be_par.put("yfsb", yfsb);
				be_par.put("jgid", jgid);
				be_par.put("begin_date", begin_date);
				be_par.put("end_date", end_date);
				Map<String, Object> be_qssj_map = dao.doLoad(
						qssj_sb.toString(), be_par);
				if (be_qssj_map.get("QSSJ") != null) {
					date = ldt_sdf.format(new Date(((Timestamp) (be_qssj_map
							.get("QSSJ"))).getTime()));
				}
				if (date == null && !isBegin) {
					StringBuffer qssj_sb2 = new StringBuffer();
					qssj_sb2.append("select max(QSSJ) as QSSJ from YF_JZJL where YFSB =:yfsb and JGID =:jgid and CWYF <=:prior_begin and CWYF <:end_date");
					Map<String, Object> pe_par = new HashMap<String, Object>();
					pe_par.put("yfsb", yfsb);
					pe_par.put("jgid", jgid);
					pe_par.put("prior_begin", prior_begin);
					pe_par.put("end_date", begin_date);
					Map<String, Object> pe_qssj_map = dao.doLoad(
							qssj_sb2.toString(), pe_par);
					if (pe_qssj_map.get("QSSJ") != null) {
						date = ldt_sdf.format(new Date(
								((Timestamp) (pe_qssj_map.get("QSSJ")))
										.getTime()));
					}
					if (date == null) {
						date = ldt_sdf.format(begin_date);
					} else {
						String hql = "select max(ZZSJ) as ZZSJ from YF_JZJL where YFSB =: yfsb and JGID =:jgid";
						Map<String, Object> par = new HashMap<String, Object>();
						par.put("yfsb", yfsb);
						par.put("jgid", jgid);
						Map<String, Object> zzsj_map = dao.doLoad(hql, par);
						if (zzsj_map.get("ZZSJ") != null) {
							date = ldt_sdf.format(new Date(
									((Timestamp) (zzsj_map.get("ZZSJ")))
											.getTime()));
						}
					}
				}
			} else {
				StringBuffer zzsj_sb = new StringBuffer();
				zzsj_sb.append("select max(ZZSJ) as ZZSJ from YF_JZJL where YFSB =:yfsb and JGID =:jgid and CWYF >= :begin_date and CWYF <:end_date and QSSJ <> ZZSJ");
				Map<String, Object> be_par = new HashMap<String, Object>();
				be_par.put("yfsb", yfsb);
				be_par.put("jgid", jgid);
				be_par.put("begin_date", begin_date);
				be_par.put("end_date", begin_date);
				Map<String, Object> zzsj_map = dao.doLoad(zzsj_sb.toString(),
						be_par);
				if (zzsj_map.get("ZZSJ") != null) {
					date = ldt_sdf.format(new Date(((Timestamp) (zzsj_map
							.get("ZZSJ"))).getTime()));
				}
				if (date == null && !isBegin) {
					StringBuffer zzsj_sb2 = new StringBuffer();
					zzsj_sb2.append("select max(QSSJ) as QSSJ from YF_JZJL  where YFSB =:yfsb and JGID =:jgid and CWYF <=:prior_begin and CWYF <:end_date and QSSJ <> ZZSJ");
					Map<String, Object> pe_par = new HashMap<String, Object>();
					pe_par.put("yfsb", yfsb);
					pe_par.put("jgid", jgid);
					pe_par.put("prior_begin", prior_begin);
					pe_par.put("end_date", end_date);
					Map<String, Object> pe_zzsj_map = dao.doLoad(
							zzsj_sb2.toString(), pe_par);
					if (pe_zzsj_map.get("QSSJ") != null) {
						date = ldt_sdf.format(new Date(
								((Timestamp) (pe_zzsj_map.get("QSSJ")))
										.getTime()));
					}
					if (date == null) {
						date = ldt_sdf.format(end_date);
					} else {
						date = ldt_sdf.format(new Date());
					}
				}
			}
			return date;
		} catch (PersistentDataOperationException e) {
			logger.error("Date query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "日期查询失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 对账日期范围查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<String> dateBalanceQuery(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<String> dates = new ArrayList<String>();
		SimpleDateFormat ym_sdf = new SimpleDateFormat("yyyy-MM");
		String endDateStr = (String) body.get("date");
		Date end_date = null;
		try {
			end_date = ym_sdf.parse(endDateStr);
			dates.addAll(this.getBalanceDates(end_date, ctx));
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期类型转换失败", e);
		}
		return dates;
	}

}
