package platform.dataset.source;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import ctd.controller.watcher.WatchHelper;
import ctd.controller.watcher.WatcherCommands;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.support.XMLDictionary;
import ctd.resource.ResourceCenter;
import ctd.service.configure.DicConfig;
import ctd.service.core.Service;
import ctd.util.AppContextHolder;
import ctd.util.PyConverter;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.xml.XMLHelper;

public class DictionaryUtil implements Service{
	
	public void execute(Map<String, Object> req,	Map<String, Object> res, Context ctx) {
		String cmd = (String)req.get("cmd");
		if(StringUtils.isEmpty(cmd)){
			res.put(Service.RES_CODE,401);
			res.put(Service.RES_MESSAGE,"OPMissing");
			return;
		}
		if(cmd.equals("getDicList")){
			getDictionaryList(req,res,ctx);
			return;
		}
		if(cmd.equals("saveDic")){    //增加字典
			doSaveDictionary(req,res,ctx);
			return;
		}
		if(cmd.equals("removeDic")){   //删除字典
			doRemoveDictionary(req,res,ctx);
			return;
		}
		if(cmd.equals("removeDic4Meta")){   //删除字典
			doRemoveDictionary4Meta(req,res,ctx);
			return;
		}
		if(cmd.equals("createItem")){
			doNewItem(req,res,ctx);
			return;
		}
		if(cmd.equals("updateItem")){
			doSaveItem(req,res,ctx);
			return;
		}
		if(cmd.equals("removeItem")){
			doRemoveItem(req,res,ctx);
			return;
		}			
		if(cmd.equals("changeParent")){
			doItemXParent(req,res,ctx);
			return;
		}
		if(cmd.equals("queryItem")){
			doQueryItem(req,res,ctx);
			return;
		}		
	}
	
	public void getDictionaryList(Map<String, Object> req,	Map<String, Object> res, Context ctx){
		
	}
	
	public void createDicTemplate(String id, String folder){
	}
	
	public void doSaveDictionary(Map<String, Object> req,	Map<String, Object> res, Context ctx){
		HashMap<String,Object> data = (HashMap<String,Object>)req.get("body");
		String msg = "Success";
		int code = 200;
		if(data == null){
			code = 402;
			msg = "NoData";
		}
		else{
			String id = (String)data.get("key");
			if(S.isEmpty(id)){
				code = 403;
				msg = "IdMissing";
			}
			else{
				try {
					String saveDicOp = (String) req.get("saveDicOp");
					String directory = ((String) req.get("directory")).replace(".", "/");
					if("create".equals(saveDicOp)){
						File file = new File(ResourceCenter.getAbstractClassPath(S.join(directory,"/",id,".dic")));
						if(file.exists()){
							code = 410;
							msg = "字典编码已经存在.";
						}else{
							// create dic doc
							Document doc = DocumentHelper.createDocument();
							Element dic = doc.addElement("dic");
							String alias = (String)data.get("text");
							dic.addAttribute("alias", alias);
							XMLHelper.putDocument(file, doc);
							//	update dicList
							String cls = (String)data.get("class");
							if(!S.isEmpty(cls)){
								dic.addAttribute("class", cls);
							}
							XMLHelper.putDocument(file, doc);
							msg = "Created";
						}
					}
					if("update".equals(saveDicOp)){
						// udate dic doc
						File file = new File(ResourceCenter.getAbstractClassPath(S.join(id.replace(".", "/"),".dic")));
						Document doc = XMLHelper.getDocument(file);
						Element dic = doc.getRootElement();
						String alias = (String)data.get("text");
						dic.addAttribute("alias", alias);
						XMLHelper.putDocument(file, doc);
						DictionaryController.instance().reload(id);
						WatchHelper.send(WatchHelper.DICTIONARY, id, WatcherCommands.CMD_RELOAD);
					}
					DictionaryController.instance().reload(DicConfig.dictionaries);
				} catch (Exception e) {
					e.printStackTrace();
					res.put(Service.RES_CODE,500);
					res.put(Service.RES_MESSAGE,e.getMessage());			
				}
			}
		}
		res.put(Service.RES_CODE,code);
		res.put(Service.RES_MESSAGE,msg);			
	}
	public void doRemoveDictionary(Map<String, Object> req,	Map<String, Object> res, Context ctx) {
		HashMap<String,Object> data = (HashMap<String,Object>)req.get("body");
		String msg = "Success";
		int code = 200;
		if(data == null){
			code = 402;
			msg = "NoData";
		}
		else{
			String id = (String)data.get("key");
			if(StringUtils.isEmpty(id)){
				code = 403;
				msg = "IdMissing";
			}
			else{
				try {
					File file = new File(ResourceCenter.getAbstractClassPath(S.join(id.replace(".", "/"),".dic")));
					if(!file.exists()){
						code = 404;
						msg = "DicNotFound";
					}
					else{
						file.delete();
						DictionaryController.instance().reload(id);
						DictionaryController.instance().reload(DicConfig.dictionaries);
						WatchHelper.send(WatchHelper.DICTIONARY, id, WatcherCommands.CMD_RELOAD);
					}
				} catch (Exception e) {
					res.put(Service.RES_CODE,500);
					res.put(Service.RES_MESSAGE,e.getMessage());		
				}
			}
		}
		res.put(Service.RES_CODE,code);
		res.put(Service.RES_MESSAGE,msg);			
	}
	public void doRemoveDictionary4Meta(Map<String, Object> req,	Map<String, Object> res, Context ctx) {
		HashMap<String,Object> data = (HashMap<String,Object>)req.get("body");
		String msg = "Success";
		int code = 200;
		if(data == null){
			code = 402;
			msg = "NoData";
		}
		else{
			String id = (String)data.get("key");
			if(StringUtils.isEmpty(id)){
				code = 403;
				msg = "IdMissing";
			}
			else{
				File file = null;
				try {
					file = new File(ResourceCenter.getAbstractClassPath(S.join(id.replace(".", "/"),".dic")));
				} catch (URISyntaxException e1) {
					res.put(Service.RES_CODE,500);
					res.put(Service.RES_MESSAGE,e1.getMessage());
					return;
				}
				if(!file.exists()){
					code = 404;
					msg = "DicNotFound";
				}
				else{
					SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
					Session ss = null;
					try {
						ss = sf.openSession();
						Query q = ss.createQuery("select count(*) from RES_DataElement where CodeSystem = ?");
						q.setString(0, id);
						if((Long)q.list().get(0) > 0){
							code = 405;
							msg = "该字典项还存在引用，删除失败";
						}else{
							file.delete();
							DictionaryController.instance().reload(id);
							DictionaryController.instance().reload(DicConfig.dictionaries);
							WatchHelper.send(WatchHelper.DICTIONARY, id, WatcherCommands.CMD_RELOAD);
						}
					} catch (Exception e) {
						code = 405;
						msg = "字典删除失败";
					} finally {
						if(ss != null && ss.isOpen()){
							ss.close();
						}
					}
				}
			}
		}
		res.put(Service.RES_CODE,code);
		res.put(Service.RES_MESSAGE,msg);			
	}
	
