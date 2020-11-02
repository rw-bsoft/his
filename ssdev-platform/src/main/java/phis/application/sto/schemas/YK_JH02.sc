<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_JH02" alias="计划02">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" not-null="1" type="string" display="0" defaultValue="%user.manageUnit.id"/>
	<item ref="b.YPMC" mode="remote"/>
	<item ref="b.YPGG" fixed="true"/>
	<item ref="b.YPDW" fixed="true"/>
	<item ref="c.CDMC" fixed="true"/>
	<item id="KCSL" alias="库存数量" type="double" precision="2" virtual="true" display="1" fixed="true"/>
	<item id="GJJG" alias="参考价格" length="12" precision="4" not-null="1" type="double" fixed="true"/>
	<item id="JHSL" alias="计划数量" length="10" precision="2" not-null="1" type="double" min="0"/>
	<item id="GJJE" alias="参考金额" length="10" precision="4" not-null="1" type="double" fixed="true" />
	<item id="SPSL" alias="审批数量" length="10" precision="2"  type="double" defaultValue="0" min="0"/>
	<item id="CGSL" alias="采购数量" length="10" precision="2"  type="double" fixed="true" defaultValue="0"/>
	<item ref="d.GCSL" fixed="true" display="1"/>
	<item ref="d.DCSL" fixed="true" display="1"/>
	<item id="XTSB" alias="药库识别" length="18" type="long" display="0"/>
	<item id="JHDH" alias="计划单号" length="6"  type="int" display="0"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long" display="0"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long" display="0"/>
	<item id="JHQD" alias="进货渠道" length="6" type="int" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
		<relation type="parent" entryName="phis.application.sto.schemas.YK_YPXX" >
			<join parent="YPXH" child="YPXH"></join>
			<join parent="JGID" child="JGID"></join>
		</relation>
	</relations>
</entry>
