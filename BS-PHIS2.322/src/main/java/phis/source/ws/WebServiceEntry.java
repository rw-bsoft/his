package phis.source.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class WebServiceEntry{
	@WebMethod
	public String invoke(@WebParam(name = "schemaName")String schemaName, @WebParam(name = "pKey")String pKey, 
			@WebParam(name = "tableName")String tableName) {
		if(schemaName==null){
			schemaName = "phis.application.hos.schemas.ZY_FYMX";
		}
		if(pKey==null){
			pKey = "JLXH";
		}
		if(tableName==null){
			tableName = "ZY_FYMX";
		}
		try {
			Schema sc = SchemaController.instance().get(schemaName);
			List<SchemaItem> items = sc.getItems();
			SchemaItem item = null;
			for (SchemaItem sit : items) {
				if (pKey.equals(sit.getId())) {
					item = sit;
					break;
				}
			}
			Context ctx = ContextUtils.getContext();
			String pkey = Long.parseLong(KeyManager.getKeyByName(tableName,
						item.getKeyRules(), item.getId(), ctx))+"";
			return pkey;
		} catch (ControllerException e) {
			e.printStackTrace();
		} catch (KeyManagerException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	 
}
