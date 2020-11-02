/**
 * @(#)HypertensionBaseModel.java Created on 2012-1-5 上午9:24:44
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.empi.EmpiModel;
import chis.source.pub.PublicModel;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.util.exp.ExpException;


/**
 * @description 高血压档案基础业务模型类
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MDCBaseModel extends PublicModel {

	protected static final Map<String, String> recordEntryName = new HashMap<String, String>();
	static {
		recordEntryName.put(BusinessType.GXY, MDC_HypertensionRecord);
		recordEntryName.put(BusinessType.TNB, MDC_DiabetesRecord);
		recordEntryName.put(BusinessType.ZL, MDC_TumourRecord);
	}

	/**
	 * @param dao
	 */
	public MDCBaseModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 更新(下一次)随访提醒时间
	 * 
	 * @param planType
	 * @param nextPlanId
	 * @param nextRemindDate
	 * @param session
	 * @throws Exception
	 */
	protected void setNextRemindDate(String businessType, String nextPlanId,
			Date nextRemindDate) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("PUB_VisitPlan")
				.append(" set beginVisitDate = ")
				.append("cast(:beginVisitDate as date) where planId = :planId")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("planId", nextPlanId);
		param.put("beginVisitDate", nextRemindDate);

		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新下一次随访提醒时间失败！");
		}
	}

	/**
	 * 获取下次随访计划
	 * 
	 * @param empiId
	 * @param businessType
	 * @param thisPlanId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getNextVisitPlan(String empiId,
			String businessType, String thisPlanId)
			throws ModelDataOperationException {
		String cnd = "[\"and\", [\"eq\", [\"$\", \"empiId\"], [\"s\", \""
				+ empiId
				+ "\"]], [\"eq\", [\"$\", \"businessType\"], [\"s\", \""
				+ businessType
				+ "\"]], [\"gt\", [\"$\", \"planId\"], [\"s\", \"" + thisPlanId
				+ "\"]]]";

		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(CNDHelper.toListCnd(cnd), " beginDate asc ",
					PUB_VisitPlan);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取下次随访计划失败！");
		}

		return rsList.size() > 0 ? rsList.get(0) : null;
	}

	/**
	 * 更新下次随访日期
	 * 
	 * @param table
	 * @param visitId
	 * @param nextDate
	 * @throws ModelDataOperationException
	 */
	public void setNextDate(String table, String visitId, Date nextDate)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(table)
				.append(" set nextDate = :nextDate ")
				.append(" where visitId = :visitId").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("visitId", visitId);
		paramMap.put("nextDate", nextDate);
		try {
			dao.doUpdate(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新下次随访日期失败！", e);
		}
	}
	
	/**
	 * 检查该档案是否有随访未做，高血压。 --据empiId查随访计划
	 * 
	 * @param empiId
	 * @return
	 */
	public List<Map<String, Object>> checkHasVisitUndo(List<String> empiIdList,String visitType,String visitName)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select empiId as empiId, count(*) as count from ")
				.append("PUB_VisitPlan")
				.append(" where empiId in (").append(BSCHISUtil.listToString(empiIdList)).append(") and businessType = :businessType")
				.append(" and planStatus=:planStatus and beginDate<=:beginDate ")
				.append(" group by empiId").toString();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("businessType", visitType);
		param.put("planStatus", String.valueOf(Constants.CODE_STATUS_NORMAL));
		Calendar calendar = Calendar.getInstance();
		// int day = calendar.get(Calendar.DAY_OF_YEAR);
		// calendar.set(Calendar.DAY_OF_YEAR, day + 10);
		Date cc = calendar.getTime();
		param.put("beginDate", cc);

		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取"+visitName+"随访计划信息失败！");
		}
		return rsList;
	}

	/**
	 * 依据empiId,businessType,planDate查询随访计划记录
	 * 
	 * @param empiId
	 * @param businessType
	 * @param planDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getListVisitPlan(String empiId,
			String businessType, Date planDate)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append("PUB_VisitPlan")
				.append(" where empiId = :empiId and businessType = :businessType and planDate >= :planDate")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("businessType", businessType);
		param.put("planDate", planDate);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访计划状态失败！");
		}
		return list;
	}

	// ====================-- 个人基本信息相关 --================================
	/**
	 * 组装 riskiness [吸烟、血压、年龄] 数据
	 * 
	 * @param jsonRec
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> makeGroup(Map<String, Object> rec)
			throws ModelDataOperationException {
		int smoke = 0;
		if (!StringUtils.isEmpty((String) rec.get("smoke"))) {
			smoke = Integer.parseInt((String) rec.get("smoke"));
		}
		StringBuffer riskiness = new StringBuffer((String) rec.get("riskiness"));
		String risk[] = riskiness.toString().split(",");
		if (smoke == 2) {// @@ 吸烟
			int i = 0;
			for (; i < risk.length; i++) {
				if (risk[i].equals("2")) {
					break;
				}
			}
			if (i == risk.length) {
				if (riskiness.toString().equals("0")) {
					riskiness = new StringBuffer("2");
				} else {
					riskiness.append(riskiness.length() > 0 ? ",2" : "2");
				}
			}
		}
		int constriction = (Integer) rec.get("constriction");
		int diastolic = (Integer) rec.get("diastolic");
		int grade = decideHypertensionGrade(constriction, diastolic);
	
		rec.put("hypertensionLevel", grade);
		EmpiModel em = new EmpiModel(dao);
		String empiId = (String) rec.get("empiId");
		Map<String, Object> obj = em.getSexAndBirthday(empiId);
		if (obj != null) {
			String sex = (String) obj.get("sexCode");
			int age = BSCHISUtil.calculateAge((Date) obj.get("birthday"), null);
			// @@ 男大于55，女大于65
			if ((sex.equals("1") && age > 55) || (sex.equals("2") && age > 65)) {
				int i = 0;
				for (; i < risk.length; i++) {
					if (risk[i].equals("1")) {
						break;
					}
				}
				if (i == risk.length) {
					if (riskiness.toString().equals("0")) {
						riskiness = new StringBuffer("1");
					} else {
						riskiness.append(riskiness.length() > 0 ? ",1" : "1");
					}
				}
			}
		}
		rec.put("riskiness", riskiness.toString());
		return rec;
	}

	/**
	 * 确定血压分级。
	 * 
	 * @param constriction
	 *            收缩压
	 * @param diastolic
	 *            舒张压
	 * @return
	 */
	public static int decideHypertensionGrade(int constriction, int diastolic) {
		if (constriction >= 180 || diastolic >= 110) {
			return 3; // @@ 3级（重度）
		}
		if ((constriction >= 160 && constriction <= 179)
				|| (diastolic >= 100 && diastolic <= 109)) {
			return 2; // @@ 2级（中度）
		}
		if ((constriction >= 140 && constriction <= 159)
				|| (diastolic >= 90 && diastolic <= 99)) {
			return 1; // @@ 1级（轻度）
		}
		if (constriction < 120 && diastolic < 80) {
			return 4; // @@ 理想血压
		}
		if (constriction < 130 && diastolic < 85) {
			return 5; // @@ 正常血压
		}
		if ((constriction >= 130 && constriction <= 139 && diastolic < 90)
				|| (diastolic >= 85 && diastolic <= 89 && constriction < 140)) {
			return 6; // @@ 正常高值
		}
		if (constriction >= 140 && diastolic < 90) {
			return 7; // @@ 单纯收缩性高血压
		}
		return 0;
	}

	/**
	 * 更新健康档案是否高血压字段
	 * 
	 * @param phrId
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public int updateIsHypertensionOfHealthRecord(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("EHR_HealthRecord")
				.append(" set isHypertension = 'y',lastModifyUser = :lastModifyUser")
				.append(",lastModifyDate = :lastModifyDate")
				.append(" where phrId = :phrId").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phrId", phrId);
		String userId =  UserRoleToken.getCurrent().getUserId();
		param.put("lastModifyUser", userId);
		param.put("lastModifyDate", new Date());
		int rsInt = 0;
		try {
			rsInt = dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新健康档案是否高血压字段失败！");
		}
		return rsInt;
	}

	/**
	 * 更新个人健康档案的生活习惯。
	 * 
	 * @param body
	 * @param ctx
	 */
	public int updateLifeStyle(Map<String, Object> body)
			throws ModelDataOperationException {
		String userId =  UserRoleToken.getCurrent().getUserId();
		String smoke = (String) body.get("smoke");
		String drink = (String) body.get("drink");
		String train = (String) body.get("train");
		String eateHabit = (String) body.get("eateHabit");
		if (StringUtils.isEmpty("smoke") && StringUtils.isEmpty(drink)
				&& StringUtils.isEmpty(train) && StringUtils.isEmpty(eateHabit)) {
			return 0;
		} else {
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer hql = new StringBuffer("update ")
					.append("EHR_LifeStyle").append(" set ")
					.append("lastModifyUser = :lastModifyUser")
					.append(",lastModifyDate = :lastModifyDate");
			if(StringUtils.isNotEmpty(smoke)){
				hql.append(" ,smokeFreqCode = :smokeFreqCode");
				parameters.put("smokeFreqCode", smoke);
			}
			int smokeCount = 0;
			if(body.get("smokeCount") instanceof String){
				smokeCount = 0;
			}
			if(body.get("smokeCount") instanceof Integer){
				smokeCount = (Integer) body.get("smokeCount");
			}
			if(smokeCount != 0){
				hql.append(" ,smokeCount = :smokeCount");
				parameters.put("smokeCount", smokeCount);
			}
			if(StringUtils.isNotEmpty(drink)){
				hql.append(" ,drinkFreqCode = :drinkFreqCode");
				parameters.put("drinkFreqCode", drink);
			}
			String drinkTypeCode = (String) body.get("drinkTypeCode");
			if(StringUtils.isNotEmpty(drinkTypeCode)){
				hql.append(" ,drinkTypeCode = :drinkTypeCode");
				parameters.put("drinkTypeCode", drinkTypeCode);
			}
			int drinkCount = 0;
			if(body.get("drinkCount") instanceof String){
				drinkCount = 0;
			}
			if(body.get("drinkCount") instanceof Integer){
				drinkCount = (Integer) body.get("drinkCount");
			}
			if(drinkCount != 0){
				hql.append(" ,drinkCount = :drinkCount");
				parameters.put("drinkCount", drinkCount);
			}
			if (StringUtils.isNotEmpty(train)) {
				hql.append(" ,trainFreqCode = :trainFreqCode");
				parameters.put("trainFreqCode", body.get("train"));
			}
			if (StringUtils.isNotEmpty(eateHabit)) {
				hql.append(" ,eateHabit = :eateHabit");
				parameters.put("eateHabit", body.get("eateHabit"));
			}
			hql.append(" where empiId = :empiId");

			parameters.put("empiId", body.get("empiId"));
			parameters.put("lastModifyUser", userId);
			parameters.put("lastModifyDate", new Date());
			int rsInt = 0;
			try {
				rsInt = dao.doUpdate(hql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "更新个人健康档案的生活习惯失败！", e);
			}
			return rsInt;
		}

	}

	/**
	 * 获取个人健康档案的生活习惯
	 * 
	 * @param cnd
	 * @param orderBy
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getLifeStyle(List<?> cnd, String orderBy)
			throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, orderBy, EHR_LifeStyle);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取个人健康档案的生活习惯失败！", e);
		}
		return rsList;
	}

	/**
	 * 依据riskiness查询高血压评估字典信息
	 * 
	 * @param riskiness
	 * @return 一级血压[HL1]、二级血压[HL2]、三级血压[HL3]、字典类别[dicType]
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> findEstimateDictionary(String riskiness)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"Select HL1 as HL1,HL2 as HL2,HL3 as HL3")
				.append(",dicType as dicType from ")
				.append(MDC_EstimateDictionary)
				.append(" where riskiness = :riskiness");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("riskiness", riskiness);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询高血压评估字典失败！");
		}
		return rsList;
	}

	/**
	 * 加载主页面，据cnd、schema分页查询(可以表关联)
	 * 
	 * @param req
	 * @return 数据[list类型，在Map中key为body 分页信息pageSize,pageNo,totalCount
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> pageQueryList(Map<String, Object> req)
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
		String schemaName = (String) req.get("schema");
		if (StringUtils.isEmpty(schemaName) || queryCnd == null
				|| queryCnd.size() == 0) {
			new ModelDataOperationException(Constants.CODE_INVALID_REQUEST,
					"参数获取失败！");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
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
		try {
			Map<String, Object> rsMap = dao.doList(queryCnd, sortInfo,
					schemaName, pageNo, pageSize, queryCndsType);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			res.put("totalCount", rsMap.get("totalCount"));
			res.put("body", rsMap.get("body"));
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
		}
		return res;
	}

	/**
	 * 获取随访计划的最小间隔时间
	 * 
	 * @param empiId
	 * @param instanceType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int getMinStep(String empiId, String instanceType)
			throws ModelDataOperationException {
		String entryName = recordEntryName.get(instanceType);
		String hql = new StringBuffer(
				"select planTypeCode as planTypeCode  from ").append(entryName)
				.append(" where empiId = :empiId").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访计划类型失败！");
		}
		if (rsList.size() == 0) {
			throw new ModelDataOperationException(
					Constants.CODE_RECORD_NOT_FOUND, new StringBuffer("No ")
							.append(entryName)
							.append(" record found of empiId[").append(empiId)
							.append("].").toString());
		}
		String planTypeCode = (String) rsList.get(0).get("planTypeCode");
		String hql2 = new StringBuffer("select minStep as minStep from ")
				.append(PUB_PlanType)
				.append(" where planTypeCode = :planTypeCode").toString();
		paramMap.clear();
		paramMap.put("planTypeCode", planTypeCode);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql2, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访计划的最小间隔时间失败！");
		}
		if (rsMap.size() == 0) {
			throw new ModelDataOperationException(
					Constants.CODE_RECORD_NOT_FOUND,
					"No such plan type of code :" + planTypeCode);
		}
		return (Integer) rsMap.get("minStep");
	}
	
	//溧阳半年度一次随访干预
	public boolean checkHasVisithalfyear(String empiId, String businessType,Date plandate)
			throws ModelDataOperationException {
		
		Calendar tempday=Calendar.getInstance();
		tempday.setTime(plandate);
		StringBuffer hql=new StringBuffer("select count(*) as count from ")
		.append("PUB_VisitPlan")
		.append(" where empiId=:empiId and businessType=:businessType")
		.append(" and visitMeddle in ('1','2') and ")
		.append("to_char(planDate,'yyyy')='").append(tempday.get(Calendar.YEAR))
		.append("'");
		if(tempday.get(Calendar.MONTH)<6){
			hql.append(" and to_char(planDate,'mm')<='06' "); 
		}else{
			hql.append(" and to_char(planDate,'mm')>'06' ");
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("businessType", businessType);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查是否有随访计划失败！");
		}
		if (rsMap != null && (Long) (rsMap.get("count")) > 0) {
			return true;
		} else {
			return false;
		}
	}
	//验证高血压糖尿病身高
	public Map<String, Object> checkMdcHight(String empiId,int type,double h)
			throws ModelDataOperationException {
		Map<String, Object> re=new HashMap<String, Object>();
		Map<String, Object> checkmap=new HashMap<String, Object>();
		Map<String, Object> pa=new HashMap<String, Object>();
		pa.put("empiId", empiId);
		//type=1或3获取高血压档案身高
		if(type==1 || type==3){
			String gxysql="select HEIGHT as HEIGHT from MDC_HypertensionRecord where empiId=:empiId ";
			try {
				Map<String, Object> gxymap=dao.doSqlLoad(gxysql, pa);
				if(gxymap!=null){
					checkmap.put("gxyheight", Double.parseDouble(gxymap.get("HEIGHT")+""));
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
		//type=2或3获取糖尿病档案身高
		if(type==2 ||type==3){
			String gxysql="select HEIGHT as HEIGHT from MDC_DiabetesRecord where empiId=:empiId ";
			try {
				Map<String, Object> tnbmap=dao.doSqlLoad(gxysql, pa);
				if(tnbmap!=null){
					checkmap.put("tnbheight", Double.parseDouble(tnbmap.get("HEIGHT")+""));
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
		if(checkmap.containsKey("gxyheight")){
			if(h==Double.parseDouble(checkmap.get("gxyheight")+"")){
				re.put("gxyheightcheck",true);
			}else{
				re.put("gxyheightcheck",false);
			}
		}
		if(checkmap.containsKey("tnbheight")){
			if(h==Double.parseDouble(checkmap.get("tnbheight")+"")){
				re.put("tnbheightcheck",true);
			}else{
				re.put("tnbheightcheck",false);
			}
		}
		return re;
	}
	
}
