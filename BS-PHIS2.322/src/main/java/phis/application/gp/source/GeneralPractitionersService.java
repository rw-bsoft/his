package phis.application.gp.source;

import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Auther: Fengld
 * @Date: 2019/3/4 15:49
 * @Description: 家医签约后台服务类
 */
public class GeneralPractitionersService extends AbstractActionService implements DAOSupportable {
    protected Logger logger = LoggerFactory.getLogger(GeneralPractitionersService.class);

    public void doQueryGPbrxz(Map<String, Object> req, Map<String, Object> res,
                              BaseDAO dao, Context ctx) throws ServiceException {

    }

    /**
     * 根据身份证或者empiid获取签约id
     * @Auther: Fengld
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doGetSCID(Map<String, Object> req, Map<String, Object> res,
                          BaseDAO dao, Context ctx) throws ServiceException {
        Map<String, Object> body = (Map<String, Object>) req.get("body");
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        Map<String, Object>ret = new HashMap<String, Object>();
        try {
            if (body.containsKey("Idcard")) {
                ret = model.GetSCIDWithIdcard(req);
            } else if (body.containsKey("EmpiId")) {
                ret = model.getSCIDWithEmpiId(req);
            }
            res.put("body", ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据SCID获取家医签约明细
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @throws ServiceException
     */
    public void doQueryGpDetail(Map<String, Object> req, Map<String, Object> res,
                                BaseDAO dao, Context ctx) throws ServiceException {
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        Map<String,Object>ret = new HashMap<String, Object>();
        try {
            ret = model.QueryGpDetail(req);
            res.put("body",ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改家医签约激活状态
     * @Auther： fengld
     * @param req
     * @param res
     * @param dao
     * @param ctx
     */
    public void doUpdateGpStatus(Map<String, Object> req, Map<String, Object> res,
                                 BaseDAO dao, Context ctx){
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        Map<String,Object>ret = new HashMap<String, Object>();
        try {
            ret = model.UpdateGpStatus(req);
            res.put("body",ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改剩余服务次数
     *
     * @Auther: Fengld
     * @param req
     * @param res
     * @param dao
     * @param ctx
     */
    public void doUpdateGpServerNums(Map<String, Object> req, Map<String, Object> res,
                                     BaseDAO dao, Context ctx){
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        Map<String,Object>ret = new HashMap<String, Object>();
        try {
            ret = model.UpdateGpServerNums(req);
            res.put("body", ret);
        } catch (Exception e) {
            logger.error("GP-修改剩余次数失败"+ret);
            e.printStackTrace();
        }
    }

    /**
     * 获取家医系统参数
     * 废弃
     * @param req
     * @param res
     * @param dao
     * @param ctx
     */
    public void doQueryGpxtcs(Map<String, Object> req, Map<String, Object> res,
    BaseDAO dao, Context ctx){
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        Map<String,Object>ret = new HashMap<String, Object>();
        try {
            ret = model.getGpxtcs(req);
            res.put("body", ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存chis传过来的信息
     * @param
     * @throws ModelDataOperationException 
     */
    @RpcService
    public void doSaveGpService(Map<String,Object> saveData) throws ModelDataOperationException{
        Context ctx = ContextUtils.getContext();
        Session ss = (Session) ctx.get(Context.DB_SESSION);
        if (ss == null) {
            SessionFactory sf = AppContextHolder.getBean(
                    AppContextHolder.DEFAULT_SESSION_FACTORY,
                    SessionFactory.class);
            ss = sf.openSession();
            ctx.put(Context.DB_SESSION, ss);
        }
        BaseDAO dao = new BaseDAO(ctx);
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        Map<String,Object>ret = new HashMap<String, Object>();
        Map<String,Object>res = new HashMap<String, Object>();
        try {
            ss.beginTransaction();
            model.SaveGpService(saveData);
            ss.beginTransaction().commit();
            System.out.println("rpc 调用成功");
        } catch (Exception e) {
            ss.getTransaction().rollback();
            logger.error("GP-保存增值服务项失败"+e.getMessage());
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "rpc 调用失败"+e.getMessage());
        }finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }

    /**
     * 查询是否家医签约病人
     * @param req
     * @param res
     * @param dao
     * @param ctx
     */
    public void doQueryIsGP(Map<String, Object> req, Map<String, Object> res,
                BaseDAO dao, Context ctx){
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        Map<String,Object>ret = new HashMap<String, Object>();
        try {
            ret = model.queryIsGP(req);
            res.put("body", ret);
        } catch (Exception e) {
            logger.error("GP-查询家医病人失败"+ret);
            e.printStackTrace();
        }
    }

    /**
     * 激活家医增值服务
     * @param req
     * @param res
     * @param dao
     * @param ctx
     */
    public void doLogOnGP(Map<String, Object> req, Map<String, Object> res,
                          BaseDAO dao, Context ctx) {
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        try {
            model.logOnGP(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取家医增值服务明细
     * @param req
     * @param res
     * @param dao
     * @param ctx
     * @return
     */
    public void doQueryGpDetil(Map<String, Object> req,Map<String, Object> res,
                                                 BaseDAO dao, Context ctx){
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        List<Map<String, Object>> ret = null;
        try {
            ret = model.queryGpDetil(req);
        } catch (PersistentDataOperationException e) {
            logger.error("GP-获取家医增值服务明细失败"+ret);
            e.printStackTrace();
        }
        res.put("body",ret);
    }

    public void doUpdateServiceTimes(Map<String, Object> req,Map<String, Object> res,BaseDAO dao, Context ctx) throws ModelDataOperationException{
        GeneralPractitionersModel model = new GeneralPractitionersModel(dao);
        try {
            model.updateServiceTimes(req);
        } catch (PersistentDataOperationException e) {
            logger.error("新增履约记录失败！"+e.getMessage());
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "新增履约记录失败！"+e.getMessage());
        } catch (ValidateException e) {
            logger.error("新增履约记录失败！"+e.getMessage());
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "新增履约记录失败！"+e.getMessage());
        }
    }

}
