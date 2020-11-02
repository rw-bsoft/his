<?xml version="1.0" encoding="UTF-8"?>

<entry id="GY_YLSF_WLDZ" alias="医疗收费_物流对照" sort="PYDM" tableName="GY_YLSF">
	<item id="FYXH" alias="费用序号"  length="18" type="int" not-null="1" display="0"  pkey="true">
	</item>
	<item id="FYMC" alias="费用名称"  type="string" length="80" width="180"/>
	<item id="FYGB" alias="费用归并" length="18" type="int" display="0" >
		<dic id="phis.dictionary.chargesCollectable" autoLoad="true" filter="['ne',['$','item.properties.FYFL'],['i',2]]"/>
	</item>
	<item id="FYDW" alias="单位" type="string" length="4"/>
	<item ref="b.FYDJ" />
	<item ref="b.JGID" display="0"/>
    <relations>
		<relation type="child" entryName="phis.application.cfg.schemas.GY_YLMX">
			<join parent="FYXH" child="FYXH"></join>
		</relation>
	</relations>
	
</entry>
