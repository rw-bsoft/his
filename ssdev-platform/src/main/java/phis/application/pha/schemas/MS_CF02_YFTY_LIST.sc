<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF02"  alias="门诊处方02表">
	<item ref="b.YPMC" width="100" renderer="onRenderer"/>
	<item id="SBXH" alias="识别序号" length="18" type="string" width="60" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="CFSB" alias="处方识别" length="18" not-null="1" display="0" type="long"/>
	<item id="YFGG" alias="药房规格" type="string" length="20" width="60"/>
	<item id="YPXH" alias="药品序号" length="18" type="long" width="50" not-null="1" display="0"/>
	<item id="YFDW" alias="单位" type="string" width="40" length="4" />
	<item ref="c.CDMC" width="60"/>
	<item id="YPDJ" alias="单价" length="12" precision="4" width="60" not-null="1" type="double" />
	<item id="YPSL" alias="发药数量" length="10" precision="2" width="70" not-null="1" type="double" />
	<item id="TYSL" alias="退药数量" length="10" precision="2" width="70" not-null="1" type="double" virtual="true"/>
	<item id="HJJE"  alias="金额" type="double" length="10"  width="170" precision="2" summaryType="sum" summaryRenderer="totalHJJE" display="0"/>
	<item id="CFTS" alias="处方帖数" type="int" 	display="0"/>
	<item id="YCJL" alias="一次剂量" length="10" precision="2" type="double" display="0"/>
	<!--
	<item id="GYTJ" alias="给药途径" length="4" type="int" width="70">
		<dic id="phis.dictionary.drugMode" autoLoad="true"/>
	</item>
	-->
	<item id="YYTS"  alias="天数" not-null="true" type="int" width="40" max="99999999" display="0"/>
	<item id="GYTJ" alias="药品用法" type="string" length="18" display="0">
		<dic id="phis.dictionary.drugMode" autoLoad="true" searchField="PYDM" fields="key,text,PYDM,FYXH"/>
	</item>
	
	<item id="YPCD" alias="药品产地" length="18" display="0" not-null="1"/>
	<item id="ZFYP" alias="转方药品" length="1" type="int" display="0" not-null="1"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" />	
	        	<join parent="YPXH" child="YPXH" />
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" />	
		        <join parent="YPCD" child="YPCD" />
	</relations>
</entry>