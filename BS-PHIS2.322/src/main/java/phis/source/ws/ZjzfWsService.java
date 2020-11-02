package phis.source.ws;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
/*import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;*/

import phis.source.ws.AbstractWsService;
import phis.application.zjzf.source.ZjzfHelper;
import phis.application.zjzf.source.ZjzfUtil;

//诊间支付webservice服务

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class ZjzfWsService extends AbstractWsService {
	@Override
	@WebMethod
	public String execute(String request) {
			// TODO Auto-generated method stub
		try{
			//1.获取待支付列表
			if(request.contains("<getUnpayedListRequest>") && request.contains("<certificateNo>")){
				try{
					//request = "<getUnpayedListRequest><orgId>320124003</orgId><beginDate>2016-07-10</beginDate><endDate>2016-07-10</endDate><certificates><certificate><certificateType>01</certificateType><certificateNo>342401198310226756</certificateNo><nationality>01</nationality></certificate></certificates><cards><card><cardType>01</cardType><cardNo>A29239292</cardNo><domain>01</domain></card></cards></getUnpayedListRequest>";
					String res = new ZjzfHelper().GetDiagnosisRecordXml(request); 
					if(res.equals("")){
						return "<getUnpayListResponse><resultCode>1</resultCode><resultMessage>未查询到待支付记录！</resultMessage></getUnpayListResponse>";
					}
					return res;
				} catch (Exception e) {
						return "<getUnpayListResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></getUnpayListResponse>";
				}
			}
			//2.获取已支付列表
			if(request.contains("<getPayedListRequest>") && request.contains("<certificateNo>")){
				try{
					//request = "<getUnpayedListRequest><orgId>320124003</orgId><beginDate>2016-07-10</beginDate><endDate>2016-07-10</endDate><certificates><certificate><certificateType>01</certificateType><certificateNo>342401198310226756</certificateNo><nationality>01</nationality></certificate></certificates><cards><card><cardType>01</cardType><cardNo>A29239292</cardNo><domain>01</domain></card></cards></getUnpayedListRequest>";
					String res = new ZjzfHelper().GetPayedListXml(request); 
					if(res.equals("")){
						return "<getPayedListResponse><resultCode>1</resultCode><resultMessage>未查询到已支付记录！</resultMessage></getPayedListResponse>";
					}
					return res;
				} catch (Exception e) {
						return "<getPayedListResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></getPayedListResponse>";
				}
			}
			//3.获取费用预结算金额
			if(request.contains("<getPrecalculatedFeeRequest>") && request.contains("<patientId>")){		
				try{
					//request = "<getUnpayedListRequest><orgId>320124003</orgId><beginDate>2016-07-10</beginDate><endDate>2016-07-10</endDate><certificates><certificate><certificateType>01</certificateType><certificateNo>342401198310226756</certificateNo><nationality>01</nationality></certificate></certificates><cards><card><cardType>01</cardType><cardNo>A29239292</cardNo><domain>01</domain></card></cards></getUnpayedListRequest>";
					String res = new ZjzfHelper().GetPrecalculatedFeeXml(request); 
					if(res.equals("")){
						return "<getPrecalculatedFeeResponse><resultCode>0</resultCode><resultMessage>获取费用预结算金额出错！</resultMessage></getPrecalculatedFeeResponse>";
					}
					return res;
				} catch (Exception e) {
						return "<getPrecalculatedFeeResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></getPrecalculatedFeeResponse>";
				}
			}
			//4.确认费用结算处理
			if(request.contains("<notifyPayedRequest>") && request.contains("<patientId>")){
				try{
					String res = new ZjzfHelper().GetNotifyPayedXml(request); 
					if(res.equals("")){
						return "<notifyPayedResponse><resultCode>0</resultCode><resultMessage>确认费用结算处理失败！</resultMessage></notifyPayedResponse>";
					}
					return res;
				} catch (Exception e) {
						return "<notifyPayedResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></notifyPayedResponse>";
				}
			}
			//5.获取收费单据详细列表
			if(request.contains("<getFeeDetailRequest>") && request.contains("<patientId>")){
				try{
					String res = new ZjzfHelper().GetFeeDetailXml(request); 
					if(res.equals("")){
						return "<getFeeDetailResponse><resultCode>0</resultCode><resultMessage>获取收费单据详细列表失败！</resultMessage></getFeeDetailResponse>";
					}
					//return new ZjzfUtil().charSetConvert(res);
					return res;
				} catch (Exception e) {
						return "<getFeeDetailResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></getFeeDetailResponse>";
				}
			}
			//6.校验关联业务退费条件
			if(request.contains("<checkRefundConditionRequest>") && request.contains("<patientId>")){
				try{
					String res = new ZjzfHelper().CheckRefundConditionXml(request); 
					if(res.equals("")){
						return "<checkRefundConditionResponse><resultCode>0</resultCode><resultMessage>校验关联业务退费条件失败！</resultMessage><resultId></resultId><allowRefundFlag>0</allowRefundFlag></checkRefundConditionResponse>";
					}
					return res;
				} catch (Exception e) {
						return "<checkRefundConditionResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage><resultId></resultId><allowRefundFlag>0</allowRefundFlag></checkRefundConditionResponse>";
				}
			}
			return "调用失败：未找到请求报文对应的方法！";
		} catch (Exception e) {
			return "调用失败："+e.getMessage();
		}
	}
	@WebMethod
	public String getUnpayedList(String request) {
		try{
			//request = "<getUnpayedListRequest><orgId>320124003</orgId><beginDate>2016-07-10</beginDate><endDate>2016-07-10</endDate><certificates><certificate><certificateType>01</certificateType><certificateNo>342401198310226756</certificateNo><nationality>01</nationality></certificate></certificates><cards><card><cardType>01</cardType><cardNo>A29239292</cardNo><domain>01</domain></card></cards></getUnpayedListRequest>";
			String res =new ZjzfHelper().GetDiagnosisRecordXml(request); 
			if(res.equals("")){
				return "<getUnpayListResponse><resultCode>1</resultCode><resultMessage>未查询到待支付记录！</resultMessage></getUnpayListResponse>";
			}
			return res;
		} catch (Exception e) {
				e.printStackTrace();
				return "<getUnpayListResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></getUnpayListResponse>";
		}
	}
	
	@WebMethod
	public String getPayedList(String request) {
		try{
			//request = "<getUnpayedListRequest><orgId>320124003</orgId><beginDate>2016-07-10</beginDate><endDate>2016-07-10</endDate><certificates><certificate><certificateType>01</certificateType><certificateNo>342401198310226756</certificateNo><nationality>01</nationality></certificate></certificates><cards><card><cardType>01</cardType><cardNo>A29239292</cardNo><domain>01</domain></card></cards></getUnpayedListRequest>";
			String res = new ZjzfHelper().GetPayedListXml(request); 
			if(res.equals("")){
				return "<getPayedListResponse><resultCode>1</resultCode><resultMessage>未查询到已支付记录！</resultMessage></getPayedListResponse>";
			}
			return res;
		} catch (Exception e) {
				e.printStackTrace();
				return "<getPayedListResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></getPayedListResponse>";
		}
	}
	
	@WebMethod
	public String getPrecalculatedFee(String request) {		
		try{
			//request = "<getUnpayedListRequest><orgId>320124003</orgId><beginDate>2016-07-10</beginDate><endDate>2016-07-10</endDate><certificates><certificate><certificateType>01</certificateType><certificateNo>342401198310226756</certificateNo><nationality>01</nationality></certificate></certificates><cards><card><cardType>01</cardType><cardNo>A29239292</cardNo><domain>01</domain></card></cards></getUnpayedListRequest>";
			String res = new ZjzfHelper().GetPrecalculatedFeeXml(request); 
			if(res.equals("")){
				return "<getPrecalculatedFeeResponse><resultCode>0</resultCode><resultMessage>获取费用预结算金额出错！</resultMessage></getPrecalculatedFeeResponse>";
			}
			return res;
		} catch (Exception e) {
				e.printStackTrace();
				return "<getPrecalculatedFeeResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></getPrecalculatedFeeResponse>";
		}
	}
	
	@WebMethod
	public String notifyPayed(String request) {
		try{
			String res = new ZjzfHelper().GetNotifyPayedXml(request); 
			if(res.equals("")){
				return "<notifyPayedResponse><resultCode>0</resultCode><resultMessage>确认费用结算处理失败！</resultMessage></notifyPayedResponse>";
			}
			return res;
		} catch (Exception e) {
				return "<notifyPayedResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></notifyPayedResponse>";
		}
	}
	
	@WebMethod
	//5.获取收费单据详细列表
	public String getFeeDetail(String request) {
		try{
			String res = new ZjzfHelper().GetFeeDetailXml(request); 
			if(res.equals("")){
				return "<getFeeDetailResponse><resultCode>0</resultCode><resultMessage>获取收费单据详细列表失败！</resultMessage></getFeeDetailResponse>";
			}
			return res;
		} catch (Exception e) {
				return "<getFeeDetailResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage></getFeeDetailResponse>";
		}
	}	
	
	@WebMethod
	//6.校验关联业务退费条件
	public String checkRefundCondition(String request) {
		try{
			String res = new ZjzfHelper().CheckRefundConditionXml(request); 
			if(res.equals("")){
				return "<checkRefundConditionResponse><resultCode>2</resultCode><resultMessage>校验关联业务退费条件失败！</resultMessage><resultId></resultId><allowRefundFlag>0</allowRefundFlag></checkRefundConditionResponse>";
			}
			return res;
		} catch (Exception e) {
				return "<checkRefundConditionResponse><resultCode>0</resultCode><resultMessage>"+e.getMessage()+"</resultMessage><resultId></resultId><allowRefundFlag>0</allowRefundFlag></checkRefundConditionResponse>";
		}
	}
}
