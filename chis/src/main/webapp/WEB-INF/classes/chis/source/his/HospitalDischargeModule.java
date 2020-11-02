package chis.source.his;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.util.exp.ExpressionProcessor;

public class HospitalDischargeModule implements BSCHISEntryNames {

	private BaseDAO dao;

	public HospitalDischargeModule(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	public void getHtmlData(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String empiId = (String) req.get("empiId");
		String ZYH = (Integer) req.get("ZYH") + "";
		String JGID = (String) req.get("JGID");
		String hql = "select a.SFZH as SFZH,b.JZKH as JZKH,a.ZYHM as ZYHM,"
				+ "a.YBKH as YBKH,b.MZHM as MZHM ,a.BRKS as BRKS,a.BRCH as BRCH, "
				+ "a.RYRQ as RYRQ,a.CYRQ as CYRQ,a.BRXZ as BRXZ "
				+ "from ZY_BRRY a,MS_BRDA b where a.BRID=b.BRID "
				+ " and b.empiId=:empiId and a.JGID=:JGID and a.ZYH=:ZYH";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("JGID", JGID);
		parameters.put("ZYH", ZYH);
		try {
			List<Map<String, Object>> records = dao.doSqlQuery(hql, parameters);
			parameters.remove("empiId");
			parameters.remove("ZYH");
			parameters.put("ZYH", Long.parseLong(ZYH));
			Map<String, Object> FYXX = dao
					.doLoad("SELECT sum(a.ZJJE) as ZJJE FROM ZY_FYMX a WHERE a.ZYH = :ZYH  and a.JGID = :JGID",
							parameters);
			Map<String, Object> body = new HashMap<String, Object>();
			if (FYXX.get("ZJJE") != null) {
				body.put("FYZE", FYXX.get("ZJJE"));
			} else {
				body.put("FYZE", 0);
			}
			Map<String, Object> ZY_TBKK = dao
					.doLoad("SELECT sum(JKJE) as JKHJ FROM ZY_TBKK WHERE ZYH  = :ZYH AND ZFPB = 0 and JGID = :JGID",
							parameters);
			if (ZY_TBKK.get("JKHJ") != null) {
				body.put("JKJE", ZY_TBKK.get("JKHJ"));
			}
			if (records != null && records.size() > 0) {
				Map<String, Object> record = records.get(0);
				if (record.get("SFZH") != null) {
					body.put("SFZH", record.get("SFZH"));
				}
				if (record.get("JZKH") != null) {
					body.put("JZKH", record.get("JZKH"));
				}
				if (record.get("YBKH") != null) {
					body.put("YBKH", record.get("YBKH"));
				}
				if (record.get("MZHM") != null) {
					body.put("MZHM", record.get("MZHM"));
				}
				if (record.get("ZYHM") != null) {
					body.put("ZYHM", record.get("ZYHM"));
				}
				if (record.get("BRKS") != null) {
					body.put(
							"BRKS",
							DictionaryController.instance()
									.get("phis.dictionary.department")
									.getText(record.get("BRKS") + ""));
				}
				if (record.get("BRCH") != null) {
					body.put("BRCH", record.get("BRCH"));
				}
				if (record.get("BRXZ") != null) {
					body.put(
							"BRXZ",
							DictionaryController.instance()
									.get("phis.dictionary.patientProperties")
									.getText(record.get("BRXZ") + ""));
				}
				if (record.get("RYRQ") != null) {
					body.put("RYRQ", record.get("RYRQ"));
					if (record.get("CYRQ") != null) {
						body.put("CYRQ", record.get("CYRQ"));
						int RYTS = BSCHISUtil.getPeriod(
								(Date) record.get("RYRQ"),
								(Date) record.get("CYRQ"));
						body.put("RYTS", RYTS+1);
					} else {
						int RYTS = BSCHISUtil.getPeriod(
								(Date) record.get("RYRQ"), null);
						body.put("RYTS", RYTS+1);
					}
				}
				body.put("ZYH", ZYH);
			}
			res.put("body", body);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取病人信息失败！", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取病人信息失败！", e);
		}
	}

