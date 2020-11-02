/**
 * @(#)AreaGridManageMent.java Created on 2012-2-6 上午11:07:28
 * <p>
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.scm;

import java.util.HashMap;
import java.util.Map;

import chis.source.PersistentDataOperationException;
import ctd.util.S;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.admin.AreaGridModel;
import chis.source.dic.YesNo;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.SendDictionaryReloadSynMsg;
import ctd.dictionary.DictionaryController;
import ctd.net.rpc.Client;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @author <a href="mailto:guol@bsoft.com.cn">guol</a>
 * @description
 */

public class ContractService extends AbstractActionService implements
        DAOSupportable {
    private BaseDAO baseDao;

    public BaseDAO getBaseDao() {
        return baseDao;
    }

    public void setBaseDao(BaseDAO baseDao) {
        this.baseDao = baseDao;
    }

    private static final Logger logger = LoggerFactory
            .getLogger(ContractService.class);

    @SuppressWarnings("unchecked")
    protected void doGetItemsFormData(Map<String, Object> req,
                                      Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
        String itemCode = (String) reqBody.get("pkey");
        ContractModel cm = new ContractModel(dao);
        Map<String, Object> body;
        try {
            body = cm.getNodeInfo(itemCode);
        } catch (ModelDataOperationException e) {
            res.put(RES_CODE, e.getCode());
            res.put(RES_MESSAGE, e.getMessage());
            throw new ServiceException(e);
        }
        res.put("body",
                SchemaUtil.setDictionaryMessageForForm(body, SCM_ServiceItems));
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
    protected void doServiceItemDelete(Map<String, Object> req,
                                       Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
        Map<String, Object> body = (HashMap<String, Object>) req.get("body");
        String itemCode = (String) body.get("itemCode");
        String parentCode = (String) body.get("parentCode");
        if (itemCode == null || itemCode.length() < 2) {
            logger.error("itemCode  is unable to delete:[{}]", itemCode);
            throw new ServiceException(ServiceCode.CODE_INVALID_REQUEST,
                    "错误的请求：当前节点不允许被删除:[" + itemCode + "]");
        }
        ContractModel cm = new ContractModel(dao);
        try {
            int CODE = cm.deleteLikeItemCode(itemCode);
            if(CODE > 9000){
            	res.put("code", 9001);
            	res.put("msg", "节点被启用,无法删除");}
            else
            	DictionaryController.instance().reload("chis.dictionary.serviceItems");
        } catch (ModelDataOperationException e) {
            logger.error("Failed to delete serviceItems node.", e);
            throw new ServiceException(e);
        }
    }

    /**
     * 生成服务项目编码
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    protected void doGenItemCode(Map<String, Object> req,
                                 Map<String, Object> res, BaseDAO dao, Context ctx)
            throws ServiceException {
        Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
        String parentCode = (String) reqBody.get("parentCode");
        if (S.isNotEmpty(parentCode)) {
            int plen = parentCode.length();
            if (plen < 9) {
                String gmHQL = new StringBuffer("select max(t.itemCode) as maxItemCode from ")
                        .append(SCM_ServiceItems).append(" t where t.parentCode=:parentCode").toString();
                Map<String,Object> rsMap = null;
                try {
                    Map<String,Object> pMap = new HashMap<String, Object>();
                    pMap.put("parentCode",parentCode);
                    rsMap = dao.doLoad(gmHQL, pMap);
                } catch (PersistentDataOperationException e) {
                    e.printStackTrace();
                }
                String nic = "";
                if(rsMap!=null && rsMap.size()>0){
                    String maxItemCode = (String) rsMap.get("maxItemCode");
                    if(S.isNotEmpty(maxItemCode)){
                        Long mic = Long.parseLong("1"+maxItemCode);
                        nic = ((mic.longValue()+1)+"").substring(1);
                    }else{
                        if(plen<6){
                            nic = parentCode+"01";
                        }else{
                            nic = parentCode+"001";
                        }
                    }
                }else{
                    if(plen<6){
                        nic = parentCode+"01";
                    }else{
                        nic = parentCode+"001";
                    }
                }
                res.put("newItemCode",nic);
            }
        }
    }

    /**
     * 新增签约节点
     *
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    protected void doSaveServiceItem(Map<String, Object> req,
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
                    "错误的请求，节点路径丢失。");
        }
        String itemCode = (String) body.get("itemCode");
        String itemType = (String) body.get("itemType");
        checkItemCode(itemCode, itemType);
        String isBottom = getDefaultIsBottom(itemType);
        body.put("isBottom", isBottom);
        body.put("parentCode", parentCode);
        ContractModel cm = new ContractModel(dao);
        try {
            Map<String, Object> rebody = cm.insertServiceItem(body, (String) req.get("op"));
            rebody.putAll(body);
            res.put("body", rebody);
            DictionaryController.instance().reload("chis.dictionary.serviceItems");
            SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.serviceItems");
        } catch (ModelDataOperationException e) {
            logger.error("Failed to save serviceItems record .", e);
            throw new ServiceException(e);
        }
    }

    /**
     * 获取签约项目节点信息
     *
     * @param
     * @return
     * @throws Exception
     * @throws ModelDataOperationException
     */
    @SuppressWarnings("unchecked")
    protected void doGetNodeInfo(Map<String, Object> req,
                                 Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {

        String itemCode = StringUtils.trimToEmpty((String) req
                .get("itemCode"));
        if (itemCode == null || itemCode.trim().length() == 0) {
            return;
        }
        Map<String, Object> nodeInfo;
        // 加载本地网格地址数据
        Map<String, Object> localInfo = dao.doLoad(SCM_ServiceItems, itemCode);
        if (localInfo != null) {
            res.put("nodeInfo", localInfo);
        }
    }

    /*
     * 签约编码是否合理。 暂缓
     */
    private void checkItemCode(String itemCode, String itemType)
            throws ServiceException {
        //todo
//        int plen = 0;
//        if (itemType.equals("1")) {
//            plen = 2;
//        } else if (itemType.equals("4")) {
//            plen = 4;
//        }
//        if (itemCode.length() != plen) {
//            throw new ServiceException("该层签约编码长度应该为" + plen);
//        }
    }

    /**
     * 根据itemType来判断isbottom标示.
     *
     * @param itemType
     * @return
     */
    private String getDefaultIsBottom(String itemType) {
        if (itemType == null)
            return YesNo.NO;
        if ("4".equals(itemType))
            return YesNo.YES;
        return YesNo.NO;
    }
}
