<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_JLSBCX" tableName="WL_JLXX" alias="计量信息(WL_JLXX)">
	<item id="JLXH" alias="计量序号" length="10" not-null="1" type="long" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>		
	<item id="KSDM" alias="在用科室" length="18" type="long">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="JLBH" alias="计量编号" type="string" length="12" display="0"/>
	<item id="JLLB" alias="计量类别" type="int" length="1">
		<dic id="phis.dictionary.jlfl">
		</dic>
	</item>
	<item id="JLQJFL" alias="计量分类" length="2" type="int" width="100">
		<dic id="phis.dictionary.jlqjfl">
		</dic>
	</item>
	<item id="WZMC" alias="物资名称" type="string" length="60" width="150"/>
	<item id="WZGG" alias="规格型号" type="string" virtual="true" length="60" width="150"/>
	<item id="CJMC" alias="生产厂家" type="string" length="60" width="150"/>
	<item id="WZDW" alias="单位" type="string" length="10"/>
	<item id="WZDJ" alias="单价" length="18" precision="4" type="double"/>
	<item id="CCBH" alias="出厂编号" type="string" length="50"/>
</entry>
