<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.rel.schemas.REL_RelevanceDoctor_list" tableName="REL_RelevanceDoctor" alias="关联医生">
	<item id="XH" alias="序号" virtual="true" width="50" length="30" not-null="1" type="string"/>
	<item id="fda" alias="助理医生" type="string" display="2" not-null="1" width="50" queryable="true">
		<dic id="gp.dictionary.user101" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="fd" alias="家庭医生" width="250" length="200" not-null="1" type="string">
		<dic id="gp.dictionary.user100"/>
	</item>
	<item id="selected" alias="选择" virtual="true" display="0"/>
</entry>
