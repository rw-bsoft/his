<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_BS01" alias="报损单据(WL_BS01)">
  <item id="DJXH" alias="单据序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true">
  	 <key>
            <rule name="increaseId" type="increase" startPos="24" />
     </key>
  </item>
  <item id="JGID" alias="机构ID" type="string" length="20"/>
  <item id="KFXH" alias="库房序号" type="int" length="8"/>
  <item id="ZBLB" alias="帐薄类别" type="int" length="8"/>
  <item id="LZDH" alias="流转单号" length="30"/>
  <item id="LZFS" alias="流转方式" type="long" length="12"/>
  <item id="BSKS" alias="报损科室" type="long" length="18">
  	<dic id="phis.dictionary.department" filter = "['eq',['$','item.properties.ORGANIZCODE']],['$','%user.manageUnit.id']]" autoLoad="true" searchField="PYDM">
		</dic>
  </item>
  <item id="BSRQ" alias="报损日期" type="date"/>
  <item id="ZDRQ" alias="制单日期" type="date"/>
  <item id="ZDGH" alias="制单人员" length="10"/>
  <item id="SHRQ" alias="审核日期" type="date"/>
  <item id="SHGH" alias="审核人员" length="10"/>
  <item id="JZRQ" alias="记帐日期" type="date"/>
  <item id="JZGH" alias="记帐人员" length="10"/>
  <item id="DJZT" alias="单据状态" type="int" length="1"/>
  <item id="DJBZ" alias="单据备注" length="160"/>
  <item id="BSFS" alias="报损方式" type="int" length="1"/>
  <item id="JBGH" alias="经办工号" length="10"/>
  <item id="DJJE" alias="单据金额" type="double" length="18" precision="4"/>
</entry>
