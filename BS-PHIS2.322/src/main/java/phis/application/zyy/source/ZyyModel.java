package phis.application.zyy.source;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.net.HttpURLConnection;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import org.apache.axis.client.Service;
import org.apache.axis.client.Call;
import org.apache.axis.description.ParameterDesc;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyCheckInOutManageModel;
import phis.application.sto.source.StorehouseCheckInOutModel;
import phis.source.utils.BSPHISUtil;

import com.alibaba.fastjson.JSONArray;
import com.bsoft.esb.ws.soapui.util.PropertiesUtil;
import com.chcit.spd.ws.service.SPDService;
import com.chcit.spd.ws.service.SPDServiceException_Exception;
import com.chcit.spd.ws.service.SPDServicePortType;

public class ZyyModel implements BSPHISEntryNames{
	protected static BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ZyyModel.class);
	private String orgCode = "";
	private String username = "";
	private String password = "";
	private String SPD_SWITCH = "0";
	private String jgid = UserRoleToken.getCurrent().getManageUnit().getId();
	
	public ZyyModel(BaseDAO dao){
		this.dao = dao;
	}
	/**
	 * 上传供应商到中医院
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doUpSuppliers(
			Map<String, Object> body, Context ctx) throws Exception {
		Map<String, Object> req = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = df.format(new Date());
		String ack = "接口关闭";
		readProperties();
		if(SPD_SWITCH.equals("1")){
			ack = "fail";
			String sql = "select "
					+ "DWXH as DWXH,"
					+ "DWMC as DWMC,"
					+ "ZFPB as ZFPB"
					+ " from yk_jhdw";
			List<Map<String, Object>> list = new ArrayList();
			try {
				list = dao.doSqlQuery(sql, null);
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}			
			Map<String, Object> ListMap = new HashMap<String, Object>();
			
			if (req.size() == 0) {
				Document document = DocumentHelper.createDocument();
				Element xml = document.addElement("xml");
				Element items = xml.addElement("items");
				for (int i = 0; i < list.size(); i++) {
					Map<String,Object> map = list.get(i);
					Element item = items.addElement("item");
					Element head = item.addElement("head");		
					head.addElement("OrgCode").setText(orgCode);
					head.addElement("SiteCode").setText(username);
					head.addElement("BPartnerID").setText(map.get("DWXH") + "");
					head.addElement("Name").setText(map.get("DWMC") + "");
					head.addElement("IsVendor").setText("Y");
					head.addElement("IsCustomer").setText("N");			
					String active = "Y";
					if(!(map.get("ZFPB")+"").equals("0")){
						 active = "N";
					}
					head.addElement("IsActive").setText(active);
					head.addElement("Remark").setText("");
					head.addElement("Timestamp").setText(currentTime);
				}
				if (list.size() > 0 && req.size() == 0) {
					String xmlStr = document.asXML();
					xmlStr = xmlStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").trim();
					SPDService service = new SPDService();
					SPDServicePortType spd = service.getSPDServiceHttpSoap11Endpoint();
					System.out.print(xmlStr);
					String result = spd.putBPartner(xmlStr, username, password);
					if(result != null && !result.equals("")){
						ack = getAck(result);
					}
				}
			}
		}
		req.put("body", ack);
		return req;
	}
		
	/**
	 * 上传商品到中医院
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doUpProduct(
			Map<String, Object> body, Context ctx) throws Exception {
		Map<String, Object> req = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = df.format(new Date());		
		String ack = "接口关闭";
		readProperties();
		if(SPD_SWITCH.equals("1")){
			ack = "fail";
			params.put("jgid", jgid);
			String sql = "select a.YPXH,a.YPMC,"
						+ "b.YPCD as YPCD,"+
					 	 "(select CDMC from YK_CDDZ where YPCD = b.YPCD) as CDMC,"+
					 	 "(select SXMC from YK_YPSX where YPSX = a.YPSX) as SXMC,"+
					 	 "a.YPGG,a.YPDW,a.TYPE,"
					 	 + "to_char(b.JHJG,'fm999999990.009') as JHJG,"
					 	 + "a.JYLX "+
					 	 "from YK_TYPK a "+
					 	 "left join YK_CDXX b "+
					 	 "on a.YPXH = b.YPXH "+
					 	 "where jgid=:jgid";
			List<Map<String, Object>> list = new ArrayList();
			try {
				list = dao.doSqlQuery(sql, params);
			} catch (PersistentDataOperationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Map<String, Object> ListMap = new HashMap<String, Object>();
			if (req.size() == 0) {
				Document document = DocumentHelper.createDocument();
				Element xml = document.addElement("xml");
				Element items = xml.addElement("items");
				for (int i = 0; i < list.size(); i++) {
					Map<String,Object> map = list.get(i);
					Element item = items.addElement("item");
					Element head = item.addElement("head");		
					head.addElement("OrgCode").setText(orgCode);
					head.addElement("SiteCode").setText(username);
					head.addElement("ProductID").setText(map.get("YPXH") + "");
					head.addElement("Name").setText(map.get("YPMC") + "");
					head.addElement("MedicineName").setText(map.get("YPMC") + "");
					head.addElement("TradeName").setText(map.get("YPMC") + "");
					head.addElement("ManufacturerID").setText(map.get("YPCD") + "");
					head.addElement("Manufacturer").setText(map.get("CDMC") + "");
					head.addElement("ProductStyle").setText(map.get("SXMC") + "");
					head.addElement("BaseProductSpec").setText(map.get("YPGG") + "");
					head.addElement("BaseUOM").setText(map.get("YPDW") + "");
					head.addElement("BaseUOMCode").setText("");
					head.addElement("ProductSpec").setText(map.get("YPGG") + "");
					head.addElement("UOM").setText(map.get("YPDW") + "");
					head.addElement("UOMCode").setText("");
					head.addElement("PackageQty").setText("");
					
					String CategoryCode = "";
					String TYPE = map.get("TYPE") + "";
					if((TYPE).equals("3")){
						TYPE = "7";
					}
					head.addElement("CategoryCode").setText(TYPE);
					head.addElement("ProductType").setText("");
					head.addElement("ProductValue").setText(map.get("PYDM") + "");
					head.addElement("CertificateNo").setText("");
					head.addElement("IsActive").setText("N");
					head.addElement("PriceList").setText(map.get("JHJG") + "");
					head.addElement("PriceSale").setText(map.get("JHJG") + "");
					head.addElement("LPackageQty").setText("1");
					head.addElement("Remark").setText("");
					head.addElement("Timestamp").setText(currentTime);
					head.addElement("IsKeepStock").setText("N");
					head.addElement("IsFee").setText("N");
					head.addElement("ProductEnglishName").setText("");
					head.addElement("Specialist").setText("");
					head.addElement("ProductArea").setText("");
					head.addElement("EssentialDrugType").setText(map.get("JYLX") + "");
					head.addElement("AntiDrugType").setText("");
				}
				if (list.size() > 0 && req.size() == 0) {
					String xmlStr = document.asXML();
					xmlStr = xmlStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").trim();
					SPDService service = new SPDService();
					SPDServicePortType spd = service.getSPDServiceHttpSoap11Endpoint();
					System.out.print(xmlStr);
					String result = spd.putProduct(xmlStr, username, password);
					if(result != null && !result.equals("")){
						ack = getAck(result);
					}
				}
			}
		}
		req.put("body", ack);
		return req;
	}

	/**
	 * 上传采购计划到中医院
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doUpProcurementPlan(
			Map<String, Object> body, Context ctx) throws Exception {
		Map<String, Object> req = new HashMap<String, Object>();
		String jgid = UserRoleToken.getCurrent().getManageUnit().getId();
		String ack = "接口关闭";
		readProperties();
		if(SPD_SWITCH.equals("1")){
			ack = "fail";
			String HospitalCode = "";
			int DWXH = (Integer) body.get("DWXH");
			int JHDH = (Integer) body.get("JHDH");
			int SBXH = (Integer) body.get("SBXH");
			String CKJE = body.get("CKJE").toString();
			String JHBZ = (String) body.get("JHBZ");
			if (JHBZ != "null" && JHBZ != null && JHBZ != "") {
				JHBZ = JHBZ;
			} else {
				JHBZ = "null";
			}
			String BZGH = (String) body.get("BZGH");
			String BZRQ = (String) body.get("BZRQ");
			String YYDZ = (String) body.get("YYDZ");
			int XTSB = (Integer) body.get("XTSB");
			if (jgid.length() >= 9) {
				HospitalCode = jgid.substring(0, 9);
			} else {
				HospitalCode = jgid;
			}
			
			String sql = "select a.organizname as ORGANIZNAME from sys_organization a where a.organizcode='"
					+ HospitalCode + "'";
			Map<String, Object> sql1 = null;
			try {
				sql1 = dao.doSqlLoad(sql, null);
			} catch (PersistentDataOperationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String sql4 = "select  DWZH as DWZH,DWMC as  DWMC from yk_jhdw  a where a.DWXH='"
					+ DWXH + "'";
			Map<String, Object> sql41 = null;
			try {
				sql41 = dao.doSqlLoad(sql4, null);
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}
			String DWMC = (String) sql41.get("DWMC");
			
			String sql5 = "select  to_char(sum(jhsl),'fm999999990.009') as SumQuantity, count(SBXH) as Num from YK_JH02   where JHDH='"
					+ JHDH + "' and jgid='" + jgid + "' and xtsb='" + XTSB + "'";
			Map<String, Object> sql51 = null;
			try {
				sql51 = dao.doSqlLoad(sql5, null);
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}			
			String SumQuantity = (String) sql51.get("SUMQUANTITY");
			String LineCount = sql51.get("NUM") + "";
			
			
			String sql6 = "select ykmc as YKMC from YK_YKLB where yksb = "+XTSB;
			Map<String, Object> sql61 = null;
			try {
				sql61 = dao.doSqlLoad(sql6, null);
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}
			String warehouseName = "";
			if(sql61.get("YKMC") != null){
				warehouseName = (String) sql61.get("YKMC");
			}
				
			Map<String, Object> map_ret = new HashMap<String, Object>();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String KSRQ = sdf1.format(new Date());
			String hql = "select to_char(t.SBXH,'fm999999990.009') as Seq,"
					+ "(select ypmc from yk_typk where ypxh=t.ypxh) as YPMC,"
					+ "(select YKMC from YK_YKLB where YKSB = t.XTSB) as YKMC,"
					+ "(select PZWH from YK_YPCD where t.YPXH = YPXH and t.YPCD = YPCD) as PZWH,"
					+ "t.YPXH as YPXH,"
					+ "b.YPDW as YPDW,"
					+ "b.YPBH as YPBH,"
					+ "(select SXMC from yk_YPSX where YPSX= b.YPSX) as SXMC,"
					+ "(select CDMC from YK_CDDZ where YPCD = t.YPCD) as CDMC, "
					+ "t.YPCD as YPCD,"
					+ "b.YPGG as YPGG,"
					+ "(select a.ZBBM   from yk_cdxx a  where a.YPXH = t.YPXH and a.YPCD = t.YPCD and a.JGID = t.JGID)  as  UniCode,"
					+ "to_char(t.SPSL,'fm999999990.009') as Quantity,"
					+ "to_char(t.GJJG,'fm999999990.009') as Price,"
					+ "to_char(t.GJJE,'fm999999990.009') as Amount "
					+ "from  YK_JH02 t "
					+ "left join yk_typk b on b.ypxh=t.ypxh where JHDH=:JHDH and jgid=:jgid  and xtsb=:XTSB";
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("XTSB", XTSB);
			map_par.put("JHDH", JHDH);
			map_par.put("jgid", jgid);
			List<Map<String, Object>> list = null;
			try {
				list = dao.doSqlQuery(hql, map_par);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			Map<String, Object> ListMap = new HashMap<String, Object>();
			if (req.size() == 0) {
				Document document = DocumentHelper.createDocument();
				Element xml = document.addElement("xml");		
				Element items = xml.addElement("items");
				Element item = items.addElement("item");
				Element head = item.addElement("head");		
				head.addElement("OrgCode").setText(orgCode);
				head.addElement("SiteCode").setText(username);
				head.addElement("WarehouseID").setText(XTSB + "");
				head.addElement("WarehouseName").setText(warehouseName);
				head.addElement("OrderPlanID").setText(JHDH + "");
				head.addElement("DatePlaned").setText(BZRQ);
				head.addElement("TotalAmt").setText(CKJE);
				head.addElement("Priority").setText("");
				head.addElement("Remark").setText("");
				head.addElement("LineCount").setText(LineCount);
				head.addElement("Timestamp").setText(BZRQ);
				head.addElement("DateDeliverPlaned").setText("");
				head.addElement("DateDeliverPlaned2").setText("");
				head.addElement("Address").setText(YYDZ);
				Element detail = item.addElement("detail");

				for (int i = 0; i < list.size(); i++) {
					String UniCode = list.get(i).get("UNICODE") + "";
					String YPMC = list.get(i).get("YPMC") + "";
					String YPXH = list.get(i).get("YPXH") + "";
					String SXMC = list.get(i).get("SXMC") + "";
					String YPDW = list.get(i).get("YPDW") + "";
					String YPGG = list.get(i).get("YPGG") + "";
					String YPCD = list.get(i).get("YPCD") + "";
					String CDMC = list.get(i).get("CDMC") + "";
					String PZWH = list.get(i).get("PZWH") + "";
					String Quantity = list.get(i).get("QUANTITY") + "";
					String Price = list.get(i).get("PRICE") + "";
					String Amount = list.get(i).get("AMOUNT") + "";
					String Seq = list.get(i).get("SEQ") + "";
					Element line = detail.addElement("line");
					line.addElement("OrderPlanLineID").setText(Seq);
					line.addElement("ProductID").setText(YPXH);
					line.addElement("MedicineName").setText(YPMC);
					line.addElement("ProductName").setText(YPMC);
					line.addElement("ProductStyle").setText(SXMC);
					line.addElement("UOM").setText(YPDW);
					line.addElement("ProductSpec").setText(YPGG);
					line.addElement("CertificateNo").setText(PZWH);
					line.addElement("ManufacturerID").setText(YPCD);
					line.addElement("Manufacturer").setText(CDMC);
					line.addElement("Price").setText(Price);
					line.addElement("Qty").setText(Quantity);
					line.addElement("VendorID").setText(DWXH+"");
					line.addElement("VendorName").setText(DWMC);
					line.addElement("InvoiceRule").setText("");
					line.addElement("Lot").setText("");
					line.addElement("GuaranteeDate").setText("");
					line.addElement("Remark").setText("");
				}
				;
				if (list.size() > 0 && req.size() == 0) {
					String xmlStr = document.asXML();
					xmlStr = xmlStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").trim();
					//xmlStr = xmlStr.replace("<xml>", "<![CDATA[<xml>");
					//xmlStr = xmlStr.replace("</xml>", "</xml>]]>").trim();
					SPDService service = new SPDService();
					SPDServicePortType spd = service.getSPDServiceHttpSoap11Endpoint();
					System.out.print(xmlStr);
					String result = spd.putPOPlan(xmlStr, username, password);
					if(result != null && !result.equals("")){
						ack = getAck(result);
						if(ack.equals("success")){
							String update = " update YK_JH01 t set t.SCBZ=:scbz,t.SCRQ=to_date(:scrq,'yyyy-mm-dd') where t.JGID='"
									+ jgid
									+ "' and t.XTSB="
									+ XTSB
									+ " and t.JHDH='"
									+ JHDH + "'";
							Map<String, Object> p = new HashMap<String, Object>();
							p.put("scbz", 2);
							p.put("scrq", KSRQ);
							try {
								dao.doUpdate(update, p);
								req.put("code", "200");
							} catch (PersistentDataOperationException e) {
								req.put("code", "201");
								req.put("msg", e.getMessage());
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		req.put("body", ack);
		return req;
	}
	
	public void readProperties() throws IOException{
		Properties pro = new Properties();
		InputStream is = ZyyModel.class.getClassLoader().getResourceAsStream("spd.properties"); 
		pro.load(is); 
		SPD_SWITCH = pro.getProperty("SPD_SWITCH");
		if(jgid.equals("320124004")){
			orgCode = pro.getProperty("NJ_LS_TTZX_HIS_ORGCODE");
			username = pro.getProperty("NJ_LS_TTZX_HIS_USERNAME");
			password = pro.getProperty("NJ_LS_TTZX_HIS_PASSWORD");
		}
		else if(jgid.equals("320124008")){
			orgCode = pro.getProperty("NJ_LS_HFZX_HIS_ORGCODE");
			username = pro.getProperty("NJ_LS_HFZX_HIS_USERNAME");
			password = pro.getProperty("NJ_LS_HFZX_HIS_PASSWORD");
		}
	}
	
	//解析返回值
	public String getAck(String xmlStr) throws DocumentException{
		if(xmlStr != null && !xmlStr.equals("")){
			Document documentData = DocumentHelper.parseText(xmlStr);
			Element root = documentData.getRootElement();
			Element sys = root.element("sys");
			String status = sys.element("status").getTextTrim();
			if(status != null && status.equals("ACK")){
				return "success";
			}else{
				return "fail";
			}
		}else{
			return "fail"; 
		}
	}
}
