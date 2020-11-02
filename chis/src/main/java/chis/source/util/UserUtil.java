/**
 * @(#)UserUtil.java Created on 2012-3-21 下午04:27:32
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.util;

import ctd.account.UserRoleToken;

/**
 * @description 获取用户的相关属性。
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class UserUtil {
	
	public static final String  MANAUNIT_ID="manaUnitId";//管辖机构编号
	public static final String  MANAUNIT_NAME="manaUnitId_text";//管辖机构名称
	
	public static final String  REGION_CODE="regionCode";//网格地址编号
	public static final String  REGION_NAME="regionCode_text";//网格地址名称
	
	public static final String  USER_ID="userId";//用户ID
	public static final String  USER_NAME="userName";//用户姓名
	
	public static final String  JOB_ID="jobId";//角色编号
	public static final String  JOB_TITLE="jobTitle";//角色名称
	
	public static final String  MAP_SIGN="mapSign";//%#￥…(*&...
	
	/**
	 * 获取用户属性。
	 * @param name
	 * @param ctx
	 * @return
	 */
	public static String get(String name){
		UserRoleToken ur = UserRoleToken.getCurrent();
		if(USER_ID.equals(name)){
			return ur.getUserId();
		}else if(USER_NAME.equals(name)){
			return ur.getUserName();
		}else if(MANAUNIT_ID.equals(name)){
			return ur.getManageUnitId();
		}else if(MANAUNIT_NAME.equals(name)){
			return  ur.getManageUnitName();
		}else if(REGION_CODE.equals(name)){
			return	ur.getRegionCode();
		}else if(REGION_NAME.equals(name)){
			return ur.getProperty("role.regionCode_text",String.class);
		}else if(JOB_ID.equals(name)){
			 return ur.getRoleId();
		}else if(JOB_TITLE.equals(name)){
			 return ur.getRoleName();
		}else if(MAP_SIGN.equals(name)){
			return (String) ur.getProperty("mapSign");
		}else {
			return null;
		}
	}
	
}
