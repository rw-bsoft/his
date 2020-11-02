/**
 * @(#)WorkListService.java Created on 2010-6-13 下午04:30:03
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.task;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import chis.source.pub.PublicService;
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

/**
 * @description 通用的获取工作列表的服务，主要包含慢病，精神病 以及老年人随访。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class Managemdcfromhis extends AbstractActionService implements
DAOSupportable {

	private static final Log logger = LogFactory
			.getLog(Managemdcfromhis.class);

	protected void doSaveConfirmfromhis(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		//传日志到大数据接口 --wdl
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf1.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = (String) ctx.get(Context.CLIENT_IP_ADDRESS);	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"02\",\n"+
			"\"apiCode\":\"ZQXGWFWTX\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.ZQXGWFWTX\",\n"+
			"\"methodDesc\":\"void doSaveConfirmfromhis()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		if (data == null || data.size() < 1) {
			return;
		}
		String idCard = (String) data.get("sfzh");
		String jdlx = (String) data.get("jdlx");// 建档类型
		if (jdlx.length() > 0) {
			String empiId = "";
			String phrId = "";
			String empidatafind = "select empiId as empiId from MPI_DemographicInfo where idCard=:idCard";
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("idCard", idCard);
			try {
				List<Map<String, Object>> l = dao.doQuery(empidatafind, p);
				if (l != null && l.size() > 0) {
					empiId = (String) l.get(0).get("empiId");
					if (empiId != null && empiId.length() > 0) {
						String findphrid = "select phrId as phrId from EHR_HealthRecord where empiId=:empiId";
						Map<String, Object> m = new HashMap<String, Object>();
						m.put("empiId", empiId);
						List<Map<String, Object>> h = dao.doQuery(findphrid, m);
						if (h != null && h.size() > 0) {
							phrId = (String) h.get(0).get("phrId");
							if (phrId != null && phrId.length() > 0) {
								if (jdlx.indexOf("1") >= 0) {
									this.creategxy(data, phrId, empiId, dao);
								}
								if (jdlx.indexOf("2") >= 0) {
									this.createtnb(data, phrId, empiId, dao);
								}
							}else{
								try {
									throw new Exception("phrid获取出错");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {
							// 创建健康档案
							Map<String, Object> ehrdata = new HashMap<String, Object>();
							ehrdata = this.builddata(data, empiId);
							ehrdata.put("regionCode_text",
									data.get("areagrid_text"));
							phrId = this.createjkda(ehrdata, dao);
							if (phrId != null && phrId.length() > 0) {
								if (jdlx.indexOf("1") >= 0) {
									this.creategxy(data, phrId, empiId, dao);
								}
								if (jdlx.indexOf("2") >= 0) {
									this.createtnb(data, phrId, empiId, dao);
								}
							}else{
								try {
									throw new Exception("ehrid获取出错");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}else{
						try {
							throw new Exception("empiId获取出错");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					// 创建基本信息档案
					Map<String, Object> empidata = new HashMap<String, Object>();
					empidata.put("personName", (String) data.get("brxm"));
					empidata.put("idCard", idCard);
					empidata.put("sexCode", (String) data.get("brxb"));
					empidata.put("birthday", (String) data.get("csny"));
					empidata.put("mobileNumber", (String) data.get("lxdh"));
					empidata.put("status", "0");
					Map<String, Object> PIXData = EmpiUtil
							.changeToPIXFormat(empidata);
					empidata = EmpiUtil.submitPerson(dao, ctx, empidata,
							PIXData);
					empiId = (String) empidata.get("empiId");
					if (empiId != null && empiId.length() > 0) {
						// 创建健康档案
						Map<String, Object> ehrdata = new HashMap<String, Object>();
						ehrdata = this.builddata(data, empiId);
						ehrdata.put("regionCode_text",data.get("areagrid_text"));
						phrId =this.createjkda(ehrdata, dao);
						if (phrId != null && phrId.length() > 0) {
							if (jdlx.indexOf("1") >= 0) {
								this.creategxy(data, phrId, empiId, dao);
							}
							if (jdlx.indexOf("2") >= 0) {
								this.createtnb(data, phrId, empiId, dao);
							}
						}else{
							try {
								throw new Exception("phrid获取出错");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}else{
						try {
							throw new Exception("empiId获取出错");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				data.put("status", ArchiveMoveStatus.CONFIRM);
				data.put("affirmType", AffirmType.CONFIRM);
				data.put("affirmUser", UserUtil.get(UserUtil.USER_ID));
				data.put("affirmUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
				data.put("affirmDate", new Date());
				try {
					dao.doSave("update", MDC_FromHis, data, true);
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "执行确认his慢病出错！", e);
				}	
			} catch (Exception e) {
				dao.rollbackTransaction();
				e.printStackTrace();
			}
		}

		
	}
	protected void doSaveRejectfromhis(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		if (data == null || data.size() < 1) {
			return;
		}
		data.put("status", ArchiveMoveStatus.CANCEL);
		data.put("affirmType", AffirmType.CANCEL);
		data.put("affirmUser", UserUtil.get(UserUtil.USER_ID));
		data.put("affirmUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		data.put("affirmDate", new Date());
		try {
			dao.doSave("update", MDC_FromHis, data, true);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "执行取消his慢病出错", e);
		}
	}
	public HashMap<String, Object> builddata(Map<String, Object> data,
			String empiId) {
		Map<String, Object> newehrdata = new HashMap<String, Object>();
		newehrdata.put("empiId", empiId);
		newehrdata.put("regionCode", (String) data.get("areagrid"));
		newehrdata.put("manaDoctorId", (String) data.get("targetDoctor"));
		newehrdata.put("manaUnitId", (String) data.get("targetUnit"));
		newehrdata.put("createUser", (String) data.get("targetDoctor"));
		newehrdata.put("createUnit", (String) data.get("targetUnit"));
		newehrdata.put("status", "0");
		return (HashMap<String, Object>) newehrdata;
	}

	public void creategxy(Map<String, Object> data, String phrId,
			String empiId, BaseDAO dao) {

		String gxyfind = "select phrId as phrId from MDC_HypertensionRecord where phrId=:phrId";
		Map<String, Object> g = new HashMap<String, Object>();
		g.put("phrId", phrId);
		List<Map<String, Object>> gxy;
		try {
			gxy = dao.doQuery(gxyfind, g);
			if (gxy != null && gxy.size() > 0) {
				// 存在高血压档案不做操作
			} else {
				Map<String, Object> gxydata = new HashMap<String, Object>();
				gxydata = this.builddata(data, empiId);
				gxydata.put("phrId", phrId);
				try {
					Session s = dao.getSession();

					String insertgxy = "insert into MDC_HypertensionRecord(phrId,empiId,manaDoctorId,"
							+ "manaUnitId,createUser,createUnit,status,createdate,PLANTYPECODE) values "
							+ " ('"
							+ gxydata.get("phrId")
							+ "','"
							+ gxydata.get("empiId")
							+ "','"
							+ gxydata.get("manaDoctorId")
							+ "'"
							+ ",'"
							+ gxydata.get("manaUnitId")
							+ "','"
							+ gxydata.get("createUser")
							+ "','"
							+ gxydata.get("createUnit") + "','0',sysdate,'01')";
					s.createSQLQuery(insertgxy).executeUpdate();
					String maxnum = "select max(to_number(fixId)+1) as FIXID "
							+ " from MDC_HypertensionFixGroup a ";
					Map<String, Object> hg = new HashMap<String, Object>();
					List<Map<String, Object>> hglist;
					hglist = dao.doQuery(maxnum,hg);
					String fixId="";
					if (hglist != null && hglist.size() > 0) {
						fixId = hglist.get(0).get("FIXID").toString();
						if (fixId != null && fixId.length() > 0) {
							int length = fixId.length();
							for (int i = 0; i < 16 - length; i++) {
								fixId = "0" + fixId;
							}
					}
					}else{
						try {
							throw new Exception("fixId获取出错");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					String insertgxygroup = "insert into MDC_HypertensionFixGroup(fixId,phrId,empiId,fixDate,"
							+ "fixType,fixUser,fixUnit,manaUnitId) values "
							+ " ('"+fixId+"','"
							+ gxydata.get("phrId")
							+ "','"
							+ gxydata.get("empiId")
							+ "',sysdate"
							+ ",'1','"
							+ gxydata.get("createUser")
							+ "','"
							+ gxydata.get("createUnit") + "','"+gxydata.get("createUnit")+"')";
					s.createSQLQuery(insertgxygroup).executeUpdate();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createtnb(Map<String, Object> data, String phrId,
			String empiId, BaseDAO dao) {
		String tnbfind = "select phrId as phrId from MDC_DiabetesRecord where phrId=:phrId";
		Map<String, Object> g = new HashMap<String, Object>();
		g.put("phrId", phrId);
		List<Map<String, Object>> tnb;
		try {
			tnb = dao.doQuery(tnbfind, g);
			if (tnb != null && tnb.size() > 0) {
				// 存在糖尿病档案不做操作
			} else {
				Map<String, Object> tnbdata = new HashMap<String, Object>();
				tnbdata = this.builddata(data, empiId);
				tnbdata.put("phrId", phrId);
				try {
					// System.out.println(dao.doSave("create",MDC_DiabetesRecord,
					// tnbdata, false));
					Session s = dao.getSession();

					String insertgxt = "insert into MDC_DiabetesRecord(phrId,empiId,manaDoctorId,"
							+ "manaUnitId,createUser,createUnit,status,createdate,PLANTYPECODE) values "
							+ " ('"
							+ tnbdata.get("phrId")
							+ "','"
							+ tnbdata.get("empiId")
							+ "','"
							+ tnbdata.get("manaDoctorId")
							+ "'"
							+ ",'"
							+ tnbdata.get("manaUnitId")
							+ "','"
							+ tnbdata.get("createUser")
							+ "','"
							+ tnbdata.get("createUnit") + "','0',sysdate,'01')";
					s.createSQLQuery(insertgxt).executeUpdate();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String createjkda(Map<String, Object> data, BaseDAO dao) {
		String phrid = "";
		try {
			String phrid12 = data.get("regionCode").toString().substring(0, 12);
			String maxnum = "select max(to_number(substr(a.phrId,13))+1) as PHRIDNUM "
					+ " from EHR_HealthRecord a where substr(a.phrId,1,12)='"+phrid12+"'";
			Map<String, Object> ehr = new HashMap<String, Object>();
			List<Map<String, Object>> phrnumlist;
			phrnumlist = dao.doQuery(maxnum,ehr);
			if (phrnumlist != null && phrnumlist.size() > 0) {
				String phrnum = "";
				phrnum = phrnumlist.get(0).get("PHRIDNUM").toString();
				if (phrnum != null && phrnum.length() > 0) {
					int length = phrnum.length();
					for (int i = 0; i < 8 - length; i++) {
						phrnum = "0" + phrnum;
					}
					phrid = phrid12 + phrnum;

					if (phrid.length() < 20) {
						return "";
					}

					try {
						Session s = dao.getSession();
						String insertgxt = "insert into EHR_HealthRecord(phrId,empiId,manaDoctorId,"
								+ "manaUnitId,createUser,createUnit,status,createdate,regionCode"
								+ ",regionCode_text,masterFlag,isAgrRegister,deadFlag,signFlag) values "
								+ " ('"
								+ phrid
								+ "','"
								+ data.get("empiId")
								+ "','"
								+ data.get("manaDoctorId")
								+ "'"
								+ ",'"
								+ data.get("manaUnitId")
								+ "','"
								+ data.get("createUser")
								+ "','"
								+ data.get("createUnit")
								+ "','0',sysdate,'"
								+ data.get("regionCode") + "'" +
								",'"+data.get("areagrid_text")+"','n','n','n','n')";
						s.createSQLQuery(insertgxt).executeUpdate();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return phrid;
	}
}
