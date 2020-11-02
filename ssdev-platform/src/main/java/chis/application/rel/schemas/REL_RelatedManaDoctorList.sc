<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.rel.schemas.REL_RelatedManaDoctorList" tableName="REL_RelatedManaDoctor" alias="助理关联责任医生">
	<item id="XH" alias="序号" virtual="true" width="50" length="30" not-null="1" type="string"/>
	<item id="mda" alias="责医助理" type="string" display="2" not-null="1" width="50" queryable="true">
		<dic id="chis.dictionary.user20" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="md" alias="责任医生" width="250" length="200" not-null="1" type="string">
		<dic id="chis.dictionary.user01"/>
	</item>
	<item id="selected" alias="选择" virtual="true" display="0"/>
</entry>