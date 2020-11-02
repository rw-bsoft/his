<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_ZT02" alias="个人处方组套" sort="YPZH,JLBH">
	<item id="JLBH" alias="记录编号" length="18" type="int" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="ZTBH" alias="组套编号" length="18" display="0" type="long" />
	<item id="YPZH" length="3" fixed="true" type="int" width="10" renderer="showColor"/>
	<item id="XMBH" alias="项目编号" length="18" display="0" type="int" not-null="1"/>
	<item id="XMMC" alias="药品名称" length="100" type="string"   width="120" anchor="100%" not-null="1"/>
	<item id="XMSL" alias="项目数量" length="10"  precision="2" type="double" min="1" max="99999999.99" defaultValue="1"/>
	<item id="YCJL" alias="剂量" length="10" width="70" type="double" min="0" precision="3" max="9999999.999" defaultValue="1"/>
	<item ref="b.JLDW" alias="" type="string" length="8" fixed="true" width="30" display="1"/>
	<item id="MRCS" alias="每日次数" length="3" display="0" type="int" defaultValue="1"/>
	<item id="YYTS" alias="天数" length="3" width="50" min="1" max="999" type="int" defaultValue="1"/>
	<item id="GYTJ" alias="药品用法" length="9" width="70" type="int" not-null="1">
		<dic id="phis.dictionary.drugMode" searchField="PYDM"/>
	</item>
	<item id="SYPC" alias="频次" length="12" width="50" type="string" not-null="1">
		<dic id="phis.dictionary.useRate"  fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<relations>
		<relation type="parent" entryName="YK_TYPK" >
			<join parent="YPXH" child="XMBH"></join>
		</relation>				
	</relations>
</entry>