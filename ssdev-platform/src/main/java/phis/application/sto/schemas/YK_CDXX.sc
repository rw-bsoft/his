<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CDXX" alias="药品产地" >
	<item id="JGID" alias="机构ID"  type="string"   length="20"  not-null="1"   display="0" defaultValue="%user.manageUnit.id" pkey="true"/>
	<item id="YPXH" alias="药品序号" type="long"  width="18" anchor="100%" length="80" not-null="true" display="0" pkey="true"/> 
	<item id="YPCD" alias="药品产地"  type="long" width="18" anchor="100%" length="80" not-null="true"  display="0" pkey="true"/>
	<!--<item ref="b.PYDM" />-->
	<item ref="b.CDMC" mode = "remote"  type="string"  renderer="onRenderer"/>
	<item id="PFJG" alias="批发价格"   width="80" type="double"  length="12" max="999999.9999" min="0"  precision="4" defaultValue="0" display="0"/>
	<item id="JHJG" alias="进货价格"  width="80"  type="double"  length="12" max="999999.9999" min="0" not-null="true" precision="4" defaultValue="0" />
	<item id="LSJG" alias="零售价格"   width="80" type="double"  length="12" max="999999.9999" min="0" not-null="true" precision="4" defaultValue="0"/>
	<item id="JHJE" alias="进货金额" fixed="true"  width="80"  type="double"  length="12" maxValue="99999999.9999" minValue="0" not-null="true" precision="4" defaultValue="0" display="0"/>
	<item id="PFJE" alias="批发金额" fixed="true"  width="80"  type="double"  length="12" maxValue="99999999.9999" minValue="0"  precision="4" defaultValue="0" display="0"/>
	<item id="LSJE" alias="零售金额" fixed="true"  width="80"  type="double"  length="12" maxValue="99999999.9999" minValue="0" not-null="true" precision="4" defaultValue="0" display="0"/>
	<item id="KCSL" alias="数量"  width="80"  type="double"  length="10" maxValue="999999.99"  not-null="true" precision="2" defaultValue="0" display="0"/>
	<item id="PZWH" alias="批准文号" type="string"  width="100"  length="60" not-null="true" defaultValue="0"/>
	<item id="GMP" alias="GMP标志"  width="100" type="int" length="1" not-null="true" defaultValue="1">
		<dic id="phis.dictionary.gmpsign"></dic>
	</item>
	<!--以后改成数据字典-->
	<item id="DJFS" alias="零售价格定价方式"   width="150" type="int"  length="1" not-null="true" defaultValue="0" fixed="true">
		<dic id="phis.dictionary.pricingWay"></dic>
	</item>
	<item id="DJGS" alias="定价计算" type="string"  width="200" length="250"  fixed="true" display="0"/>

	<item id="ZFPB" alias="作废判别" display="0" type="int"  defaultValue="0" />
	<item id="GYJJ" alias="公用进价"   width="80" type="double"  length="12" max="999999.9999"  min="0" display="0" not-null="true" precision="4" defaultValue="999999.9999"/>
	<item id="GYLJ" alias="公用零价"   width="80" type="double"  length="12" max="999999.9999"  min="0" display="0" not-null="true" precision="4" defaultValue="999999.9999"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ_YK" />
		     <join parent="YPCD" child="YPCD" />
	</relations>
</entry>
