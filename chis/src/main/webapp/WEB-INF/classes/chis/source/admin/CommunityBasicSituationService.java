package chis.source.admin;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.ws.Service;
import ctd.account.UserRoleToken;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/** 
 * 此类描述的是：社区基本信息卫统报表导入 
 * @author <a href="mailto:liyunt@bsoft.com.cn ">liyunt</a>
 * @version: 2012-9-26 下午04:41:33 
 */ 
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CommunityBasicSituationService extends AbstractActionService implements DAOSupportable {
	
	private static final Logger logger = LoggerFactory.getLogger(CommunityBasicSituationService.class);

	/**
	 * 此方法描述的是： 获取社区基本情况、人员、职称、学历等数量
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @throws Exception
	 */
	public void doGetRSituation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws Exception{
		Session session = null ; 
		try{
			session = (Session)ctx.get(Context.DB_SESSION);
			HashMap<String, Object> cnd = (HashMap<String, Object>) req
			.get("body");
			String ryGroup = (String) cnd.get("ryGroup");//人员
			String zcGroup =  (String) cnd.get("zcGroup");//职称
			String xlGroup =  (String) cnd.get("xlGroup");//学历
			String crbGroup =  (String) cnd.get("crbGroup");//传染病数
			ArrayList<Map> ry = new ArrayList();
			ArrayList<Map> zc = new ArrayList();
			ArrayList<Map> xl = new ArrayList();
			ArrayList<Map> crb = new ArrayList();
			Schema sc = SchemaController.instance().get(BSCHISEntryNames.EHR_CommunityBasicSituation);
			if (sc == null) {
				logger.error("schema not found");
				return;
			}
			List<SchemaItem> items = sc.getItems();
			StringBuffer cols = new StringBuffer("");
			//根据schema对属性count="true"进行统计数量,并分组
			for (SchemaItem si : items) {
				String id = si.getId();
				String type = si.getProperty("count").toString();
				if("true".equals(type)){
					cols.append(",");
					cols.append("sum(nvl(");
					cols.append(id);
					cols.append(",0)) as ");
					cols.append(id);
					if(ryGroup.indexOf(","+id+",")!=-1){//人员
						Map map = new HashMap();
						map.put("id", id);
						map.put("alias", si.getProperty("alias").toString());
						ry.add(map);
					}else if(zcGroup.indexOf(","+id+",")!=-1){//职称
						Map map = new HashMap();
						map.put("id", id);
						map.put("alias", si.getProperty("alias").toString());
						zc.add(map);
					}else if(xlGroup.indexOf(","+id+",")!=-1){//学历
						Map map = new HashMap();
						map.put("id", id);
						map.put("alias", si.getProperty("alias").toString());
						xl.add(map);
					}else if(crbGroup.indexOf(","+id+",")!=-1){//传染病数
						Map map = new HashMap();
						map.put("id", id);
						map.put("alias", si.getProperty("alias").toString());
						crb.add(map);
					}
				}
			}
			//执行统计数据 SQL
			String sql = "select manaunitCode "+cols+"  from "+sc.getId()+" where manaunitCode like:manaUnitId group by manaunitCode";
			Query query = session.createSQLQuery(sql).setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP);
			query.setString("manaUnitId",cnd.get("id")+"%");
			List list = query.list();
			//根据统计数据对分组赋值
			Map<String,Object> json = new HashMap<String, Object>();
			if(list.size()>0){
				Map smap = (Map)list.get(0);
				Map tmap = null;
				StringBuffer rySwfXml = new StringBuffer();
				StringBuffer zcSwfXml = new StringBuffer();
				StringBuffer xlSwfXml = new StringBuffer();
				StringBuffer crbSwfXml = new StringBuffer();
				for(int i=0;i<ry.size();i++){
					tmap = ry.get(i);
					rySwfXml.append("<set label='"); 
					rySwfXml.append(tmap.get("alias"));
					rySwfXml.append("' showDivLinues='0' value='");
					rySwfXml.append(smap.get(tmap.get("id").toString().toUpperCase()));
					rySwfXml.append("' />");
				}
				if(rySwfXml.length()>0){
					rySwfXml.insert(0, "<chart anchorradius='4' canvasBorderThickness='2' canvasBorderAlpha='60' showBorder='0' useRoundEdges='0' numVDivLines='10'  use3DLighting='1' numberScaleValue='10000,10000' baseFontSize='20'  numberScaleUnit='万,亿' labelDisplay='none'  yAxisName='' showValues='1' legendPosition ='RIGHT' >");
					rySwfXml.append("</chart>");
					json.put("rySwfXml", rySwfXml);
				}
					
				for(int i=0;i<zc.size();i++){
					tmap = zc.get(i);
					zcSwfXml.append("<set label='"); 
					zcSwfXml.append(tmap.get("alias"));
					zcSwfXml.append("' showDivLinues='0'  value='");
					zcSwfXml.append(smap.get(tmap.get("id").toString().toUpperCase()));
					zcSwfXml.append("' />");
				}
				if(zcSwfXml.length()>0){
					zcSwfXml.insert(0, "<chart anchorradius='4' canvasBorderThickness='2' canvasBorderAlpha='60' showBorder='0' useRoundEdges='0' numVDivLines='10' use3DLighting='1' numberScaleValue='10000,10000' baseFontSize='20' numberScaleUnit='万,亿' labelDisplay='none'  yAxisName='' showValues='1' legendPosition ='RIGHT'>");
					zcSwfXml.append("</chart>");
					json.put("zcSwfXml", zcSwfXml);
				}
				
				for(int i=0;i<xl.size();i++){
					tmap = xl.get(i);
					xlSwfXml.append("<set label='"); 
					xlSwfXml.append(tmap.get("alias"));
					xlSwfXml.append("' showDivLinues='0' canvasBgColor ='FFFFFF' value='");
					xlSwfXml.append(smap.get(tmap.get("id").toString().toUpperCase()));
					xlSwfXml.append("' />");
				}
				if(xlSwfXml.length()>0){
					xlSwfXml.insert(0, "<chart anchorradius='4' canvasBorderThickness='2' canvasBorderAlpha='60' showBorder='0' useRoundEdges='0' numVDivLines='10' use3DLighting='1' numberScaleValue='10000,10000' baseFontSize='20' numberScaleUnit='万,亿' labelDisplay='none'  yAxisName='' showValues='1' legendPosition ='RIGHT'>");
					xlSwfXml.append("</chart>");
					json.put("xlSwfXml", xlSwfXml);
				}
				
				for(int i=0;i<crb.size();i++){
					tmap = crb.get(i);
					crbSwfXml.append("<set label='"); 
					crbSwfXml.append(tmap.get("alias"));
					crbSwfXml.append("' showDivLinues='0' canvasBgColor ='FFFFFF' value='");
					crbSwfXml.append(smap.get(tmap.get("id").toString().toUpperCase()));
					crbSwfXml.append("' />");
				}
				if(crbSwfXml.length()>0){
					crbSwfXml.insert(0, "<chart anchorradius='4' canvasBorderThickness='2' canvasBorderAlpha='60' showBorder='0' useRoundEdges='0' numVDivLines='10' use3DLighting='1' numberScaleValue='10000,10000' baseFontSize='20' numberScaleUnit='万,亿' labelDisplay='none'  yAxisName='' showValues='1' legendPosition ='RIGHT'>");
					crbSwfXml.append("</chart>");
					json.put("crbSwfXml", crbSwfXml);
				}
			}
			res.put("body", json);
			logger.info(list.toString());
		}catch(HibernateException he){
			res.put("code", "411");
			res.put("msg", "获取基本情况>>操作数据出错！");
		}catch(Exception ex){
			res.put("code", "410");
			res.put("msg", "获取基本情况出错！");
			res.clear() ; 
		}finally{
			session.clear() ; 
			session.close();
		}
	}
	
	/**
	 * 此方法描述的是： 获取人员基本情况的人口数、（年龄段、性别）、传染病、出生死亡等数量
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @throws Exception
	 */
	public void doGetPSituation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws Exception{
		HashMap<String, Object> cnd = (HashMap<String, Object>) req.get("body");
		Session session = null;
		try{
			session =(Session)ctx.get(Context.DB_SESSION);
			String rsGroup = (String) cnd.get("rsGroup");//人口数
			String csGroup = (String)  cnd.get("csGroup");//出生/死亡数
			String male = (String)  cnd.get("male");//年龄段、性别男 
			String female =  (String) cnd.get("female"); //年龄段、性别女
			String mfAlias = (String)  cnd.get("mfAlias"); //年龄段、性别男、女别名
			ArrayList<Map> rs = new ArrayList();
			ArrayList<Map> cs = new ArrayList();
			Schema sc = SchemaController.instance().get(BSCHISEntryNames.EHR_CommunityPepBasicSituation);
			if (sc == null) {
				logger.error("schema not found");
				return;
			}
			List<SchemaItem> items = sc.getItems();
			StringBuffer cols = new StringBuffer("");
			//根据schema对属性count="true"进行统计数量,并分组
			for (SchemaItem si : items) {
				String id = si.getId();
				String type = si.getProperty("count").toString();
				if("true".equals(type)){
					cols.append(",");
					cols.append("sum(nvl(");
					cols.append(id);
					cols.append(",0)) as ");
					cols.append(id);
					if(rsGroup.indexOf(","+id+",")!=-1){//人口数
						Map map = new HashMap();
						map.put("id", id);
						map.put("alias", si.getProperty("alias").toString());
						rs.add(map);
					}else if(csGroup.indexOf(","+id+",")!=-1){//出生/死亡数
						Map map = new HashMap();
						map.put("id", id);
						map.put("alias", si.getProperty("alias").toString());
						cs.add(map);
					}
				}
			}
			//执行统计数据 SQL
			String sql = "select createUnit "+cols+"  from "+sc.getId()+" where createUnit like:manaUnitId group by createUnit";
			Query query = session.createSQLQuery(sql).setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP);
			query.setString("manaUnitId",cnd.get("id")+"%");
			List list = query.list();
			//根据统计数据对分组赋值
			Map<String,Object> json = new HashMap<String, Object>();
			if(list.size()>0){
				Map smap = (Map)list.get(0);
				Map tmap = null;
				StringBuffer rsSwfXml = new StringBuffer();
				StringBuffer csSwfXml = new StringBuffer();
				StringBuffer mFXml = new StringBuffer();
				for(int i=0;i<rs.size();i++){
					tmap = rs.get(i);
					rsSwfXml.append("<set label='"); 
					rsSwfXml.append(tmap.get("alias"));
					rsSwfXml.append("' showDivLinues='0' value='");
					rsSwfXml.append(smap.get(tmap.get("id").toString().toUpperCase()));
					rsSwfXml.append("' />");
				}
				if(rsSwfXml.length()>0){
					rsSwfXml.insert(0, "<chart anchorradius='4' canvasBorderThickness='2' canvasBorderAlpha='60' showBorder='0' useRoundEdges='0' numVDivLines='10'  use3DLighting='1' numberScaleValue='10000,10000' baseFontSize='20'  numberScaleUnit='万,亿' labelDisplay='none'  yAxisName='' showValues='1' legendPosition ='RIGHT' >");
					rsSwfXml.append("</chart>");
					json.put("rsSwfXml", rsSwfXml);
				}
					
				for(int i=0;i<cs.size();i++){
					tmap = cs.get(i);
					csSwfXml.append("<set label='"); 
					csSwfXml.append(tmap.get("alias"));
					csSwfXml.append("' showDivLinues='0'  value='");
					csSwfXml.append(smap.get(tmap.get("id").toString().toUpperCase()));
					csSwfXml.append("' />");
				}
				if(csSwfXml.length()>0){
					csSwfXml.insert(0, "<chart anchorradius='4' canvasBorderThickness='2' canvasBorderAlpha='60' showBorder='0' useRoundEdges='0' numVDivLines='10' use3DLighting='1' numberScaleValue='10000,10000' baseFontSize='20' numberScaleUnit='万,亿' labelDisplay='none'  yAxisName='' showValues='1' legendPosition ='RIGHT'>");
					csSwfXml.append("</chart>");
					json.put("csSwfXml", csSwfXml);
				}
				
				if(!"".equals(male) && !"".equals(female) && !"".equals(mfAlias)){
					String[] maleArr = male.split(",");
					String[] femaleArr = female.split(",");
					String[] mfAliasArr = mfAlias.split(",");
					mFXml.append("<DataSet>");
					for(int i=0;i<maleArr.length;i++){
						logger.info(femaleArr[i].toUpperCase()+"=female--00000--male="+maleArr[i].toUpperCase());
						mFXml.append("<Data>");
						mFXml.append("<Item Name=\"ID\" Value=\""+(i<=9?'0'+(i+1):i)+"\">"+(i<=9?'0'+(i+1):i)+"</Item>");
						mFXml.append("<Item Name=\"RowName\" Value=\""+mfAliasArr[i]+"\">"+mfAliasArr[i]+"</Item>");
						mFXml.append("<Item Name=\"Male\" Value=\""+smap.get(maleArr[i].toUpperCase())+"\">"+smap.get(maleArr[i].toUpperCase())+"</Item>");
						mFXml.append("<Item Name=\"Female\" Value=\""+smap.get(femaleArr[i].toUpperCase())+"\">"+smap.get(femaleArr[i].toUpperCase())+"</Item>");
						mFXml.append("</Data>");
					}
					mFXml.append("</DataSet>");
					json.put("mFXml", mFXml);
				}
				
			}
			res.put("body", json);
			logger.info(list.toString());
		}catch(HibernateException he){
			req.put("code", "411");
			req.put("msg", "获取基本情况>>操作数据出错！");
		}catch(Exception ex){
			req.put("code", "410");
			req.put("msg", "获取基本情况出错！");
			
		}finally{
			session.clear() ; 
			session.close();
		}
	}
	
	/**
	 * 此方法描述的是： 导出Schema成Excel
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @throws Exception
	 */
	public void doReportExcel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws Exception{
		HttpServletResponse response = (HttpServletResponse)ctx.get("response");
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("社区基本情况卫统Execl导入模板");
		Schema sc = SchemaController.instance().get(BSCHISEntryNames.EHR_CommunityBasicSituation);
		List<SchemaItem> li = sc.getItems();
		HSSFRow row = sheet.createRow(0);
		int i = 0;
		for (int j = 0; j < li.size(); j++) {
			String name = li.get(j).getId();
			SchemaItem si =  sc.getItem(name);
			boolean bl =si.getProperty("report")==null;
			if(bl){
				HSSFCell  scell= row.createCell(i);
				scell.setCellValue(new HSSFRichTextString(si.getAlias()));
				sheet.setColumnWidth(i, ((si.getAlias().length()+1)*512));
				i += 1;
			}
		}
		URL url = this.getClass().getClassLoader().getResource("");
		String path = URLDecoder.decode(url.getPath(), "UTF-8")
				+ "../../resources/temp/";
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
//		String absPath = f.getAbsolutePath() + File.separator
//				+ BSCHISUtil.toString(new Date(), "yyyyMMddHHmmss") + ".xls";
//		File file = new File(absPath);
//		FileOutputStream fos = new FileOutputStream(file);
		response.setContentType("application/msexcel");//设置导出文件格式
		response.setHeader("Content-disposition","attachment;filename=" + BSCHISUtil.toString(new Date(), "yyyyMMddHHmmss")+".xls"); 
		wb.write(response.getOutputStream());
	}
    /** 
     * 此方法描述的是： 导入卫统数据
     * @author: liyunt@bsoft.com.cn 
     * @version: 2012-9-27 上午11:35:42 
     */  		
	public void doUploadCommunityBasFile(java.util.Map<String,Object> req, java.util.Map<String,Object>res,BaseDAO dao,Context ctx)throws Exception{
		int errorRow =  0 ,update = 0 ,insert  = 0 , errorCell = 0; 
		Session session = null;
		Map<String,String> colMap = new HashMap<String,String>();
		Schema schema =SchemaController.instance().get(BSCHISEntryNames.EHR_CommunityBasicSituation);
		try {
//			WebApplicationContext wac = (WebApplicationContext) ctx.get(Context.APP_CONTEXT);
			SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
			session = sf.openSession();
			for (SchemaItem item : schema.getItems()) {
				colMap.put(item.getAlias(),item.getId());
			}
			File file =new File(req.get("path").toString());
			List<String> colsName = new ArrayList<String>();
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet ws = wb.getSheetAt(0);
			for(Iterator<Cell> hrs = ws.getRow(0).cellIterator(); hrs.hasNext();){
				HSSFCell hc = (HSSFCell) hrs.next();
				colsName.add(hc.getRichStringCellValue().toString().replaceAll("\\d-", ""));
			}
			outer:for (int j = 1; j <= ws.getLastRowNum(); j++) {
				errorRow = j ; 
				Map<String,Object> map = new HashMap<String,Object>();
				HSSFRow row = ws.getRow(j);
				if(row==null){
					continue;
				}else if(row.getCell(0).toString().trim().equals("")
						||row.getCell(0).toString().trim().equals("0.0")){
					continue outer;
				} 
				for(int h = 0 ;h<colsName.size();h++){
					HSSFCell hc = row.getCell(h);
					if(hc==null){
						continue;
					}
					String fiedId = colMap.get(colsName.get(h)); 
					String value = hc.toString() ; 
					map.put(fiedId, !value.trim().equals("")?new Double(value).intValue()+"":"");
					try{
						if(map.get(fiedId)!=null&&!map.get(fiedId).toString().trim().equals(""))
						{
							new Double(map.get(fiedId).toString()).intValue();
						}
					}catch(NumberFormatException ex){
						errorCell = h;
						throw ex;
					}
				}
				Query query =session.createQuery("from "+EHR_CommunityBasicSituation +" where manaunitCode=:manaId");
				query.setParameter("manaId", map.get("manaunitCode"));
				Map obj  = (Map) query.uniqueResult();
				UserRoleToken ur = UserRoleToken.getCurrent();
				if(obj == null){
					map.put("RecordID", System.currentTimeMillis()+""+j);
					map.put("createUnit", ur.getManageUnitId());
					map.put("createUser", ur.getUserId());
					map.put("createDate", new Date());
					res.put("count",++insert);
					session.save(EHR_CommunityBasicSituation,map);
				}else{
					map.put("lastModifyUnit", ur.getManageUnitId());
					map.put("lastModifyUser", ur.getUserId());
					map.put("lastModifyDate", new Date());
					map.put("RecordID", obj.get("RecordID"));
					map.put("createUnit", obj.get("createUnit"));
					map.put("createUser", obj.get("createUser"));
					map.put("createDate", obj.get("createDate"));
					obj.clear();
					obj.putAll(map);
					session.saveOrUpdate(EHR_CommunityBasicSituation, obj);
					res.put("update", ++update);
				}
			}
			res.put(Service.RES_CODE, 200);
			res.put(Service.RES_MESSAGE, "上传成功");
			session.flush();
		}catch(IndexOutOfBoundsException ex){
			res.put("exceptionCode", "406");
			res.put("errorRow", errorRow+1);
		}catch(NumberFormatException ex){
			res.put("exceptionCode", "407");
			res.put("errorCell", errorCell+1);
			res.put("errorRow", errorRow+1);
		}catch(RuntimeException ex){
			res.put("exceptionCode", "409");
			res.put("errorCell", errorCell+1);
			res.put("errorRow", errorRow+1);
		}catch(Exception ex){
			res.put("exceptionCode", "408");
		}finally{
			session.clear();
			session.close();
		}
		logger.debug(res.toString());
		}
	}
	
 



