package chis.source.cons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;

public class ConsultationRecordModel implements BSCHISEntryNames {
	BaseDAO dao;

	public ConsultationRecordModel(BaseDAO dao) {
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

	public Map<String, Object> getConsultationRecordByPkey(String recordId)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(CONS_ConsultationRecord, recordId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据健主键查询接诊记录失败。");
		}
	}

}
