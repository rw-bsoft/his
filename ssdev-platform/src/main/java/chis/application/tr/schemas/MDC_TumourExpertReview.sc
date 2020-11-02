<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourExpertReview" alias="专家评审">
	<item id="TERID" alias="癌前期记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="TCID" alias="确认编号" type="string" length="16" display="0"/>
	<item id="phrId" alias="档案编号" type="string" length="32" display="1"/>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	
	<item id="cancerCase" alias="癌症情况" type="string" length="1" not-null="1" defaultValue="1" display="1">
		<dic render="Radio" colWidth="40" columns="2">
			<item key="1" text="癌前期"/>
			<item key="2" text="确认"/>
		</dic>
	</item>
	<item id="assessment" alias="评定" type="string" length="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="未评"/>
			<item key="1" text="已评"/>
		</dic>
	</item>
	<item id="assessDate" alias="评定日期" type="date" not-null="1" defaultValue="%server.date.date"/>
	<item id="assessDoctor" alias="评定医生" type="string" length="20" not-null="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="assessSpecialist" alias="评定专家" type="string" length="100"/>
	<item id="assessStage" alias="评定期别" type="string" length="1" not-null="1" colspan="3">
		<dic id="chis.dictionary.tumourClinicStage" render="Radio" colWidth="80" columns="6"/>
	</item>
	<item id="assessT" alias="确认分期：T" type="string" length="10">
		<dic id="chis.dictionary.TumourT" />
	</item>
	<item id="assessN" alias="N" type="string" length="10">
		<dic id="chis.dictionary.TumourN" />
	</item>
	<item id="assessM" alias="M" type="string" length="10">
		<dic id="chis.dictionary.TumourN" />
	</item>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1" width="180">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
