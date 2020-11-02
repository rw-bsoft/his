<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_PD01" alias="盘点单据(WL_PD01)" sort="DJXH desc"> 
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="GLFS" alias="管理方式" type="int" display="0" length="1"/>
	<item id="LZDH" alias="流转单号" type="string" length="30" width="120"/>
	<item id="LZFS" alias="流转方式" type="long" length="12">
		<dic id="phis.dictionary.transfermodes" filter = "['and',['and',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],['eq',['$','item.properties.DJLX'],['s','PD']]],['eq',['$','item.properties.YWLB'],['i',0]]]" autoLoad="true">
		</dic>
	</item>
	<item id="DJZT" alias="单据状态" type="int" length="1">
		<dic>
			<item key="0" text="制单"/>
			<item key="1" text="审核"/>
			<item key="2" text="记账"/>
		</dic>
	</item>
	<item id="ZDGH" alias="制单人员" type="string" length="10">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>	
	</item>
	<item id="ZDRQ" alias="制单日期" type="date"/>
	<item id="SHGH" alias="审核人员" type="string" length="10">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>	
	</item>
	<item id="SHRQ" alias="审核日期" type="date"/>
	<item id="JZGH" alias="记帐人员" type="string" length="10">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>	
	</item>
	<item id="JZRQ" alias="记帐日期" type="date"/>
	<item id="DJBZ" alias="单据备注" type="string" length="160" display="0"/>
</entry>
