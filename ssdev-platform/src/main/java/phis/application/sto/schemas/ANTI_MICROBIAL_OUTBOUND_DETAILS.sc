<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ANTI_MICROBIAL_OUTBOUND_DETAILS"  alias="抗菌药出库明细" >
	<item id="CKDH" alias="出库单号" length="6"  type="int" not-null="1"  generator="assigned" />
	<item id="YPMC" alias="药品名称"  width="200" type="string"/>
  	<item id="YPGG" alias="药品规格"  width="90" type="string"/>
  	<item id="YPDW" alias="单位"  width="50" type="string"/>
  	<item id="CKSL" alias="出库数量"  width="80" type="string"/>
  	<item id="KSDM" alias="领用科室" type="int" length="8">
		<dic id="phis.dictionary.department" defaultIndex="0" filter = "['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" searchField="PYCODE">
		</dic>
	</item>
  	<item id="JHJG" alias="进货价格"  width="80" type="double" precision="2"/>
  	<item id="CDMC" alias="产地地址"  width="120" type="string"/>
	<item id="CKRQ" alias="出库日期" type="date"/>
  	<item id="YPPH" alias="药品批号"  width="80" type="string"/>
  	<item id="YPXQ" alias="药品效期" type="date"/>
</entry>