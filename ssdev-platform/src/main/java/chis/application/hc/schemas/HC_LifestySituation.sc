<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.HC_LifestySituation" alias="生活方式">
	<item queryable="true" id="lifestySituation" alias="记录编号" length="16" type="string" pkey="true" generator="assigned" not-null="1" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item queryable="true" id="healthCheck" alias="年检编号" length="16" type="string" display="0"/>
	<item queryable="true" id="physicalExerciseFrequency" alias="体育锻炼频率" length="1" type="string">
		<dic>
			<item key="1" text="每天"/>
			<item key="2" text="每周一次以上"/>
			<item key="3" text="偶尔"/>
			<item key="4" text="不锻炼"/>
		</dic>
	</item>
	<item queryable="true" id="everyPhysicalExerciseTime" alias="锻炼时间(分/次)" type="int" maxValue="360" fixed="true"/>
	<item queryable="true" id="insistexercisetime" alias="坚持锻炼(年)" type="int" maxValue="200" fixed="true"/>
	<item queryable="true" id="exerciseStyle" alias="体育锻炼方式" length="60" type="string" fixed="true"/>
	<item queryable="true" id="dietaryHabit" alias="饮食习惯" length="64" type="string">
		<dic render="LovCombo">
			<item key="1" text="荤素均衡"/>
			<item key="2" text="荤食为主"/>
			<item key="3" text="素食为主"/>
			<item key="4" text="嗜盐"/>
			<item key="5" text="嗜油"/>
			<item key="6" text="嗜糖"/>
		</dic>
	</item>
	<item queryable="true" id="wehtherSmoke" alias="吸烟状况" length="1" type="string" >
		<dic>
			<item key="1" text="从不吸烟"/>
			<item key="2" text="已戒烟"/>
			<item key="3" text="吸烟"/>
		</dic>
	</item>
	<item queryable="true" id="beginSmokeTime" alias="开始吸烟(岁)" type="int" maxValue="200" fixed="true"/>
	<item queryable="true" id="stopSmokeTime" alias="戒烟年龄(岁)" type="int" maxValue="200" fixed="true"/>
	<item queryable="true" id="smokes" alias="日吸烟量(支)" type="int" maxValue="200" fixed="true"/>
	<item queryable="true" id="drinkingFrequency" alias="饮酒频率" length="1" type="string">
		<dic>
			<item key="1" text="从不"/>
			<item key="2" text="偶尔"/>
			<item key="3" text="经常"/>
			<item key="4" text="每天"/>
		</dic>
	</item>
	<item queryable="true" id="alcoholConsumption" alias="日饮酒量(两)" type="int" length="5" fixed="true"/>
	<item queryable="true" id="whetherDrink" alias="是否戒酒" length="1" type="string" fixed="true">
		<dic>
			<item key="1" text="未戒酒"/>
			<item key="2" text="已戒酒"/>
		</dic>
	</item>
	<item queryable="true" id="stopDrinkingTime" alias="戒酒年龄(岁)" type="int" maxValue="200" fixed="true"/>
	<item queryable="true" id="geginToDrinkTime" alias="开始饮酒(岁)" type="int" maxValue="200" fixed="true"/>
	<item queryable="true" id="isDrink" alias="一年内醉酒否" length="1" type="string" fixed="true">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item queryable="true" id="mainDrinkingVvarieties" alias="饮酒种类" length="20" type="string" fixed="true">
		<dic id="chis.dictionary.drinkTypeCode_life" render="LovCombo"/>
	</item>
	<item queryable="true" id="drinkOther" alias="其他饮酒种类"
		length="50" type="string" fixed="true" />
	<item queryable="true" id="occupational" alias="职业病危害" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="jobs" alias="工种" length="50" type="string" fixed="true"/>
	<item queryable="true" id="workTime" alias="从业时间(年)" type="int" fixed="true"/>
	<item queryable="true" id="dust" alias="粉尘" length="50" type="string" fixed="true"/>
	<item queryable="true" id="dustPro" alias="有无防护措施" length="1" type="string" fixed="true">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="dustProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true"/>
	<item queryable="true" id="ray" alias="放射物质" length="50" type="string" fixed="true"/>
	<item queryable="true" id="rayPro" alias="有无防护措施" length="1" type="string" fixed="true">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="rayProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true"/>
	<item queryable="true" id="physicalFactor" alias="物理因素" length="50" type="string" fixed="true"/>
	<item queryable="true" id="physicalFactorPro" alias="有无防护措施" length="1" type="string" fixed="true">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="physicalFactorProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true"/>
	<item queryable="true" id="chemicals" alias="化学物质" length="50" type="string" fixed="true"/>
	<item queryable="true" id="chemicalsPro" alias="有无防护措施" length="1" type="string" fixed="true">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="chemicalsProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true"/>
	<item queryable="true" id="other" alias="其他毒物" length="50" type="string" fixed="true"/>
	<item queryable="true" id="otherPro" alias="有无防护措施" length="1" type="string" fixed="true">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="otherProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true"/>
	<item queryable="true" id="createUser" alias="录入员工" length="20" update="false" display="1"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item queryable="true" id="createUnit" alias="录入单位" length="20" update="false"  display="1"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item queryable="true" id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"  display="1"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
