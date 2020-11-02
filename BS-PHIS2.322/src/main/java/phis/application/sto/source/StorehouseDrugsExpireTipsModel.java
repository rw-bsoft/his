package phis.application.sto.source;

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
import ctd.util.context.Context;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
/**
 * 过期提醒
 * @author caijy
 *
 */
public class StorehouseDrugsExpireTipsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseDrugsExpireTipsModel.class);

	public StorehouseDrugsExpireTipsModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 获取药品过期预警 截至日期 系统参数
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int querySX_PREALARM(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		int SX_PREALARM = 3;
		String SX_PREALARM_tem = ParameterUtil.getParameter(UserRoleToken.getCurrent().getManageUnit().getId(),
				BSPHISSystemArgument.SX_PREALARM, ctx);
		if (BSPHISUtil.isNumeric(SX_PREALARM_tem)) {// 判断是否为数字（包括整数和浮点数）
			SX_PREALARM = (int) Math.floor(Double.parseDouble(SX_PREALARM_tem));// 截取整数
		}
		return SX_PREALARM;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 药品过期提示查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadStorehouseDrugsExpireTipsList(
			Map<String, Object> body,Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		Map<String, Object> map_ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));
		String adt_date = MedicineUtils.parseString(body.get("JZRQ"));
		int year=MedicineUtils.parseInt(adt_date.substring(0, 4))+1;
		String month=adt_date.substring(4, 6);
		String date=adt_date.substring(6, 8);
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb", yksb);
		map_par.put("date", year+month+date);
		StringBuffer hql = new StringBuffer();//查询过期药品
		hql.append("select distinct  b.YPMC as YPMC , b.YPGG as YPGG,  b.YPDW as YPDW , a.CDMC as CDMC, c.KCSL as KCSL, c.YPXQ as YPXQ,  b.PYDM as PYDM from YK_CDDZ a,YK_TYPK b,YK_KCMX c,YK_YPXX  d where b.YPXH = c.YPXH and c.YPCD = a.YPCD and c.ypxh = d.YPXH and c.jgid = d.jgid and  d.YKSB =:yksb and c.KCSL > 0 and b.ZFPB=0 and (to_char(c.YPXQ,'yyyymmdd') <:date or c.YPXQ is null)   ");
		if(body.containsKey("PYDM")){
			hql.append(" and b.PYDM like :pydm");
			map_par.put("pydm", body.get("PYDM")+"%");
		}
		if(body.containsKey("YPLX")){
			hql.append(" and c.TYPE=:yplx");
			map_par.put("yplx", MedicineUtils.parseInt(body.get("YPLX")));
		}
		hql.append(" order by YPXQ");
		try {
			List<Map<String, Object>> l = dao.doSqlQuery(hql.toString(), map_par);
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
