package phis.application.war.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

/**
 * description:会诊邀请DAO
  *@author:zhangfs
 * create on 2013-5-8
 */
public class WardConsultationYqModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(WardConsultationYqModel.class);

	private  static final String YS_ZY_HZYQ = "YS_ZY_HZYQ";
 
	public WardConsultationYqModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * description:查看是否有会诊者记录存在
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public Long queryisExistRecord(Context ctx, String adviceObj) throws ModelDataOperationException {
		long count = 0l;
		String hql = "";
		hql = "YQDX = :obj";
		try {

			Map<String, Object> map_par = new HashMap<String, Object>();
	
			map_par.put("obj", adviceObj);

			count = dao.doCount(YS_ZY_HZYQ, hql, map_par);
		} catch (Exception e) {
			logger.error("query fails", e);
		}
		return count;

	}

	/**
	 * description:保存邀请记录
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void save(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("YQXH", 0);//邀请序号
		map.put("SQXH", Long.parseLong(body.get("SQXH").toString()));//申请序号
		map.put("YQDX", body.get("YQDX"));//要求对象
		map.put("DXLX", 2);//对象类型
		map.put("QRBZ",0);//确认标志
		map.put("JGID",jgid);//机构ID
		try {
			dao.doInsert(BSPHISEntryNames.YS_ZY_HZYQ, map, false);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}



	

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-10-25
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-10-25
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-10-25
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-10-25
	 * @description double型数据转换
	 * @updateInfo
	 * @param number
	 *            保留小数点后几位
	 * @param data
	 *            数据
	 * @return
	 */
	public double formatDouble(int number, double data) {
//		if (number > 8) {
//			return 0;
//		}
//		double x = Math.pow(10, number);
//		return Math.round(data * x) / x;
		return BSPHISUtil.getDouble(data,number);
	}
}
