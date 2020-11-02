<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionControl" alias="高血压控制情况分组"
	sort="a.controlCondition">
	<item id="recordId" alias="recordId" type="string" length="16" not-null="1"
		pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="controlCondition" alias="控制情况" fixed="true" length="1" not-null="1" type="string">
		<dic>
			<item key="1" text="控制优良"/>
			<item key="2" text="控制尚可"/>
			<item key="3" text="控制不良"/>
			<item key="4" text="未评价"/>
			<item key="5" text="新病人"/>
		</dic>
	</item>
	<item id="veryHighRisk" alias="很高危" length="2" type="string">
		<dic id="chis.dictionary.groups"/>
	</item>
	<item id="highRisk" alias="高危" length="2" type="string">
		<dic id="chis.dictionary.groups"/>
	</item>
	<item id="middleRisk" alias="中危" length="2" type="string">
		<dic id="chis.dictionary.groups"/>
	</item>
	<item id="lowRisk" alias="低危" length="2" type="string">
		<dic id="chis.dictionary.groups"/>
	</item>
</entry>
