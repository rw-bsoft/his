package phis.application.med.source;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class WardProjectModule {
	protected Logger logger = LoggerFactory.getLogger(WardProjectModule.class);
	protected BaseDAO dao;

	public WardProjectModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 病区提交模块，按项目提交数据左边列表查询支持分页
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryProjectLeft(Map<String, Object> req,
			Map<String, Object> res) throws ModelDataOperationException {
		List<Map<String, Object>> result = null;
		// int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		// int pageSize = Integer.parseInt(req.get("pageSize").toString());
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT DISTINCT GY_YLSF.FYXH as FYXH , GY_YLSF.FYMC as FYMC, GY_YLSF.FYDW as FYDW, GY_YLSF.PYDM as PYDM, ZY_BQYZ.YPXH as YPXH ");
		// sqlBuilder.append(" FROM ZY_BQYZ,GY_YLSF WHERE ZY_BQYZ.YPXH=GY_YLSF.FYXH ");
		sqlBuilder
				.append("FROM ZY_BQYZ,GY_YLSF WHERE (ZY_BQYZ.SRKS=:al_hsql) AND (ZY_BQYZ.JFBZ=1 OR ZY_BQYZ.JFBZ=9) AND ZY_BQYZ.YPXH=GY_YLSF.FYXH ");
		sqlBuilder
				.append(" AND (ZY_BQYZ.XMLX>3) AND (ZY_BQYZ.SYBZ=0) AND (ZY_BQYZ.LSBZ=0 OR ZY_BQYZ.LSBZ=2) AND (ZY_BQYZ.YZPB=0) AND ");
		sqlBuilder
				.append("( ZY_BQYZ.YSBZ = 0 OR (ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) )  AND");
		Calendar c = Calendar.getInstance();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		sqlBuilder.append("(ZY_BQYZ.QRSJ <= ").append(
				BSPHISUtil.toDate(f.format(c.getTime()) + " 00:00:00",
						"YYYY-MM-DD HH24:MI:SS"));
		sqlBuilder
				.append(" OR (ZY_BQYZ.QRSJ IS NULL))	 AND ZFBZ = 0 AND ZY_BQYZ.JGID = :al_jgid AND ");
		sqlBuilder
				.append("( :al_fhbz = 0 or ZY_BQYZ.FHBZ = 1)  ORDER BY GY_YLSF.PYDM");
		/*
		 * 去除分页功能 StringBuilder sBuilder = new StringBuilder();
		 * sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
		 * sqlBuilder.toString()); sBuilder.append(") t");
		 */
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("al_hsql", req.get("al_hsql"));
			parameters.put("al_jgid", req.get("al_jgid"));
			parameters.put("al_fhbz", req.get("al_fhbz"));
			/*
			 * 去除分页功能 List<Map<String, Object>> coun = dao.doSqlQuery(
			 * sBuilder.toString(), parameters); int total =
			 * Integer.parseInt(coun.get(0).get("NUM").toString());
			 * res.put("totalCount", total); res.put("pageSize", pageSize);
			 * res.put("pageNo", pageNo); parameters.put("first", pageNo *
			 * pageSize); parameters.put("max", pageSize);
			 */
			result = dao.doSqlQuery(sqlBuilder.toString(), parameters);
			// SchemaUtil.setDictionaryMassageForList(result, "MED_YLSF_PRL");
		} catch (PersistentDataOperationException e) {
			logger.error("病区提交模块，按项目提交数据左边列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病区提交模块，按项目提交数据左边列表失败");
		}
		return result;
	}

	/**
	 * 病区提交模块，按病人提交数据左边列表查询支持分页
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPatientLeft(Map<String, Object> req,
			Map<String, Object> res) throws ModelDataOperationException {
		List<Map<String, Object>> result = null;
		// int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		// int pageSize = Integer.parseInt(req.get("pageSize").toString());
		StringBuilder sqlBuilder = new StringBuilder();

		sqlBuilder
				.append("SELECT DISTINCT ZY_BQYZ.ZYH as ZYH, ZY_BRRY.BRCH as BRCH, ZY_BRRY.ZYHM as ZYHM, ZY_BRRY.BRXM as BRXM, ZY_BRRY.BRXZ as BRXZ, ZY_BRRY.DJID  as DJID ");
		sqlBuilder
				.append("FROM ZY_BQYZ, ZY_BRRY  WHERE ( ZY_BQYZ.ZYH = ZY_BRRY.ZYH ) AND ( ZY_BQYZ.JGID = ZY_BRRY.JGID ) ");
		sqlBuilder
				.append("AND ( ZY_BQYZ.SRKS = :al_hsql ) AND ( ZY_BQYZ.JFBZ=1 OR ZY_BQYZ.JFBZ=9 ) AND ( ZY_BQYZ.XMLX > 3 ) AND");
		sqlBuilder
				.append(" ( ZY_BQYZ.SYBZ=0) AND ( ZY_BQYZ.LSBZ=0 OR ZY_BQYZ.LSBZ=2) AND ( ZY_BQYZ.YZPB=0) AND");
		sqlBuilder
				.append("( ZY_BQYZ.YSBZ = 0 OR (ZY_BQYZ.YSBZ = 1 AND ZY_BQYZ.YSTJ = 1) ) AND ( ZY_BQYZ.QRSJ <= ");
		Calendar c = Calendar.getInstance();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		sqlBuilder.append(
				BSPHISUtil.toDate(f.format(c.getTime()) + " 00:00:00",
						"YYYY-MM-DD HH24:MI:SS")).append(
				" OR (ZY_BQYZ.QRSJ IS NULL)) and");
		sqlBuilder
				.append("( ZY_BQYZ.JGID = :al_jgid ) and ( :al_fhbz = 0 or ZY_BQYZ.FHBZ = 1) ORDER BY ZY_BRRY.BRCH");
		/*
		 * 去除分页功能 StringBuilder sBuilder = new StringBuilder();
		 * sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
		 * sqlBuilder.toString()); sBuilder.append(") t");
		 */
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("al_hsql", req.get("al_hsql"));
			parameters.put("al_jgid", req.get("al_jgid"));
			parameters.put("al_fhbz", req.get("al_fhbz"));
			/*
			 * 去除分页功能 List<Map<String, Object>> coun = dao.doSqlQuery(
			 * sBuilder.toString(), parameters); int total =
			 * Integer.parseInt(coun.get(0).get("NUM").toString());
			 * res.put("totalCount", total); res.put("pageSize", pageSize);
			 * res.put("pageNo", pageNo); parameters.put("first", pageNo *
			 * pageSize); parameters.put("max", pageSize);
			 */
			result = dao.doSqlQuery(sqlBuilder.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("病区提交模块，按病人提交数据左边列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病区提交模块，按病人提交数据左边列表失败");
		}
		return result;
	}

	/**
	 * 病区提交模块，按病人提交右边窗口
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryPatientRight(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			List<Object> cnd = (List<Object>) req.get("cnd");
			result = dao.doList(cnd, null, String.valueOf(req.get("schema")));
			filterData(result, ctx);
		} catch (PersistentDataOperationException e) {
			logger.error("病区提交模块，按病人提交数据右边列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病区提交模块，按病人提交数据右边列表失败");
		}
		return result;
	}

	/**
	 * 按项目提交，右边列表
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryProjectRight(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		try {
			List<Object> cnd = (List<Object>) req.get("cnd");
			result = dao.doList(cnd, null, String.valueOf(req.get("schema")));
			filterData(result, ctx);
		} catch (PersistentDataOperationException e) {
			logger.error("病区提交模块，按病人提交数据右边列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病区提交模块，按病人提交数据右边列表失败");
		}
		return result;
	}

	/**
	 * 过滤数据
	 * 
	 * @param result
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	private void filterData(List<Map<String, Object>> result, Context ctx)
			throws ModelDataOperationException {
		if (result != null && result.size() > 0) {
			int ll_lsbz = 0, ll_zxcs = 0, ll_lsyz = 0;
			List<Map<String, Object>> listGY_SYPC = BSPHISUtil
					.u_his_share_yzzxsj(null, dao, ctx);
			Map<String, Object> parameters_cq = new HashMap<String, Object>();
			Map<String, Object> parameters_lsbz = new HashMap<String, Object>();
			Map<String, Object> map = null;
			for (int i = 0; i < result.size(); i++) {
				ll_zxcs = 0;
				map = result.get(i);
				parameters_lsbz = new HashMap<String, Object>();
				parameters_lsbz.put("ldt_kssj", map.get("KSSJ"));
				parameters_lsbz.put("ldt_qrsj", map.get("QRSJ"));
				parameters_lsbz.put("ldt_tzsj", map.get("TZSJ"));
				parameters_lsbz.put("ls_sypc", map.get("SYPC"));
				parameters_lsbz.put("ls_yzzxsj_str", map.get("YZZXSJ"));
				parameters_lsbz.put("ll_lsyz", map.get("LSYZ"));
				parameters_lsbz.put("al_ypbz", 0);
				parameters_lsbz.put("SRCS", map.get("SRCS"));
				ll_lsyz = Integer.parseInt(String.valueOf(map.get("LSYZ")));
				// 计算历史标志
				ll_lsbz = BSPHISUtil.uf_cacl_lsbz(listGY_SYPC, parameters_lsbz,
						dao, ctx);
				if (ll_lsbz == 1) {// 如果历史标志为1，则删除当前行
					parameters_lsbz.put("ll_yzxh", map.get("JLXH"));
					// 更新历史标志l
					BSPHISUtil.uf_update_lsbz(parameters_lsbz, dao, ctx);
					result.remove(i);
					i--;
					continue;
				}
				if (ll_lsyz == 0) {// 长期医嘱
					parameters_cq = new HashMap<String, Object>();
					BSPHISUtil.uf_cacl_zxcs_cq(listGY_SYPC, parameters_lsbz,
							parameters_cq, dao, ctx);
					if (parameters_cq.get("al_zxcs") != null) {
						ll_zxcs = Integer.parseInt(String.valueOf(parameters_cq
								.get("al_zxcs")));
					}
				} else {// 临时医嘱
					for (int j = 0; j < listGY_SYPC.size(); j++) {
						if (String.valueOf(map.get("SYPC")).equals(
								listGY_SYPC.get(j).get("PCBM"))) {
							ll_zxcs = Integer.parseInt(listGY_SYPC.get(j).get(
									"MRCS")
									+ "");
						}
					}
				}
				if (ll_zxcs <= 0) {
					result.remove(i);
					i--;
				} else {
					double je = Double.parseDouble(String.valueOf(map
							.get("YPDJ")))
							* Double.parseDouble(String.valueOf(map.get("YCSL")));
					map.put("JE", je + "x" + ll_zxcs);
					map.put("C", ll_zxcs);
				}

			}
		}
	}

	/**
	 * 保存按钮执行
	 * 
	 * @param updateList
	 * @throws ModelDataOperationException
	 */
	public void dosave(List<Map<String, String>> updateList, Context ctx)
			throws ModelDataOperationException {
		if (updateList != null && updateList.size() > 0) {
			StringBuilder sql = new StringBuilder();
			Map<String, Object> parameters = new HashMap<String, Object>();
			sql.append("UPDATE ZY_BQYZ SET ZXKS = :zxks WHERE JLXH = :jlxh");
			for (Map<String, String> map : updateList) {
				try {
					parameters.clear();
					parameters.put("zxks", map.get("ZXKS"));
					parameters.put("jlxh", map.get("JLXH"));
					dao.doSqlUpdate(sql.toString(), parameters);
				} catch (PersistentDataOperationException e) {
					logger.error("病区提交模块，进行保存数据库操作失败", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"病区提交模块，进行保存按钮数据库操作失败");
				}
			}
		}
	}

	/**
	 * 查询已作废的项目
	 * 
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryUnInvalidProject(Context ctx)
			throws ModelDataOperationException {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT b.FYXH as FYXH,b.FYMC as FYMC,b.PYDM as PYDM ,b.FYDW as FYDW,b.FYGB as FYGB,c.FYDJ as FYDJ,b.ZYSY ");
		sqlBuilder
				.append("as ZYSY,b.YJSY as YJSY,b.XMLX as XMLX,c.JGID as JGID from GY_YLSF b,GY_YLMX c");
		sqlBuilder
				.append(" where  b.ZFPB=0 and c.ZFPB = 0 and b.FYXH = c.FYXH and c.jgid = :jgid");
		List<Map<String, Object>> rs = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		try {
			Map<String, Object> parMap = new HashMap<String, Object>();
			parMap.put("jgid", manaUnitId);
			rs = dao.doSqlQuery(sqlBuilder.toString(), parMap);
		} catch (PersistentDataOperationException e) {
			logger.error("病区提交模块，进行查询未作废的项目失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病区提交模块，查询未作废的项目失败");
		}
		return rs;
	}

	/**
	 * 校验提交项目是否作废
	 * 
	 * @param invalidList
	 *            已作废项目列表
	 * @param map
	 *            校验项目
	 * @param res
	 *            返回给客户端对象
	 * @return true:校验通过 false:校验未通过,校验项目为作废项目
	 */
	public boolean checkInvalidProject(List<Map<String, Object>> invalidList,
			Map<String, Object> map, Map<String, Object> res) {
		boolean flag = false;
		String ypxh = String.valueOf(map.get("YPXH"));
		String fyxh = "";
		if (invalidList != null) {
			for (Map<String, Object> m : invalidList) {
				fyxh = String.valueOf(m.get("FYXH"));
				if (fyxh.equals(ypxh)) {
					flag = true;
					break;
				}
			}
		}

		if (!flag) {
			logger.warn("姓名:" + map.get("BRXM") + ";提交项中有作废项目");
			res.put("x-response-code", "201");
			res.put("x-response-msg", "姓名:" + map.get("BRXM") + ";提交项中有作废项目");
		}
		return flag;
	}

	/**
	 * 校验同组的执行科室是否相同
	 * 
	 * @param rightGroup
	 * @param res
	 *            返回给客户端对象
	 * @return true : 校验通过 false: 校验不通过，同组中有不同的执行科室
	 */
	public boolean checkGroupIsEqualZXKS(
			Map<String, List<Map<String, Object>>> rightGroup,
			Map<String, Object> res) {
		boolean flag = true;
		if (rightGroup != null) {
			List<Map<String, Object>> list = null;
			String tempZXKS = "";
			for (String key : rightGroup.keySet()) {
				list = rightGroup.get(key);// 获取同组中所有执行项
				for (Map<String, Object> map : list) {
					if ("".equals(tempZXKS)) {
						tempZXKS = String.valueOf(map.get("ZXKS"));// 临时执行科室变量为空字符串时说明为第一次
					} else {
						// 如果执行科室不同则不进行提交，返回给客户端
						if (!tempZXKS.equals(String.valueOf(map.get("ZXKS")))) {
							res.put("code", "201");
							res.put("msg", "同组医嘱执行科室需要相同!");
							logger.warn("组:" + key + ";执行科室不同");
							flag = false;
							break;
						}
					}
				}
				if (!flag) {
					break;
				}
				tempZXKS = "";// 重置执行科室为空字符串
			}
		}
		return flag;
	}

	/**
	 * 确认按钮提交
	 * 
	 * @param leftList
	 * @param rightGroup
	 * @param res
	 * @param jgid
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveDetermine(List<Map<String, String>> leftList,
			Map<String, List<Map<String, Object>>> rightGroup,
			Map<String, Object> res, String jgid, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = null;
		Map<String, Object> record = null, result = null;
		long yjxh = 0, fygb = 0;
		StringBuilder sqlBuilder = new StringBuilder();
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> queryList = null;
		int count = 0;
		double zfbl = 0;
		String ycls, mrcs, cs;
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.###");
		UserRoleToken user = UserRoleToken.getCurrent();
		String msg = "";
		try {
			for (String key : rightGroup.keySet()) {
				list = rightGroup.get(key);// 获取同组中所有执行项
				// 欠费病人判断
				BSPHISUtil.ArrearsPatientsQuery(list, ctx, dao,res);
				if (res.get("RES_MESSAGE1") != null) {
					if (!msg.contains(res.get("RES_MESSAGE1")+"")) {
						msg += res.get("RES_MESSAGE1")+"";
					}
				}
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					record = new HashMap<String, Object>();
					if (i == 0) {
						record.put("JGID", Long.parseLong(jgid));// 机构ID
						record.put("ZYH",
								Long.parseLong(map.get("ZYH").toString()));// 住院号
						record.put("ZYHM", map.get("ZYHM").toString());// 住院号码
						record.put("BRXM", map.get("BRXM").toString());// 病人姓名
						record.put("KDRQ", new Date());// 开单日期
						record.put("TJSJ", new Date());// 提交时间
						record.put("KSDM",
								Long.parseLong(map.get("BRKS").toString()));// 科室代码，使用病人科室
						record.put("YSDM", map.get("YSGH").toString());// 医生代码，使用开嘱医生
						record.put("ZXKS",
								Long.parseLong(map.get("ZXKS").toString()));// 执行科室
						// record.put("HJGH", map.get("CZGH").toString());//
						// 划价工号，使用操作工号
						record.put("HJGH", user.getUserId());// 划价工号，使用当前登录的提交医生
						record.put("DJZT", Long.parseLong("1"));// 单据状态，使用默认1
						record.put("FYBQ",
								Long.parseLong(map.get("SRKS").toString()));// 费用病区，使用输入科室
						record.put("ZXPB", 0);// 执行判别默认给0
						record.put("ZFPB", 0);// 作废标志默认给0
						record.put("TJBZ", "0");// 病区医技提交标志0
						
						 Long sqid;
							String sq="select SEQ_SQID.nextval as SQXH from dual ";
							List<Map<String, Object>> list_2 = new ArrayList<Map<String, Object>>();
							list_2=	dao.doSqlQuery(sq, null);
							for(Map<String,Object> o: list_2){
								
								sqid=((BigDecimal)o.get("SQXH")).longValue();
								record.put("SQDH", sqid);
								
							}
						result = dao.doSave("create", BSPHISEntryNames.YJ_ZY01,
								record, false);
						yjxh = Long
								.parseLong(String.valueOf(result.get("YJXH")));// 获取刚刚往YZ_ZY01插入的医技序号(YJXH)
						record = new HashMap<String, Object>();
					}
					record.clear();
					sqlBuilder = new StringBuilder();
					sqlBuilder
							.append("SELECT COUNT(1) as LL_COUNT FROM ZY_BQYZ Where zyh = :ll_zyh And jlxh = :li_jlxh And SYBZ = 1  And JGID = :il_jgid ");
					parameters = new HashMap<String, Object>();
					parameters.put("ll_zyh",
							Long.parseLong(map.get("ZYH").toString()));// 住院号
					parameters.put("li_jlxh",
							Long.parseLong(map.get("JLXH").toString()));
					parameters.put("il_jgid", jgid);
					queryList = dao.doSqlQuery(sqlBuilder.toString(),
							parameters);
					count = Integer.parseInt(String.valueOf(queryList.get(0)
							.get("LL_COUNT")));
					if (count == 0) {
						sqlBuilder = new StringBuilder();
						sqlBuilder
								.append("SELECT COUNT(1) as LL_COUNT FROM ZY_BQYZ Where zyh = :ll_zyh And jlxh = :li_jlxh And ( ZY_BQYZ.YSBZ = 0 Or (ZY_BQYZ.YSBZ = 1 And ZY_BQYZ.YSTJ = 1) ) And JGID = :il_jgid ");
						queryList = dao.doSqlQuery(sqlBuilder.toString(),
								parameters);
						count = Integer.parseInt(String.valueOf(queryList
								.get(0).get("LL_COUNT")));
						if (count > 0) {
							// 更新病区记录
							this.updateZY_BQYZ(map, yjxh, jgid);
						}
						record.put("JGID", Long.parseLong(jgid));// 机构ID
						record.put("YJXH", yjxh);// 医技序号
						record.put("YLXH",
								Long.parseLong(map.get("YPXH").toString()));// 医疗序号，使用药品序号
						record.put("XMLX",
								Long.parseLong(map.get("XMLX").toString()));// 项目类型
						record.put("YJZX",
								Long.parseLong(map.get("YJZX").toString()));// 医技主项
						record.put("YLDJ", map.get("YPDJ"));// 医疗单价，使用药品单价
						// record.put("YLSL", map.get("YCSL"));//医疗数量，使用一次数量
						// record.put("YLSL", map.get("C"));//医疗数量，使用一次数量
						ycls = String.valueOf(map.get("YCSL"));
						cs = String.valueOf(map.get("C"));// 在查询时已经计算好执行次数(界面显示金额后面的次数),与李云涛这边沟通需要修改
						// mrcs = String.valueOf(map.get("MRCS"));
						// 医疗数量=一次数量 × 次数
						record.put(
								"YLSL",
								Double.parseDouble(ycls)
										* Double.parseDouble(cs));
						// 根据费用序号获取费用归并
						fygb = this.getFygbByFyxh(String.valueOf(map
								.get("YPXH")));
						record.put("FYGB", fygb);// 费用归并
						Map<String, Object> body = new HashMap<String, Object>();
						body.put("TYPE", map.get("YPLX"));
						body.put("BRXZ", map.get("BRXZ"));
						body.put("FYXH", map.get("YPXH"));
						body.put("FYGB", fygb);
						// zfbl =
						// Double.parseDouble(String.valueOf(BSPHISUtil.getzfbl(body,
						// ctx, dao).get("ZFBL")))/100;
						zfbl = Double.parseDouble(String.valueOf(BSPHISUtil
								.getzfbl(body, ctx, dao).get("ZFBL")));
						record.put("ZFBL", df.format(zfbl));// 自负比例
						record.put("YZXH", map.get("JLXH"));// 医嘱序号
						record.put("YEPB", 0);// 婴儿判别
						record.put("JCBWDM", map.get("JCBWDM"));
						result = dao.doSave("create", BSPHISEntryNames.YJ_ZY02,
								record, true);
					} else {
						throw new ModelDataOperationException("当前数据已过期,请刷新后重新提交!");
					}

				}
				if (msg.length() >0) {
					res.put("RES_MESSAGE", "病人  "+msg+" 预交款余额不足，不能提交医嘱!");
				}
			}
		} catch (Exception e) {
			logger.error("病区提交模块，进行确认操作失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病区提交模块，确认操作失败");
		}
	}

	/**
	 * 病区提交模块，更新病区记录
	 * 
	 * @param map
	 * @param yjxh
	 * @param jgid
	 * @throws ModelDataOperationException
	 */
	private void updateZY_BQYZ(Map<String, Object> map, long yjxh, String jgid)
			throws ModelDataOperationException {
		try {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("UPDATE ZY_BQYZ SET SYBZ = 1,YJXH = :ll_yjxh Where zyh = :ll_zyh And jlxh = :li_jlxh And ");
			sBuilder.append("( ZY_BQYZ.YSBZ = 0 Or (ZY_BQYZ.YSBZ = 1 And ZY_BQYZ.YSTJ = 1) ) And JGID = :li_jgid ");
			// sBuilder.append("UPDATE ZY_BQYZ SET SYBZ = 1,YJXH = 14 Where zyh = 2012068 And jlxh = 2095 And ");
			// sBuilder.append("( ZY_BQYZ.YSBZ = 0 Or (ZY_BQYZ.YSBZ = 1 And ZY_BQYZ.YSTJ = 1) ) And JGID = 440402001 ");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ll_yjxh", Long.parseLong(yjxh + ""));
			parameters.put("ll_zyh", Long.parseLong(map.get("ZYH").toString()));
			parameters.put("li_jlxh",
					Long.parseLong(map.get("JLXH").toString()));
			parameters.put("li_jgid", jgid);
			dao.doSqlUpdate(sBuilder.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("病区提交模块，进行更新病区记录操作失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病区提交模块，进行更新病区记录操作失败");
		}
	}

	/**
	 * 根据费用序号获取费用归并
	 * 
	 * @param fyxh
	 *            费用序号
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public Long getFygbByFyxh(String fyxh)
			throws PersistentDataOperationException {
		long fygb = 0;
		StringBuilder sBuilder = new StringBuilder();
		Map<String, Object> parameters = new HashMap<String, Object>();
		sBuilder.append("SELECT FYGB as FYGB FROM GY_YLSF WHERE FYXH = :FYXH");
		parameters.put("FYXH", Long.parseLong(fyxh));
		List<Map<String, Object>> list = dao.doSqlQuery(sBuilder.toString(),
				parameters);
		if (list != null && list.size() > 0) {
			fygb = Long.parseLong(String.valueOf(list.get(0).get("FYGB")));
		}
		return fygb;
	}

	/**
	 * 获取未复核记录数 当复核标志为1时，说明需要复核才能在病区项目提交中显示。因此需要查询未复核的记录总数
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long getUnReviewCount(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		long count = 0;
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String al_hsql = String.valueOf(req.get("body"));
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(" (a.ZYH = b.ZYH AND b.CYPB=0) AND ");
		sqlBuilder
				.append(" (a.JGID = b.JGID ) AND ( a.SRKS=:al_hsql ) AND (a.JFBZ=1 OR a.JFBZ=9) AND ");
		sqlBuilder
				.append(" (a.XMLX>3) AND (a.SYBZ=0) AND (a.LSBZ=0 OR a.LSBZ=2) AND (a.YZPB=0) AND ");
		sqlBuilder
				.append(" ( a.YSBZ = 0 OR (a.YSBZ = 1 AND a.YSTJ = 1) ) AND ( a.QRSJ <= ");

		Calendar c = Calendar.getInstance();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		sqlBuilder.append(
				BSPHISUtil.toDate(f.format(c.getTime()) + " 00:00:00",
						"YYYY-MM-DD HH24:MI:SS")).append(
				" OR (a.QRSJ IS NULL)) and");

		sqlBuilder.append(" a.JGID = :al_jgid and a.FHBZ=0 ");
		if (req.get("type") != null && req.get("type").equals("personal")) {
			sqlBuilder.append(" and b.ZYH=" + req.get("ZYH"));
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_hsql", Long.parseLong(al_hsql));
		parameters.put("al_jgid", jgid);
		try {
			count = dao.doCount("ZY_BQYZ a ,ZY_BRRY b ", sqlBuilder.toString(),
					parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("病区提交模块，获取未复核记录数失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病区提交模块，获取未复核记录数失败");
		}
		return count;
	}

	public static void main(String[] args) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
		double d = 3;
		// System.out.println(df.format(d));
	}
}
