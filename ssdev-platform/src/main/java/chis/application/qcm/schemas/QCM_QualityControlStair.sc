<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.qcm.schemas.QCM_QualityControlStair" alias="一级质控参数配置">
	<item id="autoabstract"  alias="开启门诊系统自动抽样" type="string">
		<dic>
			<item key="true" text="开启"/>
			<item key="false" text="不开启"/>
		</dic>
	</item>
	<item id="samplingMethod"  alias="抽样方式" type="String">
		<dic>
			<item key="1" text="按人数（上限）"/>
			<item key="2" text="按比例（%）"/>
		</dic>
	</item>	
	<item id="population"  alias="按人数（上限）" type="int" minValue="0" length="3">
	</item>	
	<item id="proportion"	 alias="按比例（%）" type="int" maxValue="100" length="3" minValue="0">
	</item>	
	<item id="inNextDays" alias="距最近一次随访日期的天数内"  type="string">
		<dic>
			<item key="true" text="开启"/>
			<item key="false" text="不开启"/>
		</dic>
	</item>
	<item id="oneDays" alias="一组天数"  type="int" minValue="0" length="3">
	</item>
	<item id="twoDays" alias="二组天数"  type="int" minValue="0" length="3">
	</item>
	<item id="threeDays" alias="三组天数"  type="int" minValue="0" length="3">
	</item>
	<item id="extractNoControl" alias="不再抽取本周期其他等级质控过的病人"  type="string">
		<dic>
			<item key="true" text="开启"/>
			<item key="false" text="不开启"/>
		</dic>
	</item>
</entry>