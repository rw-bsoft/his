<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.ADMIN_RVCFormConfig" alias="离休干部模块参数设置" >
  
	<item id="RVCStartMonth" alias="年度开始月份"  not-null="1" width="50">
		<dic id="chis.dictionary.month"/>
	</item> 
	<item id="RVCEndMonth" alias="年度结束月份"  not-null="1" width="50">
		<dic id="chis.dictionary.month"/>
	</item>
	<item id="RVCPlanType" alias="计划类型"   not-null="1" width="50">
		<dic id="chis.dictionary.planTypeDic"    filter="['or',['eq',['$','item.properties.frequency'],['s','2']],['eq',['$','item.properties.frequency'],['s','1']]]"/>
	</item>
	<item id="precedeDays" alias="提前天数占比(%)" type="int"  not-null="1" />
	<item id="delayDays" alias="延后天数占比(%)" type="int"  not-null="1"/>
	<item id="planMode" alias="生成方式" type="int"  not-null="1" >
		<dic id="chis.dictionary.planModeNew"/>
	</item>	
	  
</entry>
