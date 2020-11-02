<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZDBM" alias="门诊诊断编码">
  <item id="ZDBS" alias="诊断标识" length="9" not-null="1" generator="assigned" pkey="true"/>
  <item id="ZDFL" alias="诊断分类" length="9" not-null="1"/>
  <item id="ZDDM" alias="诊断代码" type="string" length="20" not-null="1"/>
  <item id="ZDMC" alias="诊断名称" type="string" length="60" not-null="1"/>
  <item id="PYDM" alias="拼码代码" type="string" length="10"/>
  <item id="WBDM" alias="五笔代码" type="string" length="10"/>
  <item id="QTDM" alias="其它代码" type="string" length="10"/>
  <item id="XBXZ" alias="性别限制" length="1"/>
  <item id="KZFS" alias="控制方式" length="1" not-null="1"/>
  <item id="ZXBZ" alias="注销标志" length="1" not-null="1"/>
  <item id="BZXX" alias="备注信息" type="string" length="255"/>
</entry>
