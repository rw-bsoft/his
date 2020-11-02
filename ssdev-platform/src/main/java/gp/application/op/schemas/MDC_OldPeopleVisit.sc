<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="gp.application.op.schemas.MDC_OldPeopleVisit" alias="老年人随访记录" sort="a.phrId" version="1341050284140" filename="E:\gwcp\BSCHIS2.2_Tomcat\webapps\bschis2.2\WEB-INF\config\schema\mdc/MDC_OldPeopleVisit.xml"> 
	<item id="visitId" pkey="true" alias="随访标识" type="string" length="16" not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key> 
	</item>  
	<item id="phrId" alias="档案编号" type="string" not-null="1" length="30" hidden="true" display="0"/>  
	<item id="empiId" alias="EMPIID" type="string" length="32" not-null="1" hidden="true" display="0"/>  
	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>  
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.phoneNumber" display="1" queryable="true"/>  
	<item ref="c.regionCode" display="1" queryable="true"/>  
	<item ref="d.planId" display="0"/>  
	<item ref="c.status" display="0"/>  
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" queryable="true" not-null="1" display="1" defaultValue="%user.manageUnit.id"> 
		<dic id="chis.@manageUnit" render="Tree" onlySelectLeaf="true"/> 
	</item>  
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="1" display="1" queryable="true"> 
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/> 
	</item>  
	<item id="visitDate" alias="随访日期" type="date" not-null="1" queryable="true" defaultValue="%server.date.today"/>  
	<item id="visitDoctor" alias="随访医生" type="string" length="20" defaultValue="%user.userId" queryable="true" not-null="1"> 
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" /> 
	</item>  
	<item id="visitUnit" alias="随访机构" type="string" length="16" not-null="1" queryable="true" defaultValue="%user.manageUnit.id" fixed="true"> 
		<dic id="chis.@manageUnit" render="Tree" onlySelectLeaf="true"/> 
	</item>  
	<item id="nextDate" alias="下次预约时间" type="date" not-null="0" width="100"/>  
	<item id="visitType" alias="随访方式" type="string" length="1" not-null="1"> 
		<dic id="chis.dictionary.visitWay"/> 
	</item>  
	<item id="visitEffect" alias="转归" type="string" length="2" defaultValue="1"> 
		<dic> 
			<item key="1" text="继续随访"/>  
			<item key="2" text="暂时失访"/>  
			<item key="9" text="终止管理"/> 
		</dic> 
	</item>  
	<item id="noVisitReason" alias="原因" type="string" length="100" fixed="true" colspan="2"/>  
	<item id="nowSymptoms" alias="目前症状" type="string" length="20" not-null="1" defaultValue="1"> 
		<dic> 
			<item key="1" text="无不适"/>  
			<item key="2" text="新出现症状"/>  
			<item key="3" text="原症状持续"/>  
			<item key="4" text="需转诊"/> 
		</dic> 
	</item>  
	<item id="newSymptoms" alias="新症状描述" type="string" length="100" colspan="3" fixed="true" anchor="100%"/>  
	<item id="moveTreat" alias="治疗转诊" type="string" length="1" not-null="1" defaultValue="2"> 
		<dic id="chis.dictionary.yesOrNo"/> 
	</item>  
	<item id="weight" alias="体重(kg)" type="double" length="8" not-null="1" precision="2"/>  
	<item id="sbp" alias="收缩压(mmhg)" display="2" type="int" minValue="10" not-null="1" maxValue="500" enableKeyEvents="true" validationEvent="false"/>  
	<item id="dbp" alias="舒张压(mmhg)" display="2" type="int" minValue="10" not-null="1" maxValue="500" enableKeyEvents="true" validationEvent="false"/>  
	<item id="smokeFlag" alias="是否吸烟" type="string" length="1" not-null="1"> 
		<dic id="chis.dictionary.CV5101_24"/> 
	</item>  
	<item id="smokeCount" alias="日吸烟量(支)" type="int" length="3" fixed="true"/>  
	<item id="drinkFlag" alias="是否饮酒" type="string" length="2" not-null="1"> 
		<dic id="chis.dictionary.CV5101_26" render="Tree" minChars="1"/>
	</item>  
	<item id="drinkCount" alias="日饮酒量(两)" type="int" length="4" fixed="true"/>  
	<item id="drinkTypeCode" alias="饮酒种类" type="string" length="64" colspan="1" anchor="100%" fixed="true"> 
		<dic id="chis.dictionary.drinkTypeCode_life" render="LovCombo"/> 
	</item>  
	<item id="food" alias="饮食情况" type="string" length="1" not-null="1" defaultValue="1"> 
		<dic> 
			<item key="1" text="合理"/>  
			<item key="2" text="基本合理"/>  
			<item key="3" text="不合理"/> 
		</dic> 
	</item>  
	<item id="eateHabit" alias="饮食习惯" type="string" length="64" not-null="1" colspan="1" anchor="100%"> 
		<dic id="chis.dictionary.eateHabit" render="LovCombo"/> 
	</item>  
	<item id="trainFreqCode" alias="体育锻炼" type="string" length="2" not-null="1"> 
		<dic id="chis.dictionary.CV5101_28" render="Tree"/> 
	</item>  
	<item id="trainTimesWeek" alias="周运动次数" type="int" maxValue="999"/>  
	<item id="trainMinute" alias="每次时长(分)" type="int" maxValue="999"/>  
	<item id="psychologyChange" alias="心理调整" type="string" length="1" not-null="1" defaultValue="2"> 
		<dic> 
			<item key="1" text="良好"/>  
			<item key="2" text="一般"/>  
			<item key="3" text="差"/> 
		</dic> 
	</item>  
	<item id="obeyDoctor" alias="遵医行为" type="string" length="1" not-null="1" defaultValue="1"> 
		<dic> 
			<item key="1" text="良好"/>  
			<item key="2" text="一般"/>  
			<item key="3" text="差"/> 
		</dic> 
	</item>  
	<item id="pastHistory" alias="既往病史" type="string" length="100" not-null="1" colspan="2"/>  
	<item id="CHDPrevent" alias="冠心病预防" type="string" length="200" width="100"/>  
	<item id="OPPrevent" alias="骨质疏松预防" type="string" length="200"/>  
	<item id="treat" alias="治疗情况" type="string" length="100" colspan="2" not-null="1" xtype="textarea"/>  
	<item id="medication" alias="用药情况" type="string" length="100" not-null="1" colspan="2" xtype="textarea"/>  
	<item id="vaccine" alias="疫苗接种" type="string" length="200"/>  
	<item id="healthAssessment" alias="健康评价" type="string" length="1" not-null="1"> 
		<dic minChars="1"> 
			<item key="y" text="有异常" mCode="1"/>  
			<item key="n" text="无异常" mCode="2"/> 
		</dic> 
	</item>  
	<item id="healthException" alias="健康异常" type="string" length="100" fixed="true" colspan="2"/>  
	<item id="healthGuidance" alias="健康指导" type="string" length="10" not-null="1"> 
		<dic> 
			<item key="1" text="定期随访"/>  
			<item key="2" text="纳入慢性病患者健康管理"/>  
			<item key="3" text="建议复查"/>  
			<item key="4" text="建议转诊"/> 
		</dic> 
	</item>  
	<item id="riskControll" alias="危险因素控制" type="string" width="100" length="10" not-null="1"> 
		<dic render="LovCombo"> 
			<item key="1" text="戒烟"/>  
			<item key="2" text="健康饮酒"/>  
			<item key="3" text="饮食"/>  
			<item key="4" text="锻炼"/>  
			<item key="5" text="减体重"/> 
		</dic> 
	</item>  
	<item id="targetWeight" alias="目标体重(kg)" type="double" length="8" precision="2" fixed="true"/>  
	<item id="heartState" alias="心理状态与指导" type="string" length="10" width="100"> 
		<dic> 
			<item key="1" text="好"/>  
			<item key="2" text="可疑抑郁"/>  
			<item key="3" text="心理指导"/>  
			<item key="4" text="需转诊"/> 
		</dic> 
	</item>  
	<item id="visitItem" alias="下次随访事项" type="string" length="200" colspan="2"/>  
	<item id="nextVisitTarget" alias="下次随访目标" type="string" length="200" colspan="2" width="100"/>  
	<item id="inputUnit" alias="录入机构" type="string" length="16" not-null="1" hidden="true" queryable="true" update="false" defaultValue="%user.manageUnit.id" fixed="true" display="0"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />  
		<set type="exp">['$','%user.manageUnit.id']</set> 
	</item>  
	<item id="inputUser" alias="录入者" type="string" length="20" not-null="1" hidden="true" defaultValue="%user.userId" update="false" queryable="true" fixed="true" display="0"> 
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.userId']</set> 
	</item>  
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" display="0" defaultValue="%server.date.today" update="false" queryable="true" maxValue="%server.date.today" fixed="true" > 
		<set type="exp">['$','%server.date.datetime']</set> 
	</item>  
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="0"> 
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.userId']</set> 
	</item>  
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0" width="180" defaultValue="%user.manageUnit.id"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.manageUnit.id']</set> 
	</item>  
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" defaultValue="%server.date.today" display="0"> 
		<set type="exp">['$','%server.date.datetime']</set> 
	</item>  
	<item ref="c.regionCode_text" alias="网格地址" display="0"/>
	<item ref="e.manaDoctorId" display="0"/>    
	<relations> 
		<relation type="parent" entryName="gp.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="gp.application.fd.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation> 
		<relation type="parent" entryName="gp.application.pub.schemas.PUB_VisitPlan"> 
			<join parent="recordId" child="phrId"/>  
			<join parent="visitId" child="visitId"/> 
		</relation> 
		<relation type="parent" entryName="gp.application.op.schemas.MDC_OldPeopleRecord"> 
			<join parent="phrId" child="phrId"/> 
		</relation> 
	</relations> 
</entry>
