/**
 * @(#)AreaGridModel.java Created on 2012-2-6 上午11:18:13
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.IsFamily;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;

import com.alibaba.fastjson.JSONException;

import ctd.controller.exception.ControllerException;
import ctd.dao.support.QueryContext;
import ctd.dao.support.QueryResult;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.net.rpc.Client;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class AreaGridModel implements BSCHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(AreaGridModel.class);
	private BaseDAO dao;
	public static final int QULENGTH = 6;
	public static final int SHILENGTH = 4;
	public static final int SHINODE = 4;
	public static final int QUNODE = 2;
	public static final int ABOVEFAMILY = 3;
	public static final int FAMILYNODE = 4;

	public AreaGridModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取该节点自身的长度
	 * 
	 * @param regionCode
	 * @param isFamily
	 * @return
	 */
	public int getLength(String regionCode, String isFamily) {
		if (isFamily.equals(IsFamily.FAMILY))
			return FAMILYNODE;
		else if (regionCode.length() == QULENGTH)
			return QUNODE;
		else if (regionCode.length() == SHILENGTH)
			return SHINODE;
		else
			return ABOVEFAMILY;
	}

	/**
	 * 核对是否具有操作权限
	 * 
	 * @param regionCode
	 * @param userRegionCode
	 * @return
	 */
	public boolean checkRegionCode(String regionCode, String userRegionCode) {
		int length = userRegionCode.length();
		String subRegionCode = regionCode.substring(0, length);
		if (!userRegionCode.equals(subRegionCode)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取用户的网格地址
	 * 
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getUserRegionCode(Context ctx)
			throws ModelDataOperationException {
		String regionCode = UserUtil.get(UserUtil.REGION_CODE);
		if (regionCode == null || regionCode.trim().length() == 0) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_SERVICE_ERROR, "用户网格地址为空,无法进行网格地址操作");
		}
		return regionCode;
	}

	/**
	 * 根据网格地址获取完整的上层节点路径
	 * 
	 * @param regionCode
	 * @param isFamily
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String getParentFullPath(String regionCode, String isFamily)
			throws ModelDataOperationException {

		String partCode = regionCode;
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		StringBuffer cnd = new StringBuffer("");
		while (partCode.length() > 4) {
			int codeLength = getLength(partCode, isFamily);
			int sumLength = partCode.length();
			partCode = partCode.substring(0, sumLength - codeLength);
			if (codeLength == FAMILYNODE && isFamily.equals(IsFamily.FAMILY)) {
				isFamily = IsFamily.NOTFAMILY;
			}
			StringBuffer tmp = new StringBuffer(
					"['eq',['$','regionCode'],['s','").append(partCode).append(
					"']]");
			if (partCode.length() > SHILENGTH) {
				if (cnd.length() == 0) {
					cnd.append(tmp);
					continue;
				}
				cnd.insert(0, "['or',").append(",").append(tmp).append("]");
			}
		}
		StringBuffer path = new StringBuffer();
		try {
			QueryContext qc = new QueryContext();
			qc.setOrderBy(" length(regionCode) asc");
			Object[] paras = new Object[] { "area",
					CNDHelper.toListCnd(cnd.toString()), qc };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			List<Map<String, Object>> list = qr.getItems();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					path.append(list.get(i).get("regionName")).append("/");
				}
			}
		} catch (Exception e) {
			logger.error("failed to get full path from area.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取上层节点路径失败");
		}
		return path.toString();
	}

	/**
	 * 获取网格地址下这责任医生数量
	 * 
	 * @param regioncode
	 * @return
	 */
	public long countManaDoctor(String regioncode) {
		String hql = new StringBuffer("select count(distinct manadoctor) from ")
				.append("EHR_AreaGridChild").append(" where regioncode like '")
				.append(regioncode).append("%'").toString();
		long count = 0;
		try {
			Map<String, Object> map = dao.doLoad(hql,
					new HashMap<String, Object>());
			count = (Long) map.get("0");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 判断网格地址及其子节点是否已经被使用
	 * 
	 * @param regionCode
	 *            'eq' or 'like' 只查当前节点或者该节点下所有节点.
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public boolean checkRegionCodeInUse(String regionCode, String op,
			boolean containSystemUser) throws ModelDataOperationException {
		String opString = " =:regionCode ";
		if ("like".equals(op)) {
			opString = " like :regionCode || '%' ";
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("regionCode", regionCode);
		try {
			if (containSystemUser) {
				// 修改为rpc服务
				HashMap<String, Object> query = new HashMap<String, Object>();
				query.put("regionCode", regionCode);
				query.put("logoff", "1");
				try {
					List<Map<String, Object>> rs = (List<Map<String, Object>>) Client
							.rpcInvoke(AppContextHolder
									.getConfigServiceId("userLoader"), "find",
									query);
					if (rs != null && rs.size() > 0) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// // 判断 若改层网格地址已经被维护到用户角色属性中
				// String userHql = new StringBuffer("select count(*) from ")
				// .append(SYS_UserProp).append(" where regionCode")
				// .append(opString).toString();
				// long userCount = (Long) dao.doLoad(userHql, parameters)
				// .get("0");
				// if (userCount > 0) {
				// return true;
				// }
			}
			// 判断该regioncode是否被健康档案使用
			String hldc = new StringBuffer("select count(*) from  ")
					.append("EHR_HealthRecord").append(" where regionCode ")
					.append(opString).toString();
			long count = (Long) dao.doLoad(hldc, parameters).get("0");
			if (count > 0) {
				return true;
			}
			// 判断该regioncode是否被家庭档案使用
			String fmc = new StringBuffer("select count(*) from  ")
					.append("EHR_FamilyRecord").append(" where regionCode ")
					.append(opString).toString();
			long fcount = (Long) dao.doLoad(fmc, parameters).get("0");
			logger.info(fcount + " records found from familyRecord .");
			if (fcount > 0) {
				return true;
			}
			// 查询妇保档案是否使用网格地址。
			String mhc = new StringBuffer("select count(*) from ")
					.append("MHC_PregnantRecord")
					.append(" where restRegionCode ").append(opString)
					.append(" or homeAddress ").append(opString).toString();
			long mhcCount = (Long) dao.doLoad(mhc, parameters).get("0");
			if (mhcCount > 0) {
				return true;
			}
			// 查询儿童档案是否使用
			String cdh = new StringBuffer("select count(*) from ")
					.append("CDH_HealthCard").append(" where  homeAddress ")
					.append(opString).toString();
			long cdhCount = (Long) dao.doLoad(cdh, parameters).get("0");
			if (cdhCount > 0) {
				return true;
			}

			// 精神病
			String psy = new StringBuffer("select count(*) from ")
					.append("PSY_PsychosisRecord")
					.append(" where  guardianAddress ").append(opString)
					.toString();
			long psyCount = (Long) dao.doLoad(psy, parameters).get("0");
			if (psyCount > 0) {
				return true;
			}

			// 群宴
			String gdr = new StringBuffer("select count(*) from ")
					.append("GDR_GroupDinnerRecord")
					.append(" where  regionCode ").append(opString).toString();
			long gdrCount = (Long) dao.doLoad(gdr, parameters).get("0");
			if (gdrCount > 0) {
				return true;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Check RegionCode in use failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "判断网格地址及其子节点是否已经被使用失败", e);
		}
		return false;
	}

	/**
	 * 查询关联子节点
	 * 
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLikeRegionCode(String regionCode)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		List<Map<String, Object>> recs;
		List<String> reList = new ArrayList<String>();
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "parentCode", "s",
				regionCode);
		QueryContext qc = new QueryContext();
		try {
			Object[] paras = new Object[] { "area", cnd, qc };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs = qr.getItems();
			for (Map<String, Object> m : recs) {
				reList.add((String) m.get("regionCode"));
			}
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务查询关联子节点失败", e1);
		}
		return reList;
	}

	public void deleteLikeRegionCode(String regionCode)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		StringBuffer cnd = new StringBuffer(
				"['like',['$','regionCode'],['concat',['s','").append(
				regionCode).append("'],['s','%']]]");
		int rpcCount = 0;
		try {
			Object[] paras = new Object[] { "area",
					CNDHelper.toListCnd(cnd.toString()) };
			rpcCount = (Integer) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"),
					"removeByCnds", paras, header);
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务删除网格地址失败", e1);
		}
		if (rpcCount > 0) {
			String hql = new StringBuffer("delete from ")
					.append("EHR_AreaGridChild")
					.append(" where regionCode like :regionCode || '%' ")
					.toString();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("regionCode", regionCode);
			try {
				dao.doUpdate(hql, parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "删除网格地址失败", e);
			}
		}

	}

	/**
	 * 判断是否存在子节点
	 * 
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public boolean haveChildNodes(String regionCode)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		StringBuffer cnd = new StringBuffer("['eq',['$','parentCode'],['s','")
				.append(regionCode).append("']]");
		QueryContext qc = new QueryContext();
		try {
			Object[] paras = new Object[] { "area",
					CNDHelper.toListCnd(cnd.toString()), qc };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			if (qr != null && qr.getItems() != null && qr.getItems().size() > 0) {
				return true;
			}
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务查询网格地址失败", e1);
		}
		return false;
	}

	public void updateNodeIsBottom(String regionCode, String bottomNode)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("regionCode", regionCode);
		map.put("isBottom", bottomNode);
		try {
			Object[] paras = new Object[] { "area", map };
			Client.rpcInvoke(AppContextHolder.getConfigServiceId("daoService"),
					"update", paras, header);
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务查询网格地址失败", e1);
		}
	}

	/**
	 * 获取网格地址信息
	 * 
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getNodeInfo(String regionCode)
			throws ModelDataOperationException {
		// 增加RPC服务
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		Map<String, Object> nodeMap;
		try {
			Object[] paras = new Object[] { "area", regionCode };
			nodeMap = (Map<String, Object>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "get",
					paras, header);
			Map<String, Object> datas = dao.doLoad(EHR_AreaGridChild,
					regionCode);
			if (nodeMap != null && datas != null) {
				nodeMap.putAll(datas);
			}
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务查询网格地址失败", e1);
		}
		return nodeMap;
	}

	/**
	 * 删除节点
	 * 
	 * @param regionCode
	 * @throws ModelDataOperationException
	 */
	public void deleteAreaGrid(String regionCode)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		try {
			Object[] paras = new Object[] { "area", regionCode };
			Client.rpcInvoke(AppContextHolder.getConfigServiceId("daoService"),
					"remove", paras, header);
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务删除网格地址节点失败", e1);
		}

		String hql = new StringBuffer("delete from ")
				.append("EHR_AreaGridChild")
				.append(" where regionCode = :regionCode ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("regionCode", regionCode);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除网格地址节点失败", e);
		}
	}

	public Map<String, Object> insertAreaGrid(Map<String, Object> reqBody,
			String op) throws ModelDataOperationException, ValidateException {
		String isFamily = (String) reqBody.get("isFamily");
		int len = 0;
		String parentCode = (String) reqBody.get("parentCode");
		len = getLength("", isFamily);
		synchronized (this) {
			Map<String, Object> resBody;
			// 批量生成时 ID从service层传入。
			String nextId = (String) reqBody.get("regionNo");
			if (nextId == null || nextId.trim().length() == 0) {
				nextId = this.getNewId(parentCode, isFamily);
				if (nextId.length() > len) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_INVALID_REQUEST, "网格地址超出最大值限制:["
									+ parentCode + nextId + "]");
				}
			}
			nextId = parentCode + nextId;
			reqBody.put("regionCode", nextId);
			// @@ 判断是否有同名结点 --update by CHENXR
			String regionName = StringUtils.trimToEmpty((String) reqBody
					.get("regionName"));
			int plen = getLength(nextId, isFamily);
			if (regionNameExitsOnLayer(null, parentCode, plen, regionName,
					isFamily)) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_INVALID_REQUEST, "该层同名网格地址已经存在:"
								+ regionName);
			}
			resBody = saveAreaGrid(reqBody, op);
			resBody.put("regionCode", nextId);
			return resBody;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> saveAreaGrid(Map<String, Object> data, String op)
			throws ValidateException, ModelDataOperationException{
		// 增加RPC服务
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		Map<String, Object> genValues;
		try {
			Object[] paras = new Object[] { "area", data };
			genValues = (Map<String, Object>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"),
					"saveOrUpdate", paras, header);
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务保存网格地址失败", e1);
		}
		if(op.equals("create")){
			try {
				Map m = dao.doLoad(BSCHISEntryNames.EHR_AreaGridChild, (String)data.get("regionCode"));
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
			m = dao.doLoad(EHR_AreaGridChild, (String)data.get("regionCode"));
			if(m == null){
				 op ="create";
			}
		} catch (PersistentDataOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			genValues.putAll(dao.doSave(op, EHR_AreaGridChild, data, false));
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存网格地址信息失败:"
							+ e.getMessage(), e);
		}
		return genValues;
	}

	/**
	 * 当父节点编码发生变化，更新自己点编码以及parentCode字段
	 * 
	 * @param oldParent
	 * @param newParent
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public void updateChildrenRegionCode(String oldParent, String newParent)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		List<Map<String, Object>> recs;
		StringBuffer cnd = new StringBuffer(
				"['like',['$','regionCode'],['concat',['$','")
				.append(oldParent).append("'],['s','%']]]");
		try {
			QueryContext qc = new QueryContext();
			Object[] paras = new Object[] { "area", cnd, qc };

			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs = qr.getItems();
			for (Map<String, Object> regionInfo : recs) {
				HashMap<String,Object> o = new HashMap<String,Object>();
				o.put("regionCode", newParent);
				StringBuffer s = new StringBuffer(
						"['eq',['$','regoionCode'],['s','").append(oldParent)
						.append("']]");
				Object[] p = new Object[] { "area", o,
						CNDHelper.toListCnd(s.toString()) };
				Client.rpcInvoke(
						AppContextHolder.getConfigServiceId("daoService"),
						"update", p, header);
			}
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务查询网格地址失败", e1);
		}

		try {
			Map<String, Object> m1 = new HashMap<String, Object>();
			m1.put("parentCode", " substr(regionCode,1,length(regioncode)-3)");
			StringBuffer cnd1 = new StringBuffer(
					"['and',['ne',['$','isFamily'],['s','").append(
					IsFamily.FAMILY).append("']],");
			cnd1.append("['like',['$','regionCode'],['concat',['$','")
					.append(newParent).append("'],['s','%']]]").append("]");
			Object[] p1 = new Object[] { "area",
					CNDHelper.toListCnd(cnd1.toString()) };
			Map<String, Object> m2 = new HashMap<String, Object>();
			m2.put("parentCode", " substr(regionCode,1,length(regioncode)-4)");
			StringBuffer cnd2 = new StringBuffer(
					"['and',['ne',['$','isFamily'],['s','").append(
					IsFamily.FAMILY).append("']],");
			cnd1.append("['like',['$','regionCode'],['concat',['$','")
					.append(newParent).append("'],['s','%']]]").append("]");
			Object[] p2 = new Object[] { "area",
					CNDHelper.toListCnd(cnd2.toString()) };
			Client.rpcInvoke(AppContextHolder.getConfigServiceId("daoService"),
					"update", p1, header);
			Client.rpcInvoke(AppContextHolder.getConfigServiceId("daoService"),
					"update", p2, header);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 修改网格地址的 层次属性 之后 更新其所有子结点的 网格地址编码（regionCode）的值
	 * 
	 * @param parentCode
	 * @param regionCode
	 * @param regionName
	 * @throws ModelDataOperationException
	 */
	public void updateChildNodeRegionCode(String newId, String regionCode)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		// 修改
		if (StringUtils.isNotEmpty(newId)) {
			// 取出其所有子结点
			List<String> eagList = getLikeRegionCode(regionCode);
			// 循环执行修操作
			for (String oldRegionCode : eagList) {
				String newRegionCode = newId
						+ oldRegionCode.substring(regionCode.length());
				HashMap<String, Object> o = new HashMap<String,Object>();
				o.put("regionCode", newRegionCode);
				StringBuffer s = new StringBuffer(
						"['eq',['$','regoionCode'],['s','").append(
						oldRegionCode).append("']]");
				try {
					Object[] p = new Object[] { "area", o,
							CNDHelper.toListCnd(s.toString()) };
					Client.rpcInvoke(
							AppContextHolder.getConfigServiceId("daoService"),
							"update", p, header);
				} catch (Exception e) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "RPC服务更新子结点失败", e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private int getMaxSubId(String parentCode, int len)
			throws ModelDataOperationException {
		StringBuffer like = new StringBuffer();
		for (int i = 0; i < len; i++) {
			like.append("_");
		}
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		List<String> fields = new ArrayList<String>();
		fields.add("max(regionCode)");
		StringBuffer cnd = new StringBuffer(
				"['like',['$','regionCode'],['concat',['$','")
				.append(parentCode).append("'],['s','").append(like)
				.append("']]]");
		List<Map<String, Object>> recs;
		Map<String, Object> map;
		try {
			QueryContext qc = new QueryContext();
			Object[] paras = new Object[] { "area", fields, cnd, qc };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs = qr.getItems();
			map = (Map<String, Object>) recs.get(0);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC查询网格地址信息失败", e);
		}
		String maxSubId = (String) map.get("0");
		maxSubId = maxSubId.substring(maxSubId.length() - len);
		return Integer.parseInt(maxSubId);
	}

	/**
	 * 根据最大ID生成下一个ID。
	 * 
	 * @param maxSubId
	 * @param len
	 * @return
	 * @throws JSONException
	 */
	private String getNextId(int maxSubId, int len) {
		StringBuffer format = new StringBuffer("%1$0").append(len).append("d");
		return String.format(format.toString(), maxSubId);
	}

	@SuppressWarnings("unchecked")
	public boolean regionNameExitsOnLayer(String regionCode, String parentCode,
			int len, String regionName, String isFamily)
			throws ModelDataOperationException {
		StringBuffer key = new StringBuffer(parentCode);
		for (int i = 0; i < len; i++) {
			key.append("_");
		}
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		StringBuffer cnd = new StringBuffer("['like',['$','regionCode'],['s','")
				.append(key.toString()).append("']]");
		cnd.insert(0, "['and',").append(",['eq',['$','regionName'],['s','")
				.append(regionName).append("']]]");
		if (regionCode != null && regionCode.trim().length() > 0) {
			cnd.insert(0, "['and',").append(",['ne',['$','regionCode'],['s','")
					.append(regionCode).append("']]]");
		}
		if (StringUtils.isNotBlank(isFamily)) {
			cnd.insert(0, "['and',").append(",['eq',['$','isFamily'],['s','")
					.append(isFamily).append("']]]");
		}
		try {
			Object[] paras = new Object[] { "area",
					CNDHelper.toListCnd(cnd.toString()), new QueryContext() };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			if (qr.getItems() != null && qr.getItems().size() > 0) {
				return true;
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC网格地址信息失败", e);
		}
		return false;
	}

	public boolean checkBeforeInsertAreaGrid(String parentCode,
			String comeFrom, Context ctx) throws ModelDataOperationException {
		// 如果该网格地址不是用户/管辖范围内 不允许新增
		String userRegionCode = getUserRegionCode(ctx);
		if (false == checkRegionCode(parentCode, userRegionCode)) {
			if (StringUtils.isNotEmpty(comeFrom) && "update".equals(comeFrom)) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_INVALID_REQUEST,
						"错误的请求：无权修改网格地址！请与中心网管联系");
			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_INVALID_REQUEST,
						"错误的请求：无权新建网格地址！请与中心网管联系");
			}
		}

		// 判断父节点是否已经被使用
		if (true == checkRegionCodeInUse(parentCode, "eq", false)) {
			if (StringUtils.isNotEmpty(comeFrom) && "update".equals(comeFrom)) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_INVALID_REQUEST,
						"错误的请求：网格地址父节点已经被使用，不能修改子节点！");
			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_INVALID_REQUEST,
						"错误的请求：网格地址父节点已经被使用，不能新建子节点！");
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public String GetIsFamily(String regionCode)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		try {
			Object[] paras = new Object[] { "area", regionCode };
			Map map =  (Map) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "get",
					paras, header);
			if (map != null ) {
				Map<String, Object> nodeMap = (Map<String, Object>) Client
						.rpcInvoke(AppContextHolder
								.getConfigServiceId("daoService"), "get",
								paras, header);
				if (nodeMap != null && !nodeMap.isEmpty()) {
					return (String) nodeMap.get("isFamily");
				}
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC查询网格地址子节点失败", e);
		}
		return null;
	}

	public Map<String, Integer> getLimit(String isFamily) {
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get(
					"chis.dictionary.isFamily");
		} catch (ControllerException e) {
			return null;
		}
		DictionaryItem dicItem = dic.getItem(isFamily);
		String limitStr = (String) dicItem.getProperty("limit");
		if (limitStr == null || limitStr.trim().length() == 0) {
			return null;
		}
		String[] limitInfo = limitStr.split("-");
		Map<String, Integer> res = new HashMap<String, Integer>();
		res.put("min", Integer.parseInt(limitInfo[0]));
		res.put("max", Integer.parseInt(limitInfo[1]));
		return res;
	}

	/**
	 * 用于获取新增节点编码
	 * 
	 * @param parentCode
	 * @param nums
	 *            需要多少个ID
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getNewIds(String parentCode, String isFamily, int nums)
			throws ModelDataOperationException {
		int codeLen = 3;
		int min = -1;
		int max = -1;// 网格地址编码的最大值限制
		int pageNo = 1;
		int pageSize = 9999;//
		Map<String, Integer> limit = getLimit(isFamily);
		if (limit != null) {
			min = limit.get("min");
			max = limit.get("max");
		} else {
			max = 999;
		}
		if (IsFamily.FAMILY.equals(isFamily)) {
			codeLen = 4;
			max = 9999;
		}
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "parentCode", "s",
				parentCode);
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		List<Map<String, Object>> recs;
		QueryContext qc = new QueryContext();
		qc.setPageNo((Integer)pageNo);
		qc.setPageSize((Integer)pageSize);
		try {
			Object[] paras = new Object[] { "area", cnd, qc };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			recs = qr.getItems();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务查询网格地址失败", e1);
		}
		int size = 0;
		if (recs != null) {
			size = recs.size();
		}

		List<Integer> keys = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			String regionCode = ((String) recs.get(i).get("regionCode"))
					.replaceAll(parentCode, "");
			if (regionCode.length() == codeLen) {
				int v = Integer.parseInt(regionCode);
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

	/**
	 * 获取单个新ID
	 * 
	 * @param regionCode
	 * @param isFamily
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public String getNewId(String regionCode, String isFamily)
			throws ModelDataOperationException {
		List<String> ids = getNewIds(regionCode, isFamily, 1);
		return ids.get(0);
	}

	/**
	 * 更新所有的子节点的医生护士信息。
	 * 
	 * @param regionCode
	 * @param fields
	 * @throws ModelDataOperationException
	 */
	public void updateDocInfo(String regionCode, Map<String, String> fields)
			throws ModelDataOperationException {
		if (regionCode == null || "".equals(regionCode.trim())) {
			return;
		}
		try {
			Schema sc = SchemaController.instance().get(EHR_AreaGridChild);
			StringBuffer sql = new StringBuffer();
			sql.append("update ").append(sc.getId()).append(" set ");
			Set<Entry<String, String>> entrySet = fields.entrySet();
			Iterator<Entry<String, String>> itr = entrySet.iterator();
			int i = 1;
			while (itr.hasNext()) {
				Entry<String, String> entry = itr.next();
				sql.append(entry.getKey()).append("='")
						.append(entry.getValue()).append("'");
				if (i < entrySet.size())
					sql.append(",");
				i++;
			}
			sql.append(" where regionCode like :regionCode|| '%' ");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("regionCode", regionCode);
			dao.doUpdate(sql.toString(), parameters);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新子节点的医生护士信息失败", e);
		}
	}

	/**
	 * 批量修改
	 * 
	 * @param manaDoctor
	 * @param cdhDoctor
	 * @param mhcDoctor
	 * @param where
	 * @throws ModelDataOperationException
	 */
	public void batchUpdate(String manaDoctor, String cdhDoctor,
			String mhcDoctor, String where) throws ModelDataOperationException {
		StringBuilder hql = new StringBuilder();
		hql.append("update ").append("EHR_AreaGridChild").append(" set ");
		if (StringUtils.isEmpty(manaDoctor) && StringUtils.isEmpty(cdhDoctor)
				&& StringUtils.isEmpty(mhcDoctor)) {
			return;
		}
		StringBuilder temp = new StringBuilder();
		if (!StringUtils.isEmpty(manaDoctor)) {
			temp.append(",manaDoctor = '").append(manaDoctor).append("'");
		}
		if (!StringUtils.isEmpty(cdhDoctor)) {
			temp.append(",cdhDoctor = '").append(cdhDoctor).append("'");
		}
		if (!StringUtils.isEmpty(mhcDoctor)) {
			temp.append(",mhcDoctor = '").append(mhcDoctor).append("'");
		}
		hql.append(temp.substring(1)).append(" where ").append(where);
		try {
			dao.doUpdate(hql.toString(), null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "批量修改执行失败", e);
		}
	}

	/**
	 * 通过网格地址编码查询数据
	 * 
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAreaGridByRegionCode(String regionCode)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		Map<String, Object> nodeInfo;
		try {
			Object[] parameters = new Object[] { "area", regionCode };
			nodeInfo = (Map<String, Object>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "get",
					parameters, header);
			if (nodeInfo != null && nodeInfo.size() > 0) {
				Map<String,Object> child = dao.doLoad(EHR_AreaGridChild, regionCode);
				if (child != null && child.size()>0) {
					nodeInfo.putAll(child);
				}
			}
			
			return nodeInfo;
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询网格地址失败！");
		}
	}

	/**
	 * 按照网格地址名称查找数据
	 * 
	 * @param regionName
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAreaGridByRegionName(String regionName)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		StringBuffer cnd = new StringBuffer("['eq',['$','regionName'],['s','")
				.append(regionName).append("']]");
		try {
			Object[] paras = new Object[] { "area",
					CNDHelper.toListCnd(cnd.toString()), new QueryContext() };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			return qr.getItems();
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC按照网格地址名称查找数据失败！", e);
		}
	}

	/**
	 * 根据父节点regionCode查询该节点下所有的regionName
	 * 
	 * @param code
	 * @param length
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMap(String code, int length)
			throws ModelDataOperationException {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		StringBuffer cnd = new StringBuffer(
				"['like',['$','regionCode'],['concat',['s','").append(code)
				.append("'],['s','%']]]");
		cnd.insert(0, "['and',").append(",['lt',['len','regioncode'],['s','")
				.append(length).append("']]]");
		cnd.insert(0, "['and',").append(",['ne',['$','regionCode'],['s','")
				.append(code).append("']]]");
		try {
			Object[] paras = new Object[] { "area",
					CNDHelper.toListCnd(cnd.toString()), new QueryContext() };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			if(qr.getItems() == null){
				return new ArrayList<Map<String,Object>>(0);
			}
			return qr.getItems();
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"RPC服务根据父节点regionCode查询该节点下所有的regionName失败", e);
		}
	}

}
