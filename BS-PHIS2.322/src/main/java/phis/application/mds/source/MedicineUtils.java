package phis.application.mds.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import ctd.account.UserRoleToken;
import ctd.schema.DisplayModes;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.security.Permission;
import ctd.util.context.Context;



import phis.source.BSPHISSystemArgument;
import phis.source.ModelDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;

public class MedicineUtils {
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public static long parseLong(Object o) {
		if (o == null||"".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public static double parseDouble(Object o) {
		if (o == null||"".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public static int parseInt(Object o) {
		if (o == null||"".equals(o)) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public static String parseString(Object o) {
		if (o == null) {
			return "";
		}
		return o + "";
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description double型数据转换
	 * @updateInfo
	 * @param number 保留小数点后几位
	 * @param data 数据
	 * @return
	 */
	public static double formatDouble(int number, double data) {
		return BSPHISUtil.getDouble(data,number);
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 获取成功返回的Map
	 * @updateInfo
	 * @return
	 */
	public static Map<String,Object> getRetMap(){
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("code", ServiceCode.CODE_OK);
		map_ret.put("msg", "OK");
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 根据错误信息返回Map
	 * @updateInfo
	 * @param errMsg
	 * @return
	 */
	public static Map<String,Object> getRetMap(String errMsg){
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("code", ServiceCode.CODE_ERROR);
		map_ret.put("msg", errMsg);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 根据错误码和错误信息返回Map
	 * @updateInfo
	 * @param errMsg
	 * @param errCode
	 * @return
	 */
	public static Map<String,Object> getRetMap(String errMsg,int errCode){
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("code", errCode);
		map_ret.put("msg", errMsg);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 获取带分页的返回Map
	 * @updateInfo
	 * @param totalCount
	 * @param body
	 * @return
	 */
	public static Map<String,Object> getRetMap(long totalCount,List<Map<String,Object>> body){
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("totalCount", totalCount);
		map_ret.put("body", body);
		return map_ret;
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
//	public static boolean repeatVerification(Map<String,Object> body,BaseDAO dao)throws ModelDataOperationException{
//		String keyName=parseString(body.get("keyName"));
//		Object keyValue=body.get("keyValue");
//		String tableName=parseString(body.get("tableName"));
//		String PKName=parseString(body.get("PKName"));
//		Object PKValue=body.get("PKValue");
//		if("".equals(keyName)||"".equals(tableName)||keyValue==null||dao==null){
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "查询重复失败:程序错误");
//		}
//		StringBuffer hql=new StringBuffer();
//		hql.append("select count(1) as NUM from ").append(tableName).append(" where ").append(keyName).append("=:keyValue");
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("keyValue", keyValue);
//		if(PKName!=null&&!"".equals(PKName)){
//			hql.append(" and ").append(PKName).append("!=:PKValue");
//			map_par.put("PKValue", PKValue);
//		}
//		try {
//			List<Map<String,Object>> list_ret=dao.doSqlQuery(hql.toString(), map_par);
//			if(list_ret==null||list_ret.size()==0){
//				return false;
//			}
//			long l=parseLong(list_ret.get(0).get("NUM"));
//			if(l>0){
//				return true;
//			}
//			return false;
//		} catch (PersistentDataOperationException e) {
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "查询重复失败");
//		}
//	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 抛异常
	 * @updateInfo
	 * @param logger 日志
	 * @param errMsg 抛给界面的错误信息
	 * @param e 捕获的异常
	 * @throws ModelDataOperationException
	 */
	public static void throwsException(Logger logger,String errMsg,Exception e)throws ModelDataOperationException{
		logger.error(errMsg, e);
		throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,
				errMsg,e.getCause());
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-5-27
	 * @description 系统参数设置错误,抛异常
	 * @updateInfo
	 * @param logger
	 * @param ParameterName
	 * @param e
	 * @throws ModelDataOperationException
	 */
	public static void throwsSystemParameterException(Logger logger,String ParameterName,Exception e)throws ModelDataOperationException{
		logger.error("系统参数设置有误,参数名["+ParameterName+"]", e);
		throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "系统参数设置有误,参数名["+ParameterName+"]");
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-26
	 * @description 查询批零加成
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static double queryPljc(Context ctx)throws ModelDataOperationException{
		return parseDouble(ParameterUtil.getParameter(ParameterUtil.getTopUnitId(), BSPHISSystemArgument.PLJC, ctx));
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-27
	 * @description 获取分页查询信息
	 * @updateInfo
	 * @param body 带有pageSize和pageNo的集合
	 * @param map_par 查询参数
	 */
	public static void getPageInfo(Map<String,Object> body,Map<String,Object> map_par){
		if(!body.containsKey("pageSize")||!body.containsKey("pageNo")){
			return;
		}
		int pageSize = parseInt(body.get("pageSize"));
		int pageNo = parseInt(body.get("pageNo"))-1;
		map_par.put("first", pageNo * pageSize);
		map_par.put("max", pageSize);
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
//	public static Map<String,Object> getPageInfoRecord(Map<String,Object> req,Map<String,Object> map_par,String hql,BaseDAO dao,String scamelName)throws ModelDataOperationException{
//		StringBuffer hql_count = new StringBuffer();
//		hql_count.append("select count(*) as NUM from (")
//		.append(hql).append(")");
//		Map<String,Object> ret=new HashMap<String,Object>();
//		try {
//		List<Map<String, Object>> list_count = dao.doSqlQuery(
//					hql_count.toString(), map_par);
//		if(list_count==null||list_count.size()==0){
//			ret.put("totalCount", 0);
//			ret.put("body", null);
//			return ret;
//		}
//		ret.put("totalCount", list_count.get(0).get("NUM"));
//		getPageInfo(req, map_par);
//		List<Map<String,Object>> list = dao.doSqlQuery(hql, map_par);
//		if(scamelName!=null){
//			SchemaUtil.setDictionaryMassageForList(list, scamelName);
//		}
//		ret.put("body", list);
//		} catch (PersistentDataOperationException e) {
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
//		}
//		return ret;
//	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 用于判断当前操作用户有没有yfsb
	 * @updateInfo
	 * @param ctx
	 * @return
	 */
	public static boolean verificationPharmacyId(Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb =parseLong(user.getProperty("pharmacyId"));
		if (yfsb == 0) {
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 判断2个map指定的key的值是否全部相同
	 * @updateInfo
	 * @param map_1
	 * @param s_1
	 * @param map_2
	 * @param s_2
	 * @return
	 */
	public static boolean compareMaps(Map<String, Object> map_1, String[] s_1,
			Map<String, Object> map_2, String[] s_2) {
		boolean tag=true;
		for (int i = 0; i < s_1.length; i++) {
			if(map_1.get(s_1[i]) instanceof Double||map_2.get(s_2[i]) instanceof Double){
				tag=parseDouble(map_1.get(s_1[i]))==parseDouble(map_2.get(s_2[i]));
			}else if(map_1.get(s_1[i]) instanceof String){
				tag=parseString(map_1.get(s_1[i])).equals(parseString(map_2.get(s_2[i])));
			}else if(map_1.get(s_1[i]) instanceof Integer){
				tag=parseInt(map_1.get(s_1[i]))==parseInt(map_2.get(s_2[i]));
			}else if(map_1.get(s_1[i]) instanceof Long){
				tag=parseLong(map_1.get(s_1[i]))==parseLong(map_2.get(s_2[i]));
			}else{
				if(map_1.get(s_1[i]) != null && map_1.get(s_1[i]).toString().trim().length() > 0) {
					if(map_1.get(s_1[i]).toString().equals(map_2.get(s_2[i])+"")) {
						tag = true;
					}else{
						tag = false;
					}
				}else {
					if(map_1.get(s_1[i]) == null && map_2.get(s_2[i]) == null) {
						tag = true;
					}else {
						return false;
					}
				}
			}
			if(!tag) {//add by yangl 任意一次不匹配,则返回false
				return false;
			}
		}
		return tag;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-24
	 * @description 基本的乘法
	 * @updateInfo
	 * @param n
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static double simpleMultiply(int n,Object num1,Object num2){
		return formatDouble(n, parseDouble(num1)*parseDouble(num2));
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-3
	 * @description 通过主键(或唯一标识)找到结果集中对应的记录
	 * @updateInfo
	 * @param records
	 * @param key
	 * @return
	 */
	public static Map<String,Object> getRecord(List<Map<String,Object>> records,long key,String keyName){
		for(Map<String,Object> record:records){
			if(MedicineUtils.parseLong(record.get(keyName))==key){
				return record;
			}
		}
		return null;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-2
	 * @description 通过对应字段找到结果集中对应的记录
	 * @updateInfo
	 * @param records 数据结果集
	 * @param s_1  结果集中对应的字段名称
	 * @param map 需要批对的数据
	 * @param s_2  批对数据的字段名称
	 * @return
	 */
	public static Map<String,Object> getRecord(List<Map<String,Object>> records,String[] s_1,Map<String, Object> map,String[] s_2){
		for(Map<String,Object> record:records){
			if(compareMaps(record,s_1,map,s_2)){
				return record;
			}
		}
		return null;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-28
	 * @description 通过对应字段找到结果集中对应的记录(list)
	 * @updateInfo
	 * @param records 数据结果集
	 * @param s_1 结果集中对应的字段名称
	 * @param map 需要批对的数据
	 * @param s_2 批对数据的字段名称
	 * @return
	 */
	public static List<Map<String,Object>> getListRecord(List<Map<String,Object>> records,String[] s_1,Map<String, Object> map,String[] s_2){
		List<Map<String,Object>> l=new ArrayList<Map<String,Object>>();
		for(Map<String,Object> record:records){
			if(compareMaps(record,s_1,map,s_2)){
				l.add(record);
			}
		}
		return l;
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
//	public static long getMryf(UserRoleToken user,BaseDAO dao) throws ModelDataOperationException{
//		StringBuffer hql=new StringBuffer();
//		hql.append("select KSDM as KSMD from GY_QXKZ where YGDM=:ygdm and YWLB=:ywlb and JGID=:jgid");
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("ygdm", user.getId());
//		map_par.put("jgid", user.getManageUnit().getId());
//		map_par.put("ywlb", "1");
//		long yfsb=0;
//		try {
//			Map<String,Object> map_mryf=dao.doLoad(hql.toString(), map_par);
//			if(map_mryf!=null){
//				yfsb= parseLong(map_mryf.get("KSMD"));
//			}
//		} catch (PersistentDataOperationException e) {
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "查询默认药房失败");
//		}
//		return yfsb;
//		
//	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-11-2
	 * @description 根据sc返回select语句
	 * @updateInfo
	 * @param scName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getSelectHql(String scName){
		StringBuffer hql=new StringBuffer("");
		Schema sc = SchemaController.instance().getSchema(scName);
		List<SchemaItem> items = sc.getItems();
		for(SchemaItem it:items){
			if(it.isVirtual()){
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();
			if(!(p.getMode().isAccessible())){
				continue;
			}
			if(it.getDisplayMode() == DisplayModes.NO_LIST_DATA){
				continue;
			}
			if(it.hasProperty("refAlias")){
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				if(hql.length()==0){
					hql.append("select ").append(refAlias).append(".").append(fid).append(" as ").append(fid);
				}else{
					hql.append(",").append(refAlias).append(".").append(fid).append(" as ").append(fid);
				}
				
			}else{
				if(hql.length()==0){
					hql.append("select ").append("a.").append(fid).append(" as ").append(fid);
				}else{
					hql.append(",").append("a.").append(fid).append(" as ").append(fid);
				}
				
			}
		}
		return hql.toString();
	}
	
}
