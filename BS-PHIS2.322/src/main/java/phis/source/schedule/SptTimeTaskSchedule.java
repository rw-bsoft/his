package phis.source.schedule;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SptTimeTaskSchedule extends AbstractJobSchedule {
    public static final Logger logger = LoggerFactory.getLogger(SptTimeTaskSchedule.class);
    public static BaseDAO dao = new BaseDAO();
    @Override
    public void doJob(BaseDAO dao, Context ctx) throws ModelDataOperationException {
        try {
            String sql = "select jgid as JGID from jg_spt";
            List<Map<String,Object>> list=dao.doSqlQuery(sql,null);
            if(list.size()>0){
                for(int i = 0;i<list.size();i++){

                    String JGID = list.get(i).get("JGID")+"";
                    logger.info(new Date() + "========="+JGID+"  库存上传开始!==========");
                    doUpLoadkucun(JGID);
                    logger.info(new Date() + "========="+JGID+"  库存上传结束！=========");

                    logger.info(new Date() + "========="+JGID+"  消耗上传开始!==========");
                    doUpLoadxiaohao(JGID);
                    logger.info(new Date() + "========="+JGID+"  消耗上传结束！=========");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传库存到省平台药品信息
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void doUpLoadkucun(String jgid) throws Exception {
        //Map<String, Object> req = new HashMap<String, Object>();
        String mac = getMacAddress();
        String ip = getIpAddress();
        String APIURL = "http://10.2.200.202:8042/dep/business/post";
        //String jgid = JGID;
        String sql2 = "select replace(a.code,'_YP','') as CODE,a.code as CODE2,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        }
        String hql = "SELECT  to_char(c.LSJG,'fm999999990.009') as LSJG,c.JHJG as JHJG,a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,a.ZXDW as ZXDW,a.JLDW as JLDW,b.CDMC as CDMC,to_char(sum(c.KCSL),'fm999999990.009') as KCSL," +
                "c.YPPH as YPPH,to_char(c.YPXQ,'yyyy-mm-dd') as YPXQ,c.TYPE as TYPE,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,c.YPXH as YPXH,c.YPCD as YPCD,a.YPDM as YPDM,a.YPJL as YPJL," +
                "d.KWBM as KWBM,d.YKZF as YKZF,a.ZBLB as ZBLB,e.ZBBM as ZBBM FROM YK_CDDZ b,YK_KCMX c,YK_TYPK a,YK_YPXX d,YK_CDXX e WHERE  ( b.YPCD = c.YPCD ) and  c.JGID = d.JGID and ( c.YPXH = a.YPXH )" +
                " and ( a.YPXH = d.YPXH )  and (c.JGID = e.JGID)  and (c.YPCD = e.YPCD)  and (c.YPXH = e.YPXH) and  ( c.JGID = d.JGID ) and ( d.JGID = '" + jgid + "' ) and e.ZBBM is not null" +
                " GROUP BY a.YPMC,a.YPGG,a.YPDW,a.ZXDW,a.YPJL,a.JLDW,b.CDMC,c.YPPH,c.YPXQ,c.TYPE,a.PYDM,a.WBDM,a.JXDM,a.QTDM,c.YPXH,c.YPCD,a.YPDM,d.KWBM,d.YKZF,a.ZBLB,c.LSJG,c.JHJG,e.ZBBM  order by c.YPXH";
        List<Map<String, Object>> list = null;
        try {
            list = dao.doSqlQuery(hql, null);
        } catch (PersistentDataOperationException e) {
            e.printStackTrace();
        }
        if (list.size() == 0) {
            logger.info(new Date() + "=========="+jgid+"  当前无库存信息上传！==========");
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
                    String sql3 = "select t.name as YPMC,t.ManufacturerCode as ManufacturerCode,t.Manufacturer as Manufacturer from yk_typk_spt t where t.unicode='" + ZBBM + "'";
                    Map<String, Object> sql31 = null;
                    try {
                        sql31 = dao.doSqlLoad(sql3, null);
                    } catch (PersistentDataOperationException e) {
                        e.printStackTrace();
                    }
                    if (sql31 != null) {
                        String CODE = (String) sql21.get("CODE");
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
                        String res = httpURLPOSTCase(APIURL, json, jgid);
                        JSONObject jorm = new JSONObject(res);
                        if (jorm.optString("code").equals("0") || jorm.optString("Code").equals("0")) {
                            logger.info(new Date() + "========机构编码："+jgid+",药品对照编码："+ZBBM+"  的库存信息上传成功！=========");
                        }else{
                            logger.error(new Date() + "========机构编码："+jgid+",药品对照编码："+ZBBM+"  的库存信息上传失败！========");
                        }
                    }
                }
            }
        }
    }

    /**
     * 上传消耗信息到省平台药品信息
     * @return
     * @throws Exception
     */

    public static void doUpLoadxiaohao(String JGID) throws Exception {
        String mac = getMacAddress();
        String ip = getIpAddress();
        String jgid = JGID;
        String APIURL = "http://10.2.200.202:8042/gateway/dep/business/post";
        String sql2 = "select replace(a.code,'_YP','') as CODE,a.code as CODE2,a.token as TOKEN from jg_spt a where a.jgid='" + jgid + "'";
        Map<String, Object> sql21 = null;
        try {
            sql21 = dao.doSqlLoad(sql2, null);
        } catch (PersistentDataOperationException e1) {
            e1.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //得到前一天
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String XHRQ = df.format(date);
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
            logger.info(new Date() + "========="+jgid+"  当前无药品消耗信息上传！=========");
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
                        String res = httpURLPOSTCase(APIURL, json, jgid);
                        JSONObject jorm = new JSONObject(res);
                        if (jorm.optString("code").equals("0") || jorm.optString("Code").equals("0")) {
                            logger.info(new Date() + "========机构编码："+jgid+",药品对照编码："+ZBBM+"  的药品消耗信息上传成功！=========");
                        }else{
                            logger.error(new Date() + "========机构编码："+jgid+",药品对照编码："+ZBBM+"  的药品消耗信息上传失败！========");
                        }
                    }
                }
            }
        }
    }


    //连接省药品平台地址
    public static String httpURLPOSTCase(String urlStr, String json, String jgid) throws Exception {
        String methodUrl = urlStr;
        HttpURLConnection connection = null;
        PrintWriter dataout = null;
        BufferedReader reader = null;
        StringBuilder result = null;
        String line = null;
        String HospitalCode = "";
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
            dataout = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));//把流设置utf-8格式，否则会中文乱码，造成接口返回空指针
            dataout.println(json);
            dataout.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
                result = new StringBuilder();
                // 循环读取流
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.getProperty("line.separator"));
                }
                return (result.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            logger.error("MAC地址获取失败", e);
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
            logger.error("IP地址获取失败", e);
        }
        return "";
    }

}
