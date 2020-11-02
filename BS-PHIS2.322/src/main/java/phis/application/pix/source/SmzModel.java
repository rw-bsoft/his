package phis.application.pix.source;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.bsoft.pix.server.EmpiIdGenerator;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.pub.source.PublicModel;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.net.rpc.util.ServiceAdapter;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 实名制相关的数据库操作
 *
 * @author zhanghao
 */
public class SmzModel {
    protected BaseDAO dao;
    protected Logger logger = LoggerFactory
            .getLogger(SmzModel.class);

    public SmzModel(BaseDAO dao) {
        this.dao = dao;
    }

    /**
     * 保存SMZ
     *
     * @param validate
     *
     * @param body
     * @throws ValidateException
     * @throws ModelDataOperationException
     */
    public void saveSMZ(Map<String, Object> body, Context ctx)
            throws ValidateException, ModelDataOperationException {
        UserRoleToken user = UserRoleToken.getCurrent();
        String userId = (String) user.getUserId();
        String manaUnitId = user.getManageUnit().getId();

        try {
            //1、数据校验，录入系统中的数据必须有姓名，身份证号，性别，出生日期
            if((body.get("personName") + "").length() > 0
                    &&(body.get("sexCode") + "").length() > 0
                    &&(body.get("idCard") + "").length() > 0
                    &&(body.get("birthday") + "").length() > 0){
                //2、先查询该身份证号信息存不存在，不存在直接进行insert,存在取出相应数据进行补充更新
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("IDCARD",body.get("idCard") + "");
                StringBuffer hql=new StringBuffer();
                hql.append("select t.personname as PERSONNAME,t.homeplace as HOMEPLACE,t.address as ADDRESS,t.zipcode as ZIPCODE,");
                hql.append("t.phonenumber as PHONENUMBER,t.mobilenumber as MOBILENUMBER, t.email as EMAIL,t.nationalitycode as NATIONALITYCODE,");
                hql.append("t.nationcode as NATIONCODE,t.maritalstatuscode as MARITALSTATUSCODE,t.startworkdate as STARTWORKDATE,t.workcode as WORKCODE,");
                hql.append("t.educationcode as EDUCATIONCODE,t.contact as CONTACT,t.contactphone as CONTACTPHONE,t.ybkh as YBKH,t.smkh as SMKH ");
                hql.append("from smz_phicinfo t where t.IDCARD=:IDCARD ");
                Map<String, Object> smz;
                smz = dao.doSqlLoad(hql.toString(), parameters);
                Map<String, Object> orderrecord = new HashMap<String, Object>();

                if (smz != null) {
                    //身份证号  性别  出生年月不进行更新，其他数据进行增量更新
                    if ((body.get("personName") + "").length() > 0&&!(smz.get("PERSONNAME")+"").equals(body.get("personName") + ""))
                        orderrecord.put("personName", (String) body.get("personName"));
                   /* if ((body.get("sexCode") + "").length() > 0)
                        orderrecord.put("sexCode", (String) body.get("sexCode"));
                    if ((body.get("idCard") + "").length() > 0)
                        orderrecord.put("idCard", (String) body.get("idCard"));*/
                    if ((body.get("homePlace") + "").length() > 0&&!(smz.get("HOMEPLACE")+"").equals(body.get("homePlace") + ""))
                        orderrecord.put("homePlace", (String) body.get("homePlace"));
                    if ((body.get("address") + "").length() > 0&&!(smz.get("ADDRESS")+"").equals(body.get("address") + ""))
                        orderrecord.put("address", (String) body.get("address"));

                    if ((body.get("zipCode") + "").length() > 0&&!(smz.get("ZIPCODE")+"").equals(body.get("zipCode") + ""))
                        orderrecord.put("zipCode", (String) body.get("zipCode"));
                    if ((body.get("phoneNumber") + "").length() > 0&&!(smz.get("PHONENUMBER")+"").equals(body.get("phoneNumber") + ""))
                        orderrecord.put("phoneNumber", (String) body.get("phoneNumber"));
                    if ((body.get("mobileNumber") + "").length() > 0&&!(smz.get("MOBILENUMBER")+"").equals(body.get("mobileNumber") + ""))
                        orderrecord.put("mobileNumber", (String) body.get("mobileNumber"));
                    if ((body.get("email") + "").length() > 0&&!(smz.get("EMAIL")+"").equals(body.get("email") + ""))
                        orderrecord.put("email", (String) body.get("email"));
/*                    if ((body.get("birthday") + "").length() > 0&&!smz.get("email").equals(body.get("email") + ""))
                        orderrecord.put("birthday", (String) body.get("birthday"));*/

                    if ((body.get("nationalityCode") + "").length() > 0&&!(smz.get("NATIONALITYCODE")+"").equals(body.get("nationalityCode") + ""))
                        orderrecord.put("nationalityCode", (String) body.get("nationalityCode"));
                    if ((body.get("nationCode") + "").length() > 0&&!(smz.get("NATIONCODE")+"").equals(body.get("nationCode") + ""))
                        orderrecord.put("nationCode",(String) body.get("nationCode"));
                    if ((body.get("maritalStatusCode") + "").length() > 0&&!(smz.get("MARITALSTATUSCODE")+"").equals(body.get("maritalStatusCode") + ""))
                        orderrecord.put("maritalStatusCode",(String) body.get("maritalStatusCode"));
/*                    if ((body.get("startWorkDate") + "").length() > 0&&!(smz.get("STARTWORKDATE")+"").equals(body.get("startWorkDate") + ""))
                        orderrecord.put("startWorkDate", (String) body.get("startWorkDate"));
                    if ((body.get("workCode") + "").length() > 0&&!(smz.get("WORKCODE")+"").equals(body.get("workCode") + ""))
                        orderrecord.put("workCode", (String) body.get("workCode"));*/

                    if ((body.get("educationCode") + "").length() > 0&&!(smz.get("EDUCATIONCODE")+"").equals(body.get("educationCode") + ""))
                        orderrecord.put("educationCode", (String) body.get("educationCode"));
                    if ((body.get("contact") + "").length() > 0&&!(smz.get("CONTACT")+"").equals(body.get("contact") + ""))
                        orderrecord.put("contact", (String) body.get("contact"));
                    if ((body.get("contactPhone") + "").length() > 0&&!(smz.get("CONTACTPHONE")+"").equals(body.get("contactPhone") + ""))
                        orderrecord.put("contactPhone", (String) body.get("contactPhone"));

                    if ((body.get("ybkh") + "").length() > 0&&!(smz.get("YBKH")+"").equals(body.get("ybkh") + ""))
                        orderrecord.put("ybkh", (String) body.get("ybkh"));
                    if ((body.get("smkh") + "").length() > 0&&!(smz.get("SMKH")+"").equals(body.get("smkh") + ""))
                        orderrecord.put("smkh", (String) body.get("smkh"));

                    //orderrecord不为空则进行更新操作
                    if(!orderrecord.isEmpty()){
                        orderrecord.put("lastModifyUnit", manaUnitId);
                        orderrecord.put("lastModifyUser", userId);
                        orderrecord.put("lastModifyTime", new Date());
                        orderrecord.put("idCard", body.get("idCard") + "");

                        dao.doSave("update", BSPHISEntryNames.SMZ_PHICINFO, orderrecord,
                                false);
                    }

                } else {

                    //orderrecord.put("smzid", "test1"+new Random(100));  //规则待定

                    if (body.containsKey("personName") && (body.get("personName") + "").length() > 0)
                        orderrecord.put("personName", (String) body.get("personName"));
                    if (body.containsKey("sexCode") &&(body.get("sexCode") + "").length() > 0)
                        orderrecord.put("sexCode", (String) body.get("sexCode"));
                    if (body.containsKey("idCard") &&(body.get("idCard") + "").length() > 0)
                        orderrecord.put("idCard", (String) body.get("idCard"));
                    if (body.containsKey("homePlace") &&(body.get("homePlace") + "").length() > 0)
                        orderrecord.put("homePlace", (String) body.get("homePlace"));
                    if (body.containsKey("address") &&(body.get("address") + "").length() > 0)
                        orderrecord.put("address", (String) body.get("address"));

                    if (body.containsKey("zipCode") &&(body.get("zipCode") + "").length() > 0)
                        orderrecord.put("zipCode", (String) body.get("zipCode"));
                    if (body.containsKey("phoneNumber") &&(body.get("phoneNumber") + "").length() > 0)
                        orderrecord.put("phoneNumber", (String) body.get("phoneNumber"));
                    if (body.containsKey("mobileNumber") &&(body.get("mobileNumber") + "").length() > 0)
                        orderrecord.put("mobileNumber", (String) body.get("mobileNumber"));
                    if (body.containsKey("email") &&(body.get("email") + "").length() > 0)
                        orderrecord.put("email", (String) body.get("email"));
                    if (body.containsKey("birthday") &&(body.get("birthday") + "").length() > 0)
                        orderrecord.put("birthday", body.get("birthday"));

                    if (body.containsKey("nationalityCode") &&(body.get("nationalityCode") + "").length() > 0)
                        orderrecord.put("nationalityCode", (String) body.get("nationalityCode"));
                    if (body.containsKey("nationCode") &&(body.get("nationCode") + "").length() > 0)
                        orderrecord.put("nationCode",(String) body.get("nationCode"));
                    if (body.containsKey("maritalStatusCode") &&(body.get("maritalStatusCode") + "").length() > 0)
                        orderrecord.put("maritalStatusCode",(String) body.get("maritalStatusCode"));
/*                    if (body.containsKey("startWorkDate") &&(body.get("startWorkDate") + "").length() > 0)
                        orderrecord.put("startWorkDate", (String) body.get("startWorkDate"));
                    if (body.containsKey("workCode") &&(body.get("workCode") + "").length() > 0)
                        orderrecord.put("workCode", (String) body.get("workCode"));*/

                    if (body.containsKey("educationCode") &&(body.get("educationCode") + "").length() > 0)
                        orderrecord.put("educationCode", (String) body.get("educationCode"));
                    orderrecord.put("createUnit", manaUnitId);
                    orderrecord.put("createUser", userId);
                    orderrecord.put("createTime", new Date());

                    if (body.containsKey("contact") &&(body.get("contact") + "").length() > 0)
                        orderrecord.put("contact", (String) body.get("contact"));
                    if (body.containsKey("contactPhone") &&(body.get("contactPhone") + "").length() > 0)
                        orderrecord.put("contactPhone", (String) body.get("contactPhone"));

                    if (body.containsKey("ybkh") &&(body.get("ybkh") + "").length() > 0)
                        orderrecord.put("ybkh", (String) body.get("ybkh"));
                    if (body.containsKey("smkh") &&(body.get("smkh") + "").length() > 0)
                        orderrecord.put("smkh", (String) body.get("smkh"));

                    Map<String, Object> genValue = dao.doSave("create",
                            BSPHISEntryNames.SMZ_PHICINFO, orderrecord, false);

                }
            }

        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询SMZ
     *
     * @param validate
     *
     * @param body
     */
    public Map<String, Object> querySMZ(Map<String, Object> body, Context ctx){
        UserRoleToken user = UserRoleToken.getCurrent();
        String userId = (String) user.getUserId();
        String manaUnitId = user.getManageUnit().getId();

        try {
            String  idCard=body.get("idCard") + "";
            //待调
            String  ybkh=body.get("YBKH") + "";
            String  smkh=body.get("SMKH") + "";

            //查询条件做优先级，身份证>医保卡号>市民卡号（不做多条件查询）
            Map<String, Object> parameters = new HashMap<String, Object>();
            Map<String, Object> smz;
            StringBuffer hql=new StringBuffer();
            hql.append("select t.personname as PERSONNAME,t.homeplace as HOMEPLACE,t.address as ADDRESS,t.zipcode as ZIPCODE,");
            hql.append("t.idcard as IDCARD,t.SEXCODE as SEXCODE, t.BIRTHDAY as BIRTHDAY,");
            hql.append("t.phonenumber as PHONENUMBER,t.mobilenumber as MOBILENUMBER, t.email as EMAIL,t.nationalitycode as NATIONALITYCODE,");
            hql.append("t.nationcode as NATIONCODE,t.maritalstatuscode as MARITALSTATUSCODE,t.startworkdate as STARTWORKDATE,t.workcode as WORKCODE,");
            hql.append("t.educationcode as EDUCATIONCODE,t.contact as CONTACT,t.contactphone as CONTACTPHONE,t.ybkh as YBKH,t.smkh as SMKH ");
            hql.append("from smz_phicinfo t  ");

            if(!"".equals(idCard)){
                parameters.put("IDCARD",idCard);
                hql.append("where t.IDCARD=:IDCARD ");

                smz = dao.doSqlLoad(hql.toString(), parameters);
                return smz;
            }
            if(!"".equals(ybkh)){
                parameters.put("YBKH",ybkh);
                hql.append("where t.YBKH=:YBKH ");

                smz = dao.doSqlLoad(hql.toString(), parameters);

                return smz;
            }
            if(!"".equals(smkh)){
                parameters.put("SMKH",smkh);
                hql.append("where t.SMKH=:SMKH ");

                smz = dao.doSqlLoad(hql.toString(), parameters);
                return smz;
            }
        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 加载SMZ并格式化
     *
     * @param validate
     *
     * @param body
     * @throws ValidateException
     * @throws ModelDataOperationException
     */
    public List<Map<String, Object>> loadSMZ(Map<String, Object> body, Context ctx)
            throws ServiceException, ModelDataOperationException {

        Map<String, Object> smz=querySMZ(body,ctx);
        Map<String, Object> smznew= new HashMap<String, Object>();
        if(smz!=null){
            //数据处理，与sc文件字段匹配
            if ((smz.get("PERSONNAME") + "").length() > 0)
                smznew.put("personName", (smz.get("PERSONNAME") + ""));
            if ((smz.get("SEXCODE") + "").length() > 0)
                smznew.put("sexCode", smz.get("SEXCODE")+"");
            if ((smz.get("IDCARD") + "").length() > 0)
                smznew.put("idCard", smz.get("IDCARD")+"");
            if ((smz.get("HOMEPLACE") + "").length() > 0)
                smznew.put("homePlace",smz.get("HOMEPLACE")+"");
            if ((smz.get("ADDRESS") + "").length() > 0)
                smznew.put("address",  smz.get("ADDRESS")+"");

            if ((smz.get("ZIPCODE") + "").length() > 0)
                smznew.put("zipCode",  smz.get("ZIPCODE")+"");
            if ((smz.get("PHONENUMBER") + "").length() > 0)
                smznew.put("phoneNumber", smz.get("PHONENUMBER")+"");
            if ((smz.get("MOBILENUMBER") + "").length() > 0)
                smznew.put("mobileNumber", smz.get("MOBILENUMBER")+"");
            if ((smz.get("EMAIL") + "").length() > 0)
                smznew.put("email",  smz.get("EMAIL")+"");
            if ((smz.get("BIRTHDAY") + "").length() > 0)
                smznew.put("birthday", smz.get("BIRTHDAY")+"");

            if ((smz.get("NATIONALITYCODE") + "").length() > 0)
                smznew.put("nationalityCode",  smz.get("NATIONALITYCODE")+"");
            if ((smz.get("NATIONCODE") + "").length() > 0)
                smznew.put("nationCode", smz.get("NATIONCODE")+"");
            if ((smz.get("MARITALSTATUSCODE") + "").length() > 0)
                smznew.put("maritalStatusCode",smz.get("MARITALSTATUSCODE")+"");
            if ((smz.get("STARTWORKDATE") + "").length() > 0)
                smznew.put("startWorkDate",  smz.get("STARTWORKDATE")+"");
            if ((smz.get("WORKCODE") + "").length() > 0)
                smznew.put("workCode",  smz.get("WORKCODE")+"");

            if ((smz.get("EDUCATIONCODE") + "").length() > 0)
                smznew.put("educationCode", smz.get("EDUCATIONCODE")+"");
            if ((smz.get("CONTACT") + "").length() > 0)
                smznew.put("contact", smz.get("CONTACT")+"");
            if ((smz.get("CONTACTPHONE") + "").length() > 0)
                smznew.put("contactPhone", smz.get("CONTACTPHONE")+"");

            //卡号待看


            if(smznew!=null){
                smznew.put("smz","1");
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                list.add(smznew);
                return list;
            }
        }

        return null;
    }
}
