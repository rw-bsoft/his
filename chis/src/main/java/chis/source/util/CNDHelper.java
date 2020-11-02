/**
 * @(#)CNDHelper.java Created on 2012-1-5 上午11:37:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import ctd.util.exp.ExpException;

/**
 * @description 根据传入的条件提供查询功能
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">tianj</a>
 */
public class CNDHelper {

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static List<Object> createArrayCnd(Object key, Object value) {
		List<Object> array = (List<Object>) new ArrayList<Object>();
		array.add(key);
		array.add(value);
		return array;
	}

	/**
	 * 创建基于一个单独列的简单查询CND
	 * @param exp
	 * @param columnName
	 * @param columnType
	 * @param value
	 * @return
	 */
	public static List<Object> createSimpleCnd(String exp, String columnName,
			String columnType, Object value) {
		List<Object> colCnd = CNDHelper.createArrayCnd("$", columnName);
		List<Object> valCnd = CNDHelper.createArrayCnd(columnType, value);
		return createArrayCnd(exp, colCnd, valCnd);
	}
	
	/**
	 * 创建一个IN的表达式
	 * CNDHelper.craeteInCnd("empiId",new String[]{"1","2","3"});
	 */
	public static List<Object> createInCnd(String columnName,String[]values){
		List<String> valCnd = new ArrayList<String>();
		for(int i = 0 ;i <values.length ;i++){
			valCnd.add(values[i]);
		}
		return createInCnd(columnName, valCnd);
	}
	
	/**
	 * 创建一个IN的表达式
	 * @param columnName
	 * @param values
	 * @return
	 */
	public static List<Object> createInCnd(String columnName,List<String>values){
		String exp = "in";
		List<Object> colCnd = CNDHelper.createArrayCnd("s", columnName);
		return createArrayCnd(exp,colCnd,values);
	}
 	
	/**
	 * 创建一个IN的表达式
	 * CNDHelper.craeteInCnd("empiId",new String[]{"1","2","3"});
	 */
	public static List<Object> createInCnds(String columnName,Object[]values){
		List<Object> valCnd = new ArrayList<Object>();
		for(int i = 0 ;i <values.length ;i++){
			valCnd.add(values[i]);
		}
		return createInCnds(columnName, valCnd);
	}
	
	/**
	 * 创建一个IN的表达式
	 * @param columnName
	 * @param values
	 * @return
	 */
	public static List<Object> createInCnds(String columnName,List<Object>values){
		String exp = "in";
		List<Object> colCnd = CNDHelper.createArrayCnd("$", columnName);
		return createArrayCnd(exp,colCnd,values);
	}
	
	

	/**
	 * 创建基于一个单独列的简单查询CND
	 * @param exp
	 * @param columnName
	 * @param columnType
	 * @param value
	 * @return
	 */
	public static String createSimpleStringCnd(String exp, String columnName,
			String columnType, Object value) {
		List<Object> cnd = createSimpleCnd(exp, columnName, columnType, value);
		return toStringCnd(cnd);
	}

	/**
	 * 用exp将两个cnd list拼接起来。
	 * @param exp
	 * @param cnd1
	 * @param cnd2
	 * @return
	 */
	public static List<Object> createArrayCnd(String exp, List<?> cnd1,
			List<?> cnd2) {
		List<Object> array = (List<Object>) new ArrayList<Object>();
		array.add(exp);
		array.add(cnd1);
		array.add(cnd2);
		return array;
	}

	/**
	 * 创建String型cnd
	 * @param exp
	 * @param cnd1
	 * @param cnd2
	 * @return
	 */
	public static String createArrayStringCnd(String exp, List<?> cnd1,
			List<?> cnd2) {
		List<Object> array = createArrayCnd(exp, cnd1, cnd2);
		return toStringCnd(array);
	}

	/**
	 * 将字符串形式的cnd转成List
	 * 如果表达式中存在单引号的不应该调用该方法。
	 * @param s
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static List<?> toListCnd(String s) throws ExpException{
		ObjectMapper jsonMapper = MapperUtil.getJsonMapper();
		jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		List<?> exp;
		try {
			exp = jsonMapper.readValue(s, List.class);
		} catch (Exception e) {
			System.out.println("error exp:"+s);
			throw new ExpException("错误的表达式:");
		}
		return exp;
	}

	/**List cnd 转成string型
	 * @param cnd
	 * @return
	 */
	public static String toStringCnd(List<Object> cnd) {
		ObjectMapper jsonMapper = MapperUtil.getJsonMapper();
		
		try {
			return jsonMapper.writeValueAsString(cnd);
		} catch (Exception e) {
			throw new RuntimeException("cnd format error.");
		}
	}
	
	public static void main(String [] rags) throws ExpException{
		String initCnd = "['and',['and',['like',['$','regionCode'],['concat',['s','330106002001'],['s','%']]],['eq',['len','regioncode'],['s','16']]],['ne',['$','regionCode'],['s','330106002001']]]";
		System.out.println(toListCnd(initCnd));
	}
}