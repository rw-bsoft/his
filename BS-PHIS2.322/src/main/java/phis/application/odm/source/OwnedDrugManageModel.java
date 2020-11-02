package phis.application.odm.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

public class OwnedDrugManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(OwnedDrugManageModel.class);
	public OwnedDrugManageModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-10-15
	 * @description 自备药使用查询左边List数据查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryYplb( Map<String, Object> body) throws ModelDataOperationException {
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		int lx=MedicineUtils.parseInt(body.get("lx"));
		String ypmc="";
		if(body.containsKey("ypmc")){
			ypmc=MedicineUtils.parseString(body.get("ypmc"));
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer hql=new StringBuffer();
		Map<String,Object> map_par=new HashMap<String,Object>();
		try {
			map_par.put("ksrq", sdf.parse(MedicineUtils.parseString(body.get("dateFrom"))));
			map_par.put("jsrq", sdf.parse(MedicineUtils.parseString(body.get("dateTo"))));
			map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
//			if(!"".equals(ypmc)){
//				map_par.put("ypmc", ypmc);
//			}
			if(lx==1){//门诊
				hql.append("select distinct a.YPXH as YPXH,a.YPCD as YPCD,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,c.CDMC as CDMC from MS_CF02 a,YK_TYPK b,YK_CDDZ c,MS_CF01 d where a.CFSB=d.CFSB and a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.JGID=:jgid and d.KFRQ>=:ksrq and d.KFRQ<=:jsrq and a.ZFYP=1");
				if(!"".equals(ypmc)){
					hql.append(" and b.YPMC like '%").append(ypmc).append("%'");
				}
			}else{//住院
				hql.append("select distinct a.YPXH as YPXH,a.YPCD as YPCD,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,c.CDMC as CDMC from ZY_BQYZ a,YK_TYPK b,YK_CDDZ c where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.JGID=:jgid and a.KSSJ>=:ksrq and a.KSSJ<=:jsrq and a.ZFYP=1");
				if(!"".equals(ypmc)){
					hql.append(" and b.YPMC like '%").append(ypmc).append("%'");
				}
			}
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期格式错误", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "自备药查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-10-15
	 * @description 自备药使用查询右边List数据查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryMx( Map<String, Object> body) throws ModelDataOperationException {
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		int lx=MedicineUtils.parseInt(body.get("lx"));
		String ypmc="";
		if(body.containsKey("ypmc")){
			ypmc=MedicineUtils.parseString(body.get("ypmc"));
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer hql=new StringBuffer();
		Map<String,Object> map_par=new HashMap<String,Object>();
		try {
			map_par.put("ksrq", sdf.parse(MedicineUtils.parseString(body.get("dateFrom"))));
			map_par.put("jsrq", sdf.parse(MedicineUtils.parseString(body.get("dateTo"))));
			map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
			map_par.put("ypxh", MedicineUtils.parseLong(body.get("ypxh")));
			map_par.put("ypcd", MedicineUtils.parseLong(body.get("ypcd")));
//			if(!"".equals(ypmc)){
//				map_par.put("ypmc", ypmc);
//			}
			if(lx==1){//门诊
				hql.append("select a.SBXH as JLXH,d.KFRQ as KDSJ,d.CFHM as HM,d.BRXM as BRXM,b.YPMC as YPMC,b.YPGG as YPGG,a.YCJL as YPJL,e.PCMC as YPYF,a.YYTS as YYTS,a.YPSL as YPSL,a.YFDW as YFDW from MS_CF02 a,YK_TYPK b,YK_CDDZ c,MS_CF01 d,GY_SYPC e where a.YPYF=e.PCBM and a.CFSB=d.CFSB and a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.JGID=:jgid and d.KFRQ>=:ksrq and d.KFRQ<=:jsrq and a.ZFYP=1 and a.YPXH=:ypxh and a.YPCD=:ypcd");
				if(!"".equals(ypmc)){
					hql.append(" and b.YPMC like '%").append(ypmc).append("%'");
				}
			}else{//住院
				hql.append("select a.JLXH as JLXH,a.KSSJ as KDSJ,d.ZYHM as HM,d.BRXM as BRXM,b.YPMC as YPMC,b.YPGG as YPGG,a.YCJL as YPJL,e.PCMC as YPYF,a.TZSJ as TZSJ from ZY_BQYZ a,YK_TYPK b,YK_CDDZ c ,ZY_BRRY d,GY_SYPC e where a.ZYH=d.ZYH and a.SYPC=e.PCBM and a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.JGID=:jgid and a.KSSJ>=:ksrq and a.KSSJ<=:jsrq and a.ZFYP=1 and a.YPXH=:ypxh and a.YPCD=:ypcd");
				if(!"".equals(ypmc)){
					hql.append(" and b.YPMC like '%").append(ypmc).append("%'");
				}
			}
			list_ret=dao.doQuery(hql.toString(), map_par);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期格式错误", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "自备药查询失败", e);
		}
		return list_ret;
	}
}
