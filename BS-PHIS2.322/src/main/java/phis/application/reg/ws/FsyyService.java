package phis.application.reg.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.w3c.dom.Document;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import phis.source.BaseDAO;
import phis.source.ws.AbstractWsService;
//分时预约webservice接口
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class FsyyService extends AbstractWsService{
	public SessionFactory sessionFactory = null;
	@SuppressWarnings("unchecked")
	@Override
	@WebMethod
	public String execute(String request) {
		return "";
	}
	@WebMethod
	public String GetWorkInfoBy(String request) {
		Session session = null;
		String res="";
		try {
			sessionFactory=getSessionFactory();
			session = sessionFactory.openSession();
			Context ctx = ContextUtils.getContext();
			ctx.put(Context.DB_SESSION, session);
			FsyyModel fm=new FsyyModel();
			res=fm.GetWorkInfoBy(request,session);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public String GetDoctorInformation(String request){
		Session session = null;
		String res="";
		try {
			sessionFactory=getSessionFactory();
			session = sessionFactory.openSession();
			Context ctx = ContextUtils.getContext();
			ctx.put(Context.DB_SESSION, session);
			FsyyModel fm=new FsyyModel();
			res=fm.GetDoctorInformation(request,session);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	@WebMethod
	public String Order(String request){
		Session session = null;
		String res="";
		try {
			sessionFactory=getSessionFactory();
			session = sessionFactory.openSession();
			Context ctx = ContextUtils.getContext();
			ctx.put(Context.DB_SESSION, session);
			FsyyModel fm=new FsyyModel();
			res=fm.doSaveOrder(request,session);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	@WebMethod
	public String CancelOrder(String request){
		Session session = null;
		String res="";
		try {
			sessionFactory=getSessionFactory();
			session = sessionFactory.openSession();
			Context ctx = ContextUtils.getContext();
			ctx.put(Context.DB_SESSION, session);
			FsyyModel fm=new FsyyModel();
			res=fm.doSaveCancelOrder(request,session);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	@WebMethod
	public String ChangeRegStatus(String request){
		Session session = null;
		String res="";
		try {
			sessionFactory=getSessionFactory();
			session = sessionFactory.openSession();
			Context ctx = ContextUtils.getContext();
			ctx.put(Context.DB_SESSION, session);
			FsyyModel fm=new FsyyModel();
			res=fm.ChangeRegStatus(request,session);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
