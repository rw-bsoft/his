<?xml version="1.0" encoding="UTF-8"?>
<entry id="phis.application.ccl.schemas.YJ_JCSQ_BGMX"  alias="检查报告">
	<item id="JLXH" alias="记录序号"  length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" defaultFill="0" type="increase" length="18" startPos="1"/>
	    </key>
	</item>
	<item id="SQDH" alias="申请单号" type="long" length="18" not-null="1"/>
	<item id="YLLB" alias="医疗类别" type="String" length="1" not-null="1" display="0"/>
	<item id="JCLB" alias="检查类别" type="String" length="2" not-null="1" display="0"/>
	<item id="JGID" alias="机构ID" type="String" length="20" not-null="1" display="0"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0"/>
	<item id="MZHM" alias="门诊号码" type="String" length="32" display="0"/>
	<item id="ZYH" alias="住院号" type="long" length="18" display="0"/>
	<item id="YSDM" alias="执行医生代码" type="string" length="10" not-null="1" display="0"/>
	<item id="YSMC" alias="执行医生" type="string" length="20" not-null="1"/>
	<item id="KSDM" alias="执行科室代码" type="long" length="18" not-null="1" display="0"/>
	<item id="KSMC" alias="执行科室" type="string" length="20" not-null="1" display="0"/>
	<item id="ZXSJ" alias="执行时间" type="timestamp" not-null="1" width="150"/>
	<item id="LCZD" alias="临床诊断" type="String" length="200" not-null="1" width="200"/>
	<item id="BGZD" alias="报告诊断" type="String" length="500" not-null="1" width="200"/>
	<item id="BGMS" alias="报告描述" type="String" length="1000" not-null="1" width="200"/>
	<item id="JGJY" alias="结果建议" type="string" length="500" width="200"/>
	<item id="TWDZ" alias="医技图片" type="string" length="100" display="0"/>
</entry>