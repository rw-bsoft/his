<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="gp.application.op.schemas.MDC_OldPeopleRecord" alias="老年人健康档案"
	sort="a.createDate desc">
	<item ref="d.planDate" display="1" />
	<item id="phrId" alias="档案编号" type="string" length="30"
		not-null="1" fixed="true" queryable="true" width="160" />

	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.regionCode" 	display="1" queryable="true"/> 

	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1"  hidden="true" />
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" colspan="2" anchor="100%"
		width="180"  fixed="true" not-null="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" sliceType = "3" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="1" update="false">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"  keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	
	<item id="symptomsCode" alias="健康检查症状" type="string" length="200"
		display="2">
		<dic id="chis.dictionary.symptomCode" render="LovCombo" />
	</item>
	<item id="currentProblem" alias="主要健康问题" type="string" length="200" not-null="1"
		display="2">
		<dic id="chis.dictionary.healthProblem" render="LovCombo"/>
	</item>
	<item id="lifeSupport" alias="生活赡养" type="string" length="1" not-null="1"
		display="2">
		<dic>
			<item key="1" text="本人" />
			<item key="2" text="配偶" />
			<item key="3" text="子女" />
			<item key="4" text="亲戚" />
			<item key="5" text="政府" />
			<item key="6" text="集体" />
			<item key="7" text="其它" />
		</dic>
	</item>
	<item id="abilityGrade" alias="能力评定" type="string" length="64" not-null="1"
		display="2">
		<dic>
			<item key="1" text="完全正常" />
			<item key="2" text="有不同程度的功能下降" />
			<item key="3" text="有明显障碍" />
		</dic>
	</item>
	<item id="nurseStatus" alias="护理情况" type="string" length="1" not-null="1"
		display="2">
		<dic>
			<item key="1" text="不需" />
			<item key="2" text="配偶" />
			<item key="3" text="子女" />
			<item key="4" text="亲友" />
			<item key="5" text="保姆" />
		</dic>
	</item>
	<item id="toothStatus" alias="牙齿状况" type="string" length="1" not-null="1"
		display="2">
		<dic>
			<item key="1" text="不缺" />
			<item key="2" text="部分缺" />
			<item key="3" text="全缺" />
		</dic>
	</item>
	<item id="sightStatus" alias="视力情况" type="string" length="1"  not-null="1"
		display="2">
		<dic>
			<item key="1" text="裸眼可辨" />
			<item key="2" text="裸眼不可辨" />
			<item key="3" text="戴镜可辨" />
			<item key="4" text="戴镜不可辨" />
			<item key="5" text="老光" />
			<item key="6" text="白内障" />
		</dic>
	</item>
	<item id="psychologyStatus" alias="心理状态" type="string" length="1" not-null="1"
		display="2">
		<dic>
			<item key="1" text="紧张" />
			<item key="2" text="抑郁" />
			<item key="3" text="焦虑" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="currentAddress" alias="常住地址类别" type="string" length="1" not-null="1"
		display="2">
		<dic>
			<item key="1" text="城市" />
			<item key="2" text="农村" />
		</dic>
	</item>
	<item id="obeyDoctor" alias="遵医行为" type="string" length="1" not-null="1"
		display="2">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>
	<item id="medicine" alias="服药依从性" type="string" length="1" not-null="1"
		display="2">
		<dic>
			<item key="1" text="规律服药" />
			<item key="2" text="间断服药" />
			<item key="3" text="不服药" />
		</dic>
	</item>
	<item id="workExposeFlag" alias="职业暴露标志" type="string" length="1"
		display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="workExposeRiskType" alias="职业暴露种类" type="string"
		length="1" display="2">
		<dic>
			<item key="1" text="化学品" />
			<item key="2" text="毒物" />
			<item key="3" text="放射线" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="workExposeRisk" alias="职业暴露因素" type="string" length="50"
		display="2"/>
	<item id="protectFlag" alias="是否防护" type="string" length="1"
		display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="workName" alias="从事职业名称" type="string" length="100"
		display="2">
	</item>
	<item id="workYear" alias="从事时长(年)" type="int" maxValue="99" display="2"/>

	<item id="familySmokeFlag" alias="吸烟标志" type="string" length="1" not-null="1"
		display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="familyColeWarmFlag" alias="煤火取暖标志" type="string"
		length="1"  display="2">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="familyColeWarmYear" alias="取暖时间(年)" type="int" maxValue="99"
		display="2"/>
	<item id="createUnit" alias="建档机构" type="string" length="20" 
		width="180" update="false" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人员" type="string" length="20" 
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="status" alias="档案状态" type="string" length="1"
		hidden="true" defaultValue="0">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="planTypeCode" alias="随访计划类型" type="string" length="2" 
		hidden="true">
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
	
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" hidden="true" />
	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1"
		hidden="true">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true" hidden="true"
		length="100" display="2" colspan="3" anchor="100%" />
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