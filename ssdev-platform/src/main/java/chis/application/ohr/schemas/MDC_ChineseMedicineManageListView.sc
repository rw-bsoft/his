<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.ohr.schemas.MDC_ChineseMedicineManage" alias="老年人中医药健康管理" sort="a.reportDate desc,a.id desc"> 
	<item id="id" pkey="true" alias="标识" type="string" length="16" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key>
	</item>  
	<item id="phrId" alias="档案编码" type="string" length="30" fixed="true" colspan="2" display="0"/>  
	<item id="empiId" alias="empiId" type="string" length="32" fixed="true" display="0"/>  
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.definePhrid" alias="档案备注说明" display="1" length="50" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item id="bodyType" alias="体质类型" type="string"  length="20" width="240" queryable="true" >
		<dic render="LovCombo">
			<item key="1" text="气虚质" />
			<item key="2" text="阳虚质" />
			<item key="3" text="阴虚质" />
			<item key="4" text="痰湿质" />
			<item key="5" text="湿热质" /> 
			<item key="6" text="血瘀质" />
			<item key="7" text="气郁质" />
			<item key="8" text="特禀质" />
			<item key="9" text="平和质" />
		</dic>
	</item> 
	<item ref="c.regionCode" 	display="1" queryable="true"/> 
	<item ref="c.manaUnitId"/>
	<item ref="c.manaDoctorId"/>
	<item id="reportDate" alias="填表日期" type="date"  xtype="date" 
	queryable="true"	defaultValue="%server.date.date">
	</item>
	<item id="reportUser" alias="医生签名" type="string" length="20"  defaultValue="%user.userId">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
	</item>
	
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" fixed="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
		</dic>
	</item>
	<item id="year" alias="年度" length="32" type="string" display="0" queryable="true" >
	   <dic id="chis.dictionary.year"/>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20"  display="0"
		width="180" update="false" fixed="true" defaultValue="%user.manageUnit.id" queryable="true">
		<dic id="chis.@manageUnit" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人员" type="string" length="20"  display="0"
		update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false" display="0"
		defaultValue="%server.date.date" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="0"
		hidden="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" hidden="true" display="0"
		defaultValue="%server.date.date">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId"/>
		</relation>
	</relations>
</entry>
