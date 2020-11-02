<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YLSF_XG" tableName="GY_YLSF" alias="医疗收费">
	<item id="FYXH" alias="费用序号" length="18" not-null="1" type="long" display = "0" generator="assigned" pkey="true" layout="JBXX">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="7275" fillPos="before"/>
		</key>
	</item>
	<item id="FYMC" alias="费用名称" type="string" length="80" not-null="1"  layout="JBXX"/>
	<item id="FYDW" alias="费用单位" type="string" length="16"  layout="JBXX"/>
	<item id="BZJG" alias="标准价格"  display="2"  type="double" precision="2" minValue="-999999.99" maxValue="999999.99" defaultValue="0.0" layout="JBXX"/>
	<item id="PYDM" alias="拼音代码" type="string" length="50" layout="DMSX" target="FYMC" codeType="py" />
	<item id="WBDM" alias="五笔代码" type="string" length="50" layout="DMSX" target="FYMC" codeType="wb" />
	<item id="JXDM" alias="角形代码" type="string" length="50" layout="DMSX" target="FYMC" codeType="jx" />
	<item id="BHDM" alias="笔画代码" type="string" length="50" layout="DMSX" target="FYMC" codeType="bh"/>
	<item id="QTDM" alias="其他代码" type="string" length="50" layout="DMSX"/>
	
	<item id="MZSY" alias="门诊使用" length="1" layout="SYQK" colspan="4" type="int" xtype="checkbox" defaultValue="1"/>
	<item id="ZYSY" alias="住院使用" length="1" type="int" layout="SYQK" colspan="4" xtype="checkbox" defaultValue="1"/>	
	<item id="YJSY" alias="医技使用" length="1" type="int" layout="SYQK" colspan="4" xtype="checkbox" defaultValue="1"/>		
	<item id="TJFY" alias="特检" length="1" fixed="true" type="int" colspan="2" layout="QT" xtype="checkbox" display="0" defaultValue="0"/>
	<item id="TXZL" alias="特治" length="1" fixed="true" type="int" colspan="2" layout="QT" xtype="checkbox" display="0" defaultValue="0"/>
	<item id="XMLX" alias="项目类型" length="2" type="int" layout="QT">
		<dic id="phis.dictionary.projectType"/>
	</item>
	<item id="XMBM" alias="项目编码" type="string" length="20" layout="QT"/>
	<item id="BASYGB" alias="病案首页归并" type="int" length="4" layout="QT">
		<dic id="phis.dictionary.BASYGBDIC" render="Tree" onlySelectLeaf="true" autoLoad="true"></dic>
	</item>
	<item id="ZYCJGB" alias="中医创建归并" type="string" length="1" layout="QT" width="100">
		<dic id="phis.dictionary.ZYCJGB" autoLoad="true"></dic>
	</item>
	<item id="ZYZLF" alias="中医治疗费" type="string" length="1" layout="QT" width="100" defaultValue="0">
		<dic id="phis.dictionary.confirm" autoLoad="true"></dic>
	</item>
	<item id="FYGB" alias="费用归并" length="18"  display="2" not-null="1"/>
	<item id="ZFPB" alias="作废判别" display="2" length="1" type="int" not-null="1" defaultValue="0"/>
	<item id="JCDL" alias="检查大类"  type="string" length="2" layout="QT" not-null="1">
		<dic>
			<item key="01" text="超声类" />
			<item key="02" text="放射类" />
		</dic>
	</item>
	<item id="WJBM" alias="物价编码" type="string" length="20" layout="JBXX"/>

</entry>
