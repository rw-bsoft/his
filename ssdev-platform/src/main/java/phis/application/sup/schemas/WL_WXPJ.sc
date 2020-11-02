<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_WXPJ" alias="维修配件(WL_WXPJ)">
	<item id="PJXH" alias="配件序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
	<item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
	<item id="WXXH" alias="维修序号" type="long" length="18" display="0"/>
	<item id="PJMC" alias="材料名称" mode="remote" type="string" length="50" />
	<item id="PJGG" alias="规格" type="string" length="30" />
	<item id="PJDW" alias="单位" type="string" length="6" />
	<item id="PJSL" alias="数量" type="double" length="18" precision="2"/>
	<item id="PJJG" alias="价格" type="double" length="18" precision="4"/>
	<item id="PJJE" alias="金额" type="double" length="18" fixed="true" precision="2"/>
	<item id="CKMX" alias="出库明细" type="long" length="18" display="0"/>
</entry>
