<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_HSQX_KS" tableName="WL_HSQX" alias="护士权限">
	<item id="YGID" alias="员工代码" length="10" display="0" not-null="1" pkey="true"/>
	<item id="KSDM" alias="科室代码" type="long" display="0" length="18" not-null="1" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item ref="b.OFFICENAME" alias="选择科室" width="120" type="string" length="50" />
	<item id="MRZ" alias="默认值" type="int" length="1" renderer="onRenderer"/>
	<item id="HSZBZ" alias="护士长标志" type="int" display="0" length="1"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.WL_Office" >
			<join parent="ID" child="KSDM"></join>
		</relation>					
	</relations>
</entry>
