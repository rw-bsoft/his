<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_BS01_FORM_CON"  tableName="WL_BS01" alias="报损单据(WL_BS01)">
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8" display="0">
		<dic id="phis.dictionary.wl_zblb"/>
	</item>
	<item id="LZDH" alias="流转单号" length="30" display="0" />
	<item id="LZFS" alias="流转方式" type="long" length="12"  not-null="1">
		<dic id="phis.dictionary.transfermodes" defaultIndex="0"
			filter="['and',['and',['and',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],
			['eq',['$','item.properties.DJLX'],['s','BS']]],
			['eq',['$','item.properties.YWLB'],['i',0]]],
			['eq',['$','item.properties.FSZT'],['i',1]]]" autoLoad="true">
		</dic> 
	</item>
	<item id="BSFS" alias="报损方式" type="int" length="1"  not-null="1">
		<dic defaultIndex="0">
			<item key="0" text="在库报损"/>
			<item key="1" text="科室报损"/>
		</dic>
	</item>
	<item id="BSKS" alias="报损科室" type="long" length="18">
		<dic id="phis.dictionary.department" filter = "['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" searchField="PYCODE">
		</dic>
	</item>
	<item id="ZDRQ" alias="制单日期" type="date" display="0" />
	<item id="ZDGH" alias="制单人员" length="10" display="0" >
		<dic id="phis.dictionary.doctor" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="SHRQ" alias="审核日期" type="date" display="0" />
	<item id="SHGH" alias="审核人员" length="10" display="0" />
	<item id="JZRQ" alias="记帐日期" type="date" display="0" />
	<item id="JZGH" alias="记帐人员" length="10" display="0" />

	<item id="JBGH" alias="经办人员" length="10" defaultValue="%user.userId" not-null="1">
		<dic id="phis.dictionary.doctor" filter ="['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true">
		</dic>
	</item>
	<item id="BSRQ" alias="报损日期" type="date"/>
	<item id="DJBZ" alias="单据备注" length="160"/>
	<item id="DJZT" alias="单据状态" type="int" length="1"  >
		<dic>
			<item key="0" text="制单"/>
			<item key="1" text="审核"/>
			<item key="2" text="记账"/>
		</dic>
	</item>
	<item id="DJJE" alias="单据金额" type="double" length="18" precision="4" display="0" /> 
</entry>

 
 
 