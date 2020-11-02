<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YLSF_AL" alias="医疗收费" tableName="GY_YLSF">
	<item id="FYXH" alias="费用序号"  length="18" type="int" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="7275"/>
		</key>
	</item>
	<item id="FYMC" alias="费用名称"  type="string" length="80" width="130" not-null="1"/>
	<item id="FYDW" alias="单位" type="string" length="4" width="70"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" target="FYMC" codeType="py" queryable="true" />
	<item id="MZSY" alias="门诊" length="1" not-null="1" type="int" width="100" display="0" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="ZYSY" alias="住院" length="1" width="40" type="int" not-null="1" display="0" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="YJSY" alias="医技" length="1" width="40" type="int" not-null="1" display="0" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="TJFY" alias="特检" length="1" width="40" type="int" not-null="1" display="0" defaultValue="0">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="TXZL" alias="特治" length="1" width="40" type="int" not-null="1" display="0" defaultValue="0">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="WBDM" alias="五笔代码" display="0" type="string" length="10" target="FYMC" codeType="wb"/>
	<item id="JXDM" alias="角形代码" display="0" type="string" length="10" target="FYMC" codeType="jx"/>
	<item id="QTDM" alias="其他代码" display="0" type="string" length="10"/>
	<item id="FYGB" alias="费用归并" length="18" type="int" display="0" not-null="1"/>
	<item id="ZFPB" alias="作废判别" display="0" type="int" length="2" not-null="1" defaultValue="0" >
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="XMBM" alias="项目编码" display="0" type="string" length="20"/>
	<item id="BZJG" alias="标准价格" display="0" length="8" type="double" precision="2" defaultValue="0.0"/>
	<item id="XMLX" alias="项目类型" display="0" type="int" length="2">
		<dic id="phis.dictionary.projectType"/>
	</item>
	<item id="YJJK" alias="医技接口" display="0" type="int" length="9"/>
	<item id="JCSQ" alias="住院检查申请单" display="0" type="int" length="18"/>
	<item id="MZSQ" alias="门诊申请单" display="0" type="int" length="18"/>
	<item id="TSTS" alias="特殊提示" display="0" type="string" length="255"/>
	<item id="LISLX" alias="LIS类型" display="0" type="int" length="2"/>
	<item id="XMFL" alias="项目分类" display="0" type="string" length="16"/>
	<item ref="b.FYDJ" alias="费用单价" type="double" length="16"/>
	<relations>
	<relation type="parent" entryName="phis.application.cic.schemas.GY_YLMX_CIC" >
			<join parent="FYXH" child="FYXH"/>
		</relation>
	</relations>
</entry>
