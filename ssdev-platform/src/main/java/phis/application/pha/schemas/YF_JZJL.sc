<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_JZJL" alias="结帐记录">
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id" />
	<item id="YFSB" alias="药房识别" length="18" defaultValue="%user.properties.pharmacyId" display="0" type="long" not-null="1" generator="assigned" pkey="true"/>
	<item id="CKBH" alias="窗口编号" length="2" display="0" type="int" not-null="1"  pkey="true" defaultValue="0"/>
	<item id="CWYF" alias="财务月份" type="datetime" width="180" not-null="1"  pkey="true"/>
	<item id="QSSJ" alias="起始时间" type="datetime" width="180" not-null="1" fixed="true"/>
	<item id="ZZSJ" alias="终止时间" type="datetime" width="180" not-null="1" fixed="true"/>
</entry>
