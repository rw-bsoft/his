package phis.prints.bean; 
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map; 

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context; 

public class SummaryMedicinesInOfStorageDetailFile implements IHandler {
	List<Map<String,Object>> li = new ArrayList<Map<String,Object>>();
	@SuppressWarnings("unchecked")
	public void getFields(Map<String, Object> req,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(li);
		li.clear();
	}

	@Override
	public void getParameters(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String userid = user.getUserId();
		String CZY="";
		try {
			CZY = DictionaryController.instance().get("phis.dictionary.doctor")
					.getText(userid);
		} catch (ControllerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		String JGMC = user.getManageUnitName();
		String rkfs = req.get("rkfs") + "";
		String KSRQ = req.get("ksrq") + "";
		KSRQ = KSRQ.substring(0,4)+"年"+KSRQ.substring(4,6)+"月"+KSRQ.substring(6)+"日";
		String JSRQ = req.get("jsrq") + "";
		JSRQ = JSRQ.substring(0,4)+"年"+JSRQ.substring(4,6)+"月"+JSRQ.substring(6)+"日";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		String ZBRQ = sdf.format(new java.util.Date());
		String hql = "select fsmc as fsmc from yk_rkfs where rkfs="+rkfs;
		List<Map<String, Object>> fsmc_list;
		try {
			fsmc_list = dao.doSqlQuery(hql, null);
			res.put("LYBM", fsmc_list.get(0).get("FSMC"));
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("KSRQ", KSRQ);
		res.put("JSRQ", JSRQ);
		res.put("CZY", CZY);
		res.put("JGMC", JGMC);
		res.put("ZBRQ", ZBRQ);
		 
		String jgid = user.getManageUnitId();// 用户的机构ID
		Long yksb =  Long.parseLong(req.get("kfsb")+"");
		String ldt_start = req.get("ksrq") + "";
		String ldt_end = req.get("jsrq") + "";
		Map<String,Object> parameters = new HashMap<String, Object>();
		String hql2="SELECT YK_TYPK.YPMC as YPMC, YK_TYPK.YPGG as YPGG, YK_TYPK.YPDW as YPDW,YK_CDDZ.CDMC as CDMC,sum(RKSL) as RKSL,round(sum(LSJE),2) as LSJE, sum(PFJE) as  pfje, sum(JHHJ) as JHHJ,YK_TYPK.YPSX as YPSX,YK_TYPK.YPDM as YPDM,YK_TYPK.PYDM as PYDM, YK_RK02.YPXH as YPXH,YK_RK02.YPCD as YPCD "+
					" FROM YK_RK01 YK_RK01 , YK_RK02  YK_RK02, YK_TYPK  YK_TYPK, YK_CDDZ  YK_CDDZ "+
					"WHERE (YK_RK02.XTSB = YK_RK01.XTSB) and (YK_RK02.RKFS = YK_RK01.RKFS) and (YK_RK02.RKDH = YK_RK01.RKDH) and (YK_RK02.YPCD = YK_CDDZ.YPCD) and (YK_RK02.YPXH = YK_TYPK.YPXH) and  "+
					   "(YK_RK01.RKFS = "+rkfs+") AND (YK_RK01.RKPB = 1) AND (to_char(YK_RK01.RKRQ,'yyyymmdd') >= "+ldt_start+") AND ((to_char(YK_RK01.RKRQ,'yyyymmdd')<= "+ldt_end+")) and (YK_RK01.XTSB = "+yksb+") AND YK_RK01.JGID = "+jgid+
					" GROUP BY YK_RK02.YPXH,YK_RK02.YPCD,YK_TYPK.YPMC,YK_CDDZ.CDMC,YK_TYPK.PYDM,YK_TYPK.YPSX,YK_TYPK.YPDW,YK_TYPK.YPDM,YK_TYPK.YPGG "+
					"ORDER BY YK_TYPK.YPSX ASC, YK_TYPK.PYDM ASC";
		try{
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			list = dao.doSqlQuery(hql2, parameters);
			li.addAll(list);
			//计算合计
			double lshj = 0;
			if(list!=null||list.size()>0){
				for (Map<String, Object> map : list) {
					lshj += Double.valueOf(map.get("LSJE")+"");
				}
			}
			res.put("LSHJ", String.valueOf(new java.text.DecimalFormat("#.00").format(lshj)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
