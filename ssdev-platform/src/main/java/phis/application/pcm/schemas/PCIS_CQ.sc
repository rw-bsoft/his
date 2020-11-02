<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PCIS_CFDP01" alias="出库02">
	<item id="CFRQF" alias="处方日期" type="date" not-null="1"/>
	<item id="CFRQT" alias="至" type="date"  defaultValue="%server.date.date" not-null="1"/>
	<item id="CYFF" alias="抽样方法" type="int"  defaultValue="1" fixed="true">
		<dic>
			<item key="1" text="简单随机抽样"/>
		</dic>
	</item>
	<item id="CYSL" alias="抽样数量" type="int"  length="10" defaultValue="100" not-null="1" minValue="1"/>
</entry>
