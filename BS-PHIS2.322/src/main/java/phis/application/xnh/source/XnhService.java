package phis.application.xnh.source;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Hcnservices.Exception_Exception;
import Hcnservices.HCNWebservices;
import Hcnservices.HCNWebservicesService;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.application.pix.source.EmpiModel;
import phis.application.pix.source.EmpiUtil;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
//新农合服务类
public class XnhService extends AbstractActionService implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(XnhService.class);
	//下载药品目录
	protected void doSavedownloadypml(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		XnhModel xm=new XnhModel(dao);
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		try {
			xm.Savedownloadypml(body, res);
		} catch (ModelDataOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	//下载诊疗目录
	protected void doSavedownloadzlml(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		XnhModel xm=new XnhModel(dao);
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		try {
			xm.savedownloadzlml(body, res);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//下载疾病目录
	protected void doSavedownloadjbml(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		XnhModel xm=new XnhModel(dao);
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String ipandport=body.get("ipandport").toString();
		
		if(ipandport.equals("") ||ipandport.length() <=0){
			throw new ServiceException("未找到该医院的农合信息配置！");
		}
		String args[]=ipandport.split(":");
		String ip=args[1];
		String port=args[2];
		
		try {
			Integer row1=0;
			Integer row2=0;
				row2=row1+2000;
				String temprow=xm.getmaxid("CODE", "NH_BSOFT_JBBM");
				if(temprow.equals("") || temprow.length() <=0){}
				else{
					row1=Integer.parseInt(temprow);
					row2=row1+2000;
				}
				URL url = this.getwebserviceurl(jgid);
				HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
				HCNWebservices hcn=HCNWeb.getHCNWebservicesPort(); 
				String rm=hcn.dogetJbml(body.get("operator").toString(), row1,row2,ip, port);
				try {
					JSONObject jorm=new JSONObject(rm);
					if(jorm.optString("code").equals("1")){
						JSONArray arr=jorm.optJSONArray("arr");
						for(int i=0;i<arr.length();i++){
							JSONObject one =arr.getJSONObject(i);
							Map<String,Object> record=new HashMap<String,Object>();
							record.put("CODE", one.optString("CODE").length() <1?"":one.optLong("CODE"));
							record.put("NAME", one.optString("NAME").length() <1?"":one.optString("NAME"));
							record.put("PYDM", one.optString("PYDM").length() <1?"":one.optString("PYDM"));
							record.put("ICD10", one.optString("ICD10").length() <1?"":one.optString("ICD10"));
							record.put("WBDM", one.optString("WBDM").length() <1?"":one.optString("WBDM"));
							List<Map<String,Object>> zlxx=xm.getjbxxbycode(one.optLong("CODE"));
							if(zlxx!=null && zlxx.size() >0){}
							else {
								try {
									dao.doSave("create", NH_BSOFT_JBBM, record, false);
								} catch (PersistentDataOperationException e) {
									e.printStackTrace();
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
		
	}
	//下载单病种目录
	protected void doSavedownloaddbzml(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		XnhModel xm=new XnhModel(dao);
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String ipandport=body.get("ipandport").toString();
		
		if(ipandport.equals("") ||ipandport.length() <=0){
			throw new ServiceException("未找到该医院的农合信息配置！");
		}
		String args[]=ipandport.split(":");
		String yybh=args[0];
		String ip=args[1];
		String port=args[2];
		
		try {
			URL url = this.getwebserviceurl(jgid);
			HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
			HCNWebservices hcn=HCNWeb.getHCNWebservicesPort(); 
			String rm=hcn.dogetDbzml(body.get("operator").toString(),yybh,ip, port);
				try {
					JSONObject jorm=new JSONObject(rm);
					if(jorm.optString("code").equals("1")){
						JSONArray arr=jorm.optJSONArray("arr");
						for(int i=0;i<arr.length();i++){
							JSONObject one =arr.getJSONObject(i);
							Map<String,Object> record=new HashMap<String,Object>();
							record.put("CODE", one.optString("CODE").length() <1?"":one.optLong("CODE"));
							record.put("NAME", one.optString("NAME").length() <1?"":one.optString("NAME"));
							record.put("PYDM", one.optString("PYDM").length() <1?"":one.optString("PYDM"));
							record.put("ICD10", one.optString("ICD10").length() <1?"":one.optString("ICD10"));
							record.put("WBDM", one.optString("WBDM").length() <1?"":one.optString("WBDM"));
							record.put("BCJEFD", one.optString("BCJEFD").length() <1?"":one.optLong("BCJEFD"));
							record.put("JGLB", one.optString("JGLB").length() <1?"":one.optString("JGLB"));
							record.put("MZFS", one.optString("MZFS").length() <1?"":one.optLong("MZFS"));
							record.put("JGID", body.get("deptId").toString());
							
							List<Map<String,Object>> zlxx=xm.getdbzxxbycode(one.optLong("CODE"));
							if(zlxx!=null && zlxx.size() >0){
								
							}else {
								try {
									dao.doSave("create", NH_BSOFT_JBBM_DRGS, record, false);
								} catch (PersistentDataOperationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}else if(jorm.optString("code").equals("-1")) {
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		} catch (Exception_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//农合项目对照
	protected void doUpdatenhbm(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String type=body.get("type").toString();
		Map<String,Object> p=new HashMap<String,Object>();
		
		if(type.equals("1")){
			String upypxxsql="update YK_TYPK set NHBM_SH='9',NHBM_BSOFT=:NH_YPXH where YPXH=:YY_YPXH";
			p.put("NH_YPXH", body.get("NH_YPXH").toString());
			p.put("YY_YPXH",Long.parseLong(body.get("YY_YPXH").toString()));
			try {
				dao.doUpdate(upypxxsql, p);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(type.equals("2")){
			String upypxxsql="update GY_YLSF set NHBM_SH='9',NHBM_BSOFT=:NH_FYXH where FYXH=:YY_FYXH";
			p.put("NH_FYXH", body.get("NH_FYXH").toString());
			p.put("YY_FYXH",Long.parseLong(body.get("YY_FYXH").toString()));
			try {
				dao.doUpdate(upypxxsql, p);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//自动农合项目对照
	protected void doUpdatenhbmself(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String type=body.get("type").toString();
		XnhModel xm=new XnhModel(dao);
		
		if(type.equals("1")){
			List<Map<String,Object>> ypxx=xm.loadnonhbmlist(type);
			for (int i=0;i<ypxx.size();i++){
				Map<String,Object> one = (HashMap<String, Object>)ypxx.get(i);
				List<Map<String,Object>> tempnhypxx=xm.loadnhypxxlistbyypmc(one.get("YPMC").toString());
				 if(tempnhypxx.size() >0){
					 String upypxxsql="update YK_TYPK set NHBM_SH='9',NHBM_BSOFT=:NH_YPXH where YPXH=:YY_YPXH";
					 Map<String,Object> p=new HashMap<String,Object>();
					 p.put("NH_YPXH", tempnhypxx.get(0).get("YPXH").toString());
					 p.put("YY_YPXH", Long.parseLong(one.get("YPXH").toString()));
					 try {
						dao.doUpdate(upypxxsql, p);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
			}
		}else if(type.equals("2")){
			List<Map<String,Object>> zlxx=xm.loadnonhbmlist(type);
			for (int i=0;i<zlxx.size();i++){
				Map<String,Object> one = (HashMap<String, Object>)zlxx.get(i);
				List<Map<String,Object>> tempnhypxx=xm.loadnhzlxxlistbyypmc(one.get("FYMC").toString());
				 if(tempnhypxx.size() >0){
					 String upypxxsql="update GY_YLSF set NHBM_SH='9',NHBM_BSOFT=:NH_FYXH where FYXH=:YY_FYXH";
					 Map<String,Object> p=new HashMap<String,Object>();
					 p.put("NH_FYXH", tempnhypxx.get(0).get("FYXH").toString());
					 p.put("YY_FYXH", Long.parseLong(one.get("FYXH").toString()));
					 try {
						dao.doUpdate(upypxxsql, p);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
			}
		}
	}
	//取消农合项目对照
	protected void doUpdatenonhbm(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String type=body.get("type").toString();
		Map<String,Object> p=new HashMap<String,Object>();
		
		if(type.equals("1")){
			String upypxxsql="update YK_TYPK set NHBM_BSOFT='',NHBM_SH='0' where YPXH=:YPXH and NHBM_SH <>'1'";
			p.put("YPXH", Long.parseLong(body.get("YPXH").toString()));
			try {
				dao.doUpdate(upypxxsql, p);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(type.equals("2")){
			String upypxxsql="update GY_YLSF set NHBM_BSOFT='',NHBM_SH='0' where FYXH=:YPXH and NHBM_SH <>'1'";
			p.put("YPXH", Long.parseLong(body.get("YPXH").toString()));
			try {
				dao.doUpdate(upypxxsql, p);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//上传对照信息
	protected void doSenddzxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		String type=body.get("type").toString();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		if(type.equals("1")){
			XnhModel xm=new XnhModel(dao);
			if(body.containsKey("dgdzxx")){
				try {
					xm.sendypdzxx((Map<String,Object>)body.get("dgdzxx"),type);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
			
			JSONArray dzxx=xm.loaddzypxxArray(body.get("deptId").toString());
			if(dzxx.length() >0){
				String ipandport=body.get("ipandport").toString();
				if(ipandport.equals("") ||ipandport.length() <=0){
					throw new ServiceException("未找到该医院的农合信息配置！");
				}
				String args[]=ipandport.split(":");
				String yybh=args[0];
				String ip=args[1];
				String port=args[2];
				Boolean flag=true;
				URL url = this.getwebserviceurl(jgid);
				HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
				HCNWebservices hcn=HCNWeb.getHCNWebservicesPort(); 
				int i=0,l=0;
				Integer len=dzxx.length();
				while (flag){
					if(len <67){
						l=len;
					}else{
						l=l+66;
					}
					JSONArray temp=new JSONArray();
					for (int j=i;j<l && j<len;j++){
						temp.put(dzxx.optJSONObject(j));
						i=j;
					}
					try {
						String rm=hcn.douploadDzml(body.get("operator").toString(), yybh, type, temp.toString(), ip, port);
						try {
							JSONObject jorm=new JSONObject(rm);
							if(jorm.optString("code").equals("-1")){
								flag=false;
								throw new ServiceException(jorm.optString("msg"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (Exception_Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(l>=len){
						flag=false;
					}
				}
				
			}
		}
		}else if(type.equals("2")){
			XnhModel xm=new XnhModel(dao);
			JSONArray dzxx=xm.loaddzzlxxArray(body.get("deptId").toString());
			if(dzxx.length() >0){
				String ipandport=body.get("ipandport").toString();
				if(ipandport.equals("") ||ipandport.length() <=0){
					throw new ServiceException("未找到该医院的农合信息配置！");
				}
				String args[]=ipandport.split(":");
				String yybh=args[0];
				String ip=args[1];
				String port=args[2];
				Boolean flag=true;
				URL url = this.getwebserviceurl(jgid);
				HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
				HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
				int i=0,l=0;
				Integer len=dzxx.length();
				while (flag){
					if(len <67){
						l=len;
					}else{
						l=l+66;
					}
					JSONArray temp=new JSONArray();
					for (int j=i;j<l && j<len;j++){
						temp.put(dzxx.optJSONObject(j));
						i=j;
					}
					try {
						String rm=hcn.douploadDzml(body.get("operator").toString(), yybh, type, temp.toString(), ip, port);
						try {
							JSONObject jorm=new JSONObject(rm);
							if(jorm.optString("code").equals("-1")){
								flag=false;
								throw new ServiceException(jorm.optString("msg"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (Exception_Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(l>=len){
						flag=false;
					}
				}
			}
		}
	}
	
	//下载对照信息
		protected void doLoaddzxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String,Object>)req.get("body");
			String type=body.get("type").toString();
			UserRoleToken user = UserRoleToken.getCurrent();
			String uid=user.getUserId();
			String jgid = user.getManageUnitId();
			URL url = this.getwebserviceurl(jgid);
			HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
			HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
			XnhModel xm=new XnhModel(dao);
			JSONObject request=new JSONObject();
			try {
				request = xm.getjsonqzjpzxx(jgid);
				request.put("operator", uid);
				request.put("jgid", jgid);
				request.put("type", type);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(type.equals("1")){
				if(body.containsKey("dgypxz")){
					xm.getDzmlByItem(hcn,request,(Map<String,Object>)body.get("dgypxz"),res,type);
				}else{
					List<Map<String,Object>>  dzxx=xm.loaddzypxxList(jgid);
					if(dzxx.size() >0){
						for(int i=0;i<dzxx.size();i++){
							Map<String,Object> temp=dzxx.get(i);
							xm.getDzmlByItem(hcn, request, temp, res, type);
						}
					}
				}
			}else if (type.equals("2")){
				if(body.containsKey("dgzlxz")){
					xm.getDzmlByItem(hcn,request,(Map<String,Object>)body.get("dgzlxz"),res,type);
				}else{
				List<Map<String,Object>>  dzxx=xm.loaddzzlxxList(jgid);
				if(dzxx.size() >0){
					for(int i=0;i<dzxx.size();i++){
						Map<String,Object> temp=dzxx.get(i);
						xm.getDzmlByItem(hcn, request, temp, res, type);
					}
				}
			}
			}
		}
		//农合读卡
		protected void doCheckgrzh(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
				Map<String, Object> body=(Map<String,Object>)req.get("body");
				String ipandport=body.get("ipandport").toString();
				String args[]=ipandport.split(":");
				String yybh=args[0];
				String ip=args[1];
				String port=args[2];
				Calendar d = Calendar.getInstance();
				d.setTime(new Date());
				UserRoleToken user = UserRoleToken.getCurrent();
				String jgid = user.getManageUnitId();
				String Year=d.get(Calendar.YEAR)+"";
				
				URL url = this.getwebserviceurl(jgid);
				HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
				HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
				/****************add by LIZHI 2018-05-08 去除UTF-8以外的特殊字符****************/
				String idCardStr = body.get("cardid")+"";
				if (null != idCardStr){
			        byte[] byteArr = idCardStr.getBytes();
			        for ( int i=0; i < byteArr.length; i++){
			            byte ch= byteArr[i];
			            // remove any characters outside the valid UTF-8 range as well as all control characters
			            if (!(ch < 0x00FD && ch > 0x001F) || ch =='&' || ch=='#') {
			                byteArr[i]=' ';
			            }
			        }
			        idCardStr = new String( byteArr );
				}
				/****************add by LIZHI 2018-05-08 去除特UTF-8以外的殊字符****************/
				try {
					String rm=hcn.docheckGRZH(body.get("operator").toString(), yybh, idCardStr, Year, ip, port);
					try {
						JSONObject jorm = new JSONObject(rm);
						if(jorm.optString("code").equals("1")){
							JSONArray patients = jorm.optJSONArray("patients");
							List<Map<String,Object>> record=new ArrayList<Map<String,Object>>();
							for(int i=0;i<patients.length();i++){
								JSONObject one=patients.optJSONObject(i);
								Map<String,Object> temp=new HashMap<String, Object>();
								temp.put("GRBH", one.optString("grbh"));
								if(one.optString("nhkh")!=null && !"".equals(one.optString("nhkh"))){
									temp.put("NHKH", one.optString("nhkh"));
								}else{
									temp.put("NHKH", one.optString("ylzh"));
								}
								temp.put("BRXM", one.optString("xm"));
								temp.put("BRXB", one.optString("xb"));
								temp.put("BRXB_text", one.optString("xb_text"));
								temp.put("CSNY", one.optString("csrq"));
								temp.put("SFZH", one.optString("zjhm"));
								temp.put("LXDH", one.optString("lxdh"));
								temp.put("HKDZ", one.optString("address"));
								record.add(temp);		
							}
							res.put("body", record);
							res.put("RET", rm);
						}else {
							throw new ServiceException("新农合返回信息:"+jorm.optString("msg"));
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (Exception_Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	//保存农合信息	
		protected synchronized void doSavegrzhxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String,Object>)req.get("body");
			Map<String, Object> reqBody = new HashMap<String, Object>();
			reqBody.put("idCard", body.get("idCard").toString());
			body.put("bloodTypeCode", "5");
			body.put("maritalStatusCode", "5");
			body.put("BRXZ", "6000");
			Map<String, Object> query=EmpiUtil.queryByIdCardAndName(dao, ctx, reqBody);
			List<Map<String, Object>> list=(List<Map<String, Object>>)query.get("body");
			Map<String, Object> result =new HashMap<String, Object>();
			result.put("NHKH", body.get("NHKH").toString());
			result.put("nhdk", "1");
			result.put("BRXZ", 6000);
			EmpiModel em=new EmpiModel(dao);
			if(list==null){
				List<Map<String, Object>> cards= new ArrayList<Map<String,Object>>();
				//封装农合卡
				Map<String, Object> card1=new HashMap<String, Object>();
				card1.put("cardNo", body.get("NHKH").toString());
				card1.put("cardTypeCode", "03");
				//封装就诊卡
				Map<String, Object> card2=new HashMap<String, Object>();
				String mzhm="";
				try {
					mzhm=em.doOutPatientNumber(ctx);
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
				if(mzhm==null || mzhm.length() <=0){
					res.put("code", "502");
					res.put("msg", "请先维护门诊号码段");
					return;
				}
				card2.put("cardNo", mzhm);
				body.put("MZHM", mzhm);
				card2.put("cardTypeCode", "04");
				
				cards.add(card1);
				cards.add(card2);
				body.put("cards", cards);
				
				Map<String, Object> records = new HashMap<String, Object>();
				records.putAll(body);
				records = EmpiUtil.changeToPIXFormat(records);
				records.put("photo","");
				//保存人员信息
				result=EmpiUtil.submitPerson(dao, ctx, body,records);
				
			}else if(list.size() >1){
				throw new ServiceException("同一身份证号在档案表有两份档案！请联系管理员处理！");
			}else{
				Map<String, Object> brxx=list.get(0);
				String empiid=brxx.get("empiId").toString();
				String upsql="update MPI_DemographicInfo set personName='"+body.get("personName")+"' where empiId=:empiId";
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("empiId", empiid);
				try {
					dao.doUpdate(upsql, pa);
				} catch (PersistentDataOperationException e2) {
					e2.printStackTrace();
				}
				result.put("empiid", empiid);
				//yx-2017-05-07-开始判断保存农合卡号
				String cardsql="select cardNo as cardNo from MPI_Card where empiId=:empiId and cardTypeCode=:cardTypeCode";
				pa.put("cardTypeCode", "03");
				try {
					Map<String, Object> cardno=dao.doLoad(cardsql, pa);
					if(cardno==null  || cardno.size() <=0){
						Map<String, Object> cardxx=new HashMap<String, Object>();
						cardxx.put("empiId", empiid);
						cardxx.put("cardTypeCode", "03");
						cardxx.put("cardNo", body.get("NHKH").toString());
						try {
							em.saveCard(cardxx);
						} catch (ModelDataOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {//如果卡号不对应去更新卡号
						if(!(cardno.get("cardNo")+"").equals(body.get("NHKH").toString())){
							String upcardsql="update MPI_Card set cardNo=:cardNo where empiId=:empiId " +
									"and cardTypeCode=:cardTypeCode";
							pa.put("cardNo", body.get("NHKH").toString());
							dao.doSqlUpdate(upcardsql, pa);
							pa.remove("cardTypeCode");
							String upbrdasql="update MS_BRDA set NHKH=:cardNo where empiId=:empiId ";
							dao.doSqlUpdate(upbrdasql, pa);
						}
					}
				} catch (PersistentDataOperationException e1) {
					e1.printStackTrace();
				}
				//yx-2017-05-07-结束判断保存农合卡号
				//yx-2017-05-10-保存门诊号到卡号表-开始
				List<?> cnd = CNDHelper.createSimpleCnd("eq", "EMPIID", "s", empiid);
				Map<String, Object> brda=new HashMap<String, Object>();
				try {
					brda=dao.doLoad(cnd, MS_BRDA);
				} catch (PersistentDataOperationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(brda==null || brda.size()<=0){
					String tempmzhm="";
					//判断是否存在门诊号
					String sql="select cardNo as cardNo from MPI_Card where empiId=:empiId " +
							" and cardTypeCode=:cardTypeCode";
					Map<String, Object> p=new HashMap<String, Object>();
					p.put("empiId", empiid);
					p.put("cardTypeCode", "04");
					boolean cardinsertflag=true;
					try {
						Map<String, Object> cardxx=dao.doSqlLoad(sql, p);
						if(cardxx!=null && cardxx.size() >0){
							tempmzhm=cardxx.get("CARDNO")+"";
							cardinsertflag=false;
						}
					} catch (PersistentDataOperationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(tempmzhm.equals("") || tempmzhm.length() <=1){
						try {
							tempmzhm = em.doOutPatientNumber(ctx);
						} catch (ModelDataOperationException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(tempmzhm==null || tempmzhm.length() <=0){
						res.put("code", "502");
						res.put("msg", "请先维护门诊号码段");
						return;
					}
					brxx.put("MZHM", tempmzhm);
					brxx.put("BRXZ", 6000);
					brxx.put("NHKH", body.get("NHKH"));
					brxx.put("GRBH", body.get("GRBH"));
					brxx.put("HKDZ", body.get("address"));
					brxx.put("BRXM", body.get("personName"));
					brxx.put("sexCode", body.get("sexCode"));
					if(cardinsertflag){
					Map<String, Object> card=new HashMap<String, Object>();
					card.put("cardNo", tempmzhm);
					card.put("cardTypeCode", "04");
					card.put("empiId", empiid);
					
					try {
						em.saveCard(card);
					} catch (ModelDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					try {
						//保存病人档案
						em.saveBRDA(brxx, ctx);
					} catch (ModelDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("MZHM", tempmzhm);
				}else{
					if(brda.get("NHKH")==null || brda.get("NHKH").toString().length() <=0){
						brxx.put("NHKH", body.get("NHKH"));
					}
					if(brda.get("GRBH")==null || brda.get("GRBH").toString().length() <=0){
						brxx.put("GRBH", body.get("GRBH"));
					}
					brxx.put("BRXZ", "6000");
					brxx.put("HKDZ", body.get("address"));
					brxx.put("BRXM", body.get("personName"));
					brxx.put("sexCode", body.get("sexCode"));
					try {
						em.saveBRDA(brxx, ctx);
					} catch (ModelDataOperationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					result.put("MZHM", brda.get("MZHM")+"");
					String sql="select cardNo as cardNo from MPI_Card where empiId=:empiId " +
							" and cardTypeCode=:cardTypeCode and cardNo=:cardNo";	
					Map<String, Object> p=new HashMap<String, Object>();
					p.put("empiId", empiid);
					p.put("cardTypeCode", "04");
					p.put("cardNo", brda.get("MZHM")+"");
					try {
						Map<String, Object> mzcard=dao.doLoad(sql, p);
						if(mzcard==null || mzcard.size() <=0){
							Map<String, Object> card=new HashMap<String, Object>();
							card.put("cardNo", brda.get("MZHM")+"");
							card.put("cardTypeCode", "04");
							card.put("empiId", empiid);
							try {
								em.saveCard(card);
							} catch (ModelDataOperationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//yx-2017-05-10-保存门诊号到卡号表-结束
			}
			res.put("body", result);
		}
		protected void doGetghxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String,Object>)req.get("body");
			String cardid=body.get("cardid").toString();
			Map<String, Object> result= new HashMap<String, Object>();
			Map<String, Object> data= new HashMap<String, Object>();
			
			String deptId=body.get("deptId").toString();
			int adt_begin = 1;
			String adt_beginStr = ParameterUtil.getParameter(deptId,
					BSPHISSystemArgument.GHXQ, ctx);
			if (adt_beginStr != "") {
				adt_begin = Integer.parseInt(adt_beginStr);
			}
			SimpleDateFormat matter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
			String XQJSFS = ParameterUtil.getParameter(deptId,
						BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
				//挂号效期
				Date adt_begin_data = DateUtils.addDays(new Date(), -adt_begin);
				if ("1".equals(XQJSFS)) {
					try {
						adt_begin_data = DateUtils.addDays(
								matter.parse(BSHISUtil.getDate() + " 23:59:59"), -1);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			String ghxxsql="select b.MZHM as MZHM,b.BRXZ as BRXZ,b.EMPIID as EMPIID, " +
					"b.BRXM as BRXM ,a.KSDM as KSDM ,a.JZZT as JZZT from MS_GHMX a join MS_BRDA b on a.BRID=b.BRID " +
					"where b.NHKH=:cardid and a.GHSJ >=:GHSJ and  a.JGID=:JGID " ;
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("cardid", cardid);
			p.put("GHSJ", adt_begin_data);
			p.put("JGID", deptId);
			if(body.containsKey("GHGL")){
				ghxxsql=ghxxsql+" and a.SBXH=:GHGL ";
				p.put("GHGL", Long.parseLong(body.get("GHGL")+""));
			}
			
			try {
				List<Map<String, Object>> ghxxlist=dao.doSqlQuery(ghxxsql, p);
				if(ghxxlist!=null && ghxxlist.size()>0 ){
					data.put("MZHM", ghxxlist.get(0).get("MZHM").toString());
					data.put("BRXZ", ghxxlist.get(0).get("BRXZ").toString());
					data.put("EMPIID", ghxxlist.get(0).get("EMPIID").toString());
					data.put("BRXM", ghxxlist.get(0).get("BRXM").toString());
					data.put("KSDM", ghxxlist.get(0).get("KSDM").toString());
					data.put("JZZT", ghxxlist.get(0).get("JZZT").toString());
				}else{
					res.put("code", "503");
					res.put("msg", "未找到该病人有效挂号时间段内的挂号信息！");
					return;
				}
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.put("data",data);
			res.put("body", result);
		}
		//门诊挂号和预结算
		protected void doMzdjandyjs(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> mzxx=(Map<String,Object>)req.get("MZXX");
			UserRoleToken user = UserRoleToken.getCurrent();
			String uid=user.getUserId();
			String jgid = user.getManageUnitId();
			List<Map<String, Object>> body=(List<Map<String, Object>>)req.get("body");
			XnhModel xm=new XnhModel(dao);
			String MZHM=mzxx.get("MZHM").toString();
			String sql="select GRBH as GRBH,NHKH as NHKH from MS_BRDA a where a.MZHM=:MZHM ";
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("MZHM", MZHM);
			String GRBH="";
			String NHKH="";
			String djid="";
			String GHGL=mzxx.get("GHGL").toString();
			
			URL url = this.getwebserviceurl(jgid);
			HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
			HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
			
			try {
				Map<String, Object> xx =dao.doLoad(sql, p);
				GRBH=xx.get("GRBH")==null?"":xx.get("GRBH").toString();
				NHKH=xx.get("NHKH")==null?"":xx.get("NHKH").toString();
				if(GRBH.equals("") ||NHKH.equals("") ){
					throw new ServiceException("该病人档案表中的卡号信息不完善，请联系管理员");
				}
				Calendar d = Calendar.getInstance();
				d.setTime(new Date());
				String Year=d.get(Calendar.YEAR)+"";
				String ryrq=(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(d.getTime());
				JSONObject josnqzj=new JSONObject();
				try {
					josnqzj = xm.getjsonqzjpzxx(jgid);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JSONObject josnsjqzj=new JSONObject();
				try {
					josnsjqzj = xm.getjsonqzjpzxx(jgid.substring(0,9));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				JSONObject request=new JSONObject();
//				String mzdjre="";//门诊登记返回信息
				//获取挂号信息中的农合流水号
//				List<?> cnd=CNDHelper.createSimpleCnd("eq", "SBXH", "s", GHGL);
//				Map<String, Object> lsdj=dao.doLoad(cnd, MS_GHMX);
//				
//				if(lsdj!=null && lsdj.size() >0){
//					djid=lsdj.get("NHDJID")==null?"":lsdj.get("NHDJID").toString();
//				}
//				if(djid!=null && djid.length()>0){
//					//已预结算过流程
//					//已门诊登记过，先取消费用
//					JSONObject js_fyxx=new JSONObject();
//					try {
//						js_fyxx=josnsjqzj;
//						js_fyxx.put("djid", djid);
//						js_fyxx.put("jzlx", "1");
//						js_fyxx.put("operator",uid);
//						js_fyxx.put("czlx","2");//操作类型 1 上传费用，2 取消费用
//						js_fyxx.put("serviceId", "UploadFyxx");
//						try {
//							String re_qxscfyxx=hcn.doUploadFyxx(js_fyxx.toString());
//							if(re_qxscfyxx!=null && re_qxscfyxx.length() >0){
//								JSONObject qxscfyxx=new JSONObject(re_qxscfyxx);
//								if(qxscfyxx.optString("code").equals("1")){
//									//上传费用信息
//									js_fyxx.put("czlx", "1");
//									//封装处方和收费项目
//									
//									JSONObject jnsfxx=xm.bulidjsonsfxx(body,GHGL);
//									js_fyxx.put("ypArr", jnsfxx.get("yparr"));
//									js_fyxx.put("zlArr", jnsfxx.get("zlarr"));
//									String re_scfyxx=hcn.doUploadFyxx(js_fyxx.toString());
//									if(re_scfyxx!=null && re_scfyxx.length() >0){
//										JSONObject scfyxx=new JSONObject(re_scfyxx);
//										if(scfyxx.optString("code").equals("1")){
//											//进行补偿预结算
//											JSONObject fz_yjsxx=new JSONObject();
//											fz_yjsxx=josnqzj;
//											fz_yjsxx.put("operator",uid);
//											fz_yjsxx.put("djid", djid);
//											fz_yjsxx.put("cardid", NHKH);
//											fz_yjsxx.put("grbh", GRBH);
//											fz_yjsxx.put("fpje",jnsfxx.get("zje"));
//											fz_yjsxx.put("Year", Year);
//											fz_yjsxx.put("jzlx", "1");//1门诊
//											fz_yjsxx.put("serviceId", "FybxYjs");
//											String re_yjs=hcn.doFybxYjs(fz_yjsxx.toString());
//											if(re_yjs!=null && re_yjs.length() >0){
//												JSONObject js_yjs=new JSONObject(re_yjs);
//													if(js_yjs.optString("code").equals("1")){
//														Map<String, Object> m_yjs=new HashMap<String, Object>();
//														m_yjs.put("SUM01", js_yjs.optDouble("sum01"));//总补偿金额
//														m_yjs.put("SUM04", js_yjs.optDouble("sum04"));
//														m_yjs.put("SUM06", js_yjs.optDouble("sum06"));
//														m_yjs.put("SUM07", js_yjs.optDouble("sum07"));
//														m_yjs.put("SUM08", js_yjs.optDouble("sum08"));
//														m_yjs.put("SUM11", js_yjs.optDouble("sum11"));//医疗救助补偿金额
//														m_yjs.put("SUM13", js_yjs.optDouble("sum13"));
//														m_yjs.put("SUM14", js_yjs.optDouble("sum14"));
//														m_yjs.put("SUM31", js_yjs.optDouble("sum31"));//费用总金额
//														m_yjs.put("SUM32", js_yjs.optDouble("sum32"));
//														m_yjs.put("SUM33", js_yjs.optDouble("sum33"));
//														m_yjs.put("BZ", js_yjs.optString("bz"));
//														m_yjs.put("DJID", djid);
//														m_yjs.put("NHKH", NHKH);
//														m_yjs.put("GRBH", GRBH);
//														res.put("NHYJSXX", m_yjs);
//													}else {
//														js_fyxx.put("czlx", "2");
//														hcn.doUploadFyxx(js_fyxx.toString());
//														throw new ServiceException(js_yjs.optString("msg"));
//													}	
//											}
//										}else{
//											throw new ServiceException(scfyxx.optString("msg").toString());
//										}
//									}
//								}else {
//									throw new ServiceException(qxscfyxx.optString("msg").toString());
//								}
//							}
//						} catch (Exception_Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				else{
					//下面是第一次预结算流程
					try {
						request=josnqzj;
						request.put("operator",uid );
						request.put("ICD10",mzxx.get("ICD10")==null?"J06.903":mzxx.get("ICD10").toString() );
						request.put("jbmc",mzxx.get("JBMC")==null?"上呼吸道感染":mzxx.get("JBMC").toString() );
						request.put("cardid",NHKH);
						request.put("grbh",GRBH);
						request.put("year", Year);
						request.put("ryrq",ryrq);
						request.put("ryzt", "3");
						request.put("serviceId", "Mzdj");
						try {
							JSONObject jorm=new JSONObject(hcn.doMzdj(request.toString()));
							if(jorm.optString("code").equals("1")){
								djid=jorm.optString("djid");
								String upsql="update MS_GHMX set NHDJID=:NHDJID,NHDJSJ=:NHDJSJ where SBXH=:SBXH ";
								Map<String, Object> m=new HashMap<String, Object>();
								m.put("NHDJID",djid);
								m.put("NHDJSJ",d.getTime());
								m.put("SBXH",mzxx.get("GHGL").toString());
								dao.doSqlUpdate(upsql, m);
								//下面进行费用明细上传
								JSONObject reqfyxx=new JSONObject();
								reqfyxx=josnsjqzj;
								reqfyxx.put("djid", djid);
								reqfyxx.put("jzlx", "1");
								reqfyxx.put("operator",uid);
								reqfyxx.put("czlx","1");//操作类型 1 上传费用，2 取消费用
								reqfyxx.put("serviceId", "UploadFyxx");
								//封装处方和收费项目
								JSONObject jnsfxx=xm.bulidjsonsfxx(body,GHGL);
								reqfyxx.put("ypArr", jnsfxx.get("yparr"));
								reqfyxx.put("zlArr", jnsfxx.get("zlarr"));
								try {
									//上传费用明细
									String fyscrs=hcn.doUploadFyxx(reqfyxx.toString());
									if(fyscrs!=null && fyscrs.length() >0){
										JSONObject fyscrm=new JSONObject(fyscrs);
										if(fyscrm.optString("code").equals("1")){
											//开始预结算
											JSONObject fz_yjsxx=new JSONObject();
											fz_yjsxx=josnqzj;
											fz_yjsxx.put("operator",uid);
											fz_yjsxx.put("djid", djid);
											fz_yjsxx.put("cardid", NHKH);
											fz_yjsxx.put("grbh", GRBH);
											fz_yjsxx.put("fpje",jnsfxx.get("zje"));
											fz_yjsxx.put("Year", Year);
											fz_yjsxx.put("jzlx", "1");//1门诊
											fz_yjsxx.put("serviceId", "FybxYjs");
											String re_yjs=hcn.doFybxYjs(fz_yjsxx.toString());
											if(re_yjs!=null && re_yjs.length()>0 ){
												JSONObject js_yjs=new JSONObject(re_yjs);
												if(js_yjs.optString("code").equals("1")){
													Map<String, Object> m_yjs=new HashMap<String, Object>();
													m_yjs.put("SUM01", js_yjs.optDouble("sum01"));//总补偿金额
													m_yjs.put("SUM04", js_yjs.optDouble("sum04"));
													m_yjs.put("SUM06", js_yjs.optDouble("sum06"));
													m_yjs.put("SUM07", js_yjs.optDouble("sum07"));
													m_yjs.put("SUM08", js_yjs.optDouble("sum08"));
													m_yjs.put("SUM11", js_yjs.optDouble("sum11"));//医疗救助补偿金额
													m_yjs.put("SUM13", js_yjs.optDouble("sum13"));
													m_yjs.put("SUM14", js_yjs.optDouble("sum14"));
													m_yjs.put("SUM31", js_yjs.optDouble("sum31"));//费用总金额
													m_yjs.put("SUM32", js_yjs.optDouble("sum32"));
													m_yjs.put("SUM33", js_yjs.optDouble("sum33"));
													m_yjs.put("BZ", js_yjs.optString("bz"));
													m_yjs.put("DJID", djid);
													m_yjs.put("NHKH", NHKH);
													m_yjs.put("GRBH", GRBH);
													res.put("NHYJSXX", m_yjs);
												}else {
													reqfyxx.put("czlx", "2");
													hcn.doUploadFyxx(reqfyxx.toString());
													throw new ServiceException(js_yjs.optString("msg"));
												}
											}
										}else if(fyscrm.optString("code").equals("-1")){
											throw new ServiceException(fyscrm.optString("msg"));
										}
									}
								} catch (Exception_Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else if (jorm.optString("code").equals("-1")){
								throw new ServiceException(jorm.optString("msg"));
							}
						} catch (Exception_Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//				}
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//上传住院费用明细
		protected void doSendzyfymx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String, Object>)req.get("body");
			UserRoleToken user = UserRoleToken.getCurrent();
			String uid=user.getUserId();
			String jgid = user.getManageUnitId();
			XnhModel xm=new XnhModel(dao);
			try {
				xm.Sendzyfymx(res,body,uid,jgid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/**yx
		 * 验证刷的卡是否是结算病人
		 * @param req
		 * @param res
		 * @param dao
		 * @param ctx
		 * @throws ServiceException
		 */
		protected void doCheckBr(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String, Object>)req.get("body");
			XnhModel xm=new XnhModel(dao);
			try {
				xm.CheckBr(body,res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		protected void doZyyjs(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String, Object>)req.get("body");
			XnhModel xm=new XnhModel(dao);
			try {
				xm.Zyyjs(body, res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//yx-2017-06-22-每日对账明细
		protected void doMrdzMx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String,Object>)req.get("body");
			XnhModel xm=new XnhModel(dao);
			try {
				xm.MrdzMx(body, res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//每日对账
		protected void doMrnhdz(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String,Object>)req.get("body");
			XnhModel xm=new XnhModel(dao);
			try {
				xm.Mrnhdz(body, res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//农合冲账
		protected void doMrnhcz(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
			Map<String, Object> body=(Map<String,Object>)req.get("body");
			XnhModel xm=new XnhModel(dao);
			try {
				xm.Mrnhcz(body, res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	public URL getwebserviceurl(String jgid){
		URL url=null;
		try {
			url=new URL(DictionaryController.instance().get("phis.dictionary.HcnWebservice").getText(jgid.substring(0,9)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}
}
