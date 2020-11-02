<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YK_FKJL" alias="药库付款记录">
	<item id="JLXH" alias="记录序号" length="18" not-null="1" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
	<item id="SBXH" alias="识别序号" type="long" length="18" />
	<item id="FKJL" alias="付款记录" type="long" length="18" />
	<item id="XTSB" alias="药库识别" type="long" length="18" />
	<item id="RKFS" alias="入库方式" type="int" length="4" />
	<item id="RKDH" alias="入库单号" type="int" length="6" />
	<item id="YPXH" alias="药品序号" type="long" length="18" />
	<item id="YPCD" alias="药品产地" type="long" length="18" />
	<item id="FKJE" alias="付款金额" type="double" length="12" precision="4"/>
	<item id="BCJE" alias="本次付款" type="double" length="12" precision="4"/>
	<item id="YFJE" alias="已付金额" type="double" length="12" precision="4"/>
</entry>