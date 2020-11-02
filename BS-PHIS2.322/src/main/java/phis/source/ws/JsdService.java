package phis.source.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import ctd.util.AppContextHolder;

import phis.source.ws.AbstractWsService;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class JsdService extends AbstractWsService{
	private static final Log logger = LogFactory.getLog(JsdService.class);
	@WebMethod
	public String execute(String request) {
		Session session = getSessionFactory().openSession();
		logger.info("Received request data[" + request + "].");
		JSONObject jsonResPKS = new JSONObject();
		return jsonResPKS.toString();
	}
	@WebMethod
	public String getBaseInfo(String  iden)throws Exception{
		JSONObject rmsg=new JSONObject();
		
		if(iden.length()!=18){
			rmsg.put("success", "false");
			rmsg.put("message", "身份证号位数不对！");
			return rmsg.toString();
		}
		rmsg.put("referral_iden", iden);
		SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session session = null;
		session =factory.openSession();
		String mpisql="select a.personname,a.sexcode,to_char(a.birthday,'yyyy-mm-dd'),to_char(sysdate,'yyyy')-to_char(a.birthday,'yyyy'),"+
				" a.mobilenumber,a.contact,a.contactphone,a.address"+
				" from mpi_demographicinfo a where a.idcard='"+iden+"'";
		Query query=session.createSQLQuery(mpisql);
		List<?> mpil=query.list();
		query=null;
		if(mpil.size() >0){
			Object[] one = (Object[]) mpil.get(0);
			rmsg.put("referral_name", one[0]==null?"":one[0]+"");
			rmsg.put("referral_sex", (one[1]+"").equals("1")?"1":"2");
			rmsg.put("referral_birthday", one[2]==null?"":one[2]+"");
			rmsg.put("referral_age", one[3]==null?"":one[3]+"");
			rmsg.put("referral_phone", one[4]==null?"":one[4]+"");
			rmsg.put("referral_contact", one[5]==null?"":one[5]+"");
			rmsg.put("referral_cont_phone", one[6]==null?"":one[6]+"");
			rmsg.put("referral_address",one[7]==null?"":one[7]+"");
		}
		String zyh="";
		String ys="";
		String dasql="select a.zyhm,b.ybkh,b.nhkh,b.shbzkh,a.zyh,a.zzys from zy_brry a ,ms_brda b " +
				"where a.brid=b.brid and b.sfzh='"+iden+"' order by a.zyh desc";
		query=session.createSQLQuery(dasql);
		List<?> dal=query.list();
		query=null;
		if(dal.size() >0){
			Object[] one = (Object[]) dal.get(0);
			String kh="";
			if(one[1]!=null){
				kh=one[1]+"";
			}
			if(one[2]!=null){
				kh=one[2]+"";
			}
			if(one[3]!=null){
				kh=one[3]+"";
			}
			rmsg.put("referral_minum",kh);
			rmsg.put("referral_inpatnum",one[0]+"");
			zyh=one[4]+"";
			ys=one[5]==null?"":one[5]+"";
		}else{
			rmsg.put("referral_minum","");
			rmsg.put("referral_inpatnum","");
		}
		String ehrsql="select a.regionCode from EHR_HealthRecord a ,mpi_demographicinfo b " +
				"where a.empiid=b.empiid and b.idcard='"+iden+"'";
		query=session.createSQLQuery(ehrsql);
		List<?> ehrl=query.list();
		query=null;
		if(ehrl.size() >0){
			Object[] one = (Object[]) dal.get(0);
			String regionCode=one[0]+"";
			String hql="select a.regionName as REGIONNAME from EHR_AreaGrid a where a.regionCode=";
			if(regionCode.length() >4){
				query=session.createSQLQuery(hql+"'"+regionCode.substring(0,4)+"'");
				List<?> r=query.list();
				query=null;
				if(r.size()>0){
					rmsg.put("referral_city",r.get(0)+"");
				}
			}
			if(regionCode.length() >6){
				query=session.createSQLQuery(hql+"'"+regionCode.substring(0,6)+"'");
				List<?> r=query.list();
				query=null;
				if(r.size()>0){
					rmsg.put("referral_district",r.get(0)+"");
				}
			}
			if(regionCode.length() >9){
				query=session.createSQLQuery(hql+"'"+regionCode.substring(0,9)+"'");
				List<?> r=query.list();
				query=null;
				if(r.size()>0){
					rmsg.put("referral_street",r.get(0)+"");
				}
			}
			if(regionCode.length() >12){
				query=session.createSQLQuery(hql+"'"+regionCode.substring(0,9)+"'");
				List<?> r=query.list();
				query=null;
				if(r.size()>0){
					rmsg.put("referral_community",r.get(0)+"");
				}
			}
			if(regionCode.length() >15){
				query=session.createSQLQuery(hql+"'"+regionCode.substring(0,9)+"'");
				List<?> r=query.list();
				query=null;
				if(r.size()>0){
					rmsg.put("referral_unit",r.get(0)+"");
				}
			}
		}else{
			rmsg.put("referral_city","");
			rmsg.put("referral_district","");
			rmsg.put("referral_street","");
			rmsg.put("referral_community","");
			rmsg.put("referral_unit","");
		}
		if(zyh.length() >0){
			String zdsql="select a.jbbm,a.mszd,a.zdys from emr_zyzdjl a where a.jzxh="+zyh+" order by a.zdlb";
			query=session.createSQLQuery(zdsql);
			List<?> zdl=query.list();
			query=null;
			if(zdl.size()>0){
				Object[] one = (Object[]) zdl.get(0);
				rmsg.put("referral_diagnosis",one[1]==null?"":one[1]+"");
				rmsg.put("referral_diagnosis_id",one[0]==null?"":one[0]+"");
				if(ys.length()<=0){
					ys=one[2]==null?"":one[2]+"";
				}
			}else{
				rmsg.put("referral_diagnosis","");
				rmsg.put("referral_diagnosis_id","");
			}
		}else{
			rmsg.put("referral_diagnosis","");
			rmsg.put("referral_diagnosis_id","");
		}
		rmsg.put("referral_illness","");
		rmsg.put("referral_medical_history","");
		if(ys.length() >0){
			String psql="select a.cardnum as CARDNUM from SYS_Personnel a where a.personId='"+ys+"'";
			query=session.createSQLQuery(psql);
			List<?> pl=query.list();
			query=null;
			if(pl.size() >0){
				rmsg.put("doct_iden",pl.get(0)+"");
			}else{
				rmsg.put("success", "false");
				rmsg.put("message", "未获取到该病人的入院信息");
			}
		}else{
			rmsg.put("success", "false");
			rmsg.put("message", "未获取到该病人的入院信息");
		}
		return rmsg.toString();
	}
}
