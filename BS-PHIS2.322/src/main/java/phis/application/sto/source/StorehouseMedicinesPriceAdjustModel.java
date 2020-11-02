package phis.application.sto.source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.account.organ.ManageUnit;
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
 * 药品调价
 * 
 * @author caijy
 * 
 */
public class StorehouseMedicinesPriceAdjustModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseMedicinesPriceAdjustModel.class);

	public StorehouseMedicinesPriceAdjustModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-6
	 * @description 调价单删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removePriceAdjustData(Map<String, Object> body)
			throws ModelDataOperationException {
		int tjdh = MedicineUtils.parseInt(body.get("TJDH"));
		int tjfs = MedicineUtils.parseInt(body.get("TJFS"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tjdh", tjdh);
		parameters.put("xtsb", MedicineUtils.parseLong(body.get("XTSB")));
		parameters.put("tjfs", tjfs);
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("tjdh", tjdh);
		parameter.put("jgid", MedicineUtils.parseString(body.get("JGID")));
		parameter.put("tjfs", tjfs);
		StringBuffer hql_count = new StringBuffer();
		hql_count
				.append(" TJDH=:tjdh and XTSB=:xtsb and TJFS=:tjfs and ZYPB=0");
		try {
			Long l = dao.doCount("YK_TJ01", hql_count.toString(), parameters);
			if (l == 0) {
				return MedicineUtils.getRetMap("调价单已执行,请刷新页面");
			}
		} catch (PersistentDataOperationException e1) {
			MedicineUtils.throwsException(logger, "查询调价单是否执行失败", e1);
		}
		StringBuffer deleteHql = new StringBuffer();
		deleteHql
				.append("delete from YK_TJ02 where TJDH=:tjdh and JGID=:jgid and TJFS=:tjfs");
		try {
			dao.doUpdate(deleteHql.toString(), parameter);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除调价明细失败", e);
		}
		deleteHql = new StringBuffer();
		deleteHql
				.append("delete from YK_TJ01 where TJDH=:tjdh and XTSB=:xtsb and TJFS=:tjfs");
		try {
			dao.doUpdate(deleteHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除调价记录失败", e);
		}
		return MedicineUtils.getRetMap("记录删除成功", 200);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 提交前判断调价单是否已经删除或提交
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationPriceAdjustDelete(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql_tj_isDelete = new StringBuffer();// 调价记录是否已经被删除
		hql_tj_isDelete.append(" TJDH=:tjdh and XTSB=:xtsb and TJFS=:tjfs ");
		StringBuffer hql_tj_isCommit = new StringBuffer();// 调价记录是否已经被执行
		hql_tj_isCommit
				.append(" TJDH=:tjdh and XTSB=:xtsb and TJFS=:tjfs  and ZYPB=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("tjdh", MedicineUtils.parseInt(body.get("TJDH")));
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("XTSB")));
		map_par.put("tjfs", MedicineUtils.parseInt(body.get("TJFS")));
		try {
			Long l = dao
					.doCount("YK_TJ01", hql_tj_isDelete.toString(), map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("该调价单已经删除");
			}
			l = dao.doCount("YK_TJ01", hql_tj_isCommit.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("该调价单已经确定");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询调价记录是否被删除失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 调价的条件中的调价日期范围查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<String> priceAdjustDateQuery(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		List<String> l = new ArrayList<String>();
		int yjsj=32;
		try{
		yjsj = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid,
				"YJSJ_YK" + yksb,BSPHISSystemArgument.defaultValue.get("YJSJ_YK"),BSPHISSystemArgument.defaultAlias.get("YJSJ_YK"),BSPHISSystemArgument.defaultCategory.get("YJSJ_YK"),ctx));// 月结日
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, "YJSJ_YK" + yksb, e);
		}
		int year = MedicineUtils.parseInt(body.get("year"));
		int month = MedicineUtils.parseInt(body.get("month"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		int last = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		int setDay = yjsj;
		if (yjsj > last) {
			setDay = last;
		}
		c.set(Calendar.DATE, setDay);
		l.add(sdf.format(c.getTime()));
		c.add(Calendar.MONTH, -1);
		last = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (yjsj > last) {
			setDay = last;
		}
		c.set(Calendar.DATE, setDay);
		l.add(sdf.format(c.getTime()));
		return l;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 药库调价单保存
	 * @updateInfo
	 * @param body
	 * @param op
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void savePriceAdjust(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> tj = (Map<String, Object>) body.get("YK_TJ01");
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("YK_TJ02");
		String jgid = MedicineUtils.parseString(tj.get("JGID"));
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 药库识别
		try {
			// 新增时候插入调价单号
			if ("create".equals(op)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String y = sdf.format(new Date()).substring(2, 4);// 年份后2位
				StringBuffer hql_tjdh_select = new StringBuffer();// 调价单号查询
				hql_tjdh_select
						.append("select max(TJDH) as TJDH from YK_TJ01 where str(TJDH) like '")
						.append(y + "%'").append(" and JGID=:jgid");
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("jgid", jgid);
				int tjdh = MedicineUtils.parseInt(y + "0001");// 调价单号
				Map<String, Object> map_tjdh = dao.doLoad(
						hql_tjdh_select.toString(), parameters);
				if (map_tjdh != null && map_tjdh.get("TJDH") != null) {
					String tjdh_five = map_tjdh.get("TJDH") + "";
					if (MedicineUtils.parseInt(tjdh_five.substring(2, 6)) == 9999) {
						logger.error("Price adjustment single query failed");
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"该机构当年调价单号已达最大值");
					}
					tjdh = MedicineUtils.parseInt(map_tjdh.get("TJDH")) + 1;
				}
				tj.put("TJDH", tjdh);
				tj.put("XTSB", yksb);
			}
			// 保存调价主表
			dao.doSave(op, BSPHISEntryNames.YK_TJ01, tj, false);
			StringBuffer hql_tj02_delete = new StringBuffer();// 删除调价明细
			hql_tj02_delete
					.append("delete from YK_TJ02 where JGID=:jgid and TJFS=:tjfs and TJDH=:tjdh");
			Map<String, Object> map_par_delete = new HashMap<String, Object>();
			map_par_delete.put("jgid", jgid);
			map_par_delete.put("tjdh", tj.get("TJDH"));
			map_par_delete.put("tjfs", tj.get("TJFS"));
			dao.doUpdate(hql_tj02_delete.toString(), map_par_delete);
			for (Map<String, Object> med : meds) {
				med.put("XTSB", yksb);
				med.put("TJDH", tj.get("TJDH"));
				med.put("TJFS", tj.get("TJFS"));
				// 保存明细
				dao.doSave("create", BSPHISEntryNames.YK_TJ02, med, false);
			}
		} catch (NumberFormatException e) {
			MedicineUtils.throwsException(logger, "调价单号查询失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "调价单号查询失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "调价单保存验证失败", e);
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 调价单执行数据查询
	 * @updateInfo
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPriceAdjustExecutionData(
			List<Object> cnd) throws ModelDataOperationException {
		List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
		StringBuffer hql_yfsb = new StringBuffer();// 用于查询药几个药房有
		hql_yfsb.append("select a.YFSB as YFSB,b.YFMC as YFMC from YF_YPXX a,YF_YFLB b where a.JGID like:jgid and a.YPXH=:ypxh  and a.YFSB=b.YFSB");
		StringBuffer hql_yfyp = new StringBuffer();// 查询药房药品
		hql_yfyp.append("select yf.YFGG as YFGG,yf.YFBZ as YFBZ,yf.YFDW as YFDW,yp.ZXBZ as ZXBZ,yp.YPMC as YPMC  from YF_YPXX yf,YK_TYPK yp where yf.YPXH=yp.YPXH and  yf.YFSB=:yfsb and yf.YPXH=:ypxh");
		StringBuffer hql_ypkc = new StringBuffer();// 查询药品库存
		hql_ypkc.append("select SBXH as KCSB,YPSL as KCSL,YPPH as YPPH,YPXQ as YPXQ,LSJG as LSJG,JHJG as JHJG,PFJG as PFJG from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb");
		StringBuffer hql_ykkc=new StringBuffer();//查询药库库存
		hql_ykkc.append("select b.YPGG as YPGG,b.YPDW as YFDW,b.YPMC as YPMC,c.CDMC as CDMC,a.SBXH as KCSB,a.KCSL as KCSL,a.JHJG as YJHJ,a.LSJG as YLSJ,a.PFJG as YPFJ,a.YPPH as YPPH,a.YPXQ as YPXQ,a.JHJE as YJHE,a.PFJE as YPFE,a.LSJE as YLSE from YK_KCMX a,YK_TYPK b,YK_CDDZ c where a.YPXH=b.YPXH and a.YPCD=c.YPCD  and a.YPXH=:ypxh and a.YPCD=:ypcd and a.JGID like:jgid");
		try {
			// 遍历药库调价明细
			List<Map<String, Object>> list_tj02 = dao.doList(cnd, null,
					BSPHISEntryNames.YK_TJ02);
			for (Map<String, Object> map_tj02 : list_tj02) {
				//查询药库调价记录
				Map<String,Object> map_par_ykkc=new HashMap<String,Object>();
				map_par_ykkc.put("ypxh", MedicineUtils.parseLong(map_tj02.get("YPXH")));
				map_par_ykkc.put("ypcd", MedicineUtils.parseLong(map_tj02.get("YPCD")));
				map_par_ykkc.put("jgid", map_tj02.get("JGID")+"%");
				List<Map<String,Object>> list_ykkc=dao.doQuery(hql_ykkc.toString(), map_par_ykkc);
				if(list_ykkc==null||list_ykkc.size()==0){//如果没有库存,增加调价数量为0的记录(zw2说没库存的不用新增)
					Map<String,Object> map_ykkc=new HashMap<String,Object>();
					map_ykkc.putAll(map_tj02);
					map_ykkc.put("YFSB", 0);
					map_ykkc.put("YFBZ", 1);
					map_ykkc.put("YPPH", null);
					map_ykkc.put("YPXQ", null);
					map_ykkc.put("KCSB", 0);
					map_ykkc.put("KCSL", 0);
					map_ykkc.put("TJSL", 0);
					map_ykkc.put("YLSE", 0);
					map_ykkc.put("XLSE", 0);
					map_ykkc.put("YJHE", 0);
					map_ykkc.put("XJHE", 0);
					map_ykkc.put("YPFE", 0);
					map_ykkc.put("XPFE", 0);
					list_ret.add(map_ykkc);
				}else{
					for(Map<String,Object> map_ykkc:list_ykkc){
						map_ykkc.put("YFSB", 0);
						map_ykkc.put("YFMC", dao.doLoad("phis.application.sto.schemas.YK_YKLB", MedicineUtils.parseLong(map_tj02.get("XTSB"))).get("YKMC"));
						map_ykkc.put("YFBZ", 1);
						map_ykkc.put("JGID", map_tj02.get("JGID")+"");
						map_ykkc.put("XTSB", MedicineUtils.parseLong(map_tj02.get("XTSB")));
						map_ykkc.put("TJFS", MedicineUtils.parseInt(map_tj02.get("TJFS")));
						map_ykkc.put("TJDH", MedicineUtils.parseInt(map_tj02.get("TJDH")));
						map_ykkc.put("YPXH", MedicineUtils.parseLong(map_tj02.get("YPXH")));
						map_ykkc.put("YPCD", MedicineUtils.parseLong(map_tj02.get("YPCD")));
						map_ykkc.put("TJSL", MedicineUtils.parseDouble(map_ykkc.get("KCSL")));
						map_ykkc.put("XLSJ", MedicineUtils.parseDouble(map_tj02.get("XLSJ")));
						map_ykkc.put("XPFJ", MedicineUtils.parseDouble(map_tj02.get("XPFJ")));
						map_ykkc.put("XJHJ", MedicineUtils.parseDouble(map_tj02.get("XJHJ")));
						map_ykkc.put("XPFE", MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ykkc.get("XPFJ"))*MedicineUtils.parseDouble(map_ykkc.get("KCSL"))));
						map_ykkc.put("XLSE", MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ykkc.get("XLSJ"))*MedicineUtils.parseDouble(map_ykkc.get("KCSL"))));
						map_ykkc.put("XJHE", MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ykkc.get("XJHJ"))*MedicineUtils.parseDouble(map_ykkc.get("KCSL"))));
						list_ret.add(map_ykkc);	
					}
				}
				Map<String, Object> map_par_yfsb = new HashMap<String, Object>();
				map_par_yfsb.put("jgid", map_tj02.get("JGID")+"%");
				map_par_yfsb.put("ypxh", map_tj02.get("YPXH"));
				List<Map<String, Object>> list_yfsb = dao.doQuery(hql_yfsb.toString(),map_par_yfsb);
				// 遍历药房
				for (Map<String, Object> map_yfsb : list_yfsb) {
					String yfmc = (String) map_yfsb.get("YFMC");
					Map<String, Object> map_par_yfyp = new HashMap<String, Object>();
					map_par_yfyp.put("yfsb", map_yfsb.get("YFSB"));
					map_par_yfyp.put("ypxh", map_tj02.get("YPXH"));
					Map<String, Object> map_yfyp = dao.doLoad(hql_yfyp.toString(),map_par_yfyp);
					if (map_yfyp != null && map_yfyp.size() > 0) {
						Map<String, Object> map_tjjl = new HashMap<String, Object>();
						map_tjjl.put("YFMC", yfmc);
						map_tjjl.put("YPGG", map_yfyp.get("YFGG"));
						map_tjjl.put("YPMC", map_yfyp.get("YPMC"));
						map_tjjl.put("YLSJ", MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_tj02.get("YLSJ"))
										* MedicineUtils.parseDouble(map_yfyp.get("YFBZ"))
										/ MedicineUtils.parseDouble(map_yfyp.get("ZXBZ"))));
						map_tjjl.put("XLSJ", MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_tj02.get("XLSJ"))
										* MedicineUtils.parseDouble(map_yfyp.get("YFBZ"))
										/ MedicineUtils.parseDouble(map_yfyp.get("ZXBZ"))));
						map_tjjl.put("YPFJ", MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_tj02.get("YPFJ"))
										* MedicineUtils.parseDouble(map_yfyp.get("YFBZ"))
										/ MedicineUtils.parseDouble(map_yfyp.get("ZXBZ"))));
						map_tjjl.put("XPFJ", MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_tj02.get("XPFJ"))
										* MedicineUtils.parseDouble(map_yfyp.get("YFBZ"))
										/ MedicineUtils.parseDouble(map_yfyp.get("ZXBZ"))));
						map_tjjl.put("YJHJ", MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_tj02.get("YJHJ"))
										* MedicineUtils.parseDouble(map_yfyp.get("YFBZ"))
										/ MedicineUtils.parseDouble(map_yfyp.get("ZXBZ"))));
						map_tjjl.put("XJHJ", MedicineUtils.formatDouble(4,
								MedicineUtils.parseDouble(map_tj02.get("XJHJ"))
										* MedicineUtils.parseDouble(map_yfyp.get("YFBZ"))
										/ MedicineUtils.parseDouble(map_yfyp.get("ZXBZ"))));
						map_tjjl.put("YFDW", map_yfyp.get("YFDW"));
						map_tjjl.put("YFBZ",MedicineUtils.parseInt(map_yfyp.get("YFBZ")));
						map_tjjl.put("YFSB", map_yfsb.get("YFSB"));
						map_tjjl.put("TJDH", map_tj02.get("TJDH"));
						map_tjjl.put("TJFS", map_tj02.get("TJFS"));
						map_tjjl.put("JGID", map_tj02.get("JGID"));
						map_tjjl.put("YKSB", map_tj02.get("YKSB"));
						map_tjjl.put("YPXH", map_tj02.get("YPXH"));
						map_tjjl.put("YPCD", map_tj02.get("YPCD"));
						map_tjjl.put("CDMC", map_tj02.get("CDMC"));
						map_tjjl.put("TJRQ", new Date());
						Map<String, Object> map_par_ypkc = new HashMap<String, Object>();
						map_par_ypkc.put("yfsb", map_yfsb.get("YFSB"));
						map_par_ypkc.put("ypxh", map_tj02.get("YPXH"));
						map_par_ypkc.put("ypcd", map_tj02.get("YPCD"));

						List<Map<String, Object>> list_ypkc = dao.doQuery(hql_ypkc.toString(), map_par_ypkc);
						if (list_ypkc != null && list_ypkc.size() > 0) {
							for (int i = 0; i < list_ypkc.size(); i++) {
								Map<String, Object> map_ypkc = list_ypkc.get(i);
								Map<String, Object> map_tjmx = new HashMap<String, Object>();
								for (String key : map_tjjl.keySet()) {
									map_tjmx.put(key, map_tjjl.get(key));
								}
								//原零售 进货 批发价格从库存表取
								map_tjmx.put("YLSJ", MedicineUtils.parseDouble(map_ypkc.get("LSJG")));
								map_tjmx.put("YJHJ", MedicineUtils.parseDouble(map_ypkc.get("JHJG")));
								map_tjmx.put("YPFJ", MedicineUtils.parseDouble(map_ypkc.get("PFJG")));
								map_tjmx.put("KCSB", map_ypkc.get("KCSB"));
								map_tjmx.put("KCSL", map_ypkc.get("KCSL"));
								map_tjmx.put("YPPH", map_ypkc.get("YPPH"));
								map_tjmx.put("YPXQ", map_ypkc.get("YPXQ"));
								map_tjmx.put("TJJE",MedicineUtils.formatDouble(4,
										MedicineUtils.parseDouble(map_tjmx.get("KCSL"))
										* (MedicineUtils.parseDouble(map_tjmx.get("XLSJ"))
										- MedicineUtils.parseDouble(map_tjmx.get("YLSJ")))));
								map_tjmx.put("YLSE",MedicineUtils.formatDouble(4,
										MedicineUtils.parseDouble(map_tjmx.get("KCSL"))
										* MedicineUtils.parseDouble(map_tjmx.get("YLSJ"))));
								map_tjmx.put("XLSE",MedicineUtils.formatDouble(4,
										MedicineUtils.parseDouble(map_tjmx.get("KCSL"))
										* MedicineUtils.parseDouble(map_tjmx.get("XLSJ"))));
								map_tjmx.put("YJHE",MedicineUtils.formatDouble(4,
										MedicineUtils.parseDouble(map_tjmx.get("KCSL"))
										* MedicineUtils.parseDouble(map_tjmx.get("YJHJ"))));
								map_tjmx.put("XJHE",MedicineUtils.formatDouble(4,
										MedicineUtils.parseDouble(map_tjmx.get("KCSL"))
										* MedicineUtils.parseDouble(map_tjmx.get("XJHJ"))));
								map_tjmx.put("YPFE",MedicineUtils.formatDouble(4,
										MedicineUtils.parseDouble(map_tjmx.get("KCSL"))
										* MedicineUtils.parseDouble(map_tjmx.get("YPFJ"))));
								map_tjmx.put("XPFE",MedicineUtils.formatDouble(4,
										MedicineUtils.parseDouble(map_tjmx.get("KCSL"))
										* MedicineUtils.parseDouble(map_tjmx.get("XPFJ"))));
								map_tjmx.put("TJSL", map_ypkc.get("KCSL"));
								list_ret.add(map_tjmx);
							}
						} 
						else {//zw2说如果没库存,不增加调价记录
							map_tjjl.put("YPPH", null);
							map_tjjl.put("YPXQ", null);
							map_tjjl.put("KCSB", 0);
							map_tjjl.put("KCSL", 0);
							map_tjjl.put("TJJE", 0);
							map_tjjl.put("TJSL", 0);
							map_tjjl.put("YLSE", 0);
							map_tjjl.put("XLSE", 0);
							map_tjjl.put("YJHE", 0);
							map_tjjl.put("XJHE", 0);
							map_tjjl.put("YPFE", 0);
							map_tjjl.put("XPFE", 0);
							list_ret.add(map_tjjl);
						}
					}
				}
			}
