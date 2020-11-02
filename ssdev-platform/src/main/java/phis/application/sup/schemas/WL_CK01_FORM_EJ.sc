<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_CK01_FORM" tableName="WL_CK01" alias="出库单据(WL_CK01)">
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string"  display="0" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="ZBLB" alias="帐薄类别" type="int" display="0" length="8"/>
	<item id="LZDH" alias="流转单号" length="30" display="0"/>
	<item id="LZFS" alias="流转方式" type="long" length="12" not-null="1">
		<dic id="phis.dictionary.transfermodes">
		</dic>
	</item>
	<item id="CKRQ" alias="出库日期" type="date" fixed="true"/>
	<item id="KSDM" alias="出库科室" type="int" length="8" fixed="true">
		<dic id="phis.dictionary.department" filter = "['and',['eq',['$','item.properties.ORGANIZCODE']],['$','%user.manageUnit.id']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" searchField="PYCODE">
		</dic>
	</item>
	<item id="CKKF" alias="出库库房" type="long" display="0" length="18"/>
	<item id="ZDRQ" alias="制单日期" type="date" display="0"/>
	<item id="JBGH" alias="经办人员" length="10" fixed="true">
		<dic id="phis.dictionary.doctor" filter = "['and',['eq',['$','item.properties.ORGANIZCODE']],['$','%user.manageUnit.id']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" searchField="PYCODE">
		</dic>
	</item>
	<item id="QRBZ" alias="确认标志" type="int" length="1" fixed="true">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="DJLX" alias="单据类型" type="int" display="0" length="1"/>
	<item id="JGFS" alias="加工方式" type="int" display="0" length="2"/>
	<item id="ZDGH" alias="制单人员" length="10" display="0"/>
	<item id="SHRQ" alias="审核日期" type="date" display="0"/>
	<item id="SHGH" alias="审核人员" length="10" display="0"/>
	<item id="JZRQ" alias="记帐日期" type="date" display="0"/>
	<item id="JZGH" alias="记帐人员" length="10" display="0"/>
	<item id="DJZT" alias="单据状态" type="int" length="1" display="0"/>
	<item id="THDJ" alias="退回单据" type="long" display="0" length="18"/>
	<item id="DJBZ" alias="单据备注" length="160" fixed="true"/>
	<item id="PDDJ" alias="盘点单据" type="long" display="0" length="18"/>
	<item id="CKFS" alias="出库方式" type="int" display="0" length="1"/>
	<item id="SLGH" alias="申领工号" length="10" display="0"/>
	<item id="DJJE" alias="单据金额" type="double" display="0" length="18" precision="4"/>
	<item id="QRRK" alias="确认入库" type="long"  length="12"/>
	<item id="QRRQ" alias="确认日期" type="date" display="0"/>
	<item id="QRGH" alias="确认工号" length="10" display="0"/>
	<item id="RKDJ" alias="入库单据" type="long" display="0" length="18"/>
</entry>
