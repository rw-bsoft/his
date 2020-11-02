package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateUtil;

import phis.application.mds.source.MedicineUtils;
import phis.application.pub.source.PublicModel;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PharmacyDispensingFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		List<Map<String, Object>> cflist = null;
		try {
			Long cfsb = MedicineUtils.parseLong(request.get("cfsb"));
			StringBuffer hql = new StringBuffer();
			hql.append("select b.YPMC as YPMC,a.SBXH as SBXH,a.CFSB as CFSB,a.YFGG as YPGG,a.YPXH as YPXH,a.YFDW as YPDW,a.YPSL as SL,a.YPDJ as YPDJ,a.HJJE as HJJE,a.CFTS as CFTS,a.YCJL as YCJL,a.YYTS as YYTS,a.GYTJ as GYTJ,c.CDMC as CDMC,a.YPCD as YPCD,a.ZFYP as ZFYP,d.YPSL as KFSL,e.XMMC as XMMC from MS_CF02 a,YK_TYPK b,YK_CDDZ c,YF_KCMX d, ZY_YPYF e where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.YPXH=d.YPXH and a.YPCD=d.YPCD and a.GYTJ = e.YPYF and a.CFSB=:cfsb and a.JGID=:jgid and d.YFSB=:yfsb ");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("cfsb", cfsb);			
			parameters.put("jgid", jgid);
			parameters.put("yfsb",
					MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			cflist = dao.doQuery(hql.toString(), parameters);
			for (int j = 0; j < cflist.size(); j++) {
				records.add(cflist.get(j));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		response.put("JGMC",user.getManageUnit().getName());//自动获取当前机构
		response.put("zb", user.getUserName());
		response.put("zbrq",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
		Long cfsb = MedicineUtils.parseLong(request.get("cfsb"));
		StringBuffer hql = new StringBuffer();
		hql.append("select a.BRXM as BRXM from MS_CF01 a where a.CFSB=:cfsb and a.JGID=:jgid ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cfsb", cfsb);			
		parameters.put("jgid", jgid);
		try {
			Map<String,Object> cflist = dao.doLoad(hql.toString(), parameters);
			if(cflist!=null){
				response.put("brxm",cflist.get("BRXM")!=null?(cflist.get("BRXM")+""):"");
			}else{
				response.put("brxm","");
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
