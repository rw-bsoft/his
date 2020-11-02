<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_PD02" alias="药库盘点">
	<item id="JGID" alias="机构ID" length="20" type="string" display="0"/>
	<item id="SBXH" alias="识别序号" length="18" type="long" pkey="true" display="0" generator="assigned">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="XTSB" alias="药库识别" length="18" type="long" display="0"/>
	<item id="PDDH" alias="盘点单号" length="12" type="string" display="0"/>
	<item id="YPXH" alias="药品序号" length="18" type="long" display="0"/>
	<item id="YPCD" alias="药品产地" length="18" type="long" display="0"/>
	<item id="YPPH" alias="药品批号" length="20" type="string"/>
	<item id="YPXQ" alias="药品效期" type="timestamp" display="0"/>
	<item id="ZMSL" alias="账面数量" length="10" type="double" precision="4"/>
	<item id="SPSL" alias="实盘数量" length="10" type="double" precision="4"/>
	<item id="JHJG" alias="进货价格" length="12" type="double" precision="6" display="0"/>
	<item id="PFJG" alias="批发价格" length="12" type="double" precision="6" display="0"/>
	<item id="LSJG" alias="零售价格" length="12" type="double" precision="6" display="0"/>
	<item id="JHJE" alias="进货金额" length="12" type="double" precision="4" display="0"/>
	<item id="PFJE" alias="批发金额" length="12" type="double" precision="4" display="0"/>
	<item id="LSJE" alias="零售金额" length="12" type="double" precision="4" display="0"/>
	<item id="BZLJ" alias="标准零价" length="12" type="double" precision="6" display="0"/>
	<item id="KCSB" alias="库存识别" length="18" type="long" display="0"/>
	<item id="TYPE" alias="库存性质" length="6" type="int" display="0"/>
	<item id="BZXX" alias="备注信息" length="100" type="string"/>
</entry>
