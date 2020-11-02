<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_JCSQ_YKSQD_WAR"  alias="检查申请-开单01-住院" > 
	<item id="SQDH" alias="申请单号" type="long" length="12" not-null="1"  width="80"/>
	<item id="YLLB" alias="医疗类别" type="long" length="2" not-null="1"  width="80" display="0"/>
	<item id="SSLX" alias="所属类型" type="int" length="2" not-null="1" width="80" display="0"/>
	<item id="SSLXTEXT" alias="所属类型" type="string" length="12" not-null="1" width="80" display="1"/>
	<item id="JGID" alias="机构代码" type="string" length="12" not-null="1"  width="100" display="0"/>
	<item id="KSSJ" alias="申检时间" type="string" length="12" not-null="1"  width="150" display="1"/>
	<item id="CZGH" alias="申检医生" type="string" length="12" not-null="1"  width="100" display="1"/>
	<item id="ZYH" alias="住院号" type="long" length="12" not-null="1"  width="100" display="0"/>
	<item id="LSBZ" alias="执行判别" type="int" length="2" not-null="1"  width="100" display="0"/>
	<item id="LSBZTEXT" alias="执行判别" type="String" length="12" not-null="1"  width="100" display="0"/>
	
</entry>