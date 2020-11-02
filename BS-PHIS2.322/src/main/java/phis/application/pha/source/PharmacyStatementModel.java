package phis.application.pha.source;

import java.text.DecimalFormat;
import java.text.ParseException;
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
import ctd.validator.ValidateException;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;

/**
 * 药房月终过账
 * 
 * @author caijy
 * 
 */
public class PharmacyStatementModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyStatementModel.class);

	public PharmacyStatementModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 药品月终过账时间查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryMedicinesAccountingStatementDate(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer hql_date_last = new StringBuffer();// 取最后一次月结时间
		hql_date_last
				.append("select max(ZZSJ) as ZZSJ from YF_JZJL  where YFSB=:yfsb and CKBH=0");
		Map<String, Object> map_date = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("yfsb", yfsb);
		try {
			Date date_begin = (Date) (dao.doLoad(hql_date_last.toString(),
					parameters).get("ZZSJ"));
			map_date.put("QSSJ", date_begin);
			int last_yj_year = Integer.parseInt(new SimpleDateFormat("yyyy")
					.format(date_begin));// 上次月结的年份
			int last_yj_month = Integer.parseInt(new SimpleDateFormat("MM")
					.format(date_begin));// 上次月结的月份
			int yjDate=32;
			try{
			yjDate = Integer.parseInt(ParameterUtil.getParameter(jgid,
					"YJSJ_YF" + yfsb,
					BSPHISSystemArgument.defaultValue.get("YJSJ_YF"),
					BSPHISSystemArgument.defaultAlias.get("YJSJ_YF"),BSPHISSystemArgument.defaultCategory.get("YJSJ_YF"), ctx));// 月结日
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, "YJSJ_YF" + yfsb, e);
			}
			Calendar a = Calendar.getInstance();
			a.set(Calendar.YEAR, last_yj_year);
			a.set(Calendar.MONTH, last_yj_month - 1);
			a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
			a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
			int lastDate = a.get(Calendar.DATE);// 上次月结月的最后一天
			int yj_day = yjDate;
			if (yjDate > lastDate) {
				yj_day = lastDate;
			}
			a.set(Calendar.DATE, yj_day);
			a.set(Calendar.HOUR_OF_DAY, 23);
			a.set(Calendar.MINUTE, 59);
			a.set(Calendar.SECOND, 59);
			Date date_end = a.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				date_end = sdf.parse(sdf.format(date_end));
			} catch (ParseException e) {
				MedicineUtils.throwsException(logger, "月结时间查询失败", e);
			}
			// 月结日小于上次月结日
			if (date_begin.getTime() >= date_end.getTime()) {
				a.set(Calendar.MONTH, last_yj_month);// 把日期设置为月结日的下一个月
				a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
				a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
				lastDate = a.get(Calendar.DATE);// 上次月结月的下个月的最后一天
				if (yjDate > lastDate) {
					yj_day = lastDate;
				}
				a.set(Calendar.DATE, yj_day);
				// 上次月结的下个月月结日大于当前日期
				if (a.getTime().getTime() > new Date().getTime()) {
					ret.put("code", ServiceCode.CODE_ERROR);
					ret.put("msg", "该月已月结");
					map_date.put("ZZSJ", new Date());
					ret.put("body", map_date);
					return ret;
				}
				map_date.put("ZZSJ", a.getTime());
				ret.put("code", ServiceCode.CODE_OK);
				ret.put("msg", "ok");
				ret.put("body", map_date);
				return ret;
			}
			// 月结日大于当前日期
			if (date_end.getTime() > new Date().getTime()) {
				ret.put("code", ServiceCode.CODE_ERROR);
				ret.put("msg", "未到月结时间");
				map_date.put("ZZSJ", new Date());
				ret.put("body", map_date);
				return ret;
			}
			map_date.put("ZZSJ", date_end);
			ret.put("code", ServiceCode.CODE_OK);
			ret.put("msg", "ok");
			ret.put("body", map_date);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "月结时间查询失败", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 月结
	 * @updateInfo update by caijy at 2015-08-28 for提升运行速度
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveMedicinesAccountingStatementDate(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date_begin = null;
		Date date_end = null;
		try {
			date_begin = sdf.parse((String) body.get("QSSJ"));
			date_end = sdf.parse((String) body.get("ZZSJ"));
			body.put("QSSJ", date_begin);
			body.put("ZZSJ", date_end);
		} catch (ParseException e1) {
			MedicineUtils.throwsException(logger, "日期转换失败", e1);
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		body.put("YFSB", yfsb);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("yfsb", yfsb);
		parameters.put("begin", date_begin);
		parameters.put("end", date_end);
		StringBuffer hql_count_rk = new StringBuffer();
		hql_count_rk
				.append("RKPB =0 and YFSB=:yfsb and RKRQ>:begin and RKRQ<:end");
		StringBuffer hql_count_ck = new StringBuffer();
		hql_count_ck
				.append("CKPB =0 and YFSB=:yfsb and CKRQ>:begin and CKRQ<:end");
		DecimalFormat df_four = new DecimalFormat("###.0000");
		long l = 0;
		try {
			// 判断能否月结
			l = dao.doCount("YF_RK01", hql_count_rk.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("有入库单未执行");
			}
			l = dao.doCount("YF_CK01", hql_count_ck.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("有出库单未执行");
			}
			// 开始月结数据计算
			StringBuffer hql_yfyp = new StringBuffer();// 统计药房药品
			Map<String, Object> map_par_yp = new HashMap<String, Object>();
			StringBuffer hql_syjc = new StringBuffer();// 统计上月结存
			StringBuffer hql_rksl = new StringBuffer();// 统计入库数量
			StringBuffer hql_cksl = new StringBuffer();// 统计出库数量
			StringBuffer hql_fysl = new StringBuffer();// 统计发药数量
			StringBuffer hql_tjje = new StringBuffer();// 统计调价金额
			/*hql_yfyp.append("select jg.YPCD as YPCD,jg.YPXH as YPXH,0.0000 as QCSL,0.0000 as RKSL,0.0000 as CKSL,0.0000 as QMSL,yf.YFBZ as YFBZ,yp.ZXBZ as ZXBZ,yp.YPGG as YPGG,yf.YFDW as YFDW,(jg.LSJG/yp.ZXBZ)*yf.YFBZ as LSJG,(jg.PFJG/yp.ZXBZ)*yf.YFBZ as PFJG,(jg.JHJG/yp.ZXBZ)*yf.YFBZ as JHJG,0.0000 as LSJE,0.0000 as PFJE,0.0000 as JHJE  from YK_TYPK yp,YF_YPXX yf,YK_CDXX jg where yp.YPXH=yf.YPXH and jg.YPXH=yf.YPXH and yf.JGID=jg.JGID and yf.YFSB=:yfsb");
			hql_syjc.append("select a.KCSL as KCSL,a.LSJG as LSJG,a.YFBZ as YFBZ,a.LSJE as LSJE,a.PFJE as PFJE,a.JHJE as JHJE from YF_YJJG a,YF_JZJL b where a.YFSB=b.YFSB and a.CWYF=b.CWYF and a.YFSB=:yfsb and a.YPCD=:ypcd and a.YPXH=:ypxh and b.ZZSJ=:zzsj");
			hql_rksl.append("select sum(b.RKSL) as RKSL,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE,sum(b.JHJE) as JHJE,b.LSJG as LSJG,b.YFBZ as YFBZ from YF_RK01 a,YF_RK02 b  where a.YFSB =b.YFSB and a.RKFS =b.RKFS  and a.RKDH =b.RKDH and a.RKPB =1 and a.RKRQ >:begin and a.RKRQ <=:end and a.YFSB=:yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd group by b.LSJG,b.YFBZ");
			hql_cksl.append("select sum(b.CKSL) as CKSL,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE,sum(b.JHJE) as JHJE,b.LSJG as LSJG,b.YFBZ as YFBZ from YF_CK01 a,YF_CK02 b  where a.YFSB =b.YFSB and a.CKFS =b.CKFS  and a.CKDH =b.CKDH and a.CKPB =1 and a.CKRQ >:begin and a.CKRQ <=:end and a.YFSB=:yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd  group by b.LSJG,b.YFBZ");
			hql_fysl.append("select sum(YPSL) as YPSL,YFBZ as YFBZ,LSJG as LSJG,PFJG as PFJG,JHJG as JHJG,sum(LSJE) as LSJE ,sum(PFJE) as PFJE,sum(JHJE) as JHJE from YF_MZFYMX  where YFSB=:yfsb and FYRQ >:begin and FYRQ <=:end and YPXH=:ypxh and YPCD=:ypcd group by LSJG,PFJG,YFBZ,JHJG");
			hql_tjje.append("select XLSE-YLSE as LSJE ,XPFE-YPFE as PFJE ,XJHE-YJHE as JHJE from YF_TJJL  where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJRQ>:begin and TJRQ<=:end");
			*/
			hql_yfyp.append("select jg.YPCD as YPCD,jg.YPXH as YPXH,0.0000 as QCSL,0.0000 as RKSL,0.0000 as CKSL,0.0000 as QMSL,yf.YFBZ as YFBZ,yp.ZXBZ as ZXBZ,yp.YPGG as YPGG,yf.YFDW as YFDW,(jg.LSJG/yp.ZXBZ)*yf.YFBZ as LSJG,(jg.PFJG/yp.ZXBZ)*yf.YFBZ as PFJG,(jg.JHJG/yp.ZXBZ)*yf.YFBZ as JHJG,0.0000 as LSJE,0.0000 as PFJE,0.0000 as JHJE  from YK_TYPK yp,YF_YPXX yf,YK_CDXX jg where yp.YPXH=yf.YPXH and jg.YPXH=yf.YPXH and yf.JGID=jg.JGID and yf.YFSB=:yfsb");
			hql_syjc.append("select a.YPXH as YPXH,a.YPCD as YPCD ,a.KCSL as KCSL,a.LSJG as LSJG,a.YFBZ as YFBZ,a.LSJE as LSJE,a.PFJE as PFJE,a.JHJE as JHJE from YF_YJJG a,YF_JZJL b where a.YFSB=b.YFSB and a.CWYF=b.CWYF and a.YFSB=:yfsb and b.ZZSJ=:zzsj");
			hql_rksl.append("select b.YPXH as YPXH,b.YPCD as YPCD,sum(b.RKSL) as RKSL,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE,sum(b.JHJE) as JHJE,b.LSJG as LSJG,b.YFBZ as YFBZ from YF_RK01 a,YF_RK02 b  where a.YFSB =b.YFSB and a.RKFS =b.RKFS  and a.RKDH =b.RKDH and a.RKPB =1 and a.RKRQ >:begin and a.RKRQ <=:end and a.YFSB=:yfsb group by b.LSJG,b.YFBZ,b.YPXH,b.YPCD");
			hql_cksl.append("select b.YPXH as YPXH,b.YPCD as YPCD ,sum(b.CKSL) as CKSL,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE,sum(b.JHJE) as JHJE,b.LSJG as LSJG,b.YFBZ as YFBZ from YF_CK01 a,YF_CK02 b  where a.YFSB =b.YFSB and a.CKFS =b.CKFS  and a.CKDH =b.CKDH and a.CKPB =1 and a.CKRQ >:begin and a.CKRQ <=:end and a.YFSB=:yfsb  group by b.LSJG,b.YFBZ,b.YPXH,b.YPCD");
			hql_fysl.append("select YPXH as YPXH,YPCD as YPCD,sum(YPSL) as YPSL,YFBZ as YFBZ,LSJG as LSJG,PFJG as PFJG,JHJG as JHJG,sum(LSJE) as LSJE ,sum(PFJE) as PFJE,sum(JHJE) as JHJE from YF_MZFYMX  where YFSB=:yfsb and FYRQ >:begin and FYRQ <=:end  group by LSJG,PFJG,YFBZ,JHJG,YPXH,YPCD");
			hql_tjje.append("select YPXH as YPXH,YPCD as YPCD,XLSE-YLSE as LSJE ,XPFE-YPFE as PFJE ,XJHE-YJHE as JHJE from YF_TJJL  where  YFSB=:yfsb and TJRQ>:begin and TJRQ<=:end");
			StringBuffer hql_yk = new StringBuffer();// 统计盈盈亏数据
			hql_yk.append("select b.YPXH as YPXH,b.YPCD as YPCD,sum(SPSL-PQSL) as YKSL,sum(XLSE-YLSE) as LSJE,sum(XPFE-YPFE) as PFJE,sum(XJHE-YJHE) as JHJE,b.YFBZ as YFBZ,b.LSJG as LSJG from YF_YK01 a,YF_YK02 b where a.YFSB=b.YFSB and a.CKBH=b.CKBH and a.PDDH=b.PDDH and a.PDWC=1 and a.WCRQ >:begin and a.WCRQ <=:end and a.YFSB=:yfsb  group by b.LSJG,b.YFBZ,b.YPXH,b.YPCD ");
			StringBuffer hql_bqfy = new StringBuffer();// 统计病区发药
			hql_bqfy.append("select YPXH as YPXH,YPCD as YPCD,sum(YPSL) as YPSL,YFBZ as YFBZ,LSJG as LSJG,PFJG as PFJG,JHJG as JHJG,sum(LSJE) as LSJE ,sum(PFJE) as PFJE,sum(JHJE) as JHJE from YF_ZYFYMX where YFSB=:yfsb and JFRQ >:begin and JFRQ <=:end  group by LSJG,PFJG,YFBZ,JHJG,YPXH,YPCD");
			StringBuffer hql_jcfy = new StringBuffer();// 统计家床发药
			hql_jcfy.append("select YPXH as YPXH,YPCD as YPCD ,sum(YPSL) as YPSL,YFBZ as YFBZ,LSJG as LSJG,PFJG as PFJG,JHJG as JHJG,sum(LSJE) as LSJE ,sum(PFJE) as PFJE,sum(JHJE) as JHJE from YF_JCFYMX where YFSB=:yfsb and JFRQ >:begin and JFRQ <=:end  group by LSJG,PFJG,YFBZ,JHJG,YPXH,YPCD");
			StringBuffer hql_dbrk = new StringBuffer();// 统计调拨入库
			hql_dbrk.append("select b.YPXH as YPXH,b.YPCD as YPCD,sum(b.QRSL) as RKSL,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE,sum(b.JHJE) as JHJE,b.LSJG as LSJG,b.YFBZ as YFBZ from YF_DB01  a,YF_DB02  b  where  a.SQYF = b.SQYF and a.SQDH = b.SQDH  and a.RKBZ = 1 and a.SQYF=:yfsb and a.RKRQ >:begin and a.RKRQ <=:end  group by b.LSJG,b.YFBZ,b.YPXH,b.YPCD");
			StringBuffer hql_dbck = new StringBuffer();// 统计调拨出库
			hql_dbck.append("select b.YPXH as YPXH,b.YPCD as YPCD,sum(b.QRSL) as CKSL,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE,sum(b.JHJE) as JHJE,b.LSJG as LSJG,b.YFBZ as YFBZ from YF_DB01 a,YF_DB02 b  where  a.SQYF = b.SQYF and a.SQDH = b.SQDH  and a.CKBZ = 1 and a.MBYF=:yfsb and a.CKRQ >:begin and a.CKRQ <=:end  group by b.LSJG,b.YFBZ,b.YPCD ,b.YPXH");
			StringBuffer hql_ypsl = new StringBuffer();// 统计药品申领和退药
			hql_ypsl.append("select b.YPXH as YPXH,b.YPCD as YPCD,sum(b.SFSL) as RKSL,sum(b.LSJE) as LSJE,sum(b.PFJE) as PFJE,sum(b.JHJE) as JHJE,b.LSJG as LSJG,c.ZXBZ as ZXBZ from YK_CK01 a,YK_CK02 b,YK_TYPK  c  where b.YPXH=c.YPXH and a.XTSB = b.XTSB and a.CKFS = b.CKFS  and a.CKDH = b.CKDH and a.LYPB = 1 and a.LYRQ >:begin  and a.LYRQ <=:end and a.YFSB =:yfsb  group by b.LSJG,c.ZXBZ,b.YPXH,b.YPCD");
			map_par_yp.put("yfsb", yfsb);
			List<Map<String, Object>> list_yp = dao.doQuery(
					hql_yfyp.toString(), map_par_yp);
			List<Map<String,Object>> list_syjc_temp=new ArrayList<Map<String,Object>>();//查询上月结存缓存数据
			List<Map<String,Object>> list_rksl_temp=new ArrayList<Map<String,Object>>();//查询入库缓存数据
			List<Map<String,Object>> list_cksl_temp=new ArrayList<Map<String,Object>>();//查询出库缓存数据
			List<Map<String,Object>> list_fysl_temp=new ArrayList<Map<String,Object>>();//查询门诊发药缓存数据
			List<Map<String,Object>> list_tjje_temp=new ArrayList<Map<String,Object>>();//查询调价金额缓存数据
			List<Map<String,Object>> list_yk_temp=new ArrayList<Map<String,Object>>();//查询盈亏缓存数据
			List<Map<String,Object>> list_bqfy_temp=new ArrayList<Map<String,Object>>();//查询病区发药缓存数据
			List<Map<String,Object>> list_jcfy_temp=new ArrayList<Map<String,Object>>();//查询家床发药缓存数据
			List<Map<String,Object>> list_dbrk_temp=new ArrayList<Map<String,Object>>();//查询调拨入库缓存数据
			List<Map<String,Object>> list_dbck_temp=new ArrayList<Map<String,Object>>();//查询调拨出库缓存数据
			List<Map<String,Object>> list_ypsl_temp=new ArrayList<Map<String,Object>>();//查询申领缓存数据
			Map<String,Object> map_par_temp=new HashMap<String,Object>();
			map_par_temp.put("zzsj", date_begin);
			map_par_temp.put("yfsb", yfsb);
			list_syjc_temp=dao.doSqlQuery(hql_syjc.toString(), map_par_temp);
			map_par_temp.remove("zzsj");
			map_par_temp.put("begin", date_begin);
			map_par_temp.put("end", date_end);
			list_rksl_temp=dao.doSqlQuery(hql_rksl.toString(), map_par_temp);
			list_cksl_temp=dao.doSqlQuery(hql_cksl.toString(), map_par_temp);
			list_fysl_temp=dao.doSqlQuery(hql_fysl.toString(), map_par_temp);
			list_tjje_temp=dao.doSqlQuery(hql_tjje.toString(), map_par_temp);
			list_yk_temp=dao.doSqlQuery(hql_yk.toString(), map_par_temp);
			list_bqfy_temp=dao.doSqlQuery(hql_bqfy.toString(), map_par_temp);
			list_jcfy_temp=dao.doSqlQuery(hql_jcfy.toString(), map_par_temp);
			list_dbrk_temp=dao.doSqlQuery(hql_dbrk.toString(), map_par_temp);
			list_dbck_temp=dao.doSqlQuery(hql_dbck.toString(), map_par_temp);
			list_ypsl_temp=dao.doSqlQuery(hql_ypsl.toString(), map_par_temp);
			// 存月结记录
			List<Map<String, Object>> list_yjjg = new ArrayList<Map<String, Object>>();
			// 开始遍历药品
			for (Map<String, Object> map_yp : list_yp) {
				double qcsl = MedicineUtils.parseDouble(map_yp.get("QCSL"));
				double lsje = MedicineUtils.parseDouble(map_yp.get("LSJE"));
				double rksl = MedicineUtils.parseDouble(map_yp.get("RKSL"));
				double cksl = MedicineUtils.parseDouble(map_yp.get("CKSL"));
				double pfje = MedicineUtils.parseDouble(map_yp.get("PFJE"));
				double jhje = MedicineUtils.parseDouble(map_yp.get("JHJE"));
				/*Map<String, Object> map_par_syjc = new HashMap<String, Object>();
				map_par_syjc.put("ypcd",
						MedicineUtils.parseLong(map_yp.get("YPCD")));
				map_par_syjc.put("ypxh",
						MedicineUtils.parseLong(map_yp.get("YPXH")));
				map_par_syjc.put("zzsj", date_begin);
				map_par_syjc.put("yfsb", yfsb);
				Map<String, Object> map_syjc = dao.doLoad(hql_syjc.toString(),
						map_par_syjc);// 上月结存数据
				*/
				Map<String, Object> map_par_compare = new HashMap<String, Object>();
				map_par_compare.put("YPCD",
						MedicineUtils.parseLong(map_yp.get("YPCD")));
				map_par_compare.put("YPXH",
						MedicineUtils.parseLong(map_yp.get("YPXH")));
				Map<String, Object> map_syjc =MedicineUtils.getRecord(list_syjc_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (map_syjc != null && map_syjc.size() != 0) {
					qcsl = MedicineUtils.parseDouble(df_four
							.format(MedicineUtils.parseDouble(map_syjc
									.get("KCSL"))
									* MedicineUtils.parseDouble(map_syjc
											.get("YFBZ"))
									/ MedicineUtils.parseDouble(map_yp
											.get("YFBZ"))));
					lsje = MedicineUtils.parseDouble(map_syjc.get("LSJE"));
					pfje = MedicineUtils.parseDouble(map_syjc.get("PFJE"));
					jhje = MedicineUtils.parseDouble(map_syjc.get("JHJE"));
				}
				/*Map<String, Object> map_par_rksl = new HashMap<String, Object>();
				map_par_rksl.put("begin", date_begin);
				map_par_rksl.put("end", date_end);
				map_par_rksl.put("yfsb", yfsb);
				map_par_rksl.put("ypxh",
						MedicineUtils.parseLong(map_yp.get("YPXH")));
				map_par_rksl.put("ypcd",
						MedicineUtils.parseLong(map_yp.get("YPCD")));
				List<Map<String, Object>> list_rksl = dao.doQuery(
						hql_rksl.toString(), map_par_rksl);// 入库数量数据
				*/
				List<Map<String, Object>> list_rksl =MedicineUtils.getListRecord(list_rksl_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_rksl != null && list_rksl.size() > 0) {
					for (Map<String, Object> map_rksl : list_rksl) {
						rksl = rksl
								+ MedicineUtils.parseDouble(df_four
										.format(MedicineUtils
												.parseDouble(map_rksl
														.get("RKSL"))
												* MedicineUtils
														.parseDouble(map_rksl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								+ MedicineUtils.parseDouble(map_rksl
										.get("LSJE"));
						jhje = jhje
								+ MedicineUtils.parseDouble(map_rksl
										.get("JHJE"));
						pfje = pfje
								+ MedicineUtils.parseDouble(map_rksl
										.get("PFJE"));
					}
				}
//				List<Map<String, Object>> list_cksl = dao.doQuery(
//						hql_cksl.toString(), map_par_rksl);// 出库数量数据
				List<Map<String, Object>> list_cksl =MedicineUtils.getListRecord(list_cksl_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_cksl != null && list_cksl.size() > 0) {
					for (Map<String, Object> map_cksl : list_cksl) {
						cksl = cksl
								+ MedicineUtils.parseDouble(df_four
										.format(MedicineUtils
												.parseDouble(map_cksl
														.get("CKSL"))
												* MedicineUtils
														.parseDouble(map_cksl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								- MedicineUtils.parseDouble(map_cksl
										.get("LSJE"));
						jhje = jhje
								- MedicineUtils.parseDouble(map_cksl
										.get("JHJE"));
						pfje = pfje
								- MedicineUtils.parseDouble(map_cksl
										.get("PFJE"));
					}
				}
//				List<Map<String, Object>> list_fysl = dao.doQuery(
//						hql_fysl.toString(), map_par_rksl);// 发药数量数据
				List<Map<String, Object>> list_fysl =MedicineUtils.getListRecord(list_fysl_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_fysl != null && list_fysl.size() > 0) {
					for (Map<String, Object> map_fysl : list_fysl) {
						cksl = cksl
								+ MedicineUtils.parseDouble(df_four
										.format(MedicineUtils
												.parseDouble(map_fysl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_fysl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								- MedicineUtils.parseDouble(map_fysl
										.get("LSJE"));
						jhje = jhje
								- MedicineUtils.parseDouble(map_fysl
										.get("JHJE"));
						pfje = pfje
								- MedicineUtils.parseDouble(map_fysl
										.get("PFJE"));
					}
				}
//				List<Map<String, Object>> list_bqfy = dao.doQuery(
//						hql_bqfy.toString(), map_par_rksl);// 病区发药数量数据
				List<Map<String, Object>> list_bqfy =MedicineUtils.getListRecord(list_bqfy_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_bqfy != null && list_bqfy.size() > 0) {
					for (Map<String, Object> map_fysl : list_bqfy) {
						cksl = cksl
								+ MedicineUtils.parseDouble(df_four
										.format(MedicineUtils
												.parseDouble(map_fysl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_fysl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								- MedicineUtils.parseDouble(map_fysl
										.get("LSJE"));
						jhje = jhje
								- MedicineUtils.parseDouble(map_fysl
										.get("JHJE"));
						pfje = pfje
								- MedicineUtils.parseDouble(map_fysl
										.get("PFJE"));
					}
				}
//				List<Map<String, Object>> list_jcfy = dao.doQuery(
//						hql_jcfy.toString(), map_par_rksl);// 家床发药数量数据
				List<Map<String, Object>> list_jcfy =MedicineUtils.getListRecord(list_jcfy_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_jcfy != null && list_jcfy.size() > 0) {
					for (Map<String, Object> map_fysl : list_jcfy) {
						cksl = cksl
								+ MedicineUtils.parseDouble(df_four
										.format(MedicineUtils
												.parseDouble(map_fysl
														.get("YPSL"))
												* MedicineUtils
														.parseDouble(map_fysl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								- MedicineUtils.parseDouble(map_fysl
										.get("LSJE"));
						jhje = jhje
								- MedicineUtils.parseDouble(map_fysl
										.get("JHJE"));
						pfje = pfje
								- MedicineUtils.parseDouble(map_fysl
										.get("PFJE"));
					}
				}
				
//				List<Map<String, Object>> list_tjje = dao.doQuery(
//						hql_tjje.toString(), map_par_rksl);// 调价金额数据
				List<Map<String, Object>> list_tjje =MedicineUtils.getListRecord(list_tjje_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_tjje != null && list_tjje.size() > 0) {
					for (Map<String, Object> map_tjje : list_tjje) {
						lsje = lsje
								+ MedicineUtils.parseDouble(map_tjje
										.get("LSJE"));
						jhje = jhje
								+ MedicineUtils.parseDouble(map_tjje
										.get("JHJE"));
						pfje = pfje
								+ MedicineUtils.parseDouble(map_tjje
										.get("PFJE"));
					}
				}
//				List<Map<String, Object>> list_yk = dao.doQuery(
//						hql_yk.toString(), map_par_rksl);// 盘盈亏数据
				List<Map<String, Object>> list_yk =MedicineUtils.getListRecord(list_yk_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_yk != null && list_yk.size() > 0) {
					for (Map<String, Object> map_yk : list_yk) {
						rksl = rksl
								+ MedicineUtils
										.parseDouble(df_four.format(MedicineUtils
												.parseDouble(map_yk.get("YKSL"))
												* MedicineUtils
														.parseDouble(map_yk
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								+ MedicineUtils.parseDouble(map_yk.get("LSJE"));
						jhje = jhje
								+ MedicineUtils.parseDouble(map_yk.get("JHJE"));
						pfje = pfje
								+ MedicineUtils.parseDouble(map_yk.get("PFJE"));

					}
				}
//				List<Map<String, Object>> list_dbrk = dao.doQuery(
//						hql_dbrk.toString(), map_par_rksl);// 调拨入库数量数据
				List<Map<String, Object>> list_dbrk =MedicineUtils.getListRecord(list_dbrk_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_dbrk != null && list_dbrk.size() > 0) {
					for (Map<String, Object> map_rksl : list_dbrk) {
						rksl = rksl
								+ MedicineUtils.parseDouble(df_four
										.format(MedicineUtils
												.parseDouble(map_rksl
														.get("RKSL"))
												* MedicineUtils
														.parseDouble(map_rksl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								+ MedicineUtils.parseDouble(map_rksl
										.get("LSJE"));
						jhje = jhje
								+ MedicineUtils.parseDouble(map_rksl
										.get("JHJE"));
						pfje = pfje
								+ MedicineUtils.parseDouble(map_rksl
										.get("PFJE"));
					}
				}
//				List<Map<String, Object>> list_dbck = dao.doQuery(
//						hql_dbck.toString(), map_par_rksl);// 调拨出库数量数据
				List<Map<String, Object>> list_dbck =MedicineUtils.getListRecord(list_dbck_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_dbck != null && list_dbck.size() > 0) {
					for (Map<String, Object> map_cksl : list_dbck) {
						cksl = cksl
								+ MedicineUtils.parseDouble(df_four
										.format(MedicineUtils
												.parseDouble(map_cksl
														.get("CKSL"))
												* MedicineUtils
														.parseDouble(map_cksl
																.get("YFBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								- MedicineUtils.parseDouble(map_cksl
										.get("LSJE"));
						jhje = jhje
								- MedicineUtils.parseDouble(map_cksl
										.get("JHJE"));
						pfje = pfje
								- MedicineUtils.parseDouble(map_cksl
										.get("PFJE"));
					}
				}
//				List<Map<String, Object>> list_ypsl = dao.doQuery(
//						hql_ypsl.toString(), map_par_rksl);// 申领入库数量数据
				List<Map<String, Object>> list_ypsl =MedicineUtils.getListRecord(list_ypsl_temp, new String[]{"YPXH","YPCD"}, map_par_compare, new String[]{"YPXH","YPCD"});
				if (list_ypsl != null && list_ypsl.size() > 0) {
					for (Map<String, Object> map_rksl : list_ypsl) {
						rksl = rksl
								+ MedicineUtils.parseDouble(df_four
										.format(MedicineUtils
												.parseDouble(map_rksl
														.get("RKSL"))
												* MedicineUtils
														.parseDouble(map_rksl
																.get("ZXBZ"))
												/ MedicineUtils
														.parseDouble(map_yp
																.get("YFBZ"))));
						lsje = lsje
								+ MedicineUtils.parseDouble(map_rksl
										.get("LSJE"));
						jhje = jhje
								+ MedicineUtils.parseDouble(map_rksl
										.get("JHJE"));
						pfje = pfje
								+ MedicineUtils.parseDouble(map_rksl
										.get("PFJE"));
					}
				}
				if (qcsl != 0 || rksl != 0 || cksl != 0 || lsje != 0
						|| pfje != 0 || jhje != 0) {
					map_yp.put("QCSL", qcsl);
					map_yp.put("LSJE", lsje);
					map_yp.put("RKSL", rksl);
					map_yp.put("CKSL", cksl);
					map_yp.put("PFJE", pfje);
					map_yp.put("JHJE", jhje);
					map_yp.put("JGID", jgid);
					map_yp.put("YFSB", yfsb);
					map_yp.put("CKBH", 0);
					map_yp.put("KCSL", qcsl + rksl - cksl);
					list_yjjg.add(map_yp);
				}
			}
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			String[] end = sd.format(date_end).split("-");
			Calendar a = Calendar.getInstance();
			a.set(Calendar.YEAR, MedicineUtils.parseInt(end[0]));
			a.set(Calendar.MONTH, MedicineUtils.parseInt(end[1]) - 1);
			a.set(Calendar.DATE, 10);
			a.set(Calendar.HOUR_OF_DAY, 0);
			a.set(Calendar.MINUTE, 0);
			a.set(Calendar.SECOND, 0);
			Date cwyf = a.getTime();
			body.put("CWYF", cwyf);
			dao.doSave("create", BSPHISEntryNames.YF_JZJL_FORM, body, false);
			if (list_yjjg.size() > 0) {
				for (Map<String, Object> map_yjjg : list_yjjg) {
					map_yjjg.put("CWYF", cwyf);
					dao.doSave("create", BSPHISEntryNames.YF_YJJG, map_yjjg,
							false);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "月结失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "月结验证失败", e);
		}
		return MedicineUtils.getRetMap();
	}

}
