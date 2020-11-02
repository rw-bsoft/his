<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_HSLB" alias="核算类别">
  <item id="HSLB" alias="核算类别" type="long" length="8" not-null="1" generator="assigned" pkey="true">
  	<key>
			<rule name="increaseId" type="increase" startPos="24" />
	</key>
	</item>
  <item id="JGID" alias="机构ID" type="string" length="20"/>
  <item id="HSMC" alias="核算名称" length="30"/>
  <item id="ZBLB" alias="帐薄类别" type="int" length="8"/>
  <item id="SJHS" alias="上级核算" type="int" length="8"/>
  <item id="PLSX" alias="排列顺序" length="4"/>
  <item id="KFXH" alias="库房序号" type="int" length="8"/>
  <item id="HSBM" alias="核算编码" length="40"/>
  <item id="ZBMC" alias="账簿名称" length="30"/>
</entry>
