<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YLSF_CIC" tableName="GY_YLSF" alias="医疗收费" sort="PYDM">
	<item id="FYXH" alias="费用序号"  length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="7275"/>
		</key>
	</item>
	<item id="FYMC" alias="费用名称"  type="string" length="80" width="220" not-null="1"/>
	<item id="FYDW" alias="单位" type="string" length="4"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" target="FYMC" codeType="py" queryable="true" />
	<item id="MZSY" alias="门诊使用" length="1" not-null="1" type="int" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="ZYSY" alias="住院使用" length="1" type="int" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="YJSY" alias="医技使用" length="1"  type="int" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="TJFY" alias="特检" length="1" width="40" type="int" not-null="1" display="0" defaultValue="0">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="TXZL" alias="特治" length="1" width="40" type="int" not-null="1" display="0" defaultValue="0">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="WBDM" alias="五笔代码" display="2" type="string" length="10" target="FYMC" codeType="wb"/>
	<item id="JXDM" alias="角形代码" display="2" type="string" length="10" target="FYMC" codeType="jx"/>
	<item id="BHDM" alias="笔画代码" display="2" type="string" length="10" target="FYMC" codeType="bh"/>
	<item id="QTDM" alias="其他代码" display="2" type="string" length="10"/>
	<item id="FYGB" alias="费用归并" length="18" type="int" not-null="1">
		<dic id="feesDic" autoLoad="true"/>
	</item>
	<item id="ZFPB" alias="是否作废" display="1" type="int" length="2" not-null="1" defaultValue="0" >
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="XMBM" alias="项目编码" display="2" type="string" length="20"/>
	<item id="BZJG" alias="标准价格" length="8" type="double" precision="2" defaultValue="0.0"/>
	<item id="XMLX" alias="项目类型" display="2" type="int" length="2">
		<dic id="phis.dictionary.projectType"/>
	</item>
	<item id="YJJK" alias="医技接口" display="2" type="int" length="9"/>
	<item id="JCSQ" alias="住院检查申请单" display="2" type="int" length="18"/>
	<item id="MZSQ" alias="门诊申请单" display="2" type="int" length="18"/>
	<item id="TSTS" alias="特殊提示" display="2" type="string" length="255"/>
	<item id="LISLX" alias="LIS类型" display="2" type="int" length="2"/>
	<item id="XMFL" alias="项目分类" display="2" type="string" length="16"/>
	<item id="JCDL" alias="检查大类 " display="2" type="string" length="2"/>
	<item id="JCBWDM" alias="检查部位代码" display="0" type="string" length="18"/>
	<item id="BASYGB" alias="病案首页归并" type="int" length="4" not-null="1" width="150">
		<dic id="BASYGBDIC" autoLoad="true"></dic>
	</item>
	<item id="JCDL" alias="检查大类" display="2" type="string" length="2" layout="QT" not-null="1">
		<dic>
			<item key="01" text="超声类" />
			<item key="02" text="放射类" />
		</dic>
	</item>
</entry>
