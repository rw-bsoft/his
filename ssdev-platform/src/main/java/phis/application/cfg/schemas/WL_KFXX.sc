<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_KFXX" alias="库房信息" sort="SXH">
	<item id="KFXH" alias="库房序号" type="long" length="8" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" defaultValue="%user.manageUnit.id"/>
	<item id="KFMC" alias="库房名称" not-null="1" width="160" length="40"/>
	<item id="KFLB" alias="库房类别" type="int" length="1" defaultValue="1">
		<dic id="phis.dictionary.TreasuryCategory"/>
	</item>
	<item id="KFZT" alias="库房状态" fixed="true" type="int" length="1" defaultValue="0">
		<dic id="phis.dictionary.qyzt"/>
	</item>
	<item id="KFZB" alias="库房帐薄" display="2" length="40">
		<dic id="phis.dictionary.booksCategory" render="Checkbox" filter="['and',['or',['eq',['$','item.properties.JGID'],['$','%user.properties.topUnitId']],['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]],['eq',['$','item.properties.ZBZT'],['i',1]]]" autoLoad="true"/>
	</item>
	<item id="EJKF" alias="二级库房" fixed="true" type="long" length="18" >
		<dic id="phis.dictionary.department_leaf_wl" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE"/>
	</item>
	<item id="LBXH" alias="分类类别" type="int" length="8">
		<dic id="phis.dictionary.categories" autoLoad="true" filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="LBXH1" alias="以账簿核算为分类" type="int" display="2" virtual="true" xtype="checkbox" length="8"/>
	<item id="GLKF" alias="管理库房" display="2" type="int" length="1">
		<dic id="phis.dictionary.managementTreasury"/>
	</item>
	<item id="WXKF" alias="维修库房" display="2" type="int" length="1">
		<dic id="phis.dictionary.maintenanceTreasury"/>
	</item>
	<item id="CKFS" alias="出库方式" display="2" type="int" length="1">
		<dic id="phis.dictionary.libraryMode"/>
	</item>
	<item id="SXH" alias="顺序号" type="long" length="18"/>
	<item id="CSBZ" alias="初始化" display="0" type="int" length="1"/>
	<item id="ZJBZ" alias="折旧启用" display="1" type="int" length="1"/>
	<item id="ZJYF" alias="折旧启用月份" display="1"  length="6"/>
	<item id="HZPD" alias="汇总盘点" display="0" type="int" length="1"/>
	<item id="PDZT" alias="盘点状态" display="0" type="int" length="1"/>
</entry>
