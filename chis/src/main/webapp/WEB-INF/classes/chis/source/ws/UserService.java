package chis.source.ws;

import java.util.List;
import java.util.Map;

import ctd.dictionary.DictionaryController;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import ctd.dictionary.Dictionary;
import ctd.service.core.ServiceException;

/**
 * 获取根据条件获取用户
 * @author messagewell
 *
 */
public class UserService extends AbstractWsService {
	
//	private static final Log logger = LogFactory.getLog(UserService.class);
	
	public String execute(String request){
		Object[] result = preExecute(request);
		Document resDoc = (Document) result[1];
		if ((Integer) result[0] == 1) {
			return resDoc.asXML();
		}
		Document reqDoc = (Document) result[2];
		Element resRoot = resDoc.getRootElement();
		Element reqRoot = reqDoc.getRootElement();
		Element codeEle = resRoot.element("code");
		Element msgEle = resRoot.element("msg");
		Session session = getSessionFactory().openSession();
		String manaUnitId = reqRoot.elementText("manaUnitId");
		String hql ="select a.userId as userId,a.password as password," +
				"a.userName as userName,b.manaUnitId as manaUnitId,b.jobId as jobId, " +
				"b.regionCode as regionCode from SYS_USERS a,SYS_UserProp b " +
				"where a.userId = b.userId and manaUnitId like :manaUnitId and jobId in ('01','05')  and status ='0'";
		Query q = session.createQuery(hql);
		q = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		q.setString("manaUnitId",manaUnitId+"%");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> list =  q.list();
		Element roles = resRoot.addElement("roles");
		for(int i= 0 ;i<list.size();i++){
			Map<String,Object> rec = list.get(i);
			Element role = roles.addElement("role");
			role.addAttribute("userId", (String)rec.get("userId"));
			role.addAttribute("password", (String)rec.get("password"));
			role.addAttribute("userName", (String)rec.get("userName"));
			role.addAttribute("regionCode", (String)rec.get("regionCode"));
			String userManaUnitId = (String)rec.get("manaUnitId") ;
			role.addAttribute("manaUnitId", userManaUnitId);
			Dictionary manaUnitDic = DictionaryController.instance().getDic("manageUnit");
			Dictionary jobNameDic = DictionaryController.instance().getDic("jobName");
			role.addAttribute("manaUnitId_text", manaUnitDic.getText(userManaUnitId));
			String jobId = (String)rec.get("jobId") ;
			role.addAttribute("jobId", jobId);
			role.addAttribute("jobId_text", jobNameDic.getText(jobId));
 		}
		codeEle.setText("200");
		msgEle.setText("succeed.");
		return resRoot.asXML();
	}
	
	/**
	 * 
	 */
	protected boolean verifyRequest(Element reqRoot) throws ServiceException {
		return true;
	}

	protected Integer checkUser(String uid, String password, String rid)throws Exception {
		return  null;
	}
}
