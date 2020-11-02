package phis.application.pha.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

/**
 * 药房出入库管理Model
 * 
 * @author caijy
 * 
 */
public class PharmacyCheckInOutManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyCheckInOutManageModel.class);

	public PharmacyCheckInOutManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 打开入库单提交页面前校验数据是否已经删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationCheckInDelete(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql_rk_isDelete = new StringBuffer();// 入库记录是否已经被删除
		hql_rk_isDelete.append(" RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs ");
		StringBuffer hql_rk_isCommit = new StringBuffer();// 入库记录是否已经被确认
		hql_rk_isCommit
				.append(" RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs and RKPB=1");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("rkdh", MedicineUtils.parseInt(body.get("RKDH")));
		parameters.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		parameters.put("rkfs", MedicineUtils.parseInt(body.get("RKFS")));
		try {
			Long l = dao.doCount("YF_RK01", hql_rk_isDelete.toString(),
					parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("该入库单已经删除,请刷新页面");
			}
			l = dao.doCount("YF_RK01", hql_rk_isCommit.toString(), parameters);
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
	 * @createDate 2014-1-9
	 * @description 入库单删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removeCheckInData(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("rkdh", MedicineUtils.parseInt(body.get("RKDH")));
		map_par.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		map_par.put("rkfs", MedicineUtils.parseInt(body.get("RKFS")));
		StringBuffer hql_count = new StringBuffer();
		hql_count
				.append(" RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs and RKPB=0");
		try {
			Long l = dao.doCount("YF_RK01", hql_count.toString(), map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("记录已确定入库,请刷新页面",
						ServiceCode.CODE_RECORD_USING);
			}
		} catch (PersistentDataOperationException e1) {
			MedicineUtils.throwsException(logger, "查询是否确定入库失败", e1);
		}
		StringBuffer deleteHql = new StringBuffer();
		deleteHql
				.append("delete from YF_RK02  where RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs");
		try {
			dao.doUpdate(deleteHql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除入库明细失败", e);
		}
		deleteHql = new StringBuffer();
		deleteHql
				.append("delete from YF_RK01  where RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs");
		try {
			dao.doUpdate(deleteHql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除入库记录失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 页面查询条件里面的默认财务月份
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String initDateQuery(Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int yjDate=32;
		try{
		yjDate = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid,
				"YJSJ_YF" + yfsb,
				BSPHISSystemArgument.defaultValue.get("YJSJ_YF"),BSPHISSystemArgument.defaultAlias.get("YJSJ_YF"),BSPHISSystemArgument.defaultCategory.get("YJSJ_YF"), ctx));// 月结日
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, "YJSJ_YF" + yfsb, e);
		}
		Calendar a = Calendar.getInstance();
		int lastDate = a.get(Calendar.DATE);
		if (lastDate > yjDate) {
			a.set(Calendar.MONTH, a.get(Calendar.MONTH) + 1);
		}
		int month = a.get(Calendar.MONTH) + 1;
		if (month < 10) {
			return a.get(Calendar.YEAR) + "-0" + month;
		}
		return a.get(Calendar.YEAR) + "-" + month;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 入库和出库的条件中的出入库日期范围查询
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
		if (!MedicineUtils.verificationPharmacyId(ctx)) {
			l.add("900");
			l.add("请先设置药房");
			return l;
		}
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String begin = MedicineUtils.parseString(body.get("begin"));// 页面财务月份起始时间
		String end = MedicineUtils.parseString(body.get("end"));// 页面财务月份的结束时间
		String prior_begin = MedicineUtils.parseString(body.get("prior_begin"));// 界面财务月份前一个月的月初时间
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> sparameters = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		try {
			parameters.put("yfsb", yfsb);
			parameters.put("begin", sd.parse(begin));
			parameters.put("end", sd.parse(end));
			sparameters.put("yfsb", yfsb);
			sparameters.put("begin", sd.parse(prior_begin));
			sparameters.put("end", sd.parse(begin));
			StringBuffer hql = new StringBuffer();
			StringBuffer shql = new StringBuffer();
			hql.append("select max(QSSJ) as QSSJ from YF_JZJL  where YFSB=:yfsb and CKBH=0 and CWYF>:begin and CWYF<:end and QSSJ !=ZZSJ ");
			shql.append("select max(ZZSJ) as ZZSJ from YF_JZJL where YFSB=:yfsb and CKBH=0 and CWYF>:begin and CWYF<:end and QSSJ !=ZZSJ ");
			Map<String, Object> qssj = dao.doLoad(hql.toString(), parameters);
			if (qssj.get("QSSJ") != null) {
				l.add(sdf.format((Date) qssj.get("QSSJ")));
			} else {
				qssj = dao.doLoad(shql.toString(), sparameters);
				if (qssj.get("ZZSJ") != null) {
					l.add(sdf.format((Date) qssj.get("ZZSJ")));
				} else {
					l.add(begin + " 00:00:00");
				}
			}
			qssj = dao.doLoad(shql.toString(), parameters);
			if (qssj.get("ZZSJ") != null) {
				l.add(sdf.format((Date) qssj.get("ZZSJ")));
			} else {
				qssj = dao.doLoad(hql.toString(), sparameters);
				if (qssj.get("QSSJ") != null) {
					l.add(sdf.format(new Date()));
				} else {
					l.add(end + " 23:59:59");
				}
			}
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期类型转换失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "日期类型转换失败", e);
		}
		return l;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 药品入库单记录保存
	 * @updateInfo
	 * @param body
	 * @param op
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> rk = (Map<String, Object>) body.get("YF_RK01");
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("YF_RK02");
		// long yfsb = parseLong(rk.get("YFSB"));
		int rkfs = MedicineUtils.parseInt(rk.get("RKFS"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("yfsb", yfsb);
		parameters.put("rkfs", rkfs);
		if ("create".equals(op)) {
			rk.put("YFSB", yfsb);
			StringBuffer rkdhHql = new StringBuffer();
			rkdhHql.append("select RKDH as RKDH from YF_RKFS  where YFSB=:yfsb and RKFS=:rkfs");
			Map<String, Object> rkdhMap;
			int rkdh = 0;
			// 更新入库单号
			try {
				rkdhMap = dao.doLoad(rkdhHql.toString(), parameters);
				rkdh = MedicineUtils.parseInt(rkdhMap.get("RKDH"));
				StringBuffer rkdhUpdateHql = new StringBuffer();
				rkdhUpdateHql
						.append("update YF_RKFS  set RKDH=:rkdh where YFSB=:yfsb and RKFS=:rkfs");
				parameters.put("rkdh", rkdh + 1);
				dao.doUpdate(rkdhUpdateHql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "入库单号查询失败", e);
			}
			rk.put("RKDH", rkdh);
			try {
				dao.doSave("create", BSPHISEntryNames.YF_RK01_FORM, rk, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("YFSB", yfsb);
					med.put("RKDH", rkdh);
					med.put("RKFS", rkfs);
					dao.doSave("create", BSPHISEntryNames.YF_RK02, med, false);
				}
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "入库记录保存验证失败", e);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "入库记录保存验证失败", e);
			}
		} else {
			parameters.put("rkdh", rk.get("RKDH"));
			StringBuffer deleteHql = new StringBuffer();
			deleteHql
					.append("delete from YF_RK02  where YFSB=:yfsb and RKFS=:rkfs and RKDH=:rkdh");
			StringBuffer hql_count =new StringBuffer();
			hql_count.append(" RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs ");
			try {
				long l=dao.doCount("YF_RK01", hql_count.toString(), parameters);
				if(l==0){
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "该入库单已经被删除,请刷新页面");
				}
				hql_count.append(" and RKPB=1");
				l=dao.doCount("YF_RK01", hql_count.toString(), parameters);
				if(l>0){
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "该入库单已经入库,请刷新页面");
				}
				dao.doUpdate(deleteHql.toString(), parameters);
				dao.doSave("update", BSPHISEntryNames.YF_RK01_FORM, rk, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("RKDH", rk.get("RKDH"));
					med.put("YFSB", yfsb);
					med.put("RKFS", rkfs);
					dao.doSave("create", BSPHISEntryNames.YF_RK02, med, false);
				}
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "入库记录更新失败", e);
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "入库记录更新验证失败", e);
			}

		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 入库单提交
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveCheckInToInventory(Map<String, Object> body)
			throws ModelDataOperationException {
		int rkdh = MedicineUtils.parseInt(body.get("RKDH"));
		long yfsb = MedicineUtils.parseLong(body.get("YFSB"));
		int rkfs = MedicineUtils.parseInt(body.get("RKFS"));
		StringBuffer rkHql = new StringBuffer();
		rkHql.append(" RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs and RKPB=1");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("rkdh", rkdh);
		parameters.put("yfsb", yfsb);
		parameters.put("rkfs", rkfs);
		try {
			Long l = dao.doCount("YF_RK01", rkHql.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("该入库单已经入库", 9002);
			}
			StringBuffer hql_sc = new StringBuffer();// 记录是否已经删除
			hql_sc.append(" RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs ");
			l = dao.doCount("YF_RK01", hql_sc.toString(), parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("该入库单已经被删除,请刷新页面", 9002);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询是否入库失败", e);
		}
		StringBuffer cnd = new StringBuffer();
		cnd.append("['and',['eq',['$','RKFS'],['i',").append(rkfs)
				.append("]],['and',['eq',['$','RKDH'],['i',").append(rkdh)
				.append("]],['eq',['$','YFSB'],['i',").append(yfsb)
				.append("]]]]");
		List<?> cnds;
		try {
			StringBuffer updateHql = new StringBuffer();
			updateHql
					.append("update YF_RK01  set RKPB=1 where RKDH=:rkdh and YFSB=:yfsb and RKFS=:rkfs");
			dao.doUpdate(updateHql.toString(), parameters);
			cnds = CNDHelper.toListCnd(cnd.toString());
			List<Map<String, Object>> meds = dao.doQuery(cnds, null,
					BSPHISEntryNames.YF_RK02);
			Map<String, Object> sparameter = new HashMap<String, Object>();
			for (int i = 0; i < meds.size(); i++) {
				// 由于数据库里面字段=''不能用 只能用is null做判断 由于有日期存在 联合起来也有问题 故如此写
				// 以后找到好方法后改进
				Map<String, Object> med = meds.get(i);
				StringBuffer kcHql = new StringBuffer();
				Map<String, Object> parameter = new HashMap<String, Object>();
				kcHql.append(" select SBXH as SBXH from YF_KCMX   where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd and LSJG=:lsjg and PFJG=:pfjg and JHJG=:jhjg ");
				if (med.get("YPXQ") == null) {
					kcHql.append(" and YPXQ is null ");
				} else {
					kcHql.append(" and YPXQ=:ypxq");
					parameter.put("ypxq", med.get("YPXQ"));
				}
				if (med.get("YPPH") == null) {
					kcHql.append(" and YPPH is null ");
				} else {
					kcHql.append(" and YPPH=:ypph");
					parameter.put("ypph", med.get("YPPH"));
				}
				parameter.put("lsjg",
						med.get("LSJG") == null ? 0 : med.get("LSJG"));
				parameter.put("pfjg",
						med.get("PFJG") == null ? 0 : med.get("PFJG"));
				parameter.put("jhjg",
						med.get("JHJG") == null ? 0 : med.get("JHJG"));
				parameter.put("yfsb", med.get("YFSB"));
				parameter.put("ypxh", med.get("YPXH"));
				parameter.put("ypcd", med.get("YPCD"));
				Map<String, Object> kc = null;
				List<Map<String, Object>> kcs = dao.doQuery(kcHql.toString(),
						parameter);
				if (kcs != null && kcs.size() > 0) {
					kc = kcs.get(0);
				}
				StringBuffer kcSaveHql = new StringBuffer();
				if (kc != null) {
					kcSaveHql
							.append("update YF_KCMX  set YPSL=YPSL+:ypsl,LSJE=LSJE+:lsje,PFJE=PFJE+:pfje,JHJE=JHJE+:jhje where SBXH=:sbxh");
					sparameter.put("ypsl", med.get("RKSL"));
					sparameter.put("lsje", med.get("LSJE"));
					sparameter.put("pfje", med.get("PFJE"));
					sparameter.put("jhje", med.get("JHJE"));
					sparameter.put("sbxh", kc.get("SBXH"));
					dao.doUpdate(kcSaveHql.toString(), sparameter);
				} else {
					med.put("YPSL", med.get("RKSL"));
					med.put("JYBZ", 0);
					med.put("YKLJ", new Double(0));
					med.put("YKJJ", new Double(0));
					med.put("YKPJ", new Double(0));
					dao.doSave("create", BSPHISEntryNames.YF_KCMX_CSH, med,
							false);
				}
			}
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "库存保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "库存保存失败", e);
		}
		return MedicineUtils.getRetMap("入库成功", 200);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 入库保存和提交时判断有没超过中心控制最大价格
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPriceChanges(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		String tag = MedicineUtils.parseString(body.get("TAG"));
		int row = MedicineUtils.parseInt(body.get("ROW"));
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
		map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getId());
		try {
			if ("rk".equals(tag)) {
				StringBuffer hql_count = new StringBuffer();
				hql_count
						.append(" LSJG>GYLJ and JHJG>GYJJ and YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
				long l = dao.doCount("YK_CDXX", hql_count.toString(), map_par);
				if (l > 0) {
					return MedicineUtils.getRetMap("第" + row
							+ "行价格超过中心控制价格,请先进行调价!",
							ServiceCode.CODE_RECORD_USING);
				}
			} else {
				map_par.put("lsjg", MedicineUtils.parseDouble(body.get("LSJG")));
				map_par.put("jhjg", MedicineUtils.parseDouble(body.get("JHJG")));
				StringBuffer hql_count = new StringBuffer();
				hql_count
						.append(" GYLJ<:lsjg and GYJJ<:jhjg and YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
				long l = dao.doCount("YK_CDXX", hql_count.toString(), map_par);
				if (l > 0) {
					return MedicineUtils.getRetMap("第" + row
							+ "行价格超过中心控制价格,请先进行调价!",
							ServiceCode.CODE_RECORD_USING);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询有没超过中心控制最大价格失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 入库主表回填数据查询(由于入库方式是双主键的数据字典,故只能自己查)
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadCheckIn(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Long yfsb = MedicineUtils.parseLong(body.get("YFSB"));
		int rkfs = MedicineUtils.parseInt(body.get("RKFS"));
		int rkdh = MedicineUtils.parseInt(body.get("RKDH"));
		StringBuffer hql_rk = new StringBuffer();
		Map<String, Object> map_par_rk = new HashMap<String, Object>();
		hql_rk.append("select RKFS as RKFS,JGID as JGID,YFSB as YFSB,CKBH as CKBH,RKDH as RKDH,RKRQ as RKRQ,RKBZ as RKBZ,RKPB as RKPB,CZGH as CZGH from YF_RK01  where RKFS=:rkfs and YFSB=:yfsb and RKDH=:rkdh ");
		map_par_rk.put("yfsb", yfsb);
		map_par_rk.put("rkfs", rkfs);
		map_par_rk.put("rkdh", rkdh);
		Map<String, Object> map_rk = new HashMap<String, Object>();
		try {
			map_rk = dao.doLoad(hql_rk.toString(), map_par_rk);
			SchemaUtil.setDictionaryMassageForForm(map_rk,
					"phis.application.pha.schemas.YF_RK01_FORM");
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "入库数据查询失败", e);
		}
		return map_rk;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 打开出库单提交页面前校验数据是否已经删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationCheckOutDelete(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql_rk_isDelete = new StringBuffer();// 入库记录是否已经被删除
		hql_rk_isDelete.append(" CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ckdh", MedicineUtils.parseInt(body.get("CKDH")));
		parameters.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		parameters.put("ckfs", MedicineUtils.parseInt(body.get("CKFS")));
		try {
			Long l = dao.doCount("YF_CK01", hql_rk_isDelete.toString(),
					parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("该出库单已经删除,请刷新页面",
						ServiceCode.CODE_RECORD_REPEAT);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询出库记录是否被删除失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 出库单删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removeCheckOutData(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ckdh", MedicineUtils.parseInt(body.get("CKDH")));
		parameters.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		parameters.put("ckfs", MedicineUtils.parseInt(body.get("CKFS")));
		StringBuffer hql_count = new StringBuffer();
		hql_count
				.append(" CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs and CKPB=0");
		try {
			Long l = dao.doCount("YF_CK01", hql_count.toString(), parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("记录已确定出库,请刷新页面",
						ServiceCode.CODE_RECORD_USING);
			}
		} catch (PersistentDataOperationException e1) {
			MedicineUtils.throwsException(logger, "查询是否确定入库失败", e1);
		}
		StringBuffer deleteHql = new StringBuffer();
		deleteHql
				.append("delete from YF_CK02 where CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs");
		try {
			dao.doUpdate(deleteHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除出库明细失败", e);
		}
		deleteHql = new StringBuffer();
		deleteHql
				.append("delete from YF_CK01  where CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs");
		try {
			dao.doUpdate(deleteHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除出库记录失败", e);
		}
		return MedicineUtils.getRetMap("记录删除成功", ServiceCode.CODE_OK);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 保存出库记录
	 * @updateInfo
	 * @param body
	 * @param op
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveCheckOut(Map<String, Object> body, String op)
			throws ModelDataOperationException {
		Map<String, Object> ck = (Map<String, Object>) body.get("YF_CK01");
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("YF_CK02");
		long yfsb = MedicineUtils.parseLong(ck.get("YFSB"));
		int ckfs = MedicineUtils.parseInt(ck.get("CKFS"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("yfsb", yfsb);
		parameters.put("ckfs", ckfs);
		if ("create".equals(op)) {
			StringBuffer ckdhHql = new StringBuffer();
			ckdhHql.append("select CKDH as CKDH from YF_CKFS  where YFSB=:yfsb and CKFS=:ckfs");
			Map<String, Object> rkdhMap;
			int ckdh = 0;
			// 更新出库单号
			try {
				rkdhMap = dao.doLoad(ckdhHql.toString(), parameters);
				ckdh = MedicineUtils.parseInt(rkdhMap.get("CKDH"));
				StringBuffer ckdhUpdateHql = new StringBuffer();
				ckdhUpdateHql
						.append("update YF_CKFS  set CKDH=:ckdh where YFSB=:yfsb and CKFS=:ckfs");
				parameters.put("ckdh", ckdh + 1);
				dao.doUpdate(ckdhUpdateHql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "出库单号查询失败", e);
			}
			ck.put("CKDH", ckdh);
			try {
				dao.doSave("create", BSPHISEntryNames.YF_CK01, ck, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("CKDH", ckdh);
					med.put("CKFS", ckfs);
					dao.doSave("create", BSPHISEntryNames.YF_CK02, med, false);
				}
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "出库记录保存验证失败", e);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "出库记录保存失败", e);
			}
		} else {
			parameters.put("ckdh", ck.get("CKDH"));
			StringBuffer hql_count =new StringBuffer();
			hql_count.append(" CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs ");
			StringBuffer deleteHql = new StringBuffer();
			deleteHql
					.append("delete from YF_CK02 where YFSB=:yfsb and CKFS=:ckfs and CKDH=:ckdh");
			try {
				long l = dao.doCount("YF_CK01", hql_count.toString(), parameters);
				if (l == 0) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "该出库单已经删除,请刷新页面");
				}
				hql_count.append(" and CKPB=1");
				l = dao.doCount("YF_CK01", hql_count.toString(), parameters);
				if(l>0){
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "该出库单已经出库");
				}
				dao.doUpdate(deleteHql.toString(), parameters);
				dao.doSave("update", BSPHISEntryNames.YF_CK01, ck, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("CKDH", ck.get("CKDH"));
					med.put("CKFS", ckfs);
					dao.doSave("create", BSPHISEntryNames.YF_CK02, med, false);
				}
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "出库记录更新失败", e);
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "出库记录更新验证失败", e);
			}

		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 出库单提交
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveCheckOutToInventory(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		StringBuffer ckHql = new StringBuffer();
		ckHql.append(" CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs and CKPB=1");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ckdh", MedicineUtils.parseInt(body.get("CKDH")));
		parameters.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		parameters.put("ckfs", MedicineUtils.parseInt(body.get("CKFS")));
		try {
			Long l = dao.doCount("YF_CK01", ckHql.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("该出库单已经出库", 9000);
			}
			StringBuffer hql_sc = new StringBuffer();// 记录是否删除查询
			hql_sc.append(" CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs ");
			l = dao.doCount("YF_CK01", hql_sc.toString(), parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("该出库单已经删除,请刷新页面", 9000);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询是否出库失败", e);
		}
		StringBuffer kcHql = new StringBuffer();
		kcHql.append("select CKSL as YPSL,KCSB as KCSB,YPXH as YPXH,YPCD as YPCD from YF_CK02 where CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs");
		List<Map<String, Object>> map = null;
		try {
			map = dao.doQuery(kcHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询出库药品记录失败", e);
		}
		Map<String, Object> ret = lessInventory(map, ctx);
		if ((Integer) ret.get("code") == 200) {
			StringBuffer updatCkHql = new StringBuffer();
			updatCkHql
					.append("update YF_CK01 set CKPB=1 where CKDH=:ckdh and YFSB=:yfsb and CKFS=:ckfs");
			try {
				dao.doUpdate(updatCkHql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "更新出库表失败", e);
			}
		}

		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 减库存(有库存识别)
	 * @updateInfo
	 * @param kc
	 *            存有库存识别和药品数量的map的集合,map里面一定要有{"KCSB":,"YPSL":}
	 * @param ctx
	 * @return map code200减库存成功,9000减库存失败 msg错误信息,ypmc药品名称,ypxh药品序号(库存不够时才有)
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> lessInventory(List<Map<String, Object>> kc,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> map = MedicineUtils.getRetMap("记录为空", 9000);// 返回的map信息
																		// code200减库存成功,9000减库存失败
																		// msg错误信息,ypmc药品名称,ypxh药品序号(库存不够时才有)
		if (kc == null || kc.size() == 0) {
			return map;
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		StringBuffer kcHql = new StringBuffer();// 查询该药品的当前库存
		kcHql.append("select YPSL as YPSL,YPXH as YPXH from YF_KCMX  where SBXH=:kcsb");
		StringBuffer updateKcHql = new StringBuffer();// 更新库存
		updateKcHql
				.append("update YF_KCMX set YPSL=:ypsl,LSJE=LSJG*:ypsl,JHJE=JHJG*:ypsl,PFJE=PFJG*:ypsl where SBXH=:kcsb");
		StringBuffer ypHql = new StringBuffer();// 查询库存不足的药品名称
		ypHql.append("select YPMC as YPMC from YK_TYPK  where YPXH=:ypxh");
		StringBuffer sql_kc_ls_insert = new StringBuffer();
		sql_kc_ls_insert
				.append("insert into YF_KCMX_LS   select * from YF_KCMX  where YPSL=0 and SBXH=:kcsb");
		StringBuffer hql_kc_update = new StringBuffer();
		hql_kc_update
				.append("delete from YF_KCMX  where YPSL=0 and SBXH=:kcsb");
		try {
			//库存冻结代码
			int SFQYYFYFY= MedicineUtils.parseInt(ParameterUtil
					.getParameter(jgid, BSPHISSystemArgument.SFQYYFYFY, ctx));
			double KCDJTS= MedicineUtils.parseDouble(ParameterUtil
					.getParameter(jgid, BSPHISSystemArgument.KCDJTS, ctx));
//			if(SFQYYFYFY==1){//如果启用库存冻结
//				//先删除过期的冻结库存
//				MedicineCommonModel model=new MedicineCommonModel(dao);
//				model.deleteKCDJ(jgid, ctx);
//			}
			StringBuffer hql_kcdj=new StringBuffer();//查询冻结数量
			hql_kcdj.append("select YPSL as YPSL,YFBZ as YFBZ from YF_KCDJ where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and sysdate-DJSJ<=:kcdjts");
			StringBuffer hql_yfbz=new StringBuffer();//查询药房包装,用于计算冻结的实际数量
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh ");
			StringBuffer hql_kcsl_sum=new StringBuffer();//查询总的库存数量,用于减掉冻结的和当前要发的比较
			hql_kcsl_sum.append("select sum(YPSL) as KCSL from YF_KCMX where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd");
			for (int i = 0; i < kc.size(); i++) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				Map<String, Object> dqyp = kc.get(i);
				if (dqyp == null) {
					return map;
				}
				parameters.put("kcsb",
						MedicineUtils.parseLong(dqyp.get("KCSB")));
				Map<String, Object> map_kc = dao.doLoad(kcHql.toString(),
						parameters);
				if (map_kc == null) {
					map.put("msg", "库存不够");
					if (dqyp.containsKey("YPXH")) {
						parameters.remove("kcsb");
						long ypxh = MedicineUtils.parseLong(dqyp.get("YPXH"));
						parameters.put("ypxh", ypxh);
						String ypmc = (String) dao.doLoad(ypHql.toString(),
								parameters).get("YPMC");
						map.put("ypmc", ypmc);
						map.put("ypxh", ypxh);
					}
					Session session = (Session) ctx.get(Context.DB_SESSION);
					// 库存不够 数据回滚
					session.getTransaction().rollback();
					return map;
				}
				Double ypsl_kc = (Double) map_kc.get("YPSL");
				Double ypsl_dq = MedicineUtils.parseDouble(dqyp.get("YPSL"));
				if(SFQYYFYFY==1){//库存冻结
					long ypxh = MedicineUtils.parseLong(dqyp.get("YPXH"));
					long ypcd = MedicineUtils.parseLong(dqyp.get("YPCD"));
					//long cfsb=MedicineUtils.parseLong(dqyp.get("CFSB"));//有传就是发药,不传就是0
					double djsl=0;//冻结的总数量
					double kcsl=0;//总的库存数量
					Map<String,Object> map_par_kcdj=new HashMap<String,Object>();
					map_par_kcdj.put("ypxh", ypxh);
					map_par_kcdj.put("ypcd", ypcd);
					map_par_kcdj.put("yfsb", yfsb);
					//map_par_kcdj.put("cfsb", cfsb);
					map_par_kcdj.put("kcdjts", KCDJTS);
					List<Map<String,Object>> list_kcdj=dao.doQuery(hql_kcdj.toString(), map_par_kcdj);
					//map_par_kcdj.remove("cfsb");
					map_par_kcdj.remove("kcdjts");
					List<Map<String,Object>> list_kcsl=dao.doSqlQuery(hql_kcsl_sum.toString(), map_par_kcdj);
					if(list_kcsl!=null&&list_kcsl.size()>0&&list_kcsl.get(0)!=null){
						kcsl=MedicineUtils.parseDouble(list_kcsl.get(0).get("KCSL"));
					}
					if(list_kcdj!=null&&list_kcdj.size()>0){
						Map<String,Object> map_par_yfbz=new HashMap<String,Object>();
						map_par_yfbz.put("yfsb", yfsb);
						map_par_yfbz.put("ypxh", ypxh);
						Map<String,Object> map_yfbz=dao.doLoad(hql_yfbz.toString(), map_par_yfbz);
						int yfbz=MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
						for(Map<String,Object> map_kcdj:list_kcdj){
							djsl+=MedicineUtils.formatDouble(2, MedicineUtils.simpleMultiply(2, map_kcdj.get("YPSL"), map_kcdj.get("YFBZ"))/yfbz);
						}
					}
					if(kcsl-djsl<ypsl_dq){//如果库存不够
						map.put("msg", "库存 不够");
						parameters.remove("kcsb");
						parameters.put("ypxh", ypxh);
						String ypmc = (String) dao.doLoad(ypHql.toString(),
								parameters).get("YPMC");
						map.put("ypmc", ypmc);
						map.put("ypxh", ypxh);
						Session session = (Session) ctx.get(Context.DB_SESSION);
						// 库存不够 数据回滚
						session.getTransaction().rollback();
						return map;
					}
				}
				// 库存 不够
				if (ypsl_kc < ypsl_dq) {
					map.put("msg", "库存 不够");
					parameters.remove("kcsb");
					long ypxh = MedicineUtils.parseLong(map_kc.get("YPXH"));
					parameters.put("ypxh", ypxh);
					String ypmc = (String) dao.doLoad(ypHql.toString(),
							parameters).get("YPMC");
					map.put("ypmc", ypmc);
					map.put("ypxh", ypxh);
					Session session = (Session) ctx.get(Context.DB_SESSION);
					// 库存不够 数据回滚
					session.getTransaction().rollback();
					return map;
				}
				
				// 库存足够
				parameters.put("ypsl", ypsl_kc - ypsl_dq);
				dao.doUpdate(updateKcHql.toString(), parameters);
				// 完后删除库存是0的记录
				if (ypsl_kc - ypsl_dq == 0) {
					Map<String, Object> map_ypsl_par = new HashMap<String, Object>();
					map_ypsl_par.put("kcsb",
							MedicineUtils.parseLong(dqyp.get("KCSB")));
					dao.doSqlUpdate(sql_kc_ls_insert.toString(), map_ypsl_par);
					dao.doUpdate(hql_kc_update.toString(), map_ypsl_par);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "减库存失败", e);
		}
		map.put("code", 200);
		map.put("msg", "库存更新成功");
		return map;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 药品出库明细数据查询(无库存也显示)
	 * @updateInfo
	 * @param cnds
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryCheckOutToInventory(
			List<Object> cnds, Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		List<Map<String, Object>> ret = null;
		try {
			hql.append(
					"select a.JGID as JGID,a.SBXH as SBXH,a.YFSB as YFSB,a.CKBH as CKBH,a.CKFS as CKFS,nvl(d.YPSL,0) as YPSL,a.CKDH as CKDH,a.YPXH as YPXH,a.YFBZ as YFBZ,a.PFJG as PFJG,a.PFJE as PFJE,b.YPMC as YPMC ,a.YPGG as YPGG,a.YFDW as YFDW,c.CDMC as CDMC,a.YPCD as YPCD,a.LSJG as LSJG,a.JHJG as JHJG,a.CKSL as CKSL,a.LSJE as LSJE,a.JHJE as JHJE,a.YPPH as YPPH,a.YPXQ as YPXQ,a.KCSB as KCSB,a.LGJLXH as LGJLXH from YK_TYPK b,YK_CDDZ c,YF_CK02 a left outer join YF_KCMX d on a.KCSB=d.SBXH where a.YPXH=b.YPXH and a.YPCD=c.YPCD and ")
					.append(ExpressionProcessor.instance().toString(cnds));
			return dao.doSqlQuery(hql.toString(), null);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "药品出库明细查询失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品出库明细查询失败", e);
		}
		return ret;
	}

}
