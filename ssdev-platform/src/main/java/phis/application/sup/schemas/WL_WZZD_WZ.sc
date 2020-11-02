<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_WZZD_WZ" tableName="WL_WZZD" alias="物资字典(WL_WZZD)">
	<item id="WZXH" alias="物资序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="HSLB" alias="核算类别" type="int" length="8" not-null="true">
		<dic id="phis.dictionary.WL_HSLB_SJHS"  autoLoad="true"/>
	</item>
	<item id="WZMC" alias="物资名称" type="string" length="60" not-null="true" width="150"/>
	<item id="WZGG" alias="物资规格" type="string" length="40"/>
	<item id="PYDM" alias="拼音代码" type="string" length="10" queryable="true"/>
	<item id="CJMC" alias="生产厂家" type="string" length="10" virtual="true" width="160"/>
	<item id="WZJG" alias="物资价格" type="string" length="10" virtual="true"/>
	<item id="GLFS" alias="管理方式" type="int" length="1" display="0">
		<dic id="phis.dictionary.wzglfs"  autoLoad="true"/>
	</item>
</entry>
