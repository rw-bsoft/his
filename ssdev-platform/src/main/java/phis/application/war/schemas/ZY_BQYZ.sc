<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" />
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<item id="BRCH" alias="床号" length="12"/>
	<item ref="BRXM" width="120"/>
	<relations>
		<relation type="parent" entryName="phis.application.war.schemas.ZY_BRRY_BQ" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
