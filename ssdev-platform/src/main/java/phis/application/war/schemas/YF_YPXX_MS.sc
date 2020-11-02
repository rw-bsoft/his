<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YPXX" alias="药房药品信息" sort="a.YPXH">
	<item ref="b.YPMC" width="180" fixed="true"/>
	<item id="YPXH" alias="药品序号" length="18" type="long"  display="0" not-null="1" generator="assigned" pkey="true" />
	<item id="YFGG" alias="规格" type="string" length="10"/>
	<item id="YFDW" alias="单位" type="string" length="2"/>
	<item ref="d.CFLX" alias="处方类型" type="int" length="2" display="0"/>
	<item ref="c.PYDM" queryable="true" hidden="true"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK">
			<join parent="YPXH" child="YPXH"/>
		</relation>
		<relation type="child" entryName="phis.application.mds.schemas.YK_YPBM" >
			<join parent="YPXH" child="YPXH"/>
		</relation>
		<relation type="child" entryName="phis.application.sto.schemas.YK_YPXX" >
			<join parent="YPXH" child="YPXH"/>
		</relation>
	</relations>
</entry>
