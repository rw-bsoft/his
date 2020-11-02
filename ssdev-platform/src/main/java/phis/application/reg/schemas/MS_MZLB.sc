<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZLB" alias="门诊类别">
  <item id="MZLB" alias="门诊类别" length="18" type="long" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="10" startPos="1"/>
		</key>
	</item>
  <item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
  <item id="MZMC" alias="门诊名称" type="string" length="30"/>
</entry>
