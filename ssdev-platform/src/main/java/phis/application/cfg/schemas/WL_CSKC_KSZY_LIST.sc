<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_CSKC_KSZY_LIST" tableName="WL_CSKC" alias="初始库存">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="KSDM" alias="在用科室" type="long" length="18">
		<dic id="phis.dictionary.department" filter = "['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" searchField="PYCODE"/>
	</item>
	<item id="WZSL" alias="数量" type="double" length="18" precision="2"/>
	<item id="WZJG" alias="价格" type="double" length="18" precision="4"/>
	<item id="WZJE" alias="金额" type="double" length="18" fixed="true" precision="4"/>
	<item id="WZPH" alias="物资批号" length="20"/>
	<item id="MJPH" alias="灭菌批号" length="20"/>
	<item id="SCRQ" alias="生产日期" type="date"/>
	<item id="SXRQ" alias="失效日期" type="date"/>
	<item id="RKRQ" alias="入库时间" type="date"/>
	<item ref="b.DWMC" alias="供货单位" type="long" display="0" mode="remote" length="12"/>
	<item id="GHDW" alias="单位序号" type="long" display="0" length="12"/>
	<!--<relations>
			<relation type="parent" entryName="WL_GHDW" >
				<join parent="DWXH" child="GHDW"></join>
			</relation>				
		</relations>-->
</entry>
