package chis.source.phr;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.FamilyHistory;
import chis.source.dic.MHCPastHistory;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.PersonHistory;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import com.alibaba.fastjson.JSONException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.security.Condition;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class PastHistoryModel implements BSCHISEntryNames {

	private static final Logger logger = LoggerFactory
			.getLogger(PastHistoryModel.class);

	protected BaseDAO dao;

	/**
	 * @param dao
	 */
	public PastHistoryModel(BaseDAO dao) {
		this.dao = dao;
	}

	public String getFamilyPastHistoryByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "pastHisTypeCode", "s",
				PastHistoryCode.FATHER);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "pastHisTypeCode", "s",
				PastHistoryCode.MOTHER);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "pastHisTypeCode", "s",
				PastHistoryCode.BROTHER);
		List<?> cnd4 = CNDHelper.createSimpleCnd("eq", "pastHisTypeCode", "s",
				PastHistoryCode.CHILDREN);
		List<?> temp1 = CNDHelper.createArrayCnd("or", cnd1, cnd2);
		List<?> temp2 = CNDHelper.createArrayCnd("or", temp1, cnd3);
		List<?> temp3 = CNDHelper.createArrayCnd("or", temp2, cnd4);

		List<?> cnd5 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd = CNDHelper.createArrayCnd("and", temp3, cnd5);

		List<Map<String, Object>> list = null;
		List<String> familyHistory = new ArrayList<String>();
		try {
			list = dao.doQuery(cnd, "pastHistoryId", EHR_PastHistory);
			SchemaUtil.setDictionaryMessageForList(list, EHR_PastHistory);

			List<String> fatherList = new ArrayList<String>();
			List<String> motherList = new ArrayList<String>();
			List<String> brotherList = new ArrayList<String>();
			List<String> childrenList = new ArrayList<String>();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> m = list.get(i);
					if (m.get("pastHisTypeCode").equals(PastHistoryCode.FATHER)&&m.get("diseaseText")!=null) {
						fatherList.add((String) m.get("diseaseText"));
					}
					if (m.get("pastHisTypeCode").equals(PastHistoryCode.MOTHER)&&m.get("diseaseText")!=null) {
						motherList.add((String) m.get("diseaseText"));
					}
					if (m.get("pastHisTypeCode")
							.equals(PastHistoryCode.BROTHER)&&m.get("diseaseText")!=null) {
						brotherList.add((String) m.get("diseaseText"));
					}
					if (m.get("pastHisTypeCode").equals(
							PastHistoryCode.CHILDREN)&&m.get("diseaseText")!=null) {
						childrenList.add((String) m.get("diseaseText"));
					}
				}
				if (fatherList.size() > 0) {
					familyHistory.add("父亲：" + BSCHISUtil.join(fatherList, "、"));
				}
				if (motherList.size() > 0) {
					familyHistory.add("母亲：" + BSCHISUtil.join(motherList, "、"));
				}
				if (brotherList.size() > 0) {
					familyHistory
							.add("兄弟：" + BSCHISUtil.join(brotherList, "、"));
				}
				if (childrenList.size() > 0) {
					familyHistory.add("子女："
							+ BSCHISUtil.join(childrenList, "、"));
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get familyPastHistory message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取既往史失败。");
		}
		return BSCHISUtil.join(familyHistory, " ");
	}

	/**
	 * 根据既往史数据记录拼接对应字段的字典数据
	 * 
	 * @param pastHistoryList
	 * @param fieldName
	 * @param OtherFieldName
	 * @param otherCode
	 * @return
	 */
	public Map<String, Object> buildPastHistoryDic(
			List<Map<String, Object>> pastHistoryList, String fieldName,
			String OtherFieldName, String otherCode) {
		StringBuffer key = new StringBuffer();
		StringBuffer text = new StringBuffer();
		StringBuffer other = new StringBuffer();
		Map<String, Object> reMap = new HashMap<String, Object>();
		if (pastHistoryList != null) {
			for (int i = 0; i < pastHistoryList.size(); i++) {
				Map<String, Object> result = (HashMap<String, Object>) pastHistoryList
						.get(i);
				String pastkey = StringUtils.trimToEmpty((String) result
						.get("diseaseCode"));
				String pastvalue = StringUtils.trimToEmpty((String) result
						.get("diseaseText"));
				if (pastkey != null && pastvalue != null)
					if (key.equals(otherCode)) {
						other.append(pastvalue);
						other.append(",");
					} else {
						key.append(pastkey);
						key.append(",");

						text.append(pastvalue);
						text.append(",");
					}
			}
			String valueKey = key.toString();
			String valueText = text.toString();
			String fieldKey = "";
			if (valueKey.length() > 0)
				fieldKey = valueKey.substring(0, valueKey.lastIndexOf(","));
			String fieldValue = "";
			if (valueText.length() > 0) {
				fieldValue = valueText.substring(0, valueText.lastIndexOf(","));
			}
			if (!fieldKey.equals("") && !fieldValue.equals("")) {
				Map<String, Object> field = new HashMap<String, Object>();
				field.put("key", fieldKey);
				field.put("text", fieldValue);
				reMap.put(fieldName, field);
			}
			String otherAllergic = other.toString();
			String othAll = "";
			if (otherAllergic.length() > 0) {
				othAll = otherAllergic.substring(0,
						otherAllergic.lastIndexOf(","));
			}
			if (!othAll.equals("")) {
				reMap.put(OtherFieldName, othAll);
			}
		}
		return reMap;
	}

	/**
	 * 删除个人既往史
	 * 
	 * @param empiId
	 * @param typeCode
	 * @throws ModelDataOperationException
	 */
	public void deletePastHistory(String empiId, String typeCode)
			throws ModelDataOperationException {
		String hql = new StringBuilder("delete from ")
				.append(EHR_PastHistory)
				.append(" where empiId = :empiId and pastHisTypeCode = :typeCode")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("typeCode", typeCode);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新既往史信息失败", e);
		}
	}

	/**
	 * 修改个人既往史
	 * 
	 * @param body
	 * @param fieldName
	 * @param OtherFieldName
	 * @param typeCode
	 * @param otherCode
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void updatePastHistory(Map<String, Object> body, String fieldName,
			String OtherFieldName, String typeCode, String otherCode)
			throws ValidateException, ModelDataOperationException {
		String empiId = (String) body.get("empiId");
		String fieldKey = (String) body.get(fieldName);
		String fieldText = StringUtils.trimToEmpty((String) body.get(fieldName
				+ "_text"));
		ArrayList<Map<String, Object>> deformityBody = this.createPastHisBody(
				empiId, typeCode, fieldKey, fieldText, body);
		for (int i = 0; i < deformityBody.size(); i++) {
			Map<String, Object> hisBody = deformityBody.get(i);
			if (hisBody.get("diseaseCode").equals(otherCode)) {
				String otherDeformity = StringUtils.trimToEmpty((String) body
						.get(OtherFieldName));
				if (otherDeformity != null && !otherDeformity.equals("")) {
					Map<String, Object> otherBody = createSimplePasthisBody(
							empiId, typeCode, otherCode, otherDeformity, body);
					savePastHistory(otherBody, "create");
					continue;
				}
			}
			savePastHistory(hisBody, "create");
		}
	}

	/**
	 * 构建个人既往史多个实体
	 * 
	 * @param empiId
	 * @param pastHisTypeCode
	 * @param diaplayValue
	 * @param showValue
	 * @param body
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<Map<String, Object>> createPastHisBody(String empiId,
			String pastHisTypeCode, String diaplayValue, String showValue,
			Map<String, Object> body) {
		ArrayList<Map<String, Object>> reqBodys = new ArrayList<Map<String, Object>>();
		String[] display = diaplayValue.split(",");
		String[] show = showValue.split(",");
		int n = display.length > show.length ? display.length : show.length;
		for (int i = 0; i < n; i++) {
			String diseaseCode = display.length > i ? display[i] : display[0];
			String diseaseText = show[i];
			Map<String, Object> reqBody = new HashMap<String, Object>();
			reqBody = createSimplePasthisBody(empiId, pastHisTypeCode,
					diseaseCode, diseaseText, body);
			reqBodys.add(reqBody);
		}
		return reqBodys;
	}

	/**
	 * 构建个人既往史单个实体
	 * 
	 * @param empiId
	 * @param pastHisTypeCode
	 * @param diaplayValue
	 * @param showValue
	 * @param body
	 * @return
	 */
	private Map<String, Object> createSimplePasthisBody(String empiId,
			String pastHisTypeCode, String diaplayValue, String showValue,
			Map<String, Object> body) {
		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("empiId", empiId);
		reqBody.put("confirmDate",
				StringUtils.trimToEmpty((String) body.get("confirmDate")));
		reqBody.put("pastHisTypeCode", pastHisTypeCode);
		reqBody.put("recordUnit",
				StringUtils.trimToEmpty((String) body.get("createUnit")));
		reqBody.put("recordUser",
				StringUtils.trimToEmpty((String) body.get("createUser")));
		reqBody.put("recordDate",
				StringUtils.trimToEmpty((String) body.get("createDate")));
		reqBody.put("diseaseCode", diaplayValue);
		reqBody.put("diseaseText", showValue);
		return reqBody;
	}

	/**
	 * 保存个人既往史
	 * 
	 * @param data
	 * @param op
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void savePastHistory(Map<String, Object> data, String op)
			throws ValidateException, ModelDataOperationException {
		try {
			dao.doSave(op, EHR_PastHistory, data, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存个人既往史失败!", e);
		}
	}

	/**
	 * 查询个人既往史数据
	 * 
	 * @param data
	 * @param op
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> loadPastHistoryRecords(Map<String, Object> req)
			throws ValidateException, ModelDataOperationException {
		Map<String, Object> reValue = new HashMap<String, Object>();
		try {
			Schema sc = SchemaController.instance().get(EHR_PastHistorySearch);

			// User user = (User) dao.getContext().get("user.instance");
			// AuthorizeResult result = user.authorize("storage", sc.getId());
			// Condition c = result.getCondition("filter");
			Condition c = sc.lookupCondition("query");
			String filter = "";
			if (c != null) {
				filter = " and "
						+ ExpressionProcessor.instance().toString(
								(List) c.getDefine());
			}

			StringBuffer hql = new StringBuffer();

			StringBuffer existsHql = new StringBuffer(
					"select 1 from EHR_PastHistory a  where c.empiId=a.empiId");
			List cnd = (List) req.get("cnd");
			if (null != cnd) {
				try {
					existsHql.append(" and ").append(
							ExpressionProcessor.instance().toString(cnd));
				} catch (ExpException e) {
					throw new ModelDataOperationException("错误的查询参数.", e);
				}
			}

			hql.append(
					"select b.phrId as phrId,b.regionCode as regionCode,b.regionCode_text as regionCode_text,")
					.append("b.masterFlag as masterFlag,b.relaCode as relaCode,b.manaDoctorId as manaDoctorId,b.manaNurse as manaNurse,")
					.append("b.healthDoctor as healthDoctor,b.manaUnitId as manaUnitId,b.signFlag as signFlag,b.createUnit as createUnit,")
					.append("b.createUser as createUser,b.createDate as createDate,c.empiId as empiId,c.idCard as idCard,c.sexCode as sexCode,")
					.append("c.birthday as birthday,c.personName as personName,c.mobileNumber as mobileNumber ")
					.append("from EHR_HealthRecord b,")
					.append("MPI_DemographicInfo c  where ")
					.append("c.empiId = b.empiId ").append(filter)
					.append(" and exists (").append(existsHql);
			hql.append(")");
			String sql = hql.toString();

			Session session = (Session) dao.getContext()
					.get(Context.DB_SESSION);
			StringBuffer countHql = new StringBuffer(
					"select count(*) from EHR_HealthRecord b,MPI_DemographicInfo c where c.empiId = b.empiId and exists(")
					.append(existsHql.toString()).append(" )").append(filter);
			Query queryCount = session.createQuery(countHql.toString());
			Object count = queryCount.uniqueResult();
			reValue.put("totalCount", count);

			int pageSize = 0;
			int pageNo = 0;
			if (null != (Integer) req.get("pageSize")) {
				pageNo = (Integer) req.get("pageNo");
				pageSize = (Integer) req.get("pageSize");
			} else {
				pageSize = 50;
				pageNo = 1;
			}
			int first = (pageNo - 1) * pageSize;
			reValue.put("pageSize", pageSize);
			reValue.put("pageNo", pageNo);

			Query query = session.createQuery(sql.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.setFirstResult(first);
			query.setMaxResults(pageSize);

			List records = new ArrayList();
			reValue.put("records", records);
			List<?> l = query.list();
			if (l.size() > 0) {
				for (int i = 0; i < l.size(); i++) {
					Map<?, ?> r = (Map<?, ?>) l.get(i);
					Map<String, Object> jsonRec = new HashMap<String, Object>();
					records.add(jsonRec);
					for (int j = 0; j < sc.getItems().size(); j++) {
						String name = sc.getItems().get(j).getId();
						SchemaItem si = sc.getItem(name);
						if (si.getType().equals("text")) {
							Clob clob = (Clob) r.get(name);
							Reader reader = clob.getCharacterStream();
							BufferedReader br = new BufferedReader(reader);
							StringBuffer sb = new StringBuffer();
							String temp;
							while ((temp = br.readLine()) != null) {
								sb.append(temp);
							}
							jsonRec.put(name, sb.toString());
							br.close();
							continue;
						}
						Object v = r.get(name);
						jsonRec.put(name, v);
						if (si != null && si.isCodedValue()) {
							Object dv = null;
							boolean isCacheDic = false;
							for (int k = 0; k < sc.getItems().size(); k++) {
								String vtext = sc.getItems().get(k).getId();
								if (vtext.equals(name + "_text")) {
									dv = r.get(si.getId());
									isCacheDic = true;
									break;
								}
							}
							if (dv != null) {
								jsonRec.put(name + "_text", dv);
							} else {
								if (!isCacheDic) {
									jsonRec.put(name + "_text",
											si.toDisplayValue(v));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询个人既往史失败!", e);
		}
		return reValue;
	}

	/**
	 * 查询部分个人既往史默认为孕妇的既往史相关信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPersonPastHistory(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select diseaseText as diseaseText, diseaseCode as diseaseCode,pastHisTypeCode as pastHisTypeCode from ")
				.append(EHR_PastHistory)
				.append(" where empiId = :empiId and")
				.append(" pastHisTypeCode in ('01','02','03','05','07','08','09','10','12') and")
				.append(" diseaseCode is not null and diseaseCode <> '0101'")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		List<Map<String, Object>> l;
		try {
			l = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询个人既往史失败!", e);
		}
		if (l.size() < 1) {
			return null;
		}
		Map<String, Object> pastHistory = new HashMap<String, Object>();
		for (Map<String, Object> map : l) {
			String type = (String) map.get("pastHisTypeCode");
			String code = (String) map.get("diseaseCode");
			String text = (String) map.get("diseaseText");
			if (text == null || "".equals(text)) {
				continue;
			}
			if (type.equals(PastHistoryCode.ALLERGIC)) {// ** 过敏史
				// ** 默认既往史的过敏史文本为过敏史(allergicHistory)
				String key = "allergicHistory";
				if (pastHistory.get(key) != null) {
					pastHistory.put(key, pastHistory.get(key) + "," + text);
				} else {
					pastHistory.put(key, text);
				}
			} else if (type.equals(PastHistoryCode.SCREEN)) { // ** 既往病史
				// ** 默认既往史的 其他既往病史文本为其他既往病史(otherPastHistory)
				if (code.equals(PastHistoryCode.PASTHIS_SCREEN_OTHER)) {
					String key = "otherPastHistory";
					pastHistory.put(key, text);
				}
				// ** 默认既往史的既往病史code为既往病史(pastHistory)
				String key = "pastHistory";
				String tempCode = null;
				if (code.equals(PastHistoryCode.PASTHIS_SCREEN_NOT_HAVE)) {
					tempCode = MHCPastHistory.NOT_HAVE;
				} else if (code
						.equals(PastHistoryCode.PASTHIS_SCREEN_CARDIOPATHY)) {
					tempCode = MHCPastHistory.CARDIOPATHY;
				} else if (code.equals(PastHistoryCode.PASTHIS_SCREEN_RENAL)) {
					tempCode = MHCPastHistory.RENAL;
				} else if (code.equals(PastHistoryCode.PASTHIS_SCREEN_LIVER)) {
					tempCode = MHCPastHistory.LIVER;
				} else if (code
						.equals(PastHistoryCode.PASTHIS_SCREEN_HYPERTENSION)) {
					tempCode = MHCPastHistory.HYPERTENSION;
				} else if (code.equals(PastHistoryCode.PASTHIS_SCREEN_ANAEMIA)) {
					tempCode = MHCPastHistory.ANAEMIA;
				} else if (code.equals(PastHistoryCode.PASTHIS_SCREEN_DIABETES)) {
					tempCode = MHCPastHistory.DIABETES;
				} else if (code.equals(PastHistoryCode.PASTHIS_SCREEN_OTHER)) {
					tempCode = MHCPastHistory.OTHER;
				}
				String value = (String) pastHistory.get(key);
				if (value != null && !"".equals(value)) {
					pastHistory.put(key, value + "," + tempCode);
				} else {
					pastHistory.put(key, tempCode);
				}
			} else if (type.equals(PastHistoryCode.OPERATION)) {// ** 手术史
				// ** 默认既往史的手术史文本为手术史(operationHistory)
				String key = "operationHistory";
				if (pastHistory.get(key) != null) {
					pastHistory.put(key, pastHistory.get(key) + "," + text);
				} else {
					pastHistory.put(key, text);
				}
			} else if (type.equals(PastHistoryCode.HEREDOPTHIA)) { // ** 遗传病史
				// **既往史的遗传病史为有遗传病史时默认家族史(familyHistory)为遗传性疾病史
				if (code.equals(PastHistoryCode.PASTHIS_HEREDOPTHIA_CODE)) {
					String key = "familyHistory";
					String value = (String) pastHistory.get(key);
					if (value != null) {
						if (value.indexOf(FamilyHistory.HEREDOPTHIA) > 0) {
							continue;
						} else {
							pastHistory.put(key, value + ","
									+ FamilyHistory.HEREDOPTHIA);
						}
					} else {
						pastHistory.put(key, FamilyHistory.HEREDOPTHIA);
					}
				}
			} else if (type.equals(PastHistoryCode.FATHER)
					|| type.equals(PastHistoryCode.MOTHER)
					|| type.equals(PastHistoryCode.BROTHER)
					|| type.equals(PastHistoryCode.CHILDREN)) { // ** 家族疾病史
				// **既往史的家族疾病史为重性精神疾病时默认家族史(familyHistory)为精神疾病史
				if (code.equals(PastHistoryCode.PASTHIS_PSYCHOSIS_CODE)) {
					String key = "familyHistory";
					String value = (String) pastHistory.get(key);
					if (value != null) {
						if (value.indexOf(FamilyHistory.PSYCHOSIS) > 0) {
							continue;
						} else {
							pastHistory.put(key, value + ","
									+ FamilyHistory.PSYCHOSIS);
						}
					} else {
						pastHistory.put(key, FamilyHistory.PSYCHOSIS);
					}
				}
			} else if (type.equals(PastHistoryCode.EXPOSE)) { // ** 暴露史
				// ** 既往史的暴露史为毒物时默认个人史(personHistory)为接触有毒有害物质
				// ** 既往史的暴露史为射线时默认个人史(personHistory)为接触放射线
				String key = "personHistory";
				String temp = null;
				if (code.equals(PastHistoryCode.PASTHIS_POISON_CODE)) { // ** 毒物
					temp = PersonHistory.POISON;
				}
				// ** 射线
				else if (code.equals(PastHistoryCode.PASTHIS_RADIAL_CODE)) {
					temp = PersonHistory.RADIAL;
				}
				if (temp == null) {
					continue;
				}
				String value = (String) pastHistory.get(key);
				if (value != null) {
					if (value.indexOf(temp) > 0) {
						continue;
					} else {
						pastHistory.put(key, value + "," + temp);
					}
				} else {
					pastHistory.put(key, temp);
				}
			}
		}
		return pastHistory;
	}

	/**
	 * 查询部分个人既往史
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */	
	public boolean hasFamilyDiabetesPastHistory(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as cnt from ")
				.append(EHR_PastHistory).append(" where empiId = :empiId and")
				.append(" diseaseCode in('")
				.append(PastHistoryCode.PASTHIS_FATHER_DIABETES).append("','")
				.append(PastHistoryCode.PASTHIS_MOTHER_DIABETES).append("','")
				.append(PastHistoryCode.PASTHIS_BROTHER_DIABETES).append("','")
				.append(PastHistoryCode.PASTHIS_CHILDREN_DIABETES).append("')")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		List<Map<String, Object>> l;
		try {
			l = dao.doQuery(hql, param);
			if ((Long) (((Map<String, Object>) l.get(0)).get("cnt")) == 0) {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询个人既往史失败!", e);
		}
		return true;
	}
	
	public boolean hasFamilyMZFPastHistory(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as cnt from ")
				.append(EHR_PastHistory).append(" where empiId = :empiId and")
				.append(" diseaseCode in('")
				.append(PastHistoryCode.PASTHIS_FATHER_DIABETES).append("','")
				.append(PastHistoryCode.PASTHIS_MOTHER_DIABETES).append("','")
				.append(PastHistoryCode.PASTHIS_BROTHER_DIABETES).append("','")
				.append(PastHistoryCode.PASTHIS_CHILDREN_DIABETES).append("')")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		List<Map<String, Object>> l;
		try {
			l = dao.doQuery(hql, param);
			if ((Long) (((Map<String, Object>) l.get(0)).get("cnt")) == 0) {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询个人既往史失败!", e);
		}
		return true;
	}

	public List<Map<String, Object>> queryPastHistoryByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		try {
			return dao.doList(cnd, null, EHR_PastHistory);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据empiId查询个人既往史失败!", e);
		}
	}
}
