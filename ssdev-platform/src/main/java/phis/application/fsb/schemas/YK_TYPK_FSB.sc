<?xml version="1.0" encoding="UTF-8"?>

<entry id="YK_TYPK_FSB" tableName="YK_TYPK" alias="药品信息" sort="a.YPXH desc" >
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品内码" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true"  layout="JBXX" >
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="8869" />
		</key>
	</item>
	<item id="TP" alias="" type="string" renderer="onRenderer" width="23" length="20" virtual="true" display="1"/>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="TYMC" alias="通用名" type="string" display="2"  width="180" anchor="100%"
		length="80" colspan="2"  layout="JBXX"/>
	<item id="YPGG" alias="规格" type="string" length="20"  layout="JBXX"/>
	<item id="YPDW" alias="单位" type="string" length="2" layout="JBXX" not-null="1"/>
	<item id="TYPE" alias="类别" display="2"  not-null="1" defaultValue="1"
		type="int" length="2" layout="JBXX" />
	<item id="JLDW" alias="剂量单位" type="string" length="8" display="2" layout="JBXX"/>
</entry>
