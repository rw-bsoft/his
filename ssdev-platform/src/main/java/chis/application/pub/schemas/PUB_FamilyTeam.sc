<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pub.schemas.PUB_FamilyTeam" alias="家庭团队" sort="autoId">
	<item id="autoId" alias="编号" length="16" not-null="1" generator="assigned" pkey="true" type="string" width="160"
		hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="familyTeamId" alias="家庭团队编号" width="120" update="false" fixed="true" length="20" not-null="1" />
	<item id="familyTeamName" alias="家庭团队名称" width="150" length="50" not-null="1" />
	<item id="manaunitId" alias="所属机构" type="string" length="20" update="false"
		width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="pyCode" alias="拼音代码" type="string" length="4" hidden="true" />
	<item id="status" alias="状态" type="string" length="1" not-null="1">
		<dic id="chis.dictionary.status" />
	</item>
</entry>
