<?xml version="1.0" encoding="UTF-8"?>
<entry  alias="精神病个人信息表" entityName="gp.application.psyc.schemas.PSY_PsychosisRecord" sort="a.phrId desc">
	<item ref="d.planDate" display="1" />
	<item id="phrId" alias="档案编号" length="30" type="string" fixed="true" width="150"/>
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" />
	
	<item id="manaDoctorId" alias="责任医生" type="string" length="20"
		not-null="1" update="false">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		width="165" queryable="true" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="guardianName" alias="监护人姓名" length="20" type="string" />
	<item id="empiId" alias="EMPIID" length="32" type="string"
		hidden="true" />
	<item id="guardianRelation" alias="与患者关系" length="30" type="string">
		<dic id="chis.dictionary.relaCode1"/>
	</item>
	<item id="guardianPhone" alias="监护人电话" length="20" type="string" />
	<item id="guardianAddress" alias="监护人住址" length="25" type="string"
		width="200" colspan="2" anchor="100%" queryable="true">
		<dic id="chis.dictionary.areaGrid" minChars="4" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="guardianAddress_text" alias="监护人住址" type="string" length="200" display="0"/>
	<item id="VNCLinkMan" alias="村(居)委会联系人" length="20" type="string"/>
	<item id="VNCLinkPhone" alias="联系电话" length="20" type="string" />
	<item id="economicConditions" alias="经济状况" length="1" type="string" colspan="2">
		<dic>
			<item key="1" text="贫困，在当地贫困线标准以下"/>
			<item key="2" text="非贫困"/>
			<item key="3" text="不详"/>
		</dic>
	</item>
	<item id="diseasedTime" alias="初次发病时间" type="date"  maxValue="%server.date.today"/>
	<item id="firstCureTime" alias="首次药物治疗时间" type="date" maxValue="%server.date.today"/>
	<item id="specialistOpinion" alias="专科医生的意见" type="string" length="2000" colspan="2"/>
	<item id="pastClinic" alias="既往门诊情况" length="2" type="string">
		<dic>
			<item key="1" text="未治" />
			<item key="2" text="间断门诊治疗" />
			<item key="3" text="连续门诊治疗" />
		</dic>
	</item>
	<item id="pastSymptom" alias="既往主要症状" length="50" type="string" colspan="2">
		<dic id="chis.dictionary.symptom" render="LovCombo"/>
	</item>
	<item id="pastSymptomText" alias="其他" length="50" type="string" />
	<item id="closeDoor" alias="关锁情况" length="1" type="string" >
		<dic>
			<item key="1" text="无关锁" />
			<item key="2" text="关锁" />
			<item key="3" text="关锁已解除 " />
		</dic>
	</item>
	<item id="pastHospitalization" alias="既往住院情况" type="string">
		<dic>
			<item key="1" text="精神专科医院" />
			<item key="2" text="综合医院精神专科" />
		</dic>
	</item>
	<item id="pastHospitalizationText" alias="住院次数" type="int" />
	<item id="recentDiagnosis" alias="目前诊断情况" length="20" type="string" />
	<item id="recentDiagnoseHospital" alias="目前确诊医院" length="50"
		type="string" />
	<item id="recentDiagnoseTime" alias="目前确诊时间" type="date"  maxValue="%server.date.today"/>
	<item id="recentTreatResult" alias="最近治疗效果" length="2"
		type="string">
		<dic>
			<item key="1" text="痊愈" />
			<item key="2" text="好转" />
			<item key="3" text="无变化" />
			<item key="4" text="加重" />
		</dic>
	</item>
	<item id="lightAffray" alias="轻度滋事次数" type="int" />
	<item id="causeTrouble" alias="肇事次数" type="int" />
	<item id="causeAccident" alias="肇祸次数" type="int" />
	<item id="selfHurt" alias="自伤次数" type="int" />
	<item id="suicide" alias="自杀未遂次数" type="int" />
	<item id="createUnit" alias="建档机构" type="string" length="20"
		update="false" fixed="true" width="165" colspan="2" 
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人员" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId"
		queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="cancellationDate" alias="档案注销日期" type="date" hidden="true"
		fixed="true" />
	<item id="cancellationReason" alias="档案注销原因" type="string"
		length="1" hidden="true" fixed="true">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true"
		length="100" hidden="true" colspan="3" anchor="100%" />
	<item id="cancellationCheckUnit" alias="注销复核机构" type="string" length="16" fixed="true" display="0"/>
	<item id="cancellationCheckUser" alias="注销复核者" type="string"	length="20" fixed="true" display="0"/>
	<item id="cancellationCheckDate" alias="注销复核时间" type="date" fixed="true" display="0"/>
	
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
		</dic>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	
	<!-- add by yyd-->
	
	
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item ref="c.regionCode_text" alias="网格地址" display="0" />
	<item ref="d.businessType" display="0" />
	<relations>
		<relation type="parent" entryName="gp.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="gp.application.fd.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
		<relation type="children" entryName="gp.application.pub.schemas.PUB_VisitPlan">
			<join parent = "empiId" child = "empiId" />
		</relation>
	</relations>
</entry>
