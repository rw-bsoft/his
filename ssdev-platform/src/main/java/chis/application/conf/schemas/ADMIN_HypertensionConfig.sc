<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_HypertensionConfig" alias="高血压模块参数设置" >
	<item id="startMonth" alias="年度开始月份" type="int"  not-null="1" group="管理年度及生成方式">
		<dic id="chis.dictionary.month"/>
	</item>
	<item id="endMonth" alias="年度结束月份" type="int"  not-null="1" group="管理年度及生成方式">
		<dic id="chis.dictionary.month"/>
	</item>
	<item id="planMode" alias="生成方式" type="int"  not-null="1" group="管理年度及生成方式">
		<dic id="chis.dictionary.planModeNew"/>
	</item>
	<item id="precedeDays" alias="提前天数占比(%)"  type="int" minValue="0" not-null="1" group="管理年度及生成方式"/>
	<item id="delayDays" alias="延后天数占比(%)" type="int" minValue="0" not-null="1" group="管理年度及生成方式"/>
	<item id="planType1" alias="高血压一组"  defaultValue="05" expression="['and',['eq', ['s', 'group'], ['s', '01']],['eq', ['s', 'visitResult'], ['s', '1']]]" type="string" length="5"  width="100" not-null="1" group="计划类型">
		<dic id="chis.dictionary.planTypeDic"   filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['and',['eq',['$','item.properties.frequency'],['s','3']],['or',['eq',['$','item.properties.cycle'],['s','2']],['eq',['$','item.properties.cycle'],['s','3']],['eq',['$','item.properties.cycle'],['s','4']]]]]"/>
	</item>
	<item id="planType2" alias="高血压二组"  defaultValue="07"  expression="['and',['eq', ['s', 'group'], ['s', '02']],['eq', ['s', 'visitResult'], ['s', '1']]]" type="string" length="5" width="100" not-null="1" group="计划类型">
		<dic id="chis.dictionary.planTypeDic"   filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['and',['eq',['$','item.properties.frequency'],['s','3']],['or',['eq',['$','item.properties.cycle'],['s','2']],['eq',['$','item.properties.cycle'],['s','3']],['eq',['$','item.properties.cycle'],['s','4']]]]]"/>
	</item>
	<item id="planType3" alias="高血压三组"  defaultValue="10" expression="['and',['eq', ['s', 'group'], ['s', '03']],['eq', ['s', 'visitResult'], ['s', '1']]]" type="string" length="5" width="100" not-null="1" group="计划类型">
		<dic id="chis.dictionary.planTypeDic"   filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['and',['eq',['$','item.properties.frequency'],['s','3']],['or',['eq',['$','item.properties.cycle'],['s','2']],['eq',['$','item.properties.cycle'],['s','3']],['eq',['$','item.properties.cycle'],['s','4']]]]]"/>
	</item>
  
</entry>
