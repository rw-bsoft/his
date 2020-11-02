package phis.prints.bean;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
//处方明细
public class ReportForBasicDrugOutAnalysis implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("JGMC",user.getManageUnit().getName()+"基本药物出库分析表");//自动获取当前机构
		response.put("kssj",request.get("ds"));
		response.put("jssj",request.get("de"));
		response.put("zb", user.getUserName());
		response.put("zbrq",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
	}
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		//获取页面传过来的参数
		String dateF=MedicineUtils.parseString(request.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(request.get("de"));//结束时间
		if(dateF==null||dateT==null){
			return;
		}
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
		//Context ctx = ContextUtils.getContext();
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
		records.addAll(list);
	}
}
