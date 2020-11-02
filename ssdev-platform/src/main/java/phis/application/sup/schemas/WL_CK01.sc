<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_CK01" alias="出库单据(WL_CK01)" sort="LZDH desc">
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
    
	<item id="LZDH" alias="流转单号" length="30" width="130" display="1"/>
	<item id="LZFS" alias="流转方式" type="long" length="12" readOnly="true">
		<dic id="phis.dictionary.transfermodes" autoLoad="true">
		</dic>  
	</item>
	<item id="CKFS" alias="出库方式" type="int" display="0" length="1"/>
	<item id="KSDM" alias="出库科室" type="int" length="8">
		<dic id="phis.dictionary.department" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYCODE">
		</dic>
	</item>
	<item id="SLGH" alias="申领人员" length="10" display="0"/>
	<item id="ZDRQ" alias="制单日期" type="date"/>
	<item id="CKRQ" alias="出库日期" type="date" />
	<item id="DJZT" alias="单据状态" type="int" length="1">
		<dic>
			<item key="0" text="制单"/>
			<item key="1" text="审核"/>
			<item key="2" text="记账"/>
		</dic>
	</item>
	<item id="DJJE" alias="单据金额" type="double" length="18" precision="4"/>
    
    
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="ZBLB" alias="帐薄类别" type="int" display="0" length="8"/>
	<item id="JBGH" alias="经办人员" length="10"  display="0"/>
	<item id="DJBZ" alias="单据备注" length="160" display="0" />
	<item id="CKKF" alias="出库库房" type="long" display="0" length="18"/>
	<item id="DJLX" alias="单据类型" type="int" display="0" length="1"/>
	<item id="JGFS" alias="加工方式" type="int" display="0" length="2"/>
	<item id="ZDGH" alias="制单人员" length="10" display="0"/>
	<item id="SHRQ" alias="审核日期" type="date" display="0"/>
	<item id="SHGH" alias="审核人员" length="10" display="0"/>
	<item id="JZRQ" alias="记帐日期" type="date" display="0"/>
	<item id="JZGH" alias="记帐人员" length="10" display="0"/>
	<item id="THDJ" alias="退回单据" type="long" display="0" length="18">
		<dic>
			<item key="0" text="制单"/>
			<item key="1" text="审核"/>
			<item key="2" text="记账"/>
		</dic>
	</item>
	<item id="PDDJ" alias="盘点单据" type="long" display="0" length="18"/>
	<item id="QRBZ" alias="确认标志" type="int" display="0" length="1"/>
	<item id="QRRK" alias="确认入库" type="long" display="0" length="12"/>
	<item id="QRRQ" alias="确认日期" type="date" display="0"/>
	<item id="QRGH" alias="确认工号" length="10" display="0"/>
	<item id="RKDJ" alias="入库单据" type="long" display="0" length="18"/>
</entry>
