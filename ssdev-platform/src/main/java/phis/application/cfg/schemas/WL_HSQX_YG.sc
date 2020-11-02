<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_HSQX_YG" tableName="WL_HSQX" alias="护士权限">
	<item id="YGID" alias="员工代码" length="10" display="0" not-null="1" pkey="true"/>
	<item id="KSDM" alias="科室代码" type="long" display="0" length="18" not-null="1" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="MRZ" alias="默认值" type="int" display="0" length="1"/>
	<item ref="b.PERSONNAME" alias="员工姓名" mode="remote" type="string" width="150" length="50" />
	<item id="HSZBZ" alias="护士长标志" type="int" width="100" length="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item ref="b.GENDER" alias="性别" type="string" display="1" width="90" length="4"/>
	<item ref="b.PERSONID" alias="登录用户名" fixed="true" type="string" length="10" not-null="1"/>
	<item ref="b.BIRTHDAY" alias="出生年月" display="1" type="date" width="100"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.SYS_Personnel" >
			<join parent="PERSONID" child="YGID"></join>
		</relation>				
	</relations>
</entry>
