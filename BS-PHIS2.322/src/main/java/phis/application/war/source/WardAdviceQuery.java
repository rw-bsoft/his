package phis.application.war.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.support.TableDictionary;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpRunner;

public class WardAdviceQuery extends SimpleQuery {
	Map<String, Map<String, DictionaryItem>> tempDic = new HashMap<String, Map<String, DictionaryItem>>();

	/**
	 * 查询病人就诊信息
	 */
	@SuppressWarnings("rawtypes")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		// int pageNo = Integer.parseInt(req.get("pageNo") + "");
		// int pageSize = Integer.parseInt(req.get("pageSize") + "");
		// int first = (pageNo - 1) * pageSize;
		tempDic.clear();
		try {
			List queryCnd = null;
			if (req.containsKey("cnd")) {
				queryCnd = (List) req.get("cnd");
			}
			String jzbz = null;
			if (req.containsKey("jzbz")) {
				if (req.get("jzbz") != "" && req.get("jzbz") != null) {
					jzbz = req.get("jzbz") + "";
				}
			}
			String dybz = null;
			if (req.containsKey("dybz")) {
				if (req.get("dybz") != "" && req.get("dybz") != null) {
					dybz = req.get("dybz") + "";
				}
			}
			String cqbz = null;
			if (req.containsKey("cqbz")) {
				if (req.get("cqbz") != "" && req.get("cqbz") != null) {
					cqbz = req.get("cqbz") + "";
				}
			}
			String lsbz = null;
			if (req.containsKey("lsbz")) {
				if (req.get("lsbz") != "" && req.get("lsbz") != null) {
					lsbz = req.get("lsbz") + "";
				}
			}
			String ypbz = null;
			if (req.containsKey("ypbz")) {
				if (req.get("ypbz") != "" && req.get("ypbz") != null) {
					ypbz = req.get("ypbz") + "";
				}
			}
			String xmbz = null;
			if (req.containsKey("xmbz")) {
				if (req.get("xmbz") != "" && req.get("xmbz") != null) {
					xmbz = req.get("xmbz") + "";
				}
			}
			String whereCnd = ExpRunner.toString(queryCnd, ctx)
					+ " and a.JGID=" + manageUnit;
			// parameters.put("first", first);
			// parameters.put("max", pageSize);

			StringBuffer sql = new StringBuffer(
					" select a.ZFYP,b.ZFYP as ZBY,a.YFDW,b.YPJL,b.JLDW,a.JLXH as JLXH, a.JGID, a.ZYH, a.YPXH, a.YZZH, a.YFBZ,a.CFTS,a.YPZS,a.JZ, "
							+ BSPHISUtil.toChar("a.KSSJ",
									"yyyy-mm-dd hh24:mi:ss")
							+ " as KSSJ, a.YZMC, a.YCJL, a.YCSL, a.SYPC, a.MRCS, a.YZZXSJ, a.SRCS, a.YPYF, a.YPDJ, a.YSGH,a.FHGH,"
							+ BSPHISUtil.toChar("a.TZSJ",
									"yyyy-mm-dd hh24:mi:ss")
							+ " as TZSJ,"
							+ BSPHISUtil.toChar("a.QRSJ",
									"yyyy-mm-dd hh24:mi:ss") + " as QRSJ,");
			sql.append(" a.TZYS, a.FYFS, a.BZXX, a.YPCD, a.YFSB, a.CZGH, a.ZFPB, a.YPLX, a.SYBZ, a.MZCS, a.YJZX, a.ZXKS, a.YJXH, a.XMLX, a.JFBZ, a.ZFBZ, a.SFJG, a.FHBZ, a.TZFHBZ, a.PSPB, a.YZPB, a.LSBZ, a.YEPB, a.FYSX, a.YSBZ, a.YSTJ, a.LSYZ,a.YWID,a.YYTS from ");
			sql.append("ZY_BQYZ a left join YK_TYPK b on (a.YPXH=b.YPXH ) ");
			long zyh = 0l;
			if (req.containsKey("zyh")) {
				zyh = Long.parseLong(req.get("zyh") + "");
			}
			if (whereCnd.indexOf("a.XMLX = 2") > 0
					|| whereCnd.indexOf("a.XMLX = 3") > 0) {
				sql.append(" where " + whereCnd
						+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
			} else {
				if (zyh != 0) {
					if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ=0 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ=0 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ=0 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ>=0 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ>=0 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ>=0 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ=1 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ=1 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=4) and LSYZ=1 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where XMLX=2 and LSYZ=1 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where XMLX=2 and LSYZ=1 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where XMLX=2 and LSYZ=1 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0 and XMLX<>3) or XMLX=2) and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0 and XMLX<>3) or XMLX=2) and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0 and XMLX<>3) or XMLX=2) and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and XMLX<>3 and LSYZ=1 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and XMLX<>3 and LSYZ=1 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and XMLX<>3 and LSYZ=1 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and XMLX<>3 and LSYZ>=0 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and XMLX<>3 and LSYZ>=0 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "false".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and XMLX<>3 and LSYZ>=0 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where XMLX=3 and LSYZ=1 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where XMLX=3 and LSYZ=1 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where XMLX=3 and LSYZ=1 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0) or XMLX=3) and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0) or XMLX=3) and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0) or XMLX=3) and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=3) and LSYZ=1 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=3) and LSYZ=1 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=3) and LSYZ=1 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=3) and LSYZ>=0 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=3) and LSYZ>=0 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "false".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=3) and LSYZ>=0 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=2 or XMLX=3) and LSYZ>=1 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=2 or XMLX=3) and LSYZ>=1 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=2 or XMLX=3) and LSYZ=1 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0) or XMLX=2) and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0) or XMLX=2) and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "false".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (((XMLX=1 or XMLX>=4) and LSYZ=0) or XMLX=2) and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and LSYZ=1 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and LSYZ=1 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and LSYZ=1 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and LSYZ>=0 and YPLX>0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "false".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and LSYZ>=0 and YPLX=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("true".equals(cqbz) && "true".equals(lsbz)
							&& "true".equals(jzbz) && "true".equals(dybz)
							&& "true".equals(ypbz) && "true".equals(xmbz)) {
						sql.append(" where (XMLX=1 or XMLX>=2) and LSYZ>=0 and YPLX>=0 and "
								+ whereCnd
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(cqbz) && "false".equals(lsbz)
							&& "false".equals(jzbz) && "false".equals(dybz)) {
						sql.append(" where ZYH=0"
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					} else if ("false".equals(ypbz) && "false".equals(xmbz)) {
						sql.append(" where ZYH=0"
								+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
					}
				} else {
					sql.append(" where (XMLX=1 or XMLX>=4) and " + whereCnd
							+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
				}
			}
			List<Map<String, Object>> data = dao.doSqlQuery(sql.toString(),
					parameters);
			for (Map<String, Object> record : data) {
				if (Integer.parseInt(record.get("XMLX").toString()) >= 4) {
					record.remove("JLDW");
				}
				record.put(
						"SYPC_text",
						getListDicText("phis.dictionary.useRate",
								record.get("SYPC") + ""));
				record.put(
						"YPYF_text",
						DictionaryController.instance()
								.getDic("phis.dictionary.drugMode")
								.getText(record.get("YPYF") + ""));
				record.put(
						"YSGH_text",
						DictionaryController.instance()
								.getDic("phis.dictionary.doctor")
								.getText(record.get("YSGH") + ""));
				record.put(
						"TZYS_text",
						DictionaryController.instance()
								.getDic("phis.dictionary.doctor")
								.getText(record.get("TZYS") + ""));
				if (record.get("FYFS") != null) {
					record.put(
							"FYFS_text",
							DictionaryController.instance()
									.getDic("phis.dictionary.hairMedicineWay")
									.getText(record.get("FYFS") + ""));
				}
				record.put(
						"YPCD_text",
						DictionaryController.instance()
								.getDic("phis.dictionary.medicinePlace")
								.getText(record.get("YPCD") + ""));
				// tempDic.clear();
				record.put(
						"YFSB_text",
						getListDicText("phis.dictionary.wardPharmacy",
								record.get("YFSB") + ""));
				record.put(
						"CZGH_text",
						getListDicText("phis.dictionary.doctor",
								record.get("CZGH") + ""));
				record.put(
						"FHGH_text",
						getListDicText("phis.dictionary.doctor",
								record.get("FHGH") + ""));

			}
			long count = dao.doCount("ZY_BQYZ a", whereCnd, null);
			res.put("body", data);
			res.put("totalCount", count);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getListDicText(String dic, String v) {
		Map<String, DictionaryItem> dicItems = null;
		Dictionary d = DictionaryController.instance().getDic(dic);
		if (d instanceof TableDictionary) {
			if (tempDic.get(dic) != null) {
				dicItems = tempDic.get(dic);
			} else {
				List<DictionaryItem> is = ((TableDictionary) d).initAllItems();
				Map<String, DictionaryItem> items = new HashMap<String, DictionaryItem>();
				for (DictionaryItem di : is) {
					items.put(di.getKey(), di);
				}
				dicItems = items;
				tempDic.put(dic, items);
			}
			return dicItems.get(v) == null ? "" : dicItems.get(v).getText();
		} else {
			return d.getText(v);
		}
	}

}
