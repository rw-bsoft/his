<?xml version="1.0" encoding="UTF-8"?>

<entry id="chis.application.tb.schemas.TB_TuberculosisFirstVisit" alias="肺结核患者第一次入户随访">
	<item id="visitId" alias="随访ID" type="string" length="16" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32"/>
	<item id="RecordId" alias="档案编号" type="string" length="30"/>
	<item id="visitDate" alias="随访时间" type="date" not-null="1"/>
	<item id="visitType" alias="随访方式" type="string" length="1">
		<dic>
			<item key="1" text="门诊" />
			<item key="2" text="家庭" />
		</dic>
	</item>
	<item id="patientType" alias="患者类型" type="string" length="1">
		<dic>
			<item key="1" text="初治" />
			<item key="2" text="复治" />
		</dic>
	</item>
	<item id="sputumSatus" alias="痰菌情况" type="string" length="1">
		<dic>
			<item key="1" text="阳性" />
			<item key="2" text="阴性" />
			<item key="3" text="未查痰" />
		</dic>
	</item>
	<item id="resistanceStatus" alias="耐药情况" type="string" length="1">
		<dic>
			<item key="1" text="耐药" />
			<item key="2" text="非耐药" />
			<item key="3" text="未检测" />
		</dic>
	</item>
	<item id="symptomSign" alias="症状及体征" type="string" length="50">
		<dic render="Checkbox" columnWidth="120" columns="8">
			<item key="0" text="没有症状" />
			<item key="1" text="咳嗽咳痰" />
			<item key="2" text="低热盗汗" />
			<item key="3" text="咯血或血痰" />
			<item key="4" text="胸痛消瘦" />
			<item key="5" text="恶心纳差" />
			<item key="6" text="头痛失眠" />
			<item key="7" text="视物模糊" />
			<item key="8" text="皮肤瘙痒、皮疹" />
			<item key="9" text="耳鸣、听力下降" />
		</dic>
	</item>
	<item id="otherSymptomSign" alias="其他症状及体征" type="string" length="50"/>
	<item id="chemotherapy" alias="化疗方案" type="string" length="50"/>
	<item id="medicineUsage" alias="药物用法" type="string" length="1">
		<dic>
			<item key="1" text="每日" />
			<item key="2" text="间歇" />
		</dic>
	</item>
	<item id="medicineType" alias="药品剂型" type="string" length="10">
		<dic render="Checkbox">
			<item key="1" text="固定剂量复合制剂" />
			<item key="2" text="散装药" />
			<item key="3" text="板式组合药" />
			<item key="4" text="注射剂" />
		</dic>
	</item>
	<item id="supervisor" alias="督导人员" type="string" length="1">
		<dic>
			<item key="1" text="医生" />
			<item key="2" text="家属" />
			<item key="3" text="自服药" />
			<item key="4" text="其他" />
		</dic>
	</item>
	<item id="singleRoom" alias="单独的居室" type="string" length="1">
		<dic>
			<item key="1" text="有" />
			<item key="2" text="无" />
		</dic>
	</item>
	<item id="vertilation" alias="通风情况" type="string" length="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>
	<item id="smokePerDay" alias="吸烟每天" type="string" length="10"/>
	<item id="smokeDay" alias="吸烟天" type="string" length="10"/>
	<item id="drinkPerDay" alias="饮酒每天" type="string" length="10"/>
	<item id="drinkDay" alias="饮酒天" type="string" length="10"/>
	<item id="takeLocation" alias="取药地点" type="string" length="50"/>
	<item id="takeTime" alias="取药时间" type="date"/>
	<item id="medicineRecordFill" alias="服药记录卡的填写" type="string" length="1">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="medicineStore" alias="服药方法及药品存放" type="string" length="32">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="treatmentCourse" alias="肺结核治疗疗程" type="string" length="1">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="noRegularMedicineRisk" alias="不规律服药危害" type="string" length="1">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="medicineBadReactionTreatment" alias="服药后不良反应及处理" type="string" length="1">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="treatmentReferralSputum" alias="治疗期间复诊查痰" type="string" length="1">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="persistMedicineDuringOut" alias="外出期间如何坚持服药" type="string" length="1">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="lifestylePrecautions" alias="生活习惯及注意事项" type="string" length="1">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="closeContactCheck" alias="密切接触者检查" type="string" length="1">
		<dic>
			<item key="1" text="掌握" />
			<item key="2" text="未掌握" />
		</dic>
	</item>
	<item id="nextDate" alias="下次随访时间" type="date" not-null="1"/>
	<item id="visitDoctor" alias="评估医生签名" type="string" length="20" not-null="1"
		defaultValue="%user.userId">
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
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.date']</set>
	</item>
</entry>
