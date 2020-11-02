<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRYZ" alias="家床医嘱">
	<item id="JLXH" alias="记录序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" not-null="1"/>
	<item id="YZZH" alias=" " type="long" length="1" fixed="true" width="10" renderer="showColor"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<item id="ZYHM" alias="家床号" length="10" not-null="1"  />
	<item id="BRXM" alias="姓名" length="40" not-null="1" />
	<item id="YZMC" alias="医嘱项目" width="200" length="100"/>
	<item id="MRCS" alias="日次" type="int" length="2" not-null="1"/>
	<item id="YCSL" alias="总量" type="double" length="8"  precision="2" />
	<item id="FYCS" alias="次数" type="double" length="8" virtual="true" precision="2" display="0"  not-null="1"/>
	<item id="YPDJ" alias="项目单价" type="double" length="8"  precision="4"   not-null="1"/>
	<item id="ZJE" alias="总金额" width="80" type="double"  precision="2" virtual="true" length="80" renderer="doRender"/>
	<item id="YPLX" alias="药品类型" type="int" display="0"/>
	<item id="YPXH" alias="药品序号" type="int" display="0"/>
</entry>
