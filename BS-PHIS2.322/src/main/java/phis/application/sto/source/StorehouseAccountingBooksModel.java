package phis.application.sto.source;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;

public class StorehouseAccountingBooksModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseAccountingBooksModel.class);

	public StorehouseAccountingBooksModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-9
	 * @description 药库会计账簿查询
	 * @updateInfo
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryStorehouseAccountingBooks(Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("body",null);
		map_ret.put("totalCount",0);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		String qssj=MedicineUtils.parseString(body.get("QSSJ"));
		String zzsj=MedicineUtils.parseString(body.get("ZZSJ"));
		int year=MedicineUtils.parseInt(zzsj.split("-")[0]);
		int month=MedicineUtils.parseInt(zzsj.split("-")[1]);
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		StringBuffer hql_qszz = new StringBuffer();
		hql_qszz.append("select QSSJ as QSSJ,ZZSJ as ZZSJ from YK_JZJL where CWYF=:cwyf and XTSB=:yksb");
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 10);
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		a.set(Calendar.MILLISECOND, 0);
		Map<String, Object> map_par_qszz = new HashMap<String, Object>();
		map_par_qszz.put("cwyf", a.getTime());
		map_par_qszz.put("yksb", yksb);
		Map<String, Object> map_qszz;
		try {
			MedicineCommonModel model=new MedicineCommonModel(dao);
			//首先查询下终止时间的月份有没月结,如果有月结,直接返回月结数据
			StringBuffer hql_yjjg=new StringBuffer();
			hql_yjjg.append("select distinct a.YPXH as YPXH,a.YPCD as YPCD,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,c.CDMC as CDMC,a.KCSL as KCSL,a.LSJE as LSJE,a.JHJE as JHJE from YK_YJJG a,YK_TYPK b,YK_CDDZ c,YK_YPBM d where d.YPXH=b.YPXH and a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.CWYF=:cwyf and a.XTSB=:yksb");
			if(body.containsKey("PYDM")){
				hql_yjjg.append(" and d.PYDM like '").append(body.get("PYDM")).append("%'");
			}
			map_qszz = dao.doLoad(hql_qszz.toString(),
					map_par_qszz);
			if (map_qszz != null && map_qszz.get("QSSJ") != null
					&& map_qszz.get("ZZSJ") != null) {
				map_ret=model.getPageInfoRecord(req, map_par_qszz, hql_yjjg.toString(), null);
				return map_ret;
			}
			//如果终止时间没有月结,查询最近一次月结记录
			StringBuffer hql_yjsj=new StringBuffer();
			hql_yjsj.append("select max(CWYF) as CWYF,max(ZZSJ) as ZZSJ from YK_JZJL where CWYF>=:qssj and CWYF<:zzsj and XTSB=:yksb");
			Calendar b = Calendar.getInstance();
			b.set(Calendar.YEAR, MedicineUtils.parseInt(qssj.split("-")[0]));
			b.set(Calendar.MONTH, MedicineUtils.parseInt(qssj.split("-")[1]) - 1);
			b.set(Calendar.DATE, 10);
			b.set(Calendar.HOUR_OF_DAY, 0);
			b.set(Calendar.MINUTE, 0);
			b.set(Calendar.SECOND, 0);
			b.set(Calendar.MILLISECOND, 0);
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("qssj", b.getTime());
			map_par.put("zzsj", a.getTime());
			map_par.put("yksb", yksb);
			List<Map<String,Object>> l_yjsj=dao.doSqlQuery(hql_yjsj.toString(), map_par);
			//如果当前时间范围内没有月结过,直接返回
			if(l_yjsj==null||l_yjsj.size()==0||l_yjsj.get(0)==null||l_yjsj.get(0).size()==0||l_yjsj.get(0).get("CWYF")==null){
				return map_ret;
			}
			//查询最近一次月结结果,该月结的终止时间当作 开始时间,当前统计结束时间 当作终止时间 查询出入库情况
			Date cwyf=(Date)l_yjsj.get(0).get("CWYF");
			Date kssj=(Date)l_yjsj.get(0).get("ZZSJ");
			Date jssj=getYjsjByMonth(MedicineUtils.parseInt(qssj.split("-")[0]),MedicineUtils.parseInt(qssj.split("-")[1]),yksb,user.getManageUnitId(),ctx);
			map_par.clear();
			map_par.put("cwyf", cwyf);
			map_par.put("yksb", yksb);
			map_ret=model.getPageInfoRecord(req, map_par, hql_yjjg.toString(), null);
			List<Map<String,Object>> list_yjjg=(List<Map<String,Object>>)map_ret.get("body");
			StringBuffer hql_rk = new StringBuffer();// 入库数据查询
			StringBuffer hql_ck = new StringBuffer();// 出库数据查询
			StringBuffer hql_tj = new StringBuffer();// 调价数据查询
			StringBuffer hql_pz = new StringBuffer();// 平账数据查询
			hql_rk.append(
					"select sum(JHHJ) as JHJE,sum(LSJE) as LSJE,sum(RKSL) as RKSL,YPXH as YPXH,YPCD as YPCD from YK_RK02 where  XTSB=:xtsb and YSRQ>=:begin and YSRQ<=:end group by YPXH,YPCD");
			hql_ck.append(
					"select sum(b.JHJE) as JHJE,sum(b.LSJE) as LSJE,sum(b.SFSL) as SFSL,b.YPXH as YPXH,b.YPCD as YPCD from YK_CK01 a,YK_CK02 b where  b.XTSB=:xtsb and a.CKRQ>=:begin and a.CKRQ<=:end and a.XTSB=b.XTSB and a.CKDH=b.CKDH and a.CKFS=b.CKFS and a.CKPB=1 group by b.YPXH,b.YPCD");
			hql_tj.append(
					"select sum(b.LSZZ-b.LSJZ) as LSJE,b.YPXH as YPXH,b.YPCD as YPCD from YK_TJ01 a,YK_TJ02 b where  b.XTSB=:xtsb and a.ZXRQ>=:begin and a.ZXRQ<=:end and a.XTSB=b.XTSB and a.TJDH=b.TJDH and a.TJFS=b.TJFS and a.ZYPB=1 group by b.YPXH,b.YPCD");
			hql_pz.append(
					"select b.YPXH as YPXH,b.YPCD as YPCD,sum(b.XJHE-b.YJHE) as JHJE,sum(b.XLSE-b.YLSE) as LSJE from YK_PZ01 a,YK_PZ02 b where a.PZID=b.PZID and a.XTSB=:xtsb and a.PZRQ>=:begin and a.PZRQ<=:end  group by b.YPXH,b.YPCD");
			map_par.clear();
			map_par.put("begin", kssj);
			map_par.put("end", jssj);
			map_par.put("xtsb", yksb);
			List<Map<String,Object>> list_rkjl=dao.doSqlQuery(hql_rk.toString(), map_par);
			List<Map<String,Object>> list_ckjl=dao.doSqlQuery(hql_ck.toString(), map_par);
			List<Map<String,Object>> list_tjjl=dao.doSqlQuery(hql_tj.toString(), map_par);
			List<Map<String,Object>> list_pzjl=dao.doSqlQuery(hql_pz.toString(), map_par);
			for(Map<String,Object> map_yjjg:list_yjjg){
				double kcsl=MedicineUtils.parseDouble(map_yjjg.get("KCSL"));
				double jhje=MedicineUtils.parseDouble(map_yjjg.get("JHJE"));
				double lsje=MedicineUtils.parseDouble(map_yjjg.get("LSJE"));
				for(Map<String,Object> map_rk:list_rkjl){
					if(MedicineUtils.compareMaps(map_yjjg, new String[]{"YPXH","YPCD"}, map_rk, new String[]{"YPXH","YPCD"})){
						kcsl+=MedicineUtils.parseDouble(map_rk.get("RKSL"));
						jhje+=MedicineUtils.parseDouble(map_rk.get("JHJE"));
						lsje+=MedicineUtils.parseDouble(map_rk.get("LSJE"));
					}
				}
				for(Map<String,Object> map_ck:list_ckjl){
					if(MedicineUtils.compareMaps(map_yjjg, new String[]{"YPXH","YPCD"}, map_ck, new String[]{"YPXH","YPCD"})){
						kcsl-=MedicineUtils.parseDouble(map_ck.get("SFSL"));
						jhje-=MedicineUtils.parseDouble(map_ck.get("JHJE"));
						lsje-=MedicineUtils.parseDouble(map_ck.get("LSJE"));
					}
				}
				for(Map<String,Object> map_tj:list_tjjl){
					if(MedicineUtils.compareMaps(map_yjjg, new String[]{"YPXH","YPCD"}, map_tj, new String[]{"YPXH","YPCD"})){
						lsje+=MedicineUtils.parseDouble(map_tj.get("LSJE"));
					}
				}
				for(Map<String,Object> map_pz:list_pzjl){
					if(MedicineUtils.compareMaps(map_yjjg, new String[]{"YPXH","YPCD"}, map_pz, new String[]{"YPXH","YPCD"})){
						lsje-=MedicineUtils.parseDouble(map_pz.get("LSJE"));
						jhje-=MedicineUtils.parseDouble(map_pz.get("JHJE"));
					}
				}
				map_yjjg.put("KCSL", MedicineUtils.formatDouble(2, kcsl));
				map_yjjg.put("JHJE", MedicineUtils.formatDouble(4, jhje));
				map_yjjg.put("LSJE", MedicineUtils.formatDouble(4, lsje));
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "会计账簿查询失败", e);
		}
		return map_ret;
	/*		StringBuffer hql=new StringBuffer();
		hql.append("select g.YPXH as YPXH,g.YPCD as YPCD,g.YPMC as YPMC,g.YPGG as YPGG,g.YPDW as YPDW,g.CDMC as CDMC,nvl(b.KCSL,0) as KCSL,nvl(b.LSJE,0) as LSJE,nvl(b.JHJE,0) as JHJE from (select a.YPXH as YPXH,d.YPCD as YPCD,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,e.CDMC as CDMC,a.JGID as JGID from YK_YPBM f,YK_TYPK c,YK_CDXX d,YK_CDDZ e,YK_YPXX a where a.YPXH=c.YPXH and a.JGID=d.JGID and a.YPXH=d.YPXH and d.YPCD=e.YPCD and f.YPXH=c.YPXH and a.YKSB=:yksb");
		if(body.containsKey("PYDM")){
			hql.append(" and f.PYDM like ").append(body.get("PYDM")).append("%");
		}
		hql.append(") g left outer join (select sum(KCSL) as KCSL,sum(LSJE) as LSJE,sum(JHJE) as JHJE,YPXH as YPXH,YPCD as YPCD,JGID as JGID from YK_KCMX where JGID=:jgid group by YPXH,YPCD,JGID) b on b.YPXH=g.YPXH and b.YPCD=g.YPCD and b.JGID=g.JGID");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("jgid", jgid);
		map_par.put("yksb", yksb);
		MedicineCommonModel model=new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, map_par, hql.toString(), null);*/
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-12
	 * @description 根据年和月查询月结时间
	 * @updateInfo
	 * @param year 年
	 * @param month 月
	 * @param yksb 
	 * @param jgid
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getYjsjByMonth(int year,int month,long yksb,String jgid, Context ctx) throws ModelDataOperationException{
		int yjDate=32;
		try{
		yjDate = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid,
				"YJSJ_YK" + yksb,BSPHISSystemArgument.defaultValue.get("YJSJ_YK"),BSPHISSystemArgument.defaultAlias.get("YJSJ_YK"),BSPHISSystemArgument.defaultCategory.get("YJSJ_YK"),ctx));// 月结日
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, "YJSJ_YK" + yksb, e);
		}
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 10);
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		int lastDay = a.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (yjDate < lastDay) {
			a.set(Calendar.DATE, yjDate + 1);
		} else {
			a.set(Calendar.DATE, lastDay + 1);
		}
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		return a.getTime();
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-13
	 * @description 根据年月判断当月是否月结
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> querySfyj(Map<String,Object> body)throws ModelDataOperationException{
		Map<String,Object> map_ret=MedicineUtils.getRetMap();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String yjsj=MedicineUtils.parseString(body.get("KSSJ"));
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, MedicineUtils.parseInt(yjsj.split("-")[0]));
		a.set(Calendar.MONTH, MedicineUtils.parseInt(yjsj.split("-")[1]) - 1);
		a.set(Calendar.DATE, 10);
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		a.set(Calendar.MILLISECOND, 0);
		Date cwyf=a.getTime();
		StringBuffer hql=new StringBuffer();
		hql.append(" a.XTSB=b.XTSB and a.CWYF=b.CWYF and a.YPXH=:ypxh and a.YPCD=:ypcd and a.XTSB=:xtsb and a.CWYF=:cwyf ");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", yksb);
		map_par.put("cwyf", cwyf);
		map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
		try {
			long l=dao.doCount("YK_YJJG a,YK_JZJL b", hql.toString(), map_par);
			if(l==0){
				map_ret.put("code", 9000);
				map_ret.put("msg", "该药品上月未做月底过账,无法查询台帐明细");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "是否月结查询失败", e);
		}
		return map_ret;
	}
	
	public List<Map<String, Object>> countKcsl(Map<String, Object> body,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException{
		String jgid = (String) body.get("JGID");
		long ypxh =  Long.parseLong(body.get("YPXH")+"");
		Map<String, Object> parameters = new HashMap<String, Object>();
		String hql = "select nvl(sum(KCSL),0) as KCSL from YK_KCMX where JGID =:JGID and YPXH =:YPXH";
		parameters.put("JGID", jgid);
		parameters.put("YPXH", ypxh);
		List<Map<String, Object>> kcList = new ArrayList<Map<String,Object>>();
		try {
			kcList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return kcList;
	}
	
	public void changYKSBInfo(Map<String, Object> body,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException {
		String jgid = (String) body.get("JGID");
		int ypxh =  (Integer) body.get("YPXH");
		String yksb =  (String) body.get("YKSB");
		int ryksb = (Integer) body.get("RYKSB");
		Map<String, Object> parameters = new HashMap<String, Object>();
		String hql = "UPDATE YK_YPXX SET yksb =:YKSB where jgid =:JGID and ypxh =:YPXH and yksb =:RYKSB";
		parameters.put("YKSB", yksb);
		parameters.put("JGID", jgid);
		parameters.put("YPXH", ypxh);
		parameters.put("RYKSB", ryksb);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
