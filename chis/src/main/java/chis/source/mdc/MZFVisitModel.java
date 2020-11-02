/**
 * @(#)VisitMZFModel.java Created on 2012-1-17 上午9:58:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.S;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MZFVisitModel extends MDCBaseModel {

	public MZFVisitModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 依据慢阻肺随访主键值取取询问记录
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getVisitMZFByPkey(String pkey)
			throws ModelDataOperationException {
		try {
			Map<String, Object> data = dao
					.doLoad(MZF_VisitRecord, pkey);
			data = SchemaUtil.setDictionaryMessageForForm(data,
					MZF_VisitRecord);
			return data;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取慢阻肺询问信息失败！", e);
		}
	}

	/**
	 * 查询当天记录
	 * 
	 * @param phrId
	 * @param inquireDate
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ParseException 
	 */
	public long CheckHasCurInquireRecord(String phrId, String SFRQ)
			throws ModelDataOperationException, ParseException {
		String hql = new StringBuffer("select count(*) as ct from ")
				.append(MZF_VisitRecord)
				.append(" where SFRQ=:SFRQ and phrId=:phrId ")
				.toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(SFRQ);
		paramMap.put("SFRQ", date);
		paramMap.put("phrId", phrId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "当天随访记录失败！", e);
		}
		return rsMap == null ? 0 : (Long) rsMap.get("ct");
	}

	/**
	 * 保存慢阻肺询问记录
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveVisitMZFInfo(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MZF_VisitRecord, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存慢阻肺询问记录数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存慢阻肺询问记录失败！", e);
		}
		return rsMap;
	}
	
	public Map<String, Object> listMZFVisitPlanQC(Map<String, Object> req)
			throws ModelDataOperationException {
		List<?> queryCnd = null;
		if (req.get("cnd") instanceof List) {
			queryCnd = (List<?>) req.get("cnd");
		} else if (req.get("cnd") instanceof String) {
			try {
				queryCnd = CNDHelper.toListCnd((String) req.get("cnd"));
			} catch (ExpException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
			}
		}
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		// 组装SQL
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(
					"chis.application.mzf.schemas.MZF_VisitRecord");
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.isVirtual()) {
				continue;
			}
			if (si.hasProperty("refAlias")) {
				String ref = (String) si.getProperty("ref");
				sfBuffer.append(",").append(ref).append(" as ")
						.append(si.getProperty("refItemId"));
			} else {
				String f = si.getId();
				sfBuffer.append(",a.").append(f).append(" as ").append(f);
			}
		}
		String where = "";
		try {
			where = ExpressionProcessor.instance().toString(queryCnd);
		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式转SQL失败", e);
		}
		StringBuffer countSQL = new StringBuffer("select count(distinct(empiid)) as totalCount from MZF_VISITRECORD a");
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where)
			.append(" and SFFS in ('1', '4', '5')");
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> thrvpcList = null;
		try {
			thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询慢阻肺随访统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (thrvpcList != null && thrvpcList.size() > 0) {
			Map<String, Object> trMap = thrvpcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select VISITID as VISITID, a.EMPIID as EMPIID,"+
						"a.PHRID as PHRID,SFRQ as SFRQ, SFFS as SFFS,ZZ as ZZ, QTZZ as QTZZ,SG as SG, DQTZ as DQTZ,"+
						"MBTZ as MBTZ, SSY as SSY, SZY as SZY, XL as XL, HXPL as HXPL, QTTZ as QTTZ, QTQTTZ as QTQTTZ,"+
						"NNJXJZCS as NNJXJZCS, NNYJXJZZYCS as NNYJXJZZYCS, DQRXYL as DQRXYL, MBRXYL as MBRXYL,"+
						"DQYDZ as DQYDZ, MBYDZ as MBYDZ, DQYDC as DQYDC, MBYDC as MBYDC, XLTZ as XLTZ, ZYXW as ZYXW,"+
						"FEV1 as FEV1, FEV1ANDFVC as FEV1ANDFVC, FEV1ANDYJZ as FEV1ANDYJZ, SP02 as SP02, XHDB as XHDB,"+
						"HXB as HXB, BXB as BXB, XXB as XXB, LBXB as LBXB, ZXLXB as ZXLXB, SSXXB as SSXXB, XXXPBG as XXXPBG,"+
						"XDTBG as XDTBG, CATPGDF as CATPGDF, MMRCDF as MMRCDF, QLSXGOLDFJ as QLSXGOLDFJ, GOLDZHPGFZ as GOLDZHPGFZ,"+
						"MZFGLDJ as MZFGLDJ, FYYCX as FYYCX, YWBLFY as YWBLFY, XRYWZZSY as XRYWZZSY, XJZZZD as XJZZZD,"+
						"FKFZL as FKFZL, YL as YL, WCHXJZL as WCHXJZL, YMJZ as YMJZ, JKJYANDZD as JKJYANDZD, YWMC1 as YWMC1,"+
						"YFYL1 as YFYL1, YWMC2 as YWMC2, YFYL2 as YFYL2, YWMC3 as YWMC3, YFYL3 as YFYL3, YY as YY,"+
						"JGJKB as JGJKB, XCSFRQ as XCSFRQ, SFYSQM as SFYSQM, a.createUser as createUser, a.createUnit as createUnit,"+
						"a.createDate as createDate, a.lastModifyUser as lastModifyUser, a.lastModifyUnit as lastModifyUnit, b.personName as PERSONNAME,"+ 
						"b.sexCode as SEXCODE, b.birthday as BIRTHDAY, b.idCard as IDCARD,b.mobileNumber as MOBILENUMBER,"+
						"a.lastModifyDate as lastModifyDate from MZF_VISITRECORD a LEFT JOIN MPI_DemographicInfo b on a.empiid = b.empiid "+ 
						"left join EHR_HealthRecord c on c.empiid = a.empiid");
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where)
				.append(" and visitId in (select max(visitid) from MZF_VISITRECORD a group by empiid)")
				.append(" and SFFS in ('1', '4', '5')");
			}else{
				sql.append(" where visitId in (select max(visitid) from MZF_VISITRECORD a group by empiid)");
			}
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询糖尿病随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tpList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tpMap = new HashMap<String, Object>();
					for (SchemaItem si : sc.getItems()) {
						if (si.isVirtual()) {
							continue;
						}
						if (si.hasProperty("refAlias")) {
							String refItemId = (String) si
									.getProperty("refItemId");
							tpMap.put(refItemId,
									rMap.get(refItemId.toUpperCase()));
						} else {
							String f = si.getId();
							tpMap.put(f, rMap.get(f.toUpperCase()));
						}
					}
					if (tpMap.get("birthday") != null) {
						tpMap.put("age", BSCHISUtil.calculateAge(
								(Date) tpMap.get("birthday"), new Date()));
					}
					tpList.add(tpMap);
				}
				res.put("body", SchemaUtil.setDictionaryMessageForList(tpList,
						"chis.application.mzf.schemas.MZF_VisitRecord"));
			} else {
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			res.put("totalCount", 0);
			res.put("body", new ArrayList<Map<String, Object>>());
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;
	}
}
