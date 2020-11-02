<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.rvcf.schemas.RVC_RetiredVeteranCadresRecord" alias="离休干部档案">
	<item ref="d.planDate" display="1" />
	<item id="phrId" alias="档案编号" type="string" length="30"
		not-null="1" fixed="true" queryable="true" width="160" />
	<item id="empiId" alias="empiId" length="32" display="0" type="string"/>
	
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.regionCode" 	display="1" queryable="true"/> 
	
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="1" update="false">
		<dic id="gp.dictionary.user" render="Tree" onlySelectLeaf="true"  keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" anchor="100%"
		width="180"  fixed="true" not-null="1">
		<dic id="gp.@manageUnit" includeParentMinLen="6" sliceType = "3" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="onceDisease" alias="曾患疾病" length="50" colspan="2" not-null="1" type="string">
		<dic render="LovCombo">
			<item key="01" text="高血压"/>
			<item key="02" text="糖尿病"/>
			<item key="03" text="肿瘤"/>
			<item key="04" text="慢性支气管炎"/>
			<item key="05" text="肾功能衰竭"/>
			<item key="06" text="心肌梗塞"/>
			<item key="07" text="脑出血"/>
			<item key="08" text="心绞痛"/>
			<item key="09" text="充血性心肌衰竭"/>
			<item key="10" text="短暂性脑缺血发作"/>
			<item key="97" text="不详"/>
			<item key="98" text="以上都无"/>
			<item key="99" text="其他"/>
		</dic>
	</item>
	<item id="other" alias="其他" fixed="true" length="200" type="string"/>
	<item id="surgicalHistory" alias="手术史" length="1" not-null="1" type="string">
		<dic id="chis.dictionary.ifHave"/>
	</item>
	<item id="rtaumaHistory" alias="外伤史" length="1" not-null="1" type="string">
		<dic id="chis.dictionary.ifHave"/>
	</item>
	<item id="traumaPsychic" alias="精神创伤" length="1" not-null="1" type="string">
		<dic id="chis.dictionary.ifHave"/>
	</item>
	<item id="allergicHistory" alias="过敏史" length="1" not-null="1" type="string">
		<dic id="chis.dictionary.ifHave"/>
	</item>
	<item id="sleepTime" alias="睡眠(小时/天)" type="int"/>
	<item id="insomnia" alias="失眠情况" length="100" type="string"/>
	<item id="acography" alias="曾疾病治疗记录" length="255" colspan="3" type="string"/>
	<item id="smoke" alias="吸烟" length="1" type="string">
		<dic id="chis.dictionary.frequently"/>
	</item>
	<item id="tea" alias="喝茶" length="1" type="string">
		<dic id="chis.dictionary.frequently"/>
	</item>
	<item id="drink" alias="饮酒" length="1" type="string">
		<dic id="chis.dictionary.frequently"/>
	</item>
	<item id="coffee" alias="咖啡" length="1" type="string">
		<dic id="chis.dictionary.frequently"/>
	</item>
	<item id="menophania" alias="初潮" type="date"/>
	<item id="period" alias="周期(天)" type="int"/>
	<item id="leucorrhea" alias="白带" length="20" type="string"/>
	<item id="menopause" alias="绝经" type="date"/>
	<item id="parity" alias="胎次数" type="int"/>
	<item id="mature" alias="足月" type="int"/>
	<item id="prematureDelivery" alias="早产" type="int"/>
	<item id="abortion" alias="流产" type="int"/>
	<item id="familyDiseasesHistory" alias="家庭疾病史" colspan="3" length="255" type="string"/>
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
