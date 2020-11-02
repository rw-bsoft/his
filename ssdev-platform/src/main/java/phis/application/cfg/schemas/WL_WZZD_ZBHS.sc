<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WZZD_ZBHS" tableName="WL_WZZD" alias="物资字典(WL_WZZD)">
	<item id="WZXH" alias="物资序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="2">
		<key>
			<rule name="increaseId" type="increase" startPos="1"  />
		</key>
	</item>
  
	<item id="KFXH" alias="库房序号" type="int" length="8" display="2" />
  
  
	<item id="WZMC" alias="物资名称" length="60" width="200" queryable="true"/>
	<item id="WZGG" alias="物资规格" length="40" />
	<item id="WZDW" alias="物资单位" length="10" />
	<item id="GCSL" alias="高储数量" type="double" length="12" precision="2"/>
	<item id="DCSL" alias="低储数量" type="double" length="12" precision="2"/>
	<item id="GLFS" alias="管理方式" type="int" length="1" >
		<dic>
			<item key="1" text="库存管理"/>
			<item key="2" text="科室管理"/>
			<item key="3" text="台帐管理"/>
		</dic>
	</item>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8" >
		<dic id="phis.dictionary.booksCategory"  autoLoad="true"/>
	</item>
	<item id="HSLB" alias="核算类别" type="int" length="8" >
		<dic id="phis.dictionary.WL_HSLB_SJHS"  autoLoad="true"/>
	</item>
	<item id="GLKF" alias="管理库房" type="int" length="8" >
		<dic id="phis.dictionary.treasury_glkf"  filter="['eq',['$','JGID'],['$','%user.manageUnit.id']]" autoLoad="true" />
	</item>
	<item id="PYDM" alias="拼音代码" length="10" />
	<item id="WZZT" alias="物资状态" type="int" length="1" >
		<dic>
			<item key="-1" text="注销"/>
			<item key="1" text="正常"/>
		</dic>
	</item>
	<item id="JGID" alias="机构名称" type="string" width="200" length="20">
		<dic id="phis.@manageUnit" />
	</item>
	<item id="SFBZ" alias="收费标志" type="int" length="1" renderer="onRenderer"/>
  
	<item id="WBDM" alias="五笔代码" length="10" display="2"/>
	<item id="JXDM" alias="角形代码" length="10" display="2"/>
	<item id="QTDM" alias="其它代码" length="10" display="2"/>
	<item id="KWBH" alias="库位编号" length="15" display="2"/>
	<item id="BKBZ" alias="备库标志" type="int" length="1" display="2"/>
	<item id="YCWC" alias="一次性卫材" type="int" length="1" display="2"/>
	<item id="JLBZ" alias="计量标志" type="int" length="1" display="2"/>
	<item id="SXYJ" alias="失效预警天数" type="int" length="4" display="2"/>
	<item id="ZJFF" alias="折旧方法" type="int" length="6" display="2"/>
	<item id="ZJNX" alias="折旧年限" type="int" length="4" display="2"/>
	<item id="ZGZL" alias="总工作量" type="double" length="12" precision="2" display="2"/>
	<item id="JCZL" alias="净残值率" type="double" length="5" precision="2" display="2"/>
	<item id="WZTM" alias="物品条码" length="30" display="2"/>
	<item id="ZDTM" alias="自定条码" length="30" display="2"/>
	<item id="EJJK" alias="二级建库" type="int" length="1" display="2"/>
	<item id="JGCL" alias="加工材料" type="int" length="1" display="2"/>
	<item id="CWBM" alias="财务编号" length="20" display="2"/>
	<item id="GYBZ" virtual="true" alias="公用标志" length="20" display="2"/>
</entry>
