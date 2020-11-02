<?xml version="1.0" encoding="UTF-8"?>
 
<entry alias="高血压随访"  
	   tableName="chis.application.hy.schemas.MDC_HypertensionVisit" >
	<item id="visitId" alias="随访标识" type="string" display="0"
		length="16" not-null="1" pkey="true" fixed="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1"
		display="0" />
	<item ref="b.personName" display="1"  />
	<item ref="b.sexCode" display="1"   />
	<item ref="b.birthday" display="1"   />
	<item ref="b.idCard" display="1" />
	<item ref="b.phoneNumber" display="1"  />
	<item ref="c.regionCode" display="1"   />
	<item id="TP" alias="" type="string" renderer="onTpRenderer" width="23" length="20" virtual="true" display="1"/>
	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />
	 <item id="hypertensionGroup" alias="组别" type="string" length="2"
		fixed="true" >
		<dic id="chis.dictionary.QualityControl_ZKZB" />
	</item>

	<item id="visitDate" alias="随访日期" not-null="true" type="date"
		defaultValue="%server.date.today" enableKeyEvents="true"
		validationEvent="false"  />
	<item id="visitWay" alias="随访方式" type="string" length="1"
		not-null="true">
		<dic id="chis.dictionary.visitWay" />
	</item>
	<item id="visitEffect" alias="转归" type="string" defaultValue="1"
		length="1">
		<dic>
			<item key="1" text="继续随访" />
			<item key="2" text="暂时失访" />
			<item key="9" text="终止管理" />
		</dic>
	</item>
	<item id="noVisitReason" alias="原因" type="string" length="100"
		fixed="true" />

	<item id="currentSymptoms" alias="目前症状" type="string" length="64" 	display="0"
		colspan="2" defaultValue="9">
		<dic render="LovCombo">
			<item key="9" text="无症状" />
			<item key="1" text="头痛头晕" />
			<item key="2" text="恶心呕吐" />
			<item key="3" text="眼花耳鸣" />
			<item key="4" text="呼吸困难" />
			<item key="5" text="心悸胸闷" />
			<item key="6" text="鼻衄出血不止" />
			<item key="7" text="四肢发麻" />
			<item key="8" text="下肢水肿" />
			<item key="10" text="其他" />
		</dic>
	</item>
	<!-- 新增 -->
	<item id="otherSymptoms" alias="其他症状" type="string" fixed="true" 	display="0"
		colspan="2" length="64" />

	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int"  	display="0"
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int" 	display="0"
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="weight" alias="体重(kg)" type="double" minValue="0" 	display="0"
		maxValue="500" enableKeyEvents="true" validationEvent="false" />
	<item id="targetWeight" alias="目标体重(kg)" type="double" minValue="0" 	display="0"
		maxValue="500" enableKeyEvents="true" validationEvent="false" />

	<item id="bmi" alias="BMI" length="6" type="double" fixed="true" 	display="0" />
	<item id="targetBmi" alias="目标BMI" length="6" type="double" fixed="true" 	display="0"/>
	<item id="heartRate" alias="心率" not-null="1" type="int" 	display="0"/>
	<item id="targetHeartRate" alias="目标心率" type="int" 	display="0"/>
	<item id="otherSigns" alias="其它体征" type="string" length="20" 	display="0"/>

	<item id="smokeCount" alias="日吸烟量(支)" not-null="1" type="int" 	display="0"
		enableKeyEvents="true" />
	<item id="targetSmokeCount" alias="目标量(支)" type="int" 	display="0" />
	<item id="drinkCount" alias="日饮酒量(两)" not-null="1" type="int" 	display="0"
		enableKeyEvents="true" />
		
		
		
		
		
	<item id="drinkTypeCode" alias="饮酒种类" type="string" display="0"
	  length="64">
		<dic id="chis.dictionary.drinkTypeCode" render="LovCombo" />
	</item>
	

	<item id="targetDrinkCount" alias="目标量(两)" type="int" display="0"/>
	<item id="trainTimesWeek" alias="周运动次数" not-null="1" type="int" display="0"/>
	<item id="targetTrainTimesWeek" alias="目标次数" type="int" display="0"/>
	<item id="trainMinute" alias="每次时长(分)" not-null="1" type="int" display="0"/>

	<item id="targetTrainMinute" alias="目标时长(分)" type="int" display="0"/>
	<item id="loseWeight" alias="是否减轻体重" not-null="1" type="string" display="0"
		length="1" fixed="true" defaultValue="1">
		<dic>
			<item key="1" text="不需要" />
			<item key="2" text="需要" />
		</dic>
	</item>
	<item id="salt" alias="摄盐情况" type="int" display="0">
		<dic id="chis.dictionary.salt"/>
	</item>
	<item id="targetSalt" alias="目标摄盐情况" type="int" display="0" >
		<dic id="chis.dictionary.salt"/>
	</item>
	<item id="medicine" alias="服药依从性" not-null="1" type="string" length="1" display="0">
		<dic>
			<item key="1" text="规律" />
			<item key="2" text="间断" />
			<item key="3" text="不服药" />
			<item key="4" text="拒绝服药" />
		</dic>
	</item>
	<item id="medicineBadEffect" alias="药物不良反应" type="string" display="0"
		fixed="true" defaultValue="1">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="medicineBadEffectText" alias="不良反应" type="string" display="0"
		length="200" />
	<item id="auxiliaryCheck" alias="辅助检查" type="string" length="50" display="0"/>
	<item id="psychologyChange" alias="心理调整" type="string" length="1" display="0">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>
	<item id="obeyDoctor" alias="遵医行为" type="string" length="1" display="0">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>
	<!-- item id="diet" alias="饮食" type="string" length="1">
			<dic>
				<item key="1" text="规律"/>
				<item key="2" text="不规律"/>
			</dic>
		</item -->
	<item id="riskiness" alias="危险因素" type="string" defaultValue="0" display="0"
		length="64" >
		<dic id="chis.dictionary.hyperRiskiness" render="LovCombo" />
	</item>

	<item id="targetHurt" alias="靶器官损害" type="string" defaultValue="0" display="0"
		length="64" colspan="2">
		<dic id="chis.dictionary.targetHurt"  render="LovCombo" />
	</item>
	
	<item id="complication" alias="并发症" type="string" length="64" display="0"
		defaultValue="0" colspan="2">
		<dic id="chis.dictionary.complication"  render="LovCombo" />
	</item>
	<item id="complicationIncrease" alias="原并发症加重" type="string" length="1" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>	
	<item id="riskLevel" alias="危险分层" type="string" length="1" display="0"
		fixed="true" >
		<dic id="chis.dictionary.riskLevel"/>
	</item>
	<item id="visitEvaluate" alias="随访分类" type="string" length="1" display="0">
		<dic>
			<item key="1" text="控制满意" />
			<item key="2" text="控制不满意" />
			<item key="3" text="不良反应" />
			<item key="4" text="并发症" />
		</dic>
	</item>
	<item id="agencyAndDept" alias="机构及科别" type="string" length="64" display="0"/>
	<!-- 新增 -->
	<item id="referralReason" alias="转诊原因" type="string" colspan="2" length="64" display="0"/>

	<!-- 新增 -->
	<item id="nextDate" alias="下次预约时间" type="date" display="0" />
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitUnit" alias="随访机构" type="string" length="20"
		display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
	</item>

	<item id="treatEffect" alias="治疗效果" type="string" length="2"
		fixed="true" display="0">
		<dic>
			<item key="1" text="血压正常" />
			<item key="2" text="血压偏高" />
		</dic>
	</item>
	<item id="cardiovascularEvent" alias="心血管事件" defaultValue="1"
		type="string" length="1" display="0">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="waistLine" alias="腰围(cm)" type="double" length="4"
		minValue="40" maxValue="200" display="0" />
	
	<item id="incorrectMedicine" alias="不规律原因" type="string" length="64"
		fixed="true" display="0">
		<dic render="LovCombo">
			<item key="1" text="经济原因" />
			<item key="2" text="忘记" />
			<item key="3" text="不良发应" />
			<item key="4" text="配药不方便" />
		</dic>
	</item>

	<item id="noMedicine" alias="不服药原因" type="string" length="64"
		fixed="true" colspan="2" display="0">
		<dic render="LovCombo">
			<item key="1" text="经济原因" />
			<item key="2" text="忘记" />
			<item key="3" text="不良发应" />
			<item key="4" text="配药不方便" />
			<item key="5" text="不需要药物治疗" />
			<item key="6" text="其它" />
		</dic>
	</item>
	<item id="otherReason" alias="其他原因" type="string" length="100"
		fixed="true" display="0" />

	<!-- 
			先不要。
		-->
	<item id="healthRecipe" alias="健康处方建议" type="string" length="64"
		colspan="2" display="0">
		<dic render="LovCombo" />
	</item>
	<item id="nonMedicineWay" alias="非药物疗法" type="string" length="64"
		colspan="2" display="0">
		<dic render="LovCombo">
			<item key="9" text="无" />
			<item key="1" text="限盐" />
			<item key="2" text="减少吸烟量或者戒烟" />
			<item key="3" text="减少饮酒量或戒酒" />
			<item key="4" text="减少膳食脂肪" />
			<item key="5" text="减轻体重" />
			<item key="6" text="有规律体育运动" />
			<item key="7" text="放松情绪" />
			<item key="8" text="其他措施" />
		</dic>
	</item>
	<item id="acceptDegree" alias="患者接受程度" type="string" length="1"
		display="0">
		<dic>
			<item key="1" text="完全接受" />
			<item key="2" text="勉强接受" />
			<item key="3" text="不接受" />
		</dic>
	</item> 
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.manageUnit.id" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入员" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.userId"  >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false" 
		defaultValue="%server.date.today"   >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>

	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		display="0"   defaultValue="%user.manageUnit.id"  >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id"  />
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20"
		display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	
	<item id="lateInput" alias="延后录入" type="string" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
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
	
	<item ref="d.status" display="0" />
	<item ref="c.regionCode_text" alias="网格地址" display="0" />
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
		<relation type="children" entryName="chis.application.hy.schemas.MDC_HypertensionRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
