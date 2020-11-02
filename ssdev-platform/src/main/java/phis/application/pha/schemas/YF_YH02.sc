<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YH02" alias="养护02" sort="a.KCSB">
	<item ref="b.YPMC" fixed="true" width="120" queryable="true"/>
	<item ref="b.YPGG" fixed="true"/>
	<item ref="b.YPDW" fixed="true"/>
	<item ref="c.CDMC" fixed="true"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4" min="0" max="999999.9999" type="double" defaultValue="0" fixed="true"/>
	<item id="KCSL" alias="库存数量" length="10" precision="2" min="0" max="999999.99" type="double" fixed="true"/>
	<item id="YPPH" alias="批号" type="string" length="20" fixed="true"/>
	<item id="YPXQ" alias="效期" type="date" fixed="true"/>
	<item id="FXWTYPJCL" alias="发现问题药品及处理" type="string" length="100" width="120"/>
		
		
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
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long"  defaultValue="0" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
