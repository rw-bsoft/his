<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hq.schemas.MDC_HighRiskRecord" alias="高危档案" sort="a.createDate desc,a.phrId desc " version="1332292315384" filename="D:\Program Files\eclipse3.6\workspace\BSCHIS22\WebRoot\WEB-INF\config\schema\mdc/MDC_DiabetesRecord.xml">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30" width="165" queryable="true" fixed="true" update="false" />
	<item ref="b.definePhrid" display="1" queryable="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.address" display="1" />
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item ref="b.registeredPermanent" display="0" queryable="true"/>
	<item ref="c.regionCode" display="1" queryable="true"/>
	<item ref="c.familyDoctorId" display="1" queryable="true"/>
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true" colspan="3" hidden="true"/>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="1" queryable="true" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="165" fixed="true" queryable="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="HIGHRISKTYPE" alias="高危类型" type="string" length="1" >
		<dic id="chis.dictionary.HIGHRISK" render="LovCombo" />
	</item>
	<item id="findWay" alias="发现途径" type="string" length="1" defaultValue="1">
		<dic id="chis.dictionary.findway" />
	</item>
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="isSmoke" alias="是否吸烟" type="string" length="1" defaultValue="0">
		<dic id="chis.dictionary.oneyeszerono" />
	</item>
	<item id="smokeCount" alias="日吸烟量" type="int" />
	<item id="fbs" alias="空腹血糖(mmol/L)" type="double" length="4" minValue="1" maxValue="20" />
	<item id="tc" alias="总胆固醇(mmol/L)" type="double" precision="2" length="4" minValue="1" maxValue="20" />
	<item id="waistLine" alias="腰围(cm)" type="double" length="8" minValue="40" maxValue="200" />
	<item id="createUnit" alias="建档机构" type="string" length="20" update="false" fixed="true" width="165" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人员" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="date" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
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
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId"/>
		</relation>
	</relations>
</entry>
