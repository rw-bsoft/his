<?xml version="1.0" encoding="UTF-8"?>

<entry id="MS_GHMX_TDMX" tableName="MS_GHMX" alias="挂号明细表" sort="JZHM">
	<item id="JZHM" alias="就诊号码"  type="string" length="20" width="120"/>
	<item id="KSDM" alias="科室代码"  type="long" length="18" width="120" display="0"/>
	<item id="YSDM" alias="医生代码"  type="long" length="18" width="120" display="0"/>
	<item id="BRXM" alias="病人姓名" type="string"  length="40"/>
	<item id="GHKS" alias="现挂科室"  type="long" length="18" width="120"/>
	<item id="SBXH" alias="识别序号" type="long" length="18" display="0" generator="assigned" pkey="true"/>
	<item id="GHSJ" alias="挂号时间" width="140"  type="timestamp"/>
	<item id="JZZT" alias="就诊状态"  type="long" length="1" width="10" display="0"/>
	<item id="JZZT_TEXT" alias="就诊状态"  type="long" length="1" width="100" />
	<item id="BRID" alias="病人ID号" length="18" display="0" />
	<item id="YGXM" alias="挂号医生" length="40" type="string" />
	
	<item id="ZLF" display="0"/>
	<item id="GHF" display="0"/>
	<item id="ZJFY" display="0"/>
</entry>
