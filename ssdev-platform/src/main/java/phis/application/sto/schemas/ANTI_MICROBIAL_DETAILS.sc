<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ANTI_MICROBIAL_DETAILS"  alias="抗菌药采购明细" >
	<item id="RKDH" alias="入库单号" length="6"  type="int" not-null="1"  generator="assigned" />
	<item id="FPHM" alias="发票号码" length="6"  type="int" not-null="1"  generator="assigned" />
	<item id="YPMC" alias="药品名称"  width="200" type="string"/>
  	<item id="YPGG" alias="药品规格"  width="90" type="string"/>
  	<item id="YPDW" alias="单位"  width="50" type="string"/>
  	<item id="RKSL" alias="入库数量"  width="80" type="string"/>
  	<item id="JHJG" alias="进货价格"  width="80" type="double" precision="2"/>
  	<item id="CDMC" alias="产地地址"  width="120" type="string"/>
  	<item id="DWMC" alias="供货单位"  width="180" type="string"/>
	<item id="CGRQ" alias="采购日期" type="date"/>
  	<item id="YPPH" alias="药品批号"  width="80" type="string"/>
  	<item id="YPXQ" alias="药品效期" type="date"/>
</entry>