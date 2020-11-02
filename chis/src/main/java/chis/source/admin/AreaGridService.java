/**
 * @(#)AreaGridManageMent.java Created on 2012-2-6 上午11:07:28
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.BizColumnConfig;
import chis.source.dic.IsFamily;
import chis.source.dic.YesNo;
import chis.source.pub.BizInfoUpdateModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.SendDictionaryReloadSynMsg;

import com.alibaba.fastjson.JSONException;

import ctd.controller.exception.ControllerException;
import ctd.dao.support.QueryContext;
import ctd.dao.support.QueryResult;
import ctd.dictionary.DicToXMLDic;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.net.rpc.Client;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class AreaGridService extends AbstractActionService implements
		DAOSupportable {
	private BaseDAO baseDao;

	public BaseDAO getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDAO baseDao) {
		this.baseDao = baseDao;
	}

	private static final Logger logger = LoggerFactory
			.getLogger(AreaGridService.class);

	/**
	 * 获取网格地址信息
	 * 
	 * @param regionCode
	 * @return
	 * @throws Exception
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetNodeInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		
		String regionCode = StringUtils.trimToEmpty((String) req
				.get("regionCode"));
		if (regionCode == null || regionCode.trim().length() == 0) {
			return;
		}
		Map<String, Object> nodeInfo;
		try {
			HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
			Object[] parameters = new Object[] { "area", regionCode };
			nodeInfo = (Map<String, Object>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "get",
					parameters, header);
			// 加载本地网格地址数据
			Map<String,Object> localInfo = dao.doLoad(EHR_AreaGridChild, regionCode);
			if(localInfo != null){
				nodeInfo.putAll(dao.doLoad(EHR_AreaGridChild, regionCode));
				nodeInfo = SchemaUtil.setDictionaryMessageForForm(nodeInfo,
						EHR_AreaGridChild);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get node info from AreaGrid");
			throw new ServiceException(e);
		}
		if (nodeInfo != null) {
			res.put("nodeInfo", nodeInfo);
		}
	}

	/**
	 * 获取网格地址信息
	 * 
	 * @param regionCode
	 * @return
	 * @throws Exception
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doListAreaGrid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();

		QueryContext qc = new QueryContext();
		qc.setPageNo((Integer) req.get("pageNo"));
		qc.setPageSize((Integer) req.get("pageSize"));
		Object[] parameters = new Object[] { "area", req.get("cnd"), qc };
		QueryResult<?> qr = (QueryResult<?>) Client.rpcInvoke(
				AppContextHolder.getConfigServiceId("daoService"), "find",
				parameters, header);
		List rpcList = qr.getItems();
		List localList = this.fillLocalAreaGridInfo(req, rpcList, dao, ctx);
		List resList = SchemaUtil.setDictionaryMessageForList(localList,
				EHR_AreaGridChild);
		res.put("body", resList);
		res.put("pageSize", (Integer) req.get("pageSize"));
		res.put("pageNo", (Integer) req.get("pageNo"));
		res.put("totalCount", qr.getTotal());

	}

	private List<Map<String, Object>> fillLocalAreaGridInfo(
			Map<String, Object> req, List<Map<String, Object>> agList,
			BaseDAO dao, Context ctx) throws ExpException,
			PersistentDataOperationException {
		List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
		StringBuffer cnd = new StringBuffer("['in',['$','a.regionCode'],[");
		StringBuffer c = new StringBuffer();
		if (agList == null) {
			return new ArrayList<Map<String, Object>>();
		}
		for (Map<String, Object> ag : agList) {
			String regionCode = (String) ag.get("regionCode");
			c.append("'").append(regionCode).append("'").append(",");
		}
		cnd.append(c.substring(0, c.length() - 1));
		cnd.append("]]");
		List<Map<String, Object>> localList = dao.doQuery(
				CNDHelper.toListCnd(cnd.toString()), null, EHR_AreaGridChild);
		if (localList == null || localList.size() == 0) {
			return agList;
		} else {
			for (Map<String, Object> ag : agList) {
				String regionCode = (String) ag.get("regionCode");
				for (Map<String, Object> localMap : localList) {
					String localRegionCode = (String) localMap
							.get("regionCode");
					boolean b = regionCode.equals(localRegionCode);
					if (b) {
						ag.put("manaDoctor", localMap.get("manaDoctor"));
						ag.put("cdhDoctor", localMap.get("cdhDoctor"));
						ag.put("mhcDoctor", localMap.get("mhcDoctor"));
						resList.add(ag);
					}
				}
			}
			return resList;
		}
	}

	/**
	 * 删除节点
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void doBatchDelete(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String regionCode = (String) body.get("regionCode");
		String parentCode = (String) body.get("parentCode");
		if (regionCode == null || regionCode.length() < 6) {
			logger.error("regionCode  is unable to delete:[{}]", regionCode);
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求：网格地址不允许被删除:[" + regionCode + "]");
		}
		try {
			AreaGridModel agm = new AreaGridModel(dao);
			// 如果该网格地址不是用户管辖范围内 不允许删除
			String userRegionCode = agm.getUserRegionCode(ctx);
			if (false == agm.checkRegionCode(regionCode, userRegionCode)) {
				throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
						"错误的请求：无权删除网格地址！请与中心网管联系 ");
			}
			boolean inUse = agm.checkRegionCodeInUse(regionCode, "like", true);
			if (inUse == true) {
				throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
						"该网格地址已经被使用，无法删除！ ");
			}
			agm.deleteLikeRegionCode(regionCode);

			if (parentCode == null || parentCode.equals("")) {
				return;
			}
			// **查询删除节点父节点下是否还存在子节点，如不存在更新父节点为最底层
			if (agm.haveChildNodes(parentCode)) {
				return;
			}
			String parentIsFamily = (String) body.get("parentIsFamily");
			if (YesNo.NO.equals(getDefaultIsBottom(parentIsFamily))) {
				return;
			}
			agm.updateNodeIsBottom(parentCode, YesNo.YES);
			Map<String, Object> resBody = new HashMap<String, Object>();
			res.put("body", resBody);
			resBody.put("parentIsBottom", true);
			DictionaryController.instance().reload("chis.dictionary.areaGrid");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid");
		} catch (ModelDataOperationException e) {
			logger.error("Failed to delete areagrid node.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据isFamily来判断isbottom标示.
	 * 
	 * @param isFamily
	 * @return
	 */
	private String getDefaultIsBottom(String isFamily) {
		if (isFamily == null)
			return YesNo.NO;
		if (IsFamily.FAMILY.equals(isFamily))
			return YesNo.YES;

		String endTag = isFamily.substring(1, 2);
		String notBottomTag = "";
		isFamily = isFamily.substring(0, 1);

		if (IsFamily.END_TAG_CITY.equals(endTag)) {
			notBottomTag = IsFamily.NOT_BOTTOM_CITY;
		} else {
			notBottomTag = IsFamily.NOT_BOTTOM_COUNTRY;
		}

		if (notBottomTag.compareToIgnoreCase(isFamily) <= 0) {
			return YesNo.YES;
		}
		return YesNo.NO;
	}

	public Document dic() throws ControllerException {
		Dictionary dic = DictionaryController.instance().get(
				"chis.dictionary.areaGrid");
		Document doc = DicToXMLDic.toXMLDictionary(dic).getDefineDoc();
		return doc;
	}

	/**
	 * 前台获取isFamily属性对应的limit信息。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetLimitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, String> body = (Map<String, String>) req.get("body");
		String isFamily = body.get("isFamily");
		Dictionary isFamilyDic = null;
		try {
			isFamilyDic = DictionaryController.instance().get(
					"chis.dictionary.isFamily");
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		DictionaryItem dicItem = isFamilyDic.getItem(isFamily);
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("limit", dicItem.getProperty("limit"));
		res.put("body", resBody);
	}

	/**
	 * 获取默认的网格地址编号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetDefaultId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String isFamily = (String) body.get("isFamily");
		String parentCode = (String) body.get("parentCode");
		Map<String, Object> resBody = new HashMap<String, Object>();
		AreaGridModel agm = new AreaGridModel(dao);

		try {
			resBody.put("regionNo", agm.getNewId(parentCode, isFamily));
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 新增一个网格地址
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveAreaGrid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		if (body == null) {
			logger.error("body element is missing!");
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求：body节点丢失。");
		}
		String parentCode = StringUtils.trimToEmpty((String) body
				.get("parentCode"));
		if (parentCode == null || parentCode.trim().length() == 0) {
			logger.error("parentCode is missing!");
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求，网格地址路径丢失。");
		}
		String isFamily = (String) body.get("isFamily");
		String regionNo = (String) body.get("regionNo");
		checkRegionNo(regionNo, isFamily);
		String isBottom = getDefaultIsBottom(isFamily);
		body.put("isBottom", isBottom);
		body.put("parentCode", parentCode);
		AreaGridModel agm = new AreaGridModel(dao);
		try {
			// 确认ID是否被使用。
			String userRegionCode = agm.getUserRegionCode(ctx);
			if (agm.getNodeInfo(parentCode + regionNo) != null) {
				List<String> ids = agm.getNewIds(parentCode, isFamily, 5);
				throw new ServiceException("网格地址编码:" + regionNo + "已经被使用,可用编码:"
						+ ids + "等..");
			}

			if (false == agm.checkRegionCode(parentCode, userRegionCode)) {
				throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
						"错误的请求：无权新建网格地址！请与中心网管联系");
			}
			Map<String, Object> rebody = agm.insertAreaGrid(body, "create");
			rebody.putAll(body);
			res.put("body", rebody);
			// chb 如果父节点是底层节点跟新父节点底层属性
			if (YesNo.YES.equals(body.get("parentBottom"))) {
				agm.updateNodeIsBottom(parentCode, YesNo.NO);
			}
			DictionaryController.instance().reload("chis.dictionary.areaGrid");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid");
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save areaGrid record .", e);
			throw new ServiceException(e);
		}
	}

	/*
	 * 网格地址手工编码是否合理。
	 */
	private void checkRegionNo(String regionNo, String isFamily)
			throws ServiceException {
		int plen = 3;
		if (IsFamily.FAMILY.equals(isFamily)) {
			plen = 4;
		}

		if (regionNo.length() != plen) {
			throw new ServiceException("该层网格地址编码长度应该为" + plen);
		}
	}

	/**
	 * 更新网格地址。
	 * 
	 * @throws ServiceException
	 * 
	 * @throws PersistentDataOperationException
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doUpdateAreaGrid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		res.put("body", body);
		if (body == null) {
			logger.error("body element is missing!");
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求：body节点丢失。");
		}
		String regionCode = (String) body.get("regionCode");
		if (regionCode == null || regionCode.trim().length() <= 6) {
			logger.error("regionCode unable to update :" + regionCode);
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求：regionCode丢失或者该网格地址不允许修改:" + regionCode);
		}
		String regionName = StringUtils.trimToEmpty((String) body
				.get("regionName"));
		AreaGridModel agm = new AreaGridModel(dao);
		try {
			// 如果该网格地址不是用户管辖范围内 不允许修改
			String userRegionCode = agm.getUserRegionCode(ctx);
			if (false == agm.checkRegionCode(regionCode, userRegionCode)) {
				throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
						"错误的请求：无权修改网格地址！请与中心网管联系！");
			}

			// $$ 检测有没有修改户属性(即 有没有修改 网格地址的 "层次属性")
			boolean isNew = false;
			// 查询旧的网格地址
			Map<String, Object> oldGrid = agm.getNodeInfo(regionCode);
			String oldIsFamily = (String) oldGrid.get("isFamily");
			String isFamily = StringUtils.trimToEmpty((String) body
					.get("isFamily"));
			int plen = agm.getLength(regionCode, oldIsFamily);
			String parentCode = (String) body.get("parentCode");

			if (!isFamily.equals(oldIsFamily)) {// 当修改了时
				// $$ 检查该网格地址是否已经被其它档案所引用
				if (agm.checkRegionCodeInUse(regionCode, "like", true)) {
					throw new ServiceException(
							ServiceCode.CODE_INVALID_REQUEST,
							"该网格地址已经被关联使用不能修改层次属性！");
				}
				isNew = true;
			}

			// $$ 检查同层是否存在同名的网格地址。
			if (agm.regionNameExitsOnLayer(regionCode, parentCode, plen,
					regionName, isFamily)) {
				throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
						"该层同名网格地址已经存在:" + regionName);
			}

			String oldRegionName = (String) oldGrid.get("regionName");
			String regionNo = (String) body.get("regionNo");
			if (isNew) {
				String newId = new StringBuffer(parentCode).append(regionNo)
						.toString();
				if (false == newId.equals(regionCode)) {// id变化时 检查重复。
					if (agm.getNodeInfo(parentCode + regionNo) != null) {
						List<String> ids = agm.getNewIds(parentCode, isFamily,
								5);
						throw new ServiceException("网格地址编码:" + regionNo
								+ "已经被使用,可用编码:" + ids + "等..");
					}
				}
				// 提示信息分类：当父节点被引用，修改层次属性时，提示不能新建子结点
				req.put("comeFrom", "update");
				// $$ 删除原来节点
				agm.deleteAreaGrid(regionCode);
				// $$ 从新生成新节点
				// 检查是否有子节点
				List children = agm.getLikeRegionCode(regionCode);
				if (children.size() == 0) {
					String isBottom = getDefaultIsBottom(isFamily);
					body.put("isBottom", isBottom);
					body.put("oldCode", regionCode);
				}
				Map insertRes = agm.insertAreaGrid(body, "create");
				body.putAll(insertRes);
				// 修改其子结点网格地址编码（regionCode） 不同名
				agm.updateChildNodeRegionCode(newId, regionCode);
				List keyList = new ArrayList();
				keyList.add(regionCode);
				if (false == newId.equals(regionCode)) {// id变化时 检查重复。
					agm.updateChildrenRegionCode(regionCode, newId);
				}
				String newRegionCode = (String) insertRes.get("regionCode");
				if (!newRegionCode.equals(regionCode)) {
					body.put("oldCode", regionCode);// $$ 标记节点是否从新生成
				}
			} else {
				Map<String, Object> resBody = agm.saveAreaGrid(body, "update");
				body.putAll(resBody);
				boolean overWrite = (Boolean) body.get("overWrite");
				if (overWrite) {
					Map<String, String> update = new HashMap<String, String>();
					String manaDoctor = StringUtils.trimToEmpty((String) body
							.get("manaDoctor"));
					String manaNurse = StringUtils.trimToEmpty((String) body
							.get("manaNurse"));
					String healthDoctor = StringUtils.trimToEmpty((String) body
							.get("healthDoctor"));
					String mhcDoctor = StringUtils.trimToEmpty((String) body
							.get("mhcDoctor"));
					String cdhDoctor = StringUtils.trimToEmpty((String) body
							.get("cdhDoctor"));
					update.put("manaDoctor", manaDoctor);
					update.put("manaNurse", manaNurse);
					update.put("healthDoctor", healthDoctor);
					update.put("mhcDoctor", mhcDoctor);
					update.put("cdhDoctor", cdhDoctor);
					agm.updateDocInfo(regionCode, update);
				}
				// @@ 如果是底层节点修改了，要更新业务表。
				if (YesNo.YES.equals((String) body.get("isBottom"))
						&& !regionName.equals(oldRegionName)) {
					BizInfoUpdateModel bium = new BizInfoUpdateModel(dao);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("regionCode", regionCode);
					params.put("regionCode_text", regionName);
					bium.update(BizColumnConfig.UPDATE_REGION_TEXT, params);
				}
				// 判断网格地址编码是否被修改。
				String oldRegionNo = (String) oldGrid.get("regionNo");
				if (!oldRegionNo.equals(regionNo)) {
					body.put("oldCode", regionCode);// $$ 标记节点是否从新生成
					if (agm.checkRegionCodeInUse(regionCode, "like", true) == true) {
						throw new ServiceException(
								ServiceCode.CODE_DATABASE_ERROR,
								"网格地址已经被档案使用,无法修改编号!");
					}
					if (agm.getNodeInfo(parentCode + regionNo) != null) {
						List<String> ids = agm.getNewIds(parentCode, isFamily,
								5);
						throw new ServiceException("网格地址编码:" + regionNo
								+ "已经被使用,可用编码:" + ids + "等..");
					}
					String newCode = new StringBuffer(parentCode).append(
							regionNo).toString();
					agm.updateChildrenRegionCode(regionCode, newCode);
					body.put("regionCode", newCode);
					Map<String, Object> responseBody = new HashMap<String, Object>();
					responseBody.put("regionCodeChanged", true);
				}
				res.put("body", body);
			}
			DictionaryController.instance().reload("chis.dictionary.areaGrid");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid");
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
			logger.error("Failed to modify region adress with code[].",
					new Object[] { regionCode, e });
			throw new ServiceException(ServiceCode.CODE_DATABASE_ERROR,
					"网格地址修改失败:" + regionName);
		}
	}

	// ///////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected void doGetAreaGridFormData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String regionCode = (String) reqBody.get("pkey");
		AreaGridModel agm = new AreaGridModel(dao);
		Map<String, Object> body;
		try {
			body = agm.getNodeInfo(regionCode);
			if (regionCode.length() > 4) {
				String isFamily = (String) body.get("isFamily");
				String path = agm.getParentFullPath(regionCode, isFamily);
				body.put("path", path);
			}
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		res.put("body",
				SchemaUtil.setDictionaryMessageForForm(body, EHR_AreaGridChild));
	}

	/**
	 * 根据网格地址获取完整的上层节点路径
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetFullPath(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String regionCode = (String) reqBody.get("regionCode");
		String isFamily = StringUtils.isNotEmpty((String) reqBody
				.get("isFamily")) ? (String) reqBody.get("isFamily")
				: IsFamily.NOTFAMILY;
		int length = regionCode.length();
		if (length <= 4) {
			return;
		}
		AreaGridModel agm = new AreaGridModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			String path = agm.getParentFullPath(regionCode, isFamily);
			body.put("path", path);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	/**
	 * 如果需要覆盖子节点的网格地址下存在多个责任医生，给出提示。
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	protected void doCheckOverwrite(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		String regioncode = (String) req.get("regionCode");
		AreaGridModel agm = new AreaGridModel(dao);
		long count = agm.countManaDoctor(regioncode);
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("count", count);
		res.put("body", body);
	}

	/**
	 * 批量生成小区
	 * 
	 * @param req
	 * @param res
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 */
	protected void doBatchResidenceCommunity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String regionCode = (String) body.get("regionCode");
		if (regionCode == null || regionCode.trim().length() == 0) {
			logger.error("regionCode element is missing!");
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求：regionCode信息丢失。 ");
		}
		AreaGridModel agm = new AreaGridModel(dao);
		try {
			agm.checkBeforeInsertAreaGrid(regionCode, "insert", ctx);
			int count = (Integer) body.get("count") != null ? ((Integer) body
					.get("count")).intValue() : 0;
			int start = (Integer) body.get("start") != null ? ((Integer) body
					.get("start")).intValue() : 0;
			if (start < 1)
				start = 1;
			start--;
			String unit = StringUtils.trimToEmpty((String) body.get("unit"));
			Map<String, Object> parentInfo = agm.getNodeInfo(regionCode);
			parentInfo.put("regionAlias", "");// 清空简称
			String isFamily = (String) body.get("isFamily");
			List<String> ids = agm.getNewIds(regionCode, isFamily, count);
			ArrayList<Map<String, Object>> resBody = new ArrayList<Map<String, Object>>();
			for (int i = 1; i <= count; i++) {
				Map<String, Object> reqBody = new HashMap<String, Object>(
						parentInfo);
				reqBody.put("isFamily", isFamily);
				reqBody.put("parentCode", regionCode);
				reqBody.put("regionNo", ids.get(i - 1));
				reqBody.put("isBottom", getDefaultIsBottom(isFamily));
				reqBody.put("orderNo", Integer.parseInt(ids.get(i - 1)) * 10);
				String regionName = new StringBuffer(
						StringUtils.trimToEmpty((String) parentInfo
								.get("regionName"))).append(i + start)
						.append(unit).toString();
				reqBody.put("regionName", regionName);
				Map<String, Object> insertResBody = agm.insertAreaGrid(reqBody,
						"create");
				if (insertResBody == null) {
					reqBody.putAll(insertResBody);
				}
				resBody.add(reqBody);
			}
			if (YesNo.YES.equals(body.get("parentBottom"))) {
				agm.updateNodeIsBottom(regionCode, YesNo.NO);
			}
			res.put("body", resBody);
			DictionaryController.instance().reload("chis.dictionary.areaGrid");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid");
		} catch (ModelDataOperationException e) {
			logger.error("Failed to batch residence community .", e);
			throw new ServiceException(e);
		}
		logger.info("executeBatchResidenceCommunity response: " + res);
	}

	/**
	 * 批量生成户
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doBatchUnit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		if (body == null) {
			logger.error("body element is missing!");
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求：body节点丢失。 ");
		}
		String parentCode = StringUtils.trimToEmpty((String) body
				.get("parentCode"));
		logger.info("parent id :" + parentCode);
		if (parentCode == null || parentCode.trim().length() == 0) {
			logger.error("parentCode is missing!");

			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求，网格地址路径丢失。 ");
		}
		AreaGridModel agm = new AreaGridModel(dao);
		try {
			agm.checkBeforeInsertAreaGrid(parentCode, "insert", ctx);
			String codeStyle = StringUtils.trimToEmpty((String) body
					.get("codeStyle"));
			if (codeStyle == null)
				codeStyle = "01";
			int layerCount = (Integer) body.get("layerCount") != null ? ((Integer) body
					.get("layerCount")).intValue() : 0;
			int familyCount = (Integer) body.get("familyCount") != null ? ((Integer) body
					.get("familyCount")).intValue() : 0;
			logger.info("层 " + layerCount + " 户 " + familyCount);
			int startLayer = (Integer) body.get("startLayer") != null ? ((Integer) body
					.get("startLayer")).intValue() : 0;
			int startFamily = (Integer) body.get("startFamily") != null ? ((Integer) body
					.get("startFamily")).intValue() : 0;
			logger.info("起始层 " + startLayer + "起始户 " + startFamily);
			if (startLayer < 1)
				startLayer = 1;
			if (startFamily < 1)
				startFamily = 1;
			startLayer--;
			startFamily--;
			Map<String, Object> parentInfo = agm.getNodeInfo(parentCode);
			parentInfo.put("regionAlias", "");// 清空简称
			logger.info("找到的网格地址信息:" + parentInfo);
			if (parentInfo == null) {
				logger.error("no areagrid found for key " + parentCode);
				throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
						"错误的请求，数据库中没有该网格地址。 ");
			}
			ArrayList<Map<String, Object>> resBody = new ArrayList<Map<String, Object>>();
			List<String> ids = agm.getNewIds(parentCode, IsFamily.FAMILY,
					layerCount * familyCount);
			for (int i = 1; i <= layerCount; i++) {
				for (int j = 1; j <= familyCount; j++) {
					Map<String, Object> reqBody = new HashMap<String, Object>(
							parentInfo);
					int idx = (i - 1) * familyCount + j;
					reqBody.put("parentCode", parentCode);
					reqBody.put("regionNo", ids.get(idx - 1));
					reqBody.put("isFamily", IsFamily.FAMILY);
					reqBody.put("isBottom", YesNo.YES);
					reqBody.put("orderNo",
							Integer.parseInt(ids.get(idx - 1)) * 10);
					String regionName = "";
					if ("01".equals(codeStyle)) {
						StringBuffer format = new StringBuffer("%1$02d");
						regionName = StringUtils
								.trimToEmpty((String) parentInfo
										.get("regionName"))
								+ (i + startLayer)
								+ String.format(format.toString(), j
										+ startFamily);
					} else if ("02".equals(codeStyle)) {
						regionName = StringUtils
								.trimToEmpty((String) parentInfo
										.get("regionName"))
								+ (i + startLayer)
								+ "0"
								+ (char) (j + startFamily + 64);
					} else if ("03".equals(codeStyle)) {
						regionName = StringUtils
								.trimToEmpty((String) parentInfo
										.get("regionName"))
								+ (i + startLayer) + "号";
					}
					reqBody.put("regionName", regionName);
					Map<String, Object> insertResBody = agm.insertAreaGrid(
							reqBody, "create");
					if (insertResBody != null) {
						reqBody.putAll(insertResBody);
					}
					resBody.add(reqBody);
				}
			}
			if (YesNo.YES.equals(body.get("parentBottom"))) {
				agm.updateNodeIsBottom(parentCode, YesNo.NO);
			}
			res.put("body", resBody);
			DictionaryController.instance().reload("chis.dictionary.areaGrid");
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid");
		} catch (ModelDataOperationException e) {
			logger.error("Failed to batch residence Buildings .", e);
			throw new ServiceException(e);
		}
		logger.info("executeBatchUnit request:" + req);
	}

	/**
	 * 判断当前节点是否已经被使用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doCheckAreaGridUsing(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		if (body == null) {
			logger.error("body element is missing!");
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求：body节点丢失。");
		}
		String parentCode = StringUtils.trimToEmpty((String) body
				.get("regionCode"));
		if (parentCode == null || parentCode.trim().length() == 0) {
			logger.error("parentCode is missing!");
			throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
					"错误的请求，网格地址路径丢失。");
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		AreaGridModel agm = new AreaGridModel(dao);
		try {
			resBody.put("result",
					agm.checkRegionCodeInUse(parentCode, "eq", false));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to check areaGrid in using");
			throw new ServiceException(e);
		}
		res.put("body", resBody);
	}

	/**
	 * 批量修改
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doBatchUpdate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			String manaDoctor = (String) req.get("manaDoctor");
			String cdhDoctor = (String) req.get("cdhDoctor");
			String mhcDoctor = (String) req.get("mhcDoctor");

			ArrayList<?> cnd = (ArrayList<?>) req.get("cnd");
			String where;
			try {
				where = ExpressionProcessor.instance().toString(cnd);
			} catch (ExpException e) {
				e.printStackTrace();
				return;
			}
			AreaGridModel agm = new AreaGridModel(dao);
			agm.batchUpdate(manaDoctor, cdhDoctor, mhcDoctor, where);
			logger.info("=====AreaGridBatchUpdate =======complete=======");
		} catch (ModelDataOperationException e) {
			logger.error("AreaGridBatchUpdate occur error.", e);
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

}
