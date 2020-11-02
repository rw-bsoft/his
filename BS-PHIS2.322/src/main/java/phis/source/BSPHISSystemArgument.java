/**
 * @(#)EHREntryNames.java Created on 2010-6-1 下午04:43:02
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source;

import java.util.HashMap;
import java.util.Map;

/**
 * @description PHIS 系统参数配置集合
 * 
 * @author <a href="mailto:liyl@bsoft.com.cn">liyl</a>
 */
public interface BSPHISSystemArgument {
/*
 * update by caijy at 2015-06-17 对系统参数进行分类,注释,去掉没用的参数
 */
	/*
	 * 病人档案,01
	 */
	public static final String ZDMZHXNGH = "ZDMZHXNGH";//门诊号码虚拟工号
	public static final String MPI = "MPI";//1:调用中心平台患者基本信息,0:调用本地患者基本信息。
	public static final String ZDCSMZH = "ZDCSMZH";//自动产生门诊号 0.不自动产生1.自动产生
	public static final String GHKSSFPB = "GHKSSFPB";//挂号科室是否需要排班，1是，0否，默认是1
	public static final String CARDORMZHM = "CARDORMZHM";//设置卡管理兼容门诊号码，1卡号2门诊号码，默认是1
	public static final String QYNLXZ = "QYNLXZ";// 启用建档年龄限制 0：不限制 大于0的整数：表示小于该年龄时，需要输入出生年月
	public static final String MPI_ADDRESS = "MPI_ADDRESS";//医嘱录入复核可为同一工号   0：不可为同一个人 1：可为同一个人
	public static final String MPI_WORKPLACE = "MPI_WORKPLACE";// 新建档时默认工作单位，为空时不默认
	public static final String MPI_WORKCODE = "MPI_WORKCODE";// 新建档时默认职业类别，为空时不默认
	public static final String MPI_TELE = "MPI_TELE";//新建档时默认家庭电话，为空时不默认
	public static final String MRXZ = "MRXZ";//默认性质 
	
	//挂号管理,02
	public static final String ZDJZHXNGH = "ZDJZHXNGH";//就诊号码虚拟工号
	public static final String GHFPDY = "GHFPDY";//挂号时是否打印发票
	//下面5个费用参数 好像没用的 待确认
	public static final String GHF = "GHF";//挂号费的收费项目
	public static final String YBZLF = "YBZLF";//一般诊疗费的收费项目
	public static final String ZJF = "ZJF";//专家费的收费项目
	public static final String ZLF = "ZLF";//诊疗费的收费项目
	public static final String BLF = "BLF";//病历费的收费项目
	//下面2个参数值没初始化,待确认
	public static final String DQGHRQ = "DQGHRQ";//当前挂号日期，挂号初始化使用，不能修改
	public static final String DQZBLB = "DQZBLB";//当前挂号类别，判断上午还是下午，挂号初始化使用，不能修改
	public static final String GHXQ = "GHXQ";//门诊挂号效期：以天为单位
	public static final String XQJSFS = "XQJSFS";//效期计算方式：0：精确到时分，1：截止到23:59:59
	public static final String YXWGHBRJZ = "YXWGHBRJZ";//无挂号模式 0就是挂号模式，1就是无挂号模式 
	public static final String ZDCSJZH = "ZDCSJZH";//自动产生就诊号 0.不自动产生1.自动产生
	public static final String MTSQYCGHF = "MTSQYCGHF";//是否每天收取一次挂号费，1一天只收一次，0每次挂号都收
	public static final String GHDYJMC = "GHDYJMC";// 挂号打印机名称
	public static final String YYJGTS = "YYJGTS";//预约间隔天数：以天为单位
	public static final String NHGHDY = "NHGHDY";//农合挂号打印
	public static final String PDJH = "PDJH";//排队叫号
	
	//收费管理,03
	public static final String MZFPMXSL = "MZFPMXSL";// 门诊发票连打明细数量
	public static final String SFSFXZKS = "SFSFXZKS";// 收费是否需要选择科室,0是不需要 1是需要  默认0
	//FPYL参数在公用和私用分类里面都有,待确认
	public static final String FPYL = "FPYL";// 发票预览
	//下面2个参数备注已经被注释掉,待确认
	public static final String JCF = "JCF";//检查费归属的收费项目
	public static final String FP_ZLF = "FP_ZLF";//治疗费归属的收费项目
	//MZDCFJE没有java和js文件引用,是否废弃参数,待确认
	public static final String MZDCFJE = "MZDCFJE";// 门诊大处方金额
	public static final String MZFPFDFS = "MZFPFDFS";// 门诊发票分单方式
	public static final String MZFPSFLD = "MZFPSFLD";// 门诊发票是否连打
	public static final String CFXQ = "CFXQ";//自动调入处方的有效期0.不限制  以天为单位
	public static final String YS_MZ_FYYF_CY = "YS_MZ_FYYF_CY";//草药对应药房，存药房识别
	public static final String YS_MZ_FYYF_XY = "YS_MZ_FYYF_XY";//西药对应药房，存药房识别
	public static final String YS_MZ_FYYF_ZY = "YS_MZ_FYYF_ZY";//中药对应药房，存药房识别
	public static final String SFZJFY = "SFZJFY";// 收费直接发药:0.不启用，1.启用
	public static final String QYMZSF = "QYMZSF";//启用门诊审方标志，0表示不启用，1表示启用，默认是0
	public static final String YMJDYSGH = "YMJDYSGH";// 疫苗建档医生工号，多个疫苗建档医生之间用逗号(,)隔开
	public static final String CFXYZYMXSL = "CFXYZYMXSL";// 录入处方明细数量限制是否启用(西药中药)，0表示不启用,大于0则表示允许录入的最大处方明细数量
	public static final String CFCYMXSL = "CFCYMXSL";// 录入处方明细数量限制是否启用(草药)，0表示不启用,大于0则表示允许录入的最大处方明细数量
	public static final String QYCFCZQZTJ = "QYCFCZQZTJ";// 录入处方处置前置条件是否启用，如果启用，则未录入诊断不允许录入处方处置，1表示启用，0表示不启用
	public static final String MZHJSFDYJMC = "MZHJSFDYJMC";// 门诊划价收费打印机名称
	public static final String SFQYJKJSAN = "SFQYJKJSAN";// 是否启用健康结算按钮
	public static final String MZSFDZ = "MZSFDZ";// 门诊收费打折，a;b@c@d@e@f;b@c@d@e@f; a:是否启用：1启用 0禁用 b:业务类型（家医签约用户）c:病人性质 ：1000自费 6000新农合 3000医保 2000新医保 d:费用类型(化验费、放射费、心电图等等),0表示不按类型按总价优惠 e:费用序号 f:费用优惠公式：如ZFJE*0.9、ZFJE-5
	public static final String GHSFDZ = "GHSFDZ";// 挂号收费打折，a;b@c@d@e@f;b@c@d@e@f; a:是否启用：1启用 0禁用 b:业务类型（家医签约用户）c:病人性质 ：1000自费 6000新农合 3000医保 2000新医保 d:费用类型(化验费、放射费、心电图等等),0表示不按类型按总价优惠 e:费用序号 f:费用优惠公式：如ZFJE*0.9、ZFJE-5

