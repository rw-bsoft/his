package phis.application.war.source;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
//import org.hibernate.dialect.IngresDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.application.cfg.source.ConfigLogisticsInventoryControlModel;
import phis.application.ivc.source.TreatmentNumberModule;
import phis.application.mds.source.MedicineUtils;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class DoctorAdviceExecuteModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public DoctorAdviceExecuteModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 病人列表查询
	 * 
	 * @param req
	 * @param res
	 */
	@SuppressWarnings("unchecked")
	public void doPatientQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		String HSQL = (String) user.getProperty("wardId");// 护士权利。
		if (HSQL == null) {
			throw new RuntimeException("请先选择病区！");
		}
		String date = BSHISUtil.getDate();
		Date QRSJ = BSHISUtil.toDate(date);// 确认时间

		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int first = 0;
		// if (req.containsKey("pageNo")) {
		// first = (Integer) req.get("pageNo") - 1;
		// }
		long ZYH = 0;
		String zyhString = "";
		if (req.get("cnd") != null) {
			ZYH = Long.parseLong(req.get("cnd") + "");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();

		if (ZYH != 0) {
			parameters.put("ZYH", ZYH);
			zyhString += "  and (t.ZYH =:ZYH)  ";
		}

		String XYF = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.FHYZHJF, ctx);
		if ("1".equals(XYF)) {
			zyhString += " and t.FHBZ=1  ";
		}

		try {
			parameters.put("JGID", JGID);
			parameters.put("SRKS", HSQL);
			parameters.put("QRSJ", QRSJ);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t.ZYH as ZYH,t1.BRCH as BRCH,t1.ZYHM as ZYHM,t1.BRXM as BRXM,t1.BRXZ as BRXZ,t1.ZLXZ as ZLXZ FROM ");
			sql_list.append("ZY_BQYZ t, ");
			sql_list.append("ZY_BRRY t1 ");
			sql_list.append("WHERE t.ZYH = t1.ZYH and t.SRKS =:SRKS and (t.JFBZ = 2 OR t.JFBZ = 9) and (t.XMLX > 3) and t.LSBZ = 0 and t.YZPB = 0  and (t.SYBZ <> 1) and (t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and (t.JGID =:JGID) ");
			sql_list.append(zyhString);
			sql_list.append(" ORDER BY t1.BRCH ");

			// 返会列数的查询语句
			// StringBuffer Sql_count = new StringBuffer(
			// "SELECT COUNT(*) as NUM FROM ");
			// Sql_count.append("ZY_BQYZ t, ");
			// Sql_count.append("ZY_BRRY t1 ");
			// Sql_count
			// .append("WHERE t.ZYH = t1.ZYH and t1.CYPB = 0 and (t.YSBZ = 0 or (t.YSBZ = 1 and t.YSTJ = 1)) and ZFBZ = 0 and t.SRKS =:SRKS and (t.JFBZ = 2 OR t.JFBZ = 9) and (t.XMLX > 3) and t.LSBZ = 0 and t.YZPB = 0  and (t.SYBZ <> 1) and (t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and (t.JGID =:JGID) ");
			// Sql_count.append(zyhString);
			// Sql_count.append(" ORDER BY t1.BRCH ");

			// List<Map<String, Object>> coun = dao.doSqlQuery(
			// Sql_count.toString(), parameters);
			// int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			// parameters.put("first", first * pageSize);
			// parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			// if(total != inofList.size()){
			// total = inofList.size();
			// }
			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.war.schemas.ZY_BQYZ");
			// res.put("pageSize", pageSize);
			// res.put("pageNo", first);
			// res.put("totalCount", total);
			List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
			if (inofList != null) {
				for (int i = 0; i < inofList.size(); i++) {
					Map<String, Object> inof = inofList.get(i);
					inof.put("cnd", inof.get("ZYH"));
					doDetailChargeQuery(inof, res, ctx);
					if (res.get("body") != null) {
						if (((List<Map<String, Object>>) res.get("body"))
								.size() > 0) {
							reList.add(inof);
						}
					}
				}
			}
			res.put("body", reList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人列表查询失败！");
		}
	}

	/**
	 * 医嘱明细查询
	 * 
	 * @param req
	 * @param res
	 */
	public void doDoctorAdviceDetailQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		String HSQL = (String) user.getProperty("wardId");// 护士权利。
		if (HSQL == null) {
			throw new RuntimeException("请先选择病区！");
		}
		String date = BSHISUtil.getDate();
		Date QRSJ = BSHISUtil.toDate(date);// 确认时间
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		long ZYH = 0;
		String zyhString = "";
		if (req.get("cnd") != null) {
			ZYH = Long.parseLong(req.get("cnd") + "");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();

		if (ZYH != 0) {
			parameters.put("ZYH", ZYH);
			zyhString += "and (t.ZYH =:ZYH) ";
		}

		String XYF = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.FHYZHJF, ctx);
		if ("1".equals(XYF)) {
			zyhString += " and t.FHBZ=1  ";
		}

		try {
			parameters.put("JGID", JGID);
			parameters.put("SRKS", HSQL);
			parameters.put("QRSJ", QRSJ);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t.YSGH as YSGH,t.YZMC as YZMC,t.YPDJ as YPDJ,t.MRCS as MRCS,t.YCSL as YCSL,' ' AS je,'0' AS OK,");
			sql_list.append("t.SYBZ as SYBZ,t.YPCD as YPCD,t.SRKS as SRKS,to_char(t.QRSJ,'YYYY-MM-DD HH24:MI:SS')as QRSJ, t.YPXH as YPXH, t.YPLX as YPLX, t.ZYH as ZYH, t.JLXH as JLXH,");
			sql_list.append("to_char(t.KSSJ,'YYYY-MM-DD HH24:MI:SS')as KSSJ,t.YZZH as YZXH,to_char(t.TZSJ,'YYYY-MM-DD HH24:MI:SS')as TZSJ, t.MZCS MZCS,t.XMLX as XMLX, t.CZGH as CZGH, t.ZXKS as ZXKS, t.YEPB as YEPB,");
			sql_list.append("t.LSBZ as LSBZ,t.LSYZ as LSYZ, t.YZZH as YZZH, t.ZFPB as ZFPB, t1.BRCH as BRCH,t1.BRXZ as BRXZ,");
			sql_list.append("t1.GZDW as GZDW,t.BRKS as BRKS, t.SYPC as SYPC,t1.ZLXZ as ZLXZ,0 AS ts,0 AS FYCS, t1.DJID as DJID,");
			sql_list.append("t.YSTJ as YSTJ,t.SRCS as SRCS, t.YZZXSJ as YZZXSJ FROM ");
			sql_list.append("ZY_BQYZ t, ");
			sql_list.append("ZY_BRRY t1 ");
			sql_list.append("WHERE  t.ZYH = t1.ZYH and t1.CYPB = 0 and (t.JFBZ = 2 or t.JFBZ = 9) and t.XMLX > 3 and t.LSBZ = 0 and t.YZPB = 0  ");
			sql_list.append("and (t.SYBZ <> 1)  and (t.YSBZ = 0 or (t.YSBZ = 1 and t.YSTJ = 1)) and ZFBZ = 0 ");
			sql_list.append("and t.SRKS =:SRKS and(t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and t.JGID =:JGID  ");
			sql_list.append(zyhString);
			sql_list.append(" ORDER BY t.ZYH ASC ");
			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("ZY_BQYZ t, ");
			Sql_count.append("ZY_BRRY t1 ");
			Sql_count
					.append("WHERE  t.ZYH = t1.ZYH and t1.CYPB = 0 and (t.JFBZ = 2 or t.JFBZ = 9) and t.XMLX > 3 and t.LSBZ = 0 and t.YZPB = 0  ");
			Sql_count
					.append("and (t.SYBZ <> 1)  and (t.YSBZ = 0 or (t.YSBZ = 1 and t.YSTJ = 1)) and ZFBZ = 0 ");
			Sql_count
					.append("and t.SRKS =:SRKS and(t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and t.JGID =:JGID  ");
			Sql_count.append(zyhString);
			Sql_count.append(" ORDER BY t.ZYH ASC ");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.war.schemas.ZY_BQYZ_MX");
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医嘱明细查询失败！");
		}
	}
	@SuppressWarnings("unchecked")
	// 刷新
	public void doDetailChargeQuery(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		String HSQL = (String) user.getProperty("wardId");// 护士权利。
		if (HSQL == null) {
			throw new RuntimeException("请先选择病区！");
		}
		long ZYH = 0;
		if (body.get("cnd") != null) {
			ZYH = Long.parseLong(body.get("cnd") + "");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		String zyhString = "";
		if (ZYH != 0) {
			parameters.put("ZYH", ZYH);
			zyhString += " and (t.ZYH =:ZYH) ";
		}
		if(body.containsKey("ZYHS")){
			zyhString += " and (t.ZYH in(:zyhs)) ";
			List<Object> l=(List<Object>)body.get("ZYHS");
			List<Long> zyhs=new ArrayList<Long>();
			for(Object o:l){
				zyhs.add(MedicineUtils.parseLong(o));
			}
			parameters.put("zyhs",zyhs);
		}
		if(body.containsKey("XMXHS")){
			zyhString += " and (t.YPXH in(:xmxhs)) ";
			List<Object> l=(List<Object>)body.get("XMXHS");
			List<Long> xmxhs=new ArrayList<Long>();
			for(Object o:l){
				xmxhs.add(MedicineUtils.parseLong(o));
			}
			parameters.put("xmxhs",xmxhs);
		}
		if(body.containsKey("JLXHS")){//打印用
			zyhString += " and (t.JLXH in(:jlxhs)) ";
			List<Object> l=(List<Object>)body.get("JLXHS");
			List<Long> jlxhs=new ArrayList<Long>();
			for(Object o:l){
				jlxhs.add(MedicineUtils.parseLong(o));
			}
			parameters.put("jlxhs",jlxhs);
		}
		/**
		 * 2013-08-20 modify by gejj 添加判断ADD_FHBZ变量，如果为空或为1表示需要判断复核标志是否启用
		 */
		if (body.get("ADD_FHBZ") == null
				|| "1".equals(String.valueOf(body.get("ADD_FHBZ")))) {
			String XYF = ParameterUtil.getParameter(JGID,
					BSPHISSystemArgument.FHYZHJF, ctx);
			if ("1".equals(XYF)) {
				zyhString += " and t.FHBZ=1  ";
			}
		}

		String date = BSHISUtil.getDate();
		Date QRSJ = BSHISUtil.toDate(date);// 确认时间

		parameters.put("JGID", JGID);
		parameters.put("SRKS", HSQL);
		parameters.put("QRSJ", QRSJ);

		try {
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t.FHGH as FHGH,t.YSGH as YSGH,t.YZMC as YZMC,t.YPDJ as YPDJ,t.MRCS as MRCS,t.YCSL as YCSL,' ' AS je,'0' AS OK,");
			sql_list.append("t.SYBZ as SYBZ,t.YPCD as YPCD,t.SRKS as SRKS,to_char(t.QRSJ,'YYYY-MM-DD HH24:MI:SS')as QRSJ, t.YPXH as YPXH, t.YPLX as YPLX, t.ZYH as ZYH, t.JLXH as JLXH,");
			sql_list.append("to_char(t.KSSJ,'YYYY-MM-DD HH24:MI:SS')as KSSJ,t.YZZH as YZXH,to_char(t.TZSJ,'YYYY-MM-DD HH24:MI:SS')as TZSJ, t.MZCS MZCS,t.XMLX as XMLX, t.CZGH as CZGH, t.ZXKS as ZXKS, t.YEPB as YEPB,");
			sql_list.append("t.LSBZ as LSBZ,t.LSYZ as LSYZ, t.YZZH as YZZH, t.ZFPB as ZFPB, t1.BRCH as BRCH,t1.BRXM as BRXM,t1.BRXZ as BRXZ,");
			sql_list.append("t1.GZDW as GZDW,t.BRKS as BRKS, t.SYPC as SYPC,t1.ZLXZ as ZLXZ,0 AS ts,0 AS FYCS, t1.DJID as DJID,");
			sql_list.append("t.YSTJ as YSTJ,t.SRCS as SRCS, t.YZZXSJ as YZZXSJ,t1.ZYHM as ZYHM FROM ");
			sql_list.append("ZY_BQYZ t, ");
			sql_list.append("ZY_BRRY t1 ");
			sql_list.append("WHERE  t.ZYH = t1.ZYH and t1.CYPB = 0 and (t.JFBZ = 2 or t.JFBZ = 9) and t.XMLX > 3 and t.LSBZ = 0 and t.YZPB = 0  ");
			sql_list.append("and (t.SYBZ <> 1)  and (t.YSBZ = 0 or (t.YSBZ = 1 and t.YSTJ = 1)) and ZFBZ = 0 ");
			sql_list.append("and t.SRKS =:SRKS and(t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and t.JGID =:JGID  ");
			//zhaojian 2017-09-08 浦口系统：护士站点击执行时过滤掉检查单项目，检查项目必须在医技项目提交列表中确认后并切换到医技系统中执行
			if(ParameterUtil.getParameter(JGID,BSPHISSystemArgument.QYYJXMTJ, ctx).equals("1")){
				sql_list.append(" and ZFPB=1");
			}
			sql_list.append(zyhString);
			sql_list.append(" ORDER BY t.ZYH ASC ");

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			int FJBZ = 0; // 为了区分附加计价执行
			if (inofList.size() > 0) {
				List<Map<String, Object>> listGY_SYPC = BSPHISUtil
						.u_his_share_yzzxsj(parameters, dao, ctx);
				BSPHISUtil.uf_comp_yzzx(listGY_SYPC, inofList, dao, ctx);
				for (int i = 0; i < inofList.size(); i++) {
					double FYCS = Double.parseDouble(inofList.get(i)
							.get("FYCS") + "");
					double YCSL = Double.parseDouble(inofList.get(i)
							.get("YCSL") + "");
					double YPDJ = Double.parseDouble(inofList.get(i)
							.get("YPDJ") + "");

					int LSBZ = Integer.parseInt(inofList.get(i).get("LSBZ")
							+ "");
					String QRSJ1 = inofList.get(i).get("QRSJ") + "";
					if (FYCS == 0) {
						inofList.remove(i);
						i--;
					} else {
						inofList.get(i).put("FYCS", FYCS);
						inofList.get(i).put("LSBZ", LSBZ);
						inofList.get(i).put("QRSJ", QRSJ1);
						inofList.get(i).put("JE", (FYCS * YCSL * YPDJ));
						inofList.get(0).put("FJBZ", FJBZ);
					}
				}
			}
			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.war.schemas.ZY_BQYZ_MX");
			res.put("body", inofList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "刷新失败！");
		}
	}
	@SuppressWarnings("unchecked")
	public void doQueryFHSFXM(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		String HSQL = (String) user.getProperty("wardId");// 护士权利。
		if (HSQL == null) {
			throw new RuntimeException("请先选择病区！");
		}
		long ZYH = 0;
		if (body.get("zyh") != null) {
			ZYH = Long.parseLong(body.get("zyh") + "");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		String zyhString = "";
		if (ZYH != 0) {
			parameters.put("ZYH", ZYH);
			zyhString += " and (t.ZYH =:ZYH) ";
		}
		if(body.containsKey("zyhs")){
			zyhString += " and (t.ZYH in(:zyhs)) ";
			List<Object> l=(List<Object>)body.get("zyhs");
			List<Long> zyhs=new ArrayList<Long>();
			for(Object o:l){
				zyhs.add(MedicineUtils.parseLong(o));
			}
			parameters.put("zyhs",zyhs);
		}
		if(body.containsKey("xmxhs")){
			zyhString += " and (t.YPXH in(:xmxhs)) ";
			List<Object> l=(List<Object>)body.get("xmxhs");
			List<Long> xmxhs=new ArrayList<Long>();
			for(Object o:l){
				xmxhs.add(MedicineUtils.parseLong(o));
			}
			parameters.put("xmxhs",xmxhs);
		}
		try {
			String XYF = ParameterUtil.getParameter(JGID,
					BSPHISSystemArgument.FHYZHJF, ctx);
			res.put("body", XYF);
			if ("1".equals(XYF)) {
				zyhString += " and t.FHBZ=0  ";
				String date = BSHISUtil.getDate();
				Date QRSJ = BSHISUtil.toDate(date);// 确认时间

				parameters.put("JGID", JGID);
				parameters.put("SRKS", HSQL);
				parameters.put("QRSJ", QRSJ);

				// 返回list的查询语句
				StringBuffer sql_count = new StringBuffer(
						"SELECT count(*) as COUNT FROM ");
				sql_count.append("ZY_BQYZ t, ");
				sql_count.append("ZY_BRRY t1 ");
				sql_count
						.append("WHERE  t.ZYH = t1.ZYH and t1.CYPB = 0 and (t.JFBZ = 2 or t.JFBZ = 9) and t.XMLX > 3 and t.LSBZ = 0 and t.YZPB = 0  ");
				sql_count
						.append("and (t.SYBZ <> 1)  and (t.YSBZ = 0 or (t.YSBZ = 1 and t.YSTJ = 1)) and ZFBZ = 0 ");
				sql_count
						.append("and t.SRKS =:SRKS and(t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and t.JGID =:JGID  ");
				sql_count.append(zyhString);
				sql_count.append(" ORDER BY t.ZYH ASC ");

				List<Map<String, Object>> countList = dao.doSqlQuery(
						sql_count.toString(), parameters);
				int count = Integer
						.parseInt(countList.get(0).get("COUNT") + "");
				res.put("count", count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "刷新失败！");
		}
	}

	/**
	 * 附加项目---查询列表
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doAdditionProjectsQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		String HSQL = (String) user.getProperty("wardId");// 护士权利。
		if (HSQL == null) {
			throw new RuntimeException("请先选择病区！");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("JGID", JGID);
			parameters.put("SRKS", HSQL);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t1.BRCH as BRCH,t1.BRXM as BRXM,t1.BRXZ as BRXZ,t.BRKS as BRKS,t.YJXH as YJXH,t.YZMC as YZMC,t.YEPB as YEPB,t.MZCS as MZCS,to_char(t.KSSJ,'YYYY-MM-DD HH24:MI:SS')as KSSJ,");
			sql_list.append("to_char(t.TZSJ,'YYYY-MM-DD HH24:MI:SS') as TZSJ,t.YPYF as YPYF,t.MRCS as MRCS,t.YCSL as YCSL,t.YPDJ as YPDJ,t.SYBZ as SYBZ,t.SRKS as SRKS,to_char(t.QRSJ,'YYYY-MM-DD HH24:MI:SS') as QRSJ,");
			sql_list.append("t.YPXH as YPXH,t.YPLX as YPLX,t.JLXH as JLXH,t.SYPC as SYPC,t.ZYH as ZYH,t.BZXX as BZXX,t.XMLX as XMLX,t.YZZH as YZZH,t.LSBZ as LSBZ,");
			sql_list.append("t.CZGH as CZGH,t.ZXKS as ZXKS, t.YSGH as YSGH,t.LSYZ as LSYZ,");
			sql_list.append("t.ZFPB as ZFPB,t.YZPB as YZPB,t.FYSX as FYSX,t1.ZLXZ as ZLXZ,0 as FYTS,0 as FYCS, t1.DJID as DJID,t.SRCS as SRCS,t.YZZXSJ as YZZXSJ  FROM ");
			sql_list.append("ZY_BQYZ t, ");
			sql_list.append("ZY_BRRY t1 ");
			sql_list.append("WHERE(t.ZYH = t1.ZYH AND t1.CYPB = 0)AND(t.SRKS =:SRKS)AND(t.LSBZ = 0)AND((t.YZPB > 0) AND (t.JFBZ = 2)) ");
			sql_list.append("AND(t.YSBZ = 0 OR (t.YSBZ = 1 AND t.YSTJ = 1))AND(t.JGID =:JGID)AND (t.JGID = t1.JGID) ");
			if(req.containsKey("ZYHS")){
				sql_list.append(" and t.ZYH in(:zyhs)");
				List<Object> l=(List<Object>)req.get("ZYHS");
				List<Long> zyhs=new ArrayList<Long>();
				for(Object o:l){
					zyhs.add(MedicineUtils.parseLong(o));
				}
				parameters.put("zyhs",zyhs);
			}
			if(req.containsKey("XMXHS")){
				sql_list.append(" and t.YPXH in(:xmxhs)");
				List<Object> l=(List<Object>)req.get("XMXHS");
				List<Long> xmxhs=new ArrayList<Long>();
				for(Object o:l){
					xmxhs.add(MedicineUtils.parseLong(o));
				}
				parameters.put("xmxhs",xmxhs);
			}
			sql_list.append("ORDER BY t1.BRCH ASC, t.YZZH ASC ");
			// 返会列数的查询语句
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			if(parameters.containsKey("zyhs")){
				parameters.remove("parameters");
			}
			// 如果查出来有数据 就检索数据。
			int FJBZ = 1; // 再次修改
			if (inofList.size() > 0) {
				inofList.get(0).put("FJBZ", FJBZ);// 附加项目标志
				List<Map<String, Object>> listGY_SYPC = BSPHISUtil
						.u_his_share_yzzxsj(parameters, dao, ctx);
				BSPHISUtil.uf_comp_yzzx(listGY_SYPC, inofList, dao, ctx);
				//long ZYH = 0;
				for (int i = 0; i < inofList.size(); i++) {
					double FYCS = Double.parseDouble(inofList.get(i)
							.get("FYCS") + "");
					double YCSL = Double.parseDouble(inofList.get(i)
							.get("YCSL") + "");
					double YPDJ = Double.parseDouble(inofList.get(i)
							.get("YPDJ") + "");
					if (FYCS <= 0) {
						inofList.remove(i);
						i--;
					} else {
						inofList.get(i).put("FYCS", FYCS);
						inofList.get(i).put("JE", (FYCS * YCSL * YPDJ));
//						if (Integer.parseInt(inofList.get(i).get("ZYH") + "") == ZYH) {
//							inofList.get(i).put("BRCH", "");
//							inofList.get(i).put("BRXM", "");
//						} else {
//							ZYH = Integer.parseInt(inofList.get(i).get("ZYH")
//									+ "");
//						}
					}
				}
			}

			List<Map<String, Object>> inofList_fy = new ArrayList<Map<String, Object>>();
			int total_pre = inofList.size();
			if(req.containsKey("pageSize")||req.containsKey("pageNo")){
			if (first == 0) {
				for (int i = 0; i < (pageSize==0?total_pre:pageSize); i++) {
					if (inofList.size() > i) {
						inofList_fy.add(inofList.get(i));
					}
				}
			} else {
				for (int i = first * pageSize; i < (first + 1) * pageSize; i++) {
					if (inofList.size() > i) {
						inofList_fy.add(inofList.get(i));
					}
				}
			}
			}else{
				inofList_fy.addAll(inofList);
			}
			SchemaUtil.setDictionaryMassageForList(inofList_fy,
					"phis.application.war.schemas.ZY_BQYZ_FJXM");
			if(req.containsKey("pageSize")||req.containsKey("pageNo")){
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total_pre);
			}
			res.put("body", inofList_fy);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医嘱明细查询失败！");
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-16
	 * @description 附加项目执行模块 左边病人数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> additionProjectsBrQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		doAdditionProjectsQuery(req,res,ctx);
		List<Map<String,Object>> l=(List<Map<String,Object>>)res.get("body");
		List<Map<String,Object>> ret=new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map:l){
			int i=0;
			for(Map<String,Object> m:ret){
				if(MedicineUtils.parseLong(map.get("ZYH"))==MedicineUtils.parseLong(m.get("ZYH"))){
					i=1;
					break;
				}
			}
			if(i==0){
				Map<String,Object> map_temp=new HashMap<String,Object>();
				map_temp.put("ZYH", MedicineUtils.parseLong(map.get("ZYH")));
				map_temp.put("BRCH", MedicineUtils.parseString(map.get("BRCH")));
				map_temp.put("BRXM", MedicineUtils.parseString(map.get("BRXM")));
				ret.add(map_temp);
			}
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-24
	 * @description 附加项目执行模块 左边项目数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public Set<Map<String,Object>> additionProjectsXmQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		doAdditionProjectsQuery(req,res,ctx);
		List<Map<String,Object>> l=(List<Map<String,Object>>)res.get("body");
		Set<Map<String,Object>> ret=new HashSet<Map<String,Object>>();
		for(Map<String,Object> map:l){
			Map<String,Object> map_temp=new HashMap<String,Object>();
			map_temp.put("YPXH", MedicineUtils.parseLong(map.get("YPXH")));
			map_temp.put("YZMC", MedicineUtils.parseString(map.get("YZMC")));
			ret.add(map_temp);
		}
		return ret;
	}
	
	public void doSaveConfirmAdditional(List<Map<String, Object>> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		String HSQL = (String) user.getProperty("wardId");// 护士权利。
		String YGGH = (String) user.getUserId();// 当前操作员工号
		if (HSQL == null) {
			throw new RuntimeException("请先选择病区！");
		}

		String zyhString = "";
		/*
		 * String XYF =
		 * ParameterUtil.getParameter(JGID,BSPHISSystemArgument.FHYZHJF, ctx);
		 * if ("1".equals(XYF)) { zyhString += " and t.FHBZ=1  " ; }
		 */

		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			parameters.put("SRKS", HSQL);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t1.BRCH as BRCH,t1.BRXM as BRXM,t1.BRXZ as BRXZ,t.BRKS as BRKS,t.YZMC as YZMC,t.YEPB as YEPB,t.MZCS as MZCS,to_char(t.KSSJ,'YYYY-MM-DD HH24:MI:SS')as KSSJ,");
			sql_list.append("to_char(t.TZSJ,'YYYY-MM-DD HH24:MI:SS') as TZSJ,t.YPYF as YPYF,t.MRCS as MRCS,t.YCSL as YCSL,t.YPDJ as YPDJ,t.SYBZ as SYBZ,t.SRKS as SRKS,to_char(t.QRSJ,'YYYY-MM-DD HH24:MI:SS') as QRSJ,");
			sql_list.append("t.YPXH as YPXH,t.YPLX as YPLX,t.JLXH as JLXH,t.SYPC as SYPC,t.ZYH as ZYH,t.BZXX as BZXX,t.XMLX as XMLX,t.YZZH as YZZH,t.LSBZ as LSBZ,");
			sql_list.append("t.CZGH as CZGH,t.ZXKS as ZXKS, t.YSGH as YSGH,t.LSYZ as LSYZ,");
			sql_list.append("t.ZFPB as ZFPB,t.YZPB as YZPB,t.FYSX as FYSX,t1.ZLXZ as ZLXZ,0 as FYTS,0 as FYCS, t1.DJID as DJID,t.SRCS as SRCS,t.YZZXSJ as YZZXSJ  FROM ");
			sql_list.append("ZY_BQYZ t, ");
			sql_list.append("ZY_BRRY t1 ");
			sql_list.append("WHERE(t.ZYH = t1.ZYH AND t1.CYPB = 0)AND(t.SRKS =:SRKS)AND(t.LSBZ = 0)AND((t.YZPB > 0) AND (t.JFBZ = 2)) ");
			sql_list.append("AND(t.YSBZ = 0 OR (t.YSBZ = 1 AND t.YSTJ = 1))AND(t.JGID =:JGID)AND (t.JGID = t1.JGID) ");
			sql_list.append(zyhString);
			sql_list.append("ORDER BY t1.BRCH ASC, t.YZZH ASC ");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			// 如果查出来有数据 就检索数据。
			List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();
			int FJBZ = 1; // 再次修改
			if (inofList.size() > 0) {
				inofList.get(0).put("FJBZ", FJBZ);// 附加项目标志
				List<Map<String, Object>> listGY_SYPC = BSPHISUtil
						.u_his_share_yzzxsj(parameters, dao, ctx);
				BSPHISUtil.uf_comp_yzzx(listGY_SYPC, inofList, dao, ctx);
				List<Map<String,Object>> list_temp=new ArrayList<Map<String,Object>>();
				for (int i = 0; i < inofList.size(); i++) {
					double FYCS = Double.parseDouble(inofList.get(i)
							.get("FYCS") + "");
					double YPDJ = Double.parseDouble(inofList.get(i)
							.get("YPDJ") + "");
					if (FYCS <= 0) {
						//inofList.remove(i);
						//i--;
					} else {
						inofList.get(i).put("FYCS", FYCS);
						double QRSL = 0;
						boolean x=false;
						for (int j = 0; j < req.size(); j++) {
							long JLXH = Long.parseLong(req.get(j).get("JLXH")
									+ "");
							long JLXH2 = Long.parseLong(inofList.get(i).get(
									"JLXH")
									+ "");
							if (JLXH == JLXH2) {
								QRSL = Double.parseDouble(req.get(j)
										.get("QRSL") + "");
								x=true;
								break;
							}
							
						}
						if(!x){
							continue;
						}
						inofList.get(i).put("JE", (FYCS * QRSL * YPDJ));

						Map<String, Object> zyfymx_map = (Map<String, Object>) inofList
								.get(i);// 住院费用明细表的用于插入的Map
						zyfymx_map.put("YZXH", Long.parseLong(inofList.get(i)
								.get("JLXH") + ""));
						zyfymx_map
								.put("ZYH",
										Long.parseLong(inofList.get(i).get(
												"ZYH")
												+ ""));
						zyfymx_map.put("FYXH", Long.parseLong(inofList.get(i)
								.get("YPXH") + ""));
						zyfymx_map.put(
								"YPLX",
								Integer.parseInt(inofList.get(i).get("YPLX")
										+ ""));
						zyfymx_map
								.put("FYMC", inofList.get(i).get("YZMC") + "");
						zyfymx_map.put("FYRQ", BSHISUtil.getDateTime());
						zyfymx_map.put("YPCD", 0);
						zyfymx_map.put("DZBL", 0);
						zyfymx_map.put("JSCS", 0);
						zyfymx_map.put("ZJJE", 0);
						zyfymx_map.put("ZFJE", 0);
						zyfymx_map.put("ZLJE", 0);
						zyfymx_map.put("ZFBL", 0);
						zyfymx_map.put("JGID", JGID);
						zyfymx_map.put("QRGH", YGGH);
						zyfymx_map.put("XMLX", 1);
						zyfymx_map.put("FYXM", 0);
						zyfymx_map.put("FYBQ", HSQL);
						zyfymx_map.put("JFRQ", BSHISUtil.getDateTime());
						zyfymx_map
								.put("YSGH", inofList.get(i).get("YSGH") + "");
						zyfymx_map.put("FYSL", QRSL);
						zyfymx_map.put(
								"FYDJ",
								Double.parseDouble(inofList.get(i).get("YPDJ")
										+ ""));
						zyfymx_map.put("FYKS", Long.parseLong(inofList.get(i)
								.get("BRKS") + ""));
						zyfymx_map.put(
								"BRXZ",
								Integer.parseInt(inofList.get(i).get("BRXZ")
										+ ""));
						// 执行科室为空时 默认为费用科室
						long ZXKS = 0;
						if (inofList.get(i).get("ZXKS") == null
								|| inofList.get(i).get("ZXKS") == "") {
							ZXKS = Long.parseLong(inofList.get(i).get("BRKS")
									+ "");
						} else {
							ZXKS = Long.parseLong(inofList.get(i).get("ZXKS")
									+ "");
						}
						zyfymx_map.put("ZXKS", ZXKS);

						if (inofList.get(i).get("CZGH") != null) {
							zyfymx_map.put(
									"SRGH",
									Long.parseLong(inofList.get(i).get("CZGH")
											+ ""));
						}
						if (inofList.get(i).get("ZLXZ") != null) {
							zyfymx_map.put(
									"ZLXZ",
									Integer.parseInt(inofList.get(i)
											.get("ZLXZ") + ""));
						}
						if (inofList.get(i).get("YEPB") != null) {
							zyfymx_map.put(
									"YEPB",
									Integer.parseInt(inofList.get(i)
											.get("YEPB") + ""));
						}
						/**
						 * 2013-08-23 modify by gejj
						 * 将FYCS给去掉，因为确认数量为可修改值，显示中的默认值为数量* 次数
						 **/
						// zyfymx_map.put("JE", (FYCS*QRSL*YPDJ));
						zyfymx_map.put("JE", (QRSL * YPDJ));
						body.add(zyfymx_map);
						list_temp.add(inofList.get(i));
					}
				}
				// 判断欠费病人
				BSPHISUtil.ArrearsPatientsQuery(body, ctx, dao, res);

				List<Map<String, Object>> listForputFYMX = new ArrayList<Map<String, Object>>();
				BSPHISUtil.uf_insert_fymx(body, listForputFYMX, dao, ctx);
				List<Map<String, Object>> list_xhmx = new ArrayList<Map<String, Object>>();// 物资消耗明细
				StringBuffer hql_xhmx = new StringBuffer();// 查询费用与物资对照信息
				hql_xhmx.append("select WZXH as WZXH,WZSL as WZSL from GY_FYWZ")
						.append(" where FYXH=:fyxh and JGID=:jgid");
				StringBuffer hql_brxx = new StringBuffer();// 病人信息查询
				hql_brxx.append(
						"select ZYHM as ZYHM,BRXM as BRXM,BRID as BRID from ZY_BRRY")
						.append(" where ZYH=:zyh");
				ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(
						dao);
				int wpjfbz = mmd.queryWPJFBZ(ctx);
				int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil
						.getParameter(JGID, BSPHISSystemArgument.WZSFXMJGZY,
								ctx)) ? "0" : ParameterUtil.getParameter(JGID,
						BSPHISSystemArgument.WZSFXMJGZY, ctx));
				long brid = 0;
				long ksdm = 0;
				String ksmc = "";
				for (int i = 0; i < listForputFYMX.size(); i++) {
					Map<String, Object> parametersForputFYMX = listForputFYMX
							.get(i);
					Map<String, Object> parameters_update = new HashMap<String, Object>();
					if (wpjfbz == 1 && wzsfxmjg == 0) {
						if (Integer.parseInt(parametersForputFYMX.get("YPLX")
								+ "") == 0) {// 项目才统计
							Map<String, Object> map_par = new HashMap<String, Object>();
							map_par.put(
									"fyxh",
									Long.parseLong(parametersForputFYMX
											.get("FYXH") + ""));
							map_par.put("jgid", JGID);
							List<Map<String, Object>> list_fywz = dao.doQuery(
									hql_xhmx.toString(), map_par);
							if (list_fywz != null && list_fywz.size() > 0) {
								map_par.clear();
								map_par.put("zyh",
										parametersForputFYMX.get("ZYH"));
								Map<String, Object> map_brxx = dao.doLoad(
										hql_brxx.toString(), map_par);
								for (Map<String, Object> map_fywz : list_fywz) {
									Map<String, Object> m = new HashMap<String, Object>();
									brid = mmd.parseLong(map_brxx.get("BRID"));
									ksdm = mmd.parseLong(HSQL);
									ksmc = (String) user
											.getProperty("wardName");
									m.put("JGID", JGID);
									m.put("KSDM", mmd.parseLong(HSQL));
									m.put("KSMC", user.getProperty("wardName"));
									m.put("BRID", map_brxx.get("BRID"));
									m.put("BRHM", map_brxx.get("ZYHM"));
									m.put("BRLY", "ZY");
									m.put("BRXM", map_brxx.get("BRXM"));
									m.put("WZXH", map_fywz.get("WZXH"));
									m.put("ZJJE",
											parametersForputFYMX.get("ZJJE"));
									m.put("WZSL",
											mmd.formatDouble(
													2,
													mmd.parseDouble(map_fywz
															.get("WZSL"))
															* mmd.parseDouble(parametersForputFYMX
																	.get("FYSL"))));
									list_xhmx.add(m);
								}
							}
						}
					}
					for (int j = 0; j < list_temp.size(); j++) {
						long JLXH = Long.parseLong(list_temp.get(j).get("JLXH")
								+ "");
						long JLXH_l = Long.parseLong(listForputFYMX.get(i).get(
								"YZXH")
								+ "");
						if (JLXH == JLXH_l) {
							String QRSJ = list_temp.get(j).get("QRSJ") + "";
							parameters_update.put("JLXH", JLXH);
							parameters_update.put("QRSJ",
									BSHISUtil.toDate(QRSJ));
							dao.doUpdate(
									"update ZY_BQYZ set QRSJ=:QRSJ where JLXH =:JLXH",
									parameters_update);
							if (Double.parseDouble(parametersForputFYMX
									.get("FYSL") + "") != 0) {
								parametersForputFYMX.put("SRGH", YGGH);
								dao.doSave("create", BSPHISEntryNames.ZY_FYMX,
										parametersForputFYMX, false);
							}
						}
					}
				}
				if (wpjfbz == 1) {
					String d = mmd.saveXhmx(list_xhmx, ctx);
					Map<String, Object> map_ret = new HashMap<String, Object>();
					map_ret.put("BRID", brid);
					map_ret.put("XHRQ", d);
					map_ret.put("KSMC", ksmc);
					map_ret.put("KSDM", ksdm);
					res.put("body", map_ret);
				}
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("确认提交医嘱失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("确认提交医嘱失败!", e);
		}
	}

	// 病区项目执行。
	public void doSaveConfirm(List<Map<String, Object>> body,
			List<Map<String, Object>> bodyForZX, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, ServiceException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();// 用户的机构ID
			String HSQL = (String) user.getProperty("wardId");// 护士权利。
			if (HSQL == null) {
				throw new RuntimeException("请先选择病区！");
			}
//			long ZYH = 0;
//			if (body != null && body.size() > 0
//					&& body.get(0).get("ZYH") != null) {
//				ZYH = Long.parseLong(body.get(0).get("ZYH") + "");
//			}

			Map<String, Object> parameters = new HashMap<String, Object>();
			String zyhString = "";
//			if (ZYH != 0) {
//				parameters.put("ZYH", ZYH);
//				zyhString = "and (t.ZYH =:ZYH) ";
//			}
			/***********add by LIZHI 2018-03-09执行选择的所有病人项目**********/
			if (body != null && body.size() > 0){
				zyhString = " and (t.ZYH in(:zyhs)) ";
				List<Long> zyhs=new ArrayList<Long>();
				for(Map<String,Object> data:body){
					if(data.containsKey("ZYH")){
						zyhs.add(MedicineUtils.parseLong(data.get("ZYH")));
					}
				}
				parameters.put("zyhs",zyhs);
			}
			/***********add by LIZHI 2018-03-09执行选择的所有病人项目**********/

			String XYF = ParameterUtil.getParameter(JGID,
					BSPHISSystemArgument.FHYZHJF, ctx);
			if ("1".equals(XYF)) {
				zyhString += " and t.FHBZ=1  ";
			}

			String date = BSHISUtil.getDate();
			Date QRSJ_new = BSHISUtil.toDate(date);// 确认时间

			parameters.put("JGID", JGID);
			parameters.put("SRKS", HSQL);
			parameters.put("QRSJ", QRSJ_new);

			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t.YSGH as YSGH,t.YZMC as YZMC,t.YPDJ as YPDJ,t.MRCS as MRCS,t.YCSL as YCSL,' ' AS je,'0' AS OK,");
			sql_list.append("t.SYBZ as SYBZ,t.YPCD as YPCD,t.SRKS as SRKS,to_char(t.QRSJ,'YYYY-MM-DD HH24:MI:SS')as QRSJ, t.YPXH as YPXH, t.YPLX as YPLX, t.ZYH as ZYH, t.JLXH as JLXH,");
			sql_list.append("to_char(t.KSSJ,'YYYY-MM-DD HH24:MI:SS')as KSSJ,t.YZZH as YZXH,to_char(t.TZSJ,'YYYY-MM-DD HH24:MI:SS')as TZSJ, t.MZCS MZCS,t.XMLX as XMLX, t.CZGH as CZGH, t.ZXKS as ZXKS, t.YEPB as YEPB,");
			sql_list.append("t.LSBZ as LSBZ,t.LSYZ as LSYZ, t.YZZH as YZZH, t.ZFPB as ZFPB, t1.BRCH as BRCH,t1.BRXZ as BRXZ,");
			sql_list.append("t1.GZDW as GZDW,t.BRKS as BRKS, t.SYPC as SYPC,t1.ZLXZ as ZLXZ,0 AS ts,0 AS FYCS, t1.DJID as DJID,");
			sql_list.append("t.YSTJ as YSTJ,t.SRCS as SRCS, t.YZZXSJ as YZZXSJ FROM ");
			sql_list.append("ZY_BQYZ t, ");
			sql_list.append("ZY_BRRY t1 ");
			sql_list.append("WHERE  t.ZYH = t1.ZYH and t1.CYPB = 0 and (t.JFBZ = 2 or t.JFBZ = 9) and t.XMLX > 3 and t.LSBZ = 0 and t.YZPB = 0  ");
			sql_list.append("and (t.SYBZ <> 1)  and (t.YSBZ = 0 or (t.YSBZ = 1 and t.YSTJ = 1)) and ZFBZ = 0 ");
			sql_list.append("and t.SRKS =:SRKS and(t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and t.JGID =:JGID  ");
			sql_list.append(zyhString);
			sql_list.append(" ORDER BY t.ZYH ASC ");

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int FJBZ = 0; // 为了区分附加计价执行

			if (inofList.size() > 0) {
				List<Map<String, Object>> listGY_SYPC = BSPHISUtil
						.u_his_share_yzzxsj(null, dao, ctx);
				BSPHISUtil.uf_comp_yzzx(listGY_SYPC, inofList, dao, ctx);
				for (int i = 0; i < inofList.size(); i++) {
					double FYCS = Double.parseDouble(inofList.get(i)
							.get("FYCS") + "");
					double YCSL = Double.parseDouble(inofList.get(i)
							.get("YCSL") + "");
					double YPDJ = Double.parseDouble(inofList.get(i)
							.get("YPDJ") + "");

					int LSBZ = Integer.parseInt(inofList.get(i).get("LSBZ")
							+ "");
					String QRSJ1 = inofList.get(i).get("QRSJ") + "";
					if (FYCS == 0) {
						inofList.remove(i);
						i--;
					} else {
						inofList.get(i).put("FYCS", FYCS);
						inofList.get(i).put("LSBZ", LSBZ);
						inofList.get(i).put("QRSJ", QRSJ1);
						inofList.get(i).put("JE", (FYCS * YCSL * YPDJ));
						inofList.get(0).put("FJBZ", FJBZ);
					}
				}
			}
			/** 2013-08-13 modify by gejj 直接将主项和附加项合并为一个list中然后进行判断是否超过余额 **/
			// 判断欠费病人
			List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
			tmpList.addAll(body);
			tmpList.addAll(bodyForZX);
			boolean flag = BSPHISUtil.ArrearsPatientsQuery(tmpList, ctx, dao,
					res);// 2013-08--13 修改使用body判断,infoList中为全部医技项目
			if (flag) {// 重新过滤将不需要执行的病人项目去除！(根据住院号判断)
				boolean f = true;
				Map<String, Object> tmpMap = null;
				for (int i = 0; i < body.size(); i++) {
					tmpMap = body.get(i);
					f = true;
					for (Map<String, Object> map : tmpList) {
						if (tmpMap.get("ZYH").equals(map.get("ZYH"))) {
							f = false;
							continue;
						}
					}
					if (f) {
						body.remove(i);
						i--;
					}
				}
				for (int i = 0; i < bodyForZX.size(); i++) {
					tmpMap = bodyForZX.get(i);
					f = true;
					for (Map<String, Object> map : tmpList) {
						if (tmpMap.get("ZYH").equals(map.get("ZYH"))) {
							f = false;
							continue;
						}
					}
					if (f) {
						bodyForZX.remove(i);
						i--;
					}
				}
				// 主项和附加项都没有时直接返回
				if ((body == null || body.size() == 0)
						&& (bodyForZX == null || bodyForZX.size() == 0)) {
					return;
				}
			}
			// BSPHISUtil.ArrearsPatientsQuery(inofList, ctx, dao,res);
			// // 如果主医嘱不执行 附加项目也不能执行。
			// String zyhresString = res.get("zyhString")+"";
			// for (int i = 0; i < bodyForZX.size(); i++) {
			// String ZYHForZX = bodyForZX.get(i).get("ZYH")+"";
			// if (zyhresString.contains(ZYHForZX)) {
			// bodyForZX.remove(i);
			// }
			// }
			// BSPHISUtil.ArrearsPatientsQuery(bodyForZX, ctx, dao,res);
			/** 2013-08-13 end **/
			List<Map<String, Object>> listForputFYMX = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> listForputFYMXforZX = new ArrayList<Map<String, Object>>();

			BSPHISUtil.uf_insert_fymx(body, listForputFYMX, dao, ctx);
			// 项目的附加计价
			BSPHISUtil.uf_insert_fymx(bodyForZX, listForputFYMXforZX, dao, ctx);

			List<Map<String, Object>> list_xhmx = new ArrayList<Map<String, Object>>();// 物资消耗明细
			StringBuffer hql_xhmx = new StringBuffer();// 查询费用与物资对照信息
			hql_xhmx.append("select WZXH as WZXH,WZSL as WZSL from GY_FYWZ")
					.append(" where FYXH=:fyxh and JGID=:jgid");
			StringBuffer hql_brxx = new StringBuffer();// 病人信息查询
			hql_brxx.append("select ZYHM as ZYHM,BRXM  as BRXM,BRID as BRID from ZY_BRRY where ZYH=:zyh");
			ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(
					dao);
			int wpjfbz = mmd.queryWPJFBZ(ctx);
			int wzsfxmjgzy = Integer
					.parseInt("".equals(ParameterUtil.getParameter(JGID,
							BSPHISSystemArgument.WZSFXMJGZY, ctx)) ? "0"
							: ParameterUtil.getParameter(JGID,
									BSPHISSystemArgument.WZSFXMJGZY, ctx));
			long brid = 0;
			long ksdm = 0;
			String ksmc = "";
			boolean tag=false;//用于判断是否有项目被执行(先打开执行界面,然后提交项目,不刷新执行界面直接提交)
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> parametersForputFYMX = listForputFYMX
						.get(i);
				Map<String, Object> parameters_update = new HashMap<String, Object>();
				// 物资消耗明细数据
				if (wpjfbz == 1 && wzsfxmjgzy == 0) {
					if (Integer.parseInt(parametersForputFYMX.get("YPLX") + "") == 0) {// 项目才统计
						Map<String, Object> map_par = new HashMap<String, Object>();
						map_par.put(
								"fyxh",
								Long.parseLong(parametersForputFYMX.get("FYXH")
										+ ""));
						map_par.put("jgid", JGID);
						List<Map<String, Object>> list_fywz = dao.doQuery(
								hql_xhmx.toString(), map_par);
						if (list_fywz != null && list_fywz.size() > 0) {
							map_par.clear();
							map_par.put("zyh", parametersForputFYMX.get("ZYH"));
							Map<String, Object> map_brxx = dao.doLoad(
									hql_brxx.toString(), map_par);
							for (Map<String, Object> map_fywz : list_fywz) {
								Map<String, Object> m = new HashMap<String, Object>();
								brid = mmd.parseLong(map_brxx.get("BRID"));
								ksdm = mmd.parseLong(HSQL);
								ksmc = (String) user.getProperty("wardName");
								m.put("JGID", JGID);
								m.put("KSDM", mmd.parseLong(HSQL));
								m.put("KSMC", user.getProperty("wardName"));
								m.put("BRID", map_brxx.get("BRID"));
								m.put("BRHM", map_brxx.get("ZYHM"));
								m.put("BRLY", "ZY");
								m.put("BRXM", map_brxx.get("BRXM"));
								m.put("WZXH", map_fywz.get("WZXH"));
								m.put("ZJJE", parametersForputFYMX.get("ZJJE"));
								m.put("WZSL",
										mmd.formatDouble(
												2,
												mmd.parseDouble(map_fywz
														.get("WZSL"))
														* mmd.parseDouble(parametersForputFYMX
																.get("FYSL"))));
								list_xhmx.add(m);
							}
						}
					}
				}
				for (int j = 0; j < inofList.size(); j++) {
					long JLXH = Long
							.parseLong(inofList.get(j).get("JLXH") + "");
					long JLXH_l = Long.parseLong(body.get(i).get("JLXH") + "");
					String QRSJ = inofList.get(j).get("QRSJ") + "";
					int LSBZ = Integer.parseInt(inofList.get(j).get("LSBZ")
							+ "");
					parameters_update.put("JLXH", JLXH);
					parameters_update.put("QRSJ", BSHISUtil.toDate(QRSJ));
					parameters_update.put("LSBZ", LSBZ);

					if (JLXH == JLXH_l) {
						dao.doUpdate(
								"update ZY_BQYZ set QRSJ=:QRSJ,LSBZ=:LSBZ where JLXH =:JLXH",
								parameters_update);
						dao.doSave("create", BSPHISEntryNames.ZY_FYMX,
								parametersForputFYMX, false);
						if (wpjfbz == 1 && wzsfxmjgzy == 1) {
							BSPHISUtil.updateXHMXZT(dao, JLXH, "2", JGID);
						}
						tag=true;
					}
				}
			}
			for (int j = 0; j < bodyForZX.size(); j++) {
				Map<String, Object> parameters_update = new HashMap<String, Object>();
				long JLXH = Long.parseLong(bodyForZX.get(j).get("JLXH") + "");
				String QRSJ = bodyForZX.get(j).get("QRSJ") + "";
				int LSBZ = Integer.parseInt(bodyForZX.get(j).get("LSBZ") + "");
				parameters_update.put("JLXH", JLXH);
				parameters_update.put("QRSJ", BSHISUtil.toDate(QRSJ));
				parameters_update.put("LSBZ", LSBZ);
				dao.doUpdate(
						"update ZY_BQYZ set QRSJ=:QRSJ,LSBZ=:LSBZ where JLXH =:JLXH",
						parameters_update);
				dao.doSave("create", BSPHISEntryNames.ZY_FYMX,
						bodyForZX.get(j), false);
				tag=true;
			}
			if(!tag){
				throw new ModelDataOperationException(9000,"没有需要执行的项目！");
			}
			if (wpjfbz == 1 && wzsfxmjgzy == 0) {
				String d = mmd.saveXhmx(list_xhmx, ctx);
				if (list_xhmx.size() > 0) {
					Map<String, Object> map_ret = new HashMap<String, Object>();
					map_ret.put("BRID", brid);
					map_ret.put("XHRQ", d);
					map_ret.put("KSMC", ksmc);
					map_ret.put("KSDM", ksdm);
					res.put("body", map_ret);
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("确认提交医嘱失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间转换失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("确认提交医嘱失败!", e);
		}
	}

	/**
	 * 费用医嘱附加计价单---查询列表
	 * 
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void doAdditionProjectsFeeQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		String HSQL = (String) user.getProperty("wardId");// 护士权利。
		if (HSQL == null) {
			throw new RuntimeException("请先选择病区！");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		List<Map<String, Object>> yzxhList = new ArrayList<Map<String, Object>>();
		if (req.get("cnds") != null) {
			yzxhList = (List<Map<String, Object>>) req.get("cnds");
		}
		String zyhString = "";
		if (req.get("cnd") != null) {
			zyhString = req.get("cnd") + "";
		}
		List<Map<String, Object>> allYZZHList = new ArrayList<Map<String, Object>>();
		// 设置在查询主项时不进行判断复核标识是否启用
		req.put("ADD_FHBZ", "0");
		this.doDetailChargeQuery(req, res, ctx);
		if (res.get("body") != null) {
			allYZZHList = (List<Map<String, Object>>) res.get("body");
			res.remove("body");
		}
		// String yzxhString = "";
		// for (int i = 0; i < yzxhList.size(); i++) {
		// if (i == 0) {
		// yzxhString = yzxhList.get(i).get("YZXH") + "";
		// } else {
		// yzxhString += "," + yzxhList.get(i).get("YZXH");
		// }
		// }
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("JGID", JGID);
			parameters.put("SRKS", HSQL);
			String date = BSHISUtil.getDate();
			parameters.put("ad_today", BSHISUtil.toDate(date));
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t1.BRCH as BRCH,t1.BRXM as BRXM,t1.BRXZ as BRXZ,t.BRKS as BRKS,t.YZMC as YZMC,t.YEPB as YEPB,t.MZCS as MZCS,to_char(t.KSSJ,'YYYY-MM-DD HH24:MI:SS')as KSSJ,");
			sql_list.append("to_char(t.TZSJ,'YYYY-MM-DD HH24:MI:SS') as TZSJ,t.YPYF as YPYF,t.MRCS as MRCS,t.YCSL as YCSL,t.YPDJ as YPDJ,t.SYBZ as SYBZ,t.SRKS as SRKS,to_char(t.QRSJ,'YYYY-MM-DD HH24:MI:SS') as QRSJ,");
			sql_list.append("t.YPXH as FYXH,t.YPLX as YPLX,t.JLXH as JLXH,t.SYPC as SYPC,t.ZYH as ZYH,t.BZXX as BZXX,t.XMLX as XMLX,t.YZZH as YZZH,t.LSBZ as LSBZ,");
			sql_list.append("t.CZGH as CZGH,t.ZXKS as ZXKS, t.YSGH as YSGH,t.LSYZ as LSYZ,");
			sql_list.append("t.ZFPB as ZFPB,t.YZPB as YZPB,t.FYSX as FYSX,t1.ZLXZ as ZLXZ,0 as FYTS,0 as FYCS, t1.DJID as DJID,t.SRCS as SRCS,t.YZZXSJ as YZZXSJ  FROM ");
			sql_list.append("ZY_BQYZ t, ");
			sql_list.append("ZY_BRRY t1 ");
			sql_list.append("WHERE(t.ZYH = t1.ZYH AND t1.CYPB = 0)AND(t.SRKS =:SRKS)AND(t.YPLX = 0) AND(t.LSBZ = 0)AND(t.YZPB > 0) AND (t.JFBZ=2) AND (t.SYBZ <> 1) ");
			sql_list.append("AND(t.YSBZ = 0 OR (t.YSBZ = 1 AND t.YSTJ = 1))AND(t.JGID =:JGID)AND (t.JGID = t1.JGID) and (ZFBZ = 0) ");
			sql_list.append(" AND (t.QRSJ <=:ad_today or t.QRSJ IS NULL) ");
			// if (yzxhString != "") {
			// sql_list.append(" AND (t.YZZH in (" + yzxhString +") )");
			// }
			if (zyhString != "") {
				sql_list.append(" AND (t.ZYH = '" + zyhString + "' )");
			}
			sql_list.append("ORDER BY t1.BRCH ASC, t.YZZH ASC ");
			// 返会列数的查询语句
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			// 如果查出来有数据 就检索数据。
			int FJBZ = 1; // 再次修改
			if (inofList.size() > 0) {
				inofList.get(0).put("FJBZ", FJBZ);// 附加项目标志
				List<Map<String, Object>> listGY_SYPC = BSPHISUtil
						.u_his_share_yzzxsj(parameters, dao, ctx);
				BSPHISUtil.uf_comp_yzzx(listGY_SYPC, inofList, dao, ctx);
				// long ZYH = 0;
				for (int i = 0; i < inofList.size(); i++) {
					double FYCS = Double.parseDouble(inofList.get(i)
							.get("FYCS") + "");
					double YCSL = Double.parseDouble(inofList.get(i)
							.get("YCSL") + "");
					double YPDJ = Double.parseDouble(inofList.get(i)
							.get("YPDJ") + "");
					inofList.get(i).put("FYCS", FYCS);
					inofList.get(i).put("JE", (FYCS * YCSL * YPDJ));
					if (FYCS <= 0) {
						inofList.remove(i);
						i--;
					} else {
					}
				}
			}
			List<Map<String, Object>> yjList = new ArrayList<Map<String, Object>>();// 定义医技列表，医技有可能在病区项目提交中会将主项目提交，而附加项未提交
			List<Map<String, Object>> xmList = new ArrayList<Map<String, Object>>();// 定义项目列表
			String yzzh = "";
			boolean flag = true;
			/**
			 * 分辨出从数据库中查询出来的，哪些为项目列表，哪些为医技列表
			 * 分辨条件为：数据库中的记录在页面传来的列表中的任何一条记录的YZZH不匹配为医技列表， 否则为项目列表
			 */
			for (Map<String, Object> map : inofList) {
				flag = true;
				yzzh = String.valueOf(map.get("YZZH"));
				for (Map<String, Object> map2 : allYZZHList) {
					// 如果数据库中医嘱组号与页面传过来的医嘱组号有相同，则表明该记录为项目，添加到项目列表中
					if (yzzh.equals(String.valueOf(map2.get("YZZH")))) {
						// 遍历勾选框中医嘱组号，如果存在说明有勾选，放入项目列表中
						for (Map<String, Object> map3 : yzxhList) {
							if (yzzh.equals(String.valueOf(map3.get("YZXH")))) {
								xmList.add(map);
							}
						}
						flag = false;
						break;
					}
				}
				if (flag) {
					yjList.add(map);
				}
			}
			inofList.clear();
			inofList.addAll(yjList);
			inofList.addAll(xmList);
			List<Map<String, Object>> inofList_fy = new ArrayList<Map<String, Object>>();
			int total_pre = inofList.size();
			if (first == 0) {
				for (int i = 0; i < pageSize; i++) {
					if (inofList.size() > i) {
						inofList_fy.add(inofList.get(i));
					}
				}
			} else {
				for (int i = first * pageSize; i < (first + 1) * pageSize; i++) {
					if (inofList.size() > i) {
						inofList_fy.add(inofList.get(i));
					}
				}
			}
			SchemaUtil.setDictionaryMassageForList(inofList_fy,
					"phis.application.war.schemas.ZY_BQYZ_FYYZFJJJ");
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total_pre);
			res.put("body", inofList_fy);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医嘱明细查询失败！");
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-23
	 * @description 项目执行按项目-项目查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Set<Map<String,Object>> xmQuery(Context ctx) throws ModelDataOperationException{
		Map<String,Object> req=new HashMap<String,Object>();
		Map<String,Object> res=new HashMap<String,Object>();
		doDetailChargeQuery(req,res,ctx);
		List<Map<String,Object>> body=(List<Map<String,Object>>)res.get("body");
		Set<Map<String,Object>> ret=new HashSet<Map<String,Object>>();
		for(Map<String,Object> m:body){
			Map<String,Object> map_temp=new HashMap<String,Object>();
			map_temp.put("YPXH", MedicineUtils.parseLong(m.get("YPXH")));
			map_temp.put("YZMC", MedicineUtils.parseString(m.get("YZMC")));
			ret.add(map_temp);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-20
	 * @description 医技项目退回
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryZxks() throws ModelDataOperationException{
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		Map<String,Object> map_qbks=new HashMap<String,Object>();
		map_qbks.put("KSDM", 0);
		map_qbks.put("KSMC", "全部医技科室");
		list_ret.add(map_qbks);
		StringBuffer hql=new StringBuffer();
		hql.append("select OFFICENAME as KSMC,ID as KSDM from SYS_Office where ORGANIZCODE=:jgid and MEDICALLAB=1");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("jgid", UserRoleToken.getCurrent().getManageUnit().getRef());
		try {
			List<Map<String,Object>> list_ks=dao.doQuery(hql.toString(), map_par);
			if(list_ks!=null&&list_ks.size()!=0){
				list_ret.addAll(list_ks);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医技科室查询失败", e);
		}
		
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-21
	 * @description 查询当前科室已提交未执行医技
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryThyj(Map<String,Object> body) throws ModelDataOperationException{
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
//		StringBuffer hql=new StringBuffer();
//		hql.append(" b.QRSJ is not null and a.YJXH=:yjxh and a.YZXH=b.JLXH");
		String manaUnitId = UserRoleToken.getCurrent().getManageUnit().getId();
		List<?> cnd;
		if(MedicineUtils.parseLong(body.get("KSDM"))!=0){
			cnd=CNDHelper.createSimpleCnd("eq", "a.ZXKS", "l", MedicineUtils.parseLong(body.get("KSDM")));
		}else{
			cnd=CNDHelper.createSimpleCnd("eq", "a.JGID", "s", manaUnitId);
		}
		if(body.containsKey("ZYHM")){
			String zyhm=BSPHISUtil.get_public_fillleft(MedicineUtils.parseString(body.get("ZYHM")), "0",
					BSPHISUtil.getRydjNo(manaUnitId, "ZYHM", "", dao).length());
			cnd=CNDHelper.createArrayCnd("and", cnd, CNDHelper.createSimpleCnd("eq", "a.ZYHM", "s", zyhm));
		}
		cnd=CNDHelper.createArrayCnd("and", cnd, CNDHelper.createSimpleCnd("ne", "a.ZXPB", "i", 1));//查询未执行的
		try {
			list_ret=dao.doList(cnd, null, "phis.application.war.schemas.YJTH_YJ01");
//			for(int i=0;i<list_ret.size();i++){//过滤掉已经执行的医技,医技执行会更新QRSJ
//				Map<String,Object> map_ret=list_ret.get(i);
//				Map<String,Object> map_par=new HashMap<String,Object>();
//				map_par.put("yjxh", MedicineUtils.parseLong(map_ret.get("YJXH")));
//				long l=dao.doCount(" YJ_ZY02 a,ZY_BQYZ b", hql.toString(), map_par);
//				if(l>0){
//					list_ret.remove(i);
//					i--;
//				}
//			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医技查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-21
	 * @description 医技退回
	 * @updateInfo
	 * @param yjxh
	 * @throws ModelDataOperationException
	 */
	public void saveYjth(Map<String,Object> body)throws ModelDataOperationException{
//		StringBuffer hql_yjdelete=new StringBuffer();//查询医技是否已经执行(病区医技执行)
//		hql_yjdelete.append(" JLXH in (:yzxh) and QRSJ is not null");
		StringBuffer hql_yjzx=new StringBuffer();//查询医技是否已经执行(医技管理医技执行)
		hql_yjzx.append(" ZXPB=1 and YJXH=:yjxh");
		StringBuffer hql_yzxhs=new StringBuffer();
		hql_yzxhs.append("select YZXH as YZXH from YJ_ZY02 where YJXH=:yjxh");
		StringBuffer hql_yj02=new StringBuffer();
		hql_yj02.append("delete from YJ_ZY02 where YJXH=:yjxh");
		StringBuffer hql_yj01=new StringBuffer();
		hql_yj01.append("delete from YJ_ZY01 where YJXH=:yjxh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("yjxh", MedicineUtils.parseLong(body.get("YJXH")));
		StringBuffer hql_yz=new StringBuffer();
		hql_yz.append("update ZY_BQYZ set SYBZ=0 where JLXH in (:yzxh)");
		try {
			long l=dao.doCount("YJ_ZY01", hql_yjzx.toString(), map_par);
			if(l==1){
				throw new ModelDataOperationException(9000,"该医技项目已经被执行，无法退回，将刷新页面");
			}
			List<Map<String,Object>> l_yz=dao.doQuery(hql_yzxhs.toString(), map_par);
			List<Long> l_yzxhs=new ArrayList<Long>();
			for(Map<String,Object> m:l_yz){
				l_yzxhs.add(MedicineUtils.parseLong(m.get("YZXH")));
			}
			Map<String,Object> map_par_update=new HashMap<String,Object>();
			map_par_update.put("yzxh", l_yzxhs);
//			l=dao.doCount("ZY_BQYZ", hql_yjdelete.toString(), map_par_update);
//			if(l==1){
//				throw new ModelDataOperationException(9000,"医技已经执行");
//			}
			dao.doUpdate(hql_yz.toString(), map_par_update);
			dao.doUpdate(hql_yj02.toString(), map_par);
			dao.doUpdate(hql_yj01.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医技退回失败", e);
		}
		
	}
}
