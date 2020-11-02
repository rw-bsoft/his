<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_ChildrenConfigDetail" alias="儿童随访参数">
  <item id="instanceType"  alias="方案类型" type="string" hidden="true" not-null="true" defaultValue="5" display="0"/>
  <item id="startMonth"  alias="起始月龄" type="String" virtual="true" not-null="true">
  	<dic id="chis.dictionary.childrenMonth"/>
  </item>	
  <item id="endMonth"  alias="终止月龄" type="String" virtual="true" not-null="true" >
    	<dic id="chis.dictionary.childrenMonth"/>
  </item>	
  <item id="planTypeCode"	 alias="计划类型" type="string" length="2" width="120" not-null="true">
  	<dic id="chis.dictionary.planTypeDic"  filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['eq',['$','item.properties.frequency'],['s','1']]]"/>
  </item>	
  <item id="expression" alias="条件表达式"  type="string" hidden="true" length="200" width="100" not-null="1"/>
  
</entry>
  	