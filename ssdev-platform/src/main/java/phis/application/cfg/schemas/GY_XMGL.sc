<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_XMGL" tableName="GY_XMGL" alias="附加项目关联" sort="a.KSDM asc">
	<item id="JLXH" alias="记录序号"  length="18" type="long" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000"/>
		</key>
	</item>
	<item id="XMXH" alias="项目序号"  type="long" not-null="1" display="0"/>
	<item id="GLXH" alias="关联序号"  type="long" not-null="1" display="0"/>
	<item ref="b.FYMC" alias="项目名称"  type="string" width="180" mode="remote" renderer="fymcRender"/>
	<item ref="b.FYDW" alias="费用单位" type="string"  display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20"  display="0"/>
	<item id="SYLB" alias="使用类别" length="1" type="int" defaultValue="2" display="0"/>
	<item id="KSDM" alias="使用科室" type="long" not-null="1" length="18" width="160" defaultValue="99">
		<dic id="phis.dictionary.department_leaf_wl" searchField="PYCODE" autoLoad="true" />
	</item>
	<item id="FYSL" alias="数量"  type="double" not-null="1" precision="2" min="1" max="999999.99" defaultValue="1" length="10"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.GY_YLSF" >
			<join parent="FYXH" child="GLXH"></join>
		</relation>
	</relations>
</entry>
