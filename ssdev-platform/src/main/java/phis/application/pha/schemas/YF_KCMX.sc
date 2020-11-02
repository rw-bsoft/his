<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_KCMX"  alias="药房库存帐" sort="a.YPXH">
	<item ref="b.YPMC" fixed="true"/>
	<item ref="b.YPMC" fixed="true"/>
	<item ref="b.PYDM" fixed="true" selected="true" queryable="true"/>
	<item ref="b.TYPE" fixed="true" display="0"  queryable="true" />
	<item ref="c.YFGG" fixed="true"/>
	<item ref="c.YFBZ" fixed="true" display="0"/>
	<item ref="c.YFDW" fixed="true"/>
	<item ref="d.CDMC" fixed="true" alias="药品产地"/>
	<item id="YPCD" alias="药品产地"  length="18" not-null="1" display="0" type="long" update="false">
		<dic id="medicinePlace"></dic>
	</item>
	
	<item id="LSJG" alias="零售价格" length="13" precision="4" type="double" max="999999.999999" not-null="1" fixed="true" renderer="onRenderer_four"/>
	<item id="LSJE" alias="零售金额" length="13" precision="4" type="double" max="99999999.9999" not-null="1" display="0" renderer="onRenderer_four"/>
	<item id="YPSL" alias="库存数量" length="9" precision="2" min="0" max="999999.99" type="double" not-null="1" />
	<item id="YPPH" alias="药品批号" type="string" length="20" />
	<item id="YPXQ" alias="药品效期" type="date" />
	<item id="JHJG" alias="进货价格" length="12" precision="4" type="double" not-null="1" max="999999.999999" fixed="true" renderer="onRenderer_four"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" type="double" not-null="1"  max="99999999.9999" fixed="true" renderer="onRenderer_four"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4" type="double"  max="999999.999999" fixed="true" renderer="onRenderer_four"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4" type="double"  max="99999999.9999" fixed="true" renderer="onRenderer_four"/>
	<item id="JGID" alias="机构ID" length="20" not-null="1" display="0"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
	</item>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" display="0" type="long"/>
	<item id="CKBH" alias="窗口编号" length="2" not-null="1" display="0" type="int"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1"  display="0" type="long"/>
	<item id="YKLJ" alias="药库临价" length="13" precision="4" type="double" max="999999.999999" display="0"/>
	<item id="YKJJ" alias="药库进价" length="13" precision="4" type="double" max="999999.999999" display="0"/>
	<item id="YKPJ" alias="药库批价" length="13" precision="4" type="double" max="999999.999999" display="0"/>
	<item id="YKKCSB" alias="药库库存识别" length="18"  display="0" type="long"/>
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
