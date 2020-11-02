<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_ZK02_FORM" tableName="WL_ZK01" alias="转科单据(WL_ZK01)">
	<item id="DJXH" alias="单据序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8" display="0"/>
	<item id="LZDH" alias="流转单号" length="30" display="0"/>
	<item id="LZFS" alias="流转方式" type="long" length="12" not-null="1">
		<dic id="phis.dictionary.transfermodes" defaultIndex="0" filter = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],['eq',['$','item.properties.DJLX'],['s','ZK']]],['eq',['$','item.properties.YWLB'],['i',0]]],['eq',['$','item.properties.FSZT'],['i',1]]]" autoLoad="true">
		</dic>
	</item>
	<item id="ZCKS" alias="转出科室" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department" defaultIndex="0" filter = "['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="ZRKS" alias="转入科室" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department" defaultIndex="0" filter = "['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="ZDRQ" alias="制单日期" type="date" display="0"/>
	<item id="ZDGH" alias="制单人员" length="10" display="0"/>
	<item id="SHRQ" alias="审核日期" type="date" display="0"/>
	<item id="SHGH" alias="审核人员" length="10" display="0"/>
	<item id="JZRQ" alias="记帐日期" type="date" display="0"/>
	<item id="JZGH" alias="记帐人员" length="10" display="0"/>
	<item id="DJZT" alias="单据状态" type="int" length="1" display="0">
		<dic>
			<item key="0" text="制单"/>
			<item key="1" text="审核"/>
			<item key="2" text="记账"/>
		</dic>
	</item>
	<item id="ZRGH" alias="转入人员" length="10" not-null="1" defaultValue="%user.userId">
		<dic id="phis.dictionary.doctor" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="DJBZ" alias="单据备注" length="160" />
	<item id="DJJE" alias="单据金额" type="double" length="18" precision="4" display="0"/>
</entry>
