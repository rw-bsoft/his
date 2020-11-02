<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYFS" alias="药品发药方式" sort="a.FYFS desc">
  <item id="FYFS" alias="发药方式"   length="16" type="long"  not-null="1" generator="assigned" pkey="true" display="0">
    <key>
      <rule name="increaseId" type="increase" length="18" startPos="8"/>
    </key>
  </item>
  <item id="FSMC" alias="发药方式"  queryable="true" colspan="2" selected="true"  type="string"  width="180" anchor="100%" length="10" not-null="true" />
</entry>
