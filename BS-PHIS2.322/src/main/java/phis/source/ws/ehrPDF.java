package phis.source.ws;
import ctd.util.AppContextHolder;
import ctd.validator.ValidateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.bean.Item;
import phis.source.security.DESCoder;
import phis.source.security.EncryptException;
import phis.source.security.EncryptUtil;
import phis.source.security.RSA;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 *
 * hj
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class ehrPDF extends AbstractWsService {
	protected BaseDAO dao;

    private static final Log logger = LogFactory
            .getLog(ehrPDF.class);

    @WebMethod
    public String execute(String request) {
        Session session = getSessionFactory().openSession();
        logger.info("Received request data[" + request + "].");
        JSONObject jsonResPKS = new JSONObject();
        try {
			jsonResPKS.put("status","1");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			jsonResPKS.put("msg","测试成功");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResPKS.toString();
    }

    @WebMethod
    public String getehrPDF(String request) throws JSONException, ValidateException, PersistentDataOperationException {
        //1.记录传参//后续删除日志记录
        logger.info("Received request data[" + request + "].");
        JSONObject requestJson = new JSONObject(request);
        JSONObject returnJson = new JSONObject();
        boolean msyj01=false;
        
//        {"idcard":"",organ_code":"","name":"","sys_code":"961","opeCode":"10210239","opeName":"663835",
//        	","name":"8888888","inspectionType":"1","inspectItemIdText":"腿部","inspectPartId":"7777777","jzxh":"2417150"}
 
        //2.验证参数
        if(requestJson.length()>0){
        	 if(isEmpty(requestJson.get("idcard")+"")) {
                 returnJson.put("status","0");
                 returnJson.put("msg","身份证号不能为空！");
                 return returnJson.toString();
             }
            if(isEmpty(requestJson.get("organ_code")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","机构编码不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("name")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","姓名不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("sys_organ_code")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","sys_organ_code不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("sys_code")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","sys_code不能为空！");
                return returnJson.toString();
            }
            //key
    		String key = "17780c4e4e376e40f1e5d7ec4916e44f";
    		//openid
    		String openid = "bsoft0001";
    		//openkey
    		String openkey ="QnNvZnQwMDAxQ1pX";
    		//时间戳
    		String timestamp = String.valueOf(System.currentTimeMillis());
//    		timestamp = new DESUtil(openkey).encryptStr(timestamp);
//    		timestamp = timestamp.replace("+","%2B");
    		//加密sign
    		String sign = new DESUtil(openkey).encryptStr(timestamp+";"+openid);//时间戳与openid以分号拼接，然后使用openKey为Base64加盐，再对结合后的字符串加密。
    		sign = sign.replace("+","%2B");
    		//加密idcard
    		String idcard = new DESedeUtil(key).jiami(requestJson.get("idcard")+"");
    		idcard = idcard.replace("+","%2B");
    		//加密organ_code
    		String organ_code = new DESedeUtil(key).jiami(requestJson.get("organ_code")+"");
    		organ_code = organ_code.replace("+","%2B");
    		//加密name
    		String name = new DESedeUtil(key).jiami(requestJson.get("name")+"");
    		name = name.replace("+","%2B");
    		//sys_organ_code
    		String sys_organ_code = requestJson.get("sys_organ_code")+"";
    		//sys_code
    		String sys_code = requestJson.get("sys_code")+"";
    		
    		String url = "http://10.2.202.56:8080/BHRView/api/v1/getTestRecord.do?"+
    		"openid="+openid+"&timestamp="+timestamp+"&sign="+sign+"&idcard="+idcard+
    		"&organ_code="+organ_code+"&name="+name+"&sys_organ_code="+sys_organ_code+
    		"&sys_code="+sys_code;
    		
    		returnJson.put("status","200");
            returnJson.put("msg",url);
    		
        }
    		return returnJson.toString();
    }

    /**
     * 判断时间格式 格式必须为“YYYY-MM-dd”
     * 2004-2-30 是无效的
     * 2003-2-29 是无效的
     * @param sDate
     * @return
     */
    private static boolean isLegalDate(String sDate) {
        int legalLen = 10;
        if ((sDate == null) || (sDate.length() != legalLen)) {
            return false;
        }

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh24:mi:ss");
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }
    
    
	/**
	 * @param request
	 * @return
	 * @throws EncryptException
	 * @throws UnsupportedEncodingException
	 * @throws RsaException
	 */
	protected String decodeRequest(String request) throws EncryptException,
			UnsupportedEncodingException {
		int index = request.indexOf(".");
		int index2 = request.indexOf("_");
		byte kk[] = EncryptUtil.hexStr2ByteArr(request.substring(index + 1,
				index2));
		RSA rsa = new RSA(EncryptUtil.getPublicKey(),
				EncryptUtil.getPrivateKey());
		byte[] key2 = rsa.decrypt(kk);
		return new String(new DESCoder(key2).decrypt(EncryptUtil
				.hexStr2ByteArr(request.substring(0, index))), "UTF-8");
	}
}
