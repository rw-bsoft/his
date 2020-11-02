<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_DiabetesConfig" alias="糖尿病模块参数设置" >
	<item id="startMonth" alias="年度开始月份" type="int"  not-null="1" group="管理年度及生成方式">
		<dic id="chis.dictionary.month"/>
	</item>
	<item id="endMonth" alias="年度结束月份" type="int"  not-null="1" group="管理年度及生成方式">
		<dic id="chis.dictionary.month"/>
	</item>
	<item id="planMode" alias="生成方式" type="int"  not-null="1" group="管理年度及生成方式">
		<dic id="chis.dictionary.planModeNew"/>
	</item> 
	<item id="precedeDays" alias="提前天数占比(%)"  type="int"  not-null="1"  group="管理年度及生成方式"/>
	<item id="delayDays" alias="延后天数占比(%)" type="int"  not-null="1" group="管理年度及生成方式"/>
	<item id="manageType" alias="管理方式" type="string"  not-null="1" group="管理年度及生成方式">
		<dic>
			<item key="1" text="一组管理"/>
			<item key="2" text="分组管理"/>
		</dic>
	</item>
	<item id="planType1" alias="糖尿病一组" defaultValue="05"  expression="['and',['eq', ['s', 'group'], ['s', '01']],['eq', ['s', 'visitResult'], ['s', '1']]]" type="string"  length="5"  width="100" not-null="1" group="分组计划">
		<dic id="chis.dictionary.planTypeDic"   filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['and',['eq',['$','item.properties.frequency'],['s','3']],['or',['eq',['$','item.properties.cycle'],['s','2']],['eq',['$','item.properties.cycle'],['s','3']],['eq',['$','item.properties.cycle'],['s','4']]]]]"/>
	</item>
	<!-- 
		  <item id="planTypeCode" alias="默认计划周期" type="string"  length="5"  width="100" not-null="1" group="默认计划周期">
			<dic id="planTypeDic"  filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['and',['eq',['$','item.properties.frequency'],['s','3']],['or',['eq',['$map',['s','cycle']],['s','3']],['eq',['$map',['s','cycle']],['s','4']]]]]"/>
		  </item>
		   -->
	<item id="planType2" alias="糖尿病二组" defaultValue="07"  expression="['and',['eq', ['s', 'group'], ['s', '02']],['eq', ['s', 'visitResult'], ['s', '1']]]" type="string" length="5" width="100"  group="分组计划">
		<dic id="chis.dictionary.planTypeDic"   filter="['eq',['$','item.properties.frequency'],['s','2']]"/>
	</item>
	<item id="planType3" alias="糖尿病三组" defaultValue="10"  expression="['and',['eq', ['s', 'group'], ['s', '03']],['eq', ['s', 'visitResult'], ['s', '1']]]" type="string" length="5" width="100"  group="分组计划">
		<dic id="chis.dictionary.planTypeDic"   filter="['eq',['$','item.properties.frequency'],['s','2']]"/>
	</item>
	<item id="configType" alias="分组标准" colspan="3" virtual="true" type="string" length="5" width="100" group="分组标准">
	</item>
</entry>
