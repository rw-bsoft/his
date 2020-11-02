package phis.application.pay.source;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceLocator;
import phis.source.ws.SelfHelpMachineAPIService.LSTFWebserviceSoapStub;
import java.util.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

import javax.xml.rpc.ServiceException;

public class SelfHelpMachineModel implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(SelfHelpMachineModel.class);

	public SelfHelpMachineModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	//测试
	public static void main(String[] args) throws ServiceException, RemoteException
	{			
		LSTFWebserviceLocator service = new LSTFWebserviceLocator();
		service.setLSTFWebserviceSoap12EndpointAddress("http://192.168.10.123:8076/LSTFWebservice.asmx");
		LSTFWebserviceSoapStub stub = (LSTFWebserviceSoapStub)service.getLSTFWebserviceSoap();
		String res = stub.helloWorld();
		System.out.println("返回：" + res);
		res = stub.trans("E709FA0C5E7A5EF95EB5AC1D8EABA0AC", "H0501", "01", "02", "130507384926321108", "2016111414071428", "0.01", "挂号费|1|管理员", "null", "null", "null");
		System.out.println("返回：" + res);
	}
	
	/**
	 * 自助机挂号退款接口
	 * 
	 * @param ms_ghmx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doGhRefund(Map<String, Object> ms_ghmx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();
			String url = ParameterUtil.getParameter(manageUnit.substring(0,6), "SELFHELPMACINEAPIURL", null);
			if(url==null){
				url = "http://192.168.10.123:8076/LSTFWebservice.asmx";
				//http://192.168.10.123:8076/LSTFWebservice.asmx   ---old
				//http://10.2.202.19:8085/service.asmx --new
			}
			DictionaryItem dicAPI = DictionaryController.instance().getDic("phis.dictionary.SelfHelpMachineAPI").getItem(manageUnit);
			String aeskey = "";
			String clientid = "";
			if(dicAPI!=null){
				aeskey = dicAPI.getProperty("aeskey").toString();
				clientid = dicAPI.getProperty("clientid").toString();
			}
			LSTFWebserviceLocator service = new LSTFWebserviceLocator();
			service.setLSTFWebserviceSoap12EndpointAddress(url);
			LSTFWebserviceSoapStub stub = (LSTFWebserviceSoapStub)service.getLSTFWebserviceSoap();
			//获取交易流水号
			StringBuffer hql = new StringBuffer("select PAYMONEY,VOUCHERNO,PATIENTYPE,PATIENTID,NAME,SEX,IDCARD,BIRTHDAY,VERIFYNO " +
					"from PAYRECORD_SELFHELPMACHINE where STATUS='1' and PATIENTID =:PATIENTID and ORGANIZATIONCODE =:ORGANIZATIONCODE " +
					"and VOUCHERNO =:VOUCHERNO and PAYSERVICE =:PAYSERVICE");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("PAYSERVICE","1");
			parameters.put("PATIENTID",ms_ghmx.get("BRID")+"");
			parameters.put("ORGANIZATIONCODE", ms_ghmx.get("JGID")+"");
			parameters.put("VOUCHERNO", ms_ghmx.get("JZHM")+"");
			Map<String, Object> mapPayRecord = dao.doSqlLoad(hql.toString(),parameters);
			if(mapPayRecord==null){
				throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求自助机聚合支付平台退款接口.");//直接手动抛出异常
			}
			String PayType = "";
			//自助机微信支付
			if((ms_ghmx.get("FFFS")+"").equals("39")){
				PayType = "01";//支付类型：01 微信 02 支付宝 11 百富MISPOS 12 银石MISPOS
			}
			//自助机支付宝支付
			if((ms_ghmx.get("FFFS")+"").equals("40")){
				PayType = "02";//支付类型：01 微信 02 支付宝 11 百富MISPOS 12 银石MISPOS
			}
			String TxnType = "02";//交易类型：00签到； 01 收费；02 退费； 06余额查询 POS；07 结算；23测试通讯
			String QRCodeNo = "";//二维码号：仅用于扫用户支付宝或者微信二维码支付时用(调用POS自带扫码功能无需)
			String COMM_HIS = "TH"+ms_ghmx.get("JZHM");//HIS端流水号：非收退交易时可以不传
			String Je = mapPayRecord.get("PAYMONEY")+"";//交易金额：非收退交易时可以不传
			String Body = "挂号退费";//费用描述：如"挂号费"、"预交金"等
			String OldCOMM_SN = mapPayRecord.get("VERIFYNO")+"";//原平台流水号：退款用
			String OldCOMM_HIS = "";//原HIS流水号：退款用
			String OldHostSer = "";//原交易参考号12位：MIS-POS退货时用，必须输入原交易参考号，此号码在凭条上，由操作员手工录入			
			String res = stub.trans(clientid, aeskey, PayType, TxnType, QRCodeNo, COMM_HIS, Je, Body, OldCOMM_SN, OldCOMM_HIS, OldHostSer);
			System.out.println("返回：" + res);
			if(!res.contains("\"RspCode\":\"00\"")){
				throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求自助机聚合支付平台退款接口.");//直接手动抛出异常
			}
			mapPayRecord.put("PAYSERVICE", "-1");
			mapPayRecord.put("ORGANIZATIONCODE", manageUnit);
			mapPayRecord.put("VERIFYNO", "");
			mapPayRecord.put("PAYTIME",new Date());
			mapPayRecord.put("BANKTYPE", "");
			mapPayRecord.put("BANKCODE", "");
			mapPayRecord.put("BANKNO", "");
			mapPayRecord.put("PAYSOURCE", "1");
			mapPayRecord.put("PAYNO", "");
			mapPayRecord.put("COLLECTFEESCODE", user.getUserId());
			mapPayRecord.put("COLLECTFEESNAME", user.getUserName());
			mapPayRecord.put("CARDTYPE", "");
			mapPayRecord.put("CARDNO", "");
			mapPayRecord.put("STATUS", "1");
			//mapPayRecord.put("SENDXML", Hibernate.createBlob(paramsxml.getBytes("UTF-8")));
			//mapPayRecord.put("RETNRNXML", Hibernate.createBlob("".getBytes("UTF-8")));
			mapPayRecord.put("return_code", "");
			mapPayRecord.put("return_msg", "");
			mapPayRecord.put("TRADENO", "");
			mapPayRecord.put("TKBZ", "1");
			mapPayRecord.put("REFUND_FEE", 0);
			mapPayRecord.put("HOSPNO_ORG", "");
			Map<String, Object> genValue = dao.doSave("create",
					BSPHISEntryNames.PAYRECORD_SELFHELPMACHINE, mapPayRecord, true);
			//long orderid = Long.parseLong(genValue.get("ID") + "");// 获取PAYRECORD的主键
		} catch (Exception e) {
			logger.error("请求自助机聚合支付平台退款接口.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求自助机聚合支付平台退款接口.");
		}
	}

	/**
	 * 自助机门诊结算退款接口
	 *
	 * @param mzmx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doRefund(Map<String, Object> mzmx) throws ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();
			String url = ParameterUtil.getParameter(manageUnit.substring(0,6), "SELFHELPMACINEAPIURL", null);
			if(url==null){
				url = "http://192.168.10.123:8076/LSTFWebservice.asmx";
			}
			DictionaryItem dicAPI = DictionaryController.instance().getDic("phis.dictionary.SelfHelpMachineAPI").getItem(manageUnit);
			String aeskey = "";
			String clientid = "";
			if(dicAPI!=null){
				aeskey = dicAPI.getProperty("aeskey").toString();
				clientid = dicAPI.getProperty("clientid").toString();
			}
			LSTFWebserviceLocator service = new LSTFWebserviceLocator();
			service.setLSTFWebserviceSoap12EndpointAddress(url);
			LSTFWebserviceSoapStub stub = (LSTFWebserviceSoapStub)service.getLSTFWebserviceSoap();
			//获取交易流水号
			StringBuffer hql = new StringBuffer("select PAYMONEY,VOUCHERNO,PATIENTYPE,PATIENTID,NAME,SEX,IDCARD,BIRTHDAY,VERIFYNO " +
					"from PAYRECORD_SELFHELPMACHINE where STATUS='1' and PATIENTID =:PATIENTID and ORGANIZATIONCODE =:ORGANIZATIONCODE " +
					"and VOUCHERNO =:VOUCHERNO and PAYSERVICE =:PAYSERVICE");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("PAYSERVICE","2");
			parameters.put("PATIENTID",mzmx.get("BRID")+"");
			parameters.put("ORGANIZATIONCODE", mzmx.get("JGID")+"");
			parameters.put("VOUCHERNO", mzmx.get("FPHM")+"");
			Map<String, Object> mapPayRecord = dao.doSqlLoad(hql.toString(),parameters);
			if(mapPayRecord==null){
				throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求自助机聚合支付平台退款接口.");//直接手动抛出异常
			}
			String PayType = "";
			//自助机微信支付
			if((mzmx.get("FFFS")+"").equals("39")){
				PayType = "01";//支付类型：01 微信 02 支付宝 11 百富MISPOS 12 银石MISPOS
			}
			//自助机支付宝支付
			if((mzmx.get("FFFS")+"").equals("40")){
				PayType = "02";//支付类型：01 微信 02 支付宝 11 百富MISPOS 12 银石MISPOS
			}
			String TxnType = "02";//交易类型：00签到； 01 收费；02 退费； 06余额查询 POS；07 结算；23测试通讯
			String QRCodeNo = "";//二维码号：仅用于扫用户支付宝或者微信二维码支付时用(调用POS自带扫码功能无需)
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String strtime = sdf.format(date);
			String COMM_HIS = "TH"+strtime+mzmx.get("FPHM");//HIS端流水号：非收退交易时可以不传
			String Je = ""+mapPayRecord.get("PAYMONEY");//交易金额：非收退交易时可以不传
			String Body = "挂号退费";//费用描述：如"挂号费"、"预交金"等
			String OldCOMM_SN =  ""+ mapPayRecord.get("VERIFYNO");//原平台流水号：退款用
			String OldCOMM_HIS = "";//原HIS流水号：退款用
			String OldHostSer = "";//原交易参考号12位：MIS-POS退货时用，必须输入原交易参考号，此号码在凭条上，由操作员手工录入
			String res = stub.trans(clientid, aeskey, PayType, TxnType, QRCodeNo, COMM_HIS, Je, Body, OldCOMM_SN, OldCOMM_HIS, OldHostSer);
			System.out.println("返回：" + res);
			if(!res.contains("\"RspCode\":\"00\"")){
				throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求自助机聚合支付平台退款接口.");//直接手动抛出异常
			}
			mapPayRecord.put("PAYSERVICE", "-1");
			mapPayRecord.put("ORGANIZATIONCODE", manageUnit);
			mapPayRecord.put("VERIFYNO", "");
			mapPayRecord.put("PAYTIME",new Date());
			mapPayRecord.put("BANKTYPE", "");
			mapPayRecord.put("BANKCODE", "");
			mapPayRecord.put("BANKNO", "");
			mapPayRecord.put("PAYSOURCE", "1");
			mapPayRecord.put("PAYNO", "");
			mapPayRecord.put("COLLECTFEESCODE", user.getUserId());
			mapPayRecord.put("COLLECTFEESNAME", user.getUserName());
			mapPayRecord.put("CARDTYPE", "");
			mapPayRecord.put("CARDNO", "");
			mapPayRecord.put("STATUS", "1");
			//mapPayRecord.put("SENDXML", Hibernate.createBlob(paramsxml.getBytes("UTF-8")));
			//mapPayRecord.put("RETNRNXML", Hibernate.createBlob("".getBytes("UTF-8")));
			mapPayRecord.put("return_code", "");
			mapPayRecord.put("return_msg", "");
			mapPayRecord.put("TRADENO", "");
			mapPayRecord.put("TKBZ", "1");
			mapPayRecord.put("REFUND_FEE", 0);
			mapPayRecord.put("HOSPNO_ORG", "");
			Map<String, Object> genValue = dao.doSave("create",
					BSPHISEntryNames.PAYRECORD_SELFHELPMACHINE, mapPayRecord, true);
			//long orderid = Long.parseLong(genValue.get("ID") + "");// 获取PAYRECORD的主键
		} catch (Exception e) {
			logger.error("请求自助机聚合支付平台退款接口.",	e);
			throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求自助机聚合支付平台退款接口.");
		}
	}
}
