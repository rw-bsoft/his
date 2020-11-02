package phis.prints.bean;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateUtil;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
//处方明细
public class ReportForAntiMicrobialOutBoundAnalysis implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("JGMC",user.getManageUnit().getName());//自动获取当前机构
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
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql=new StringBuffer();
		hql.append("select c.YPMC as YPMC,c.YPGG as YPGG,c.YPDW as YPDW,sum(a.SFSL) as CKSL,sum(a.JHJE) as JHJE,sum(a.LSJE) as LSJE,(sum(a.LSJE)-sum(a.JHJE)) as JXCE,100  as KL,a.JHJG as JHJG,e.CDMC as CDMC " +
				" from YK_CK02 a,YK_CK01 b,YK_TYPK c,YK_CKFS d,YK_CDDZ e" +
				" where a.YPCD=e.YPCD and a.CKFS=d.CKFS  and a.XTSB=d.XTSB and a.YPXH=c.YPXH and a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.CKPB=1 and c.KSBZ = 1 and to_char(b.CKRQ,'yyyy-mm-dd')>=:datef and to_char(b.CKRQ,'yyyy-mm-dd')<=:datet and a.XTSB=:yksb");
		hql.append(" group by c.YPMC,c.YPGG ,c.YPDW ,a.JHJG,e.CDMC order by c.YPMC");
		parameters.put("datef", dateF);
		parameters.put("datet", dateT);
		parameters.put("yksb", yksb);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = dao.doSqlQuery(hql.toString(), parameters);
			records.addAll(list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
