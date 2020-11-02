package phis.application.fsb.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.support.TableDictionary;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpressionProcessor;

public class FamilySickBedAdviceQuery extends SimpleQuery {
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
			String whereCnd = ExpressionProcessor.instance().toString(queryCnd)
					+ " and a.JGID=" + manageUnit;
			// parameters.put("first", first);
			// parameters.put("max", pageSize);
			String cqbz = (req.get("cqbz") == null ? "false" : req.get("cqbz")
					.toString());
			String lsbz = (req.get("lsbz") == null ? "false" : req.get("lsbz")
					.toString());
			String ypbz = (req.get("ypbz") == null ? "false" : req.get("ypbz")
					.toString());
			String xmbz = (req.get("xmbz") == null ? "false" : req.get("xmbz")
					.toString());
			StringBuffer group_1 = new StringBuffer();
			StringBuffer group_2 = new StringBuffer();
			if ("true".equals(cqbz)) {
				group_1.append(" (LSYZ=0 ");
			}
			if ("true".equals(lsbz)) {
				if (group_1.length() > 0) {
					group_1.append(" or LSYZ=1 ");
				} else {
					group_1.append(" (LSYZ=1 ");
				}
			}
			if (group_1.length() > 0) {
				group_1.append(") and ");
			}
			if ("true".equals(ypbz)) {
				group_2.append(" (XMLX=1 ");
			}
			if ("true".equals(xmbz)) {
				if (group_2.length() > 0) {
					group_2.append(" or XMLX>=4 ");
				} else {
					group_2.append(" (XMLX>=4 ");
				}
			}
			if (group_2.length() > 0) {
				group_2.append(")");
			}
			group_1 = group_1.append(group_2);
			if (group_1.length() > 0) {
				group_1.insert(0, " and ");
			}

			StringBuffer sql = new StringBuffer(
					" select a.TJZX as TJZX,a.HSGH as HSGH,a.ZFYP,b.ZFYP as ZBY,a.YFDW,b.YPJL,b.JLDW,a.JLXH as JLXH, a.JGID, a.ZYH, a.YPXH, a.YZZH, a.YFBZ,a.CFTS,a.YPZS,a.JZ, "
							+ BSPHISUtil.toChar("a.KSSJ",
									"yyyy-mm-dd hh24:mi:ss")
							+ " as KSSJ, a.YZMC, a.YCJL, a.YCSL, a.SYPC, a.MRCS, a.YZZXSJ, a.SRCS, a.YPYF, a.YPDJ, a.YSGH,a.FHGH,"
							+ BSPHISUtil.toChar("a.TZSJ",
									"yyyy-mm-dd hh24:mi:ss")
							+ " as TZSJ,"
							+ BSPHISUtil.toChar("a.QRSJ",
									"yyyy-mm-dd hh24:mi:ss") + " as QRSJ,a.TPN,");
			sql.append(" a.TZYS, a.FYFS, a.BZXX, a.YPCD, a.YFSB, a.CZGH, a.ZFPB, a.YPLX, a.SYBZ, a.MZCS, a.YJZX, a.ZXKS, a.YJXH, a.XMLX, a.JFBZ, a.ZFBZ, a.SFJG, a.FHBZ, a.TZFHBZ, a.PSPB, a.YZPB, a.LSBZ, a.YEPB, a.FYSX, a.YSBZ, a.YSTJ, a.LSYZ,a.YWID,a.YYTS,a.TJZRQ,a.TJZRQ as YTJZRQ,a.CCJL as CCJL from ");
			sql.append("JC_BRYZ a left join YK_TYPK b on (a.YPXH=b.YPXH ) ");
			sql.append(" where (XMLX=1 or XMLX>=4) and " + whereCnd + group_1
					+ " order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
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
								.get("phis.dictionary.drugMode")
								.getText(record.get("YPYF") + ""));
				record.put(
						"YSGH_text",
						DictionaryController.instance()
								.get("phis.dictionary.doctor")
								.getText(record.get("YSGH") + ""));
				record.put(
						"TZYS_text",
						DictionaryController.instance()
								.get("phis.dictionary.doctor")
								.getText(record.get("TZYS") + ""));
				if (record.get("FYFS") != null) {
					record.put(
							"FYFS_text",
							DictionaryController.instance()
									.get("phis.dictionary.hairMedicineWay")
									.getText(record.get("FYFS") + ""));
				}
				record.put(
						"YPCD_text",
						DictionaryController.instance()
								.get("phis.dictionary.medicinePlace")
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
				record.put(
						"HSGH_text",
						getListDicText("phis.dictionary.doctor",
								record.get("HSGH") + ""));

			}
			long count = dao.doCount("JC_BRYZ a", whereCnd, null);
			res.put("body", data);
			res.put("totalCount", count);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getListDicText(String dic, String v)
			throws ControllerException {
		Map<String, DictionaryItem> dicItems = null;
		Dictionary d = DictionaryController.instance().get(dic);
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
