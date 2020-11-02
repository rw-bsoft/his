package phis.application.sto.source;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.utils.SchemaUtil;
/**
 * 药库采购历史
 * @author caijy
 *
 */
public class StorehousePurchaseHistoryModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehousePurchaseHistoryModel.class);

	public StorehousePurchaseHistoryModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-8
	 * @description 查询药库采购历史模块中的药品记录
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> querySPHDrugRecord(Map<String, Object> body)
			throws ModelDataOperationException {
		List<Map<String, Object>> drugRecordList=null;
		try {
			StorehousePurchaseHistoryUtil sphUtil = new StorehousePurchaseHistoryUtil();
			body.put(StorehousePurchaseHistoryUtil.QUERYTYPE,
					StorehousePurchaseHistoryUtil.DRUG_RECORD);
			String dataSourceSql = sphUtil.dataSource(body);
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();// 获取JGID
			long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));
			String adt_begin = String.valueOf(body.get("KSSJ") + "00:00:00");
			String adt_end = String.valueOf(body.get("JSSJ") + "23:59:59");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("adt_begin", adt_begin);
			parameters.put("adt_end", adt_end);
			parameters.put("al_yksb", yksb);
			parameters.put("ls_jgid", manageUnit);
			List<Map<String, Object>> dataSourceList = dao.doSqlQuery(
					dataSourceSql, parameters);
			drugRecordList = new ArrayList<Map<String, Object>>();
			sphUtil.drugRecordHandle(body, dataSourceList, drugRecordList);
			
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "查询药库采购历史模块中的药品记录失败", e);
		}
		return drugRecordList;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-8
	 * @description 查询药库采购历史模块中的药品明细
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> querySPHDrugDetails(
			Map<String, Object> body)
			throws ModelDataOperationException {
		StorehousePurchaseHistoryUtil sphUtil = new StorehousePurchaseHistoryUtil();
		body.put(StorehousePurchaseHistoryUtil.QUERYTYPE,
				StorehousePurchaseHistoryUtil.DRUG_DETAILS);
		String dataSourceSql = sphUtil.dataSource(body);
		List<Map<String, Object>> dataSourceList=null;
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();// 获取JGID
			long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));
			String adt_begin = String.valueOf(body.get("KSSJ") + "00:00:00");
			String adt_end = String.valueOf(body.get("JSSJ") + "23:59:59");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("adt_begin", adt_begin);
			parameters.put("adt_end", adt_end);
			parameters.put("al_yksb", yksb);
			parameters.put("ls_jgid", manageUnit);
			dataSourceList = dao.doSqlQuery(
					dataSourceSql, parameters);
			List<Map<String, Object>> drugRecordList = new ArrayList<Map<String, Object>>();
			sphUtil.durgDetailsHandle(body, dataSourceList, drugRecordList, dao);
			SchemaUtil.setDictionaryMassageForList(dataSourceList,
					"phis.application.sto.schemas.YK_DURG_DETAILS");
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "查询药库采购历史模块中的药品明细失败", e);
		}
		return dataSourceList;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-8
	 * @description 查询药库采购历史模块中的供应商记录
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> querySPHSupplierRecord(
			Map<String, Object> body)
			throws ModelDataOperationException {
		StorehousePurchaseHistoryUtil sphUtil = new StorehousePurchaseHistoryUtil();
		body.put(StorehousePurchaseHistoryUtil.QUERYTYPE,
				StorehousePurchaseHistoryUtil.SUPPLIER_RECORD);
		String dataSourceSql = sphUtil.dataSource(body);
		List<Map<String, Object>> dataSourceList=null;
		List<Map<String, Object>> supplierRecordList = new ArrayList<Map<String, Object>>();
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();// 获取JGID
			long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));
			String adt_begin = String.valueOf(body.get("KSSJ") + "00:00:00");
			String adt_end = String.valueOf(body.get("JSSJ") + "23:59:59");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("adt_begin", adt_begin);
			parameters.put("adt_end", adt_end);
			parameters.put("al_yksb", yksb);
			parameters.put("ls_jgid", manageUnit);
			dataSourceList = dao.doSqlQuery(
					dataSourceSql, parameters);
			// System.out.println(parameters.toString());
			sphUtil.supplierRecordHandle(body, dataSourceList,
					supplierRecordList, dao);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "查询药库采购历史模块中的供应商记录失败", e);
		}
		return supplierRecordList;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-8
	 * @description 查询药库采购历史模块中的供应商明细
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> querySPHSupplierDetails(
			Map<String, Object> body)
			throws ModelDataOperationException {
		StorehousePurchaseHistoryUtil sphUtil = new StorehousePurchaseHistoryUtil();
		body.put(StorehousePurchaseHistoryUtil.QUERYTYPE,
				StorehousePurchaseHistoryUtil.SUPPLIER_DETAILS);
		String dataSourceSql = sphUtil.dataSource(body);
		List<Map<String, Object>> dataSourceList=null;
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();// 获取JGID
			long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));
			String adt_begin = String.valueOf(body.get("KSSJ") + "00:00:00");
			String adt_end = String.valueOf(body.get("JSSJ") + "23:59:59");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("adt_begin", adt_begin);
			parameters.put("adt_end", adt_end);
			parameters.put("al_yksb", yksb);
			parameters.put("ls_jgid", manageUnit);
			dataSourceList = dao.doSqlQuery(
					dataSourceSql, parameters);
			SchemaUtil.setDictionaryMassageForList(dataSourceList,
					"phis.application.sto.schemas.YK_SUPPLIER_DETAILS");
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "查询药库采购历史模块中的供应商明细失败", e);
		}
		return dataSourceList;
	}
	
}
