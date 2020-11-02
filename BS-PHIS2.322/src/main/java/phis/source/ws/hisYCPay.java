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
public class hisYCPay extends AbstractWsService {
	protected BaseDAO dao;

    private static final Log logger = LogFactory
            .getLog(hisYCPay.class);

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
    public String inspectApply(String request) throws JSONException, ValidateException, PersistentDataOperationException {
        //1.记录传参//后续删除日志记录
        logger.info("Received request data[" + request + "].");
        JSONObject requestJson = new JSONObject(request);
        JSONObject returnJson = new JSONObject();
        boolean msyj01=false;
        
//        {"applyId":"123456789","applyOrganizationCode":"320124001","applyDepartmentCode":"961","applyDoctorId":"10210239","patientId":"663835",
//        	"patientName":"胡健","inspectStackId":"8888888","inspectionType":"1","inspectItemIdText":"腿部","inspectPartId":"7777777","jzxh":"2417150"}
  
        //2.验证参数
        if(requestJson.length()>0){
        	 if(isEmpty(requestJson.get("applyId")+"")) {
                 returnJson.put("status","0");
                 returnJson.put("msg","申请单号不能为空！");
                 return returnJson.toString();
             }
            if(isEmpty(requestJson.get("applyOrganizationCode")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","申请机构不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("applyDepartmentCode")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","申请科室不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("applyDoctorId")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","申请医生不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("patientId")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","病人id不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("patientName")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","病人姓名不能为空！");
                return returnJson.toString();
            }
//            else if(!isLegalDate(requestJson.get("applyTime")+"")){
//                returnJson.put("status","0");
//                returnJson.put("msg","申请日期格式不正确，正确格式为yyyy-MM-dd hh24:mi:ss！");
//                return returnJson.toString();
//            }
//            if(isEmpty(requestJson.get("inspectStackId")+"")) {
//                returnJson.put("status","0");
//                returnJson.put("msg","组套id不能为空！");
//                return returnJson.toString();
//            }
            if(isEmpty(requestJson.get("inspectionType")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","所属类别不能为空！");//1:心电  2：放射3：B超
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("inspectItemIdText")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","检查部位不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("inspectPartId")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","检查部位id不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("jzxh")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","就诊序号不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("hisxmids")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","检查项目不能为空！");
                return returnJson.toString();
            }
            if(isEmpty(requestJson.get("items")+"")) {
                returnJson.put("status","0");
                returnJson.put("msg","检查项目不能为空！");
                return returnJson.toString();
            }
          
        }else{
            returnJson.put("status","0");
            returnJson.put("msg","参数格式不正确！"+request);
            return returnJson.toString();
        }
        
		int brid = Integer.parseInt(requestJson.get("patientId")+"");// 病人ID
		String brxm = requestJson.get("patientName")+ "";// 病人姓名
		Date kdrq = new Date();// 开单日期
		int ksdm = Integer.parseInt(requestJson.get("applyDepartmentCode")+ "");// 科室代码
		String ysdm = requestJson.get("applyDoctorId")+"";// 医生代码
		//String clinicId = + "";// 就诊序号		
		String jgid = requestJson.get("applyOrganizationCode")+"";// 机构代码	
		int jzxh = Integer.parseInt(requestJson.get("jzxh")+ "");// 就诊序号
		String hmsid = requestJson.get("inspectStackId")+"";//hms组套id
		long ztbh = 0;//本地组套编号
		String jcbwmc = requestJson.get("inspectItemIdText")+"";//检查部位名称
		String hmsbwid = requestJson.get("inspectPartId")+"";//检查部位id
		int bwid = 0;
		String fyxhs = requestJson.get("hisxmids")+"";//检查项目ID
		String items = requestJson.get("items")+"";//检查项目数量价格集合
		List<Item> datas =JSON.parseArray(items, Item.class);//解析转换成集合
		String applyid = requestJson.get("applyId")+"";//订单编号
		
		//创建数据库连接  
        SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
        Session session = null;
        session =factory.openSession();
        
        /**-------------------------检查基本信息维护是否同步-------------------------**/
     	//通过HMS平台hisxmid是否与his端匹配
    	for(int j=0;j<datas.size();j++){
			Item data = datas.get(j);
			Integer ylid = Integer.parseInt(data.getHisxmid());
			String ztsql = "select fymc from gy_ylsf  where fyxh="+ylid;
			Query query = session.createSQLQuery(ztsql);
			List<?> result = query.list();
			if(result.size()==0){
				session.close();
	            returnJson.put("status","0");
	            returnJson.put("msg","请联系管理员，匹配检查项目编码:"+ylid);
	            return returnJson.toString();
			}			
		}	
		//获取本地检查部位id
//		ztsql = "select bwid from yj_jcsq_jcbw where hmsbwid='"+hmsbwid+"'";
//		query = session.createSQLQuery(ztsql);
//	    result = query.list();//对应组套明细详情
//		if(result.size()>0){
//			 String bw = result.get(0)+"";
//			 bwid = Integer.parseInt(bw);//部位id
//			 query = null;
//			 ztsql = null;
//		}else{
//			session.close();
//            returnJson.put("status","0");
//            returnJson.put("msg","请联系管理员，匹配检查部位编码!");
//            return returnJson.toString();
//		}
    	
    	//查询数据库看是否相同的applyid，有需要更新没有需要插入
    	String applyidsql="select yjxh from ms_yj01 where applyid= '"+applyid+ "'order by yjxh desc";//获取ms_yj01的主键
    	Query query=session.createSQLQuery(applyidsql);
    	List<?> idnum=query.list();
    	int count = idnum.size();//是否有applyid
    	query = null;
    	if(count>0){//有applyid则需要更新
    		String yjkey = idnum.get(0)+"";
    		long yjxh = Long.parseLong(yjkey);//ms_yj01的主键
    		/**----------------------------更新ms_yj01表 -------------------**/
            String sql="";
            sql+="update ms_yj01 set ";
    	    String valuesName="";//属性名
    	    String values="";//值
    	    valuesName+="jgid = "+jgid+","+"brid = "+""+brid+","+"brxm = "+"'"+brxm+"',"+"kdrq = "+"sysdate,"+"ksdm = "+""+ksdm+","+"ysdm = "+"'"+ysdm+"',"+"zxks = "+""+ksdm;

    		//创建更新sql
    	    sql = sql+valuesName+" where applyid ='"+applyid+ "'";
    	    boolean flage = false;
            if(sql.length()>0){
                try{
                    session.createSQLQuery(sql).executeUpdate();
                }
                catch (Exception ex){
                    session.close();
                    returnJson.put("status","0");
                    returnJson.put("msg","修改失败!"+ex.getMessage());
                    return returnJson.toString();
                }
                finally {
                	flage = true;
                	sql = "";
                }
            }
            //更新ms_yj02数据
            if(flage){
            	//删除原来的信息
            	sql = "delete ms_yj02 where yjxh = "+yjkey;
            	session.createSQLQuery(sql).executeUpdate();
            	query = null;
     			sql = null;
    			//通过项目序号查询项目明细
    			sql = "select * from  gy_ylsf a where a.fyxh in ("+fyxhs+")";
    	        query = session.createSQLQuery(sql);
    	        List<?> result = query.list();//项目明细详情
    	        query = null;
    			sql = null;
    	        //遍历解析组套明细
    	        for (int i = 0; i < result.size(); i++) {
    	        	Object[] one = (Object[]) result.get(i);
    	        	//获取ms_yj02的主键
    				String seqsql02="select SEQ_MS_YJ02.nextval  from dual";//获取ms_yj02的主键
    	    		query = session.createSQLQuery(seqsql02);
    	    		List<?> yj02 = query.list();
    	    		String sbxhid = yj02.get(0)+"";
    	    		long sbxh = Long.parseLong(sbxhid);//ms_yj02的主键
    	    		query = null;
    				sql = null;
    				
    				/** 插入ms_yj02表 **/
    				BigDecimal xh= (BigDecimal)one[0];
    				Integer ylxh=Integer.parseInt(xh.toString());//医疗项目序号
    				
    				double yldj = 0.00;
    				int ylsl= 0;
    				for(int j=0;j<datas.size();j++){
    					Item data = datas.get(j);
    					Integer ylid = Integer.parseInt(data.getHisxmid());
    					System.out.println(ylid);
    					if(ylid.equals(ylxh)){
    						yldj=Double.parseDouble(data.getYldj());//单价
    						ylsl=Integer.parseInt(data.getYlsl());//数量
    					}
    				}
    				int yjzx = (i == 0 ? 1 : 0);//医技主项
    				BigDecimal gb= (BigDecimal)one[3];
    				int fygb=Integer.parseInt(gb.toString());//费用归并				
    				
    		       sql ="insert into ms_yj02 ";
    			   valuesName="";//属性名
    			   values="";//值
    			   //识别序号
    			   valuesName+="sbxh,";
    			   values+=""+sbxh+",";
    			   //机构代码
    			   valuesName+="jgid,";
    			   values+=""+jgid+",";
    			   //医疗序号
    			   valuesName+="ylxh,";
    			   values+=""+ylxh+",";
    			   //医技序号
    			   valuesName+="yjxh,";
    			   values+=""+yjxh+",";
    			   //项目类型
    			   valuesName+="xmlx,";
    			   values+=""+0+",";
    			   //单价    
    			   valuesName+="yldj,";
    			   values+=""+yldj+",";
    			   //医技主项 
    			   valuesName+="yjzx,";
    			   values+=""+yjzx+",";
    			   //数量
    			   valuesName+="ylsl,";
    			   values+=""+ylsl+",";
    			   //合计金额
    			   valuesName+="hjje,";
    			   values+=""+ylsl*yldj+",";
    			   // 费用归并
    			   valuesName+="fygb,";
    			   values+=""+fygb+",";
    			   //自负比例
    			   valuesName+="zfbl,";
    			   values+="1,";
    			   //打折比例
    			   valuesName+="dzbl,";
    			   values+="1,";
    			   //医技组号
    			   valuesName+="yjzh,";
    			   values+="0,";
    			   //
    			   valuesName+="cxbz,";
    			   values+="0,";
    			   //检查部位代码
    			   valuesName+="jcbwdm,";
    			   values+=""+bwid+",";
    			   //检查部位名称
    			   valuesName+="jcbwmc";
    			   values+="'"+jcbwmc+"'";
    		   
    				//创建插入sql
    			    sql = sql+"("+valuesName+") values("+values+")";
    			    
    		        if(sql.length()>0){
    		            try{
    		                session.createSQLQuery(sql).executeUpdate();
    		            }
    		            catch (Exception ex){
    		                session.close();
    		                returnJson.put("status","0");
    		                returnJson.put("msg","修改失败!"+ex.getMessage());
    		                return returnJson.toString();
    		            }
    		    
    		        }			    
    	        }
                	session.close();
    	            returnJson.put("status","1");
    	            returnJson.put("msg","修改成功!");
    	        
             }	
    	}else{//没有则需要插入
		/**----------------------------插入ms_yj01表 -------------------**/
		String seqsql="select SEQ_MS_YJ01.nextval  from dual";//获取ms_yj01的主键
		query=session.createSQLQuery(seqsql);
		List<?> seqyjxh=query.list();
		String yjkey = seqyjxh.get(0)+"";
		long yjxh = Long.parseLong(yjkey);//ms_yj01的主键
		
		query=null;
        String sql="";
        sql+="insert into ms_yj01 ";
	    String valuesName="";//属性名
	    String values="";//值
	    if(yjxh>0){
	          valuesName+="yjxh,";
	          values+=""+yjxh+",";
	    }
	    if(jgid.length()>0){
	          valuesName+="jgid,";
	          values+=""+jgid+",";
	    }
	    if(brid>0){
	          valuesName+="brid,";
	          values+=""+brid+",";
	    }
	    if(brxm.length()>0){
	          valuesName+="brxm,";
	          values+="'"+brxm+"',";
	    }
	    if(true){
	          valuesName+="kdrq,";
	          values+="sysdate,";
	    }
	    if(ksdm>0){
	          valuesName+="ksdm,";
	          values+=""+ksdm+",";
	    }
	    if(ysdm.length()>0){
	          valuesName+="ysdm,";
	          values+="'"+ysdm+"',";
	    }
	    if(ksdm>0){
	          valuesName+="zxks,";
	          values+=""+ksdm+",";
	    }
	    if(jzxh>0){
	          valuesName+="jzxh,";
	          values+=""+jzxh+",";
	    }
	    if(true){
	          valuesName+="djly,";
	          values+="9,";
	    }
	    if(true){
	          valuesName+="mzxh,";
	          values+="null,";
	    }
	    if(true){
	          valuesName+="applyid,";
	          values+=""+applyid+",";
	    }
		//远程开单标记
		valuesName+="hmsremark";
		values+="'"+"远程检查开单"+"'";
		//创建插入sql
	    sql = sql+"("+valuesName+") values("+values+")";
	    
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
            	msyj01 = true;
            	sql = "";
            }
        }
        //向ms_yj02插入数据
        if(msyj01){
			//通过项目序号查询项目明细
			sql = "select * from  gy_ylsf a where a.fyxh in ("+fyxhs+")";
	        query = session.createSQLQuery(sql);
	        List<?> result = query.list();//项目明细详情
	        query = null;
			sql = null;
	        //遍历解析组套明细
	        for (int i = 0; i < result.size(); i++) {
	        	Object[] one = (Object[]) result.get(i);
	        	//获取ms_yj02的主键
				String seqsql02="select SEQ_MS_YJ02.nextval  from dual";//获取ms_yj02的主键
	    		query = session.createSQLQuery(seqsql02);
	    		List<?> yj02 = query.list();
	    		String sbxhid = yj02.get(0)+"";
	    		long sbxh = Long.parseLong(sbxhid);//ms_yj02的主键
	    		query = null;
				sql = null;
				
				/** 插入ms_yj02表 **/
				BigDecimal xh= (BigDecimal)one[0];
				Integer ylxh=Integer.parseInt(xh.toString());//医疗项目序号
				
				double yldj = 0.00;
				int ylsl= 0;
				for(int j=0;j<datas.size();j++){
					Item data = datas.get(j);
					Integer ylid = Integer.parseInt(data.getHisxmid());
					System.out.println(ylid);
					if(ylid.equals(ylxh)){
						yldj=Double.parseDouble(data.getYldj());//单价
						ylsl=Integer.parseInt(data.getYlsl());//数量
					}
				}
				int yjzx = (i == 0 ? 1 : 0);//医技主项
				BigDecimal gb= (BigDecimal)one[3];
				int fygb=Integer.parseInt(gb.toString());//费用归并				
				
		       sql ="insert into ms_yj02 ";
			   valuesName="";//属性名
			   values="";//值
			   //识别序号
			   valuesName+="sbxh,";
			   values+=""+sbxh+",";
			   //机构代码
			   valuesName+="jgid,";
			   values+=""+jgid+",";
			   //医疗序号
			   valuesName+="ylxh,";
			   values+=""+ylxh+",";
			   //医技序号
			   valuesName+="yjxh,";
			   values+=""+yjxh+",";
			   //项目类型
			   valuesName+="xmlx,";
			   values+=""+0+",";
			   //单价    
			   valuesName+="yldj,";
			   values+=""+yldj+",";
			   //医技主项 
			   valuesName+="yjzx,";
			   values+=""+yjzx+",";
			   //数量
			   valuesName+="ylsl,";
			   values+=""+ylsl+",";
			   //合计金额
			   valuesName+="hjje,";
			   values+=""+ylsl*yldj+",";
			   // 费用归并
			   valuesName+="fygb,";
			   values+=""+fygb+",";
			   //自负比例
			   valuesName+="zfbl,";
			   values+="1,";
			   //打折比例
			   valuesName+="dzbl,";
			   values+="1,";
			   //医技组号
			   valuesName+="yjzh,";
			   values+="0,";
			   //
			   valuesName+="cxbz,";
			   values+="0,";
			   //检查部位代码
			   valuesName+="jcbwdm,";
			   values+=""+bwid+",";
			   //检查部位名称
			   valuesName+="jcbwmc";
			   values+="'"+jcbwmc+"'";
		   
				//创建插入sql
			    sql = sql+"("+valuesName+") values("+values+")";
			    
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
		    
		        }			    
	        }
            	session.close();
	            returnJson.put("status","1");
	            returnJson.put("msg","创建成功!");
	        
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