	// 门诊诊疗,04
	public static final String KSDM_TJ = "KSDM_TJ";// 健康证默认挂号科室
	public static final String SFQYGWXT = "SFQYGWXT";//是否启用社区系统 1:启用社区系统,0:不启用社区系统。
	public static final String YSZJS = "YSZJS";//医生站结算:0.不允许结算，1.允许结算
	public static final String XSFJJJ = "XSFJJJ";//显示门诊附加项目 0:不显示,1:显示
	public static final String QYJYBZ = "QYJYBZ";//启用检验标志，0表示未启用，1表示启用，默认是0
	public static final String QYJCBZ = "QYJCBZ";//启用检查标志，0表示未启用，1表示启用，默认是0
	public static final String JCIP = "JCIP";//检查结果查看IP
	public static final String YXIP = "YXIP";//影像结果查看IP
	public static final String QYTJBGBZ = "QYTJBGBZ";//启用体检报告标志，0表示未启用，1表示启用，默认是0
	public static final String HQFYYF = "HQFYYF";//启用门诊医生站处方界面的药房切换功能，1表示启用，0表示不启用，默认是0
	public static final String QYSXZZ = "QYSXZZ"; // 启用门诊转诊 检查申请 住院上转
	public static final String SJDOREHY = "SJDOREHY";// 时间段或号源
	public static final String QYCFYYTXBZ = "QYCFYYTXBZ";//启用重复用药提醒标志，0表示未启用，1表示启用，默认是0
	public static final String CFYYPDZQ = "CFYYPDZQ";//重复用药判定周期（x天前至今），天数
	public static final String QYCFJCTXBZ = "QYCFJCTXBZ";//启用重复检查提醒标志，0表示未启用，1表示启用，默认是0
	public static final String CFJCPDZQ = "CFJCPDZQ";//重复检查判定周期（x天前至今），天数
	public static final String QYCFJYTXBZ = "QYCFJYTXBZ";//启用重复检验提醒标志，0表示未启用，1表示启用，默认是0
	public static final String CFJYPDZQ = "CFJYPDZQ";//重复检验判定周期（x天前至今），天数
	public static final String MZKSSXZ = "MZKSSXZ";//门诊抗生素限制
	public static final String QYHLYYBZ = "QYHLYYBZ";//启用合理用药标志，0表示未启用，1表示启用，默认是0
	public static final String CRBBGK = "CRBBGK ";//启用传染病报告卡，0表示未启用，1表示启用，默认0
	//危机值
	public static final String SFQYWJZTX = "SFQYWJZTX";// 是否启用危机值提醒
	public static final String WJZCLSX = "WJZCLSX";//危急值处理时限
	public static final String WJZCSSX = "WJZCSSX";//危急值超时时限
	public static final String WJZTXJGSJ = "WJZTXJGSJ";//危急值提醒间隔时间
	public static final String CFDYMB = "CFDYMB";//处方打印模板设置
	// 药房管理,05
	public static final String JSYP = "JSYP";//精神药品代码对应特殊药品字典中的数据
	public static final String MZYP = "MZYP";//麻醉药品代码 对应特殊药品字典中的数据
	public static final String SX_PREALARM = "SX_PREALARM";// 值为n，表示药品失效预警截止时间为当前时间加上n个月。若n为非法数字则取默认值3。若为带小数点的数值则取其整数部分
	public static final String SFQYYFYFY = "SFQYYFYFY";// 是否启用药房预发药,0是不启用,1启用
	public static final String MZKCDJSJ = "MZKCDJSJ";// 门诊库存冻结时间 1是开单,2是收费
	public static final String KCDJTS = "KCDJTS";// 库存冻结天数,单位是天
	public static final String CKSX_KCSL_YF = "CKSX_KCSL_YF";//出库顺序_库存数量1.按库存数量出库0.不按库存数量出库 该参数值是1时，才会根据参数CKSX_KCSL_ORDER_YF的值进行排序，否则不会；并且排序时，先效期再数量
	public static final String CKSX_KCSL_ORDER_YF = "CKSX_KCSL_ORDER_YF";//出库顺序_库存大小 A.库存少的先出库（小先出） D.库存多的先出库（大先出）
	public static final String CKSX_YPXQ_ORDER_YF = "CKSX_YPXQ_ORDER_YF";//出库顺序_效期先后 A.效期早的药品先出库（早先出）D.效期迟的药品先出库（迟先出）
	public static final String CKSX_YPXQ_YF = "CKSX_YPXQ_YF";//出库顺序_药品效期 1.按效期出库 0.不按效期出库 
	//public static final String YJSJ_YF = "YJSJ_YF";//月结时间 对应一个月的31天  32为月底结 
	public static final String QYFYCK = "QYFYCK";//启用发药窗口 0：不启用，1：启用，默认为0
	public static final String MZFYZDSXMS = "MZFYZDSXMS";//门诊发药自动刷新秒数
	public static final String YFCKDDYZ = "YFCKDDYZ";//药房出库单打印纸210X140：A5纸，A4：A4纸，默认：210X140
	public static final String YFPDJH="YFPDJH";//药房排队叫号
	public static final String YFPDJHCFLX="YFPDJHCFLX";//药房排队叫号处方类型
	/*
	 * 药库管理,06
	 */
	//public static final String KCPD_PC = "KCPD_PC";// 药库库存盘点是否按批次盘点,false是不按批次盘点,true是按批次盘点
	public static final String PLJC = "PLJC";//批零加成
	public static final String YKACCOUNTPRICE = "YKACCOUNTPRICE";// 药库记账价格标准：1，零售价格；2，进货价格；3，批发价格
	public static final String YPKL_YK = "YPKL_YK";// 药库财务验收药品扣率
	//public static final String YJSJ_YK = "YJSJ_YK";// 药库月结日
	public static final String ZDZDTJ = "ZDZDTJ";// 中心调价站点是否自动调价
	public static final String ZXKZJG = "ZXKZJG";//中心控制价格 0.中心不控制价格 1.中心控制价格
	
	public static final String CKSX_KCSL_YK = "CKSX_KCSL_YK";// 出库顺序_库存数量1.按库存数量出库0.不按库存数量出库
	// 该参数值是1时，才会根据参数CKSX_KCSL_ORDER_YF的值进行排序，否则不会；并且排序时，先效期再数量再批号
	public static final String CKSX_KCSL_ORDER_YK = "CKSX_KCSL_ORDER_YK";// 出库顺序_库存大小
	// A.库存少的先出库（小先出）
	// D.库存多的先出库（大先出）
	public static final String CKSX_YPXQ_ORDER_YK = "CKSX_YPXQ_ORDER_YK";// 出库顺序_效期先后
	// A.效期早的药品先出库（早先出）D.效期迟的药品先出库（迟先出）
	public static final String CKSX_YPXQ_YK = "CKSX_YPXQ_YK";// 出库顺序_药品效期
	// 1.按效期出库
	// 0.不按效期出库
	public static final String CKSX_YPPH_ORDER_YK = "CKSX_YPPH_ORDER_YK";// 出库顺序_批号先后
	// A.批号早的药品先出库（早先出）D.批号迟的药品先出库（迟先出）
	public static final String CKSX_YPPH_YK = "CKSX_YPPH_YK";// 出库顺序_药品批号
	
	public static final String FYJBZ = "FYJBZ";
	public static final String YJSJ_YK1 = "YJSJ_YK1";
	public static final String YJSJ_YK2 = "YJSJ_YK2";
	// 1.按批号出库
	// 0.不按批号出库
	/*
	 * 住院管理,07
	 */
	public static final String CWFXH = "CWFXH";//床位费序号,自动床位费功能使用到
	public static final String ZFCWF = "ZFCWF";//自负床位费序号,自动床位费功能使用到
	public static final String ZLFXH = "ZLFXH";//诊疗费序号,自动床位费功能使用到
	public static final String ICUXH = "ICUXH";//ICU序号,自动床位费功能使用到
	public static final String CWFZDLJ = "CWFZDLJ";// 床位费自动累加
	public static final String ETLRQLX = "ETLRQLX";// ETL日期类型(收费日期(SFRQ)、结帐日期(JZRQ)和汇总日期(HZRQ))
	public static final String FHYZHJF = "FHYZHJF";//复核医嘱后计费
	public static final String ZYHM = "ZYHM";// 住院号码
	public static final String BAHM = "BAHM";// 病案号码
	public static final String BAHMDYZYHM = "BAHMDYZYHM";//启用病案号码等于住院号码标志，0表示不启用，1表示启用，默认是0
	public static final String BAHMCXFP = "BAHMCXFP";//病案号码重新分配，0表示不启用，1表示启用，默认是0
	public static final String ZYQFJEDJKZ = "ZYQFJEDJKZ";//住院欠费金额冻结控制 1表示控制 0表示不控制默认为0
	public static final String ZYJKDYJMC = "ZYJKDYJMC";// 住院缴款打印机名称
	public static final String ZYJSDYJMC = "ZYJSDYJMC";// 住院结算打印机名称
	public static final String ZLFYDJ = "ZLFYDJ";//诊疗费单价
	public static final String ZYFPSFZCGY = "ZYFPSFZCGY";// 住院发票是否支持公有
	public static final String JKSJSFZCGY = "JKSJSFZCGY";// 缴款收据是否支持公有
	public static final String CKWH = "CKWH";// 催款维护
	// 病区管理,08
	public static final String YZLR_BZLX = "YZLR_BZLX";//包装类型,1：医嘱录入使用包装类型 2使用药品信息中的病房包装
	public static final String BQCHXSWS = "BQCHXSWS";//病区床号显示位数，默认为0时显示全部，其余按值从右截取显示
	public static final String XSFJJJ_YS = "XSFJJJ_YS";//显示医生站附加项目 0:不显示,1:显示
	public static final String XSFJJJ_HS = "XSFJJJ_HS";//显示护士站附加项目 0:不显示,1:显示
	public static final String ZYYSQY = "ZYYSQY";//住院医生站启用 0.不启用1.启用
	public static final String YZLRFHTYGH = "YZLRFHTYGH";// 医嘱录入可为同一工号
	public static final String YZTDMYTS = "YZTDMYTS";// 医嘱套打每页条数
	public static final String YZBDYSFTD = "YZBDYSFTD";// 医嘱本打印是否需要套打
	public static final String YZBDYSJ = "YZBDYSJ";// 打印时间,1是提交后打印,2是复核后打印
	public static final String ZKCZHHYDY = "ZKCZHHYDY";// 转科重整后换页打印,0是否,1是是
	public static final String HSQMBZ = "HSQMBZ";//护理记录中是否自动显示护士签名0：不显示，1：显示
	public static final String QYYJXMTJ = "QYYJXMTJ";//启用医技项目提交（yj_zy01和yj_zy02表产生数据）0.不启用1.启用
	public static final String YZKPDYZB = "YZKPDYZB";//医嘱卡片打印自备药
	public static final String YZKPDYST = "YZKPDYZB";//医嘱卡片打印ST药品
	public static final String HSZDSTX = "HSZDSTX";//启用护士站定时提醒，0表示启用，1表示不启用，默认是0
	/*
	 * 医技管理, 09
	 */
	public static final String BMSZ = "BMSZ";//医技取消窗口前置条件1:门诊医技,2:住院医技,3:同时使用 默认使用3 其余1和2未进行配置
	public static final String YJZXQXZD = "YJZXQXZD";//医技执行取消诊断，0表示不启用，1表示启用，默认是0
	public static final String ZXSSFDJ = "ZXSSFDJ";//医技项目执行门诊医技只显示已收费单据 1表示显示收费单据 0表示显示全部单据
	
