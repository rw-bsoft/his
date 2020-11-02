<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_ZDJG" tableName="YJ_ZDJG" alias="医技诊断结果">
	<item id="ZDID" alias="诊断ID"  length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" type="increase" length="18" startPos="10000"/>
	    </key>
	</item>
	<item id="JGID" alias="机构ID" type="long" length="20" not-null="1" display="0"/>
	<item id="ZDMC" alias="诊断名称" type="string" length="10" width ="145"  not-null="1"/>
	<item id="ZDDM" alias="诊断代码" type="string" length="10" width = "100" not-null="1" target="ZDMC" codeType="py"/>
	<item id="KSDM" alias="科室代码" type="long" length="18" display="0"/>
</entry>