<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_DB01" alias="药房调拔01"  >
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0"/>
	<item id="SQYF" alias="申请药房" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0"/>
	<item id="MBYF" alias="目标药房" length="18" type="long" display="0"/>
	<item id="CZPB" alias=" " length="1" type="int" virtual="true" width="30" renderer="onRenderer"/>
	<item id="SQDH" alias="申请单号" length="6" not-null="1" pkey="true" type="int"/>
	<item id="RKRQ" alias="入库日期" type="datetime" width="120"/>
	<item id="JHJE" alias="进货金额" type="double" virtual="true" precision="4"/>
	<item id="LSJE" alias="零售金额" type="double" virtual="true" precision="4"/>
	<item id="BZXX" alias="备注信息" type="string" length="100"/>
</entry>
