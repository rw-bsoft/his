package phis.application.ivc.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ChargesDailyService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ChargesDailyService.class);

	public void doQueryVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ChargesDailyModel cdm = new ChargesDailyModel(dao);
		cdm.doQueryVerification(req,res, ctx);
	}
	//获取汇总日期
	public void doGetHZRQ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId()+"";
		String jgid = user.getManageUnitId();// 用户的机构ID
		String sql = "select to_char(min(jzrq),'yyyy-mm-dd hh24:mi:ss') as jzrq from " +
				" (select min(SFRQ) as jzrq from MS_MZXX where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh" +
				" and JZRQ is null  " +
				" union all select min(GHSJ) as jzrq from MS_GHMX where JGID=:jgid and MZLB =:mzlb " +
				" and CZGH = :czgh and JZRQ is null " +
				" union all select min(THRQ) as jzrq from MS_THMX where JGID=:jgid and MZLB =:mzlb " +
				" and CZGH = :czgh and JZRQ is null " +
				" union all select min(ZFRQ) as jzrq from MS_ZFFP where JGID=:jgid and MZLB =:mzlb " +
				" and CZGH = :czgh and JZRQ is null )";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("jgid", jgid);
		p.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
		p.put("czgh", uid);
		try {
			List<Map<String, Object>> map = dao.doSqlQuery(sql, p);
			if (map != null && map.get(0) != null) {
				if (map.get(0).get("JZRQ") == null) {
					Calendar d = Calendar.getInstance();
					Date de=new Date() ;
					d.setTime(de);
					SimpleDateFormat lsdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					res.put("JZRQ", lsdftime.format(d.getTime()));
				} else {
					SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat lsdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String jzrq=map.get(0).get("JZRQ")+"";
					String jzrqstr=jzrq.substring(0, 10);
					Calendar d = Calendar.getInstance();
					Date de=new Date() ;
					d.setTime(de);
					String datestr=sdftime.format(d.getTime());
					if(jzrqstr.equals(datestr)){
						res.put("JZRQ", lsdftime.format(d.getTime()));
					}else{
						res.put("JZRQ", jzrqstr+" 23:59:59");
					}
				}
			}else{
				Calendar d = Calendar.getInstance();
				Date de=new Date() ;
				d.setTime(de);
				SimpleDateFormat lsdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				res.put("JZRQ", lsdftime.format(d.getTime()));
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//查询最后一次汇总时间
	public void doGetLastHZRQ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if(sdftime.parse(req.get("jzrq")+"").getTime()>new java.util.Date().getTime()){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "结账时间不能大于当前时间!");
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String uid = user.getUserId()+"";
			String jgid = user.getManageUnitId();// 用户的机构ID
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", jgid);
			parameters.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
			parameters.put("czgh", uid);
			String sql = "select to_char(max(jzrq),'yyyy-mm-dd hh24:mi:ss') as jzrq from (select max(jzrq) as jzrq from MS_MZXX where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh union all select max(jzrq) as jzrq from MS_GHMX where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh)";
			List<Map<String, Object>> map = dao.doSqlQuery(sql, parameters);
			if (map != null && map.get(0) != null) {
				if (map.get(0).get("JZRQ") == null) {
					res.put("body", "");
				} else {
					res.put("body", map.get(0).get("JZRQ")+"");
				}
			}
			parameters.put("jzrq", sdftime.parse(req.get("jzrq")+""));
			long mzxx_count = dao.doCount("MS_MZXX", "JGID = :jgid and JZRQ is null and MZLB = :mzlb and CZGH = :czgh and SFRQ<=:jzrq", parameters);
			long zffp_count = dao.doCount("MS_ZFFP", "JGID = :jgid and JZRQ is null and MZLB = :mzlb and CZGH = :czgh and ZFRQ<=:jzrq", parameters);
			long ghmx_count = dao.doCount("MS_GHMX", "JGID = :jgid and JZRQ is null and MZLB = :mzlb and CZGH = :czgh and CZSJ<=:jzrq", parameters);
			long thmx_count = dao.doCount("MS_THMX", "JGID = :jgid and JZRQ is null and MZLB = :mzlb and CZGH = :czgh and THRQ<=:jzrq", parameters);
			if(mzxx_count+zffp_count+ghmx_count+thmx_count==0){
				res.put("code", "502");
				res.put("msg", "没有需要结账的信息!");
				return;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//取消日报查询
	public void doQueryCancelCommit(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ChargesDailyModel csm = new ChargesDailyModel(dao);
		csm.doQueryCancelCommit(req, res, dao, ctx);
	}
	
	//取消日报
	public void doCancelCommit(Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		ChargesDailyModel csm = new ChargesDailyModel(dao);
		csm.doCancelCommit(req, res, dao, ctx);
	}
	public void doCheckout(Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ServiceException {
		ChargesDailyModel csm = new ChargesDailyModel(dao);
		try {
			csm.doCheckout(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}