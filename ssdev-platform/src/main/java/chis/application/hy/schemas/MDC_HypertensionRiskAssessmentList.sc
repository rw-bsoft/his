<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionRiskAssessmentList"
	alias="糖尿病高危人群评估" sort="recordId">
	<item id="recordId" alias="标识列" type="string" length="16"
		not-null="1" pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="phrId" alias="个人健康档案号" type="string" length="30" hidden="true"
		fixed="true" />
	<item id="empiId" alias="empiid" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="riskId" alias="高危表id" type="string" length="16" hidden="true"
		fixed="true" />

	<item id="estimateDate" alias="评估日期" type="date" defaultValue="%server.date.today"
		update="false">
	</item>
	<item id="estimateUser" alias="评估人" type="string" length="20"
		defaultValue="%user.userId" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="estimateUnit" alias="评估单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" fixed="true" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="estimateType" alias="评估类型" type="string" length="1"
		fixed="true" hidden="true">
		<dic>
			<item key="1" text="首次评估" />
			<item key="2" text="不定期评估" />
		</dic>
	</item>
	<item id="riskiness" alias="高危因素" type="string" length="64"
		colspan="4" width="450" hidden="true">
		<dic render="Checkbox" columnWidth="300" columns="2">
			<item key="01" text="高龄" />
			<item key="02" text="超重或者肥胖(BMI>=24kg/m2)" />
			<item key="03" text="收缩压介于120~139mmHg之间和舒张压介于80~89之间" />
			<item key="04" text="高血压家族史(一,二级亲属)" />
			<item key="05" text="长期过量饮酒(每日饮2两白酒/2.5瓶啤酒/4个易拉罐啤酒/6两黄酒/1斤2两葡萄酒,且每周饮酒4次以上)" columnWidth="2"/>
			<item key="06" text="长期膳食高盐" />
			<item key="07" text="糖尿病患者" />
			<item key="08" text="缺乏体力劳动" />
			<item key="09" text="吸烟" />
			<item key="10" text="血脂异常" />
			<item key="11" text="糖调节异常" />
		</dic>
	</item>
	<item id="age" alias="年龄" type="int" enableKeyEvents="true" hidden="true"/>
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false" hidden="true" />
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false" hidden="true"/>
	<item id="waistLine" alias="腰围(cm)" type="double" length="4"
		minValue="40" maxValue="200" display="0" />
	<item id="weight" alias="体重(kg)" type="double" length="6" display="0"
		minValue="30" maxValue="500" not-null="1" enableKeyEvents="true" />
	<item id="height" alias="身高(cm)" type="double" length="6" display="0"
		minValue="100" maxValue="300" not-null="1" enableKeyEvents="true" />
	<item id="bmi" alias="BMI" length="6" type="double" fixed="true"  hidden="true"/>
	<item id="smokeCount" alias="日吸烟量" type="int" length="3" hidden="true"/>
	<item id="smokeYears" alias="吸烟年数" type="int" length="2" hidden="true"/>
	<item id="drinkTimes" alias="周饮酒次数" type="int" length="4" hidden="true"/>
	<item id="drinkCount" alias="日饮酒量(两)" type="int" length="4" hidden="true" />
	<item id="saltCount" alias="日摄盐量" type="int" length="3" hidden="true"/>
	<item id="tc" alias="TC" type="double" length="10" width="165" hidden="true"/>
	<item id="td" alias="TD" type="double" length="10" width="165"
		colspan="2" hidden="true"/>
	<item id="ldl" alias="LDL-C" type="double" length="10" width="165" hidden="true"/>
	<item id="hdl" alias="HDL-C" type="double" length="10" width="165"
		colspan="2" hidden="true"/>
	<item id="fbs" alias="空腹血糖" type="double" length="6" precision="2" hidden="true"/>
	<item id="pbs" alias="餐后血糖" type="double" length="6" precision="2" hidden="true"/>

	<item id="inputDate" alias="登记日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" hidden="true">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="inputUser" alias="登记人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="inputUnit" alias="登记机构" type="string" length="8"
		colspan="3" defaultValue="%user.manageUnit.id" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>


	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
