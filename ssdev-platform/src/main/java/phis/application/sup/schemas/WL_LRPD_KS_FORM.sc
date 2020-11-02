<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_LRPD_KS_FORM" tableName="WL_LRPD" alias="盘点录入单据(WL_LRPD)">
	<item id="LRXH" alias="录入单号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="PDXH" alias="盘点单据" type="long" display="0" length="18"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="ZDSJ" alias="生成时间" display="0" type="date"/>
	<item id="SCSJ" alias="上传时间" display="0" type="date"/>
	<item id="DJZT" alias="状态" type="int" display="0" length="1"/>
	<item id="PDGH" alias="盘点人员" defaultValue="%user.userId" fixed="true" length="10">
		<dic id="phis.dictionary.doctor" filter = "['ne',['$','item.properties.LOGOFF'],['s','1']]" autoLoad="true"/>	
	</item>
	<item id="FSMC" alias="方式名称" fixed="true" defaultValue="科室管理" virtual="true"/>
	<item id="PDFS" alias="盘点方式" type="int" display="0" defaultValue="2" length="1"/>
</entry>
