package chis.source.task;
//体检项目管理
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.AffirmType;
import chis.source.dic.ArchiveMoveStatus;
import chis.source.dic.BusinessType;
import chis.source.empi.EmpiUtil;
import chis.source.phr.BasicPersonalInformationModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.UserUtil;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

public class HssXmManage extends AbstractActionService implements
DAOSupportable {
	private static final Log logger = LogFactory.getLog(HssXmManage.class);
	//匹配项目
	protected void doMatchXm(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException, ModelDataOperationException {
		Map<String, Object> data=(Map<String, Object>)req.get("data");
		String EHRMATCH=data.get("EHRMATCH")==null?"":data.get("EHRMATCH")+"";
		String XMBH=data.get("XMBH")==null?"":data.get("XMBH")+"";
		String KSBM=data.get("KSBM")==null?"":data.get("KSBM")+"";
		String SYNCHRONIZE="";//同步到其他医院标记
		if(data.containsKey("SYNCHRONIZE")){
			SYNCHRONIZE=data.get("SYNCHRONIZE")+"";
		}
		Connection con=null;
		Statement st=null;
		try {
			con=getconnection("bstjxt");
			st=con.createStatement();
			String upsql="update TJ_XMDM set EHRMATCH='"+EHRMATCH+"' where XMBH='"+XMBH+"'";
			if("1".equals(SYNCHRONIZE)){
				upsql="update TJ_XMDM set EHRMATCH='"+EHRMATCH+"' where XMBH='"+XMBH+"' and EHRMATCH is null";
			}
			st.executeUpdate(upsql);
			st.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 public static Connection getconnection(String user){
	 	    Locale locale = Locale.getDefault();
		    ResourceBundle db = ResourceBundle.getBundle("chis\\spring\\db");
			try {
				Class.forName(db.getString("jdbcName"));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Connection con=null;
			try {
				con = DriverManager.getConnection(db.getString(user+"Url"),db.getString(user+"UserName"),db.getString(user+"Password"));
			} catch (SQLException e) {
				System.out.println("db.properties文件中数据库连接配置不正确！");
				e.printStackTrace();
			}
			System.out.println(db.getString(user+"Url")+"数据库连接成功");
			return con;
	}
	 //同步标准医院所有项目到本院
	protected void doSynchronizeAll(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		String jgid=req.get("jgid")+"";
		Locale locale = Locale.getDefault();
	    ResourceBundle db = ResourceBundle.getBundle("chis\\spring\\db");
	    String dzjg="";
	    try{
	    	dzjg=db.getString("dzjg");
	    }catch(Exception e) {
	    	e.printStackTrace();
	    	res.put("code",400);
	    	res.put("msg","请在db.properties文件中配置dzjg,dzjg(对照机构)为设置的对照模板机构");
	    	return;
		}
	    if(dzjg.length()!=9){
	    	res.put("code",400);
	    	res.put("msg","请在db.properties文件中配置dzjg,dzjg(对照机构)为设置的对照模板机构");
	    	return;
	    }
	    String sql="select b.xmbh as XMBH ,b.ehrmatch as EHRMATCH from tj_ksdm a ,tj_kskzxm b" +
	    		" where a.ksbm=b.ksbm and a.unitid='"+dzjg+"' and b.ehrmatch is not null";
	    Connection con=null;
		Statement st=null;
		try {
			con=getconnection("bstjxt");
			st=con.createStatement();
			ResultSet rs=st.executeQuery(sql);
			Map<String, Object> data=new HashMap<String, Object>();
			while(rs.next()){
				data.put(rs.getString("XMBH"),rs.getString("EHRMATCH"));
			}
			if(data!=null && data.size() >0){
				for (String XMBH : data.keySet()) {
					String EHRMATCH = data.get(XMBH)+"";
					String upsql="update tj_kskzxm a  set a.ehrmatch='"+EHRMATCH+"'" +
							" where a.ksbm in (select b.ksbm from tj_ksdm b where b.unitid='"+jgid+"')" +
							" and a.xmbh='"+XMBH+"' and a.ehrmatch is null";
					st.executeUpdate(upsql);
				}
			}else{
				res.put("code",400);
		    	res.put("msg","请检查db.properties文件中配置的dzjg,dzjg(对照机构)为设置的对照模板机构");
			}
			st.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}