package phis.application.lis.source.rpc;

import java.util.List;
import java.util.Map;

import ctd.util.annotation.RpcService;

/**
 * @description 区域HIS内部接口
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public abstract interface IPHISProvider {

	/**
	 * 门诊医技提交
	 */
	@RpcService
	public abstract Map<String, Object> mzyjtj(List<Object> yjxxs,long sqdhId,String djly)
			throws PHISServiceException;

	/**
	 * 门诊医技作废
	 */
	@RpcService
	public abstract Map<String, Object>  mzyjzf(Map<String,Object> parameters)
			throws PHISServiceException;
	
	/**
	 * 门诊医技取消作废
	 */
	@RpcService
	public abstract Map<String, Object>  mzyjqxzf(Map<String,Object> parameters)
			throws PHISServiceException;
	
	/**
	 * 门诊医技执行
	 */
	@RpcService
	public abstract Map<String, Object>  mzyjzx(List yjxhs,String userId)
			throws PHISServiceException;
	/**
	 * 门诊医技取消执行
	 */
	@RpcService
	public abstract Map<String, Object>  mzyjqxzx(List yjxhs)
			throws PHISServiceException;
	
	/**
	 * 门诊医技状态查询
	 * sqid 申请单号
	 * jgid 机构ID
	 */
	@RpcService
	public abstract Map<String, Object>  mzyjztcx(long sqid,String jgid)
			throws PHISServiceException;
	
	/**
	 * 病人信息查询
	 */
	@RpcService
	public abstract Map<String, Object>  getbrda(Map<String,Object> parameters)
			throws PHISServiceException;

	/**
	 * 门诊病人信息修改
	 */
	@RpcService
	public abstract Map<String, Object>  mzbrxxxg(String op,Map<String,Object> brxx)
			throws PHISServiceException;
	
	/**
	 * 住院医嘱提交
	 */
	@RpcService
	public abstract  Map<String, Object> zyyztj(List<Object> yzxxs,long sqdhId,String djly)
			throws PHISServiceException;
	
	/**
	 * 住院医嘱作废
	 */
	@RpcService
	public abstract Map<String, Object>  zyyzzf(Map<String,Object> parameters)
			throws PHISServiceException;
	
	/**
	 * 住院医嘱取消作废
	 */
	@RpcService
	public abstract Map<String, Object>  zyyzqxzf(Map<String,Object> parameters)
			throws PHISServiceException;
	
	/**
	 * 住院医嘱执行
	 */
	@RpcService
	public abstract Map<String, Object>  zyyzzx(List yjxhs,String userId)
			throws PHISServiceException;
	
	/**
	 * 住院医嘱取消执行
	 */
	@RpcService
	public abstract Map<String, Object>  zyyzqxzx(List yjxhs)
			throws PHISServiceException;
	
	/**
	 * 住院医嘱状态查询
	 */
	@RpcService
	public abstract Map<String, Object>  zyyzztcx(long yjxh)
			throws PHISServiceException;
	
	/**
	 * 住院病人信息修改
	 */
	@RpcService
	public abstract Map<String, Object>  zybrxxxg(String op,Map<String,Object> brxx)
			throws PHISServiceException;
	
	/**
	 * 获取费用信息
	 * @param body(TYPE,BRXZ,FYXH,FYGB)
	 * @return
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract Map<String, Object>  getfyxx(Map<String,Object> body)
			throws PHISServiceException;
	
	/**
	 * 获取数据库表主键
	 * @param body(tableName,pkey)
	 * @return Map(tableName,pkey)
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract Map<String, Object>  getTablePKey(Map<String,Object> body)
			throws PHISServiceException;
	
	/**
	 * 获取数据库表主键
	 * @param body(tableName,pkey)
	 * @return xml(tableName,pkey)
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getTablePKey(String tableName,String pk) throws PHISServiceException;
	
	/**
	 * 获取机构科室部门信息
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><ksdm>科室代码(否)</ksdm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getUnitDepartment(String xml) throws PHISServiceException;
	
	/**
	 * 获取机构医生信息
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><ksdm>科室代码(否)</ksdm><ygdm>员工代码(否)</ygdm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getUnitEmployee(String xml) throws PHISServiceException;
	
	/**
	 * 获取机构挂号类别信息(目前只有普通门诊1)
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getUnitRegisterType(String xml) throws PHISServiceException;
	
	/**
	 * 获取医院信息
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getUnitInfo(String xml) throws PHISServiceException;
	
	/**
	 * 获取挂号科室信息（科室排班）
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh><mzlb>门诊类别</mzlb><pblb>类别</pblb></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getUnitDepartmentScheduling(String xml) throws PHISServiceException;
	
	/**
	 * 获取挂号医生信息（科室医生排班）
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh><ksdm>科室代码</ksdm><pblb>类别</pblb><sxwbz>上下午标志</sxwbz></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getUnitDoctorScheduling(String xml) throws PHISServiceException;
	
	/**
	 * 实时号源查询（查询实时挂号排版信息）
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh><ksdm>科室代码</ksdm><mzlb>门诊类别</mzlb><sxwbz>上下午标志</sxwbz></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getUnitRealTimeRegister(String xml) throws PHISServiceException;
	
	/**
	 * 用户信息查询(实时和预约的用户信息)
	 * @param xml
	 * {@code <request><klx>卡类型</klx><kh>卡号</kh></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getPatientInfoForRegister(String xml) throws PHISServiceException;
	
	/**
	 * 实时挂号结算(显示挂号费用清单，待用户确认支付)
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh>
	 * <jzkh>病人就诊卡号</jzkh><mzlb>门诊类别</mzlb><ksdm>医院科室代码</ksdm>
	 * <ysgh>医生工号</ysgh><sxwpb>上下午判别</sxwpb><jylsh>交易流水号码</jylsh></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getPatientRealTimeRegisterFee(String xml) throws PHISServiceException;
	
	/**
	 * 获取病人信息
	 * @param xml
	 * {@code <request><brid>病人ID</brid><sfzh>身份证号</sfzh><mzhm>门诊号码</mzhm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getPatientInfo(String xml) throws PHISServiceException;
	
	/**
	 * 获取处方信息
	 * @param xml
	 * {@code <request><brid>病人ID</brid><cfhm>处方号码</cfhm><jgid>机构号码</jgid><fphm>发票号码</fphm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getPrescriptionInfo(String xml) throws PHISServiceException;
	
	/**
	 * 获取处方明细信息
	 * @param xml
	 * {@code <request><brid>病人ID</brid><cfhm>处方号码</cfhm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getPrescriptionDetails(String xml) throws PHISServiceException;
	
	/**
	 * 获取费用信息
	 * @param xml
	 * {@code <request><brid>病人ID</brid><jzhm>就诊号码</jzhm><fphm>发票号码</fphm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getInfusionFee(String xml) throws PHISServiceException;
	
	/**
	 * 获取药品信息
	 * @param xml
	 * {@code <request><ypxh>药品序号</ypxh><ypmc>药品名称</ypmc></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getDrugsInfo(String xml) throws PHISServiceException;
	
	/**
	 * 获取皮试结果信息(回写)
	 * @param xml
	 * {@code <request><psjg>皮试结果</psjg><ysdm>医生代码</ysdm><pssj>皮试时间</pssj>
	 * <brid>病人ID</brid><ypbh>药品编号</ypbh><cfhm>处方号码</cfhm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  receiveSkinTestResult(String xml) throws PHISServiceException;
	
	/**
	 * 获取门诊病人申请单信息(心电图)
	 * @param xml
	 * {@code <request><mzhm>门诊号码</mzhm><brid>病人ID</brid><fphm>发票号码</fphm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getECGClinicBill(String xml) throws PHISServiceException;
	
	/**
	 * 获取住院病人申请单信息(心电图)
	 * @param xml
	 * {@code <request><zyh>住院号</zyh><zyhm>住院号码</zyhm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  getECGInhospBill(String xml) throws PHISServiceException;
	
	/**
	 * 心电图结果回写
	 * @param xml
	 * {@code <request>...心电图报告相关信息...</request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public abstract String  receiveECGReport(String xml) throws PHISServiceException;
}
