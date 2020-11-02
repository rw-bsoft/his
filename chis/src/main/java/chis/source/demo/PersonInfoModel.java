/**
 * 
 */
package chis.source.demo;


import java.util.Map;

import ctd.validator.ValidateException;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

/**
 * @author 86159
 *
 */
public class PersonInfoModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public PersonInfoModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存演示记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-7-8 下午10:00:14
	 * @Modify:
	 */
	public Map<String, Object> saveDemo(String op, Map<String, Object> record,boolean validate) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PersonInfo, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存演示记录数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存演示记录失败！", e);
		}
		return rsMap;
	}
	
	/**
	 * 
	 * @Description:根据主键获取人员信息
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-7-8 下午10:00:33
	 * @Modify:
	 */
	public Map<String, Object> getPersonInfoByPkey(String pkey) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(PersonInfo, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取人员信息失败！", e);
		}
		return rsMap;
	}

}
