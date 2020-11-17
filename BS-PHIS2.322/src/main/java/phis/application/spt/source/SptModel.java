package phis.application.spt.source;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import org.json.JSONObject;
import phis.application.mds.source.MedicineUtils;
import phis.application.sto.source.StorehouseCheckInOutModel;


public class SptModel implements BSPHISEntryNames {
    protected static BaseDAO dao;
    protected Logger logger = LoggerFactory.getLogger(SptModel.class);

    public SptModel(BaseDAO dao) {
        this.dao = dao;
    }

    /**
     * 省平台药品下载
     *
     * @param body
     * @param ctx
     * @return
     * @throws ModelDataOperationException
     */

    @SuppressWarnings("unchecked")
    public Map<String, Object> doMedicinesDownloads(Map<String, Object> body,
                                                    Context ctx) throws ModelDataOperationException {
        UserRoleToken user = UserRoleToken.getCurrent();
        Map<String, Object> req = new HashMap<String, Object>();
        String mac = getMacAddress();
        String ip = getIpAddress();
        String jgid = user.getManageUnitId();
        String HospitalCode = "";
        String Kssj = (String) body.get("date");
        if (jgid.length() >= 9) {
            HospitalCode = jgid.substring(0, 9);
        } else {
            HospitalCode = jgid;
        }
        String sql = "select a.organizname as ORGANIZNAME from sys_organization a where a.organizcode='" + HospitalCode + "'";
        Map<String, Object> sql1 = null;
        try {
            sql1 = dao.doSqlLoad(sql, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String sql2 = "select replace(a.code,'_YP','') as CODE,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String CODE = (String) sql21.get("CODE");
        String HostName = (String) sql1.get("ORGANIZNAME");
        try {

            String APIURL = (String) body.get("url");
            String json = "{\n " +
                    "\"BusinessType\":\"" + "YY003\",\n" +
                    "\"HospitalCode\":\"" + CODE + "\",\n" +
                    "\"IP\":\"" + ip + "\",\n" +
                    "\"MAC\":\"" + mac + "\",\n" +
                    "\"HostName\":\"" + jgid + "\",\n" +
                    "\"Data\"" + ":{\n " +
                    "\"Kssj\":\"" + Kssj + "\",\n" +
                    "\"Count\":\"" + "0\",\n" +
                    "\"Page\":\"" + "1\"\n" +
                    " } }";
            String res = httpURLPOSTCase(APIURL, json);
            JSONObject jorm = new JSONObject(res);
            if (jorm.optString("Code").equals("0")) {
                org.json.JSONArray Data = jorm.optJSONArray("Data");
                int length = Data.length();
                if (length == 0) {
                    req.put("msg", "该时间段内无新增药品，请重新选择时间！");
                    req.put("code", "249");
                } else {
                    for (int i = 0; i < Data.length(); i++) {
                        JSONObject one = Data.getJSONObject(i);
                        Map<String, Object> record = new HashMap<String, Object>();
                        record.put("UNICODE", one.optString("UniCode") == null ? null : one.optString("UniCode"));
                        record.put("NAME", one.optString("Name") == null ? null : one.optString("Name"));
                        record.put("TRADENAME", one.optString("TradeName") == null ? null : one.optString("TradeName"));
                        record.put("PACKSPEC", one.optString("PackSpec") == null ? null : one.optString("PackSpec"));
                        record.put("SPEC", one.optString("Spec") == null ? null : one.optString("Spec"));
                        record.put("PACKUNIT", one.optString("PackUnit") == null ? null : one.optString("PackUnit"));
                        record.put("PACKQUANTITY", one.optDouble("PackQuantity"));
                        record.put("UNIT", one.optString("Unit") == null ? null : one.optString("Unit"));
                        record.put("DOSAGEFORM", one.optString("DosageForm") == null ? null : one.optString("DosageForm"));
                        record.put("SDC", one.optString("SDC"));
                        record.put("APPROVALNUMBER", one.optString("ApprovalNumber") == null ? null : one.optString("ApprovalNumber"));
                        record.put("APPROVALTIME", one.optString("ApprovalTime") == null ? null : one.optString("ApprovalTime"));
                        record.put("PRICE", one.optDouble("Price"));
                        record.put("RETAILPRICE", one.optDouble("Price"));
                        record.put("CATEGORY", one.optString("Category") == null ? null : one.optDouble("Category"));
                        record.put("PRODUCTSOURCE", one.optString("ProductSource") == null ? null : one.optString("ProductSource"));
                        record.put("PROPERTY", one.optString("Property") == null ? null : one.optString("Property"));
                        record.put("ESSENTIAL", one.optString("Essential") == null ? null : one.optString("Essential"));
                        record.put("MANUFACTURERCODE", one.optString("ManufacturerCode") == null ? null : one.optString("ManufacturerCode"));
                        record.put("MANUFACTURER", one.optString("Manufacturer") == null ? null : one.optString("Manufacturer"));
                        record.put("JGID", HospitalCode);
                        List<Map<String, Object>> ypxx = this.getypxxbyypxh(one
                                .get("UniCode").toString());
                        if (ypxx != null && ypxx.size() > 0) {
                            // 已有药品信息不做保存
                        } else {
                            try {
                                dao.doSave("create", BSPHISEntryNames.YK_TYPK_SPT,
                                        record, false);
                            } catch (ValidateException e) {
                                e.printStackTrace();
                            } catch (PersistentDataOperationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else if (jorm.optString("Code").equals("1")) {
                req.put("msg", "省平台下载药品信息接口返回：”错误！“");
                req.put("code", "201");
            } else if (jorm.optString("Code").equals("2")) {
                req.put("msg", "省平台下载药品信息接口返回：”部分错误！“");
                req.put("code", "202");
            } else if (jorm.optString("Code").equals("3")) {
                req.put("msg", "省平台下载药品信息接口返回：”与历史数据不匹配，无法新增/更新！“");
                req.put("code", "203");
            } else if (jorm.optString("Code").equals("10000")) {
                req.put("msg", "省平台下载药品信息接口返回：”未知错误！“");
                req.put("code", "204");
            } else if (jorm.optString("Code").equals("10001")) {
                req.put("msg", "省平台下载药品信息接口返回：”未指定业务类型[BusinessType]！“");
                req.put("code", "205");
            } else if (jorm.optString("Code").equals("10002")) {
                req.put("msg", "省平台下载药品信息接口返回：”无效的业务类型[BusinessType]！“");
                req.put("code", "206");
            } else if (jorm.optString("Code").equals("10003")) {
                req.put("msg", "省平台下载药品信息接口返回：”业务类型[BusinessType]在此请求中不支持！“");
                req.put("code", "207");
            } else if (jorm.optString("Code").equals("10004")) {
                req.put("msg", "省平台下载药品信息接口返回：”业务类型[BusinessType]未配置数据流！“");
                req.put("code", "208");
            } else if (jorm.optString("Code").equals("10005")) {
                req.put("msg", "省平台下载药品信息接口返回：”未知用户！“");
                req.put("code", "209");
            } else if (jorm.optString("Code").equals("10006")) {
                req.put("msg", "省平台下载药品信息接口返回：”密码不正确！“");
                req.put("code", "210");
            } else if (jorm.optString("Code").equals("10007")) {
                req.put("msg", "省平台下载药品信息接口返回：”用户未设置数据接收地址[PushUrl]！“");
                req.put("code", "211");
            } else if (jorm.optString("Code").equals("10010")) {
                req.put("msg", "省平台下载药品信息接口返回：”未提交单据明细数据！“");
                req.put("code", "212");
            } else if (jorm.optString("Code").equals("10011")) {
                req.put("msg", "省平台下载药品信息接口返回：”提交数据包含重复的商品统一编码[UniCode]！“");
                req.put("code", "213");
            } else if (jorm.optString("Code").equals("10012")) {
                req.put("msg", "省平台下载药品信息接口返回：”生产厂家/供应商类型有误[OrgTypeCode]！“");
                req.put("code", "214");
            } else if (jorm.optString("Code").equals("10013")) {
                req.put("msg", "省平台下载药品信息接口返回：”单据明细序号[Seq]重复！“");
                req.put("code", "215");
            } else if (jorm.optString("Code").equals("10014")) {
                req.put("msg", "省平台下载药品信息接口返回：”单据明细记录数过多！“");
                req.put("code", "216");
            } else if (jorm.optString("Code").equals("10015")) {
                req.put("msg", "省平台下载药品信息接口返回：”医院代码不能为空！“");
                req.put("code", "217");
            } else if (jorm.optString("Code").equals("10016")) {
                req.put("msg", "省平台下载药品信息接口返回：”医院名称不能为空！“");
                req.put("code", "218");
            } else if (jorm.optString("Code").equals("10017")) {
                req.put("msg", "省平台下载药品信息接口返回：”科室编码不能为空！“");
                req.put("code", "219");
            } else if (jorm.optString("Code").equals("10018")) {
                req.put("msg", "省平台下载药品信息接口返回：”配送点(库房)编码不能为空空！“");
                req.put("code", "220");
            } else if (jorm.optString("Code").equals("11001")) {
                req.put("msg", "省平台下载药品信息接口返回：”医院代码[HospitalCode]在平台字典中不存在！“");
                req.put("code", "221");
            } else if (jorm.optString("Code").equals("11002")) {
                req.put("msg", "省平台下载药品信息接口返回：”商品统一编码[UniCode]在平台字典中不存在！“");
                req.put("code", "222");
            } else if (jorm.optString("Code").equals("11003")) {
                req.put("msg", "省平台下载药品信息接口返回：”配送点[DistributionSiteCode]在平台字典中不存在！“");
                req.put("code", "223");
            } else if (jorm.optString("Code").equals("11004")) {
                req.put("msg", "省平台下载药品信息接口返回：”供应商[SupplierCode]在平台字典中不存在！“");
                req.put("code", "224");
            } else if (jorm.optString("Code").equals("11005")) {
                req.put("msg", "省平台下载药品信息接口返回：”厂家在平台字典中不存在！“");
                req.put("code", "225");
            } else if (jorm.optString("Code").equals("11006")) {
                req.put("msg", "省平台下载药品信息接口返回：” 企业已存在待审核数据！“");
                req.put("code", "226");
            } else if (jorm.optString("Code").equals("11011")) {
                req.put("msg", "省平台下载药品信息接口返回：”药监下载数据暂不支持修改！“");
                req.put("code", "227");
            } else if (jorm.optString("Code").equals("11012")) {
                req.put("msg", "省平台下载药品信息接口返回：”产品品名已存在！“");
                req.put("code", "228");
            } else if (jorm.optString("Code").equals("11013")) {
                req.put("msg", "省平台下载药品信息接口返回：”产品品名不存在！“");
                req.put("code", "229");
            } else if (jorm.optString("Code").equals("11014")) {
                req.put("msg", "省平台下载药品信息接口返回：”产品规格已存在！“");
                req.put("code", "230");
            } else if (jorm.optString("Code").equals("11015")) {
                req.put("msg", "省平台下载药品信息接口返回：”未获取到产品类型！“");
                req.put("code", "231");
            } else if (jorm.optString("Code").equals("11016")) {
                req.put("msg", "省平台下载药品信息接口返回：”品名不是待审核状态，如果品名已通过审核，请传入品名 Code！“");
                req.put("code", "232");
            } else if (jorm.optString("Code").equals("11017")) {
                req.put("msg", "省平台下载药品信息接口返回：”产品品名存在待审核数据！“");
                req.put("code", "233");
            } else if (jorm.optString("Code").equals("11018")) {
                req.put("msg", "省平台下载药品信息接口返回：”产品规格存在待审核数据！“");
                req.put("code", "234");
            } else if (jorm.optString("Code").equals("11019")) {
                req.put("msg", "省平台下载药品信息接口返回：”发票号不匹配！“");
                req.put("code", "235");
            } else if (jorm.optString("Code").equals("11020")) {
                req.put("msg", "省平台下载药品信息接口返回：”平台代码[PlateformCode]不存在！“");
                req.put("code", "236");
            } else if (jorm.optString("Code").equals("12000")) {
                req.put("msg", "省平台下载药品信息接口返回：”数据已上传！“");
                req.put("code", "237");
            } else if (jorm.optString("Code").equals("20000")) {
                req.put("msg", "省平台下载药品信息接口返回：”数据不需要处理(更新状态等)！“");
                req.put("code", "238");
            } else if (jorm.optString("Code").equals("20001")) {
                req.put("msg", "省平台下载药品信息接口返回：”数据提交不成功！“");
                req.put("code", "239");
            } else if (jorm.optString("Code").equals("20002")) {
                req.put("msg", "省平台下载药品信息接口返回：”数据提交时发生错误！“");
                req.put("code", "240");
            } else if (jorm.optString("Code").equals("20003")) {
                req.put("msg", "省平台下载药品信息接口返回：”需要更新的数据不存在！“");
                req.put("code", "241");
            } else if (jorm.optString("Code").equals("20004")) {
                req.put("msg", "省平台下载药品信息接口返回：”数据已作废！“");
                req.put("code", "242");
            } else if (jorm.optString("Code").equals("20005")) {
                req.put("msg", "省平台下载药品信息接口返回：”数据已有后续流程，无法更新！“");
                req.put("code", "243");
            } else if (jorm.optString("Code").equals("40101")) {
                req.put("msg", "省平台下载药品信息接口返回：”用户名不存在！“");
                req.put("code", "244");
            } else if (jorm.optString("Code").equals("40102")) {
                req.put("msg", "省平台下载药品信息接口返回：”密码错误！“");
                req.put("code", "245");
            } else if (jorm.optString("Code").equals("40300")) {
                req.put("msg", "省平台下载药品信息接口返回：”未授权！“");
                req.put("code", "246");
            } else if (jorm.optString("Code").equals("40301")) {
                req.put("msg", "省平台下载药品信息接口返回：”过期！“");
                req.put("code", "247");
            } else if (jorm.optString("Code").equals("40302")) {
                req.put("msg", "省平台下载药品信息接口返回：”未到开始时间！“");
                req.put("code", "248");
            } else {
                req.put("msg", "省平台下载药品信息接口返回错误代码不在字典范围内，错误代码为”" + jorm.optString("Code") + "“；接口返回内容为：" + jorm.optString("Message"));
                req.put("code", jorm.optString("Code"));
            }
        } catch (Exception e) {
            logger.error("请求省平台药品接口.", e);
            throw new ModelDataOperationException(ServiceCode.CODE_ERROR,
                    "请求省平台药品接口.");
        }
        return req;
    }


    /**
     * 省平台供应商下载
     *
     * @param body
     * @param ctx
     * @return
     * @throws ModelDataOperationException
     */

    @SuppressWarnings("unchecked")
    public Map<String, Object> doFactoryDownload(Map<String, Object> body,
                                                 Context ctx) throws ModelDataOperationException {
        UserRoleToken user = UserRoleToken.getCurrent();
        Map<String, Object> req = new HashMap<String, Object>();
        String mac = getMacAddress();
        String ip = getIpAddress();
        String jgid = user.getManageUnitId();
        String HospitalCode = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String Kssj = sdf1.format(new Date());
        if (jgid.length() >= 9) {
            HospitalCode = jgid.substring(0, 9);
        } else {
            HospitalCode = jgid;
        }
        String sql = "select a.organizname as ORGANIZNAME from sys_organization a where a.organizcode='" + HospitalCode + "'";
        Map<String, Object> sql1 = null;
        try {
            sql1 = dao.doSqlLoad(sql, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String sql2 = "select replace(a.code,'_YP','') as CODE,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String CODE = (String) sql21.get("CODE");
        String HostName = (String) sql1.get("ORGANIZNAME");
        try {

            String APIURL = (String) body.get("url");
            String json = "{\n " +
                    "\"BusinessType\":\"" + "YY004\",\n" +
                    "\"HospitalCode\":\"" + CODE + "\",\n" +
                    "\"IP\":\"" + ip + "\",\n" +
                    "\"MAC\":\"" + mac + "\",\n" +
                    "\"HostName\":\"" + jgid + "\",\n" +
                    "\"Data\"" + ":{\n " +
                    "\"Count\":\"" + "0\",\n" +
                    " } }";
            String res = httpURLPOSTCase(APIURL, json);
            JSONObject jorm = new JSONObject(res);
            if (jorm.optString("Code").equals("0")) {
                org.json.JSONArray Data = jorm.optJSONArray("Data");
                int length = Data.length();
                if (length == 0) {
                    req.put("msg", "无新增供应商下载！");
                    req.put("code", "249");
                } else {
                    for (int i = 0; i < Data.length(); i++) {
                        JSONObject one = Data.getJSONObject(i);
                        Map<String, Object> record = new HashMap<String, Object>();
                        record.put("DWMC", one.optString("Name") == null ? null : one.optString("Name"));
                        record.put("LXR", one.optString("Contacter") == null ? null : one.optString("Contacter"));
                        record.put("LXDH", one.optString("ContactNumber") == null ? null : one.optString("ContactNumber"));
                        record.put("DWDZ", one.optString("CompanyAddress") == null ? null : one.optString("CompanyAddress"));
                        record.put("DWZH", one.optString("Code") == null ? null : one.optString("Code"));
                        record.put("KHYH", one.optString("OrgCode") == null ? null : one.optString("OrgCode"));
                        record.put("PYDM", one.optString("SimpleCharacter") == null ? null : one.optString("SimpleCharacter"));
                        String Type = one.optString("Type") == null ? null : one.optString("Type");
                        String Name = one.optString("Name") == null ? null : one.optString("Name");
                        if (Type.equals("2")) {
                            Map<String, Object> record1 = new HashMap<String, Object>();
                            List<Map<String, Object>> gysxx = this.getjhdwbydwmc(one.get("Name").toString());
                            record1.put("body", gysxx);
                            String xzgysxx = record1.get("body").toString();

                            if (xzgysxx != null && xzgysxx.length() > 0 && xzgysxx == "[]") {
                                String update = " update YK_JHDW t set t.LXR=:LXR,t.LXDH=:LXDH,t.DWDZ=:DWDZ,t.DWZH:=DWZH,t.KHYH=:KHYH where t.DWMC='" + Name + "'";
                                Map<String, Object> p = new HashMap<String, Object>();
                                p.put("LXR", record.get("LXR"));
                                p.put("LXDH", record.get("LXDH"));
                                p.put("KHYH", record.get("KHYH"));
                                p.put("DWDZ", record.get("DWDZ"));
                                p.put("DWZH", record.get("DWZH"));
                                try {
                                    dao.doUpdate(update, p);
                                } catch (PersistentDataOperationException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                // 已有工供应商信息不做保存
                            } else {
                                try {
                                    dao.doSave("create", BSPHISEntryNames.YK_JHDW,
                                            record, false);
                                } catch (ValidateException e) {
                                    e.printStackTrace();
                                } catch (PersistentDataOperationException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } else if (jorm.optString("Code").equals("1")) {
                req.put("msg", "下载供应商信息接口返回：”错误！“");
                req.put("code", "201");
            } else if (jorm.optString("Code").equals("2")) {
                req.put("msg", "下载供应商信息接口返回：”部分错误！“");
                req.put("code", "202");
            } else if (jorm.optString("Code").equals("3")) {
                req.put("msg", "下载供应商信息接口返回：”与历史数据不匹配，无法新增/更新！“");
                req.put("code", "203");
            } else if (jorm.optString("Code").equals("10000")) {
                req.put("msg", "下载供应商信息接口返回：”未知错误！“");
                req.put("code", "204");
            } else if (jorm.optString("Code").equals("10001")) {
                req.put("msg", "下载供应商信息接口返回：”未指定业务类型[BusinessType]！“");
                req.put("code", "205");
            } else if (jorm.optString("Code").equals("10002")) {
                req.put("msg", "下载供应商信息接口返回：”无效的业务类型[BusinessType]！“");
                req.put("code", "206");
            } else if (jorm.optString("Code").equals("10003")) {
                req.put("msg", "下载供应商信息接口返回：”业务类型[BusinessType]在此请求中不支持！“");
                req.put("code", "207");
            } else if (jorm.optString("Code").equals("10004")) {
                req.put("msg", "下载供应商信息接口返回：”业务类型[BusinessType]未配置数据流！“");
                req.put("code", "208");
            } else if (jorm.optString("Code").equals("10005")) {
                req.put("msg", "下载供应商信息接口返回：”未知用户！“");
                req.put("code", "209");
            } else if (jorm.optString("Code").equals("10006")) {
                req.put("msg", "下载供应商信息接口返回：”密码不正确！“");
                req.put("code", "210");
            } else if (jorm.optString("Code").equals("10007")) {
                req.put("msg", "下载供应商信息接口返回：”用户未设置数据接收地址[PushUrl]！“");
                req.put("code", "211");
            } else if (jorm.optString("Code").equals("10010")) {
                req.put("msg", "下载供应商信息接口返回：”未提交单据明细数据！“");
                req.put("code", "212");
            } else if (jorm.optString("Code").equals("10011")) {
                req.put("msg", "下载供应商信息接口返回：”提交数据包含重复的商品统一编码[UniCode]！“");
                req.put("code", "213");
            } else if (jorm.optString("Code").equals("10012")) {
                req.put("msg", "下载供应商信息接口返回：”生产厂家/供应商类型有误[OrgTypeCode]！“");
                req.put("code", "214");
            } else if (jorm.optString("Code").equals("10013")) {
                req.put("msg", "下载供应商信息接口返回：”单据明细序号[Seq]重复！“");
                req.put("code", "215");
            } else if (jorm.optString("Code").equals("10014")) {
                req.put("msg", "下载供应商信息接口返回：”单据明细记录数过多！“");
                req.put("code", "216");
            } else if (jorm.optString("Code").equals("10015")) {
                req.put("msg", "下载供应商信息接口返回：”医院代码不能为空！“");
                req.put("code", "217");
            } else if (jorm.optString("Code").equals("10016")) {
                req.put("msg", "下载供应商信息接口返回：”医院名称不能为空！“");
                req.put("code", "218");
            } else if (jorm.optString("Code").equals("10017")) {
                req.put("msg", "下载供应商信息接口返回：”科室编码不能为空！“");
                req.put("code", "219");
            } else if (jorm.optString("Code").equals("10018")) {
                req.put("msg", "下载供应商信息接口返回：”配送点(库房)编码不能为空空！“");
                req.put("code", "220");
            } else if (jorm.optString("Code").equals("11001")) {
                req.put("msg", "下载供应商信息接口返回：”医院代码[HospitalCode]在平台字典中不存在！“");
                req.put("code", "221");
            } else if (jorm.optString("Code").equals("11002")) {
                req.put("msg", "下载供应商信息接口返回：”商品统一编码[UniCode]在平台字典中不存在！“");
                req.put("code", "222");
            } else if (jorm.optString("Code").equals("11003")) {
                req.put("msg", "下载供应商信息接口返回：”配送点[DistributionSiteCode]在平台字典中不存在！“");
                req.put("code", "223");
            } else if (jorm.optString("Code").equals("11004")) {
                req.put("msg", "下载供应商信息接口返回：”供应商[SupplierCode]在平台字典中不存在！“");
                req.put("code", "224");
            } else if (jorm.optString("Code").equals("11005")) {
                req.put("msg", "下载供应商信息接口返回：”厂家在平台字典中不存在！“");
                req.put("code", "225");
            } else if (jorm.optString("Code").equals("11006")) {
                req.put("msg", "下载供应商信息接口返回：” 企业已存在待审核数据！“");
                req.put("code", "226");
            } else if (jorm.optString("Code").equals("11011")) {
                req.put("msg", "下载供应商信息接口返回：”药监下载数据暂不支持修改！“");
                req.put("code", "227");
            } else if (jorm.optString("Code").equals("11012")) {
                req.put("msg", "下载供应商信息接口返回：”产品品名已存在！“");
                req.put("code", "228");
            } else if (jorm.optString("Code").equals("11013")) {
                req.put("msg", "下载供应商信息接口返回：”产品品名不存在！“");
                req.put("code", "229");
            } else if (jorm.optString("Code").equals("11014")) {
                req.put("msg", "下载供应商信息接口返回：”产品规格已存在！“");
                req.put("code", "230");
            } else if (jorm.optString("Code").equals("11015")) {
                req.put("msg", "下载供应商信息接口返回：”未获取到产品类型！“");
                req.put("code", "231");
            } else if (jorm.optString("Code").equals("11016")) {
                req.put("msg", "下载供应商信息接口返回：”品名不是待审核状态，如果品名已通过审核，请传入品名 Code！“");
                req.put("code", "232");
            } else if (jorm.optString("Code").equals("11017")) {
                req.put("msg", "下载供应商信息接口返回：”产品品名存在待审核数据！“");
                req.put("code", "233");
            } else if (jorm.optString("Code").equals("11018")) {
                req.put("msg", "下载供应商信息接口返回：”产品规格存在待审核数据！“");
                req.put("code", "234");
            } else if (jorm.optString("Code").equals("11019")) {
                req.put("msg", "下载供应商信息接口返回：”发票号不匹配！“");
                req.put("code", "235");
            } else if (jorm.optString("Code").equals("11020")) {
                req.put("msg", "下载供应商信息接口返回：”平台代码[PlateformCode]不存在！“");
                req.put("code", "236");
            } else if (jorm.optString("Code").equals("12000")) {
                req.put("msg", "下载供应商信息接口返回：”数据已上传！“");
                req.put("code", "237");
            } else if (jorm.optString("Code").equals("20000")) {
                req.put("msg", "下载供应商信息接口返回：”数据不需要处理(更新状态等)！“");
                req.put("code", "238");
            } else if (jorm.optString("Code").equals("20001")) {
                req.put("msg", "下载供应商信息接口返回：”数据提交不成功！“");
                req.put("code", "239");
            } else if (jorm.optString("Code").equals("20002")) {
                req.put("msg", "下载供应商信息接口返回：”数据提交时发生错误！“");
                req.put("code", "240");
            } else if (jorm.optString("Code").equals("20003")) {
                req.put("msg", "下载供应商信息接口返回：”需要更新的数据不存在！“");
                req.put("code", "241");
            } else if (jorm.optString("Code").equals("20004")) {
                req.put("msg", "下载供应商信息接口返回：”数据已作废！“");
                req.put("code", "242");
            } else if (jorm.optString("Code").equals("20005")) {
                req.put("msg", "下载供应商信息接口返回：”数据已有后续流程，无法更新！“");
                req.put("code", "243");
            } else if (jorm.optString("Code").equals("40101")) {
                req.put("msg", "下载供应商信息接口返回：”用户名不存在！“");
                req.put("code", "244");
            } else if (jorm.optString("Code").equals("40102")) {
                req.put("msg", "下载供应商信息接口返回：”密码错误！“");
                req.put("code", "245");
            } else if (jorm.optString("Code").equals("40300")) {
                req.put("msg", "下载供应商信息接口返回：”未授权！“");
                req.put("code", "246");
            } else if (jorm.optString("Code").equals("40301")) {
                req.put("msg", "下载供应商信息接口返回：”过期！“");
                req.put("code", "247");
            } else if (jorm.optString("Code").equals("40302")) {
                req.put("msg", "下载供应商信息接口返回：”未到开始时间！“");
                req.put("code", "248");
            } else {
                req.put("msg", "下载供应商信息接口返回错误代码不在字典范围内，错误代码为”" + jorm.optString("Code") + "“；接口返回内容为：" + jorm.optString("Message"));
                req.put("code", jorm.optString("Code"));
            }
        } catch (Exception e) {
            logger.error("请求下载供应商信息接口：", e);
            throw new ModelDataOperationException(ServiceCode.CODE_ERROR,
                    "请求下载供应商信息接口：");
        }
        return req;
    }

    /**
     * 省平台配送单下载
     *
     * @param spt
     * @param ctx
     * @return
     * @throws ModelDataOperationException
     */

    @SuppressWarnings("unchecked")
    public Map<String, Object> doDistributionDownloads(Map<String, Object> spt,
                                                       Context ctx) throws ModelDataOperationException {
        UserRoleToken user = UserRoleToken.getCurrent();
        Map<String, Object> body = new HashMap<String, Object>();

        Map<String, Object> req = new HashMap<String, Object>();
        String mac = getMacAddress();
        String ip = getIpAddress();
        String jgid = user.getManageUnitId();
        String HospitalCode = "";
        String Kssj = (String) spt.get("KSRQ");
        String Jssj = (String) spt.get("JSRQ");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String Lrrq = sdf1.format(new Date());
        long storehouseId = MedicineUtils.parseLong(spt.get("storehouseId"));
        if (jgid.length() >= 9) {
            HospitalCode = jgid.substring(0, 9);
        } else {
            HospitalCode = jgid;
        }
        String sql = "select a.organizname as ORGANIZNAME from sys_organization a where a.organizcode='" + HospitalCode + "'";
        Map<String, Object> sql1 = null;
        try {
            sql1 = dao.doSqlLoad(sql, null);
        } catch (PersistentDataOperationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String sql2 = "select replace(a.code,'_YP','') as CODE,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String CODE = (String) sql21.get("CODE");
        String HostName = (String) sql1.get("ORGANIZNAME");
        try {

            //String APIURL = (String) spt.get("url");
            String APIURL = "http://10.2.200.202:8042/dep/business/post";
            String json = "{\n " +
                    "\"BusinessType\":\"" + "YY102\",\n" +
                    "\"HospitalCode\":\"" + CODE + "\",\n" +
                    "\"IP\":\"" + ip + "\",\n" +
                    "\"MAC\":\"" + mac + "\",\n" +
                    "\"HostName\":\"" + jgid + "\",\n" +
                    "\"Data\"" + ":{\n " +
                    "\"DownloadState\":\"" + "0\",\n" +
                    "\"Kssj\":\"" + Kssj + "\",\n" +
                    "\"Jssj\":\"" + Jssj + "\",\n" +
                    "\"Managed\":\"" + "null\"\n" +
                    " }" +
                    " }";
            String res = httpURLPOSTCase(APIURL, json);
            //测试 数据
//			res = "{\"Code\":0,\"Completed\":true,\"Data\":[{\"Accepter\":null,\"Amount\":83.6000,\"Auditor\":null,\"Barcode\":null,\"BillSate\":null,\"CreateTime\":\"2020-03-18 10:12:41\",\"Creator\":\"白马中心卫生院\",\"DeliverDate\":null,\"DetectionQuantity\":0.0000,\"DistributionDetail\":[{\"AbandonReason\":null,\"AbandonType\":null,\"AcceptanceConclusion\":null,\"Acceptor\":null,\"AmbientTempEnd\":0.00,\"AmbientTempStart\":0.00,\"Amount\":83.6000,\"ApprovalCertificate\":null,\"ApprovalNumber\":null,\"Barcode\":null,\"BarcodeStyle\":null,\"BatchNO\":\"A200211\",\"CheckReportFileName\":null,\"CheckReportPath\":null,\"CheckReportStatus\":null,\"ContractNo\":null,\"DeliveryMethod\":null,\"DeptCode\":null,\"DeptName\":null,\"DetectionInvoiceDate\":null,\"DetectionInvoiceNo\":null,\"DetectionMemo\":null,\"DetectionQuantity\":20.0000,\"DetectionState\":null,\"DetectionTime\":null,\"ExtlPackQuality\":null,\"ExtlPackWeeping\":null,\"HisCode\":null,\"ID\":\"202003181013360000010001\",\"ID3\":null,\"InspectionReport\":null,\"Inspector\":null,\"IntlExtlAccordance\":null,\"IntlPackIntact\":null,\"IntlPackLeaking\":null,\"IntlPackWeeping\":null,\"InvalidDate\":\"2021-07-31\",\"InvoiceAmount\":0.0000,\"InvoiceDate\":null,\"InvoiceNo\":\"36986691\",\"LogisticsCode\":null,\"Manufacturer\":\"南京正大天晴制药有限公司\",\"MasterBarCode\":null,\"Memo\":null,\"Name\":\"瑞舒伐他汀钙片\",\"PackSpec\":\"10mg *14 / 盒\",\"Price\":4.1800,\"ProductTempEnd\":0.00,\"ProductTempStart\":0.00,\"ProductionDate\":\"2011-11-11\",\"PurchaseCategory\":null,\"PurchaseDetailID\":null,\"PurchaseDetailID3\":null,\"PurchaseDetailSeq\":1,\"PurchaseOrderNo\":null,\"PurchaseQuantity\":0.0000,\"Quantity\":20.0000,\"ScanStatus\":0,\"SerialNumber\":null,\"SlaveBarCode\":null,\"Spec\":\"10mg\",\"State\":\"0\",\"StoreCode\":null,\"StoreKeeper\":null,\"StoreName\":null,\"Supervisor\":null,\"UniCode\":\"GJLM01020101\",\"UniCode3\":null,\"Unit\":\"盒\",\"UnqualifiedReason\":null}],\"DistributionSite\":null,\"DistributionSiteCode\":\"1\",\"HospitalCode\":\"H00121\",\"HospitalName\":\"白马中心卫生院\",\"ID\":\"20200318101336000001\",\"ID3\":null,\"Managed\":true,\"Memo\":null,\"OrderNO\":\"RN202003180001\",\"OrderType\":null,\"ProductType\":\"0\",\"PurchaseOrderNo\":null,\"SaleType\":null,\"Saler\":null,\"State\":\"0\",\"SumQuantity\":20.0000,\"Supplier\":\"江苏省医药有限公司\",\"SupplierCode\":\"FR20T0000004000000006004\",\"Unit\":null,\"supplier2\":\"江苏省医药有限公司\"}],\"Message\":\"\"}";
            JSONObject jorm = new JSONObject(res);
            if (jorm.optString("Code").equals("0")) {
                org.json.JSONArray Data = jorm.optJSONArray("Data");
                int length = Data.length();
                if (length == 0) {
                    req.put("msg", "该时间段内未下载的配送单，请重新选择时间！");
                    req.put("code", "249");
                }
                if (req.size() == 0) {
                    for (int i = 0; i < Data.length(); i++) {
                        JSONObject one = Data.getJSONObject(i);
                        org.json.JSONArray DistributionDetail = one.optJSONArray("DistributionDetail");
                        String ID = one.optString("ID") == null ? null : one.optString("ID");
                        String DWMC = one.optString("Supplier") == null ? null : one.optString("Supplier");
                        String CreateTime = one.optString("CreateTime") == null ? null : one.optString("CreateTime");
                        List<Map<String, Object>> gysxx = this.getjhdwbydwmc(DWMC);
                        if (gysxx == null || gysxx.size() == 0) {
                            Map<String, Object> jhdw = new HashMap<String, Object>();
                            jhdw.put("DWMC", one.optString("Supplier") == null ? null : one.optString("Supplier"));// 进货单位
                            jhdw.put("LXR", "供应商");
                            jhdw.put("LXDH", "NULL");
                            jhdw.put("DWDZ", "NULL");
                            jhdw.put("DWZH", "NULL");
                            jhdw.put("KHYH", "NULL");
                            jhdw.put("PYDM", "NULL");
                            jhdw.put("ZFPB", "0");
                            jhdw.put("BZXX", "省平台供应商下载");
                            try {
                                dao.doSave("create", BSPHISEntryNames.YK_JHDW, jhdw, false);
                            } catch (ValidateException e) {
                                e.printStackTrace();
                            } catch (PersistentDataOperationException e) {
                                e.printStackTrace();
                            }
                        }
                        String sql4 = "select DWXH from YK_JHDW where DWMC=" + "'" + DWMC + "'";
                        Map<String, Object> sql41 = null;
                        try {
                            sql41 = dao.doSqlLoad(sql4, null);
                        } catch (PersistentDataOperationException e1) {
                            e1.printStackTrace();
                        }
                        String DWXH = sql41.get("DWXH").toString();
                        String sql5 = "select RKFS from YK_RKFS where XTSB='" + storehouseId + "' and jgid='" + jgid + "' and dyfs='1' and rownum=1";
                        Map<String, Object> sql51 = null;
                        try {
                            sql51 = dao.doSqlLoad(sql5, null);
                        } catch (PersistentDataOperationException e1) {
                            e1.printStackTrace();
                        }
                        String RKFS = sql51.get("RKFS").toString();
                        Map<String, Object> record = new HashMap<String, Object>();
                        record.put("JGID", jgid);
                        record.put("XTSB", storehouseId);// 药库识别
                        record.put("RKFS", RKFS);// 入库方式
                        record.put("CGRQ", CreateTime);// 采购日期
                        record.put("FDJS", "0");// 附单据数
                        record.put("PWD", "0");// 购入方式
                        record.put("RKBZ", one.optString("Memo") == null ? null : one.optString("Memo"));// 备注
                        record.put("RKDH", one.optString("PurchaseOrderNo") == null ? null : one.optString("PurchaseOrderNo"));// 入库单号
                        record.put("DWXH", DWXH);// 进货单位"
                        record.put("CWPB", "0");// 财务判别
                        record.put("RKPB", "0");// 入库判别
                        record.put("LRRQ", Lrrq);
                        record.put("RKRQ", Lrrq);
                        record.put("DJFS", "0");
                        body.put("YK_RK01", record);
                        for (int j = 0; j < DistributionDetail.length(); j++) {
                            JSONObject two = DistributionDetail.getJSONObject(j);
                            Map<String, Object> record1 = new HashMap<String, Object>();
                            String zbbm = (String) two.optString("UniCode") == null ? null : two.optString("UniCode");
                            String name = (String) two.optString("Name") == null ? null : two.optString("Name");
                            String sqlzbbm = "select count(1)+10 as  LENGTH from V_YPXX_SPT a where a.ZBBM='" + zbbm + "' and a.jgid='" + jgid + "'";
                            Map<String, Object> sqlzbbm1 = null;
                            Integer temp = 0;
                            try {
                                sqlzbbm1 = dao.doSqlLoad(sqlzbbm, null);
                            } catch (PersistentDataOperationException e1) {
                                e1.printStackTrace();
                            }
                            if (sqlzbbm1 != null && sqlzbbm1.size() > 0) {
                                temp = Integer.parseInt(sqlzbbm1.get("LENGTH") + "");
                                if (temp == 10) {
                                    req.put("msg", "配送单中存在未对照的药品信息，药品统一编码为：'" + zbbm + "'，药品名称为：'" + name + "'");
                                    req.put("code", "249");
                                }
                            }
                            if (req.size() == 0) {
                                String sqlrecord = " select a.YPXH,a.YPMC,a.YPCD,a.CDMC,a.YPGG,a.YPDW  from V_YPXX_SPT a where a.ZBBM='" + zbbm + "' and a.jgid='" + jgid + "' and rownum=1";
                                Map<String, Object> sqlrecord1 = null;
                                try {
                                    sqlrecord1 = dao.doSqlLoad(sqlrecord, null);
                                } catch (PersistentDataOperationException e1) {
                                    e1.printStackTrace();
                                }
                                record1.put("JGID", jgid);// 机构编码
                                record1.put("RKFS", "0");// 入库方式
                                record1.put("RKDH", "0");// 入库单号
                                record1.put("YSDH", "0");// 验收单号
                                record1.put("TYPE", "1");// 库存性质
                                record1.put("KCSB", "0");// 库存识别
                                record1.put("DJFS", "0");// 定价方式
                                record1.put("FKJE", "0");// 付款金额
                                record1.put("YFJE", "0");// 已付金额
                                record1.put("YPKL", "0");// 药品扣率
                                record1.put("JBYWBZ", "0");// 基本药物标志
                                record1.put("DJFS_text", "标准零售价格");// 入库方式
                                record1.put("BZLJ", null);// 标准零价
                                record1.put("FPHM", two.optString("InvoiceNo") == null ? null : two.optString("InvoiceNo"));// 发票号码
                                record1.put("YPCD", MedicineUtils.parseInt(sqlrecord1.get("YPCD")));// 药品产地
                                record1.put("YPXH", MedicineUtils.parseInt(sqlrecord1.get("YPXH")));// 药品序号
                                record1.put("CDMC", (String) sqlrecord1.get("CDMC"));// 产地简称
                                record1.put("YPMC", (String) sqlrecord1.get("YPMC"));// 药品通用名
                                record1.put("LSJG", two.optString("Price") == null ? null : two.optString("Price"));// 零售价格
                                record1.put("JHJG", two.optString("Price") == null ? null : two.optString("Price"));// 进货合计
                                record1.put("YPGG", (String) sqlrecord1.get("YPGG"));// 规格
                                record1.put("YPDW", (String) sqlrecord1.get("YPDW"));// 单位
                                record1.put("YFBZ", "1");//
                                record1.put("PFJG", two.optString("Price") == null ? null : two.optString("Price"));// 批发价格
                                record1.put("DJGS", null);// 定价公式
                                record1.put("RKSL", two.optString("Quantity") == null ? null : two.optString("Quantity"));// 入库数量
                                record1.put("LSJE", two.optString("Amount") == null ? null : two.optString("Amount"));// 零售合计
                                record1.put("JHJE", "0");//
                                record1.put("JHHJ", two.optString("Amount") == null ? null : two.optString("Amount"));// 进货合计
                                record1.put("PFJE", two.optString("Amount") == null ? null : two.optString("Amount"));// 批发金额
                                record1.put("CJHJ", "0");// 差价
                                record1.put("XTSB", storehouseId);// 药库识别
                                record1.put("YPPH", two.optString("BatchNO") == null ? "" : two.optString("BatchNO"));// 药品批号
                                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(two.optString("InvalidDate"));
                                record1.put("YPXQ", two.optString("InvalidDate") == null ? "" : date);// 有效日期
                                List p = new ArrayList();
                                p.add(record1);
                                body.put("YK_RK02", p);
                            }
                        }
                        if (req.size() == 0) {
                            String op = "create";
                            StorehouseCheckInOutModel model = new StorehouseCheckInOutModel(dao);
                            try {
                                model.saveCheckIn(body, op);
                            } catch (ModelDataOperationException e) {
                                new ServiceException(e);
                            }
                            try {
                                String APIURL1 = (String) spt.get("url1");
                                String json1 = "{\n " +
                                        "\"BusinessType\":\"" + "YY102\",\n" +
                                        "\"HospitalCode\":\"" + CODE + "\",\n" +
                                        "\"IP\":\"" + ip + "\",\n" +
                                        "\"MAC\":\"" + mac + "\",\n" +
                                        "\"HostName\":\"" + jgid + "\",\n" +
                                        "\"Data\"" + ":{\n " +
                                        "\"ID\":\"" + ID + "\"\n" +
                                        " } }";
                                String res1 = httpURLPOSTCase(APIURL1, json1);

                            } catch (Exception e) {
                                logger.error("请求下载配送单信息接口.", e);
                                throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求下载配送单信息接口:");
                            }
                        }
                    }
                }
            } else if (jorm.optString("Code").equals("1")) {
                req.put("msg", "下载配送单信息接口返回：”错误！“");
                req.put("code", "201");
            } else if (jorm.optString("Code").equals("2")) {
                req.put("msg", "下载配送单信息接口返回：”部分错误！“");
                req.put("code", "202");
            } else if (jorm.optString("Code").equals("3")) {
                req.put("msg", "下载配送单信息接口返回：”与历史数据不匹配，无法新增/更新！“");
                req.put("code", "203");
            } else if (jorm.optString("Code").equals("10000")) {
                req.put("msg", "下载配送单信息接口返回：”未知错误！“");
                req.put("code", "204");
            } else if (jorm.optString("Code").equals("10001")) {
                req.put("msg", "下载配送单信息接口返回：”未指定业务类型[BusinessType]！“");
                req.put("code", "205");
            } else if (jorm.optString("Code").equals("10002")) {
                req.put("msg", "下载配送单信息接口返回：”无效的业务类型[BusinessType]！“");
                req.put("code", "206");
            } else if (jorm.optString("Code").equals("10003")) {
                req.put("msg", "下载配送单信息接口返回：”业务类型[BusinessType]在此请求中不支持！“");
                req.put("code", "207");
            } else if (jorm.optString("Code").equals("10004")) {
                req.put("msg", "下载配送单信息接口返回：”业务类型[BusinessType]未配置数据流！“");
                req.put("code", "208");
            } else if (jorm.optString("Code").equals("10005")) {
                req.put("msg", "下载配送单信息接口返回：”未知用户！“");
                req.put("code", "209");
            } else if (jorm.optString("Code").equals("10006")) {
                req.put("msg", "下载配送单信息接口返回：”密码不正确！“");
                req.put("code", "210");
            } else if (jorm.optString("Code").equals("10007")) {
                req.put("msg", "下载配送单信息接口返回：”用户未设置数据接收地址[PushUrl]！“");
                req.put("code", "211");
            } else if (jorm.optString("Code").equals("10010")) {
                req.put("msg", "下载配送单信息接口返回：”未提交单据明细数据！“");
                req.put("code", "212");
            } else if (jorm.optString("Code").equals("10011")) {
                req.put("msg", "下载配送单信息接口返回：”提交数据包含重复的商品统一编码[UniCode]！“");
                req.put("code", "213");
            } else if (jorm.optString("Code").equals("10012")) {
                req.put("msg", "省平台下载药品信息接口返回：”生产厂家/供应商类型有误[OrgTypeCode]！“");
                req.put("code", "214");
            } else if (jorm.optString("Code").equals("10013")) {
                req.put("msg", "下载配送单信息接口返回：”单据明细序号[Seq]重复！“");
                req.put("code", "215");
            } else if (jorm.optString("Code").equals("10014")) {
                req.put("msg", "下载配送单信息接口返回：”单据明细记录数过多！“");
                req.put("code", "216");
            } else if (jorm.optString("Code").equals("10015")) {
                req.put("msg", "下载配送单信息接口返回：”医院代码不能为空！“");
                req.put("code", "217");
            } else if (jorm.optString("Code").equals("10016")) {
                req.put("msg", "下载配送单信息接口返回：”医院名称不能为空！“");
                req.put("code", "218");
            } else if (jorm.optString("Code").equals("10017")) {
                req.put("msg", "下载配送单信息接口返回：”科室编码不能为空！“");
                req.put("code", "219");
            } else if (jorm.optString("Code").equals("10018")) {
                req.put("msg", "下载配送单信息接口返回：”配送点(库房)编码不能为空空！“");
                req.put("code", "220");
            } else if (jorm.optString("Code").equals("11001")) {
                req.put("msg", "下载配送单信息接口返回：”医院代码[HospitalCode]在平台字典中不存在！“");
                req.put("code", "221");
            } else if (jorm.optString("Code").equals("11002")) {
                req.put("msg", "下载配送单信息接口返回：”商品统一编码[UniCode]在平台字典中不存在！“");
                req.put("code", "222");
            } else if (jorm.optString("Code").equals("11003")) {
                req.put("msg",
                        "下载配送单信息接口返回：”配送点[DistributionSiteCode]在平台字典中不存在！“");
                req.put("code", "223");
            } else if (jorm.optString("Code").equals("11004")) {
                req.put("msg", "下载配送单信息接口返回：”供应商[SupplierCode]在平台字典中不存在！“");
                req.put("code", "224");
            } else if (jorm.optString("Code").equals("11005")) {
                req.put("msg", "下载配送单信息接口返回：”厂家在平台字典中不存在！“");
                req.put("code", "225");
            } else if (jorm.optString("Code").equals("11006")) {
                req.put("msg", "下载配送单信息接口返回：” 企业已存在待审核数据！“");
                req.put("code", "226");
            } else if (jorm.optString("Code").equals("11011")) {
                req.put("msg", "下载配送单信息接口返回：”药监下载数据暂不支持修改！“");
                req.put("code", "227");
            } else if (jorm.optString("Code").equals("11012")) {
                req.put("msg", "下载配送单信息接口返回：”产品品名已存在！“");
                req.put("code", "228");
            } else if (jorm.optString("Code").equals("11013")) {
                req.put("msg", "下载配送单信息接口返回：”产品品名不存在！“");
                req.put("code", "229");
            } else if (jorm.optString("Code").equals("11014")) {
                req.put("msg", "下载配送单信息接口返回：”产品规格已存在！“");
                req.put("code", "230");
            } else if (jorm.optString("Code").equals("11015")) {
                req.put("msg", "下载配送单信息接口返回：”未获取到产品类型！“");
                req.put("code", "231");
            } else if (jorm.optString("Code").equals("11016")) {
                req.put("msg", "下载配送单信息接口返回：”品名不是待审核状态，如果品名已通过审核，请传入品名 Code！“");
                req.put("code", "232");
            } else if (jorm.optString("Code").equals("11017")) {
                req.put("msg", "下载配送单信息接口返回：”产品品名存在待审核数据！“");
                req.put("code", "233");
            } else if (jorm.optString("Code").equals("11018")) {
                req.put("msg", "下载配送单信息接口返回：”产品规格存在待审核数据！“");
                req.put("code", "234");
            } else if (jorm.optString("Code").equals("11019")) {
                req.put("msg", "下载配送单信息接口返回：”发票号不匹配！“");
                req.put("code", "235");
            } else if (jorm.optString("Code").equals("11020")) {
                req.put("msg", "下载配送单信息接口返回：”平台代码[PlateformCode]不存在！“");
                req.put("code", "236");
            } else if (jorm.optString("Code").equals("12000")) {
                req.put("msg", "下载配送单信息接口返回：”数据已上传！“");
                req.put("code", "237");
            } else if (jorm.optString("Code").equals("20000")) {
                req.put("msg", "下载配送单信息接口返回：”数据不需要处理(更新状态等)！“");
                req.put("code", "238");
            } else if (jorm.optString("Code").equals("20001")) {
                req.put("msg", "下载配送单信息接口返回：”数据提交不成功！“");
                req.put("code", "239");
            } else if (jorm.optString("Code").equals("20002")) {
                req.put("msg", "下载配送单信息接口返回：”数据提交时发生错误！“");
                req.put("code", "240");
            } else if (jorm.optString("Code").equals("20003")) {
                req.put("msg", "下载配送单信息接口返回：”需要更新的数据不存在！“");
                req.put("code", "241");
            } else if (jorm.optString("Code").equals("20004")) {
                req.put("msg", "下载配送单信息接口返回：”数据已作废！“");
                req.put("code", "242");
            } else if (jorm.optString("Code").equals("20005")) {
                req.put("msg", "下载配送单信息接口返回：”数据已有后续流程，无法更新！“");
                req.put("code", "243");
            } else if (jorm.optString("Code").equals("40101")) {
                req.put("msg", "下载配送单信息接口返回：”用户名不存在！“");
                req.put("code", "244");
            } else if (jorm.optString("Code").equals("40102")) {
                req.put("msg", "下载配送单信息接口返回：”密码错误！“");
                req.put("code", "245");
            } else if (jorm.optString("Code").equals("40300")) {
                req.put("msg", "下载配送单信息接口返回：”未授权！“");
                req.put("code", "246");
            } else if (jorm.optString("Code").equals("40301")) {
                req.put("msg", "下载配送单信息接口返回：”过期！“");
                req.put("code", "247");
            } else if (jorm.optString("Code").equals("40302")) {
                req.put("msg", "下载配送单信息接口返回：”未到开始时间！“");
                req.put("code", "248");
            } else {
                req.put("msg", "下载配送单信息接口返回错误代码不在字典范围内，错误代码为”" + jorm.optString("Code") + "“；接口返回内容为：" + jorm.optString("Message"));
                req.put("code", jorm.optString("Code"));
            }
        } catch (Exception e) {
            logger.error("请求下载配送单信息接口.", e);
            throw new ModelDataOperationException(ServiceCode.CODE_ERROR, "请求下载配送单信息接口.");
        }
        return req;
    }

    /**
     * 上传库存到省平台药品信息
     *
     * @param body
     * @param ctx
     * @return
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    public Map<String, Object> doUpLoadphysicalDetail(Map<String, Object> body, Context ctx) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        String mac = getMacAddress();
        String ip = getIpAddress();
        String jgid = UserRoleToken.getCurrent().getManageUnit().getId();
        String sql2 = "select replace(a.code,'_YP','') as CODE,a.code as CODE2,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        //String YKSB=MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId"))+"";
        String JGID = UserRoleToken.getCurrent().getManageUnit().getId();
        String hql = "SELECT  to_char(c.LSJG,'fm999999990.009') as LSJG,c.JHJG as JHJG,a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,a.ZXDW as ZXDW,a.JLDW as JLDW,b.CDMC as CDMC,to_char(sum(c.KCSL),'fm999999990.009') as KCSL," +
                "c.YPPH as YPPH,to_char(c.YPXQ,'yyyy-mm-dd') as YPXQ,c.TYPE as TYPE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,c.YPXH as YPXH,c.YPCD as YPCD,a.YPDM as YPDM,a.YPJL as YPJL," +
                "d.KWBM as KWBM,d.YKZF as YKZF,a.ZBLB as ZBLB,e.ZBBM as ZBBM FROM YK_CDDZ b,YK_KCMX c,YK_TYPK a,YK_YPXX d,YK_CDXX e WHERE  ( b.YPCD = c.YPCD ) and  c.JGID = d.JGID and ( c.YPXH = a.YPXH )" +
                //" and ( a.YPXH = d.YPXH )  and (c.JGID = e.JGID)  and (c.YPCD = e.YPCD)  and (c.YPXH = e.YPXH) and  ( c.JGID = d.JGID ) and ( d.YKSB = '"+YKSB+"' ) and ( d.JGID = '"+JGID+"' ) and e.ZBBM is not null" +
                " and ( a.YPXH = d.YPXH )  and (c.JGID = e.JGID)  and (c.YPCD = e.YPCD)  and (c.YPXH = e.YPXH) and  ( c.JGID = d.JGID ) and ( d.JGID = '" + JGID + "' ) and e.ZBBM is not null" +
                " GROUP BY a.YPMC,a.YPGG,a.YPDW,a.ZXDW,a.YPJL,a.JLDW,b.CDMC,c.YPPH,c.YPXQ,c.TYPE,a.PYDM,a.WBDM,a.JXDM,a.QTDM,c.YPXH,c.YPCD,a.YPDM,d.KWBM,d.YKZF,a.ZBLB,c.LSJG,c.JHJG,e.ZBBM  order by c.YPXH";//CN00027953

        List<Map<String, Object>> list = null;
        try {
            list = dao.doSqlQuery(hql, null);
        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        }
        if (list.size() == 0) {
            req.put("msg", "当前无库存信息上传！");
            req.put("code", "999");
            return req;
        } else {
            for (int i = 0; i < list.size(); i++) {
                String ZBBM = list.get(i).get("ZBBM") + "";

                //大包装库存数量
                String PackQuantity = list.get(i).get("KCSL") + "";
                String JsonPackQuantity = null;
                if (PackQuantity != "null" && PackQuantity != null) {
                    JsonPackQuantity = "\"PackQuantity\":\"" + PackQuantity + "\",\n";
                } else {
                    JsonPackQuantity = "\"PackQuantity\":0,\n";
                }

                //小规格数量
                String Quantity = list.get(i).get("KCSL") + "";
                String JsonQuantity = null;
                if (Quantity != "null" && Quantity != null) {
                    JsonQuantity = "\"Quantity\":\"" + Quantity + "\",\n";
                } else {
                    JsonQuantity = "\"Quantity\":0,\n";
                }

                //大包装单位
                String PackUnit = list.get(i).get("YPDW") + "";
                String JsonPackUnit = null;
                if (PackUnit != "null" && PackUnit != null) {
                    JsonPackUnit = "\"PackUnit\":\"" + PackUnit + "\",\n";
                } else {
                    JsonPackUnit = "\"PackUnit\":无,\n";
                }
                //小包装单位
                String Unit = list.get(i).get("JLDW") + "";
                String JsonUnit = null;
                if (Unit != "null" && Unit != null) {
                    JsonUnit = "\"Unit\":\"" + Unit + "\",\n";
                } else {
                    JsonUnit = "\"Unit\":无,\n";
                }

                //批号
                String YPPH = list.get(i).get("YPPH") + "";
                String JsonYPPH = null;
                if (YPPH != "null" && YPPH != null) {
                    JsonYPPH = "\"BatchNO\":\"" + YPPH + "\",\n";
                } else {
                    JsonYPPH = "\"BatchNO\":无,\n";
                }

                //零售价格
                String LSJG = list.get(i).get("LSJG") + "";
                String JsonPrice = null;
                String JsonRetailPrice = null;
                if (LSJG != "null" && LSJG != null) {
                    JsonPrice = "\"Price\":\"" + LSJG + "\",\n";
                    JsonRetailPrice = "\"RetailPrice\":\"" + LSJG + "\",\n";
                } else {
                    JsonPrice = "\"Price\":0,\n";
                    JsonRetailPrice = "\"RetailPrice\":0,\n";
                }

                //效期,生产日期
                String YPXQ = (String) list.get(i).get("YPXQ");
                String JsonYPXQ = null;
                String JsonProductionDate = null;
                if (YPXQ != "null" && YPXQ != null) {
                    JsonYPXQ = "\"InvalidDate\":\"" + YPXQ + " 00:00:00\",\n";
                    JsonProductionDate = "\"ProductionDate\":\"" + YPXQ + "\",\n";
                } else {
                    JsonYPXQ = "\"InvalidDate\":无,\n";
                    JsonProductionDate = "\"ProductionDate\":无,\n";
                }

                if (ZBBM != null) {
                    String sql3 = "select t.ManufacturerCode as ManufacturerCode,t.Manufacturer as Manufacturer from yk_typk_spt t where t.unicode='" + ZBBM + "'";
                    Map<String, Object> sql31 = null;
                    try {
                        sql31 = dao.doSqlLoad(sql3, null);
                    } catch (PersistentDataOperationException e1) {
                        e1.printStackTrace();
                    }
                    if (sql31 != null) {
                        String CODE = (String) sql21.get("CODE");
                        //String CODE2 = (String) sql21.get("CODE2");
                        //String HostName = (String) sql1.get("ORGANIZNAME");
                        //String APIURL = (String) body.get("url");//http://10.2.200.202:8042/gateway/dep/business/post
                        String APIURL = "http://10.2.200.202:8042/dep/business/post";
                        //生产厂编码
                        String ManufacturerCode = (String) sql31.get("MANUFACTURERCODE");
                        String JsonManufacturerCode = null;
                        if (ManufacturerCode != "null" && ManufacturerCode != null) {
                            JsonManufacturerCode = "\"ManufacturerCode\":\"" + ManufacturerCode + "\",\n";
                        } else {
                            JsonManufacturerCode = "\"ManufacturerCode\":无,\n";
                        }
                        //生产厂家名称
                        String Manufacturer = (String) sql31.get("MANUFACTURER");
                        String JsonManufacturer = null;
                        if (Manufacturer != "null" && Manufacturer != null) {
                            JsonManufacturer = "\"Manufacturer\":\"" + Manufacturer + "\",\n";
                        } else {
                            JsonManufacturer = "\"Manufacturer\":无,\n";
                        }

                        String json = "{\n " +
                                "\"BusinessType\":\"" + "YY006\",\n" +
                                "\"HospitalCode\":\"" + CODE + "\",\n" +
                                "\"IP\":\"" + ip + "\",\n" +
                                "\"MAC\":\"" + mac + "\",\n" +
                                "\"HostName\":\"" + jgid + "\",\n" +
                                "\"Data\"" + ":[{\n " +
                                "\"HospitalCode\":\"" + CODE + "\",\n" +
                                "\"SupplierCode\":\"" + CODE + "\",\n" +
                                "\"DistributionSiteCode\":\"" + "1\",\n" +
                                "\"DistributionSiteType\":\"" + "3\",\n" +
                                "\"UniCode\":\"" + ZBBM + "\",\n" +
                                JsonManufacturerCode +
                                JsonManufacturer +
                                JsonYPPH +
                                JsonProductionDate +
                                JsonPackQuantity +
                                JsonQuantity +
                                JsonPackUnit +
                                JsonUnit +
                                JsonPrice +
                                JsonRetailPrice +
                                "\"Ratio\":\"" + "1\",\n" +
                                JsonYPXQ +
                                "\"Memo\":\"" + "无\"\n" +
                                " }]" +
                                " }";
                        //System.out.println("json----->" + json);
                        //System.out.println("APIURL----->"+APIURL);
                        String res = httpURLPOSTCase(APIURL, json);
                        //System.out.println("res----->" + res);
                        JSONObject jorm = new JSONObject(res);
                        if (jorm.optString("code").equals("0") || jorm.optString("code").equals("0")) {
                            req.put("msg", "库存信息上传成功！");
                            req.put("code", "200");
                        } else if (jorm.optString("code").equals("1")) {
                            req.put("msg", "上传库存信息接口返回：”错误！“");
                            req.put("code", "201");
                        } else if (jorm.optString("code").equals("2")) {
                            req.put("msg", "上传库存信息接口返回：”部分错误！“");
                            req.put("code", "202");
                        } else if (jorm.optString("code").equals("3")) {
                            req.put("msg", "上传库存信息接口返回：”与历史数据不匹配，无法新增/更新！“");
                            req.put("code", "203");
                        } else if (jorm.optString("code").equals("10000")) {
                            req.put("msg", "上传库存信息接口返回：”未知错误！“");
                            req.put("code", "204");
                        } else if (jorm.optString("code").equals("10001")) {
                            req.put("msg", "上传库存信息接口返回：”未指定业务类型[BusinessType]！“");
                            req.put("code", "205");
                        } else if (jorm.optString("code").equals("10002")) {
                            req.put("msg", "上传库存信息接口返回：”无效的业务类型[BusinessType]！“");
                            req.put("code", "206");
                        } else if (jorm.optString("code").equals("10003")) {
                            req.put("msg", "上传库存信息接口返回：”业务类型[BusinessType]在此请求中不支持！“");
                            req.put("code", "207");
                        } else if (jorm.optString("code").equals("10004")) {
                            req.put("msg", "上传库存信息接口返回：”业务类型[BusinessType]未配置数据流！“");
                            req.put("code", "208");
                        } else if (jorm.optString("code").equals("10005")) {
                            req.put("msg", "上传库存信息接口返回：”未知用户！“");
                            req.put("code", "209");
                        } else if (jorm.optString("code").equals("10006")) {
                            req.put("msg", "上传库存信息接口返回：”密码不正确！“");
                            req.put("code", "210");
                        } else if (jorm.optString("code").equals("10007")) {
                            req.put("msg", "上传库存信息接口返回：”用户未设置数据接收地址[PushUrl]！“");
                            req.put("code", "211");
                        } else if (jorm.optString("code").equals("10010")) {
                            req.put("msg", "上传库存信息接口返回：”未提交单据明细数据！“");
                            req.put("code", "212");
                        } else if (jorm.optString("code").equals("10011")) {
                            req.put("msg", "上传库存信息接口返回：”提交数据包含重复的商品统一编码[UniCode]！“");
                            req.put("code", "213");
                        } else if (jorm.optString("code").equals("10012")) {
                            req.put("msg", "上传库存信息接口返回：”生产厂家/供应商类型有误[OrgTypeCode]！“");
                            req.put("code", "214");
                        } else if (jorm.optString("code").equals("10013")) {
                            req.put("msg", "上传库存信息接口返回：”单据明细序号[Seq]重复！“");
                            req.put("code", "215");
                        } else if (jorm.optString("code").equals("10014")) {
                            req.put("msg", "上传库存信息接口返回：”单据明细记录数过多！“");
                            req.put("code", "216");
                        } else if (jorm.optString("code").equals("10015")) {
                            req.put("msg", "上传库存信息接口返回：”医院代码不能为空！“");
                            req.put("code", "217");
                        } else if (jorm.optString("code").equals("10016")) {
                            req.put("msg", "上传库存信息接口返回：”医院名称不能为空！“");
                            req.put("code", "218");
                        } else if (jorm.optString("code").equals("10017")) {
                            req.put("msg", "上传库存信息接口返回：”科室编码不能为空！“");
                            req.put("code", "219");
                        } else if (jorm.optString("code").equals("10018")) {
                            req.put("msg", "上传库存信息接口返回：”配送点(库房)编码不能为空空！“");
                            req.put("code", "220");
                        } else if (jorm.optString("code").equals("11001")) {
                            req.put("msg", "上传库存信息接口返回：”医院代码[HospitalCode]在平台字典中不存在！“");
                            req.put("code", "221");
                        } else if (jorm.optString("code").equals("11002")) {
                            req.put("msg", "上传库存信息接口返回：”商品统一编码[UniCode]在平台字典中不存在！“");
                            req.put("code", "222");
                        } else if (jorm.optString("code").equals("11003")) {
                            req.put("msg", "上传库存信息接口返回：”配送点[DistributionSiteCode]在平台字典中不存在！“");
                            req.put("code", "223");
                        } else if (jorm.optString("code").equals("11004")) {
                            req.put("msg", "上传库存信息接口返回：”供应商[SupplierCode]在平台字典中不存在！“");
                            req.put("code", "224");
                        } else if (jorm.optString("code").equals("11005")) {
                            req.put("msg", "上传库存信息接口返回：”厂家在平台字典中不存在！“");
                            req.put("code", "225");
                        } else if (jorm.optString("code").equals("11006")) {
                            req.put("msg", "上传库存信息接口返回：” 企业已存在待审核数据！“");
                            req.put("code", "226");
                        } else if (jorm.optString("code").equals("11011")) {
                            req.put("msg", "上传库存信息接口返回：”药监下载数据暂不支持修改！“");
                            req.put("code", "227");
                        } else if (jorm.optString("code").equals("11012")) {
                            req.put("msg", "上传库存信息接口返回：”产品品名已存在！“");
                            req.put("code", "228");
                        } else if (jorm.optString("code").equals("11013")) {
                            req.put("msg", "上传库存信息接口返回：”产品品名不存在！“");
                            req.put("code", "229");
                        } else if (jorm.optString("code").equals("11014")) {
                            req.put("msg", "上传库存信息接口返回：”产品规格已存在！“");
                            req.put("code", "230");
                        } else if (jorm.optString("code").equals("11015")) {
                            req.put("msg", "上传库存信息接口返回：”未获取到产品类型！“");
                            req.put("code", "231");
                        } else if (jorm.optString("code").equals("11016")) {
                            req.put("msg", "上传库存信息接口返回：”品名不是待审核状态，如果品名已通过审核，请传入品名 Code！“");
                            req.put("code", "232");
                        } else if (jorm.optString("code").equals("11017")) {
                            req.put("msg", "上传库存信息接口返回：”产品品名存在待审核数据！“");
                            req.put("code", "233");
                        } else if (jorm.optString("code").equals("11018")) {
                            req.put("msg", "上传库存信息接口返回：”产品规格存在待审核数据！“");
                            req.put("code", "234");
                        } else if (jorm.optString("code").equals("11019")) {
                            req.put("msg", "上传库存信息接口返回：”发票号不匹配！“");
                            req.put("code", "235");
                        } else if (jorm.optString("code").equals("11020")) {
                            req.put("msg", "上传库存信息接口返回：”平台代码[PlateformCode]不存在！“");
                            req.put("code", "236");
                        } else if (jorm.optString("code").equals("12000")) {
                            req.put("msg", "上传库存信息接口返回：”数据已上传！“");
                            req.put("code", "237");
                        } else if (jorm.optString("code").equals("20000")) {
                            req.put("msg", "上传库存信息接口返回：”数据不需要处理(更新状态等)！“");
                            req.put("code", "238");
                        } else if (jorm.optString("code").equals("20001")) {
                            req.put("msg", "上传库存信息接口返回：”数据提交不成功！“");
                            req.put("code", "239");
                        } else if (jorm.optString("code").equals("20002")) {
                            req.put("msg", "上传库存信息接口返回：”数据提交时发生错误！“");
                            req.put("code", "240");
                        } else if (jorm.optString("code").equals("20003")) {
                            req.put("msg", "上传库存信息接口返回：”需要更新的数据不存在！“");
                            req.put("code", "241");
                        } else if (jorm.optString("code").equals("20004")) {
                            req.put("msg", "上传库存信息接口返回：”数据已作废！“");
                            req.put("code", "242");
                        } else if (jorm.optString("code").equals("20005")) {
                            req.put("msg", "上传库存信息接口返回：”数据已有后续流程，无法更新！“");
                            req.put("code", "243");
                        } else if (jorm.optString("code").equals("40101")) {
                            req.put("msg", "上传库存信息接口返回：”用户名不存在！“");
                            req.put("code", "244");
                        } else if (jorm.optString("code").equals("40102")) {
                            req.put("msg", "上传库存信息接口返回：”密码错误！“");
                            req.put("code", "245");
                        } else if (jorm.optString("code").equals("40300")) {
                            req.put("msg", "上传库存信息接口返回：”未授权！“");
                            req.put("code", "246");
                        } else if (jorm.optString("code").equals("40301")) {
                            req.put("msg", "上传库存信息接口返回：”过期！“");
                            req.put("code", "247");
                        } else if (jorm.optString("code").equals("40302")) {
                            req.put("msg", "上传库存信息接口返回：”未到开始时间！“");
                            req.put("code", "248");
                        } else {
                            req.put("msg", "上传库存信息接口返回错误代码不在字典范围内，错误代码为”" + jorm.optString("code") + "“；接口返回内容为：" + jorm.optString("Message"));
                            req.put("code", jorm.optString("code"));
                        }
                    }
                }
            }
        }
        return req;
    }

    /**
     * 上传消耗信息到省平台药品信息
     *
     * @param body
     * @param ctx
     * @return
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    public Map<String, Object> doUpLoadphysicalDetails(Map<String, Object> body,
                                                       Context ctx) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        String mac = getMacAddress();
        String ip = getIpAddress();
        String jgid = UserRoleToken.getCurrent().getManageUnit().getId();
        String APIURL = "http://10.2.200.202:8042/gateway/dep/business/post";
        String sql2 = "select replace(a.code,'_YP','') as CODE,a.code as CODE2,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }


        //Map<String, Object> map_par = new HashMap<String, Object>();
        //map_par.put("YKSB", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")));
        //map_par.put("XHRQ", sdf1.format(new Date()));
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1); //得到前一天
		Date date = calendar.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String XHRQ = df.format(date);
//      String YKSB = MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")) + "";
//		String hql ="select e.zbbm as ZBBM, to_char(sum(a.SFSL),'fm999999990.009') as CKSL, to_char(sum(a.LSJE),'fm999999990.009') as LSJE, a.ypph as YPPH," +
//				" to_char(a.ypxq,'yyyy-mm-dd') as YPXQ, to_char(a.lsjg,'fm999999990.009') as LSJG ,c.zxbz as ZXBZ from YK_CK02 a, YK_CK01 b, YK_TYPK c, yk_cdxx e" +
//				"  where a.YPCD = e.YPCD and a.YPXH = c.YPXH and a.XTSB = b.XTSB  and a.CKFS = b.CKFS and a.CKDH = b.CKDH   and a.ypxh=e.ypxh and a.jgid=e.jgid" +
//				" and b.CKPB = 1 and to_char(b.CKRQ, 'yyyy-mm-dd') ='"+XHRQ+"' and a.XTSB ='"+YKSB+"' and e.zbbm is not null group by e.zbbm,a.ypph, a.ypxq, c.YPDW,c.zxbz, a.lsjg";

        String hql = "select e.ypxh,e.ypmc,e.ypgg,e.ypdw,e.jldw,to_char(sum(e.ypsl),'fm999999990.009') as ypsl,to_char(sum(e.hjje),'fm999999990.009') as hjje,e.ypdj,e.cdmc,e.ypcd,e.zbbm,e.jgid from" +
                " (select  a.ypxh ,b.YPMC as YPMC,a.YFGG as YPGG, b.YPDW as YPDW,b.jldw,a.YPSL as YPSL,a.YPDJ as YPDJ,a.HJJE as HJJE,c.CDMC as CDMC, a.YPCD as YPCD,d.zbbm as ZBBM,a.jgid" +
                " from MS_CF02 a, YK_TYPK b, YK_CDDZ c,ms_cf01 f,yk_cdxx d where  a.YPXH = b.YPXH and a.YPCD = c.YPCD and a.ypxh=d.ypxh and a.jgid=d.jgid and a.ypcd= d.ypcd" +
                " and d.zbbm is not null and a.JGID = '" + jgid + "' and a.cfsb = f.cfsb and to_char(f.fyrq,'yyyy-mm-dd')='" + XHRQ + "' and f.fybz='1'" +
                " union all" +
                " select  a.ypxh ,b.YPMC as YPMC,a.YPGG as YPGG, b.YPDW as YPDW,b.jldw,a.YPSL as YPSL,a.YPDJ as YPDJ,a.FYJE as HJJE," +
                " c.CDMC as CDMC, a.YPCD as YPCD,d.zbbm as ZBBM,a.jgid from YF_ZYFYMX a,YK_TYPK b,YK_CDDZ c,yk_cdxx d  where a.YPXH = b.YPXH and a.YPCD = c.YPCD and a.ypxh=d.ypxh" +
                " and a.jgid=d.jgid and a.ypcd= d.ypcd and d.zbbm is not null and a.jgid='" + jgid + "' and to_char(a.fyrq,'yyyy-mm-dd')='" + XHRQ + "' and tyxh='0'" +
                " ) e  group by ypxh,ypmc,ypgg,ypdw,jldw,ypdj,cdmc,ypcd,zbbm,jgid ";

        List<Map<String, Object>> list = null;
        try {
            list = dao.doSqlQuery(hql, null);
        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        }

        if (list.size() == 0) {
            req.put("msg", "当前无药品消耗信息上传！");
            req.put("code", "999");
            return req;
        } else {
            for (int i = 0; i < list.size(); i++) {
                String ZBBM = list.get(i).get("ZBBM") + ""; //对照编码
                //大包装库存数量
                String PackQuantity = list.get(i).get("YPSL") + "";
                String JsonPackQuantity = null;
                if (PackQuantity != "null" && PackQuantity != null) {
                    JsonPackQuantity = "\"PackQuantity\":\"" + PackQuantity + "\",\n";
                } else {
                    JsonPackQuantity = "\"PackQuantity\":0,\n";
                }

                //小规格数量
                String Quantity = list.get(i).get("YPSL") + "";
                String JsonQuantity = null;
                if (Quantity != "null" && Quantity != null) {
                    JsonQuantity = "\"Quantity\":\"" + Quantity + "\",\n";
                } else {
                    JsonQuantity = "\"Quantity\":0,\n";
                }

                //大包装单位
                String PackUnit = list.get(i).get("YPDW") + "";
                String JsonPackUnit = null;
                if (PackUnit != "null" && PackUnit != null) {
                    JsonPackUnit = "\"PackUnit\":\"" + PackUnit + "\",\n";
                } else {
                    JsonPackUnit = "\"PackUnit\":无,\n";
                }
                //小包装单位
                String Unit = list.get(i).get("JLDW") + "";
                String JsonUnit = null;
                if (Unit != "null" && Unit != null) {
                    JsonUnit = "\"Unit\":\"" + Unit + "\",\n";
                } else {
                    JsonUnit = "\"Unit\":无,\n";
                }

                //划价金额
                String HJJE = list.get(i).get("HJJE") + "";
                String JsonPrice = null;
                String JsonRetailPrice = null;
                if (HJJE != "null" && HJJE != null) {
                    JsonPrice = "\"Price\":\"" + HJJE + "\",\n";
                    JsonRetailPrice = "\"RetailPrice\":\"" + HJJE + "\",\n";
                } else {
                    JsonPrice = "\"Price\":0,\n";
                    JsonRetailPrice = "\"RetailPrice\":0,\n";
                }

                //药品单价
                String YPDJ = list.get(i).get("YPDJ") + "";
                String JsonAmount = null;
                if (YPDJ != "null" && YPDJ != null) {
                    JsonAmount = "\"Amount\":\"" + YPDJ + "\",\n";
                } else {
                    JsonAmount = "\"Amount\":0,\n";
                }

                //效期,生产日期
                String JsonYPXQ = "\"InvalidDate\":null,\n";
                String JsonProductionDate = "\"ProductionDate\":null,\n";

                //批号
                String JsonYPPH = "\"BatchNO\":null,\n";

                if (ZBBM != null) {
                    String sql3 = "select t.ManufacturerCode as ManufacturerCode,t.Manufacturer as Manufacturer,t.Unit as Unit from yk_typk_spt t where t.unicode='" + ZBBM + "'";
                    Map<String, Object> sql31 = null;
                    try {
                        sql31 = dao.doSqlLoad(sql3, null);
                    } catch (PersistentDataOperationException e1) {
                        e1.printStackTrace();
                    }
                    if (sql31 != null) {
                        String CODE = (String) sql21.get("CODE");
                        //String CODE2 = (String) sql21.get("CODE2");
                        //String HostName = (String) sql1.get("ORGANIZNAME");
                        //String APIURL = (String) body.get("url");
                        //生产厂编码
                        String ManufacturerCode = (String) sql31.get("MANUFACTURERCODE");
                        String JsonManufacturerCode = null;
                        if (ManufacturerCode != "null" && ManufacturerCode != null) {
                            JsonManufacturerCode = "\"ManufacturerCode\":\"" + ManufacturerCode + "\",\n";
                        } else {
                            JsonManufacturerCode = "\"ManufacturerCode\":null,\n";
                        }
                        //生产厂家名称
                        String Manufacturer = (String) sql31.get("MANUFACTURER");
                        String JsonManufacturer = null;
                        if (Manufacturer != "null" && Manufacturer != null) {
                            JsonManufacturer = "\"Manufacturer\":\"" + Manufacturer + "\",\n";
                        } else {
                            JsonManufacturer = "\"Manufacturer\":null,\n";
                        }

                        String json = "{\n " +
                                "\"BusinessType\":\"" + "YY108\",\n" +
                                "\"HospitalCode\":\"" + CODE + "\",\n" +
                                "\"IP\":\"" + ip + "\",\n" +
                                "\"MAC\":\"" + mac + "\",\n" +
                                "\"HostName\":\"" + jgid + "\",\n" +
                                "\"Data\"" + ":[{\n " +
                                "\"HospitalCode\":\"" + CODE + "\",\n" +
                                "\"SupplierCode\":null,\n" +
                                "\"DistributionSiteCode\":\"" + "1\",\n" +
                                "\"DistributionSiteType\":\"" + "3\",\n" +
                                "\"UniCode\":\"" + ZBBM + "\",\n" +
                                JsonManufacturerCode +
                                JsonManufacturer +
                                JsonYPPH +
                                JsonYPXQ +
                                JsonPackQuantity +
                                JsonQuantity +
                                JsonPackUnit +
                                JsonUnit +
                                JsonProductionDate +
                                JsonPrice +
                                JsonRetailPrice +
                                JsonAmount +
                                "\"ConsumeTime\":\"" + XHRQ + "\",\n" +
                                "\"Type\":\"" + "1\",\n" +
                                "\"Source\":\"" + "1\",\n" +
                                "\"Memo\":null,\n" +
                                " }]" +
                                " }";
                        //System.out.println("json----->" + json);
                        //System.out.println("APIURL----->" + APIURL);
                        String res = httpURLPOSTCase(APIURL, json);
                        //System.out.println("res----->" + res);
                        JSONObject jorm = new JSONObject(res);
                        if (jorm.optString("code").equals("0") || jorm.optString("code").equals("0")) {
                            req.put("msg", "药品消耗信息上传成功！");
                            req.put("code", "200");
                        } else if (jorm.optString("code").equals("1")) {
                            req.put("msg", "上传药品消耗信息接口返回：”错误！“");
                            req.put("code", "201");
                        } else if (jorm.optString("code").equals("2")) {
                            req.put("msg", "上传药品消耗信息接口返回：”部分错误！“");
                            req.put("code", "202");
                        } else if (jorm.optString("code").equals("3")) {
                            req.put("msg", "上传药品消耗信息接口返回：”与历史数据不匹配，无法新增/更新！“");
                            req.put("code", "203");
                        } else if (jorm.optString("code").equals("10000")) {
                            req.put("msg", "上传药品消耗信息接口返回：”未知错误！“");
                            req.put("code", "204");
                        } else if (jorm.optString("code").equals("10001")) {
                            req.put("msg", "上传药品消耗信息接口返回：”未指定业务类型[BusinessType]！“");
                            req.put("code", "205");
                        } else if (jorm.optString("code").equals("10002")) {
                            req.put("msg", "上传药品消耗信息接口返回：”无效的业务类型[BusinessType]！“");
                            req.put("code", "206");
                        } else if (jorm.optString("code").equals("10003")) {
                            req.put("msg", "上传药品消耗信息接口返回：”业务类型[BusinessType]在此请求中不支持！“");
                            req.put("code", "207");
                        } else if (jorm.optString("code").equals("10004")) {
                            req.put("msg", "上传药品消耗信息接口返回：”业务类型[BusinessType]未配置数据流！“");
                            req.put("code", "208");
                        } else if (jorm.optString("code").equals("10005")) {
                            req.put("msg", "上传药品消耗信息接口返回：”未知用户！“");
                            req.put("code", "209");
                        } else if (jorm.optString("code").equals("10006")) {
                            req.put("msg", "上传药品消耗信息接口返回：”密码不正确！“");
                            req.put("code", "210");
                        } else if (jorm.optString("code").equals("10007")) {
                            req.put("msg", "上传药品消耗信息接口返回：”用户未设置数据接收地址[PushUrl]！“");
                            req.put("code", "211");
                        } else if (jorm.optString("code").equals("10010")) {
                            req.put("msg", "上传药品消耗信息接口返回：”未提交单据明细数据！“");
                            req.put("code", "212");
                        } else if (jorm.optString("code").equals("10011")) {
                            req.put("msg", "上传药品消耗信息接口返回：”提交数据包含重复的商品统一编码[UniCode]！“");
                            req.put("code", "213");
                        } else if (jorm.optString("Code").equals("10012")) {
                            req.put("msg", "上传药品消耗信息接口返回：”生产厂家/供应商类型有误[OrgTypeCode]！“");
                            req.put("code", "214");
                        } else if (jorm.optString("code").equals("10013")) {
                            req.put("msg", "上传药品消耗信息接口返回：”单据明细序号[Seq]重复！“");
                            req.put("code", "215");
                        } else if (jorm.optString("Code").equals("10014")) {
                            req.put("msg", "上传药品消耗信息接口返回：”单据明细记录数过多！“");
                            req.put("code", "216");
                        } else if (jorm.optString("code").equals("10015")) {
                            req.put("msg", "上传药品消耗信息接口返回：”医院代码不能为空！“");
                            req.put("code", "217");
                        } else if (jorm.optString("code").equals("10016")) {
                            req.put("msg", "上传药品消耗信息接口返回：”医院名称不能为空！“");
                            req.put("code", "218");
                        } else if (jorm.optString("code").equals("10017")) {
                            req.put("msg", "上传药品消耗信息接口返回：”科室编码不能为空！“");
                            req.put("code", "219");
                        } else if (jorm.optString("code").equals("10018")) {
                            req.put("msg", "上传药品消耗信息接口返回：”配送点(库房)编码不能为空空！“");
                            req.put("code", "220");
                        } else if (jorm.optString("code").equals("11001")) {
                            req.put("msg", "上传药品消耗信息接口返回：”医院代码[HospitalCode]在平台字典中不存在！“");
                            req.put("code", "221");
                        } else if (jorm.optString("code").equals("11002")) {
                            req.put("msg", "上传药品消耗信息接口返回：”商品统一编码[UniCode]在平台字典中不存在！“");
                            req.put("code", "222");
                        } else if (jorm.optString("code").equals("11003")) {
                            req.put("msg", "上传药品消耗信息接口返回：”配送点[DistributionSiteCode]在平台字典中不存在！“");
                            req.put("code", "223");
                        } else if (jorm.optString("code").equals("11004")) {
                            req.put("msg", "上传药品消耗信息接口返回：”供应商[SupplierCode]在平台字典中不存在！“");
                            req.put("code", "224");
                        } else if (jorm.optString("Code").equals("11005")) {
                            req.put("msg", "上传药品消耗信息接口返回：”厂家在平台字典中不存在！“");
                            req.put("code", "225");
                        } else if (jorm.optString("code").equals("11006")) {
                            req.put("msg", "上传药品消耗信息接口返回：” 企业已存在待审核数据！“");
                            req.put("code", "226");
                        } else if (jorm.optString("code").equals("11011")) {
                            req.put("msg", "上传药品消耗信息接口返回：”药监下载数据暂不支持修改！“");
                            req.put("code", "227");
                        } else if (jorm.optString("code").equals("11012")) {
                            req.put("msg", "上传药品消耗信息接口返回：”产品品名已存在！“");
                            req.put("code", "228");
                        } else if (jorm.optString("code").equals("11013")) {
                            req.put("msg", "上传药品消耗信息接口返回：”产品品名不存在！“");
                            req.put("code", "229");
                        } else if (jorm.optString("code").equals("11014")) {
                            req.put("msg", "上传药品消耗信息接口返回：”产品规格已存在！“");
                            req.put("code", "230");
                        } else if (jorm.optString("code").equals("11015")) {
                            req.put("msg", "上传药品消耗信息接口返回：”未获取到产品类型！“");
                            req.put("code", "231");
                        } else if (jorm.optString("code").equals("11016")) {
                            req.put("msg", "上传药品消耗信息接口返回：”品名不是待审核状态，如果品名已通过审核，请传入品名 Code！“");
                            req.put("code", "232");
                        } else if (jorm.optString("code").equals("11017")) {
                            req.put("msg", "上传药品消耗信息接口返回：”产品品名存在待审核数据！“");
                            req.put("code", "233");
                        } else if (jorm.optString("code").equals("11018")) {
                            req.put("msg", "上传药品消耗信息接口返回：”产品规格存在待审核数据！“");
                            req.put("code", "234");
                        } else if (jorm.optString("code").equals("11019")) {
                            req.put("msg", "上传药品消耗信息接口返回：”发票号不匹配！“");
                            req.put("code", "235");
                        } else if (jorm.optString("code").equals("11020")) {
                            req.put("msg", "上传药品消耗信息接口返回：”平台代码[PlateformCode]不存在！“");
                            req.put("code", "236");
                        } else if (jorm.optString("code").equals("12000")) {
                            req.put("msg", "上传药品消耗信息接口返回：”数据已上传！“");
                            req.put("code", "237");
                        } else if (jorm.optString("code").equals("20000")) {
                            req.put("msg", "上传药品消耗信息接口返回：”数据不需要处理(更新状态等)！“");
                            req.put("code", "238");
                        } else if (jorm.optString("code").equals("20001")) {
                            req.put("msg", "上传药品消耗信息接口返回：”数据提交不成功！“");
                            req.put("code", "239");
                        } else if (jorm.optString("code").equals("20002")) {
                            req.put("msg", "上传药品消耗信息接口返回：”数据提交时发生错误！“");
                            req.put("code", "240");
                        } else if (jorm.optString("code").equals("20003")) {
                            req.put("msg", "上传药品消耗信息接口返回：”需要更新的数据不存在！“");
                            req.put("code", "241");
                        } else if (jorm.optString("code").equals("20004")) {
                            req.put("msg", "上传药品消耗信息接口返回：”数据已作废！“");
                            req.put("code", "242");
                        } else if (jorm.optString("code").equals("20005")) {
                            req.put("msg", "上传药品消耗信息接口返回：”数据已有后续流程，无法更新！“");
                            req.put("code", "243");
                        } else if (jorm.optString("code").equals("40101")) {
                            req.put("msg", "上传药品消耗信息接口返回：”用户名不存在！“");
                            req.put("code", "244");
                        } else if (jorm.optString("code").equals("40102")) {
                            req.put("msg", "上传药品消耗信息接口返回：”密码错误！“");
                            req.put("code", "245");
                        } else if (jorm.optString("code").equals("40300")) {
                            req.put("msg", "上传药品消耗信息接口返回：”未授权！“");
                            req.put("code", "246");
                        } else if (jorm.optString("code").equals("40301")) {
                            req.put("msg", "上传药品消耗信息接口返回：”过期！“");
                            req.put("code", "247");
                        } else if (jorm.optString("code").equals("40302")) {
                            req.put("msg", "上传药品消耗信息接口返回：”未到开始时间！“");
                            req.put("code", "248");
                        } else {
                            req.put("msg", "上传药品消耗信息接口返回错误代码不在字典范围内，错误代码为”" + jorm.optString("code") + "“；接口返回内容为：" + jorm.optString("Message"));
                            req.put("code", jorm.optString("code"));
                        }
                    }
                }
            }
        }
        return req;
    }

    /**
     * 上传采购计划
     *
     * @param body
     * @param ctx
     * @return
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    public Map<String, Object> doUpProcurementPlan(Map<String, Object> body,
                                                   Context ctx) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        String mac = getMacAddress();
        String ip = getIpAddress();
        String jgid = UserRoleToken.getCurrent().getManageUnit().getId();
        String HospitalCode = "";
        int DWXH = (Integer) body.get("DWXH");
        int JHDH = (Integer) body.get("JHDH");
        int SBXH = (Integer) body.get("SBXH");
        String CKJE = body.get("CKJE").toString();
        String JHBZ = (String) body.get("JHBZ");
        if (JHBZ != "null" && JHBZ != null && JHBZ != "") {
            JHBZ = JHBZ;
        } else {
            JHBZ = "null";
        }
        String BZGH = (String) body.get("BZGH");
        String BZRQ = (String) body.get("BZRQ");
        int XTSB = (Integer) body.get("XTSB");
        if (jgid.length() >= 9) {
            HospitalCode = jgid.substring(0, 9);
        } else {
            HospitalCode = jgid;
        }
        String sql = "select a.organizname as ORGANIZNAME from sys_organization a where a.organizcode='"
                + HospitalCode + "'";
        Map<String, Object> sql1 = null;
        try {
            sql1 = dao.doSqlLoad(sql, null);
        } catch (PersistentDataOperationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String sql2 = "select replace(a.code,'_YP','') as CODE,a.code as CODE2,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String sql4 = "select  DWZH as DWZH,DWMC as  DWMC from yk_jhdw  a where a.DWXH='" + DWXH + "'";
        Map<String, Object> sql41 = null;
        try {
            sql41 = dao.doSqlLoad(sql4, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String DWZH = (String) sql41.get("DWZH");
        String DWMC = (String) sql41.get("DWMC");
        if (DWZH.equals("NULL") || DWZH == null || DWZH == "") {
            req.put("msg", "存在未与省药品平台对照的供应商信息，供应商名称为" + DWMC + "！");
            req.put("code", "250");
        }
        String sql5 = "select  to_char(sum(jhsl),'fm999999990.009') as SumQuantity from YK_JH02   where JHDH='" + JHDH + "' and jgid='" + jgid + "' and xtsb='" + XTSB + "'";
        Map<String, Object> sql51 = null;
        try {
            sql51 = dao.doSqlLoad(sql5, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String SumQuantity = (String) sql51.get("SUMQUANTITY");
        Map<String, Object> map_ret = new HashMap<String, Object>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String KSRQ = sdf1.format(new Date());
        String hql = "select  to_char(t.SBXH,'fm999999990.009') as Seq,(select ypmc from yk_typk b where b.ypxh=t.ypxh) as YPMC ,(select a.ZBBM   from yk_cdxx a  where a.YPXH = t.YPXH and a.YPCD = t.YPCD and a.JGID = t.JGID)  as  UniCode,to_char(t.SPSL,'fm999999990.009') as Quantity,to_char(t.GJJG,'fm999999990.009') as Price,to_char(t.GJJE,'fm999999990.009') as Amount from  YK_JH02 t  where JHDH=:JHDH and jgid=:jgid  and xtsb=:XTSB";
        Map<String, Object> map_par = new HashMap<String, Object>();
        map_par.put("XTSB", XTSB);
        map_par.put("JHDH", JHDH);
        map_par.put("jgid", jgid);
        List<Map<String, Object>> list = null;
        try {
            list = dao.doSqlQuery(hql, map_par);
        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        }
        Map<String, Object> ListMap = new HashMap<String, Object>();
        String MX = null;
        if (req.size() == 0) {
            for (int i = 0; i < list.size(); i++) {
                String UniCode = (String) list.get(i).get("UNICODE");
                String YPMC = (String) list.get(i).get("YPMC");
                if (UniCode == null || UniCode == "null" || UniCode == "") {
                    req.put("msg", "存在未与省药品平台对照的药品信息，药品名称为" + YPMC + "!");
                    req.put("code", "249");
                }
                ;
                String Quantity = (String) list.get(i).get("QUANTITY");
                String Price = (String) list.get(i).get("PRICE");
                String Amount = (String) list.get(i).get("AMOUNT");
                String Seq = (String) list.get(i).get("SEQ");
                if (i == 0) {
                    MX = "{\n " +
                            "\"Seq\":\"" + Seq + "\",\n" +//明细顺序号(1,2,3…)
                            "\"UniCode\":\"" + UniCode + "\",\n" +//商品统一编码
                            "\"Quantity\":\"" + Quantity + "\",\n" + //数量
                            "\"Price\":\"" + Price + "\",\n" + //单价
                            "\"Amount\":\"" + Amount + "\",\n" +//金额
                            "\"DeptCode\":\"" + XTSB + "\",\n" +//科室编码
                            "\"DeptName\":\"" + XTSB + "\",\n" +//科室名称
                            "\"StoreCode\":\"" + XTSB + "\",\n" +//库房编码
                            "\"StoreName\":\"" + XTSB + "\",\n" + //库房名称
                            "\"Memo\":null\n" +  //明细备注
                            " },";
                } else if (i + 1 == list.size()) {
                    MX = MX + "{\n " +
                            "\"Seq\":\"" + Seq + "\",\n" +//明细顺序号(1,2,3…)
                            "\"UniCode\":\"" + UniCode + "\",\n" +//商品统一编码
                            "\"Quantity\":\"" + Quantity + "\",\n" + //数量
                            "\"Price\":\"" + Price + "\",\n" + //单价
                            "\"Amount\":\"" + Amount + "\",\n" +//金额
                            "\"DeptCode\":\"" + XTSB + "\",\n" +//科室编码
                            "\"DeptName\":\"" + XTSB + "\",\n" +//科室名称
                            "\"StoreCode\":\"" + XTSB + "\",\n" +//库房编码
                            "\"StoreName\":\"" + XTSB + "\",\n" + //库房名称
                            "\"Memo\":null\n" +  //明细备注
                            " }";
                } else {
                    MX = MX + "{\n " +
                            "\"Seq\":\"" + Seq + "\",\n" +//明细顺序号(1,2,3…)
                            "\"UniCode\":\"" + UniCode + "\",\n" +//商品统一编码
                            "\"Quantity\":\"" + Quantity + "\",\n" + //数量
                            "\"Price\":\"" + Price + "\",\n" + //单价
                            "\"Amount\":\"" + Amount + "\",\n" +//金额
                            "\"DeptCode\":\"" + XTSB + "\",\n" +//科室编码
                            "\"DeptName\":\"" + XTSB + "\",\n" +//科室名称
                            "\"StoreCode\":\"" + XTSB + "\",\n" +//库房编码
                            "\"StoreName\":\"" + XTSB + "\",\n" + //库房名称
                            "\"Memo\":null\n" +  //明细备注
                            " },";
                }
            }
            ;
            if (MX != null && req.size() == 0) {
                String CODE = (String) sql21.get("CODE");
                String APIURL = (String) body.get("url");
                String json = "{\n " +
                        "\"BusinessType\":\"" + "YY101\",\n" +
                        "\"HospitalCode\":\"" + CODE + "\",\n" +
                        "\"IP\":\"" + ip + "\",\n" +
                        "\"MAC\":\"" + mac + "\",\n" +
                        "\"HostName\":\"" + jgid + "\",\n" +
                        "\"Data\"" + ":[{\n " +
                        "\"HospitalCode\":\"" + CODE + "\",\n" +
                        "\"OrderNO\":\"" + JHDH + "\",\n" +  //订单编号
                        "\"OrderType\":\"" + "0\",\n" + //订单类型
                        "\"OrderLevel\":\"" + "0\",\n" + //订单等级
                        "\"SupplierCode\":\"" + DWZH + "\",\n" +//供应商编码
                        "\"DistributionSiteCode\":1,\n" +  //配送点编码
                        "\"Employee\":\"" + BZGH + "\",\n" +//采购员
                        "\"SumQuantity\":\"" + SumQuantity + "\",\n" +//总数量
                        "\"Amount\":\"" + CKJE + "\",\n" +//总金额
                        "\"ArrivalTime\":\"" + KSRQ + " 23:59:59\",\n" + //到货时间
                        "\"Creator\":\"" + BZGH + "\",\n" + //制单人
                        "\"CreateTime\":\"" + BZRQ + " 00:00:00\",\n" +  //制单时间
                        "\"Memo\":" + JHBZ + ",\n" + //备注
                        "\"PurchaseDetail\"" + ":[" +
                        MX +
                        " ]" +
                        " }]" +
                        " }";
                String res = httpURLPOSTCase(APIURL, json);
                JSONObject jorm = new JSONObject(res);
                if (jorm.optString("Code").equals("0")) {
                    String update = " update YK_JH01 t set t.SCBZ=:scbz,t.SCRQ=to_date(:scrq,'yyyy-mm-dd') where t.JGID='" + jgid + "' and t.XTSB=" + XTSB + " and t.JHDH='" + JHDH + "'";
                    Map<String, Object> p = new HashMap<String, Object>();
                    p.put("scbz", 2);
                    p.put("scrq", KSRQ);
                    try {
                        dao.doUpdate(update, p);
                    } catch (PersistentDataOperationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    req.put("msg", "采购订单信息上传成功！");
                    req.put("code", "200");
                } else if (jorm.optString("Code").equals("1")) {
                    req.put("msg", "采购订单信息上传接口返回：”错误！“");
                    req.put("code", "201");
                } else if (jorm.optString("Code").equals("2")) {
                    req.put("msg", "采购订单信息上传接口返回：”部分错误！“");
                    req.put("code", "202");
                } else if (jorm.optString("Code").equals("3")) {
                    req.put("msg", "采购订单信息上传接口返回：”与历史数据不匹配，无法新增/更新！“");
                    req.put("code", "203");
                } else if (jorm.optString("Code").equals("10000")) {
                    req.put("msg", "采购订单信息上传接口返回：”未知错误！“");
                    req.put("code", "204");
                } else if (jorm.optString("Code").equals("10001")) {
                    req.put("msg", "采购订单信息上传接口返回：”未指定业务类型[BusinessType]！“");
                    req.put("code", "205");
                } else if (jorm.optString("Code").equals("10002")) {
                    req.put("msg", "采购订单信息上传接口返回：”无效的业务类型[BusinessType]！“");
                    req.put("code", "206");
                } else if (jorm.optString("Code").equals("10003")) {
                    req.put("msg", "采购订单信息上传接口返回：”业务类型[BusinessType]在此请求中不支持！“");
                    req.put("code", "207");
                } else if (jorm.optString("Code").equals("10004")) {
                    req.put("msg", "采购订单信息上传接口返回：”业务类型[BusinessType]未配置数据流！“");
                    req.put("code", "208");
                } else if (jorm.optString("Code").equals("10005")) {
                    req.put("msg", "采购订单信息上传接口返回：”未知用户！“");
                    req.put("code", "209");
                } else if (jorm.optString("Code").equals("10006")) {
                    req.put("msg", "采购订单信息上传接口返回：”密码不正确！“");
                    req.put("code", "210");
                } else if (jorm.optString("Code").equals("10007")) {
                    req.put("msg", "采购订单信息上传接口返回：”用户未设置数据接收地址[PushUrl]！“");
                    req.put("code", "211");
                } else if (jorm.optString("Code").equals("10010")) {
                    req.put("msg", "采购订单信息上传接口返回：”未提交单据明细数据！“");
                    req.put("code", "212");
                } else if (jorm.optString("Code").equals("10011")) {
                    req.put("msg", "采购订单信息上传接口返回：”提交数据包含重复的商品统一编码[UniCode]！“");
                    req.put("code", "213");
                } else if (jorm.optString("Code").equals("10012")) {
                    req.put("msg", "采购订单信息上传接口返回：”生产厂家/供应商类型有误[OrgTypeCode]！“");
                    req.put("code", "214");
                } else if (jorm.optString("Code").equals("10013")) {
                    req.put("msg", "采购订单信息上传接口返回：”单据明细序号[Seq]重复！“");
                    req.put("code", "215");
                } else if (jorm.optString("Code").equals("10014")) {
                    req.put("msg", "采购订单信息上传接口返回：”单据明细记录数过多！“");
                    req.put("code", "216");
                } else if (jorm.optString("Code").equals("10015")) {
                    req.put("msg", "采购订单信息上传接口返回：”医院代码不能为空！“");
                    req.put("code", "217");
                } else if (jorm.optString("Code").equals("10016")) {
                    req.put("msg", "采购订单信息上传接口返回：”医院名称不能为空！“");
                    req.put("code", "218");
                } else if (jorm.optString("Code").equals("10017")) {
                    req.put("msg", "采购订单信息上传接口返回：”科室编码不能为空！“");
                    req.put("code", "219");
                } else if (jorm.optString("Code").equals("10018")) {
                    req.put("msg", "采购订单信息上传接口返回：”配送点(库房)编码不能为空空！“");
                    req.put("code", "220");
                } else if (jorm.optString("Code").equals("11001")) {
                    req.put("msg", "采购订单信息上传接口返回：”医院代码[HospitalCode]在平台字典中不存在！“");
                    req.put("code", "221");
                } else if (jorm.optString("Code").equals("11002")) {
                    req.put("msg", "采购订单信息上传接口返回：”商品统一编码[UniCode]在平台字典中不存在！“");
                    req.put("code", "222");
                } else if (jorm.optString("Code").equals("11003")) {
                    req.put("msg", "采购订单信息上传接口返回：”配送点[DistributionSiteCode]在平台字典中不存在！“");
                    req.put("code", "223");
                } else if (jorm.optString("Code").equals("11004")) {
                    req.put("msg", "采购订单信息上传接口返回：”供应商[SupplierCode]在平台字典中不存在！“");
                    req.put("code", "224");
                } else if (jorm.optString("Code").equals("11005")) {
                    req.put("msg", "采购订单信息上传接口返回：”厂家在平台字典中不存在！“");
                    req.put("code", "225");
                } else if (jorm.optString("Code").equals("11006")) {
                    req.put("msg", "采购订单信息上传接口返回：” 企业已存在待审核数据！“");
                    req.put("code", "226");
                } else if (jorm.optString("Code").equals("11011")) {
                    req.put("msg", "采购订单信息上传接口返回：”药监下载数据暂不支持修改！“");
                    req.put("code", "227");
                } else if (jorm.optString("Code").equals("11012")) {
                    req.put("msg", "采购订单信息上传接口返回：”产品品名已存在！“");
                    req.put("code", "228");
                } else if (jorm.optString("Code").equals("11013")) {
                    req.put("msg", "采购订单信息上传接口返回：”产品品名不存在！“");
                    req.put("code", "229");
                } else if (jorm.optString("Code").equals("11014")) {
                    req.put("msg", "采购订单信息上传接口返回：”产品规格已存在！“");
                    req.put("code", "230");
                } else if (jorm.optString("Code").equals("11015")) {
                    req.put("msg", "采购订单信息上传接口返回：”未获取到产品类型！“");
                    req.put("code", "231");
                } else if (jorm.optString("Code").equals("11016")) {
                    req.put("msg", "采购订单信息上传接口返回：”品名不是待审核状态，如果品名已通过审核，请传入品名 Code！“");
                    req.put("code", "232");
                } else if (jorm.optString("Code").equals("11017")) {
                    req.put("msg", "采购订单信息上传接口返回：”产品品名存在待审核数据！“");
                    req.put("code", "233");
                } else if (jorm.optString("Code").equals("11018")) {
                    req.put("msg", "采购订单信息上传接口返回：”产品规格存在待审核数据！“");
                    req.put("code", "234");
                } else if (jorm.optString("Code").equals("11019")) {
                    req.put("msg", "采购订单信息上传接口返回：”发票号不匹配！“");
                    req.put("code", "235");
                } else if (jorm.optString("Code").equals("11020")) {
                    req.put("msg", "采购订单信息上传接口返回：”平台代码[PlateformCode]不存在！“");
                    req.put("code", "236");
                } else if (jorm.optString("Code").equals("12000")) {
                    req.put("msg", "采购订单信息上传接口返回：”数据已上传！“");
                    req.put("code", "237");
                } else if (jorm.optString("Code").equals("20000")) {
                    req.put("msg", "采购订单信息上传接口返回：”数据不需要处理(更新状态等)！“");
                    req.put("code", "238");
                } else if (jorm.optString("Code").equals("20001")) {
                    req.put("msg", "采购订单信息上传接口返回：”数据提交不成功！“");
                    req.put("code", "239");
                } else if (jorm.optString("Code").equals("20002")) {
                    req.put("msg", "采购订单信息上传接口返回：”数据提交时发生错误！“");
                    req.put("code", "240");
                } else if (jorm.optString("Code").equals("20003")) {
                    req.put("msg", "采购订单信息上传接口返回：”需要更新的数据不存在！“");
                    req.put("code", "241");
                } else if (jorm.optString("Code").equals("20004")) {
                    req.put("msg", "采购订单信息上传接口返回：”数据已作废！“");
                    req.put("code", "242");
                } else if (jorm.optString("Code").equals("20005")) {
                    req.put("msg", "采购订单信息上传接口返回：”数据已有后续流程，无法更新！“");
                    req.put("code", "243");
                } else if (jorm.optString("Code").equals("40101")) {
                    req.put("msg", "采购订单信息上传接口返回：”用户名不存在！“");
                    req.put("code", "244");
                } else if (jorm.optString("Code").equals("40102")) {
                    req.put("msg", "采购订单信息上传接口返回：”密码错误！“");
                    req.put("code", "245");
                } else if (jorm.optString("Code").equals("40300")) {
                    req.put("msg", "采购订单信息上传接口返回：”未授权！“");
                    req.put("code", "246");
                } else if (jorm.optString("Code").equals("40301")) {
                    req.put("msg", "采购订单信息上传接口返回：”过期！“");
                    req.put("code", "247");
                } else if (jorm.optString("Code").equals("40302")) {
                    req.put("msg", "采购订单信息上传接口返回：”未到开始时间！“");
                    req.put("code", "248");
                } else {
                    req.put("msg", "采购订单信息上传接口返回错误代码不在字典范围内，错误代码为”" + jorm.optString("Code") + "“；接口返回内容为：" + jorm.optString("Message"));
                    req.put("code", jorm.optString("Code"));
                }
            }
        }
        return req;

    }

    //连接省药品平台地址
    public static String httpURLPOSTCase(String urlStr, String json)
            throws Exception {
        UserRoleToken user = UserRoleToken.getCurrent();
        String jgid = user.getManageUnitId();
        String methodUrl = urlStr;
        HttpURLConnection connection = null;
        PrintWriter dataout = null;
        BufferedReader reader = null;
        StringBuilder result = null;
        String line = null;
        String HospitalCode = "";
        if (jgid.length() >= 9) {
            HospitalCode = jgid.substring(0, 9);
        } else {
            HospitalCode = jgid;
        }
        String sql = "select a.code as CODE,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql1 = null;
        try {
            sql1 = dao.doSqlLoad(sql, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }

        String sql2 = "select a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }
        String CODE = (String) sql1.get("CODE");
        String TOKEN = (String) sql21.get("TOKEN");

        try {
            URL url = new URL(methodUrl);
            connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection

            connection.setRequestMethod("POST");// 设置请求方式为post,默认GET请求
            connection.setRequestProperty("Code", CODE);
            connection.setRequestProperty("token", TOKEN);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoOutput(true);// 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,默认情况下是false
            connection.setDoInput(true); // 设置是否从connection读入，默认情况下是true;
            //dataout = new PrintWriter(connection.getOutputStream());// 创建输入输出流,用于往连接里面输出携带的参数
            dataout = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));//把流设置utf-8格式，否则会中文乱码，造成接口返回空指针
            dataout.println(json);
            dataout.flush();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
                result = new StringBuilder();
                // 循环读取流
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.getProperty("line.separator"));//
                }
                return (result.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 验证药品是否下载过，若下载在无需插入到表中
    public List<Map<String, Object>> getypxxbyypxh(String UNICODE) {
        String sql = "select UNICODE,NAME from YK_TYPK_SPT where UNICODE=:UNICODE ";
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("UNICODE", UNICODE);
        List<Map<String, Object>> version = new ArrayList<Map<String, Object>>();
        try {
            version = dao.doQuery(sql, p);
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    // 验证进货单位HIS系统是否存在，若存在在无需插入到表中
    public List<Map<String, Object>> getjhdwbydwmc(String DWMC) {

        String sql = "select DWXH from YK_JHDW where DWMC=:DWMC";
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("DWMC", DWMC);
        List<Map<String, Object>> version = new ArrayList<Map<String, Object>>();
        try {
            version = dao.doQuery(sql, p);
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    // 验证下载的配送单中的药品是否已经和HIS系统对照
    public List<Map<String, Object>> gethiszbbmbyunicode(String UNICODE, String JGID) {

        String sql = "select YPXH from YK_CDXX where ZBBM='" + UNICODE + "' and JGID='" + JGID + "'";
        Map<String, Object> sql1 = null;
        try {
            sql1 = dao.doSqlLoad(sql, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }

        List<Map<String, Object>> version = new ArrayList<Map<String, Object>>();
        if (sql1.size() > 0) {
            version.add(sql1);
        }
        return version;
    }

    public List<Map<String, Object>> getypxxbyzbbm(Map<String, Object> body,
                                                   Context ctx) throws ModelDataOperationException {
        UserRoleToken user = UserRoleToken.getCurrent();
        String jgid = user.getManageUnitId();
        String UNICODE = (String) body.get("SPT_YPXH");
        String sql = "select YPXH from YK_CDXX WHERE  JGID=:JGID AND ZBBM=:ZBBM";
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("ZBBM", UNICODE);
        p.put("JGID", jgid);
        List<Map<String, Object>> version = new ArrayList<Map<String, Object>>();
        try {
            version = dao.doQuery(sql, p);
        } catch (PersistentDataOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    // 获取mac地址
    public static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            byte[] mac = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
                        .nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual()
                        || netInterface.isPointToPoint()
                        || !netInterface.isUp()) {
                    continue;
                } else {
                    mac = netInterface.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i],
                                    (i < mac.length - 1) ? "-" : ""));
                        }
                        if (sb.length() > 0) {
                            return sb.toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger _logger = null;
            _logger.error("MAC地址获取失败", e);
        }
        return "";
    }

    // 获取ip地址
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
                        .nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual()
                        || netInterface.isPointToPoint()
                        || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface
                            .getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger _logger = null;
            _logger.error("IP地址获取失败", e);
        }
        return "";
    }
}
