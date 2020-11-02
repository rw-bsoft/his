<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_TYMX"   alias="家床退药明细">
	<item ref="b.BRXM" alias="病人姓名" type="string" queryable="false"/>
	<item ref="b.ZYHM" alias="家床号" type="string" queryable="false"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.fsb.schemas.JC_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
