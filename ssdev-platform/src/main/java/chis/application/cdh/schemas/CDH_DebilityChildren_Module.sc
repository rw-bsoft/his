<?xml version="1.0" encoding="UTF-8"?>

<entry  alias="体弱儿童档案" entityName="chis.application.cdh.schemas.CDH_DebilityChildren"
	sort="a.createDate,a.recordId">
	<item id="recordId" alias="主键" type="string" length="30" width="160"
		pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" width="160"
		display="2" fixed="true" />
	<item ref="b.manaUnitId" queryable="true" not-null="1" fixed="true"
		hidden="true" />
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true"
		notDefaultValue="true" display="0" />
	<item id="phoneNo" alias="联系电话" type="string" length="16" display="0" />
	<item id="feedWay" alias="喂养方式" type="string" length="1" display="0">
		<dic id="chis.dictionary.feedWay" />
	</item>
	<item id="debilityReason" alias="体弱儿分类" type="string" length="64"
		defaultValue="20" colspan="2">
		<dic id="chis.dictionary.debilityDiseaseType" render="LovCombo" />
	</item>
	<item id="debilityOtherReason" alias="其他分类" type="string" length="64"
		display="0" />
	<item id="deseaseReason" alias="病因" type="string" length="64"
		defaultValue="7" colspan="2">
		<dic id="chis.dictionary.debilityChildrenDeseaseReason" render="LovCombo" />
	</item>

	<item id="deseaseOtherReason" alias="其他病因" type="string" length="64"
		display="0" />
	<item id="vestingCode" alias="转归" type="string" length="1"
		display="0">
		<dic id="chis.dictionary.vestingCode" />
	</item>
	<item id="otherVesting" alias="其他转归" type="string" length="64"
		enableKeyEvents="true" display="0" />

	<item id="createUser" alias="建档医生" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime" xtype="datefield"
		fixed="true" update="false" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="createUnit" alias="建档单位" type="string" length="20"
		update="false" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"
			render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="closedUnit" alias="结案单位" type="string" length="16"
		fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"
			render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="closedDoctor" alias="结案医师" type="string" length="20"
		fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="closedDate" alias="结案日期" type="date" fixed="true" />

	<item id="closeFlag" alias="结案标识" type="string" length="1"
		display="1">
		<dic id="chis.dictionary.closeFlag" />
	</item>

	<item id="planTypeCode" alias="随访计划类型" type="string" length="2"
		display="0" />
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" fixed="true" display="0">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="注销" />
		</dic>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"
			render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.cdh.schemas.CDH_HealthCard" />
	</relations>
</entry>
