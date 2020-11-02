<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesRepeatVisit" alias="糖尿病复诊" sort="diagnosisDate"> 
	<item id="recordId" pkey="true" alias="复诊主键" type="string" length="30" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="30" startPos="1"/> 
		</key> 
	</item>  
	<item id="visitId" alias="随访id" type="string" length="20" fixed="true"  hidden="true" display="0"/>  
	<item id="empiId" alias="empiId" type="string" length="32" fixed="true" hidden="true" display="0"/>  
	<item id="visitDate" alias="随访日期" type="date" display="2" defaultValue="%server.date.today"/>  
	<item id="diagnosisDate" alias="诊断日期" type="date" not-null="1" defaultValue="%server.date.today"/>  
	<item id="diagnosisUnit" alias="诊断医院" type="string" length="20" display="2" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="false"/>
	</item>
	<item id="comfirmUnit" alias="确诊医院" type="string" length="20"
		display="2" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="false"/>
	</item>
	<item id="fbs" alias="空腹血糖" type="double" length="6" enableKeyEvents="true"/>  
	<item id="pbs" alias="餐后血糖" type="double" length="6" enableKeyEvents="true"/>  


	<item id="createUnit" alias="创建单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="创建人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="创建日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="0" defaultValue="%user.userId">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="0" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
