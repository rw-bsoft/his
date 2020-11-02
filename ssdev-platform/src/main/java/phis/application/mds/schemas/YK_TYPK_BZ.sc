<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TYPK" alias="药品信息"   sort="a.YPXH desc">
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品内码" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="TP" alias="" type="string" renderer="onRenderer" width="23" length="20" virtual="true" display="1"/>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%" length="80" colspan="2" not-null="true" />
	<item id="YPGG" alias="规格" type="string" length="20" />
	<item id="YPDW" alias="单位" type="string" length="4" />
	<!-- 药品包装 -->
	<item id="ZXDW" alias="单位" type="string"  length="4"  />
	<item id="ZXBZ" alias="包装数量" type="int" length="4"  not-null="1"/>
	<item id="YFDW" alias="单位" type="string"   length="4"  />
	<item id="YFBZ" alias="包装数量"  type="int"   defaultValue="1" not-null="true" length="4" />
	<item id="BFDW" alias="单位" type="string"   length="4"  />
	<item id="BFBZ" alias="包装数量" type="int"  defaultValue="1" not-null="true" length="4"/>
	<item id="PYDM" alias="拼音码" display="2" type="string" length="160" selected="true" queryable="true">
	</item>
	<item id="JXDM" alias="角形码"  display="2" type="string" length="160" queryable="true">
	</item>
	<item id="QTDM" alias="其它码"  type="string" length="160" display="2" />
	<item id="ZFPB" alias="作废" display="0"  defaultValue="0"/>
</entry>
