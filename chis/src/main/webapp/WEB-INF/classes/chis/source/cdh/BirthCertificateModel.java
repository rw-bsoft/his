/**
 * @(#)BirthCertificateModel.java Created on 2012-2-7 下午5:17:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class BirthCertificateModel implements BSCHISEntryNames {
	private BaseDAO dao;

	/**
	 * @param dao
	 */
	public BirthCertificateModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存儿童出生医学证明
	 * 
	 * @param empiId
	 * @param op
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveChildInfo(Map<String, Object> data, String op,Map<String,Object> res)
			throws ModelDataOperationException, ValidateException {
		data.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		data.put("lastModifyDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		try {
			Map<String, Object> genValues = dao.doSave(op, CDH_BirthCertificate, data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童出生医学证明失败", e);
		}
	}

	/**
	 * 更新儿童基本信息的出生证编号
	 * @param empiId
	 * @param certificateNo
	 * @throws ModelDataOperationException
	 */
	public void updateCertificateNo(String empiId, String certificateNo)
			throws ModelDataOperationException {
		// 更新出生证编号
		String hql = new StringBuffer("update ")
				.append("MPI_ChildInfo")
				.append(" set certificateNo=:certificateNo where childEmpiId = :empiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("certificateNo", certificateNo);
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新出生证号失败", e);
		}
	}

	/**
	 * 初始化出生信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> initBirthData(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select a.personName as personName,a.sexCode as sexCode,")
				.append("a.birthday as birthday,a.homePlace as homePlace,")
				.append("b.certificateNo as certificateNo from ")
				.append(MPI_DemographicInfo)
				.append(" a ,")
				.append(MPI_ChildInfo)
				.append(" b where a.empiId=:empiId and a.empiId = b.childEmpiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		Map<String, Object> map;
		try {
			map = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "初始化出生信息失败。", e);
		}
		return map;
	}

	/**
	 * 根据empiId获取出生证明
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getBirthCertificateRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		List<Map<String, Object>> birthData = null;
		try {
			birthData = dao.doList(cnd, null, CDH_BirthCertificate);
			if (birthData != null && birthData.size() > 0) {
				return birthData.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取出生医学证明失败", e);
		}
		return null;
	}

	/**
	 * 根据recordId获取出生证明
	 * 
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getBirthCertificateRecordByRecordId(
			String recordId) throws ModelDataOperationException {
		List<?> cnd = CNDHelper
				.createSimpleCnd("eq", "recordId", "s", recordId);
		List<Map<String, Object>> birthData = null;
		try {
			birthData = dao.doList(cnd, null, CDH_BirthCertificate);
			if (birthData != null && birthData.size() > 0) {
				return birthData.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取出生医学证明失败", e);
		}
		return null;
	}
}
