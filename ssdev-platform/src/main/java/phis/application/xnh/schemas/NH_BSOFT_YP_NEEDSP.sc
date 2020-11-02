<?xml version="1.0" encoding="UTF-8"?>

<entry id="NH_BSOFT_YP_NEEDSP" alias="待审批药品信息" tableName="NH_BSOFT_YP_NEEDSP" sort="a.YPXH desc" >
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品编码" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true"  layout="JBXX" >
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="8869" />
		</key>
	</item>
	<item id="YPMC" alias="药品通用名" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX" selected="true" queryable="true" />
	<item id="YPCD" alias="药品产地" type="long" layout="JBXX" pkey="true"/>
	<item id="CDMC" alias="产地名称" type="string" length="60" layout="JBXX" />
	<item id="YPGG" alias="药品规格"  length="32" layout="JBXX" />
	<item id="LSJG" alias="零售价格"   width="80" type="double"  length="12" max="999999.9999"  min="0" not-null="true" precision="4" defaultValue="0"/>
	<item id="NHBM_BSOFT" alias="农合编码" type="string" length="20" layout="JBXX" />
	<item id="JGID" alias="机构" type="string" length="20" layout="JBXX" hidden="true"/>
	<item id="ICODE" alias="药品编码系统" type="string" length="20" layout="JBXX" hidden="true"/>
</entry>
