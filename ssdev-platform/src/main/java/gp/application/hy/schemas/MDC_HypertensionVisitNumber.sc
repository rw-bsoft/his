<?xml version="1.0" encoding="UTF-8"?>
<entry  alias="高血压随访" entityName="gp.application.hy.schemas.MDC_HypertensionVisitNumber" sort="a.empiId">
	<item id="visitId" alias="随访标识" type="string" display="0"
		length="16" not-null="1" pkey="true" fixed="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1"
		display="0" />
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.phoneNumber" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" />
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		display="0" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id"  />
	</item>	

  	<item ref="c.signFlag" 	display="1" 	queryable="true"/> 
	<item id="visitDate" alias="随访日期" not-null="true" type="date"
		defaultValue="%server.date.today" enableKeyEvents="true"
		validationEvent="false" queryable="true" />  	
		
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		display="0">
	</item>	

	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />



	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.manageUnit.id" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入员" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false" 
		defaultValue="%server.date.today" queryable="true" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>


	<item id="manaDoctorId" alias="责任医生" type="string" length="20"
		display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	
	<item id="lateInput" alias="延后录入" type="string" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	<item ref="d.planId" display="0"/>
	<item ref="d.businessType" display="0"/>
	<item ref="d.planStatus" display="0"/>
	<relations>
		<relation type="parent" entryName="gp.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="gp.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
		<relation type="parent" entryName="gp.application.pub.schemas.PUB_VisitPlan"> 
			<join parent="empiId" child="empiId"/>  
		</relation>
	</relations>
</entry>
