package platform.dataset.source;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.resource.ResourceCenter;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.xml.XMLHelper;

@SuppressWarnings("rawtypes")
public class DataSetXsd{
	private Dictionary groupDic=DictionaryController.instance().getDic("platform.dataset.dic.resDataGroup");
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSetXsd.class);
	//载入body以外的其他数据源验证段
	private	Document docXsd=null;
	private	Element rootXsd=null;
	//基础的数据类型集
	private Set<String> type=new HashSet<String>();
	//包含长度的数据类型集
	private Set<String> type4Length=new HashSet<String>();
	
	public List getQuertCnd(String key,String value) throws Exception{
		String exp="['eq',['$','"+key+"'],['i',"+value+"]]";
		List queryCnd=JSONUtils.parse(exp, List.class);
		return queryCnd;
	}
	public  QueryResult find(String enrtyName,List queryCnd,Context ctx) throws Exception{
		Schema schema=SchemaController.instance().getSchema(enrtyName);
		SimpleDAO dao=new SimpleDAO(schema, ctx);
		QueryResult qr=dao.find(queryCnd);
		return qr; 
	}
	//数据源验证
	public void addElements(Element el,List<Map<String, Object>> list,String customIdentify){
		for(Map<String, Object> map:list){
			String dataType=S.obj2String(map.get("DataType"));
			if(StringUtils.isEmpty(dataType)){
				dataType=S.obj2String(map.get("bDataType"));
			}
			String dataLength=S.obj2String(map.get("DataLength"));
			if(StringUtils.isEmpty(dataLength)){
				dataLength=S.obj2String(map.get("bDataLength"));
			}
			if(dataType.subSequence(0, 1).equals("S")){
		//		dataType="string";
				type.add("normalizedString");
				if(!StringUtils.isEmpty(dataLength)){
					type4Length.add(dataLength);
					dataType=dataLength;
				}
			}else if(dataType.equals("N")){
			//以加N为结尾区别string和decimal类型长度	
				dataType="decimal";
				type.add(dataType);
				if(!StringUtils.isEmpty(dataLength)){
					dataLength=dataLength+"N";
					type4Length.add(dataLength);
					//将","替换成"."schema命名不能有","
					dataType=dataLength.replaceAll(",",".");
				}
			}else if(dataType.equals("D")){
				dataType="forceDate";
				//		type.add(dataType);
				//改用和datetime同样验证，如果不是强制性标识为1的，用可以为空的表达式
				if(!(S.obj2String(map.get("ForceIdentify"))==null?"":S.obj2String(map.get("ForceIdentify"))).equals("1")){
					dataType="date";
				}
				type4Length.add(dataType);
				type.add("normalizedString");
			}else if(dataType.equals("DT")){
				dataType="forceDateTime";
				if(!(S.obj2String(map.get("ForceIdentify"))==null?"":S.obj2String(map.get("ForceIdentify"))).equals("1")){
					dataType="dateTime";
				}
				type4Length.add(dataType);
				type.add("normalizedString");				
			}else if(dataType.equals("L")){
				dataType="boolean";
				type.add(dataType);
			}else{
				String cutName=S.obj2String(map.get("CustomIdentify"));
				LOGGER.warn("data element [{}] in dataset[{}] has no type!",cutName,customIdentify);
			}
			//添加数据组和分类
			String groupId=S.obj2String(map.get("DataGroup"));
			if(!StringUtils.isEmpty(groupId)){
				String groupname =S.obj2String(groupDic.getItem(groupId).getProperty("GroupIdentify"));
				String frequencyNumber=S.obj2String(groupDic.getItem(groupId).getProperty("FrequencyNumber"));
				if(StringUtils.isEmpty(groupname)){
					LOGGER.error("could not find DataGroup:"+S.obj2String(map.get("DataGroup")));
				}else{
					findGroup(groupId,null,null,null);
					String[] topGroup=getTopGroup(groupId);
					if(StringUtils.isEmpty(frequencyNumber)||frequencyNumber.equals("1")){
						Element group=(Element) rootXsd.selectSingleNode("//xs:complexType[@name='"+groupname+"']//xs:all");
						Element ele=group.addElement("xs:element").addAttribute("name", S.obj2String(map.get("CustomIdentify")==null?"":S.obj2String(map.get("CustomIdentify")))).addAttribute("type",dataType);
						if(!(S.obj2String(map.get("ForceIdentify"))==null?"":S.obj2String(map.get("ForceIdentify"))).equals("1")){
							ele.addAttribute("minOccurs", "0");
						}
						if(((Element) el.selectSingleNode("//xs:element[@name='"+topGroup[0]+"']")==null)){
							Element elm=el.addElement("xs:element").addAttribute("name",topGroup[0]).addAttribute("type",topGroup[0]);
							if(StringUtils.isEmpty(topGroup[1])||topGroup[1].equals("0")){
								elm.addAttribute("minOccurs", "0");
							}
						}
					}else{
						Element group=(Element) rootXsd.selectSingleNode("//xs:complexType[@name='"+groupname+"s']//xs:all");
						Element ele=group.addElement("xs:element").addAttribute("name", S.obj2String(map.get("CustomIdentify")==null?"":S.obj2String(map.get("CustomIdentify")))).addAttribute("type",dataType);
						if(!(S.obj2String(map.get("ForceIdentify"))==null?"":S.obj2String(map.get("ForceIdentify"))).equals("1")){
							ele.addAttribute("minOccurs", "0");
						}
						if(((Element) el.selectSingleNode("//xs:element[@name='"+topGroup[0]+"']")==null)){
							Element elm=el.addElement("xs:element").addAttribute("name",topGroup[0]).addAttribute("type",topGroup[0]);
							if(StringUtils.isEmpty(topGroup[1])||topGroup[1].equals("0")){
								elm.addAttribute("minOccurs", "0");
							}
						}
					}
				}
			}else{
				Element el1=el.addElement("xs:element").addAttribute("name", S.obj2String(map.get("CustomIdentify")==null?"":S.obj2String(map.get("CustomIdentify")))).addAttribute("type",dataType);
				if(!(S.obj2String(map.get("ForceIdentify"))==null?"":S.obj2String(map.get("ForceIdentify"))).equals("1")){
					el1.addAttribute("minOccurs", "0");
				}
			}
		}
	}
	//返回最顶层组的名字和强制性标识符
	private String[] getTopGroup(String groupId){
		String parentKey=S.obj2String(groupDic.getItem(groupId).getProperty("ParentGroupId"));
		if(StringUtils.isEmpty(parentKey)){
			String[] arr=new String[2];
			String name=S.obj2String(groupDic.getItem(groupId).getProperty("GroupIdentify"));
			String frequencyNumber=S.obj2String(groupDic.getItem(groupId).getProperty("FrequencyNumber"));
			String forceIdentify=S.obj2String(groupDic.getItem(groupId).getProperty("ForceIdentify"));
			if("2".equals(frequencyNumber)){
				name=name+"s";
			}
			arr[0]=name;
			arr[1]=forceIdentify;
			return arr;
		}else{
			return getTopGroup(parentKey);
		}
	}
	
	//寻找数据组引用,没有新建数据组引用。 组出现频率 1:1次,2:多次,强制性标识:0:可选 1:必填
	private void findGroup(String groupId,String sonName,String sonRrequency,String sonForceIdentify){
		String groupname =S.obj2String(groupDic.getItem(groupId).getProperty("GroupIdentify"));
		String frequencyNumber=S.obj2String(groupDic.getItem(groupId).getProperty("FrequencyNumber"));
		String forceIdentify=S.obj2String(groupDic.getItem(groupId).getProperty("ForceIdentify"));
		if(StringUtils.isEmpty(frequencyNumber)||frequencyNumber.equals("1")){
			Element group=(Element) rootXsd.selectSingleNode("//xs:complexType[@name='"+groupname+"']//xs:all");
			if(group!=null){
				if(!StringUtils.isEmpty(sonName)){
					Element sonElement=group.addElement("xs:element").addAttribute("name",sonName).addAttribute("type",sonName);
					if(StringUtils.isEmpty(sonForceIdentify)||sonForceIdentify.equals("0")){
						sonElement.addAttribute("minOccurs", "0");
					}
				}
				return;
			}else{
				group=rootXsd.addElement("xs:complexType").addAttribute("name", groupname).addElement("xs:all");
				if(!StringUtils.isEmpty(sonName)){
					Element sonElement=group.addElement("xs:element").addAttribute("name",sonName).addAttribute("type",sonName);
					if(StringUtils.isEmpty(sonForceIdentify)||sonForceIdentify.equals("0")){
						sonElement.addAttribute("minOccurs", "0");
					}
				}
				String parentKey=S.obj2String(groupDic.getItem(groupId).getProperty("ParentGroupId"));
				if(StringUtils.isEmpty(parentKey)){
					return;
				}else{
					findGroup(parentKey,groupname,frequencyNumber,forceIdentify);
				}
			}
		}else if(frequencyNumber.equals("2")){
			Element group=(Element) rootXsd.selectSingleNode("//xs:complexType[@name='"+groupname+"s']//xs:all");
			if(group!=null){
				if(!StringUtils.isEmpty(sonName)){
					Element sonElement=group.addElement("xs:element").addAttribute("name",sonName).addAttribute("type",sonName);
					if(StringUtils.isEmpty(sonForceIdentify)||sonForceIdentify.equals("0")){
						sonElement.addAttribute("minOccurs", "0");
					}
				}
				return;
			}else{
				Element e=rootXsd.addElement("xs:complexType").addAttribute("name", groupname+"s");
				group=e.addElement("xs:sequence").addElement("xs:element").addAttribute("name", groupname).addAttribute("maxOccurs", "unbounded").addElement("xs:complexType").addElement("xs:all");
				if(!StringUtils.isEmpty(sonName)){
					Element sonElement=group.addElement("xs:element").addAttribute("name",sonName).addAttribute("type",sonName);
					if(StringUtils.isEmpty(sonForceIdentify)||sonForceIdentify.equals("0")){
						sonElement.addAttribute("minOccurs", "0");
					}
				}
				String parentKey=S.obj2String(groupDic.getItem(groupId).getProperty("ParentGroupId"));
				if(StringUtils.isEmpty(parentKey)){
					return;
				}else{
					findGroup(parentKey,groupname+"s",frequencyNumber,forceIdentify);
				}
			}
		}
	}

	public Document getDocument(String dataSetId,String customIdentify,String standardIdentify,Context ctx) throws Exception{
		docXsd=DocumentHelper.parseText(XMLHelper.getDocument(ResourceCenter.load("platform/dataset/source/dataset.xsd").getInputStream()).asXML());
		rootXsd=docXsd.getRootElement();
		List queryCnd=getQuertCnd("DataSetId",dataSetId);
		List<Map<String, Object>> list=find("platform.dataset.schemas.RES_DataSetContent",queryCnd,ctx).getRecords();

		//添加body里的元部分
		Element body=(Element) rootXsd.selectSingleNode("//xs:element[@name='Body']/xs:complexType/xs:sequence");
		Element xmlRoot=body.addElement("xs:element");
		xmlRoot.addAttribute("name",customIdentify);
		Element complexType=xmlRoot.addElement("xs:complexType");
		Element all=complexType.addElement("xs:all");
		complexType.addElement("xs:attribute").addAttribute("name", "Name").addAttribute("type", "xs:string");
		addElements(all, list,customIdentify);
		
		//生成基本类型的引用
		for(String t:type){
			Element e=rootXsd.addElement("xs:complexType").addAttribute("name", t);
		    Element e1=e.addElement("xs:simpleContent").addElement("xs:extension").addAttribute("base", "xs:"+t);
		    e1.addElement("xs:attribute").addAttribute("name", "de").addAttribute("type", "xs:string").addAttribute("use","optional");
		    e1.addElement("xs:attribute").addAttribute("name", "text").addAttribute("type", "xs:string");
		    e1.addElement("xs:attribute").addAttribute("name", "display").addAttribute("type", "xs:string");
		}
		//生成拓展类型的引用
		for(String length:type4Length){
			String pattern=getPattern(length,list,customIdentify);
			if(StringUtils.isEmpty(pattern)){
				continue;
			}
			Element e=rootXsd.addElement("xs:complexType").addAttribute("name",length.replaceAll(",",".")).addElement("xs:simpleContent");
			if(length.endsWith("N")){
			Element e1=e.addElement("xs:restriction").addAttribute("base", "decimal");
				e1.addElement("xs:pattern").addAttribute("value", pattern);
			}else{
				Element e1=e.addElement("xs:restriction").addAttribute("base", "normalizedString");
				e1.addElement("xs:pattern").addAttribute("value", pattern);
			}
		}
		docXsd.asXML();
		return docXsd;
	}
	
	//生成数据长度类型的引用
	public String getPattern(String originalLength,List<Map<String, Object>> list,String customIdentify) {
		String length=originalLength;
		if(length.endsWith("N")){
			length=length.substring(0,length.length()-1);
		}
		String pattern=null;
		//日期用字符串来约束，这样就不要中间有"T",date和datetime暂时要求同一个正则表达式
		//当强制性标识为1的时候，时间类型加force，一定要有值，当强制性标识为0或无的时候，时间类型可有可为空值
		if(length.equals("forceDateTime")){
			pattern="\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[12][0-9]|[3][0-1])([T,\\s](([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])))?";
		}else if(length.equals("dateTime")){
			pattern="(\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[12][0-9]|[3][0-1])([T,\\s](([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])))?)?";
		}else if(length.equals("forceDate")){
			pattern="\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[12][0-9]|[3][0-1])([T,\\s](([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])))?";
		}else if(length.equals("date")){
			pattern="(\\d{4}-([0][1-9]|[1][0-2])-([0][1-9]|[12][0-9]|[3][0-1])([T,\\s](([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])))?)?";
		}else if(Pattern.matches("AN\\.\\.\\d+", length)){
			//AN..20
			pattern=".{0,"+length.split("AN\\.\\.")[1]+"}";
		}else if(Pattern.matches("A\\.\\.\\d+", length)){
			//A..20
			pattern="\\D{0,"+length.split("A\\.\\.")[1]+"}";
		}else if(Pattern.matches("N\\.\\.\\d+", length)){
			//N..20
			pattern="\\d{0,"+length.split("N\\.\\.")[1]+"}";
		}else if(Pattern.matches("AN\\d+",length)){
			//AN20
			pattern=".{"+length.split("AN")[1]+"}";
		}else if(Pattern.matches("A\\d+",length)){
			//A20
			pattern="\\D{"+length.split("A")[1]+"}";
		}else if(Pattern.matches("N\\d+",length)){
			//N20
			pattern="\\d{"+length.split("N")[1]+"}";
		}else if(Pattern.matches("AN\\d+\\.\\.\\d+",length)){
			//AN6..16
			String[] arr=length.split("AN")[1].split("\\.\\.");
			pattern=".{"+arr[0]+","+arr[1]+"}";
		}else if(Pattern.matches("A\\d+\\.\\.\\d+",length)){
			//A6..16
			String[] arr=length.split("A")[1].split("\\.\\.");
			pattern="\\D{"+arr[0]+","+arr[1]+"}";
		}else if(Pattern.matches("N\\d+\\.\\.\\d+",length)){
			//N6..16
			String[] arr=length.split("N")[1].split("\\.\\.");
			pattern="\\d{"+arr[0]+","+arr[1]+"}";
		}else if(Pattern.matches("N\\d+\\,\\d+",length)){
			//N6,1
			String[] arr=length.split("N")[1].split("\\,");
			int max=Integer.parseInt(arr[0])-Integer.parseInt(arr[1])-1;
			pattern="\\d{1,"+max+"}\\.\\d{1,"+arr[1]+"}";
		}else if(Pattern.matches("N\\.\\.\\d+\\,\\d+",length)){
			//N..6,1
			String[] arr=length.split("N\\.\\.")[1].split("\\,");
			int max=Integer.parseInt(arr[0])-Integer.parseInt(arr[1])-1;
			pattern="\\d{1,"+max+"}\\.\\d{1,"+arr[1]+"}";
		}else if(Pattern.matches("N\\d+\\.\\.\\d+\\,\\d+",length)){
			//N3..6,1
			int min=Integer.parseInt(length.split("\\.\\.")[0].split("N")[1]);
			int max=Integer.parseInt(length.split("\\.\\.")[1].split(",")[0]);
			int dig=Integer.parseInt(length.split("\\.\\.")[1].split(",")[1]);
			pattern="\\d{"+(min-dig-1)+","+(max-dig-1)+"}\\.\\d{1,"+dig+"}";
		}else{
			for(Map<String, Object> map:list){
				String dataLength=S.obj2String(map.get("DataLength"));
				if(length.equals(dataLength)){
					String[] errInfo=new String[]{length,S.obj2String(map.get("StandardIdentify")),S.obj2String(map.get("CustomIdentify")),customIdentify};
					LOGGER.error("the length[{}] of data element[StandardIdentify:{},CustomIdentify:{}] in dataset[{}] is undefined",errInfo);
				}
			}
			return null;
		}
		//判断此类型如果为数值型，则允许为负数
		if(originalLength.endsWith("N")){
			pattern="[-]?"+pattern;
		}
		return pattern;
	}

	
}
