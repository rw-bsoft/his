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

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ChargesDailyModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ChargesDailyModel.class);
//	Date nowdate = new Date();

	public ChargesDailyModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 产生数据前的验证
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryVerification(Map<String, Object> req,Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql_mzxx = new StringBuffer();// 统计收费信息MS_MZXX
		StringBuffer hql_ghmx = new StringBuffer();// 统计收费信息MS_MZXX
		parameters.put("jgid", jgid);
		parameters.put("czgh", uid);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cdate = Calendar.getInstance();
		try {
			Date date = sdf.parse(req.get("jzrq")+"");
			cdate.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		parameters.put("jzrq", cdate.getTime());
		hql_mzxx.append(
				"select CZGH as CZGH,count(CZGH) as FPZS,sum(ZJJE) as ZJJE,sum(XJJE) as XJJE,sum(ZPJE) as ZPJE,sum(ZHJE) as ZHJE,sum(QTYS) as QTYS,sum(JJZFJE) as JJZFJE,sum(HBWC) as HBWC from ")
				.append("MS_MZXX where JGID=:jgid and JZRQ IS NULL and MZLB =:mzlb and CZGH=:czgh and SFRQ<=:jzrq group by CZGH");
		try {
			parameters.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
			Map<String, Object> map_mzxx = dao.doLoad(hql_mzxx.toString(),
					parameters);
			hql_ghmx.append(
					"SELECT CZGH as CZGH,count(CZGH) as FPZS,sum(GHJE + ZLJE + ZJFY + BLJE) as ZJJE,sum(XJJE) as XJJE,sum(ZPJE) as ZPJE,sum(ZHJE) as ZHJE,sum(QTYS) as QTYS,sum(HBWC) as HBWC FROM ")
					.append("MS_GHMX where JGID=:jgid and JZRQ IS NULL and MZLB =:mzlb and CZGH=:czgh and CZSJ<=:jzrq group by CZGH");
			Map<String, Object> map_ghmx = dao.doLoad(hql_ghmx.toString(),
					parameters);
			StringBuffer hql_zffp = new StringBuffer();
			hql_zffp.append(" JGID=:jgid and MZLB =:mzlb and CZGH=:czgh and JZRQ IS NULL ");
			long zffpcount = dao.doCount("MS_ZFFP", hql_zffp.toString(),
					parameters);
			StringBuffer hql_thmx = new StringBuffer();
			hql_thmx.append(" JGID=:jgid and MZLB =:mzlb and CZGH=:czgh and JZRQ IS NULL ");
			long thmxcount = 0;
			thmxcount = dao.doCount("MS_THMX", hql_thmx.toString(), parameters);
			if ((map_mzxx == null || map_mzxx.size() == 0)
					&& (map_ghmx == null || map_ghmx.size() == 0)
					&& zffpcount == 0 && thmxcount == 0) {
				res.put(Service.RES_CODE, 801);
				return;
			}
			long count = 0;
			Map<String, Object> parameters1 = new HashMap<String, Object>();
			parameters1.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
			parameters1.put("czgh", uid);
			parameters1.put("jgid", jgid);
			parameters1.put("jzrq", cdate.getTime());
			StringBuffer hql_sql = new StringBuffer();
			parameters.put("mzlb", BSPHISUtil.getMZLB(jgid, dao));
			hql_sql.append(" JGID=:jgid and MZLB =:mzlb and CZGH=:czgh and SFRQ >=:jzrq and JZRQ IS NULL ");
			count = dao.doCount("MS_MZXX", hql_sql.toString(), parameters1);
			if (count > 0) {
				res.put(Service.RES_CODE, 802);
				return;
			}
			hql_sql = new StringBuffer();
			hql_sql.append(" JGID=:jgid and MZLB =:mzlb and CZGH=:czgh and ZFRQ >=:jzrq and JZRQ IS NULL ");
			count = 0;
			count = dao.doCount("MS_ZFFP", hql_sql.toString(), parameters1);
			if (count > 0) {
				res.put(Service.RES_CODE, 802);
				return;
			}
			hql_sql = new StringBuffer();
			hql_sql.append(" JGID=:jgid and MZLB =:mzlb and CZGH=:czgh and CZSJ >=:jzrq and JZRQ IS NULL ");
			count = 0;

			count = dao.doCount("MS_GHMX", hql_sql.toString(), parameters1);

			if (count > 0) {
				res.put(Service.RES_CODE, 802);

				return;
			}
			hql_sql = new StringBuffer();
			hql_sql.append(" JGID=:jgid and MZLB =:mzlb and CZGH=:czgh and THRQ >=:jzrq and JZRQ IS NULL ");
			count = 0;
			count = dao.doCount("MS_THMX", hql_sql.toString(), parameters1);
			if (count > 0) {
				res.put(Service.RES_CODE, 802);
				return;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * 产生数据后的验证
//	 * 
//	 * @param req
//	 * @param res
//	 * @param ctx
//	 * @throws ModelDataOperationException
//	 */
//	public void doSetDateAction(Map<String, Object> res, Context ctx)
//			throws ModelDataOperationException {
//		nowdate = new Date();
//	}

	// 取消汇总查询
	public void doQueryCancelCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构名称
		String userId = user.getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		parameters.put("czgh", userId);
		try {
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select distinct to_char(JZRQ,'yyyy-mm-dd hh24:mi:ss') as JZRQ,CZGH as CZGH from (SELECT JZRQ as JZRQ,CZGH as CZGH FROM MS_HZRB WHERE CZGH =:czgh and JGID =:jgid and HZRQ is null union all SELECT JZRQ as JZRQ,CZGH as CZGH FROM MS_GHRB WHERE CZGH =:czgh and JGID =:jgid and HZRQ is null) order by JZRQ desc",
							parameters);
			if (list.size() > 0) {
				res.put("body", list);
//				res.put("JZRQ", list.get(0).get("JZRQ"));
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询日报信息出错!");
		}

	}

	// 取消汇总查询
	public void doCancelCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构名称
		String userId = user.getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String jzrq = req.get("JZRQ") + "";
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date jzdate = null;
		try {
			jzdate = sdfdatetime.parse(jzrq);
		} catch (ParseException e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取消日报信息出错!");
		}
		parameters.put("jgid", jgid);
		parameters.put("czgh", userId);
		parameters.put("jzrq", jzdate);
		try {
			List<?> list = dao.doSqlQuery("SELECT JZRQ as JZRQ FROM MS_HZRB WHERE CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq and HZRQ is null union all SELECT JZRQ as JZRQ FROM MS_GHRB WHERE CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq and HZRQ is null", parameters);
			if (list.size() ==0){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "当前记录已汇总不能取消!");
			}
			Map<String, Object> jzmaxparameters = new HashMap<String, Object>();
			jzmaxparameters.put("jgid", jgid);
			jzmaxparameters.put("czgh", userId);
			List<Map<String,Object>> list1 = dao.doSqlQuery("select to_char(max(JZRQ),'yyyy-mm-dd hh24:mi:ss') as JZRQ from (SELECT JZRQ as JZRQ FROM MS_HZRB WHERE CZGH =:czgh and JGID =:jgid and HZRQ is null union all SELECT JZRQ as JZRQ FROM MS_GHRB WHERE CZGH =:czgh and JGID =:jgid and HZRQ is null)", jzmaxparameters);
			if (list1.size() >0){
				if(!jzrq.equals(list1.get(0).get("JZRQ")+"")){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "当前记录不是最后一条结账信息,不能取消!");
				}
			}
			dao.doUpdate("delete from MS_RBMX where CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq", parameters);
			dao.doUpdate("delete from MS_XZMX where CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq", parameters);
			dao.doUpdate("delete from MS_SFRB_FKMX where CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq", parameters);
			dao.doUpdate("delete from MS_HZRB where CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq", parameters);
			dao.doUpdate("delete from MS_GRMX where CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq", parameters);
			dao.doUpdate("delete from MS_GHRB_FKMX where CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq", parameters);
			dao.doUpdate("delete from MS_GHRB where CZGH =:czgh and JGID =:jgid and JZRQ=:jzrq", parameters);
			long mzlb = BSPHISUtil.getMZLB(jgid, dao);
			parameters.put("mzlb", mzlb);
			dao.doUpdate("update MS_MZXX set JZRQ = null where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh and JZRQ=:jzrq", parameters);
			dao.doUpdate("update MS_ZFFP set JZRQ = null where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh and JZRQ=:jzrq", parameters);
			dao.doUpdate("update MS_GHMX set JZRQ = null where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh and JZRQ=:jzrq", parameters);
			dao.doUpdate("update MS_THMX set JZRQ = null where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh and JZRQ=:jzrq", parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取消日报信息出错!");
		}

	}
	
	public void doCheckout(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		parameters.put("czgh", uid);
		try {
			long mzlb = BSPHISUtil.getMZLB(jgid, dao);
			Calendar cdate = Calendar.getInstance();
			Date jzdate = sdf.parse(req.get("jzrq")+"");
			cdate.setTime(jzdate);
			cdate.set(Calendar.MILLISECOND, 0);
			ChargesCheckout cck =  new ChargesCheckout();
			parameters.put("jzrq", cdate.getTime());
			parameters.put("mzlb", mzlb);

			//挂号明细
			StringBuffer hql_grmx = new StringBuffer(" SELECT (a.GHJE + a.ZLJE + a.ZJFY + a.BLJE ) as ZJJE,a.GHJE as GHJE,a.ZLJE as ZLJE,a.ZJFY as ZJFY,a.BLJE as BLJE,a.XJJE as XJJE,a.ZPJE as ZPJE,a.ZHJE as ZHJE,a.QTYS as QTYS,a.HBWC as HBWC,a.JZHM as FPHM,a.YZBZ as YZBZ,a.YZJM as YZJM," +
					"CASE WHEN FFFS=32 THEN ZPJE WHEN FFFS=39 THEN ZPJE ELSE 0 END AS WXJE," +
					"CASE WHEN FFFS=33 THEN ZPJE WHEN FFFS=40 THEN ZPJE ELSE 0 END AS ZFBJE,a.BRXZ as BRXZ,a.YHJE as YHJE from MS_GHMX a")
					.append(" where a.JGID =:jgid and a.JZRQ IS NULL and a.MZLB =:mzlb and a.CZGH=:czgh and a.CZSJ<=:jzrq order by a.JZHM");
			StringBuffer hql_grthmx = new StringBuffer(" SELECT (a.GHJE + a.ZLJE + a.ZJFY + a.BLJE ) as ZJJE,a.GHJE as GHJE,a.ZLJE as ZLJE,a.ZJFY as ZJFY,a.BLJE as BLJE,a.XJJE as XJJE,a.ZPJE as ZPJE,a.ZHJE as ZHJE,a.QTYS as QTYS,a.HBWC as HBWC,a.JZHM as FPHM,a.YZBZ as YZBZ,a.YZJM as YZJM," +
					"CASE WHEN FFFS=32 THEN ZPJE WHEN FFFS=39 THEN ZPJE ELSE 0 END AS WXJE," +
					"CASE WHEN FFFS=33 THEN ZPJE WHEN FFFS=40 THEN ZPJE ELSE 0 END AS ZFBJE,a.BRXZ as BRXZ,a.YHJE as YHJE from MS_GHMX a,MS_THMX b")
					.append(" where a.SBXH = b.SBXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB =:mzlb and b.CZGH=:czgh and b.THRQ<=:jzrq order by a.JZHM");
			List<Map<String, Object>> grmx_list = dao.doSqlQuery(hql_grmx.toString(), parameters);
			String jzhmxx = cck.getFPXX(grmx_list);
			List<Map<String, Object>> grthmx_list = dao.doSqlQuery(hql_grthmx.toString(), parameters);
			List<Map<String, Object>> thxx_list = cck.getFPList(grthmx_list);
			String thjzhmxx = cck.getFPXX(thxx_list);
			double ghhj = 0;
			double thhj = 0;
//			double thzf = 0;
//			double ghje = 0;
//			double zlje = 0;
//			double blje = 0;
//			double zjfy = 0;
			double ghxjhj = 0;
			double ghzphj = 0;
			double ghzhhj = 0;
			double ghqtyshj = 0;
			double ghhbwchj = 0;
			double ghzffshj=0;//挂号自费方式合计
			double ghybzfhj=0;//挂号医保自付合计
			double ghwxhj = 0;//挂号微信合计
			double ghzfbhj = 0;//挂号支付宝合计
			double ghyhhj = 0;//zhaojian 2019-05-12 增加优惠金额合计
			double yzjm = 0;
			for(int i = 0 ; i < grmx_list.size() ; i ++){
				Map<String,Object> grmx = grmx_list.get(i);
				if("1".equals(grmx.get("YZBZ")+"")){
					yzjm = BSHISUtil.doublesum(yzjm,BSPHISUtil.getDouble(grmx.get("YZJM"),2));
				}
				ghhj = BSHISUtil.doublesum(ghhj,BSPHISUtil.getDouble(grmx.get("ZJJE"),2));
//				ghje += BSPHISUtil.getDouble(grmx.get("GHJE"),2);
//				zlje += BSPHISUtil.getDouble(grmx.get("ZLJE"),2);
//				blje += BSPHISUtil.getDouble(grmx.get("BLJE"),2);
//				zjfy += BSPHISUtil.getDouble(grmx.get("ZJFY"),2);
				//ghxjhj = BSHISUtil.doublesum(ghxjhj,BSPHISUtil.getDouble(grmx.get("XJJE"),2))-yzjm;//add by Wangjl 2018-09-06如果是义诊挂号费减去义诊减免
				ghxjhj = BSHISUtil.doublesum(ghxjhj,BSPHISUtil.getDouble(grmx.get("XJJE"),2));
				ghzphj = BSHISUtil.doublesum(ghzphj,BSPHISUtil.getDouble(grmx.get("ZPJE"),2));
				ghzhhj = BSHISUtil.doublesum(ghzhhj,BSPHISUtil.getDouble(grmx.get("ZHJE"),2));
				ghqtyshj = BSHISUtil.doublesum(ghqtyshj,BSPHISUtil.getDouble(grmx.get("QTYS"),2));
				ghhbwchj = BSHISUtil.doublesum(ghhbwchj,BSPHISUtil.getDouble(grmx.get("HBWC"),2));
				double ghwxje =BSPHISUtil.getDouble(grmx.get("WXJE"),2);//微信金额
				double ghzfbje = BSPHISUtil.getDouble(grmx.get("ZFBJE"),2);//支付宝金额
				ghwxhj = BSHISUtil.doublesum(ghwxhj,ghwxje);//挂号微信合计
				ghzfbhj = BSHISUtil.doublesum(ghzfbhj,ghzfbje);//挂号支付宝合计
				//zhaojian 2019-05-12 增加优惠金额合计
				ghyhhj = BSHISUtil.doublesum(ghyhhj,BSPHISUtil.getDouble(grmx.get("YHJE"),2));
				Long brxz = Long.parseLong(grmx.get("BRXZ")+"");
				String brxzSql = "select XZDL as XZDL from GY_BRXZ where BRXZ=:BRXZ";
				Map<String, Object> brxzPara = new HashMap<String, Object>();
				brxzPara.put("BRXZ", brxz);
				List<Map<String, Object>> brxz_list = dao.doQuery(brxzSql, brxzPara);
				String xzdl = "";
				if(brxz_list.size()>0){
					if(brxz_list.get(0)!=null){
						Map<String,Object> brxzMap = brxz_list.get(0);
						xzdl = brxzMap.get("XZDL")!=null?brxzMap.get("XZDL")+"":"";
					}
				}
				if("1000".equals(grmx.get("BRXZ")+"")){
					//ghzffshj=BSHISUtil.doublesub(BSHISUtil.doublesum(ghzffshj,BSPHISUtil.getDouble(grmx.get("XJJE"),2)),BSHISUtil.doublesum(ghwxje,ghzfbje));
					ghzffshj=BSHISUtil.doublesum(ghzffshj,BSPHISUtil.getDouble(grmx.get("XJJE"),2));
				}
				if("3000".equals(grmx.get("BRXZ")+"") || "2000".equals(grmx.get("BRXZ")+"")){
					//ghybzfhj=BSHISUtil.doublesub(BSHISUtil.doublesum(ghybzfhj,BSPHISUtil.getDouble(grmx.get("XJJE"),2)),BSHISUtil.doublesum(ghwxje,ghzfbje));
					ghybzfhj=BSHISUtil.doublesum(ghybzfhj,BSPHISUtil.getDouble(grmx.get("XJJE"),2));
				}/*	else if("1".equals(xzdl)){//add by lizhi 2017-07-13根据病人性质大类判断医保病人
					ghybzfhj=BSPHISUtil.getDouble(ghybzfhj+ BSPHISUtil.getDouble(grmx.get("XJJE"),2),2);
				}*/
			}
			for(int i = 0 ; i < grthmx_list.size() ; i ++){
				Map<String,Object> grthmx = grthmx_list.get(i);
				//double yzjm = 0;
				if("1".equals(grthmx.get("YZBZ")+"")){
					yzjm = BSHISUtil.doublesub(yzjm,BSPHISUtil.getDouble(grthmx.get("YZJM"),2));
				}				
				ghhj = BSHISUtil.doublesub(ghhj,BSPHISUtil.getDouble(grthmx.get("ZJJE"),2));
				thhj = BSHISUtil.doublesum(thhj,BSPHISUtil.getDouble(grthmx.get("ZJJE"),2));
//				thzf += BSPHISUtil.getDouble(grthmx.get("XJJE"),2);
//				ghje -= BSPHISUtil.getDouble(grthmx.get("GHJE"),2);
//				zlje -= BSPHISUtil.getDouble(grthmx.get("ZLJE"),2);
//				blje -= BSPHISUtil.getDouble(grthmx.get("BLJE"),2);
//				zjfy -= BSPHISUtil.getDouble(grthmx.get("ZJFY"),2);
//				ghxjhj -= BSPHISUtil.getDouble(grthmx.get("XJJE"),2)-yzjm;//add by Wangjl 2018-09-06如果是义诊挂号费减去义诊减免
				ghxjhj = BSHISUtil.doublesub(ghxjhj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2));
				ghzphj = BSHISUtil.doublesub(ghzphj,BSPHISUtil.getDouble(grthmx.get("ZPJE"),2));
				ghzhhj = BSHISUtil.doublesub(ghzhhj,BSPHISUtil.getDouble(grthmx.get("ZHJE"),2));
				ghqtyshj = BSHISUtil.doublesub(ghqtyshj,BSPHISUtil.getDouble(grthmx.get("QTYS"),2));
				ghhbwchj = BSHISUtil.doublesub(ghhbwchj,BSPHISUtil.getDouble(grthmx.get("HBWC"),2));
				double ghwxje =BSPHISUtil.getDouble(grthmx.get("WXJE"),2);//挂号微信金额
				double ghzfbje = BSPHISUtil.getDouble(grthmx.get("ZFBJE"),2);//挂号支付宝金额
				ghwxhj = BSHISUtil.doublesub(ghwxhj,ghwxje);//挂号微信合计
				ghzfbhj = BSHISUtil.doublesub(ghzfbhj,ghzfbje);//挂号支付宝合计
				//zhaojian 2019-05-12 增加优惠金额合计
				ghyhhj = BSHISUtil.doublesub(ghyhhj,BSPHISUtil.getDouble(grthmx.get("YHJE"),2));
				Long brxz = Long.parseLong(grthmx.get("BRXZ")+"");
				String brxzSql = "select XZDL as XZDL from GY_BRXZ where BRXZ=:BRXZ";
				Map<String, Object> brxzPara = new HashMap<String, Object>();
				brxzPara.put("BRXZ", brxz);
				List<Map<String, Object>> brxz_list = dao.doQuery(brxzSql, brxzPara);
				String xzdl = "";
				if(brxz_list.size()>0){
					if(brxz_list.get(0)!=null){
						Map<String,Object> brxzMap = brxz_list.get(0);
						xzdl = brxzMap.get("XZDL")!=null?brxzMap.get("XZDL")+"":"";
					}
				}
				if("1000".equals(grthmx.get("BRXZ")+"")){
					//ghzffshj=BSHISUtil.doublesum(BSHISUtil.doublesub(ghzffshj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2)),BSHISUtil.doublesum(ghwxje,ghzfbje));
					ghzffshj=BSHISUtil.doublesub(ghzffshj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2));
				}
				if("3000".equals(grthmx.get("BRXZ")+"") || "2000".equals(grthmx.get("BRXZ")+"")){
					//ghybzfhj=BSHISUtil.doublesum(BSHISUtil.doublesub(ghybzfhj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2)),BSHISUtil.doublesum(ghwxje,ghzfbje));
					ghybzfhj=BSHISUtil.doublesub(ghybzfhj,BSPHISUtil.getDouble(grthmx.get("XJJE"),2));
				}/*else if("1".equals(xzdl)){//add by lizhi 2017-07-13根据病人性质大类判断医保病人
					ghybzfhj=BSPHISUtil.getDouble(ghybzfhj+ BSPHISUtil.getDouble(grthmx.get("XJJE"),2),2);
				}*/
			}
			if(grmx_list.size() > 0 || grthmx_list.size() > 0){
				Map<String, Object> ms_ghrb = new HashMap<String, Object>();
				ms_ghrb.put("CZGH",uid);
				ms_ghrb.put("JZRQ", cdate.getTime());
				ms_ghrb.put("JGID",jgid);
				ms_ghrb.put("QZPJ",jzhmxx);
				ms_ghrb.put("ZJJE", ghhj);
				ms_ghrb.put("XJJE", ghxjhj);
				ms_ghrb.put("ZPJE", ghzphj);
				ms_ghrb.put("ZHJE", ghzhhj);
				ms_ghrb.put("QTYS", ghqtyshj);
				ms_ghrb.put("HBWC", ghhbwchj);
				ms_ghrb.put("FPZS", grmx_list.size());
				ms_ghrb.put("MZLB", mzlb);
				ms_ghrb.put("THFP", thjzhmxx);
				ms_ghrb.put("THJE", thhj);
				ms_ghrb.put("THSL", grthmx_list.size());
				ms_ghrb.put("WXHJ", ghwxhj);//挂号微信合计
				ms_ghrb.put("ZFBHJ", ghzfbhj);//挂号支付宝合计
				ms_ghrb.put("ZFFSHJ", ghzffshj);//挂号自费现金合计
				ms_ghrb.put("YBZFHJ", ghybzfhj);//挂号医保自费合计
				ms_ghrb.put("YZJM", yzjm);
				//zhaojian 2019-05-12 增加优惠金额合计
				ms_ghrb.put("YHJE", ghyhhj);
				
				dao.doSave("create", BSPHISEntryNames.MS_GHRB, ms_ghrb,
						false);
				StringBuffer hql_grxzmx = new StringBuffer(" select c.BRXZ,sum(c.FPZS) as FPZS,sum(c.SFJE) as SFJE,sum(c.GHJE) as GHJE,sum(c.YZJM) as YZJM,sum(c.ZLJE) as ZLJE,sum(c.ZJFY) as ZJFY,sum(c.BLJE) as BLJE,sum(c.YHJE) as YHJE  from" )
				.append("(SELECT a.BRXZ as BRXZ,count(a.BRXZ) as FPZS,sum(a.GHJE + a.ZLJE + a.ZJFY + a.BLJE ) as SFJE,sum(a.GHJE) as GHJE,sum(a.YZJM) as YZJM,sum(a.ZLJE) as ZLJE,sum(a.ZJFY) as ZJFY,sum(a.BLJE) as BLJE,sum(a.YHJE) as YHJE from MS_GHMX a where a.JGID =:jgid and a.JZRQ IS NULL and a.MZLB =:mzlb and a.CZGH=:czgh and a.CZSJ<=:jzrq GROUP BY a.BRXZ")
				.append(" union all")
				.append(" select a.BRXZ as BRXZ, -count(a.BRXZ) as FPZS,-sum(a.GHJE + a.ZLJE + a.ZJFY + a.BLJE) as SFJE, -sum(a.GHJE) as GHJE,-sum(a.YZJM) as YZJM, -sum(a.ZLJE) as ZLJE,-sum(a.ZJFY) as ZJFY,-sum(a.BLJE) as BLJE,-sum(a.YHJE) as YHJEf from MS_GHMX a,ms_thmx b where a.sbxh=b.sbxh and a.JGID =:jgid and b.JZRQ IS NULL and a.MZLB =:mzlb and b.CZGH=:czgh  and b.thrq<=:jzrq GROUP BY a.BRXZ)c GROUP BY c.BRXZ");
				
				/*
				StringBuffer hql_grthxzmx = new StringBuffer(" SELECT a.BRXZ as BRXZ,count(a.BRXZ) as FPZS,sum(a.GHJE + a.ZLJE + a.ZJFY + a.BLJE ) as SFJE,(sum(a.GHJE)-sum(a.YZJM)) as GHJE,sum(a.ZLJE) as ZLJE,sum(a.ZJFY) as ZJFY,sum(a.BLJE) as BLJE from ")
						.append("MS_GHMX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB =:mzlb and b.CZGH=:czgh and b.THRQ<=:jzrq GROUP BY a.BRXZ");
				StringBuffer sql_grxzmx_all = new StringBuffer("select BRXZ as BRXZ,sum(FPZS) as FPZS,sum(SFJE) as SFJE,sum(GHJE) as GHJE,sum(ZLJE) as ZLJE,sum(ZJFY) as ZJFY,sum(BLJE) as BLJE from (").append(hql_grxzmx).append(" union all ").append(hql_grthxzmx).append(") group by BRXZ");
				List<Map<String, Object>> list_ghrbmx = dao.doSqlQuery(sql_grxzmx_all.toString(), parameters);*/
				List<Map<String, Object>> list_ghrbmx = dao.doSqlQuery(hql_grxzmx.toString(), parameters);
				if (list_ghrbmx.size() > 0) {
					for (Map<String, Object> map_rbmx : list_ghrbmx) {
						map_rbmx.put("CZGH", uid);
						map_rbmx.put("JZRQ", jzdate);
						map_rbmx.put("JGID", jgid);
						dao.doSave("create", BSPHISEntryNames.MS_GRMX, map_rbmx,
								false);
					}
				}
				StringBuffer hql_ghfkxx = new StringBuffer("select b.FKFS as FKFS,SUM(b.FKJE) as FKJE from MS_GHMX a,MS_GH_FKXX b")
				.append(" where b.SBXH = a.SBXH and  a.JGID = :jgid and a.JZRQ is null and a.MZLB = :mzlb and a.CZGH = :czgh and a.CZSJ<=:jzrq group by b.FKFS");
				StringBuffer hql_ghfkxx_zf = new StringBuffer("select b.FKFS as FKFS,-SUM(b.FKJE) as FKJE from MS_THMX a,MS_GH_FKXX b")
				.append(" where b.SBXH = a.SBXH and a.JGID = :jgid and a.JZRQ is null and a.MZLB = :mzlb and a.CZGH = :czgh and a.THRQ<=:jzrq group by b.FKFS");
				StringBuffer sql_ghfkxx_all = new StringBuffer("select FKFS as FKFS,sum(FKJE) as FKJE from (").append(hql_ghfkxx).append(" union all ").append(hql_ghfkxx_zf).append(") group by FKFS");
				List<Map<String, Object>> list_ghrb_fkmx = dao.doSqlQuery(sql_ghfkxx_all.toString(), parameters);
				if (list_ghrb_fkmx.size() > 0) {
					for (Map<String, Object> map_fkxx : list_ghrb_fkmx) {
						map_fkxx.put("CZGH", uid);
						map_fkxx.put("JZRQ", jzdate);
						map_fkxx.put("JGID", jgid);
						dao.doSave("create", BSPHISEntryNames.MS_GHRB_FKMX,
								map_fkxx, false);
					}
				}
			}
			
			
			//统计收费信息MS_MZXX
			StringBuffer hql_mzxx = new StringBuffer("select a.CZGH as CZGH,a.FPHM as FPHM," +
					" nvl(a.FPZS,1) as FPZS,a.ZJJE as ZJJE,a.XJJE as XJJE,a.ZPJE as ZPJE,a.QTYS as QTYS," +
					" a.ZHJE as ZHJE,a.JJZFJE as JJZFJE,a.HBWC as HBWC,a.BRXZ as BRXZ," +
					" a.ZFJE as ZFJE,CASE WHEN FFFS=32 THEN ZPJE  WHEN FFFS=39 THEN ZPJE ELSE 0 END AS WXJE," +
					"CASE WHEN FFFS=33 THEN ZPJE WHEN FFFS=40 THEN ZPJE ELSE 0 END AS ZFBJE," +
					"CASE WHEN FFFS=35 THEN ZPJE ELSE 0 END AS APPWXJE," +
					"CASE WHEN FFFS=36 THEN ZPJE ELSE 0 END AS APPZFBJE from MS_MZXX a")
					.append(" where a.JGID=:jgid and a.JZRQ IS NULL and a.MZLB =:mzlb and a.CZGH=:czgh" +
							" and a.SFRQ<=:jzrq order by a.FPHM");
			//统计收费作废信息MS_MZXX
			StringBuffer hql_mzxx_zf = new StringBuffer("select b.CZGH as CZGH,a.FPHM as FPHM," +
					" nvl(a.FPZS,1) as FPZS,a.ZJJE as ZJJE,a.XJJE as XJJE,a.ZPJE as ZPJE,a.QTYS as QTYS," +
					" a.ZHJE as ZHJE,a.JJZFJE as JJZFJE,a.HBWC as HBWC,a.BRXZ as BRXZ," +
					" a.ZFJE as ZFJE,CASE WHEN FFFS=32 THEN ZPJE  WHEN FFFS=39 THEN ZPJE ELSE 0 END AS WXJE," +
					"CASE WHEN FFFS=33 THEN ZPJE WHEN FFFS=40 THEN ZPJE ELSE 0 END AS ZFBJE," +
					"CASE WHEN FFFS=35 THEN ZPJE ELSE 0 END AS APPWXJE," +
					"CASE WHEN FFFS=36 THEN ZPJE ELSE 0 END AS APPZFBJE  from MS_MZXX a,")
					.append("MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.JZRQ IS NULL" +
							" and b.MZLB = :mzlb and b.CZGH =:czgh and b.ZFRQ<=:jzrq order by a.FPHM");
			List<Map<String, Object>> mzxx_list = dao.doSqlQuery(hql_mzxx.toString(), parameters);
			List<Map<String, Object>> fpxx_list = cck.getFPList(mzxx_list);
			String fpxx = cck.getFPXX(fpxx_list);
			List<Map<String, Object>> mzxx_zf_list = dao.doSqlQuery(hql_mzxx_zf.toString(), parameters);
			List<Map<String, Object>> zffpxx_list = cck.getFPList(mzxx_zf_list);
			String zffpxx = cck.getFPXX(zffpxx_list);
			double sfhj = 0;
			double xjhj = 0;
			double zphj = 0;
			double zhhj = 0;
			double qtyshj = 0;
			double hbwchj = 0;
			double jjzfhj = 0;
			double zfhj = 0;//作废
			double zffshj=0;//自费方式合计
			double nhzfhj=0;//农合自付合计
			double ybzfhj=0;//医保自付合计
			//double smze=0;//扫码总额
			double wxhj=0;//微信合计
			double zfbhj=0;//支付宝合计
			double appwxhj=0;//app微信合计
			double appzfbhj=0;//app支付宝合计

			for(int i = 0 ; i < mzxx_list.size() ; i ++){
				Map<String,Object> mzxx = mzxx_list.get(i);
				/***************add by lizhi 2017-07-13根据病人性质大类判断医保病人*****************/
				Long brxzL = Long.parseLong(mzxx.get("BRXZ")+"");
				String brxzSql = "select XZDL as XZDL from GY_BRXZ where BRXZ=:BRXZ";
				Map<String, Object> brxzPara = new HashMap<String, Object>();
				brxzPara.put("BRXZ", brxzL);
				List<Map<String, Object>> brxz_list = dao.doQuery(brxzSql, brxzPara);
				String xzdl = "";
				if(brxz_list.size()>0){
					if(brxz_list.get(0)!=null){
						Map<String,Object> brxzMap = brxz_list.get(0);
						xzdl = brxzMap.get("XZDL")!=null?brxzMap.get("XZDL")+"":"";
					}
				}
				/***************add by lizhi 2017-07-13根据病人性质大类判断医保病人*****************/
				sfhj = BSHISUtil.doublesum(sfhj,BSPHISUtil.getDouble(mzxx.get("ZJJE"),2));
				xjhj = BSHISUtil.doublesum(xjhj,BSPHISUtil.getDouble(mzxx.get("XJJE"),2));
				zphj = BSHISUtil.doublesum(zphj,BSPHISUtil.getDouble(mzxx.get("ZPJE"),2));
				zhhj = BSHISUtil.doublesum(zhhj,BSPHISUtil.getDouble(mzxx.get("ZHJE"),2));
				qtyshj = BSHISUtil.doublesum(qtyshj,BSPHISUtil.getDouble(mzxx.get("QTYS"),2));
				hbwchj = BSHISUtil.doublesum(hbwchj,BSPHISUtil.getDouble(mzxx.get("HBWC"),2));
				jjzfhj = BSHISUtil.doublesum(jjzfhj,BSPHISUtil.getDouble(mzxx.get("JJZFJE"),2));
				double wxje =BSPHISUtil.getDouble(mzxx.get("WXJE"),2);//微信金额
				double zfbje = BSPHISUtil.getDouble(mzxx.get("ZFBJE"),2);//支付宝金额
				double appwxje =BSPHISUtil.getDouble(mzxx.get("APPWXJE"),2);//app微信金额
				double appzfbje = BSPHISUtil.getDouble(mzxx.get("APPZFBJE"),2);//app支付宝金额
				wxhj = BSHISUtil.doublesum(wxhj,wxje);//微信合计
				zfbhj = BSHISUtil.doublesum(zfbhj,zfbje);//支付宝合计
				appwxhj = BSHISUtil.doublesum(appwxhj,appwxje);//app微信合计
				appzfbhj = BSHISUtil.doublesum(appzfbhj,appzfbje);//app支付宝合计
				String brxz=mzxx.get("BRXZ")+"";
				if("1000".equals(brxz)){
					//zffshj=BSHISUtil.doublesum(zffshj,BSPHISUtil.getDouble(mzxx.get("ZJJE"),2));
					zffshj=BSHISUtil.doublesub(BSHISUtil.doublesum(zffshj,BSPHISUtil.getDouble(mzxx.get("ZJJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					zffshj=BSHISUtil.doublesub(zffshj,BSHISUtil.doublesum(appwxje,appzfbje));//app
				}
				if("6000".equals(brxz)){
					//nhzfhj=BSHISUtil.doublesum(nhzfhj,BSPHISUtil.getDouble(mzxx.get("ZFJE"),2));
					nhzfhj=BSHISUtil.doublesub(BSHISUtil.doublesum(nhzfhj,BSPHISUtil.getDouble(mzxx.get("ZFJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					nhzfhj=BSHISUtil.doublesub(nhzfhj,BSHISUtil.doublesum(appwxje,appzfbje));//app
				}
				else if("3000".equals(brxz) || "2000".equals(brxz)){
					//ybzfhj=BSHISUtil.doublesum(ybzfhj,BSPHISUtil.getDouble(mzxx.get("ZFJE"),2));
					ybzfhj=BSHISUtil.doublesub(BSHISUtil.doublesum(ybzfhj,BSPHISUtil.getDouble(mzxx.get("ZFJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					ybzfhj=BSHISUtil.doublesub(ybzfhj,BSHISUtil.doublesum(appwxje,appzfbje));//app
				}else if("1".equals(xzdl)){//add by lizhi 2017-07-13根据病人性质大类判断医保病人
					//ybzfhj=BSHISUtil.doublesum(ybzfhj,BSPHISUtil.getDouble(mzxx.get("ZFJE"),2));
					ybzfhj=BSPHISUtil.getDouble(ybzfhj+ BSPHISUtil.getDouble(mzxx.get("XJJE"),2),2);
				}
			}
			for(int i = 0 ; i < mzxx_zf_list.size() ; i ++){
				Map<String,Object> mzxx_zf = mzxx_zf_list.get(i);
				/***************add by lizhi 2017-07-13根据病人性质大类判断医保病人*****************/
				Long brxzL = Long.parseLong(mzxx_zf.get("BRXZ")+"");
				String brxzSql = "select XZDL as XZDL from GY_BRXZ where BRXZ=:BRXZ";
				Map<String, Object> brxzPara = new HashMap<String, Object>();
				brxzPara.put("BRXZ", brxzL);
				List<Map<String, Object>> brxz_list = dao.doQuery(brxzSql, brxzPara);
				String xzdl = "";
				if(brxz_list.size()>0){
					if(brxz_list.get(0)!=null){
						Map<String,Object> brxzMap = brxz_list.get(0);
						xzdl = brxzMap.get("XZDL")!=null?brxzMap.get("XZDL")+"":"";
					}
				}
				/***************add by lizhi 2017-07-13根据病人性质大类判断医保病人*****************/
				sfhj = BSHISUtil.doublesub(sfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZJJE"),2));
				xjhj = BSHISUtil.doublesub(xjhj,BSPHISUtil.getDouble(mzxx_zf.get("XJJE"),2));
				zphj = BSHISUtil.doublesub(zphj,BSPHISUtil.getDouble(mzxx_zf.get("ZPJE"),2));
				zhhj = BSHISUtil.doublesub(zhhj,BSPHISUtil.getDouble(mzxx_zf.get("ZHJE"),2));
				qtyshj = BSHISUtil.doublesub(qtyshj,BSPHISUtil.getDouble(mzxx_zf.get("QTYS"),2));
				hbwchj = BSHISUtil.doublesub(hbwchj,BSPHISUtil.getDouble(mzxx_zf.get("HBWC"),2));
				jjzfhj = BSHISUtil.doublesub(jjzfhj,BSPHISUtil.getDouble(mzxx_zf.get("JJZFJE"),2));
				zfhj = BSHISUtil.doublesum(zfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZJJE"),2));
				double wxje =BSPHISUtil.getDouble(mzxx_zf.get("WXJE"),2);//微信金额
				double zfbje = BSPHISUtil.getDouble(mzxx_zf.get("ZFBJE"),2);//支付宝金额
				double appwxje =BSPHISUtil.getDouble(mzxx_zf.get("APPWXJE"),2);//app微信金额
				double appzfbje = BSPHISUtil.getDouble(mzxx_zf.get("APPZFBJE"),2);//app支付宝金额
				wxhj = BSHISUtil.doublesub(wxhj,wxje);//微信合计
				zfbhj = BSHISUtil.doublesub(zfbhj,zfbje);//支付宝合计
				appwxhj = BSHISUtil.doublesub(appwxhj,appwxje);//app微信合计
				appzfbhj = BSHISUtil.doublesub(appzfbhj,appzfbje);//app支付宝合计
				if("1000".equals(mzxx_zf.get("BRXZ")+"")){
					//zffshj=BSHISUtil.doublesub(zffshj,BSPHISUtil.getDouble(mzxx_zf.get("ZJJE"),2));
					zffshj=BSHISUtil.doublesum(BSHISUtil.doublesub(zffshj,BSPHISUtil.getDouble(mzxx_zf.get("ZJJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					zffshj=BSHISUtil.doublesum(zffshj,BSHISUtil.doublesum(appwxje,appzfbje));//app
				}
				if("6000".equals(mzxx_zf.get("BRXZ")+"")){
					//nhzfhj=BSHISUtil.doublesub(nhzfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2));
					nhzfhj=BSHISUtil.doublesum(BSHISUtil.doublesub(nhzfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					nhzfhj=BSHISUtil.doublesum(nhzfhj,BSHISUtil.doublesum(appwxje,appzfbje));//app
				}
				if("3000".equals(mzxx_zf.get("BRXZ")+"") || "2000".equals(mzxx_zf.get("BRXZ")+"")){
					//ybzfhj=BSHISUtil.doublesub(ybzfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2));
					ybzfhj=BSHISUtil.doublesum(BSHISUtil.doublesub(ybzfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2)),BSHISUtil.doublesum(wxje,zfbje));
					ybzfhj=BSHISUtil.doublesum(ybzfhj,BSHISUtil.doublesum(appwxje,appzfbje));//app
				}else if("1".equals(xzdl)){//add by lizhi 2017-07-13根据病人性质大类判断医保病人
					//ybzfhj=BSHISUtil.doublesub(ybzfhj,BSPHISUtil.getDouble(mzxx_zf.get("ZFJE"),2));
					ybzfhj=BSPHISUtil.getDouble(ybzfhj+ BSPHISUtil.getDouble(mzxx_zf.get("XJJE"),2),2);
				}
			}
			//zffshj = zffshj - wxhj - zfbhj;//移动支付
			if(mzxx_list.size()>0 || mzxx_zf_list.size()>0){
				Map<String, Object> ms_hzrb = new HashMap<String, Object>();
				ms_hzrb.put("CZGH",uid);
				ms_hzrb.put("JZRQ", cdate.getTime());
				ms_hzrb.put("JGID",jgid);
				ms_hzrb.put("QZPJ",fpxx);
				ms_hzrb.put("ZJJE", sfhj);
				ms_hzrb.put("XJJE", xjhj);
				ms_hzrb.put("ZPJE", zphj);
				ms_hzrb.put("ZFZF", zhhj);
				ms_hzrb.put("QTYS", qtyshj);
				ms_hzrb.put("HBWC", hbwchj);
				ms_hzrb.put("FPZS", fpxx_list.size());
				ms_hzrb.put("MZLB", mzlb);
				ms_hzrb.put("ZFFP", zffpxx);
				ms_hzrb.put("ZFJE", zfhj);
				ms_hzrb.put("ZFZS", zffpxx_list.size());
				ms_hzrb.put("JJZFJE",jjzfhj);
				ms_hzrb.put("ZFFSHJ",zffshj);
				ms_hzrb.put("NHZFHJ",nhzfhj);
				ms_hzrb.put("YBZFHJ",ybzfhj);
				ms_hzrb.put("WXHJ",wxhj);//微信
				ms_hzrb.put("ZFBHJ",zfbhj);//支付宝
				ms_hzrb.put("APPWXHJ",appwxhj);//app微信
				ms_hzrb.put("APPZFBHJ",appzfbhj);//app支付宝

				StringBuffer sql_fkfs = new StringBuffer("select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from (")
					.append("select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b where a.MZXH = b.MZXH and b.JGID = :jgid and b.JZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.SFRQ<=:jzrq")
					.append(" union all ")
					.append("select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID = :jgid and b.JZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.ZFRQ<=:jzrq")
					.append(" union all ")
					.append("select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.JZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.CZSJ<=:jzrq")
					.append(" union all ")
					.append("select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.JZRQ is null and b.MZLB = :mzlb and b.CZGH = :czgh and b.THRQ<=:jzrq")
					.append(") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS");
				StringBuffer sql_brxz = new StringBuffer("select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,nvl(d.DBPB,0) as DBPB from (")
					.append("select BRXZ as BRXZ,QTYS as QTYS from MS_MZXX where JGID=:jgid and JZRQ IS NULL and MZLB = :mzlb and CZGH = :czgh and SFRQ<=:jzrq")
					.append(" union all ")
					.append("select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB = :mzlb and b.CZGH = :czgh and b.ZFRQ<=:jzrq")
					.append(" union all ")
					.append("select BRXZ as BRXZ,QTYS as QTYS from MS_GHMX where JGID=:jgid and JZRQ IS NULL and MZLB = :mzlb and CZGH = :czgh and CZSJ<=:jzrq")
					.append(" union all ")
					.append("select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB = :mzlb and b.CZGH = :czgh and b.THRQ<=:jzrq")
					.append(") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB");
				List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs.toString(),parameters);
				List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz.toString(),parameters);
				String  qtysFb="";
				String jzjeSt="0.00";
				double ybjz=0;//医保记账
				double nhjz=0;//农合记账
				if (ids_fkfs  != null && ids_fkfs .size() != 0) {
					 for(int j=0;j<ids_fkfs.size();j++){
							 qtysFb = qtysFb +ids_fkfs.get(j).get("FKMC")+ ":"
									+ String.format("%1$.2f",ids_fkfs.get(j).get("FKJE"))
									+ " ";
					 }
				}
				ybzfhj = BSHISUtil.doublesum(ybzfhj,ghybzfhj);
				zffshj = BSHISUtil.doublesum(zffshj,ghzffshj);
				qtysFb = qtysFb+"农合自付："+String.format("%1$.2f",nhzfhj)+" "+"医保自付："+String.format("%1$.2f",ybzfhj)+" "+"自费自付："+String.format("%1$.2f",zffshj)+" ";
				if (ids_brxz  != null && ids_brxz .size() != 0) {
					 for(int j=0;j<ids_brxz.size();j++){
						 if("3000".equals(ids_brxz.get(j).get("BRXZ")+"") ||"2000".equals(ids_brxz.get(j).get("BRXZ")+"")){
							ybjz+=BSPHISUtil.getDouble(ids_brxz.get(j).get("QTYS"),2);
							qtysFb = qtysFb +ids_brxz.get(j).get("XZMC");
/*							if("2000".equals(ids_brxz.get(j).get("BRXZ")+"")){
								qtysFb+="金保记账:";
							}else{*/
								qtysFb+="记账:";
							//}
							qtysFb+=String.format("%1$.2f",parseDouble(ids_brxz.get(j).get("QTYS")+ ""))+ " ";
						 }
						 else if("6000".equals(ids_brxz.get(j).get("BRXZ")+"")){
							 nhjz+=BSPHISUtil.getDouble(ids_brxz.get(j).get("QTYS"),2);
							 qtysFb = qtysFb +ids_brxz.get(j).get("XZMC")+ "记账:"
										+ String.format("%1$.2f",parseDouble(ids_brxz.get(j).get("QTYS")+ ""))
										+ " ";
						 }
						 else{
							 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +parseDouble(ids_brxz.get(j).get("QTYS")+" "));
						 }
					 }
					 qtysFb = qtysFb+"南京金保账户 :"+String.format("%1$.2f",BSHISUtil.doublesum(zhhj,ghzhhj))+" ";
					 qtysFb = qtysFb+"其他记账 :"+jzjeSt+" ";
				}
				ms_hzrb.put("YSQTFB", qtysFb);
				ms_hzrb.put("NHJZ",nhjz);
				ms_hzrb.put("YBJZ",qtyshj);
				ms_hzrb.put("JZJEST",BSPHISUtil.getDouble(jzjeSt,2));
				ms_hzrb.put("JZZE",BSPHISUtil.getDouble(nhjz+qtyshj+parseDouble(jzjeSt),2));//记账总额
				dao.doSave("create", BSPHISEntryNames.MS_HZRB, ms_hzrb,false);
//				StringBuffer hql_sfmx = new StringBuffer();// 统计收费明细MS_SFMX
//				StringBuffer hql_sfmx_zf = new StringBuffer();// 统计收费作废明细MS_SFMX
				StringBuffer hql_sfmx = new StringBuffer("select b.SFXM as SFXM,b.ZJJE as ZJJE from MS_MZXX a,MS_SFMX b")
				.append(" where a.MZXH = b.MZXH and a.JGID=:jgid and a.JZRQ IS NULL and a.MZLB = :mzlb and a.CZGH = :czgh and a.SFRQ<=:jzrq");
				StringBuffer hql_sfmx_zf = new StringBuffer("select b.SFXM as SFXM,-b.ZJJE as ZJJE from MS_ZFFP a,MS_SFMX b")
				.append(" where a.MZXH = b.MZXH and a.JGID=:jgid and a.JZRQ IS NULL and a.MZLB = :mzlb and a.CZGH = :czgh and a.ZFRQ<=:jzrq");
				StringBuffer sql_sfmx_all = new StringBuffer("select c.SFXM as SFXM,c.SFMC as SFMC,sum(ZJJE) as SFJE from (").append(hql_sfmx).append(" union all ").append(hql_sfmx_zf).append(")t,GY_SFXM c where t.SFXM = c.SFXM group by c.SFMC,c.SFXM");
				List<Map<String, Object>> list_rbmx = dao.doSqlQuery(sql_sfmx_all.toString(), parameters);
				if (list_rbmx.size() > 0) {
					for (Map<String, Object> map_rbmx : list_rbmx) {
						map_rbmx.put("CZGH", uid);
						map_rbmx.put("JZRQ", jzdate);
						map_rbmx.put("JGID", jgid);
						dao.doSave("create", BSPHISEntryNames.MS_RBMX, map_rbmx,
								false);
					}
				}
//				StringBuffer hql_xzmx = new StringBuffer();// 统计性质明细MS_SFMX
//				StringBuffer hql_xzmx_zf = new StringBuffer();// 统计性质作废明细MS_SFMX
				StringBuffer hql_xzmx = new StringBuffer("select a.BRXZ as BRXZ,sum(nvl(a.FPZS,1)) as FPZS,sum(a.ZJJE) as ZJJE from ")
						.append("MS_MZXX a where a.JGID=:jgid and a.JZRQ IS NULL and a.MZLB = :mzlb and a.CZGH = :czgh and a.SFRQ<=:jzrq group by a.BRXZ");
				StringBuffer hql_xzmx_zf = new StringBuffer("select a.BRXZ as BRXZ,sum(nvl(a.FPZS,1)) as FPZS,sum(a.ZJJE) as ZJJE from MS_MZXX a,")
						.append("MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.JZRQ IS NULL and b.MZLB = :mzlb and b.CZGH = :czgh and b.ZFRQ<=:jzrq group by a.BRXZ");
				StringBuffer sql_xzmx_all = new StringBuffer("select BRXZ as BRXZ,sum(FPZS) as FPZS,sum(ZJJE) as SFJE from (").append(hql_xzmx).append(" union all ").append(hql_xzmx_zf).append(") group by BRXZ");
				List<Map<String, Object>> list_xzmx = dao.doSqlQuery(sql_xzmx_all.toString(), parameters);
				if (list_xzmx.size() > 0) {
					for (Map<String, Object> map_xzmx : list_xzmx) {
						map_xzmx.put("CZGH", uid);
						map_xzmx.put("JZRQ", jzdate);
						map_xzmx.put("JGID", jgid);
						dao.doSave("create", BSPHISEntryNames.MS_XZMX, map_xzmx,
								false);
					}
				}
//				StringBuffer hql_fkxx = new StringBuffer();// 统计性质明细MS_SFMX
//				StringBuffer hql_fkxx_zf = new StringBuffer();// 统计性质作废明细MS_SFMX
				StringBuffer hql_fkxx = new StringBuffer("select b.FKFS as FKFS,SUM(b.FKJE) as FKJE from MS_MZXX a,MS_FKXX b")
				.append(" where b.MZXH = a.MZXH and a.JGID = :jgid and a.JZRQ is null and a.MZLB = :mzlb and a.CZGH = :czgh and a.SFRQ<=:jzrq group by b.FKFS");
				StringBuffer hql_fkxx_zf = new StringBuffer("select b.FKFS as FKFS,SUM(b.FKJE) as FKJE from MS_ZFFP a,MS_FKXX b")
				.append(" where b.MZXH = a.MZXH and a.JGID = :jgid and a.JZRQ is null and a.MZLB = :mzlb and a.CZGH = :czgh and a.ZFRQ<=:jzrq group by b.FKFS");
				StringBuffer sql_fkxx_all = new StringBuffer("select FKFS as FKFS,sum(FKJE) as FKJE from (").append(hql_fkxx).append(" union all ").append(hql_fkxx_zf).append(") group by FKFS");
				List<Map<String, Object>> list_sfrb_fkmx = dao.doSqlQuery(sql_fkxx_all.toString(), parameters);
				if (list_sfrb_fkmx.size() > 0) {
					for (Map<String, Object> map_fkxx : list_sfrb_fkmx) {
						map_fkxx.put("CZGH", uid);
						map_fkxx.put("JZRQ", jzdate);
						map_fkxx.put("JGID", jgid);
						dao.doSave("create", BSPHISEntryNames.MS_SFRB_FKMX,
								map_fkxx, false);
					}
				}
			}
			dao.doUpdate("update MS_MZXX set JZRQ = :jzrq where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh and JZRQ is null and SFRQ<=:jzrq",parameters);
			dao.doUpdate("update MS_ZFFP set JZRQ = :jzrq where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh and JZRQ is null and ZFRQ<=:jzrq",parameters);
			dao.doUpdate("update MS_GHMX set JZRQ = :jzrq where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh and JZRQ is null and CZSJ<=:jzrq",parameters);
			dao.doUpdate("update MS_THMX set JZRQ = :jzrq where JGID=:jgid and MZLB =:mzlb and CZGH = :czgh and JZRQ is null and THRQ<=:jzrq",parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
}
