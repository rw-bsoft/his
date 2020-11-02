<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CK02" alias="出库02">
	<item id="JGID" alias="机构ID" length="20" not-null="1" type="string"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" type="long"/>
	<item id="CKFS" alias="出库方式" length="4" not-null="1" type="int"/>
	<item id="CKDH" alias="出库单号" length="6" not-null="1" type="int"/>
	<item id="YPXH" alias="药品序号" length="18" not-null="1" type="long"/>
	<item id="YPCD" alias="药品产地" length="18" not-null="1" type="long"/>
	<item id="YPPH" alias="药品批号" type="string" length="20" />
	<item id="YPXQ" alias="药品效期" type="date"/>
	<item id="PFJG" alias="批发价格" length="12" precision="6"  type="double"/>
	<item id="LSJG" alias="零售价格" length="12" precision="6" not-null="1" type="double"/>
	<item id="JHJG" alias="进货价格" length="12" precision="6" not-null="1" type="double"/>
	<item id="SQSL" alias="申请数量" length="10" precision="4" not-null="1" type="double"/>
	<item id="SFSL" alias="实发数量" length="10" precision="4" not-null="1" type="double"/>
	<item id="TYPE" alias="库存性质" length="6" type="int"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" not-null="1" type="double"/>
	<item id="PFJE" alias="批发金额" length="12" precision="4"  type="double"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" not-null="1" type="double"/>
	<item id="BZLJ" alias="标准零价" length="12" precision="6" not-null="1" type="double"/>
	<item id="KCSB" alias="库存识别" length="18" not-null="1" type="long"/>
	<item id="YKJH" alias="药库进货" length="12" precision="4" type="double"/>
	<item id="JBYWBZ" alias="基本药物标志" length="1" not-null="1" type="int"/>
	<item id="YFKCSB" alias="药房库存识别" length="18" not-null="1" display="0" type="long" defaultValue="0"/>
</entry>
