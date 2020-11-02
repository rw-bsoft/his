<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourConfirmedReview" tableName="chis.application.tr.schemas.MDC_TumourConfirmed" alias="肿瘤确诊人群表">
	<item id="TCID" alias="确诊编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="32" not-null="1" display="1" width="150"/>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>

	<item id="reviewDate" alias="评审日期" type="date" defaultValue="%server.date.date"  not-null="1" colspan="3"/>
	<item id="reviewSpecialist" alias="评审专家" type="string" length="100"  not-null="1" colspan="3"/>
	<item id="cancerStage" alias="确诊期别" type="string" length="1" not-null="1" colspan="3">
		<dic id="chis.dictionary.tumourClinicStage" render="Radio" colWidth="80" columns="6"/>
	</item>
	<item id="T" alias="T" type="string" not-null="1" length="10">
		<dic id="chis.dictionary.TumourT" />
	</item>
	<item id="N" alias="N" type="string" not-null="1" length="10">
		<dic id="chis.dictionary.TumourN" />
	</item>
	<item id="M" alias="M" type="string" not-null="1" length="10">
		<dic id="chis.dictionary.TumourM" />
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