/**
 * @(#)Constants.java Created on Aug 19, 2009 11:35:35 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source;

import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public abstract class Constants {
	
	// **本项目域名
	public static final String DOMIN = "chis";
	// **公共app文件
	public static final String UTIL_APP_ID="chis.application.util.UTIL";
	
	//分页，默认页记录数
    public static final int DEFAULT_PAGESIZE=25;
    
    //分页，默认页数
    public static final int DEFAULT_PAGENO = 1;	
    
	// @@ 默认头像的记录编号。
	public static final String DEFAULT_PHOTO_ID = "0000000000000000.jpg";

	public static final int CODE_OK = 200;
	
	// 根据EMPIID查找信息，EMPIID在数据库中不存在
	public static final int CODE_EMPIID_NOT_EXISTS = 301;
	public static final int CODE_TARGET_EXISTS = 302;
	public static final int CODE_REQUEST_PARSE_ERROR = 304;
	public static final int CODE_RESPONSE_PARSE_ERROR = 305;
	public static final int CODE_BUSINESS_DATA_NULL = 306;
	public static final int CODE_NECESSARY_PART_MISSING = 307;
	public static final int CODE_HYPERTENSION_RECORD_WRITEOFF = 2;
	public static final String CODE_WRITEOFF_CONFIRMED = "1";
	public static final int CODE_INVALID_REQUEST = 400;
	public static final int CODE_NOT_FOUND = 404;
	public static final int CODE_EXP_ERROR = 444;
	public static final int CODE_PERSISTENT_ERROR = 445;
	public static final int CODE_SERVICE_ERROR = 450;
	public static final int CODE_SERVICE_AUTHORIZATION_FAIL = 488;
	public static final int CODE_RECORD_EXSIT = 555;
	public static final int CODE_RECORD_NOT_FOUND = 556;
	public static final int CODE_UNKNOWN_ERROR = 600;
	public static final int CODE_INPUTSTREAM_TO_BYTE_ERROR = 650;
	public static final int CODE_STRING_TO_INPUTSTREAM_ERROR = 651;
	public static final int CODE_DATABASE_ERROR = 700;
	public static final int CODE_NAME_NOT_MATCHED = 750;
	public static final int CODE_UNAUTHORIZED = 800;
	public static final int CODE_DATE_PASE_ERROR = 801;
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
	public static final String CODE_CLOSEFLAG_NO="0";
	public static final String CODE_CLOSEFLAG_YES="1";
	public static final String DATAFORMAT4FORM = "_data";
	public static final String DATAFORMAT4LIST = "_list";
	
	public static final String EXP_EQ = "eq";

	public static final Map<Integer, String> PIX_MSG = new HashMap<Integer, String>();
	static {
		PIX_MSG.put(200, "请求执行成功。");
		PIX_MSG.put(400, "非法的请求。");
		PIX_MSG.put(401, "缺少EMPIID！");
		PIX_MSG.put(500, "服务内部错误！");
		PIX_MSG.put(501, "索引写入错误。");
		PIX_MSG.put(502, "索引删除出错。");
		PIX_MSG.put(600, "未知错误！");
		PIX_MSG.put(601, "协议错误！");
		PIX_MSG.put(602, "个人信息检索返回结果过多。");
		PIX_MSG.put(603, "未检索到个人信息。");
		PIX_MSG.put(604, "个人信息已存在。");
		PIX_MSG.put(605, "进行个人信息合并时，两个个人信息的字段有冲突。");
		PIX_MSG.put(606, "执行个人信息合并请求时，EMPIID为空。");
		PIX_MSG.put(607, "本地编号已存在。");
		PIX_MSG.put(608, "该卡不存在。");
		PIX_MSG.put(609, "卡类别不存在。");
		PIX_MSG.put(610, "卡注销失败！");
		PIX_MSG.put(611, "卡注册失败！");
		PIX_MSG.put(612, "机构不存在。");
		PIX_MSG.put(613, "该卡已被注册！");
		PIX_MSG.put(614, "已注册过该种卡。");
		PIX_MSG.put(615, "该身份证已被注册。");
		PIX_MSG.put(700, "数据库错误。");
		PIX_MSG.put(701, "个人基本信息入库错误。");
		PIX_MSG.put(702, "个人基本信息查询错误。");
		PIX_MSG.put(703, "个人基本信息更新错误。");
		PIX_MSG.put(704, "个人基本信息删除错误。");
		PIX_MSG.put(705, "地址信息入库错误。");
		PIX_MSG.put(706, "地址信息查询错误。");
		PIX_MSG.put(707, "地址信息更新错误。");
		PIX_MSG.put(708, "地址信息删除错误。");
		PIX_MSG.put(709, "卡信息入库错误。");
		PIX_MSG.put(710, "卡信息查询错误。");
		PIX_MSG.put(711, "卡信息更新错误。");
		PIX_MSG.put(712, "卡信息删除错误。");
		PIX_MSG.put(713, "本地唯一编号入库错误。");
		PIX_MSG.put(714, "本地唯一编号查询错误。");
		PIX_MSG.put(715, "本地唯一编号更新错误。");
		PIX_MSG.put(716, "本地唯一编号删除错误。");
		PIX_MSG.put(717, "证件信息入库错误。");
		PIX_MSG.put(718, "证件信息查询错误。");
		PIX_MSG.put(719, "证件信息更新错误。");
		PIX_MSG.put(720, "证件信息删除错误。");
		PIX_MSG.put(721, "电话号码入库错误。");
		PIX_MSG.put(722, "电话号码查询错误。");
		PIX_MSG.put(723, "电话号码更新错误。");
		PIX_MSG.put(724, "电话号码删除错误。");
		PIX_MSG.put(750, "证件与姓名不匹配。");
	}
}
