/*
 * @(#)ServiceErrorCode.java Created on 2011-12-21 下午3:17:53
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.service;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public abstract class ServiceCode {

	// @@ 系统级代码。
	public static final int CODE_OK = 200;
	public static final int CODE_MISSING_SERVICE_ID = 401;
	public static final int CODE_SERVICE_AUTHORIZATION_FAIL = 402;
	public static final int CODE_SERIVCE_NOT_FOUND = 406;

	// @@ 业务处理代码。
	//add by caijy
	public static final int CODE_RECORD_REPEAT=9000;//记录重复
	public static final int CODE_RECORD_USING=9001;//记录被使用
	public static final int NO_RECORD=9002;//没记录
	public static final int CODE_ERROR=9003;//有错误
	// 根据EMPIID查找信息，EMPIID在数据库中不存在
	public static final int CODE_EMPIID_NOT_EXISTS = 3001;
	public static final int CODE_TARGET_EXISTS = 3002;
	public static final int CODE_REQUEST_PARSE_ERROR = 3004;
	public static final int CODE_RESPONSE_PARSE_ERROR = 3005;
	public static final int CODE_BUSINESS_DATA_NULL = 3006;
	public static final int CODE_NECESSARY_PART_MISSING = 3007;
	public static final int CODE_HYPERTENSION_RECORD_WRITEOFF = 2;
	public static final int CODE_INVALID_REQUEST = 4000;
	public static final int CODE_UNSUPPORTED_ENCODING = 4001;
	public static final int CODE_NOT_FOUND = 4004;
	public static final int CODE_SERVICE_ERROR = 4500;
	public static final int CODE_NOT_LOGON = 4090;
	public static final int CODE_IO_EXCEPTION = 5002;
	public static final int CODE_RECORD_EXSIT = 5505;
	public static final int CODE_RECORD_NOT_FOUND = 5506;
	public static final int CODE_UNKNOWN_ERROR = 6000;
	public static final int CODE_DATABASE_ERROR = 7000;
	public static final int CODE_NAME_NOT_MATCHED = 7500;
	public static final int CODE_UNAUTHORIZED = 8000;
	public static final int CODE_DATE_PASE_ERROR = 8001;
	public static final String CODE_STATUS_NORMAL = "0";
	public static final String CODE_STATUS_WRITE_OFF = "1";
	public static final String CODE_STATUS_NOT_AUDIT = "2";
	public static final String CODE_STATUS_END_MANAGE = "3";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_SHORT_DATE_FORMAT = "yyyy-MM-dd";
	public static final int CODE_NO_NEED_VISIT = 0;
	public static final int CODE_NEED_VISIT = 1;
	public static final int CODE_NEW_WINDOW = 900;
	public static final int CODE_ENCEYPT_ERROR = 1000;// @@ 加解密错误。
}
