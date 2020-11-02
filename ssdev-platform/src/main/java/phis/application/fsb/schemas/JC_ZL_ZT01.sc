<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_ZL_ZT01" alias="诊疗计划模版名称">
	<item id="ZTBH" type="long" length="18" not-null="1" generator="assigned"
		display="0" pkey="true">
		<key>
			<rule type="sequence" />
		</key>
	</item>
	<item id="ZTMC" alias="计划名称" length="20" not-null="1" width="140" />
	<item id="PYDM" alias="拼音码" length="10" display="2">
		<set type="exp" run="server">['py',['$','r.ZTMC']]</set>
	</item>
	<item id="SFQY" alias="启用" type="int" length="1" width="60"
		renderer="onRenderer" display="1" />
	<item id="JGID" alias="机构编码" length="20" display="0"
		defaultValue="%user.manageUnitId" />
	<item id="YGDM" alias="员工代码" length="20" display="0" defaultValue="%user.userId" />
</entry>
