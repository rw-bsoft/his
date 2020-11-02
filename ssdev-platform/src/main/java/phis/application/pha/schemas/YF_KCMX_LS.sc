<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_KCMX_LS" alias="药房库存帐" sort="a.YPXH">
	
	<item id="YPCD" alias="药品产地"  length="18"  type="long" />
	<item id="LSJG" alias="零售价格" length="13" precision="4" type="double"  />
	<item id="LSJE" alias="零售金额" length="13" precision="4" type="double" />
	<item id="YPSL" alias="库存数量" length="9" precision="2" type="double" />
	<item id="YPPH" alias="药品批号" type="string" length="20" />
	<item id="YPXQ" alias="药品效期" type="date" />
	<item id="JHJG" alias="进货价格" length="12" precision="4" type="double" />
	<item id="JHJE" alias="进货金额" length="12" precision="4" type="double"/>
	<item id="PFJG" alias="批发价格" length="12" precision="4" type="double"  />
	<item id="PFJE" alias="批发金额" length="12" precision="4" type="double"/>
	<item id="JGID" alias="机构ID" length="20" />
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0" generator="assigned" pkey="true" type="long">
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
	</item>
	<item id="YFSB" alias="药房识别" length="18"  type="long"/>
	<item id="CKBH" alias="窗口编号" length="2" type="int"/>
	<item id="YPXH" alias="药品序号" length="18"  type="long"/>
	<item id="JYBZ" alias="禁用标志" length="1"  type="int"/>
	<item id="YKLJ" alias="药库包装零价"  length="12" precision="6" type="double" />
	<item id="YKJJ" alias="药库包装进价" length="12" precision="6" type="double"/>
	<item id="YKPJ" alias="药库包装批价" length="12" precision="6" type="double"/>
	<item id="YKKCSB" alias="药库库存识别" length="18"  display="0" type="long"/>
</entry>
