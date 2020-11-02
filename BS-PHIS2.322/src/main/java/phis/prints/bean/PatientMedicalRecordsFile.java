package phis.prints.bean;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class PatientMedicalRecordsFile implements IHandler {
    @Override
    public void getFields(Map<String, Object> request,
                          List<Map<String, Object>> records, Context ctx)
            throws PrintException {
        Map<String, Object> result = new HashMap<String, Object>();
        StringBuffer bljbxx = getBljbxx(request, ctx);
        bljbxx.append("\n");
        bljbxx.append("处置:\n");
        BaseDAO dao = new BaseDAO(ctx);
        long CLINICID = Long.parseLong(request.get("CLINICID") + "");// 就诊序号
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("JZXH", CLINICID);
        String sql_CF = "SELECT t2.YPMC as YPMC,t1.YFGG as YFGG,(SELECT t3. PCMC  FROM GY_SYPC t3 WHERE t3.PCBM = t1.YPYF)as YPYF,(SELECT t3. BZXX  FROM GY_SYPC t3 WHERE t3.PCBM = t1.YPYF)as BZXX,t1.YCJL as YCJL,t2.JLDW as JLDW,t2.YPGG as YPGG ,t2.YPDW as YPDW,t1.YFDW as YFDW "
                + ",t1.YPSL as YPSL,t1.YFDW as YFDW,(SELECT t4.XMMC FROM ZY_YPYF t4 WHERE t4.YPYF= t1.GYTJ) as XMMC,t1.YPZH as YPZH,t1.CFTS as CFTS,(t1.CFTS*t1.YPSL) as SL,t.CFLX as CFLX   "
                + "FROM MS_CF01 t,MS_CF02 t1,YK_TYPK t2 WHERE t.CFSB = t1.CFSB and t.JZXH =:JZXH and t2.YPXH = t1.YPXH and t.ZFPB<>1 ORDER BY t1.YPZH,t1.SBXH asc ";
        String sql_CZ = "SELECT t1.FYMC as FYMC,t1.FYDW as FYDW,t.YJZH as YJZH ,t.YLSL as YLSL FROM MS_YJ02 t, GY_YLSF t1, MS_YJ01 t2  "
                + "WHERE t1.FYXH = t.YLXH and t2.YJXH = t.YJXH and t2.JZXH =:JZXH and t2.ZFPB<>1 order by t.YJZH";
        try {
            List<Map<String, Object>> list_CF = dao.doQuery(sql_CF, parameter); // 得到处方
            // 药品
            int zh = 1;
            int ypzh = 0;
            int yjzh = 0;

            for (int i = 0; i < list_CF.size(); i++) {
                if (i == 0) {
                    list_CF.get(0).put("ZH", zh+".");
                    ypzh = Integer.parseInt(list_CF.get(i).get("YPZH") + "");
                } else {
                    if (Integer.parseInt(list_CF.get(i).get("YPZH") + "") != ypzh) {
                        zh++;
                       
                        list_CF.get(i).put("ZH", zh+".");
                        ypzh = Integer
                                .parseInt(list_CF.get(i).get("YPZH") + "");
                    } else {
                        list_CF.get(i).put("ZH", "");
                    }
                }
                if (list_CF.get(i).get("YPMC") != null) {
                    list_CF.get(i).put("YPMC", list_CF.get(i).get("YPMC") + "");
                    if (list_CF.get(i).get("YFGG") != null) {
                        list_CF.get(i).put(
                                "XYYPMC",
                                list_CF.get(i).get("YPMC") + "" + "/"
                                        + list_CF.get(i).get("YFGG") + "");
                    }
                }
                if (list_CF.get(i).get("XMMC") != null) {
                    list_CF.get(i).put("XMMC", list_CF.get(i).get("XMMC") + "");
                }
                if (list_CF.get(i).get("YCJL") != null) {
                    list_CF.get(i).put("YCJL", list_CF.get(i).get("YCJL") + "");
                    if (list_CF.get(i).get("JLDW") != null) {
                        list_CF.get(i).put(
                                "YCJL",
                                list_CF.get(i).get("YCJL") + ""
                                        + list_CF.get(i).get("JLDW") + "");
                    }
                }
                if (list_CF.get(i).get("YPSL") != null) {
                    list_CF.get(i).put("YPSL", list_CF.get(i).get("YPSL") + "");
                    if (list_CF.get(i).get("YFDW") != null) {
                        list_CF.get(i).put(
                                "YPSL",
                                list_CF.get(i).get("YPSL") + ""
                                        + list_CF.get(i).get("YFDW") + "");
                    } else {
                        list_CF.get(i).put("YPSL",
                                list_CF.get(i).get("YPSL") + "");
                    }
                }
                if (list_CF.get(i).get("CFTS") != null) {
                    list_CF.get(i).put("CFTS", list_CF.get(i).get("CFTS") + "");
                }
                if (list_CF.get(i).get("YPYF") != null) {
                    list_CF.get(i).put("YPYF", list_CF.get(i).get("YPYF") + "");
                }
                if (list_CF.get(i).get("SL") != null) {
                    list_CF.get(i).put("SL", list_CF.get(i).get("SL") + "");
                    if (list_CF.get(i).get("YFDW") != null) {
                        list_CF.get(i).put(
                                "SL",
                                list_CF.get(i).get("SL") + ""
                                        + list_CF.get(i).get("YFDW") + "");
                    } else {
                        list_CF.get(i).put("YPSL",
                                list_CF.get(i).get("YPSL") + "");
                    }
                }
                if (list_CF.get(i).get("YPMC") != null) {
                    list_CF.get(i).put("ZYYPMC", list_CF.get(i).get("YPMC") + "");
                }
                Map<String, Object> temp = list_CF.get(i);
                String ZHtemp = temp.get("ZH").toString();
                ZHtemp = ("  " + ZHtemp);
//                ZHtemp = ZHtemp.substring(ZHtemp.length() - 6);
                if (list_CF.get(i).get("CFLX") != null) {
                	 String CFLX = temp.get("CFLX").toString();
                	    if ((CFLX.equals("1")) || (CFLX.equals("2"))) {
                	    	String XYYPMC = "";
                	    	if(temp.get("XYYPMC") != null){
            	    			XYYPMC = temp.get("XYYPMC").toString();
            	    		}
                	    	if(ZHtemp.equals("  ")){
                	    		bljbxx.append(ZHtemp).append("   ").append(XYYPMC).append("  ").append(temp.get("YPYF")).append("  ")
                                .append(temp.get("YCJL").toString()).append("  ").append(temp.get("YPSL").toString()).append("  ").append(temp.get("XMMC").toString()).append("\n");                                   
                	    	}else{
                	    		bljbxx.append(ZHtemp).append(" ").append(XYYPMC).append("  ").append(temp.get("YPYF")).append("  ")
                                .append(temp.get("YCJL").toString()).append("  ").append(temp.get("YPSL").toString()).append("  ").append(temp.get("XMMC").toString()).append("\n");            	    	
                	    	}
                	    	}
                	    if (CFLX.equals("3")){
                	    	if(ZHtemp.equals("  ")){
                	               bljbxx.append(ZHtemp).append("   ").append(temp.get("ZYYPMC").toString()).append("  ").append(temp.get("YPYF")).append("  ")
                                  .append(temp.get("SL").toString()).append("  ").append(temp.get("XMMC").toString()).append("\n");
                	    	  }else{
                	    		  bljbxx.append(ZHtemp).append(" ").append(temp.get("ZYYPMC").toString()).append("  ").append(temp.get("YPYF")).append("  ")
                                  .append(temp.get("SL").toString()).append("  ").append(temp.get("XMMC").toString()).append("\n");
                	    	  }
                	    }
                }
            }
            Map<String, Object> parameter_KSSJ = new HashMap<String, Object>();
            parameter_KSSJ.put("JZXH", CLINICID);
            String sql_BCJL = "SELECT t.BRID as BRID,t.BQGZ as BQGZ,t.JKJYNR as JKJYNR FROM MS_BCJL t WHERE t.JZXH =:JZXH";
            List<Map<String, Object>> list_BCJL = dao.doQuery(sql_BCJL,
                    parameter_KSSJ); // 得到病程记录
            if (list_BCJL.size() > 0) {
                // 病程记录插入
                 if (list_BCJL.get(0).get("BQGZ") != null) {
                     String clyjText = list_BCJL.get(0).get("BQGZ").toString();
                     bljbxx.append("  "+clyjText).append("\n");
                 }
                 bljbxx.append("\n");
                 if (list_BCJL.get(0).get("JKJYNR") != null) {
                     String jkjyText = list_BCJL.get(0).get("JKJYNR").toString();
                     bljbxx.append("健康教育："+jkjyText).append("\n");
                 }
            }
            long BRID = Long.parseLong(request.get("BRID") + "");// 病人ID
            Map<String, Object> parameter_KSSJ1 = new HashMap<String, Object>();
            parameter_KSSJ1.put("BRID", BRID);
            parameter_KSSJ1.put("JZXH", CLINICID);
            String sql_BRFB = "SELECT  JZYS  as  JZYS FROM MS_BCJL t WHERE  t.BRID=:BRID AND t.JZXH=:JZXH ";
            List<Map<String, Object>> list_BRXZ = dao.doQuery(sql_BRFB,
                    parameter_KSSJ1); //
            if (list_BRXZ != null && list_BRXZ.size() > 0) {
                Map<String, Object> ysdm = new HashMap<String, Object>();
                ysdm.put("JZYS",
                         DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
                                 .getText(list_BRXZ.get(0).get("JZYS") + ""));
                bljbxx.append("\n"); 
            bljbxx.append("                                                      医生签名："+ysdm.get("JZYS"));

            }
        } catch (Exception e) {
            throw new RuntimeException("查询病人处方信息失败!");
        }

        result.put("BLXX", bljbxx);
        records.add(result);
    }

    @Override
    public void getParameters(Map<String, Object> request,
                              Map<String, Object> response, Context ctx) throws PrintException {
        BaseDAO dao = new BaseDAO(ctx);
        UserRoleToken user = UserRoleToken.getCurrent();
        String JGMC = user.getManageUnit().getName();// 用户的机构名称
        //response.put("JGMC", JGMC);
        long CLINICID = Long.parseLong(request.get("CLINICID") + "");// 就诊序号
        long BRID = Long.parseLong(request.get("BRID") + "");// 病人ID

        Map<String, Object> parameter_KSSJ = new HashMap<String, Object>();
        parameter_KSSJ.put("JZXH", CLINICID);
        Map<String, Object> parameter_KSSJ1 = new HashMap<String, Object>();
        parameter_KSSJ1.put("BRID", BRID);

        String sql_JZLS = "SELECT t.KSSJ as JZSJ,(SELECT ys.PERSONID FROM SYS_Personnel ys WHERE ys.PERSONID = t.YSDM) as YSGH FROM YS_MZ_JZLS t WHERE t.JZXH =:JZXH";
        String sql_BRFB = "SELECT t.BRXZ as BRXZ,(SELECT k.KSMC FROM MS_GHKS k WHERE k.KSDM=t.KSDM) as JZKS,t1.YSDM as YSDM,t.JZHM as JZHM,(SELECT k.BRXM FROM MS_BRDA k WHERE k.BRID = t.BRID) as BRXM  FROM MS_GHMX t,YS_MZ_JZLS t1 WHERE t.BRID=t1.BRBH AND t.BRID=:BRID AND t1.JZXH=:JZXH and t.SBXH=t1.GHXH";
 
        try {
            response.put("title1", JGMC);
            List<Map<String, Object>> list_KSSJ = dao.doQuery(sql_JZLS,
                    parameter_KSSJ); // 得到就诊时间
            parameter_KSSJ1.put("JZXH", CLINICID);
            List<Map<String, Object>> list_BRXZ = dao.doQuery(sql_BRFB,
                    parameter_KSSJ1); //
            if (list_BRXZ != null && list_BRXZ.size() > 0) {
                String xzmc = "";
                if (list_BRXZ.get(0).get("BRXZ") != null && list_BRXZ.get(0).get("BRXZ") != "") {
                    String BRXZ = list_BRXZ.get(0).get("BRXZ").toString();
                        xzmc = DictionaryController.instance().getDic("phis.dictionary.patientProperties")
                                .getText(list_BRXZ.get(0).get("BRXZ") + "");
                }
                if (list_BRXZ.get(0).get("JZKS") != null) {

                    response.put("JZKS", list_BRXZ.get(0).get("JZKS") + "");
                } 
                if (list_BRXZ.get(0).get("BRXM") != null) {
                    response.put("BRXM",
                    		list_BRXZ.get(0).get("BRXM"));
                }
                response.put("YSDM",
                        DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
                                .getText(list_BRXZ.get(0).get("YSDM") + ""));
                response.put("BRXZ", xzmc);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (list_KSSJ.size() > 0) {
                if (list_KSSJ.get(0).get("JZSJ") != null) {
                    response.put("ZDSJ",
                            sdf.format(BSHISUtil.toDate(list_KSSJ.get(0).get("JZSJ") + "")));
                }
                if (list_KSSJ.get(0).get("YSGH") != null) {
                    response.put("YSGH", list_KSSJ.get(0).get("YSGH") + "");
                }
            }
            if (!request.get("NL").equals("null")) {
                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
             //   Date bir = simpleDateFormat.parse(request.get("NL").toString());
               // Map<String, Object> personAge = BSPHISUtil.getPersonAge(bir, new Date());
                response.put("NL", request.get("NL"));
            }
            if (request.get("LXDH") != null && (!request.get("LXDH").equals("null"))) {
                response.put("LXDH", request.get("LXDH"));
            }
            if (request.get("LXDZ") != null && (!request.get("LXDZ").equals("null"))) {
                response.put("LXDZ", hexToString(request.get("LXDZ").toString()));
            }
            if (!request.get("XB").equals("null")) {
                if (request.get("XB").equals("1")) {
                    response.put("XB", "男");
                }
                if (request.get("XB").equals("2")) {
                    response.put("XB", "女");
                }
                if (request.get("XB").equals("9")) {
                    response.put("XB", "未说明的性别");
                }
                if (request.get("XB").equals("0")) {
                    response.put("XB", "未知的性别");
                }
            }
            if (!request.get("MZHM").equals("null")) {
                response.put("MZHM", request.get("MZHM"));
            }
        } catch (Exception e) {
            throw new RuntimeException("得到病人基本信息失败!");
        }

    }

    public String hexToString(String Hex) {
        String tempStrs[] = Hex.split("/u");
        String newStr = "";
        for (int i = 1; i < tempStrs.length; i++) {
            newStr += (char) (Integer.parseInt(tempStrs[i]));
        }
        return newStr;
    }

    public StringBuffer getBljbxx(Map<String, Object> request, Context ctx) {
        StringBuffer result = new StringBuffer();
        BaseDAO dao = new BaseDAO(ctx);
        UserRoleToken user = UserRoleToken.getCurrent();
        long CLINICID = Long.parseLong(request.get("CLINICID") + "");// 就诊序号
        long BRID = Long.parseLong(request.get("BRID") + "");// 病人ID

        Map<String, Object> parameter_KSSJ = new HashMap<String, Object>();
        parameter_KSSJ.put("JZXH", CLINICID);
        Map<String, Object> parameter_KSSJ1 = new HashMap<String, Object>();
        parameter_KSSJ1.put("BRID", BRID);
        String sql_BCJL = "SELECT t.BMI as BMI,t.W as W,t.H as H,t.ZSXX as ZSXX,t.XBS as XBS,t.TGJC as TGJC,t.FZJC as FZJC,t.CLCS as CLCS,JWS as JWS,T as T,P as P,R as R,SSY as SSY,SZY as SZY,KS as KS,YT as YT,HXKN as HXKN,OT as OT,FT as FT,FX as FX,PZ as PZ,QT as QT,t.GMS as GMS FROM MS_BCJL t WHERE t.JZXH =:JZXH";
        String sql_BRZD = "SELECT t.ZDMC as ZDMC ,t.ZZBZ as ZZBZ FROM MS_BRZD t WHERE t.JZXH = :JZXH order  by t.ZZBZ desc,t.PLXH ASC ";

        try {
            List<Map<String, Object>> list_BCJL = dao.doQuery(sql_BCJL,
                    parameter_KSSJ); // 得到病程记录
            List<Map<String, Object>> list_BRZD = dao.doQuery(sql_BRZD,
                    parameter_KSSJ); // 得到病人诊断记录
            parameter_KSSJ1.put("JZXH", CLINICID);

            if (list_BCJL.size() > 0) {
                // 病程记录插入
                 if (list_BCJL.get(0).get("ZSXX") != null) {
                     String zsText = list_BCJL.get(0).get("ZSXX").toString();
                     result.append("主诉：" + zsText).append("\n");
                 }
                 result.append("\n");
                 if (list_BCJL.get(0).get("XBS") != null) {
                     String xbsText = list_BCJL.get(0).get("XBS").toString();
                     result.append("现病史："+xbsText).append("\n");
                 }
                 result.append("\n");
                 if (list_BCJL.get(0).get("GMS") != null) {
                     String gmsText = list_BCJL.get(0).get("GMS").toString();
                     result.append("过敏史："+gmsText).append("\n");
                 }
                 result.append("\n");
                 if (list_BCJL.get(0).get("JWS") != null) {
                     String jwsText = list_BCJL.get(0).get("JWS").toString();
                     result.append("既往史："+jwsText).append("\n").append("\n");
                 }
//                /************************以下是体温脉搏信息查询开始*****************************/
                StringBuffer twmb = new StringBuffer();
                twmb.append("     体温:");
                if (list_BCJL.get(0).get("T") != null
                        && !"null".equals(list_BCJL.get(0).get("T"))) {
                    twmb.append(list_BCJL.get(0).get("T"));
                }
                twmb.append("℃    脉搏:");
                if (list_BCJL.get(0).get("P") != null
                        && !"null".equals(list_BCJL.get(0).get("P"))) {
                    twmb.append(list_BCJL.get(0).get("P"));
                }
                twmb.append("次/分   呼吸:");

                if (list_BCJL.get(0).get("R") != null
                        && !"null".equals(list_BCJL.get(0).get("R"))) {
                    twmb.append(list_BCJL.get(0).get("R"));
                }
                twmb.append("次/分");
                twmb.append("   血压:");
                if (list_BCJL.get(0).get("SSY") != null
                        && !"null".equals(list_BCJL.get(0).get("SSY"))) {
                    twmb.append(list_BCJL.get(0).get("SSY"));
                }
                twmb.append("/");
                if (list_BCJL.get(0).get("SZY") != null
                        && !"null".equals(list_BCJL.get(0).get("SZY"))) {
                    twmb.append(list_BCJL.get(0).get("SZY"));
                }
                twmb.append("mmHg").append("\n");
                //增加身高
                twmb.append("     身高:");
                if (list_BCJL.get(0).get("H") != null
                        && !"null".equals(list_BCJL.get(0).get("H"))) {
                    twmb.append(list_BCJL.get(0).get("H"));
                }
                //增加体重
                twmb.append("/cm   体重:");
                if (list_BCJL.get(0).get("W") != null
                        && !"null".equals(list_BCJL.get(0).get("W"))) {
                    result.append("     ");
                    twmb.append(list_BCJL.get(0).get("W"));
                }
                //增加BMI
                twmb.append("/kg     BMI:");
                if (list_BCJL.get(0).get("BMI") != null
                        && !"null".equals(list_BCJL.get(0).get("BMI"))) {
                    twmb.append(list_BCJL.get(0).get("BMI"));
                }

                result.append(twmb.toString()).append("\n");
                /************************体温脉搏信息查询结束*****************************/
                result.append("\n");
                if (list_BCJL.get(0).get("TGJC") != null) {
                    String tgjcText = list_BCJL.get(0).get("TGJC").toString();
                    result.append("体格检查："+tgjcText).append("\n");
                }
                result.append("\n");
                if (list_BCJL.get(0).get("FZJC") != null) {
                    String fzjcText = list_BCJL.get(0).get("FZJC").toString();
                    result.append("辅助检查："+fzjcText).append("\n");
                }
            }
            // 插入诊断信息
            String ss = "";
            for (int i = 0; i < list_BRZD.size(); i++) {
            	if(i%2!=0){
                ss += (i + 1) + "." + list_BRZD.get(i).get("ZDMC") + " \n";
            	}else{
            		 ss += (i + 1) + "." + list_BRZD.get(i).get("ZDMC")+"     ";
            	}
            }

            String YT = "";
            String JHLB = "";
            result.append("初步诊断:").append("\n");
           
            if (!ss.equals("")) {
                result.append(stringFormat("     " + ss, 40));
            }
        } catch (Exception e) {
            throw new RuntimeException("得到病人基本信息失败!");
        }
        return result;
    }

    /**
     * 门诊病例报表字符串格式化
     *
     * @param beforeStr 需要格式化字符串
     * @param subLength 分割长度
     * @return 格式化后字符串
     * @author dongzhic 2018.04.13
     */
    public String stringFormat(String beforeStr, int subLength) {
        String afterStr = "";
        String[] strTemp = beforeStr.split("\n");
        for (int i = 0; i < strTemp.length; i++) {
            String str = strTemp[i];
            if (str.length() >= subLength) {
                //1.把原始字符串分割成指定长度的字符串列表
                int size = str.length() / subLength;
                if (str.length() % subLength != 0) {
                    size += 1;
                }
                //2.把原始字符串分割成指定长度的字符串列表
                String strFormat = "";
                for (int index = 0; index < size; index++) {
                    //3.分割字符串，如果开始位置大于字符串长度，返回空
                    String childStr = "";
                    int f = index * subLength;
                    int t = (index + 1) * subLength;
                    if (t > str.length()) {
                        childStr = str.substring(f, str.length());
                    } else {
                        childStr = str.substring(f, t);
                    }
                    strFormat += childStr;
                    strFormat += "\n";
                    strFormat += "     ";
                }
                afterStr += strFormat;
            } else {
                afterStr += str;
                afterStr += "\n";
                afterStr += "     ";
            }
        }
        return afterStr;
    }
}
