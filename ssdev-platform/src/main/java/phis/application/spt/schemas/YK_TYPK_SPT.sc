<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TYPK_SPT" alias="省平台目录" >
<item id="UNICODE" alias="统一编码" length="18" width="100" not-null="true" queryable="true" pkey="true" type="string"/>
<item id="NAME" alias="药品名称"  length="256" type="string" queryable="true"/>
<item alias="商品名" id="TRADENAME" length="256" type="string"/>
<item alias="包装规格" id="PACKSPEC" length="256" type="string"/>
<item alias="规格" id="SPEC" length="4000" type="string"/>
<item alias="包装单位" id="PACKUNIT" length="64" type="string"/>
<item alias="包装数量" id="PACKQUANTITY" type="double"/>
<item alias="用药单位" id="UNIT" type="string" length="64"/>
<item alias="剂型" id="DOSAGEFORM" length="64" type="string" queryable="true" />
<item alias="国家本位码" id="SDC" length="256" type="string"/>
<item alias="批准文号" id="APPROVALNUMBER" length="32" type="string"/>
<item alias="批准日期" id="APPROVALTIME"  type="string"/>
<item alias="进价" id="PRICE" length="30" type="double"/>
<item alias="零售价" id="RETAILPRICE" length="30"  type="double" />
<item alias="药品分类" id="CATEGORY" type="string" length="64" />
<item alias="产品来源" id="PRODUCTSOURCE" type="string"  >
     	<dic>
			<item key="0" text="国产"/>
			<item key="1" text=" 进口"/>
			<item key="2" text="进口封装"/>
		</dic>
</item>
<item alias="药性分类" id="PROPERTY" length="8" type="int" />
<item alias="是否基药" id="ESSENTIAL" length="18" type="string" />
<item alias="生产厂家代码" id="MANUFACTURERCODE" length="32" type="string"  hidden="true"/>
<item alias="生产厂家名称" id="MANUFACTURER" length="256" type="string" />
<item alias="机构编码" id="JGID" length="20" type="string"  hidden="true"/>

</entry>
