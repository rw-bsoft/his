<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_RKFS" alias="入库方式">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<!--默认值暂时先写死,药库切换做好后更改-->
	<item id="XTSB" alias="药库识别" length="18" type="long" display="0" />
	<item id="RKFS" alias="入库方式" length="4" type="int" display="1" not-null="1" pkey="true" generator="assigned">
		<key>
			<rule name="increaseId" type="increase" length="4"
				startPos="1" />
		</key>
	</item>
	<item id="FSMC" alias="方式名称" type="string" length="20" not-null="1" renderer="onRenderer_reg"/>
	<item id="RKDH" alias="入库单号" length="6" type="int" not-null="1" minValue="1" defaultValue="1" />
	<item id="YSDH" alias="验收单号" length="6" type="int" not-null="1" minValue="1" defaultValue="1" />
	<item id="SBFH" alias="识别符号" type="string" length="4" display="0"/>
	<item id="DJFS" alias="定价方式" length="1" not-null="1" type="int" width="120">
		<dic id="phis.dictionary.pricingWay"/>
	</item>
	<item id="DJGS" alias="定价公式" type="string" length="250" display="0"/>
	<item id="DYFS" alias="对应方式" length="8" type="int" >
		<dic id="phis.dictionary.correspondingWay"/>
	</item>
</entry>
