/**
 * @(#)DiabetesIdLoader.java Created on Mar 16, 2010 8:43:36 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.ModelDataOperationException;
import chis.source.per.CheckupRecordModel;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class CheckupRecordIdLoader extends AbstractIdLoader {

	public static final String HAS_TOTAL_CHECKED = "hasTotalChecked";

	public static final String EDITABLE = "editable";

	// public String getLoadBy() {
	// return "checkupNo";
	// }

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return "PER_CheckupRegister";
	}
	
	@Override
	public String getEntityName() {
		return PER_CheckupRegister;
	}
	

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdColumn()
	 */
	public String getIdColumn() {
		return "checkupNo";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdName()
	 */
	public String getIdName() {
		return "PER_CheckupRegister.checkupNo";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.ehrview.AbstractIdLoader#load(java.lang.String,
	 * ctd.util.context.Context)
	 */
	@Override
	public Map<String, Object> load(String loadBy, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> ids = null;
		CheckupRecordModel crmModel = new CheckupRecordModel(ctx);
		try {
			ids = crmModel.idsLoader(loadBy);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		Map<String, Object> data = null;
		if (ids.size() > 0) {
			data = ids.get(0);
		}
		if (data != null) {
			if (data.get("totalCheckupDate") != null) {
				data.put(HAS_TOTAL_CHECKED, true);
				// data.put(EDITABLE, false);
				return data;
			} else {
				data.put(HAS_TOTAL_CHECKED, false);
			}
			// data.put(EDITABLE, true);
			return data;
		}
		data = new HashMap<String, Object>();
		// if (NOT_CREATED.equals(ctx.value(PHR_STATUS))) {
		// //data.put(EDITABLE, false);
		// } else {
		// //data.put(EDITABLE, true);
		// }
		data.put(HAS_TOTAL_CHECKED, false);
		return data;
	}





}
