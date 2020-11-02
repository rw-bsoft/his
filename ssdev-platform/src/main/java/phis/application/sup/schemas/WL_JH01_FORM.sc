<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_JH01_FORM" tableName="WL_JH01" alias="计划单据(WL_JH01)">
	<item id="DJXH" alias="单据序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
    
	<item id="LZFS" alias="流转方式" type="long" length="12">
		<dic id="phis.dictionary.transfermodes" defaultIndex="0"
			filter = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],
			['eq',['$','item.properties.DJLX'],['s','JH']]],
			['eq',['$','item.properties.YWLB'],['i',0]]],
			['eq',['$','item.properties.FSZT'],['i',1]]]"
			autoLoad="true">
		</dic>  
	</item>
	<item id="DJBZ" alias="单据备注" length="160" width="200"/>
    
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
	<item id="LZDH" alias="流转单号" length="30" display="0"/>
	<item id="ZDRQ" alias="制单日期" type="date" display="0"/>
	<item id="ZDGH" alias="制单人员" length="10" display="0"/>
	<item id="SHRQ" alias="审核日期" type="date" display="0"/>
	<item id="SHGH" alias="审核人员" length="10" display="0"/>
	<item id="DJZT" alias="单据状态" type="int" length="1" display="0"/>
  
</entry>
