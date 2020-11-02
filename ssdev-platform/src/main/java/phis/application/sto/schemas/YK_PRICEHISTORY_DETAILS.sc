<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_PRICEHISTORYD_DETAILS"  alias="药库调价历史明细" >
	<item id="TJDH" alias="调价单号" length="6"  type="int" not-null="1"  generator="assigned" />
	<item id="TJFS" alias="调价方式"  length="2"  type="int"  display="0"/>
	<item id="TJRQ" alias="调价日期"  type="timestamp" />
	<item id="TJWH" alias="调价文号" type="string" length="15"/>
	<item id="YJHJ" alias="原进货价" defaultValue="0" length="12" precision="6"  type="double" display="0"/>
	<item id="XJHJ" alias="新进货价" defaultValue="0" length="12" precision="6"  type="double" display="0"/>
	<item id="YLSJ" alias="原零售价" width="71" length="13" precision="4"  fixed="true" type="double" />
	<item id="XLSJ" alias="新零售价" width="71" length="11" precision="4"   type="double" />
	<item id="TJSL" alias="调价数量" length="10" precision="4"  type="double" fixed="true" />
	<item id="YGXM" alias="员工姓名" type="string" length="5" />
</entry>