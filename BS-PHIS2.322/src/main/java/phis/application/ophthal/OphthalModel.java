package phis.application.ophthal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;


/**
 * 眼科
 * @author zhangxx
 */
public class OphthalModel  {
	private static Logger logger = LoggerFactory.getLogger(OphthalModel.class);

	private BaseDAO dao;

	public OphthalModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	
	/**
	 * 是否是眼科
	 * @param params
	 * @return
	 * @throws ModelDataOperationException 
	 */
	public boolean isOphthal (String id) throws ModelDataOperationException {
		boolean flg = false;
		try {
			Map data = new HashMap();
			data.put("id",id);
			
			String sql = " select count(1) COUNT from ms_ghks a, sys_office b where a.mzks = b.id and b.mhjksy = '1' and a.ksdm= :id  ";
			Map map = dao.doSqlLoad(sql, data);
			
			int count = map!=null ? NumberUtils.toInt(String.valueOf(map.get("COUNT"))) : 0 ;
			flg = count>0;
		}catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "眼科查询失败！");
		}
		return flg;
	}
}
