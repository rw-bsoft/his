<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_ZT02" alias="个人处方组套">
	<item id="JLBH" alias="记录编号" length="18" type="int" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item ref="c.ZTLB" alias=" " length="18"  type="string" renderer="ZtlbRender" width="20" display="1"/>
	<item ref="c.ZTBH" alias="组套编号" length="18"  type="int" />
	<item ref="b.YPXH" alias="项目编号" length="18" not-null="1" type="int" />
	<item ref="b.YPMC" alias="药品名称" length="100" type="string" width="195"/>
	<item ref="b.YPGG" alias="规格" type="string" length="20" />
	<item ref="b.YPDW" alias="单位" type="string" length="8" fixed="true" width="60" display="1"/>
	<item id="MRCS" alias="每日次数" length="3" display="0" type="int" defaultValue="1"/>
	<item id="YYTS" alias="天数" length="3" width="50" min="1" max="999" type="int" defaultValue="1"/>
	<item id="GYTJ" alias="药品用法" length="9" width="70" type="int" not-null="1">
		<dic id="phis.dictionary.drugMode" searchField="PYDM"/>
	</item>
	<item id="SYPC" alias="频次" length="12" width="50" type="string" not-null="1">
		<dic id="phis.dictionary.useRate"  fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item ref="b.PYDM" alias="拼音码" type="string" length="20" queryable="true" selected="true" />
	<relations>
		<relation type="child" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="XMBH" child="YPXH"></join>
		</relation>	
		<relation type="child" entryName="phis.application.war.schemas.YS_MZ_ZT01_BQ" >
			<join parent="ZTBH" child="ZTBH"></join>
		</relation>				
	</relations>
</entry>