	public void getHtmlDataMZ(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String empiId = (String) req.get("empiId");
		String hql = "select a.SFZH as SFZH,a.JZKH as JZKH,"
				+ "a.YBKH as YBKH,a.MZHM as MZHM ,a.FYZH as FYZH ,a.DWMC as DWMC,a.DWDH as DWDH, "
				+ "a.DWYB as DWYB,a.LXGX as LXGX,a.LXRM as LXRM,a.LXDH as LXDH,a.LXDZ as LXDZ "
				+ "from MS_BRDA a where a.empiId=:empiId ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			List<Map<String, Object>> records = dao.doSqlQuery(hql, parameters);
			res.put("body", records);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取病人信息失败！", e);
		}
	}

	public void doHospitalCostDetalsQueryYZGS(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ExpException {
		@SuppressWarnings("unchecked")
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameter = new HashMap<String, Object>();
		String sql = "select  min(a.FYRQ) as KSRQ, max(a.FYRQ) as ZZRQ, a.YPLX as YPLX, a.FYXH as FYXH, a.FYMC as FYMC,sum(a.FYSL) as FYSL,a.FYDJ as FYDJ,sum(a.ZJJE) as ZJJE,sum(a.ZFJE) as ZFJE,sum(a.ZLJE) as ZLJE,a.ZFBL as ZFBL,a.FYKS as FYKS "
				+ "from ZY_FYMX a,ZY_BQYZ b  where b.jlxh=a.yzxh ";
		if (cnd != null) {
			if (cnd.size() > 0) {
				String where = ExpressionProcessor.instance().toString(cnd);
				sql += " and " + where;
			}
		}
		sql += " group by a.YPLX,a.FYXH,a.FYMC,a.FYDJ,a.ZFBL,a.FYKS "
				+ "order by KSRQ desc,a.YPLX, a.FYXH ,FYSL,a.FYDJ,a.ZFBL,a.FYKS desc";

		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameter);
			for (int i = 0; i < list.size(); i++) {
				String KSMC = DictionaryController.instance()
						.get("phis.dictionary.department")
						.getText(list.get(i).get("FYKS") + "");
				list.get(i).put("FYKS_text", KSMC);
				list.get(i).put(
						"FYSL",
						list.get(i).get("FYSL") != null ? Double
								.parseDouble(list.get(i).get("FYSL") + "") : 0);
				list.get(i).put(
						"FYDJ",
						list.get(i).get("FYDJ") != null ? Double
								.parseDouble(list.get(i).get("FYDJ") + "") : 0);
				list.get(i).put(
						"ZJJE",
						list.get(i).get("ZJJE") != null ? Double
								.parseDouble(list.get(i).get("ZJJE") + "") : 0);
				list.get(i).put(
						"ZFBL",
						list.get(i).get("ZFBL") != null ? Double
								.parseDouble(list.get(i).get("ZFBL") + "") : 0);
			}
			res.put("totalCount", list.size());
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	public void doHospitalCostDetalsQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ExpException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameter = new HashMap<String, Object>();
		String sql = "select  a.ZYH as ZYH, a.FYRQ as FYRQ, a.YPLX as YPLX, a.FYXH as FYXH, a.FYMC as FYMC,a.FYSL as FYSL,a.FYDJ as FYDJ,a.ZJJE as ZJJE,a.ZFJE as ZFJE,a.ZLJE as ZLJE,a.ZFBL as ZFBL,a.FYKS as FYKS,a.SRGH as SRGH "
				+ "from ZY_FYMX a,ZY_BQYZ b  where b.jlxh=a.yzxh ";
		if (cnd != null) {
			if (cnd.size() > 0) {
				String where = ExpressionProcessor.instance().toString(cnd);
				sql += " and " + where;
			}
		}
		sql += " order by a.FYRQ desc,a.YPLX, a.FYXH ,a.FYSL,a.FYDJ,a.ZFBL,a.FYKS desc";

		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameter);
			for (int i = 0; i < list.size(); i++) {
				String KSMC = DictionaryController.instance()
						.get("phis.dictionary.department")
						.getText(list.get(i).get("FYKS") + "");
				list.get(i).put("FYKS_text", KSMC);
				list.get(i).put(
						"FYSL",
						list.get(i).get("FYSL") != null ? Double
								.parseDouble(list.get(i).get("FYSL") + "") : 0);
				list.get(i).put(
						"FYDJ",
						list.get(i).get("FYDJ") != null ? Double
								.parseDouble(list.get(i).get("FYDJ") + "") : 0);
				list.get(i).put(
						"ZJJE",
						list.get(i).get("ZJJE") != null ? Double
								.parseDouble(list.get(i).get("ZJJE") + "") : 0);
				list.get(i).put(
						"ZFBL",
						list.get(i).get("ZFBL") != null ? Double
								.parseDouble(list.get(i).get("ZFBL") + "") : 0);
			}
			list=SchemaUtil.setDictionaryMessageForList(list, "phis.application.hos.schemas.ZY_FYMX_FYQD");
			res.put("totalCount", list.size());
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	public void getSkinTestHistroy(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ExpException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameter = new HashMap<String, Object>();
		String sql = "select  distinct a.YPBH as YPBH, b.YPMC as YPMC "
				+ " from YS_MZ_PSJL a,YK_TYPK b,MS_BRDA c  where a.YPBH=b.YPXH "
				+ " and a.BRBH=c.BRID ";
		if (cnd != null) {
			if (cnd.size() > 0) {
				String where = ExpressionProcessor.instance().toString(cnd);
				sql += " and " + where;
			}
		}
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameter);
			res.put("totalCount", list.size());
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void queryList(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String schema = (String) req.get("schema");
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		try {
			List<Map<String, Object>> records = dao.doList(queryCnd, sortInfo,
					schema);
			records = SchemaUtil.setDictionaryMessageForList(records, schema);
			List<Integer> removeR=new ArrayList<Integer>();
			if (schema.contains("MS_CF02")) {
				for (int i = 0; i < records.size(); i++) {
					if (records.get(i).get("FPHM") != null) {
						records.get(i).put("SFSF", "已收费");
					} else{
						records.get(i).put("SFSF", "未收费");
					}
				}
			} else if (schema.contains("MS_YJ02")) {
				String sql1 = "select ID as ZXKS from SYS_OFFICE where MEDICALLAB='1'";
				List<Map<String, Object>> list = dao.doSqlQuery(sql1, null);
				List<String> ksList = new ArrayList<String>();
				for (Map<String, Object> map : list) {
					ksList.add(map.get("ZXKS") + "");
				}
				for (int i = 0; i < records.size(); i++) {
					if ("1".equals(records.get(i).get("ZXPB") + "")) {
						records.get(i).put("SFZX", "已执行");
					} else if (!ksList.contains(records.get(i).get("ZXKS"))
							&& records.get(i).get("FPHM") != null) {
						records.get(i).put("SFZX", "已执行");
					} else {
						records.get(i).put("SFZX", "未执行");
					}
				}
			}
			res.put("totalCount", records.size());
			res.put("body", records);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

}
