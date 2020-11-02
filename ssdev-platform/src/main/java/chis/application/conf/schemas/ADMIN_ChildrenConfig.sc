<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_ChildrenConfig" alias="儿童模块参数设置">
  <item id="instanceId"  alias="方案编号" hidden="true" type="string" 
    length="16" not-null="1" generator="assigned" pkey="true" display="0"/>
  <item id="childrenRegisterAge"  alias="建册截至年龄" type="int" virtual="true" maxValue="6"  not-null="true" />
  <item id="childrenDieAge"  alias="死亡登记截至年龄" type="int" virtual="true" maxValue="6" not-null="true"/>
  <item id="childrenFirstVistDays"  alias="首次随访天数" type="int" virtual="true"  maxValue="42" defaultValue="1"/>
  <item id="precedeDays" alias="提前天数占比(%)" type="int"  not-null="1" />
  <item id="delayDays" alias="延后天数占比(%)" type="int"  not-null="1"/>
  <item id="planTypeCode"	 alias="计划类型" type="string" length="2" not-null="true">
    <dic id="chis.dictionary.planTypeDic"  filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['eq',['$','item.properties.frequency'],['s','1']]]"/>
  </item>	
  <item id="visitIntervalSame"  alias="所有月龄随访间隔一致" type="string" xtype="checkbox" />
</entry>
  	