//			StringBuffer hql_yktj=new StringBuffer();
//			hql_yktj.append("select sum(KCSL) as KCSL from YK_KCMX where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
//			for(Map<String,Object> map_tj02:list_tj02){
//				Map<String,Object> map_par=new HashMap<String,Object>();
//				map_par.put("ypxh", MedicineUtils.parseLong(map_tj02.get("YPXH")));
//				map_par.put("jgid", MedicineUtils.parseString(map_tj02.get("JGID")));
//				map_par.put("ypcd", MedicineUtils.parseLong(map_tj02.get("YPCD")));
//				List<Map<String,Object>> list_kcsl=dao.doSqlQuery(hql_yktj.toString(), map_par);
//				if(list_kcsl==null||list_kcsl.size()==0){
//					map_tj02.put("TJSL", 0);	
//				}else{
//					map_tj02.put("TJSL", MedicineUtils.parseDouble(list_kcsl.get(0).get("KCSL")));	
//				}
//			}
			//list_ret.addAll(list_tj02);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "调价单执行数据查询失败", e);
		}
		return list_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 药品调价查看数据查询
	 * @updateInfo
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPriceAdjustExecutionedData(
			List<Object> cnd) throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String,Object>>();
		try {
			ret.addAll(dao.doList(cnd, null, BSPHISEntryNames.YF_TJJL_ZX));
			ret.addAll(dao.doList(cnd, null, BSPHISEntryNames.YK_TJJL));
//			ret = dao.doList(cnd, null, BSPHISEntryNames.YF_TJJL_ZX);
//			if (ret == null || ret.size() == 0) {
//				ret = dao.doList(cnd, null, BSPHISEntryNames.YK_TJ02);
//			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "已执行调价单数据查询失败", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 执行调价单
	 * @updateInfo
	 * @param cnd
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePriceAdjustToInventory(List<Map<String,Object>> body,List<Object> cnd,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 药库识别
		String userId = user.getUserId();
		int ckbh = 0;// 窗口编号 暂时写死
		// trunc在db2下用decimal代替
//		StringBuffer hql_jg_update = new StringBuffer();// 更新价格
//		hql_jg_update
//				.append("update YK_CDXX set LSJG=:lsjg ,JHJG=:jhjg,LSJE=round(KCSL*:lsjg,2),JHJE=round(KCSL*:jhjg,2)  where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid and LSJG=:ylsj and JHJG=:yjhj");
		StringBuffer hql_kc_update = new StringBuffer();// 更新库存
		hql_kc_update
				.append("update YF_KCMX  set LSJG=:lsjg ,LSJE=YPSL*:lsjg,JHJG=:jhjg,JHJE=YPSL*:jhjg,YKJJ=:ykjj,YKLJ=:yklj  where SBXH=:kcsb");
		StringBuffer hql_tj_update = new StringBuffer();// 调价记录更新
		hql_tj_update
				.append("update YK_TJ01  set ZYPB=1,ZXRQ=:zxrq,ZXGH=:zxgh where TJDH=:tjdh and TJFS=:tjfs and XTSB=:xtsb");
		StringBuffer hql_sfyj = new StringBuffer();// 用于判断是否月结
		hql_sfyj.append("select max(ZZSJ) as ZZSJ from YF_JZJL where JGID=:jgid");
		StringBuffer hql_sfcsh = new StringBuffer();// 用于判断药房有没初始账簿
		hql_sfcsh.append("YFSB=:yfsb");
		StringBuffer hql_sfrk = new StringBuffer();// 用于判断是否有未确定入库的入库记录
		hql_sfrk.append(" a.YFSB=b.YFSB and a.RKFS=b.RKFS and a.RKDH=b.RKDH and a.YFSB=:yfsb and b.YPCD=:ypcd and b.YPXH=:ypxh and a.RKPB!=1");
		StringBuffer hql_ckjl_wqr = new StringBuffer();// 未确认出库记录,用于调价时更新对应的出库明细
		hql_ckjl_wqr
				.append("select b.SBXH as SBXH,b.KCSB as KCSB from YF_CK01 a,YF_CK02 b where a.CKDH=b.CKDH and a.YFSB=b.YFSB and a.CKFS=b.CKFS and a.JGID=:jgid and a.CKPB!=1");
		StringBuffer hql_ckjl_update = new StringBuffer();// 更新调价已调价库存对应的出库明细
		hql_ckjl_update
				.append("update YF_CK02 set LSJG=:lsjg ,JHJG=:jhjg,LSJE=trunc(:lsjg*CKSL,4),JHJE=trunc(:jhjg*CKSL,4) where SBXH=:sbxh");
		StringBuffer hql_yld = new StringBuffer();// 判断是否有药房未确认领药单
		hql_yld.append("select count(1) as NUM from YK_TJ02 c  where c.XTSB=:yksb and TJFS=:tjfs and TJDH=:tjdh and EXISTS (select 1 from YK_CK01 a,YK_CK02 b where  a.XTSB=c.XTSB and a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.YPXH=c.YPXH and b.YPCD=c.YPCD and a.CKPB=1 and a.LYPB=0 and a.YFSB>0)");
		StringBuffer hql_ty = new StringBuffer();// 判断是否有药房退药,药库未确认领药单
		hql_ty.append("select count(1) as NUM from YK_TJ02  c where c.XTSB=:yksb and TJFS=:tjfs and TJDH=:tjdh and EXISTS (select 1 from YK_CK01 a,YK_CK02 b,YK_CKFS  d where a.XTSB=c.XTSB and a.XTSB=d.XTSB and a.CKFS=d.CKFS and a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.YPXH=c.YPXH and b.YPCD=c.YPCD and a.CKPB=0 and a.LYPB=1 and a.YFSB>0 and d.DYFS=6)");
		StringBuffer hql_ykkc_update = new StringBuffer();// 更新药库库存
		hql_ykkc_update
				.append("update YK_KCMX  set LSJG=:lsjg ,LSJE=round(KCSL*:lsjg,2),BZLJ=:lsjg,JHJG=:jhjg,JHJE=round(KCSL*:jhjg,2) where SBXH=:kcsb ");
//		StringBuffer hql_ykck_update = new StringBuffer();// 更新药库出库单
//		hql_ykck_update
//				.append("update YK_CK02  a set a.LSJG=:lsjg ,a.JHJG=:jhjg ,a.LSJE=:lsjg*a.SQSL,a.JHJE=:jhjg*a.SQSL where a.YPXH=:ypxh and a.YPCD=:ypcd and a.JGID=:jgid and EXISTS (select 1 from YK_CK01  b where a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.CKPB=0)");
		StringBuffer hql_kc_update_bxg=new StringBuffer();//不修改库存的零售价格等,只修改药库进价
		hql_kc_update_bxg
		.append("update YF_KCMX  set YKJJ=:ykjj,YKLJ=:yklj where SBXH=:kcsb");
		List<Map<String, Object>> list_tjmx = queryPriceAdjustExecutionData(cnd);// 执行的药房药品调价明细
		Map<String, Object> map_tj=null;
		List<Map<String, Object>> list_tj02=null;
		try {
			map_tj = dao.doLoad(cnd, BSPHISEntryNames.YK_TJ01);
			Map<String, Object> map_par_ckjl = new HashMap<String, Object>();
			map_par_ckjl.put("jgid", map_tj.get("JGID") + "");
			Map<String, Object> paramters = new HashMap<String, Object>();
			paramters.put("tjdh", MedicineUtils.parseInt(map_tj.get("TJDH")));
			paramters.put("tjfs", MedicineUtils.parseInt(map_tj.get("TJFS")));
			paramters.put("xtsb", MedicineUtils.parseLong(map_tj.get("XTSB")));
			// 查询出库记录
			List<Map<String, Object>> list_ckjl = dao.doQuery(
					hql_ckjl_wqr.toString(), map_par_ckjl);
			
			// 判断是否月结
			Map<String, Object> map_par_sfyj = new HashMap<String, Object>();
			map_par_sfyj.put("jgid", map_tj.get("JGID") + "");
			Map<String, Object> map_sfyj = dao.doLoad(hql_sfyj.toString(),
					map_par_sfyj);
			if (map_sfyj != null
					&& map_sfyj.get("ZZSJ") != null
					&& ((Date) map_sfyj.get("ZZSJ")).getTime() >= new Date()
							.getTime()) {
				return MedicineUtils.getRetMap("有药房已月结,不能进行调价");
			}
			if(list_tjmx.size()==body.size()){//只有当全部调价时才判断是否有单子未确认,如果部分调价 则表示支持不同价格,所以不去判断单子
				// 判断是否有药房未确认领药单
				Map<String, Object> map_par_lyd = new HashMap<String, Object>();
				map_par_lyd
						.put("yksb", MedicineUtils.parseLong(map_tj.get("XTSB")));
				map_par_lyd.put("tjdh", MedicineUtils.parseInt(map_tj.get("TJDH")));
				map_par_lyd.put("tjfs", MedicineUtils.parseInt(map_tj.get("TJFS")));
				List<Map<String, Object>> list_lyd = dao.doSqlQuery(
						hql_yld.toString(), map_par_lyd);
				if (list_lyd != null && list_lyd.size() > 0) {
					Map<String, Object> map_lyd = list_lyd.get(0);
					if (MedicineUtils.parseInt(map_lyd.get("NUM")) > 0) {
						return MedicineUtils
								.getRetMap("调价单中含有药库出库而药房未领用药品,不能执行此调价单!");
					}
				}
				// 判断是否有药房退药,药库未确认领药单
				list_lyd = dao.doSqlQuery(hql_ty.toString(), map_par_lyd);
				if (list_lyd != null && list_lyd.size() > 0) {
					Map<String, Object> map_lyd = list_lyd.get(0);
					if (MedicineUtils.parseInt(map_lyd.get("NUM")) > 0) {
						return MedicineUtils
								.getRetMap("调价单中含有药房已退药而药库未确认药品,不能执行此调价单!");
					}
				}
			}
			
			// 更新执行标志字段
			paramters.put("zxrq", new Date());
			paramters.put("zxgh", userId);
			dao.doUpdate(hql_tj_update.toString(), paramters);
			list_tj02 = dao.doList(cnd, null, BSPHISEntryNames.YK_TJ02);
			List<Map<String,Object>> list_yk=new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> list_yf=new ArrayList<Map<String,Object>>();
			for(Map<String,Object> b:body){
				if(MedicineUtils.parseInt(b.get("TAG"))==1){
					list_yk.add(b);
				}else{
					list_yf.add(b);
				}
			}
			for (Map<String, Object> map_tjmx : list_tjmx) {
				long kcsb = MedicineUtils.parseLong(map_tjmx.get("KCSB"));
				if(MedicineUtils.parseLong(map_tjmx.get("YFSB"))==0){//YFSB是0的是药库调价记录
					boolean x=false;
					for(Map<String,Object> b:list_yk){
						if(kcsb!=0){
							if(MedicineUtils.parseLong(b.get("KCSB"))==kcsb){
								x=true;
								break;
							}
						}else{
							if(MedicineUtils.compareMaps(b, new String []{"YKSB","YPCD","YPXH"}, map_tjmx, new String []{"YKSB","YPCD","YPXH"})){
								x=true;
								break;
							}
						}
					}
					if(!x){
						continue;
					}
					if(kcsb!=0){//前台选中的药库调价记录值是0的是药库的KCSB,值非0的是药房的KCSB
						//更新库存表
						if(kcsb!=0){
							Map<String,Object> map_par_ykkc=new HashMap<String,Object>();
							map_par_ykkc.put("lsjg", MedicineUtils.parseDouble(map_tjmx.get("XLSJ")));
							map_par_ykkc.put("kcsb", MedicineUtils.parseLong(map_tjmx.get("KCSB")));
							map_par_ykkc.put("jhjg", MedicineUtils.parseDouble(map_tjmx.get("XJHJ")));
							dao.doUpdate(hql_ykkc_update.toString(), map_par_ykkc);
							//dao.doSqlUpdate(hql_ykck_update.toString(), map_par_ykkc);
						}
					}else{
						map_tjmx.put("TJSL", 0);
						map_tjmx.put("XLSE", MedicineUtils.parseDouble(map_tjmx.get("YLSE")));
						map_tjmx.put("XJHE", MedicineUtils.parseDouble(map_tjmx.get("YJHE")));
					}
					if(!map_tjmx.containsKey("YPFJ")||map_tjmx.get("YPFJ")==null||"null".equals(map_tjmx.get("YPFJ")+"")){
						map_tjmx.put("YPFJ", 0);
					}
					if(!map_tjmx.containsKey("YPFE")||map_tjmx.get("YPFE")==null||"null".equals(map_tjmx.get("YPFE")+"")){
						map_tjmx.put("YPFE", 0);
					}
					dao.doSave("create", BSPHISEntryNames.YK_TJJL, map_tjmx, false);
					continue;
				}
				
				boolean x=false;
				for(Map<String,Object> b:list_yf){
					if(kcsb!=0){
						if(MedicineUtils.parseLong(b.get("KCSB"))==kcsb){
							x=true;
							break;
						}
					}else{
						if(MedicineUtils.compareMaps(b, new String []{"YFSB","YPCD","YPXH"}, map_tjmx, new String []{"YFSB","YPCD","YPXH"})){
							x=true;
							break;
						}
					}
				}
				if(x){
					Map<String, Object> map_par_sfcsh = new HashMap<String, Object>();
					map_par_sfcsh.put("yfsb",
							MedicineUtils.parseLong(map_tjmx.get("YFSB")));
					long l = dao.doCount("YF_JZJL", hql_sfcsh.toString(),
							map_par_sfcsh);
					if (l == 0) {
						Session session = (Session) ctx.get(Context.DB_SESSION);
						session.getTransaction().rollback();
						return MedicineUtils.getRetMap("药房[" + map_tjmx.get("YFMC")
								+ "]账簿未初始化,不能调价");
					}
					Map<String, Object> map_par_sfrk = new HashMap<String, Object>();
					map_par_sfrk.put("yfsb",
							MedicineUtils.parseLong(map_tjmx.get("YFSB")));
					map_par_sfrk.put("ypxh",
							MedicineUtils.parseLong(map_tjmx.get("YPXH")));
					map_par_sfrk.put("ypcd",
							MedicineUtils.parseLong(map_tjmx.get("YPCD")));
					long ll = dao.doCount("YF_RK01 a,YF_RK02 b",
							hql_sfrk.toString(), map_par_sfrk);
					if (ll != 0) {
						Session session = (Session) ctx.get(Context.DB_SESSION);
						session.getTransaction().rollback();
						return MedicineUtils.getRetMap("药房[" + map_tjmx.get("YFMC")
								+ "]中有未确定入库单,不能调价");
					}
				}
				Map<String, Object> map_par_jgjs = new HashMap<String, Object>();
				map_par_jgjs.put("xlsj",
						MedicineUtils.parseDouble(map_tjmx.get("XLSJ")));
				map_par_jgjs.put("ylsj",
						MedicineUtils.parseDouble(map_tjmx.get("YLSJ")));
				map_par_jgjs.put("xjhj",
						MedicineUtils.parseDouble(map_tjmx.get("XJHJ")));
				map_par_jgjs.put("yjhj",
						MedicineUtils.parseDouble(map_tjmx.get("YJHJ")));
				map_par_jgjs.put("yfsb",
						MedicineUtils.parseLong(map_tjmx.get("YFSB")));
				map_par_jgjs.put("ypxh",
						MedicineUtils.parseLong(map_tjmx.get("YPXH")));
			//	long kcsb = MedicineUtils.parseLong(map_tjmx.get("KCSB"));
				if (kcsb != 0) {
					Map<String, Object> map_par_kc = new HashMap<String, Object>();
					// 更新库存表的零售价格
					for (Map<String, Object> map_tj02 : list_tj02) {
						if (MedicineUtils.parseLong(map_tj02.get("YPXH")) == MedicineUtils
								.parseLong(map_tjmx.get("YPXH"))
								&& MedicineUtils
										.parseLong(map_tj02.get("YPCD")) == MedicineUtils
										.parseLong(map_tjmx.get("YPCD"))) {
							map_par_kc.put("ykjj", MedicineUtils
									.parseDouble(map_tj02.get("XJHJ")));
							map_par_kc.put("yklj", MedicineUtils
									.parseDouble(map_tj02.get("XLSJ")));
							break;
						}
					}
					map_par_kc.put("kcsb", kcsb);
					if(x){//如果选择的记录,不修改库存的价格,调价记录数量金额等存0
						// 更新库存表的零售价格
						map_par_kc.put("lsjg", MedicineUtils.parseDouble(map_tjmx.get("XLSJ")));
						map_par_kc.put("jhjg", MedicineUtils.parseDouble(map_tjmx.get("XJHJ")));
					dao.doUpdate(hql_kc_update.toString(), map_par_kc);

					if (list_ckjl != null && list_ckjl.size() != 0) {
						for (Map<String, Object> map_ckjl : list_ckjl) {
							long kcsb_ckjl = MedicineUtils.parseLong(map_ckjl
									.get("KCSB"));
							if (kcsb_ckjl == kcsb) {
								Map<String, Object> map_par_ck = new HashMap<String, Object>();
								map_par_ck.put("lsjg", MedicineUtils
										.parseDouble(map_tjmx.get("XLSJ")));
								map_par_ck.put("jhjg", MedicineUtils
										.parseDouble(map_tjmx.get("XJHJ")));
								map_par_ck.put("sbxh", MedicineUtils
										.parseLong(map_ckjl.get("SBXH")));
								dao.doUpdate(hql_ckjl_update.toString(),
										map_par_ck);
							}
						}
					}
					}else{
						dao.doUpdate(hql_kc_update_bxg.toString(), map_par_kc);
						map_tjmx.put("TJSL", 0);
						map_tjmx.put("TJJE", 0);
						map_tjmx.put("XLSE", MedicineUtils.parseDouble(map_tjmx.get("YLSE")));
						map_tjmx.put("XJHE", MedicineUtils.parseDouble(map_tjmx.get("YJHE")));
				}
				}
				map_tjmx.put("CZGH", userId);
				map_tjmx.put("YKSB", yksb);
				map_tjmx.put("CKBH", ckbh);
				// 插入调价明细
				dao.doSave("create", BSPHISEntryNames.YF_TJJL, map_tjmx, false);
			}
			// 更新价格表,药库库存明细
			StringBuffer hql_pysl_sum = new StringBuffer();// 查询药库更新的药品数量
			hql_pysl_sum
					.append("select sum(TJSL) as TJSL,sum((XLSJ-YLSJ)*TJSL) as LSZZ from YK_TJJL where YPXH=:ypxh and YPCD=:ypcd and TJDH=:tjdh and TJFS=:tjfs and XTSB=:xtsb ");
			StringBuffer hql_tj02_update = new StringBuffer();// 更新tj02的调价数量和零售增值
			hql_tj02_update
					.append("update YK_TJ02  set TJSL=:tjsl,LSZZ=:lszz,LSJZ=:lsjz where SBXH=:sbxh");
			StringBuffer hql_jg_update = new StringBuffer();// 更新价格
			hql_jg_update
			.append("update YK_CDXX set LSJG=:lsjg ,JHJG=:jhjg,LSJE=:lsje,JHJE=:jhje  where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid ");
			StringBuffer hql_zcje=new StringBuffer();
			hql_zcje.append("select sum(LSJE) as LSJE,sum(JHJE) as JHJE from YK_KCMX where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
			for (Map<String, Object> map_tj02 : list_tj02) {
				Map<String, Object> map_par_jg = new HashMap<String, Object>();
				map_par_jg.put("ypxh", MedicineUtils.parseLong(map_tj02.get("YPXH")));
				map_par_jg.put("ypcd", MedicineUtils.parseLong(map_tj02.get("YPCD")));
				map_par_jg.put("jgid", map_tj02.get("JGID")+"");
				List<Map<String,Object>> list_zcje=dao.doSqlQuery(hql_zcje.toString(), map_par_jg);
				if(list_zcje==null||list_zcje.size()==0){
					map_par_jg.put("lsje", 0);
					map_par_jg.put("jhje", 0);
				}else{
					Map<String,Object> map_zcje=list_zcje.get(0);
					map_par_jg.put("lsje", MedicineUtils.parseDouble(map_zcje.get("LSJE")));
					map_par_jg.put("jhje", MedicineUtils.parseDouble(map_zcje.get("JHJE")));
				}
				map_par_jg.put("lsjg", MedicineUtils.parseDouble(map_tj02.get("XLSJ")));
				map_par_jg.put("jhjg", MedicineUtils.parseDouble(map_tj02.get("XJHJ")));
				int i = dao.doUpdate(hql_jg_update.toString(), map_par_jg);
				if (i == 0) {
					// 找不到记录 ,数据回滚
					Session session = (Session) ctx.get(Context.DB_SESSION);
					session.getTransaction().rollback();
					return MedicineUtils.getRetMap("有药品已经被调价");
				}
				
				Map<String, Object> map_par_tj02 = new HashMap<String, Object>();
				map_par_tj02.put("tjdh", MedicineUtils.parseInt(map_tj.get("TJDH")));
				map_par_tj02.put("tjfs", MedicineUtils.parseInt(map_tj.get("TJFS")));
				map_par_tj02.put("xtsb", MedicineUtils.parseLong(map_tj.get("XTSB")));
				map_par_tj02.put("ypxh", MedicineUtils.parseLong(map_tj02.get("YPXH")));
				map_par_tj02.put("ypcd", MedicineUtils.parseLong(map_tj02.get("YPCD")));
				List<Map<String,Object>> list_tjjl = dao.doSqlQuery(
						hql_pysl_sum.toString(), map_par_tj02);
				if(list_tjjl!=null&&list_tjjl.size()!=0){
					Map<String,Object> map_tjjl=list_tjjl.get(0);
					map_par_tj02.clear();
					map_par_tj02.put("sbxh", MedicineUtils.parseLong(map_tj02.get("SBXH")));
					map_par_tj02.put("tjsl", MedicineUtils.parseDouble(map_tjjl.get("TJSL")));
					double lszz=MedicineUtils.parseDouble(map_tjjl.get("LSZZ"));
					if(lszz>0){
						map_par_tj02.put("lszz", lszz);
						map_par_tj02.put("lsjz", MedicineUtils.parseDouble(0));
					}else{
						map_par_tj02.put("lszz", MedicineUtils.parseDouble(0));
						map_par_tj02.put("lsjz", lszz);
					}
					dao.doSqlUpdate(hql_tj02_update.toString(), map_par_tj02);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "调价单执行失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "调价明细保存验证失败", e);
		}
		// 以下是新功能 中心更新自动新增站点调价
		int zdzdtj=0;
		try{
		zdzdtj=MedicineUtils.parseInt(ParameterUtil.getParameter(
				ParameterUtil.getTopUnitId(), BSPHISSystemArgument.ZDZDTJ, ctx));
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.ZDZDTJ, e);
		}
		if (zdzdtj > 0) {
			saveZDPriceAdjust(map_tj, list_tj02, list_tjmx, ctx);
			// return zdret;
		}
		return MedicineUtils.getRetMap();
	}
/**
 * 
 * @author caijy
 * @createDate 2014-1-15
 * @description 保存站点的调价记录
 * @updateInfo
 * @param map_tj
 * @param list_tj02
 * @param list_tjmx
 * @param ctx
 * @throws ModelDataOperationException
 */
	public void saveZDPriceAdjust(Map<String, Object> map_tj,
			List<Map<String, Object>> list_tj02,
			List<Map<String, Object>> list_tjmx, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = user.getUserId();
		Collection<ManageUnit> list_manageUnit = user.getManageUnit()
				.getChildren();
		if (list_manageUnit == null || list_manageUnit.size() == 0) {
			return;
		}
		int ckbh = 0;// 窗口编号 暂时写死
		StringBuffer hql_yksb = new StringBuffer();// 查询当前机构下面站点的YKSB
		hql_yksb.append("select YKSB as YKSB,YKMC as YKMC from YK_YKLB where JGID=:jgid");
		StringBuffer hql_yfsb = new StringBuffer();// 查询当前机构下面站点的YFSB
		hql_yfsb.append("select YFSB as YFSB,YFMC as YFMC from YF_YFLB where JGID=:jgid");
		StringBuffer hql_ypyk = new StringBuffer();// 查询调价药品在站点的哪个药库
		hql_ypyk.append("select YKSB as YKSB from YK_YPXX where YPXH=:ypxh and JGID=:jgid");
		StringBuffer hql_zdypjl = new StringBuffer();// 查询站点药库药品记录
		hql_zdypjl.append("select b.JHJG as YJHJ,b.LSJG as YLSJ from YK_YPXX a,YK_CDXX b where a.YPXH=b.YPXH and a.JGID=b.JGID and a.YPXH=:ypxh and a.YKSB=:yksb and b.YPCD=:ypcd");
		StringBuffer hql_sfyj = new StringBuffer();// 用于判断是否月结
		hql_sfyj.append("select max(ZZSJ) as ZZSJ from YF_JZJL  where JGID=:jgid");
		StringBuffer hql_sfcsh = new StringBuffer();// 用于判断药房有没初始账簿
		hql_sfcsh.append("YFSB=:yfsb");
		StringBuffer hql_sfrk = new StringBuffer();// 用于判断是否有未确定入库的入库记录
		hql_sfrk.append(" a.YFSB=b.YFSB and a.RKFS=b.RKFS and a.RKDH=b.RKDH and a.YFSB=:yfsb and b.YPCD=:ypcd and b.YPXH=:ypxh and a.RKPB!=1");
		StringBuffer hql_yld = new StringBuffer();// 判断是否有药房未确认领药单
		hql_yld.append("select count(1) as NUM from YK_TJ02  c  where  c.XTSB=:yksb and TJFS=:tjfs and TJDH=:tjdh and EXISTS (select 1 from YK_CK01 a,YK_CK02  b where a.XTSB=c.XTSB and a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.YPXH=c.YPXH and b.YPCD=c.YPCD and a.CKPB=1 and a.LYPB=0 and a.YFSB>0)");
		StringBuffer hql_ty = new StringBuffer();// 判断是否有药房退药,药库未确认领药单
		hql_ty.append("select count(1) as NUM from YK_TJ02  c  where c.XTSB=:yksb and TJFS=:tjfs and TJDH=:tjdh and EXISTS (select 1 from YK_CK01  a,YK_CK02 b,YK_CKFS  d where a.XTSB=c.XTSB and a.XTSB=d.XTSB and a.CKFS=d.CKFS and a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.YPXH=c.YPXH and b.YPCD=c.YPCD and a.CKPB=0 and a.LYPB=1 and a.YFSB>0 and d.DYFS=6)");
		StringBuffer hql_yfyp = new StringBuffer();// 查询药房药品
		hql_yfyp.append(
				"select yf.YFGG as YFGG,yf.YFBZ as YFBZ,yf.YFDW as YFDW,yp.ZXBZ as ZXBZ,yp.YPMC as YPMC  from YF_YPXX yf,YK_TYPK yp where yf.YPXH=yp.YPXH and  yf.YFSB=:yfsb and yf.YPXH=:ypxh");
		StringBuffer hql_ypkc = new StringBuffer();// 查询药品库存
		hql_ypkc.append(
				"select SBXH as KCSB,YPSL as KCSL,YPPH as YPPH,YPXQ as YPXQ from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb");
		StringBuffer hql_kc_update = new StringBuffer();// 更新库存
		hql_kc_update
		.append("update YF_KCMX set LSJG=:lsjg ,LSJE=YPSL*:lsjg,JHJG=:jhjg,JHJE=YPSL*:jhjg,YKJJ=:ykjj,YKLJ=:yklj where SBXH=:kcsb");
		StringBuffer hql_ckjl_wqr = new StringBuffer();// 未确认出库记录,用于调价时更新对应的出库明细
		hql_ckjl_wqr
		.append("select b.SBXH as SBXH,b.KCSB as KCSB from YF_CK01 a,YF_CK02 b where a.CKDH=b.CKDH and a.YFSB=b.YFSB and a.CKFS=b.CKFS and a.JGID=:jgid and a.CKPB!=1 and b.KCSB=:kcsb");
		StringBuffer hql_ckjl_update = new StringBuffer();// 更新调价已调价库存对应的出库明细
		hql_ckjl_update
		.append("update YF_CK02 set LSJG=:lsjg ,JHJG=:jhjg,LSJE= round(:lsjg*CKSL,4),JHJE=round(:jhjg*CKSL,4) where SBXH=:sbxh");
		StringBuffer hql_jg_update = new StringBuffer();// 更新价格
		hql_jg_update
		.append("update YK_CDXX  set LSJG=:lsjg ,JHJG=:jhjg,LSJE=round(KCSL*:lsjg,2),JHJE=round(KCSL*:jhjg,2)  where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid and LSJG=:ylsj and JHJG=:yjhj");
		StringBuffer hql_ykkc_update = new StringBuffer();// 更新药库库存
		hql_ykkc_update
		.append("update YK_KCMX  set LSJG=:lsjg ,LSJE=round(KCSL*:lsjg,2),BZLJ=:lsjg,JHJG=:jhjg,JHJE=round(KCSL*:jhjg,2) where SBXH=:kcsb ");
		StringBuffer hql_ykck_update = new StringBuffer();// 更新药库出库单
		hql_ykck_update
				.append("update YK_CK02  a set a.LSJG=:lsjg ,a.JHJG=:jhjg ,a.LSJE=:lsjg*a.SQSL,a.JHJE=:jhjg*a.SQSL where a.YPXH=:ypxh and a.YPCD=:ypcd and a.JGID=:jgid and EXISTS (select 1 from YK_CK01  b where a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and b.CKPB=0)");
		List<Map<String, Object>> list_tj01_temp = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_tj02_temp = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_tjjl_temp = new ArrayList<Map<String, Object>>();
		try {
			for (ManageUnit m : list_manageUnit) {
				String jgid = m.getId();
				Map<String, Object> map_par_jgid = new HashMap<String, Object>();
				map_par_jgid.put("jgid", m.getId());
				String zdjgid = m.getId();
				// 如果下级机构没有药库或者没有药房 则不处理
				List<Map<String, Object>> list_yksb_temp = dao.doQuery(
						hql_yksb.toString(), map_par_jgid);
				if (list_yksb_temp == null || list_yksb_temp.size() == 0) {
					continue;
				}
				List<Map<String, Object>> list_yfsb_temp = dao.doQuery(
						hql_yfsb.toString(), map_par_jgid);
				if (list_yfsb_temp == null || list_yfsb_temp.size() == 0) {
					continue;
				}

				long yksb = 0;// yksb只取第一个找到的药品的yksb
				boolean yy = false;// 站点机构是否有需要调价的药
				for (Map<String, Object> map_tj02 : list_tj02) {
					if (yksb == 0) {
						Map<String, Object> map_par_ypyk = new HashMap<String, Object>();
						map_par_ypyk.put("ypxh",
								MedicineUtils.parseLong(map_tj02.get("YPXH")));
						map_par_ypyk.put("jgid", zdjgid);
						Map<String, Object> map_ypyk = dao.doLoad(
								hql_ypyk.toString(), map_par_ypyk);
						if (map_ypyk == null || map_ypyk.size() == 0) {
							continue;
						}
						yksb = MedicineUtils.parseLong(map_ypyk.get("YKSB"));
					}
					Map<String, Object> map_par_zdypjl = new HashMap<String, Object>();
					map_par_zdypjl.put("ypxh", MedicineUtils.parseLong(map_tj02.get("YPXH")));
					map_par_zdypjl.put("yksb", yksb);
					map_par_zdypjl.put("ypcd", MedicineUtils.parseLong(map_tj02.get("YPCD")));
					Map<String, Object> map_zdypjl = dao.doLoad(
							hql_zdypjl.toString(), map_par_zdypjl);
					if (map_zdypjl == null || map_zdypjl.size() == 0) {
						continue;
					}
					yy = true;
					double tjsl = 0;
					Map<String, Object> map_zdtj02 = new HashMap<String, Object>();
					map_zdtj02.putAll(map_tj02);
					map_zdtj02.put("JGID", jgid);
					map_zdtj02.put("XTSB", yksb);
					map_zdtj02.put("YJHJ", MedicineUtils.parseDouble(map_zdypjl.get("YJHJ")));
					map_zdtj02.put("YLSJ", MedicineUtils.parseDouble(map_zdypjl.get("YLSJ")));
					map_zdtj02.put("YPFJ", 0);
					map_zdtj02.put("TJSL", tjsl);
					for (Map<String, Object> map_yfsb : list_yfsb_temp) {// 遍历当前机构下面的药房
						String yfmc = (String) map_yfsb.get("YFMC");
						Map<String, Object> map_par_yfyp = new HashMap<String, Object>();
						map_par_yfyp.put("yfsb", map_yfsb.get("YFSB"));
						map_par_yfyp.put("ypxh", map_tj02.get("YPXH"));
						Map<String, Object> map_yfyp = dao.doLoad(
								hql_yfyp.toString(), map_par_yfyp);
						// 循环药房 生成药房调价明细
						if (map_yfyp != null && map_yfyp.size() > 0) {
							// 判断药房是否初始化
							Map<String, Object> map_par_sfcsh = new HashMap<String, Object>();
							map_par_sfcsh.put("yfsb",
									MedicineUtils.parseLong(map_yfsb.get("YFSB")));
							long l = dao.doCount("YF_JZJL",
									hql_sfcsh.toString(), map_par_sfcsh);
							if (l == 0) {
								throw new ModelDataOperationException(
										ServiceCode.CODE_DATABASE_ERROR, "药房["
												+ yfmc + "]账簿未初始化,不能调价");
							}
							// 判断是否有未确认入库单
							Map<String, Object> map_par_sfrk = new HashMap<String, Object>();
							map_par_sfrk.put("yfsb",
									MedicineUtils.parseLong(map_yfsb.get("YFSB")));
							map_par_sfrk.put("ypxh",
									MedicineUtils.parseLong(map_tj02.get("YPXH")));
							map_par_sfrk.put("ypcd",
									MedicineUtils.parseLong(map_tj02.get("YPCD")));
							long ll = dao.doCount("YF_RK01 a,YF_RK02 b ",
									hql_sfrk.toString(), map_par_sfrk);
							if (ll != 0) {
								throw new ModelDataOperationException(
										ServiceCode.CODE_DATABASE_ERROR, "药房["
												+ yfmc + "]中有未确定入库单,不能调价");
							}
							Map<String, Object> map_tjjl = new HashMap<String, Object>();
							map_tjjl.put("YFMC", yfmc);
							map_tjjl.put("YPGG", map_yfyp.get("YFGG"));
							map_tjjl.put("YPMC", map_yfyp.get("YPMC"));
							map_tjjl.put(
									"YLSJ",
									MedicineUtils.formatDouble(
											4,
											MedicineUtils.parseDouble(map_zdtj02.get("YLSJ"))
													* MedicineUtils.parseDouble(map_yfyp
															.get("YFBZ"))
													/ MedicineUtils.parseDouble(map_yfyp
															.get("ZXBZ"))));
							map_tjjl.put(
									"XLSJ",
									MedicineUtils.formatDouble(
											4,
											MedicineUtils.parseDouble(map_zdtj02.get("XLSJ"))
													* MedicineUtils.parseDouble(map_yfyp
															.get("YFBZ"))
													/ MedicineUtils.parseDouble(map_yfyp
															.get("ZXBZ"))));
							map_tjjl.put(
									"YPFJ",
									MedicineUtils.formatDouble(
											4,
											MedicineUtils.parseDouble(map_zdtj02.get("YPFJ"))
													* MedicineUtils.parseDouble(map_yfyp
															.get("YFBZ"))
													/ MedicineUtils.parseDouble(map_yfyp
															.get("ZXBZ"))));
							map_tjjl.put(
									"XPFJ",
									MedicineUtils.formatDouble(
											4,
											MedicineUtils.parseDouble(map_zdtj02.get("XPFJ"))
													* MedicineUtils.parseDouble(map_yfyp
															.get("YFBZ"))
													/ MedicineUtils.parseDouble(map_yfyp
															.get("ZXBZ"))));
							map_tjjl.put(
									"YJHJ",
									MedicineUtils.formatDouble(
											4,
											MedicineUtils.parseDouble(map_zdtj02.get("YJHJ"))
													* MedicineUtils.parseDouble(map_yfyp
															.get("YFBZ"))
													/ MedicineUtils.parseDouble(map_yfyp
															.get("ZXBZ"))));
							map_tjjl.put(
									"XJHJ",
									MedicineUtils.formatDouble(
											4,
											MedicineUtils.parseDouble(map_zdtj02.get("XJHJ"))
													* MedicineUtils.parseDouble(map_yfyp
															.get("YFBZ"))
													/ MedicineUtils.parseDouble(map_yfyp
															.get("ZXBZ"))));
							map_tjjl.put("YFDW", map_yfyp.get("YFDW"));
							map_tjjl.put("YFBZ", MedicineUtils.parseInt(map_yfyp.get("YFBZ")));
							map_tjjl.put("YFSB", map_yfsb.get("YFSB"));
							map_tjjl.put("TJDH", map_zdtj02.get("TJDH"));
							map_tjjl.put("TJFS", map_zdtj02.get("TJFS"));
							map_tjjl.put("JGID", map_zdtj02.get("JGID"));
							map_tjjl.put("YKSB", map_zdtj02.get("YKSB"));
							map_tjjl.put("YPXH", map_zdtj02.get("YPXH"));
							map_tjjl.put("YPCD", map_zdtj02.get("YPCD"));
							map_tjjl.put("CDMC", map_zdtj02.get("CDMC"));
							map_tjjl.put("TJRQ", new Date());
							Map<String, Object> map_par_ypkc = new HashMap<String, Object>();
							map_par_ypkc.put("yfsb", map_yfsb.get("YFSB"));
							map_par_ypkc.put("ypxh",
									MedicineUtils.parseLong(map_zdtj02.get("YPXH")));
							map_par_ypkc.put("ypcd",
									MedicineUtils.parseLong(map_zdtj02.get("YPCD")));

							List<Map<String, Object>> list_ypkc = dao.doQuery(
									hql_ypkc.toString(), map_par_ypkc);
							if (list_ypkc != null && list_ypkc.size() > 0) {
								for (int i = 0; i < list_ypkc.size(); i++) {
									Map<String, Object> map_ypkc = list_ypkc
											.get(i);
									tjsl += MedicineUtils.parseDouble(map_ypkc.get("KCSL"));
									Map<String, Object> map_tjmx = new HashMap<String, Object>();
									map_tjmx.putAll(map_tjjl);
									map_tjmx.put("KCSB", map_ypkc.get("KCSB"));
									map_tjmx.put("KCSL", map_ypkc.get("KCSL"));
									map_tjmx.put("YPPH", map_ypkc.get("YPPH"));
									map_tjmx.put("YPXQ", map_ypkc.get("YPXQ"));
									map_tjmx.put(
											"TJJE",
											MedicineUtils.formatDouble(
													4,
													MedicineUtils.parseDouble(map_tjmx
															.get("KCSL"))
															* (MedicineUtils.parseDouble(map_tjmx
																	.get("XLSJ")) - MedicineUtils.parseDouble(map_tjmx
																	.get("YLSJ")))));
									map_tjmx.put(
											"YLSE",
											MedicineUtils.formatDouble(
													4,
													MedicineUtils.parseDouble(map_tjmx
															.get("KCSL"))
															* MedicineUtils.parseDouble(map_tjmx
																	.get("YLSJ"))));
									map_tjmx.put(
											"XLSE",
											MedicineUtils.formatDouble(
													4,
													MedicineUtils.parseDouble(map_tjmx
															.get("KCSL"))
															* MedicineUtils.parseDouble(map_tjmx
																	.get("XLSJ"))));
									map_tjmx.put(
											"YJHE",
											MedicineUtils.formatDouble(
													4,
													MedicineUtils.parseDouble(map_tjmx
															.get("KCSL"))
															* MedicineUtils.parseDouble(map_tjmx
																	.get("YJHJ"))));
									map_tjmx.put(
											"XJHE",
											MedicineUtils.formatDouble(
													4,
													MedicineUtils.parseDouble(map_tjmx
															.get("KCSL"))
															* MedicineUtils.parseDouble(map_tjmx
																	.get("XJHJ"))));
									map_tjmx.put(
											"YPFE",
											MedicineUtils.formatDouble(
													4,
													MedicineUtils.parseDouble(map_tjmx
															.get("KCSL"))
															* MedicineUtils.parseDouble(map_tjmx
																	.get("YPFJ"))));
									map_tjmx.put(
											"XPFE",
											MedicineUtils.formatDouble(
													4,
													MedicineUtils.parseDouble(map_tjmx
															.get("KCSL"))
															* MedicineUtils.parseDouble(map_tjmx
																	.get("XPFJ"))));
									map_tjmx.put("CZGH", userId);
									map_tjmx.put("YKSB", yksb);
									map_tjmx.put("CKBH", ckbh);
									Map<String, Object> map_par_kc = new HashMap<String, Object>();
									map_par_kc.put("lsjg",
											MedicineUtils.parseDouble(map_tjmx.get("XLSJ")));
									map_par_kc.put("jhjg",
											MedicineUtils.parseDouble(map_tjmx.get("XJHJ")));
									map_par_kc
											.put("kcsb", map_ypkc.get("KCSB"));
									map_par_kc
											.put("ykjj", MedicineUtils.parseDouble(map_zdtj02
													.get("XJHJ")));
									map_par_kc
											.put("yklj", MedicineUtils.parseDouble(map_zdtj02
													.get("XLSJ")));
									// 更新库存表的零售价格
									dao.doUpdate(hql_kc_update.toString(),
											map_par_kc);
									Map<String, Object> map_par_ckjl = new HashMap<String, Object>();
									map_par_ckjl.put("jgid", jgid);
									map_par_ckjl.put("kcsb",
											map_ypkc.get("KCSB"));
									List<Map<String, Object>> list_ckjl = dao
											.doQuery(hql_ckjl_wqr.toString(),
													map_par_ckjl);
									if (list_ckjl != null
											&& list_ckjl.size() > 0) {
										for (Map<String, Object> map_ckjl : list_ckjl) {
											Map<String, Object> map_par_ck = new HashMap<String, Object>();
											map_par_ck.put("lsjg",
													MedicineUtils.parseDouble(map_tjmx
															.get("XLSJ")));
											map_par_ck.put("jhjg",
													MedicineUtils.parseDouble(map_tjmx
															.get("XJHJ")));
											map_par_ck.put("sbxh",
													MedicineUtils.parseLong(map_ckjl
															.get("SBXH")));
											dao.doUpdate(
													hql_ckjl_update.toString(),
													map_par_ck);
										}
									}
									map_tjmx.put("TJSL", map_ypkc.get("KCSL"));
									map_tjmx.remove("SBXH");
									list_tjjl_temp.add(map_tjmx);
								}
							}

						}

					}
					map_zdtj02.put("TJSL", tjsl);
					map_zdtj02
							.put("LSZZ",
									MedicineUtils.formatDouble(
											4,
											(MedicineUtils.parseDouble(map_zdtj02.get("XLSJ")) - MedicineUtils.parseDouble(map_zdtj02
													.get("YLSJ"))))
											* tjsl);
					map_zdtj02.remove("SBXH");
					list_tj02_temp.add(map_zdtj02);
				}
				if (yy) {
					// 判断是否月结
					Map<String, Object> map_par_sfyj = new HashMap<String, Object>();
					map_par_sfyj.put("jgid", jgid);
					Map<String, Object> map_sfyj = dao.doLoad(
							hql_sfyj.toString(), map_par_sfyj);
					if (map_sfyj != null
							&& map_sfyj.get("ZZSJ") != null
							&& ((Date) map_sfyj.get("ZZSJ")).getTime() >= new Date()
									.getTime()) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"有药房已月结,不能进行调价");
					}
					// 判断是否有药房未确认领药单
					Map<String, Object> map_par_lyd = new HashMap<String, Object>();
					map_par_lyd.put("yksb", yksb);
					map_par_lyd.put("tjdh", MedicineUtils.parseInt(map_tj.get("TJDH")));
					map_par_lyd.put("tjfs", MedicineUtils.parseInt(map_tj.get("TJFS")));
					List<Map<String, Object>> list_lyd = dao.doSqlQuery(
							hql_yld.toString(), map_par_lyd);
					if (list_lyd != null && list_lyd.size() > 0) {
						Map<String, Object> map_lyd = list_lyd.get(0);
						if (MedicineUtils.parseInt(map_lyd.get("NUM")) > 0) {
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR,
									"调价单中含有药库出库而药房未领用药品,不能执行此调价单!");
						}
					}
					// 判断是否有药房退药,药库未确认领药单
					list_lyd = dao.doSqlQuery(hql_ty.toString(), map_par_lyd);
					if (list_lyd != null && list_lyd.size() > 0) {
						Map<String, Object> map_lyd = list_lyd.get(0);
						if (MedicineUtils.parseInt(map_lyd.get("NUM")) > 0) {
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR,
									"调价单中含有药房已退药而药库未确认药品,不能执行此调价单!");
						}
					}
					Map<String, Object> map_zdtj01 = new HashMap<String, Object>();
					map_zdtj01.putAll(map_tj);
					map_zdtj01.put("XTSB", yksb);
					map_zdtj01.put("JGID", jgid);
					map_zdtj01.put("ZXRQ", new Date());
					map_zdtj01.put("ZXGH", userId);
					map_zdtj01.put("ZYPB", 1);
					// 暂时调价单号让站点和中心一样的单号,如果站点自己也调价的话,这里要改成取站点的调价单号,上面的明细里面也要改
					list_tj01_temp.add(map_zdtj01);
				}

			}
			for (Map<String, Object> map_zdtj01 : list_tj01_temp) {
				dao.doSave("create", BSPHISEntryNames.YK_TJ01, map_zdtj01,
						false);
			}
			for (Map<String, Object> map_zdtj02 : list_tj02_temp) {
				Map<String, Object> map_par_jg = new HashMap<String, Object>();
				map_par_jg.put("lsjg", map_zdtj02.get("XLSJ"));
				map_par_jg.put("ylsj", map_zdtj02.get("YLSJ"));
				map_par_jg.put("ypxh", map_zdtj02.get("YPXH"));
				map_par_jg.put("ypcd", map_zdtj02.get("YPCD"));
				map_par_jg.put("jhjg", map_zdtj02.get("XJHJ"));
				map_par_jg.put("yjhj", map_zdtj02.get("YJHJ"));
				map_par_jg.put("jgid", map_zdtj02.get("JGID"));
				int i = dao.doUpdate(hql_jg_update.toString(), map_par_jg);
				if (i == 0) {
					// 找不到记录 ,数据回滚
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "有药品已经被调价!");
				}
				Map<String, Object> map_par_ykkc = new HashMap<String, Object>();
				map_par_ykkc.put("lsjg", map_zdtj02.get("XLSJ"));
				map_par_ykkc.put("ypxh", map_zdtj02.get("YPXH"));
				map_par_ykkc.put("ypcd", map_zdtj02.get("YPCD"));
				map_par_ykkc.put("jgid", map_zdtj02.get("JGID"));
				map_par_ykkc.put("jhjg", map_zdtj02.get("XJHJ"));
				dao.doUpdate(hql_ykkc_update.toString(), map_par_ykkc);
				dao.doSqlUpdate(hql_ykck_update.toString(), map_par_ykkc);
				dao.doSave("create", BSPHISEntryNames.YK_TJ02, map_zdtj02,
						false);
			}
			for (Map<String, Object> map_zdtjjl : list_tjjl_temp) {
				dao.doSave("create", BSPHISEntryNames.YF_TJJL, map_zdtjjl,
						false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "站点调价单保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "站点调价单保存失败", e);
		}
	}
}
