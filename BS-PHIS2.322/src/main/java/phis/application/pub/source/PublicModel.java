/**
 * @(#)PublicModel.java Created on 2012-1-12 上午9:43:35
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.pub.source;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.application.pix.source.EmpiModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.EHRUtil;
import phis.source.utils.ParameterUtil;
import com.alibaba.fastjson.JSONException;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PublicModel implements BSPHISEntryNames {
	// 不需要做首诊测压也不用做高血压核实。
	public static final int DO_HYPERTENSION_NON = 0;
	// 需要做首诊测压
	public static final int DO_HYPERTENSION_FIRST = 1;
	// 需要做高血压核实
	public static final int DO_HYPERTENSION_CHECK = 2;
	// 需要建立高血压档案。
	public static final int DO_HYPERTENSION_CREATE = 3;

	private static final Logger logger = LoggerFactory
			.getLogger(PublicModel.class);

	protected BaseDAO dao = null;

	/**
	 * 
	 * @param dao
	 */
	public PublicModel(BaseDAO dao) {
		this.dao = dao;
	}

	public PublicModel() {
	}

	/**
	 * 获取责任医生ID
	 * 
	 * @param empiId
	 * @param recordEntry
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getManaDoctor(String empiId, String recordEntry)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select manaDoctorId from ")
				.append(recordEntry)
				.append(" where empiId = :empiId and status=:status")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		String manaDoctorId = "";
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("get manaDoctorId failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取责任医生ID失败！");
		}
		if (null != rsMap) {
			manaDoctorId = (String) rsMap.get("manaDoctorId");
		}
		return manaDoctorId;
	}

	/**
	 * 取主档管辖机构
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getManaUnit(String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer("select manaUnitId from ")
				.append(EHR_HealthRecord)
				.append(" where empiId=:empiId and status=:status").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Get manaUnitId of EHR_HealthRecord with empiId = ["
					+ empiId + "] and status = ["
					+ Constants.CODE_STATUS_NORMAL + "]", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "取主档管辖机构失败！");
		}
		if (rsMap == null) {
			logger.error("Get manaUnitId of EHR_HealthRecord with empiId = ["
					+ empiId + "] and status = ["
					+ Constants.CODE_STATUS_NORMAL + "]");
			throw new ModelDataOperationException(
					"Get manaUnitId of EHR_HealthRecord with empiId = ["
							+ empiId + "] and status = ["
							+ Constants.CODE_STATUS_NORMAL + "]");
		}
		return (String) rsMap.get("manaUnitId");
	}

	/**
	 * 获取年度随访计划 <br/>
	 * year 1:前一年度，2:本年度，3:下一年度。<br/>
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getVisitPlan(Map<String, Object> req,
			Map<String, Object> res) throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat(
				Constants.DEFAULT_SHORT_DATE_FORMAT);
		String strStartDate = sdf.format(req.get("startDate"));
		String strEndDate = sdf.format(req.get("endDate"));
		String empiId = (String) req.get("empiId");
		String instanceType = (String) req.get("instanceType");

		String initCnd = "['and', ['eq', ['$', 'empiId'], ['s', '" + empiId
				+ "']], ['eq', ['$', 'planType'], ['s', '" + instanceType
				+ "']]]";

		String dateCnd = "['and', ['ge', ['$', \"str(beginDate,'yyyy-MM-dd')\"], ['s', '"
				+ strStartDate
				+ "']], ['lt', ['$', \"str(beginDate,'yyyy-MM-dd')\"], ['s', '"
				+ strEndDate + "']]]";

		String cnd = "['and' ,['and', " + initCnd + ", " + dateCnd
				+ "], ['ne', ['$', 'planStatus'], ['s', '"
				+ Constants.VISIT_PLAN_WRITEOFF + "']]]";
		try {
			return dao.doQuery(CNDHelper.toListCnd(cnd), " beginDate asc ",
					PUB_VisitPlan);
		} catch (Exception e) {
			logger.error("Get visit plan with year failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取年度随访计划失败！");
		}
	}

	/**
	 * 判断是否需要进行首诊测压、高血压核实
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int ifHypertensionFirst(String empiId)
			throws ModelDataOperationException {
		EmpiModel em = new EmpiModel(dao);
		int age = em.getAge(empiId);
		if (age == -1) {
			logger.error("Could not found empi record by empiId:" + empiId
					+ " or birthday info is not registed.");
			throw new ModelDataOperationException(
					Constants.CODE_RECORD_NOT_FOUND, "该个人编号无法找到个人信息或出生日期未填写。");
		}
		if (age < 35) {
			return DO_HYPERTENSION_NON;
		}

		// query hyertensionfisrt record
		Calendar startc = Calendar.getInstance();
		Calendar endc = Calendar.getInstance();
		startc.set(startc.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		endc.set(endc.get(Calendar.YEAR), 11, 31, 23, 59, 59);

		// String hql = new StringBuffer("select count(*) as c from ")
		// .append(MDC_HypertensionRecord)
		// .append(" where empiId=:empiId and status=:status").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("empiId", empiId);
		paramMap.put("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		Map<String, Object> rsMap = null;
		// try {
		// // rsMap = dao.doLoad(hql, paramMap);
		// } catch (PersistentDataOperationException e) {
		// logger.error(
		// "Failed to count of MDC_HypertensionRecord with empiId = ["
		// + empiId + "] and status = [0]", e);
		// throw new ModelDataOperationException(
		// Constants.CODE_DATABASE_ERROR, "无状态为正常的高血压记录！");
		// }
		long l = ((Long) rsMap.get("c")).longValue();
		if (l > 0) {
			return DO_HYPERTENSION_NON;
		}

		// String hypertensionFirstHql = new StringBuffer(
		// "select diagnosisType from ").append(MDC_HypertensionFirst)
		// .append(" where empiId = :empiId and hypertensionFirstDate>")
		// .append(":startDate and hypertensionFirstDate<:endDate")
		// .toString();
		paramMap.clear();
		paramMap.put("empiId", empiId);
		paramMap.put("startDate", startc.getTime());
		paramMap.put("endDate", endc.getTime());
		rsMap.clear();
		// try {
		// // rsMap = dao.doLoad(hypertensionFirstHql, paramMap);
		// } catch (PersistentDataOperationException e) {
		// logger.error(
		// "Failed to get diagnosisType of MDC_HypertensionFirst", e);
		// throw new ModelDataOperationException(
		// Constants.CODE_DATABASE_ERROR, "获取首诊测压核实结果失败！");
		// }
		String diagnosisType = (String) rsMap.get("diagnosisType");
		if (diagnosisType == null) {
			return DO_HYPERTENSION_FIRST;
		}
		if ("3".equals(diagnosisType)) {
			return DO_HYPERTENSION_NON;
		}
		if ("1".equals(diagnosisType)) {
			return DO_HYPERTENSION_CREATE;
		}
		return DO_HYPERTENSION_CHECK;
	}

	/**
	 * 获取生命周期
	 * 
	 * @param birthday
	 * @param calculateDate
	 * @param session
	 * @return lifeCycle的code，text以及年龄。
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getLifeCycle(Date birthday, Date calculateDate)
			throws PersistentDataOperationException {
		int age = EHRUtil.calculateAge(birthday, calculateDate);
		List<?> cnd1 = CNDHelper.createSimpleCnd("le", "startAge", "i", age);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ge", "endAge", "i", age);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		return dao.doQuery(cnd, "", EHR_LifeCycle).get(0);
	}

	// ===========================-- 写日志 --===================================
	/**
	 * 写日志
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void writeLog(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException, ValidateException {
		HashMap<String, Object> req = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		req.put("op", "create");
		req.put("body", body);
		req.put("schema", PUB_Log);
		body.put("operType", (String) body.get("operType"));
		body.put("recordType", (String) body.get("recordType"));
		body.put("empiId", (String) body.get("empiId"));
		body.put("personName", (String) body.get("personName"));
		body.put("idCard", (String) body.get("idCard"));
		body.put("createUnit", user.getProperty("manaUnitId"));
		body.put("createUser", user.getId());
		body.put("createTime", BSHISUtil.toString(new Date(), null));
		try {
			dao.doSave("create", PUB_Log, body, true);
		} catch (PersistentDataOperationException e) {
			logger.error("create log data of PUB_Log failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "日志记录失败！");
		}
	}

	/**
	 * 计算人员年龄
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	// 新模式:
	// 年龄大于等于3*12个月的，用岁表示；
	// 小于3*12个月而又大于等于1*12个月的，用岁月表示；
	// 小于12个月而又大于等于6个月的，用月表示；
	// 小于6个月而大于等于29天的，用月天表示；
	// 大于72小时小于29天的，用天表示；
	// 小于72小时的，用小时表示。

	public void doPersonAge(Date birthday, Map<String, Object> res) {
		Calendar now = Calendar.getInstance();
		Calendar birth = Calendar.getInstance();
		birth.setTime(birthday);
		int age = BSHISUtil.calculateAge(birthday, null);
		String reAge = age + "岁";
		if (age < 3 && age >= 1) {
			int month = BSHISUtil.getMonths(birthday, now.getTime());
			reAge = age + "岁";
			if ((month - 12 * age) > 0) {
				reAge = age + "岁" + (month - 12 * age) + "月";
			}
		} else if (age < 1) {
			int month = BSHISUtil.getMonths(birthday, now.getTime());
			if (month < 12 && month >= 6) {
				reAge = month + "月";
			} else {
				int day = BSHISUtil.getPeriod(birthday, null);
				if (day >= 29 && month > 0) {
					if (now.get(Calendar.DAY_OF_MONTH) >= birth
							.get(Calendar.DAY_OF_MONTH)) {
						day = now.get(Calendar.DAY_OF_MONTH)
								- birth.get(Calendar.DAY_OF_MONTH);
					} else {
						now.set(Calendar.MONTH, birth.get(Calendar.MONTH) + 1);
						day = now.get(Calendar.DAY_OF_YEAR)
								- birth.get(Calendar.DAY_OF_YEAR);
					}
					reAge = month + "月";
					if (day > 0) {
						reAge = month + "月" + day + "天";
					}
				} else {
					if (day >= 4) {
						if((now.get(Calendar.DAY_OF_YEAR)- birth.get(Calendar.DAY_OF_YEAR))>0){
							day = now.get(Calendar.DAY_OF_YEAR)
									- birth.get(Calendar.DAY_OF_YEAR);
						}
						reAge = day-1 + "天";
					} else {
						int hour = now.get(Calendar.HOUR_OF_DAY)
								- birth.get(Calendar.HOUR_OF_DAY);
						reAge = hour + 24 * (day) + "小时";
					}
				}
			}
		}
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("age", age);
		resBody.put("ages", reAge);
		res.put("body", resBody);

	}

	public void doSaveSystemParamsType(Map<String, Object> body, Context ctx) {
		String sql = "update GY_XTCS set ";
		
	}
	//zhaojian 2019-02-20 第三方接口调用次数统计接口
	private  static String httpURLPOSTCase(String methodUrl, String body) {
		   HttpURLConnection connection = null;
		   OutputStream dataout = null;
		   BufferedReader reader = null;
		   StringBuilder result = null;
		   String line = null;
		   try {
		       URL url = new URL(methodUrl);
		       connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
		      connection.setDoOutput(true);// 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,默认情况下是false
		      connection.setDoInput(true); // 设置是否从connection读入，默认情况下是true;
		      connection.setRequestMethod("POST");// 设置请求方式为post,默认GET请求
		      //connection.setUseCaches(false);// post请求不能使用缓存设为false
		      //connection.setConnectTimeout(3000);// 连接主机的超时时间
		      //connection.setReadTimeout(3000);// 从主机读取数据的超时时间
		      //connection.setInstanceFollowRedirects(true);// 设置该HttpURLConnection实例是否自动执行重定向
		      //connection.setRequestProperty("connection", "Keep-Alive");// 连接复用
		      connection.setRequestProperty("charset", "utf-8");
		      
		      connection.setRequestProperty("Content-Type", "application/json");
//		           connection.setRequestProperty("Authorization", "Bearer 66cb225f1c3ff0ddfdae31rae2b57488aadfb8b5e7");
//		          connection.connect();// 建立TCP连接,getOutputStream会隐含的进行connect,所以此处可以不要

		      dataout = new DataOutputStream(connection.getOutputStream());// 创建输入输出流,用于往连接里面输出携带的参数
		 //      body = "[{\"orderNo\":\"44921902\",\"adviser\":\"张怡筠\"}]";
		     // dataout.write(body.getBytes());
		      ((DataOutputStream) dataout).writeChars(body);
		     // dataout.flush();
		     // dataout.close();

		      if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
		          reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
		          result = new StringBuilder();
		          // 循环读取流
		          while ((line = reader.readLine()) != null) {
		             result.append(line).append(System.getProperty("line.separator"));//
		          }
		          System.out.println(result.toString());
		      }
		     } catch (IOException e) {
		      e.printStackTrace();
		     } 
		      return result.toString();
		}
	
	private static String getIpByEthNum() {
       try {
           Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
           InetAddress ip;
           while (allNetInterfaces.hasMoreElements()) {
               NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();      
                   Enumeration addresses = netInterface.getInetAddresses();
                   while (addresses.hasMoreElements()) {
                       ip = (InetAddress) addresses.nextElement();
                       if (ip !=null  && ip instanceof Inet4Address) {
                           return ip.getHostAddress();
                       }
                   }
           }
       } catch (SocketException e) {
           logger.error(e.getMessage(), e);
       }
       return "获取服务器IP错误";
   }
	
	/**
	 * 调用第三方接口实现次数统计
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void visitCountLogForInterface(Map<String, Object> body,Map<String, Object> res,Context ctx)
			throws ModelDataOperationException, ValidateException {
		//HashMap<String, Object> req = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> map_data =  (Map<String, Object>)body.get("data");
		String callType = (String) body.get("callType") == null ? "" : body.get("callType").toString();
		String patientCardNo = (String) body.get("patientCardNo") == null ? "" : body.get("patientCardNo").toString();
		String patientName = (String) body.get("patientName") == null ? "" : body.get("patientName").toString();
		String apiCode = (String) body.get("apiCode") == null ? "" : body.get("apiCode").toString();//调用接口编码:XTGWBB 协同公卫报病、 XTGWSFGL 协同公卫随访管理
		String fromDomain = (String) body.get("fromDomain") == null ? "" : body.get("fromDomain").toString();
		String toDomain = (String) body.get("toDomain") == null ? "" : body.get("toDomain").toString();
		String operSystemCode = (String) body.get("operSystemCode") == null ? "" : body.get("operSystemCode").toString();
		String operSystemName = (String) body.get("operSystemName") == null ? "" : body.get("operSystemName").toString();
		String serviceBean = (String) body.get("serviceBean") == null ? "" : body.get("serviceBean").toString();
		String methodDesc = (String) body.get("methodDesc") == null ? "" : body.get("methodDesc").toString();
		String stat = (String) body.get("stat") == null ? "" : body.get("stat").toString();
		String avgTimeCost = (body.get("avgTimeCost") == null ? ((int) (Math.random( )*50+50)+"") : body.get("avgTimeCost").toString());
		String request = (String) body.get("request") == null ? "" : body.get("request").toString();
		String response = (String) body.get("response") == null ? "" : body.get("response").toString();
		String curUserId = user.getUserId();
		String curUnitId = user.getManageUnit().getId();// 用户的机构ID
		String organname = user.getManageUnit().getName();
		String USER_NAME = user.getUserName();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
		String curDate = dateFormat.format(new Date());
		try {
			//String ip = getIpByEthNum();
			String ip = "192.168.10.121";
			String ipc = InetAddress.getLocalHost().getHostAddress();
			String json="{ \n"+
				"\"orgCode\":\""+curUnitId+"\",\n"+
				"\"orgName\":\""+organname+"\",\n"+
				"\"ip\":\""+ip+"\",\n"+
				"\"opertime\":\""+curDate+"\",\n"+
				"\"operatorCode\":\""+curUserId+"\",\n"+
				"\"operatorName\":\""+USER_NAME+"\",\n"+
				"\"patientCardNo\":\""+patientCardNo+"\",\n"+
				"\"patientName\":\""+patientName+"\",\n"+
				"\"callType\":\""+callType+"\",\n"+
				"\"apiCode\":\""+apiCode+"\",\n"+
				"\"fromDomain\":\""+fromDomain+"\",\n"+
				"\"toDomain\":\""+toDomain+"\",\n"+
				"\"clientAddress\":\""+ipc+"\",\n"+
				"\"operSystemCode\":\""+operSystemCode+"\",\n"+
				"\"operSystemName\":\""+operSystemName+"\",\n"+
				"\"serviceBean\":\""+serviceBean+"\",\n"+
				"\"methodDesc\":\""+methodDesc+"\",\n"+
				"\"statEnd\":\""+curDate+"\",\n"+
				"\"stat\":\""+stat+"\",\n"+
				"\"avgTimeCost\":\""+avgTimeCost+"\",\n"+
				"\"request\":\""+request+"\",\n"+
				"\"response\":\""+response+"\"\n"+
			          "}";	
			System.out.println(json);
			String jgid = user.getManageUnitId();
			String url = "";
			url = ParameterUtil.getParameter(jgid.substring(0,4),"URL_VisitCount", ctx);
			if(url.equals("") || url.equals("null")){
				url = "http://192.168.10.178:8881/apiCallLog";
			}
			res.put("url",url);
			String result = httpURLPOSTCase(url, json);
	        System.out.println("调用第三方接口实现次数统计返回结果："+result);
			res.put("body", "调用第三方接口实现次数统计返回结果："+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 待诊病人双击后调用市健康档案浏览器调阅接口（统计次数）
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void doVisitCountForCityInterface(Map<String, Object> body,Map<String, Object> res,Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
				String dyHql = "select jgdm_dr as YLJGDM from ehr_manageunit where manageunitid='" + user.getManageUnitId() + "'";
				Map<String, Object> dy = dao.doSqlLoad(dyHql, null);
				String yljgdm=user.getManageUnitId();
				if(dy!=null && dy.get("YLJGDM")!=null && !("".equals(dy.get("YLJGDM") + ""))){
						yljgdm=dy.get("YLJGDM") + "";
				}else{
					logger.info("获取当前登录人所在机构编码对应的省编码失败，请确认ehr_manageunit表中字段jgdm_dr是否已对照！");
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "获取当前登录人所在机构编码对应的省编码失败，请确认ehr_manageunit表中字段jgdm_dr是否已对照！");
				}
				StringBuffer dyInfo = new StringBuffer();
				dyInfo.append("<url>");
				dyInfo.append("<yljgdm>"+yljgdm+"</yljgdm>");//医疗机构代码
				dyInfo.append("<ysgh>"+user.getUserId()+"</ysgh>");//医生工号
				dyInfo.append("<ysxm>"+user.getUserName()+"</ysxm>");//医生姓名
				dyInfo.append("<ksmc>"+body.get("ksmc")+"</ksmc>");//科室名称
				dyInfo.append("<ip>"+body.get("ip")+"</ip>");
				dyInfo.append("<zjhm>"+body.get("zjhm")+"</zjhm>");//证件号码
				dyInfo.append("<hzxm>"+body.get("hzxm")+"</hzxm>");//患者姓名
				dyInfo.append("<ywxt>"+body.get("ywxt")+"</ywxt>");
				dyInfo.append("</url>");
				//获取东软健康档案浏览器url
				String url=DictionaryController.instance().getDic("phis.dictionary.drwebservice").getText("url");
				phis.source.ws.CityEhrBrowseAPIService.impl.OuterEncryServiceImplServiceLocator myService=new phis.source.ws.CityEhrBrowseAPIService.impl.OuterEncryServiceImplServiceLocator();
				myService.setOuterEncryServiceImplPortEndpointAddress(url);
				phis.source.ws.CityEhrBrowseAPIService.EncryWebService ews =myService.getOuterEncryServiceImplPort();
				logger.error("-----"+ sdf.format(new Date()) + "开始调用调用市健康档案浏览器调阅接口（统计次数），接口地址："+url+"，请求参数："+dyInfo.toString());
				String str=ews.encryXmlUrl(dyInfo.toString(),3000);//设置接口超时时间为3秒
				logger.error("-----"+ sdf.format(new Date()) + "调用市健康档案浏览器调阅接口（统计次数）成功，返回："+str);
		}catch (Exception e){
			logger.error("-----"+ sdf.format(new Date()) + "调用市健康档案浏览器调阅接口（统计次数）失败："+e.getMessage().toString());
		}
	}
	public static void main(String[] args) throws ServiceException, RemoteException {
			StringBuffer dyInfo = new StringBuffer();
			dyInfo.append("<url>");
			dyInfo.append("<yljgdm></yljgdm>");
			dyInfo.append("<ysgh></ysgh>");
			dyInfo.append("<ysxm></ysxm>");
			dyInfo.append("<ksmc></ksmc>");
			dyInfo.append("<ip></ip>");
			dyInfo.append("<zjhm></zjhm>");
			dyInfo.append("<hzxm></hzxm>");
			dyInfo.append("<ywxt></ywxt>");
			dyInfo.append("</url>");
			logger.error("开始调用市接口（统计次数），请求参数："+dyInfo.toString());
			phis.source.ws.CityEhrBrowseAPIService.impl.OuterEncryServiceImplServiceLocator myService=new phis.source.ws.CityEhrBrowseAPIService.impl.OuterEncryServiceImplServiceLocator();
			myService.setOuterEncryServiceImplPortEndpointAddress("http://11.42.30.12:7001/ehrEncry/ehrencry/service/outerEncry");
			phis.source.ws.CityEhrBrowseAPIService.EncryWebService ews =myService.getOuterEncryServiceImplPort();
			String str=ews.encryXmlUrl(dyInfo.toString(),1000);
			logger.error("调用市接口（统计次数）成功，返回："+str);
	}
}
