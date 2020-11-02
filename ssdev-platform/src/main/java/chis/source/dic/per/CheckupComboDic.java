package chis.source.dic.per;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.Dictionary;
import ctd.util.AppContextHolder;

public class CheckupComboDic {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Document dic() {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("dic");
//		WebApplicationContext wac = (WebApplicationContext)AppContextHolder.get();
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
		HibernateTemplate ht = new HibernateTemplate(sf);
		String hql = "from PER_Combo a";
		List<HashMap<String, Object>> list = ht.find(hql);
		Iterator<HashMap<String, Object>> it = list.iterator();
		Dictionary dic=null;
		try {
			dic = DictionaryController.instance().get("chis.dictionary.projectOffice");
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		int iden = 1;
		while (it.hasNext()) {
			String tckey = "tc" + iden++;
			HashMap<String, Object> r = it.next();
			String maganeUnit = r.get("manaUnitId").toString();
			Element el = root.addElement("item")
					.addAttribute("key", r.get("id").toString())
					.addAttribute("text", r.get("name").toString())
					.addAttribute("manaUnitId", maganeUnit)
					.addAttribute("type", "tc").addAttribute("folder", "true"); // 套餐

			// String hql1 =
			// "from PER_ComboDetail a where comboId = ? order by orderNo asc ";
			String hql1 = "from PER_ComboDetail a where comboId = ? ";
			List<HashMap<String, Object>> list1 = ht.find(hql1, r.get("id"));
			if (list1.size() == 0) {
				continue;
			}
			// StringBuilder pp = new StringBuilder();
			for (Iterator<HashMap<String, Object>> it1 = list1.iterator(); it1
					.hasNext();) {
				String kskey = tckey + "ks" + iden++;
				HashMap<String, Object> d1 = it1.next();
				String projectOfficeCode = d1.get("projectOfficeId").toString();
				String projectOffice = dic.getText(projectOfficeCode);
				Element ele = el.addElement("item").addAttribute("key", kskey)
						.addAttribute("text", projectOffice)
						.addAttribute("projectOfficeCode", projectOfficeCode)
						.addAttribute("projectOffice", projectOffice)
						.addAttribute("manaUnitId", maganeUnit)
						.addAttribute("type", "ks")
						.addAttribute("folder", "true"); // 科室

				String projects = d1.get("itemId").toString();
				final String[] ps = projects.split(",");
				StringBuilder projects_new = new StringBuilder();
				for (String p : ps) {
					projects_new.append(",").append("'").append(p).append("'");
				}
				List<HashMap<String, Object>> ls = ht
						.find("from PER_CheckupDict a where checkupProjectId in ("
								+ projects_new.substring(1) + ")");
				Collections.sort(ls, new Comparator() {
					public int compare(Object o1, Object o2) {
						int a = 0, b = 0;
						HashMap<String, Object> d1 = (HashMap<String, Object>) o1;
						HashMap<String, Object> d2 = (HashMap<String, Object>) o2;
						for (int i = 0; i < ps.length; i++) {
							if (d1.get("checkupProjectId").equals(ps[i])) {
								a = i;
							}
							if (d2.get("checkupProjectId").equals(ps[i])) {
								b = i;
							}
						}
						if (a < b) {
							return -1;
						}
						if (a > b) {
							return 1;
						}
						return 0;
					}
				});
				Iterator<HashMap<String, Object>> itr = ls.iterator();
				while (itr.hasNext()) {
					HashMap<String, Object> d = itr.next();
					String xmkey = kskey + "xm" + iden++;
					Double referenceUpper = (Double) d.get("referenceUpper");
					String referenceUpperString = "";
					if(referenceUpper !=null){
						referenceUpperString = referenceUpper.toString();
					}
					Double referenceLower = (Double) d.get("referenceLower");
					String referenceLowerString = "";
					if(referenceLower != null){
						referenceLowerString = referenceLower.toString();
					}
					String memo = (String) d.get("memo");
					if(StringUtils.isEmpty(memo)){
						memo = "";
					}
					Element elem = ele
							.addElement("item")
							.addAttribute("type", "item")
							.addAttribute("key", xmkey)
							.addAttribute("checkupProjectId", d.get("checkupProjectId").toString())
							.addAttribute("projectOfficeCode", projectOfficeCode)
							.addAttribute("projectOffice", projectOffice)
							.addAttribute("manaUnitId", maganeUnit)
							.addAttribute("text", d.get("checkupProjectName").toString())
							.addAttribute("referenceUpper", referenceUpperString)
							.addAttribute("referenceLower", referenceLowerString)
							.addAttribute("memo", memo); // 项目
					for (Iterator<String> iter = d.keySet().iterator(); iter
							.hasNext();) {
						String key = iter.next();
						if (!key.contains("$") && d.get(key) != null
								&& d.get(key).toString().trim().length() > 0) {
							elem.addAttribute(key, d.get(key).toString());
						}
					}
				}
			}
		}
		return doc;
	}

}
