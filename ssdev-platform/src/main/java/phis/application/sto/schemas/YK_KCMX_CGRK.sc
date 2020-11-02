<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_KCMX" alias="库存明细账" >
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="YPPH" alias="批号" type="string" length="20"/>
	<item id="YPXQ" alias="效期" type="date"/>
	<item id="KCSL" alias="库存数" length="10" precision="4" type="double" not-null="1"/>
	<item id="TYPE" alias="状态" length="6" type="int">
		<dic>
			<item key="1" text="合格"/>
			<item key="2" text="次品"/>
			<item key="3" text="伪劣"/>
			<item key="4" text="破损"/>
			<item key="5" text="霉变"/>
		</dic>
	</item>
	<item id="JHJG" alias="进货价格" length="12" precision="4" type="double" not-null="1"/>
	<item id="LSJG" alias="零售价格" length="12" precision="4" type="double" not-null="1"/>
	<item id="JHJE" alias="进货金额" length="12" precision="4" type="double" not-null="1"/>
	<item id="LSJE" alias="零售金额" length="12" precision="4" type="double" not-null="1"/>
	<item id="YPXH" alias="药品序号" length="18" type="long" display="0"  not-null="1" />
	<item id="YPCD" alias="药品产地" length="18" type="long" not-null="1" display="0"/>
	<item id="JHRQ" alias="进货日期" type="date" display="0"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4" type="double" display="0" />
	<item id="PFJE" alias="批发金额" length="12" precision="4" type="double" display="0"/>
	<item id="BZLJ" alias="标准零价" length="12" precision="4" type="double" not-null="1" display="0"/>
</entry>
