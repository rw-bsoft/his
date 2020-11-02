<?xml version="1.0" encoding="UTF-8"?>

<entry id="chis.application.tb.schemas.TB_TuberculosisVisitList" alias="肺结核患者随访列表" tableName="chis.application.tb.schemas.TB_TuberculosisVisit">
	<item id="visitDate" alias="随访时间" type="date"/>
	<item id="treatmentMonthOrder" alias="治疗月序" type="string" length="10"/>
	<item id="nextDate" alias="下次随访时间" type="date"/>
	<item id="visitDoctor" alias="随访医生签名" type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			 keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	
	
	<item id="visitId" alias="随访ID" type="string" length="16" not-null="1" display="0" hidden="true" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" display="0" hidden="true" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" display="0" hidden="true" length="32"/>
	<item id="RecordId" alias="档案编号" type="string" display="0" hidden="true" length="30"/>
	<item id="supervisor" alias="督导人员" type="string" length="1" display="0" hidden="true">
		<dic>
			<item key="1" text="医生" />
			<item key="2" text="家属" />
			<item key="3" text="自服药" />
			<item key="4" text="其他" />
		</dic>
	</item>
	<item id="visitType" alias="随访方式" type="string" length="1" display="0" hidden="true">
		<dic>
			<item key="1" text="门诊" />
			<item key="2" text="家庭" />
			<item key="3" text="电话" />
		</dic>
	</item>
	<item id="symptomSign" alias="症状及体征" type="string" length="50" display="0" hidden="true">
		<dic>
			<item key="0" text="没有症状" />
			<item key="1" text="咳嗽咳痰" />
			<item key="2" text="低热盗汗" />
			<item key="3" text="咯血或血痰" />
			<item key="4" text="胸痛消瘦" />
			<item key="5" text="恶心纳差" />
			<item key="6" text="关节疼痛" />
			<item key="7" text="头痛失眠" />
			<item key="8" text="视物模糊" />
			<item key="9" text="皮肤瘙痒、皮疹" />
			<item key="10" text="耳鸣、听力下降" />
		</dic>
	</item>
	<item id="otherSymptomSign" alias="其他症状及体征" type="string" length="50" display="0" hidden="true"/>
	<item id="smokePerDay" alias="吸烟每天" type="string" length="10" display="0" hidden="true"/>
	<item id="smokeDay" alias="吸烟天" type="string" length="10" display="0" hidden="true"/>
	<item id="drinkPerDay" alias="饮酒每天" type="string" length="10" display="0" hidden="true"/>
	<item id="drinkDay" alias="饮酒天" type="string" length="10" display="0" hidden="true"/>
	<item id="chemotherapy" alias="化疗方案" type="string" length="50" display="0" hidden="true"/>
	<item id="medicineUsage" alias="药物用法" type="string" length="1" display="0" hidden="true">
		<dic>
			<item key="1" text="每日" />
			<item key="2" text="间歇" />
		</dic>
	</item>
	<item id="medicineType" alias="药品剂型" type="string" length="10" display="0" hidden="true">
		<dic>
			<item key="1" text="固定剂量复合制剂" />
			<item key="2" text="散装药" />
			<item key="3" text="板式组合药" />
			<item key="4" text="注射剂" />
		</dic>
	</item>
	<item id="missMedicineNum" alias="漏服药次数" type="string" length="10" display="0" hidden="true"/>
	<item id="medicineBadReaction" alias="药物不良反应" type="string" length="1" display="0" hidden="true">
		<dic>
			<item key="1" text="无" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item id="medicineBadReactionDesc" alias="药物不良反应详情" type="string" length="100" display="0" hidden="true"/>
	<item id="complicationComorbidity" alias="并发症或合并症" type="string" length="1" display="0" hidden="true">
		<dic>
			<item key="1" text="无" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item id="complicationComorbidityDesc" alias="并发症或合并症详情" type="string" length="100" display="0" hidden="true"/>
	<item id="referralDivision" alias="转诊科别" type="string" length="50" display="0" hidden="true"/>
	<item id="referralReason" alias="转诊原因" type="string" length="100" display="0" hidden="true"/>
	<item id="twoWeekVisitResult" alias="2周内随访的结果" type="string" length="100" display="0" hidden="true"/>
	<item id="processAdvice" alias="处理意见" type="string" length="100" display="0" hidden="true"/>
	<item id="stopTreatmentTime" alias="停止治疗时间" type="date" display="0" hidden="true"/>
	<item id="stopTreatmentReason" alias="停止治疗原因" type="string" length="20" display="0" hidden="true">
		<dic>
			<item key="1" text="完成疗程" />
			<item key="2" text="死亡" />
			<item key="3" text="丢失" />
			<item key="4" text="转入耐多药治疗" />
		</dic>
	</item>
	<item id="shouldVisitNum" alias="应访视次数" type="string" length="10" display="0" hidden="true"/>
	<item id="actualVisitNum" alias="实际访视次数" type="string" length="10" display="0" hidden="true"/>
	<item id="shouldMedicineNum" alias="应服药次数" type="string" length="10" display="0" hidden="true"/>
	<item id="actualMedicineNum" alias="实际服药次数" type="string" length="10" display="0" hidden="true"/>
	<item id="assessDoctor" alias="评估医生" type="string" length="20" display="0" hidden="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" 
			 keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
  
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="0">
		<set type="exp">['$','%server.date.date']</set>
	</item>
</entry>
