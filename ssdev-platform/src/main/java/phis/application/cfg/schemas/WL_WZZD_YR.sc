<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WZZD_YR" tableName="WL_WZZD" alias="物资字典">
	<item id="WZXH" alias="物资序号" type="long" length="18" not-null="1"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0" />
	<item id="CJXH" alias="厂家序号" type="long" virtual="true" length="8" display="0"/>
  
	<item id="FLXH" alias="分类序号" type="long" virtual="true" length="8" display="0"/>
	<item id="LBXH" alias="类别序号" type="long" virtual="true" length="8" display="0"/>
	<item id="ZDXH" alias="诊断序号" type="long" virtual="true" length="8" display="0"/>
	<item id="GLFS" alias="管理方式" type="int" length="1" display="0">
		<dic>
			<item key="1" text="库存管理"/>
			<item key="2" text="科室管理"/>
		</dic>
	</item>
	<item id="WZMC" alias="物资名称" length="60" width="150"/>
	<item id="WZGG" alias="物资规格" length="40"/>
	<item id="WZDW" alias="物资单位" length="10" />
	<item id="KFXH" alias="库房名称" type="int" length="8" not-null="true" layout="JBXX">
		<dic id="phis.dictionary.treasury"  filter="['eq',['$map',['s','JGID']],['$','%user.manageUnit.id']]" autoLoad="true" />
	</item>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8" >
		<dic id="phis.dictionary.booksCategory"  autoLoad="true" />
	</item>
  
	<item id="HSLB" alias="核算类别" type="int" length="8">
		<dic id="phis.dictionary.accountingCategory"  autoLoad="true" />
	</item>
	<item id="PYDM" alias="拼音代码" display="0"  length="10" queryable="true" target="WZMC" codeType="py" />
	<item id="WZZT" alias="物资状态" type="int" length="1">
		<dic>
			<item key="-1" text="注销"/>
			<item key="1" text="正常"/>
		</dic>
	</item>
</entry>
