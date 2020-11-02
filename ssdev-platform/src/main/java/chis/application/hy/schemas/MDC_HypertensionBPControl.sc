<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionBPControl" alias="高血压血压控制设置"
	sort="a.groups">
	<item id="recordId" alias="recordId" type="string" length="16" not-null="1"
		pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="groups" alias="组别" length="2" width="45" type="string" fixed="true">
		<dic id="chis.dictionary.groups"/>
	</item>
	<item id="visitCount" alias="随访次数(&gt;=)" width="95"  length="3" type="int"/>
	<item id="SBP" alias="血压控制范围&lt;br/&gt;(&lt;=)(收缩压" width="150" type="int"/>
	<item id="xg" alias="/" type="string" fixed="true" width="15" length="1"/>
	<item id="DBP" alias="舒张压)" type="int" width="65"/>
	<item id="controlCondition" alias="控制情况" width="75" type="string" length="1">
		<dic>
			<item key="1" text="控制优良"/>
			<item key="2" text="控制尚可"/>
			<item key="3" text="控制不良"/>
		</dic>
	</item>
</entry>
