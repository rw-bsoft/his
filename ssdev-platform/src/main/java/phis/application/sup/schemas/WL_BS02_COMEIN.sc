<?xml version="1.0" encoding="UTF-8"?>
<entry id="phis.application.sup.schemas.WL_BS02_COMEIN" tableName="WL_BS02"  alias="报损明细(WL_BS02)">
	<item id="ZBXH" alias="账簿序号" width="120"  display="0"  type="long" fixed="false" />
	<item id="WZBH" alias="资产编号" width="120"  type="string" fixed="false" />
	<item id="WZSL" alias="数量" type="double" length="18" precision="2"/>
	<item id="WZMC" alias="物资名称" type="string" width="200" queryable="true"/>
	<item id="WZGG" alias="物资规格"  type="string" fixed="false"/>
	<item id="WZDW" alias="单位"  type="string" fixed="false"/>
	<item id="GLFS" alias="管理方式"  display="0"  type="long"  fixed="false"/>
	<item id="WZJG" alias="单价" type="double" length="18" precision="4"  display="0" />
	<item id="WZJE" alias="金额" type="double" length="18" precision="4" fixed="false"  display="0" />
	<item id="CJMC" alias="厂家名称"  type="string" fixed="false" width="200"/>
	<item id="WZPH" alias="物资批号"  type="string" length="20" fixed="false" width="120"/>
	<item id="SCRQ" alias="生产日期" type="date" fixed="false" />
	<item id="SXRQ" alias="失效日期" type="date" fixed="false" />
	<item id="KCXH" alias="库存序号"  display="0" type="long" fixed="false" />
	<item id="CJXH" alias="厂家序号"  display="0" type="long" fixed="false" />
	<item id="WZXH" alias="物品序号"  display="0" type="long" length="18"/>
	<item id="DJXH" alias="单据序号"  display="0" type="long" length="18"/>
	<item id="JLXH" alias="记录序号"  display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
  
</entry>
