<?xml version="1.0" encoding="UTF-8"?>

<entry id="NH_BSOFT_ZL_NEEDSP" alias="待审批诊疗信息" tableName="NH_BSOFT_ZL_NEEDSP" sort="a.FYXH desc" >
	<!-- 药品基本信息 -->
	<item id="FYXH" alias="费用编码" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true"  layout="JBXX" >
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="8869" />
		</key>
	</item>
	<item id="FYMC" alias="费用名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX" selected="true" queryable="true" />
	<item id="FYDW" alias="费用单位"  length="32" layout="JBXX" />
	<item id="BZJG" alias="标准价格"   width="80" type="double"  length="12" max="999999.9999"  min="0" not-null="true" precision="4" defaultValue="0"/>
	<item id="NHBM_BSOFT" alias="农合编码" type="string" length="20" layout="JBXX" />
	<item id="JGID" alias="机构" type="string" length="20" layout="JBXX" hidden="true"/>
	<item id="ICODE" alias="药品编码系统" type="string" length="20" layout="JBXX" hidden="true"/>
</entry>
