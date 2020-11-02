<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YPXX_MS" tableName="YF_YPXX" alias="药房药品信息" sort="a.YPXH">
	<item ref="b.YPMC" width="180" fixed="true"/>
	<item id="YPXH" alias="药品序号" length="18" type="long"  display="0" not-null="1" generator="assigned" pkey="true" />
	<item id="YFGG" alias="规格" type="string" length="10"/>
	<item id="YFDW" alias="单位" type="string" length="2"/>
	<item ref="d.CFLX" alias="处方类型" type="int" length="2" display="0"/>
	<item ref="c.PYDM" queryable="true" hidden="true"/>
	<item ref="b.ZBLB" alias="账簿类别" type="long" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.YK_TYPK_CIC" />
		<relation type="child" entryName="phis.application.cic.schemas.YK_YPBM_CIC" >
			<join parent="YPXH" child="YPXH"/>
		</relation>
		<relation type="child" entryName="phis.application.cic.schemas.YK_YPXX_CIC" >
			<join parent="YPXH" child="YPXH"/>
		</relation>
	</relations>
</entry>
