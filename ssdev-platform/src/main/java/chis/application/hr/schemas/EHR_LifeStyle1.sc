<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_LifeStyle"   alias="个人生活习惯">
	<item id="lifeStyleId" pkey="true" alias="档案编号" type="string"
		length="16" not-null="1" fixed="true" hidden="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiid" type="string" length="32"
		queryable="true" fixed="true" notDefaultValue="true" hidden="true" />
	<item id="smokeFlag" alias="吸烟情况" type="string" length="2" queryable="true" group="吸烟">
		<dic>
			<item key="1" text="从不吸烟" />
			<item key="2" text="已戒烟" />
			<item key="3" text="吸烟" />
		</dic>
	</item>
	<item id="smokeCount" alias="每天支数" type="int" length="3"
		queryable="true" group="吸烟"/>
	<item id="smokeFreqCode" alias="吸烟频率" type="string" length="2"
		queryable="true" group="吸烟">
		<dic id="chis.dictionary.CV5101_24" />
	</item>
	<item id="smokeTypeCode" alias="吸烟种类" type="string" length="2"
		queryable="true" group="吸烟">
		<dic id="chis.dictionary.CV5305_01" />
	</item>
	
	<item id="drinkFlag" alias="饮酒频率" type="string" length="1"
		queryable="true" group="饮酒">
		<dic>
			<item key="1" text="从不" />
			<item key="2" text="偶尔" />
			<item key="3" text="经常" />
			<item key="4" text="每天" />
		</dic>
	</item>
	<item id="drinkCount" alias="日饮酒量(两)" type="int" length="4"
		queryable="true" group="饮酒"/>
	<item id="drinkTypeCode" alias="饮酒种类" type="string" length="64"
		queryable="true" colspan="2" group="饮酒">
		<dic id="chis.dictionary.drinkTypeCode_life" render="LovCombo"/>
	</item>
	
	<item id="eateHabit" alias="饮食习惯" type="string" length="64"
		queryable="true" colspan="4" anchor="50%" group="饮食">
		<dic id="chis.dictionary.eateHabit" render="LovCombo" />
	</item>
	<item id="trainFreqCode" alias="锻炼频率" type="string" length="2"
		queryable="true" group="锻炼">
		<dic id="chis.dictionary.CV5101_28" />
	</item>
	<item id="trainModeCode" alias="锻炼方式" type="string" length="2"
		queryable="true" group="锻炼">
		<dic>
			<item key="1" text="散步" />
			<item key="2" text="做操或气功" />
			<item key="3" text="太极拳" />
			<item key="4" text="跑步" />
			<item key="5" text="跳舞" />
			<item key="6" text="球类" />
			<item key="7" text="其他" />
		</dic>
	</item>
	<item id="trainMinute" alias="每次时长(分)" type="int" length="3"
		queryable="true" group="锻炼"/>
</entry>