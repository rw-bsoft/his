package phis.prints.bean;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * 会诊申请
 * 
 * @author Liws
 * 
 */
public class ConsultationManageList implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {

		try {
			String[] record = ((String) request.get("record")).split(",");

			String BRXM = new String(record[0].getBytes("iso8859_1"), "UTF-8");
			String BRXB = new String(record[1].getBytes("iso8859_1"), "UTF-8");
			String ZDMC = new String(record[6].getBytes("iso8859_1"), "UTF-8");
			String HZMD = new String(record[record.length-6].getBytes("iso8859_1"), "UTF-8");
			String BQZL = new String(record[record.length-5].getBytes("iso8859_1"), "UTF-8");
			String YQDX = new String(record[record.length-2].getBytes("iso8859_1"), "UTF-8");//会诊者姓名
			String[] HZSJ = record[2].split("-");// 会诊时间
			String[] SQSJ = record[record.length-3].split("-");// 申请时间
			
			String JJBZ = record[record.length-1];//紧急标识
			String JJBZ1 = "";
			String JJBZ2 = "";
			String JJBZ3 = "";
			if(JJBZ.equals("2")){
				JJBZ1 = "√";
			}else if(JJBZ.equals("1")){
				JJBZ2 = "√";
			}else if(JJBZ.equals("0")){
				JJBZ3 = "√";
			}

			response.put("BRXM", BRXM);
			response.put("BRXB", BRXB);
			response.put("SQDH", record[3]);// 申请单号
			response.put("BRCH", record[4]);// 病人床号
			response.put("ZYHM", record[5]);// 住院号码
			response.put("DQZD", ZDMC);// 当前会诊 zdmc
			response.put("SQKS", new String(record[record.length-8].getBytes("iso8859_1"),
					"UTF-8"));// 申请科室
			response.put("SQYS", new String(record[record.length-7].getBytes("iso8859_1"),
					"UTF-8"));// 申请医师
			response.put("HZMD", HZMD);// 会诊目的
			response.put("HZBQ", BQZL);// 患者病情及诊疗情况
			response.put("BRNL", record[record.length-4]);
			response.put("Year", SQSJ[0]);// 申请时间 年月日
			response.put("Month", SQSJ[1]);
			response.put("Day", SQSJ[2]);
			response.put("HZYear", HZSJ[0]);// 会诊年月日
			response.put("HZMonth", HZSJ[1]);
			response.put("HZDay", HZSJ[2]);
			response.put("YQDX", YQDX);
			response.put("JJBZ1", JJBZ1);
			response.put("JJBZ2", JJBZ2);
			response.put("JJBZ3", JJBZ3);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}
}
