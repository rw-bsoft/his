<?xml version="1.0" encoding="UTF-8"?>

<entry  alias="高血压随访" sort="a.empiId">
	<item id="visitId" alias="随访标识" type="string" display="0"
		length="16" not-null="1" pkey="true" fixed="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1"
		display="0" />

	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />

	<item id="visitDate" alias="随访日期" not-null="true" type="date"
		defaultValue="%server.date.today" enableKeyEvents="true"
		validationEvent="false" queryable="true" />
	<item id="visitWay" alias="随访方式" type="string" length="1"
		not-null="true" tag="radioGroup">
		<dic id="chis.dictionary.visitWay" />
	</item>
</entry>
