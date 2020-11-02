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
public class SMZInfoService extends AbstractWsService {

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
    public String SMZInfoCreate(String request) throws JSONException {
        //1.记录传参//后续删除日志记录
        logger.info("Received request data[" + request + "].");
        JSONObject requestJson = new JSONObject(request);
        JSONObject returnJson = new JSONObject();
        String wheresql="";
        //2.验证参数
        if(requestJson.length()>0){
            if(isEmpty(requestJson.get("personname")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","姓名不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("sexcode")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","性别不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("idcard")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","身份证号不能为空！");
                return returnJson.toString();
            }
            //待定
            if(isEmpty(requestJson.get("address")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","联系地址不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("birthday")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","出生日期不能为空！");
                return returnJson.toString();
            }
            else if(!isLegalDate(requestJson.get("birthday")+"")){
                returnJson.put("status","0");
                returnJson.put("msg","出生日期格式不正确，正确格式为yyyy-MM-dd！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("createunit")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","建档机构不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("createuser")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","建档人不能为空！");
                return returnJson.toString();
            }
            if(!isEmpty(requestJson.get("startworkdate")+"")) {
                if(!isLegalDate(requestJson.get("startworkdate")+"")){
                    returnJson.put("status","0");
                    returnJson.put("msg","开始工作日期格式不正确，正确格式为yyyy-MM-dd！");
                    return returnJson.toString();
                }
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

        String querysql="select t.personname ,t.homeplace ,t.address ,t.zipcode ,t.idcard ,t.sexcode , t.birthday ,"+
                "t.phonenumber ,t.mobilenumber , t.email ,t.nationalitycode ,t.nationcode ,t.maritalstatuscode ,t.startworkdate ,t.workcode ,"+
                "t.educationcode ,t.contact ,t.contactphone ,t.ybkh ,t.smkh  from smz_phicinfo t where idcard='"+requestJson.get("idcard").toString()+"'";
        Query query=session.createSQLQuery(querysql);
        List<?> smz=query.list();
        query=null;
        String sql="";
        if(smz.size()>0){
            sql+="update smz_phicinfo set ";
            Object[] one = (Object[]) smz.get(0);
            if(!((requestJson.get("personname")+"").equals(one[0]==null?"":one[0]+""))){
                //姓名不一致更新姓名
                sql+="personname='"+requestJson.get("personname")+"',";
            }
            if(!((requestJson.get("homeplace")+"").equals(one[1]==null?"":one[1]+""))){
                sql+="homeplace='"+requestJson.get("homeplace")+"',";
            }
            if(!((requestJson.get("address")+"").equals(one[2]==null?"":one[2]+""))){
                sql+="address='"+requestJson.get("address")+"',";
            }
            if(!((requestJson.get("zipcode")+"").equals(one[3]==null?"":one[3]+""))){
                sql+="zipcode='"+requestJson.get("zipcode")+"',";
            }
            if(!((requestJson.get("phonenumber")+"").equals(one[7]==null?"":one[7]+""))){
                sql+="phonenumber='"+requestJson.get("phonenumber")+"',";
            }
            if(!((requestJson.get("mobilenumber")+"").equals(one[8]==null?"":one[8]+""))){
                sql+="mobilenumber='"+requestJson.get("mobilenumber")+"',";
            }
            if(!((requestJson.get("email")+"").equals(one[9]==null?"":one[9]+""))){
                sql+="email='"+requestJson.get("email")+"',";
            }
            if(!((requestJson.get("nationalitycode")+"").equals(one[10]==null?"":one[10]+""))){
                sql+="nationalitycode='"+requestJson.get("nationalitycode")+"',";
            }
            if(!((requestJson.get("nationcode")+"").equals(one[11]==null?"":one[11]+""))){
                sql+="nationcode='"+requestJson.get("nationcode")+"',";
            }
            if(!((requestJson.get("maritalstatuscode")+"").equals(one[12]==null?"":one[12]+""))){
                sql+="maritalstatuscode='"+requestJson.get("maritalstatuscode")+"',";
            }
            if(!((requestJson.get("startworkdate")+"").equals(one[13]==null?"":one[13]+""))){
                sql+="startworkdate=to_date('"+requestJson.get("startworkdate")+"','yyyy-mm-dd'),";
            }
            if(!((requestJson.get("workcode")+"").equals(one[14]==null?"":one[14]+""))){
                sql+="workcode='"+requestJson.get("workcode")+"',";
            }
            if(!((requestJson.get("educationcode")+"").equals(one[15]==null?"":one[15]+""))){
                sql+="educationcode='"+requestJson.get("educationcode")+"',";
            }
            if(!((requestJson.get("contact")+"").equals(one[16]==null?"":one[16]+""))){
                sql+="contact='"+requestJson.get("contact")+"',";
            }
            if(!((requestJson.get("contactphone")+"").equals(one[17]==null?"":one[17]+""))){
                sql+="contactphone='"+requestJson.get("contactphone")+"',";
            }
            if(!((requestJson.get("ybkh")+"").equals(one[18]==null?"":one[18]+""))){
                sql+="ybkh='"+requestJson.get("ybkh")+"',";
            }
            if(!((requestJson.get("smkh")+"").equals(one[19]==null?"":one[19]+""))){
                sql+="smkh='"+requestJson.get("smkh")+"',";
            }
            //待调整
            sql+="lastmodifyunit='"+requestJson.get("createunit")+"',";
            sql+="lastmodifyuser='"+requestJson.get("createuser")+"',";
            sql+="lastmodifytime=sysdate,";
            sql+="idcard='"+requestJson.get("idcard")+"'";

            sql+=" where idcard='"+requestJson.get("idcard")+"'";
        }
        else{
            sql+="insert into smz_phicinfo ";
            String valuesName="";//属性名
            String values="";//值
            if((requestJson.get("personname")+"").length()>0){
                valuesName+="personname,";
                values+="'"+requestJson.get("personname")+"',";
            }
            if((requestJson.get("sexcode")+"").length()>0){
                valuesName+="sexcode,";
                values+="'"+requestJson.get("sexcode")+"',";
            }
            if((requestJson.get("idcard")+"").length()>0){
                valuesName+="idcard,";
                values+="'"+requestJson.get("idcard")+"',";
            }
            if((requestJson.get("homeplace")+"").length()>0){
                valuesName+="homeplace,";
                values+="'"+requestJson.get("homeplace")+"',";
            }
            if((requestJson.get("address")+"").length()>0){
                valuesName+="address,";
                values+="'"+requestJson.get("address")+"',";
            }
            if((requestJson.get("zipcode")+"").length()>0){
                valuesName+="zipcode,";
                values+="'"+requestJson.get("zipcode")+"',";
            }
            if((requestJson.get("phonenumber")+"").length()>0){
                valuesName+="phonenumber,";
                values+="'"+requestJson.get("phonenumber")+"',";
            }
            if((requestJson.get("mobilenumber")+"").length()>0){
                valuesName+="mobilenumber,";
                values+="'"+requestJson.get("mobilenumber")+"',";
            }
            if((requestJson.get("email")+"").length()>0){
                valuesName+="email,";
                values+="'"+requestJson.get("email")+"',";
            }
            //待调
            if((requestJson.get("birthday")+"").length()>0){
                valuesName+="birthday,";
                values+="to_date('"+requestJson.get("birthday")+"','yyyy-mm-dd'),";
            }
            if((requestJson.get("nationalitycode")+"").length()>0){
                valuesName+="nationalitycode,";
                values+="'"+requestJson.get("nationalitycode")+"',";
            }
            if((requestJson.get("nationcode")+"").length()>0){
                valuesName+="nationcode,";
                values+="'"+requestJson.get("nationcode")+"',";
            }
            if((requestJson.get("maritalstatuscode")+"").length()>0){
                valuesName+="maritalstatuscode,";
                values+="'"+requestJson.get("maritalstatuscode")+"',";
            }
            if((requestJson.get("startworkdate")+"").length()>0){
                valuesName+="startworkdate,";
                values+="to_date('"+requestJson.get("startworkdate")+"','yyyy-mm-dd'),";
            }
            if((requestJson.get("workcode")+"").length()>0){
                valuesName+="workcode,";
                values+="'"+requestJson.get("workcode")+"',";
            }
            if((requestJson.get("educationcode")+"").length()>0){
                valuesName+="educationcode,";
                values+="'"+requestJson.get("educationcode")+"',";
            }
            if((requestJson.get("contact")+"").length()>0){
                valuesName+="contact,";
                values+="'"+requestJson.get("contact")+"',";
            }
            if((requestJson.get("contactphone")+"").length()>0){
                valuesName+="contactphone,";
                values+="'"+requestJson.get("contactphone")+"',";
            }
            if((requestJson.get("ybkh")+"").length()>0){
                valuesName+="ybkh,";
                values+="'"+requestJson.get("ybkh")+"',";
            }
            if((requestJson.get("smkh")+"").length()>0){
                valuesName+="smkh,";
                values+="'"+requestJson.get("smkh")+"',";
            }

            valuesName+="createunit,";
            values+="'"+requestJson.get("createunit")+"',";
            valuesName+="createuser,";
            values+="'"+requestJson.get("createuser")+"',";
            valuesName+="createtime";
            values+="sysdate";
            sql=sql+"("+valuesName+") values("+values+")";

        }
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
                session.close();
            }
        }

        return returnJson.toString();
    }

    @WebMethod
    public String SMZInfoQuery(String request) throws JSONException, UnsupportedEncodingException, EncryptException {
        //1.记录传参//后续删除日志记录
        logger.info("Received request data[" + request + "].");
//        String requestData = "{\"CardID\":\"320124198905020610\",\"CardType\":\"01\"}";
	
        JSONObject requestJson = new JSONObject(request);
        JSONObject returnJson = new JSONObject();
        String wheresql="";
        //2.验证参数
        if(requestJson.length()>0){
            if(requestJson.has("CardID")&&requestJson.has("CardType")){
                if(isEmpty(requestJson.get("CardID").toString())&&isEmpty(requestJson.get("CardType").toString())) {
                    returnJson.put("status","0");
                    returnJson.put("msg","卡号或者卡类型为空！");
                    return returnJson.toString();
                }

                if("01".equals(requestJson.get("CardType").toString())){
                    wheresql=" where t.idCard='"+requestJson.get("CardID").toString()+"'";
                } else if("02".equals(requestJson.get("CardType").toString())){
                    wheresql=" where t.ybkh='"+requestJson.get("CardID").toString()+"'";
                } else if("03".equals(requestJson.get("CardType").toString())){
                    wheresql=" where t.smkh='"+requestJson.get("CardID").toString()+"'";
                }
                else{
                    returnJson.put("status","0");
                    returnJson.put("msg","卡类型不存在！");
                    return returnJson.toString();
                }
            }
            else{
                returnJson.put("status","0");
                returnJson.put("msg","查询失败");
                return returnJson.toString();
            }
        }
        //3.数据查询
        SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
        Session session = null;
        session =factory.openSession();
        String smzsql="select t.personname ,t.homeplace ,t.address ,t.zipcode ,"+
        "t.idcard ,t.sexcode , t.birthday ,"+
        "t.phonenumber ,t.mobilenumber , t.email ,t.nationalitycode ,"+
        "t.nationcode ,t.maritalstatuscode ,t.startworkdate ,t.workcode ,"+
        "t.educationcode ,t.contact ,t.contactphone ,t.ybkh ,t.smkh  "+
        "from smz_phicinfo t  ";

        SQLQuery query=session.createSQLQuery(smzsql+wheresql);
        List<?> smz=query.list();
        session.close();
        query=null;
        if(smz.size()>0){
            returnJson.put("status","1");
            returnJson.put("msg","查询成功");

            Object[] one = (Object[]) smz.get(0);
            returnJson.put("personname", one[0]==null?"":one[0]+"");
            returnJson.put("homeplace", one[1]==null?"":one[1]+"");
            returnJson.put("address", one[2]==null?"":one[2]+"");
            returnJson.put("zipcode", one[3]==null?"":one[3]+"");
            returnJson.put("idcard", one[4]==null?"":one[4]+"");
            returnJson.put("sexcode", one[5]==null?"":one[5]+"");
            returnJson.put("birthday", one[6]==null?"":one[6]+"");
            returnJson.put("phonenumber",one[7]==null?"":one[7]+"");
            returnJson.put("mobilenumber", one[8]==null?"":one[8]+"");
            returnJson.put("email", one[9]==null?"":one[9]+"");
            returnJson.put("nationalitycode", one[10]==null?"":one[10]+"");
            returnJson.put("nationcode", one[11]==null?"":one[11]+"");
            returnJson.put("maritalstatuscode", one[12]==null?"":one[12]+"");
            returnJson.put("startworkdate", one[13]==null?"":one[13]+"");
            returnJson.put("workcode", one[14]==null?"":one[14]+"");
            returnJson.put("educationcode",one[15]==null?"":one[15]+"");
            returnJson.put("contact", one[16]==null?"":one[16]+"");
            returnJson.put("contactphone",one[17]==null?"":one[17]+"");
            returnJson.put("ybkh", one[18]==null?"":one[18]+"");
            returnJson.put("smkh",one[19]==null?"":one[19]+"");
        }
        else{
            returnJson.put("status","0");
            returnJson.put("msg","查询失败，不存在患者信息");
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
