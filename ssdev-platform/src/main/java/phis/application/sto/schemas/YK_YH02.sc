<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YH02" alias="养护02" sort="a.KCSB">
	<item ref="b.YPMC" fixed="true" width="160"/>
	<item ref="b.YPGG" fixed="true"/>
	<item ref="b.PYDM" fixed="true"/>
	<item ref="b.YPDW" fixed="true"/>
	<item ref="c.CDMC" fixed="true"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" not-null="1" min="0" max="999999.9999" type="double" fixed="true"/>
	<item id="JHJG" alias="进货价格" length="12" precision="4" not-null="1" min="0" max="999999.9999" type="double" fixed="true"/>
	<item id="YPPH" alias="批号" type="string" length="20" fixed="true"/>
	<item id="YPXQ" alias="效期" type="date" fixed="true"/>
	<item id="SHSL" alias="损坏数量" length="10" precision="2" not-null="1" min="0" max="999999.99" type="double" />
	<item id="TYPE" alias="损坏原因" length="6" type="int"  defaultValue="2">
		<dic>
			<item key="2" text="次品"/>
			<item key="3" text="伪劣"/>
			<item key="4" text="破损"/>
			<item key="5" text="霉变"/>
		</dic>
	</item>
	<item id="KCSL" alias="合格库存" length="10" precision="2" not-null="1" min="0" max="999999.99" type="double" fixed="true"/>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0" defaultValue="%user.manageUnit.id" />
	<item id="SBXH" alias="识别序号" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" type="long" display="0"/>
	<item id="YHDH" alias="养护单号" length="12" not-null="1"  type="string" display="0"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long" display="0"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long" display="0"/>
	<item id="JHRQ" alias="进货日期" type="datetime" display="0"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4"  min="0" max="999999.9999" type="double" display="0" defaultValue="0"/>
    <item id="JHJE" alias="进货金额" length="12" precision="4" not-null="1" min="0" max="99999999.99" type="double" fixed="true" display="0"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  type="double" display="0" defaultValue="0"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" not-null="1" type="double" fixed="true" display="0"/>
	<item id="BZLJ" alias="标准零价" length="12" precision="6" not-null="1" type="double" display="0"/>
    <item id="KCSB" alias="库存识别" length="18" not-null="1" type="long"  defaultValue="0" display="0"/>
    <item id="YKKC" alias="药库库存" type="double" virtual="true" display="0"/>
    <relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
