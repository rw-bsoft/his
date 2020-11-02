<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_WZFL" alias="物资分类(WL_WZFL)">
  <item id="FLXH" alias="分类序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
  </item>
  <item id="JGID" alias="机构ID" type="string" length="20"/>
  <item id="LBXH" alias="类别序号" type="int" length="8"/>
  <item id="ZDXH" alias="字典序号" type="long" length="12"/>
  <item id="WZXH" alias="物资序号" type="long" length="18"/>
</entry>
