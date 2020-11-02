<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cons.schemas.CONS_ConsultationRecord" alias="会诊记录" sort="a.consultationDate desc">
	<item id="recordId" alias="recordId" length="16" not-null="1" generator="assigned"  pkey="true" type="string" width="160"
		hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	
	<item id="healthNo" alias="健康卡号" type="string" display="2" length="30" virtual="true"/>
	<item ref="b.personName" queryable="true" />
	<item ref="b.sexCode" queryable="true" />
	<item ref="b.birthday" queryable="true" />
	<item ref="b.idCard" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.regionCode" 	display="1" queryable="true"/> 
	<item ref="c.manaDoctorId" 	display="1" width="120"/> 
	<item ref="c.manaUnitId" 	display="0" width="120"/> 
	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1"  hidden="true" />
	<item id="phrId" alias="档案编号" type="string" length="30" display="0"/>
	<item id="serialNumber" type="string" alias="编号" length="50"/>
	<item id="consultationReason" type="string" alias="会诊原因" width="300" xtype="textarea" colspan="3" length="1000"/>
	<item id="consultationOpinion" type="string" alias="会诊意见" width="300" xtype="textarea" colspan="3" length="1000"/>
	<item id="doctor" alias="会诊医生" width="500" type="string" colspan="2" length="1000">
		<dic id="chis.dictionary.userHER" render="TreeCheck" onlyLeafCheckable="true" maxHeight="400" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="consultationDate" alias="会诊日期" type="date" defaultValue="%server.date.date" maxValue="%server.date.date"/>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false"
		width="120"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入医生" type="string" width="120" length="20" update="false"
		fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" fixed="true" defaultValue="%server.date.today" queryable="true" update="false">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" width="120"
		display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
