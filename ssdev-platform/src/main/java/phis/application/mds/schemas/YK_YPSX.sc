<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YPSX" alias="药品属性"  sort="a.YPSX desc">
  <item id="YPSX" alias="药品属性" type="long" length="16"  not-null="1" generator="assigned" pkey="true" display="0">
    <key>
      <rule name="increaseId" type="increase" length="16" startPos="0"/>
    </key>
  </item>
  <item id="SXMC" alias="剂型名称" type="string"  width="300" anchor="100%" length="15" not-null="true" colspan="3"/>
  <item id="PYDM" alias="拼音码" target="SXMC" codeType="py" type="string" length="6" queryable="true"  colspan="3" selected="true"/>
  <item id="SYBZ" alias="输液标志" type="int"  length="1"   defaultValue="0" colspan="2" anchor="70%" >
  	<dic id="phis.dictionary.confirm"/>
  </item>
</entry>
