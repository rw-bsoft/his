<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_WZZD" alias="物资字典(WL_WZZD)">
	<item id="WZXH" alias="物资序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" type="int" length="8" display="0" />
	<item id="CJXH" alias="厂家序号" type="long" virtual="true" length="8" display="0"/>
  
	<item id="FLXH" alias="分类序号" type="long" virtual="true" length="8" display="0"/>
	<item id="LBXH" alias="类别序号" type="long" virtual="true" length="8" display="0"/>
	<item id="ZDXH" alias="诊断序号" type="long" virtual="true" length="8" display="0"/>
  
	<item id="ZBLB" alias="帐薄类别" type="int" length="8" >
		<dic id="phis.dictionary.booksCategory"  autoLoad="true" />
	</item>
  
	<item id="HSLB" alias="核算类别" type="int" length="8">
		<dic id="phis.dictionary.accountingCategory"  autoLoad="true" />
	</item>
	<item id="GLFS" alias="管理方式" type="int" length="1" display="0">
		<dic>
			<item key="1" text="库存管理"/>
			<item key="2" text="科室管理"/>
			<item key="3" text="台账管理"/>
		</dic>
	</item>
	<item id="WZMC" alias="物资名称" type="string" length="60" width="150"/>
	<item id="WZGG" alias="物资规格" type="string" length="40"/>
	<item id="WZDW" alias="物资单位" type="string" length="10" />
	<item id="PYDM" alias="拼音代码" type="string" display="0"  length="10" />
	<item id="WZZT" alias="物资状态" type="int" length="1">
		<dic>
			<item key="1" text="在用"/>
			<item key="-1" text="注销"/>
		</dic>
	</item>

</entry>
