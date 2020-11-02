<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_WZZD_EJYR" tableName="WL_WZZD" alias="二级建库(WL_EJJK)">
	<item id="WZXH" alias="物资序号" type="long" length="18" not-null="1" display="0"/>
	<item id="WZMC" alias="物资名称" length="60" width="200"/>
	<item id="WZGG" alias="物资规格" length="40"/>
	<item id="WZDW" alias="物资单位" length="10" />
	<item id="GCSL" alias="高储数量" type="double" length="12" precision="2"/>
	<item id="DCSL" alias="低储数量" type="double" length="12" precision="2"/>
	<item id="WZZT" alias="物资状态" type="int" length="1">
		<dic id="phis.dictionary.wzzt"  autoLoad="true"/>
	</item>
	<item id="HSLB" alias="核算类别" type="int" length="8">
		<dic id="phis.dictionary.accountingCategory"  autoLoad="true" />
	</item>
	<item id="KFXH" alias="所属库房" type="int" length="8" not-null="true" queryable="true">
		<dic id="phis.dictionary.treasury"  filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" autoLoad="true" />
	</item>
	<item id="PYDM" alias="拼音代码" display="0"  length="10" queryable="true" />
	<item id="ZBLB" alias="帐薄类别" type="int" length="8" >
		<dic id="phis.dictionary.booksCategory"  autoLoad="true" />
	</item>
</entry>
