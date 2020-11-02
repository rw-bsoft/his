<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_KSXX" tableName="YJ_KSXX" alias="医技科室信息">

	<item id="KSDM" alias="科室代码" type="long"  length="18" not-null="1" generator="assigned" pkey="true" display="0" />
	<item id="JGID" display="0" alias="机构ID" type="long" length="20" not-null="1"  defaultValue="%user.manageUnit.id"/>
	<item id="DABM" alias="档案表名" type="string" length="12" not-null="1"/>
	<item id="TJHM" alias="特检号码" type="string" length="10"/>
	<item id="YJPH" alias="医技片号" type="string" length="20" />
</entry>
