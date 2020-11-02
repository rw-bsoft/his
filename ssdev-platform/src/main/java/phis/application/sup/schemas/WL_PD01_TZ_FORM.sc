<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_PD01_KS_FORM" tableName="WL_PD01" alias="盘点单据(WL_PD01)">
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="LZFS" alias="流转方式" type="long" not-null="1" length="12">
		<dic id="phis.dictionary.transfermodes" defaultIndex="0" filter = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],['eq',['$','item.properties.DJLX'],['s','PD']]],['eq',['$','item.properties.YWLB'],['i',0]]],['eq',['$','item.properties.FSZT'],['i',1]]]" autoLoad="true">
		</dic>
	</item>
	<item id="FSMC" alias="方式名称" fixed="true" defaultValue="台帐管理" virtual="true"/>
	<item id="GLFS" alias="盘点方式" display="0"  defaultValue="3"/>
	<item id="LZDH" alias="流转单号" length="30" display="0"/>
	<item id="DJZT" alias="单据状态" type="int" display="0" length="1"/>
	<item id="ZDGH" alias="制单人员" length="10" display="0"/>
	<item id="ZDRQ" alias="制单日期" type="date" display="0"/>
	<item id="SHGH" alias="审核人员" length="10" display="0"/>
	<item id="SHRQ" alias="审核日期" type="date" display="0"/>
	<item id="JZGH" alias="记帐人员" length="10" display="0"/>
	<item id="JZRQ" alias="记帐日期" type="date" display="0"/>
	<item id="DJBZ" alias="单据备注" length="160"/>
</entry>
