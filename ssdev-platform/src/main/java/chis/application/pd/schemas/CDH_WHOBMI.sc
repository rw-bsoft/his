<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pd.schemas.CDH_WHOBMI" alias="WHO标准-BMI指数 " sort="age">
	<item id="recordId" alias="行标识" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="171"/>
		</key>
	</item>
	<item id="age" alias="年龄" type="int" not-null="1"/>
	<item id="ageNote" alias="年龄描述" type="string" length="20" display="0"/>
	<item id="sexCode" alias="性别" type="string" length="1" not-null="1" >
		<dic >
			<item key="1" text="男" />
			<item key="2" text="女" />
		</dic>
	</item>
	<item id="BMIL" alias="L值-BMI" type="double" maxValue="999.99999"
		precision="5" />
	<item id="BMIM" alias="M值-BMI" type="double" maxValue="999.99999"
		precision="5" />
	<item id="BMIS" alias="S值-BMI" type="double" maxValue="999.99999"
		precision="5" />
	<item id="BMISD3neg" alias="SD3neg-BMI" type="double" maxValue="999.99"
		precision="2" />
	<item id="BMISD2neg" alias="SD2neg-BMI" type="double" maxValue="999.99"
		precision="2" />
	<item id="BMISD1neg" alias="SD1neg-BMI" type="double" maxValue="999.99"
		precision="2" />
	<item id="BMISD0" alias="SD0-BMI" type="double" maxValue="999.99"
		precision="2" />
	<item id="BMISD1" alias="SD1-BMI" type="double" maxValue="999.99"
		precision="2" />
	<item id="BMISD2" alias="SD2-BMI" type="double" maxValue="999.99"
		precision="2" />
	<item id="BMISD3" alias="SD3-BMI" type="double" maxValue="999.99"
		precision="2" />
</entry>
