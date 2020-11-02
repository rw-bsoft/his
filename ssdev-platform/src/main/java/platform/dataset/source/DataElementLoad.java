package platform.dataset.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleLoad;
import ctd.util.S;
import ctd.util.context.Context;

public class DataElementLoad extends SimpleLoad {
	@Override
	public void doAfterExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		if((Integer)res.get(Service.RES_CODE) == 200){
			Map<String,Object> rec = (Map<String, Object>) res.get("body");
			if(rec!=null){
				String dicName=S.obj2String(rec.get("CodeSystem"));
				String dataStandardId=S.obj2String(((Map)rec.get("DataStandardId")).get("key"));
				if(!StringUtils.isEmpty(dicName)){
					if(!StringUtils.isEmpty(dataStandardId)){
						dicName="platform.dataset.dic.res."+dataStandardId+"."+dicName;
					}
					Dictionary dic = DictionaryController.instance().getDic(dicName);
					if (dic != null) {
						Map<String,String> dicItems=new HashMap<String, String>();
						List<DictionaryItem> items = dic.itemsList();
						for (DictionaryItem item : items) {
							dicItems.put(item.getKey(), item.getText());
						}
						rec.put("dicItems", dicItems);
					}
				}
			}
		}
	}
	
}
