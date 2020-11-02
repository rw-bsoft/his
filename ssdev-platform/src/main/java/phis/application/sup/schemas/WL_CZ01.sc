<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_CZ01" alias="重置单据(WL_CZ01)">
  <item id="DJXH" alias="单据序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
	  <key>
		 <rule name="increaseId" type="increase" startPos="24" />
	  </key>
  </item>
  <item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
  <item id="KFXH" alias="库房序号" type="int" length="8" display="0"/>
  <item id="ZBLB" alias="帐薄类别" type="int" length="8" display="0"/>
  
  
  <item id="LZDH" alias="流转单号" length="30" width="130"/>
  <item id="LZFS" alias="流转方式" type="long" length="12">
        <dic id="phis.dictionary.transfermodes" 
        	 filter = "['and',['and',['eq',['$','item.properties.KFXH'],['$','%user.properties.treasuryId']],
        	   ['eq',['$','item.properties.DJLX'],['s','RK']]],
        	   ['eq',['$','item.properties.YWLB'],['i',1]]]" autoLoad="true">
        </dic>
    </item>
  <item id="CZFS" alias="重置方式" type="int" length="1">
  	 <dic id="phis.dictionary.resetMode"/>
  </item>
  <item id="CZRQ" alias="重置日期" type="date"/>
  <item id="DJZT" alias="单据状态" type="int" length="1">
  	    <dic>
			<item key="0" text="新增"/>
			<item key="1" text="审核"/>
			<item key="2" text="记账"/>
		</dic>
  </item>
  <item id="DJBZ" alias="单据备注" length="160"/>
  
  
  <item id="ZDRQ" alias="制单日期" type="date" display="0"/>
  <item id="ZDGH" alias="制单人员" length="10" display="0"/>
  <item id="SHRQ" alias="审核日期" type="date" display="0"/>
  <item id="SHGH" alias="审核人员" length="10" display="0"/>
  <item id="JZRQ" alias="记帐日期" type="date" display="0"/>
  <item id="JZGH" alias="记帐人员" length="10" display="0"/>
</entry>
