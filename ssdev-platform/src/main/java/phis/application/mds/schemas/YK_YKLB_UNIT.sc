<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YKLB" alias="药库列表" sort="a.JGID">
  <item id="YKSB" alias="药库识别" length="18" not-null="1"  display="0" type="long" generator="assigned" pkey="true">
  		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="JGID" alias="机构名称" type="string" width="200" length="20">
		<dic id="phis.@manageUnit" />
	</item>
  <item id="YKMC" alias="药库名称" type="string" length="30" not-null="1" renderer="onRenderer_reg"/>
</entry>
