package phis.application.sto.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.CalculatorIn;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;
/**
 * 药库出入库model
 * @author caijy
 *
 */
public class StorehouseCheckInOutModel{
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseCheckInOutModel.class);

	public StorehouseCheckInOutModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-31
	 * @description 打开入库单提交页面前校验数据是否已经删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationCheckInDelete(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql_rk_isDelete = new StringBuffer();// 入库记录是否已经被删除
		hql_rk_isDelete.append(" RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs ");
		StringBuffer hql_rk_isCommit = new StringBuffer();// 入库记录是否已经被确认
		hql_rk_isCommit
				.append(" RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs and RKPB=1");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("rkdh", MedicineUtils.parseInt(body.get("RKDH")));
		parameters.put("yksb", MedicineUtils.parseLong(body.get("XTSB")));
		parameters.put("rkfs", MedicineUtils.parseInt(body.get("RKFS")));
		try {
			Long l = dao.doCount("YK_RK01", hql_rk_isDelete.toString(),
					parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("该入库单已经删除,请刷新页面");
			}
			l = dao.doCount("YK_RK01", hql_rk_isCommit.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("该入库单已经确定,请刷新页面");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询入库记录是否被删除失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-31
	 * @description 删除入库
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removeCheckInData(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("rkdh", MedicineUtils.parseInt(body.get("RKDH")));
		parameters.put("yksb", MedicineUtils.parseLong(body.get("XTSB")));
		parameters.put("rkfs", MedicineUtils.parseInt(body.get("RKFS")));
		StringBuffer hql_count = new StringBuffer();
		hql_count
				.append(" RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs and RKPB=0");
		try {
			Long l = dao.doCount("YK_RK01", hql_count.toString(), parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("记录已确定入库,请刷新页面");
			}
		} catch (PersistentDataOperationException e1) {
			MedicineUtils.throwsException(logger, "查询是否确定入库失败", e1);
		}
		StringBuffer deleteHql = new StringBuffer();
		deleteHql
				.append("delete from YK_RK02 where RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs");
		try {
			dao.doUpdate(deleteHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除入库明细失败", e);
		}
		deleteHql = new StringBuffer();
		deleteHql
				.append("delete from YK_RK01  where RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs");
		try {
			dao.doUpdate(deleteHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除入库记录失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-31
	 * @description 采购入库时间条件查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<String> dateQuery(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		List<String> l = new ArrayList<String>();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int year = MedicineUtils.parseInt(body.get("year"));
		int month = MedicineUtils.parseInt(body.get("month"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer hql_qszz = new StringBuffer();
		hql_qszz.append("select QSSJ as QSSJ,ZZSJ as ZZSJ from YK_JZJL where CWYF=:cwyf and XTSB=:yksb");
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 10);
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		a.set(Calendar.MILLISECOND, 0);
		Map<String, Object> map_par_qszz = new HashMap<String, Object>();
		map_par_qszz.put("cwyf", a.getTime());
		map_par_qszz.put("yksb", yksb);
		try {
			Map<String, Object> map_qszz = dao.doLoad(hql_qszz.toString(),
					map_par_qszz);
			if (map_qszz != null && map_qszz.get("QSSJ") != null
					&& map_qszz.get("ZZSJ") != null) {
				l.add(sdf.format((Date) map_qszz.get("QSSJ")));
				l.add(sdf.format((Date) map_qszz.get("ZZSJ")));
				return l;
			}
			a.set(Calendar.MONTH, month - 2);
			map_par_qszz.put("cwyf", a.getTime());
			map_qszz = dao.doLoad(hql_qszz.toString(), map_par_qszz);
			if (map_qszz != null && map_qszz.get("ZZSJ") != null) {
				l.add(sdf.format((Date) map_qszz.get("ZZSJ")));
			}
			int yjDate=32;
			try{
			yjDate = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid,
					"YJSJ_YK" + yksb,BSPHISSystemArgument.defaultValue.get("YJSJ_YK"),BSPHISSystemArgument.defaultAlias.get("YJSJ_YK"),BSPHISSystemArgument.defaultCategory.get("YJSJ_YK"),ctx));// 月结日
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, "YJSJ_YK" + yksb, e);
			}
			if (l.size() == 0) {
				int lastDay = a.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (yjDate < lastDay) {
					a.set(Calendar.DATE, yjDate + 1);
				} else {
					a.set(Calendar.DATE, lastDay + 1);
				}
				a.set(Calendar.HOUR_OF_DAY, 0);
				a.set(Calendar.MINUTE, 0);
				a.set(Calendar.SECOND, 0);
				l.add(sdf.format(a.getTime()));
			}
			a.set(Calendar.YEAR, year);
			a.set(Calendar.MONTH, month - 1);
			int lastDay = a.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (yjDate < lastDay) {
				a.set(Calendar.DATE, yjDate);
			} else {
				a.set(Calendar.DATE, lastDay);
			}
			a.set(Calendar.HOUR_OF_DAY, 23);
			a.set(Calendar.MINUTE, 59);
			a.set(Calendar.SECOND, 59);
			l.add(sdf.format(a.getTime()));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "日期查询失败", e);
		}
		return l;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-2
	 * @description 页面查询条件里面的默认财务月份
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String initDateQuery(Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		SimpleDateFormat sdf_month = new SimpleDateFormat("yyyy-MM");
		StringBuffer hql_cwyf = new StringBuffer();
		StringBuffer hql_zzsj = new StringBuffer();
		hql_cwyf.append("select CWYF as CWYF from YK_JZJL where XTSB=:yksb and QSSJ<=:now and QSSJ>=:now");
		hql_zzsj.append("select ZZSJ as ZZSJ from YK_JZJL where XTSB=:yksb and CWYF=:cwyf");
		Map<String, Object> map_par_cwyf = new HashMap<String, Object>();
		map_par_cwyf.put("yksb", yksb);
		map_par_cwyf.put("now", new Date());
		String cwyf="";
		try {
			Map<String, Object> map_cwyf = dao.doLoad(hql_cwyf.toString(),
					map_par_cwyf);
			if (map_cwyf != null && map_cwyf.get("CWYF") != null) {
				return sdf_month.format((Date) map_cwyf.get("CWYF"));
			}
			Calendar a = Calendar.getInstance();
			a.set(Calendar.DATE, 10);
			a.set(Calendar.HOUR_OF_DAY, 0);
			a.set(Calendar.MINUTE, 0);
			a.set(Calendar.SECOND, 0);
			Map<String, Object> map_par_zzsj = new HashMap<String, Object>();
			map_par_zzsj.put("yksb", yksb);
			map_par_zzsj.put("cwyf", a.getTime());
			Map<String, Object> map_zzsj = dao.doLoad(hql_zzsj.toString(),
					map_par_zzsj);
			Date ldt_end = null;
			if (map_zzsj != null && map_zzsj.get("ZZSJ") != null) {
				ldt_end = (Date) map_zzsj.get("ZZSJ");
			} else {
				a = Calendar.getInstance();
				a.set(Calendar.MONTH, a.get(Calendar.MONTH));
				int lastDay = a.getActualMaximum(Calendar.DAY_OF_MONTH);
				int yjDate=32;
				try{
					yjDate = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid,
							"YJSJ_YK" + yksb,BSPHISSystemArgument.defaultValue.get("YJSJ_YK"),BSPHISSystemArgument.defaultAlias.get("YJSJ_YK"),BSPHISSystemArgument.defaultCategory.get("YJSJ_YK"),ctx));// 月结日
					}catch(Exception e){
						MedicineUtils.throwsSystemParameterException(logger, "YJSJ_YK" + yksb, e);
					}
				if (yjDate < lastDay) {
					a.set(Calendar.DATE, yjDate + 1);
				} else {
					a.set(Calendar.DATE, lastDay + 1);
				}
				a.set(Calendar.HOUR_OF_DAY, 23);
				a.set(Calendar.MINUTE, 59);
				a.set(Calendar.SECOND, 59);
				ldt_end = a.getTime();
			}
			a = Calendar.getInstance();
			if (ldt_end.getTime() < a.getTimeInMillis()) {
				a.set(Calendar.MONTH, a.get(Calendar.MONTH) + 1);
			}
			int year = a.get(Calendar.YEAR);
			int month = a.get(Calendar.MONTH) + 1;
			if (month < 10) {
				cwyf= year + "-0" + month;
			}else{
				cwyf=year + "-" + month;
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "日期查询失败", e);
		}
		return cwyf;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-2
	 * @description 保存入库单
	 * @updateInfo
	 * @param body
	 * @param op
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op)
			throws ModelDataOperationException {
		Map<String, Object> rk = (Map<String, Object>) body.get("YK_RK01");
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("YK_RK02");
		//保存前判断计划单是否被保存(zww提出的缺陷,2个人同时操作)
		List<Long> jhxbxhs=new ArrayList<Long>();
		List<Long> sbxhs=new ArrayList<Long>();
		for(Map<String,Object> map_rk02:meds){
			if(map_rk02.containsKey("JHSBXH")&&MedicineUtils.parseLong(map_rk02.get("JHSBXH"))!=0){
				jhxbxhs.add(MedicineUtils.parseLong(map_rk02.get("JHSBXH")));
			}
			if(map_rk02.containsKey("SBXH")&&MedicineUtils.parseLong(map_rk02.get("SBXH"))!=0){
				sbxhs.add(MedicineUtils.parseLong(map_rk02.get("SBXH")));
			}
		}
		if(jhxbxhs.size()>0){
			StringBuffer hql_jhcf=new StringBuffer();//查询计划单是否重复
			hql_jhcf.append(" JHSBXH in (:jhsbxhs) ");
			Map<String,Object> map_par_jhcf=new HashMap<String,Object>();
			map_par_jhcf.put("jhsbxhs", jhxbxhs);
			if(sbxhs.size()>0){
				hql_jhcf.append(" and SBXH not in (:sbxhs)");
				map_par_jhcf.put("sbxhs", sbxhs);
			}
			try {
				long	l_jhcf = dao.doCount("YK_RK02", hql_jhcf.toString(), map_par_jhcf);
				if(l_jhcf>0){
					throw new ModelDataOperationException("有计划单已经被其他采购入库单调用!");
				}
			} catch (PersistentDataOperationException e1) {
				MedicineUtils.throwsException(logger, "计划单是否被引入验证失败", e1);
			}
			
		}
		
		long yksb = MedicineUtils.parseLong(rk.get("XTSB"));
		int rkfs = MedicineUtils.parseInt(rk.get("RKFS"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("yksb", yksb);
		parameters.put("rkfs", rkfs);
		if ("create".equals(op)) {
			StringBuffer rkdhHql = new StringBuffer();
			rkdhHql.append("select RKDH as RKDH from YK_RKFS where XTSB=:yksb and RKFS=:rkfs");
			Map<String, Object> rkdhMap;
			int rkdh=0;
			// 更新入库单号
			try {
				rkdhMap = dao.doLoad(rkdhHql.toString(), parameters);
				rkdh = MedicineUtils.parseInt(rkdhMap.get("RKDH")) ;
				StringBuffer rkdhUpdateHql = new StringBuffer();
				rkdhUpdateHql
						.append("update YK_RKFS set RKDH=:rkdh where XTSB=:yksb and RKFS=:rkfs");
				parameters.put("rkdh", rkdh + 1);
				dao.doUpdate(rkdhUpdateHql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "入库单号查询失败,单号已达最大值,请联系开发人员修改单号长度", e);
			}
			rk.put("RKDH", rkdh);
			try {
				dao.doSave("create", BSPHISEntryNames.YK_RK01_FORM, rk, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("RKDH", rkdh);
					med.put("RKFS", rkfs);
					med.put("HGSL", MedicineUtils.parseDouble(med.get("RKSL")));
					med.put("LSJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(med.get("LSJG"))
									* MedicineUtils.parseDouble(med.get("RKSL"))));
					med.put("BZLJ", MedicineUtils.parseDouble(med.get("LSJG")));
					dao.doSave("create", BSPHISEntryNames.YK_RK02, med, false);
				}
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "入库记录保存验证失败", e);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "入库记录保存验证失败", e);
			}

		} else {
			parameters.put("rkdh", rk.get("RKDH"));
			StringBuffer deleteHql = new StringBuffer();
			deleteHql.append("delete from YK_RK02 where XTSB=:yksb and RKFS=:rkfs and RKDH=:rkdh");
			try {
				StringBuffer hql_count=new StringBuffer();
				hql_count.append(" RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs ");
				long l=dao.doCount("YK_RK01", hql_count.toString(), parameters);
				if (l == 0) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "该入库单已经被删除,请刷新页面!");
				}
				hql_count.append("  and RKPB=1");
				l=dao.doCount("YK_RK01", hql_count.toString(), parameters);
				if (l > 0) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "该入库单已经入库,请刷新页面!");
				}
				dao.doUpdate(deleteHql.toString(), parameters);
				dao.doSave("update", BSPHISEntryNames.YK_RK01_FORM, rk, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("RKDH", rk.get("RKDH"));
					med.put("RKFS", rkfs);
					med.put("HGSL", MedicineUtils.parseDouble(med.get("RKSL")));
					med.put("LSJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(med.get("LSJG"))
									* MedicineUtils.parseDouble(med.get("RKSL"))));
					med.put("BZLJ", MedicineUtils.parseDouble(med.get("LSJG")));
					dao.doSave("create", BSPHISEntryNames.YK_RK02, med, false);
				}
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "入库记录更新失败", e);
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "入库记录更新失败", e);
			}

		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-2
	 * @description 提交入库单
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> saveCheckInToInventory(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId();// 用户ID
		int rkdh = MedicineUtils.parseInt(body.get("RKDH"));
		long yksb = MedicineUtils.parseLong(body.get("XTSB"));
		int rkfs = MedicineUtils.parseInt(body.get("RKFS"));
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("YK_RK02");
		String tag = MedicineUtils.parseString(body.get("tag"));// 用于判断是采购入库还是其他入库
		StringBuffer rkHql = new StringBuffer();// 判断是否已经入库
		rkHql.append(" RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs and RKPB=1");
		StringBuffer hql_yj = new StringBuffer();// 判断是否已经结账
		hql_yj.append("select count(1) as num from YK_JZJL  having max(ZZSJ)>sysdate and XTSB=:yksb group by XTSB");
		StringBuffer kc_update = new StringBuffer();// 更新库存(明细中带有kcsb的)
		kc_update
		.append("update YK_KCMX set KCSL=KCSL+:ypsl,LSJE=LSJG*(KCSL+:ypsl),PFJE=PFJG*(KCSL+:ypsl),JHJE=JHJG*(KCSL+:ypsl) where SBXH=:sbxh");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("rkdh", rkdh);
		parameters.put("yksb", yksb);
		parameters.put("rkfs", rkfs);
		try {
			Long l = dao.doCount("YK_RK01", rkHql.toString(),
					parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("该入库单已经入库!");
			}
			StringBuffer hql_sc = new StringBuffer();// 记录是否已经删除
			hql_sc.append(" RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs ");
			l = dao.doCount("YK_RK01", hql_sc.toString(),
					parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("该入库单已经被删除,请刷新页面!");
			}
			Map<String, Object> map_par_yj = new HashMap<String, Object>();
			map_par_yj.put("yksb", yksb);
			List<Map<String, Object>> list_yj = dao.doSqlQuery(
					hql_yj.toString(), map_par_yj);
			if (list_yj != null && list_yj.size() != 0) {
				return MedicineUtils.getRetMap("本月已过账,不能把入库单确认在本月!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询是否入库失败", e);
		}
		try {
			StringBuffer updateHql = new StringBuffer();// 更新rk01
			updateHql
			.append("update YK_RK01 set RKPB=1,RKRQ=sysdate,CZGH=:czgh where RKDH=:rkdh and XTSB=:yksb and RKFS=:rkfs");
			StringBuffer hql_kcsl = new StringBuffer();// 查询库存数量
			hql_kcsl.append("select KCSL as KCSL from YK_KCMX where SBXH=:kcsb");
			StringBuffer hql_rk02_update = new StringBuffer();// 更新入库明细的kcsb
			hql_rk02_update.append("update YK_RK02 set KCSB=:kcsb where SBXH=:sbxh");
			StringBuffer hql_rk02_qt_update = new StringBuffer();// 其他入库更新入库明细
			hql_rk02_qt_update
			.append("update YK_RK02 set KCSB=:kcsb , YSDH=RKDH , YSRQ=:ysrq , YSGH=:ysgh where SBXH=:sbxh ");
			StringBuffer hql_cd_update = new StringBuffer();
			hql_cd_update
					.append("update YK_CDXX set JHJE=JHJE+:jhje,PFJE=PFJE+:pfje,LSJE=LSJE+:lsje,KCSL=KCSL+:kcsl where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
			// 保存前先判断数量是负的明细库存是否足够
			for (Map<String, Object> map_rkmx : meds) {
				if (map_rkmx.get("KCSB") == null
						|| MedicineUtils.parseLong(map_rkmx.get("KCSB")) == 0) {
					continue;
				}
				Map<String, Object> map_par_kcsl = new HashMap<String, Object>();
				map_par_kcsl.put("kcsb", MedicineUtils.parseLong(map_rkmx.get("KCSB")));
				Map<String, Object> map_kcsl = dao.doLoad(hql_kcsl.toString(),
						map_par_kcsl);
				if (map_kcsl == null
						|| MedicineUtils.parseDouble(map_kcsl.get("KCSL"))
								+ MedicineUtils.parseDouble(map_rkmx.get("RKSL")) < 0) {
					return MedicineUtils.getRetMap(map_rkmx.get("YPMC") + "库存不够,请修改!");
				}
			}
			Date nowDate = new Date();
			StringBuffer hql_rk01_update = new StringBuffer();// 更新rk01
			List<Long> list_jhsbxh=new ArrayList<Long>();
			if ("cgrk".equals(tag)) {
				StringBuffer hql_jh02=new StringBuffer();
				hql_jh02.append("update YK_JH02 set CGSL=:hgsl where SBXH=:sbxh");
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					ifyptj(med);// 提交前进行调价验证
					double rksl = MedicineUtils.parseDouble(med.get("RKSL"));
					double hgsl = MedicineUtils.parseDouble(med.get("HGSL"));
					double cpsl = rksl - hgsl;
					// 有次品
					if (rksl != hgsl) {
						Map<String, Object> map_kc = new HashMap<String, Object>();
						map_kc = saveKc(hgsl, med, nowDate, 1, ctx);// 保存合格的库存明细
						long kcsb = 0;
						if (map_kc != null) {
							kcsb = MedicineUtils.parseLong(map_kc.get("SBXH"));
							saveKcFl(kcsb, kcsb, med, hgsl);// 保存合格的库存分裂
							Map<String, Object> map_par_rk02 = new HashMap<String, Object>();
							map_par_rk02.put("kcsb", kcsb);
							map_par_rk02
									.put("sbxh", MedicineUtils.parseLong(med.get("SBXH")));
							dao.doUpdate(hql_rk02_update.toString(),
									map_par_rk02);
						}
						map_kc = saveKc(
								cpsl,
								med,
								nowDate,
								med.get("TYPE") == null ? 1 : MedicineUtils.parseInt(med
										.get("TYPE")), ctx);// 保存次品库存
						if (kcsb != 0) {
							saveKcFl(kcsb, MedicineUtils.parseLong(map_kc.get("SBXH")), med,
									cpsl);// 保存次品的库存分裂
						}
					} else {
						if (med.get("KCSB") != null
								&& MedicineUtils.parseLong(med.get("KCSB")) != 0) {
							Map<String, Object> map_par_kc_update = new HashMap<String, Object>();
							map_par_kc_update.put("ypsl", hgsl);
							map_par_kc_update.put("sbxh",
									MedicineUtils.parseLong(med.get("KCSB")));
							dao.doUpdate(kc_update.toString(),
									map_par_kc_update);
							updateYkkc(MedicineUtils.parseLong(med.get("KCSB")));
						} else {
							Map<String, Object> map_kc = new HashMap<String, Object>();
							map_kc = saveKc(hgsl, med, nowDate, 1, ctx);// 保存合格的库存明细
							long kcsb = MedicineUtils.parseLong(map_kc.get("SBXH"));
							saveKcFl(kcsb, kcsb, med, hgsl);// 保存合格的库存分裂
							Map<String, Object> map_par_rk02 = new HashMap<String, Object>();
							map_par_rk02.put("kcsb", kcsb);
							map_par_rk02
									.put("sbxh", MedicineUtils.parseLong(med.get("SBXH")));
							dao.doUpdate(hql_rk02_update.toString(),
									map_par_rk02);
						}
					}
					if(med.containsKey("JHSBXH")&&med.get("JHSBXH")!=null){
						Map<String,Object> map_par=new HashMap<String,Object>();
						map_par.put("sbxh", MedicineUtils.parseLong(med.get("JHSBXH")));
						map_par.put("hgsl", hgsl);
						dao.doUpdate(hql_jh02.toString(), map_par);
						list_jhsbxh.add(MedicineUtils.parseLong(med.get("JHSBXH")));
					}
				}
				hql_rk01_update
						.append("update YK_RK01  set RKPB=1,RKRQ=:rkrq,CZGH=:czgh where RKDH=:rkdh and RKFS=:rkfs and XTSB=:xtsb");
			} else {// 其他入库
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					ifyptj(med);// 提交前进行调价验证
					double rksl = MedicineUtils.parseDouble(med.get("RKSL"));
					if (med.get("KCSB") != null
							&& MedicineUtils.parseLong(med.get("KCSB")) != 0) {
						Map<String, Object> map_par_kc_update = new HashMap<String, Object>();
						map_par_kc_update.put("ypsl", rksl);
						map_par_kc_update.put("sbxh",
								MedicineUtils.parseLong(med.get("KCSB")));
						dao.doUpdate(kc_update.toString(), map_par_kc_update);
						updateYkkc(MedicineUtils.parseLong(med.get("KCSB")));
						Map<String, Object> map_par_rk02 = new HashMap<String, Object>();
						map_par_rk02.put("kcsb", MedicineUtils.parseLong(med.get("KCSB")));
						map_par_rk02.put("ysrq", nowDate);
						map_par_rk02.put("ysgh", userid);
						map_par_rk02.put("sbxh", MedicineUtils.parseLong(med.get("SBXH")));
						dao.doUpdate(hql_rk02_qt_update.toString(),
								map_par_rk02);
					} else {
						Map<String, Object> map_kc = new HashMap<String, Object>();
						map_kc = saveKc(rksl, med, nowDate, 1, ctx);
						long kcsb = MedicineUtils.parseLong(map_kc.get("SBXH"));
						Map<String, Object> map_par_rk02 = new HashMap<String, Object>();
						map_par_rk02.put("kcsb", kcsb);
						map_par_rk02.put("ysrq", nowDate);
						map_par_rk02.put("ysgh", userid);
						map_par_rk02.put("sbxh", MedicineUtils.parseLong(med.get("SBXH")));
						dao.doUpdate(hql_rk02_qt_update.toString(),
								map_par_rk02);
					}
					Map<String, Object> map_par_cd = new HashMap<String, Object>();
					map_par_cd
							.put("jhje",
									MedicineUtils.formatDouble(4,
											MedicineUtils.parseDouble(med.get("JHJG")) * rksl));
					map_par_cd
							.put("lsje",
									MedicineUtils.formatDouble(4,
											MedicineUtils.parseDouble(med.get("LSJG")) * rksl));
					map_par_cd
							.put("pfje",
									MedicineUtils.formatDouble(4,
											MedicineUtils.parseDouble(med.get("PFJG")) * rksl));
					map_par_cd.put("kcsl", rksl);
					map_par_cd.put("ypxh", MedicineUtils.parseLong(med.get("YPXH")));
					map_par_cd.put("ypcd", MedicineUtils.parseLong(med.get("YPCD")));
					map_par_cd.put("jgid",MedicineUtils.parseString(med.get("JGID")));
					dao.doUpdate(hql_cd_update.toString(), map_par_cd);
				}
				hql_rk01_update
						.append("update YK_RK01 set RKPB=1,CWPB=1,RKRQ=:rkrq,CZGH=:czgh where RKDH=:rkdh and RKFS=:rkfs and XTSB=:xtsb");
			}
			Map<String, Object> map_par_rk01 = new HashMap<String, Object>();
			map_par_rk01.put("rkrq", nowDate);
			map_par_rk01.put("czgh", userid);
			map_par_rk01.put("rkdh", rkdh);
			map_par_rk01.put("xtsb", yksb);
			map_par_rk01.put("rkfs", rkfs);
			dao.doUpdate(hql_rk01_update.toString(), map_par_rk01);
			if(list_jhsbxh.size()>0){
				StringBuffer hql_update=new StringBuffer();
				hql_update.append("update YK_JH01 set ZXRQ=:zxrq,ZXGH=:zxgh where XTSB=:xtsb and JHDH in (select JHDH from YK_JH02 where SBXH in (:sbxhs))");
				Map<String,Object> map_par=new HashMap<String,Object>();
				map_par.put("xtsb", yksb);
				map_par.put("zxrq", new Date());
				map_par.put("zxgh", userid);
				map_par.put("sbxhs", list_jhsbxh);
				dao.doSqlUpdate(hql_update.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存保存失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-2
	 * @description 入库提交前判断药品是否被调价
	 * @updateInfo
	 * @param med
	 * @throws ModelDataOperationException
	 */
	public void ifyptj(Map<String, Object> med)
			throws ModelDataOperationException {
		StringBuffer hql_sftj = new StringBuffer();// 查询是否已经调价
		hql_sftj.append("select count(1) as NUM from YK_TJ01 a,YK_TJ02  b where a.XTSB=b.XTSB and a.TJFS=b.TJFS and a.TJDH=b.TJDH and a.ZXRQ>(select LRRQ from YK_RK01 where XTSB=:xtsb and RKFS=:rkfs and RKDH=:rkdh) and b.YPXH=:ypxh and b.YPCD=:ypcd and a.ZYPB=1 and a.JGID=:jgid");
		StringBuffer hql_cdxx = new StringBuffer();// 查询产地信息
		hql_cdxx.append("select PFJG as PFJG,LSJG as LSJG from YK_CDXX where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
		Map<String, Object> map_par_sttj = new HashMap<String, Object>();
		map_par_sttj.put("xtsb", MedicineUtils.parseLong(med.get("XTSB")));
		map_par_sttj.put("ypxh", MedicineUtils.parseLong(med.get("YPXH")));
		map_par_sttj.put("ypcd", MedicineUtils.parseLong(med.get("YPCD")));
		map_par_sttj.put("jgid", MedicineUtils.parseString(med.get("JGID")));
		map_par_sttj.put("rkdh", MedicineUtils.parseInt(med.get("RKDH")));
		map_par_sttj.put("rkfs", MedicineUtils.parseInt(med.get("RKFS")));
		try {
			List<Map<String, Object>> list_sftj = dao.doSqlQuery(
					hql_sftj.toString(), map_par_sttj);
			int num = MedicineUtils.parseInt(list_sftj.get(0).get("NUM"));
			if (num != 0) {
				Map<String, Object> map_par_cdxx = new HashMap<String, Object>();
				map_par_cdxx.put("ypxh", MedicineUtils.parseLong(med.get("YPXH")));
				map_par_cdxx.put("ypcd", MedicineUtils.parseLong(med.get("YPCD")));
				map_par_cdxx.put("jgid", MedicineUtils.parseString(med.get("JGID")));
				Map<String, Object> map_cdxx = dao.doLoad(hql_cdxx.toString(),
						map_par_cdxx);
				double pfje = 0;
				double bzlj = 0;
				if (MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_cdxx.get("PFJG"))) != MedicineUtils.formatDouble(
						4, MedicineUtils.parseDouble(med.get("PFJG")))) {
					pfje = MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(med.get("RKSL")))
							* MedicineUtils.parseDouble(map_cdxx.get("PFJG"));
					med.put("PFJE", pfje);
				}
				if (MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_cdxx.get("LSJG"))) != MedicineUtils.formatDouble(
						4, MedicineUtils.parseDouble(med.get("BZLJ")))) {
					bzlj = MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_cdxx.get("LSJG")));
					med.put("BZLJ", bzlj);
				}
				if (pfje != 0 || bzlj != 0) {
					StringBuffer hql_rk02_update = new StringBuffer();
					Map<String, Object> map_par_rk02 = new HashMap<String, Object>();
					hql_rk02_update.append("update YK_RK02 set ");
					if (pfje != 0) {
						hql_rk02_update.append(" PFJE=:pfje ");
						map_par_rk02.put("pfje", pfje);
						if (bzlj != 0) {
							hql_rk02_update.append(" ,BZLJ=:bzlj ");
							map_par_rk02.put("bzlj", bzlj);
						}
					} else {
						hql_rk02_update.append(" BZLJ=:bzlj ");
						map_par_rk02.put("bzlj", bzlj);
					}
					dao.doUpdate(hql_rk02_update.toString(), map_par_rk02);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "是否调价查询失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 保存库存
	 * @updateInfo
	 * @param rksl
	 * @param med
	 * @param d
	 * @param type
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveKc(double rksl, Map<String, Object> med,
			Date d, int type, Context ctx) throws ModelDataOperationException {
		if (rksl == 0) {
			return null;
		}
		StorehouseStockManageModel model=new StorehouseStockManageModel(dao);
		model.synchronousPrimaryKey(ctx);
		Map<String, Object> map_kc = new HashMap<String, Object>();
		map_kc.put("KCSL", rksl);
		map_kc.put("JHRQ", d);
		map_kc.put("JHJE", MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(med.get("JHJG")) * rksl));
		map_kc.put("LSJE", MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(med.get("LSJG")) * rksl));
		map_kc.put("PFJE", MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(med.get("PFJG")) * rksl));
		map_kc.put("JGID", med.get("JGID"));
		map_kc.put("YPXH", MedicineUtils.parseLong(med.get("YPXH")));
		map_kc.put("YPCD", MedicineUtils.parseLong(med.get("YPCD")));
		map_kc.put("YPPH", med.get("YPPH"));
		map_kc.put("YPXQ", med.get("YPXQ"));
		map_kc.put("TYPE", type);
		map_kc.put("JHJG", MedicineUtils.parseDouble(med.get("JHJG")));
		map_kc.put("PFJG", MedicineUtils.parseDouble(med.get("PFJG")));
		map_kc.put("LSJG", MedicineUtils.parseDouble(med.get("LSJG")));
		map_kc.put("BZLJ", MedicineUtils.parseDouble(med.get("BZLJ")));
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			ret = dao.doSave("create", BSPHISEntryNames.YK_KCMX, map_kc, false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "库存保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存保存失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 保存库存分裂
	 * @updateInfo
	 * @param kcsb
	 * @param flsb
	 * @param med
	 * @param kcsl
	 * @throws ModelDataOperationException
	 */
	public void saveKcFl(long kcsb, long flsb, Map<String, Object> med,
			double kcsl) throws ModelDataOperationException {
		try {
			StringBuffer hql=new StringBuffer();//判断有无相同分裂识别的记录,有就更新 没有就新增
			hql.append("FLSB=:flsb");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("flsb", flsb);
			long l=dao.doCount("YK_KCFL", hql.toString(), map_par);
			if(l>0){
				StringBuffer hql_update=new StringBuffer();
				hql_update.append("update YK_KCFL set KCSL=:kcsl,JHJE=:jhje,PFJE=:pfje,LSJE=:lsje where FLSB=:flsb");
				map_par.put("kcsl", kcsl);
				map_par.put("jhje", MedicineUtils.simpleMultiply(4, kcsl, med.get("JHJG")));
				map_par.put("pfje", MedicineUtils.simpleMultiply(4, kcsl, med.get("PFJG")));
				map_par.put("lsje", MedicineUtils.simpleMultiply(4, kcsl, med.get("LSJG")));
				dao.doSqlUpdate(hql_update.toString(), map_par);
			}else{
				Map<String, Object> map_kcfl = new HashMap<String, Object>();
				map_kcfl.put("JGID", med.get("JGID"));
				map_kcfl.put("XTSB", MedicineUtils.parseLong(med.get("XTSB")));
				map_kcfl.put("KCSB", kcsb);
				map_kcfl.put("FLSB", flsb);
				map_kcfl.put("KCSL", kcsl);
				map_kcfl.put("JHJE",MedicineUtils.simpleMultiply(4, kcsl, med.get("JHJG")));
				map_kcfl.put("PFJE",MedicineUtils.simpleMultiply(4, kcsl, med.get("PFJG")));
				map_kcfl.put("LSJE",MedicineUtils.simpleMultiply(4, kcsl, med.get("LSJG")));
					dao.doSave("create", BSPHISEntryNames.YK_KCFL, map_kcfl, false);
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "库存分裂保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存分裂保存失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 查询指定库存是否数量为0 如果为0 删除记录 并且保存到临时表
	 * @updateInfo
	 * @param sbxh
	 * @throws ModelDataOperationException
	 */
	public void updateYkkc(long sbxh) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();// 查询库存数量是否为0
		hql.append(" SBXH=:sbxh and KCSL=0");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sbxh", sbxh);
		long l;
		try {
			StringBuffer hql_kc_update = new StringBuffer();
			hql_kc_update.append("delete from YK_KCMX_LS where SBXH=:sbxh");
			dao.doUpdate(hql_kc_update.toString(), map_par);
			l = dao.doCount("YK_KCMX", hql.toString(), map_par);
			if (l > 0) {
				hql_kc_update = new StringBuffer();
				hql_kc_update.append("insert into YK_KCMX_LS  select * from YK_KCMX where SBXH=:sbxh ");
				dao.doSqlUpdate(hql_kc_update.toString(), map_par);
				hql_kc_update = new StringBuffer();
				hql_kc_update.append("delete from YK_KCMX where SBXH=:sbxh");
				dao.doUpdate(hql_kc_update.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存更新失败", e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 查询入库记录
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadCheckInData(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		String tag = body.get("tag") + "";
		if ("cgrk".equals(tag)) {
			hql.append(
					"select a.JGID as JGID,a.XTSB as XTSB,a.RKFS as RKFS,b.FSMC as FSMC,c.DWMC as DWMC,a.CGRQ as CGRQ,a.FDJS as FDJS,a.PWD as PWD,a.RKBZ as RKBZ,a.RKDH as RKDH,a.DWXH as DWXH,a.CWPB as CWPB,a.RKPB as RKPB,a.LRRQ as LRRQ ,a.RKRQ as RKRQ,a.CGGH as CGGH,a.CZGH as CZGH,a.DJFS as DJFS,a.DJGS as DJGS from YK_RK01 a,YK_RKFS b,YK_JHDW c where a.XTSB=:yksb and a.RKDH=:rkdh and a.RKFS=:rkfs and a.RKFS=b.RKFS and a.DWXH=c.DWXH ");
		} else {
			hql.append(
					"select a.JGID as JGID,a.XTSB as XTSB,a.RKFS as RKFS,b.FSMC as FSMC,a.CGRQ as CGRQ,a.FDJS as FDJS,a.PWD as PWD,a.RKBZ as RKBZ,a.RKDH as RKDH,a.DWXH as DWXH,a.CWPB as CWPB,a.RKPB as RKPB,a.LRRQ as LRRQ ,a.RKRQ as RKRQ,a.CGGH as CGGH,a.CZGH as CZGH,a.DJFS as DJFS,a.DJGS as DJGS from YK_RK01 a,YK_RKFS b  where a.XTSB=:yksb and a.RKDH=:rkdh and a.RKFS=:rkfs and a.RKFS=b.RKFS  ");
		}
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb",MedicineUtils.parseLong(body.get("XTSB")));
		map_par.put("rkfs", MedicineUtils.parseInt(body.get("RKFS")));
		map_par.put("rkdh", MedicineUtils.parseInt(body.get("RKDH")));
		Map<String, Object> ret=new HashMap<String,Object>();
		try {
			ret = dao.doLoad(hql.toString(), map_par);
			StringBuffer hql_rk01_ys = new StringBuffer();// 查询rk02是否有记录被验收 如果有
															// 则不能修改进货单位
			hql_rk01_ys
					.append(" XTSB=:yksb and RKDH=:rkdh and RKFS=:rkfs and YSDH!=0");
			long l = dao.doCount("YK_RK02",
					hql_rk01_ys.toString(), map_par);
			if (l > 0) {
				ret.put("YYS", 1);// 有已经验收的入库明细
			}
			if (ret.get("PWD") != null) {
				Map<String, Object> pwd = new HashMap<String, Object>();
				String text = "";
				switch (MedicineUtils.parseInt(ret.get("PWD"))) {
				case 0:
					text = "货到票到";
					break;
				case 1:
					text = "货到票未到";
					break;
				case 2:
					text = "票到货未到";
					break;
				}
				pwd.put("text", text);
				pwd.put("key", MedicineUtils.parseInt(ret.get("PWD")));
				ret.put("PWD", pwd);
			}
			
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "入库单查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 入库方式下拉框数据查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<List<Object>> queryCheckInWay(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		int tag = MedicineUtils.parseInt(body.get("tag"));
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		List<List<Object>> ret = new ArrayList<List<Object>>();
		StringBuffer hql = new StringBuffer();
		if (tag == 1) {
			hql.append("Select RKFS as RKFS,FSMC as FSMC From YK_RKFS Where XTSB = :yksb and DYFS=1");
		} else {
			hql.append("Select RKFS as RKFS,FSMC as FSMC From YK_RKFS Where XTSB = :yksb and DYFS!=1");
		}
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb", yksb);
		try {
			List<Map<String, Object>> list_rkfs = dao.doSqlQuery(
					hql.toString(), map_par);
			for (Map<String, Object> map_rkfs : list_rkfs) {
				List<Object> l = new ArrayList<Object>();
				l.add(MedicineUtils.parseLong(map_rkfs.get("RKFS")));
				l.add(map_rkfs.get("FSMC") + "");
				ret.add(l);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "入库方式查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 修改确认前判断出库单是否已经删除或者确认
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationCheckOutDelete(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append(" CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb ");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("ckdh", MedicineUtils.parseInt(body.get("ckdh")));
			map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
			map_par.put("xtsb", MedicineUtils.parseLong(body.get("xtsb")));
			long l = dao.doCount("YK_CK01", hql.toString(),
					map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("出库单已经被删除!");
			}
			hql.append(" and CKPB=1");
			l = dao.doCount("YK_CK01", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("出库单已经被确认!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "判断是否已经删除和确认失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 删除出库单
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removeCheckOutData(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("xtsb")));
		map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
		map_par.put("ckdh", MedicineUtils.parseInt(body.get("ckdh")));
		Map<String, Object> ret = verificationCheckOutDelete(map_par);
		if (MedicineUtils.parseInt(ret.get("code")) == 200) {
			StringBuffer hql_delete = new StringBuffer();
			hql_delete.append("delete from YK_CK02 where CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb");
			try {
				dao.doUpdate(hql_delete.toString(), map_par);
				hql_delete = new StringBuffer();
				hql_delete
						.append("delete from YK_CK01 where CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb");
				dao.doUpdate(hql_delete.toString(), map_par);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "出库单删除失败", e);
			}
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 出库单退回
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveCheckOutBack(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("code", 200);
		ret.put("msg", "ok");
		StringBuffer hql = new StringBuffer();
		Map<String, Object> map_par = new HashMap<String, Object>();
		hql.append("select YFSB as YFSB,CKPB as CKPB from YK_CK01 where XTSB=:xtsb and CKFS=:ckfs and CKDH=:ckdh ");
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("xtsb")));
		map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
		map_par.put("ckdh", MedicineUtils.parseInt(body.get("ckdh")));
		try {
			Map<String, Object> map_ck = dao.doLoad(hql.toString(), map_par);
			if (map_ck.get("YFSB") == null || MedicineUtils.parseLong(map_ck.get("YFSB")) < 1) {
				return MedicineUtils.getRetMap("本出库单由药库直接开出,不能退回!");
			}
			if (map_ck.get("CKPB") != null && MedicineUtils.parseInt(map_ck.get("CKPB")) == 1) {
				return MedicineUtils.getRetMap("已确认出库不能退回!窗口已刷新!");
			}
			hql = new StringBuffer();
			hql.append("update YK_CK01 set SQTJ=0 where XTSB=:xtsb and CKFS=:ckfs and CKDH=:ckdh ");
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "出库单退回失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 查询出库方式的对应方式和科室判别
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryDyfs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer hql_dyfs = new StringBuffer();// 查询对应方式可科室判别
		hql_dyfs.append("select DYFS as DYFS ,KSPB as KSPB from YK_CKFS where XTSB=:yksb and CKFS=:ckfs");
		StringBuffer hql_yfsb = new StringBuffer();// 查询出库方式对应的药房识别
		hql_yfsb.append("select YFSB as YFSB from YF_LYFS where YKSB=:yksb and LYFS=:ckfs ");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")));
		map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
		try {
			ret = dao.doLoad(hql_dyfs.toString(), map_par);
			Map<String, Object> map_yfsb = dao.doLoad(hql_yfsb.toString(),
					map_par);
			if (map_yfsb == null) {
				ret.put("YFSB", 0);
			} else {
				ret.put("YFSB", MedicineUtils.parseLong(map_yfsb.get("YFSB")));
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "对应方式查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-3
	 * @description 出库记录保存
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveStorehouseCheckOut(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> list_ck02 = (List<Map<String, Object>>) body
				.get("YK_CK02");
		Map<String, Object> map_ck01 = (Map<String, Object>) body
				.get("YK_CK01");
		long yksb =MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"));// 用户的药库识别
		String op = body.get("op") + "";
		try {
			if ("create".equals(op)) {
				StringBuffer hql_ckdh = new StringBuffer();
				StringBuffer hql_ckdh_update = new StringBuffer();
				hql_ckdh.append("select CKDH as CKDH from YK_CKFS where CKFS=:ckfs and XTSB=:xtsb");
				hql_ckdh_update
						.append("update YK_CKFS set CKDH=CKDH+1 where CKFS=:ckfs and XTSB=:xtsb");
				Map<String, Object> map_par_ckdh = new HashMap<String, Object>();
				map_par_ckdh.put("ckfs", MedicineUtils.parseInt(map_ck01.get("CKFS")));
				map_par_ckdh.put("xtsb", yksb);
				Map<String, Object> map_ckdh = dao.doLoad(hql_ckdh.toString(),
						map_par_ckdh);
				int ckdh = MedicineUtils.parseInt(map_ckdh.get("CKDH"));
				map_ck01.put("CKDH", ckdh);
				map_ck01.put("XTSB", yksb);
				map_ck01.put("CZGH", MedicineUtils.parseString(UserRoleToken.getCurrent().getUserId()));
				// map_ck01.put("CKKS",
				// parseLong(map_ck01.get("CKKS")==null?0:map_ck01.get("CKKS")));
				dao.doSave("create", BSPHISEntryNames.YK_CK01_FORM, map_ck01,
						false);
				for (Map<String, Object> map_ck02 : list_ck02) {
					map_ck02.put("CKDH", ckdh);
					map_ck02.put("CKFS", MedicineUtils.parseInt(map_ck01.get("CKFS")));
					map_ck02.put("XTSB", yksb);
					// map_ck02.put("SFSL", parseDouble(map_ck02.get("SQSL")));
					dao.doSave("create", BSPHISEntryNames.YK_CK02_LIST,
							map_ck02, false);
				}
				dao.doUpdate(hql_ckdh_update.toString(), map_par_ckdh);
			} else {
				StringBuffer hql_ck02_delete = new StringBuffer();
				hql_ck02_delete
						.append("delete from YK_CK02 where CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("ckdh", MedicineUtils.parseInt(map_ck01.get("CKDH")));
				map_par.put("ckfs", MedicineUtils.parseInt(map_ck01.get("CKFS")));
				map_par.put("xtsb", MedicineUtils.parseLong(map_ck01.get("XTSB")));
				Map<String, Object> r = verificationCheckOutDelete(map_par);
				if (MedicineUtils.parseInt(r.get("code")) != 200) {
					return r;
				}
				dao.doUpdate(hql_ck02_delete.toString(), map_par);
				for (Map<String, Object> map_ck02 : list_ck02) {
					map_ck02.put("CKDH", MedicineUtils.parseInt(map_ck01.get("CKDH")));
					map_ck02.put("CKFS", MedicineUtils.parseInt(map_ck01.get("CKFS")));
					map_ck02.put("XTSB", yksb);
					dao.doSave("create", BSPHISEntryNames.YK_CK02_LIST,
							map_ck02, false);
				}
				dao.doSave("update", BSPHISEntryNames.YK_CK01_FORM, map_ck01,
						false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "出库单保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "出库单保存失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 药库出库form记录查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryStorehouseCheckOut(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		int ksly = MedicineUtils.parseInt(body.get("ksly"));
		Map<String, Object> ret = new HashMap<String, Object>();
		if (ksly == 1) {
			hql.append("select a.JGID as JGID,a.XTSB as XTSB,a.CKFS as CKFS,c.FSMC as FSMC," +
					" a.CKDH as CKDH,a.YFSB as YFSB,a.CKPB as CKPB,a.SQRQ as SQRQ,a.CKRQ as CKRQ," +
					" b.OFFICENAME as KSMC,a.CKKS as CKKS,a.CKBZ as CKBZ,a.CZGH as CZGH,a.QRGH as QRGH," +
					" a.SQTJ as SQTJ,a.LYRQ as LYRQ,a.LYPB as LYPB,a.LYGH as LYGH from YK_CK01 a " +
					" left join SYS_Office b on a.CKKS=b.ID " +
					" join YK_CKFS c on (a.CKFS=c.CKFS and a.XTSB=c.XTSB) " +
					" where a.CKDH=:ckdh and a.CKFS=:ckfs and a.XTSB=:xtsb ");
		} else {
			hql.append("select a.JGID as JGID,a.XTSB as XTSB,a.CKFS as CKFS,c.FSMC as FSMC,a.CKDH as CKDH," +
					" a.YFSB as YFSB,a.CKPB as CKPB,a.SQRQ as SQRQ,a.CKRQ as CKRQ,a.CKKS as CKKS," +
					" a.CKBZ as CKBZ,a.CZGH as CZGH,a.QRGH as QRGH,a.SQTJ as SQTJ,a.LYRQ as LYRQ," +
					" a.LYPB as LYPB,a.LYGH as LYGH from YK_CK01 a,YK_CKFS c " +
					" where  a.CKFS=c.CKFS and a.XTSB=c.XTSB and a.CKDH=:ckdh " +
					" and a.CKFS=:ckfs and a.XTSB=:xtsb ");
		}
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ckdh", MedicineUtils.parseInt(body.get("ckdh")));
		map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("xtsb")));
		try {
			ret = dao.doSqlLoad(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "出库单查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 查询药品的库存数量
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryKcsl(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		Map<String, Object> map_par = new HashMap<String, Object>();
		if (body.containsKey("KCSB") 
				&& MedicineUtils.parseLong(body.get("KCSB")) != 0) {
			hql.append("select KCSL as KCSL from YK_KCMX where SBXH=:kcsb");
			map_par.put("kcsb", MedicineUtils.parseLong(body.get("KCSB")));
		} else {
			hql.append("select sum(KCSL) as KCSL from YK_KCMX  where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
			map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
			map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
			map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
		}
		try {
			Map<String, Object> map_kcsl = dao.doLoad(hql.toString(), map_par);
			if(map_kcsl.get("KCSL")==null || map_kcsl.get("KCSL")=="" || "".equals(map_kcsl.get("KCSL")) || "null".equals(map_kcsl.get("KCSL"))){
				ret.put("YKKC", MedicineUtils.parseDouble("0"));
			}else{
				ret.put("YKKC", MedicineUtils.parseDouble(map_kcsl.get("KCSL")));
			}
			if (body.containsKey("YFSB") && MedicineUtils.parseLong(body.get("YFSB")) != 0) {
				StringBuffer hql_yfkc = new StringBuffer();
				Map<String, Object> map_par_yfkc = new HashMap<String, Object>();
				if(body.containsKey("YFKCSB") && MedicineUtils.parseLong(body.get("YFKCSB")) != 0){
					hql_yfkc.append("select YPSL from YF_KCMX where SBXH=:sbxh");
					map_par_yfkc.put("sbxh", MedicineUtils.parseLong(body.get("YFKCSB")));
				}else{
					hql_yfkc.append("select sum(YPSL) as YFKC from YF_KCMX  where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd group by YPXH,YPCD,YFSB");
					map_par_yfkc.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
					map_par_yfkc.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
					map_par_yfkc.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
				}
				List<Map<String, Object>> list_yfkc = dao.doSqlQuery(
						hql_yfkc.toString(), map_par_yfkc);
				if (list_yfkc == null || list_yfkc.size() == 0
						|| list_yfkc.get(0) == null) {
					ret.put("YFKC", 0);
				} else {
					ret.put("YFKC", list_yfkc.get(0).get("YFKC"));
				}
			} else {
				ret.put("YFKC", 0);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存数量查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 确认出库单
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveStorehouseCheckOutCommit(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Date nowDate = new Date();
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId();// 用户ID
		List<Map<String, Object>> list_ck02 = (List<Map<String, Object>>) body
				.get("YK_CK02");
		Map<String, Object> map_ck01 = (Map<String, Object>) body
				.get("YK_CK01");
		int ckfs = MedicineUtils.parseInt(map_ck01.get("CKFS"));
		int ckdh = MedicineUtils.parseInt(map_ck01.get("CKDH"));
		long xtsb = MedicineUtils.parseLong(map_ck01.get("XTSB"));
		long yfsb = MedicineUtils.parseLong(map_ck01.get("YFSB"));
		StringBuffer hql_verify = new StringBuffer();// 确认前验证是否删除确认月结
		hql_verify.append(" CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb ");
		StringBuffer hql_tj = new StringBuffer();// 判断药品在出库单申请后是否有调价
		hql_tj.append(" a.XTSB=b.XTSB and a.TJFS=b.TJFS and a.TJDH=b.TJDH and a.ZXRQ>:sqrq and b.KCSB=:kcsb and b.TJSL!=0 and a.ZYPB=1 and a.JGID=:jgid");
		StringBuffer hql_ck02_delete = new StringBuffer();// 删除出库明细
		hql_ck02_delete.append("delete from YK_CK02 where XTSB=:xtsb and CKDH=:ckdh and CKFS=:ckfs");
		Map<String, Object> map_par_verify = new HashMap<String, Object>();	
		map_par_verify.put("ckfs", ckfs);
		map_par_verify.put("ckdh", ckdh);
		map_par_verify.put("xtsb", xtsb);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long l = dao.doCount("YK_CK01",
					hql_verify.toString(), map_par_verify);
			if (l == 0) {
				return MedicineUtils.getRetMap("出库单" + ckdh + "已经被删除!");
			}
			hql_verify.append(" and CKPB=1");
			l = dao.doCount("YK_CK01", hql_verify.toString(),
					map_par_verify);
			if (l > 0) {
				return MedicineUtils.getRetMap("出库单" + ckdh + "已经确认出库!");
			}
			hql_verify = new StringBuffer();
			hql_verify.append("select max(ZZSJ) as ZZSJ from YK_JZJL where XTSB=:xtsb");
			map_par_verify.clear();
			map_par_verify.put("xtsb", xtsb);
			List<Map<String, Object>> list_zzsj = dao.doSqlQuery(
					hql_verify.toString(), map_par_verify);
			if (list_zzsj == null || list_zzsj.size() == 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "出库确认失败,数据异常");
			}
			Map<String, Object> map_zzjs = list_zzsj.get(0);
			if (((Date) map_zzjs.get("ZZSJ")).getTime() > nowDate.getTime()) {
				return MedicineUtils.getRetMap("本月已过账,不能把入库单确认在本月!");
			}
			StringBuffer hql_yfypzf = new StringBuffer();// 确认前判断下药房药品是否作废
			hql_yfypzf.append("YFSB=:yfsb and YPXH=:ypxh and YFZF=1");
			for (Map<String, Object> map_ck02 : list_ck02) {
				Map<String, Object> map_par_yfypzf = new HashMap<String, Object>();
				map_par_yfypzf.put("yfsb", yfsb);
				map_par_yfypzf.put("ypxh", MedicineUtils.parseLong(map_ck02.get("YPXH")));
				l = dao.doCount("YF_YPXX",
						hql_yfypzf.toString(), map_par_yfypzf);
				if (l > 0) {
					return MedicineUtils.getRetMap(map_ck02.get("YPMC") + " 在目标药房已经作废,不能出库操作!");
				}
			}

			// 删除出库明细记录
			Map<String, Object> map_par_ck02_delete = new HashMap<String, Object>();
			map_par_ck02_delete.put("ckfs", ckfs);
			map_par_ck02_delete.put("ckdh", ckdh);
			map_par_ck02_delete.put("xtsb", xtsb);
			dao.doUpdate(hql_ck02_delete.toString(), map_par_ck02_delete);
			for (Map<String, Object> map_ck02 : list_ck02) {
				// 判断药房退药记录申请后有无调价
				if (MedicineUtils.parseDouble(map_ck02.get("SFSL")) < 0) {
					Map<String, Object> map_tj_par = new HashMap<String, Object>();
					map_tj_par
							.put("sqrq", sdf.parse(map_ck01.get("SQRQ") + ""));
				    
					map_tj_par.put("kcsb", MedicineUtils.parseLong(map_ck02.get("KCSB")));
					map_tj_par.put("jgid", map_ck02.get("JGID") + "");
					l = dao.doCount("YK_TJ01 a,YK_TJJL b", hql_tj.toString(),
							map_tj_par);
					if (l > 0) {
						StringBuffer hql_cd = new StringBuffer();
						hql_cd.append("select LSJG as LSJG,PFJG as PFJG,JHJG as JHJG from YK_CDXX where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
						map_tj_par.remove("sqrq");
						map_tj_par.put("ypxh", MedicineUtils.parseLong(map_ck02.get("YPXH")));
						map_tj_par.put("ypcd", MedicineUtils.parseLong(map_ck02.get("YPCD")));
						Map<String, Object> map_cd = dao.doLoad(
								hql_cd.toString(), map_tj_par);
						map_ck02.put("LSJG", MedicineUtils.parseDouble(map_cd.get("LSJG")));
						map_ck02.put("PFJG", MedicineUtils.parseDouble(map_cd.get("PFJG")));
						map_ck02.put("JHJG", MedicineUtils.parseDouble(map_cd.get("JHJG")));
						map_ck02.put("BZLJ", MedicineUtils.parseDouble(map_cd.get("LSJG")));
						map_ck02.put(
								"LSJE",
								MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_cd.get("LSJG"))
										* MedicineUtils.parseDouble(map_ck02.get("SFSL"))));
						map_ck02.put(
								"PFJE",
								MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_cd.get("PFJG"))
										* MedicineUtils.parseDouble(map_ck02.get("SFSL"))));
						map_ck02.put(
								"JHJE",
								MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_cd.get("JHJG"))
										* MedicineUtils.parseDouble(map_ck02.get("SFSL"))));
					}
				}
				double sl = -MedicineUtils.parseDouble(map_ck02.get("SFSL"));
				if (sl < 0) {
					Map<String, Object> map_kcmx = dao.doLoad(BSPHISEntryNames.YK_KCMX,
							MedicineUtils.parseLong(map_ck02.get("KCSB")));
					if (map_kcmx == null
							|| MedicineUtils.parseDouble(map_kcmx.get("KCSL")) + sl < 0) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "产地为"
										+ map_ck02.get("CDMC") + "的药品"
										+ map_ck02.get("YPMC") + " 库存不足");
					} else if (MedicineUtils.parseDouble(map_kcmx.get("KCSL")) + sl == 0) {
						map_ck02.put("LSJG", map_kcmx.get("LSJG"));
						map_ck02.put("JHJG", map_kcmx.get("JHJG"));
						map_ck02.put("PFJG", map_kcmx.get("PFJG"));
						map_ck02.put("LSJG", map_kcmx.get("LSJG"));
						map_ck02.put("LSJE", map_kcmx.get("LSJE"));
						map_ck02.put("PFJE", map_kcmx.get("PFJE"));
						map_ck02.put("JHJE", map_kcmx.get("JHJE"));
						map_ck02.put("BZLJ", map_kcmx.get("BZLJ"));
						map_kcmx.put("KCSL", 0);
						map_kcmx.put("JHJE", 0);
						map_kcmx.put("PFJE", 0);
						map_kcmx.put("LSJE", 0);
						dao.doSave("update", BSPHISEntryNames.YK_KCMX,
								map_kcmx, false);
						updateYkkc(MedicineUtils.parseLong(map_ck02.get("KCSB")));
					} else {
						map_ck02.put("LSJG", map_kcmx.get("LSJG"));
						map_ck02.put("JHJG", map_kcmx.get("JHJG"));
						map_ck02.put("PFJG", map_kcmx.get("PFJG"));
						map_ck02.put("BZLJ", map_kcmx.get("BZLJ"));
						map_ck02.put(
								"LSJE",
								MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
										* MedicineUtils.parseDouble(map_ck02.get("SFSL"))));
						map_ck02.put(
								"PFJE",
								MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_kcmx.get("PFJG"))
										* MedicineUtils.parseDouble(map_ck02.get("SFSL"))));
						map_ck02.put(
								"JHJE",
								MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_kcmx.get("JHJG"))
										* MedicineUtils.parseDouble(map_ck02.get("SFSL"))));
						map_kcmx.put("KCSL", MedicineUtils.parseDouble(map_kcmx.get("KCSL"))
								+ sl);
						map_kcmx.put("JHJE", MedicineUtils.parseDouble(map_kcmx.get("JHJE"))
								- MedicineUtils.parseDouble(map_ck02.get("JHJE")));
						map_kcmx.put("PFJE", MedicineUtils.parseDouble(map_kcmx.get("PFJE"))
								- MedicineUtils.parseDouble(map_ck02.get("PFJE")));
						map_kcmx.put("LSJE", MedicineUtils.parseDouble(map_kcmx.get("LSJE"))
								- MedicineUtils.parseDouble(map_ck02.get("LSJE")));
						dao.doSave("update", BSPHISEntryNames.YK_KCMX,
								map_kcmx, false);
					}
				} else {
					Map<String, Object> map_kcmx = dao.doLoad(BSPHISEntryNames.YK_KCMX,MedicineUtils.parseLong(map_ck02.get("KCSB")));
					if (map_kcmx == null) {
						Map<String, Object> map_kc = new HashMap<String, Object>();
						map_kc.put("SBXH", MedicineUtils.parseLong(map_ck02.get("KCSB")));
						saveKc(sl, map_ck02, nowDate, 1,ctx);// 新增库存
					} else {
						map_kcmx.put("KCSL", MedicineUtils.parseDouble(map_kcmx.get("KCSL"))
								+ sl);
						map_kcmx.put(
								"JHJE",
								MedicineUtils.formatDouble(
										2,
										MedicineUtils.parseDouble(map_kcmx.get("JHJG"))
												* MedicineUtils.parseDouble(map_kcmx
														.get("KCSL"))));
						map_kcmx.put(
								"LSJE",
								MedicineUtils.formatDouble(
										2,
										MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
												* MedicineUtils.parseDouble(map_kcmx
														.get("KCSL"))));
						map_kcmx.put(
								"PFJE",
								MedicineUtils.formatDouble(
										2,
										MedicineUtils.parseDouble(map_kcmx.get("PFJG"))
												* MedicineUtils.parseDouble(map_kcmx
														.get("KCSL"))));
						dao.doSave("update", BSPHISEntryNames.YK_KCMX,
								map_kcmx, false);
					}
					if (MedicineUtils.parseDouble(map_ck02.get("JHJG")) == 0
							&& MedicineUtils.parseDouble(map_ck02.get("YKJH")) != 0) {// 进货价为空的退药记录进行平账
						Map<String, Object> map_pz01 = new HashMap<String, Object>();
						map_pz01.put("JGID", map_ck02.get("JGID") + "");
						map_pz01.put("PZLX", 9);// 平账类型
						map_pz01.put("XTSB", xtsb);
						map_pz01.put("RCFS", 1);// 入出库方式
						map_pz01.put("RCDH", ckdh);// 入出库单号
						map_pz01.put("PZRQ", nowDate);
						map_pz01.put("PZGH", userid);
						map_pz01.put("PZYY", "退库导致进货价格误差");
						map_pz01 = dao.doSave("create",
								BSPHISEntryNames.YK_PZ01, map_pz01, false);
						Map<String, Object> map_pz02 = new HashMap<String, Object>();
						map_pz02.put("PZID", map_pz01.get("PZID"));
						map_pz02.put("JGID", map_ck02.get("JGID") + "");
						map_pz02.put("XTSB", xtsb);
						map_pz02.put("SBXH", MedicineUtils.parseLong(map_ck02.get("SBXH")));
						map_pz02.put("YPXH", MedicineUtils.parseLong(map_ck02.get("YPXH")));
						map_pz02.put("YPCD", MedicineUtils.parseLong(map_ck02.get("YPCD")));
						map_pz02.put("YPPH", map_ck02.get("YPPH"));
						map_pz02.put("YPXQ", map_ck02.get("YPXQ"));
						map_pz02.put("PZSL", MedicineUtils.parseDouble(map_ck02.get("SFSL")));
						map_pz02.put("YJHJ", MedicineUtils.parseDouble(map_ck02.get("JHJG")));
						map_pz02.put("YPFJ", MedicineUtils.parseDouble(map_ck02.get("PFJG")));
						map_pz02.put("YLSJ", MedicineUtils.parseDouble(map_ck02.get("LSJG")));
						map_pz02.put("XPFJ", MedicineUtils.parseDouble(map_ck02.get("PFJG")));
						map_pz02.put("XLSJ", MedicineUtils.parseDouble(map_ck02.get("LSJG")));
						map_pz02.put(
								"YJHE",
								MedicineUtils.formatDouble(
										2,
										MedicineUtils.parseDouble(map_ck02.get("YKJH"))
												* MedicineUtils.parseDouble(map_ck02
														.get("SFSL"))));
						map_pz02.put("YPFE", MedicineUtils.parseDouble(map_ck02.get("PFJE")));
						map_pz02.put("YLSE", MedicineUtils.parseDouble(map_ck02.get("LSJE")));
						map_pz02.put("XJHE", MedicineUtils.parseDouble(map_ck02.get("JHJE")));
						map_pz02.put("XPFE", MedicineUtils.parseDouble(map_ck02.get("PFJE")));
						map_pz02.put("XLSE", MedicineUtils.parseDouble(map_ck02.get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.YK_PZ02,
								map_pz02, false);
					}
				}
				StringBuffer hql_cdxx_update = new StringBuffer();
				hql_cdxx_update
						.append("update YK_CDXX  set KCSL=KCSL+:sl,JHJE=JHJE+:jhje,PFJE=PFJE+:pfje,LSJE=LSJE+:lsje where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
				Map<String, Object> map_par_cdxx = new HashMap<String, Object>();
				map_par_cdxx.put("sl", sl);
				if (sl > 0 && MedicineUtils.parseDouble(map_ck02.get("JHJG")) == 0
						&& MedicineUtils.parseDouble(map_ck02.get("YKJH")) != 0) {
					map_par_cdxx.put(
							"jhje",
							-MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ck02.get("YKJH"))
									* MedicineUtils.parseDouble(map_ck02.get("SFSL"))));
				} else {
					map_par_cdxx
							.put("jhje", -MedicineUtils.parseDouble(map_ck02.get("JHJE")));
				}
				map_par_cdxx.put("lsje", -MedicineUtils.parseDouble(map_ck02.get("LSJE")));
				map_par_cdxx.put("pfje", -MedicineUtils.parseDouble(map_ck02.get("PFJE")));
				if (yfsb != 0) {
					map_ck02.put("LYRQ", nowDate);
				}
				if (map_ck02.get("PFJG") == null
						|| "null".equals(map_ck02.get("PFJG") + "")) {
					map_ck02.put("PFJG", 0);
				}
				map_par_cdxx.put("ypxh", MedicineUtils.parseLong(map_ck02.get("YPXH")));
				map_par_cdxx.put("ypcd", MedicineUtils.parseLong(map_ck02.get("YPCD")));
				map_par_cdxx.put("jgid", map_ck02.get("JGID") + "");
				dao.doUpdate(hql_cdxx_update.toString(), map_par_cdxx);
				dao.doSave("create", BSPHISEntryNames.YK_CK02_LIST_COMMIT,
						map_ck02, false);
			}
			map_ck01.put("CKRQ", nowDate);
			map_ck01.put("CKPB", 1);
			map_ck01.put("QRGH", userid);
			dao.doSave("update", BSPHISEntryNames.YK_CK01_FORM, map_ck01, false);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "出库确认失败", e);
		}  catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "出库确认失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "出库确认失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 药库出库确认明细查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> queryCheckOutDetail(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		List<Map<String, Object>> list_ck02 = new ArrayList<Map<String, Object>>();
		long xtsb = MedicineUtils.parseLong(body.get("xtsb"));
		int ckdh = MedicineUtils.parseInt(body.get("ckdh"));
		int ckfs = MedicineUtils.parseInt(body.get("ckfs"));
		StringBuffer cnds = new StringBuffer();
		cnds.append("['and',['and',['eq',['$','XTSB'],['d',").append(xtsb)
				.append("]],['eq',['$','CKDH'],['i',").append(ckdh)
				.append("]]],['eq',['$','CKFS'],['i',").append(ckfs)
				.append("]]]");
		/*****add by lizhi at 2017年6月24日 for 药库出库增加出库顺序*****/
		// 出库顺序
		List<String> list_order = new ArrayList<String>();
		//药品效期
		int cksx_ypxq_yk = 1;
		try {
			cksx_ypxq_yk = MedicineUtils.parseInt(ParameterUtil.getParameter(
					jgid, BSPHISSystemArgument.CKSX_YPXQ_YK, ctx));// 是否按效期排序
		} catch (Exception e) {
			MedicineUtils.throwsSystemParameterException(logger,
					BSPHISSystemArgument.CKSX_YPXQ_YK, e);
		}
		if (cksx_ypxq_yk > 0) {
			String cksx_ypxq_order_yk = "A";
			try {
				cksx_ypxq_order_yk = MedicineUtils.parseString(ParameterUtil
						.getParameter(jgid,
								BSPHISSystemArgument.CKSX_YPXQ_ORDER_YK, ctx));
			} catch (Exception e) {
				MedicineUtils.throwsSystemParameterException(logger,
						BSPHISSystemArgument.CKSX_YPXQ_ORDER_YF, e);
			}
			if ("A".equals(cksx_ypxq_order_yk)) {
				list_order.add("YPXQ");
			} else {
				list_order.add("YPXQ desc");
			}
		}
		//出库数量
		int cksx_kcsl_yk = 0;
		try {
			cksx_kcsl_yk = MedicineUtils.parseInt(ParameterUtil.getParameter(
					jgid, BSPHISSystemArgument.CKSX_KCSL_YK, ctx));// 是否按库存排序
		} catch (Exception e) {
			MedicineUtils.throwsSystemParameterException(logger,
					BSPHISSystemArgument.CKSX_KCSL_YK, e);
		}
		if (cksx_kcsl_yk > 0) {
			String cksx_kcsl_order_yk = "A";
			try {
				cksx_kcsl_order_yk = MedicineUtils.parseString(ParameterUtil
						.getParameter(jgid,
								BSPHISSystemArgument.CKSX_KCSL_ORDER_YK, ctx));
			} catch (Exception e) {
				MedicineUtils.throwsSystemParameterException(logger,
						BSPHISSystemArgument.CKSX_KCSL_ORDER_YK, e);
			}
			if ("A".equals(cksx_kcsl_order_yk)) {
				if (list_order.size() > 0) {
					list_order.add(",KCSL");
				} else {
					list_order.add("KCSL");
				}

			} else {
				if (list_order.size() > 0) {
					list_order.add(",KCSL desc");
				} else {
					list_order.add("KCSL desc");
				}
			}
		}
		//批号
		int cksx_ypph_yk = 0;
		try {
			cksx_ypph_yk = MedicineUtils.parseInt(ParameterUtil.getParameter(
					jgid, BSPHISSystemArgument.CKSX_YPPH_YK, ctx));// 是否按库存排序
		} catch (Exception e) {
			MedicineUtils.throwsSystemParameterException(logger,
					BSPHISSystemArgument.CKSX_YPPH_YK, e);
		}
		if (cksx_ypph_yk > 0) {
			String cksx_ypph_order_yk = "A";
			try {
				cksx_ypph_order_yk = MedicineUtils.parseString(ParameterUtil
						.getParameter(jgid,
								BSPHISSystemArgument.CKSX_YPPH_ORDER_YK, ctx));
			} catch (Exception e) {
				MedicineUtils.throwsSystemParameterException(logger,
						BSPHISSystemArgument.CKSX_YPPH_ORDER_YK, e);
			}
			if ("A".equals(cksx_ypph_order_yk)) {
				if (list_order.size() > 0) {
					list_order.add(",YPPH");
				} else {
					list_order.add("YPPH");
				}

			} else {
				if (list_order.size() > 0) {
					list_order.add(",YPPH desc");
				} else {
					list_order.add("YPPH desc");
				}
			}
		}
		/*****add by lizhi at 2017年6月24日 for 药库出库增加出库顺序*****/
		StringBuffer hql_kcmx = new StringBuffer();// 出库里面没有kcsb或者当前kcsb数量不够时查询库存
		hql_kcmx.append(
				"select YPXH as YPXH,YPCD as YPCD,KCSL as KCSL,SBXH as SBXH,YPPH as YPPH,YPXQ as YPXQ,LSJG as LSJG,JHJG as JHJG from YK_KCMX where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid and SBXH!=:sbxh");
		/*****add by lizhi at 2017年6月24日 for 药库出库增加出库顺序*****/
		if (list_order.size() > 0) {
			StringBuffer order = new StringBuffer();
			order.append(" order by ");
			for (String o : list_order) {
				order.append(o);
			}
			hql_kcmx.append(order.toString());
		}
		/*****add by lizhi at 2017年6月24日 for 药库出库增加出库顺序*****/
		StringBuffer hql_kcmx_sbxh = new StringBuffer();// 带kcsb的库存记录查询
		hql_kcmx_sbxh.append("select KCSL as KCSL,YPXQ as YPXQ,YPPH as YPPH from YK_KCMX where SBXH=:kcsb");
		StringBuffer hql_yfkc = new StringBuffer();//查询药房库存
		hql_yfkc.append("select sum(YPSL) as YFKC from YF_KCMX where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd  group by YPXH,YPCD,YFSB");
		StringBuffer hql_yfkc_kcsb = new StringBuffer();//查询药房库存(有药房库存识别)
		hql_yfkc_kcsb.append("select YPSL as YFKC from YF_KCMX where SBXH=:sbxh");
		try {
			Map<String, Object> map_par_kcmx = new HashMap<String, Object>();
			map_par_kcmx.put("jgid", jgid);
			list_ck02 = dao.doList(CNDHelper.toListCnd(cnds.toString()), null,
					BSPHISEntryNames.YK_CK02_LIST_COMMIT);
			Map<String,Object> map_ck01=dao.doLoad(CNDHelper.toListCnd(cnds.toString()), BSPHISEntryNames.YK_CK01_FORM);
			long yfsb=MedicineUtils.parseLong(map_ck01.get("YFSB"));
			// 查看时候的,用于查询库存
			if (body.get("isRead") != null && MedicineUtils.parseInt(body.get("isRead")) == 1) {
				for (Map<String, Object> map_ck02 : list_ck02) {
					long kcsb = MedicineUtils.parseLong(map_ck02.get("KCSB"));
					long ypxh = MedicineUtils.parseLong(map_ck02.get("YPXH"));
					long ypcd = MedicineUtils.parseLong(map_ck02.get("YPCD"));
					Map<String, Object> map_par_kcsl = new HashMap<String, Object>();
					Map<String, Object> map_kcsl = null;
//					if (kcsb == 0) {
						StringBuffer hql_kcsl = new StringBuffer();
						hql_kcsl.append("select sum(KCSL) as KCSL from YK_KCMX  where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
						map_par_kcsl.put("jgid", map_ck02.get("JGID")+"");
						map_par_kcsl.put("ypxh", ypxh);
						map_par_kcsl.put("ypcd", ypcd);
						map_kcsl = dao.doLoad(hql_kcsl.toString(), map_par_kcsl);
//					} else {
//						map_par_kcsl.put("kcsb",
//								MedicineUtils.parseLong(map_ck02.get("KCSB")));
//						map_kcsl = dao.doLoad(hql_kcmx_sbxh.toString(),
//								map_par_kcsl);
//					}

					if (map_kcsl == null || map_kcsl.get("KCSL") == null) {
						map_ck02.put("KCSL", 0);
					} else {
						map_ck02.put("KCSL", MedicineUtils.parseDouble(map_kcsl.get("KCSL")));
					}
					if(yfsb!=0){
						StringBuffer hql_yfkc_ck = new StringBuffer();//查询药房库存
						hql_yfkc_ck.append("select sum(YPSL) as YFKC from YF_KCMX where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd ");
						Map<String, Object> map_par_yfkc = new HashMap<String, Object>();
						map_par_yfkc.put("yfsb", yfsb);
						map_par_yfkc.put("ypxh",ypxh);
						map_par_yfkc.put("ypcd", ypcd);
						if(!"".equals(MedicineUtils.parseString(map_ck02.get("YPPH")))){
							hql_yfkc_ck.append(" and YPPH=:ypph");
							map_par_yfkc.put("ypph", map_ck02.get("YPPH"));
						}else{
							hql_yfkc_ck.append(" and YPPH is null");
						}
						if(map_ck02.get("YPXQ")!=null&&!"".equals(map_ck02.get("YPXQ"))){
							hql_yfkc_ck.append(" and YPXQ=:ypxq");
							map_par_yfkc.put("ypxq", map_ck02.get("YPXQ"));
						}else{
							hql_yfkc_ck.append(" and YPXQ is null");
						}
						hql_yfkc_ck.append("  group by YPXH,YPCD,YFSB");
						List<Map<String, Object>> list_yfkc = dao.doSqlQuery(
								hql_yfkc_ck.toString(), map_par_yfkc);
						if (list_yfkc == null || list_yfkc.size() == 0
								|| list_yfkc.get(0) == null) {
							map_ck02.put("YFKC", 0);
						} else {
							map_ck02.put("YFKC", list_yfkc.get(0).get("YFKC"));
						}
					}
					
				}
				return list_ck02;
			}
			List<Map<String, Object>> list_ck02_temp = new ArrayList<Map<String, Object>>();// 存那些单个库存不够的分离出来的出库记录
			for (Map<String, Object> map_ck02 : list_ck02) {
				long ypxh = MedicineUtils.parseLong(map_ck02.get("YPXH"));
				long ypcd = MedicineUtils.parseLong(map_ck02.get("YPCD"));
				map_ck02.put("SFSL", MedicineUtils.parseDouble(map_ck02.get("SQSL")));
				// 药房退药
				if (MedicineUtils.parseDouble(map_ck02.get("SFSL")) < 0) {// 先暂时假设 从药房退药过来的出库单
															// SFSL已经赋值并且有KCSB.以后做完药房退药后确认下
					StringBuffer hql_kcsl = new StringBuffer();
					Map<String, Object> map_par_kcsl = new HashMap<String, Object>();
					if (map_ck02.get("KCSB") != null
							&& MedicineUtils.parseLong(map_ck02.get("KCSB")) != 0) {
						hql_kcsl.append(hql_kcmx_sbxh.toString());
						map_par_kcsl.put("kcsb",
								MedicineUtils.parseLong(map_ck02.get("KCSB")));
					} else {
						hql_kcsl.append("select sum(KCSL) as KCSL from YK_KCMX  where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
						map_par_kcsl.put("jgid", jgid);
						map_par_kcsl.put("ypxh", ypxh);
						map_par_kcsl.put("ypcd", ypcd);
					}
					List<Map<String, Object>> list_kcsl = dao.doSqlQuery(
							hql_kcsl.toString(), map_par_kcsl);
					for (Map<String, Object> map_kcsl : list_kcsl) { 
						if (map_kcsl == null || map_kcsl.get("KCSL") == null) {
							map_ck02.put("KCSL", 0);
						} else {
							map_ck02.put("KCSL", MedicineUtils.parseDouble(map_kcsl.get("KCSL")));
						}
					}
					continue;
				}
				double yjsl = MedicineUtils.parseDouble(map_ck02.get("SQSL"));// 需要从库存表扣除的数量
				boolean insert = false;// 判断是否要增加临时记录
				map_par_kcmx.put("sbxh", 0l);
				// 出库记录有库存识别
				if (map_ck02.get("KCSB") != null
						&& MedicineUtils.parseLong(map_ck02.get("KCSB")) != 0) {
					Map<String, Object> map_par_kcsl = new HashMap<String, Object>();
					map_par_kcsl.put("kcsb", MedicineUtils.parseLong(map_ck02.get("KCSB")));
					Map<String, Object> map_kcsl = dao.doLoad(
							hql_kcmx_sbxh.toString(), map_par_kcsl);
					if (map_kcsl != null) {
						if (MedicineUtils.parseDouble(map_kcsl.get("KCSL")) >= yjsl) {
							if (map_kcsl.get("KCSL") == null || map_kcsl.get("KCSL") == "" || "".equals(map_kcsl.get("KCSL")) || "null".equals(map_kcsl.get("KCSL")) ) {
								map_ck02.put("KCSL", MedicineUtils.parseDouble("0"));
							} else{
								map_ck02.put("KCSL", MedicineUtils.parseDouble(map_kcsl.get("KCSL")));
							} 
							map_ck02.put("YPXQ", map_kcsl.get("YPXQ"));
							map_ck02.put("YPPH",
									map_kcsl.get("YPPH") == null ? ""
											: map_kcsl.get("YPPH"));
							yjsl = 0;
						} else {
							double kcsl = MedicineUtils.parseDouble(map_kcsl.get("KCSL"));
							if (map_kcsl.get("KCSL") == null || map_kcsl.get("KCSL") == "" || "".equals(map_kcsl.get("KCSL")) || "null".equals(map_kcsl.get("KCSL")) ) {
								kcsl=0;
							}  
							map_ck02.put("KCSL", kcsl);
							map_ck02.put("SQSL", kcsl);
							map_ck02.put("SFSL", kcsl);
							map_ck02.put(
									"LSJE",
									MedicineUtils.formatDouble(
											2,
											kcsl
													* MedicineUtils.parseDouble(map_ck02
															.get("LSJG"))));
							map_ck02.put(
									"JHJE",
									MedicineUtils.formatDouble(
											2,
											kcsl
													* MedicineUtils.parseDouble(map_ck02
															.get("JHJG"))));
							yjsl -= kcsl;
							map_par_kcmx.put("sbxh",
									MedicineUtils.parseLong(map_ck02.get("KCSB")));
							insert = true;
						}
					}
					if (yjsl == 0) {
						continue;
					}
				}
				// 出库记录无KCSB或KCSB对应的库存数量不够
				map_par_kcmx.put("ypxh", ypxh);
				map_par_kcmx.put("ypcd", ypcd);
				List<Map<String, Object>> list_kcmx = dao.doQuery(
						hql_kcmx.toString(), map_par_kcmx);
				if (list_kcmx == null || list_kcmx.size()==0) {// 无库存或库存不够
					if (!insert) {
						map_ck02.put("KCSL", 0);
						map_ck02.put("SFSL", 0);
						map_ck02.put("LSJE", 0);
						map_ck02.put("JHJE", 0);
					} else {
						Map<String, Object> map_ck02_temp = new HashMap<String, Object>();
						for (String key : map_ck02.keySet()) {
							map_ck02_temp.put(key, map_ck02.get(key));
						}
						map_ck02_temp.put("SBXH", null);
						map_ck02_temp.put("KCSL", 0);
						map_ck02_temp.put("SQSL", yjsl);
						map_ck02_temp.put("SFSL", 0);
						map_ck02_temp.put("LSJE", 0);
						map_ck02_temp.put("JHJE", 0);
						map_ck02_temp.put("YPXQ", null);
						map_ck02_temp.put("YPPH", "");
						map_ck02_temp.put("LSJE", 0);
						map_ck02_temp.put("PFJE", 0);
						map_ck02_temp.put("JHJE", 0);
						map_ck02_temp.put("KCSB", 0);
						list_ck02_temp.add(map_ck02_temp);
					}
					continue;
				}
				for (int i = 0; i < list_kcmx.size(); i++) {
					Map<String, Object> map_kcmx = list_kcmx.get(i);
					if (MedicineUtils.parseDouble(map_kcmx.get("KCSL")) >= yjsl) {// 库存数量足够
						if (!insert) {// 第一条库存就数量足够(不带KCSB或者带KCSB但库存数量为0)
							if (map_kcmx.get("KCSL") == null || map_kcmx.get("KCSL") == "" || "".equals(map_kcmx.get("KCSL")) || "null".equals(map_kcmx.get("KCSL")) ) {
								 map_ck02.put("KCSL", MedicineUtils.parseDouble("0"));
							} else {
								 map_ck02.put("KCSL", MedicineUtils.parseDouble(map_kcmx.get("KCSL")));
							} 
							map_ck02.put("YPXQ", map_kcmx.get("YPXQ"));
							map_ck02.put("YPPH",
									map_kcmx.get("YPPH") == null ? ""
											: map_kcmx.get("YPPH"));
							map_ck02.put("KCSB",
									MedicineUtils.parseLong(map_kcmx.get("SBXH")));
						} else {// 分裂一条出库记录
							flckjl(list_ck02_temp, map_ck02, map_kcmx, yjsl,
									yjsl);
						}
						yjsl = 0;
					} else {// 查出来的库存不够
						if (!insert) {
							if (map_kcmx.get("KCSL") == null || map_kcmx.get("KCSL") == "" || "".equals(map_kcmx.get("KCSL")) || "null".equals(map_kcmx.get("KCSL")) ) {
								map_ck02.put("KCSL", MedicineUtils.parseDouble("0"));
							} else {
								 map_ck02.put("KCSL", MedicineUtils.parseDouble(map_kcmx.get("KCSL")));
							}
							
							if (i == list_kcmx.size() - 1) {
								map_ck02.put("SQSL", yjsl);
							} else {
								map_ck02.put("SQSL",
										MedicineUtils.parseDouble(map_kcmx.get("KCSL")));
							}
							map_ck02.put("SFSL",
									MedicineUtils.parseDouble(map_kcmx.get("KCSL")));
							map_ck02.put("YPXQ", map_kcmx.get("YPXQ"));
							map_ck02.put("YPPH",
									map_kcmx.get("YPPH") == null ? ""
											: map_kcmx.get("YPPH"));
							map_ck02.put("LSJG",
									MedicineUtils.parseDouble(map_kcmx.get("LSJG")));
							map_ck02.put("JHJG",
									MedicineUtils.parseDouble(map_kcmx.get("JHJG")));
							map_ck02.put(
									"LSJE",
									MedicineUtils.formatDouble(
											2,
											MedicineUtils.parseDouble(map_ck02.get("SFSL"))
													* MedicineUtils.parseDouble(map_kcmx
															.get("LSJG"))));
							map_ck02.put(
									"PFJE",
									MedicineUtils.formatDouble(
											2,
											MedicineUtils.parseDouble(map_ck02.get("SFSL"))
													* MedicineUtils.parseDouble(map_kcmx
															.get("PFJG"))));
							map_ck02.put(
									"JHJE",
									MedicineUtils.formatDouble(
											2,
											MedicineUtils.parseDouble(map_ck02.get("SFSL"))
													* MedicineUtils.parseDouble(map_kcmx
															.get("JHJG"))));
							map_ck02.put("KCSB",
									MedicineUtils.parseLong(map_kcmx.get("SBXH")));
							insert = true;
						} else {// 分裂一条出库记录
							double kcsl =MedicineUtils.parseDouble(map_kcmx.get("KCSL"));
							if (i == list_kcmx.size() - 1) {
								flckjl(list_ck02_temp, map_ck02, map_kcmx,
										yjsl, kcsl);
							} else {
								flckjl(list_ck02_temp, map_ck02, map_kcmx,
										kcsl, kcsl);
							}
						}
						yjsl -= MedicineUtils.parseDouble(map_kcmx.get("KCSL"));
					}
					if (yjsl == 0) {
						break;
					}
				}
			}
			for (Map<String, Object> map_ck02_temp : list_ck02_temp) {
				list_ck02.add(map_ck02_temp);
			}
			Collections.sort(list_ck02, new Comparator() {
				@Override
				public int compare(Object o1, Object o2) {
					return new Long(((Map<String, Object>) o1).get("YPXH") + "")
							.compareTo(new Long(((Map<String, Object>) o2)
									.get("YPXH") + ""));
				}
			});
			if(yfsb!=0){
				for(Map<String,Object> map_ck02:list_ck02){
					Map<String, Object> map_par_yfkc = new HashMap<String, Object>();
					List<Map<String, Object>> list_yfkc=null;
					if(map_ck02.containsKey("YFKCSB")&&MedicineUtils.parseLong(map_ck02.get("YFKCSB"))!=0){
						map_par_yfkc.put("sbxh", MedicineUtils.parseLong(map_ck02.get("YFKCSB")));
						list_yfkc=dao.doSqlQuery(
								hql_yfkc_kcsb.toString(), map_par_yfkc);
					}else{
						map_par_yfkc.put("yfsb", yfsb);
						map_par_yfkc.put("ypxh",MedicineUtils.parseLong(map_ck02.get("YPXH")));
						map_par_yfkc.put("ypcd", MedicineUtils.parseLong(map_ck02.get("YPCD")));
						list_yfkc= dao.doSqlQuery(
								hql_yfkc.toString(), map_par_yfkc);
					}
					if (list_yfkc == null || list_yfkc.size() == 0
							|| list_yfkc.get(0) == null) {
						map_ck02.put("YFKC", 0);
					} else {
						map_ck02.put("YFKC", list_yfkc.get(0).get("YFKC"));
					}
				}
				}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "出库确认明细查询失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "出库确认明细查询失败", e);
		}
		return list_ck02;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 药库出库明细分裂(一条库存数量不够出库数量时分裂出多条出库记录)
	 * @updateInfo
	 * @param list_ck02_temp
	 * @param map_ck02
	 * @param map_kcmx
	 * @param sqsl
	 * @param sfsl
	 */
	public void flckjl(List<Map<String, Object>> list_ck02_temp,
			Map<String, Object> map_ck02, Map<String, Object> map_kcmx,
			double sqsl, double sfsl) {
		Map<String, Object> map_ck02_temp = new HashMap<String, Object>();
		for (String key : map_ck02.keySet()) {
			map_ck02_temp.put(key, map_ck02.get(key));
		}
		double kcsl = MedicineUtils.parseDouble("0");
		if (map_kcmx.get("KCSL") == null || map_kcmx.get("KCSL") == "" || "".equals(map_kcmx.get("KCSL")) || "null".equals(map_kcmx.get("KCSL")) ) {
		  kcsl = MedicineUtils.parseDouble("0");
		}else{
		  kcsl = MedicineUtils.parseDouble(map_kcmx.get("KCSL"));
		}
		map_ck02_temp.put("SBXH", null);
		map_ck02_temp.put("KCSL", kcsl);
		map_ck02_temp.put("SQSL", sqsl);
		map_ck02_temp.put("SFSL", sfsl);
		map_ck02_temp.put("YPXQ", map_kcmx.get("YPXQ"));
		map_ck02_temp.put("YPPH",
				map_kcmx.get("YPPH") == null ? "" : map_kcmx.get("YPPH"));
		map_ck02_temp.put("LSJG", MedicineUtils.parseDouble(map_kcmx.get("LSJG")));
		map_ck02_temp.put("JHJG", MedicineUtils.parseDouble(map_kcmx.get("JHJG")));
		map_ck02_temp.put("LSJE",
				MedicineUtils.formatDouble(4, sfsl * MedicineUtils.parseDouble(map_kcmx.get("LSJG"))));
		map_ck02_temp.put("PFJE",
				MedicineUtils.formatDouble(4, sfsl * MedicineUtils.parseDouble(map_kcmx.get("PFJG"))));
		map_ck02_temp.put("JHJE",
				MedicineUtils.formatDouble(4, sfsl * MedicineUtils.parseDouble(map_kcmx.get("JHJG"))));
		map_ck02_temp.put("KCSB", MedicineUtils.parseLong(map_kcmx.get("SBXH")));
		list_ck02_temp.add(map_ck02_temp);
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-11-12
	 * @description 根据定价公式计算零售价格
	 * @updateInfo
	 * @param body
	 * @return
	 */
	public double jsLsjg(Map<String, Object> body) {
		double lsjg=MedicineUtils.parseDouble(body.get("LSJG"));
		double jhjg=MedicineUtils.parseDouble(body.get("JHJG"));
		String djgs=MedicineUtils.parseString(body.get("DJGS"));
		djgs=djgs.replaceAll("实际进价", jhjg+"");
		djgs=djgs.replaceAll("标准零价", lsjg+"");
		 CalculatorIn cal=new CalculatorIn();
		 return cal.js(djgs);
	}

}
