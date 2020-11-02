/*
 * 
 */
package chis.source.pub;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.BizColumnConfig;
import chis.source.util.HQLHelper;
/**
 * 
 * @author messagewell
 *
 */
public class BizInfoUpdateModel {
	
	private static final Logger logger = LoggerFactory
			.getLogger(BizInfoUpdateModel.class);

	protected BaseDAO dao = null;
	
	public BizInfoUpdateModel(BaseDAO dao){
		this.dao = dao;
	}
	/**
	 * 
	 * @param bizName
	 * @param params
	 * @throws ModelDataOperationException 
	 * @throws PersistentDataOperationException 
	 */
	public void update(String bizName , Map<String,Object>params) throws ModelDataOperationException {
		List<String> sqlList = BizColumnConfig.getUpdateSql(bizName,params);
		try {
			for(String sql : sqlList){
				Map<String,Object> realParams = HQLHelper.selectParameters(sql, params);
				dao.doUpdate(sql, realParams);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
	}
	
	/**
	 * 
	 * @param bizName
	 * @param params
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public boolean checkInUse(String bizName , Map<String,Object> params) throws PersistentDataOperationException{
		List<String> sqlList = BizColumnConfig.getQuerySql(bizName,params);
		for(int i = 0 ;i<sqlList.size() ; i++){
			String hql = sqlList.get(i);
			List<Map<String, Object>> res = dao.doQuery(hql, null);
			Long count =(Long) res.get(0).get("count");
			if(count>0){
				return true ;
			}
		}
		return false ;
	}
	
	
}
