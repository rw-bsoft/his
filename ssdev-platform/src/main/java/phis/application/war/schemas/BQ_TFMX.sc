<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_TFMX" alias="退费明细">
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1"/>
	<item id="FYXH" alias="费用序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="SQRQ" alias="申请日期" type="data" not-null="1"/>
	<item id="JGID" alias="机构ID" type="long" length="8" not-null="1"/>
	<item id="TFRQ" alias="退费日期"  type="date" />

	<item id="FYDW" alias="费用单位"  type="string" length="4" />
	<item id="FYJG" alias="费用价格" type="double" length="12" />
	<item id="TFSL" alias="退费数量" type="double" length="8"/>
	<item id="ZFBL" alias="自负比例" type="double" length="4" />
	
	<item id="YEPB" alias="婴儿判别" type="long" length="1"/>
	<item id="TFBQ" alias="退费病区" type="long" length="18"/>
	<item id="CZGH" alias="操作工号" type="string" length="10"/>
	<item id="YZID" alias="医嘱ID" type="long" length="18"/>
	<item id="FYXM" alias="费用项目" type="long" length="18"/>

</entry>
