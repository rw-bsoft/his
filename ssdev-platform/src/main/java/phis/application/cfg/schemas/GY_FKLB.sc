<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_FKLB" alias="付款类别" sort="a.FKLB desc">
  <item id="FKLB" alias="付款类别" length="18" type="long" display="0" not-null="1" generator="assigned" pkey="true">
    <key>
      <rule name="increaseId" type="increase" length="12" startPos="7"/>
    </key>
  </item>
  <item id="LBMC" alias="类别名称" type="string" width="200" not-null="1" length="40"/>
  <item id="MZSY" alias="门诊使用" length="8" type="int" width="150" defaultValue="1">
  	<dic id="phis.dictionary.confirm"/>
  </item>
  <item id="ZYSY" alias="住院使用" length="8" type="int" width="150" defaultValue="1">
  	<dic id="phis.dictionary.confirm"/>
  </item>
</entry>
