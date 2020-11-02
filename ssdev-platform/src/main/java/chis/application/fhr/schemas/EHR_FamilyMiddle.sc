<?xml version="1.0" encoding="UTF-8"?>
<entry alias="家庭信息中间表" sort="familyId desc">
	<item id="middleId" pkey="true" alias="中间表id" type="string"
		length="17" not-null="1"  fixed="true" queryable="true"  generator="assigned"  width="160">
		<key>
			<Rule name="areaCode" defaultFill="0" length="12" fillPos="after" type="string">
      		%codeCtx.regionCode
			</Rule>
			<Rule name="increaseId" defaultFill="0" type="increase" seedRel="areaCode" length="5" startPos="1"/>
		</key>
	</item>
	<item id="fuelType" alias="燃料类型" type="string" length="6"  display="2">
		<dic id="chis.dictionary.fuelType"/>
	</item>
	<item id="cookAirTool" alias="厨房排风设施" type="string" length="6" defaultValue="1"  display="2">
		<dic id="chis.dictionary.cookAirTool"/>
	</item>
	<item id="waterSourceCode" alias="饮水类型" type="string" length="6" defaultValue="1"  display="2">
		<dic id="chis.dictionary.waterSourceCode"/>
	</item>
	<item id="washroom" alias="厕所类别" type="string" length="2" defaultValue="1"  display="2">
		<dic id="chis.dictionary.washroom"/>
	</item>
	<item id="livestockColumn" alias="禽畜栏" type="string" length="2" display="2">
		<dic id="chis.dictionary.livestockColumn"/>
	</item>
	<item id="empiId" alias="empiid" type="string" length="32"  notDefaultValue="true"  display="2"/>

	
	
</entry>