	public void doNewItem(Map<String, Object> req,	Map<String, Object> res, Context ctx){
		HashMap<String,Object> data = (HashMap<String,Object>)req.get("body");
		String msg = "Success";
		int code = 200;
		if(data == null){
			code = 402;
			msg = "NoData";
		}
		else{
			String dicId = (String)data.get("dicId");
			if(StringUtils.isEmpty(dicId)){
				code = 403;
				msg = "IdMissing";
			}
			else{
				msg = "Created";
				Dictionary dic = DictionaryController.instance().getDic(dicId);
				if(dic != null){
					Document dicDoc = ((XMLDictionary)dic).getDefineDoc();
					String parentKey = (String)data.get("parentKey");
					Element parent = null;
					Element find = null;
					String key = (String)data.get("key");
					if(StringUtils.isEmpty(parentKey)){
						parent = dicDoc.getRootElement();
					}
					else{
						parent  = (Element)dicDoc.selectSingleNode("//item[@key='" + parentKey + "']");
					}
					find = (Element)dicDoc.selectSingleNode("//item[@key='" + key + "']");
					if(parent != null && find ==null){
						String text = (String)data.get("text");
						List<HashMap<String,Object>> props = (List<HashMap<String,Object>>)data.get("props");					
						Element item = DocumentHelper.createElement("item")
						.addAttribute("key", key)
						.addAttribute("text",text)
						.addAttribute("pyCode",PyConverter.getFirstLetter(text));
						if(props != null){
							int n = props.size();
							for(int i = 0;i < n; i ++){
								HashMap<String,Object> o = (HashMap<String,Object>)props.get(i);
								String name = (String)o.get("name");
								if(!StringUtils.isAlpha(name)){
									code = 407;
									msg = "属性项中[属性]不能包含数字和符号";
									res.put(Service.RES_CODE,code);
									res.put(Service.RES_MESSAGE,msg);
									return;
								}								
								item.addAttribute((String)o.get("name"), (String)o.get("value"));
							}
						}
						parent.add(item);
						File file = null;
						try {
							file = new File(ResourceCenter.getAbstractClassPath(S.join(dicId.replace(".", "/"),".dic")));
							XMLHelper.putDocument(file,dicDoc);
						} catch (Exception e) {
							res.put(Service.RES_CODE,500);
							res.put(Service.RES_MESSAGE,e.getMessage());
							return;
						}
						DictionaryController.instance().reload(dicId);
						WatchHelper.send(WatchHelper.DICTIONARY, dicId, WatcherCommands.CMD_RELOAD);
					}
					else{ 
						code = 406;
						msg = "ParentNotFoundOrKeyDul";
					}
				}
				else{
					code = 405;
					msg = "DicLookupFailed";					
				}
			}
		}
		res.put(Service.RES_CODE,code);
		res.put(Service.RES_MESSAGE,msg);	
	}
	
