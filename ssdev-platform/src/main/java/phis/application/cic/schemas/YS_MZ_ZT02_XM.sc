<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_ZT02" alias="个人处方组套">
	<item id="JLBH" alias="记录编号" length="18" hidden="true" type="int" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="ZTBH" alias="组套编号" length="18" hidden="true" type="long"/>
	<item id="YPZH" length="3" fixed="true" type="int" width="20" renderer="showColor"/>
	<item id="XMBH" alias="项目编号" length="18" hidden="true" type="int" not-null="1"/>
	<item id="XMMC" alias="项目名称" length="100" type="string"  mode="remote" width="250" anchor="100%"  not-null="1"/>
	<item id="XMSL" alias="项目数量" length="10" precision="2" width="100" type="double" min="1" max="99999999.99" defaultValue="1"/>
	<item id="YCJL" alias="一次剂量" length="10" hidden="true"  type="double" defaultValue="0"/>
	<item id="SYPC" alias="使用频次" length="12" hidden="true" type="string">
		<dic id="phis.dictionary.useRate"/>
	</item>
	<item id="MRCS" alias="每日次数" length="3" hidden="true" type="int" defaultValue="0"/>
	<item id="YYTS" alias="用药天数" length="3" hidden="true" type="int" defaultValue="1"/>
	<item id="GYTJ" alias="给药途径" length="9" hidden="true" type="int" defaultValue="0">
		<dic id="phis.dictionary.drugMode" />
	</item>
</entry>