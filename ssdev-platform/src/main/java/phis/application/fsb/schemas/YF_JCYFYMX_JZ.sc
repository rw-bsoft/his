<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_JCFYMX" alias="家床病人记账明细"  >
	<item id="KCSB" alias="库存识别" type="long" length="18" pkey="true"/>
	<item ref="b.YPMC" type="string" />
	<item id="YPGG" alias="药品规格" type="string" length="20"/>
	<item id="YFDW" alias="药房单位" type="string" length="4"/>
	<item id="LSJG" alias="零售价格" type="double" length="12" precision="4" not-null="1" />
	<item id="JHJG" alias="进货价格" type="double" length="12" precision="4" not-null="1" />
	<item id="YPPH" alias="批号" type="string" length="20"/>
	<item id="YPXQ" alias="效期" type="date" />
	<relations>
		<relation type="child" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>
