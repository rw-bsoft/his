<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pd.schemas.CDH_9CityHeight" alias="9市标准-身高别体重" sort="height">
	<item id="recordId" alias="行标识" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="97"/>
		</key>
	</item>
	<item id="height" alias="身高" type="double" 
		precision="2" not-null="1" maxValue="999.99"/>
	<item id="sexCode" alias="性别" type="string" length="1" not-null="1" >
		<dic >
			<item key="1" text="男" />
			<item key="2" text="女" />
		</dic>
	</item>
	<item id="sD3neg" alias="SD3neg-身高别体重" type="double" maxValue="999.99"
		precision="2"/>
	<item id="sD2neg" alias="SD2neg-身高别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="sD1neg" alias="SD1neg-身高别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="sD0" alias="SD0-身高别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="sD1" alias="SD1-身高别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="sD2" alias="SD2-身高别体重" type="double" maxValue="999.99"
		precision="2" />
	<item id="sD3" alias="SD3-身高别体重" type="double" maxValue="999.99"
		precision="2" />
</entry>
