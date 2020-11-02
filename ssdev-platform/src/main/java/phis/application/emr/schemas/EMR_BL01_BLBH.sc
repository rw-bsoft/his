<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BL01" alias="病历01表">
	<item id="BLBH" alias="病历编号" type="long" length="18" not-null="1" isGenerate="false" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1000" />
		</key>
	</item>
</entry>
