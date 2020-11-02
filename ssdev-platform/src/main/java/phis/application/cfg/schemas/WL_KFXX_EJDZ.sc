<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_KFXX_EJDZ" tableName="WL_KFXX" alias="库房信息" sort="SXH">
	<item id="KFXH" alias="库房序号" type="long" length="8" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" defaultValue="%user.manageUnit.id"/>
	<item id="KFMC" alias="库房名称" not-null="1" width="160" length="40" queryable="true" />
	<item id="KFZT" alias="库房状态" fixed="true" type="int" length="1" defaultValue="0">
		<dic id="phis.dictionary.qyzt"/>
	</item>
</entry>
