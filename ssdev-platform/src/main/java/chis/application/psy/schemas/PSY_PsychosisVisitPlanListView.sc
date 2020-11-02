<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.psy.schemas.PSY_PsychosisVisitPlanListView" tableName="chis.application.pub.schemas.PUB_VisitPlan" alias="精神病随访计划展示列表">
	<item id="planId" pkey="true" alias="计划识别" type="string" length="16" not-null="1" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="recordId" alias="档案编号" type="string" length="30" hidden="true"/>
	<item id="empiId" alias="EMPIID" type="string" length="32" hidden="true"/>
	<item id="visitId" alias="随访记录ID" type="string" length="16" hidden="true"/>
	<item id="businessType" alias="计划类型" type="string" length="2" display="0">
		<dic id="chis.dictionary.planType"/>
	</item>
	<item id="planDate" alias="计划随访日期" type="date" width="100" display="1"/>
	<item id="endDate" alias="计划结束日期" type="date" width="100" display="1"/>
	<item id="visitDate" alias="实际随访日期" type="date" width="100" length="1"/>
	<item id="planStatus" alias="计划状态" type="string" length="1" default="0">
		<dic>
			<item key="0" text="应访"/>
			<item key="1" text="已访"/>
			<item key="2" text="失访"/>
			<item key="3" text="未访"/>
			<item key="4" text="过访"/>
			<item key="8" text="结案"/>
			<item key="9" text="档案注销"/>
		</dic>
	</item>
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.phoneNumber" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" />
	<item ref="c.regionCode_text" alias="网格地址" display="0" />
	<item ref="d.status" display="0" />
	
	<item ref="d.manaDoctorId" alias="责任医生" type="string" length="20"
		not-null="1" update="false">
        <dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
        	 parentKey="%user.manageUnit.id" />
	</item>
	<item ref="d.manaUnitId" alias="管辖机构" type="string" length="20"
		width="165" queryable="true" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
	</item>
	
	<item ref="e.symptom" alias="目前症状" type="string" length="50" not-null="1" colspan="2">
		<dic id="chis.dictionary.symptom" render="LovCombo"/>
	</item>
	<item ref="e.symptomText" alias="其他" type="string" length="100" />
	<item ref="e.insight" alias="自知力" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="自知力完全" />
			<item key="2" text="自知力不全" />
			<item key="3" text="自知力缺失" />
		</dic>
	</item>
	<item ref="e.sleepQuality" alias="睡眠情况" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item ref="e.eatQuality" alias="饮食情况" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item ref="e.liveQuality" alias="个人生活料理" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item ref="e.houseWork" alias="家务劳动" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item ref="e.productiveLabor" alias="生产劳动及工作" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
			<item key="9" text="此项不适用" />
		</dic>
	</item>
	<item ref="e.study" alias="学习能力" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item ref="e.social" alias="社交能力" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item ref="e.familySocialImpact" alias="对家庭社会影响" type="string" length="20" defaultValue="0" not-null="1" colspan="4">
		<dic id="chis.dictionary.psyFamilySocialImpact" render="Checkbox" columnWidth="100" columns="6"/>
	</item>
	<item ref="e.lightAffray" alias="轻度滋事次数" type="int" />
	<item ref="e.causeTrouble" alias="肇事次数" type="int" />
	<item ref="e.causeAccident" alias="肇祸次数" type="int" />
	<item ref="e.selfHurt" alias="自伤次数" type="int" />
	<item ref="e.suicide" alias="自杀未遂次数" type="int" />
	<item ref="e.closeDoor" alias="关锁情况" length="1" type="string" not-null="1">
		<dic>
			<item key="1" text="无关锁" />
			<item key="2" text="关锁" />
			<item key="3" text="关锁已解除 " />
		</dic>
	</item>
	<item ref="e.hospitalization" alias="住院情况" length="1" type="string" not-null="1">
		<dic>
			<item key="0" text="从未住院"/>
			<item key="1" text="目前正在住院"/>
			<item key="2" text="既往住院，现未住院"/>
		</dic>
	</item>
	<item ref="e.lastHospitalizationTime" alias="末次出院时间" type="date" maxValue="%server.date.today"/>
	<item ref="e.isLabCheckup" alias="实验室检查" length="1" type="string" not-null="1">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item ref="e.labCheckup" alias="实验室检查描述" length="500" type="String" colspan="3"/>
	<item ref="e.medicine" alias="服药依从性" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="规律" />
			<item key="2" text="间断" />
			<item key="3" text="不服药" />
		</dic>
	</item>
	<item ref="e.adverseReactions" alias="药物不良反应" type="string" not-null="1"
		length="1">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item ref="e.adverseReactionsText" alias="不良反应" type="string" 
		length="200" />
	<item ref="e.visitType" alias="本次随访分类" type="string" length="1" fixed="true" not-null="1">
		<dic>
			<item key="1" text="不稳定" />
			<item key="2" text="基本稳定" />
			<item key="3" text="稳定" />
			<item key="0" text="未访到"/>
		</dic>
	</item>
	<item ref="e.visitEffect" alias="转归" type="string" defaultValue="1" not-null="1"
		length="1">
		<dic>
			<item key="1" text="继续随访" />
			<item key="2" text="暂时失访" />
			<item key="9" text="终止管理" />
		</dic>
	</item>
	<item ref="e.lostReason" alias="原因" type="String" length="200" colspan="3"/>
	<!--
	<item id="isReferral" alias="是否已转诊" type="string" length="1" defalutValue="n" not-null="1"> 
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	-->
	<item ref="e.healing" alias="康复措施" type="string" length="20" colspan="2" not-null="1">
		<dic render="LovCombo">
			<item key="1" text="生活劳动能力" />
			<item key="2" text="职业训练" />
			<item key="3" text="学习能力" />
			<item key="4" text="社会交往" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item ref="e.healingText" alias="其他" type="string" length="50" />
	<item ref="e.treatment" alias="治疗效果" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="痊愈" />
			<item key="2" text="好转" />
			<item key="3" text="无变化" />
			<item key="4" text="加重" />
		</dic>
	</item>
	<item ref="e.riskFactor" alias="危害度" type="string" length="200" colspan="3" not-null="1">
		<dic>
			<item key="1" text="头威胁，喊叫，但没有打砸行为" />
			<item key="2" text="打砸行为，局限在家里，针对财物。能被劝说制止" />
			<item key="3" text="明显打砸行为，不分场合，针对财物。不能接受劝说而停止" />
			<item key="4" text="持续的打砸行为，不分场合，针对财物或人，不能接受劝说而停止" />
			<item key="5" text="持管制性危险武器的针对人的任何暴力行为，或者纵火，爆炸等行为" />
		</dic>
	</item>
	<item ref="e.dangerousGrade" alias="危险性" type="String" length="1" defaultValue="0" fixed="true" not-null="1">
		<dic>
			<item key="0" text="0级"/>
			<item key="1" text="1级"/>
			<item key="2" text="2级"/>
			<item key="3" text="3级"/>
			<item key="4" text="4级"/>
			<item key="5" text="5级"/>
		</dic>
	</item>
	
	<item ref="e.referral" alias="是否转诊" type="string" length="1" not-null="1">
		<dic id="chis.dictionary.yesOrNo"/>
	</item> 
	<item ref="e.reason" alias="原因" type="string" length="50" colspan="2"/>
	<item ref="e.doccol" alias="科室" type="string" length="50" />
	
	<item ref="e.nextDate" alias="下次随访日期" type="date" not-null="1"/>
	<item ref="e.visitDoctor" alias="随访医生" type="string" length="20"
		not-null="1" defaultValue="%user.userId" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item ref="e.visitUnit" alias="随访机构" type="string" length="20"
		width="165" fixed="true" defaultValue="%user.manageUnit.id" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<!--
	<item ref="e.type" alias="随访类型" type="string" length="1" display="0">
		<dic>
			<item key="0" text="首次随访" />
			<item key="1" text="计划随访" />
		</dic>
	</item>
	-->
	<item ref="e.inputUser" alias="录入员工" type="string" length="20" 
		update="false" fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item ref="e.inputUnit" alias="录入单位" type="string" update="false" 
		length="20" fixed="true" defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" 
			onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item ref="e.inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item ref="e.lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item ref="e.lastModifyUnit" alias="最后修改单位" type="string" length="20" 
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" 
			onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item ref="e.lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	
	
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "empiId" child = "empiId" />
		</relation>
		<relation type="children" entryName="chis.application.psy.schemas.PSY_PsychosisRecord">
			<join parent="empiId" child="empiId" />
		</relation>
		<relation type="children" entryName="chis.application.psy.schemas.PSY_PsychosisVisit">
			<join parent = "empiId" child = "empiId" />
			<join parent="visitId" child="visitId" />
		</relation>	
	</relations>
</entry>