package phis.prints.bean;
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
public class ReportForPurchaseAnalysis implements IHandler{

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("JGMC",user.getManageUnit().getName());//自动获取当前机构
		response.put("kssj",request.get("ds"));
		response.put("jssj",request.get("de"));
		response.put("sorttype",request.get("sorttype"));
		String topNum = (String)request.get("topnum");
		if(topNum == null) {
			topNum = "10";
		}
		String yplb="全部药品";
		if(!"".equals(request.get("yplb"))){
			StringBuffer hql=new StringBuffer();
			hql.append("select BMMC as BMMC from YK_BMZD where BMXH=:bmxh");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("bmxh", MedicineUtils.parseString(request.get("yplb")));
			try {
				Map<String,Object> m=dao.doLoad(hql.toString(), map_par);
				if(m!=null){
					yplb=m.get("BMMC")+"";
				}
			} catch (PersistentDataOperationException e) {
				throw new PrintException(9000,"采购分析药品查询失败");
			}
		}
		response.put("topnum",topNum);
		response.put("yplb",yplb);
		response.put("zb", user.getUserName());
		response.put("zbrq",DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
	}
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		long yksb=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));
		//获取页面传过来的参数
		String tjlx=MedicineUtils.parseString(request.get("sorttype"));//统计方式
		String dateF=MedicineUtils.parseString(request.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(request.get("de"));//结束时间
		int topnum=MedicineUtils.parseInt(request.get("topnum"));//前几位
		String yplb=request.get("yplb")+"";//药品类别,左边的树
		if(tjlx==null||dateF==null||dateT==null||topnum==0||yplb==null){
			return;
		}
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql=new StringBuffer();
		hql.append("select YPMC as YPMC,YPGG as YPGG,YPDW as YPDW,RKSL as RKSL,JHJE as JHJE,LSJE as LSJE," +
				" JXCE as JXCE,KL as KL,JHJG as JHJG,CDMC as CDMC,DWMC as DWMC from ( select c.YPMC as YPMC," +
				" c.YPGG as YPGG,c.YPDW as YPDW,sum(a.RKSL) as RKSL,sum(a.JHHJ) as JHJE,sum(a.LSJE) as LSJE," +
				" (sum(a.LSJE)-sum(a.JHHJ)) as JXCE,100  as KL,a.JHJG as JHJG,e.CDMC as CDMC,f.DWMC as DWMC " +
				" from YK_RK02 a,YK_RK01 b,YK_TYPK c,YK_RKFS d,YK_CDDZ e,YK_JHDW f where b.DWXH=f.DWXH " +
				" and a.YPCD=e.YPCD and a.RKFS=d.RKFS  and a.XTSB=d.XTSB and  d.DYFS=1 and  a.YPXH=c.YPXH " +
				" and a.XTSB=b.XTSB and a.RKFS=b.RKFS and a.RKDH=b.RKDH and b.RKPB=1 " +
				" and to_char(b.RKRQ,'yyyy-mm-dd')>=:datef and to_char(b.RKRQ,'yyyy-mm-dd')<=:datet and a.XTSB=:yksb " +
				" and c.YPDM like :yplb  group by c.YPMC,c.YPGG ,c.YPDW ,a.JHJG,e.CDMC,f.DWMC order by "+tjlx+" desc ) " +
				" where rownum<=:topnum");
		parameters.put("datef", dateF);
		parameters.put("datet", dateT);
		parameters.put("topnum", topnum);
		parameters.put("yplb", yplb+"%");
		parameters.put("yksb", yksb);
		List<Map<String, Object>> list;
		try {
			list = dao.doSqlQuery(hql.toString(), parameters);
			records.addAll(list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

}
