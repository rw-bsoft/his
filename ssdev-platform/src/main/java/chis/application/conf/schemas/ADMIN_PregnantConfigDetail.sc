<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_PregnantConfigDetail" alias="孕妇模块参数设置">
  <item id="instanceType"  alias="方案类型" type="string" hidden="true" not-null="true" defaultValue="8" display="0"/>
  <item id="startWeek"  alias="起始孕周" type="String" virtual="true" not-null="true">
  	<dic id="chis.dictionary.pregnantWeeks"/>
  </item>	
  <item id="endWeek"  alias="终止孕周" type="String" virtual="true" not-null="true" >
    	<dic id="chis.dictionary.pregnantWeeks"/>
  </item>	
  <item id="planTypeCode"	 alias="计划类型" type="string" length="2" width="120" not-null="true">
  <dic id="chis.dictionary.planTypeDic"  filter="['eq',['$','item.properties.frequency'],['s','3']]"/>
  </item>	
  <item id="expression" alias="条件表达式"  type="string" hidden="true" length="200" width="100" not-null="1"/>
</entry>