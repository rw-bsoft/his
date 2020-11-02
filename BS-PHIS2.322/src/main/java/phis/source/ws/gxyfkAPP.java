package phis.source.ws;
import ctd.util.AppContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONException;
import org.json.JSONObject;

import phis.source.security.DESCoder;
import phis.source.security.EncryptException;
import phis.source.security.EncryptUtil;
import phis.source.security.RSA;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description
 *
 * @author <a href="mailto:zhanghao1@bsoft.com.cn">zhanghao</a>
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class gxyfkAPP extends AbstractWsService {

    private static final Log logger = LogFactory
            .getLog(SMZInfoService.class);

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
    public String gxyCreate(String request) throws JSONException {
        //1.记录传参//后续删除日志记录
        logger.info("Received request data[" + request + "].");
        JSONObject requestJson = new JSONObject(request);
        JSONObject returnJson = new JSONObject();
        String wheresql="";
        //2.验证参数
        if(requestJson.length()>0){
            if(isEmpty(requestJson.get("idcard")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","身份证号不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("yqdm")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","仪器代码不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("yqmc")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","仪器名称不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("jg")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","结果不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("orgcode")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","机构不能为空！");
                return returnJson.toString();
            }
        }else{
            returnJson.put("status","0");
            returnJson.put("msg","参数格式不正确！"+request);
            return returnJson.toString();
        }
        //根据身份证号进行查询，已存在数据进行字段update，不存在数据进行insert
        SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
        Session session = null;
        session =factory.openSession();
        	
        String xmdw="";
    	String xmbh ="";
    	String xmmc = "";
        String data = requestJson.get("jg")+"";
        String [] stringArr= data.split(",");
        for(int i = 0;i<stringArr.length;i++){
            String querysql="select lpad(SEQ_zj_record.nextval,8,'0') from dual";
            Query query=session.createSQLQuery(querysql);
            List<?> zj=query.list();
            query=null;
            String sql="";
            //主键
            String id = zj.get(0)+"";
        	//项目单位
            if(i==0){
            	xmdw="mmHg";
            	xmbh ="000004";
            	xmmc = "收缩压";
            }else if(i==1){
            	xmdw="mmHg";
            	xmbh ="000005";
            	xmmc = "舒张压";
            }else{
            	xmdw="bpm";
            	xmbh ="000007";
            	xmmc = "脉搏";
            }
            sql+="insert into jkxw.ZJ_RECORD ";
            String valuesName="";//属性名
            String values="";//值

            valuesName+="id,";
            values+="'"+requestJson.get("orgcode")+id+"',";

            if((requestJson.get("idcard")+"").length()>0){
                valuesName+="idcard,";
                values+="'"+requestJson.get("idcard")+"',";
            }
            valuesName+="zjrq,";
            values+="sysdate,";
            if((requestJson.get("yqdm")+"").length()>0){
                valuesName+="yqdm,";
                values+="'"+requestJson.get("yqdm")+"',";
            }
            if((requestJson.get("yqmc")+"").length()>0){
                valuesName+="yqmc,";
                values+="'"+requestJson.get("yqmc")+"',";
            }
            if((requestJson.get("jg")+"").length()>0){
                valuesName+="jg,";
                values+="'"+stringArr[i]+"',";
            }
            valuesName+="xmdw,";
            values+="'"+xmdw+"',";
            valuesName+="jcrq,";
            values+="sysdate,";
            if((requestJson.get("orgcode")+"").length()>0){
                valuesName+="orgcode,";
                values+="'"+requestJson.get("orgcode")+"',";
            }
            valuesName+="intime,";
            values+="sysdate,";
            valuesName+="xmbh,";
            values+="'"+xmbh+"',";
            valuesName+="xmmc";
            values+="'"+xmmc+"'";
           
            sql=sql+"("+valuesName+") values("+values+")";


	        if(sql.length()>0){
	            try{
	                session.createSQLQuery(sql).executeUpdate();
	            }
	            catch (Exception ex){
	                session.close();
	                returnJson.put("status","0");
	                returnJson.put("msg","创建失败!"+ex.getMessage());
	                return returnJson.toString();
	            }
	            finally {
	                returnJson.put("status","1");
	                returnJson.put("msg","创建成功!");
	                
	            }
	        }
        }
        session.close();
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

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
