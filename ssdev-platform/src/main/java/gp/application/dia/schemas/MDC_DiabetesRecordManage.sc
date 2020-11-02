<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="gp.application.dia.schemas.MDC_DiabetesRecordManage" alias="糖尿病档案" sort="a.createDate desc" >
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30" width="165" queryable="true" fixed="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item ref="c.regionCode" display="1" queryable="true"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="165" fixed="true" queryable="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item ref="c.signFlag" 	display="1" 	queryable="true"/> 
	
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true" colspan="3" hidden="true"/>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="1" update="false">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="familyHistroy" alias="糖尿病家族史" type="string" length="1" display="2" defaultValue="3" not-null="1">
		<dic>
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="3" text="不知道"/>
		</dic>
	</item>
	<item id="history" alias="家族史" type="string" length="100" virtual="true" display="2" colspan="2" fixed="true"/>
	<item id="diagnosisDate" alias="确诊年月" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true" not-null="1" maxValue="%server.date.today"/>
	<item id="years" alias="病程年数" type="string" length="25" virtual="true" display="2" fixed="true"/>
	<item id="diagnosisUnit" alias="确诊单位" type="string" length="25" display="2" not-null="1"/>
	<item id="diabetesGroup" alias="糖尿病组别" type="string" length="2" display="0" fixed="true" queryable="true" defaultValue="01">
		<dic>
			<item key="01" text="一组"/>
			<item key="02" text="二组"/>
		</dic>
	</item>
	<item id="fbs" alias="建卡空腹血糖" type="double" length="6" not-null="1" display="2" enableKeyEvents="true"/>
	<item id="pbs" alias="建卡餐后血糖" type="double" length="6" not-null="1" display="2" enableKeyEvents="true"/>
	<item id="unit" alias="血糖单位" type="string" not-null="1" display="2" defaultValue="1" fixed="true">
		<dic>
			<item key="1" text="mmol/L"/>
			<item key="2" text="mg/dl"/>
		</dic>
	</item>
	<item id="height" alias="身高(cm)" type="double" length="6" display="2" minValue="100" maxValue="300" not-null="1" enableKeyEvents="true"/>
	<item id="weight" alias="体重(kg)" type="double" length="6" display="2" minValue="30" maxValue="500" not-null="1" enableKeyEvents="true"/>
	<item id="bmi" alias="BMI" type="double" display="2" length="6" fixed="true" virtual="true"/>
	<item id="diabetesType" alias="病例种类" type="string" length="1" display="3" not-null="1">
		<dic id="chis.dictionary.diabetesType"/>
	</item>
	<item id="caseSource" alias="病例来源" type="string" length="1" display="0">
		<dic id="chis.dictionary.caseSource"/>
	</item>
	<item id="createUnit" alias="建档机构" type="string" length="20" update="false" fixed="true" width="165" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人员" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="date" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
			<item key="2" text="（注销）待核实"/>
		</dic>
	</item>
	<item id="cancellationDate" alias="档案注销日期" type="date" display="2" fixed="true"/>
	<item id="cancellationReason" alias="档案注销原因" type="string" length="1" display="2" fixed="true">
		<dic>
			<item key="1" text="死亡"/>
			<item key="2" text="迁出"/>
			<item key="3" text="失访"/>
			<item key="4" text="拒绝"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true" length="100" hidden="true" colspan="3" anchor="100%"/>
	<item id="cancellationCheckUser" alias="注销复核者" type="string" length="20" hidden="true" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="cancellationCheckDate" alias="注销复核时间" type="date" hidden="true" fixed="true"/>
	<item id="cancellationCheckUnit" alias="注销复核单位" type="string" length="20" hidden="true" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="yearEstimate" alias="年度评估" type="date" hidden="true"/>
	<item id="planTypeCode" alias="计划类型代码" type="string" length="2" hidden="true"/>
	<item id="needDoVisit" alias="标识该档案是否有随访需要做" type="boolean" display="0" virtual="true"/>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"  defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20" width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item ref="c.regionCode_text" alias="网格地址" display="0"/>
	<relations>
		<relation type="parent" entryName="gp.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="gp.application.fd.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
