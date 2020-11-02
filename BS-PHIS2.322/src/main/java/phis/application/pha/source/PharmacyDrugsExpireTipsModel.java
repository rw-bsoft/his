package phis.application.pha.source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

public class PharmacyDrugsExpireTipsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyDrugsExpireTipsModel.class);

	public PharmacyDrugsExpireTipsModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-16
	 * @description 药房失效药品查询
	 * @updateInfo
	 * @param body
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPharmacyDrugsExpireTips(
			Map<String, Object> body,Map<String, Object> req)
			throws ModelDataOperationException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Map<String, Object> map_ret = new HashMap<String, Object>();
		map_ret.put("totalCount", 0);
		map_ret.put("body", new ArrayList<Map<String,Object>>());
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String adt_date = MedicineUtils.parseString(body.get("JZRQ"));
		int year=MedicineUtils.parseInt(adt_date.substring(0, 4))+1;
		String month=adt_date.substring(4, 6);
		String date=adt_date.substring(6, 8);
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		map_par.put("date", year+month+date);
		StringBuffer hql = new StringBuffer();//查询过期药品
		hql.append("select c.PYDM as PYDM,c.YPMC as YPMC,b.YFGG as YFGG,b.YFDW as YFDW,a.YPSL as KCSL,a.YPPH as YPPH,a.YPXQ as YPXQ from YF_KCMX a,YF_YPXX b,YK_TYPK c where a.YPXH=b.YPXH and a.YFSB= b.YFSB and a.YPXH=c.YPXH and a.YFSB=:yfsb and (to_char(a.YPXQ,'yyyymmdd') <=:date or a.YPXQ is null) ");
		if(body.containsKey("PYDM")){
			hql.append(" and c.PYDM like :pydm");
			map_par.put("pydm", body.get("PYDM")+"%");
		}
		if(body.containsKey("YPLX")){
			hql.append(" and c.TYPE=:yplx");
			map_par.put("yplx", MedicineUtils.parseInt(body.get("YPLX")));
		}
		hql.append(" order by a.YPXQ");
		//MedicineCommonModel model=new MedicineCommonModel(dao);
	//	map_ret=model.getPageInfoRecord(req, map_par, hql.toString(),null);
	//	if(map_ret.get("body")!=null){
			try {
				List<Map<String, Object>> l = dao.doQuery(hql.toString(), map_par);
				if(l==null||l.size()==0){
					return map_ret;
				}
				List<Map<String,Object>> list_allRet=new ArrayList<Map<String,Object>>();//所有满足条件的数据集合
				int xqsy=0;
				if(body.containsKey("XQSY")){
					xqsy=MedicineUtils.parseInt(body.get("XQSY"));
				}
				for(Map<String,Object> m:l){
					if(m.get("YPXQ")==null){
						m.put("SYXQ", "未维护");
						if(xqsy==0||xqsy==13){
							list_allRet.add(m);
						}
						continue;
					}
					String ypxq=sdf.format((Date)m.get("YPXQ"));
					if(MedicineUtils.parseInt(ypxq)<MedicineUtils.parseInt(adt_date)){
						m.put("SYXQ", "已过期");
						if(xqsy==0||xqsy==14){
							list_allRet.add(m);
						}
						continue;
					}
					int xqm=MedicineUtils.parseInt(ypxq.substring(0, 4))==year?MedicineUtils.parseInt(ypxq.substring(4, 6))+12:MedicineUtils.parseInt(ypxq.substring(4, 6));
					int bm=xqm-MedicineUtils.parseInt(month);//相差的月
					int xqd=MedicineUtils.parseInt(ypxq.substring(6, 8));
					int bd=0;//想差的日
					if(xqd<MedicineUtils.parseInt(date)){
						Calendar a = Calendar.getInstance();
						a.set(Calendar.YEAR, MedicineUtils.parseInt(ypxq.substring(0, 4)));
						a.set(Calendar.MONTH, MedicineUtils.parseInt(ypxq.substring(4, 6)) - 1);
						a.set(Calendar.DATE, xqd);
						a.set(Calendar.HOUR_OF_DAY, 0);
						a.set(Calendar.MINUTE, 0);
						a.set(Calendar.SECOND, 0);
						a.set(Calendar.MILLISECOND, 0);
						a.add(Calendar.DATE, -MedicineUtils.parseInt(date));
						bd=a.get(Calendar.DATE)+1;
						bm-=1;
					}else if(xqd>MedicineUtils.parseInt(date)){
						bd=xqd-MedicineUtils.parseInt(date)+1;
					}
					StringBuffer xcqx=new StringBuffer();
					if(bm!=0){
						xcqx.append(bm).append("月");
					}
					if(bd!=0){
						xcqx.append(bd).append("日");
					}
					if(bm==0&&bd==0){//zhangwei2要求的 如果效期是当天,显示1日
						xcqx.append("1日");
					}
					m.put("SYXQ", xcqx.toString());
					if(xqsy==0||(xqsy!=13&&xqsy!=14&&bm<xqsy)){
						list_allRet.add(m);
					}
				}
				//计算分页
				if(list_allRet.size()==0){
					return map_ret;
				}
				if(body.containsKey("print")){//如果是打印 不需要分页
					map_ret.put("totalCount", list_allRet.size());
					map_ret.put("body", list_allRet);
					return map_ret;
				}
				int first = MedicineUtils.parseInt(req.get("pageSize"))*(MedicineUtils.parseInt(req.get("pageNo"))-1);
				int last = MedicineUtils.parseInt(req.get("pageSize"))*MedicineUtils.parseInt(req.get("pageNo"));
				map_ret.put("totalCount", list_allRet.size());
				List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();//返回界面显示的数据集合
				for(int i=first;i<list_allRet.size()&&i<last;i++){
					list_ret.add(list_allRet.get(i));
				}
				map_ret.put("body",list_ret);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "失效药品查询失败", e);
			}
			
	//	}
		return map_ret;
	}
}
