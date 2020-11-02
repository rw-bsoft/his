package phis.application.xnh.source;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
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
import ctd.validator.ValidateException;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.SchemaUtil;

public class XnhModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(XnhModel.class);
	public XnhModel(BaseDAO dao) {
		this.dao = dao;
	}
	public void Savedownloadypml(Map<String, Object> body,Map<String, Object> res) throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String jgid = user.getManageUnitId();
		String version=this.getmaxid("VERSION","NH_BSOFT_YPML");
		Integer downtype=2;
		if(version.equals("") || version.length() <=0){
			downtype=1;
		}
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
		try {
			JSONObject request=new JSONObject();
			try {
				request = this.getjsonqzjpzxx(jgid);
				request.put("operator", uid);
				request.put("downtype", downtype);
				request.put("version", version);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String rm=hcn.dogetYpml(request.toString());
			try {
				JSONObject jorm=new JSONObject(rm);
				if(jorm.optString("code").equals("1")){
					JSONArray arr=jorm.optJSONArray("arr");
					for(int i=0;i<arr.length();i++){
						JSONObject one =arr.getJSONObject(i);
						Map<String,Object> record=new HashMap<String,Object>();
						record.put("YPXH", one.optString("YPXH").length() <1?"":one.optString("YPXH"));
						record.put("YPMC", one.optString("YPMC").length() <1?"":one.optString("YPMC"));
						record.put("YPLX", one.optString("YPLX").length() <1?"":one.optString("YPLX"));
						record.put("YPDW", one.optString("YPDW").length() <1?"":one.optString("YPDW"));
						record.put("YPGG", one.optString("YPGG").length() <1?"":one.optString("YPGG"));
						record.put("YPSX", one.optString("YPSX").length() <1?"":one.optString("YPSX"));
						record.put("ZFBL", one.optDouble("ZFBL"));
						record.put("PYDM", one.optString("PYDM").length() <1?"":one.optString("PYDM"));
						record.put("TYMC", one.optString("TYMC").length() <1?"":one.optString("TYMC"));
						record.put("MZZFBL", one.optDouble("MZZFBL"));
						record.put("VERSION", one.optString("VERSION").length() <1?"":one.optString("VERSION"));
						record.put("ZBM", one.optString("ZBM").length() <1?"":one.optString("ZBM"));
						record.put("SCQY", one.optString("SCQY").length() <1?"":one.optString("SCQY"));
						record.put("ZBJG", one.optString("ZBJG")==""?"":one.optDouble("ZBJG") );
						record.put("SFJY", one.optString("SFJY")==""?"":one.optDouble("SFJY") );
						record.put("BZ", one.optString("BZ").length() <1?"":one.optString("BZ"));
						record.put("XYBBH", one.optString("XYBBH").length() <1?"":one.optString("XYBBH"));
						List<Map<String,Object>> ypxx=this.getypxxbyypxh(one.get("YPXH").toString());
						if(ypxx!=null && ypxx.size() >0){
							//已有药品信息不做保存
						}else {
							try {
								dao.doSave("create",BSPHISEntryNames.NH_BSOFT_YPML, record, false);
							} catch (ValidateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (PersistentDataOperationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}else {
					throw new ModelDataOperationException(jorm.optString("msg"));
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
	public void savedownloadzlml(Map<String, Object> body,Map<String, Object> res) throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String jgid = user.getManageUnitId();
		//获取最大版本号
		String version=this.getmaxid("VERSION","NH_BSOFT_ZLML");
		Integer downtype=2;
		if(version.equals("") || version.length() <=0){
			downtype=1;
		}
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
		try {
			JSONObject request=new JSONObject();
			try {
				request = this.getjsonqzjpzxx(jgid);
				request.put("operator", uid);
				request.put("downtype", downtype);
				request.put("version", version);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				JSONObject jorm=new JSONObject(hcn.dogetZlml(request.toString()));
				if(jorm.optString("code").equals("1")){
					JSONArray arr=jorm.optJSONArray("arr");
					for(int i=0;i<arr.length();i++){
						JSONObject one =arr.getJSONObject(i);
						Map<String,Object> record=new HashMap<String,Object>();
						record.put("FYXH", one.optString("FYXH").length() <1?"":one.optString("FYXH"));
						record.put("FYMC", one.optString("FYMC").length() <1?"":one.optString("FYMC"));
						record.put("FYDW", one.optString("FYDW").length() <1?"":one.optString("FYDW"));
						record.put("ZFBL", one.optString("ZFBL").length() <1?"":one.optDouble("ZFBL"));
						record.put("PYDM", one.optString("PYDM").length() <1?"":one.optString("PYDM"));
						record.put("FYDJ", one.optString("FYDJ").length() <1?"":one.optDouble("FYDJ"));
						record.put("MZZFBL", one.optString("MZZFBL").length() <1?"":one.optDouble("MZZFBL"));
						record.put("VERSION", one.optString("VERSION").length() <1?"":one.optString("VERSION"));
						record.put("XETYPE", one.optString("XETYPE").length() <1?"":one.optDouble("XETYPE"));
						record.put("DJXE", one.optString("XE").length() <1?"":one.optDouble("XE"));
						record.put("XYBBH", "");
						List<Map<String,Object>> zlxx=this.getzlxxbyfyxh(one.optString("FYXH"));
						if(zlxx!=null && zlxx.size() >0){
							
						}else {
							try {
								try {
									dao.doSave("create", BSPHISEntryNames.NH_BSOFT_ZLML, record, false);
								} catch (ValidateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (PersistentDataOperationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}else {
					throw new ModelDataOperationException(jorm.optString("msg"));		
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
	public void getDzmlByItem(HCNWebservices hcn,JSONObject request,Map<String,Object> data,Map<String,Object> res,String type)throws ServiceException{
		try {
			if(type.equals("1")){
				request.put("icode", data.get("YPXH")+"");
				JSONObject jorm=new JSONObject(hcn.dogetDzmlByItem(request.toString()));
				if(jorm.optString("code").equals("1")){
					JSONObject map=jorm.optJSONObject("map");
					List<Map<String,Object>> dzlist=this.loadspxxList(data.get("JGID")+"", request.optString("icode"),type);
					if(dzlist.size() > 0){
						String upspmlsql="update NH_BSOFT_SPML set SPJG=:SPJG,ZFBL=:ZFBL,MZZFBL=:MZZFBL where JGID=:JGID and HYBM=:HYBM and XMLX=1";
						Map<String,Object> p=new HashMap<String,Object>();
						p.put("SPJG", map.optString(data.get("YPXH").toString()));
						p.put("ZFBL", Double.parseDouble(map.optString("zfbl")));
						p.put("MZZFBL", Double.parseDouble(map.optString("mzzfbl")));
						p.put("JGID", data.get("JGID").toString());
						p.put("HYBM", data.get("ICODE").toString());
						try {
							dao.doUpdate(upspmlsql, p);
						} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					Map<String,Object> savedata=new HashMap<String,Object>();
					savedata.put("HYBM", data.get("NHBM_BSOFT").toString());
					savedata.put("XMLX", type);
					savedata.put("XMMC", data.get("YPMC").toString());
					savedata.put("XMGG", data.get("YPGG")==null?"":data.get("YPGG").toString());
					savedata.put("ZFBL", Double.parseDouble(map.optString("zfbl")));
					savedata.put("MZZFBL", Double.parseDouble(map.optString("mzzfbl")));
					savedata.put("SPJG", map.optString(data.get("YPXH").toString()));
					savedata.put("YYBM", data.get("YPXH").toString());
					savedata.put("JGID", data.get("JGID").toString());
					try {
						dao.doSave("create", BSPHISEntryNames.NH_BSOFT_SPML, savedata, false);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			   //更新审核状态
				String upypxx="update YK_TYPK set NHBM_SH=:NHBM_SH where YPXH=:YPXH ";
				Map<String,Object> pa=new HashMap<String,Object>();
				pa.put("NHBM_SH", map.optString(data.get("YPXH").toString()));
				pa.put("YPXH", Long.parseLong(data.get("ICODE")+""));
				try {
					dao.doUpdate(upypxx,pa);
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else
			if(jorm.optString("code").equals("-1")){
				System.out.println(jorm.optString("msg"));
//				throw new ServiceException("农合返回信息："+jorm.optString("msg"));
			}
			}else if (type.equals("2")){	
				try {
					request.put("icode", data.get("FYXH")+"");
					JSONObject jorm=new JSONObject(hcn.dogetDzmlByItem(request.toString()));
					if(jorm.optString("code").equals("1")){
						JSONObject map=jorm.optJSONObject("map");
						List<Map<String,Object>> dzlist=this.loadspxxList( data.get("JGID").toString(), data.get("FYXH").toString(),type);
						if(dzlist.size() > 0){
							String upspmlsql="update NH_BSOFT_SPML set SPJG=:SPJG,ZFBL=:ZFBL,MZZFBL=:MZZFBL where JGID=:JGID and HYBM=:HYBM and XMLX=2";
							Map<String,Object> p=new HashMap<String,Object>();
							p.put("SPJG", map.optString(data.get("FYXH").toString()));
							p.put("ZFBL", Double.parseDouble(map.optString("zfbl")));
							p.put("MZZFBL", Double.parseDouble(map.optString("mzzfbl")));
							p.put("JGID", data.get("JGID").toString());
							p.put("HYBM", data.get("FYXH").toString());
							try {
								dao.doUpdate(upspmlsql, p);
							} catch (PersistentDataOperationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else {
							Map<String,Object> savedata=new HashMap<String,Object>();
							savedata.put("HYBM", data.get("NHBM_BSOFT").toString());
							savedata.put("XMLX", type);
							savedata.put("XMMC", data.get("FYMC").toString()); 
							savedata.put("XMGG", data.get("FYDW")==null?"":data.get("FYDW").toString());  
							savedata.put("ZFBL", Double.parseDouble(map.optString("zfbl")));
							savedata.put("MZZFBL", Double.parseDouble(map.optString("mzzfbl")));
							savedata.put("SPJG", map.optString(data.get("FYXH").toString())); 
							savedata.put("YYBM", data.get("FYXH").toString()); 
							savedata.put("JGID", data.get("JGID").toString());
							try {
								dao.doSave("create", BSPHISEntryNames.NH_BSOFT_SPML, savedata, false);
							} catch (PersistentDataOperationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					//更新审核状态
						String upypxx="update GY_YLSF set NHBM_SH=:NHBM_SH where FYXH=:FYXH ";
						Map<String,Object> pa=new HashMap<String,Object>();
						pa.put("NHBM_SH", map.optString(data.get("FYXH").toString()));
						pa.put("FYXH", Long.parseLong(data.get("FYXH")+""));
						try {
							dao.doUpdate(upypxx,pa);
						} catch (PersistentDataOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}else
					if(jorm.optString("code").equals("-1")){
						System.out.println(jorm.optString("msg"));
//						throw new ServiceException(jorm.optString("msg"));
					}
				
			} catch (Exception_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String getmaxid(String col,String tablename){
		String sql="select max("+col+") as "+col+" from "+tablename;
		Map<String,Object> p=new HashMap<String,Object>();
		String version="";
		try {
			version=dao.doLoad(sql, p).get(col)==null?"":dao.doLoad(sql, p).get(col).toString();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	
	public List<Map<String,Object>> getypxxbyypxh(String YPXH){
		String sql="select YPXH,YPMC,YPLX from NH_BSOFT_YPML where YPXH=:YPXH ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("YPXH", YPXH);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public List<Map<String,Object>> getzlxxbyfyxh(String FYXH){
		String sql="select FYXH,FYMC,FYDW from NH_BSOFT_ZLML where FYXH=:FYXH ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("FYXH", FYXH);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	
	public List<Map<String,Object>> getjbxxbycode(Long CODE){
		String sql="select CODE,NAME,PYDM from NH_BSOFT_JBBM where CODE=:CODE ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("CODE", CODE);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public List<Map<String,Object>> getdbzxxbycode(Long CODE){
		String sql="select CODE,NAME,PYDM from NH_BSOFT_JBBM_DRGS where CODE=:CODE ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("CODE", CODE);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public List<Map<String,Object>> loadnonhbmlist(String type){
		String sql="";
		if(type.equals("1")){
			sql="select YPXH as YPXH ,YPMC as YPMC from YK_TYPK where YPXH>:YPXH and NHBM_BSOFT is null  ";
		}else if (type.equals("2")){
			sql="select FYXH as FYXH ,FYMC as FYMC from GY_YLSF where FYXH>:YPXH and NHBM_BSOFT is null  ";
		}
		Map<String,Object> p=new HashMap<String,Object>();
		
		long xh=0;
		p.put("YPXH", xh);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public List<Map<String,Object>> loadnhypxxlistbyypmc(String YPMC){
		String sql="select YPXH as YPXH ,YPMC as YPMC from NH_BSOFT_YPML where YPMC=:YPMC ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("YPMC", YPMC);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public List<Map<String,Object>> loadnhzlxxlistbyypmc(String FYMC){
		String sql="select FYXH as FYXH ,FYMC as FYMC from NH_BSOFT_ZLML where FYMC=:FYMC ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("FYMC", FYMC);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public void sendypdzxx(Map<String,Object> dzxx,String type) throws Exception{
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String jgid = user.getManageUnitId();
		JSONObject temp=new JSONObject();
		temp.put("iCode", dzxx.get("YPXH").toString());
		temp.put("iNamety", dzxx.get("YPMC").toString());
		temp.put("iName", dzxx.get("YPMC")==null?"":dzxx.get("YPMC").toString());
		temp.put("spec", dzxx.get("YPGG")==null?"":dzxx.get("YPGG").toString());
		temp.put("Price", dzxx.get("LSJG").toString());
		temp.put("oCode", dzxx.get("NHBM_BSOFT").toString());
		temp.put("Zbm", "");
		temp.put("SCQY", dzxx.get("CDMC").toString());
		JSONArray temparr=new JSONArray();
		temparr.put(temp);
		JSONObject qzjxx=this.getjsonqzjpzxx(jgid);
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
		String re=hcn.douploadDzml(uid, qzjxx.optString("yybh"), type, temparr.toString(), qzjxx.optString("ip"), qzjxx.optString("port"));
		JSONObject jorm=new JSONObject(re);
		if(jorm.optString("code").equals("-1")){
			throw new ServiceException(jorm.optString("msg"));
		}
	}
	//获取对照的药品信息
	public JSONArray loaddzypxxArray(String JGID){
		String sql="select YPXH as YPXH ,YPMC as YPMC,CDMC as CDMC,YPGG as YPGG," +
				   " LSJG as LSJG,NHBM_BSOFT as NHBM_BSOFT from NH_BSOFT_YP_NEEDSP where JGID=:JGID ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("JGID", JGID);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		JSONArray re=new JSONArray();
		try {
			version=dao.doQuery(sql, p);
			for(int i=0;i<version.size();i++){
				JSONObject temp=new JSONObject();
				Map<String,Object> one=version.get(i);
				try {
					temp.put("iCode", one.get("YPXH").toString());
					temp.put("iNamety", one.get("YPMC").toString());
					temp.put("iName", one.get("YPMC")==null?"":one.get("YPMC").toString());
					temp.put("spec", one.get("YPGG")==null?"":one.get("YPGG").toString());
					temp.put("Price", one.get("LSJG").toString());
					temp.put("oCode", one.get("NHBM_BSOFT").toString());
					temp.put("Zbm", "");
					temp.put("SCQY", one.get("CDMC").toString());
					re.put(temp);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return re;
	}
	//获取对照的药品信息
		public JSONArray loaddzzlxxArray(String JGID){
			String sql="select FYXH as FYXH ,FYMC as FYMC,FYDW as FYDW,BZJG as BZJG," +
					   " NHBM_BSOFT as NHBM_BSOFT from NH_BSOFT_ZL_NEEDSP where JGID=:JGID ";
			Map<String,Object> p=new HashMap<String,Object>();
			p.put("JGID", JGID);
			List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
			JSONArray re=new JSONArray();
			try {
				version=dao.doQuery(sql, p);
				for(int i=0;i<version.size();i++){
					JSONObject temp=new JSONObject();
					Map<String,Object> one=version.get(i);
					try {
						temp.put("iCode", one.get("FYXH").toString());
						temp.put("iNamety", one.get("FYMC").toString());
						temp.put("iName", one.get("FYMC")==null?"":one.get("FYMC").toString());
						temp.put("spec", one.get("FYDW")==null?"":one.get("FYDW").toString());
						temp.put("Price", one.get("BZJG").toString());
						temp.put("oCode", one.get("NHBM_BSOFT").toString());
						temp.put("Zbm", "");
						temp.put("SCQY", "");
						re.put(temp);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				return re;
		}
	public List<Map<String,Object>> loaddzypxxList(String JGID){
		String sql="select YPXH as YPXH ,YPMC as YPMC,CDMC as CDMC,YPGG as YPGG," +
				   " LSJG as LSJG,NHBM_BSOFT as NHBM_BSOFT,JGID as JGID,ICODE as ICODE from NH_BSOFT_YP_NEEDSP where JGID=:JGID ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("JGID", JGID);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public List<Map<String,Object>> loaddzzlxxList(String JGID){
		String sql="select FYXH as FYXH ,FYMC as FYMC,FYDW as FYDW,BZJG as BZJG," +
				   " NHBM_BSOFT as NHBM_BSOFT,JGID as JGID from NH_BSOFT_ZL_NEEDSP where JGID=:JGID ";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("JGID", JGID);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}	
	public List<Map<String,Object>> loadspxxList(String JGID,String YYBM,String XMLX){
		String sql="select HYBM as HYBM,XMLX as XMLX from NH_BSOFT_SPML where JGID=:JGID" +
				   " and YYBM=:YYBM and XMLX=:XMLX";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("JGID", JGID);
		p.put("YYBM", YYBM);
		p.put("XMLX", Double.parseDouble(XMLX));
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public List<Map<String,Object>> loadbrxxbymzhm(String MZHM){
		String sql="select BRID as BRID,MZHM as MZHM from MS_BRDA where MZHM=:MZHM";
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("MZHM", MZHM);
		List<Map<String,Object>> version=new ArrayList<Map<String,Object>>();
		try {
			version=dao.doQuery(sql, p);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return version;
	}
	public JSONObject bulidjsonsfxx(List<Map<String,Object>> sfxx,String ghxh){
		JSONObject jsonsfxx=new JSONObject();
		JSONArray yparr=new JSONArray();
		JSONArray zlarr=new JSONArray();
		Double zje=0.00;//总金额
		for(int i=0;i<sfxx.size();i++){
			JSONObject temp=new JSONObject();
			Map<String,Object> one=sfxx.get(i);
			if(one.get("CFLX").toString().equals("1")||one.get("CFLX").toString().equals("2")||one.get("CFLX").toString().equals("3")){
				try {
					temp.put("xnhbm",one.get("NHBM_BSOFT"));
					temp.put("ypmc",one.get("YPMC"));
					temp.put("ypgg",one.get("YFGG"));
					temp.put("jldw",one.get("YFDW"));
					temp.put("ypjx",one.get("YPSX"));
					temp.put("ypsl",one.get("YPSL"));
					temp.put("ypdj",one.get("YPDJ"));
					Calendar d = Calendar.getInstance();
					d.setTime(new Date());
					String ryrq=(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(d.getTime());
					temp.put("billdate",ryrq);
					if(one.get("CFLX").toString().equals("3")){
						temp.put("ypzyfs",one.get("CFTS"));
					}
					temp.put("ypje",one.get("HJJE"));
					zje=zje+Double.parseDouble(one.get("HJJE").toString());
					temp.put("icode",one.get("YPXH"));
					temp.put("ypcd",one.get("YPCD"));
					temp.put("cfsb",one.get("CFSB")==null?ghxh:one.get("CFSB"));
					temp.put("kfys",one.get("YSDM"));
					yparr.put(temp);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(one.get("CFLX").toString().equals("0")){
				try {
					temp.put("xnhbm",one.get("NHBM_BSOFT"));
					temp.put("ypmc",one.get("YPMC"));
					temp.put("ypgg","");
					temp.put("jldw",one.get("YFDW"));
					temp.put("ypjx","");
					temp.put("ypsl",one.get("YPSL"));
					temp.put("ypdj",one.get("YPDJ"));
					Calendar d = Calendar.getInstance();
					d.setTime(new Date());
					String ryrq=(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(d.getTime());
					temp.put("billdate",ryrq);
					temp.put("je",one.get("HJJE"));
					zje=zje+Double.parseDouble(one.get("HJJE").toString());
					temp.put("icode",one.get("YPXH"));
					temp.put("ypcd",one.get("YPCD"));
					temp.put("cfsb",one.get("CFSB")==null?ghxh:one.get("CFSB"));
					temp.put("kfys",one.get("YSDM"));
					zlarr.put(temp);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			jsonsfxx.put("yparr", yparr);
			jsonsfxx.put("zlarr", zlarr);
			jsonsfxx.put("zje", zje);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonsfxx;
		
	}
	public void Sendzyfymx(Map<String, Object> res,Map<String, Object> body,String uid,String jgid)throws Exception{
		JSONObject request=this.getjsonqzjpzxx(jgid.substring(0, 9));
		List<Map<String,Object>> fyxx=(List<Map<String,Object>>)body.get("fyxxdata");
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
		
		String resendcheck=body.get("resendcheck").toString();
		String nodjmsg="";
		List<Map<String,Object>> zyxx=(List<Map<String,Object>>)body.get("zyxxdata");
		Map<String,String> nodj=new HashMap<String, String>();
		for(int l=0;l<zyxx.size();l++){
			Map<String,Object> temp=zyxx.get(l);
			String NHDJID=temp.get("NHDJID")+"";
			if(NHDJID.equals("")||NHDJID.equals("null")||NHDJID.length()<1){
				nodjmsg=nodjmsg+"住院号："+temp.get("ZYH")+"还未进行农合登记！请先登记！";
			}else{
				if(resendcheck.equals("true")){
					this.canceluploadFyxx(NHDJID, "2", uid, jgid);
				}
			}
		}
		if(nodjmsg!=null && nodjmsg.length() >0){
			res.put("code", "500");
			res.put("msg", nodjmsg);
		}
		for(int i=0;i<fyxx.size();i++){
			Map<String, Object> one=fyxx.get(i);
			String sql="";
			if(one.get("YPCD").toString().equals("0")){
				JSONArray zlarr=new JSONArray();
				Map<String, Object> p=new HashMap<String, Object>();
				p.put("JLXH", Long.parseLong(one.get("JLXH")+""));
				sql=" select b.NHBM_BSOFT as NHBM_BSOFT,d.FYMC as NH_FYMC,a.FYMC as YPMC," +
					" b.FYDW as YPDW,a.FYSL as FYSL,a.FYDJ as FYDJ," +
					"to_char(a.FYRQ,'yyyy-mm-dd') as FYRQ,c.NHDJID as NHDJID," +
					" '1' as ZFBL ,a.FYXH as YPXH,a.ZYH as ZYH,a.YSGH as YSGH,a.ZJJE as ZJJE  " +
					" from ZY_FYMX a " +
					" join GY_YLSF b on a.FYXH=b.FYXH" +
					" join ZY_BRRY c on a.ZYH=c.ZYH " +
					" left join NH_BSOFT_ZLML d on b.NHBM_BSOFT=d.FYXH"+
					" where  a.JLXH=:JLXH ";
				try {
					Map<String, Object> zlxx=dao.doSqlLoad(sql,p);
					String MXNHDJID=zlxx.get("NHDJID")+"";
					if(MXNHDJID.equals("")||MXNHDJID.equals("null")||MXNHDJID.length()<1){
						continue;
					}
					//费用信息封装
					JSONObject temp=new JSONObject();
					try {
						temp.put("xnhbm", zlxx.get("NHBM_BSOFT")+"");
						temp.put("ypmc", zlxx.get("YPMC")+"");
						temp.put("je", zlxx.get("ZJJE")+"");
						temp.put("billdate", zlxx.get("FYRQ")+"");
						temp.put("icode", zlxx.get("YPXH")+"");
						temp.put("cfsb", zlxx.get("ZYH")+"");
						temp.put("kfys", zlxx.get("YSGH")+"");
						temp.put("ypdj", zlxx.get("FYDJ")+"");
						temp.put("ypsl", zlxx.get("FYSL")+"");
						zlarr.put(temp);
						//封装请求数据
						request.put("djid",MXNHDJID);
						request.put("jzlx","2");
						request.put("operator",uid);
						request.put("czlx","1");//上传
						request.put("zlArr", zlarr);
						request.put("ypArr", new JSONArray());
						request.put("serviceId", "UploadFyxx");
						String refyxx=hcn.doUploadFyxx(request.toString());
						JSONObject jsonfyxx=new JSONObject(refyxx);
						if(jsonfyxx.optString("code").equals("1")){
							String upsql="update ZY_FYMX set NHSCBZ='1' where JLXH=:JLXH ";
							dao.doUpdate(upsql, p);
						}else{
							res.put("code", "500");
							res.put("msg", jsonfyxx.optString("msg"));
							throw new Exception(jsonfyxx.optString("msg"));
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
					JSONArray yparr=new JSONArray();
					Map<String, Object> p=new HashMap<String, Object>();
					p.put("JLXH", Long.parseLong(one.get("JLXH")+""));
					sql=" select b.NHBM_BSOFT as NHBM_BSOFT,d.YPMC as NH_YPMC,a.FYMC as YPMC," +
						" b.YPDW as YPDW,a.FYSL as FYSL,a.FYDJ as FYDJ,b.YPGG as YPGG," +
						" to_char(a.FYRQ,'yyyy-mm-dd') as FYRQ,c.NHDJID as NHDJID," +
						" '1' as ZFBL ,a.FYXH as YPXH,a.ZYH as ZYH,a.YSGH as YSGH," +
						" a.ZJJE as ZJJE,b.YPSX as YPJX,a.YPCD as YPCD  " +
						" from ZY_FYMX a " +
						" join YK_TYPK b on a.FYXH=b.YPXH" +
						" join ZY_BRRY c on a.ZYH=c.ZYH " +
						" left join NH_BSOFT_YPML d on b.NHBM_BSOFT=d.YPXH"+
						" where  a.JLXH=:JLXH ";
					try {
						Map<String, Object> ypxx=dao.doSqlLoad(sql,p);
						String MXNHDJID=ypxx.get("NHDJID")+"";
						if(MXNHDJID.equals("")||MXNHDJID.equals("null")||MXNHDJID.length()<1){
							continue;
						}
						//费用信息封装
						JSONObject temp=new JSONObject();
						try {
							temp.put("xnhbm", ypxx.get("NHBM_BSOFT")+"");
							temp.put("xnhmc", ypxx.get("NH_YPMC")+"");
							temp.put("ypmc", ypxx.get("YPMC")+"");
							temp.put("ypgg", ypxx.get("YPGG")+"");
							temp.put("jldw", ypxx.get("YPDW")+"");
							temp.put("ypjx", ypxx.get("YPJX")+"");
							temp.put("ypsl", ypxx.get("FYSL")+"");
							temp.put("ypdj", ypxx.get("FYDJ")+"");
							temp.put("ypje", ypxx.get("ZJJE")+"");
							temp.put("billdate", ypxx.get("FYRQ")+"");
							temp.put("cfsb", ypxx.get("ZYH")+"");
							temp.put("kfys", ypxx.get("YSGH")+"");
							temp.put("icode", ypxx.get("YPXH")+"");
							temp.put("ypcd", ypxx.get("YPCD")+"");
							yparr.put(temp);
							//封装请求数据
							request.put("djid",MXNHDJID);
							request.put("jzlx","2");
							request.put("operator",uid);
							request.put("czlx","1");//上传
							request.put("ypArr", yparr);
							request.put("zlArr", new JSONArray());
							request.put("serviceId", "UploadFyxx");
							String refyxx=hcn.doUploadFyxx(request.toString());
							JSONObject jsonfyxx=new JSONObject(refyxx);
							if(jsonfyxx.optString("code").equals("1")){
								String upsql="update ZY_FYMX set NHSCBZ='1' where JLXH=:JLXH ";
								dao.doUpdate(upsql, p);
							}else{
								res.put("code", "500");
								res.put("msg",jsonfyxx.optString("msg"));
								throw new Exception(jsonfyxx.optString("msg"));
							}
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	}
	public void Sendyjszyfymx(String zyh,String uid,String jgid,String nhdjid ,Map<String, Object> res)throws Exception{
		JSONObject request=this.getjsonqzjpzxx(jgid.substring(0,9));
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
		String zlsql=" select b.NHBM_BSOFT as NHBM_BSOFT,d.FYMC as NH_FYMC,a.FYMC as YPMC," +
				" b.FYDW as YPDW,a.FYSL as FYSL,a.FYDJ as FYDJ," +
				"to_char(a.FYRQ,'yyyy-mm-dd') as FYRQ,c.NHDJID as NHDJID," +
				" '1' as ZFBL ,a.FYXH as YPXH,a.ZYH as ZYH,a.YSGH as YSGH,a.ZJJE as ZJJE  " +
				" from ZY_FYMX a " +
				" join GY_YLSF b on a.FYXH=b.FYXH" +
				" join ZY_BRRY c on a.ZYH=c.ZYH " +
				" left join NH_BSOFT_ZLML d on b.NHBM_BSOFT=d.FYXH"+
				" where  a.ZYH=:ZYH and a.YPCD =0 and (a.NHSCBZ=0 or a.NHSCBZ is null) ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("ZYH", Long.parseLong(zyh));
		
		List<Map<String, Object>> zllist=dao.doSqlQuery(zlsql, p);
		JSONArray zlarr=new JSONArray();
		if(zllist!=null && zllist.size() >0){
			for(int i=0;i<zllist.size();i++){
				JSONObject temp=new JSONObject();
				Map<String, Object> zlxx=zllist.get(i);
				try {
					temp.put("xnhbm", zlxx.get("NHBM_BSOFT")+"");
					temp.put("ypmc", zlxx.get("YPMC")+"");
					temp.put("je", zlxx.get("ZJJE")+"");
					temp.put("billdate", zlxx.get("FYRQ")+"");
					temp.put("icode", zlxx.get("YPXH")+"");
					temp.put("cfsb", zlxx.get("ZYH")+"");
					temp.put("kfys", zlxx.get("YSGH")+"");
					temp.put("fydj", zlxx.get("FYDJ")+"");
					temp.put("fysl", zlxx.get("FYSL")+"");
					zlarr.put(temp);
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		String ypsql=" select b.NHBM_BSOFT as NHBM_BSOFT,d.YPMC as NH_YPMC,a.FYMC as YPMC," +
				" b.YPDW as YPDW,a.FYSL as FYSL,a.FYDJ as FYDJ,b.YPGG as YPGG," +
				" to_char(a.FYRQ,'yyyy-mm-dd') as FYRQ,c.NHDJID as NHDJID," +
				" '1' as ZFBL ,a.FYXH as YPXH,a.ZYH as ZYH,a.YSGH as YSGH," +
				" a.ZJJE as ZJJE,b.YPSX as YPJX,a.YPCD as YPCD  " +
				" from ZY_FYMX a " +
				" join YK_TYPK b on a.FYXH=b.YPXH" +
				" join ZY_BRRY c on a.ZYH=c.ZYH " +
				" left join NH_BSOFT_YPML d on b.NHBM_BSOFT=d.YPXH"+
				" where  a.ZYH=:ZYH and a.YPCD >0 and (a.NHSCBZ=0 or a.NHSCBZ is null) ";
		List<Map<String, Object>> yplist=dao.doSqlQuery(ypsql, p);
		JSONArray yparr=new JSONArray();
	    if(yplist!=null && yplist.size() >0) {
	    	for(int j=0 ;j<yplist.size();j++){
	    		JSONObject oneyp=new JSONObject();
	    		Map<String, Object> ypxx=yplist.get(j);
	    		oneyp.put("xnhbm", ypxx.get("NHBM_BSOFT")+"");
	    		oneyp.put("xnhmc", ypxx.get("NH_YPMC")+"");
	    		oneyp.put("ypmc", ypxx.get("YPMC")+"");
	    		oneyp.put("ypgg", ypxx.get("YPGG")+"");
	    		oneyp.put("jldw", ypxx.get("YPDW")+"");
	    		oneyp.put("ypjx", ypxx.get("YPJX")+"");
	    		oneyp.put("ypsl", ypxx.get("FYSL")+"");
	    		oneyp.put("ypdj", ypxx.get("FYDJ")+"");
	    		oneyp.put("ypje", ypxx.get("ZJJE")+"");
	    		oneyp.put("billdate", ypxx.get("FYRQ")+"");
	    		oneyp.put("cfsb", ypxx.get("ZYH")+"");
	    		oneyp.put("kfys", ypxx.get("YSGH")+"");
	    		oneyp.put("icode", ypxx.get("YPXH")+"");
	    		oneyp.put("ypcd", ypxx.get("YPCD")+"");
				yparr.put(oneyp);
	    	}
	    }
	  //封装请求数据
		request.put("djid",nhdjid);
		request.put("jzlx","2");
		request.put("operator",uid);
		request.put("czlx","1");//上传
		request.put("ypArr", yparr);
		request.put("zlArr", zlarr);
		request.put("serviceId", "UploadFyxx");
		String refyxx=hcn.doUploadFyxx(request.toString());
		JSONObject jsonfyxx=new JSONObject(refyxx);
		if(jsonfyxx.optString("code").equals("1")){
			String upsql="update ZY_FYMX set NHSCBZ='1' where ZYH=:ZYH ";
			dao.doUpdate(upsql, p);
		}else{
			res.put("code", "500");
			res.put("msg",jsonfyxx.optString("msg"));
			throw new Exception(jsonfyxx.optString("msg"));
		}			 
		
	}	
	public void CheckBr(Map<String, Object> body,Map<String, Object> res)throws Exception{
		String cardid=(String)body.get("cardid");
		String ZYH=(String)body.get("ZYH");
		String sql="select b.NHKH as NHKH,b.GRBH as GRBH from ZY_BRRY a ,MS_BRDA b where a.BRID=b.BRID and  a.ZYH=:ZYH and b.NHKH=:NHKH";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("ZYH", ZYH);
		p.put("NHKH", cardid);
		Map<String, Object> brxx=dao.doSqlLoad(sql, p);
		Map<String, Object> re=new HashMap<String, Object>();
		if(brxx!=null && brxx.size()>0 ){
			re.put("nhdk", "1");
			re.put("GRBH", brxx.get("GRBH")+"");
			re.put("NHKH", cardid);
			res.put("body", re);
		}else{
			res.put("code", "502");
			res.put("msg", "未找到该卡的住院登记信息");
		}
	}
	public void Zyyjs(Map<String, Object> body,Map<String, Object> res)throws Exception{
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String jgid = user.getManageUnitId();
		try{
		String dbzjs=body.get("dbzjs")+"";	
		String ZYH=body.get("ZYH")+"";
		String zdsql="select a.JBBM as JBBM,a.MSZD as MSZD from EMR_ZYZDJL a ,GY_JBBM b " +
				" where a.JBBM=b.ICD10 and b.ZFPB='0' and a.JZXH=:JZXH order by a.ZDLB desc";
		Map<String, Object> zdmap=new HashMap<String, Object>();
		zdmap.put("JZXH",Long.parseLong(ZYH));
		List<Map<String, Object>> zdlist=dao.doQuery(zdsql, zdmap);;
		Map<String, Object> zd=new HashMap<String, Object>();
		if(zdlist!=null && zdlist.size() >0 ){
			zd=zdlist.get(0);
		}else if (!dbzjs.equals("1")){
			res.put("code", "502");
			res.put("msg", "未找到该病人有效的农合诊断，请医生在诊断录入界面补录下诊断！");
			return;
		}
		String sql="select a.NHDJID as NHDJID ,a.CYFS as BRZG, a.BRBQ as RYKS,a.BRBQ as CYKS,a.ZZYS as JZYS,to_char(a.RYRQ,'yyyy-mm-dd') as RYRQ ,to_char(a.CYRQ,'yyyy-mm-dd') as CYRQ from ZY_BRRY a where a.ZYH=:ZYH ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("ZYH", ZYH);
		Map<String, Object> brxx=dao.doSqlLoad(sql, p);
		if((brxx.get("JZYS")+"").equals("null")||(brxx.get("JZYS")+"").equals("")){
			res.put("code", "502");
			res.put("msg", "住院结算主治医生不能为空，请到住院护士中病人信息中完善好，谢谢，亲！！！");
			return;
		}
		if(brxx.get("NHDJID")==null && (brxx.get("NHDJID")+"").length() <2){
			res.put("code", "502");
			res.put("msg", "农合登记流水号不存在，请先转病人性质为自费再转农合，之后结算！！！");
			return;
		}
		this.Sendyjszyfymx(ZYH, uid, jgid, brxx.get("NHDJID")+"", res);
		JSONObject request=new JSONObject();
		request=this.getjsonqzjpzxx(jgid.substring(0,9));
		request.put("operator", uid);
		request.put("djid", brxx.get("NHDJID")+"");
		request.put("cardid", body.get("NHKH")+"");
		request.put("grbh", body.get("GRBH")+"");
		request.put("fpje", body.get("FYHJ")+"");
		request.put("Year", this.getyear());
		request.put("jzlx", "2");
		
		if(dbzjs.equals("1")){//单病种预结算
			request.put("ICD10",body.get("DRGS")+"");
			request.put("jbmc",body.get("DRGSTEXT")+"");
		}else if(zd!=null && zd.size() >0){
			request.put("ICD10",zd.get("JBBM")+"");
			request.put("jbmc",zd.get("MSZD")+"");
		}else{
			request.put("ICD10","J06.903");
			request.put("jbmc","上呼吸道感染");
		}
		request.put("brzg", brxx.get("BRZG")+"");
		request.put("ryks", brxx.get("RYKS")+"");
		request.put("cyks", brxx.get("CYKS")+"");
		request.put("jzys", brxx.get("JZYS")+"");
		request.put("ryrq", brxx.get("RYRQ")+"");
		request.put("cyrq", brxx.get("CYRQ")+"");
		request.put("sjzyts", body.get("ZYTS")+"");
		request.put("sfzz", "1");
		request.put("zcfmzy", "0");
		request.put("mzyxbz", "1");
		request.put("mzfs", "0");
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
		JSONObject reyjsxx=new JSONObject();
		if(dbzjs.equals("1")){
			request.put("serviceId", "FybxYjs_Drgs");
			reyjsxx=new JSONObject(hcn.doFybxYjsDrgs(request.toString()));
		}else{
			request.put("serviceId", "FybxYjs");
			reyjsxx=new JSONObject(hcn.doFybxYjs(request.toString()));
		}
		if(reyjsxx.optString("code").equals("1")){
		Map<String, Object> z_yjs=new HashMap<String, Object>();
		z_yjs.put("SUM02", reyjsxx.optDouble("sum02"));
		z_yjs.put("SUM03", reyjsxx.optDouble("sum03"));
		z_yjs.put("SUM04", reyjsxx.optDouble("sum04"));
		z_yjs.put("SUM05", reyjsxx.optDouble("sum05"));
		z_yjs.put("SUM06", reyjsxx.optDouble("sum06"));
		z_yjs.put("SUM07", reyjsxx.optDouble("sum07"));
		z_yjs.put("SUM08", reyjsxx.optDouble("sum08"));
		z_yjs.put("SUM12", reyjsxx.optDouble("sum12"));
		z_yjs.put("SUM13", reyjsxx.optDouble("sum13"));
		z_yjs.put("SUM14", reyjsxx.optDouble("sum14"));
		z_yjs.put("SUM39", reyjsxx.optDouble("sum39"));
		z_yjs.put("BZ", reyjsxx.optString("bz"));
		if(dbzjs.equals("1")){//单病种增加的返回值
			z_yjs.put("SUM18", reyjsxx.optDouble("sum18"));
			z_yjs.put("SUM19", reyjsxx.optDouble("sum19"));
			z_yjs.put("SUM20", reyjsxx.optDouble("sum20"));
			z_yjs.put("SUM21", reyjsxx.optDouble("sum21"));
			z_yjs.put("SUM22", reyjsxx.optDouble("sum22"));
			z_yjs.put("SUM23", reyjsxx.optDouble("sum23"));
			z_yjs.put("SUM24", reyjsxx.optDouble("sum24"));
			z_yjs.put("SUM25", reyjsxx.optDouble("sum25"));
			z_yjs.put("SUM26", reyjsxx.optDouble("sum26"));
			z_yjs.put("SUM27", reyjsxx.optString("sum27").equals("")?0:reyjsxx.optDouble("sum27"));
			z_yjs.put("SUM39", reyjsxx.optString("sum39").equals("")?0:reyjsxx.optDouble("sum39"));
			z_yjs.put("SUM40", reyjsxx.optString("sum40").equals("")?0:reyjsxx.optDouble("sum40"));
			z_yjs.put("DBZJSFLAG", "1");
			z_yjs.put("DRGS",body.get("DRGS")+"");
		}else{
			z_yjs.put("SUM09", reyjsxx.optDouble("sum09"));
		}
		z_yjs.put("DJID", brxx.get("NHDJID")+"");
		z_yjs.put("NHKH", body.get("NHKH")+"");
		z_yjs.put("ICKH", body.get("NHKH")+"");
		z_yjs.put("GRBH", body.get("GRBH")+"");
		res.put("yjsxx",z_yjs);
		}else{
			res.put("code","502");
			res.put("msg",reyjsxx.optString("msg"));
			return;
		}
		}catch(Exception e)  {
			e.printStackTrace();
			res.put("code","502");
			res.put("msg","预结算失败！！！");
		}
		
	}
	
	public String FybxJs(Map<String, Object> body,Map<String, Object> res,Context ctx)throws Exception{
		String bxid="";
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String jgid = user.getManageUnitId();
		JSONObject request=new JSONObject();
		if((body.get("JZLX")+"").equals("1")){
			request=this.getjsonqzjpzxx(jgid);
		}else if((body.get("JZLX")+"").equals("2")) {
			request=this.getjsonqzjpzxx(jgid.substring(0,9));
		}else{
			request=this.getjsonqzjpzxx(jgid);
		}
		request.put("operator", uid);
		request.put("jzlx", body.get("JZLX")+"");
		request.put("djid", body.get("DJID")+"");
		request.put("Year", this.getyear());
		request.put("fprq", body.get("FPRQ"));
		request.put("fphm", body.get("FPHM"));
		request.put("zyh", body.get("ZYH"));
		request.put("ztjsbj", body.get("ZTJSBJ"));
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
		JSONObject jsxx=new JSONObject();
		if(body.containsKey("DBZJSFLAG")&& body.get("DBZJSFLAG").toString().equals("1")){
			request.put("serviceId", "FybxJs_Drgs");
			jsxx=new JSONObject(hcn.doFybxJsDrgs(request.toString()));
		}else{
			request.put("serviceId", "FybxJs");
			jsxx=new JSONObject(hcn.doFYbxjs(request.toString()));
		}
		
		if(jsxx.optString("code").equals("1")){
			bxid=jsxx.optString("bxid");
			body.put("BXID", bxid);
			body.put("JGID", jgid);
			body.put("YEAR", this.getyear());
			body.put("JZLB", "2");
			body.put("ZFPB", "0");
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.beginTransaction();
			dao.doSave("create", BSPHISEntryNames.NH_BSOFT_JSJL, body, false);
			ss.getTransaction().commit();
		}else{
			res.put("code","502");
			res.put("msg",jsxx.optString("msg"));
		}
		return bxid;
	}
	
	public void ReSaveFybx(Map<String, Object> body,Map<String, Object> res)throws Exception{
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String jgid = user.getManageUnitId();
		String sql="";
		Map<String, Object> p=new HashMap<String, Object>();
		if(body.containsKey("ZYH")){
			sql="select a.NHBXID as BXID ,c.NHKH as NHKH,a.DRGS as DRGS from ZY_ZYJS a ,ZY_BRRY b ,MS_BRDA c" +
				" where a.ZYH=b.ZYH and b.BRID=c.BRID and a.ZYH=:ZYH and a.FPHM=:FPHM ";
			p.put("ZYH", body.get("ZYH"));
		}else{
			//门诊
		}
		p.put("FPHM", body.get("ZYHM"));//住院号码就是发票号码
		Map<String, Object> bxxx=dao.doSqlLoad(sql, p);
		if(bxxx!=null && bxxx.size()>0){
			JSONObject request=new JSONObject();
			request=this.getjsonqzjpzxx(jgid);
			request.put("operator",uid);
			request.put("invoiceId",body.get("ZYHM"));
			request.put("cardid",bxxx.get("NHKH"));
			request.put("Year",this.getyear());
			request.put("bxid",bxxx.get("BXID"));
			URL url = this.getwebserviceurl(jgid);
			HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
			HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
			
			JSONObject zfxx=new JSONObject();
			if(bxxx.get("DRGS")!=null && !bxxx.get("DRGS").toString().equals("null") && bxxx.get("DRGS").toString().length()>0 ){
				request.put("serviceId","ReSaveFybx_Drgs");
				zfxx=new JSONObject(hcn.doReSaveFybxDrgs(request.toString()));	
				
			}else{
				request.put("serviceId","ReSaveFybx");
				zfxx=new JSONObject(hcn.doReSaveFybx(request.toString()));	
			}
				
			if(zfxx.optString("code").equals("1")){
				p.remove("FPHM");
				p.put("BXID", bxxx.get("BXID"));
				dao.doSqlUpdate("update NH_BSOFT_JSJL set ZFPB='1' where ZYH=:ZYH and BXID=:BXID ", p);
			}else{
				res.put("code", "502");
				res.put("msg", zfxx.optString("msg"));
				throw new Exception(zfxx.optString("msg"));
			}		
		}else{
			res.put("code", "502");
			res.put("msg", "未找到有效农合报销信息,请联系管理员！");
			throw new Exception("未找到有效农合报销信息,请联系管理员！");
		}
	}
	public void MrdzMx(Map<String, Object> body,Map<String, Object> res)throws Exception{
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
		
		JSONObject request=new JSONObject();
		request=this.getjsonqzjpzxx(jgid);
		request.put("operator", "all");
		request.put("hospitalcode", request.getString("yybh"));
		request.put("Dzrq",body.get("Dzrq")+"");
		request.put("Zzrq",body.get("Zzrq")+"");
		request.put("dzlx",body.get("dzlx")+"");
		JSONObject jorm = new JSONObject(hcn.doMrdzMx(request.toString()));
		List<Map<String,Object>> records=new ArrayList<Map<String,Object>>();
		if(jorm.optString("code").equals("1")){
			JSONArray list= jorm.optJSONArray("arr");
			for(int i=0;i<list.length();i++){
				Map<String,Object> temp=new HashMap<String, Object>();
				JSONObject one=list.optJSONObject(i);
				temp.put("BXID", one.optString("BXID"));
				temp.put("FPHM", one.optString("FPHM"));
				temp.put("ZJJE", one.optString("ZJJE"));
				temp.put("JYSJ", one.optString("JYSJ"));
				temp.put("JYLX", one.optString("JYLX"));
				temp.put("ICKH", one.optString("ICKH"));
				temp.put("SBJE", one.optString("SBJE"));
				temp.put("DJLSH", one.optString("DJLSH"));
				records.add(temp);
			}
		}
		records=SchemaUtil.setDictionaryMassageForList(records,
				"phis.application.xnh.schemas.NH_BSOFT_MXDZ");
		res.put("body", records);
	}
	public void Mrnhdz(Map<String, Object> body,Map<String, Object> res)throws Exception{
//		String ksrq=body.get("Dzrq")+"";
//		String zzrq=body.get("Zzrq")+"";
		String dzlx=body.get("dzlx")+"";
		List<Map<String,Object>> records=(List<Map<String,Object>>)body.get("data");
		List<Map<String,Object>> dzjg=new ArrayList<Map<String,Object>>();
		Map<String,Object> pa= new HashMap<String, Object>();
		if(dzlx.equals("1")){//门诊
		for(Map<String,Object> one:records){
			pa.clear();
			pa.put("DJLSH",one.get("DJLSH"));
			pa.put("FPHM",one.get("FPHM"));
			List<Map<String,Object>> tfjl=new ArrayList<Map<String,Object>>();//退费记录
			List<Map<String,Object>> jsjl=new ArrayList<Map<String,Object>>();//结算记录
			tfjl=dao.doSqlQuery("select -a.zjje  as ZJJE,-a.qtys as QTYS " +
					" from ms_mzxx a ,nh_bsoft_jsjl b where a.mzxh=b.mzxh and" +
					" a.bxid=b.bxid and a.fphm=:FPHM and b.djid=:DJLSH and a.ZFPB='1' ", pa);
			jsjl=dao.doSqlQuery("select a.zjje  as ZJJE,a.qtys as QTYS " +
					" from ms_mzxx a ,nh_bsoft_jsjl b where a.mzxh=b.mzxh and" +
					" a.bxid=b.bxid and a.fphm=:FPHM and b.djid=:DJLSH ", pa);
			if((one.get("JYLX")+"").equals("-1")){
				tfjl=dao.doSqlQuery("select -a.zjje  as ZJJE,-a.qtys as QTYS " +
						" from ms_mzxx a ,nh_bsoft_jsjl b where a.mzxh=b.mzxh and" +
						" a.bxid=b.bxid and a.fphm=:FPHM and b.djid=:DJLSH and a.ZFPB='1' ", pa);
				if(tfjl==null || tfjl.size()==0){
					if(jsjl==null || jsjl.size()==0){
						//农合已作废不处理
					}else{
						one.put("DZJG", "1");//农合作废医院没作废
						dzjg.add(one);
					}
				}else if(tfjl.size() >1){
					one.put("DZJG", "3");//农合作废医院多条作废
					dzjg.add(one);
				}
			}else if ((one.get("JYLX")+"").equals("1")) {
				if(jsjl==null || jsjl.size()==0){
					one.put("DZJG", "2");//农合报销医院没记录
					dzjg.add(one);
				}else if (jsjl.size() >1){
					one.put("DZJG", "4");//农合报销医院多条报销
					dzjg.add(one);
					//农合作废医院作废不处理
				}
			}
//			if(jsjl!=null && jsjl.size()==1){
//				Boolean  err=false;
//				if(Double.parseDouble(jsjl.get(0).get("QTYS")+"")!=Double.parseDouble(one.get("SBJE")+"")){
//					err=true;
//					one.put("DZJG", one.get("DZJG")==null?"5":one.get("DZJG")+",5");//
//				}
//				if(Double.parseDouble(jsjl.get(0).get("ZJJE")+"")!=Double.parseDouble(one.get("ZJJE")+"")){
//					err=true;
//					one.put("DZJG", one.get("DZJG")==null?"6":one.get("DZJG")+",6");//
//				}
//				if(err){
//					dzjg.add(one);
//				}
//			}
		}
		}
		dzjg=SchemaUtil.setDictionaryMassageForList(dzjg,
				"phis.application.xnh.schemas.NH_BSOFT_MXDZ");
		res.put("body", dzjg);		
	}
	public void Mrnhcz(Map<String, Object> body,Map<String, Object> res)throws Exception{
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String jgid = user.getManageUnitId();
		if("1".equals(body.get("DZLX"))){
			String sql="select b.MZXH as MZXH from NH_BSOFT_JSJL a ,MS_MZXX b " +
					" where a.BXID=b.BXID and a.BXID=:BXID and b.JGID=:JGID and b.ZFPB=0 ";
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("BXID",Long.parseLong(body.get("BXID")+""));
			p.put("JGID",jgid);
			Map<String, Object> jsmap=dao.doSqlLoad(sql, p);
			if(jsmap==null || jsmap.size()==0){
				JSONObject request=new JSONObject();
				request=this.getjsonqzjpzxx(jgid);
				request.put("operator",uid);
				request.put("invoiceId",body.get("FPHM"));
				request.put("cardid",body.get("ICKH"));
				request.put("Year",this.getyear());
				request.put("bxid",body.get("BXID"));
				URL url = this.getwebserviceurl(jgid);
				HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
				HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
				request.put("serviceId","ReSaveFybx");
				JSONObject tfxx=new JSONObject();
				tfxx=new JSONObject(hcn.doReSaveFybx(request.toString()));
				if(tfxx.optString("code").equals("1")){
					res.put("code", "200");
					res.put("msg","农合提示:"+tfxx.optString("msg"));
				}else{
					res.put("code", "502");
					res.put("msg","农合提示:"+tfxx.optString("msg"));
				}
			}
		}
	}
	/**yx
	 * 获取前置机信息并增加常量参数msgType
	 * @param jgid
	 * @return
	 * @throws Exception
	 */
	public JSONObject getjsonqzjpzxx(String jgid) throws Exception{
		String pzxxstr=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(jgid);
		JSONObject pzxx =new JSONObject();
		if(pzxxstr!=null && pzxxstr.length() >0){
			String args[]=pzxxstr.split(":");
			pzxx.put("yybh", args[0]);
			pzxx.put("ip", args[1]);
			pzxx.put("port", args[2]);
			pzxx.put("msgType", "2");
		}else{
			throw new Exception("还没设置本医院可农合报销！");
		}
		return pzxx;
	}
	/**
	 * 取消上传的费用信息
	 * @param djid
	 * @param jzlx
	 * @param operator
	 * @param jgid
	 * @throws Exception
	 */
	public void canceluploadFyxx(String djid,String jzlx,String operator,String jgid)throws Exception{
		URL url = this.getwebserviceurl(jgid);
		HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
		HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
			JSONObject request=new JSONObject();
			request=this.getjsonqzjpzxx(jgid.substring(0,9));
			request.put("djid", djid);
			request.put("jzlx", jzlx);
			request.put("operator", operator);
			request.put("czlx", "2");
			request.put("serviceId", "UploadFyxx");
			JSONObject cancelfyxx=new JSONObject(hcn.doUploadFyxx(request.toString()));
			if(cancelfyxx.optString("code").equals("1")){
				//取消成功不做任何操作
			}else{
				throw new Exception(cancelfyxx.optString("msg"));
			}
	}
	
	public String getyear(){
		Calendar d = Calendar.getInstance();
		d.setTime(new Date());
		return d.get(Calendar.YEAR)+"";
	}
	public String getday(Date day){
		Calendar d = Calendar.getInstance();
		d.setTime(day);
		return (new java.text.SimpleDateFormat("yyyy-MM-dd")).format(d.getTime());
	}
	public static URL getwebserviceurl(String jgid){
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
