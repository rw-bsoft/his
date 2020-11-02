<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pub.schemas.PUB_FamilyDoctor" alias="家庭医生" sort="autoId">
	<item id="autoId" alias="编号" length="16" not-null="1" generator="assigned" pkey="true" type="string" width="160"
		hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="familyDoctorId" alias="家庭医生" width="150" length="50" not-null="1" >
		<dic id="chis.dictionary.Personnel" />
	</item>
	<item id="familyTeamId" alias="所属家庭团队" width="120" update="false" fixed="true" length="20" not-null="1" >
		<dic id="chis.dictionary.familyteam" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" />
	</item>
	<item id="status" alias="状态" length="1" not-null="1">
		<dic id="chis.dictionary.status" />
	</item>
</entry>
