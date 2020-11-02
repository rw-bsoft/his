/**
 * @(#)Constants.java Created on Aug 19, 2009 11:35:35 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source;

import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public abstract class Constants {
	// 分页，默认页记录数
	public static final int DEFAULT_PAGESIZE = 25;
	// 分页，默认页数
	public static final int DEFAULT_PAGENO = 1;
	// @@ 默认头像的记录编号。
	public static final String DEFAULT_PHOTO_ID = "0";

	// @@ 初次定级
	public static final String FIX_TYPE_CREATE = "1";
	// @@ 维持原定级
	public static final String FIX_TYPE_NO_CHANGE = "2";
	// @@ 不定期转级
	public static final String FIX_TYPE_NON_FIX_DATE = "4";

	// @@ 性别代码。
	public static final String SEX_CODE_MALE = "1";
	public static final String SEX_CODE_FEMALE = "2";

	// @@ 以下是随访计划类型。
	public static final String INSTANCE_TYPE_GXY = "1";
	public static final String INSTANCE_TYPE_TNB = "2";
	public static final String INSTANCE_TYPE_ZL = "3";
	public static final String INSTANCE_TYPE_LNR = "4";
	// 儿童询问
	public static final String INSTANCE_TYPE_CD_IQ = "5";
	// 体格检查
	public static final String INSTANCE_TYPE_CD_CU = "6";
	// @@ 体弱儿童随访计划类型。
	public static final String INSTANCE_TYPE_CD_DC = "7";
	// @@ 孕妇随访。
	public static final String INSTANCE_TYPE_MATERNAL = "8";
	// @@ 孕妇高危随访。
	public static final String INSTANCE_TYPE_PREGNANT_HIGH_RISK = "9";
	// @@ 精神病随访。
	public static final String INSTANCE_TYPE_PSYCHOSIS = "10";
	// @@ 高血压询问。
	public static final String INSTANCE_TYPE_HYPERINQUIRE = "11";

	// @@ 以下是随访计划状态。
	// @@ 应访
	public static final String VISIT_PLAN_NEED_VISIT = "0";
	// @@ 已访
	public static final String VISIT_PLAN_VISITED = "1";
	// @@ 失访
	public static final String VISIT_PLAN_LOST = "2";
	// @@ 未访
	public static final String VISIT_PLAN_NOT_VISIT = "3";
	// @@ 过访
	public static final String VISIT_PLAN_OVER = "4";
	// 注销
	public static final String VISIT_PLAN_WRITEOFF = "9";

	// @@ 继续随访。
	public static final String VISIT_EFFECT_CONTINUE = "1";
	// @@ 暂时失访。
	public static final String VISIT_EFFECT_LOST = "2";
	// @@ 终止管理。
	public static final String VISIT_EFFECT_END = "9";
	// ## 终止妊娠
	public static final String VISIT_RESULT_END = "4";
	// @@ 控制情况--优良。
	public static final String CONTROL_RESULT_VERY_WELL = "1";
	// @@ 控制情况--尚可。
	public static final String CONTROL_RESULT_JUST_SOSO = "2";
	// @@ 控制情况--不良。
	public static final String CONTROL_RESULT_BAD = "3";
	// @@ 控制情况--新档案。
	public static final String CONTROL_RESULT_NEW_DOC = "5";
	// @@ 控制情况--不定期转组。
	public static final String CONTROL_RESULT_NON_FIX_DATE = "6";

	// **既往史类别--疾病史**
	public static final String PASTHIS_TYPECODE_SCREEN = "05";
	// **既往史类别--残疾**
	public static final String PASTHIS_TYPECODE_DEFORMITY = "14";
	// **既往史类别--过敏物质**
	public static final String PASTHIS_TYPECODE_ALLERGIC = "03";
	// **既往史类别--父亲**
	public static final String PASTHIS_TYPECODE_FATHER = "08";
	// **既往史类别--母亲**
	public static final String PASTHIS_TYPECODE_MOTHER = "09";
	// **既往史类别--兄弟姐妹**
	public static final String PASTHIS_TYPECODE_BROTHER = "10";
	// **既往史类别--子女**
	public static final String PASTHIS_TYPECODE_CHILDREN = "11";

	// **既往史--疾病史--编码--其他疾病**
	public static final String PASTHIS_SCREEN_CODE = "0512";
	// **既往史--残疾--编码--其他残疾**
	public static final String PASTHIS_DEFORMITY_CODE = "1412";
	// **既往史--过敏--编码--其他过敏物质**
	public static final String PASTHIS_ALLERGIC_CODE = "0305";
	// @@ 结案管理标志，已结案
	public static final String END_MANAGE_FLAG_YES = "1";
	// @@ 结案管理标志，未结案
	public static final String END_MANAGE_FLAG_NO = "0";
	// 体弱儿 已结案
	public static final String CLOSE_FLAG_YES = "1";
	public static final String CLOSE_FLAG_NO = "0";

	public static final String IS_DIABETES = "1";
	public static final String IS_NOT_DIABETES = "2";
	public static final String IS_HYPERTENSION = "1";
	public static final String IS_NOT_HYPERTENSION = "2";

	// ** 日志操作类型 **
	public static final String LOG_OPERTYPE_LOGOUT = "0";
	public static final String LOG_OPERTYPE_REVERT = "1";
	public static final String LOG_OPERTYPE_REVOKE = "2";

	// ** 日志档案类型 **
	public static final String LOG_RECORDTYPE_MPI = "0";
	public static final String LOG_RECORDTYPE_HEALTHRECORD = "1";
	public static final String LOG_RECORDTYPE_FAMILYECORD = "2";
	public static final String LOG_RECORDTYPE_HYPERTENSIONRECORD = "3";
	public static final String LOG_RECORDTYPE_DIABETESRECORD = "4";
	public static final String LOG_RECORDTYPE_TUMOURRECORD = "5";
	public static final String LOG_RECORDTYPE_OLDRECORD = "6";
	public static final String LOG_RECORDTYPE_CHILDRENRECORD = "7";
	public static final String LOG_RECORDTYPE_DEBILITYCHILDRENRECORD = "8";
	public static final String LOG_RECORDTYPE_PREGNANTRECORD = "9";
	public static final String LOG_RECORDTYPE_PER_CHECKUP = "10";
	public static final String LOG_RECORDTYPE_PSYCHOSISRECORD = "11";
	public static final String LOG_RECORDTYPE_HEALTHEDUCATION = "12";

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
	public static final int CODE_SERVICE_ERROR = 450;
	public static final int CODE_SERVICE_AUTHORIZATION_FAIL = 488;
	public static final int CODE_RECORD_EXSIT = 555;
	public static final int CODE_RECORD_NOT_FOUND = 556;
	public static final int CODE_UNKNOWN_ERROR = 600;
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

	// 评价标准
	public static final String appraise_zhonggao = "1";
	public static final String appraise_zhongshang = "2";
	public static final String appraise_shang = "3";
	public static final String appraise_shangshang = "4";
	public static final String appraise_zhongdi = "-1";
	public static final String appraise_zhongxia = "-2";
	public static final String appraise_xia = "-3";
	public static final String appraise_xiaxia = "-4";
	public static final String appraise_nomatch = "0";

	public static final String DATAFORMAT4FORM = "_data";
	public static final String DATAFORMAT4LIST = "_list";

	public static final String EXP_EQ = "eq";
	public static final int CODE_EXP_ERROR = 444;

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
