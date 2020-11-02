package phis.application.sto.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

public class TjfxSTOModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TjfxSTOModel.class);

	public TjfxSTOModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-9-9
	 * @description 采购分析查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> loadPurchaseAnalysicList(Map<String, Object> body) throws ModelDataOperationException {
		//String tjlx=MedicineUtils.parseString(body.get("sorttype"));//统计方式
		String dateF=MedicineUtils.parseString(body.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(body.get("de"));//结束时间
		int topnum=MedicineUtils.parseInt(body.get("topnum"));//前几位
		String yplb=body.get("yplb")+"";//药品类别,左边的树
		String medType=body.get("medType")+"";//是否抗菌药物
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		int cd=1;
		if(!"".equals(yplb)){
			cd=yplb.length()+2;
		}
		StringBuffer hql=new StringBuffer();
		hql.append("select (case (substr(c.YPDM,1,").append(cd).append(") ) when 'A' then 'A99999999' else (substr(c.YPDM,1,").append(cd).append(") ) end) as BMXH,sum(a.JHHJ) as JHJE,sum(a.LSJE) as LSJE , (sum(a.LSJE)-sum(a.JHHJ)) as JXCE,100 as KL from YK_RK02 a,YK_RKFS b,YK_TYPK c,YK_RK01 e where a.RKFS=b.RKFS and a.XTSB=b.XTSB and  b.DYFS=1 and a.YPXH=c.YPXH  and a.XTSB=e.XTSB and a.RKFS=e.RKFS and a.RKDH=e.RKDH  and e.RKPB=1 and to_char(e.RKRQ,'yyyy-mm-dd')>=:datef and to_char(e.RKRQ,'yyyy-mm-dd')<=:datet and a.XTSB=:yksb");
		Map<String,Object> map_par=new HashMap<String,Object>();
		if(!"".equals(yplb)){
			hql.append(" and  substr(c.YPDM,1,:ppcd) = :yplb  ");
			map_par.put("yplb", yplb);
			map_par.put("ppcd", yplb.length());
		}else{
			//hql.append(" and  substr(c.YPDM,1,1) ='0'  ");
		}
		if(!"".equals(medType)){
			if("1".equals(medType)){
				hql.append(" and  c.KSBZ = 1  ");
			}else if("9".equals(medType)){
				hql.append(" and  c.KSBZ <> 1  ");
			}
		}
		hql.append(" group by substr(c.YPDM,1,").append(cd).append(")");
		StringBuffer hql_r=new StringBuffer();
		hql_r.append("select b.BMMC as YPLBMC ,a.JHJE as JHZE,a.LSJE as LSZE,a. JXCE as JXCE ,a.KL as KL from ( ").append(hql).append(") a,YK_BMZD b where a.BMXH=b.BMXH and rownum<=:topnum ");
		map_par.put("datef", dateF);
		map_par.put("datet", dateT);
		map_par.put("topnum", topnum);
		map_par.put("yksb", yksb);
		List<Map<String,Object>> list=null;
		try {
			list=dao.doSqlQuery(hql_r.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "采购分析查询失败", e);
		}
		return list;
	} 
	
	/**
	 * 抗菌药物采购分析
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> loadAntiMicrobialPurchaseAnalysicList(Map<String, Object> body) throws ModelDataOperationException {
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		//获取页面传过来的参数
		String dateF=MedicineUtils.parseString(body.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(body.get("de"));//结束时间
		if(dateF==null||dateT==null){
			return null;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql=new StringBuffer();
		hql.append("select distinct a.YPXH as YPXH,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,sum(a.RKSL) as RKSL,sum(a.JHHJ) as JHJE,sum(a.LSJE) as LSJE,(sum(a.LSJE)-sum(a.JHHJ)) as JXCE,100  as KL " +
				"from YK_RK02 a,YK_RK01 b,YK_TYPK c,YK_RKFS d,YK_CDDZ e,YK_JHDW f " +
				"where b.DWXH=f.DWXH and a.YPCD=e.YPCD and a.RKFS=d.RKFS  and a.XTSB=d.XTSB and d.DYFS=1 and c.KSBZ = 1 and  a.YPXH=c.YPXH and a.XTSB=b.XTSB and a.RKFS=b.RKFS and a.RKDH=b.RKDH and b.RKPB=1 and to_char(b.RKRQ,'yyyy-mm-dd')>=:datef and to_char(b.RKRQ,'yyyy-mm-dd')<=:datet and a.XTSB=:yksb ");
		hql.append("group by a.YPXH,c.YPMC,c.YPGG ,c.YPDW order by c.YPMC");
		parameters.put("datef", dateF);
		parameters.put("datet", dateT);
		parameters.put("yksb", yksb);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = dao.doSqlQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 抗菌药物采购明细查询
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> loadAntiMicrobialPurchaseDetailList(Map<String, Object> body) throws ModelDataOperationException {
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		//获取页面传过来的参数
		String dateF=MedicineUtils.parseString(body.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(body.get("de"));//结束时间
		long YPXH=MedicineUtils.parseLong(body.get("YPXH"));//药品序号
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql=new StringBuffer();
		hql.append("select a.YPXH as YPXH,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,sum(a.RKSL) as RKSL,a.RKDH as RKDH,a.FPHM as FPHM,b.CGRQ as CGRQ,a.YPPH as YPPH,a.YPXQ as YPXQ,a.JHJG as JHJG,e.CDMC as CDMC,f.DWMC as DWMC " +
				" from YK_RK02 a,YK_RK01 b,YK_TYPK c,YK_RKFS d,YK_CDDZ e,YK_JHDW f " +
				" where b.DWXH=f.DWXH and a.YPCD=e.YPCD and a.RKFS=d.RKFS  and a.XTSB=d.XTSB and d.DYFS=1 and c.KSBZ = 1 " +
				" and a.YPXH=c.YPXH and a.XTSB=b.XTSB and a.RKFS=b.RKFS and a.RKDH=b.RKDH and b.RKPB=1 and a.XTSB=:XTSB " +
				" and a.YPXH=:YPXH and to_char(b.RKRQ,'yyyy-mm-dd')>=:datef and to_char(b.RKRQ,'yyyy-mm-dd')<=:datet");
		hql.append(" group by a.RKDH,a.FPHM,a.YPXH,b.CGRQ,c.YPMC,c.YPGG ,c.YPDW ,a.JHJG,e.CDMC,f.DWMC,a.YPPH,a.YPXQ order by b.CGRQ");
		parameters.put("XTSB", yksb);
		parameters.put("YPXH", YPXH);
		parameters.put("datef", dateF);
		parameters.put("datet", dateT);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = dao.doSqlQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 抗菌药物出库分析
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> loadAntiMicrobialOutBoundAnalysicList(Map<String, Object> body) throws ModelDataOperationException {
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		//获取页面传过来的参数
		String dateF=MedicineUtils.parseString(body.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(body.get("de"));//结束时间
		if(dateF==null||dateT==null){
			return null;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql=new StringBuffer();
		hql.append("select distinct a.YPXH as YPXH,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,sum(a.SFSL) as CKSL,sum(a.JHJE) as JHJE,sum(a.LSJE) as LSJE,(sum(a.LSJE)-sum(a.JHJE)) as JXCE,100  as KL " +
				"from YK_CK02 a,YK_CK01 b,YK_TYPK c,YK_CKFS d,YK_CDDZ e " +
				"where a.YPCD=e.YPCD and a.CKFS=d.CKFS and a.XTSB=d.XTSB and c.KSBZ = 1 and  a.YPXH=c.YPXH and a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.CKPB=1 and to_char(b.CKRQ,'yyyy-mm-dd')>=:datef and to_char(b.CKRQ,'yyyy-mm-dd')<=:datet and a.XTSB=:yksb ");
		hql.append("group by a.YPXH,c.YPMC,c.YPGG ,c.YPDW ");
		parameters.put("datef", dateF);
		parameters.put("datet", dateT);
		parameters.put("yksb", yksb);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = dao.doSqlQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 抗菌药物出库明细查询
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> loadAntiMicrobialOutBoundDetailList(Map<String, Object> body) throws ModelDataOperationException {
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		//获取页面传过来的参数
		String dateF=MedicineUtils.parseString(body.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(body.get("de"));//结束时间
		long YPXH=MedicineUtils.parseLong(body.get("YPXH"));//药品序号
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql=new StringBuffer();
		hql.append("select a.YPXH as YPXH,c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,sum(a.SFSL) as CKSL,a.CKDH as CKDH,b.CKRQ as CKRQ,a.YPPH as YPPH,a.YPXQ as YPXQ,a.JHJG as JHJG,e.CDMC as CDMC " +
				" from YK_CK02 a,YK_CK01 b,YK_TYPK c,YK_CKFS d,YK_CDDZ e " +
				" where a.YPCD=e.YPCD and a.CKFS=d.CKFS  and a.XTSB=d.XTSB and c.KSBZ = 1 " +
				" and a.YPXH=c.YPXH and a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.CKPB=1 and a.XTSB=:XTSB " +
				" and a.YPXH=:YPXH and to_char(b.CKRQ,'yyyy-mm-dd')>=:datef and to_char(b.CKRQ,'yyyy-mm-dd')<=:datet");
		hql.append(" group by a.CKDH,a.YPXH,b.CKRQ,c.YPMC,c.YPGG ,c.YPDW ,a.JHJG,e.CDMC,a.YPPH,a.YPXQ order by b.CKRQ");
		parameters.put("XTSB", yksb);
		parameters.put("YPXH", YPXH);
		parameters.put("datef", dateF);
		parameters.put("datet", dateT);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = dao.doSqlQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-9-9
	 * @description 出库分析
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> loadOutBoundAnalysicList(Map<String, Object> body) throws ModelDataOperationException {
		//String tjlx=MedicineUtils.parseString(body.get("sorttype"));//统计方式
		String dateF=MedicineUtils.parseString(body.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(body.get("de"));//结束时间
		int topnum=MedicineUtils.parseInt(body.get("topnum"));//前几位
		String yplb=body.get("yplb")+"";//药品类别,左边的树
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		int cd=1;
		if(!"".equals(yplb)){
			cd=yplb.length()+2;
		}
		StringBuffer hql=new StringBuffer();
		hql.append("select (case (substr(c.YPDM,1,").append(cd).append(") ) when 'A' then 'A99999999' else (substr(c.YPDM,1,").append(cd).append(") ) end) as BMXH,sum(a.JHJE) as JHJE,sum(a.LSJE) as LSJE , (sum(a.LSJE)-sum(a.JHJE)) as JXCE,100 as KL from YK_CK02 a,YK_TYPK c,YK_CK01 e where a.YPXH=c.YPXH  and a.XTSB=e.XTSB and a.CKFS=e.CKFS and a.CKDH=e.CKDH and e.CKPB = 1  and to_char(e.CKRQ,'yyyy-mm-dd')>=:datef and to_char(e.CKRQ,'yyyy-mm-dd')<=:datet and a.XTSB=:yksb ");
		Map<String,Object> map_par=new HashMap<String,Object>();
		if(!"".equals(yplb)){
			hql.append(" and  substr(c.YPDM,1,:ppcd) = :yplb  ");
			map_par.put("yplb", yplb);
			map_par.put("ppcd", yplb.length());
		}else{
			//hql.append(" and  substr(c.YPDM,1,1) ='0'  ");
		}
		hql.append(" group by substr(c.YPDM,1,").append(cd).append(")");
		StringBuffer hql_r=new StringBuffer();
		hql_r.append("select b.BMMC as YPLBMC ,a.JHJE as JHZE,a.LSJE as LSZE,a. JXCE as JXCE ,a.KL as KL " +
				" from ( ").append(hql).append(") a left join YK_BMZD b on a.BMXH=b.BMXH where rownum<=:topnum ");
		map_par.put("datef", dateF);
		map_par.put("datet", dateT);
		map_par.put("topnum", topnum);
		map_par.put("yksb", yksb);
		List<Map<String,Object>> list=null;
		try {
			list=dao.doSqlQuery(hql_r.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "采购分析查询失败", e);
		}
		return list;
	}
	
	
	/**
	 * 基本药物出库分析
	 * @author renwei  2020-08-28
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> loadBasicDrugOutAnalysicList(Map<String, Object> body) throws ModelDataOperationException {
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		//获取页面传过来的参数
		String dateF=MedicineUtils.parseString(body.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(body.get("de"));//结束时间
		if(dateF==null||dateT==null){
			return null;
		}
		//Map<String, Object> parameters = new HashMap<String, Object>();
		//parameters.put("datef", dateF);
		//parameters.put("datet", dateT);
		//parameters.put("yksb", yksb);
		StringBuffer hql=new StringBuffer();
		//hql.append("select a.yfsb as DWID,a.yfmc as CKDW,b.ypje as JYZE,a.ckze as CKZE,100*round(b.ypje/a.ckze,4)||'%' as BL from ("+
		//			" select yfsb,yfmc,sum(lsje) as ckze from ("+
		//			" select a.yfsb,c.yfmc,b.lsje,d.jylx from YK_CK01 a,YK_CK02 b,yf_yflb c,yk_typk d"+
		//			" where a.ckdh=b.ckdh and a.yfsb=c.yfsb and b.ypxh=d.ypxh " +
		//			" and ckrq>=to_date('"+dateF+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and ckrq<=to_date('"+dateT+" 23:59:59','yyyy-mm-dd hh24:mi:ss') "+
		//			" and b.sqsl>=0 and a.xtsb="+yksb+
		//			" ) group by yfsb,yfmc) a"+
		//			" left join"+ 
		//			" (select yfmc,sum(lsje) as ypje,jylx from ("+
		//			" select a.yfsb,c.yfmc,b.lsje,d.jylx from YK_CK01 a,YK_CK02 b,yf_yflb c,yk_typk d"+
		//			" where a.ckdh=b.ckdh and a.yfsb=c.yfsb and b.ypxh=d.ypxh" +
		//			" and ckrq>=to_date('"+dateF+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and ckrq<=to_date('"+dateT+" 23:59:59','yyyy-mm-dd hh24:mi:ss')"+
		//			" and b.sqsl>=0 and a.xtsb="+yksb+
		//			" ) group by yfmc,jylx) b"+
		//			" on a.yfmc=b.yfmc where b.jylx=2");
		hql.append("select a.ckfs as DWID,a.fsmc as CKDW,b.ypje as JYZE,a.ckze as CKZE,100*round(b.ypje/a.ckze,4)||'%' as BL from ("+
					" select ckfs,fsmc,sum(jhje) as ckze from ("+
					" select b.xtsb,b.ckfs,c.fsmc,b.jhje,d.jylx from YK_CK02 b left join YK_CK01 a on a.xtsb=b.xtsb and a.ckfs=b.ckfs and a.ckdh=b.ckdh"+ 
					" left join yk_ckfs c on b.xtsb=c.xtsb and b.ckfs=c.ckfs"+
					" left join yk_typk d on b.ypxh=d.ypxh"+
					" where a.ckrq>=to_date('"+dateF+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and a.ckrq<=to_date('"+dateT+" 23:59:59','yyyy-mm-dd hh24:mi:ss')"+ 
					" and a.xtsb="+yksb+
					" ) group by ckfs,fsmc order by ckfs) a"+
					" left join"+
					" (select ckfs,sum(jhje) as ypje,jylx from ("+
					" select b.xtsb,b.ckfs,c.fsmc,b.jhje,d.jylx from YK_CK02 b left join YK_CK01 a on a.xtsb=b.xtsb and a.ckfs=b.ckfs and a.ckdh=b.ckdh"+ 
					" left join yk_ckfs c on b.xtsb=c.xtsb and b.ckfs=c.ckfs"+
					" left join yk_typk d on b.ypxh=d.ypxh"+
					" where a.ckrq>=to_date('"+dateF+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and a.ckrq<=to_date('"+dateT+" 23:59:59','yyyy-mm-dd hh24:mi:ss')"+
					" and a.xtsb="+yksb+         
					" ) group by ckfs,jylx order by ckfs) b"+
					" on a.ckfs=b.ckfs where b.jylx=2");					
		//List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = ss.createSQLQuery(hql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		ss.close();
		return list;
		
	}
	
	
}
