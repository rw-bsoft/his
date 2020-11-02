package chis.source.service.adapter;


import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import ctd.service.core.ServiceException;
import ctd.util.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CHenXR on 2018-02-11.
 */
public class ServiceAdaptItem implements BSCHISEntryNames {


    public static final String NONKEYGROUP = "0";
    public static final String HYPERTENSION = "1";
    public static final String DIABETES = "2";
    public static final String CHILD = "3";
    public static final String PREGNANT = "4";
    public static final String OLDMAN = "5";



    private static final Logger logger = LoggerFactory
            .getLogger(ServiceAdaptItem.class);

    /**
     * 公卫服务与签约项目适配器
     *
     * @param chisService 公卫服务代码
     * @param awm         适配参数
     *                    儿童：monthAge（月龄age）
     *                    孕妇：gestationalWeeks（孕周week）
     *                    高血压、糖尿病、严重精神障碍患者 month(随访月份month)
     * @param dao
     * @return
     * @throws ServiceException
     */
    public static String getItemCode(String chisService, String awm, BaseDAO dao) throws ServiceException {
        String siHQL = new StringBuffer("select itemCode as itemCode,remark as remark from ").append(SCM_ServiceItems)
                .append(" where itemType='4' and moduleAppId=:chisService order by itemCode").toString();
        Map<String, Object> pMap = new HashMap<String, Object>();
        pMap.put("chisService", chisService);
        List<Map<String, Object>> riList = null;
        try {
            riList = dao.doQuery(siHQL, pMap);
        } catch (PersistentDataOperationException e) {
            logger.error("依据公卫服务代码获取服务项目失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "依据公卫服务代码获取服务项目失败失败！", e);
        }
        String itemCode = "";
        if (riList != null && riList.size() > 0) {
            if (riList.size() == 1) {//单条，直接取出返回
                Map<String, Object> riMap = riList.get(0);
                itemCode = (String) riMap.get("itemCode");
            } else {//多条，需分类处理，从中筛选出一条来
                String st = S.substringAfter(chisService, "_");
                if ("JZFW".equals(st) || ETJKJC_ETFWONE.equals(chisService)||ETJKJC_ETFWTOW.equals(chisService)
                        || ETZYYJKZD_ZYYFW.equals(chisService)||ETJKJC_ETFWTHREE.equals(chisService)) {//接种服务 儿童健康检查服务
                    if (S.isNotEmpty(awm)) {
                        for (Map<String, Object> riMap : riList) {
                            String remark = (String) riMap.get("remark");
                            if (awm.equals(remark)) {
                                itemCode = (String) riMap.get("itemCode");
                                break;
                            }
                        }
                    }
                }
                if (CJ_YCFFW.equals(chisService) || GXYSF_GXYFW.equals(chisService) || TNBSF_TNBFW.equals(chisService)
                        || JSBSF_JSBFW.equals(chisService)) {
                    if (S.isNotEmpty(awm)) {
                        for (Map<String, Object> riMap : riList) {
                            String remark = (String) riMap.get("remark");
                            if (S.isEmpty(remark)) {
                                continue;
                            }
                            String[] yz = remark.split("-");
                            if (yz.length == 2) {
                                int syz = Integer.parseInt(yz[0]);
                                int eyz = Integer.parseInt(yz[1]);
                                int gw = Integer.parseInt(awm);
                                if (gw >= syz && gw <= eyz) {
                                    itemCode = (String) riMap.get("itemCode");
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }
        return itemCode;
    }

    /**
     * 适配器增加适用人群
     * @param chisService
     * @param awm
     * @param intendedpopulation
     * @param dao
     * @return
     * @throws ServiceException
     */
    public static String getItemCode(String chisService, String awm, String intendedpopulation, BaseDAO dao) throws ServiceException {
        String siHQL = new StringBuffer("select itemCode as itemCode,remark as remark from ").append(SCM_ServiceItems)
                .append(" where itemType='4' and moduleAppId=:chisService").append(intendedpopulation !=null?" and intendedpopulation =:intendedpopulation ":"").append(" order by itemCode").toString();
        Map<String, Object> pMap = new HashMap<String, Object>();
        pMap.put("chisService", chisService);
        if(intendedpopulation !=null){ pMap.put("intendedpopulation", intendedpopulation);}
        List<Map<String, Object>> riList = null;
        try {
            riList = dao.doQuery(siHQL, pMap);
        } catch (PersistentDataOperationException e) {
            logger.error("依据公卫服务代码获取服务项目失败！", e);
            throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                    "依据公卫服务代码获取服务项目失败失败！", e);
        }
        String itemCode = "";
        if (riList != null && riList.size() > 0) {
            if (riList.size() == 1) {//单条，直接取出返回
                Map<String, Object> riMap = riList.get(0);
                itemCode = (String) riMap.get("itemCode");
            } else {//多条，需分类处理，从中筛选出一条来
                String st = S.substringAfter(chisService, "_");
                if ("JZFW".equals(st) || ETJKJC_ETFWONE.equals(chisService)||ETJKJC_ETFWTOW.equals(chisService)
                        || ETZYYJKZD_ZYYFW.equals(chisService)||ETJKJC_ETFWTHREE.equals(chisService)) {//接种服务 儿童健康检查服务
                    if (S.isNotEmpty(awm)) {
                        for (Map<String, Object> riMap : riList) {
                            String remark = (String) riMap.get("remark");
                            if (awm.equals(remark)) {
                                itemCode = (String) riMap.get("itemCode");
                                break;
                            }
                        }
                    }
                }
                if (CJ_YCFFW.equals(chisService) || GXYSF_GXYFW.equals(chisService) || TNBSF_TNBFW.equals(chisService)
                        || JSBSF_JSBFW.equals(chisService)) {
                    if (S.isNotEmpty(awm)) {
                        for (Map<String, Object> riMap : riList) {
                            String remark = (String) riMap.get("remark");
                            if (S.isEmpty(remark)) {
                                continue;
                            }
                            String[] yz = remark.split("-");
                            if (yz.length == 2) {
                                int syz = Integer.parseInt(yz[0]);
                                int eyz = Integer.parseInt(yz[1]);
                                int gw = Integer.parseInt(awm);
                                if (gw >= syz && gw <= eyz) {
                                    itemCode = (String) riMap.get("itemCode");
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }
        return itemCode;
    }
}
