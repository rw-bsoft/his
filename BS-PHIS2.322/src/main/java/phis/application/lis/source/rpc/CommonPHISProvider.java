package phis.application.lis.source.rpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.util.annotation.RpcService;
import ctd.util.context.ContextUtils;

/**
 * @description
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class CommonPHISProvider implements IPHISProvider {

	private IPHISService phisService;

	public IPHISService getPhisService() {
		return phisService;
	}

	public void setPhisService(IPHISService phisService) {
		this.phisService = phisService;
	}

	private Map<String, Object> buildRequest(Action action,
			Map<String, Object> data) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("action", action.toString());
		request.put("client", getClient());
		request.put("body", data);
		return request;
	}

	private String getClient() {
		String address = (String) ContextUtils.get("clientAddress");
		String domain = (String) ContextUtils.get("fromDomain");
		return domain + ":" + address;
	}

	/**
	 * 病人信息查询
	 */
	@RpcService
	public Map<String, Object> getbrda(Map<String, Object> parameters) throws PHISServiceException {
		Map<String, Object> request = buildRequest(Action.getbrda, parameters);
		return this.phisService.execute(request);
	}

	/**
	 * 门诊病人信息修改
	 */
	@RpcService
	public Map<String, Object> mzbrxxxg(String op, Map<String, Object> brxx)
			throws PHISServiceException {
		brxx.put("op", op);// "create"表示新建,"update"表示修改
		Map<String, Object> request = buildRequest(Action.mzbrxxxg, brxx);
		return this.phisService.execute(request);
	}

	/**
	 * 门诊医技取消执行
	 */
	@RpcService
	public Map<String, Object> mzyjqxzx(List yjxhs) throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", yjxhs);
		Map<String, Object> request = buildRequest(Action.mzyjqxzx, map);
		return this.phisService.execute(request);
	}

	/**
	 * 门诊医技提交
	 */
	@RpcService
	public Map<String, Object> mzyjtj(List<Object> yjxxs, long sqdhId, String djly)
			throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", yjxxs);
		map.put("sqdhId", sqdhId);
		map.put("djly", djly);
		Map<String, Object> request = buildRequest(Action.mzyjtj, map);
		return this.phisService.execute(request);
	}

	/**
	 * 门诊医技作废
	 */
	@RpcService
	public Map<String, Object> mzyjzf(Map<String,Object> parameters) throws PHISServiceException {
		Map<String, Object> request = buildRequest(Action.mzyjzf, parameters);
		return this.phisService.execute(request);
	}
	
	/**
	 * 门诊医技取消作废
	 */
	@RpcService
	public Map<String, Object> mzyjqxzf(Map<String,Object> parameters) throws PHISServiceException {
		Map<String, Object> request = buildRequest(Action.mzyjqxzf, parameters);
		return this.phisService.execute(request);
	}

	/**
	 * 门诊医技状态查询
	 * sqid 申请单号
	 * jgid 机构ID
	 */
	@RpcService
	public Map<String, Object> mzyjztcx(long sqid,String jgid) throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sqid", sqid);
		map.put("jgid", jgid);
		Map<String, Object> request = buildRequest(Action.mzyjztcx, map);
		return this.phisService.execute(request);
	}

	/**
	 * 门诊医技执行
	 */
	@RpcService
	public Map<String, Object> mzyjzx(List yjxhs, String userId) throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yjxhs", yjxhs);
		map.put("userId", userId);
		Map<String, Object> request = buildRequest(Action.mzyjzx, map);
		return this.phisService.execute(request);
	}

	/**
	 * 住院病人信息修改
	 */
	@RpcService
	public Map<String, Object> zybrxxxg(String op, Map<String, Object> brxx)
			throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("op", op);
		map.put("brxx", brxx);
		Map<String, Object> request = buildRequest(Action.zybrxxxg, map);
		return this.phisService.execute(request);
	}

	/**
	 * 住院医嘱取消执行
	 */
	@RpcService
	public Map<String, Object> zyyzqxzx(List yjxhs) throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yjxhs", yjxhs);
		Map<String, Object> request = buildRequest(Action.zyyzqxzx, map);
		return this.phisService.execute(request);
	}

	/**
	 * 住院医嘱状态查询
	 */
	@RpcService
	public Map<String, Object> zyyzztcx(long yjxh) throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yjxh", yjxh);
		Map<String, Object> request = buildRequest(Action.zyyzztcx, map);
		return this.phisService.execute(request);
	}

	/**
	 * 住院医嘱提交
	 */
	@RpcService
	public Map<String, Object> zyyztj(List<Object> yzxxs, long sqdhId, String djly)
			throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yzxxs", yzxxs);
		map.put("sqdhId", sqdhId);
		map.put("djly", djly);
		Map<String, Object> request = buildRequest(Action.zyyztj, map);
		return this.phisService.execute(request);
	}

	/**
	 * 住院医嘱作废
	 */
	@RpcService
	public Map<String, Object> zyyzzf(Map<String,Object> parameters) throws PHISServiceException {
		Map<String, Object> request = buildRequest(Action.zyyzzf, parameters);
		return this.phisService.execute(request);
	}
	
	/**
	 * 住院医嘱取消作废
	 */
	@RpcService
	public Map<String, Object> zyyzqxzf(Map<String,Object> parameters) throws PHISServiceException {
		Map<String, Object> request = buildRequest(Action.zyyzqxzf, parameters);
		return this.phisService.execute(request);
	}

	/**
	 * 住院医嘱执行
	 */
	@RpcService
	public Map<String, Object> zyyzzx(List yjxhs, String userId) throws PHISServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yjxhs", yjxhs);
		map.put("userId", userId);
		Map<String, Object> request = buildRequest(Action.zyyzzx, map);
		return this.phisService.execute(request);
	}

	/**
	 * 费用信息
	 */
	@RpcService
	public Map<String, Object> getfyxx(Map<String, Object> body) throws PHISServiceException {
		Map<String, Object> request = buildRequest(Action.getfyxx, body);
		return this.phisService.execute(request);
	}

	/**
	 * 获取表主键
	 */
	@RpcService
	public Map<String, Object> getTablePKey(Map<String, Object> body)
			throws PHISServiceException {
		Map<String, Object> request = buildRequest(Action.getTablePKey, body);
		return this.phisService.execute(request);
	}

	@RpcService
	public String getTablePKey(String tableName,String pk) throws PHISServiceException {
		
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("tableName", tableName);
		reqMap.put("pkey", pk);
		Map<String, Object> request = buildRequest(Action.getTablePKey, reqMap);
		Map<String, Object> res = this.phisService.execute(request);
		String xml = "<body><tableName>"+res.get("tableName")+"</tableName><pkey>"+res.get("pkey")+"</pkey></body>";
		return xml;
	}
	
	
	/**
	 * 获取机构科室部门信息
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><ksdm>科室代码(否)</ksdm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getUnitDepartment(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getUnitDepartment,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

	/**
	 * 获取机构医生信息
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><ksdm>科室代码(否)</ksdm><ygdm>员工代码(否)</ygdm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getUnitEmployee(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getUnitEmployee,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

	/**
	 * 获取机构挂号类别信息(目前只有普通门诊1)
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getUnitRegisterType(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getUnitRegisterType,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 获取医院信息
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getUnitInfo(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getUnitInfo,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 获取挂号科室信息（科室排班）
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh><mzlb>门诊类别</mzlb><pblb>类别</pblb></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getUnitDepartmentScheduling(String xml)
			throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getUnitDepartmentScheduling,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 获取挂号医生信息（科室医生排班）
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh><ksdm>科室代码</ksdm><pblb>类别</pblb><sxwbz>上下午标志</sxwbz></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getUnitDoctorScheduling(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getUnitDoctorScheduling,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 实时号源查询（查询实时挂号排版信息）
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh><ksdm>科室代码</ksdm><mzlb>门诊类别</mzlb><sxwbz>上下午标志</sxwbz></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getUnitRealTimeRegister(String xml)
			throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getUnitRealTimeRegister,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 用户信息查询(实时和预约的用户信息)
	 * @param xml
	 * {@code <request><klx>卡类型</klx><kh>卡号</kh></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getPatientInfoForRegister(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getPatientInfoForRegister,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

	/**
	 * 实时挂号结算(显示挂号费用清单，待用户确认支付)
	 * @param xml
	 * {@code <request><jgid>机构ID</jgid><sbbh>设备编号</sbbh>
	 * <jzkh>病人就诊卡号</jzkh><mzlb>门诊类别</mzlb><ksdm>医院科室代码</ksdm>
	 * <ysgh>医生工号</ysgh><sxwpb>上下午判别</sxwpb><jylsh>交易流水号码</jylsh></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getPatientRealTimeRegisterFee(String xml)
			throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getPatientRealTimeRegisterFee,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 获取病人信息
	 * @param xml
	 * {@code <request><brid>病人ID</brid><sfzh>身份证号</sfzh><mzhm>门诊号码</mzhm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getPatientInfo(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getPatientInfo,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

	/**
	 * 获取处方信息
	 * @param xml
	 * {@code <request><brid>病人ID</brid><cfhm>处方号码</cfhm><jgid>机构号码</jgid><fphm>发票号码</fphm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getPrescriptionInfo(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getPrescriptionInfo,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 获取处方明细信息
	 * @param xml
	 * {@code <request><brid>病人ID</brid><jzhm>就诊号码</jzhm><fphm>发票号码</fphm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getPrescriptionDetails(String xml)
			throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getPrescriptionDetails,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 获取费用信息
	 * @param xml
	 * {@code <request><brid>病人ID</brid><cfhm>处方号码</cfhm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getInfusionFee(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getInfusionFee,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

	/**
	 * 获取门诊病人申请单信息(心电图)
	 * @param xml
	 * {@code <request><mzhm>门诊号码</mzhm><brid>病人ID</brid><fphm>发票号码</fphm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getECGClinicBill(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getECGClinicBill,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

	/**
	 * 获取住院病人申请单信息(心电图)
	 * @param xml
	 * {@code <request><zyh>住院号</zyh><zyhm>住院号码</zyhm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getECGInhospBill(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getECGInhospBill,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

	/**
	 * 获取药品信息
	 * @param xml
	 * {@code <request><ypxh>药品序号</ypxh><ypmc>药品名称</ypmc></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String getDrugsInfo(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.getDrugsInfo,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

	/**
	 * 获取皮试结果信息(回写)
	 * @param xml
	 * {@code <request><psjg>皮试结果</psjg><ysdm>医生代码</ysdm><pssj>皮试时间</pssj>
	 * <brid>病人ID</brid><ypbh>药品编号</ypbh><cfhm>处方号码</cfhm></request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String receiveSkinTestResult(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.receiveSkinTestResult,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}
	
	/**
	 * 心电图结果回写
	 * @param xml
	 * {@code <request>...心电图报告相关信息...</request>}
	 * @throws PHISServiceException
	 */
	@RpcService
	public String receiveECGReport(String xml) throws PHISServiceException {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("xml", xml);
		Map<String, Object> request = buildRequest(Action.receiveECGReport,map);
		Map<String,Object> res = this.phisService.execute(request);
		return "<response>"+(String)res.get("body")+"</response>";
	}

}
