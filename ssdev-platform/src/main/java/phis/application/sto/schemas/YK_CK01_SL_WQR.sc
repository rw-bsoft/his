<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CK01" alias="出库01"  >
	<item id="JGID" alias="机构ID" length="20" not-null="1" ype="string" display="0"/>
	<item id="XTSB" alias="药库识别" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0"/>
	<item id="CKFS" alias="出库方式" length="4" not-null="1" type="int" pkey="true" display="0">
		<dic id="phis.dictionary.storehouseDelivery" autoLoad="true"/>
	</item>
	<item id="CZPB" alias=" " length="1" type="int" virtual="true" width="30" renderer="onRenderer"/>
	<item id="CKDH" alias="领药单号" length="6" not-null="1" type="int" pkey="true"/>
	<item id="SQRQ" alias="申请日期" type="date"  width="140"  />
	<item id="LYRQ" alias="领药日期" type="datetime"  width="160" display="0"/>
	<item id="JHJE" alias="进货金额" length="12" type="double" precision="4" virtual="true"/>
	<item id="LSJE" alias="零售金额" length="12" type="double" precision="4" virtual="true"/>
	<item id="CKBZ" alias="备注" type="string" length="30"  />
	<item id="YFSB" alias="药房识别" length="18" not-null="1" type="long"  display="0"/>
	<item id="CKPB" alias="出库判别" length="1" not-null="1" type="int"  display="0"/>
	<item id="CKRQ" alias="出库日期" type="datetime"  display="0"/>
	<item id="CKKS" alias="出库科室" length="18"  display="0"/>
	<item id="CZGH" alias="操作工号" type="string" length="10"  display="0"/>
	<item id="QRGH" alias="确认工号" type="string" length="10"  display="0"/>
	<item id="SQTJ" alias="申请提交" length="1" not-null="1"  display="0"/>
	<item id="LYPB" alias="领用判别" length="1" not-null="1" type="int"  display="0"/>
	<item id="LYGH" alias="领用工号" type="string" length="10"  display="0"/>
</entry>
