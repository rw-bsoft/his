<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServicePackageQuery" tableName="chis.application.scm.schemas.SCM_ServicePackage" alias="签约服务包">
	<item id="SPID" alias="服务包编号" type="string" length="20" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="packageName" alias="服务包名" type="string" length="50" width="150" queryable="true"/>
	<item id="manaUtil" alias="所属机构" type="string" length="20" width="150" queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="price" alias="价格" type="double" length="6" precision="2" queryable="true"/>
	<item id="realPrice" alias="实际价格" type="double" length="6" precision="2" queryable="true"/>
	<item id="startUsingDate" alias="启用日期" type="date" width="100" queryable="true"/>
	<item id="validEndDate" alias="有效结止日期" type="date" width="100" queryable="true"/>
	<item id="packageIntendedPopulation" alias="适应人群" type="string" length="20" colspan="2" width="300" queryable="true">
		<dic id="chis.dictionary.intendedPopulation" render="LovCombo"/>
	</item>
	<item id="intro" alias="简介" type="string" xtype="textarea" length="2000" colspan="2" width="400"/>
</entry>