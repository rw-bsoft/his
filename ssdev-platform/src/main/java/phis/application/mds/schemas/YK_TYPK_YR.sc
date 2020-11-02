<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TYPK_YR" tableName="YK_TYPK_YR" alias="药品数据引入表" sort="a.SCQYMC desc">
  <item id="YPXH" alias="药品序号" type="long" length="18"  not-null="1" generator="assigned" pkey="true" display="0">
    <key>
	    <rule name="increaseId" type="sequence" startPos="1" />
    </key>
  </item>
   <item id="YPID" alias="药品ID" type="string" width="120" display="0"/>
   <item id="PZWH" alias="批准文号" type="string" width="120" display="0"/>
   <item id="YPCD" alias="药品产地" type="long" length="18" width="120" display="0"/>
   <item id="CPMC" alias="产品名称" type="string" width="120"/>
  <item id="SCQYMC" alias="生产企业名称" type="string"  width="360"/>
  <item id="GG" alias="规格" type="string" />
  <item id="QYBM" alias="企业编码" type="string" />
  <item id="JYTYM2018" alias="2018版基药通用名" type="string"  display="0"/>
  <item id="ZXBZDW" alias="最小包装单位" type="string" display="0"/>
  <item id="ZXZJDW" alias="最小制剂单位" type="string" display="0"/>
  <item id="DL" alias="大类" type="string" display="0"/>
  <item id="JXFLM" alias="剂型分类码" type="string" display="0"/>
  <item id="ZHXS" alias="转换系数" type="string"/>
</entry>
