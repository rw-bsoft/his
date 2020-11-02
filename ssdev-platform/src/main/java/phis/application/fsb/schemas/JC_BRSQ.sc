<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRSQ" alias="家床申请">
	<item id="ID" alias="家床申请ID" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="LXR" alias="联系人" type="string" length="40"/>
	<item id="YHGX" alias="与患关系" type="string">
		<dic id="phis.dictionary.GB_T4761" />
	</item>
	<item id="LXDH" alias="联系电话" type="string"/>
	<item id="SQRQ" alias="申请日期" minValue="%server.date.date" defaultValue="%server.date.date" type="date"/>
	
	<item id="ZDRQ" alias="诊断日期" type="date" fixed="true" />
	<item id="SQYS" alias="申请医生" type="string" display="2" not-null="1" length="20" defaultValue="%user.userId" >
		<dic id="phis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.properties.manaUnitId" rootVisible="true" />
	</item>
	<item id="BQZY" alias="病情摘要" type="string" display="2" not-null="1" colspan="3" xtype="textarea" length="2000"/>
	<item id="JCYJ" alias="收治指征和建床意见" type="string" display="2" not-null="1" colspan="3" xtype="textarea" length="2000"/>
	<item id="SQZT" alias="状态" type="string" display="0" defaultValue="2">
		<dic id="phis.dictionary.fsb_sqzt"/>
	</item>
	<item id="BRID" alias="病人ID" type="long" display="0"/>
	<item id="JGID" alias="机构ID" length="8" display="0" not-null="1" type="string"/>
</entry>
