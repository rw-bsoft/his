<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.rel.schemas.REL_RelatedManaDoctor" alias="助理关联责医列表">
	<item id="recordId" alias="recordId" type="string" length="16"  display="0"
		width="160" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>	
	</item>
	<item id="mda" alias="责医助理" type="string" display="2" not-null="1" width="50" queryable="true">
		<dic id="chis.dictionary.user20" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="md" alias="责任医生" width="300" colspan="2" length="200" not-null="1" type="string">
		<dic id="chis.dictionary.user01" render="TreeCheck" onlyLeafCheckable="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="md_text" alias="责任医生" width="300" display="0" colspan="2" length="200" not-null="1" type="string">
	</item>
</entry>