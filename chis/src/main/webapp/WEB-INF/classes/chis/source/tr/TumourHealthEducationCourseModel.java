/**
 * @(#)TumourHealthEducationCourseModel.java Created on 2014-7-31 上午10:34:15
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.HashMap;
import java.util.Map;

import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourHealthEducationCourseModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public TumourHealthEducationCourseModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存课程记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-7-31 上午10:37:55
	 * @Modify:
	 */
	public Map<String, Object> saveTHECourse(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PHQ_HealthEducationCourse, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存课程记录时数据验证失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存课程记录失败", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:判断该课程是否已经存在该听众
	 * @param empiId
	 * @param courseId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-7-31 下午4:33:30
	 * @Modify:
	 */
	public boolean isExistListener(String empiId, String courseId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(APID) as countNum from ")
				.append(PHQ_AttendPersonnel)
				.append(" where empiId = :empiId and courseId = :courseId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("courseId", courseId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断该课程是否已经存在该听众失败！", e);
		}
		boolean isExist = false;
		if (rsMap != null && rsMap.size() > 0) {
			long countNum = 0;
			countNum = (Long) rsMap.get("countNum");
			if (countNum > 0) {
				isExist = true;
			}
		}
		return isExist;
	}

	/**
	 * 
	 * @Description:保存课程听众信息
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-7-31 下午4:36:17
	 * @Modify:
	 */
	public Map<String, Object> saveListtener(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PHQ_AttendPersonnel, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存课程听众信息时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存课程听众信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:更新课程参加人数
	 * @param courseId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-1-14 下午2:30:29
	 * @Modify:
	 */
	public void updateNumberOfParticipants(String courseId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recNum from ")
				.append(PHQ_AttendPersonnel)
				.append(" where courseId=:courseId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("courseId", courseId);
		Map<String, Object> nopMap = null;
		try {
			nopMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "统计听课人数失败!", e);
		}
		if (nopMap != null && nopMap.size() > 0) {
			pMap.put("nop", ((Long)nopMap.get("recNum")).intValue());
			String unopHql = new StringBuffer("update ")
					.append(PHQ_HealthEducationCourse)
					.append(" set numberOfParticipants=:nop")
					.append(" where courseId=:courseId").toString();
			try {
				dao.doUpdate(unopHql, pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "更新课程参加人数失败！", e);
			}
		}
	}

	/**
	 * 
	 * @Description:删除听课人员
	 * @param pkey
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-1-14 下午3:11:36
	 * @Modify:
	 */
	public void removeAttendPersonnel(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, PHQ_AttendPersonnel);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除听课人员失败！", e);
		}
	}
}
