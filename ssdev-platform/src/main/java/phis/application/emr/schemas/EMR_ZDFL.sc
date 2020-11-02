<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZDFL" alias="门诊诊断分类">
  <item id="FLXH" alias="分类序号" length="9" not-null="1" generator="assigned" pkey="true"/>
  <item id="FLBM" alias="分类编码" type="string" length="30" not-null="1"/>
  <item id="FLDM" alias="分类代码" type="string" length="20" not-null="1"/>
  <item id="FLMC" alias="分类名称" type="string" length="60" not-null="1"/>
  <item id="PYDM" alias="拼音代码" type="string" length="10"/>
  <item id="WBDM" alias="五笔代码" type="string" length="10"/>
  <item id="QTDM" alias="其它代码" type="string" length="10"/>
  <item id="ZXBZ" alias="注销标志" length="1" not-null="1"/>
  <item id="BZXX" alias="备注信息" type="string" length="255"/>
</entry>
