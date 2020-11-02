<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.HC_HealthCheck" alias="基本情况" sort="checkDate desc" >
	<item id="healthCheck" alias="检查单号" length="16" width="130"
		type="string" pkey="true" generator="assigned" not-null="1" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPIID" length="32"
		type="string" display="0" />
	<item id="phrId" alias="健康档案号" length="30" type="string"
		display="0" />
	<item queryable="true" id="checkDate" alias="年检日期" type="date"
		not-null="true" defaultValue="%server.date.today"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item ref="b.contactPhone" display="1" queryable="true"/>
	<item ref="c.regionCode" 	display="1" queryable="true"/> 
	<item id="checkWay" alias="检查途径"
		length="20" type="string" queryable="true" width="200" display="1" defaultValue="1">
		<dic render="LovCombo">
			<item key="1" text="健康档案" />
			<item key="2" text="老年人" />
			<item key="3" text="高血压" />
			<item key="4" text="糖尿病" />
			<item key="5" text="精神病" />
		</dic>
	</item>
	<item id="personalCode" alias="个人编码" length="30"
		type="string" display="0" />
	<item queryable="true" id="symptom" alias="症状" length="80" type="string"
		colspan="2" not-null="true" >
		<dic render="LovCombo">
			<item key="01" text="无症状" />
			<item key="02" text="头痛" />
			<item key="03" text="头晕" />
			<item key="04" text="心悸" />
			<item key="05" text="胸闷" />
			<item key="06" text="胸痛" />
			<item key="07" text="慢性咳嗽" />
			<item key="08" text="咳痰" />
			<item key="09" text="呼吸困难" />
			<item key="10" text="多饮" />
			<item key="11" text="多尿" />
			<item key="12" text="体重下降" />
			<item key="13" text="乏力" />
			<item key="14" text="关节肿痛" />
			<item key="15" text="视力模糊" />
			<item key="16" text="手脚麻木" />
			<item key="17" text="尿急" />
			<item key="18" text="尿痛" />
			<item key="19" text="便秘" />
			<item key="20" text="腹泻" />
			<item key="21" text="恶心呕吐" />
			<item key="22" text="眼花" />
			<item key="23" text="耳鸣" />
			<item key="24" text="乳房胀痛" />
			<item key="25" text="其他" />
		</dic>
	</item>
	<item id="symptomOt" alias="其他症状" length="100"
		type="string" fixed="true" display="2" />
	<item id="temperature" alias="体温(℃)" length="5"
		precision="2" type="double" display="2" not-null="true" />
	<item id="breathe" alias="呼吸(次/分)" type="int" maxValue="100" not-null="true" display="2" />
	<item id="pulse" alias="脉率(次/分)" type="int" maxValue="200" not-null="true" display="2" />
	<item id="constriction_L" alias="收缩压L(mmHg)" width="110"
		minValue="10" maxValue="500" type="int" not-null="true" queryable="true" />
	<item id="diastolic_L" alias="舒张压L(mmHg)" width="110"
		minValue="10" maxValue="500" type="int" not-null="true" queryable="true" />
	<item id="constriction" alias="收缩压R(mmHg)" width="110"
		minValue="10" maxValue="500" type="int" not-null="true" queryable="true" />
	<item id="diastolic" alias="舒张压R(mmHg)" minValue="10" width="110"
		maxValue="500" type="int" not-null="true" queryable="true" />
	<item queryable="true" id="height" alias="身高(cm)" length="8"
		precision="2" type="double" not-null="true" />
	<item queryable="true" id="weight" alias="体重(kg)" length="8"
		precision="2" type="double" not-null="true" />
	<item id="waistline" alias="腰围(cm)" length="8"
		precision="2" type="double" not-null="true" />
	<item queryable="true" id="bmi" alias="BMI(Kg/m2)" length="8"
		precision="2" type="double" fixed="true" />
	<!--  <item id="healthStatus" alias="老年人健康状态" length="1"
		type="string" display="2">
		<dic>
			<item key="1" text="满意" />
			<item key="2" text="基本满意" />
			<item key="3" text="说不清楚" />
			<item key="4" text="不太满意" />
			<item key="5" text="不满意" />
		</dic>
	</item>
	<item id="selfCare" alias="老年人自理能力" length="1"
		type="string" display="2" >
		<dic>
			<item key="1" text="可自理" />
			<item key="2" text="轻度依赖" />
			<item key="3" text="中度依赖" />
			<item key="4" text="不能自理" />
		</dic>
	</item>
	<item id="cognitive" alias="老年人认知" length="20"
		type="string" display="2">
		<dic>
			<item key="1" text="粗筛阴性" />
			<item key="2" text="粗筛阳性" />
		</dic>
	</item>
	<item id="emotion" alias="老年人情感状态" length="20"
		type="string" display="2">
		<dic>
			<item key="1" text="粗筛阴性" />
			<item key="2" text="粗筛阳性" />
		</dic>
	</item>
	-->
	
	
	<item id="cognitiveZf" alias="智力检查总分" length="5"
		precision="2" type="double"  display="2"/>
	
	<item id="emotionZf" alias="抑郁评分" length="5"
		precision="2" type="double" display="2" />
	<item id="cerebrovascularDiseases" alias="脑血管疾病"
		length="20" type="string" display="2">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="缺血性卒中" />
			<item key="3" text="脑出血" />
			<item key="4" text="蛛网膜下腔出血" />
			<item key="5" text="短暂性脑缺血发作" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item id="othercerebrovascularDiseases" alias="其他脑血管疾病"
		length="50" type="string" fixed="true" display="2" />
	<item id="heartDisease" alias="心脏疾病" length="20"
		type="string" display="2">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="心肌梗塞" />
			<item key="3" text="心绞痛" />
			<item key="4" text="冠状动脉血运重建" />
			<item key="5" text="充血性心力衰竭" />
			<item key="6" text="心前区疼痛" />
			<item key="7" text="其他" />
		</dic>
	</item>
	<item id="otherheartDisease" alias="其他心脏疾病"
		length="50" type="string" fixed="true" display="2"/>
	<item id="kidneyDiseases" alias="肾脏疾病" length="20"
		type="string" display="2">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="糖尿病肾病" />
			<item key="3" text="肾衰竭" />
			<item key="4" text="急性肾炎" />
			<item key="5" text="慢性肾炎" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item id="otherkidneyDiseases" alias="其他肾脏疾病"
		length="50" type="string" fixed="true" display="2" />
	<item id="VascularDisease" alias="血管疾病" length="20"
		type="string" display="2">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="夹层动脉瘤" />
			<item key="3" text="动脉闭塞性疾病" />
			<item key="4" text="其他" />
		</dic>
	</item>
	<item id="otherVascularDisease" alias="其他血管疾病"
		length="50" type="string" fixed="true" display="2" />
	<item id="eyeDiseases" alias="眼部疾病" length="20"
		type="string" display="2">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="视网膜出血或渗出" />
			<item key="3" text="视乳头水肿" />
			<item key="4" text="白内障" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="othereyeDiseases" alias="其他眼部疾病"
		length="50" type="string" fixed="true" display="2" />
	<item id="neurologicalDiseases" alias="神经疾病"
		length="1" type="string" display="2">
		<dic>
			<item key="1" text="未发现" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item id="neurologicalDiseasesDesc" alias="神经疾病描述"
		length="100" type="string" fixed="true" display="2" />
	<item id="otherDiseasesone" alias="其他疾病" length="1"
		type="string" display="2">
		<dic>
			<item key="1" text="未发现" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item id="otherDiseasesoneDesc" alias="其他疾病描述"
		length="100" type="string" fixed="true" display="2" />
	<item id="manaDoctorId" alias="责任医生" type="string" fixed="true" length="20" queryable="true" 
		defaultValue="%user.userId" not-null="1" update="false">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"  parentKey="%user.manageUnit.id"/>
	</item>
	<item id="inhospitalFlag" alias="住院情况" type="string" length="1" defaultValue="n" display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="infamilybedFlag" alias="家庭病床情况" type="string" length="1" defaultValue="n" display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="medicineFlag" alias="服药情况" type="string" length="1" defaultValue="n" display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="nonimmuneFlag" alias="接种情况" type="string" length="1" defaultValue="n" display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" colspan="2" anchor="100%" 
		display="1" width="180" not-null="1" fixed="true" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  includeParentMinLen="6" render="Tree"/>
	</item>
	<item queryable="true" id="createUser" alias="录入员工" length="20" update="false"
		type="string" fixed="true" defaultValue="%user.userId">
		<!--<dic id="chis.dictionary.Personnel"/>-->
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"  parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item queryable="true" id="createUnit" alias="录入单位" length="20" update="false"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item queryable="true" id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" width="100"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
		<relation type="children" entryName="chis.application.hc.schemas.HC_AccessoryExamination">
			<join parent="healthCheck" child="healthCheck" />
		</relation>
	</relations>
</entry>
