<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MDC_HYPE_HRG_VISIT" alias="高血压高危随访记录表">
  <item id="visitId" alias="随访ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" fixed="true" display="0">
  		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
  </item>
  <item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
  <item id="visitDate" alias="随访日期" type="date"/>
  <item id="visitDoctor" alias="随访医生" type="string" length="20"/>
  <item id="visitWay" alias="随访方式" type="string" length="1"/>
  <item id="constriction" alias="收缩压(mmHg)" type="double" length="3"/>
  <item id="diastolic" alias="舒张压(mmHg)" type="double" length="3"/>
  <item id="waistline" alias="腰围(cm)" type="double" length="3"/>
  <item id="weight" alias="体重(kg)" type="double" length="3"/>
  <item id="targetWeight" alias="目标体重(kg)" type="double" length="3"/>
  <item id="BMI" alias="身体质量指数" type="double" length="8" precision="2"/>
  <item id="BFDI" alias="遵医行为" type="string" length="1"/>
  <item id="riskiness" alias="危险因素" type="string" length="100"/>
  <item id="healthGuidance" alias="健康指导" type="string" length="4000"/>
 
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
	
</entry>
