<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_OldPeopleListConfig" alias="老年人模块参数设置" >
  <item id="instanceType"  alias="方案类型" type="string" hidden="true" not-null="true" defaultValue="4" display="0"/>
  <item id="oldPeopleStartAge" alias="起始年龄" virtual="true" not-null="true" width="100" minValue="1">
  </item> 
  <item id="oldPeopleEndAge" alias="结束年龄"  virtual="true" not-null="true" width="100" minValue="1">
  </item>

  <item id="planTypeCode" alias="计划类型"  xtype="textarea"  not-null="true" width="150">
    <dic id="chis.dictionary.planTypeDic"   filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['eq',['$','item.properties.frequency'],['s','1']]]"/>
  </item>
  <item id="expression" alias="条件表达式"  type="string" hidden="true" length="200" width="100" not-null="1"/>
</entry>
