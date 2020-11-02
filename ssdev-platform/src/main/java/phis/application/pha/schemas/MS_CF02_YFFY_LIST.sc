<?xml version="1.0" encoding="UTF-8"?>

<!-- modify by zhaojian 2017-05-31 处方明细中增加库存数量-->
<entry entityName="MS_CF02"  alias="门诊处方02表">
	<item id="YPMC" alias="药品通用名" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" renderer="onRenderer"/>
	<item id="SBXH" alias="识别序号" length="18" type="string" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="CFSB" alias="处方识别" length="18" not-null="1" display="0" type="long"/>
	<item id="YFGG" alias="药房规格" type="string" length="20" width="70"/>
	<item id="YPXH" alias="药品序号" length="18" type="long" not-null="1" display="0"/>
	<item id="YFDW" alias="单位" type="string" width="50" length="4" />
	<item id="YPSL" alias="数量" length="10" precision="2" width="50" not-null="1" type="double" />
	<item id="KFSL" alias="库存数量" length="9" precision="2" min="0" max="999999.99" type="double" not-null="1" />
	<item id="YPDJ" alias="单价" length="12" precision="4" width="60" not-null="1" type="double" />
	<item id="HJJE"  alias="金额" type="double" length="10"  width="70" precision="2" summaryType="sum" summaryRenderer="totalHJJE"/>
	<item id="CFTS" alias="处方帖数" type="int" 	display="0"/>
	<item id="YCJL" alias="一次剂量" length="10" precision="2" type="double" />
	<!--
	<item id="GYTJ" alias="给药途径" length="4" type="int" width="70">
		<dic id="phis.dictionary.drugMode" autoLoad="true"/>
	</item>
	-->
	<item id="YYTS"  alias="天数" not-null="true" type="int" width="50" max="99999999" />
	<!--<item id="GYTJ" alias="药品用法" type="string" length="18">
		<dic id="phis.dictionary.drugMode" autoLoad="true" searchField="PYDM" fields="key,text,PYDM,FYXH"/>
	</item>	-->
	<item id="XMMC" alias="药品用法" type="string" length="18" width="80" >
	</item>
	<item id="CDMC" alias="产地简称" type="string"  width="130"  length="15" not-null="false"  />
	<item id="YPCD" alias="药品产地" length="18" display="0" not-null="1"/>
	<item id="ZFYP" alias="转方药品" length="1" type="int" display="0" not-null="1"/> 
	<item id="BZXX" alias="备注信息" length="100" type="string" />
</entry>