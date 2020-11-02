<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_PostnatalVisitInfo" alias="产后访视信息"  sort="visitDate,visitId">
	<item id="visitId" alias="检查单号" length="16" pkey="true"
		type="string" not-null="1" fixed="true" hidden="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPIID" length="32"
		display="0" type="string" />
	<item id="pregnantId" alias="孕妇档案编号" length="16" display="2"
		type="string" />
	<item id="visitDate" alias="随访日期" not-null="1" type="date"
		defaultValue="%server.date.today" maxValue="%server.date.today" />
	<item id="birthDay" alias="出生日期" not-null="1" type="date"
		enableKeyEvents="true" maxValue="%server.date.today"   display="2"/>
	<item id="postnatalDays" alias="产后天数" length="2"
		type="int" fixed="true" />
	<item id="temperature" alias="体温(℃)" type="double" not-null="1" length="6" precision="2"
		display="2" />
	<item id="healthState" alias="一般健康情况描述" length="64" not-null="1"
		display="2" type="string"  colspan="2"/>
	<item id="psychologyState" alias="一般心理状况描述" length="64" not-null="1"
		display="2" type="string"  colspan="2"/>
	<item id="constriction" alias="收缩压(mmHg)" type="int" not-null="1"
		display="2" minValue="10" maxValue="500" enableKeyEvents="true" />
	<item id="diastolic" alias="舒张压(mmHg)" type="int" not-null="1"
		display="2" minValue="10" maxValue="500" enableKeyEvents="true" />
	<item id="breast" alias="乳房" type="string" display="2">
		<dic>
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="breastText" alias="乳房异常描述" type="string" display="2"
		length="200"/>
	<item id="lochia" alias="恶露" type="string" display="2"
		not-null="1">
		<dic>
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="lochiaText" alias="恶露异常描述" type="string" display="2"
		length="200"/>
	<item id="lochiaColor" alias="恶露色" type="string" display="2"
		not-null="1">
		<dic>
			<item key="1" text="红" />
			<item key="2" text="淡红" />
			<item key="3" text="白" />
		</dic>
	</item>
	<item id="lochiaStench" alias="恶露臭" type="string" display="2"
		not-null="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="lochiaMeasure" alias="恶露量" type="string" display="2"
		not-null="1">
		<dic>
			<item key="1" text="多" />
			<item key="2" text="少" />
		</dic>
	</item>
	<item id="uterus" alias="子宫" type="string" display="2"  not-null="1">
		<dic>
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="uterusText" alias="子宫异常描述" type="string" display="2"
		length="200"/>
	<item id="wound" alias="伤口" type="string" display="2" not-null="1">
		<dic>
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="woundText" alias="伤口异常描述" type="string" display="2"
		length="200"/>
	<item id="palace" alias="宫底高度" type="int" not-null="1" length="3" display="2" />
	<item id="healing" alias="愈合情况" type="string" display="2"
		not-null="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="infectant" alias="感染" type="string" display="2"
		not-null="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="other" alias="其他" type="string" display="2"
		length="200"  colspan="2"/>
	<item id="classification" alias="分类" type="string" display="2"
		length="1">
		<dic>
			<item key="1" text="未见异常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="classificationText" alias="异常描述" type="string" display="2"
		length="200" fixed="true"  colspan="2"/>
	<item id="suggestion" alias="指导" length="45" display="2"
		type="string">
		<dic render="LovCombo">
			<item key="01" text="个人卫生" />
			<item key="02" text="心理" />
			<item key="03" text="膳食营养" />
			<!--<item key="04" text="避免致畸因素和疾病对胚胎的不良影响 " />
				  <item key="05" text="产前筛查宣传告知" />
				  <item key="06" text="运动" />
				  <item key="07" text="自我监测" />
				  <item key="08" text="分娩准备" />-->
			<item key="09" text="母乳喂养" />
			<item key="10" text="新生儿护理与喂养" />
			<!--<item key="11" text="婴儿喂养与营养" />
				  <item key="12" text="性保健" />
				  <item key="13" text="避孕" />-->
			<item key="99" text="其他" />
		</dic>
	</item>
	<item id="otherSuggestion" alias="其他指导" type="string" display="2"
		length="200" fixed="true"/>
	<item id="specialCases" alias="特殊情况记录" length="200" display="2"
		type="string"  colspan="2"/>
	<item id="referral" alias="是否转诊" type="string" display="2"
		length="1">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="reason" alias="原因" type="string" display="2" length="200"/>
	<item id="doccol" alias="机构及科室" type="string" display="2" length="50" />
	<item id="checkDoctor" alias="随访医生" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="nextVisitDate" alias="下次随访日期" type="date"  minValue="%server.date.today"/>
</entry>
