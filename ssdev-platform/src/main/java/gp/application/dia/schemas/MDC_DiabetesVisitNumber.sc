<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="gp.application.dia.schemas.MDC_DiabetesVisitNumber" alias="糖尿病随访" sort="a.empiId" >
	<item id="visitId" pkey="true" alias="随访标识" type="string" length="16" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key> 
	</item>  
	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>  
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.phoneNumber" display="1" queryable="true"/>  
	<item ref="c.regionCode" display="1" queryable="true"/>  
	<item id="phrId" alias="档案编码" type="string" length="30" fixed="true" colspan="2" hidden="true" display="0"/>  
	<item id="empiId" alias="empiId" type="string" length="32" fixed="true" hidden="true" display="0"/>  
	<item id="visitDate" alias="随访日期" type="date" not-null="1" queryable="true"/>  
	<item id="visitDoctor" alias="随访医生" type="string" length="20" defaultValue="%user.userId" queryable="true"> 
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/> 
	</item>  
	<item id="visitUnit" alias="随访机构" type="string" length="20"
		display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20" colspan="2" update="false" defaultValue="%user.manageUnit.id" fixed="true"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.manageUnit.id']</set> 
	</item>  
	<item id="inputUser" alias="录入者" type="string" length="20" update="false" defaultValue="%user.userId" fixed="true" queryable="true"> 
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" />  
		<set type="exp">['$','%user.userId']</set> 
	</item>  
	<item id="inputDate" alias="录入日期" type="datetime" xtype="datefield" update="false" defaultValue="%server.date.today" fixed="true" queryable="true"> 
		<set type="exp">['$','%server.date.datetime']</set> 
	</item>  
	<item id="lateInput" alias="延后录入" type="string" display="0"> 
		<dic> 
			<item key="1" text="是"/>  
			<item key="2" text="否"/> 
		</dic> 
	</item>  
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1"> 
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.userId']</set> 
	</item>  
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1" width="180" defaultValue="%user.manageUnit.id"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>  
		<set type="exp">['$','%user.manageUnit.id']</set> 
	</item>  
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield" defaultValue="%server.date.today" display="1"> 
		<set type="exp">['$','%server.date.datetime']</set> 
	</item>  
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" display="0" queryable="true"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" parentKey="%user.manageUnit.id" rootVisible="true"/> 
	</item>  
	<item ref="c.regionCode_text" alias="网格地址" display="0"/>   
	<item ref="d.planId" display="0"/>
	<item ref="d.businessType" display="0"/>
	<item ref="d.planStatus" display="0"/>
	<relations> 
		<relation type="parent" entryName="gp.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="gp.application.fd.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation> 
		<relation type="parent" entryName="gp.application.pub.schemas.PUB_VisitPlan"> 
			<join parent="empiId" child="empiId"/> 
		</relation> 
	</relations> 
</entry>
