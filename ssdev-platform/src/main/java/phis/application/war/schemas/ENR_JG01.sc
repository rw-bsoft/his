<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ENR_JG01" alias="护理记录结构表">
  <item id="JGBH" alias="结构编号" type="long" length="9" not-null="1" generator="assigned" pkey="true"/>
  <item id="JGMC" alias="结构名称" length="30" not-null="1"/>
  <item id="BLLB" alias="病历类别" type="long" length="9" not-null="1"/>
  <item id="MBLB" alias="模板类别" type="long" length="9" not-null="1"/>
  <item id="SSKS" alias="所属科室" length="200"/>
  <item id="PLCX" alias="排序次序" type="long" length="2" not-null="1"/>
  <item id="PYDM" alias="拼音代码" length="10"/>
  <item id="WBDM" alias="五笔代码" length="10"/>
  <item id="GLMB" alias="关联模板" length="30" not-null="1"/>
  <item id="ZZKD" alias="纸张宽度" type="long" length="5"/>
  <item id="ZZGD" alias="纸张高度" type="long" length="5"/>
  <item id="MYHS" alias="每页行数" type="long" length="4"/>
  <item id="ZXBZ" alias="注销标志" type="long" length="1" not-null="1"/>
  <item id="BZXX" alias="备注信息" length="30"/>
  <item id="DLHHXM" alias="独立换行项目" type="long" length="18"/>
  <item id="DLYM" alias="独立页码" type="long" length="1"/>
  <item id="ZZFX" alias="纸张方向" type="long" length="1"/>
  <item id="ZZDX" alias="纸张大小" type="long" length="2"/>
</entry>