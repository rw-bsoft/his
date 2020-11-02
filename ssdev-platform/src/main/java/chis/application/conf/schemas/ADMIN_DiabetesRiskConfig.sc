<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_DiabetesRiskConfig" alias="糖尿病高危人群模块参数设置" >
  <item id="startMonth" alias="年度开始月份" type="int"  not-null="1" group="管理年度及生成方式" >
    <dic id="chis.dictionary.month"/>
  </item>
  <item id="endMonth" alias="年度结束月份" type="int"  not-null="1" group="管理年度及生成方式" >
    <dic id="chis.dictionary.month"/>
  </item>
  <item id="planMode" alias="生成方式" type="int"  not-null="1" group="管理年度及生成方式" defaultValue="1" display="0">
    <dic id="chis.dictionary.planMode"/>
  </item> 
  <item id="planType1" alias="随访间隔"  type="string"  length="5"  width="100" not-null="1"  group="管理年度及生成方式" >
    <dic id="chis.dictionary.planTypeDic"   />
  </item>
  
  <item id="precedeDays" alias="提前天数"  type="int"  not-null="1"  group="管理年度及生成方式" />
  <item id="delayDays" alias="延后天数" type="int"  not-null="1"  group="管理年度及生成方式" />
  
  <!-- 
  <item id="planTypeCode" alias="默认计划周期" type="string"  length="5"  width="100" not-null="1" group="默认计划周期">
    <dic id="planTypeDic"  filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['and',['eq',['$','item.properties.frequency'],['s','3']],['or',['eq',['$map',['s','cycle']],['s','3']],['eq',['$map',['s','cycle']],['s','4']]]]]"/>
  </item>
   -->
   <!-- 
  <item id="planType2" alias="糖尿病二组"  expression="['and',['eq', ['s', 'group'], ['s', '02']],['eq', ['s', 'visitResult'], ['s', '1']]]" type="string" length="5" width="100"  group="糖尿病二组">
  <dic id="planTypeDic"   filter="['eq',['$','item.properties.frequency'],['s','2']]"/>
  </item>
  <item id="planType3" alias="糖尿病三组"  expression="['and',['eq', ['s', 'group'], ['s', '03']],['eq', ['s', 'visitResult'], ['s', '1']]]" type="string" length="5" width="100"  group="糖尿病三组">
    <dic id="planTypeDic"   filter="['eq',['$','item.properties.frequency'],['s','2']]"/>
  </item>
  -->
</entry>
