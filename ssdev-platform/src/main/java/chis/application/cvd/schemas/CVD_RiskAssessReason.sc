<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_RiskAssessReason" alias="危险评估因素">
	<item id="riskId" alias="危险因素编号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="tc" alias="总胆固醇(mmol/L)" type="string" length="1">
		<dic>
			<item key="0" text="0" />
			<item key="4" text="4" />
			<item key="5" text="5" />
			<item key="6" text="6" />
			<item key="7" text="7" />
			<item key="8" text="8" />
		</dic>
	</item>
	<item id="diabetes" alias="是否糖尿病" type="string" length="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="sexCode" alias="性别" type="string" length="1">
		<dic minChars="1">
			<item key="1" text="男" mCode="1" />
			<item key="2" text="女" mCode="2" />
		</dic>
	</item>
	<item id="smoke" alias="是否吸烟" type="string" length="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>

	<item id="ageGroup" alias="年龄组" type="string" length="1">
		<dic>
			<item key="1" text="40-49" />
			<item key="2" text="50-59" />
			<item key="3" text="60-69" />
			<item key="4" text="70-79" />
		</dic>
	</item>
	<item id="bp" alias="血压" type="string" length="1">
		<dic>
			<item key="1" text="120-139" />
			<item key="2" text="140-159" />
			<item key="3" text="160-179" />
			<item key="4" text="180--" />
		</dic>
	</item>

	<item id="riskRatio" alias="危险度" type="string" length="1" />
	<dic>
		<item key="1" text="0-10%" />
		<item key="2" text="10%-20%" />
		<item key="3" text="20%-30%" />
		<item key="4" text="30%-40%" />
		<item key="5" text=">=40%" />
	</dic>

</entry>
