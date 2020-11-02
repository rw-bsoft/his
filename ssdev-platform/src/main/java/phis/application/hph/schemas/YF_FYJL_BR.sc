<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="发药记录病人表" sort="a.BRCH">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="BRCH" alias="床号" type="String" length="12" not-null="1"/>
	<item id="BRXM" alias="病人姓名" length="20" not-null="1"/>
	<item id="ZYHM" alias="住院号码" type="string" length="10" not-null="1" />
</entry>
