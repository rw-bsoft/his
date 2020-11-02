<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="BQ_TYMX"  alias="病人入院">
	<item id="TYBQ" alias="退药病区" length="18" type="long" display="0"/>
	<item ref="b.ZYH" display="0"/>
	<item ref="b.BRCH"/>
	<item ref="b.BRXM" />
	<relations>
		<relation type="child" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="JGID" child="JGID"></join>
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
