<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_CK01_RKQR" alias="出库单据(WL_CK01)" tableName="WL_CK01" sort="LZDH desc">
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="LZDH" alias="流转单号" length="30" width="130"/>
	<item id="ZBLB" alias="账簿类别" type="int" length="8">
	    <dic id="phis.dictionary.booksCategory"  autoLoad="true" />
	</item>
	<item id="LZFS" alias="流转方式" type="long" length="12">
		<dic id="phis.dictionary.transfermodes" autoLoad="true">
		</dic>  
	</item>
	<item id="KSDM" alias="出库科室" type="int" length="8">
		<dic id="phis.dictionary.department" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYCODE">
		</dic>
	</item>
	<item id="CKRQ" alias="出库日期" type="date"/>
	<item id="DJJE" alias="单据金额" type="double" length="18" precision="4"/>
	<item id="QRGH" alias="确认工号" length="10" display="0"/>
    <item id="DJZT" alias="单据状态" type="int" length="1" display="0"/>	
    <item id="QRRQ" alias="确认日期" type="date" display="0"/>
    <item id="QRBZ" alias="确认标志" type="int" length="1" display="0"/>
    <item id="THDJ" alias="退回单据" type="long" display="0" length="18">
	</item>
</entry>
