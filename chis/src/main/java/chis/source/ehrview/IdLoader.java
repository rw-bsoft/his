/**
 * @(#)IdLoader.java Created on Mar 16, 2010 6:44:07 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;

import java.util.Map;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public interface IdLoader {

	public static final String ID = "id";
	public static final String LOADBY_EMPIID = "empiId";
	public static final String LOADBY_PK = "Pkey";
	public static final String LOADBY_TYPE = "recordType";// 档案类型
	public static final String STATUS = "status";
	public static final String CLOSEFLAG = "closeFlag";

	/**
	 * 未建立。
	 */
	public static final String NOT_CREATED = "4";

	/**
	 * 是否有状态这个属性。
	 * 
	 * @return
	 */
	public boolean hasStatus();

	/**
	 * 是否有结案标记
	 */
	public boolean hasCloseFlag();

	/**
	 * 是否存在多条档案，类似血吸虫、糖尿病等。 如果true，idloader将选择状态最正常的一条返回到前台，
	 * 用于判断EHRVIEW树是否显示红++，以及是否重点人群。
	 */
	public boolean hasMutiRecords();

	/**
	 * 根据哪个字段去查id。
	 * 
	 * @return
	 */
	public String getLoadBy();

	/**
	 * 根据哪个字段去查id。
	 * 
	 * @return
	 */
	public String getLoadPkey();

	/**
	 * 根据哪个类型去查id。
	 * 
	 * @return
	 */
	public String getLoadType();

	/**
	 * 获取对应的表名。
	 * 
	 * @return
	 */
	public String getEntryName();

	/**
	 * 获取对应schema名字
	 * 
	 * @return
	 */
	public String getEntityName();

	/**
	 * id的列名称。
	 * 
	 * @return
	 */
	public String getIdColumn();

	/**
	 * id参数的名称。
	 * 
	 * @return
	 */
	public String getIdName();

	/**
	 * id值。
	 * 
	 * @return
	 */
	public String getId(Map<String, Object> data);

	/**
	 * 记录状态。
	 * 
	 * @return
	 */
	public String getStatus(Map<String, Object> data);

	/**
	 * @param loadBy
	 * @param session
	 * @param ctx
	 * @throws ServiceException
	 */
	public Map<String, Object> load(String loadBy, Context ctx)
			throws ServiceException;

	/**
	 * @param loadBy
	 * @param loadType
	 * @param session
	 * @param ctx
	 * @throws ServiceException
	 */
	public Map<String, Object> load(String loadBy, String loadType, Context ctx)
			throws ServiceException;

	/**
	 * 
	 * @Description:
	 * @param pKey
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2014-9-17 上午10:28:57
	 * @Modify:
	 */
	public Map<String, Object> loadByPkey(String pKey, Context ctx)
			throws ServiceException;

	/**
	 * 是否为只读。
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public Map<String, Boolean> getControlData(Map<String, Object> data,
			Context ctx) throws ServiceException;

}
