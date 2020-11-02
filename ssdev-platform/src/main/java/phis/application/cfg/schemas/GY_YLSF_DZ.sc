<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YLSF_DZ" alias="医疗收费_对照" sort="PYDM" tableName="GY_YLSF">
	<item id="FYXH" alias="费用序号"  length="18" type="int" not-null="1" display="0"  pkey="true">
	</item>
	<item id="FYMC" alias="费用名称"  type="string" length="80" width="220"/>
	<item id="FYGB" alias="费用归并" length="18" type="int" display="2" />
	<item id="FYDW" alias="单位" type="string" length="4"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" selected="true"  queryable="true"/>
	<item ref="c.ZFPB" display="0"/>
	<item ref="c.JGID" display="0" pkey="false"/>
    <relations>
		<relation type="child" entryName="phis.application.cfg.schemas.GY_FYBM" >
			<join parent="FYXH" child="FYXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.cfg.schemas.GY_YLMX">
			<join parent="FYXH" child="FYXH"></join>
		</relation>
	</relations>
	
</entry>