	public void doSaveItem(Map<String, Object> req,	Map<String, Object> res, Context ctx) {
		HashMap<String,Object> data = (HashMap<String,Object>)req.get("body");
		String msg = "";
		int code = 200;
		if(data == null){
			code = 402;
			msg = "NoData";
		}
		else{
			String dicId = (String)data.get("dicId");
			if(StringUtils.isEmpty(dicId)){
				code = 403;
				msg = "IdMissing";
			}
			else{
				msg = "Update";
				Dictionary dic = DictionaryController.instance().getDic(dicId);
				if(dic != null){
					Document dicDoc = ((XMLDictionary)dic).getDefineDoc();
					String key = (String)data.get("key");
					Element item  = (Element)dicDoc.selectSingleNode("//item[@key='" + key + "']");
					if(item != null){
							String text = (String)data.get("text");
							//JSONArray props = data.optJSONArray("props");
							List<HashMap<String,Object>> props = (List<HashMap<String,Object>>)data.get("props");
							// {{{ update by wuk
							for(Iterator<Attribute> it=item.attributeIterator();it.hasNext();){
								@SuppressWarnings("unused")
								Attribute attr = it.next();
								it.remove();
							}
							// }}}
							item.addAttribute("key", key)
								.addAttribute("text",text)
								.addAttribute("pyCode",PyConverter.getFirstLetter(text));
							
							if(props != null){						
								int n = props.size();
								for(int i = 0;i < n; i ++){
									HashMap<String,Object> o = (HashMap<String,Object>)props.get(i);
									String name = (String)o.get("name");
									if(!StringUtils.isAlpha(name)){
										code = 407;
										msg = "属性项中[属性]不能包含数字和符号";
										res.put(Service.RES_CODE,code);
										res.put(Service.RES_MESSAGE,msg);
										return;
									}
									
									item.addAttribute((String)o.get("name"), (String)o.get("value"));
								}							
							}
							long ver = System.currentTimeMillis();
							File f = null;
							try {
								f = new File(ResourceCenter.getAbstractClassPath(S.join(dicId.replace(".", "/"),".dic")));
								XMLHelper.putDocument(f,dicDoc);
							} catch (Exception e) {
								res.put(Service.RES_CODE,500);
								res.put(Service.RES_MESSAGE,e.getMessage());
								return;
							}
							f.setLastModified(ver);
							DictionaryController.instance().reload(dicId);
							WatchHelper.send(WatchHelper.DICTIONARY, dicId, WatcherCommands.CMD_RELOAD);
					}
					else{
						code = 406;
						msg = "ItemNotFound";
					}
				}
				else{
					code = 405;
					msg = "DicLookupFailed";					
				}
			}
		}
		res.put(Service.RES_CODE,code);
		res.put(Service.RES_MESSAGE,msg);		
	}

	public void doRemoveItem(Map<String, Object> req,	Map<String, Object> res, Context ctx){
		HashMap<String,Object> data = (HashMap<String,Object>)req.get("body");
		String msg = "";
		int code = 200;
		if(data == null){
			code = 402;
			msg = "NoData";
		}
		else{
			String dicId = (String)data.get("dicId");
			if(StringUtils.isEmpty(dicId)){
				code = 403;
				msg = "IdMissing";
			}
			else{
				Dictionary dic = DictionaryController.instance().getDic(dicId);
				if(dic != null){
					Document dicDoc = ((XMLDictionary)dic).getDefineDoc();
					String key = (String)data.get("key");
					Element item  = (Element)dicDoc.selectSingleNode("//item[@key='" + key + "']");
					if(item != null){
						item.getParent().remove(item);
						File file = null;
						try {
							file = new File(ResourceCenter.getAbstractClassPath(S.join(dicId.replace(".", "/"),".dic")));
							XMLHelper.putDocument(file,dicDoc);
						} catch (Exception e) {
							res.put(Service.RES_CODE,500);
							res.put(Service.RES_MESSAGE,e.getMessage());
							return;
						}
						DictionaryController.instance().reload(dicId);
						WatchHelper.send(WatchHelper.DICTIONARY, dicId, WatcherCommands.CMD_RELOAD);
					}
					else{
						code = 406;
						msg = "ItemNotFound";
					}
				}
				else{
					code = 405;
					msg = "DicLookupFailed";					
				}
			}
		}
		res.put(Service.RES_CODE,code);
		res.put(Service.RES_MESSAGE,msg);		
	}
	
	public void doItemXParent(Map<String, Object> req,	Map<String, Object> res, Context ctx){
		
	}	
	
	private void doQueryItem(Map<String, Object> req,	Map<String, Object> res, Context ctx){
		
	}
	
}
