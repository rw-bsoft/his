<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_ZT02_CI" tableName="YS_MZ_ZT02" alias="个人处方组套">
	<item id="JLBH" alias="记录编号" length="18" hidden="true" type="int" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item ref="c.ZTBH" alias="组套编号" length="18" hidden="true" type="int"/>
	<item ref="b.FYXH" alias="项目编号" length="18" hidden="true" type="int"/>
	<item ref="b.FYMC" alias="项目名称" length="100" width="100" type="string" mode="remote"/>
	<item id="XMSL" alias="项目数量" length="10"  type="int" min="1" defaultValue='1'/>
	<item id="YCJL" alias="一次剂量" length="10" hidden="true" type="double" />
	<item id="SYPC" alias="使用频次" length="12" hidden="true" type="string"/>
	<item id="MRCS" alias="每日次数" length="3" hidden="true" type="int" />
	<item id="YYTS" alias="用药天数" length="3" hidden="true" type="int" />
	<item id="GYTJ" alias="给药途径" length="9" hidden="true" type="int" />
	<item ref="c.ZTLB" alias="组套类别" length="9" hidden="true" type="int" />
	<item ref="b.PYDM" alias="拼音代码" length="9" hidden="true" />
	<relations>
		<relation type="child" entryName="phis.application.cic.schemas.GY_YLSF_CIC" >
			<join parent="XMBH" child="FYXH"></join>
		</relation>	
		<relation type="child" entryName="phis.application.cic.schemas.YS_MZ_ZT01_XM" >
			<join parent="ZTBH" child="ZTBH"></join>
		</relation>				
	</relations>
</entry>