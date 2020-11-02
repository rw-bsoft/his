<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TJ01"   alias="调价01" sort="a.TJDH desc">
	<item id="JGID" alias="机构ID"  type="string" length="20" not-null="1" defaultValue="%user.manageUnit.id" display="0" />
	<item id="XTSB" alias="药库识别" type="long" pkey="true" length="18" not-null="1"  defaultValue="1" display="0"/>
	<item id="TJFS" alias="调价方式" fixed="true"  pkey="true" length="2" not-null="1" type="int"  display="2"  queryable="true" selected="true" defaultValue="1">
		<dic id="phis.dictionary.priceAdjust"/>
	</item>
	<item id="TJDH" alias="调价单号" length="6"  type="int" not-null="1" display="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="TJRQ" alias="调价日期"  display="2"  fixed="true" type="datetime" defaultValue="%server.date.datetime"/>
	<item id="ZXRQ" alias="执行日期" type="datetime" width="130" fixed="true"/>
	<item id="TJWH" alias="调价文号" type="string" length="30" fixed="true"/>
</entry>
