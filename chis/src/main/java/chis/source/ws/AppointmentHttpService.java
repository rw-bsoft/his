package chis.source.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import ctd.util.AppContextHolder;

public class AppointmentHttpService extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
//		try {
//			request.setCharacterEncoding("utf-8");
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		String methodName = request.getParameter("method");
		response.setCharacterEncoding("GBK");
		if(methodName==null || "".equals(methodName)){
			System.out.println("接口有请求过来，但传参不对");
			try {
				PrintWriter out = response.getWriter();
				out.print("传参不对！");
				out.flush();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONObject req=new JSONObject();
		try {
			PrintWriter out = response.getWriter();
			Map<String, Object> paras = new HashMap<String, Object>();
			paras.clear();
			String resultStr = "";
			if("getHospitalAndDeptInfo".equals(methodName)){//获取医院，科室列表
				String version = request.getParameter("version");
				paras.put("version", version);
				resultStr = checkParamters(paras,response);
				if(resultStr.length()>0 && !"{}".equals(resultStr)){
					out.print(resultStr);
				}else{
					req.put("version", version);//版本号：时间yyyymmdd
					out.print(doGetHospitalAndDeptInfo(req.toString()));
				}
			}else if("getDoctorAndWorkList".equals(methodName)){//获取医生和排班列表
				String hospitalId = request.getParameter("hospitalId");
				String sectionId = request.getParameter("sectionId");
				paras.put("hospitalId", hospitalId);
				paras.put("sectionId", sectionId);
				resultStr = checkParamters(paras,response);
				if(resultStr.length()>0 && !"{}".equals(resultStr)){
					out.print(resultStr);
				}else{
					req.put("hospitalId", hospitalId);//医院id
					req.put("sectionId", sectionId);//科室id
					out.print(doGetDoctorAndWorkList(req.toString()));
				}
			}else if("appointment".equals(methodName) || "cancelAppointment".equals(methodName)){//預約、取消預約
				String doctorId = request.getParameter("doctorId");
//				String doctorName =  URLEncoder.encode(request.getParameter("doctorName"),"utf-8");
				String doctorName =  request.getParameter("doctorName");
				String hospitalId = request.getParameter("hospitalId");
//				String hospitalName =  URLEncoder.encode(request.getParameter("hospitalName"),"utf-8");
				String hospitalName =  request.getParameter("hospitalName");
				String sectionId = request.getParameter("sectionId");
//				String sectionName =  URLEncoder.encode(request.getParameter("sectionName"),"utf-8");
				String sectionName =  request.getParameter("sectionName");
				String startTime = request.getParameter("startTime");
				String endTime = request.getParameter("endTime");
//				String patientName =  URLEncoder.encode(request.getParameter("patientName"),"utf-8");
				String patientName =  request.getParameter("patientName");
				String patientCard = request.getParameter("patientCard");
//				paras.put("doctorId", doctorId);
//				paras.put("doctorName", doctorName);
				paras.put("hospitalId", hospitalId);
				paras.put("hospitalName", hospitalName);
				paras.put("sectionId", sectionId);
				paras.put("sectionName", sectionName);
				paras.put("startTime", startTime);
				paras.put("endTime", endTime);
				paras.put("patientName", patientName);
				paras.put("patientCard", patientCard);
				resultStr = checkParamters(paras,response);
				if(resultStr.length()>0 && !"{}".equals(resultStr)){
					out.print(resultStr);
				}else{
					req.put("doctorId", doctorId);//医生id
					req.put("doctorName", new String(doctorName.getBytes("iso8859-1"),"UTF-8"));//医生姓名
					req.put("hospitalId", hospitalId);//医院id
					req.put("hospitalName", new String(hospitalName.getBytes("iso8859-1"),"UTF-8"));//医院名称
					req.put("sectionId", sectionId);//科室id
					req.put("sectionName", new String(sectionName.getBytes("iso8859-1"),"UTF-8"));//科室名称
					req.put("startTime", startTime);//预约时间
					req.put("endTime", endTime);//预约截止时间
					req.put("patientName", new String(patientName.getBytes("iso8859-1"),"UTF-8"));//患者姓名
					req.put("patientCard", patientCard);//患者身份证号
					if("appointment".equals(methodName)){//預約
						out.print(doAppointment(req.toString()));
					}else if("cancelAppointment".equals(methodName)){//取消預約
						out.print(doCancelAppointment(req.toString()));
					}
				}
			}else if("getAppointmentList".equals(methodName)){//查询预约信息
				String patientCard = request.getParameter("patientCard");
				paras.put("patientCard", patientCard);
				resultStr = checkParamters(paras,response);
				if(resultStr.length()>0 && !"{}".equals(resultStr)){
					out.print(resultStr);
				}else{
					req.put("patientCard", patientCard);//患者身份证号
					out.print(doGetAppointmentList(req.toString()));
				}
			}
			out.flush();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		String methodName = request.getParameter("method");
		response.setCharacterEncoding("GBK");
		if(methodName==null || "".equals(methodName)){
			System.out.println("接口有请求过来，但传参不对");
			try {
				PrintWriter out = response.getWriter();
				out.print("传参不对！");
				out.flush();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONObject req=new JSONObject();
		try {
			PrintWriter out = response.getWriter();
			Map<String, Object> paras = new HashMap<String, Object>();
			paras.clear();
			String resultStr = "";
			if("getHospitalAndDeptInfo".equals(methodName)){//获取医院，科室列表
				String version = request.getParameter("version");
				paras.put("version", version);
				resultStr = checkParamters(paras,response);
				if(resultStr.length()>0 && !"{}".equals(resultStr)){
					out.print(resultStr);
				}else{
					req.put("version", version);//版本号：时间yyyymmdd
					out.print(doGetHospitalAndDeptInfo(req.toString()));
				}
			}else if("getDoctorAndWorkList".equals(methodName)){//获取医生和排班列表
				String hospitalId = request.getParameter("hospitalId");
				String sectionId = request.getParameter("sectionId");
				paras.put("hospitalId", hospitalId);
				paras.put("sectionId", sectionId);
				resultStr = checkParamters(paras,response);
				if(resultStr.length()>0 && !"{}".equals(resultStr)){
					out.print(resultStr);
				}else{
					req.put("hospitalId", hospitalId);//医院id
					req.put("sectionId", sectionId);//科室id
					out.print(doGetDoctorAndWorkList(req.toString()));
				}
			}else if("appointment".equals(methodName) || "cancelAppointment".equals(methodName)){//預約、取消預約
				String doctorId = request.getParameter("doctorId");
//				String doctorName =  URLEncoder.encode(request.getParameter("doctorName"),"utf-8");
				String doctorName =  request.getParameter("doctorName");
				String hospitalId = request.getParameter("hospitalId");
//				String hospitalName =  URLEncoder.encode(request.getParameter("hospitalName"),"utf-8");
				String hospitalName =  request.getParameter("hospitalName");
				String sectionId = request.getParameter("sectionId");
//				String sectionName =  URLEncoder.encode(request.getParameter("sectionName"),"utf-8");
				String sectionName =  request.getParameter("sectionName");
				String startTime = request.getParameter("startTime");
//				String patientName =  URLEncoder.encode(request.getParameter("patientName"),"utf-8");
				String patientName =  request.getParameter("patientName");
				String patientCard = request.getParameter("patientCard");
//				paras.put("doctorId", doctorId);
//				paras.put("doctorName", doctorName);
				paras.put("hospitalId", hospitalId);
				paras.put("hospitalName", hospitalName);
				paras.put("sectionId", sectionId);
				paras.put("sectionName", sectionName);
				paras.put("startTime", startTime);
				paras.put("patientName", patientName);
				paras.put("patientCard", patientCard);
				resultStr = checkParamters(paras,response);
				if(resultStr.length()>0 && !"{}".equals(resultStr)){
					out.print(resultStr);
				}else{
					req.put("doctorId", doctorId);//医生id
					req.put("doctorName", new String(doctorName.getBytes("iso8859-1"),"UTF-8"));//医生姓名
					req.put("hospitalId", hospitalId);//医院id
					req.put("hospitalName", new String(hospitalName.getBytes("iso8859-1"),"UTF-8"));//医院名称
					req.put("sectionId", sectionId);//科室id
					req.put("sectionName", new String(sectionName.getBytes("iso8859-1"),"UTF-8"));//科室名称
					req.put("startTime", startTime);//预约时间
					req.put("patientName", new String(patientName.getBytes("iso8859-1"),"UTF-8"));//患者姓名
					req.put("patientCard", patientCard);//患者身份证号
					if("appointment".equals(methodName)){//預約
						out.print(doAppointment(req.toString()));
					}else if("cancelAppointment".equals(methodName)){//取消預約
						out.print(doCancelAppointment(req.toString()));
					}
				}
			}else if("getAppointmentList".equals(methodName)){//查询预约信息
				String patientCard = request.getParameter("patientCard");
				paras.put("patientCard", patientCard);
				resultStr = checkParamters(paras,response);
				if(resultStr.length()>0 && !"{}".equals(resultStr)){
					out.print(resultStr);
				}else{
					req.put("patientCard", patientCard);//患者身份证号
					out.print(doGetAppointmentList(req.toString()));
				}
			}
			out.flush();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String doGetHospitalAndDeptInfo(String param) {
		AppointmentHttpImpl gs = (AppointmentHttpImpl) AppContextHolder.get()
				.getBean("appointmentService");
		return gs.doGetHospitalAndDeptInfo(param);
	}
	
	private String doGetDoctorAndWorkList(String param) {
		AppointmentHttpImpl gs = (AppointmentHttpImpl) AppContextHolder.get()
				.getBean("appointmentService");
		return gs.doGetDoctorAndWorkList(param);
	}

	private String doAppointment(String param) {
		AppointmentHttpImpl gs = (AppointmentHttpImpl) AppContextHolder.get()
				.getBean("appointmentService");
		return gs.doAppointment(param);
	}
	
	private String doCancelAppointment(String param) {
		AppointmentHttpImpl gs = (AppointmentHttpImpl) AppContextHolder.get()
				.getBean("appointmentService");
		return gs.doCancelAppointment(param);
	}
	
	private String doGetAppointmentList(String param) {
		AppointmentHttpImpl gs = (AppointmentHttpImpl) AppContextHolder.get()
				.getBean("appointmentService");
		return gs.doGetAppointmentList(param);
	}
	
	/**
	 * 检查参数
	 * @param paras
	 * @param response
	 */
	private String checkParamters(Map<String, Object> paras,HttpServletResponse response){
		JSONObject res=new JSONObject();
		if(paras.size()>0){
			Iterator<Map.Entry<String, Object>> entries = paras.entrySet().iterator();
			while (entries.hasNext()) {
			    Map.Entry<String, Object> entry = entries.next();
			    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			    if(entry.getValue()==null || "".equals(entry.getValue())){
					System.out.println("接口有请求过来，但传参不对");
					try {
//						PrintWriter writer = response.getWriter();
//						writer.print("传参"+entry.getKey()+"为空！");
//						writer.flush();
						res.put("result","0");
						res.put("errMsg","参数"+entry.getKey()+"为空！");
						res.put("data","{}");
						return res.toString();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return res.toString();
	}
}
