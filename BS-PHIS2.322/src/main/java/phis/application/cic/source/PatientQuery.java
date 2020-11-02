package phis.application.cic.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.utils.T;
import ctd.account.UserRoleToken;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

public class PatientQuery extends SimpleQuery {
	/**
	 * 查询病人就诊信息
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Integer jzzt = (Integer) body.get("jzzt");
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		// Integer departmentId = (Integer) body.get("departmentId");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String departmentId = jzzt > 0 ? user.getProperty("biz_departmentId")
				+ "" : user.getProperty("reg_departmentId") + "";
		// String topUint = ParameterUtil.getTopUnitId();
		String GHXQ = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.GHXQ, ctx);
		String XQJSFS = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
		// String YXWGHBRJZ = ParameterUtil.getParameter(manageUnit,
		// BSPHISSystemArgument.YXWGHBRJZ, ctx);
		StringBuffer hql = new StringBuffer();
		try {
			Date now = new Date();
			if ("1".equals(XQJSFS + "")) {
				SimpleDateFormat matter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				try {
					now = matter.parse(BSHISUtil.getDate() + " 23:59:59");
				} catch (ParseException e) {
					throw new ModelDataOperationException("挂号效期转化错误!", e);
				}
			}
			Date regBegin = DateUtils.addDays(now, -Integer.parseInt(GHXQ));
			SimpleDateFormat matter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			StringBuffer jzztSql = new StringBuffer(
					"['eq', ['$', 'a.JZZT'], ['d', " + jzzt + "]]");
			if (jzzt == 2) {
				jzztSql.insert(0, "['or',").append(
						",['eq', ['$', 'a.JZZT'], ['d', 1]]]");
			}
			Date regEnd = matter.parse(BSHISUtil.getDate() + " 23:59:59");
			StringBuffer cnds = new StringBuffer();

			if (jzzt != 9) {
				cnds.append("['and',['and',");
			}
			StringBuffer ksysSql = new StringBuffer(
					"['eq', ['$', 'a.JGID'],['s', ");
			ksysSql.append(manageUnit);
			ksysSql.append("]]");
			// if (!"1".equals(YXWGHBRJZ)) {
			ksysSql.insert(0, "['and',")
					.append(",['eq',['$', 'a.KSDM'],['d', ");
			ksysSql.append(departmentId);
			ksysSql.append("]]]");
			// }
			cnds.append(ksysSql);
			if (jzzt != 9) {
				cnds.append(",['or',['eq',['$','a.YSDM'],['s','");
				cnds.append(user.getUserId());// 获取员工代码暂时用该方法替代
				cnds.append("']],['isNull',['$','a.YSDM']]]]]");
			}
			if (jzzt > 0) {// 暂挂和已诊
				req.put("schema", BSPHISEntryNames.YS_MZ_JZLS);
				StringBuffer zg_sql = new StringBuffer(jzztSql.toString());
				zg_sql = zg_sql.insert(0, "['and',").append(
						",['ge',['$','a.KSSJ'],['todate',['s','"
								+ T.format(regBegin, T.DATETIME_FORMAT)
								+ "'],['s','YYYY-MM-DD HH24:MI:SS']]]]");
				zg_sql = zg_sql.insert(0, "['and',").append(
						",['le',['$','a.KSSJ'],['todate',['s','"
								+ T.format(regEnd, T.DATETIME_FORMAT)
								+ "'],['s','YYYY-MM-DD HH24:MI:SS']]]]");
				/*************add by lizhi 2017-10-24已诊列表显示24小时内的病人（00；00；00--23；59；59）*************/
				if(jzzt == 9){
					zg_sql = zg_sql.insert(0, "['and',").append(
							",['ge',['$','b.GHSJ'],['todate',['s','"
									+ T.format(regBegin, T.DATETIME_FORMAT)
									+ "'],['s','YYYY-MM-DD HH24:MI:SS']]]]");
					zg_sql = zg_sql.insert(0, "['and',").append(
							",['le',['$','b.GHSJ'],['todate',['s','"
									+ T.format(regEnd, T.DATETIME_FORMAT)
									+ "'],['s','YYYY-MM-DD HH24:MI:SS']]]]");
				}
				/*************add by lizhi 2017-10-24已诊列表显示24小时内的病人（00；00；00--23；59；59）*************/				
				if (jzzt == 2) {
					StringBuffer js_sql = new StringBuffer(
							"['ne', ['$', 'a.GHFZ'], ['i', 1]]");
					js_sql = js_sql.insert(0, "['and',").append(
							",['ge',['$','a.FZRQ'],['todate',['s','"
									+ T.format(regBegin, T.DATETIME_FORMAT)
									+ "'],['s','YYYY-MM-DD HH24:MI:SS']]]]");
					js_sql = js_sql.insert(0, "['and',").append(
							",['le',['$','a.FZRQ'],['todate',['s','"
									+ T.format(regEnd, T.DATETIME_FORMAT)
									+ "'],['s','YYYY-MM-DD HH24:MI:SS']]]]");
					zg_sql.insert(0, "['or',").append(
							"," + js_sql.toString() + "]");
				}
				cnds = cnds.insert(0, "['and',").append(
						"," + zg_sql.toString() + "]");
				hql.append("select a.JZZT as JZZT,a.GHXH as SBXH,a.JGID as JGID,b.BRID as BRID,b.JZHM as JZHM,b.PLXH as PLXH,c.EMPIID as EMPIID," +
						" d.CardNo as JZKH,c.MZHM as MZHM,c.BRXM as BRXM,c.BRXB as BRXB," +
						" to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as GHSJ,c.BRXZ as BRXZ,c.CSNY as CSNY,c.SFZH as SFZH,e.signFlag as SIGNFLAG,e.sceEndDate as SCEENDDATE," +
						" (to_char(sysdate,'yyyy')-to_char(c.CSNY,'yyyy'))+1 as HOWOLD ,a.YSDM as YSDM," +
						"case when exists(select EMPIID from EHR_HealthRecord where EMPIID=c.EMPIID) then 1 else 0 end as GRDA," +
						"case when exists(select EMPIID from MDC_HYPERTENSIONRECORD where EMPIID=c.EMPIID) then 1 else 0 end as GXYDA," +
						"case when exists(select EMPIID from MDC_DIABETESRECORD where EMPIID=c.EMPIID) then 1 else 0 end as TNBDA," +
						"case when exists(select g.JBPB as JBPB,g.JBBGK as JBBGK from MS_BRZD f,GY_JBBM g where f.ZDXH=g.JBXH and f.JZXH=a.jzxh and g.JBPB='01') then 1 else 0 end as GXYZD," +
						"case when exists(select g.JBPB as JBPB,g.JBBGK as JBBGK from MS_BRZD f,GY_JBBM g where f.ZDXH=g.JBXH and f.JZXH=a.jzxh and g.JBPB='02') then 1 else 0 end as TNBZD from ");
				hql.append(" YS_MZ_JZLS a,");
				hql.append(" MS_GHMX b,MPI_DEMOGRAPHICINFO e,");
				hql.append(" MS_BRDA c left outer join ");
				hql.append(" MPI_Card d on d.cardTypeCode="+KLX+" and d.empiId = c.EMPIID where b.BRID = c.BRID and a.GHXH = b.SBXH and c.empiId = e.empiId");
			} else {
				cnds = cnds.insert(0, "['and',").append(
						"," + jzztSql.toString() + "]");
				// cnds = cnds.insert(0, "['and',").append(
				// ",['isNull',  ['$', 'a.HZRQ']]]");
				cnds = cnds.insert(0, "['and',").append(
						",['eq',['$','a.THBZ'],['d',0]]]");
				cnds = cnds.insert(0, "['and',").append(
						",['ge',['$','a.GHSJ'],['todate',['s','"
								+ T.format(regBegin, T.DATETIME_FORMAT)
								+ "'],['s','YYYY-MM-DD HH24:MI:SS']]]]");
				cnds = cnds.insert(0, "['and',").append(
						",['le',['$','a.GHSJ'],['todate',['s','"
								+ T.format(regEnd, T.DATETIME_FORMAT)
								+ "'],['s','YYYY-MM-DD HH24:MI:SS']]]]");
				hql.append("select 0 as JZZT,a.SBXH as SBXH,a.JGID as JGID,a.BRID as BRID,a.JZHM as JZHM,a.PLXH as PLXH,c.EMPIID as EMPIID," +
						" d.CardNo as JZKH,c.MZHM as MZHM,c.BRXM as BRXM,c.BRXB as BRXB," +
						" to_char(a.GHSJ,'yyyy-mm-dd hh24:mi:ss') as GHSJ,c.BRXZ as BRXZ,c.CSNY as CSNY,c.SFZH as SFZH,e.signFlag as signFlag,e.sceEndDate as sceEndDate," +
						" (to_char(sysdate,'yyyy')-to_char(c.CSNY,'yyyy'))+1 as HOWOLD," +
						"case when exists(select EMPIID from EHR_HealthRecord where EMPIID=c.EMPIID) then 1 else 0 end as GRDA," +
						"case when exists(select EMPIID from MDC_HYPERTENSIONRECORD where EMPIID=c.EMPIID) then 1 else 0 end as GXYDA," +
						"case when exists(select EMPIID from MDC_DIABETESRECORD where EMPIID=c.EMPIID) then 1 else 0 end as TNBDA,0 as GXYZD,0 as TNBZD from ");
				hql.append(" MS_GHMX a,MPI_DEMOGRAPHICINFO e,");
				hql.append(" MS_BRDA c left outer join ");
				hql.append(" MPI_Card d on d.cardTypeCode="+KLX+" and d.empiId = c.EMPIID where a.BRID = c.BRID and c.empiId = e.empiId ");
			}
			if (req.get("cnd") != null) {
				req.put("cnd",
						CNDHelper.createArrayCnd("and",
								(List<?>) req.get("cnd"),
								CNDHelper.toListCnd(cnds.toString())));
			} else {
				req.put("cnd", CNDHelper.toListCnd(cnds.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.put(RES_CODE, ServiceCode.CODE_REQUEST_PARSE_ERROR);
			res.put(RES_MESSAGE, "获取病人就诊信息错误");
			return;
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		int first = pageNo - 1;
		Map<String, Object> parameters = new HashMap<String, Object>();
		// parameters.put("JGID", manageUnit);

		// StringBuffer hql = new
		// StringBuffer("select a.GHXH as SBXH,a.JGID as JGID,b.BRID as BRID,b.JZHM as JZHM,c.EMPIID as EMPIID,d.CardNo as JZKH,c.MZHM as MZHM,c.BRXM as BRXM,c.BRXB as BRXB,a.KSSJ as GHSJ from ");
		// hql.append(BSPHISEntryNames.YS_MZ_JZLS);
		// hql.append(" a,");
		// hql.append(BSPHISEntryNames.MS_GHMX);
		// hql.append(" b,");
		// hql.append(BSPHISEntryNames.MS_BRDA);
		// hql.append(" c left outer join ");
		// hql.append(BSPHISEntryNames.MPI_Card);
		// hql.append(" d on d.cardTypeCode='04' and d.empiId = c.EMPIID where b.BRID = c.BRID and a.GHXH = b.SBXH");
		try {
			String where = ExpRunner.toString((List<Object>) req.get("cnd"),
					ctx);
			hql.append(" and " + where);
			BaseDAO dao = new BaseDAO();
			int count = dao.doSqlQuery(hql.toString(), parameters).size();
			if (jzzt > 0) {
				if (jzzt == 9) {// 已诊倒序
					hql.append("order by a.KSSJ desc ");
				} else {// 1就诊中 暂挂 2 正序
					hql.append("order by a.KSSJ desc ");
				}
			} else {
//				hql.append("order by a.GHSJ desc ");
				hql.append("order by a.PLXH ");
			}
			List<Map<String, Object>> list;
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			list = dao.doSqlQuery(hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.cic.schemas.MS_GHMX_MZ");
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			res.put("totalCount", count);
			res.put("body", list);
			// if (jzzt > 0) {
			// List<Map<String, Object>> resBody = (List<Map<String, Object>>)
			// res
			// .get("body");
			// for (Map<String, Object> patient : resBody) {
			// patient.put("BRID", patient.get("BRBH"));
			// patient.put("SBXH", patient.get("GHXH"));
			// patient.put("GHSJ", patient.get("KSSJ"));
			// }
			// }
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
