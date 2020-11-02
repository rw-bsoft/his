<?xml version="1.0" encoding="UTF-8"?>

<entry alias="精神病随访录入界面" entityName="gp.application.psyc.schemas.PSY_PsychosisVisit">
	<item id="visitId" alias="随访id" pkey="true" type="string" length="16"
		display="0" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>  
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.phoneNumber" display="1" queryable="true"/>  
	<item ref="c.regionCode" display="1" queryable="true"/>
	<item ref="c.status" display="0"/> 
	<item id="phrId" alias="档案编号" type="string" length="30" display="0" />
	<item id="empiId" alias="EMPIID" type="string" length="32" display="0" />
	<item id="visitDate" alias="随访日期" type="date" not-null="1" defaultValue="%server.date.today"/>
	<item id="symptom" alias="目前症状" type="string" length="50" colspan="2">
		<dic id="chis.dictionary.symptom" render="LovCombo"/>
	</item>
	<item id="symptomText" alias="其他" type="string" length="100" />
	<item id="insight" alias="自知力" type="string" length="1">
		<dic>
			<item key="1" text="自知力完全" />
			<item key="2" text="自知力不全" />
			<item key="3" text="自知力缺失" />
		</dic>
	</item>
	<item id="sleepQuality" alias="睡眠情况" type="string" length="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item id="eatQuality" alias="饮食情况" type="string" length="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item id="liveQuality" alias="个人生活料理" type="string" length="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item id="houseWork" alias="家务劳动" type="string" length="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item id="productiveLabor" alias="生产劳动及工作" type="string"
		length="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
			<item key="9" text="此项不适用" />
		</dic>
	</item>
	<item id="study" alias="学习能力" type="string" length="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item id="social" alias="社交能力" type="string" length="1">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="较差" />
		</dic>
	</item>
	<item id="lightAffray" alias="轻度滋事次数" type="int" />
	<item id="causeTrouble" alias="肇事次数" type="int" />
	<item id="causeAccident" alias="肇祸次数" type="int" />
	<item id="selfHurt" alias="自伤次数" type="int" />
	<item id="suicide" alias="自杀未遂次数" type="int" />
	<item id="closeDoor" alias="关锁情况" length="1" type="string" >
		<dic>
			<item key="1" text="无关锁" />
			<item key="2" text="关锁" />
			<item key="3" text="关锁已解除 " />
		</dic>
	</item>
	<item id="hospitalization" alias="住院情况" length="1" type="string">
		<dic>
			<item key="0" text="从未住院"/>
			<item key="1" text="目前正在住院"/>
			<item key="2" text="既往住院，现未住院"/>
		</dic>
	</item>
	<item id="lastHospitalizationTime" alias="末次出院时间" type="date" maxValue="%server.date.today"/>
	<item id="isLabCheckup" alias="实验室检查" length="1" type="string" >
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item id="labCheckup" alias="实验室检查描述" length="500" type="String" colspan="3"/>
	<item id="medicine" alias="服药依从性" type="string" length="1" not-null="1">
		<dic>
			<item key="1" text="规律" />
			<item key="2" text="间断" />
			<item key="3" text="不服药" />
		</dic>
	</item>
	<item id="adverseReactions" alias="药物不良反应" type="string"
		length="1">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="adverseReactionsText" alias="不良反应" type="string"
		length="200" />
	<item id="visitType" alias="本次随访分类" type="string" length="1" fixed="true" not-null="1">
		<dic>
			<item key="1" text="不稳定" />
			<item key="2" text="基本稳定" />
			<item key="3" text="稳定" />
			<item key="0" text="未访到"/>
		</dic>
	</item>
	<item id="visitEffect" alias="转归" type="string" defaultValue="1" not-null="1"
		length="1">
		<dic>
			<item key="1" text="继续随访" />
			<item key="2" text="暂时失访" />
			<item key="9" text="终止管理" />
		</dic>
	</item>
	<item id="lostReason" alias="原因" type="String" length="200" colspan="3"/>
	<item id="referral" alias="是否转诊" type="string" length="1" >
		<dic id="chis.dictionary.yesOrNo"/>
	</item> 
	<item id="reason" alias="原因" type="string" length="50" colspan="2"/>
	<item id="doccol" alias="科室" type="string" length="50" />
	<!--
		<item id="isReferral" alias="是否已转诊" type="string" length="1" defalutValue="n" not-null="1"> 
			<dic id="chis.dictionary.yesOrNo"/>
		</item>
		-->
	<item id="healing" alias="康复措施" type="string" length="20" colspan="2" >
		<dic render="LovCombo">
			<item key="1" text="生活劳动能力" />
			<item key="2" text="职业训练" />
			<item key="3" text="学习能力" />
			<item key="4" text="社会交往" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="healingText" alias="其他" type="string" length="50" />
	<item id="treatment" alias="治疗效果" type="string" length="1">
		<dic>
			<item key="1" text="痊愈" />
			<item key="2" text="好转" />
			<item key="3" text="无变化" />
			<item key="4" text="加重" />
		</dic>
	</item>
	<item id="riskFactor" alias="危害度" type="string" length="200" colspan="2">
		<dic>
			<item key="1" text="头威胁，喊叫，但没有打砸行为" />
			<item key="2" text="打砸行为，局限在家里，针对财物。能被劝说制止" />
			<item key="3" text="明显打砸行为，不分场合，针对财物。不能接受劝说而停止" />
			<item key="4" text="持续的打砸行为，不分场合，针对财物或人，不能接受劝说而停止" />
			<item key="5" text="持管制性危险武器的针对人的任何暴力行为，或者纵火，爆炸等行为" />
		</dic>
	</item>
	<item id="dangerousGrade" alias="危险性" type="String" length="1" defaultValue="0" fixed="true">
		<dic>
			<item key="0" text="0级"/>
			<item key="1" text="1级"/>
			<item key="2" text="2级"/>
			<item key="3" text="3级"/>
			<item key="4" text="4级"/>
			<item key="5" text="5级"/>
		</dic>
	</item>
	<item id="nextDate" alias="下次随访日期" type="date" />
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		not-null="1" defaultValue="%user.userId" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitUnit" alias="随访机构" type="string" length="20"
		width="165" fixed="true" defaultValue="%user.manageUnit.id" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="type" alias="随访类型" type="string" length="1" display="0">
		<dic>
			<item key="0" text="首次随访" />
			<item key="1" text="计划随访" />
		</dic>
	</item>
	<item id="inputUser" alias="录入员工" type="string" length="20" 
		update="false" fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入单位" type="string" update="false" 
		length="20" fixed="true" defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" 
			onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" 
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" 
			onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item ref="c.regionCode_text" alias="网格地址" display="0"/>
	<item ref="e.manaDoctorId" display="0"/>  
	<relations>
		<relation type="parent" entryName="gp.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="gp.application.fd.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation> 
		<relation type="parent" entryName="gp.application.psyc.schemas.PSY_PsychosisRecord"> 
			<join parent="phrId" child="phrId"/> 
		</relation> 
	</relations>
</entry>
