package phis.prints.bean;

import java.util.*;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

public class ExtractionAgreementFile implements IHandler {

    public void getFields(Map<String, Object> request, List<Map<String, Object>> records, Context ctx) throws PrintException {

    }

    @Override
    public void getParameters(Map<String, Object> request, Map<String, Object> response, Context ctx) throws PrintException {
        BaseDAO dao = new BaseDAO(ctx);
        String id = (String) request.get("id");
        UserRoleToken user = UserRoleToken.getCurrent();
        String doctorname=user.getUserName();
        //获取当前日期
        Calendar rightNow = Calendar.getInstance();
        /*用Calendar的get(int field)方法返回给定日历字段的值。HOUR 用于 12 小时制时钟 (0 - 11)，HOUR_OF_DAY 用于 24 小时制时钟。*/
        String year = rightNow.get(Calendar.YEAR)+"";
        String month = (rightNow.get(Calendar.MONTH)+1)+""; //第一个月从0开始，所以得到月份＋1
        String day = rightNow.get(rightNow.DAY_OF_MONTH)+"";
        //String hour12 = rightNow.get(rightNow.HOUR)+"";
        String hour24 = rightNow.get(rightNow.HOUR_OF_DAY)+"";
        String minute = rightNow.get(rightNow.MINUTE)+"";
        //String second = rightNow.get(rightNow.SECOND)+"";
        //String millisecond = rightNow.get(rightNow.MILLISECOND)+"";
        //String TimeNow12 = year+"-"+month+"-"+day+" "+hour12+":"+minute+":"+second+":"+millisecond;
        //String TimeNow24 = year+"-"+month+"-"+day+" "+hour24+":"+minute+":"+second+":"+millisecond;
        //System.out.println("日历："+rightNow+"\n12小时制时钟："+TimeNow12+"\n24小时制时钟："+TimeNow24);
        if(id != null||!"null".equals(id)){
            //Map<String, Object> params = new HashMap<String, Object>();
            //params.put("id", id);
            String sql = "select personName as PERSONNAME, sex as SEX, age as AGE, diagnosis as DIAGNOSIS,proposed as PROPOSED from DPC_ExtractionAgreement where id='"+id+"'";
            try {
                List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
                resList = dao.doSqlQuery(sql, null);
                Map<String, Object> result = resList.get(0);
                if(result.get("PERSONNAME") == null){
                    response.put("PERSONNAME", "");
                }else{
                    response.put("PERSONNAME", result.get("PERSONNAME"));
                }

                if(result.get("SEX") == "2"){
                    response.put("SEX", "女");
                }else{
                    response.put("SEX", "男");
                }

                if(result.get("AGE") == null){
                    response.put("AGE", "");
                }else{
                    response.put("AGE", result.get("AGE"));
                }

                if(result.get("DIAGNOSIS") == null){
                    response.put("DIAGNOSIS", "");
                }else{
                    response.put("DIAGNOSIS", result.get("DIAGNOSIS"));
                }

                if(result.get("PROPOSED") == null){
                    response.put("PROPOSED", "");
                }else{
                    response.put("PROPOSED", result.get("PROPOSED"));
                }

                if(month.length()==1){
                    month="0"+month;
                }
                if(day.length()==1){
                    day="0"+day;
                }
                if(hour24.length()==1){
                    hour24="0"+hour24;
                }
                if(minute.length()==1){
                    minute="0"+minute;
                }
                response.put("DOCTORNAME", doctorname);
                response.put("YEAR", year);
                response.put("MONTH", month);
                response.put("DAY", day);
                response.put("HOUR", hour24);
                response.put("MIN", minute);

            } catch (PersistentDataOperationException e) {
                e.printStackTrace();
            }
        }
    }


    public double parseDouble(Object o) {
        if (o == null) {
            return new Double(0);
        }
        return Double.parseDouble(o + "");
    }

    public long parseLong(Object o) {
        if (o == null) {
            return new Long(0);
        }
        return Long.parseLong(o + "");
    }

    public int parseInteger(Object o) {
        if (o == null) {
            return new Integer(0);
        }
        return Integer.parseInt(o + "");
    }
}

