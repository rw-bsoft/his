<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01"  alias="处方待发药详情">
	<item id="YSXM" alias="医生姓名" type="string" fixed="true"/>
	<item id="YSDM" alias="医生代码" type="string"  display="0"/>
	<item id="BRID" alias="病人ID" type="long" display="0"/>
	<item id="KFRQ" alias="开方日期" xtype="datetimefield" type="datetime"  fixed="true"/>
	<item id="CFHM" alias="处方号码" type="string" length="10" fixed="true"/>
	<item id="FYR" alias="发药人" type="string" length="10" fixed="true"/>
	<item id="FYRQ" alias="发药日期" xtype="datetimefield" type="datetime" fixed="true"/>
	<item id="FPHM" alias="发票号码" type="string" length="20" fixed="true"/>
	<item id="CFSB" alias="处方识别" length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true"/>	
	<item id="CFLX" alias="处方类型" type="string" display="0"/>
	<item id="CFTS" alias="草药帖数"  type="int" minValue="1"  fixed="true" />
</entry>
