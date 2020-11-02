<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mobileApp.schemas.MDC_DiabetesRecordApp" alias="糖尿病档案" sort="a.createDate desc" version="1332292315384" filename="D:\Program Files\eclipse3.6\workspace\BSCHIS22\WebRoot\WEB-INF\config\schema\mdc/MDC_DiabetesRecord.xml">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30" width="165" queryable="true" fixed="true"/>
	<item ref="b.definePhrid" display="1" queryable="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item id="checkType" type="string" display="1"  virtual = "true"  alias="是否年检"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item ref="c.regionCode" display="1" queryable="true"/>
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true" colspan="3" hidden="true"/>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="1" update="false">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="diabetesType" alias="病例种类" type="string" length="1" display="3" not-null="1">
		<dic id="chis.dictionary.diabetesType"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="165" fixed="true" queryable="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>	
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
			<item key="2" text="（注销）待核实"/>
		</dic>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"  defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item ref="c.regionCode_text" alias="网格地址" display="0"/>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId"/>
		</relation>
	</relations>
</entry>
