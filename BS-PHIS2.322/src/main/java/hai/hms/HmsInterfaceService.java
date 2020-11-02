package hai.hms;

import phis.application.zyy.source.ZyyModel;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import hai.hms.ws.BSXmlWsEntryClass;
import hai.hms.ws.BSXmlWsEntryClassService;

public class HmsInterfaceService extends AbstractActionService implements
		DAOSupportable {
	@SuppressWarnings("unchecked")
	private String hms_switch = "0";

	public void doUpdateDownBizType(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, IOException {
		hms_switch = getHMS_SWITCH();
		if (hms_switch.equals("1")) {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String result = "没有获取到身份证号码";
			if (body.get("idcard") != null && !body.get("idcard").equals("")) {
				String idcard = body.get("idcard") + "";
				String bizType = body.get("bizType") + "";
				String jgid = UserRoleToken.getCurrent().getManageUnit()
						.getId();
				String param = "<root><idCard>" + idcard + "</idCard><unitId>"
						+ jgid + "</unitId><bizType>" + bizType
						+ "</bizType></root>";
				BSXmlWsEntryClassService service = new BSXmlWsEntryClassService();
				BSXmlWsEntryClass portType = service.getBSXmlWsEntryClassPort();
				result = portType.invoke("UpdateDownBizType", "", "", param);
			}
			System.out.print("下转:转诊类型更新:doUpdateDownBizType:" + result);
		}
	}

	public void doUpdateUpBizType(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, IOException {
		hms_switch = getHMS_SWITCH();
		if (hms_switch.equals("1")) {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String result = "没有获取到身份证号码";
			if (body.get("idcard") != null && !body.get("idcard").equals("")) {
				String idcard = body.get("idcard") + "";
				String bizType = body.get("bizType") + "";
				String jgid = UserRoleToken.getCurrent().getManageUnit()
						.getId();
				String param = "<root><idCard>" + idcard + "</idCard><unitId>"
						+ jgid + "</unitId><bizType>" + bizType
						+ "</bizType></root>";
				BSXmlWsEntryClassService service = new BSXmlWsEntryClassService();
				BSXmlWsEntryClass portType = service.getBSXmlWsEntryClassPort();
				result = portType.invoke("UpdateUpBizType", "", "", param);
			}
			System.out.print("上转:转诊类型更新:doUpdateUpBizType:" + result);
		}
	}

	public void doUpdateDownStatus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, IOException, PersistentDataOperationException {
		hms_switch = getHMS_SWITCH();
		if (hms_switch.equals("1")) {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String result = "没有获取到身份证号码";
			if (body.get("idcard") != null && !body.get("idcard").equals("")) {
				String jgid = UserRoleToken.getCurrent().getManageUnit().getId();
				String userId = UserRoleToken.getCurrent().getUserId();
				String doctorCode = userId;
				String idcard = String.valueOf(body.get("idcard"));
				String status = String.valueOf(body.get("status"));
				String deptCode = String.valueOf(body.get("deptCode"));
				
				//获取科室代码
				String officeCode = "";
				if(!deptCode.equals("") && !deptCode.toLowerCase().equals("null")){
					Map<String, Object> sqlParam = new HashMap<String, Object>();
					Map<String, Object> sqlRes = new HashMap<String, Object>();
					sqlParam.put("deptId", deptCode);
					String sql = "select OFFICECODE as OFFICECODE from SYS_Office where id = :deptId";
					sqlRes = dao.doSqlLoad(sql, sqlParam);
					officeCode =  String.valueOf(sqlRes.get("OFFICECODE"));
					officeCode =  jgid + officeCode;
				}
				
				if(body.get("doctorCode") != null && !body.get("doctorCode").equals("")){
					doctorCode = body.get("doctorCode") + "";
				}
				String param = "<root>"
						+ "<idCard>" + idcard + "</idCard>"
						+ "<unitId>"+ jgid + "</unitId>"
						+ "<doctorCode>"+ doctorCode + "</doctorCode>"
						+ "<departmentCode>"+ officeCode + "</departmentCode>"
						+ "<auditUser>"+ userId + "</auditUser>"
						+ "<confirmVisitUser>"+ userId + "</confirmVisitUser>"
						+ "<status>" + status+ "</status></root>";
				BSXmlWsEntryClassService service = new BSXmlWsEntryClassService();
				BSXmlWsEntryClass portType = service.getBSXmlWsEntryClassPort();
				result = portType.invoke("UpdateDownStatus", "", "", param);
			}
			System.out.print("下转:转诊状态更新:doUpdateDownStatus:" + result);
		}
	}

	public void doUpdateUpStatus(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, IOException, PersistentDataOperationException {
		hms_switch = getHMS_SWITCH();
		if (hms_switch.equals("1")) {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String result = "没有获取到身份证号码";
			if (body.get("idcard") != null && !body.get("idcard").equals("")) {
				String jgid = UserRoleToken.getCurrent().getManageUnit().getId();
				String userId = UserRoleToken.getCurrent().getUserId();
				String doctorCode = userId;
				String idcard = body.get("idcard") + "";
				String status = body.get("status") + "";
				String deptCode = body.get("deptCode") + "";
				
				//获取科室代码
				String officeCode = "";
				if(!deptCode.equals("") && !deptCode.toLowerCase().equals("null")){
					Map<String, Object> sqlParam = new HashMap<String, Object>();
					Map<String, Object> sqlRes = new HashMap<String, Object>();
					sqlParam.put("deptId", deptCode);
					String sql = "select OFFICECODE as OFFICECODE from SYS_Office where id = :deptId";
					sqlRes = dao.doSqlLoad(sql, sqlParam);
					officeCode =  String.valueOf(sqlRes.get("OFFICECODE"));
					officeCode =  jgid + officeCode;
				}
				
				if(body.get("doctorCode") != null && !body.get("doctorCode").equals("")){
					doctorCode = body.get("doctorCode") + "";
				}
				String param = "<root>"
						+ "<idCard>" + idcard + "</idCard>"
						+ "<unitId>"+ jgid + "</unitId>"
						+ "<doctorCode>"+ doctorCode + "</doctorCode>"
						+ "<departmentCode>"+ officeCode + "</departmentCode>"
						+ "<auditUser>"+ userId + "</auditUser>"
						+ "<confirmVisitUser>"+ userId + "</confirmVisitUser>"
						+ "<status>" + status+ "</status></root>";
				BSXmlWsEntryClassService service = new BSXmlWsEntryClassService();
				BSXmlWsEntryClass portType = service.getBSXmlWsEntryClassPort();
				result = portType.invoke("UpdateUpStatus", "", "", param);
			}
			System.out.print("上转:转诊状态更新:doUpdateUpStatus:" + result);
		}
	}

	public void doGetDownRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, DocumentException, IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		hms_switch = getHMS_SWITCH();
		if (hms_switch.equals("1")) {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String strXml = "没有获取到身份证号码";
			res.put("body", false);
			if (body.get("idcard") != null && !body.get("idcard").equals("")) {
				String idcard = body.get("idcard") + "";
				String jgid = UserRoleToken.getCurrent().getManageUnit()
						.getId();
				String param = "<root><idCard>" + idcard + "</idCard><unitId>"
						+ jgid + "</unitId></root>";
				BSXmlWsEntryClassService service = new BSXmlWsEntryClassService();
				BSXmlWsEntryClass portType = service.getBSXmlWsEntryClassPort();
				strXml = portType.invoke("GetDownRecord", "", "", param);
				if (result != null && !result.equals("")) {
					Document document = DocumentHelper.parseText(strXml);
					Element root = document.getRootElement();
					Element xmlMsg = root.element("xmlMsg");
					Element row = xmlMsg.element("row");
					if (row != null) {
						Element xzTime = row.element("XZTIME");
						Element yyName = row.element("YYNAME");
						String strXzTime = xzTime.getTextTrim();
						String strYyName = yyName.getTextTrim();
						result.put("xzTime", strXzTime);
						result.put("yyName", strYyName);
					}
				}
			}			
			System.out.print("下转:查询该用户记录" + ":doGetDownRecord:" + strXml);
		}
		res.put("body", result);
	}

	public void doGetUpRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			DocumentException, IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		hms_switch = getHMS_SWITCH();
		if (hms_switch.equals("1")) {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String strXml = "没有获取到身份证号码";
			res.put("body", false);
			if (body.get("idcard") != null && !body.get("idcard").equals("")) {
				String idcard = body.get("idcard") + "";
				String jgid = UserRoleToken.getCurrent().getManageUnit()
						.getId();
				String param = "<root><idCard>" + idcard + "</idCard><unitId>"
						+ jgid + "</unitId></root>";
				BSXmlWsEntryClassService service = new BSXmlWsEntryClassService();
				BSXmlWsEntryClass portType = service.getBSXmlWsEntryClassPort();
				strXml = portType.invoke("GetUpRecord", "", "", param);
				if (result != null && !result.equals("")) {
					Document document = DocumentHelper.parseText(strXml);
					Element root = document.getRootElement();
					Element xmlMsg = root.element("xmlMsg");
					Element row = xmlMsg.element("row");
					if (row != null) {
						Element szTime = row.element("SZTIME");
						Element yyName = row.element("YYNAME");
						String strSzTime = szTime.getTextTrim();
						String strYyName = yyName.getTextTrim();
						result.put("szTime", strSzTime);
						result.put("yyName", strYyName);
					}
				}
			}
			System.out.print("上转:查询该用户记录:doGetUpRecord:" + strXml);
		}
		res.put("body", result);		
	}
	
	private String getHMS_SWITCH() throws IOException{
		Properties pro = new Properties();
		InputStream is = HmsInterfaceService.class.getClassLoader()
				.getResourceAsStream("wsSwitch.properties");
		pro.load(is);
		return pro.getProperty("HMS_SWITCH").trim();
	}
}
