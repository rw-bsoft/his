package phis.application.cic.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import phis.source.utils.SchemaUtil;
import ctd.util.context.Context;

/**
 * 医技项目取消Module层
 * 
 * @author bsoft
 * 
 */
public class ClinicOutpatientExpensesInfoModule {
	protected Logger logger = LoggerFactory
			.getLogger(ClinicOutpatientExpensesInfoModule.class);
	private BaseDAO dao;

	public ClinicOutpatientExpensesInfoModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 医技项目取消，家床项目查询病人
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> doQueryFYXXInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ksdm = req.get("kdsm") + "";
		String jzys= req.get("jzys") + "";
		if (ksdm == "" || ksdm == null || "".equals(ksdm)
				|| "null".equals(ksdm)) {
			ksdm = "0";
		}
		if (!"0".equals(ksdm)) {
			ksdm = ksdm.substring(1, ksdm.length() - 1);
		}
		if (jzys == "" || jzys == null || "".equals(jzys)
				|| "null".equals(jzys)) {
			jzys = "0";
		}
		int sign = 0;
		try {
			Date adt_Begin = null;
			if (req.get("strdate") != null && req.get("strdate") != "") {
				adt_Begin = sdf.parse(req.get("strdate") + " 00:00:00");
			}
			Date adt_End = null;
			if (req.get("enddate") != null && req.get("enddate") != "") {
				adt_End = sdf.parse(req.get("enddate") + " 23:59:59");
			}
			Map<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("adt_Begin", adt_Begin);
			parameter.put("adt_End", adt_End);
			StringBuffer sqlcf = new StringBuffer(
					"select to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as JZSJ,a.BRBH as BRBH,h.MZHM as MZHM,h.BRXM as BRXM,h.BRXB as BRXB,h.BRXZ as BRXZ,a.KSDM as KSDM,count(distinct b.CFSB) as CFS,sum(c.HJJE) as CFJE from YS_MZ_JZLS a,MS_BRDA h,MS_CF01 b,MS_CF02 c where a.JZXH=b.JZXH and b.CFSB=c.CFSB and a.BRBH=h.BRID and b.MZXH is not null and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End");
			if (!"0".equals(ksdm)) {
				sqlcf.append(" and a.KSDM in(");
				sqlcf.append(ksdm);
				sqlcf.append(")");
			}
			if(!"0".equals(jzys)){
				sqlcf.append(" and a.YSDM='");
				sqlcf.append(jzys);
				sqlcf.append("'");
			}
			sqlcf.append(" group by a.KSSJ,a.BRBH,h.MZHM,h.BRXM,h.BRXB,h.BRXZ,a.KSDM order by a.KSSJ");
			StringBuffer sqlyj = new StringBuffer(
					"select to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as JZSJ,a.BRBH as BRBH,d.MZHM as MZHM,d.BRXM as BRXM,d.BRXB as BRXB,d.BRXZ as BRXZ,a.KSDM as KSDM,count(distinct b.YJXH) as JCS,sum(c.HJJE) as JCJE from YS_MZ_JZLS a,MS_YJ01 b,MS_YJ02 c,MS_BRDA d where a.jZXH=b.jZXH and b.YJXH=c.YJXH and a.BRbh=d.BRID and b.FJGL is null and b.FJLB is null and b.MZXH is not null and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End");
			if (!"0".equals(ksdm)) {
				sqlyj.append(" and a.KSDM in(");
				sqlyj.append(ksdm);
				sqlyj.append(")");
			}
			if(!"0".equals(jzys)){
				sqlyj.append(" and a.YSDM='");
				sqlyj.append(jzys);
				sqlyj.append("'");
			}
			sqlyj.append(" group by a.KSSJ,a.BRBH,d.MZHM,d.BRXM,d.BRXB,d.BRXZ,a.KSDM order by a.KSSJ");
			StringBuffer sqlfjje = new StringBuffer(
					"select s.JZSJ as JZSJ,s.BRBH as BRBH,s.MZHM as MZHM,s.BRXM as BRXM,s.BRXB as BRXB,s.BRXZ as BRXZ,s.KSDM as KSDM,sum(s.FJJE) as FJJE from (select distinct to_char(a.KSSJ, 'yyyy-mm-dd hh24:mi:ss') as JZSJ,a.BRBH as BRBH,h.MZHM as MZHM,h.BRXM as BRXM,h.BRXB as BRXB,h.BRXZ as BRXZ,a.KSDM as KSDM,c.ypzh as ypzh,e.HJJE as FJJE from YS_MZ_JZLS a,MS_BRDA h,MS_CF01 b,MS_CF02 c,MS_YJ01 d,MS_YJ02 e where a.JZXH = b.JZXH and b.CFSB = c.CFSB and a.BRBH = h.BRID and c.YPZH=d.FJGL and d.FJLB=2 and d.YJXH=e.YJXH and b.MZXH is not null and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End");
			if (!"0".equals(ksdm)) {
				sqlfjje.append(" and a.KSDM in(");
				sqlfjje.append(ksdm);
				sqlfjje.append(")");
			}
			if(!"0".equals(jzys)){
				sqlfjje.append(" and a.YSDM='");
				sqlfjje.append(jzys);
				sqlfjje.append("'");
			}
			sqlfjje.append(") s");
			sqlfjje.append(" group by s.JZSJ,s.BRBH,s.MZHM,s.BRXM,s.BRXB,s.BRXZ,s.KSDM order by s.JZSJ");
			List<Map<String, Object>> resultfjje = dao.doSqlQuery(
					sqlfjje.toString(), parameter);
			List<Map<String, Object>> resultcf = dao.doSqlQuery(
					sqlcf.toString(), parameter);
			List<Map<String, Object>> resultyj = dao.doSqlQuery(
					sqlyj.toString(), parameter);
			List<Map<String, Object>> resultmzfyall = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < resultfjje.size(); i++) {
				for (int j = i; j < resultcf.size(); j++) {
					if (resultfjje.get(i).get("JZSJ").toString()
							.equals(resultcf.get(j).get("JZSJ").toString())
							&& Long.parseLong(resultfjje.get(i).get("BRBH")
									+ "") == Long.parseLong(resultcf.get(j)
									.get("BRBH") + "")
							&& Long.parseLong(resultfjje.get(i).get("KSDM")
									+ "") == Long.parseLong(resultcf.get(j)
									.get("KSDM") + "")) {
						resultcf.get(j).put("FJJE",
								resultfjje.get(i).get("FJJE"));
						break;
					}
				}
			}
			for (int i = 0; i < resultcf.size(); i++) {
				for (int j = 0; j < resultyj.size(); j++) {
					if (resultcf.get(i).get("JZSJ").toString()
							.equals(resultyj.get(j).get("JZSJ").toString())
							&& Long.parseLong(resultcf.get(i).get("BRBH") + "") == Long
									.parseLong(resultyj.get(j).get("BRBH") + "")
							&& Long.parseLong(resultcf.get(i).get("KSDM") + "") == Long
									.parseLong(resultyj.get(j).get("KSDM") + "")) {
						resultcf.get(i).put("JCS", resultyj.get(j).get("JCS"));
						resultcf.get(i)
								.put("JCJE", resultyj.get(j).get("JCJE"));
						resultyj.remove(j);
						sign = 1;
					} else {
						resultyj.get(j).put("CFS", "");
						resultyj.get(j).put("CFJE", "");
					}
				}
				if (sign == 0) {
					resultcf.get(i).put("JCS", "");
					resultcf.get(i).put("JCJE", "");
				}
			}
			resultmzfyall.addAll(resultcf);
			resultmzfyall.addAll(resultyj);
			SchemaUtil.setDictionaryMassageForList(resultmzfyall,
					BSPHISEntryNames.MS_FYXX);
			return resultmzfyall;
		} catch (PersistentDataOperationException e) {
			logger.error("门诊费用统计查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "门诊费用统计查询失败");
		} catch (ParseException e) {
			logger.error("门诊费用统计查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "门诊费用统计查询失败");
		}
	}

	public List<Map<String, Object>> doQueryFYMXInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long ksdm = Long.parseLong(req.get("KSDM") + "");
		String jzys="0";
		if(req.containsKey("JZYS")&&(req.get("JZYS")+"").length()>0){
			jzys=req.get("JZYS")+"";
		}
		try {
			Date adt_Begin = new Date();
			if (!"0".equals(req.get("JZSJ") + "")) {
				adt_Begin = sdf.parse(req.get("JZSJ") + "");
			}
			Map<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("adt_Begin", adt_Begin);
			StringBuffer sqlcf = new StringBuffer(
					"select b.CFSB as DH,(case when b.CFLX=1 then '西药' when b.CFLX=2 then '中药' when b.CFLX=3 then '草药' end) as LB,e.PERSONNAME as YSDM,b.KFRQ as KDRQ,d.YPMC as YPMC,c.CFTS as CFTS,c.YPSL as YPSL,c.YCJL as YCJL,c.YFDW as YFDW,c.YPYF as YPYF,c.GYTJ as GYTJ,c.YFGG as YFGG,c.HJJE as HJJE from YS_MZ_JZLS a,MS_CF01 b,MS_CF02 c,YK_TYPK d,SYS_Personnel e where a.JZXH=b.JZXH and b.CFSB=c.CFSB and b.YSDM=e.PERSONID and c.YPXH=d.YPXH and b.MZXH is not null and a.KSSJ=:adt_Begin");
			if (!"0".equals(ksdm)) {
				sqlcf.append(" and a.KSDM=");
				sqlcf.append(ksdm);
			}
			if (!"0".equals(jzys)) {
				sqlcf.append(" and a.YSDM='");
				sqlcf.append(jzys+"' ");
			}
			StringBuffer sqlyj = new StringBuffer(
					"select b.YJXH as DH,( case when FJLB='2' then '附加' else '检查' end) as LB,e.PERSONNAME as YSDM,b.KDRQ as KDRQ,d.FYMC as YPMC,c.YLSL as YPSL,'次' as YFDW,c.HJJE as HJJE from YS_MZ_JZLS a,MS_YJ01 b,MS_YJ02 c,GY_YLSF d,SYS_Personnel e where a.jZXH=b.jZXH and b.YJXH=c.YJXH and c.YLXH=d.FYXH and b.YSDM=e.PERSONID and b.MZXH is not null and a.KSSJ=:adt_Begin");
			if (!"0".equals(ksdm)) {
				sqlyj.append(" and a.KSDM =");
				sqlyj.append(ksdm);
			}
			if (!"0".equals(jzys)) {
				sqlyj.append(" and a.YSDM='");
				sqlyj.append(jzys+"' ");
			}
			List<Map<String, Object>> resultcf = dao.doSqlQuery(
					sqlcf.toString(), parameter);
			List<Map<String, Object>> resultyj = dao.doSqlQuery(
					sqlyj.toString(), parameter);
			List<Map<String, Object>> resultmzfyall = new ArrayList<Map<String, Object>>();
			resultmzfyall.addAll(resultcf);
			resultmzfyall.addAll(resultyj);
			SchemaUtil.setDictionaryMassageForList(resultmzfyall,
					BSPHISEntryNames.MS_FYMX);
			return resultmzfyall;
		} catch (PersistentDataOperationException e) {
			logger.error("门诊费用统计查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "门诊费用统计查询失败");
		} catch (ParseException e) {
			logger.error("门诊费用统计查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "门诊费用统计查询失败");
		}
	}
}
