<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.per.schemas.PER_CheckupDetail" alias="体检明细表"
	sort="projectOfficeCode">
	<item id="id" alias="ID" type="string" length="16" hidden="true"
		pkey="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="checkupNo" alias="体检号" type="string" length="16"
		hidden="true" />
	<item id="recordNo" alias="档案号" type="string" length="30"
		hidden="true" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		hidden="true" />
	
	<item id="projectOffice" alias="项目科室" type="string" length="16" />
	<item id="checkupProjectName" alias="体检项目名称" type="string"
		length="60" width="120" fixed="true" />
	<item id="checkupOutcome" alias="体检结果" type="string" length="200"
		width="230" />
	<item id="ifException" alias="是否异常" width="100">
		<dic render="Simple">
			<item key="1" text="正常" />
			<item key="2" text="异常" />
			<item key="3" text="未查" />
		</dic>
	</item>
	<item id="referenceLower" alias="参考下限" length="8" type="double"
		width="100" fixed="true" />
	<item id="referenceUpper" alias="参考上限" length="8" type="double"
		width="100" fixed="true" />
	<item id="checkupUnit" alias="项目单位" type="string" length="20"
		hidden="true" />
	<item id="projectOfficeCode" alias="项目科室代码" type="string"
		length="16" hidden="true" />
	<item id="projectType" alias="项目类型" type="string" length="2"
		hidden="true" />
	<item id="projectClass" alias="项目类别" type="string" length="2"
		hidden="true" />
	<item id="createUnit" alias="录入机构" type="string" length="20"
		width="180" fixed="true" update="false" display="0"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" display="0"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false" display="0"
		fixed="true" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="checkupProjectId" alias="体检项目编码" type="string" length="16"  display="0"
		width="120" fixed="true" />
</entry>
