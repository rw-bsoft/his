<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TJ02" alias="调价02">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" not-null="1"  type="string" defaultValue="%user.manageUnit.id" display="0" />
	<item id="XTSB" alias="药库识别" length="18" not-null="1" defaultValue="1" display="0" type="long"/>
	<!--从上面表单获取-->
	<item id="TJFS" alias="调价方式" length="2" type="int" not-null="1" display="0"/>
	<!--从上面表单获取-->
	<item id="TJDH" alias="调价单号" length="6" not-null="1" display="0" type="int"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" display="0" />
	<item ref="b.YPMC" mode="remote"  width="120"/>
	<item ref="b.YPGG" fixed="true"/>
	<item ref="b.YPDW" fixed="true"/>
	<item ref="c.CDMC"  fixed="true"/>
	<item id="YJHJ" alias="原进货价" width="71" length="13" precision="4" not-null="1" fixed="true" type="double" max="999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="YLSJ" alias="原零售价" width="71" length="13" precision="4" not-null="1" fixed="true" type="double" max="999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="XJHJ" alias="新进货价" width="71" length="11" precision="4" not-null="1"  type="double" max="999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="XLSJ" alias="新零售价" width="71" length="11" precision="4" not-null="1"  type="double" max="999999.9999" min="0" renderer="onRenderer_four"/>
	<item id="YPFJ" alias="原批发价" defaultValue="0" length="12" precision="6"  type="double" display="0"/>
	<item id="XPFJ" alias="新批发价" defaultValue="0" length="12" precision="6"  type="double" display="0"/>
	<item id="TJSL" alias="调价数量" length="10" precision="4" not-null="1" defaultValue="0" type="double" fixed="true" display="0"/>
	<item id="YPCD" alias="药品产地" length="18" display="0" type="long"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" defaultValue="1" display="0" type="long"/>
	<item id="YFBZ" alias="药房包装" length="4" display="0" type="int"/>
	<item id="LSZZ" alias="零售增值" length="12" precision="4"  display="0" type="double" defaultValue="0"/>
	<item id="LSJZ" alias="零售减值" length="12" precision="4" display="0" type="double" defaultValue="0"/>
	<item id="PFZZ" alias="批发增值" length="12" precision="4"  display="0" type="double" defaultValue="0"/>
	<item id="PFJZ" alias="批发减值" length="12" precision="4" display="0" type="double" defaultValue="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />
		<join parent="YPXH" child="YPXH" />
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />
		<join parent="YPCD" child="YPCD" />
	</relations>
</entry>
