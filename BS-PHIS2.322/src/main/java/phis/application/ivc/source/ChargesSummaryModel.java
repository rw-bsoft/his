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
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class ChargesSummaryModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ChargesSummaryModel.class);

	public ChargesSummaryModel(BaseDAO dao) {
		this.dao = dao;
	}
	// 统计前验证
	public void doChargesSummaryBefVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构名称
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("JGID", jgid);
			parameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			Calendar cdate = Calendar.getInstance();
			Calendar nowcdate = Calendar.getInstance();
			nowcdate.set(Calendar.HOUR_OF_DAY, 23);
			nowcdate.set(Calendar.MINUTE, 59);
			nowcdate.set(Calendar.SECOND, 59);
			nowcdate.set(Calendar.MILLISECOND, 999);
//			if(req.containsKey("jzrq")){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date date = sdf.parse(req.get("hzrq")+"");
					if(date.getTime()>nowcdate.getTime().getTime()){
						res.put(Service.RES_CODE, 601);
						return;
					}
//					jzdate = date;
					cdate.setTime(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				parameters.put("hzrq", cdate.getTime());
//			}else{
//				parameters.put("jzrq", cdate.getTime());
//			}
				String hzrb_sql1 = "select max(HZRQ) as HZRQ from MS_HZRB where JGID =:JGID and MZLB =:MZLB and HZRQ>=:hzrq group by CZGH";
//				String ghrb_sql1 = "select CZGH as YGDM,sum(ZJJE) as GHJE,0.0 as XJJE,sum(QTYS) as QTYS,0.0 as HBWC,sum(FPZS) as GHRC,sum(THSL) as THSL from MS_GHRB where JGID =:JGID and MZLB =:MZLB and HZRQ>=:hzrq group by CZGH";
//				String sfrb_fkmx_sql1 = "select MS_HZRB.CZGH as YGDM,MS_SFRB_FKMX.FKFS as FKFS,sum(MS_SFRB_FKMX.FKJE) as FKJE from MS_SFRB_FKMX MS_SFRB_FKMX,MS_HZRB MS_HZRB where MS_HZRB.CZGH = MS_SFRB_FKMX.CZGH and MS_HZRB.JZRQ = MS_SFRB_FKMX.JZRQ and MS_HZRB.JGID =:JGID and MS_HZRB.MZLB =:MZLB and MS_HZRB.HZRQ>=:hzrq group by MS_HZRB.CZGH,MS_SFRB_FKMX.FKFS";
//				String ghrb_fkmx_sql1 = "select MS_GHRB.CZGH as YGDM,MS_GHRB_FKMX.FKFS as FKFS,sum(MS_GHRB_FKMX.FKJE) as FKJE from MS_GHRB_FKMX MS_GHRB_FKMX,MS_GHRB MS_GHRB where MS_GHRB.CZGH = MS_GHRB_FKMX.CZGH and MS_GHRB.JZRQ = MS_GHRB_FKMX.JZRQ and MS_GHRB.JGID =:JGID and MS_GHRB.MZLB =:MZLB and MS_GHRB.HZRQ>=:hzrq group by MS_GHRB.CZGH,MS_GHRB_FKMX.FKFS";
				List<Map<String, Object>> hzrb_list1 = dao.doQuery(hzrb_sql1,
						parameters);// 收费日报数据
//				List<Map<String, Object>> ghrb_list1 = dao.doQuery(ghrb_sql1,
//						parameters);// 挂号日报数据
//				List<Map<String, Object>> sfrb_fkmx_list1 = dao.doQuery(
//						sfrb_fkmx_sql1, parameters);// 已结帐未汇总收费付款数据
//				List<Map<String, Object>> ghrb_fkmx_list1 = dao.doQuery(
//						ghrb_fkmx_sql1, parameters);// 已结帐未汇总挂号付款数据
				if (hzrb_list1.size() > 0) {
					res.put(Service.RES_CODE, 602);
					res.put("body", hzrb_list1.get(0).get("HZRQ"));
					return;
				}
			String hzrb_sql = "select CZGH as YGDM,sum(ZJJE) as ZJJE,0.0 as XJJE,sum(QTYS) as QTYS,sum(JJZFJE) as JJZFJE,0.0 as HBWC,sum(FPZS) as FPZS,sum(ZFZS) as ZFZS from MS_HZRB where JGID =:JGID and MZLB =:MZLB and HZRQ is null and JZRQ<=:hzrq group by CZGH";
			String ghrb_sql = "select CZGH as YGDM,sum(ZJJE) as GHJE,0.0 as XJJE,sum(QTYS) as QTYS,0.0 as HBWC,sum(FPZS) as GHRC,sum(THSL) as THSL from MS_GHRB where JGID =:JGID and MZLB =:MZLB and HZRQ is null and JZRQ<=:hzrq group by CZGH";
			String sfrb_fkmx_sql = "select MS_HZRB.CZGH as YGDM,MS_SFRB_FKMX.FKFS as FKFS,sum(MS_SFRB_FKMX.FKJE) as FKJE from MS_SFRB_FKMX MS_SFRB_FKMX,MS_HZRB MS_HZRB where MS_HZRB.CZGH = MS_SFRB_FKMX.CZGH and MS_HZRB.JZRQ = MS_SFRB_FKMX.JZRQ and MS_HZRB.JGID =:JGID and MS_HZRB.MZLB =:MZLB and MS_HZRB.HZRQ is null and MS_HZRB.JZRQ<=:hzrq group by MS_HZRB.CZGH,MS_SFRB_FKMX.FKFS";
			String ghrb_fkmx_sql = "select MS_GHRB.CZGH as YGDM,MS_GHRB_FKMX.FKFS as FKFS,sum(MS_GHRB_FKMX.FKJE) as FKJE from MS_GHRB_FKMX MS_GHRB_FKMX,MS_GHRB MS_GHRB where MS_GHRB.CZGH = MS_GHRB_FKMX.CZGH and MS_GHRB.JZRQ = MS_GHRB_FKMX.JZRQ and MS_GHRB.JGID =:JGID and MS_GHRB.MZLB =:MZLB and MS_GHRB.HZRQ is null and MS_GHRB.JZRQ<=:hzrq group by MS_GHRB.CZGH,MS_GHRB_FKMX.FKFS";
			List<Map<String, Object>> hzrb_list = dao.doQuery(hzrb_sql,
					parameters);// 收费日报数据
			List<Map<String, Object>> ghrb_list = dao.doQuery(ghrb_sql,
					parameters);// 挂号日报数据
			List<Map<String, Object>> sfrb_fkmx_list = dao.doQuery(
					sfrb_fkmx_sql, parameters);// 已结帐未汇总收费付款数据
			List<Map<String, Object>> ghrb_fkmx_list = dao.doQuery(
					ghrb_fkmx_sql, parameters);// 已结帐未汇总挂号付款数据
			if (hzrb_list.size() <= 0 && ghrb_list.size() <= 0
					&& sfrb_fkmx_list.size() <= 0 && ghrb_fkmx_list.size() <= 0) {
				res.put(Service.RES_CODE, 600);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	// 汇总前验证
	public void doChargesSummaryCheckOutBefVerification(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
//		Date nowdate = new Date();
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date csdate = null;
		try {
			if (req.get("hzrq") != null) {
				csdate = sdfdatetime.parse(req.get("hzrq") + "");
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();// 用户的机构名称
			Calendar startc = Calendar.getInstance();
			startc.setTime(csdate);
			startc.set(Calendar.HOUR_OF_DAY, 0);
			startc.set(Calendar.MINUTE, 0);
			startc.set(Calendar.SECOND, 0);
			startc.set(Calendar.MILLISECOND, 0);
			Date ldt_begin = startc.getTime();
			startc.set(Calendar.HOUR_OF_DAY, 23);
			startc.set(Calendar.MINUTE, 59);
			startc.set(Calendar.SECOND, 59);
			startc.set(Calendar.MILLISECOND, 999);
			Date ldt_end = startc.getTime();

//			Map<String, Object> hzrbjzrqparameters = new HashMap<String, Object>();
//			hzrbjzrqparameters.put("JGID", jgid);
//			hzrbjzrqparameters.put("JZRQ", csdate);
//			hzrbjzrqparameters.put("MZLB",
//					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
//			String hzrbjzrqsql = "select CZGH as CZGH from MS_HZRB where JGID =:JGID and MZLB =:MZLB and JZRQ>:JZRQ";
//			List<Map<String, Object>> hzrbjzrqlist;
//
//			hzrbjzrqlist = dao.doQuery(hzrbjzrqsql, hzrbjzrqparameters);

//			if (hzrbjzrqlist.size() > 0) {// 判断在idt_jzrq后是否有新的结帐发生
//				res.put(Service.RES_CODE, 601);
//			}
			Map<String, Object> hzrbhzrqparameters = new HashMap<String, Object>();
			hzrbhzrqparameters.put("JGID", jgid);
			hzrbhzrqparameters.put("ldt_begin", ldt_begin);
			hzrbhzrqparameters.put("ldt_end", ldt_end);
			hzrbhzrqparameters.put("MZLB",
					Long.parseLong(BSPHISUtil.getMZLB(jgid, dao) + ""));
			String hzrbhzrqql = "select CZGH as CZGH from MS_HZRB where JGID =:JGID and HZRQ >=:ldt_begin and HZRQ <=:ldt_end and MZLB =:MZLB";
			List<Map<String, Object>> hzrbhzrqlist = dao.doQuery(hzrbhzrqql,
					hzrbhzrqparameters);
			if (hzrbhzrqlist.size() > 0) {// 判断在指定的汇总日期是否已有汇总结帐,如果已有则不能再结帐
				res.put(Service.RES_CODE, 602);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	//取消汇总查询
	public void doQueryCancelCommit(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构名称
//		String userId = user.getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
//		parameters.put("czgh", userId);
		try {
			List<Map<String,Object>> list = dao.doSqlQuery("select to_char(max(HZRQ ),'yyyy-mm-dd hh24:mi:ss') as HZRQ from (SELECT HZRQ as HZRQ FROM MS_HZRB WHERE JGID =:jgid union all SELECT HZRQ as HZRQ FROM MS_GHRB WHERE JGID =:jgid)", parameters);
			if(list.size()>0 && list.get(0).get("HZRQ")!=null){
				res.put("HZRQ", list.get(0).get("HZRQ"));
			}else{
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "当前没有汇总信息!");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "查询汇总信息出错!");
		}
		
	}
	
	//取消汇总查询
		public void doCancelCommit(
				Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
				Context ctx) throws ModelDataOperationException {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();// 用户的机构名称
			Map<String, Object> parameters = new HashMap<String, Object>();
			long mzlb = BSPHISUtil.getMZLB(jgid, dao);
			String hzrq = req.get("HZRQ")+"";
			SimpleDateFormat sdfdatetime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date hzdate = null;
			try {
				hzdate = sdfdatetime.parse(hzrq);
			} catch (ParseException e1) {
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "取消汇总信息出错!");
			}
			parameters.put("jgid", jgid);
			parameters.put("MZLB", mzlb);
			parameters.put("hzrq", hzdate);
			try {
				dao.doUpdate("update MS_MZXX set HZRQ =null where JGID =:jgid and MZLB =:MZLB and JZRQ Is Not Null and HZRQ=:hzrq", parameters);
				dao.doUpdate("update MS_ZFFP set HZRQ =null where JGID =:jgid and MZLB =:MZLB and JZRQ Is Not Null and HZRQ=:hzrq", parameters);
				dao.doUpdate("update MS_HZRB set HZRQ =null where JGID =:jgid and MZLB =:MZLB and JZRQ Is Not Null and HZRQ=:hzrq", parameters);
				dao.doUpdate("update MS_GHMX set HZRQ =null where JGID =:jgid and MZLB =:MZLB and JZRQ Is Not Null and HZRQ=:hzrq", parameters);
				dao.doUpdate("update MS_THMX set HZRQ =null where JGID =:jgid and MZLB =:MZLB and JZRQ Is Not Null and HZRQ=:hzrq", parameters);
				dao.doUpdate("update MS_GHRB set HZRQ =null where JGID =:jgid and MZLB =:MZLB and JZRQ Is Not Null and HZRQ=:hzrq", parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "取消汇总信息出错!");
			}
			
		}
}
