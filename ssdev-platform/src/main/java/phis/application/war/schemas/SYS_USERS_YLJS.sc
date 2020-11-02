<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="SYS_Personnel" alias="用户档案">
	<item id="PERSONID" alias="用户名" type="string" length="50" not-null="true" pkey="true" queryable="true" />
	<item ref="b.JSXH" />
	<item ref="b.JSMC" />
	<item ref="b.JSLX" />
	<item ref="b.JSJB" />
	<item ref="b.JSSM" />
	<relations>
		<relation type="children" entryName="phis.application.emr.schemas.EMR_YLJS">
			<join parent="MEDICALROLES" child="JSXH" />
		</relation>
	</relations>
</entry>