<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisit" alias="离休干部随访">
	<item id="visitId" alias="visitId" length="16" not-null="1" pkey="true" type="string"
		hidden="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="phrId" length="30" display="0" type="string"/>
	<item id="empiId" alias="empiId" length="32" display="0" type="string"/>
	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>  
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.phoneNumber" display="1" queryable="true"/>  
	<item ref="c.regionCode" display="1" queryable="true"/>  
	<item ref="d.planId" display="0"/>  
	<item ref="c.status" display="0"/> 
	<item id="visitDate" alias="随访时间" not-null="1" width="150" type="date"/>
	<item id="diet" alias="饮食" length="1" not-null="1" type="string" display="2">
		<dic>
			<item key="1" text="可"/>
			<item key="2" text="佳"/>
			<item key="3" text="差"/>
		</dic>
	</item>
	<item id="sleep" alias="睡眠" length="1" not-null="1" type="string" display="2">
		<dic>
			<item key="1" text="可"/>
			<item key="2" text="佳"/>
			<item key="3" text="差"/>
		</dic>
	</item>
	<item id="excrement" alias="大便" length="1" not-null="1" type="string" display="2">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="便秘"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="excrementOther" alias="其他情况" fixed="true" length="100" type="string" display="2"/>
	<item id="pee" alias="小便" length="1" not-null="1" type="string" display="2">
		<dic id="chis.dictionary.isNormal2"/>
	</item>
	<item id="emotion" alias="情绪心情" length="100" not-null="1" type="string" display="2">
		<dic>
			<item key="1" text="平和愉悦"/>
			<item key="2" text="焦虑忧郁"/>
			<item key="3" text="易发脾气"/>
			<item key="4" text="感情淡漠"/>
			<item key="5" text="疲倦感"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="other" alias="其他" length="100" fixed="true" type="string" display="2"/>
	<item id="communityActivity" alias="社区活动" not-null="1" length="1" type="string" display="2">
		<dic>
			<item key="1" text="不参加"/>
			<item key="2" text="偶尔参加"/>
			<item key="3" text="经常参加"/>
		</dic>
	</item>
	<item id="currentStatus" alias="目前状况" not-null="1" length="100" type="string" display="2">
		<dic render="LovCombo">
			<item key="01" text="头痛"/>
			<item key="02" text="头昏"/>
			<item key="03" text="流鼻血"/>
			<item key="04" text="耳鸣"/>
			<item key="05" text="耳聋"/>
			<item key="06" text="声音嘶哑"/>
			<item key="07" text="咳血"/>
			<item key="08" text="心悸"/>
			<item key="09" text="胸痛"/>
			<item key="10" text="腹痛"/>
			<item key="11" text="胃痛"/>
			<item key="12" text="腰痛"/>
			<item key="13" text="大便稀"/>
			<item key="14" text="便血"/>
			<item key="15" text="下肢肿"/>
			<item key="16" text="烦躁"/>
			<item key="17" text="视力模糊"/>
			<item key="18" text="面色"/>
			<item key="98" text="以上都无"/>
			<item key="99" text="其他"/>
		</dic>
	</item>
	<item id="otherStatus" alias="其他状况" colspan="2" fixed="true" length="100" type="string" display="2"/>
	<item id="sbp" alias="收缩压(mmhg)" type="int" not-null="1" display="2"/>
	<item id="dbp" alias="舒张压(mmhg)" type="int" not-null="1" display="2"/>
	<item id="heartRateN" alias="心率(次/分)" type="int" display="2"/>
	<item id="heartRate" alias="心率" length="1" not-null="1" type="string" display="2">
		<dic>
			<item key="1" text="齐"/>
			<item key="2" text="不齐"/>
		</dic>
	</item>
	<item id="suggest" alias="建议" length="255" not-null="1" colspan="2" xtype="textarea" type="string" display="2"/>
	<item id="visitUser" alias="随访医生" length="20" not-null="1" type="string" display="2">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" /> 
	</item>
	<item id="visitUnit" alias="随访机构" length="20" fixed="true" type="string" display="2">
		<dic id="chis.@manageUnit" render="Tree" onlySelectLeaf="true"/> 
	</item>
	<item id="visitWay" alias="随访方式" length="1" not-null="1" type="string" display="2">
		<dic id="chis.dictionary.visitWay"/> 
	</item>
	<item id="visitResult" alias="随访结果" length="1" not-null="1" type="string" display="2">
		<dic id="chis.dictionary.isNormal2"/>
	</item>
	<item id="visitResultExplain" alias="随访结果说明" not-null="1" length="100" type="string" display="2"/>
	<item id="nextDate" alias="下次预约时间" type="date" not-null="0" width="100" display="2"/> 
	<item id="createUnit" alias="建档机构" type="string" length="20"  display="0"
		width="180" update="false" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人员" type="string" length="20" 
		update="false" fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		hidden="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" hidden="true"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item> 
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
		<relation type="parent" entryName="gp.application.rvcf.schemas.RVC_RetiredVeteranCadresRecord"> 
			<join parent="phrId" child="phrId"/> 
		</relation> 
	</relations>
</entry>