	/*
	 * 物资管理,10
	 */
	public static final String WPJFBZ = "WPJFBZ";// '物品计费标志'，'0'表示不启用，‘1’表示启用，默认是‘0’
	public static final String MZWPJFBZ = "MZWPJFBZ";// '物品计费标志'，'0'表示不启用，‘1’表示启用，默认是‘0’
	public static final String WZSFXMJG = "WZSFXMJG";// '门诊物资收费项目价格'，'0'表示不启用，‘1’表示启用，默认是‘0’
	public static final String WZSFXMJGZY = "WZSFXMJGZY";// '住院物资收费项目价格'，'0'表示不启用，‘1’表示启用，默认是‘0’
	/*
	 * 电子病历,11
	 */
	public static final String QNYDK = "QNYDK";//病程从当前病程前N份打开
	public static final String YXXJYSXG = "YXXJYSXG";//允许下级医生修改病历 0:不允许 1:允许
	public static final String QZWZXJY = "QZWZXJY";//强制完整性校验 0:不需要 1:需要
	public static final String DYQWZXJY = "DYQWZXJY";//打印前强制完整性校验 0:不是 1:是
	public static final String QMYZXJY = "QMYZXJY";//签名一致性校验 0:不是 1:是
	public static final String SFJYWBKB = "SFJYWBKB";//是否禁用外部拷贝，1：是，0否，默认是0
	public static final String QYMZDZBL = "QYMZDZBL";// 启用门诊电子病历，0：不启用 1：启用，默认不启用
	public static final String QYDZBL = "QYDZBL";//启用电子病历功能，0：不启用 1：启用，默认启用
	public static final String BASYKSMRZ = "BASYKSMRZ";//病案首页空时默认值：1.空白、2.无、3.-、4./、其他为：/
	public static final String EMRVERSION = "EMRVERSION";//电子病历activeX插件版本号，如:4. 6. 0. 4
	
	/*
	 * 处方点评,12
	 */
	public static final String CFDPCQSL = "CFDPCQSL";// 处方点评抽取数量,默认是100
	/*
	 * 抗菌药物,13
	 */
	public static final String QYKJYWGL = "QYKJYWGL";//是否启用抗菌药物管理:0表示不启用，1表示启用，默认为0
	public static final String QYKJYYY = "QYKJYYY";//是否启用抗菌药录入原因： 0表示不启用 ，1表示启用，默认为1
	public static final String QYSJYSSQ = "QYSJYSSQ";//是否启用门诊抗菌药物上级医生授权 0表示不启用,1表示启用,默认为1
	public static final String QYZYKJYWSP = "QYZYKJYWSP";//是否启用住院抗菌药物审批：0表示不开启，1表示开启，默认为0
	public static final String KJYSYTS = "KJYSYTS";//抗菌药物限制最大用药天数，默认为3天
	/*
	 * 皮试管理,14
	 */
	public static final String QYPSXT = "QYPSXT";//启用皮试系统,0不启用,1启用,默认为0
	public static final String PSXQ = "PSXQ";//皮试效期(天),默认值为1天
	public static final String PSSJ = "PSSJ";//皮试时间:默认为20,单位为分钟
	public static final String PSSXJG = "PSSXJG";//皮试界面刷新间隔:默认为20,单位为秒
	public static final String PSSFDYXM = "PSSFDYXM";//皮试收费对应项目，设置皮试收费对应项目的费用序号，值为整数
	/*
	 * 家床管理,15
	 */
	public static final String JCQFKZ = "JCQFKZ";//家床欠费控制 1表示控制 0表示不控制默认为0
	public static final String JCJSDYJMC = "JCJSDYJMC";// 家床结算打印机名称
	public static final String JCEDTS = "JCEDTS";// 家床额定天数
	public static final String JCJKDYJMC = "JCJKDYJMC";// 家床缴款打印机名称
	public static final String QYJCGL = "QYJCGL";// 启用家床管理
	public static final String JCBH = "JCBH";// 家床编号
	public static final String JCH = "JCH";// 家床号
	public static final String JCKCGL = "JCKCGL";// 家床库存管理,1不进行库存管理,2提交并直接发药,3提交到药房发药
	public static final String JCZZLJTS = "JCZZLJTS";// 家床终止临近天数


	//'SFQYJYQY', 'JYFY', 'JYKS'
	/**
	 * 家医参数
	 */
	public static final String SFQYJYQY = "SFQYJYQY";//是否启用家医签约
	public static final String JYFY = "JYFY";//家医签约预缴费用
	public static final String JYKS = "JYKS";//家医科室


	/*
	 * 已废弃参数
	 */
	//public static final String BZLBFFHJGH = "BZLBFFHJGH";// 边诊疗边付费划价工号(杭州边诊疗边付费参数)
	
	/*
	 * 未分类参数
	 */
	public static final String GWWEBSERVICE_ADDRESS = "GWWEBSERVICE_ADDRESS";//公卫WebService地址
	//BSDYRJBZ没有地方用到,是否废弃,待确认
	public static final String BSDYRJBZ = "BSDYRJBZ";// 博思打印软件标志
	public static final String JIANYANSERVERIP = "JIANYANSERVERIP";// 检验服务IP地址
	//下面3个参数 没有java文件和js文件调用
	public static final String TIJIANSERVERIP = "TIJIANSERVERIP";// 体检服务IP地址
	public static final String ETLBEGINDATE = "ETLBEGINDATE";// ETL时间段采集数据 开始日期
	public static final String ETLENDDATE = "ETLENDDATE";// ETL时间段采集数据 结束日期
	// 业务锁,单用户登录
	public static final String XZYHDL = "XZYHDL";// 0：不限制，允许用户多客户端同时登录 1：限制单用户登录，登录后则不允许其它客户端登录 2：提示是否强制登录，后登录用户会把前一登录用户踢下线
	public static final String QYYWS = "QYYWS";// 启用业务锁功能：0不启用 1启用
	//QYXXXT没有注释,没有默认值,没有分类 只有PublicService用到
	public static final String QYXXXT = "QYXXXT";
	// 皮试管理
	public static final String KLX = "KLX";// 卡类型
	//静滴卡列数
	public static final String JDKLS = "JDKLS";
	
