<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true" />
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" display="0" length="18" not-null="1"/>
	<item id="BAHM" alias="病案号码" length="10" display="0"/>
	<item id="ZYHM" alias="住院号" length="10"  fixed="true"/>
	<item id="BRCH" alias="床号" length="12" fixed="true"/>
	<item id="BRXM" alias="姓名" length="20" not-null="1" fixed="true"/>
	<item id="XB" alias="性别"  length="4"  fixed="true" virtual="true" />
	<item id="KSMC" alias="科室"  fixed="true" display="0" virtual="true"/>
	<item id="XZ" alias="性质"  fixed="true" display="0" virtual="true"/>
	<item id="BRXB" alias="性别" type="int" length="4" not-null="1" fixed="true" display="0"/>
	<item id="BRKS" alias="科室" type="long" length="18" not-null="1" fixed="true" display="0"/>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" fixed="true" display="0"/>
	<item id="CSNY" alias="年龄" type="date" fixed="true"/>
	<item id="YJK" alias="预交款" type="double" defaultValue="0.00" virtual="true" fixed="true"/>
	<item id="ZFJE" alias="自费款" type="double" defaultValue="0.00" virtual="true" fixed="true"/>
	<item id="SYJE" alias="剩余款" type="double" defaultValue="0.00" virtual="true" fixed="true"/>
</entry>
