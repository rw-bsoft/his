/**
 * @(#)AbstractIdLoader.java Created on Mar 16, 2010 6:45:28 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public abstract class AbstractIdLoader implements IdLoader, BSCHISEntryNames {

	/**
	 * @see chis.source.biz.ehrview.IdLoader#getLoadBy()
	 */
	public String getLoadBy() {
		return LOADBY_EMPIID;
	}

	/**
	 * @see chis.source.biz.ehrview.IdLoader#getLoadBy()
	 */
	public String getLoadPkey() {
		return LOADBY_PK;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.ehrview.IdLoader#getLoadType()
	 */
	@Override
	public String getLoadType() {
		return LOADBY_TYPE;
	}

	/**
	 * @see chis.source.biz.ehrview.IdLoader#hasCloseFlag()
	 */
	public boolean hasCloseFlag() {
		return false;
	}

	/***
	 * @see chis.source.biz.ehrview.IdLoader#hasMutiRecords()
	 */
	public boolean hasMutiRecords() {
		return false;
	}

	/**
	 * 按钮权限控制属性名称
	 * 
	 * @return
	 */
	public String getControlName() {
		return getEntryName() + "_control";
	}

	/**
	 * @param loadBy
	 * @param phrStatus
	 * @param session
	 * @throws ServiceException
	 */
	public Map<String, Object> load(String loadBy, Context ctx)
			throws ServiceException {
		Map<String, Object> datas = getRecordDatas(loadBy, ctx);
		if (datas == null) {
			return new HashMap<String, Object>();
		}
		return datas;
	}

	/**
	 * @param loadBy
	 * @param loadType
	 * @param phrStatus
	 * @param session
	 * @throws ServiceException
	 */
	public Map<String, Object> load(String loadBy, String loadType, Context ctx)
			throws ServiceException {
		Map<String, Object> datas = getRecordDatas(loadBy, loadType, ctx);
		if (datas == null) {
			return new HashMap<String, Object>();
		}
		return datas;
	}
	
	/**
	 * 
	 */
	public Map<String, Object> loadByPkey(String pKey, Context ctx)
			throws ServiceException{
		Map<String, Object> datas = getRecordByPkey(pKey, ctx);
		if (datas == null) {
			return new HashMap<String, Object>();
		}
		return datas;
	}

	/**
	 * @see chis.source.biz.ehrview.IdLoader#getId()
	 */
	public String getId(Map<String, Object> recordDatas) {
		return (String) recordDatas.get(getIdColumn());
	}

	/**
	 * @see chis.source.biz.ehrview.IdLoader#getStatus()
	 */
	public String getStatus(Map<String, Object> datas) {
		if (datas.get(STATUS) == null) {
			return NOT_CREATED;
		}
		return (String) datas.get(STATUS);
	}

	/**
	 * @throws ServiceException
	 * @see chis.source.biz.ehrview.IdLoader#getControlData()
	 */
	public Map<String, Boolean> getControlData(Map<String, Object> data,
			Context ctx) throws ServiceException {
		return ControlRunner.run(getEntityName(), data, ctx,
				ControlRunner.CREATE, ControlRunner.UPDATE);
	}

	/**
	 * 获取档案记录数据
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	protected Map<String, Object> getRecordDatas(String loadBy, Context ctx)
			throws ServiceException {
		BaseDAO dao = new BaseDAO();
		String loadByFiled = getLoadBy();
		String loadByValue = (String) ctx.get(loadByFiled);
		if ("".equals(loadByValue) || loadByValue == null) {
			loadByFiled = LOADBY_EMPIID;
		}
		List<?> cnd = CNDHelper.createSimpleCnd("eq", loadByFiled, "s", loadBy);
		List<Map<String, Object>> datas;
		try {
			datas = dao.doQuery(cnd, null, getEntityName());
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(e);
		}
		if (datas.size() == 0) {
			return new HashMap<String, Object>();
		} else if (datas.size() == 1) {
			return datas.get(0);
		} else {
			return BSCHISUtil.getTopRecord(datas);
		}
	}

	/**
	 * 获取档案记录数据
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	protected Map<String, Object> getRecordDatas(String loadBy, String loadType, Context ctx)
			throws ServiceException {
		BaseDAO dao = new BaseDAO();
		String loadByFiled = getLoadBy();
		String loadByValue = (String) ctx.get(loadByFiled);
		if ("".equals(loadByValue) || loadByValue == null) {
			loadByFiled = LOADBY_EMPIID;
		}
		String loadByTypeFiled = getLoadType();
		String loadByTypeValue = (String) ctx.get(loadByTypeFiled);
		if("".equals(loadByTypeValue) || loadByValue == null){
			loadByTypeFiled = LOADBY_TYPE;
		}
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", loadByFiled, "s", loadBy);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", loadByTypeFiled, "s", loadType);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> datas;
		try {
			datas = dao.doQuery(cnd, null, getEntityName());
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(e);
		}
		if (datas.size() == 0) {
			return new HashMap<String, Object>();
		} else if (datas.size() == 1) {
			return datas.get(0);
		} else {
			return BSCHISUtil.getTopRecord(datas);
		}
	}
	
	/**
	 * 获取档案记录数据
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	protected Map<String, Object> getRecordByPkey(String pKey, Context ctx)
			throws ServiceException {
		BaseDAO dao = new BaseDAO();
		String loadByPKFiled = getLoadPkey();
		String loadByPKValue = (String) ctx.get(loadByPKFiled);
		if ("".equals(loadByPKValue) || loadByPKValue == null) {
			loadByPKFiled = LOADBY_EMPIID;
		}
		Map<String, Object> datas = null;
		try {
			datas = dao.doLoad(getEntityName(), pKey);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(e);
		}
		if (datas != null && datas.size() >= 0) {
			return datas;
		} else  {
			return new HashMap<String, Object>();
		}
	}
}
