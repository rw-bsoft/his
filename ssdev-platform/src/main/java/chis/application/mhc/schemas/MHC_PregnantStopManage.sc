<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_PregnantStopManage" alias="孕妇终止妊娠信息" tableName="MHC_EndManagement">
	<item id="empiId" alias="EMPIID" length="32" hidden="true" type="string"/>
	<item id="pregnantId" alias="孕妇档案编号" length="30" width="130" display="1"  type="string" queryable="true" />
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.manaDoctorId" display="1" queryable="true" />
	<item ref="c.manaUnitId" display="1" queryable="true" />
	<item ref="c.ownerArea" display="1" queryable="true" />
	<item ref="c.homeAddress" display="1" queryable="true" />
	<item ref="c.homeAddress_text" display="0"  />
	<item ref="c.lastMenstrualPeriod" display="1" queryable="true" />
	<item id="endDate" alias="终止妊娠时间" type="date" not-null="1" defaultValue="%server.date.today" />
	<item id="week" alias="终止孕周" type="int" not-null="1"   virtual="true"  />
	<item id="gestationMode" alias="终止妊娠方式" length="20" display="1" fixed="true" type="string">
		<dic id="gestationMode" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="remark" alias="备注" xtype="textarea" length="200" display="2" colspan="3" />
	<item id="endUnit" alias="录入机构" length="20" type="string"
		width="180" defaultValue="%user.manageUnit.id" anchor="100%"
		fixed="true" colspan="2" >
		<dic id="manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="endDoctor" alias="录入医生" length="20" type="string"
		fixed="true" defaultValue="%user.userId" >
		<dic id="user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.mhc.schemas.MHC_PregnantRecord">
			<join parent="pregnantId" child="pregnantId" />
		</relation>
	</relations>
</entry>
