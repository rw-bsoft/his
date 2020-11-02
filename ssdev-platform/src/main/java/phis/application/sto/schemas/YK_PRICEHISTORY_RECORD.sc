<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_PRICEHISTORY_RECORD"  alias="药库调价历史记录" >
	<item id="YPXH" alias="药品序号" type="long"  width="18" anchor="100%" length="80" not-null="true" display="0" />
	<item id="YPCD" alias="药品产地"  type="long" width="18" anchor="100%" length="80" not-null="true"  display="0" />
	<item id="YPMC" alias="药品名称" type="string" width="180" length="80" />
	<item id="YPGG" alias="规格" type="string" length="20" />
	<item id="YPDW" alias="单位" type="string" length="2" not-null="1"/>
	<item id="CDMC" alias="产地简称" type="string"  width="100" />
	<item id="JHJG" alias="进货价格" type="double" precision="2" />
	<item id="LSJG" alias="零售价格" type="double" precision="2" />
	<item id="PYDM" alias="拼音码" type="string" length="10" not-null="1" />
	<item id="WBDM" alias="五笔码" type="string" length="10" />
	<item id="JXDM" alias="角形码" type="string" length="10" />
	<item id="QTDM" alias="其它码" type="string" length="10" />
	<item id="YPDM" alias="药品代码" type="string"  length="10" layout="JBXX" display="0" >
	</item>	
	<item id="YPSX" alias="剂型" type="long"  not-null="1" length="18" display="0" />
	<item id="TYPE" alias="类别" display="0"  not-null="1" defaultValue="1"
		type="int" length="2"  />
	<item id="DJJB" alias="定价级别" type="int"  display="0" />
	<item id="YPDC" alias="档次"   not-null="1" type="int" length="2" display="0" />
	<item id="FYFS" type="long" alias="发药方式" length="18" display="0"  />
	<item id="TSYP" alias="特殊药品" type="int" length="2" display="0" />
	<item id="ZBLB" alias="账薄类别" type="int" length="2" display="0" />
	<item id="ABC" alias="ABC" type="string" defaultValue="C"
		not-null="true" length="1" display="0"/>
	<item id="YKZF" alias="药库作废" type="int" length="1" display="0" not-null="1" />
	<item id="ZFPB" alias="作废判别" display="0" type="int"  defaultValue="0" />	
	<item id="PZWH" alias="批准文号" type="string"  width="100"  length="30" not-null="true" defaultValue="0"/>
	
</entry>