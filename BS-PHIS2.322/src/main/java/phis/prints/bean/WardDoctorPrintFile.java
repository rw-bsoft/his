package phis.prints.bean;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class WardDoctorPrintFile  implements IHandler{
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-15
	 * @description 获取list数据
	 * @updateInfo
	 * @param request
	 * @param records
	 * @param ctx
	 * @throws PrintException
	 */
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfo=new SimpleDateFormat("yy-MM-dd");
		SimpleDateFormat sdft=new SimpleDateFormat("HH:mm");
		String dylx=request.get("DYLX")+"";//打印类型,即哪个按钮打印
		int yzlx = MedicineUtils.parseInt(request.get("YZLX"));//医嘱期效,1是长期医嘱2是临时医嘱
		StringBuffer hql=new StringBuffer();
		Map<String,Object> map_par=new HashMap<String,Object>();
		List<Long> list_tzdyxhs=new ArrayList<Long>();//储存那些已经打印过的停嘱医嘱的打印表主键
		int sftd=MedicineUtils.parseInt(ParameterUtil.getParameter(UserRoleToken.getCurrent().getManageUnitId(), "YZBDYSFTD", ctx));
		List<Long> list_yzxhs=new ArrayList<Long>();//继续打印的医嘱集合
		if("jxdy".equals(dylx)){//继续打印
			String zyxhs=request.get("dyyzs")+"";
			if("".equals(zyxhs)){
				return;
			}
			String[] dyyzs=zyxhs.split(",");
			for(String dyyz:dyyzs){
				list_yzxhs.add(MedicineUtils.parseLong(dyyz));
			}
			String tzyzs ="";
			if(request.containsKey("tzyzs")){
				 tzyzs = request.get("tzyzs") + "";
			}
			if(!"".equals(tzyzs)){
				String[] tzyz=tzyzs.split(",");
				for(String tz:tzyz){
					list_tzdyxhs.add(MedicineUtils.parseLong(tz));
				}
			}
			hql.append("select b.DYXH as DYXH, b.YZBXH as YZBXH, a.YZZH as YZZH," +
					" to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as KSSJ,b.DYNR as YZMC,a.YSGH as YSGH," +
					" a.FHGH as FHGH,to_char(a.TZSJ,'yyyy-mm-dd hh24:mi:ss') as TZSJ ,a.TZYS as TZYS," +
					" a.TZFHR as TZFHR,a.YPXH as YPXH,b.DYYM as DYYM,b.DYHH as DYHH,to_char(a.FHSJ,'yyyy-mm-dd hh24:mi:ss') as ZXSJ" +
					" from EMR_YZDY b left outer join ZY_BQYZ a on a.JLXH=b.YZBXH  where  b.DYXH in (:jlxhs) " +
					" and b.ZYH=:zyh and b.YZBXH >0 and a.YSBZ='1' and a.YSTJ = '1' ");
//			hql.append(" order by b.DYYM,b.DYHH");
			hql.append(" order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
			map_par.put("jlxhs", list_yzxhs);
			map_par.put("zyh", MedicineUtils.parseLong(request.get("ZYH")));//住院号
		}else if("aydy".equals(dylx)){//按页打印
			int pageFrom = MedicineUtils.parseInt(request.get("pageFrom"));//打印起始页
			int pageTo = MedicineUtils.parseInt(request.get("pageTo"));//打印终止页
			hql.append("select b.YZBXH as YZBXH,a.YZZH as YZZH, to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as KSSJ," +
					" b.DYNR as YZMC,a.YSGH as YSGH,a.FHGH as FHGH,to_char(a.TZSJ,'yyyy-mm-dd hh24:mi:ss') as TZSJ," +
					" a.TZYS as TZYS,a.YPXH as YPXH,a.TZFHR as TZFHR,b.DYYM as DYYM,b.DYHH as DYHH," +
					" to_char(a.FHSJ,'yyyy-mm-dd hh24:mi:ss') as ZXSJ " +
					" from EMR_YZDY b left outer join ZY_BQYZ a on a.JLXH=b.YZBXH " +
					" where  b.ZYH=:zyh and b.YZQX=:yzqx and b.YZBXH >0  and a.YSBZ='1' and a.YSTJ = '1'");
//			hql.append(" order by b.DYYM,b.DYHH");
			hql.append(" order by a.YZZH asc,a.YJZX desc,a.JLXH asc");
			map_par.put("zyh", MedicineUtils.parseLong(request.get("ZYH")));//住院号
			map_par.put("yzqx", MedicineUtils.parseInt(request.get("YZLX")));//医嘱期效,1是长期医嘱2是临时医嘱
		}else if("zdhdy".equals(dylx)){
			hql.append("select b.YZBXH as YZBXH, a.YZZH as YZZH,to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as KSSJ," +
					" b.DYNR as YZMC,a.YSGH as YSGH,a.FHGH as FHGH,to_char(a.TZSJ,'yyyy-mm-dd hh24:mi:ss') as TZSJ," +
					" a.TZYS as TZYS,a.YPXH as YPXH,a.TZFHR as TZFHR,b.DYYM as DYYM,b.DYHH as DYHH," +
					" to_char(a.FHSJ,'yyyy-mm-dd hh24:mi:ss') as ZXSJ " +
					" from EMR_YZDY b left outer join ZY_BQYZ a on a.JLXH=b.YZBXH " +
					" where  b.ZYH=:zyh and b.YZQX=:yzqx and (b.DYYM>:dyym or (b.DYYM=:dyym and b.DYHH>=:dyhh)) " +
					" and b.YZBXH >0  and a.YSBZ='1' and a.YSTJ = '1' order by b.DYYM,b.DYHH");
			map_par.put("zyh", MedicineUtils.parseLong(request.get("ZYH")));//住院号
			map_par.put("yzqx", MedicineUtils.parseInt(request.get("YZLX")));//医嘱期效,1是长期医嘱2是临时医嘱
			map_par.put("dyym", MedicineUtils.parseInt(request.get("DYYM")));//页码
			map_par.put("dyhh", MedicineUtils.parseInt(request.get("DYHH")));//行号
		}else if("czdy".equals(dylx)){
			hql.append("select b.YZBXH as YZBXH,a.YZZH as YZZH,to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as KSSJ," +
					"b.DYNR as YZMC,a.YSGH as YSGH,a.FHGH as FHGH,a.TZYS as TZYS," +
					"to_char(a.TZSJ,'yyyy-mm-dd hh24:mi:ss') as TZSJ,a.YPXH as YPXH,a.TZFHR as TZFHR,b.DYYM as DYYM,b.DYHH as DYHH," +
					"to_char(a.FHSJ,'yyyy-mm-dd hh24:mi:ss') as ZXSJ " +
					"from EMR_YZDY b left outer join ZY_BQYZ a on a.JLXH=b.YZBXH where  b.ZYH=:zyh " +
					"and b.YZQX=:yzqx and (b.CZBZ=0 or b.CZBZ=2) and b.YZBXH >0  and a.YSBZ='1' and a.YSTJ = '1' order by b.DYYM,b.DYHH");
			map_par.put("zyh", MedicineUtils.parseLong(request.get("ZYH")));//住院号
			map_par.put("yzqx", MedicineUtils.parseInt(request.get("YZLX")));//医嘱期效,1是长期医嘱2是临时医嘱
		}
		try {
			List<Map<String,Object>> list_jls=dao.doSqlQuery(hql.toString(), map_par);
			if(list_jls==null||list_jls.size()==0){
				return;
			}
			/**
			 * add by wangyd --2019.6.28
			 * 1如果停嘱复核是1 拿到ypxh和kssj
			 * 2使用ypxh和kssj 查询提交明细  拿到TJXH
			 * 3使用提交序号 查询提交记录 获取TJGH
			 * 4将TJGH翻译 设置到TZFHR
			 * */
			for(int i=0;i<list_jls.size();i++){
				//提交序号参数集合
				Map<String,Object> tjxh_par=new HashMap<String,Object>();
				if(list_jls.get(i)!=null&&list_jls.get(i).get("TZSJ")!=null){
					//获取查询所需参数
					tjxh_par.put("YPXH",list_jls.get(i).get("YPXH"));
					//SimpleDateFormat kssjdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String dstr=(String) list_jls.get(i).get("KSSJ");
					tjxh_par.put("KSSJ",dstr);
					String sql ="select f.TJGH as TJGH, t.TJXH as TJXH, t.YPXH as YPXH,t.KSSJ as KSSJ from BQ_TJ02 t left outer join BQ_TJ01 f on t.TJXH = f.TJXH where t.YPXH=:YPXH  and t.KSSJ =to_date(:KSSJ,'yyyy-mm-dd hh24:mi:ss')";
					List<Map<String,Object>> tjgh_jls=dao.doSqlQuery(sql, tjxh_par);
					if(tjgh_jls!=null&&tjgh_jls.size()!=0){
						list_jls.get(i).put("TZFHR",tjgh_jls.get(0).get("TJGH"));
					}
				}
			}

			SchemaUtil.setDictionaryMassageForList(list_jls, "phis.application.war.schemas.YZDY_CQYZ");
			Map<String,Object> map_first=list_jls.get(0);
			int firsthh=MedicineUtils.parseInt(map_first.get("DYHH"));
			Map<String,Object> map_last=list_jls.get(list_jls.size()-1);
			int lasthh=MedicineUtils.parseInt(map_last.get("DYHH"));
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();// 用户的机构ID
			int YZTDMYTS =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "YZTDMYTS", ctx)) ;//每页打印行数
			String sfqm = ParameterUtil.getParameter(jgid, "YZYZQM", ctx);//是否打印签名
			int YZYZQM = 0;//默认打印签名
			if(sfqm!=null && !"".equals(sfqm) && !"null".equals(sfqm)){
				YZYZQM =MedicineUtils.parseInt(sfqm);
			}
			int printedRows = 0;//打印过的行数
			int lastPageHeadEmptyRows = 0;//继续打印首页头空行数
			if("jxdy".equals(dylx)){
				Map<String,Object> ordermap_par=new HashMap<String,Object>();
				StringBuffer orderhql=new StringBuffer();
				orderhql.append("select count(*) as YDYHS" +
						" from EMR_YZDY b left outer join ZY_BQYZ a on a.JLXH=b.YZBXH  where b.DYXH not in (:jlxhs) and b.YZQX=:yzqx" +
						" and b.ZYH=:zyh and b.YZBXH >0 and a.YSBZ='1' and a.YSTJ = '1' and a.DYZT <> '0' order by b.DYYM,b.DYHH ");
				ordermap_par.put("zyh", MedicineUtils.parseLong(request.get("ZYH")));//住院号
				ordermap_par.put("yzqx", yzlx);
				ordermap_par.put("jlxhs", list_yzxhs);
				List<Map<String,Object>> printedOrderlist=dao.doSqlQuery(orderhql.toString(), ordermap_par);
				if(printedOrderlist!=null && printedOrderlist.size()>0){
					printedRows = MedicineUtils.parseInt(printedOrderlist.get(0).get("YDYHS"));
					lastPageHeadEmptyRows = printedRows%YZTDMYTS;
				}
				if(printedRows%YZTDMYTS>0){//首页前面之前打印过
					for(int head = 0;head<lastPageHeadEmptyRows;head++){//表格头高160
						records.add(getEmptyRowWhithOutBorder());
					}
				}
			}else if("aydy".equals(dylx)){
				int pageFrom = MedicineUtils.parseInt(request.get("pageFrom"));//打印起始页
				int pageTo = MedicineUtils.parseInt(request.get("pageTo"));//打印终止页
				int allpages = list_jls.size()/YZTDMYTS+1;//所有记录页数
				List<Map<String,Object>> readyPrintList = new ArrayList<Map<String,Object>>();
				if(pageFrom<=allpages){
					if(pageTo!=0 && pageTo<allpages){
						for(int i=(pageFrom-1)*YZTDMYTS;i<pageTo*YZTDMYTS;i++){
							Map<String,Object> data = list_jls.get(i);
							readyPrintList.add(data);
						}
					}else{
						for(int i=(pageFrom-1)*YZTDMYTS;i<list_jls.size();i++){
							Map<String,Object> data = list_jls.get(i);
							readyPrintList.add(data);
						}
					}
					list_jls=readyPrintList;
				}else{
					return;
				}
			}else if("zdhdy".equals(dylx) && firsthh>1){
				for(int i=1;i<firsthh;i++){
					records.add(getEmptyRowWhithOutBorder());
				}
			}
			for(int i=0;i<list_jls.size();i++){
					Map<String,Object> map_jl=list_jls.get(i);
					
					Map<String,Object> map_dy=new HashMap<String,Object>();
					map_dy.put("ZXSJ", "　");
					map_dy.put("FHGH", "　");//add by lizhi 2017-11-06增加执行者签名
					if(map_jl.get("KSSJ")==null){
						map_dy.put("KZRQ", "　");
						map_dy.put("KZSJ", "　");
					}else{
						map_dy.put("KZRQ", sdfo.format(sdf.parse(map_jl.get("KSSJ")+"")));
						map_dy.put("KZSJ", sdft.format(sdf.parse(map_jl.get("KSSJ")+"")));
					}
					String yzmc=MedicineUtils.parseString(map_jl.get("YZMC"));
					map_dy.put("YZMC",yzmc);
					if(map_jl.get("TZSJ")==null){
						map_dy.put("TZRQ", "　");
						map_dy.put("TZSJ", "　");
					}else{
						map_dy.put("TZRQ", "".equals(sdfo.format(sdf.parse(map_jl.get("TZSJ")+"")))?"　":sdfo.format(sdf.parse(map_jl.get("TZSJ")+"")));
						map_dy.put("TZSJ", "".equals(sdft.format(sdf.parse(map_jl.get("TZSJ")+"")))?"　":sdft.format(sdf.parse(map_jl.get("TZSJ")+"")));
					}
					if(YZYZQM > 0){//add by lizhi 2017-11-30增加参数控制是否打印签名，默认0打印签名
						map_dy.put("YSQM", "　");
						map_dy.put("HSQM", "　");
						map_dy.put("TZYS", "　");
						map_dy.put("TZHS", "　");
						map_dy.put("FHGH", "　");
					}else{
						map_dy.put("YSQM", "".equals(MedicineUtils.parseString(map_jl.get("YSGH_text")))?"　":MedicineUtils.parseString(map_jl.get("YSGH_text")));
						map_dy.put("HSQM", "".equals(MedicineUtils.parseString(map_jl.get("FHGH_text")))?"　":MedicineUtils.parseString(map_jl.get("FHGH_text")));
						map_dy.put("TZYS", "".equals(MedicineUtils.parseString(map_jl.get("TZYS_text")))?"　":MedicineUtils.parseString(map_jl.get("TZYS_text")));
						map_dy.put("TZHS", "".equals(MedicineUtils.parseString(map_jl.get("TZFHR_text")))?"　":MedicineUtils.parseString(map_jl.get("TZFHR_text")));
						map_dy.put("FHGH", "".equals(MedicineUtils.parseString(map_jl.get("FHGH_text")))?"　":MedicineUtils.parseString(map_jl.get("FHGH_text")));//add by lizhi 2017-11-06增加执行者签名
					}
					map_dy.put("ZXSJ", "".equals(MedicineUtils.parseString(map_jl.get("ZXSJ")))?"　":MedicineUtils.parseString(map_jl.get("ZXSJ")));
					if("重整医嘱".equals(MedicineUtils.parseString(map_jl.get("YZMC")))||"转科后".equals(MedicineUtils.parseString(map_jl.get("YZMC")))){
						if(MedicineUtils.parseInt(map_jl.get("DYHH"))==1){
							map_dy.put("CZBZ1", "1");
						}else{
							map_dy.put("CZBZ", "1");
						}
						//增加这些代码 为了转科打印  转科记录不打印医生和护士
						map_dy.put("YSQM", "　");
						map_dy.put("HSQM", "　");
					}
					if(MedicineUtils.parseInt(map_jl.get("YZBXH"))==-1){//空行特殊处理
						map_dy.put("KZRQ", "- - - ");
						map_dy.put("KZSJ", "- - - ");
						map_dy.put("YZMC", "- - - - - - - - - - - - - - - - - - - - - - - - ");
						map_dy.put("YSQM", "- - - ");
						map_dy.put("HSQM", "- - - ");
						map_dy.put("TZRQ", "- - - ");
						map_dy.put("TZSJ", "- - - ");
						map_dy.put("TZYS", "- - - ");
						map_dy.put("TZHS", "- - - ");
						map_dy.put("ZXSJ", "- - - ");
					}
					map_dy.put("ZH", "　");
					if(i!=0){//实现相同日期只显示第一条和最后一条,同组加符号
						//相同日期只显示第一条和最后一条(每页首行除外)
						if(map_jl.get("KSSJ")!=null && (i)%YZTDMYTS!=0 && (i+1)%YZTDMYTS!=0){
							if(list_jls.get(i-1).get("KSSJ")!=null&&!"转科后".equals(MedicineUtils.parseString(map_jl.get("YZMC")))){
								if(sdfo.format(sdf.parse(map_jl.get("KSSJ")+"")).equals(sdfo.format(sdf.parse(list_jls.get(i-1).get("KSSJ")+"")))){//判断日期和上一条是否相同
									if(i<list_jls.size()-1){//判断是否还有下一条
										if(MedicineUtils.parseLong(map_jl.get("YZZH"))!=MedicineUtils.parseLong(list_jls.get(i-1).get("YZZH"))){//如果和上一条不是同组
											map_dy.put("KZRQ", "　");
										}
										else{
											if(list_jls.get(i+1).get("KSSJ")!=null&&!"转科后".equals(MedicineUtils.parseString(map_jl.get("YZMC")))){
												if(sdfo.format(sdf.parse(map_jl.get("KSSJ")+"")).equals(sdfo.format(sdf.parse(list_jls.get(i+1).get("KSSJ")+"")))){//判断是否和下一条日期相同
													if(MedicineUtils.parseLong(map_jl.get("YZZH"))==MedicineUtils.parseLong(list_jls.get(i+1).get("YZZH"))){//如果和下一条是同组
														map_dy.put("KZRQ", "　");
														map_dy.put("KZSJ", "　");
													}else{
														map_dy.put("KZRQ", "　");
													}
												}else{
													map_dy.put("KZRQ", "　");
												}
											}
										}
									}
								}
							}
						}
						if(MedicineUtils.parseLong(map_jl.get("YZZH"))==MedicineUtils.parseLong(list_jls.get(i-1).get("YZZH"))&&MedicineUtils.parseInt(map_jl.get("YZBXH"))!=-1&&MedicineUtils.parseInt(map_jl.get("YZBXH"))!=0){
							if((i+1)<list_jls.size()){
								if(MedicineUtils.parseLong(map_jl.get("YZZH"))==MedicineUtils.parseLong(list_jls.get(i+1).get("YZZH"))){
									map_dy.put("ZH", "┃");
								}else{
									map_dy.put("ZH", "┛");
								}
							}else{
								map_dy.put("ZH", "┛");
							}
						}else{
							if((i+1)<list_jls.size()){
								if(MedicineUtils.parseLong(map_jl.get("YZZH"))==MedicineUtils.parseLong(list_jls.get(i+1).get("YZZH"))&&MedicineUtils.parseInt(map_jl.get("YZBXH"))!=-1){
									map_dy.put("ZH", "┓");
								}
							}
						}
					}
					if(i==0){
						if((i+1)<list_jls.size()){
							if(MedicineUtils.parseLong(map_jl.get("YZZH"))==MedicineUtils.parseLong(list_jls.get(i+1).get("YZZH"))&&MedicineUtils.parseInt(map_jl.get("YZBXH"))!=-1&&MedicineUtils.parseInt(map_jl.get("YZBXH"))!=0){
								map_dy.put("ZH", "┓");
							}
						}
					}
					if("jxdy".equals(dylx)&&MedicineUtils.parseInt(request.get("yzzt"))==2){//停嘱的继续打印 判断以后是否打过 如果打过则只打印停嘱部分
						int sfdy=0;
						for(long tzdyxh:list_tzdyxhs){
							if(tzdyxh==MedicineUtils.parseLong(map_jl.get("DYXH"))){
								map_dy.put("KZRQ", "");
								map_dy.put("KZSJ", "");
								map_dy.put("YZMC", "");
								map_dy.put("YSQM", "");
								map_dy.put("HSQM", "");
								map_dy.put("ZH", "");
								if(sftd!=1){
									map_dy.remove("KZRQ");
									map_dy.remove("KZSJ");
									map_dy.remove("YZMC");
									map_dy.remove("YSQM");
									map_dy.remove("HSQM");
									map_dy.remove("ZH");
									map_dy.put("TZRQ1", map_dy.get("TZRQ"));
									map_dy.remove("TZRQ");
									map_dy.put("TZSJ1", map_dy.get("TZSJ"));
									map_dy.remove("TZSJ");
									map_dy.put("TZYS1", map_dy.get("TZYS"));
									map_dy.remove("TZYS");
									map_dy.put("TZHS1", map_dy.get("TZHS"));
									map_dy.remove("TZHS");
									map_dy.put("FHGH1", map_dy.get("FHGH"));//add by lizhi 2017-11-06增加执行者签名
									map_dy.remove("FHGH");
								}
								sfdy=1;
								break;
							}
						}
						if(sfdy==0){//如果没打印过,则整条数据套打
							if(MedicineUtils.parseInt(map_first.get("DYHH"))>1&&MedicineUtils.parseInt(map_first.get("DYYM"))==MedicineUtils.parseInt(map_jl.get("DYYM"))){//第一条记录不是第一行 当前页所有记录都套打
								if(sftd!=1){
									map_dy.put("KH", "　");
									map_dy.put("KZRQ1", map_dy.get("KZRQ"));
									map_dy.remove("KZRQ");
									map_dy.put("KZSJ1", map_dy.get("KZSJ"));
									map_dy.remove("KZSJ");
									map_dy.put("YZMC1", map_dy.get("YZMC"));
									map_dy.remove("YZMC");
									map_dy.put("YSQM1", map_dy.get("YSQM"));
									map_dy.remove("YSQM");
									map_dy.put("HSQM1", map_dy.get("HSQM"));
									map_dy.remove("HSQM");
									map_dy.put("ZH1", map_dy.get("ZH"));
									map_dy.remove("ZH");
									map_dy.put("TZRQ1", map_dy.get("TZRQ"));
									map_dy.remove("TZRQ");
									map_dy.put("TZSJ1", map_dy.get("TZSJ"));
									map_dy.remove("TZSJ");
									map_dy.put("TZYS1", map_dy.get("TZYS"));
									map_dy.remove("TZYS");
									map_dy.put("TZHS1", map_dy.get("TZHS"));
									map_dy.remove("TZHS");
									map_dy.put("ZXSJ1", map_dy.get("ZXSJ"));
									map_dy.remove("ZXSJ");
									map_dy.put("FHGH1", map_dy.get("FHGH"));//add by lizhi 2017-11-06增加执行者签名
									map_dy.remove("FHGH");
								}
							}
						}
					}else{
						if(("zdhdy".equals(dylx) && firsthh>1 && MedicineUtils.parseInt(map_first.get("DYYM"))==MedicineUtils.parseInt(map_jl.get("DYYM")))
								|| ("jxdy".equals(dylx) && printedRows%YZTDMYTS>0 && (printedRows%YZTDMYTS+i)/YZTDMYTS<1)){
							if(sftd!=1){
								map_dy.put("KH", "　");
								map_dy.put("KZRQ1", map_dy.get("KZRQ"));
								map_dy.remove("KZRQ");
								map_dy.put("KZSJ1", map_dy.get("KZSJ"));
								map_dy.remove("KZSJ");
								map_dy.put("YZMC1", map_dy.get("YZMC"));
								map_dy.remove("YZMC");
								map_dy.put("YSQM1", map_dy.get("YSQM"));
								map_dy.remove("YSQM");
								map_dy.put("HSQM1", map_dy.get("HSQM"));
								map_dy.remove("HSQM");
								map_dy.put("ZH1", map_dy.get("ZH"));
								map_dy.remove("ZH");
								map_dy.put("TZRQ1", map_dy.get("TZRQ"));
								map_dy.remove("TZRQ");
								map_dy.put("TZSJ1", map_dy.get("TZSJ"));
								map_dy.remove("TZSJ");
								map_dy.put("TZYS1", map_dy.get("TZYS"));
								map_dy.remove("TZYS");
								map_dy.put("TZHS1", map_dy.get("TZHS"));
								map_dy.remove("TZHS");
								map_dy.put("ZXSJ1", map_dy.get("ZXSJ"));
								map_dy.remove("ZXSJ");
								map_dy.put("FHGH1", map_dy.get("FHGH"));//add by lizhi 2017-11-06增加执行者签名
								map_dy.remove("FHGH");
							}
						}
					}
					records.add(map_dy);
				}
				//add by lizhi 2017-11-07  补全最后一页的格子
				if(records.get(records.size()-1).containsKey("YZMC")){//如果第一条记录是第一行或者有多页,那么补全最后一页的打印格子
					boolean isPrintBorder = false;
					if("jxdy".equals(dylx)){
						if((printedRows+list_jls.size())%YZTDMYTS>0){//换页
							isPrintBorder = true;
						}
						if(isPrintBorder){
							for(int i=(printedRows+list_jls.size())%YZTDMYTS;i<YZTDMYTS;i++){
								records.add(getEmptyRowWhithBorder());
							}
						}else{
							for(int i=(printedRows+list_jls.size())%YZTDMYTS;i<YZTDMYTS;i++){
								records.add(getEmptyRowWhithOutBorder());
							}
						}
					}
					if("aydy".equals(dylx) && list_jls.size()%YZTDMYTS!=0){
						for(int i=0;i<YZTDMYTS-list_jls.size()%YZTDMYTS;i++){
							records.add(getEmptyRowWhithBorder());
						}
					}
					if("zdhdy".equals(dylx) && (firsthh==1 || (list_jls.size()+firsthh-1)/YZTDMYTS>0)){
						for(int i=0;i<YZTDMYTS-lasthh;i++){
							records.add(getEmptyRowWhithBorder());
						}
					}
				}
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		} catch (ParseException e) {
			throw new PrintException(9000,e.getMessage());
		}
	}
	
	/**
	 * 获取带边框的表格行
	 * @return
	 */
	public Map<String,Object> getEmptyRowWhithBorder(){
		Map<String,Object> map_dy=new HashMap<String,Object>();
		map_dy.put("KZRQ", "　");
		map_dy.put("KZSJ", "　");
		map_dy.put("YZMC", "　");
		map_dy.put("YSQM", "　");
		map_dy.put("HSQM", "　");
		map_dy.put("ZH", "　");
		map_dy.put("TZRQ", "　");
		map_dy.put("TZSJ", "　");
		map_dy.put("TZYS", "　");
		map_dy.put("TZHS", "　");
		map_dy.put("ZXSJ", "　");
		map_dy.put("FHGH", "　");//add by lizhi 2017-11-06增加执行者签名
		return map_dy;
	}
	
	/**
	 * 获取不带边框的表格行
	 * @return
	 */
	public Map<String,Object> getEmptyRowWhithOutBorder(){
		Map<String,Object> map_dy=new HashMap<String,Object>();
		map_dy.put("KH", "　");
		map_dy.put("KZRQ1", "　");
		map_dy.put("KZSJ1", "　");
		map_dy.put("YZMC1", "　");
		map_dy.put("YSQM1", "　");
		map_dy.put("HSQM1", "　");
		map_dy.put("ZH1", "　");
		map_dy.put("TZRQ1", "　");
		map_dy.put("TZSJ1", "　");
		map_dy.put("TZYS1", "　");
		map_dy.put("TZHS1", "　");
		map_dy.put("ZXSJ1", "　");
		map_dy.put("FHGH1", "　");//add by lizhi 2017-11-06增加执行者签名
		return map_dy;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-15
	 * @description 获取form数据
	 * @updateInfo
	 * @param request
	 * @param response
	 * @param ctx
	 * @throws PrintException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		String jgid = user.getManageUnitId();// 用户的机构ID
		response.put("TITLE", jgname);
		long zyh=MedicineUtils.parseLong(request.get("ZYH"));
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		int YZTDMYTS =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "YZTDMYTS", ctx)) ;//每页打印行数
		try {
			Map<String,Object> zyxx=dao.doLoad("phis.application.hos.schemas.ZY_BRRY", zyh);
			SchemaUtil.setDictionaryMassageForForm(zyxx, "phis.application.hos.schemas.ZY_BRRY");
			String brxb="";
			if(zyxx.get("BRXB")!=null){
				brxb=((Map<String,Object>)zyxx.get("BRXB")).get("text")+"";
			}
			String kb="";
			if(zyxx.get("BRKS")!=null){
				kb=((Map<String,Object>)zyxx.get("BRKS")).get("text")+"";
			}
			String bq = "";
			Long bqh=0L;
			if(zyxx.get("BRBQ")!=null){
				bqh=(Long)zyxx.get("BRBQ");
			}
			if(bqh!=null && bqh > 0){//add by lizhi 2017-11-06 增加病区
				try {
					bq = DictionaryController.instance().get("phis.dictionary.department")
							.getText(bqh.toString());
				} catch (ControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			response.put("BRXM", MedicineUtils.parseString(zyxx.get("BRXM")));
			response.put("XB", 	 brxb);
			response.put("NL",   BSHISUtil.calculateAge(sdf.parse(zyxx.get("CSNY")+""),null));
			response.put("KB",   kb);
			response.put("BQ",   bq);
			response.put("CH",   MedicineUtils.parseString(zyxx.get("BRCH")));
			response.put("ZYHM", MedicineUtils.parseString(zyxx.get("ZYHM")));
			response.put("MYHS", YZTDMYTS);
			response.put("SFTD", getSFTD(request, ctx));
//			response.put("KH",	"　\n 　\n 　\n");
			/****************增加继续打印和按页打印页码显示*****************/
			int printedpages = 0;//打印过的页数
			int printedRows = 0;//打印过的行数
			String dylx=request.get("DYLX")+"";//打印类型,即哪个按钮打印
			int yzlx = MedicineUtils.parseInt(request.get("YZLX"));//医嘱期效,1是长期医嘱2是临时医嘱
			List<Long> list_yzxhs=new ArrayList<Long>();//继续打印的医嘱集合
			if("jxdy".equals(dylx)){//继续打印
				String zyxhs=request.get("dyyzs")+"";
				if("".equals(zyxhs)){
					return;
				}
				String[] dyyzs=zyxhs.split(",");
				for(String dyyz:dyyzs){
					list_yzxhs.add(MedicineUtils.parseLong(dyyz));
				}
				Map<String,Object> ordermap_par=new HashMap<String,Object>();
				StringBuffer orderhql=new StringBuffer();
				orderhql.append("select count(*) as YDYHS" +
						" from EMR_YZDY b left outer join ZY_BQYZ a on a.JLXH=b.YZBXH  where b.DYXH not in (:jlxhs) and b.YZQX=:yzqx" +
						" and b.ZYH=:zyh and b.YZBXH >0 and a.YSBZ='1' and a.YSTJ = '1' and a.DYZT <> '0' order by b.DYYM,b.DYHH ");
				ordermap_par.put("zyh", MedicineUtils.parseLong(request.get("ZYH")));//住院号
				ordermap_par.put("yzqx", yzlx);
				ordermap_par.put("jlxhs", list_yzxhs);
				List<Map<String,Object>> printedOrderlist=dao.doSqlQuery(orderhql.toString(), ordermap_par);
				if(printedOrderlist!=null && printedOrderlist.size()>0){
					printedRows = MedicineUtils.parseInt(printedOrderlist.get(0).get("YDYHS"));
					if(printedRows>0){//没打印过
						printedpages = printedRows/YZTDMYTS+1;
					}
				}
				if(printedpages>0){
					if(printedRows%YZTDMYTS>0){
						response.put("PAGE", printedpages-1);
					}else{
						response.put("PAGE", printedpages);
					}
				}else{
					response.put("PAGE", 0);
				}
			}else if("aydy".equals(dylx)){//按页打印
				int pageFrom = MedicineUtils.parseInt(request.get("pageFrom"));//打印起始页
				response.put("PAGE", pageFrom-1);
			}else{
				response.put("PAGE", 0);
			}
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		} catch (ParseException e) {
			throw new PrintException(9000,e.getMessage());
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-17
	 * @description 判断当前打印的第一页是否需要套打(不是从第一行开始打的 都是套打)
	 * @updateInfo
	 * @param request
	 * @param ctx
	 * @return 0是非套打.1是套打
	 */
	public int getSFTD(Map<String, Object> request, Context ctx) throws PrintException {
		String dylx=request.get("DYLX")+"";//打印类型,即哪个按钮打印
		BaseDAO dao = new BaseDAO(ctx);
		StringBuffer hql=new StringBuffer();
		Map<String,Object> map_par=new HashMap<String,Object>();
		try {
		if("jxdy".equals(dylx)){
			String zyxhs=request.get("dyyzs")+"";
			if("".equals(zyxhs)){
				return 0;
			}
			String[] dyyzs=zyxhs.split(",");
			List<Long> list_yzxhs=new ArrayList<Long>();//打印的医嘱集合
			for(String dyyz:dyyzs){
				list_yzxhs.add(MedicineUtils.parseLong(dyyz));
			}
			hql.append("select DYHH as DYHH,DYXH as DYXH,DYYM as DYYM from EMR_YZDY where DYXH in (:jlxhs) order by DYYM,DYHH");
			map_par.put("jlxhs", list_yzxhs);
			List<Map<String,Object>> list_dyjls=dao.doQuery(hql.toString(), map_par);
			if(list_dyjls==null||list_dyjls.size()==0){
				return 0;
			}
			Map<String,Object> map_dyjl=list_dyjls.get(0);
			String tzyzs ="";
			if(request.containsKey("tzyzs")){
				 tzyzs = request.get("tzyzs") + "";
			}
			if (!"".equals(tzyzs)) {//停嘱的,如果已经打印过的记录,哪怕第一行也要套打,如果非第一行并且已经打印过 则也套打
					String[] tzyz = tzyzs.split(",");
					int dyym=MedicineUtils.parseInt(map_dyjl.get("DYYM"));
					int k=0;
					for (String tz : tzyz) {
						if(MedicineUtils.parseLong(tz)==MedicineUtils.parseLong(map_dyjl.get("DYXH"))){
							k++;
						}
					}
					if(k==1){
						for(Map<String,Object> dyjl:list_dyjls){
							if(MedicineUtils.parseInt(dyjl.get("DYYM"))>dyym){
								for (String tz : tzyz) {
									if(MedicineUtils.parseLong(tz)==MedicineUtils.parseLong(dyjl.get("DYXH"))){
										k++;
										dyym=MedicineUtils.parseInt(dyjl.get("DYYM"));
									}
								}
							}
						}
					}
					return k;
			}else{
				if(MedicineUtils.parseInt(map_dyjl.get("DYHH"))!=1){
					return 1;
				}
			}
			return 0;
		}else if("aydy".equals(dylx)){
			return 0;
		}
		else if("zdhdy".equals(dylx)){
			if(MedicineUtils.parseInt(request.get("DYHH"))==1){
				return 0;
			}
			return 1;
		}
		else if("czdy".equals(dylx)){
			if(MedicineUtils.parseInt(request.get("DYHH"))==1){
				return 0;
			}
			return 1;
		}
		} catch (PersistentDataOperationException e) {
			throw new PrintException(9000,e.getMessage());
		}
		return 0;
	}
}
