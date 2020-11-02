/**
 * @(#)AreaGridModel.java Created on 2012-2-6 上午11:18:13
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.scm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.IsFamily;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.controller.exception.ControllerException;
import ctd.dao.support.QueryContext;
import ctd.dao.support.QueryResult;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.net.rpc.Client;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.PyConverter;
import ctd.validator.ValidateException;
import edu.emory.mathcs.backport.java.util.Collections;
/**
 * @description
 * 
 * @author <a href="mailto:guolb@bsoft.com.cn">guol</a>
 */

public class ContractModel implements BSCHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(ContractModel.class);
	private BaseDAO dao;
	public ContractModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 获取服务项目信息
	 * 
	 * @param itemCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getNodeInfo(String itemCode)
			throws ModelDataOperationException {
		Map<String,Object> datas = null;
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "itemCode", "s",
				itemCode);
		try {
			 datas = dao.doLoad(cnd, SCM_ServiceItems);
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取节点下的服务项目", e1);
		}
		return datas;
	}
	/**
	 * 获取服务项目子节点信息
	 * 
	 * @param itemCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getchildNodeInfo(String itemCode,List<?> cnd)
			throws ModelDataOperationException {
		List<Map<String, Object>> datas = null;
		try {
			 datas = dao.doQuery(cnd, null, SCM_ServiceItems);
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取父节点节点下的服务项目", e1);
		}
		return datas;
	}
	/**
	 * 用于获取新增节点编码
	 * 
	 * @param parentCode
	 * @param nums
	 *            需要多少个ID
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException 
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getNewIds(String parentCode, String itemType, int nums)
			throws ModelDataOperationException, ServiceException {
		int codeLen = 3;
		int min = -1;
		int max = -1;//
		Map<String, Integer> limit = getLimit(itemType);
		if (limit != null) {
			min = limit.get("min");
			max = limit.get("max");
			codeLen = String.valueOf(max).length();
		}else{
			throw new ServiceException("项目类型存在异常，无法正常保存");
		}
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "parentCode", "s",
				parentCode);
		List<Map<String, Object>> recs;
		recs = getchildNodeInfo(parentCode,cnd);
		int size = 0;
		if (recs != null) {
			size = recs.size();
		}

		List<Integer> keys = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			String itemCode = ((String) recs.get(i).get("itemCode"))
					.replaceAll(parentCode, "");
			if (itemCode.length() == codeLen) {
				int v = Integer.parseInt(itemCode);
				if (min > -1) {
					if (v >= min && v <= max) {
						keys.add(v);
					}
				} else {
					keys.add(v);
				}
			}
		}
		Collections.sort(keys);
		List<String> ids = new ArrayList<String>();
		StringBuffer format = new StringBuffer("%1$0").append(codeLen).append(
				"d");
		int num = 0;
		if (min >= 0) {
			num = min - 1;
		}
		size = keys.size();
		// 空洞ID回收。
		for (int i = 0; i < size && ids.size() <= nums; i++) {
			num++;
			if (max >= 0 && num >= max) {
				throw new ModelDataOperationException("ID超过该层最大限制数:" + max);
			}
			if (num == keys.get(i)) {
				if (i == size - 1) {
					ids.add(String.format(format.toString(), ++num));
					break;
				}
				continue;
			}
			ids.add(String.format(format.toString(), num));
			i--;
		}
		int curSize = ids.size();
		for (int i = 0; i < nums - curSize; i++) {
			ids.add(String.format(format.toString(), ++num));
		}
		return ids;
	}
	public Map<String, Integer> getLimit(String itemType) {
		Map<String, Integer> res = new HashMap<String, Integer>();
		//编码规则为2,2,2,3
		int min = -1;
		int max = -1;
		if(itemType!=null){
			if(itemType.equals("1")){
				min = 1;
				max = 9;
			}
			if(itemType.equals("2")){
				min = 1;
				max = 99;
			}
			if(itemType.equals("3")){
				min = 1;
				max = 99;
			}
			if(itemType.equals("4")){
				min = 1;
				max = 999;
			}
		res.put("min", min);
		res.put("max", max);
		}
		return res;
	}

	public Map<String, Object> insertServiceItem(Map<String, Object> reqBody,
			String op) throws ModelDataOperationException, ValidateException {
		String itemType = (String) reqBody.get("itemType");
		int len = 0;
		String parentCode = (String) reqBody.get("parentCode");
		synchronized (this) {
			Map<String, Object> resBody;
			resBody = saveServiceItem(reqBody, op);
			return resBody;
		}
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveServiceItem(Map<String, Object> data, String op)
			throws ValidateException, ModelDataOperationException{
		Map<String,Object> result = new HashMap<String, Object>();
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "itemCode", "s",
				data.get("itemCode"));
		if(op.equals("create")){
			try {
				Map m =  dao.doLoad(cnd, SCM_ServiceItems);
				if( m != null){
					op = "update";
				}
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Map m;
		try {
			m = dao.doLoad(cnd, SCM_ServiceItems);
			if(m == null){
				 op ="create";
			}
		} catch (PersistentDataOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String itemName = (String) data.get("itemName");
		if(itemName!=null&&itemName.length()>0){
			String pyCode = PyConverter.getFirstLetter(itemName);
			data.put("pyCode", pyCode);
		}
		try {
			result = dao.doSave(op, SCM_ServiceItems, data, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "维护签约项目信息失败:"
							+ e.getMessage(), e);
		}
		return result;
	}
	public int deleteLikeItemCode(String itemCode)
			throws ModelDataOperationException {
		int code = 200;
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
			String count_hql = new StringBuffer("itemCode like :itemCode || '%'").toString();
			
		
			String hql = new StringBuffer("delete from ")
					.append("SCM_ServiceItems ")
					.append(" where itemCode like :itemCode || '%' ")
					.toString();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("itemCode", itemCode);
			try {
				long count = dao.doCount(SCM_ServicePackageItems, count_hql, parameters);
				if(count<1)
					dao.doUpdate(hql, parameters); 
				else 
//					System.out.println(count+" 已存在启用的项目,不允许删除");
					code = 9001;
					
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "删除节点失败", e);
			}
			return code;
		}


}
