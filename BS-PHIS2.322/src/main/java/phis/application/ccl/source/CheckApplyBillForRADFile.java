package phis.application.ccl.source;

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

public class CheckApplyBillForRADFile implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		try {
			BaseDAO dao = new BaseDAO(ctx);
			//User user = (User) ctx.get("user.instance");
			//String jgmc = user.get("manageUnit.name");
			UserRoleToken user = UserRoleToken.getCurrent();  
			String jgmc = user.getManageUnit().getName();// 用户的MC  
			Integer yllb = Integer.parseInt(request.get("yllb") + "");
			String brid = request.get("brid") + "";
			long sqdh = Long.parseLong(request.get("sqdh") + "");
			String age = request.get("age") + "";
			/** 获得病人信息 **/
			StringBuffer hql = new StringBuffer();
			hql.append("select BRXM as BRXM,BRXB as BRXB,MZHM as MZHM from MS_BRDA where BRID=:BRID");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BRID", brid);
			Map<String, Object> resMap = dao.doLoad(hql.toString(), parameters);
			String brxm = resMap.get("BRXM") + "";
			String brxb = "1".equals(resMap.get("BRXB") + "") ? "男" : "女";
			String mzhm = resMap.get("MZHM") + "";
			/** 获得就诊相关信息 **/
			hql.setLength(0);
			parameters.clear();
			resMap.clear();
			String lczd = "";
			String sjsj = "";
			String sjys = "";
			String sjks = "";
			if (yllb == 1) {//门诊
				hql.append("select b.ZDMC as LCZD,to_char(a.KDRQ,'yyyy-mm-dd hh24:mi:ss') as SJSJ,c.YGXM as SJYS,"
						+ "d.KSMC as SJKS from MS_YJ01 a,MS_BRZD b,GY_YGDM c,GY_KSDM d where a.JZXH=b.JZXH and "
						+ "a.YSDM=c.YGDM and a.KSDM=d.KSDM and b.ZZBZ=1 and a.YJXH=:YJXH");
				parameters.put("YJXH", sqdh);
				resMap = dao.doLoad(hql.toString(), parameters);
				lczd = resMap.get("LCZD") + "";
				sjsj = resMap.get("SJSJ") + "";
				sjys = resMap.get("SJYS") + "";
				sjks = resMap.get("SJKS") + "";
			}else {//住院
				long zyh = Long.parseLong(request.get("zyh")+"");//住院号
				String zyhm = request.get("zyhm")+"";//住院号码
				String brch = request.get("brch")+"";//病人床号
				String zrysdm = request.get("zrysdm")+"";//主任医生代码
				long brksdm = Long.parseLong(request.get("brksdm")+"");//病人科室
				sjsj = request.get("sjsj")+"";//申检时间
				//获取入院诊断
				hql.append("select RYZD as RYZD from ZY_BRRY where ZYH=:ZYH");
				parameters.put("ZYH", zyh);
				lczd = dao.doLoad(hql.toString(),parameters).get("RYZD")+"";
				hql.setLength(0);
				parameters.clear();
				//获取申检医生姓名
				hql.append("select YGXM as YGXM from GY_YGDM where YGDM=:YGDM");
				parameters.put("YGDM", zrysdm);
				sjys = dao.doLoad(hql.toString(), parameters).get("YGXM")+"";
				hql.setLength(0);
				parameters.clear();
				//获取申检科室
				hql.append("select KSMC as KSMC from GY_KSDM where KSDM=:KSDM");
				parameters.put("KSDM", brksdm);
				sjks = dao.doLoad(hql.toString(), parameters).get("KSMC")+"";
				response.put("zyhm", zyhm);
				response.put("ch", brch);
			}
			/** 获得申请单相关信息 **/
			hql.setLength(0);
			parameters.clear();
			resMap.clear();
			hql.append("select ZSXX as ZSXX,CTXX as CTXX,SYXX as SYXX,BZXX as BZXX from YJ_JCSQ_KD01 "
					+ "where YLLB=:YLLB and SQDH=:SQDH");
			parameters.put("YLLB", yllb);
			parameters.put("SQDH", sqdh);
			resMap = dao.doLoad(hql.toString(), parameters);
			String zsxx = resMap.get("ZSXX")==null?"":resMap.get("ZSXX")+"";
			String ctxx = resMap.get("CTXX")==null?"":resMap.get("CTXX")+"";
			String syxx = resMap.get("SYXX")==null?"":resMap.get("SYXX")+"";
			String bzxx = resMap.get("BZXX")==null?"":resMap.get("BZXX")+"";
			/** 前台展示 **/
			if(yllb==1){
				response.put("sqdmc", "放射申请单(门诊)");
			}else{
				response.put("sqdmc", "放射申请单(住院)");
			}
			response.put("jgmc", "上城区"+jgmc);
			response.put("sqdh", sqdh+"");
			response.put("name", brxm);
			response.put("sex", brxb);
			response.put("age", age);
			response.put("mzhm", mzhm);
			response.put("lczd", lczd);
			response.put("sjks", sjks);
			response.put("sjys", sjys);
			response.put("sjsj", sjsj);
			response.put("zsxx", zsxx);
			response.put("ctxx", ctxx);
			response.put("syxx", syxx);
			response.put("bzxx", bzxx);

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
			List<Map<String,Object>> list = dao.doSqlQuery(hql.toString(), parameters);
			records.addAll(list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

}
