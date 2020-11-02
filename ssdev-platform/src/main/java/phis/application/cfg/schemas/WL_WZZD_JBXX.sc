<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WZZD_JBXX" tableName="WL_WZZD" alias="物资字典">
	<item id="WZXH" alias="物资序号" type="long" length="18" not-null="1" generator="assigned" pkey="true"  >
		<key>
			<rule name="increaseId" type="increase" startPos="1"  />
		</key>
	</item>
  
	<item id="FLMC" alias="物资分类" length="60" fixed="true" colspan="2" layout="JBXX"/>
	<item id="KFXH" alias="所属库房" type="int" fixed="true" length="8" layout="JBXX">
		<dic id="phis.dictionary.treasury"  autoLoad="true" />
	</item>
	<item id="GLKF" alias="管理库房" type="int" length="8" layout="JBXX">
		<dic id="phis.dictionary.treasury_glkf" filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" autoLoad="true" />
	</item>
	<item id="ZBLB" alias="帐薄类别" type="int" length="8" not-null="true"  layout="JBXX">
		<dic id="phis.dictionary.booksCategory" autoLoad="true"/>
	</item>
	<item id="HSLB" alias="核算类别" type="int" length="8"  not-null="true" layout="JBXX">
		<dic id="phis.dictionary.WL_HSLB_SJHS" autoLoad="true"/>
	</item>
	<item id="GLFS" alias="管理方式" type="int" length="1" not-null="true" layout="JBXX">
		<dic id="phis.dictionary.wzglfs"  autoLoad="true"/>
	</item>
	<item id="WZZT" alias="物资状态" type="int" length="1" defaultValue="1" fixed="true" layout="JBXX">
		<dic id="phis.dictionary.wzzt"  autoLoad="true"/>
	</item>
	<item id="WZGG" alias="物资规格" length="40" layout="JBXX"/>
	<item id="WZMC" alias="物资名称" length="60" not-null="true" colspan="2" layout="JBXX"/>
	<item id="WZDW" alias="物资单位" length="10" not-null="true" layout="JBXX"/>
	<item id="PYDM" alias="拼音代码" length="10" layout="JBXX" target="WZMC" codeType="py"/>
	<item id="WBDM" alias="五笔代码" length="10" layout="JBXX" target="WZMC" codeType="wb"/>
	<item id="JXDM" alias="角形代码" length="10" layout="JBXX" target="WZMC" codeType="jx"/>
	<item id="QTDM" alias="其它代码" length="10" layout="JBXX"/>
	<item id="KWBH" alias="库位编号" length="15" layout="JBXX" />
	<item id="GCSL" alias="高储数量" type="double" length="12" precision="2" layout="JBXX"/>
	<item id="DCSL" alias="低储数量" type="double" length="12" precision="2" layout="JBXX"/>
	<item id="SXYJ" alias="失效预警天数" type="int" length="4" labelWidth="150" layout="JBXX" />  

  
	<item id="YCWC" alias="一次性卫材" type="int" length="1"  defaultValue="0" layout="SX">
		<dic id="phis.dictionary.confirm" autoLoad="true" defaultIndex="0"/>
	</item>
	<item id="JLBZ" alias="计量标志" type="int" length="1"  defaultValue="0" layout="SX">
		<dic id="phis.dictionary.confirm" autoLoad="true" defaultIndex="0"/>
	</item>
	<item id="EJJK" alias="二级库存管理" type="int" length="1" defaultValue="0" layout="SX">
		<dic id="phis.dictionary.confirm" autoLoad="true" defaultIndex="0"/>
	</item>
	<item id="SFBZ" alias="可收费标志" type="int" length="1" defaultValue="0" layout="SX">
		<dic id="phis.dictionary.confirm" autoLoad="true" defaultIndex="0"/>
	</item>
	<item id="BKBZ" alias="备库标志" type="int" length="1" defaultValue="0" layout="SX">
		<dic id="phis.dictionary.confirm" autoLoad="true" defaultIndex="0"/>
	</item>
  
	<item id="WZTM" alias="物品条码" length="30"  layout="TMDY"/>
	<item id="ZDTM" alias="自定条码" length="30"  layout="TMDY"/>
 
  
	<item id="JGID" alias="机构ID" type="string"  length="20" display="0"/>
	<item id="ZJFF" alias="折旧方法" type="int" length="6" layout="SX" not-null="true">
		<dic id="phis.dictionary.zjff" autoLoad="true"/>
	</item>
	<item id="ZJNX" alias="折旧年限" type="int" length="4" layout="SX"/>
	<item id="ZGZL" alias="总工作量" type="double" length="12" precision="2" layout="SX"/>
	<item id="JCZL" alias="净残值率" type="double" length="5" precision="2" layout="SX"/>
	<item id="JGCL" alias="加工材料" type="int" length="1" display="0"/>
	<item id="CWBM" alias="财务编号" length="20"  display="0"/>
	<item id="GYBZ" virtual="true" alias="公用标志" length="20"/>
</entry>
