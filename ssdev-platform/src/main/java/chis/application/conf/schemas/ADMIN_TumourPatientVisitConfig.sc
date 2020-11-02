<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_TumourPatientVisitConfig" alias="肿瘤患者随访参数设置">
  <item id="instanceId"  alias="方案编号" hidden="true" type="string" 
    length="16" not-null="1" generator="assigned" pkey="true" display="0"/>
  <item id="startMonth" alias="年度开始月份" type="string" not-null="true" virtual="true" >
    <dic id="chis.dictionary.month" />
  </item>
  <item id="endMonth" alias="年度结束月份" type="string" not-null="true" virtual="true" >
    <dic id="chis.dictionary.month" />
  </item>
  <item id="precedeDays" alias="提前天数" type="int"  not-null="1"   />
  <item id="delayDays" alias="延后天数" type="int"  not-null="1"  />
</entry>