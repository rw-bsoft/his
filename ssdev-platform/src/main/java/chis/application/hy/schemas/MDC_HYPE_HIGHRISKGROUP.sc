<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MDC_HYPE_HIGHRISKGROUP" alias="高血压高危人群表">
  <item id="recordId" alias="记录标识" type="string" length="16" not-null="1" generator="assigned" pkey="true" fixed="true" display="0">
  		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
  </item>
  
    <item ref="b.personName" 	display="1" 	queryable="true"/>
	<item ref="b.sexCode" 		display="1" 	queryable="true"/>
	<item ref="b.birthday" 		display="1" 	queryable="true"/>
	<item ref="b.idCard" 		display="1" 	queryable="true"/>
	<item ref="b.mobileNumber" 	display="1" 	queryable="true"/>
	<item ref="b.phoneNumber" 	display="1" 	queryable="true"/>
	
  <item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
  <item id="constriction" alias="收缩压(mmHg)" type="double" length="3"/>
  <item id="diastolic" alias="舒张压(mmHg)" type="double" length="3"/>
  <item id="status" alias="标识" type="string" length="1"/>
  <item id="closedTime" alias="结案时间" type="datetime"/>
  <item id="closedUser" alias="结案人员" type="string" length="20"/>
  <item id="closedUnit" alias="结案机构" type="string" length="20"/>
  <item id="Effect" alias="转归" type="string" length="1"/>

  <item id="createUnit" alias="建档单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
	</relations>
</entry>
