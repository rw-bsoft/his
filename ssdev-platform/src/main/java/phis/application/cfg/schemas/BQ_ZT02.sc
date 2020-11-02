<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_ZT02" tableName="YS_MZ_ZT02" alias="个人处方组套" sort="YPZH,JLBH">
	<item id="JLBH" alias="记录编号" length="18" type="int" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="ZTBH" alias="组套编号" length="18" display="0" type="long" />
	<item id="YPZH" length="3" fixed="true" type="int" width="20" renderer="showColor"/>
	<item id="XMBH" alias="项目编号" length="18" display="0" type="int" not-null="1"/>
	<item id="XMMC" alias="药品名称" length="100" type="string"  mode="remote" width="195" anchor="100%" not-null="1"/>
	<item id="YCJL" alias="剂量" length="10" width="90" type="double" min="0" precision="3" max="9999999.999" defaultValue="1"/>
	<item ref="b.JLDW" alias="" type="string" length="8" fixed="true" width="30" display="1"/>
	<item id="MRCS" alias="每日次数" length="3" width="80" display="0" type="int" defaultValue="1"/>
	<item id="YYTS" alias="天数" length="3" width="80" min="1" max="999" type="int" defaultValue="1"/>
	<item id="XMSL" alias="总量" length="10"  precision="2" type="double" min="1" max="99999999.99" defaultValue="1"/>
	<item id="GYTJ" alias="途径" length="9" width="80" type="int" not-null="1">
		<dic id="phis.dictionary.drugMode" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="SYPC" alias="频次" length="12" width="80" type="string" not-null="1">
		<dic id="phis.dictionary.useRate" autoLoad="true" searchField="text" fields="key,text,MRCS" />
	</item>
	<item id="JCDL" alias="检查大类" type="string" display="0" virtual="true"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.YK_TYPK" >
			<join parent="YPXH" child="XMBH"></join>
		</relation>				
	</relations>
</entry>