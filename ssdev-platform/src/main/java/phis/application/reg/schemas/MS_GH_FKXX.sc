<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_GH_FKXX" alias="挂号付款信息">
  <item id="JLXH" alias="记录序号" display="0" length="18" type="long" not-null="1" generator="assigned" pkey="true">
    <key>
      <rule name="increaseId" type="increase" length="12" startPos="1"/>
    </key>
  </item>
  <item id="SBXH" alias="识别序号" type="long" length="18"/>
  <item id="FKFS" alias="付款方式" type="long" length="18"/>
  <item id="FKJE" alias="付款金额"  type="double" length="12"/>
  <item id="FKHM" alias="付款号码"  type="string" length="40"/>
</entry>
