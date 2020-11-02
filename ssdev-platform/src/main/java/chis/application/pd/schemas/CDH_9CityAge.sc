<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pd.schemas.CDH_9CityAge" alias="9市标准-年龄别身高、体重、头围" sort="age">
	<item id="recordId" alias="行标识" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="73"/>
		</key>
	</item>
	<item id="age" alias="年龄" type="int" not-null="1" />
	<item id="ageNote" alias="年龄描述" type="string" length="20" hidden="true"/>
	<item id="sexCode" alias="性别" type="string" length="1" not-null="1" >
		<dic >
			<item key="1" text="男" />
			<item key="2" text="女" />
		</dic>
	</item>
	<item id="wSD3neg" alias="SD3neg-年龄别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="wSD2neg" alias="SD2neg-年龄别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="wSD1neg" alias="SD1neg-年龄别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="wSD0" alias="SD0-年龄别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="wSD1" alias="SD1-年龄别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="wSD2" alias="SD2-年龄别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="wSD3" alias="SD3-年龄别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="hSD3neg" alias="SD3neg-年龄别身高" type="double" maxValue="999.99"
		precision="2" />
	<item id="hSD2neg" alias="SD2neg-年龄别身高" type="double" maxValue="999.99"
		precision="2" />
	<item id="hSD1neg" alias="SD1neg-年龄别身高" type="double" maxValue="999.99"
		precision="2" />
	<item id="hSD0" alias="SD0-年龄别身高" type="double" maxValue="999.99"
		precision="2" />
	<item id="hSD1" alias="SD1-年龄别身高" type="double" maxValue="999.99"
		precision="2" />
	<item id="hSD2" alias="SD2-年龄别身高" type="double" maxValue="999.99"
		precision="2" />
	<item id="hSD3" alias="SD3-年龄别身高" type="double" maxValue="999.99"
		precision="2" />
	<item id="mSD3neg" alias="SD3neg-年龄别头围" type="double" maxValue="999.99"
		precision="2" />
	<item id="mSD2neg" alias="SD2neg-年龄别头围" type="double" maxValue="999.99"
		precision="2" />
	<item id="mSD1neg" alias="SD1neg-年龄别头围" type="double" maxValue="999.99"
		precision="2" />
	<item id="mSD0" alias="SD0-年龄别头围" type="double" maxValue="999.99"
		precision="2" />
	<item id="mSD1" alias="SD1-年龄别头围" type="double" maxValue="999.99"
		precision="2" />
	<item id="mSD2" alias="SD2-年龄别头围" type="double" maxValue="999.99"
		precision="2" />
	<item id="mSD3" alias="SD3-年龄别头围" type="double" maxValue="999.99"
		precision="2" />
</entry>