	//动态参数前缀 key 表示动态参数前缀部分， value表示对应查询的字典
	public static final Map<String, String> dynamicParamter = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("BQTJTS", "department");
			put("YZTS", "department");
			put("YJSJ_YF", "pharmacy");
			put("YJSJ_YK", "pharmacy");
			put("PYFS_YK", "storehouse");
			put("PKFS_YK", "storehouse");
			put("YF_PCPD", "pharmacy");
			put("KCPD_PC", "pharmacy");
			put("YJSJ_KF", "treasury");
			put("CKWZJSLX", "treasury");
			put("KCPDLX", "treasury");
			put("EJSLKZ", "treasury");

		}
	};

	// 公用默认值
	public static final Map<String, String> defaultPubValue = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			/*
			 * 病人档案,01
			 */
			put(ZDMZHXNGH, "0");
			put(MPI, "0");
			/*
			 * 挂号管理,02
			 */
			put(ZDJZHXNGH, "0");
			put(GHF, "12");
			put(YBZLF, "42");
			put(ZJF, "7");
			put(ZLF, "13");
			put(GHFPDY, "0");
			/*
			 * 收费管理,03
			 */
			put(JCF, "5");
			put(FP_ZLF, "6");
			put(MZDCFJE, "100");
			/*
			 * 门诊诊疗,04
			 */
			put(SFQYGWXT, "0");
			put(CRBBGK, "0");
			/*
			 * 药房管理,05
			 */
			put(JSYP, "2");
			put(MZYP, "1");
			put(YFCKDDYZ, "210X140");
			/*
			 * 药库管理,06
			 */
			put(PLJC, "0");
			put(ZDZDTJ, "0");
			put(ZXKZJG, "0");
			/*
			 * 住院管理,07
			 */
			put(CWFXH, "0");
			put(ZFCWF, "0");
			put(ZLFXH, "0");
			put(ICUXH, "0");
			put("CWFZDLJ", "0");
			put(ETLRQLX, "SFRQ");
			
			/*
			 * 病区管理,08
			 */
			put("YZYZQM", "0");
			put("HSZDSTX", "0");
			
			/*
			 * 医技管理, 09
			 */
			
			/*
			 * 物资管理,10
			 */
			
			/*
			 * 电子病历,11
			 */
			put(BASYKSMRZ, "/");
			put(EMRVERSION, "4. 6. 0. 4");
			put(QYDZBL, "1");
			/*
			 * 处方点评,12
			 */
			
			/*
			 * 抗菌药物,13
			 */
			
			/*
			 * 皮试管理,14
			 */
			
			/*
			 * 家床管理,15
			 */
			/*
			 * 未分类参数
			 */
			put(GWWEBSERVICE_ADDRESS, "http://127.0.0.1:8080/CHIS");
			put(JIANYANSERVERIP, "http://localhost:8080/BS_JYTJ");
			put(TIJIANSERVERIP, "http://localhost:8080/CYTJ");
			put(ETLBEGINDATE, "1970-01-01");
			put(ETLENDDATE, "1970-01-01");
			put(XZYHDL, "0");// 限制用户登录
			put(KLX, "04");// 卡类型
		}
	};

	// 私有默认值
	public static final Map<String, String> defaultValue = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			/*
			 * 病人档案,01
			 */
			put(ZDCSMZH, "0");
			put("GHKSSFPB", "1");
			put("CARDORMZHM", "2");
			put("QYNLXZ", "0");
			put("MPI_ADDRESS", "");
			put("MPI_WORKPLACE", "");
			put("MPI_WORKCODE", "");
			put("MPI_TELE", "");
			put(MRXZ, "30");
			//挂号管理,02
			put(BLF, "0");
			put(GHXQ, "1");
			put(XQJSFS, "1");
			put(YXWGHBRJZ, "0");
			put(ZDCSJZH, "1");
			put("MTSQYCGHF", "1");
			put("GHDYJMC", "GHDYJ");// 挂号打印机名称
			put(YYJGTS, "7");
			put(GHFPDY, "0");
			put(NHGHDY, "0");
			put(PDJH,"0");
			// 收费管理,03
			put(MZFPMXSL, "16");// 门诊发票连打明细数量
			put("SFSFXZKS", "0");
			put("FPYL", "0");
			put(MZFPFDFS, "0");// 门诊发票分单方式
			put(MZFPSFLD, "1");// 门诊发票是否连打
			put(CFXQ, "1");
			put(YS_MZ_FYYF_CY, "-1");
			put(YS_MZ_FYYF_XY, "-1");
			put(YS_MZ_FYYF_ZY, "-1");
			put(SFZJFY, "0");
			put("QYMZSF", "0");
			put("YMJDYSGH", "");
			put("CFXYZYMXSL", "0");
			put("CFCYMXSL", "20");
			put("QYCFCZQZTJ", "0");
			put("MZHJSFDYJMC", "MZHJSFDYJ");// 门诊划价收费打印机名称
			put(SFQYJKJSAN, "0");
			//门诊诊疗,04
			put(KSDM_TJ, "0");// 健康证挂号
			put(YSZJS, "0");
			put(XSFJJJ, "0");
			put("QYJYBZ", "0");
			put("QYTJBGBZ", "0");
			put("HQFYYF", "0");
			put("QYSXZZ", "0");
			put("SJDOREHY", "1");// 时间段或者号源
			put(SFQYWJZTX,"0");// 是否启用危机值提醒
			put(WJZCLSX,"5");//危急值处理时限
			put(WJZCSSX,"5");//危急值超时时限
			put(WJZTXJGSJ,"30");//危急值提醒间隔时间
			put(CFDYMB,"0");
			put(MZKSSXZ,"0");//门诊抗生素限制 
			put(CRBBGK,"0");
			/*
			 * 药房管理,05
			 */
			put("YF_PCPD", "1");
			put("SX_PREALARM", "3");
			put("SFQYYFYFY", "0");
			put("MZKCDJSJ", "1");
			put("KCDJTS", "1");
			put(CKSX_KCSL_YF, "0");
			put(CKSX_KCSL_ORDER_YF, "A");
			put(CKSX_YPXQ_ORDER_YF, "A");
			put(CKSX_YPXQ_YF, "0");
			put("YJSJ_YF", "32");
			put("QYFYCK", "0");
			put("MZFYZDSXMS", "20");
			put("YFCKDDYZ", "210X140");
			put(YFPDJH,"0");
			put(YFPDJHCFLX,"4");
			/*
			 * 药库管理,06
			 */
			put("YKACCOUNTPRICE", "1");
			put(YPKL_YK, "1");
			put("PKFS_YK", "0");
			put("PYFS_YK", "0");
			put("YJSJ_YK", "32");
			
			put("CKSX_KCSL_ORDER_YK", "A");
			put("CKSX_KCSL_YK", "0");
			put("CKSX_YPPH_ORDER_YK", "A");
			put("CKSX_YPPH_YK", "0");
			put("CKSX_YPXQ_ORDER_YK", "A");
			put("CKSX_YPXQ_YK", "1");
			put("FYJBZ", "0");
			put("YJSJ_YK1", "32");
			put("YJSJ_YK2", "32");
			/*
			 * 住院管理,07
			 */
			put(FHYZHJF, "0");
			put(ZYHM, "0000000001");
			put(BAHM, "0000000001");
			put("BAHMDYZYHM", "0");
			put("BAHMCXFP", "0");
			put("ZYQFJEDJKZ", "0");
			put("ZYJKDYJMC", "ZYJKDYJ");// 住院缴款打印机名称
			put("ZYJSDYJMC", "ZYJSDYJ");// 住院结算打印机名称
			put("ZLFYDJ", "0");
			put("ZYFPSFZCGY", "0");// 住院发票是否支持公有
			put("JKSJSFZCGY", "0");// 缴款收据是否支持公有
			put("CKWH","0");//催款维护
			/*
			 * 病区管理,08
			 */
			put(YZLR_BZLX, "1");
			put(BQCHXSWS, "0");
			put(XSFJJJ_YS, "0");
			put(XSFJJJ_HS, "0");
			put(ZYYSQY, "0");
			put("YZLRFHTYGH", "1");
			put("YZTDMYTS", "20");
			put("YZBDYSFTD", "0");
			put("YZBDYSJ", "1");
			put("ZKCZHHYDY", "1");
			put(HSQMBZ,"1");//护理记录护士自动签名
			put(JDKLS,"2");
			put("YZYZQM","0");
			put("YZKPDYZB","0");
			put("YZKPDYST","0");
			put("HSZDSTX","0");
			/*
			 * 医技管理, 09
			 */
			put("BMSZ", "3");
			put("ZXSSFDJ", "1");
			put("YJZXQXZD", "0");
			
			/*
			 * 物资管理,10
			 */
			put("WPJFBZ", "0");
			put("MZWPJFBZ", "0");
			put("WZSFXMJG", "1");
			put("WZSFXMJGZY", "1");
			/*
			 * 电子病历,11
			 */
			put("QNYDK", "1");
			put("YXXJYSXG", "0");
			put("QZWZXJY", "0");
			put("DYQWZXJY", "0");
			put("QMYZXJY", "0");
			put("SFJYWBKB", "0");
			put(QYMZDZBL, "0");// 启用门诊电子病历
			/*
			 * 处方点评,12
			 */
			put("CFDPCQSL", "100");
			/*
			 * 抗菌药物,13
			 */
			put("QYKJYYY", "1");
			put("QYSJYSSQ", "1");
			put("QYKJYWGL", "0");
			put("QYZYKJYWSP", "0");
			put("KJYSYTS", "3");
			/*
			 * 皮试管理,14
			 */
			put("PSXQ", "1");
			put("PSSJ", "20");
			put("PSSXJG", "20");
			put("QYPSXT", "0");
			put("PSSFDYXM", "0");
			/*
			 * 家床管理,15
			 */
			put("JCQFKZ", "0");
			put(QYJCGL, "0");// 启用家床管理
			put("JCBH", "0000000001");// 家床编号
			put("JCH", "0000000001");// 家床号
			put("JCKCGL", "1");// 家床库存管理
			put("JCZZLJTS", "7");// 家床终止临近天数
			put("JCEDTS", "30");// 家床额定天数
			put("JCJKDYJMC", "JCJKDYJ");// 家床缴款打印机名称
			put("JCJSDYJMC", "JCJSDYJ");// 家床结算打印机名称
			
			// put(XYF, "2");
			// put(ZYF, "3");
			// put(CYF, "4");
			// put(ZXSYKCYP, "0");
			// put(WGHMS, "0");
			// put(REPORT_COUNTDATE_MZ, "1");
			// put("QYMZPD", "0");
			// put("BSDYRJBZ", "0");
			// put("BZLBFFHJGH", "0");
			// put("XNFPXL", "1");
			// put("SYBCYXE", "30");
			// put("SYBGDBZCYXE", "50");
			// put(TJRQLX, "SFRQ");
			
			// 抗菌药物
			put("QYYWS", "0");
			// 皮试管理
			//发药机接口
			put("FYJBZ", "0");

			//家医签约相关
			//'SFQYJYQY', 'JYFY', 'JYKS'
			put("SFQYJYQY","0");
			put("JYFY","0");
			put("JYKS","0");
			
		}
	};

	// 备注信息
	public static final Map<String, String> defaultAlias = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			/*
			 * 病人档案,01
			 */
			put(ZDCSMZH, "自动产生门诊号 0.不自动产生1.自动产生");
			put("GHKSSFPB", "挂号科室是否需要排班，1是，0否，默认是1");
			put("CARDORMZHM", "设置卡管理兼容门诊号码，1卡号2门诊号码，默认是1");
			put("QYNLXZ", "启用建档年龄限制 0：不限制 大于0的整数：表示小于该年龄时，需要输入出生年月");
			put("MPI_ADDRESS", "新建档时默认联系地址，为空时不默认");
			put("MPI_WORKPLACE", "新建档时默认工作单位，为空时不默认");
			put("MPI_WORKCODE", "新建档时默认职业类别，为空时不默认");
			put("MPI_TELE", "新建档时默认家庭电话，为空时不默认");
			put(MRXZ, " 默认性质 ");
			/*
			 * 挂号管理,02
			 */
			put(BLF, "病历费的价格");
			put(GHXQ, " 门诊挂号效期：以天为单位 ");
			put(XQJSFS, "效期计算方式：0：精确到时分，1：截止到23:59:59");
			put(YXWGHBRJZ, "无挂号模式 0就是挂号模式，1就是无挂号模式 ");
			put(ZDCSJZH, "自动产生就诊号 0.不自动产生1.自动产生");
			put("MTSQYCGHF", "是否每天收取一次挂号费，1一天只收一次，0每次挂号都收");
			put("GHDYJMC", "挂号打印机名称");// 挂号打印机名称
			put(YYJGTS, " 预约间隔天数：以天为单位 ");
			put(GHFPDY, "挂号时是否打印发票0：不打印；1：打印");
			put(NHGHDY, "农合挂号打印发票0：不打印；1：打印");
			put(PDJH, "排队叫号 0：不开启；1：开启");
			//收费管理,03
			put(MZFPMXSL, "设置大于0的数字:0表示不控制打印明细数");// 门诊发票连打明细数量
			put(SFSFXZKS, "收费是否需要选择科室,0是不需要 1是需要 默认0");// 收费是否需要选择科室,0是不需要 1是需要 默认0
			put("FPYL", "发票预览，0是直接打印，1是发票预览，默认为0");
			put(MZFPFDFS, "0：不分单，1：按收费项目分单，2：检查按执行科室分单，药品按药房分单");// 门诊发票分单方式
			put(MZFPSFLD, "0：不连打，1：连打");// 门诊发票是否连打
			put(CFXQ, " 自动调入处方的有效期0.不限制  以天为单位 ");
			put(YS_MZ_FYYF_CY, "草药对应药房，存药房识别");
			put(YS_MZ_FYYF_XY, "西药对应药房，存药房识别");
			put(YS_MZ_FYYF_ZY, "中药对应药房，存药房识别");
			put(SFZJFY, "收费直接发药:0.不启用，1.启用");
			put("QYMZSF", "启用门诊审方标志，0表示不启用，1表示启用，默认是0");
			put("YMJDYSGH", "疫苗建档医生工号，多个疫苗建档医生之间用逗号(,)隔开");
			put("CFXYZYMXSL", "录入处方明细数量限制是否启用(西药中药)，0表示不启用,大于0则表示允许录入的最大处方明细数量");
			put("CFCYMXSL", "录入处方明细数量限制是否启用(草药)，0表示不启用,大于0则表示允许录入的最大处方明细数量");
			put("QYCFCZQZTJ","录入处方处置前置条件是否启用，如果启用，则未录入诊断不允许录入处方处置，1表示启用，0表示不启用");
			put("MZHJSFDYJMC", "门诊划价收费打印机名称");
			put(SFQYJKJSAN, "是否启用健康结算按钮:0表示不显示，1表示显示");
			//门诊诊疗,04
			put(KSDM_TJ, "健康证默认挂号科室");
			put(YSZJS, "医生站结算:0.不允许结算，1.允许结算");
			put(XSFJJJ, " 显示门诊附加项目 0:不显示,1:显示 ");
			put("QYJYBZ", "启用检验标志，0表示未启用，1表示启用，默认是0");
			put("QYTJBGBZ", "启用体检报告标志，0表示未启用，1表示启用，默认是0");
			put("HQFYYF", "启用门诊医生站处方界面的药房切换功能，1表示启用，0表示不启用，默认是0");
			put("QYSXZZ", "启用双向转诊功能,1表示启用 0表示不启用");
			put("SJDOREHY", "门诊转诊号源支持时间段和具体号源两种模式，1表示时间段，2表示号源");
			put(SFQYWJZTX,"是否启用危机值提醒:0.不启用,1.启用");
			put(WJZCLSX,"危急值处理时限,单位:分");
			put(WJZCSSX,"危急值超时时限,单位:分");
			put(WJZTXJGSJ,"危急值提醒间隔时间,单位:秒");
			put(CFDYMB,"处方打印模板，0表示套打，1表示其他，默认是0");
			put(MZKSSXZ,"门诊启用抗生素限制，0不限，1限制，启用时与个人基本信息维护有关联");
			put(CRBBGK,"启用传染病报告卡，0表示未启用，1表示启用，默认0");
			// 药房管理,05
			put("YF_PCPD", "药房库存盘点是否按批次盘点,0是不按批次盘点,1是按批次盘点");
			put("SX_PREALARM","值为n，表示药品失效预警截止时间为当前时间加上n个月。若n为非法数字则取默认值3。若为带小数点的数值则取其整数部分");
			put("SFQYYFYFY", "是否启用药房预发药,0是不启用,1启用");
			put("MZKCDJSJ", "门诊库存冻结时间  1是开单,2是收费");
			put("KCDJTS", "库存冻结天数,单位是天");
			put(CKSX_KCSL_YF,
					"出库顺序_库存数量1.按库存数量出库0.不按库存数量出库 该参数值是1时，才会根据参数CKSX_KCSL_ORDER_YF的值进行排序，否则不会；并且排序时，先效期再数量");
			put(CKSX_KCSL_ORDER_YF, " 出库顺序_库存大小 A.库存少的先出库（小先出） D.库存多的先出库（大先出） ");
			put(CKSX_YPXQ_ORDER_YF," 出库顺序_效期先后 A.效期早的药品先出库（早先出）D.效期迟的药品先出库（迟先出） ");
			put(CKSX_YPXQ_YF, " 出库顺序_药品效期 1.按效期出库 0.不按效期出库 ");
			put("YJSJ_YF", " 月结时间 对应一个月的31天  32为月底结 ");
			put("QYFYCK", "启用发药窗口 0：不启用，1：启用，默认为0");
			put("MZFYZDSXMS", "门诊发药自动刷新秒数");
			put("YFCKDDYZ", "药房出库单打印纸210X140：A5纸，A4：A4纸，默认：210X140");
			put(YFPDJH, "药房排队叫号 0：不开启；1：开启");
			put(YFPDJHCFLX, "药房排队叫号处方类型 0：全部；1：西药；2：中成药；3：草药；4：不排队叫号。多种类型英文,隔开。");
			//药库管理,06
			put("PYFS_YK", "药库盘盈方式");
			put("PKFS_YK", "药库盘亏方式");
			put("KCPD_PC", "药库库存盘点是否按批次盘点,false是不按批次盘点,true是按批次盘点");
			put("YKACCOUNTPRICE", "药库记账标准价格，1表示零售价格，2表示进货价格，3表示批发价格");
			put(YPKL_YK, "药库财务验收药品扣率");
			put("YJSJ_YK", "药库月结时间 对应一个月的31天  32为月底结");
			put(CKSX_KCSL_ORDER_YK, "出库顺序_库存大小A.库存少的先出库（小先出）D.库存多的先出库（大先出）");
			put(CKSX_KCSL_YK, "出库顺序_库存数量1.按库存数量出库0.不按库存数量出库");
			put(CKSX_YPPH_ORDER_YK, "出库顺序_批号先后A.批号早的药品先出库（早先出）D.批号迟的药品先出库（迟先出）");
			put(CKSX_YPPH_YK, "出库顺序_药品批号1.按批号出库0.不按批号出库");
			put(CKSX_YPXQ_ORDER_YK, "出库顺序_效期先后A.效期早的药品先出库（早先出）D.效期迟的药品先出库（迟先出）");
			put(CKSX_YPXQ_YK, "出库顺序_药品效期1.按效期出库0.不按效期出库");
			put(FYJBZ, "启用调取发药机WebService服务0：不启用，1：启用，默认为0");
			put(YJSJ_YK1, "药库月结时间 对应一个月的31天  32为月底结");
			put(YJSJ_YK2, "药库月结时间 对应一个月的31天  32为月底结");
			//住院管理,07
			put(FHYZHJF, " 复核医嘱后计费 ");
			put(ZYHM, "住院号码");
			put(BAHM, "病案号码");
			put("BAHMDYZYHM", "启用病案号码等于住院号码标志，0表示不启用，1表示启用，默认是0");
			put("BAHMCXFP", "病案号码重新分配，0表示不启用，1表示启用，默认是0");
			put("ZYQFJEDJKZ", "住院欠费控制 1表示控制 0表示不控制默认为0");
			put("ZYJKDYJMC", "住院缴款打印机名称");// 住院缴款打印机名称
			put("ZYJSDYJMC", "住院结算打印机名称");// 住院结算打印机名称
			put("ZLFYDJ", "诊疗费单价");
			put(ZYFPSFZCGY, "住院发票是否支持公有:0表示不支持，1表示支持");
			put(JKSJSFZCGY, "缴款收据是否支持公有:0表示不支持，1表示支持");
			put("CKWH","催款维护 0:按性质维护,1:按科室维护");//催款维护
			//病区管理,08
			put(YZLR_BZLX, "包装类型,1：医嘱录入使用包装类型 2使用药品信息中的病房包装");
			put(BQCHXSWS, "病区床号显示位数，默认为0时显示全部，其余按值从右截取显示");
			put(XSFJJJ_YS, " 显示医生站附加项目 0:不显示,1:显示 ");
			put(XSFJJJ_HS, " 显示护士站附加项目 0:不显示,1:显示 ");
			put(ZYYSQY, "住院医生站启用 0.不启用1.启用");
			put("YZLRFHTYGH", "医嘱录入复核可为同一工号   0：不可为同一个人 1：可为同一个人");
			put("YZTDMYTS", "医嘱套打每页条数,默认20,设置好后请勿随便修改 以防打印不正确");
			put("YZBDYSFTD", "医嘱本打印是否需要套打,0是不需要,1是需要,默认为0");
			put("YZBDYSJ", "医嘱本打印时间,1是提交后打印,2是复核后打印,默认为1");
			put("ZKCZHHYDY", "医嘱本打印,转科重整后换页打印,0是否,1是是");
			put(HSQMBZ,"护理记录中是否自动显示护士签名0：不显示，1：显示");//护理记录护士自动签名
			put(JDKLS, "静滴卡列数");
			put("YZYZQM", "医嘱本打印签名,0打印签名,1不打印签名,默认为0");
			put("YZKPDYZB","医嘱卡片打印自备药品，0不打1打");
			put("YZKPDYST","医嘱卡片打印ST药品，0不打1打");
			put("HSZDSTX","护士站定时提醒，0表示启用，1表示不启用，默认是0");
			/*
			 * 医技管理, 09
			 */
			put(BMSZ, "医技取消窗口前置条件1:门诊医技,2:住院医技,3:同时使用 默认使用3 其余1和2未进行配置");
			put("ZXSSFDJ", "医技项目执行门诊医技只显示已收费单据 1表示显示收费单据 0表示显示全部单据");
			put("YJZXQXZD", "医技执行取消诊断，0表示不启用，1表示启用，默认是0");
			
			/*
			 * 物资管理,10
			 */
			put("WPJFBZ", "物品计费标志,0表示不启用，1表示启用，默认是0");
			put("MZWPJFBZ", "门诊物品计费标志,0表示不启用，1表示启用，默认是0");
			put("WZSFXMJG", "门诊物资收费项目价格,0表示不启用,1表示启用,默认是0");
			put("WZSFXMJGZY", "住院物资收费项目价格,0表示不启用,1表示启用,默认是0");
			/*
			 * 电子病历,11
			 */
			put("QNYDK", "病程从当前病程前N份打开");
			put("YXXJYSXG", "允许下级医生修改病历 0:不允许 1:允许");
			put("QZWZXJY", "强制完整性校验 0:不需要 1:需要");
			put("DYQWZXJY", "打印前强制完整性校验 0:不是 1:是");
			put("QMYZXJY", "签名一致性校验 0:不是 1:是");
			put("SFJYWBKB", "是否禁用外部拷贝，1：是，0否，默认是0");
			put(QYMZDZBL, "启用门诊电子病历，0：不启用 1：启用，默认不启用");// 启用门诊电子病历
			/*
			 * 处方点评,12
			 */
			put(CFDPCQSL, "处方点评抽取数量,默认是100");// 处方点评抽取数量,默认是100
			/*
			 * 抗菌药物,13
			 */
			put("QYKJYWGL", "是否启用抗菌药物管理:0表示不启用，1表示启用，默认为0");
			put("QYSJYSSQ", "是否启用门诊抗菌药物上级医生授权 0表示不启用,1表示启用,默认为1");
			put("QYKJYYY", "是否启用抗菌药录入原因： 0表示不启用 ，1表示启用，默认为1");
			put("QYZYKJYWSP", "是否启用住院抗菌药物审批：0表示不开启，1表示开启，默认为0");
			put("KJYSYTS", "抗菌药物限制最大用药天数，默认为3天");
			/*
			 * 皮试管理,14
			 */
			put("QYPSXT", "启用皮试系统,0不启用,1启用,默认为0");
			put("PSXQ", "皮试效期(天),默认值为1天");
			put("PSSJ", "皮试时间:默认为20,单位为分钟");
			put("PSSXJG", "皮试界面刷新间隔:默认为20,单位为秒");
			put(PSSFDYXM, "皮试收费对应项目，设置皮试收费对应项目的费用序号，值为整数");
			/*
			 * 家床管理,15
			 */
			put("JCQFKZ", "家床欠费控制 1表示控制 0表示不控制默认为0");
			put(QYJCGL, "启用家床管理： 0 不启用 1 启用，默认为0");
			put(JCBH, "家床病人的唯一识别号，从1开始长度为10的流水号（如0000000001）");
			put(JCH, "家床病人家床流水号，从1开始长度为10的流水号（如0000000001）");
			put(JCEDTS, "机构允许病人家床的最大天数，即开始时间和终止时间的天数");
			put(JCKCGL, "家床库存管理,1不进行库存管理,2提交并直接发药,3提交到药房发药");
			put("JCZZLJTS", "家床登记临近终止日期提醒天数，默认临近7天提示");// 家床终止临近天数
			put("JCJKDYJMC", "家床缴款打印机名称");// 住院缴款打印机名称
			put("JCJSDYJMC", "家床结算打印机名称");// 家床结算打印机名称
			
			// put(XYF, " 西药归属的收费项目 ");
			// put(ZYF, " 中药归属的收费项目 ");
			// put(CYF, " 草药归属的收费项目 ");
			// put(ZXSYKCYP,
			// " 只显示有库存的药品 0: 没有库存的药品也可以在下拉列表中显示 1: 下拉列表中只显示有库存的的药品 ");
			// put(WGHMS, "无挂号模式0.挂号模式1.无挂号模式");
			// put(REPORT_COUNTDATE_MZ, "报表统计日期 1.按收费日期,默认是1");
			// put("QYMZPD", "启用门诊排队 0：不启用，1：启用，默认为0");
			// put("BSDYRJBZ", "博思打印软件标志,0表示不启用,1表示启用,默认是0");
			// put("XNFPXL", "虚拟发票序列");
			// put(TJRQLX,
			// "//住院性质费用汇总报表/出院病人汇总表的统计日期类型 收费日期(SFRQ)、结帐日期(JZRQ)和汇总日期(HZRQ)");
			
			// 抗菌药物
			put("QYYWS", "启用业务锁功能：0不启用 1启用");
			// 皮试管理

			//家医相关
			put(SFQYJYQY,"是否启用家医签约 0 否 1 是");
			put(JYKS,"家医收费科室");
			put(JYFY,"家医签约预缴费用");
			
		}
	};

	/**
	 * 所属类别 <item key="01" text="病人档案" /> <item key="02" text="挂号管理" /> <item
	 * key="03" text="收费管理" /> <item key="04" text="门诊诊疗" /> <item key="05"
	 * text="药房管理" /> <item key="06" text="药库管理" /> <item key="07" text="住院管理"
	 * /> <item key="08" text="病区管理" /> <item key="09" text="医技管理" /> <item
	 * key="10" text="物资管理" /> <item key="11" text="电子病历" /><item key="12"
	 * text="处方点评" /> <item key="13" text="抗菌药物" /> <item key="14" text="皮试管理"
	 * /><item key="15" text="家床管理"/>
	 */
	public static final Map<String, String> defaultCategory = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			/*
			 * 病人档案,01
			 */
			put(ZDCSMZH, "01");// 自动产生门诊号 0.不自动产生1.自动产生
			put("GHKSSFPB", "01");// 挂号科室是否需要排班，1是，0否，默认是1
			put("CARDORMZHM", "01");// 设置卡管理兼容门诊号码，1卡号2门诊号码，默认是1
			put("QYNLXZ", "01");// 启用建档年龄限制 0：不限制 大于0的整数：表示小于该年龄时，需要输入出生年月
			put("MPI_ADDRESS", "01");// 新建档时默认联系地址，为空时不默认
			put("MPI_WORKPLACE", "01");// 新建档时默认工作单位，为空时不默认
			put("MPI_WORKCODE", "01");// 新建档时默认职业类别，为空时不默认
			put("MPI_TELE", "01");// 新建档时默认家庭电话，为空时不默认
			put(MRXZ, "01");// 默认性质
			/*
			 * 挂号管理,02
			 */
			put(GHXQ, "02");// 门诊挂号效期：以天为单位
			put(XQJSFS, "02,03,04");// 效期计算方式：0：精确到时分，1：截止到23:59:59
			put(YXWGHBRJZ, "02,03,04");// 无挂号模式 0就是挂号模式，1就是无挂号模式
			put(ZDCSJZH, "02,03,04");// 自动产生就诊号 0.不自动产生1.自动产生
			put(BLF, "02,03,04");// 病历费的价格
			put("MTSQYCGHF", "02,03");// 是否每天收取一次挂号费，1一天只收一次，0每次挂号都收
			put("GHDYJMC", "02");// 挂号打印机名称
			put(YYJGTS, "02,03");
			put(GHFPDY, "02");
			put(NHGHDY, "02");
			put(PDJH, "02");
			/*
			 * 收费管理,03
			 */
			put(MZFPFDFS, "03");// " 门诊发票分单方式 "
			put(MZFPSFLD, "03");// " 门诊发票是否连打 "
			put(CFXQ, "03");// 自动调入处方的有效期0.不限制 以天为单位
			put(YS_MZ_FYYF_CY, "03,04,05,07,08");// 草药对应药房，存药房识别
			put(YS_MZ_FYYF_XY, "03,04,05,07,08");// 西药对应药房，存药房识别
			put(YS_MZ_FYYF_ZY, "03,04,05,07,08");// 中药对应药房，存药房识别
			put(SFZJFY, "03");// 收费直接发药:0.不启用，1.启用
			put("QYMZSF", "03,04");// 启用门诊审方标志，0表示不启用，1表示启用，默认是0
			put("YMJDYSGH", "03");// 疫苗建档医生工号，多个疫苗建档医生之间用逗号(,)隔开
			put("CFXYZYMXSL", "03,04");// 录入处方明细数量限制是否启用(西药中药)，0表示不启用,大于0则表示允许录入的最大处方明细数量
			put("CFCYMXSL", "03,04");// 录入处方明细数量限制是否启用(草药)，0表示不启用,大于0则表示允许录入的最大处方明细数量
			put("QYCFCZQZTJ", "03,04");// 录入处方处置前置条件是否启用，如果启用，则未录入诊断不允许录入处方处置，1表示启用，0表示不启用
			put("FPYL", "03");
			put("MZHJSFDYJMC", "03");// 门诊划价收费打印机名称
			put(SFQYJKJSAN, "03");
			/*
			 * 门诊诊疗,04
			 */
			put(YSZJS, "04");// 医生站结算:0.不允许结算，1.允许结算
			put(XSFJJJ, "04");// 显示门诊附加项目 0:不显示,1:显示
			put("QYJYBZ", "04");// 启用检验标志，0表示未启用，1表示启用，默认是0
			put("QYTJBGBZ", "04");// 启用体检报告标志，0表示未启用，1表示启用，默认是0
			put("HQFYYF", "04,05");// 启用门诊医生站处方界面的药房切换功能，1表示启用，0表示不启用，默认是0
			put("QYSXZZ", "04");// 启用双向转诊功能,1表示启用 0表示不启用
			put(SJDOREHY, "04");
			put(SFQYWJZTX, "04");// 是否启用危机值提醒
			put(WJZCLSX, "04");//危急值处理时限
			put(WJZCSSX, "04");//危急值超时时限
			put(WJZTXJGSJ, "04");//危急值提醒间隔时间
			put(CFDYMB, "04");//处方打印模板
			put(MZKSSXZ, "04");//门诊抗生素限制
			put(CRBBGK, "04");//启用传染病报告卡，0表示未启用，1表示启用，默认0
			/*
			 * 药房管理,05
			 */
			put("SX_PREALARM", "05,06");// 值为n，表示药品失效预警截止时间为当前时间加上n个月。若n为非法数字则取默认值3。若为带小数点的数值则取其整数部分
			put("YF_PCPD", "05");// 药库库存盘点是否按批次盘点,false是不按批次盘点,true是按批次盘点
			put("SFQYYFYFY", "05");// 是否启用药房预发药,0是不启用,1启用
			put("MZKCDJSJ", "05");// 门诊库存冻结时间 1是开单,2是收费
			put("KCDJTS", "05");// 库存冻结天数,单位是天
			put(CKSX_KCSL_YF, "05");// 出库顺序_库存数量1.按库存数量出库0.不按库存数量出库,该参数值是1时，才会根据参数CKSX_KCSL_ORDER_YF的值进行排序，否则不会；并且排序时，先效期再数量
			put(CKSX_KCSL_ORDER_YF, "05");// 出库顺序_库存大小 A.库存少的先出库（小先出）D.库存多的先出库（大先出）
			put(CKSX_YPXQ_ORDER_YF, "05"); // 出库顺序_效期先后A.效期早的药品先出库（早先出）D.效期迟的药品先出库（迟先出）
			put(CKSX_YPXQ_YF, "05");// 出库顺序_药品效期 1.按效期出库 0.不按效期出库
			put("YJSJ_YF", "05"); // 月结时间 对应一个月的31天 32为月底结
			put("QYFYCK", "05");// 启用发药窗口 0：不启用，1：启用，默认为0
			put("MZFYZDSXMS", "05");// 门诊发药自动刷新秒数
			put("FYJBZ", "05,06");//发药机WebService服务
			put("YFCKDDYZ", "05");
			put(YFPDJH, "05");
			put(YFPDJHCFLX, "05");
			/*
			 * 药库管理,06
			 */
			put("PLJC", "06");// 批零加成
			put("YKACCOUNTPRICE", "06");// 药库记账标准价格，1表示零售价格，2表示进货价格，3表示批发价格
			put(YPKL_YK, "06");// 药库财务验收药品扣率
			put("YJSJ_YK", "06");// 药库月结时间 对应一个月的31天 32为月底结
			put("KCPD_PC", "06");// 药库库存盘点是否按批次盘点,false是不按批次盘点,true是按批次盘点
			put("PYFS_YK", "06");// 药库盘盈方式
			put("PKFS_YK", "06");// 药库盘亏方式
			
			put("CKSX_KCSL_ORDER_YK", "06");//出库顺序_库存大小
			put("CKSX_KCSL_YK", "06");//出库顺序_库存数量
			put("CKSX_YPPH_ORDER_YK", "06");//出库顺序_批号先后
			put("CKSX_YPPH_YK", "06");//出库顺序_药品批号
			put("CKSX_YPXQ_ORDER_YK", "06");//出库顺序_效期先后
			put("CKSX_YPXQ_YK", "06");//出库顺序_药品效期
			put("YJSJ_YK1", "06");//药库月结时间
			put("YJSJ_YK2", "06");//药库月结时间
			
			/*
			 * 住院管理,07
			 */
			put(FHYZHJF, "07,08");// " 复核医嘱后计费 "
			put(ZYHM, "07");// 住院号码
			put(BAHM, "07");// 病案号码
			put("BAHMDYZYHM", "07");// 启用病案号码等于住院号码标志，0表示不启用，1表示启用，默认是0
			put("BAHMCXFP", "07");// 病案号码重新分配，0表示不启用，1表示启用，默认是0
			put("ZYQFJEDJKZ", "07");// 住院欠费控制 1表示控制 0表示不控制默认为0
			put("ZYJKDYJMC", "07");// 住院缴款打印机名称
			put("ZYJSDYJMC", "07");// 住院结算打印机名称
			put("ZLFYDJ", "07,08");// 诊疗费单价
			put(ZYFPSFZCGY, "07");
			put(JKSJSFZCGY, "07");
			put("CKWH","07");//催款维护
			/*
			 * 病区管理,08
			 */
			put(YZLR_BZLX, "08");// 包装类型,1：医嘱录入使用包装类型 2使用药品信息中的病房包装
			put(BQCHXSWS, "08");// 病区床号显示位数，默认为0时显示全部，其余按值从右截取显示
			put(XSFJJJ_YS, "08");// 显示医生站附加项目 0:不显示,1:显示
			put(XSFJJJ_HS, "08");// 显示护士站附加项目 0:不显示,1:显示
			put(ZYYSQY, "08");// 住院医生站启用 0.不启用1.启用
			put("YZLRFHTYGH", "08");// 医嘱录入可为同一工号 0：不可为同一个人 1：可为同一个人
			put(YZTDMYTS, "08");// 医嘱套打每页条数
			put(YZBDYSFTD, "08");// 医嘱本打印是否需要套打
			put(YZBDYSJ, "08");// 打印时间,1是提交后打印,2是复核后打印
			put(ZKCZHHYDY, "08");// 转科重整后换页打印,0是否,1是是
			put(HSQMBZ, "08");// 护理记录中是否自动显示护士签名0：不显示，1：显示
			put(JDKLS, "08");
			put("YZYZQM", "08");//医嘱本打印签名0：打印签名，1：不打印签名，默认0
			put("YZKPDYZB", "08");//医嘱卡片打印自备药
			put("YZKPDYST", "08");//医嘱卡片打印ST药品
			put("HSZDSTX", "08");//护士站定时提醒
			/*
			 * 医技管理, 09
			 */
			put(BMSZ, "09");// 医技取消窗口前置条件1:门诊医技,2:住院医技,3:同时使用 默认使用3 其余1和2未进行配置
			put("YJZXQXZD", "09");// 医技执行取消诊断，0表示不启用，1表示启用，默认是0
			put("ZXSSFDJ", "09");// 医技项目执行门诊医技只显示已收费单据 1表示显示收费单据 0表示显示全部单据
			/*
			 * 物资管理,10
			 */
			put("WPJFBZ", "10");// 物品计费标志,0表示不启用，1表示启用，默认是0
			put("MZWPJFBZ", "10");// 门诊物品计费标志,0表示不启用，1表示启用，默认是0
			put("WZSFXMJG", "10");// 门诊物资收费项目价格,0表示不启用,1表示启用,默认是0
			put("WZSFXMJGZY", "10");// 住院物资收费项目价格,0表示不启用,1表示启用,默认是0
			/*
			 * 电子病历,11
			 */
			put("QNYDK", "11");// 病程从当前病程前N份打开
			put("YXXJYSXG", "11");// 允许下级医生修改病历 0:不允许 1:允许
			put("QZWZXJY", "11");// 强制完整性校验 0:不需要 1:需要
			put("DYQWZXJY", "11");// 打印前强制完整性校验 0:不是 1:是
			put("QMYZXJY", "11");// 签名一致性校验 0:不是 1:是
			put("SFJYWBKB", "11");// 是否禁用外部拷贝，1：是，0否，默认是0
			put(QYMZDZBL, "11");// 启用门诊电子病历
			/*
			 * 处方点评,12
			 */
			put(CFDPCQSL, "12");// 处方点评抽取数量,默认是100
			/*
			 * 抗菌药物,13
			 */
			put(QYKJYWGL, "13");
			put(QYKJYYY, "13");
			put(QYSJYSSQ, "13");
			put(QYZYKJYWSP, "13");
			put(KJYSYTS, "13");
			/*
			 * 皮试管理,14
			 */
			put(QYPSXT, "14");
			put(PSSJ, "14");
			put(PSSXJG, "14");
			put(PSXQ, "14");
			put(PSSFDYXM, "14");// 皮试收费对应项目
			/*
			 * 家床管理,15
			 */
			put("JCQFKZ", "15");// 家床欠费控制 1表示控制 0表示不控制默认为0
			put("JCJSDYJMC", "15");// 家床结算打印机名称
			put(JCBH, "15");
			put(JCH, "15");
			put(JCZZLJTS, "15");
			put(JCKCGL, "15");
			put(JCEDTS, "15");
			put(JCJKDYJMC, "15");// 家床缴款打印机名称
			put(QYJCGL, "15");

			//家医相关
			put(SFQYJYQY,"16");
			put(JYKS,"16");
			put(JYFY,"16");
		}
	};

	// 公用备注信息
	public static final Map<String, String> defaultPubAlias = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			/*
			 * 病人档案,01
			 */
			put(ZDMZHXNGH, "门诊号码虚拟工号");
			put(MPI, "1:调用中心平台患者基本信息,0:调用本地患者基本信息。");
			/*
			 * 挂号管理,02
			 */
			put(ZDJZHXNGH, "就诊号码虚拟工号");
			put(GHF, "挂号费的收费项目");
			put(YBZLF, "一般诊疗费的收费项目");
			put(ZJF, "专家费的收费项目");
			put(ZLF, "诊疗费的收费项目");
			put(DQGHRQ, "当前挂号日期，挂号初始化使用，不能修改");
			put(DQZBLB, "当前挂号类别，判断上午还是下午，挂号初始化使用，不能修改");
			put(GHFPDY, "挂号时是否打印发票0：不打印；1：打印");
			/*
			 * 收费管理,03
			 */
			put(MZDCFJE, "门诊大处方金额");
			/*
			 * 门诊诊疗,04
			 */
			put(SFQYGWXT, "是否启用社区系统 1:启用社区系统,0:不启用社区系统。");
			put(CRBBGK, "启用传染病报告卡，0表示未启用，1表示启用，默认0");
			/*
			 * 药房管理,05
			 */
			put(JSYP, " 精神药品代码对应特殊药品字典中的数据 ");
			put(MZYP, " 麻醉药品代码 对应特殊药品字典中的数据 ");
			put(YFCKDDYZ, "药房出库单打印纸210X140：A5纸，A4：A4纸，默认：210X140");
			/*
			 * 药库管理,06
			 */
			put(PLJC, " （零售价格-进货价格）/（进货价格）的值 ");
			put(ZDZDTJ, "中心调价站点是否自动调价");
			put(ZXKZJG, " 中心控制价格 0.中心不控制价格 1.中心控制价格 ");
			/*
			 * 住院管理,07
			 */
			put(CWFXH, "床位费序号,自动床位费功能使用到");
			put(ZFCWF, "自负床位费序号,自动床位费功能使用到");
			put(ZLFXH, "诊疗费序号,自动床位费功能使用到");
			put(ICUXH, "ICU序号,自动床位费功能使用到");
			put("CWFZDLJ", "床位费自动累加,1表示自动累加");
			put(ETLRQLX, "收费日期(SFRQ)(住院统计自动使用JSRQ)、结帐日期(JZRQ)和汇总日期(HZRQ)");
			/*
			 * 病区管理,08
			 */
			put("YZYZQM", "医嘱本打印签名0：打印签名，1：不打印签名，默认0");
			put("HSZDSTX", "护士站定时提醒，0表示启用，1表示不启用，默认是0");
			
			/*
			 * 医技管理, 09
			 */
			
			/*
			 * 物资管理,10
			 */
			
			/*
			 * 电子病历,11
			 */
			put(BASYKSMRZ, "病案首页空时默认值：1.空白、2.无、3.-、4./、其他为：/");
			put(EMRVERSION, "电子病历activeX插件版本号，如:4. 6. 0. 4");
			put(QYDZBL, "启用电子病历功能，0：不启用 1：启用，默认启用");
			//处方点评,12
			
			//抗菌药物,13
			
			//皮试管理,14
			
			/*
			 * 家床管理,15
			 */
			// put(JCF, " 检查费归属的收费项目");
			// put(FP_ZLF, " 治疗费归属的收费项目");
			put(GWWEBSERVICE_ADDRESS, "公卫WebService地址");
			// put(YB_ZH_BRXZLIST, "珠海医保用于性质用于控制开放哪些报销");
			// put(QYDDDL, "启用单点登录0：不启用 1：启用");
			// put(SHIYB, "市医保病人性质");
			// put(SHENGYB, "省医保病人性质");
			
			put(JIANYANSERVERIP, "检验服务IP地址");
			put(TIJIANSERVERIP, "体检服务IP地址");
			put(ETLBEGINDATE, "ETL时间段采集数据 开始日期, 例：2014-07-28");
			put(ETLENDDATE, "ETL时间段采集数据 结束日期, 例：2014-07-28");
			put(XZYHDL,"0：不限制，允许用户多客户端同时登录 1：限制单用户登录，登录后则不允许其它客户端登录 2：提示是否强制登录，后登录用户会把前一登录用户踢下线");// 限制用户登录
			put(KLX, "01健康卡;02市民卡;03社保卡;04就诊卡;99其他卡。默认04");
		}
	};

	/**
	 * 公用所属类别 <item key="01" text="病人档案" /> <item key="02" text="挂号管理" /> <item
	 * key="03" text="收费管理" /> <item key="04" text="门诊诊疗" /> <item key="05"
	 * text="药房管理" /> <item key="06" text="药库管理" /> <item key="07" text="住院管理"
	 * /> <item key="08" text="病区管理" /> <item key="09" text="医技管理" /> <item
	 * key="10" text="物资管理" /> <item key="11" text="电子病历" /><item key="12"
	 * text="处方点评" /> <item key="13" text="抗菌药物" /> <item key="14" text="皮试管理"
	 * /><item key="15" text="家床管理"/>
	 */
	public static final Map<String, String> defaultPubCategory = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			/*
			 * 病人档案,01
			 */
			put(ZDMZHXNGH, "01");// 门诊号码虚拟工号
			put(MPI, "01");// 1:调用中心平台患者基本信息,0:调用本地患者基本信息。
			/*
			 * 挂号管理,02
			 */
			put(ZDJZHXNGH, "02,03,04");// 就诊号码虚拟工号
			put(GHF, "02");// 挂号费的收费项目
			put(YBZLF, "02");// 一般诊疗费的收费项目
			put(ZJF, "02");// 专家费的收费项目
			put(ZLF, "02");// 诊疗费的收费项目
			put(DQGHRQ, "02");// 当前挂号日期，挂号初始化使用，不能修改
			put(DQZBLB, "02");// 当前挂号类别，判断上午还是下午，挂号初始化使用，不能修改
			put(GHFPDY, "02");
			/*
			 * 收费管理,03
			 */
			put(MZFPMXSL, "03");
			put(SFSFXZKS, "03");
			put(FPYL, "03");
			put(FP_ZLF, "03");
			put(JCF, "03");
			put(MZDCFJE, "03,04");// 门诊大处方金额
			/*
			 * 门诊诊疗,04
			 */
			put(KSDM_TJ, "04");
			put(SFQYGWXT, "04");// 是否启用社区系统 1:启用社区系统,0:不启用社区系统。
			put(CRBBGK, "04");//启用传染病报告卡，0表示未启用，1表示启用，默认0
			/*
			 * 药房管理,05
			 */
			put(JSYP, "05,06");// 精神药品代码对应特殊药品字典中的数据
			put(MZYP, "05,06");// 麻醉药品代码 对应特殊药品字典中的数据
			put("YFCKDDYZ", "05");
			/*
			 * 药库管理,06
			 */
			put(PLJC, "06");// （零售价格-进货价格）/（进货价格）的值
			put(ZXKZJG, "06");// 中心控制价格 0.中心不控制价格 1.中心控制价格
			put(ZDZDTJ, "06");// 中心调价站点是否自动调价
			/*
			 * 住院管理,07
			 */
			put(CWFXH, "07,08");// 床位费序号
			put(ZFCWF, "07,08");// 自负床位费序号
			put(ZLFXH, "07,08");// 诊疗费序号
			put(ICUXH, "07,08");// ICU序号
			put("CWFZDLJ", "07,08");// 床位费自动累加,1表示自动累加
			put(ETLRQLX, "07");// 收费日期(SFRQ)(住院统计自动使用JSRQ)、结帐日期(JZRQ)和汇总日期(HZRQ)
			/*
			 * 病区管理,08
			 */
			put("YZYZQM", "08");
			put("HSZDSTX", "08");
			/*
			 * 医技管理, 09
			 */
			
			/*
			 * 物资管理,10
			 */
			
			/*
			 * 电子病历,11
			 */
			put(BASYKSMRZ, "11");// 病案首页空时默认值：1.空白、2.无、3.-、4./、其他为：/
			put(EMRVERSION, "11");// 电子病历activeX插件版本号，如:2. 3. 0. 2
			put(QYDZBL, "11");// 启用电子病历功能，0：不启用 1：启用，默认启用
			/*
			 * 处方点评,12
			 */
			
			/*
			 * 抗菌药物,13
			 */
			
			/*
			 * 皮试管理,14
			 */
			
			/*
			 * 家床管理,15
			 */
			
			/*
			 * 未分类
			 */
			put(GWWEBSERVICE_ADDRESS, "");// 公卫WebService地址
			put(JIANYANSERVERIP, "");// 检验服务IP地址
			put(TIJIANSERVERIP, "");// 体检服务IP地址
		}
	};
}
