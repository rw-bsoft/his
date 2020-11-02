<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_KSQX_CIC" tableName="YS_KSQX" alias="医生科室权限">
	<item id="JLXH" alias="记录序号" type="int" length="9" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="9" startPos="1" />
		</key>
	</item>
	<item id="YSDM" alias="医生代码" display="0" width="250" type="string" length="20" not-null="1"/>
	<item id="KSDM" alias="科室名称" type="string" length="6" not-null="1">
		<dic id="phis.dictionary.publicDepartment"/>
	</item>
	<item id="QXJB" alias="权限级别" type="string" length="18" display="0" not-null="1"/>
	<item id="MRBZ" alias="默认标志" type="string" length="1" display="0"/>
	<item id="KSLB" alias="科室类别" type="string" length="1" display="0" defaultValue="1"/>
	<item id="JGID" alias="机构ID" type="string" length="1" display="0" not-null="1" defaultValue="%user.properties.manaUnitId"/>
</entry>