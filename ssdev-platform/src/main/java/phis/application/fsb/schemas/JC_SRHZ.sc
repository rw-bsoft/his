<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_SRHZ" alias="家床收入汇总">
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" generator="assigned" pkey="true"/>
	<item id="HZRQ" alias="汇总日期" type="date" not-null="1" pkey="true"/>
	<item id="KSLB" alias="科室类别" type="int" length="1" not-null="1" pkey="true"/>
	<item id="KSDM" alias="科室代码" type="long" length="18" not-null="1" pkey="true"/>
	<item id="SFXM" alias="收费项目" type="long" length="18" not-null="1" pkey="true"/>
	<item id="ZJJE" alias="总计金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="ZFJE" alias="自负金额" type="double" length="12" precision="2" not-null="1"/>
</entry>
