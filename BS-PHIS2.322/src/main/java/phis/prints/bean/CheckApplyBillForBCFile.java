package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.util.context.Context;
import ctd.print.IHandler;
import ctd.print.PrintException;

public class CheckApplyBillForBCFile implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		try {
			BaseDAO dao = new BaseDAO(ctx);
			StringBuffer hql = new StringBuffer();
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> resMap = new HashMap<String, Object>();
			List<Map<String, Object>> list = null;
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgmc = user.getManageUnit().getName();// 用户的MC
			Integer yllb = Integer.parseInt(request.get("yllb") + "");
			String brid = request.get("brid") + "";
			String jgid = user.getManageUnitId();// 用户的MC
			long sqdh = Long.parseLong(request.get("sqdh") + "");
			String age = request.get("age") + "";
			String brxm = "";
			String brxb = "";
			String brxz = "";
			String hm = "";
			String lczd = "";
			String sjsj = "";
			String sjys = "";
			String sjks = "";
			if (yllb == 1) {// 门诊
				/** 获得病人信息 **/
				hql.append("select a.BRXM as BRXM,a.BRXB as BRXB,a.MZHM as MZHM,b.XZMC as BRXZ from MS_BRDA a left join GY_BRXZ b on a.BRXZ=b.BRXZ where a.BRID=:BRID");
				parameters.put("BRID", brid);
				resMap = dao.doSqlLoad(hql.toString(), parameters);
				brxm = resMap.get("BRXM") + "";
				brxb = "1".equals(resMap.get("BRXB") + "") ? "男" : "女";
				brxz = resMap.get("BRXZ") + "";
				hm = resMap.get("MZHM") + "";// 门诊号码
				hql.setLength(0);
				parameters.clear();
				resMap.clear();
				
				/** 获得就诊相关信息 **/
				hql.append("select b.ZDMC as LCZD,to_char(a.KDRQ,'yyyy-mm-dd hh24:mi:ss') as SJSJ,c.PERSONNAME as SJYS,"
						+ "d.KSMC as SJKS from MS_YJ01 a left join MS_BRZD b on a.JZXH = b.JZXH inner join SYS_Personnel c on a.YSDM = c.PERSONID"
						+ " inner join GY_KSDM d on a.KSDM = d.KSDM and a.YJXH=:YJXH order by ZZBZ desc");
				parameters.put("YJXH", sqdh);
				list = dao.doSqlQuery(hql.toString(), parameters);
				if (list != null && list.size() != 0) {
					lczd = list.get(0).get("LCZD") + "";
					sjsj = list.get(0).get("SJSJ") + "";
					sjys = list.get(0).get("SJYS") + "";
					sjks = list.get(0).get("SJKS") + "";
				}
				hql.setLength(0);
				parameters.clear();
				list.clear();
			} else {// 住院				
				/** 获得病人信息 **/
				hql.append("select * from (select a.BRXM as BRXM,a.BRXB as BRXB,b.XZMC as BRXZ,a.MQZD as RYZD,a.ZYHM as ZYHM from ZY_BRRY a left join GY_BRXZ b on a.BRXZ=b.BRXZ where a.BRID=:BRID order by a.ryrq desc) where rownum=1");
				parameters = new HashMap<String, Object>();
				parameters.put("BRID", brid);
				resMap = dao.doSqlLoad(hql.toString(), parameters);
				brxm = resMap.get("BRXM") + "";
				brxb = "1".equals(resMap.get("BRXB") + "") ? "男" : "女";
				brxz = resMap.get("BRXZ") + "";
				hm = resMap.get("ZYHM") + "";// 住院号码
				// 获取入院诊断
				lczd = resMap.get("RYZD") + "";
				hql.setLength(0);
				parameters.clear();
				resMap.clear();

				// 申检时间
				Date date = new Date();
				SimpleDateFormat sfd = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				sjsj = sfd.format(date);

				// 获取申检科室
				hql.append("select KSMC as KSMC from GY_KSDM where KSDM=:KSDM");
				parameters.put("KSDM",
						Long.parseLong(request.get("brksdm") + ""));// 病人科室
				sjks = dao.doLoad(hql.toString(), parameters).get("KSMC") + "";
				hql.setLength(0);
				parameters.clear();

				// 获取申检医生姓名
				String sql = "select personname as YGXM from sys_personnel where PERSONID=:YGDM";
				parameters.put("YGDM", request.get("zrysdm") + "");// 主任医生代码
				List<Map<String, Object>> list_sjys = dao.doSqlQuery(sql,
						parameters);
				if (list_sjys.size() != 0) {
					sjys = list_sjys.get(0).get("YGXM").toString();
				}
				hql.setLength(0);
				parameters.clear();

				// 病人床号
				response.put("ch", request.get("brch") + "");
			}
			
