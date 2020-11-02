<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_PregnantConfig" alias="孕妇模块参数设置">
  <item id="planTypeCode"	 alias="计划类型" type="string" length="2" not-null="true">
  	<dic id="chis.dictionary.planTypeDic"  filter="['eq',['$','item.properties.frequency'],['s','3']]"/>
  </item>
   <item id="planMode" alias="生成方式" type="int"  not-null="1" >
     <dic id="chis.dictionary.planMode"/>
  </item>
<item id="precedeDays" alias="提前天数占比(%)" type="int"  not-null="1" />
  <item id="delayDays" alias="延后天数占比(%)" type="int"  not-null="1"/>
  <item id="visitIntervalSame"  alias="所有孕周随访间隔一致" type="string" xtype="checkbox" />
</entry>
  	