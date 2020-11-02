<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_XTCS" alias="系统参数" sort="a.JGID, a.CSMC">
	<item id="JGID" alias="机构名称" length="20" fixed="true" pkey="true"
		width="120" display="1" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="phis.@manageUnit" render="Tree" parentKey="%user.manageUnit.id" />
	</item>
	<item id="CSMC" alias="参数名称" type="string" length="20" pkey="true"
		width="160" not-null="1" queryable="true" selected="true" update="false" />
	<item id="SSLB" alias="所属类别" type="string" width="160" length="500" >
		<dic id="phis.dictionary.businessType" render="TreeCheck" autoLoad="true" />
	</item>
	<item id="CSZ" alias="参数值" type="string" length="400" width="100" />
	<item id="MRZ" alias="业务类别" type="string" length="400" width="100" display="1"/>
	<item id="BZ" alias="备注" type="string" length="100" width="400"
		queryable="true" />
	<item id="XXSM" alias="详细说明" type="text" length="2000" colspan="2"
		display="2" />

</entry>
