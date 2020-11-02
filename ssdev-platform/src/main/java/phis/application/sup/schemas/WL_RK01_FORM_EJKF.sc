<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_RK01_FORM_EJKF" tableName="WL_RK01" alias="入库单据(WL_RK01)">
	<item id="DJXH" alias="单据序号" type="long" length="18" display="1" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" defaultValue="%user.manageUnit.id"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="ZBLB" alias="帐薄类别" type="int" display="0" length="8"/>
	<item id="LZDH" alias="流转单号" display="0" length="30"/>
	<item id="LZFS" alias="流转方式" type="long" length="12" not-null="1">
		<dic id="phis.dictionary.transfermodes" >
		</dic>
	</item>
	<item id="DWXH" alias="供货单位" type="long" length="12" display="0">
		<dic id="phis.dictionary.supplyUnit" filter="['and',['eq',['$','item.properties.DWZT'],['i',1]],['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']]]" autoLoad="true" searchField="PYDM">
		</dic> 
	</item>
	<item id="RKRQ" alias="入库日期" type="date" defaultValue="%server.date.date" not-null="1"/>
	<item id="JBGH" alias="经办人" length="10" defaultValue="%user.userId">
		<dic id="phis.dictionary.doctor" filter="['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]"  autoLoad="true">
		</dic>
	</item>
	<item id="DJBZ" alias="单据备注" length="160"/>
	<item id="JGFS" alias="加工方式" type="int" display="0" length="2"/>
	<item id="DJJE" alias="单据金额" display="0" type="double" length="18" precision="4"/>
	<item id="YFJE" alias="应付金额" display="0" type="double" length="18" precision="4"/>
	<item id="FKJE" alias="付款金额" display="0" type="double" length="18" precision="4"/>
	<item id="FPBZ" alias="发票标志" display="0" type="int" length="1"/>
	<item id="DJLX" alias="单据类型" display="0" type="int" length="1"/>
	<item id="ZDRQ" alias="制单日期" display="0" type="date"/>
	<item id="ZDGH" alias="制单人员" display="0" length="10"/>
	<item id="SHRQ" alias="审核日期" display="0" type="date"/>
	<item id="SHGH" alias="审核人员" display="0" length="10"/>
	<item id="JZRQ" alias="记帐日期" display="0" type="date"/>
	<item id="JZGH" alias="记帐人员" display="0" length="10"/>
	<item id="DJZT" alias="单据状态" display="0" type="int" length="1"/>
	<item id="THDJ" alias="退回单据" type="long" length="18" defaultValue="0" display="0"/>
	<item id="PDDJ" alias="盘点单据" display="0" type="long" length="18"/>
	<item id="DRBZ" alias="导入标志" display="0" type="int" length="1"/>
	<item id="CWBH" alias="财务编号" display="0" type="long" length="18"/>
</entry>
