<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_KBM_BLLB" alias="病历类别表">
  <item id="LBBH" alias="类别编号" type="int" length="9" not-null="1" generator="assigned" pkey="true"/>
  <item id="YDLBBM" alias="约定类别编码" length="8" not-null="1"/>
  <item id="SJLBBH" alias="上级类别编号" length="10" not-null="1"/>
  <item id="LBBM" alias="类别编码" length="8" not-null="1"/>
  <item id="LBMC" alias="类别名称" length="30" not-null="1"/>
  <item id="XSMC" alias="显示名称" length="255"/>
  <item id="BLLX" alias="病历类型" type="int" length="1" not-null="1"/>
  <item id="BLFZ" alias="病历分组" type="int" length="1" not-null="1"/>
  <item id="DYWD" alias="单一文档" type="int" length="1" not-null="1"/>
  <item id="MLBZ" alias="目录标志" type="int" length="1" not-null="1"/>
  <item id="HYBZ" alias="换页标志" type="int" length="1"/>
  <item id="ZYPLXH" alias="在院排列序号" length="4" not-null="1"/>
  <item id="CYPLXH" alias="出院排列序号" length="4" not-null="1"/>
  <item id="ZYPLSX" alias="在院排列顺序" type="int" length="1" not-null="1"/>
  <item id="CYPLSX" alias="出院排列顺序" type="int" length="1" not-null="1"/>
  <!--<item id="PRINTSETUP" alias="打印设置"/>-->
  <item id="DYTS" alias="打印提示" length="255"/>
  <item id="WGXBZ" alias="网格线标志" type="int" length="1"/>
  <item id="MRDYJ" alias="默认打印机" length="20"/>
</entry>
