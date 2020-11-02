<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_KCMX"  alias="药房库存_记账" >
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item ref="b.YPMC" fixed="true"/>
	<item ref="c.YFGG" fixed="true"/>
	<item ref="c.YFDW" fixed="true"/>
	<item ref="d.CDMC" fixed="true" alias="药品产地"/>
	<item id="YPCD" alias="药品产地"  length="18" not-null="1" display="0" type="long" update="false">
		<dic id="phis.dictionary.medicinePlace"></dic>
	</item>
	
	<item id="LSJG" alias="零售价格" length="13" precision="4" type="double" max="999999.999999" not-null="1" fixed="true" renderer="onRenderer_four"/>
	<item id="YPSL" alias="库存数量" length="9" precision="2" min="0" max="999999.99" type="double" not-null="1" />
	<item id="YPPH" alias="药品批号" type="string" length="20" />
	<item id="YPXQ" alias="药品效期" type="datetime" />
	<item id="JHJG" alias="进货价格" length="12" precision="4" type="double" not-null="1" max="999999.999999" fixed="true" renderer="onRenderer_four"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1"  display="0" type="long"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" display="0" type="long"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.pha.schemas.YF_YPXX" >
			<join parent="YPXH" child="YPXH"></join>
			<join parent="YFSB" child="YFSB"></join>
		</relation>
		<relation type="child" entryName="phis.application.mds.schemas.YK_CDDZ_YK" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
