<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YWLB" alias="业务锁类别" sort="YWXH">
	<item id="YWXH" alias="业务编号" type="string" length="4" not-null="1" 
		generator="assigned" pkey="true" />
	<item id="YWMC" alias="业务名称" type="string" length="50" width="250"
		not-null="1" />
	<item id="YWSDSC" alias="锁定时长(分钟)" type="int" minValue="0"
		width="200" maxValue="999" defaultValue="0" length="50" not-null="1" />
	<item id="HCYW" alias="互斥业务" type="string" colspan="3" length="200" update="false"
		width="400">
		<dic id="phis.dictionary.busiLockType" render="Checkbox" autoLoad="true" />
	</item>
</entry>
