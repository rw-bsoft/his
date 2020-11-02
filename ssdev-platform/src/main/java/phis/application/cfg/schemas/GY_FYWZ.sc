<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_FYWZ" alias="费用执行科室">
	<item id="JLXH" alias="费用序号" type="long" length="18" generator="assigned"  display="0"  pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="15" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID"   not-null="true" length="20" display="0"/>
	<item id="FYXH" alias="费用序号"  type="long"  length="18" display="0"/>
	<item id="FYLB" alias="费用类别"   type="int" length="18" display="0"/>
	<item id="WZXH" alias="物资序号"  type="long"  length="18" display="0"/>
	<item ref="b.WZMC" fixed="true"/>
	<item ref="b.WZGG" fixed="true" width="60"/>
	<item ref="b.WZDW" fixed="true" width="60"/>
	<item id="WZSL" alias="数量"  type="double"  length="18"  max="999999999999999.99"/>
	<relations>
		<relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD">
			<join parent="WZXH" child="WZXH"></join>
		</relation>
	</relations>
</entry>
