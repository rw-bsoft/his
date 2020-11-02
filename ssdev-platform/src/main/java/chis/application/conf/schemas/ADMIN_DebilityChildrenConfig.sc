<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_DebilityChildrenConfig" alias="体弱儿模块参数设置">
  <item id="planTypeCode"	 alias="计划类型" type="string" length="2" not-null="true" >
  	<dic id="chis.dictionary.planTypeDic"  filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['eq',['$','item.properties.frequency'],['s','3']]]"/>
  </item>
<item id="precedeDays" alias="提前天数占比(%)" type="int"  not-null="1" />
  <item id="delayDays" alias="延后天数占比(%)" type="int"  not-null="1"/>
 <item id="exceptionalCase"  alias="例外情况" type="string" xtype="checkbox" />
</entry>
  	