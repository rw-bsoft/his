/**
 * @(#)YearsDictionary.java Created on 2014-6-13 上午10:56:42
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.dic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import ctd.service.core.ServiceException;
import ctd.service.dao.DBService;
import ctd.util.context.Context;
import ctd.util.xml.XMLHelper;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class YearsDictionary extends DBService{
	
	public Document dic(){
		Document doc = XMLHelper.createDocument();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String thisYears = Integer.parseInt(sdf.format(new Date()))+1+"";
		Element root = doc.addElement("dic").addAttribute("class",
				"XMLDictionary");
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if (thisYears.equals("2000")) {
				break;
			}
			root.addElement("item").addAttribute("key", thisYears)
					.addAttribute("text", thisYears + "年");
			thisYears = (Integer.parseInt(thisYears) - 1) + "";
		}

		return doc;
	}
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		
	}
}
