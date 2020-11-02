<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.qcm.schemas.QCM_HYVisit" tableName="chis.application.hy.schemas.MDC_HypertensionVisit" alias="高血压随访">
	<item id="visitId" alias="随访标识" type="string" display="0"
		length="16" not-null="1" pkey="true" fixed="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	


	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>  
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.mobileNumber" display="1" queryable="true"/>  
	<item ref="c.regionCode" display="1" queryable="true"/>  
	
	<item ref="d.hypertensionGroup" alias="组别" type="string" length="2" fixed="true" display="1"> 
		<dic> 
			<item key="01" text="一组"/>  
			<item key="02" text="二组"/>  
			<item key="03" text="三组"/>  
		</dic> 
	</item>  
	<item id="visitDate" alias="随访日期" type="date" width="100" length="1" display="1"/>	
	<item id="visitWay" alias="随访方式" type="string" length="1" not-null="true"> 
		<dic id="chis.dictionary.visitWay"/> 
	</item>  	
	<item ref="d.manaUnitId" alias="管辖机构" type="string" length="20" display="1" queryable="true"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" parentKey="%user.manageUnit.id" rootVisible="true"/> 
	</item>  	
	<item ref="d.manaDoctorId" alias="责任医生" type="string" length="20" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>	
	
	<item id="visitEffect" alias="转归" type="string" length="1" defaultValue="1"> 
		<dic> 
			<item key="1" text="继续随访"/>  
			<item key="2" text="暂时失访"/>  
			<item key="9" text="终止管理"/> 
		</dic> 
	</item>  
	<item id="noVisitReason" alias="原因" type="string" length="100" colspan="2" anchor="100%" fixed="true"> 
		<dic> 
			<item key="1" text="死亡"/>  
			<item key="2" text="迁出"/>  
			<item key="3" text="失访"/> 
			<item key="4" text="拒绝"/> 
		</dic> 
	</item> 	
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="medicine" alias="服药依从性" type="string" length="1" enableKeyEvents="true" not-null="1"> 
		<dic> 
			<item key="1" text="规律"/>  
			<item key="2" text="间断"/>  
			<item key="3" text="不服药"/>  
			<item key="4" text="拒绝服药"/> 
		</dic> 
	</item> 
	<item id="inputUnit" alias="录入机构" type="string" length="20" colspan="2" update="false" defaultValue="%user.manageUnit.id" fixed="true"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.manageUnit.id']</set> 
	</item>  
	<item id="inputUser" alias="录入者" type="string" length="20" update="false" defaultValue="%user.userId" fixed="true" queryable="true"> 
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.userId']</set> 
	</item>  
	<item id="inputDate" alias="录入日期" type="datetime" xtype="datefield" update="false" defaultValue="%server.date.today" fixed="true" queryable="true"> 
		<set type="exp">['$','%server.date.datetime']</set> 
	</item> 	
	
	
	
	
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1"
		display="0" />

	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />
 

	<item id="currentSymptoms" alias="目前症状" type="string" length="64"
		colspan="2" defaultValue="9" display="0">
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
	<item id="otherSymptoms" alias="其他症状" type="string" fixed="true"
		colspan="2" length="64" display="0"/>

	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int" 
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" display="0"/>
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" display="0"/>
	<item id="weight" alias="体重(kg)" type="double" minValue="0"
		maxValue="500" enableKeyEvents="true" validationEvent="false" display="0"/>
	<item id="targetWeight" alias="目标体重(kg)" type="double" minValue="0"
		maxValue="500" enableKeyEvents="true" validationEvent="false" display="0"/>

	<item id="bmi" alias="BMI" length="6" type="double" fixed="true" display="0"/>
	<item id="targetBmi" alias="目标BMI" length="6" type="double" fixed="true" display="0"/>
	<item id="heartRate" alias="心率" not-null="1" type="int" display="0"/>
	<item id="targetHeartRate" alias="目标心率" type="int" display="0"/>
	<item id="otherSigns" alias="其它体征" type="string" length="20" display="0"/>

	<item id="smokeCount" alias="日吸烟量(支)" not-null="1" type="int"
		enableKeyEvents="true" display="0"/>
	<item id="targetSmokeCount" alias="目标量(支)" type="int" display="0"/>
	<item id="drinkCount" alias="日饮酒量(两)" not-null="1" type="int"
		enableKeyEvents="true" display="0"/>
	<item id="drinkTypeCode" alias="饮酒种类" type="string"
		 length="64" display="0">
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
	<item id="targetSalt" alias="目标摄盐情况" type="int" display="0">
		<dic id="chis.dictionary.salt"/>
	</item>

	<item id="medicineNot" alias="不规律服药原因" fixed="true" colspan="2" type="string" length="2" display="0">
		<dic>
			<item key="1" text="经济原因" />
			<item key="2" text="忘记" />
			<item key="3" text="不良反应" />
			<item key="4" text="配药不方便" />
			<item key="99" text="其他" />
		</dic>
	</item>
	<item id="medicineOtherNot" alias="其他原因" fixed="true" type="string" length="100"  colspan="2" display="0">
	</item>
	<item id="medicineBadEffect" alias="药物不良反应" type="string"
		fixed="true" defaultValue="1" display="0">
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
	<item id="riskiness" alias="危险因素" type="string" defaultValue="0"
		length="64" display="0">
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
	<item id="riskLevel" alias="危险分层" type="string" length="1"
		fixed="true" display="0">
		<dic id="chis.dictionary.riskLevel"/>
	</item>
	<item id="visitEvaluate" alias="随访分类"  colspan="2" type="string" length="1" display="0">
		<dic>
			<item key="1" text="控制满意" />
			<item key="2" text="控制不满意" />
			<item key="3" text="不良反应" />
			<item key="4" text="并发症" />
		</dic>
	</item>
	<item id="healthProposal" alias="健康处方建议"  colspan="3" type="string" length="100" display="0">
		<dic  render="LovCombo">
			<item key="1-1" text="去医院确定治疗方案" />
			<item key="1-2" text="坚持按医嘱服药" />
			<item key="1-3" text="需要调整方案" />
			<item key="1-4" text="去医院进一步确诊" />
			<item key="2-1" text="定期测量血压" />
			<item key="2-2" text="增加测量血压频率" />
			<item key="2-3" text="接受技能指导" />
			<item key="3-1" text="阅读发放的宣传材料" />
			<item key="4-1" text="限制烟量或戒烟" />
			<item key="4-2" text="戒烟" />
			<item key="4-3" text="避免被动吸烟" />
			<item key="5-1" text="减少或不要饮酒" />
			<item key="6-1" text="限钠盐(＜1斤/3人*月)" />
			<item key="6-2" text="减少脂肪食品摄入" />
			<item key="6-3" text="增加鱼、禽、奶制品摄入" />
			<item key="6-4" text="增加新鲜水果蔬菜摄入" />
			<item key="6-5" text="减少谷类，面制品摄入" />
			<item key="7-1" text="开始低强度的运动" />
			<item key="7-2" text="接受技能指导" />
			<item key="7-3" text="逐步增加运动强度或延长运动时间" />
			<item key="7-4" text="逐步减少运动强度或缩短运动时间" />
			<item key="8-1" text="放松心情，调节睡眠，注意休息" />
		</dic>
	</item>
	<item id="agencyAndDept" alias="机构及科别" type="string" length="64" display="0"/>
	<!-- 新增 -->
	<item id="referralReason" alias="转诊原因" type="string" colspan="2" length="64" display="0"/>

	<!-- 新增 -->
	<item id="nextDate" alias="下次预约时间" type="date" display="0"/>
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






	
	<item id="lateInput" alias="延后录入" type="string" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
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
