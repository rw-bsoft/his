package phis.application.cfg.source;

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
import phis.source.utils.SchemaUtil;


import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigDoseFrequencyModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigDoseFrequencyModel.class);

	public ConfigDoseFrequencyModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doSaveCommit(List<Map<String, Object>> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		try {
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> qxkz = (Map<String, Object>) body.get(i);

				if (body.get(i).get("PCMC") == null
						|| body.get(i).get("PCMC") == "") {
					throw new RuntimeException("频次名称不能为空！");
				}
				if (body.get(i).get("MRCS") == null
						|| body.get(i).get("MRCS") == "") {
					throw new RuntimeException("每日次数不能为空！");
				}
				if (body.get(i).get("RLZ") == null
						|| body.get(i).get("RLZ") == "") {
					throw new RuntimeException("日历周不能为空！");
				}
				if (body.get(i).get("ZXZQ") == null
						|| body.get(i).get("ZXZQ") == "") {
					throw new RuntimeException("最小周期不能为空！");
				}
				if (body.get(i).get("ZXSJ") == null
						|| body.get(i).get("ZXSJ") == "") {
					throw new RuntimeException("执行时间不能为空！");
				}
				if (body.get(i).get("RZXZQ") == null
						|| body.get(i).get("RZXZQ") == "") {
					throw new RuntimeException("周期天数不能为空！");
				}

				String PCMC = body.get(i).get("PCMC") + "";
				String ZXSJ = body.get(i).get("ZXSJ") + "";
				String RZXZQ = body.get(i).get("RZXZQ") + "";
				int MRCS = Integer.parseInt(body.get(i).get("MRCS") + "");
				int ZXZQ = Integer.parseInt(body.get(i).get("ZXZQ") + "");
				long RLZ = Long.parseLong(body.get(i).get("RLZ") + "");
				String BZXX = "";
				if (body.get(i).get("BZXX") != null) {
					BZXX = body.get(i).get("BZXX") + "";
				}

				// 检查每日次数（MRCS）中是否包含"-" -1不包含
				int index = ZXSJ.indexOf("-");
				int last = ZXSJ.lastIndexOf("-");
				int num = 1;
				if (index == last && index != -1) {
					num = num + 1;
				} else if (index > 0) {
					for (int j = index; j < last; j++) {
						j = ZXSJ.indexOf("-", j);
						num++;
					}
				}
				if (index == 0 || last == ZXSJ.length() - 1) {
					throw new RuntimeException("频次【" + PCMC + "】的执行时间格式错误！");
				}
				if (MRCS != num) {
					throw new RuntimeException("频次【" + PCMC + "】的每日次数和执行时间不一致！");
				}

				if (body.get(i).get("PCBM") == null
						|| body.get(i).get("PCBM") == "") {
					qxkz.put("PCMC", PCMC);
					qxkz.put("MRCS", MRCS);
					qxkz.put("RLZ", RLZ);
					qxkz.put("ZXZQ", ZXZQ);
					qxkz.put("ZXSJ", ZXSJ);
					qxkz.put("RZXZQ", RZXZQ);
					qxkz.put("BZXX", BZXX);
					dao.doSave("create", BSPHISEntryNames.GY_SYPC, qxkz, false);
				} else {
					String PCBM = body.get(i).get("PCBM") + "";
					dao.doUpdate("update GY_SYPC set PCMC='" + PCMC + "' ,MRCS=" + MRCS
							+ " , RLZ=" + RLZ + " , ZXZQ=" + ZXZQ + " ,ZXSJ='"
							+ ZXSJ + "' ,RZXZQ='" + RZXZQ + "',BZXX='" + BZXX
							+ "' WHERE PCBM='" + PCBM + "' ", null);
				}

			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		}

	}

	public void doListQuery(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
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
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT PCBM as PCBM,PCMC as PCMC,MRCS as MRCS,RLZ as RLZ,ZXZQ as ZXZQ,ZXSJ  as ZXSJ,RZXZQ as RZXZQ,BZXX as BZXX FROM ");
			sql_list.append("GY_SYPC  ");
			sql_list.append(" ORDER BY PCMC ");

			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("GY_SYPC ");
			Sql_count.append(" ORDER BY PCMC ");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			for (int i = 0; i < inofList.size(); i++) {
				String RZXZQ = inofList.get(i).get("RZXZQ").toString();
				int index = RZXZQ.indexOf("1");
				int last = RZXZQ.lastIndexOf("1");
				int num = 0;
				if (index == last && index == -1) {
					num = 0;
				}
				if (index == last && index != -1) {
					num = 1;
				} else {
					if (index >= 0) {
						for (int j = index; j <= last; j++) {
							j = RZXZQ.indexOf("1", j);
							num++;
						}
					}
				}
				inofList.get(i).put("RZXZQ2", num);
			}
			SchemaUtil.setDictionaryMassageForList(inofList, "phis.application.cfg.schemas.GY_SYPC");
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the Frequency method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "频次列表查询失败！");
		}
	}

	public void doMxquery(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String PCBM = req.get("body") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("PCBM", PCBM);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT PCBM as PCBM,PCMC as PCMC,MRCS as MRCS,RLZ as RLZ,ZXZQ as ZXZQ,ZXSJ  as ZXSJ,RZXZQ as RZXZQ FROM ");
			sql_list.append("GY_SYPC  ");
			sql_list.append("WHERE PCBM =:PCBM");
			sql_list.append(" ORDER BY PCMC ");

			List<Map<String, Object>> inofList = dao.doQuery(
					sql_list.toString(), parameters);
			String RZXZQ = "0";
			if (inofList.get(0).get("RZXZQ") != null) {
				RZXZQ = inofList.get(0).get("RZXZQ") + "";
			}
			res.put("body", RZXZQ);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the Frequency method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "频次列表查询失败！");
		}
	}

}
