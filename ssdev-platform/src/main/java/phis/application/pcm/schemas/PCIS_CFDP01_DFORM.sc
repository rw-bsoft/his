<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PCIS_CFDP01" alias="抽样01_FORM">
	<item id="CYXH" alias="抽样序号" length="18" type="long"  generator="assigned" pkey="true"  display="0">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="KSRQ" alias="开始日期" type="String" fixed="true"/>
	<item id="JSRQ" alias="结束日期" type="String" fixed="true"/>
	<item id="CYFF" alias="抽样方法" length="8"   type="int" fixed="true">
		<dic>
			<item key="1" text="简单随机抽样"/>
		</dic>
	</item>
	<item id="CYSL" alias="抽样数量" length="10"   type="long" fixed="true"/>
	<item id="CYRQ" alias="抽样日期" type="String"  fixed="true"/>
	<item id="CYGH" alias="抽样人员" type="string" length="10"  fixed="true">
		<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="WCZT" alias="完成状态" length="1"   type="int" fixed="true">
		<dic>
			<item key="0" text="未完成"/>
			<item key="1" text="完成"/>
		</dic>
	</item>
</entry>
