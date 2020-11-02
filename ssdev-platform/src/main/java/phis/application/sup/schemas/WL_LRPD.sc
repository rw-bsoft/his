<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_LRPD" alias="盘点录入单据(WL_LRPD)" sort="LRXH desc">
	<item id="LRXH" alias="录入单号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="PDXH" alias="盘点单据" type="long" display="0" length="18"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="PDGH" alias="盘点人员" type="string" length="10">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>	
	</item>
	<item id="ZDSJ" alias="生成时间" type="date"/>
	<item id="SCSJ" alias="上传时间" type="date"/>
	<item id="DJZT" alias="状态" type="int" length="1">
		<dic>
			<item key="0" text="制单"/>
			<item key="1" text="确认"/>
			<item key="2" text="记账"/>
		</dic>
	</item>
	<item id="PDFS" alias="盘点方式" type="int" display="0" length="1"/>
</entry>
