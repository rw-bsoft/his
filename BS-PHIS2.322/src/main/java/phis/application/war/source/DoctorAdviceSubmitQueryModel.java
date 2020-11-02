package phis.application.war.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.BSHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.application.ivc.source.TreatmentNumberModule;
import phis.application.mds.source.MedicineUtils;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class DoctorAdviceSubmitQueryModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public DoctorAdviceSubmitQueryModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 医嘱提交查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doDoctorAdviceQueryVerification(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			if (user.getProperty("wardId") != null
					&& user.getProperty("wardId") != "") {
				Long al_hsql = Long.parseLong((String) user
						.getProperty("wardId"));
				// 判断是否成立
				String bqtjts = ParameterUtil.getParameter(manaUnitId, "BQTJTS"
						+ al_hsql, "0", "病区提交天数启用 0.不启用1.启用", ctx);
				String ll_yzts = ParameterUtil.getParameter(manaUnitId, "YZTS"
						+ al_hsql, "0", "医嘱提交最大天数控制", ctx);
				Date ldt_lyrq = sdfdate.parse(sdfdate.format(new Date()));
				if (req.get("LYRQ") != null) {
					ldt_lyrq = sdfdate.parse(req.get("LYRQ") + "");
				}
				int li_tjts = BSHISUtil.getPeriod(ldt_lyrq, new Date());
				if ("1".equals(bqtjts)) {
					if (li_tjts > Integer.parseInt(ll_yzts)) {
						res.put(Service.RES_CODE, 600);
					}
				}
			}
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		}
	}

	/**
	 * 医嘱提交查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doGetDoctorAdviceSubmitQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = (String) user.getManageUnitId();// 用户的机构ID
			Long al_hsql = 0L;
			if (user.getProperty("wardId") != null
					&& user.getProperty("wardId") != "") {
				al_hsql = Long.parseLong((String) user.getProperty("wardId"));
			}
			String queryCnd = null;
			if (req.containsKey("cnd")) {
				queryCnd = (String) req.get("cnd");
			}
			Long al_zyh = 0L;
			Long ldt_fyfs = 0L;
			int ldt_lsyz = 0;
			Long ldt_yfsb = 0L;
			Date ldt_server = sdfdate.parse(sdfdate.format(new Date()));
			if (queryCnd.indexOf("#") > 0) {
				String[] strs = queryCnd.split("#");
				al_zyh = Long.parseLong(strs[0]);
				ldt_server = sdfdate.parse(strs[1]);
				ldt_fyfs = Long.parseLong(strs[2]);
				ldt_lsyz = Integer.parseInt(strs[3]);
				ldt_yfsb = Long.parseLong(strs[4]);
			}

			List<Map<String, Object>> collardrugdetailslist = new ArrayList<Map<String, Object>>();
			// List<Date> qrsjlist = new ArrayList<Date>();
			Map<Long,List<Date>> qrsjMap = new HashMap<Long,List<Date>>();
			Map<String, Object> collardrugdetailsparameters = new HashMap<String, Object>();
			StringBuffer sql = new StringBuffer(
					"SELECT ZY_BRRY.BRCH as BRCH,ZY_BRRY.BRXM as BRXM,ZY_BQYZ.YZMC as YZMC,ZY_BQYZ.MZCS as MZCS,str(ZY_BQYZ.KSSJ,'YYYY-MM-DD HH24:MI:SS') as KSSJ,str(ZY_BQYZ.TZSJ,'YYYY-MM-DD HH24:MI:SS') as TZSJ,ZY_BQYZ.YPYF as YPYF,ZY_BQYZ.MRCS as MRCS,ZY_BQYZ.YCSL as YCSL,ZY_BQYZ.YPDJ as YPDJ,ZY_BQYZ.SYBZ as SYBZ,ZY_BQYZ.SRKS as SRKS,str(ZY_BQYZ.QRSJ,'YYYY-MM-DD HH24:MI:SS') as QRSJ,ZY_BQYZ.YPXH as YPXH,ZY_BQYZ.YPLX as YPLX,ZY_BQYZ.JLXH as JLXH,ZY_BQYZ.FYFS as FYFS,ZY_BQYZ.SYPC as SYPC,ZY_BQYZ.ZYH as ZYH,ZY_BQYZ.BZXX as BZXX,ZY_BQYZ.XMLX as XMLX,ZY_BQYZ.YZZH as YZZH,ZY_BQYZ.FYSX as FYSX,ZY_BQYZ.LSYZ as LSYZ,ZY_BQYZ.YFSB as YFSB,0 as FYTS,0 as FYCS,0.00 as YPSL,YK_TYPK.YPSX as YPSX,ZY_BQYZ.YPCD as YPCD,ZY_BRRY.DJID as DJID,ZY_BQYZ.YZPB as YZPB,ZY_BQYZ.YSTJ as YSTJ,ZY_BQYZ.YEPB as YEPB,ZY_BQYZ.SRCS as SRCS,ZY_BQYZ.ZXKS as ZXKS,ZY_BQYZ.YFGG as YFGG,ZY_BQYZ.YFDW as YFDW,ZY_BQYZ.YFBZ as YFBZ,ZY_BQYZ.YYTS as YYTS,ZY_BQYZ.BRKS as BRKS,ZY_BQYZ.BRBQ as BRBQ,ZY_BQYZ.YZZXSJ as YZZXSJ FROM ZY_BQYZ ZY_BQYZ,ZY_BRRY ZY_BRRY,YK_TYPK YK_TYPK WHERE ZY_BQYZ.ZFYP!=1 and ( ZY_BQYZ.ZYH=ZY_BRRY.ZYH and ZY_BRRY.CYPB=0) and (ZY_BQYZ.YPXH=YK_TYPK.YPXH) and (ZY_BQYZ.SRKS=:SRKS) and (ZY_BQYZ.LSBZ=0) and (ZY_BQYZ.SYBZ = 0) and (ZY_BQYZ.XMLX<4) and (ZY_BQYZ.FYSX<>2) AND (ZY_BQYZ.JFBZ<2) and ( ZY_BQYZ.JGID=:JGID) and (ZY_BQYZ.YSBZ=0 or (ZY_BQYZ.YSBZ=1 AND ZY_BQYZ.YSTJ=1)) and ZFBZ=0 and ((ZY_BQYZ.PSPB IS NULL) or (ZY_BQYZ.PSPB=0) or (ZY_BQYZ.PSPB=1))");
			if (req.containsKey("ZYHS")) {
				sql.append(" and ZY_BQYZ.ZYH in(:zyhs)");
				List<Object> l = (List<Object>) req.get("ZYHS");
				List<Long> zyhs = new ArrayList<Long>();
				for (Object o : l) {
					zyhs.add(MedicineUtils.parseLong(o));
				}
				collardrugdetailsparameters.put("zyhs", zyhs);
			} else {
				if (al_zyh != 0L) {
					sql.append(" and (ZY_BQYZ.ZYH=:ZYH or ZY_BQYZ.ZYH=0)");
					collardrugdetailsparameters.put("ZYH", al_zyh);
				}
			}
			if (ldt_yfsb != 0L) {
				sql.append(" and ZY_BQYZ.YFSB=:YFSB");
				collardrugdetailsparameters.put("YFSB", ldt_yfsb);
			}
			if (ldt_lsyz == 0 || ldt_lsyz == 1) {
				sql.append(" and ZY_BQYZ.LSYZ=:LSYZ and XMLX<>2 and XMLX<>3");
				collardrugdetailsparameters.put("LSYZ", ldt_lsyz);
			} else if (ldt_lsyz == 3) {
				sql.append(" and ZY_BQYZ.XMLX=:LSYZ");
				collardrugdetailsparameters.put("LSYZ", 2);
			} else if (ldt_lsyz == 4) {
				sql.append(" and ZY_BQYZ.XMLX=:LSYZ");
				collardrugdetailsparameters.put("LSYZ", 3);
			}
			if (ldt_fyfs != 0L) {
				sql.append(" and ZY_BQYZ.FYFS=:FYFS");
				collardrugdetailsparameters.put("FYFS", ldt_fyfs);
			}
			String XYF = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.FHYZHJF, ctx);
			if ("1".equals(XYF)) {
				sql.append(" and ZY_BQYZ.FHBZ=1");
			}
			sql.append(" ORDER BY ZY_BQYZ.XMLX,ZY_BQYZ.YZZH ASC,ZY_BQYZ.YFSB DESC,ZY_BQYZ.FYFS,ZY_BQYZ.LSYZ ASC");
			collardrugdetailsparameters.put("JGID", manaUnitId);
			collardrugdetailsparameters.put("SRKS", al_hsql);
			collardrugdetailslist = dao.doQuery(sql.toString(),
					collardrugdetailsparameters);
			// 处理领药明细表单数据
			for (int i = 0; i < collardrugdetailslist.size(); i++) {
				Date ldt_qrsj = null;
				Date ldt_kssj = null;
				if (collardrugdetailslist.get(i).get("QRSJ") != null) {
					ldt_qrsj = sdfdate.parse(collardrugdetailslist.get(i).get(
							"QRSJ")
							+ "");
				}
				if (collardrugdetailslist.get(i).get("KSSJ") != null) {
					ldt_kssj = sdfdate.parse(collardrugdetailslist.get(i).get(
							"KSSJ")
							+ "");
				}
				if (ldt_qrsj == null
						&& (ldt_server.getTime() >= ldt_kssj.getTime())) {
					continue;
				}
				if (ldt_qrsj != null) {
					if (ldt_server.getTime() < ldt_qrsj.getTime()
							|| ldt_server.getTime() < ldt_kssj.getTime()) {
						collardrugdetailslist.remove(i);
						i--;
					}
				} else if (ldt_server.getTime() < ldt_kssj.getTime()) {// 如果领药日期小于医嘱确认日期
					collardrugdetailslist.remove(i);
					i--;
				}
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("firstrow", 1);
			parameters.put("lastrow", collardrugdetailslist.size());
			parameters.put("ldt_server", ldt_server);
			BSPHISUtil.uf_yztj(collardrugdetailslist, qrsjMap, parameters,
					dao, ctx);
			for (int i = 0; i < collardrugdetailslist.size(); i++) {
				int ll_fycs_total = Integer.parseInt(collardrugdetailslist.get(
						i).get("FYCS")
						+ "");
				int ll_xmlx = Integer.parseInt(collardrugdetailslist.get(i)
						.get("XMLX") + "");// 项目类型
				if (ll_xmlx != 3) {
					if (ll_fycs_total == 0) {
						collardrugdetailslist.remove(i);
						i--;
					}
				}
			}
			SchemaUtil.setDictionaryMassageForList(collardrugdetailslist,
					"phis.application.war.schemas.ZY_BQYZ_TJ");
			res.put("body", collardrugdetailslist);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("医嘱查询失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-12
	 * @description 病区医嘱提交 左边病人列表数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDoctorAdviceBrQuery(
			Map<String, Object> req, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		doGetDoctorAdviceSubmitQuery(req, res, ctx);
		List<Map<String, Object>> list = (List<Map<String, Object>>) res
				.get("body");
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			int i = 0;
			for (Map<String, Object> m : ret) {
				if (MedicineUtils.parseLong(map.get("ZYH")) == MedicineUtils
						.parseLong(m.get("ZYH"))) {
					i = 1;
					break;
				}
			}
			if (i == 0) {
				Map<String, Object> map_temp = new HashMap<String, Object>();
				map_temp.put("ZYH", MedicineUtils.parseLong(map.get("ZYH")));
				map_temp.put("BRCH", MedicineUtils.parseString(map.get("BRCH")));
				map_temp.put("BRXM", MedicineUtils.parseString(map.get("BRXM")));
				ret.add(map_temp);
			}
		}
		return ret;
	}

	/**
	 * 校验抗菌药物是否审批
	 * 
	 * @param body
	 * @param ctx
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> doCheckAntibacterial(List<Map<String, Object>> list,
			Context ctx) throws PersistentDataOperationException,
			ModelDataOperationException {
		List<String> errMsg = new ArrayList<String>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String QYKJYWGL = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.QYKJYWGL, ctx);
		String QYZYKJYWSP = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.QYZYKJYWSP, ctx);
		Map<Object, Object> allMeds = new HashMap<Object, Object>();
		if (!"1".equals(QYKJYWGL) || !"1".equals(QYZYKJYWSP))// 参数启用
			return errMsg;
		// 1.按住院号分组，获取所有临时医嘱集合
		for (Map<String, Object> r : list) {
			Object zyh = r.get("ZYH");
			if (!allMeds.containsKey(zyh)) {
				allMeds.put(zyh, new ArrayList<Map<String, Object>>());
			}
			if ("1".equals(r.get("LSYZ").toString())
					&& "1".equals(r.get("KSBZ").toString())) {// 临时医嘱且是抗菌药物
				((List<Map<String, Object>>) allMeds.get(zyh)).add(r);
			}
		}
		for (Object zyh : allMeds.keySet()) {
			// 2.获取所有药品的审批总量
			Map<String, Object> cacheSpxx = new HashMap<String, Object>();// 缓存审批信息
			for (Map<String, Object> ypxx : (List<Map<String, Object>>) allMeds
					.get(zyh)) {
				// 查询医嘱中所有需要审批的抗菌药物的数量
				Map<String, Object> params = new HashMap<String, Object>();
				if (!cacheSpxx.containsKey(zyh + "_" + ypxx.get("YPXH"))) {
					params.put("ZYH", zyh.toString());
					// params.put("YSGH", body.get("YSGH"));
					params.put("JGID", manageUnit);
					params.put("YPXH",
							Long.parseLong(ypxx.get("YPXH").toString()));
					// 不论是谁申请，只要审批通过，其他医生也能使用
					Map<String, Object> r = dao
							.doLoad("select sum(SPYL) as SPYL from AMQC_SYSQ01 where JZLX=1 and JZXH=:ZYH and YPXH=:YPXH and (SPJG=1 or SPJG=2) and ZFBZ=0 and JGID=:JGID group by YPXH",
									params);
					params.put("ZYH", Long.parseLong(zyh.toString()));
					List<Map<String, Object>> l = dao
							.doQuery(
									"select sum(YCSL) as SYSL from ZY_BQYZ where JGID=:JGID and ZYH=:ZYH and YPXH=:YPXH and SYBZ=1",
									params);
					Double sysl = 0.0;
					if (l != null && l.size() > 0
							&& l.get(0).get("SYSL") != null) {
						sysl = Double.parseDouble(l.get(0).get("SYSL")
								.toString());
					}
					if (r != null && r.get("SPYL") != null
							&& r.get("SPYL").toString().trim().length() > 0) {
						r.put("SPYL",
								Double.parseDouble(r.get("SPYL").toString())
										- sysl);
					}
					cacheSpxx.put(zyh + "_" + ypxx.get("YPXH"), r);
				}
				Map<String, Object> qmqc_sysq01 = (Map<String, Object>) cacheSpxx
						.get(zyh + "_" + ypxx.get("YPXH"));
				if (qmqc_sysq01 == null
						|| qmqc_sysq01.get("SPYL") == null
						|| qmqc_sysq01.get("SPYL").toString().trim().length() == 0) {
					// 不需要审批，限一日用量
					if (!"1".equals(ypxx.get("SFSP") + "")) {
						Double ycyl = 0.0;
						if (ypxx.get("YCYL") != null
								&& ypxx.get("YCYL").toString().trim().length() > 0) {
							ycyl = Double.parseDouble(ypxx.get("YCYL")
									.toString());
						}
						if (Double.parseDouble(ypxx.get("YCSL").toString()) > ycyl) {
							list.remove(ypxx);
							errMsg.add("病人(" + ypxx.get("BRXM") + ")的抗菌药物【"
									+ ypxx.get("YPMC") + "】提交失败：超过一日限量!");
						}
					} else {
						list.remove(ypxx);
						errMsg.add("病人(" + ypxx.get("BRXM") + ")的抗菌药物【"
								+ ypxx.get("YPMC") + "】提交失败：尚未审批或审批未通过!");
					}
				} else if (Double.parseDouble(ypxx.get("YCSL").toString()) > Double
						.parseDouble(qmqc_sysq01.get("SPYL").toString())) {
					list.remove(ypxx);
					errMsg.add("病人(" + ypxx.get("BRXM") + ")的抗菌药物【"
							+ ypxx.get("YPMC") + "】用量超过审批用量，<br />已审批用量为"
							+ String.format("%.2f", qmqc_sysq01.get("SPYL"))
							+ "!当前使用数量为" + ypxx.get("YCSL").toString());

				} else {
					qmqc_sysq01.put(
							"SPYL",
							Double.parseDouble(qmqc_sysq01.get("SPYL")
									.toString())
									- Double.parseDouble(ypxx.get("YCSL")
											.toString()));
				}

			}
		}
		return errMsg;
	}

	/**
	 * 获取已提交的总量
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadAmqcCount(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		// List<Object> cnd = (List<Object>) req.get("cnd");
		long zyh = Long.parseLong(body.get("ZYH") + "");
		long ypxh = Long.parseLong(body.get("YPXH") + "");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", zyh);
			parameters.put("YPXH", ypxh);
			List<Map<String, Object>> list = dao
					.doQuery(
							"select sum(YCSL) as SYSL from ZY_BQYZ where ZYH=:ZYH and YPXH=:YPXH and SYBZ=1",
							parameters);
			if (list == null || list.size() == 0) {
				res.put("SYSL", 0);
			} else {
				res.put("SYSL", list.get(0).get("SYSL"));
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取抗菌药物使用数量错误!", e);
		}
	}

	/**
	 * 确认医嘱提交
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDoctorAdviceSubmit(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfdatetime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Long al_zyh = 0L;
			Long ldt_fyfs = 0L;
			int ldt_lsyz = 0;
			Long ldt_yfsb = 0L;
			int tjindx = 0;
			// int sjindex = 0;
			if (req.get("ZYH") != null) {
				al_zyh = Long.parseLong(req.get("ZYH") + "");
			}
			Date ldt_tjsj = sdfdatetime.parse(sdfdatetime.format(new Date()));
			Date ldt_server = sdfdate.parse(sdfdate.format(new Date()));
			if (req.get("LYRQ") != null) {
				ldt_server = sdfdate.parse(req.get("LYRQ") + "");
			}
			ldt_fyfs = Long.parseLong(req.get("FYFS") + "");
			ldt_lsyz = Integer.parseInt(req.get("LSYZ") + "");
			ldt_yfsb = Long.parseLong(req.get("YFSB") + "");
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			String uid = (String) user.getUserId();
			Long al_hsql = 0L;
			if (user.getProperty("wardId") != null
					&& user.getProperty("wardId") != "") {
				al_hsql = Long.parseLong((String) user.getProperty("wardId"));
				// 判断是否成立
				String bqtjts = ParameterUtil.getParameter(manaUnitId, "BQTJTS"
						+ al_hsql, "0", "病区提交天数启用 0.不启用1.启用", ctx);
				String ll_yzts = ParameterUtil.getParameter(manaUnitId, "YZTS"
						+ al_hsql, "0", "医嘱提交最大天数控制", ctx);
				if (req.get("LYRQ") != null) {
					ldt_server = sdfdate.parse(req.get("LYRQ") + "");
				}
				int li_tjts = BSHISUtil.getPeriod(ldt_server, new Date());
				if ("1".equals(bqtjts)) {
					if (li_tjts > Integer.parseInt(ll_yzts)) {
						throw new ModelDataOperationException("提交天数超过最大天数控制!");
					}
				}
			}
			List<Map<String, Object>> collardrugdetailslist = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> tj01list = new ArrayList<Map<String, Object>>();
			// List<Date> qrsjlist = new ArrayList<Date>();
			Map<Long,List<Date>> qrsjMap = new HashMap<Long,List<Date>>();
			Map<String, Object> collardrugdetailsparameters = new HashMap<String, Object>();
			StringBuffer sql = new StringBuffer(
					"SELECT ZY_BRRY.BRCH as BRCH,ZY_BRRY.BRXM as BRXM,ZY_BRRY.BRXZ as BRXZ,ZY_BQYZ.YZMC as YPMC,ZY_BQYZ.MZCS as MZCS,str(ZY_BQYZ.KSSJ,'YYYY-MM-DD HH24:MI:SS') as KSSJ,str(ZY_BQYZ.TZSJ,'YYYY-MM-DD HH24:MI:SS') as TZSJ,ZY_BQYZ.YPYF as YPYF,ZY_BQYZ.MRCS as MRCS,ZY_BQYZ.YCSL as YCSL,ZY_BQYZ.YPDJ as YPDJ,ZY_BQYZ.SYBZ as SYBZ,ZY_BQYZ.SRKS as SRKS,str(ZY_BQYZ.QRSJ,'YYYY-MM-DD HH24:MI:SS') as QRSJ,ZY_BQYZ.YPXH as YPXH,ZY_BQYZ.YPLX as YPLX,ZY_BQYZ.JLXH as JLXH,ZY_BQYZ.FYFS as FYFS,ZY_BQYZ.SYPC as SYPC,ZY_BQYZ.ZYH as ZYH,ZY_BQYZ.BZXX as BZXX,ZY_BQYZ.XMLX as XMLX,ZY_BQYZ.YZZH as YZZH,ZY_BQYZ.FYSX as FYSX,ZY_BQYZ.LSYZ as LSYZ,YK_TYPK.SFSP as SFSP,YK_TYPK.YCYL as YCYL,ZY_BQYZ.YFSB as YFSB,0 as FYTS,0 as FYCS,0.00 as YPSL,YK_TYPK.YPSX as YPSX,YK_TYPK.KSBZ as KSBZ,ZY_BQYZ.YPCD as YPCD,ZY_BRRY.DJID as DJID,ZY_BQYZ.YZPB as YZPB,ZY_BQYZ.YSTJ as YSTJ,ZY_BQYZ.YEPB as YEPB,ZY_BQYZ.SRCS as SRCS,ZY_BQYZ.ZXKS as ZXKS,ZY_BQYZ.YFGG as YFGG,ZY_BQYZ.YFDW as YFDW,ZY_BQYZ.YFBZ as YFBZ,ZY_BQYZ.YYTS as YYTS,ZY_BQYZ.BRKS as BRKS,ZY_BQYZ.BRBQ as BRBQ,ZY_BQYZ.YZZXSJ as YZZXSJ FROM ZY_BQYZ ZY_BQYZ,ZY_BRRY ZY_BRRY,YK_TYPK YK_TYPK WHERE ( ZY_BQYZ.ZYH=ZY_BRRY.ZYH AND ZY_BRRY.CYPB=0) and (ZY_BQYZ.YPXH=YK_TYPK.YPXH) and (ZY_BQYZ.SRKS=:SRKS) AND (ZY_BQYZ.LSBZ=0) AND (ZY_BQYZ.SYBZ = 0) AND (ZY_BQYZ.XMLX<4) AND (ZY_BQYZ.FYSX<>2) AND (ZY_BQYZ.JFBZ<2) AND ( ZY_BQYZ.JGID=:JGID) AND (ZY_BQYZ.YSBZ=0 OR (ZY_BQYZ.YSBZ=1 AND ZY_BQYZ.YSTJ=1)) AND ZFBZ=0 AND ((ZY_BQYZ.PSPB IS NULL) OR (ZY_BQYZ.PSPB=0) OR (ZY_BQYZ.PSPB=1))");
			collardrugdetailsparameters.put("JGID", manaUnitId);
			collardrugdetailsparameters.put("SRKS", al_hsql);
			if (req.containsKey("JLXHS")) {
				sql.append(" and ZY_BQYZ.JLXH in(:jlxhs)");
				List<Object> l = (List<Object>) req.get("JLXHS");
				List<Long> jlxhs = new ArrayList<Long>();
				for (Object o : l) {
					jlxhs.add(MedicineUtils.parseLong(o));
				}
				collardrugdetailsparameters.put("jlxhs", jlxhs);
			}
			if (al_zyh != 0L) {
				sql.append(" AND (ZY_BQYZ.ZYH=:ZYH or ZY_BQYZ.ZYH=0)");
				collardrugdetailsparameters.put("ZYH", al_zyh);
			}
			if (ldt_yfsb != 0L) {
				sql.append(" and ZY_BQYZ.YFSB=:YFSB");
				collardrugdetailsparameters.put("YFSB", ldt_yfsb);
			}
			if (ldt_lsyz == 0 || ldt_lsyz == 1) {
				sql.append(" and ZY_BQYZ.LSYZ=:LSYZ and XMLX<>2 and XMLX<>3");
				collardrugdetailsparameters.put("LSYZ", ldt_lsyz);
			} else if (ldt_lsyz == 3) {
				sql.append(" and ZY_BQYZ.XMLX=:LSYZ");
				collardrugdetailsparameters.put("LSYZ", 2);
			} else if (ldt_lsyz == 4) {
				sql.append(" and ZY_BQYZ.XMLX=:LSYZ");
				collardrugdetailsparameters.put("LSYZ", 3);
			}
			if (ldt_fyfs != 0L) {
				sql.append(" and ZY_BQYZ.FYFS=:FYFS");
				collardrugdetailsparameters.put("FYFS", ldt_fyfs);
			}
			String XYF = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.FHYZHJF, ctx);
			if ("1".equals(XYF)) {
				sql.append(" and ZY_BQYZ.FHBZ=1");
			}
			sql.append(" ORDER BY ZY_BQYZ.XMLX,ZY_BQYZ.YZZH ASC,ZY_BQYZ.YFSB DESC,ZY_BQYZ.FYFS,ZY_BQYZ.LSYZ ASC");
			collardrugdetailslist = dao.doQuery(sql.toString(),
					collardrugdetailsparameters);
			// 处理领药明细表单数据
			for (int i = 0; i < collardrugdetailslist.size(); i++) {
				Date ldt_qrsj = null;
				Date ldt_kssj = null;
				if (collardrugdetailslist.get(i).get("QRSJ") != null) {
					ldt_qrsj = sdfdate.parse(collardrugdetailslist.get(i).get(
							"QRSJ")
							+ "");
				}
				if (collardrugdetailslist.get(i).get("KSSJ") != null) {
					ldt_kssj = sdfdate.parse(collardrugdetailslist.get(i).get(
							"KSSJ")
							+ "");
				}
				if (ldt_qrsj == null
						&& (ldt_server.getTime() >= ldt_kssj.getTime())) {
					continue;
				}
				if (ldt_qrsj != null) {
					if (ldt_server.getTime() < ldt_qrsj.getTime()
							|| ldt_server.getTime() < ldt_kssj.getTime()) {
						collardrugdetailslist.remove(i);
						i--;
					}
				} else if (ldt_server.getTime() < ldt_kssj.getTime()) {// 如果领药日期小于医嘱确认日期
					collardrugdetailslist.remove(i);
					i--;
				}
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("firstrow", 1);
			parameters.put("lastrow", collardrugdetailslist.size());
			parameters.put("ldt_server", ldt_server);

			BSPHISUtil.uf_yztj(collardrugdetailslist, qrsjMap, parameters,
					dao, ctx);

			BSPHISUtil.ArrearsPatientsQuery(collardrugdetailslist, ctx, dao,
					res);

			for (int i = 0; i < collardrugdetailslist.size(); i++) {
				int ll_xmlx = Integer.parseInt(collardrugdetailslist.get(i)
						.get("XMLX") + "");// 项目类型
				int ll_fycs_total = Integer.parseInt(collardrugdetailslist.get(
						i).get("FYCS")
						+ "");
				if (ll_xmlx != 3) {
					if (ll_fycs_total == 0) {
						collardrugdetailslist.remove(i);
						i--;
					}
				}
			}
			// add by yangl 增加抗菌药物审批判断
			List<String> warnMsg = doCheckAntibacterial(collardrugdetailslist,
					ctx);
			if (warnMsg != null && warnMsg.size() > 0) {
				res.put("warnMsg", warnMsg);
			}
			for (int i = 0; i < collardrugdetailslist.size(); i++) {
				int eqorne = 0;
				Long ll_zyh = Long.parseLong(collardrugdetailslist.get(i).get(
						"ZYH")
						+ ""); // 住院号
				Long ll_yzxh = Long.parseLong(collardrugdetailslist.get(i).get(
						"JLXH")
						+ ""); // 医嘱序号
				Long ll_ypcd = Long.parseLong(collardrugdetailslist.get(i).get(
						"YPCD")
						+ ""); // 药品产地
				Long ll_ypxh = Long.parseLong(collardrugdetailslist.get(i).get(
						"YPXH")
						+ ""); // 药品序号
				Double ld_ypdj = Double.parseDouble(collardrugdetailslist
						.get(i).get("YPDJ") + "");// 药品单价
				Double ld_ycsl = Double.parseDouble(collardrugdetailslist
						.get(i).get("YCSL") + "");// 药品单价
				Date ldt_kssj = sdfdatetime.parse(collardrugdetailslist.get(i)
						.get("KSSJ") + ""); // 开始时间
				String ls_sypc = collardrugdetailslist.get(i).get("SYPC") + "";// 使用频次
				int ll_xmlx = Integer.parseInt(collardrugdetailslist.get(i)
						.get("XMLX") + "");// 项目类型
				Long ll_yfsb = Long.parseLong(collardrugdetailslist.get(i).get(
						"YFSB")
						+ ""); // 药房识别
				Long ll_fyfs = Long.parseLong(collardrugdetailslist.get(i).get(
						"FYFS")
						+ ""); // 发药方式
				int ll_lsyz = Integer.parseInt(collardrugdetailslist.get(i)
						.get("LSYZ") + "");// 临时医嘱标志
				Long ll_brks = Long.parseLong(collardrugdetailslist.get(i).get(
						"BRKS")
						+ "");// 病人科室
				int ll_yepb = Integer.parseInt(collardrugdetailslist.get(i)
						.get("YEPB") + "");// 婴儿判别
				String ls_yfdw = collardrugdetailslist.get(i).get("YFDW") + "";// 药房单位
				String ls_yfgg = collardrugdetailslist.get(i).get("YFGG") + ""; // 药房规格
				int ll_yfbz = Integer.parseInt(collardrugdetailslist.get(i)
						.get("YFBZ") + "");// 药房包装
				int ll_mrcs = Integer.parseInt(collardrugdetailslist.get(i)
						.get("MRCS") + "");// 每日次数
				int ll_fycs_total = 0;
				if (ll_xmlx != 3) {
					ll_fycs_total = Integer.parseInt(collardrugdetailslist.get(
							i).get("FYCS")
							+ "");
					if (ll_fycs_total == 0) {
						continue;
					}
				}
				List<Date> qrsjlist = qrsjMap.get(ll_yzxh); // 根据医嘱需要获取医嘱对应的确认时间
				Map<String, Object> updatesybz = new HashMap<String, Object>();
				updatesybz.put("JLXH", ll_yzxh);
				dao.doUpdate("update ZY_BQYZ set SYBZ=1 where JLXH=:JLXH",
						updatesybz);
				if (tj01list.size() <= 0) {// 如果提交表里没有就插入一条
					// 主表bq_tj01
					Map<String, Object> tj01map = new HashMap<String, Object>();
					tj01map.put("TJYF", ll_yfsb);
					tj01map.put("YZLX", ll_xmlx);
					tj01map.put("TJSJ", ldt_tjsj);
					if (user.getProperty("wardId") != null
							&& user.getProperty("wardId") != "") {
						tj01map.put("TJBQ", Long.parseLong((String) user
								.getProperty("wardId")));
					}
					tj01map.put("TJGH", uid);
					tj01map.put("XMLX", 1);
					tj01map.put("FYBZ", 0);
					tj01map.put("FYFS", ll_fyfs);
					tj01map.put("LSYZ", ll_lsyz);
					tj01map.put("JGID", manaUnitId);
					Map<String, Object> yj01Req;
					yj01Req = dao.doSave("create", BSPHISEntryNames.BQ_TJ01,
							tj01map, false);
					tj01map.put("TJXH", yj01Req.get("TJXH"));// 放主键
					tj01list.add(tjindx, tj01map);
					// 明细表bq_tj02
					if (ll_xmlx == 3) {
						Map<String, Object> tj02map = new HashMap<String, Object>();
						tj02map.put(
								"TJXH",
								Long.parseLong(tj01list.get(tjindx).get("TJXH")
										+ ""));// 取主键
						tj02map.put("YZXH", ll_yzxh);
						tj02map.put("ZYH", ll_zyh);
						tj02map.put("YPXH", ll_ypxh);
						tj02map.put("YPCD", ll_ypcd);
						tj02map.put("KSSJ", ldt_kssj);
						tj02map.put("YCSL", ld_ycsl);
						tj02map.put("YTCS", ll_mrcs);
						tj02map.put("JFRQ", sdfdatetime.parse(sdfdatetime
								.format(new Date())));
						tj02map.put("SYPC", ls_sypc);
						tj02map.put("YPDJ", ld_ypdj);
						tj02map.put("YFDW", "null".equals(ls_yfdw) ? ""
								: ls_yfdw);
						tj02map.put("YFGG", "null".equals(ls_yfgg) ? ""
								: ls_yfgg);
						tj02map.put("YFBZ", ll_yfbz);
						tj02map.put("QRRQ", sdfdatetime.parse(sdfdatetime
								.format(new Date())));
						tj02map.put("LSYZ", ll_lsyz);
						tj02map.put("FYJE", 0);
						tj02map.put("FYBZ", 0);
						tj02map.put("QZCL", 0);
						tj02map.put("SJFYBZ", 0);
						tj02map.put("FYSL", ld_ycsl);
						tj02map.put("JGID", manaUnitId);
						tj02map.put("YEPB", ll_yepb);
						tj02map.put("FYKS", ll_brks);
						tj02map.put("TJYF", ll_yfsb);
						tj02map.put("FYFS", ll_fyfs);
						tj02map.put("YZLX", ll_xmlx);
						dao.doSave("create", BSPHISEntryNames.BQ_TJ02, tj02map,
								false);
					} else {
						for (int li_i = 0; li_i < ll_fycs_total; li_i++) {
							Map<String, Object> tj02map = new HashMap<String, Object>();
							tj02map.put(
									"TJXH",
									Long.parseLong(tj01list.get(tjindx).get(
											"TJXH")
											+ ""));// 取主键
							tj02map.put("YZXH", ll_yzxh);
							tj02map.put("ZYH", ll_zyh);
							tj02map.put("YPXH", ll_ypxh);
							tj02map.put("YPCD", ll_ypcd);
							tj02map.put("KSSJ", ldt_kssj);
							tj02map.put("YCSL", ld_ycsl);
							tj02map.put("YTCS", ll_mrcs);
							tj02map.put("JFRQ", sdfdatetime.parse(sdfdatetime
									.format(qrsjlist.get(li_i))));
							tj02map.put("SYPC", ls_sypc);
							tj02map.put("YPDJ", ld_ypdj);
							tj02map.put("YFDW", "null".equals(ls_yfdw) ? ""
									: ls_yfdw);
							tj02map.put("YFGG", "null".equals(ls_yfgg) ? ""
									: ls_yfgg);
							tj02map.put("YFBZ", ll_yfbz);
							tj02map.put("QRRQ", sdfdatetime.parse(sdfdatetime
									.format(qrsjlist.get(li_i))));
							tj02map.put("LSYZ", ll_lsyz);
							tj02map.put("FYJE", 0);
							tj02map.put("FYBZ", 0);
							tj02map.put("QZCL", 0);
							tj02map.put("SJFYBZ", 0);
							tj02map.put("FYSL", ld_ycsl);
							tj02map.put("JGID", manaUnitId);
							tj02map.put("YEPB", ll_yepb);
							tj02map.put("FYKS", ll_brks);
							tj02map.put("TJYF", ll_yfsb);
							tj02map.put("FYFS", ll_fyfs);
							tj02map.put("YZLX", ll_xmlx);
							dao.doSave("create", BSPHISEntryNames.BQ_TJ02,
									tj02map, false);
							// sjindex++;
						}
					}
					tjindx++;
				} else {
					for (int j = 0; j < tj01list.size(); j++) {
						if (tj01list.get(j).get("TJYF").toString()
								.equals(ll_yfsb + "")
								&& tj01list.get(j).get("YZLX").toString()
										.equals(ll_xmlx + "")
								&& tj01list.get(j).get("FYFS").toString()
										.equals(ll_fyfs + "")
								&& tj01list.get(j).get("LSYZ").toString()
										.equals(ll_lsyz + "")) {
							// 明细表bq_tj02
							if (ll_xmlx == 3) {
								Map<String, Object> tj02map = new HashMap<String, Object>();
								tj02map.put(
										"TJXH",
										Long.parseLong(tj01list.get(j).get(
												"TJXH")
												+ ""));// 如果相等就直接取此条的主键
								tj02map.put("YZXH", ll_yzxh);
								tj02map.put("ZYH", ll_zyh);
								tj02map.put("YPXH", ll_ypxh);
								tj02map.put("YPCD", ll_ypcd);
								tj02map.put("KSSJ", ldt_kssj);
								tj02map.put("YCSL", ld_ycsl);
								tj02map.put("YTCS", ll_mrcs);
								tj02map.put("JFRQ", sdfdatetime
										.parse(sdfdatetime.format(new Date())));
								tj02map.put("SYPC", ls_sypc);
								tj02map.put("YPDJ", ld_ypdj);
								tj02map.put("YFDW", ls_yfdw);
								tj02map.put("YFGG", ls_yfgg);
								tj02map.put("YFBZ", ll_yfbz);
								tj02map.put("QRRQ", sdfdatetime
										.parse(sdfdatetime.format(new Date())));
								tj02map.put("LSYZ", ll_lsyz);
								tj02map.put("FYJE", 0);
								tj02map.put("FYBZ", 0);
								tj02map.put("QZCL", 0);
								tj02map.put("SJFYBZ", 0);
								tj02map.put("FYSL", ld_ycsl);
								tj02map.put("JGID", manaUnitId);
								tj02map.put("YEPB", ll_yepb);
								tj02map.put("FYKS", ll_brks);
								tj02map.put("TJYF", ll_yfsb);
								tj02map.put("FYFS", ll_fyfs);
								tj02map.put("YZLX", ll_xmlx);
								dao.doSave("create", BSPHISEntryNames.BQ_TJ02,
										tj02map, false);
							} else {
								for (int li_i = 0; li_i < ll_fycs_total; li_i++) {
									Map<String, Object> tj02map = new HashMap<String, Object>();
									tj02map.put(
											"TJXH",
											Long.parseLong(tj01list.get(j).get(
													"TJXH")
													+ ""));// 如果相等就直接取此条的主键
									tj02map.put("YZXH", ll_yzxh);
									tj02map.put("ZYH", ll_zyh);
									tj02map.put("YPXH", ll_ypxh);
									tj02map.put("YPCD", ll_ypcd);
									tj02map.put("KSSJ", ldt_kssj);
									tj02map.put("YCSL", ld_ycsl);
									tj02map.put("YTCS", ll_mrcs);
									tj02map.put("JFRQ", sdfdatetime
											.parse(sdfdatetime.format(qrsjlist
													.get(li_i))));
									tj02map.put("SYPC", ls_sypc);
									tj02map.put("YPDJ", ld_ypdj);
									tj02map.put("YFDW", ls_yfdw);
									tj02map.put("YFGG", ls_yfgg);
									tj02map.put("YFBZ", ll_yfbz);
									tj02map.put("QRRQ", sdfdatetime
											.parse(sdfdatetime.format(qrsjlist
													.get(li_i))));
									tj02map.put("LSYZ", ll_lsyz);
									tj02map.put("FYJE", 0);
									tj02map.put("FYBZ", 0);
									tj02map.put("QZCL", 0);
									tj02map.put("SJFYBZ", 0);
									tj02map.put("FYSL", ld_ycsl);
									tj02map.put("JGID", manaUnitId);
									tj02map.put("YEPB", ll_yepb);
									tj02map.put("FYKS", ll_brks);
									tj02map.put("TJYF", ll_yfsb);
									tj02map.put("FYFS", ll_fyfs);
									tj02map.put("YZLX", ll_xmlx);
									dao.doSave("create",
											BSPHISEntryNames.BQ_TJ02, tj02map,
											false);
									// sjindex++;
								}
							}
							eqorne = 1;
						}
					}
					if (eqorne == 0) {
						// 如果不想等 继续插入主表明细表
						// 主表bq_tj01
						Map<String, Object> tj01map = new HashMap<String, Object>();
						tj01map.put("TJYF", ll_yfsb);
						tj01map.put("YZLX", ll_xmlx);
						tj01map.put("TJSJ", ldt_tjsj);
						if (user.getProperty("wardId") != null
								&& user.getProperty("wardId") != "") {
							tj01map.put("TJBQ", Long.parseLong((String) user
									.getProperty("wardId")));
						}
						tj01map.put("TJGH", uid);
						tj01map.put("XMLX", 1);
						tj01map.put("FYBZ", 0);
						tj01map.put("FYFS", ll_fyfs);
						tj01map.put("LSYZ", ll_lsyz);
						tj01map.put("JGID", manaUnitId);
						Map<String, Object> yj01Req;
						yj01Req = dao.doSave("create",
								BSPHISEntryNames.BQ_TJ01, tj01map, false);
						tj01map.put("TJXH", yj01Req.get("TJXH"));
						tj01list.add(tjindx, tj01map);
						// 明细表bq_tj02
						if (ll_xmlx == 3) {
							Map<String, Object> tj02map = new HashMap<String, Object>();
							tj02map.put(
									"TJXH",
									Long.parseLong(tj01list.get(tjindx).get(
											"TJXH")
											+ ""));
							tj02map.put("YZXH", ll_yzxh);
							tj02map.put("ZYH", ll_zyh);
							tj02map.put("YPXH", ll_ypxh);
							tj02map.put("YPCD", ll_ypcd);
							tj02map.put("KSSJ", ldt_kssj);
							tj02map.put("YCSL", ld_ycsl);
							tj02map.put("YTCS", ll_mrcs);
							tj02map.put("JFRQ", sdfdatetime.parse(sdfdatetime
									.format(new Date())));
							tj02map.put("SYPC", ls_sypc);
							tj02map.put("YPDJ", ld_ypdj);
							tj02map.put("YFDW", ls_yfdw);
							tj02map.put("YFGG", ls_yfgg);
							tj02map.put("YFBZ", ll_yfbz);
							tj02map.put("QRRQ", sdfdatetime.parse(sdfdatetime
									.format(new Date())));
							tj02map.put("LSYZ", ll_lsyz);
							tj02map.put("FYJE", 0);
							tj02map.put("FYBZ", 0);
							tj02map.put("QZCL", 0);
							tj02map.put("SJFYBZ", 0);
							tj02map.put("FYSL", ld_ycsl);
							tj02map.put("JGID", manaUnitId);
							tj02map.put("YEPB", ll_yepb);
							tj02map.put("FYKS", ll_brks);
							tj02map.put("TJYF", ll_yfsb);
							tj02map.put("FYFS", ll_fyfs);
							tj02map.put("YZLX", ll_xmlx);
							dao.doSave("create", BSPHISEntryNames.BQ_TJ02,
									tj02map, false);
						} else {
							for (int li_i = 0; li_i < ll_fycs_total; li_i++) {
								Map<String, Object> tj02map = new HashMap<String, Object>();
								tj02map.put(
										"TJXH",
										Long.parseLong(tj01list.get(tjindx)
												.get("TJXH") + ""));
								tj02map.put("YZXH", ll_yzxh);
								tj02map.put("ZYH", ll_zyh);
								tj02map.put("YPXH", ll_ypxh);
								tj02map.put("YPCD", ll_ypcd);
								tj02map.put("KSSJ", ldt_kssj);
								tj02map.put("YCSL", ld_ycsl);
								tj02map.put("YTCS", ll_mrcs);
								tj02map.put("JFRQ", sdfdatetime
										.parse(sdfdatetime.format(qrsjlist
												.get(li_i))));
								tj02map.put("SYPC", ls_sypc);
								tj02map.put("YPDJ", ld_ypdj);
								tj02map.put("YFDW", ls_yfdw);
								tj02map.put("YFGG", ls_yfgg);
								tj02map.put("YFBZ", ll_yfbz);
								tj02map.put("QRRQ", sdfdatetime
										.parse(sdfdatetime.format(qrsjlist
												.get(li_i))));
								tj02map.put("LSYZ", ll_lsyz);
								tj02map.put("FYJE", 0);
								tj02map.put("FYBZ", 0);
								tj02map.put("QZCL", 0);
								tj02map.put("SJFYBZ", 0);
								tj02map.put("FYSL", ld_ycsl);
								tj02map.put("JGID", manaUnitId);
								tj02map.put("YEPB", ll_yepb);
								tj02map.put("FYKS", ll_brks);
								tj02map.put("TJYF", ll_yfsb);
								tj02map.put("FYFS", ll_fyfs);
								tj02map.put("YZLX", ll_xmlx);
								dao.doSave("create", BSPHISEntryNames.BQ_TJ02,
										tj02map, false);
								// sjindex++;
							}
						}
						tjindx++;
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("确认提交医嘱失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("确认提交医嘱失败!", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-13
	 * @description 退药申请病人信息查询
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryBackApplicationPatientInformation(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select a.ZYH as ZYH,a.ZYHM as ZYHM,a.BRXZ as BRXZ,a.BRXM as BRXM , a.BRXB as BRXB,a.BRCH as BRCH,a.CSNY as CSNY,a.ZYYS as ZYYS,a.ZSYS as ZSYS,a.ZZYS as ZZYS,a.BRKS as BRKS,b.OFFICENAME as OFFICENAME,c.XZMC as XZ from ")
				.append("ZY_BRRY a,SYS_Office b,GY_BRXZ c where a.BRKS=b.ID and a.BRXZ=c.BRXZ   and a.ZYH=:zyh and a.JGID=:jgid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", manaUnitId);
		map_par.put("zyh", Long.parseLong(body.get("ZYH") + ""));
		try {
			ret = dao.doLoad(hql.toString(), map_par);
			if (ret != null) {
				ret.put("JSLX", 0);
				int brxz = Integer.parseInt(ret.get("BRXB") + "");
				if (brxz == 1) {
					ret.put("XB", "男");
				} else if (brxz == 2) {
					ret.put("XB", "女");
				} else if (brxz == 9) {
					ret.put("XB", "未说明的性别");
				} else if (brxz == 0) {
					ret.put("XB", "未知的性别");
				}
				BSPHISUtil.gf_zy_gxmk_getjkhj(ret, dao, ctx);
				BSPHISUtil.gf_zy_gxmk_getzfhj(ret, dao, ctx);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query patient information", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}

		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-15
	 * @description 退药申请查询已发药记录
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryDispensingRecords(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		StringBuffer hql = new StringBuffer();
		UserRoleToken user = UserRoleToken.getCurrent();
		hql.append(
				"select a.JGID as JGID,a.YFSB as YFSB,a.YPXH as YPXH,d.YZMC as YPMC,a.YPCD as YPCD,c.CDMC as CDMC,a.YPDJ as YPDJ,sum(YPSL) as YPSL,b.YFMC as YFMC,a.YPGG as YPGG,a.YFDW as YFDW,a.YFBZ as YFBZ,a.LYBQ as LYBQ,a.YEPB as YEPB,a.ZFBL as ZFBL,a.ZFPB as ZFPB,a.YZXH as YZXH,a.ZYH as ZYH from ")
				.append("YF_ZYFYMX a,YF_YFLB b,YK_CDDZ c,ZY_BQYZ d,ZY_FYMX e where (e.JSCS=0 or e.JSCS is null) and a.JFID=e.JLXH and a.YZXH=d.JLXH and a.YFSB=b.YFSB and a.JGID=b.JGID and a.YPCD =c.YPCD and a.ZYH=:zyh and a.LYBQ=:lybq  and a.FYLX!=0 and a.JGID=:jgid group by a.JGID ,a.YFSB,a.YPXH,d.YZMC,a.YPCD,c.CDMC,a.YPDJ,b.YFMC,a.YPGG ,a.YFDW,a.YFBZ ,a.LYBQ ,a.YEPB ,a.ZFBL,a.ZFPB,a.YZXH,a.ZYH");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("lybq", Long.parseLong(body.get("lybq") + ""));
		map_par.put("zyh", Long.parseLong(body.get("zyh") + ""));
		map_par.put("jgid", (String) user.getManageUnitId());
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					map_par);
			if (list != null) {
				for (Map<String, Object> map : list) {
					if (Double.parseDouble(map.get("YPSL") + "") > 0) {
						ret.add(map);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Dispensing records query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "已发药记录查询失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-16
	 * @description 查询可退数量
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public double queryTurningBackNumber(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql_fysl = new StringBuffer();// 统计发药数量
		StringBuffer hql_tysl = new StringBuffer();// 统计退药数量(从退费的数量里面查..不知道为什么..)
		StringBuffer hql_ytjsl = new StringBuffer();// 统计已提交的退药数量
		UserRoleToken user = UserRoleToken.getCurrent();
		hql_fysl.append("select sum(YPSL) as YPSL from YF_ZYFYMX")
				.append(" where ZYH=:zyh and LYBQ=:lybq and YPSL>0 and FYLX!=0 and JGID=:jgid and YPXH=:ypxh and YPCD=:ypcd  and YZXH=:yzxh and YPDJ=:ypdj");
		hql_tysl.append("select sum(FYSL) as YPSL from ZY_FYMX")
				.append(" where JGID=:jgid and ZYH=:zyh and FYXH=:ypxh and YPCD=:ypcd and YZXH=:yzxh and FYSL<0");
		hql_ytjsl
				.append("select sum(YPSL) as YPSL from BQ_TYMX")
				.append(" where TJBZ = 1 and ZYH=:zyh and TYRQ is null and  YPXH=:ypxh and YPCD=:ypcd and YZID=:yzxh and JGID=:jgid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("lybq", Long.parseLong(body.get("lybq") + ""));
		map_par.put("zyh", Long.parseLong(body.get("zyh") + ""));
		map_par.put("ypxh", Long.parseLong(body.get("ypxh") + ""));
		map_par.put("ypcd", Long.parseLong(body.get("ypcd") + ""));
		map_par.put("jgid", (String) user.getManageUnitId());
		map_par.put("yzxh", Long.parseLong(body.get("yzxh") + ""));
		try {
			map_par.put("ypdj", Double.parseDouble(body.get("ypdj") + ""));
			Map<String, Object> map_fysl = dao.doLoad(hql_fysl.toString(),
					map_par);
			if (map_fysl == null || map_fysl.get("YPSL") == null) {
				return 0;
			}
			double fysl = Double.parseDouble(map_fysl.get("YPSL") + "");
			map_par.remove("lybq");
			map_par.remove("ypdj");
			Map<String, Object> map_tysl = dao.doLoad(hql_tysl.toString(),
					map_par);
			double tysl = 0;
			if (map_tysl != null && map_tysl.get("YPSL") != null) {
				tysl = Double.parseDouble(map_tysl.get("YPSL") + "");
			}
			Map<String, Object> map_tjsl = dao.doLoad(hql_ytjsl.toString(),
					map_par);
			double tjsl = 0;
			if (map_tjsl != null && map_tjsl.get("YPSL") != null) {
				tjsl = Double.parseDouble(map_tjsl.get("YPSL") + "");
			}
			return fysl + tysl + tjsl;
		} catch (PersistentDataOperationException e) {
			logger.error("Dispensing records query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "已发药记录查询失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-16
	 * @description 保存退药申请记录
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveBackApplication(List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> map_mx = body.get(0);
		StringBuffer hql_delete = new StringBuffer();
		hql_delete
				.append("delete from BQ_TYMX")
				.append(" where ZYH=:zyh and JGID=:jgid and (TJBZ = 0 Or TJBZ Is Null) and TYRQ Is Null");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", Long.parseLong(map_mx.get("ZYH") + ""));
		map_par.put("jgid", (String) user.getManageUnitId());
		try {
			dao.doUpdate(hql_delete.toString(), map_par);
			for (int i = 1; i < body.size(); i++) {
				Map<String, Object> map_tymx = body.get(i);
				Map<String, Object> map_tymx_insert = new HashMap<String, Object>();
				for (String key : map_tymx.keySet()) {
					if ("YFSB".equals(key) || "YPXH".equals(key)
							|| "TYBQ".equals(key) || "YPCD".equals(key)
							|| "YZID".equals(key) || "ZYH".equals(key)) {
						map_tymx_insert.put(key,
								Long.parseLong(map_tymx.get(key) + ""));
					} else {
						map_tymx_insert.put(key, map_tymx.get(key));
					}
				}
				map_tymx_insert.put("THBZ", 0);
				map_tymx_insert.put("SQRQ", new Date());
				map_tymx_insert.put("CZGH", user.getUserId() + "");
				map_tymx_insert.put("TYLX", 2);
				map_tymx_insert.put("YPSL",
						-Double.parseDouble(map_tymx.get("YPSL") + ""));
				dao.doSave("create", BSPHISEntryNames.BQ_TYMX_TYSQ,
						map_tymx_insert, false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Detailed recordkeeping failed drug withdrawal", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退药明细记录保存失败");
		} catch (ValidateException e) {
			logger.error(
					"The drug withdrawal detailed recordkeeping verify fails",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退药明细记录保存验证失败");
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-16
	 * @description 提交退药记录
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveCommitBackApplication(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		StringBuffer hql_commit = new StringBuffer();
		hql_commit
				.append("update BQ_TYMX")
				.append(" set TJBZ = 1 where ZYH=:zyh and JGID=:jgid and (TJBZ = 0 Or TJBZ Is Null) and TYRQ Is Null");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", Long.parseLong(body.get("zyh") + ""));
		map_par.put("jgid", (String) user.getManageUnitId());
		try {
			dao.doUpdate(hql_commit.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			logger.error("Detail records submission failed drug withdrawal", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退药明细记录提交失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-15
	 * @description 退药申请查询退药记录
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> querytyRecords(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		List<Object> cnd = (List<Object>) req.get("cnd");
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select a.TYRQ as TYRQ,b.YPMC as YPMC,a.YPGG as YPGG,d.YFDW as YFDW,a.YPSL as YPSL,a.YPJG as YPJG,c.CDMC as CDMC from ")// ,ZY_FYMX
																																		// e
																																		// where
																																		// (e.JSCS=1
																																		// or
																																		// e.JSCS=0
																																		// or
																																		// e.JSCS
																																		// is
																																		// null)
																																		// and
																																		// and
																																		// d.JFID=e.JLXH
				.append("BQ_TYMX a,YK_TYPK b,YK_CDDZ c,BQ_TJ02 d where    d.JLXH=a.JLXH and a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.JLXH=d.JLXH   ");
		String countHql = "select count(*) as NUM from BQ_TYMX a,YK_TYPK b,YK_CDDZ c,BQ_TJ02 d  where d.JLXH=a.JLXH and a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.JLXH=d.JLXH  ";
		try {
			if (cnd != null) {
				if (cnd.size() > 0) {
					ExpressionProcessor exp = new ExpressionProcessor();
					String where = " and " + exp.toString(cnd);
					hql.append(where);
					countHql = countHql + where;
				}
			}
			List<Map<String, Object>> coun = dao.doSqlQuery(
					countHql.toString(), null);
			Map<String, Object> parameters = new HashMap<String, Object>();
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(hql.toString(),
					parameters);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("Dispensing records query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "已发药记录查询失败");
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收款发票查询失败！");
		}
		return ret;
	}
}
