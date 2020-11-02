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
        String year = "";
        String month = "";
        String day = "";
        if(id != null||!"null".equals(id)){
            //Map<String, Object> params = new HashMap<String, Object>();
            //params.put("id", id);
            //String sql = "select personName as PERSONNAME, sex as SEX, age as AGE, diagnosis as DIAGNOSIS,proposed as PROPOSED,cost as COST,to_char(operationdate,'YYYY-MM-DD') as OPERATIONDATE from DPC_ExtractionAgreement where id='"+id+"'";
            String sql = "select a.personname as PERSONNAME, a.sex as SEX, a.age as AGE, a.diagnosis as DIAGNOSIS,a.proposed as PROPOSED,a.cost as COST,to_char(a.operationdate,'YYYY-MM-DD') as OPERATIONDATE，b.organizname as JGMC from DPC_ExtractionAgreement a,sys_organization b where a.createunit=b.organizcode and id='"+id+"'";
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

                if(result.get("OPERATIONDATE") == null){
                    response.put("YEAR", "");
                    response.put("MONTH", "");
                    response.put("DAY", "");
                }else{
                    year=result.get("OPERATIONDATE").toString().substring(0,4);
                    month=result.get("OPERATIONDATE").toString().substring(5,7);
                    day=result.get("OPERATIONDATE").toString().substring(8,10);
                    if(month.length()==1){
                        month="0"+month;
                    }
                    if(day.length()==1){
                        day="0"+day;
                    }
                    response.put("YEAR", year);
                    response.put("MONTH", month);
                    response.put("DAY", day);
                }

                if(result.get("COST") == null){
                    response.put("COST", "");
                }else{
                    response.put("COST", result.get("COST"));
                }

                response.put("DOCTORNAME", doctorname);

                if(result.get("JGMC") == null){
                    response.put("JGMC", "");
                }else{
                    response.put("JGMC", result.get("JGMC"));
                }

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

