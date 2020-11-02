<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_YJZC_ZCZB_WZZD" tableName="WL_YJZC" alias="月结资产(WL_YJZC)">
	<item id="JLXH" alias="记录序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" startPos="1" />
		</key>
	</item>
	
	<item id="ZJNX" alias="折旧年限" type="int" display="0" virtual="true"/>
	<item id="ZGZL" alias="总工作量" type="double" length="12" precision="2" display="0" virtual="true"/>
	<item id="JCZL" alias="净残值率" type="double" display="0" length="5" precision="2" virtual="true"/>
	<item id="WZXH" alias="物资序号" type="long" display="0" virtual="true"/>
	<item id="CJXH" alias="厂家序号" type="long" display="0" virtual="true"/>
	<item id="WZBH" alias="物资编号" type="string" fixed="true" virtual="true"/>
	<item id="WZMC" alias="名称" type="string" fixed="true" width="200" virtual="true"/>
	<item id="WZGG" alias="物资规格" type="string" fixed="true" virtual="true"/>
	<item id="WZDW" alias="物资单位" type="string" fixed="true" virtual="true"/>
	<item id="CJMC" alias="厂家名称" type="string"  fixed="true" width="200" virtual="true"/>
	<item id="JTZJ" alias="计提折旧" type="double" fixed="true" length="18" precision="2" virtual="true"/>
	<item id="ZBLB" alias="帐薄类别" type="int" fixed="true" length="8">
		<dic id="phis.dictionary.booksCategory"/>
	</item>
	<item id="WZZT" alias="物资状态" type="int" fixed="true" length="2">
		<dic id="phis.dictionary.assetstatuszc" autoLoad="true"/>
	</item>
	<item id="ZYKS" alias="在用科室" type="long" fixed="true" length="18">
		<dic id="phis.dictionary.department" autoLoad="true"/>
	</item>
	<item id="ZJFF" alias="折旧方法" type="int" fixed="true" virtual="true">
		<dic id="phis.dictionary.zjff" autoLoad="true"/>
	</item>
	<item id="GZL" alias="工作量" type="double" precision="2" virtual="true"/>
	<item id="CZYZ" alias="资产原值" type="double" length="18" precision="2" virtual="true"/>
	<item id="ZCXZ" alias="资产现值" type="double" length="18" precision="2" virtual="true"/>
	<item id="ZJJE" alias="折旧金额" type="double" length="18" precision="2" virtual="true"/>
	<item id="ZJSM" alias="折旧说明" type="string" virtual="true"/>
	<item id="ZJYS" alias="折旧月数" type="int" display="0" virtual="true"/>
	<item id="KCXH" alias="库存序号" type="long" display="0" virtual="true"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="JZXH" alias="结帐序号" type="long" display="0" length="18"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8"/>
	<item id="CWYF" alias="财务月份" display="0" length="6"/>
	<item id="ZBXH" alias="帐薄序号" display="0" type="long" length="18"/>
	<item id="WZXH" alias="物资序号" display="0" type="long" length="18"/>
	<item id="QYRQ" alias="启用日期" display="0" type="int" virtual="true"/>
	<item id="TZRQ" alias="台帐日期" display="0" type="int" virtual="true"/>
</entry>
