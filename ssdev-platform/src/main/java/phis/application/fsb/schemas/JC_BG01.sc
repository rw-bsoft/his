<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BG01" alias="医技报告01">
	
  <item id="YJXH" alias="医技序号" length="18" type="long" not-null="1" generator="assigned" pkey="true"/>
  <item id="MZZY" alias="门诊住院" length="1" type="long" not-null="1"/>
  <item id="JGID" alias="机构ID" length="20" type="long" not-null="1"/>
  <item id="MBXH" alias="模板序号" length="18" type="long"/>
  <item id="TJHM" alias="特检号码" type="string" length="10"/>
  <item id="BRHM" alias="病人号码" type="string" length="32"/>
  <item id="ZYH" alias="住院号" length="18" type="long"/>
  <item id="BRXM" alias="病人姓名" type="string"  length="40"/>
  <item id="BRXB" alias="病人性别" type="string" length="2"/>
  <item id="BRNL" alias="病人年龄" length="3" type="long"/>
  <item id="SJYS" alias="申检医生" type="string" length="10" />
  <item id="SJKS" alias="申检科室" type="long" length="18"/>
  <item id="JCYS" alias="检查医生" type="string" length="10"/>
  <item id="JCKS" alias="检查科室" length="18" type="long"/>
  <item id="JCRQ" alias="检查日期" type="timestamp"/>
  <item id="XMXH" alias="项目序号" length="18" type="long"/>
  <item id="XMMC" alias="项目名称" type="string" length="80"/>
  <item id="HJJE" alias="合计金额" type="bigDecimal" length="10" precision="2"/>
  <item id="ZDDM" alias="诊断代码" type="string" length="10"/>
  <item id="YQDH" alias="仪器代号" type="string" length="10"/>
  <item id="BBBM" alias="标本编码" type="string" length="4"/>
  <item id="YJPH" alias="医技片号" type="string" length="20"/>
</entry>
