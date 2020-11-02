package chis.source.cvd;

import java.util.Map;
import org.springframework.dao.DataAccessException;

import chis.source.util.SendDictionaryReloadSynMsg;
import ctd.dictionary.DictionaryController;
import ctd.service.dao.SimpleRemove;
import ctd.util.context.Context;


public class CategoryRemove extends SimpleRemove {

	public void doAfterExec(Map<String,Object> jsonReq, Map<String,Object> jsonRes, Context ctx) throws DataAccessException{
		DictionaryController.instance().reload("chis.dictionary.category");
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.category");
	}	
 
}