			/** 获得申请单相关信息 **/
			hql.append("select BZXX as BZXX,ZSXX as ZSXX,XBS as XBS,JWS as JWS,GMS as GMS,FZJC as FZJC,TGJC as TGJC from YJ_JCSQ_KD01 "
					+ "where YLLB=:YLLB and SQDH=:SQDH");
//			hql.append("select ZSXX as ZSXX,CTXX as CTXX,SYXX as SYXX,BZXX as BZXX,XBS as XBS from YJ_JCSQ_KD01 "
//					+ "where YLLB=:YLLB and SQDH=:SQDH");
			parameters.put("YLLB", yllb);
			parameters.put("SQDH", sqdh);
			resMap = dao.doLoad(hql.toString(), parameters);
			String zsxx = resMap.get("ZSXX") == null ? "" : resMap.get("ZSXX")
					+ "";
			String xbs = resMap.get("XBS") == null ? "" : resMap.get("XBS")
					+ "";
			String jws = resMap.get("JWS") == null ? "" : resMap.get("JWS")
					+ "";
			String gms = resMap.get("GMS") == null ? "" : resMap.get("GMS")
					+ "";
			String fzjc = resMap.get("FZJC") == null ? "" : resMap.get("FZJC")
					+ "";
			String tgjc = resMap.get("TGJC") == null ? "" : resMap.get("TGJC")
					+ "";
//			String ctxx = resMap.get("CTXX") == null ? "" : resMap.get("CTXX")
//					+ "";
//			String syxx = resMap.get("SYXX") == null ? "" : resMap.get("SYXX")
//					+ "";
			String bzxx = resMap.get("BZXX") == null ? "" : resMap.get("BZXX")
					+ "";
			hql.setLength(0);
			parameters.clear();
			
			/** 获得申请单检查项目 **/
			hql.setLength(0);
			parameters.clear();
			resMap.clear();
			StringBuffer sjxm = new StringBuffer("");
			hql.append("select b.XMMC as XMMC from YJ_JCSQ_KD02 a,YJ_JCSQ_JCXM b where a.XMID=b.XMID and a.SQDH=:SQDH and a.YLLB=:YLLB");
			parameters.put("YLLB", yllb);
			parameters.put("SQDH", sqdh);
			list = dao.doSqlQuery(hql.toString(), parameters);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				sjxm.append(map.get("XMMC") + ",");
			}
			hql.setLength(0);
			parameters.clear();
			list.clear();

			/** 获得检查费用 **/
			/*parameters.put("SQDH", sqdh);
			if (yllb == 1) {// 门诊
				hql.append("select sum(HJJE) as JCFY from MS_YJ02  where YJXH=:SQDH");
			} else {// 住院
				hql.append("select sum(YCSL*YPDJ) as JCFY from ZY_BQYZ  where YZZH=:SQDH");
			}
			String jcfy = String.format(
					"%.2f",
					Double.parseDouble(dao
							.doSqlQuery(hql.toString(), parameters).get(0)
							.get("JCFY")
							+ ""));*/

			/** 前台展示 **/
			//机构名称
			response.put("jgmc", "" + jgmc);
			//申请单名称、门诊号码/住院号码
			if (yllb == 1) {
				response.put("sqdmc", "B超申请单(门诊)");
				response.put("hmtitle", "门诊号码：");
			} else {
				response.put("sqdmc", "B超申请单(住院)");
				response.put("hmtitle", "住院号码：");
			}
			//门诊号码/住院号码
			response.put("hm", hm);
			//病人姓名
			response.put("name", brxm);
			//申请科室
			response.put("sjks", sjks);
			//年龄
			response.put("age", age);
			//性别
			response.put("sex", brxb);
			//病人性质
			response.put("brxz", brxz);
			//临床诊断
			response.put("lczd", lczd.equals("null")?"":lczd);
			//现病史
			response.put("xbs", xbs);
			//主诉信息
			response.put("zsxx", zsxx);
			//申请时间
			response.put("sjsj", sjsj);
			//既往史信息
			response.put("jws", jws);
			//过敏史信息
			response.put("gms", gms);
			//辅助检查信息
			response.put("fzjc", fzjc);
			//体格检查信息
			response.put("tgjc", tgjc);
			//开单医生
			response.put("sjys", sjys);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		try {
			BaseDAO dao = new BaseDAO(ctx);
			Integer yllb = Integer.parseInt(request.get("yllb") + "");
			long sqdh = Long.parseLong(request.get("sqdh") + "");
			StringBuffer hql = new StringBuffer();
			hql.append("select b.LBMC as JCLB,c.BWMC as JCBW,d.XMMC as JCXM from YJ_JCSQ_KD02 a,YJ_JCSQ_JCLB b,"
					+ "YJ_JCSQ_JCBW c,YJ_JCSQ_JCXM d where a.LBID=b.LBID and a.BWID=c.BWID and a.XMID=d.XMID "
					+ "and a.YLLB=:YLLB and a.SQDH=:SQDH");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("YLLB", yllb);
			parameters.put("SQDH", sqdh);
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			records.addAll(list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
