package phis.application.ehr.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.service.core.Service;
import org.json.JSONArray;
import org.json.JSONException; 
import phis.application.ehr.source.DESedeUtil;
import phis.application.ehr.source.DESUtil;

//【浦口】调用大数据健康档案浏览器接口服务传参加密规则，用于对sign进行加密的规则 zhaojian 2017-10-25
public class DESedeService  extends AbstractActionService
implements DAOSupportable {

	protected Logger logger = LoggerFactory
			.getLogger(DESedeService.class);
	/**
	 * 获取加密信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	public Map<String, Object> doGetDesInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		
		int code = 200;
		String msg = "Success";
		try {
			String params = (String)req.get("params");
			JSONArray jsonArray = new JSONArray(params);
			//key
			String key = "17780c4e4e376e40f1e5d7ec4916e44f";
			//openid
			String openid = "bsoft0001";
			//openkey
			String openkey ="QnNvZnQwMDAxQ1pX";
			for(int i=0;i<params.split("}").length-1;i++){
				String name =(String) jsonArray.getJSONObject(i).get("name");
				String value =(String) jsonArray.getJSONObject(i).get("value");
				//加密
				if(!name.equals("sys_organ_code") && !name.equals("sys_code")){
					value = new DESedeUtil(key).jiami(value);
				}
				res.put(name,value);
			}
			//时间戳
			String timestamp = String.valueOf(System.currentTimeMillis());
			//加密sign
			String sign = new DESUtil(openkey).encryptStr(timestamp+";"+openid);//时间戳与openid以分号拼接，然后使用openKey为Base64加盐，再对结合后的字符串加密。
			res.put("openid",openid);
			res.put("timestamp",timestamp);
			res.put("sign",sign);
			res.put(Service.RES_CODE,code);
			res.put(Service.RES_MESSAGE, msg);
			return res;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code = 501;
			msg = "DataAccessException:" + e.getMessage();
			res.put(Service.RES_CODE,code);
			res.put(Service.RES_MESSAGE,msg);
			return res;
		}
	}
}
