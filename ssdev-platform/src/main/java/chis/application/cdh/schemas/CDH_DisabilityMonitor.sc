<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_DisabilityMonitor" alias="疑似残疾儿童报告卡">
  <item id="phrId" alias="档案编号" length="30" not-null="1" generator="assigned" pkey="true" fixed="true"/>
  <item id="empiId" alias="EMPIID" length="32" hidden="true"/>
  <item id="disabilityType" alias="疑似残疾类别" type="string" length="64" not-null = "1">
  		<dic id="chis.dictionary.disabilityType" render="LovCombo" />
  </item>
  <item id="disabilityReason" alias="疑似残疾原因" type="string" length="64" not-null = "1">
  	<dic render="LovCombo">
			<item key="1" text="遗传" />
			<item key="2" text="原发性疾病" />
			<item key="3" text="继发性疾病" />
			<item key="4" text="工伤" />
			<item key="5" text="交通事故" />
			<item key="6" text="外伤" />
			<item key="7" text="中毒" />
			<item key="8" text="发育畸形" />
			<item key="9" text="白内障" />
			<item key="10" text="唐氏综合症" />
			<item key="11" text="传染性疾病" />
			<item key="12" text="自闭症" />
			<item key="13" text="肿瘤" />
			<item key="14" text="精神分裂症" />
			<item key="15" text="其他原因" />
			<item key="16" text="原因未明" />
		</dic>
  </item>
  <item id="otherReason" alias="其他原因描述" type="string" length="100" fixed="true"/>
  <item id="details" alias="医院处置概述" type="string" length="200"/>
	<item id="reportUnit" alias="报告医院" type="string" length="16">
		
	</item>
	<item id="reportDep" alias="报告科室" type="string" length="20" />
	<item id="reportDoctor" alias="报告人" type="string" length="20"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user04" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="reportDate" alias="报告日期" type="date"
		defaultValue="%server.date.today"  />
	<item id="inputUser" alias="填写人" type="string" length="20" fixed="true" update="false"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user02" render="Tree" onlySelectLeaf="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="填写日期" type="datetime"  xtype="datefield"  fixed="true" update="false"
		defaultValue="%server.date.today" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="inputUnit" alias="填写单位" type="string" length="20" fixed="true" update="false"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id"  display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
        <set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.cdh.schemas.CDH_HealthCard">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
