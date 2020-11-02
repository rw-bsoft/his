<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.dbs.schemas.MDC_DiabetesYearAssess" alias="糖尿病年度评估" sort="a.phrId">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1"
		pkey="true" hidden="true" >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30"/>
	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>  
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.phoneNumber" display="1" queryable="true"/>  
	<item ref="c.manaUnitId" display="1" queryable="true"/> 
	<item ref="c.regionCode" display="1" queryable="true"/> 
	<item id="empiId" alias="empiId" type="string" length="32" hidden="true" />
	<item id="fixDate" alias="定转组日期" type="date" defaultValue="%server.date.today">
	</item>
	<item id="diabetesGroup" alias="组别" type="string" length="2">
		<dic>
			<item key="01" text="一组" />
			<item key="02" text="二组" />
			<item key="03" text="三组" />
			<item key="99" text="一般管理对象" />
		</dic>
	</item>
	<item id="oldGroup" alias="原组别" type="string" length="2">
		<dic>
			<item key="01" text="一组" />
			<item key="02" text="二组" />
			<item key="03" text="三组" />
			<item key="99" text="一般管理对象" />
		</dic>
	</item>
	<item id="visitCount" alias="随访次数" type="int" length="5"/>
	<item id="normManage" alias="规范管理" type="string" length="1">
		<dic>
			<item key="1" text="规范" />
			<item key="2" text="不规范" />
			<item key="3" text="未到时间" />
		</dic>
	</item>
	<item id="inputUser" alias="录入医生" length="20" update="false"
		type="string" fixed="true" defaultValue="%user.userId"  display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	
	<item id="inputUnit" alias="录入单位" length="20" update="false"  display="0"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield"  update="false"  display="0"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations> 
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>  
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord"> 
			<join parent="phrId" child="phrId"/> 
		</relation>  
		<relation type="children" entryName="chis.application.dbs.schemas.MDC_DiabetesRecord"> 
			<join parent="phrId" child="phrId"/> 
		</relation> 
	</relations> 
</entry>
