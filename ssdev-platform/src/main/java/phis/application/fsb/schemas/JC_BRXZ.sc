<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRXZ" alias="病人入院">
	<item id="JSLX" alias="结算类型" defaultValue="5">
		<dic autoLoad="true">
			<item key="5" text="正常结算"/>
			<item key="10" text="取消结算"/>
		</dic>
	</item>
	<item id="ZYHM" alias="住院号码" length="10" not-null="1"/>
	<item id="BRCH" alias="病人床号" length="12"/>
	<item id="BRXM" alias="病人姓名" length="40" fixed="true" not-null="1"/>
	<item id="BRXZ" alias="病人性质" type="long" fixed="true" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="JSRQ" alias="结算日期" type="date" defaultValue="%server.date.date"/>
</entry>
