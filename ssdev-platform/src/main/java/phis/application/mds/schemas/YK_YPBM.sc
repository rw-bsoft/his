<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YPBM" alias="药品别名" sort="a.BMXH desc">
	<item id="BMXH" alias="别名序号" type="long" length="18" generator="assigned" display="0"  pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="30000" />
		</key>
	</item>
	<item id="YPXH" alias="药品内码" type="long" length="18"  display="2" />
	<item id="YPMC" alias="药品名称" type="string" width="180" length="80" not-null="true"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" width="80" target="YPMC" codeType="py"/>
	<item id="WBDM" alias="五笔码" type="string" length="10" width="80" target="YPMC" codeType="wb"/>
	<item id="JXDM" alias="角形码" type="string" length="10" width="80"  target="YPMC" codeType="jx"/>
	<item id="BHDM" alias="笔画码" type="string" length="10" width="80"  target="YPMC" codeType="bh"/>
	<item id="QTDM" alias="其它码" type="string" length="10" width="80"/>
	<item id="BMFL" alias="编码方式" type="int" length="2" defaultValue="1"  display="0"/>
</entry>
