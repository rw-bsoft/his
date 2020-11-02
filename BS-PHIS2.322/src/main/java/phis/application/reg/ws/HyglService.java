package phis.application.reg.ws;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.mobile.JSONHelper;
import phis.source.security.DESCoder;
import phis.source.security.EncryptException;
import phis.source.security.EncryptUtil;
import phis.source.security.RSA;
import phis.source.ws.AbstractWsService;
import ctd.service.core.ServiceException;
import ctd.util.context.ContextUtils;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class HyglService extends AbstractWsService{
	private static final Log logger = LogFactory
			.getLog(HyglService.class);
//	/**
//	 * 查询/保存病人档案信息
//	 * @author caijy
//	 * @createDate 2016-10-24
//	 * @description 
//	 * @updateInfo
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	protected void doSaveBrxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
//		HyglModel model = new HyglModel(dao);
//		Map<String,Object> body=(Map<String,Object>)req.get("data");
//		try {
//			 long brid=model.saveBrxx(body,ctx);
//			 res.put("data", brid);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
//	/**
//	 * 根据机构代码查询开启预约的科室列表
//	 * @author caijy
//	 * @createDate 2016-10-24
//	 * @description 
//	 * @updateInfo
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	protected void doQueryGhks(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
//		HyglModel model = new HyglModel(dao);
//		Map<String,Object> body=(Map<String,Object>)req.get("data");
//		try {
//			List<Map<String,Object>> l= model.queryGhks(body,ctx);
//			res.put("data",l);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
//	/**
//	 * 根据机构和科室 查询预约医生信息
//	 * @author caijy
//	 * @createDate 2016-10-24
//	 * @description 
//	 * @updateInfo
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	protected void doQueryYsxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
//		HyglModel model = new HyglModel(dao);
//		Map<String,Object> body=(Map<String,Object>)req.get("data");
//		try {
//			List<Map<String,Object>> l= model.queryYsxx(body,ctx);
//			res.put("data",l);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
//	/**
//	 * 查询基础数据(字典数据查询)
//	 * @author caijy
//	 * @createDate 2016-10-24
//	 * @description 
//	 * @updateInfo
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	protected void doQueryJcsj(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
//		HyglModel model = new HyglModel(dao);
//		try {
//			Map<String,Map<String,Object>> m= model.queryJcsj();
//			res.put("data",m);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
	/**
	 * 挂号预约
	 * @author caijy
	 * @createDate 2016-10-25
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public String doSaveYygh(HashMap<String,Object> yyghxx) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		HyglModel model = new HyglModel();
		JSONObject jsonResPKS = new JSONObject();
		try {
			model.saveGhyy(yyghxx);
			res.put(RES_CODE, 200);
			res.put(RES_MESSAGE, "succeeded.");
			logger.info("doSaveYygh succeeded!");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			logger.error(e.getMessage(), e);
			try {
				jsonResPKS = JSONHelper.map2JSONObject(res);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return jsonResPKS.toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jsonResPKS = JSONHelper.map2JSONObject(res);
		} catch (org.json.JSONException e) {
			logger.error("Failed to encoded doSaveYyqx response data.",
					e);
		}
		System.out.println(jsonResPKS.toString());
		return jsonResPKS.toString();
	}
//	/**
//	 * 查询号源信息
//	 * @author caijy
//	 * @createDate 2016-10-25
//	 * @description 
//	 * @updateInfo
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	protected void doQueryHy(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
//		HyglModel model = new HyglModel(dao);
//		Map<String,Object> data=(Map<String,Object>)req.get("data");
//		Map<String,Object> body=new HashMap<String,Object>();
//		for(String key:data.keySet()){//小写转大写
//			body.put(key.toUpperCase(), data.get(key));
//		}
//		body.put("DSFYY", 1);
//		req.put("body", body);
//		try {
//			model.queryKsHy(req,res,ctx);
//		} catch (ModelDataOperationException e) {
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
//	}
	/**
	 * 取消预约
	 * @author caijy
	 * @createDate 2016-10-25
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public String doSaveYyqx(long sbxh,String brid) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		HyglModel model = new HyglModel();
		JSONObject jsonResPKS = new JSONObject();
		try {
			model.saveYyqx(sbxh,brid);
			res.put(RES_CODE, 200);
			res.put(RES_MESSAGE, "succeeded.");
			logger.info("doSaveYyqx succeeded!");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			logger.error(e.getMessage(), e);
			try {
				jsonResPKS = JSONHelper.map2JSONObject(res);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return jsonResPKS.toString();
		}
		try {
			jsonResPKS = JSONHelper.map2JSONObject(res);
		} catch (JSONException e) {
			logger.error("Failed to encoded doSaveYyqx response data.",
					e);
		}
		return jsonResPKS.toString();
	}
//	/**
//	 * 1.获取各个科室的预约天数，计算日期
//	 * 2.与本地记录做比较，哪些日期该上传却没有上传
//	 * 3.上传对应日期的科室排班信息
//	 * 4.记录各个科室上传了的日期
//	 * 5.删除日期记录表今天以前的冗余信息
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ParseException 
//	 * @throws ModelDataOperationException 
//	 */
//	public void doSynchor(Map req, Map res, BaseDAO dao, Context ctx) throws ParseException, ModelDataOperationException {
//		HyglModel model = new HyglModel(dao);
//		model.doSynchorAll(req,res,dao,ctx);
//	}
//	public void doDownLoadYYInfo(Map req, Map res, BaseDAO dao, Context ctx){
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String jgid = user.getManageUnit().getId();// 用户的机构ID
//		HyglModel model = new HyglModel(dao);
//		try {
//			model.DownLoadYYInfo(jgid, ctx);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	@SuppressWarnings("unchecked")
	@Override
	@WebMethod
	public String execute(String request) {
		// TODO Auto-generated method stub
		logger.info("Received request data[" + request + "].");
		HashMap<String, Object> jsonRes = new HashMap<String, Object>();
		String serviceId = null;
		String serviceAction = null;
		try {
			jsonRes.put(RES_CODE, Constants.CODE_OK);
			String requestData = request;
			if (!request.contains("serviceId")) {
				requestData = decodeRequest(request);
				logger.info("decodeRequest  data[" + requestData + "].");
			}
			if(!"".equals(requestData)){
				requestData=requestData.replaceAll("[\\t\\n\\r]", "%br");
			}
			System.out.println(requestData);
			Map<String, Object> jsonReq = new HashMap<String, Object>();
			JSONObject jsonObject = new JSONObject(requestData);
			jsonReq = JSONHelper.jsonObject2Map(jsonObject, jsonReq);
			serviceId = (String) jsonReq.get("serviceId");
			serviceAction = (String) jsonReq.get("serviceAction");
			HashMap<String,Object> req = (HashMap<String,Object>) jsonReq.get("data");
			jsonRes.put("serviceId", serviceId);
			jsonRes.put("serviceAction", serviceAction);
			if(serviceId.equals("phis.hyglService")){
				if (serviceAction.equals("saveYygh")){
					return doSaveYygh(req);
				}else if(serviceAction.equals("saveYyqx")){
					Long sbxh = Long.parseLong(req.get("sbxh")+"");
					String brid = req.get("brid")+"";
					return doSaveYyqx(sbxh,brid);
				}
			}else{
				jsonRes.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
				jsonRes.put(RES_MESSAGE, "serviceId is wrong.");
			}
		}catch (Exception e) {
			logger.error("Failed to operate on json object.", e);
			jsonRes.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			jsonRes.put(RES_MESSAGE, "Failed to operate on json object.");
		} finally {
			ContextUtils.clear();
		}
		JSONObject jsonResPKS = new JSONObject();
		try {
			jsonResPKS = JSONHelper.map2JSONObject(jsonRes);
		} catch (org.json.JSONException e) {
			logger.error("Failed to encoded execute response data.",
					e);
		}
		return jsonResPKS.toString();
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
