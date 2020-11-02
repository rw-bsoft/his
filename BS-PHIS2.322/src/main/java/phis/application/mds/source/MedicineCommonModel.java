package phis.application.mds.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
/**
 * 药品模块基本业务类,一些公用的业务方法
 * @author caijy
 *
 */
public class MedicineCommonModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(MedicineCommonModel.class);
	public MedicineCommonModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 验证是否重复
	 * @updateInfo 
	 * @param body body包含的参数 :keyName 要判断的键名,keyValue 判断的键对应的值,tableName 表名,PKName 主键名(如果有的话,一般用于修改的时候排除掉本身的值,不需要的话传null),PKValue 主键值
	 * @param dao
	 * @return 有重复返回true 否则false
	 * @throws ModelDataOperationException
	 */
	public boolean repeatVerification(Map<String,Object> body)throws ModelDataOperationException{
		String keyName=MedicineUtils.parseString(body.get("keyName"));
		Object keyValue=body.get("keyValue");
		String tableName=MedicineUtils.parseString(body.get("tableName"));
		String PKName=MedicineUtils.parseString(body.get("PKName"));
		Object PKValue=body.get("PKValue");
		if("".equals(keyName)||"".equals(tableName)||keyValue==null){
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询重复失败:程序错误");
		}
		StringBuffer hql=new StringBuffer();
		hql.append("select count(1) as NUM from ").append(tableName).append(" where ").append(keyName).append("=:keyValue");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("keyValue", keyValue);
		if(PKName!=null&&!"".equals(PKName)){
			hql.append(" and ").append(PKName).append("!=:PKValue");
			map_par.put("PKValue", PKValue);
		}
		try {
			List<Map<String,Object>> list_ret=dao.doSqlQuery(hql.toString(), map_par);
			if(list_ret==null||list_ret.size()==0){
				return false;
			}
			long l=MedicineUtils.parseLong(list_ret.get(0).get("NUM"));
			if(l>0){
				return true;
			}
			return false;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询重复失败:"+e.getMessage());
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 查询分页数据
	 * @updateInfo
	 * @param req
	 * @param map_par
	 * @param hql
	 * @param dao
	 * @param scamelName 需要转换字典的传scamel名字,如果不需要传null
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> getPageInfoRecord(Map<String,Object> req,Map<String,Object> map_par,String hql,String scamelName)throws ModelDataOperationException{
		StringBuffer hql_count = new StringBuffer();
		if(hql.contains("JHJE") && hql.contains("LSJE") ){
			hql_count.append("select count(*) as NUM,sum(JHJE) as JHJE,sum(LSJE) as LSJE from (")
			.append(hql).append(")");
		}
		else{
			hql_count.append("select count(*) as NUM from (")
			.append(hql).append(")");		
		}
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
		List<Map<String, Object>> list_count = dao.doSqlQuery(
					hql_count.toString(), map_par);
		if(list_count==null||list_count.size()==0){
			ret.put("totalCount", 0);
			ret.put("body", null);
			return ret;
		}
		ret.put("totalCount", list_count.get(0).get("NUM"));
		ret.put("totalJhje", list_count.get(0).get("JHJE"));
		ret.put("totalLsje", list_count.get(0).get("LSJE"));
		MedicineUtils.getPageInfo(req, map_par);
		List<Map<String,Object>> list = dao.doSqlQuery(hql, map_par);
		if(scamelName!=null){
			SchemaUtil.setDictionaryMassageForList(list, scamelName);
		}
		ret.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败:"+e.getMessage());
		}
		return ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-20
	 * @description 查询当前选择的 药房.用于解决集群时不能获得当前药房的BUG
	 * @updateInfo
	 * @param user
	 * @param dao
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long getMryf(UserRoleToken user) throws ModelDataOperationException{
		StringBuffer hql=new StringBuffer();
		hql.append("select KSDM as KSMD from GY_QXKZ where YGDM=:ygdm and YWLB=:ywlb and JGID=:jgid");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("ygdm", user.getUserId());
		map_par.put("jgid", user.getManageUnit().getId());
		map_par.put("ywlb", "1");
		long yfsb=0;
		try {
			Map<String,Object> map_mryf=dao.doLoad(hql.toString(), map_par);
			if(map_mryf!=null){
				yfsb= MedicineUtils.parseLong(map_mryf.get("KSMD"));
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询默认药房失败:"+e.getMessage());
		}
		return yfsb;
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-4-10
	 * @description 删除过期的库存冻结
	 * @updateInfo
	 * @param jgid
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void deleteKCDJ(String jgid,Context ctx) throws ModelDataOperationException{
		double KCDJTS=0;
		try{
		KCDJTS= MedicineUtils.parseDouble(ParameterUtil
				.getParameter(jgid, BSPHISSystemArgument.KCDJTS, ctx));
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.KCDJTS, e);
		}
		StringBuffer hql_kcdj_delete =new StringBuffer();//删除过期冻结信息
		hql_kcdj_delete.append("delete from YF_KCDJ where sysdate-DJSJ >:kcdjts and JGID=:jgid");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("kcdjts", KCDJTS);
		map_par.put("jgid", jgid);
		try {
			dao.doSqlUpdate(hql_kcdj_delete.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除过期库存失败:"+e.getMessage());
		}
	}
}
