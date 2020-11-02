package chis.source.dr;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.pub.PublicService;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.UserUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class DrApplyService extends AbstractActionService implements DAOSupportable{
	/**
	 * 保存转诊申请信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @param dao
	 * @throws ServiceException
	 */
	public void doSaveSendExchange(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		//传日志到大数据接口 （双向转诊）--wdl
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf1.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = PublicService.getIpByEthNum();	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"01\",\n"+
			"\"apiCode\":\"SXZZ\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.SXZZ\",\n"+
			"\"methodDesc\":\"void doSaveSendExchange()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
				System.out.println(json);
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		try {
			applyModule.doSaveSendExchange(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 更新转诊申请信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @param dao
	 * @throws ServiceException
	 */
	public void doUpdate(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		try {
			applyModule.doUpdate(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public void doLoadResourcesHtml(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		try {
			applyModule.doLoadResourcesHtml(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询病人基本信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @param dao
	 * @throws ServiceException
	 */
	public void doGetMPI(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		try {
			applyModule.doGetMPI(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询转诊信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @param dao
	 * @throws ServiceException
	 */
	public void doCndQuery(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		try {
			applyModule.doCndQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 保存转诊下转申请信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @param dao
	 * @throws ServiceException
	 */
	public void doSaveSendExchangeReport(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		//传日志到大数据接口 （双向转诊）--wdl
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf1.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = PublicService.getIpByEthNum();	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"01\",\n"+
			"\"apiCode\":\"SXZZ\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.SXZZ\",\n"+
			"\"methodDesc\":\"void doSaveSendExchangeReport()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
				System.out.println(json);
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		try {
			applyModule.doSaveSendExchangeReport(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 更新转诊下转申请信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @param dao
	 * @throws ServiceException
	 */
	public void doUpdateReport(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		try {
			applyModule.doUpdateReport(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 查询转诊下转信息
	 * @param req
	 * @param res
	 * @param ctx
	 * @param dao
	 * @throws ServiceException
	 */
	public void doCndQueryReport(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		try {
			applyModule.doCndQueryReport(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public static void main(String[] args) {
		ResultSet rs = null;
		Statement stmt = null;
		Connection conn = null;
		try {
			String idcard = "320111196207094826";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@32.33.1.77:1521:orcl", "phis77", "bsoft");
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("select empiid from mpi_card t where t.cardno = '"
							+ idcard + "'");
			while (rs.next()) {
				String mpiid = rs.getString("empiid");
				System.out.println(mpiid);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取双向转诊内嵌页面url
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public void doGetPageUrl_HTTPPOST(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		DrApplyModel applyModule = new DrApplyModel(dao);
		try {
			applyModule.doGetPageUrl_HTTPPOST(req, res, ctx);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}
