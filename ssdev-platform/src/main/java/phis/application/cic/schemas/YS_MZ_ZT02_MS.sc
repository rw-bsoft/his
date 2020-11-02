<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_ZT02_MS" tableName="YS_MZ_ZT02" alias="个人处方组套" sort="ZTBH,JLBH">
	<item id="JLBH" alias="记录编号" length="18" type="int" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="ZTBH" alias="组套编号" length="18" display="0" type="int" />
	<item id="YPZH" length="3" fixed="true" type="int" width="20"/>
	<item ref="b.YPXH" alias="项目编号" length="18" not-null="1" type="int" />
	<item ref="b.YPMC" alias="药品名称" length="100" type="string" summaryType="count" mode="remote" width="195" anchor="100%" summaryRenderer="totalYPSL" not-null="1"/>
	<item ref="b.YPGG" alias="规格" type="string" length="20" />
	<item id="XMSL" alias="项目数量" length="10" display="0" type="int" defaultValue="0"/>
	<item id="YCJL" alias="每次剂量" width="80" type="double" min="0" precision="3" defaultValue="1"/>
	<item ref="b.JLDW" alias="" type="string" length="8" fixed="true" width="30" display="1"/>
	<item id="SYPC" alias="使用频次" length="12" width="80" type="string" defaultValue="1" not-null="1">
		<dic id="phis.dictionary.useRate"/>
	</item>
	<item id="MRCS" alias="每日次数" length="3" width="80" display="0" type="int" defaultValue="1"/>
	<item id="YYTS" alias="用药天数" length="3" width="80" type="int" defaultValue="1"/>
	<item id="GYTJ" alias="给药途径" length="9" width="80" type="int" defaultValue="1" not-null="1">
		<dic id="phis.dictionary.drugMode" />
	</item>
	<relations>
			<relation type="parent" entryName="phis.application.cic.schemas.YK_TYPK_CIC" >
				<join parent="YPXH" child="XMBH"></join>
			</relation>				
	</relations>
</entry>