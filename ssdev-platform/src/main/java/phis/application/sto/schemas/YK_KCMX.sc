<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_KCMX" alias="库存明细账">
	<item id="JGID" alias="机构ID" length="20" not-null="1" type="string"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="YPXH" alias="药品序号" length="18" type="long" display="0"  not-null="1"/>
	<item id="YPCD" alias="药品产地" length="18" type="long" not-null="1"/>
	<item id="YPPH" alias="药品批号" type="string" length="20"/>
	<item id="YPXQ" alias="药品效期" type="date"/>
	<item id="KCSL" alias="库存数量" length="10" precision="4" type="double" not-null="1"/>
	<item id="TYPE" alias="库存性质" length="6" type="int"/>
	<item id="JHRQ" alias="进货日期" type="datetime"/>
	<item id="JHJG" alias="进货价格" length="12" precision="6" type="double" not-null="1"/>
	<item id="PFJG" alias="批发价格" length="12" precision="6" type="double" />
	<item id="LSJG" alias="零售价格" length="12" precision="6" type="double" not-null="1"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" type="double" not-null="1"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4" type="double" />
	<item id="LSJE" alias="零售金额" length="12" precision="4" type="double" not-null="1"/>
	<item id="BZLJ" alias="标准零价" length="12" precision="6" type="double" not-null="1"/>
</entry>
