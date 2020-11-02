package chis.source.inc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class IncompleteRecordModel implements BSCHISEntryNames {
	BaseDAO dao;

	public IncompleteRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> queryPersonInfoByIdCard(String idCard)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "idCard", "s", idCard);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					MPI_DemographicInfo);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据身份证号查询个人基本信息失败。");
		}
	}

	public Map<String, Object> queryPersonInfoByHealthNo(String healthNo)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select a.empiId as empiId, a.personName as personName,a.idCard as idCard,a.sexCode as sexCode,a.birthday as birthday,a.mobileNumber as mobileNumber,a.address as address from ")
				.append(MPI_DemographicInfo)
				.append(" a,")
				.append(MPI_Card)
				.append(" b where a.empiId=b.empiId and b.cardTypeCode='01' and b.cardNo=:cardNo");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cardNo", healthNo);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql.toString(),
					parameters);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
			return null;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据健康卡号查询个人基本信息失败。");
		}
	}

	public String getHealthNoByEmpiId(String empiId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select a.cardNo as cardNo from ")
				.append(MPI_Card).append(
						" a where a.empiId=:empiId and a.cardTypeCode='01'");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql.toString(),
					parameters);
			if (list != null && list.size() > 0) {
				Map<String, Object> map = list.get(0);
				return (String) map.get("cardNo");
			}
			return null;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据健康卡号查询个人基本信息失败。");
		}
	}

	public Map<String, Object> getIncompleteRecordByPkey(String recordId)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(INC_IncompleteRecord, recordId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据健主键查询接诊记录失败。");
		}
	}

	@SuppressWarnings("deprecation")
	public List<Map<String, Object>> getYLData(String empiId)
			throws ModelDataOperationException {
		List<Map<String, Object>> resBody = new ArrayList<Map<String, Object>>();
		try {
			String userId = UserRoleToken.getCurrent().getUserId();
			String hql = "select a.JZXH as JZXH, a.ZSXX as ZSXX,a.XBS as XBS,a.JWS as JWS,a.TGJC as TGJC,a.FZJC as FZJC,a.T as T,a.P as P,a.R as R,a.SSY as SSY,a.SZY as SZY,a.KS as KS,a.YT as YT,a.HXKN as HXKN,a.OT as OT,a.FT as FT,a.FX as FX,a.PZ as PZ,a.QT as QT from MS_BCJL a,MS_BRDA b "
					+ "where a.BRID=b.BRID and b.EMPIID=:empiId and a.JZYS=:doctor";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("empiId", empiId);
			parameters.put("doctor", userId);
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);

			if (list == null || list.size() == 0) {
				return null;
			}
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> resRecord = new HashMap<String, Object>();
				Map<String, Object> record = list.get(i);
				long JZXH = Long.parseLong(record.get("JZXH") + "");
				resRecord.put("serialNumber", JZXH + "");
				String subjectivityData = "";
				if (record.get("ZSXX") != null) {
					subjectivityData += "主诉:" + record.get("ZSXX") + "   ";
				}
				if (record.get("XBS") != null) {
					subjectivityData += "现病史:" + record.get("XBS") + "   ";
				}
				if (record.get("JWS") != null) {
					subjectivityData += "既往史:" + record.get("JWS");
				}
				resRecord.put("subjectivityData", subjectivityData);
				String[] LCBX = { "KS", "YT", "HXKN", "OT", "FT", "FX", "PZ",
						"QT" };
				String[] LCBX_ZW = { "咳嗽", "咽痛", "呼吸困难", "呕吐", "腹痛", "腹泻",
						"皮疹", "其他" };
				String LCBX_text = "";
				for (int j = 0; j < LCBX.length; j++) {
					if (record.get(LCBX[j]) != null
							&& !"".equals(record.get(LCBX[j]))) {
						String b = record.get(LCBX[j]) + "";
						if ("1".equals(b)) {
							if (LCBX_text.length() > 0) {
								LCBX_text = LCBX_text + ",";
							}
							LCBX_text = LCBX_text + LCBX_ZW[j];
						}
					}
				}
				String ImpersonalityData = "";
				if (record.get("T") != null && !"".equals(record.get("T") + "")) {
					ImpersonalityData += "T:" + record.get("T") + "℃  ";
				}
				if (record.get("P") != null && !"".equals(record.get("P") + "")) {
					ImpersonalityData += "P:" + record.get("P") + "次/分  ";
				}
				if (record.get("R") != null && !"".equals(record.get("R") + "")) {
					ImpersonalityData += "R:" + record.get("R") + "次/分 ";
				}
				if (record.get("SSY") != null
						&& !"".equals(record.get("SSY") + "")
						&& record.get("SZY") != null
						&& !"".equals(record.get("SZY") + "")) {
					ImpersonalityData += "BP:" + record.get("SSY") + "/"
							+ record.get("SZY")+"mmHg";
				}
				if (LCBX_text.length() > 0) {
					ImpersonalityData += "  临床表现:" + LCBX_text;
				}
				if (record.get("TGJC") != null
						&& !"".equals(record.get("TGJC") + "")) {
					ImpersonalityData += "  体格检查:" + record.get("TGJC");
				}
				if (record.get("FZJC") != null
						&& !"".equals(record.get("FZJC") + "")) {
					ImpersonalityData += "  辅助检查:" + record.get("FZJC");
				}
				resRecord.put("ImpersonalityData", ImpersonalityData.trim());
				StringBuffer hql2 = new StringBuffer(
						"select ZDMC as ZDMC from ");
				hql2.append(" MS_BRZD where JZXH=:JZXH order by PLXH ");
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("JZXH", JZXH);
				List<Map<String, Object>> ms_brzd = dao.doQuery(
						hql2.toString(), parameters2);
				String assessment = "";
				for (int j = 0; j < ms_brzd.size(); j++) {
					Map<String, Object> r = ms_brzd.get(j);
					String ZDMC = (String) r.get("ZDMC");
					if (assessment.length() > 0) {
						assessment = assessment + ",";
					}
					assessment = assessment + ZDMC;
				}
				resRecord.put("assessment", assessment);
				Map<String, Object> parameters3 = new HashMap<String, Object>();
				parameters3.put("JZXH", JZXH);
				List<Map<String, Object>> cfsbList = dao
						.doQuery(
								"select CFSB as CFSB from MS_CF01 where JZXH=:JZXH and ZFPB<>1 ORDER BY CFLX,CFHM",
								parameters3);
				String cfsbs = "";
				for (Map<String, Object> cfsb : cfsbList) {
					if (cfsbs.length() > 0) {
						cfsbs += ",";
					}
					cfsbs += cfsb.get("CFSB");
				}
				String disposePlan = "";
				if (cfsbs.trim().length() > 0) {
					String cnd = "['in',['$','a.CFSB'],[" + cfsbs + "]]";
					List<Map<String, Object>> measures = queryCfmx(CNDHelper
							.toListCnd(cnd));
					if (measures != null && measures.size() > 0) {
						for (int j = 0; j < measures.size(); j++) {
							Map<String, Object> measure = measures.get(j);
							if (disposePlan.length() > 0) {
								disposePlan += "; ";
							}
							if (measure.get("YPMC") != null) {
								disposePlan += measure.get("YPMC");
								if (measure.get("YPYF") != null) {
									disposePlan += "  " + measure.get("YPYF");
								}
								if (measure.get("YPZS") != null) {
									disposePlan += "  " + measure.get("YPZS");
								}
								if (measure.get("YPSL") != null) {
									disposePlan += "  " + measure.get("YPSL");
								}
								if (measure.get("YFDW") != null) {
									disposePlan += "  " + measure.get("YFDW");
								}
								if (measure.get("GYTJ") != null) {
									Dictionary drugMode = DictionaryController
											.instance().getDic(
													"chis.dictionary.drugMode");
									String GYTJ = measure.get("GYTJ")+"";
									disposePlan += "  " + drugMode.getText(GYTJ);
								}

							}
						}
					}
				}
				String cnd = "['eq', ['$', 'c.JZXH'],['d', " + JZXH + "]]";
				List<Map<String, Object>> disposal = dao.doList(
						CNDHelper.toListCnd(cnd), "YJZH ,SBXH", MS_YJ02_CIC);
				for (int j = 0; j < disposal.size(); j++) {
					Map<String, Object> measure = disposal.get(j);
					if (disposePlan.length() > 0) {
						disposePlan += "; ";
					}
					if (measure.get("FYMC") != null) {
						disposePlan += measure.get("FYMC");
						if (measure.get("YLSL") != null) {
							disposePlan += "  " + measure.get("YLSL");
						}
						if (measure.get("FYDW") != null) {
							disposePlan += "  " + measure.get("FYDW");
						}
					}
				}
				resRecord.put("disposePlan", disposePlan.trim());
				List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "wayId", "s",
						JZXH);
				List<Map<String, Object>> adviceses = dao.doList(cnd2,
						"recordId", HER_HealthRecipeRecord_MZ);
				if (adviceses != null && adviceses.size() > 0) {
					Map<String, Object> advicesMap = adviceses.get(0);
					String healthTeach = (String) advicesMap.get("healthTeach");
					if (healthTeach != null && !"".equals(healthTeach)) {
						resRecord.put("advices", healthTeach.trim());
					}
				}
				resBody.add(resRecord);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取医疗诊断数据查询失败", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("获取医疗诊断数据查询失败", e);
		}
		return resBody;
	}

	public List<Map<String, Object>> queryCfmx(List<?> cnd)
			throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		try {
			String where = ExpressionProcessor.instance().toString(cnd);
			StringBuffer hql = new StringBuffer();
			// String sql=HQLHelper.buildQueryHql(cnd, null, "MS_CF02_CF", ctx);
			hql.append(
					"select a.SBXH as SBXH,a.YPXH as YPXH,a.CFSB as CFSB,a.YPZH as YPZH,b.YPMC as YPMC,a.YFGG as YFGG,a.YFBZ as YFBZ,a.CFTS as CFTS,a.YCJL as YCJL,b.JLDW as JLDW,b.YPJL as YPJL,a.YPYF as YPYF,a.MRCS as MRCS,a.YYTS as YYTS,a.YPSL as YPSL,a.YFDW as YFDW,a.GYTJ as GYTJ,a.YPZS as YPZS,a.YPCD as YPCD,a.YPDJ as YPDJ,a.ZFBL as ZFBL,a.BZXX as BZXX,a.HJJE as HJJE,a.PSPB as PSPB,a.PSJG as PSJG,a.FYGB as FYGB,b.KSBZ as KSBZ, b.YCYL as YCYL,b.TYPE as TYPE,b.TSYP as TSYP,b.JBYWBZ as JBYWBZ,a.BZMC as BZMC,a.SFJG as SFJG, a.ZFPB as ZFPB ,c.KPDY as KPDY,a.SYYY,a.YQSY,a.SQYS")
					.append(" from MS_CF02 a left outer join YK_TYPK b on a.YPXH=b.YPXH left outer join ZY_YPYF c on a.GYTJ=c.YPYF where ")
					.append(where).append(" order by a.YPZH,a.SBXH");
			ret = dao.doSqlQuery(hql.toString(), null);
			return ret;
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处方明细查询失败");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处方明细查询失败");
		}
	}

	public long parseLong(Object o) {
		if (o == null || "".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

}
