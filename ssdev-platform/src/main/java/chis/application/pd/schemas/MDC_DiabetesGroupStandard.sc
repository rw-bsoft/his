<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pd.schemas.MDC_DiabetesGroupStandard" alias="糖尿病控制目标（标准）">
	<item id="standardId" alias="行标识" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="bloodType" alias="采血类型" type="string" length="1"
		queryable="true" not-null="1">
		<dic>
			<item key="1" text="静脉" />
			<item key="2" text="毛细血管" />
		</dic>
	</item>
	<item id="fbs1" alias="餐前血糖低值" type="double" length="6"
		precision="2" queryable="true" not-null="1" />
	<item id="fbs2" alias="餐前血糖高值" type="double" length="6"
		precision="2" queryable="true" not-null="1" />
	<item id="pbs1" alias="餐后血糖低值" type="double" length="6"
		precision="2" queryable="true" not-null="1"/>
	<item id="pbs2" alias="餐后血糖高值" type="double" length="6"
		precision="2" queryable="true" not-null="1"/>
	<item id="diabetesControl" alias="控制目标" type="string" length="2"
		queryable="true" not-null="1">
		<dic>
			<item key="01" text="一组" />
			<item key="02" text="二组" />
			<item key="03" text="三组" />
			<item key="99" text="一般管理对象" />
		</dic>
	</item>
</entry>
