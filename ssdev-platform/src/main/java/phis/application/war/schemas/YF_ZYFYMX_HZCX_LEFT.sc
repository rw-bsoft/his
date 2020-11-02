<?xml version="1.0" encoding="UTF-8"?>

<entry alias="住院病人发药明细"  entityName="YF_ZYFYMX">
	<item ref="b.YPMC" />
	<item id="YPGG" alias="药品规格" length="20"/>
	<item id="YFDW" alias="药房单位" length="4"/>
	<item id="YPSL" alias="数量" type="double" length="10" precision="2" not-null="1" />
	<item id="LSJE" alias="金额" type="double" length="12" precision="2" not-null="1" />
	<item ref="d.CDMC" display="0"/>
	<item id="YPDJ" alias="费用单价" type="double" length="12" precision="4" not-null="1" display="0"/>
	<item id="YPXH" alias="费用序号" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.YPSX" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<item id="YFSB" alias="药房识别" type="long" length="18" not-null="1" display="0"/>
	<relations>
		<relation type="child" entryName="YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="child" entryName="ZY_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
			<join parent="JGID" child="JGID"></join>
		</relation>
		<relation type="child" entryName="YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
