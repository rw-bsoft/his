/**
 * @(#)CVDServiceModel.java Created on 2013-3-18 上午09:47:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.cvd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.CLOB;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.pub.PublicModel;
import chis.source.util.CNDHelper;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class CVDServiceModel extends PublicModel {

	public CVDServiceModel(BaseDAO dao) {
		super(dao);
	}

	public Map<String, Object> initCvdAssessRegister(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			String hql = "select familyAddr as familyAddr from EHR_FamilyRecord where regionCode = (select regionCode as regionCode from  EHR_HealthRecord   where empiId =:empiId )";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("empiId", empiId);
			Map<String, Object> r = dao.doLoad(hql, parameters);
			if (r != null && r.size() != 0) {
				res.put("homeAddress", r.get("familyAddr"));
			}
			hql = "select smokeFreqCode as smokeFreqCode from EHR_LifeStyle where empiId =:empiId";
			r = dao.doLoad(hql, parameters);
			if (r != null && r.size() != 0) {
				String smokeFreqCode = (String) r.get("smokeFreqCode");
				if (smokeFreqCode.equals("4")) {
					res.put("smoke", "n");
				} else {
					res.put("smoke", "y");
				}
			}
			String hql2 = "select a.weight as weight,a.visitDate as visitDate,a.constriction as constriction,a.diastolic as diastolic  From "
					+ MDC_HypertensionVisit
					+ " a where a.empiId=:empiId order by visitDate desc";
			List<?> hypervisitList = dao.doQuery(hql2, parameters);

			String hql3 = "select a.weight as weight,a.visitDate as visitDate,a.fbs as fbs,a.pbs as pbs from "
					+ MDC_DiabetesVisit
					+ " a where a.empiId=:empiId order by visitDate desc";
			List<?> diabetesVisitList = dao.doQuery(hql3, parameters);

			Date hyVisitDate = null;
			Double hyHeight = null;
			if (hypervisitList.size() > 0) {
				Map<?, ?> m = (HashMap<?, ?>) hypervisitList.get(0);
				hyHeight = (Double) m.get("weight");
				res.put("constriction", (Integer) m.get("constriction"));
				res.put("diastolic", (Integer) m.get("diastolic"));
				hyVisitDate = (Date) m.get("visitDate");
			}

			Date diabetesVisitDate = null;
			Double diabetesHeight = null;
			if (diabetesVisitList.size() > 0) {
				Map<?, ?> m = (HashMap<?, ?>) diabetesVisitList.get(0);
				diabetesHeight = (Double) m.get("weight");
				diabetesVisitDate = (Date) m.get("visitDate");
				res.put("fbs", (Double) m.get("fbs"));
				res.put("pbs", (Double) m.get("pbs"));
			}
			if (diabetesVisitList.size() > 0 && hypervisitList.size() > 0) {
				if (hyVisitDate.before(diabetesVisitDate)) {
					res.put("weight", diabetesHeight);
				} else {
					res.put("weight", hyHeight);
				}
			} else {
				if (hypervisitList.size() > 0) {
					res.put("weight", hyHeight);
				} else {
					if (diabetesVisitList.size() > 0) {
						res.put("weight", diabetesHeight);
					}
				}
			}

			hql2 = "select a.height as height,a.weight as weight,a.constriction as constriction,a.diastolic as diastolic,a.createDate as createDate from "
					+ MDC_HypertensionRecord + " a where a.empiId=:empiId";
			List<?> hyperRecordList = dao.doQuery(hql2, parameters);

			hql3 = "select a.height as height,a.weight as weight,a.fbs as fbs,a.pbs as pbs,a.createDate as createDate from "
					+ MDC_DiabetesRecord + " a where a.empiId=:empiId";
			List<?> diabetesRecordList = dao.doQuery(hql3, parameters);

			Date hyRecordDate = null;
			Double hyRecordHeight = null;
			Double hyRecordWeight = null;
			if (hyperRecordList.size() > 0) {
				Map<?, ?> m = (HashMap<?, ?>) hyperRecordList.get(0);
				hyRecordDate = (Date) m.get("createDate");
				hyRecordHeight = (Double) m.get("height");
				hyRecordWeight = (Double) m.get("weight");
				if (res.get("constriction") == null) {
					res.put("constriction", (Integer) m.get("constriction"));
				}
				if (res.get("diastolic") == null) {
					res.put("diastolic", (Integer) m.get("diastolic"));
				}

				res.put("hypertension", "y");
			} else {
				res.put("hypertension", "n");
			}

			Date diabetesRecordDate = null;
			Double diabetesRecordHeight = null;
			Double diabetesRecordWeight = null;
			if (diabetesRecordList.size() > 0) {
				Map<?, ?> m = (HashMap<?, ?>) diabetesRecordList.get(0);
				diabetesRecordDate = (Date) m.get("createDate");
				diabetesRecordHeight = (Double) m.get("height");
				diabetesRecordWeight = (Double) m.get("weight");
				if (res.get("fbs") == null) {
					res.put("fbs", (Double) m.get("fbs"));
				}
				if (res.get("pbs") == null) {
					res.put("pbs", (Double) m.get("pbs"));
				}
				res.put("diabetes", "y");
			} else {
				res.put("diabetes", "n");
			}

			if (res.get("weight") == null) {
				if (hyperRecordList.size() > 0 && diabetesRecordList.size() > 0) {
					if (hyRecordDate.before(diabetesRecordDate)) {
						res.put("weight", diabetesRecordWeight);
					} else {
						res.put("weight", hyRecordWeight);
					}
				} else {
					if (hyperRecordList.size() > 0) {
						res.put("weight", hyRecordWeight);
					} else {
						if (diabetesRecordList.size() > 0) {
							res.put("weight", diabetesRecordWeight);
						}
					}
				}
			}

			if (hyperRecordList.size() > 0 && diabetesRecordList.size() > 0) {
				if (hyRecordDate.before(diabetesRecordDate)) {
					res.put("height", diabetesRecordHeight);
				} else {
					res.put("height", hyRecordHeight);
				}
			} else {
				if (hyperRecordList.size() > 0) {
					res.put("height", hyRecordHeight);
				} else {
					if (diabetesRecordList.size() > 0) {
						res.put("height", diabetesRecordHeight);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("initCvdAssessRegister失败.");
		}
		return res;
	}
	
	
	public Map<String, Object> initDiseaseRegistration(Map<String, Object> req)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			 String empiId = (String) req.get("empiId");
			 String recordId = (String) req.get("recordId");
			 String entryName = (String) req.get("schema");
			 res = dao.doLoad(entryName, recordId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("initCvdAssessRegister失败.");
		}
		return res;
	}
	
	public Map<String, Object> initDiseaseVerification(Map<String, Object> req)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			 String empiId = (String) req.get("empiId");
			 String recordId = (String) req.get("recordId");
			 String entryName = (String) req.get("schema");
			 List<?> cnd = CNDHelper.createSimpleCnd("eq", "precordId", "s",recordId);
			 res = dao.doLoad(cnd, entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("initDiseaseVerification失败.");
		}
		return res;
	}

	public List<Map<String, Object>> getModifyRecord()
			throws ModelDataOperationException, ExpException {
		String exp = "['isNull',['s','is'],['$','riskpredictionresult']]";
		try {
			List<?> cnd = CNDHelper.toListCnd(exp);
			return dao.doList(cnd, null, CVD_Appraisal);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"查询riskpredictionresult为空数据失败.");
		}
	}

	public List<Map<String, Object>> getAssessRegisterByInquireId(
			String inquireId) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "inquireId", "s",
				inquireId);
		try {
			return dao.doList(cnd, null, CVD_AssessRegister);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据inquireId查询数据失败.");
		}
	}

	public Map<String, Object> saveAssessRegisterRecord(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, CVD_AssessRegister, body, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存心血管数据失败.");
		}
	}
	
	public Map<String, Object> saveDeseaseVerificationRecord(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		try {
			Map param=new HashMap();
			param.put("recordId", body.get("precordId"));
			dao.doUpdate("update CVD_DiseaseManagement set hszt=2 where recordId=:recordId",param);
			return dao.doSave(op, CVD_DiseaseVerification, body, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存心脑血管核实数据失败.");
		}
	}
	
	public void deleteDiseaseRegister(String pkey)
			throws ValidateException, ModelDataOperationException {
		try {
			dao.doRemove(pkey,CVD_DiseaseManagement);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除心血管数据失败");
		}
	}
	
	public Map<String, Object> saveDeseaseRegisterRecord(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, CVD_DiseaseManagement, body, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存心脑血管数据失败.");
		}
	}
	
	

	public void updateAssessRegisterAnswer(String inquireId, String answer)
			throws ValidateException, ModelDataOperationException {
		StringBuilder sb = new StringBuilder("update ").append(
				"CVD_AssessRegister").append(
				" set answer =:answer where inquireId = :inquireId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("inquireId", inquireId);
		parameters.put("answer", answer.toString());
		try {
			dao.doUpdate(sb.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("修改心血管测试结果失败.");
		}
	}

	public void deleteAssessRegisterRecord(String inquireId)
			throws ValidateException, ModelDataOperationException {
		try {
			String sql = "update " + CVD_AssessRegister
					+ " set isDelete='1' where inquireId=:inquireId";
			Map<String, Object> parameters=new HashMap<String, Object>();
			parameters.put("inquireId", inquireId);
			dao.doUpdate(sql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除心血管数据失败");
		}
	}

	public List<Map<String, Object>> getSuggestionRecordByExp(
			String lifeStyleExp) throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.toListCnd(lifeStyleExp);
			List<Map<String, Object>> body = dao.doList(cnd, null,
					CVD_Suggestion);
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> m = body.get(i);
				if ("<CLOB>".equals(m.get("content"))) {
					m.put("content", "");
					result.add(m);
					continue;
				}
				if (m.get("content") != null) {
					String content = ((String) m.get("content")).replace(
							"<clob></clob>", "");
					m.put("content", content);
				}
				result.add(m);
			}
			return result;
		} catch (Exception e) {
			throw new ModelDataOperationException("获取建议数据失败.");
		}
	}

	public List<Map<String, Object>> getRecordByInfo(
			Map<String, Object> parameters) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select riskRatio as riskRatio from ")
				.append(CVD_RiskAssessReason).append(" where tc=:tc")
				.append(" and sexCode=:sexCode")
				.append(" and diabetes=:diabetes").append(" and smoke=:smoke")
				.append(" and ageGroup=:ageGroup").append(" and bp=:bp");
		try {
			return dao.doQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据条件查询危险评估因素数据失败.");
		}
	}

	public List<Map<String, Object>> getAppraisalByInquireId(String inquireId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "inquireId", "s",
				inquireId);
		try {
			return dao.doList(cnd, null, CVD_Appraisal);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据inquireId查询评价结果数据失败.");
		}
	}

	public Map<String, Object> saveAppraisalData(String op,
			Map<String, Object> appraisalBody) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, CVD_Appraisal, appraisalBody, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存查询评价结果数据失败.");
		}

	}

	public void deleteAppraisalData(String inquireId) throws ValidateException,
			ModelDataOperationException {
		try {
			dao.doRemove("inquireId", inquireId, CVD_Appraisal);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除心血管查询评价结果数据失败");
		}
	}

	public Map<String, Object> saveCVDTest(String op, Map<String, Object> body)
			throws ValidateException, ModelDataOperationException {
		try {
			return dao.doSave(op, CVD_Test, body, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存心血管疾病知识测验失败");
		}
	}

	public void deleteCVDTest(String inquireId) throws ValidateException,
			ModelDataOperationException {
		try {
			dao.doRemove("inquireId", inquireId, CVD_Test);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除心血管疾病知识测验失败");
		}
	}

	public void updateAssessRegisterData() throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(CVD_AssessRegister).append(" a set a.empiId =")
				.append(" (select empiId from ").append(MPI_DemographicInfo)
				.append(" b where a.idCard = b.idCard")
				.append(" and b.idcard is not null and rownum = 1 ) ")
				.append("where a.empiId is null and a.isImported = '2' ");
		try {
			dao.doUpdate(hql.toString(), new HashMap<String, Object>());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("更新心血管危险因素评估数据失败.");
		}

	}

	public int queryAssessRegisterData() throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select count(*) from ")
				.append(CVD_AssessRegister)
				.append(" t where t.idcard in ")
				.append("(select t.idcard from ")
				.append(CVD_AssessRegister)
				.append(" t group by t.idcard, t.inputdate, t.isImport having count(1) > 1)")
				.append(" and rowid not in ")
				.append("(select min(rowid) from ")
				.append(CVD_AssessRegister)
				.append(" t group by t.idcard, t.inputdate, t.isImport having count(1) > 1)")
				.append(" and t.isimport = '1'");
		try {
			return dao.doUpdate(hql.toString(), new HashMap<String, Object>());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("查询心血管危险因素评估数据失败.");
		}
	}

	public void updateAssessRegisterIsImported()
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(CVD_AssessRegister).append(" a set a.isImported = '1'")
				.append(" where a.isImported = '2'");
		try {
			dao.doUpdate(hql.toString(), new HashMap<String, Object>());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("更新心血管危险因素评估数据失败.");
		}
	}

	public void deleteAssessregisterData() throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("delete from ")
				.append(CVD_AssessRegister)
				.append(" t where t.idcard in ")
				.append("(select t.idcard from ")
				.append(CVD_AssessRegister)
				.append(" t group by t.idcard, t.inputdate, t.isImport having count(1) > 1)")
				.append(" and rowid not in ")
				.append("(select min(rowid) from ")
				.append(CVD_AssessRegister)
				.append(" t group by t.idcard, t.inputdate, t.isImport having count(1) > 1)")
				.append(" and t.isimport = '1'");
		try {
			dao.doUpdate(hql.toString(), new HashMap<String, Object>());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除心血管危险因素评估数据失败.");
		}
	}

}
