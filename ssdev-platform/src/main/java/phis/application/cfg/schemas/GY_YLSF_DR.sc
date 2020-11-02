<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YLSF_DR" tableName="GY_YLSF" alias="医疗收费">
	<item id="FYXH" alias="费用序号"  length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="7275" fillPos="before"/>
		</key>
	</item>
	<item id="FYMC" alias="费用名称"  type="string" width="180" length="80" not-null="1"/>
	<item id="FYDW" alias="单位" type="string" length="4" width="40"/>
	<item id="PYDM" alias="拼音代码" type="string" length="10" queryable="true" target="FYMC" codeType="py"/>
	<item id="MZSY" alias="门诊" length="1" display="0" not-null="1" width="40" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="ZYSY" alias="住院" length="1" display="0" width="40" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="YJSY" alias="医技" length="1" display="0" width="40" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="TJFY" alias="特检" length="1" display="0" width="40" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="TXZL" alias="特治" length="1" display="0" width="40" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item id="WBDM" alias="五笔代码" display="2" type="string" length="10" target="FYMC" codeType="wb"/>
	<item id="JXDM" alias="角形代码" display="2" type="string" length="10" target="FYMC" codeType="jx"/>
	<item id="BHDM" alias="笔画代码" display="2" type="string" length="10" target="FYMC" codeType="bh"/>
	<item id="QTDM" alias="其他代码" display="2" type="string" length="10"/>
	<item id="FYGB" alias="费用归并" length="18"  display="2" not-null="1" defaultValue="0"/>
	<item id="ZFPB" alias="作废判别" display="2" length="1" not-null="1" defaultValue="0"/>
	<item id="XMBM" alias="项目编码" type="string" width="100" length="20"/>
	<item id="BZJG" alias="标准价格" length="8" precision="2"/>
	<item id="XMLX" alias="项目类别" display="2" length="2">
		<dic id="phis.dictionary.projectType" autoLoad="true"/>
	</item>
	<item id="YJJK" alias="医技接口" display="2" length="9"/>
	<item id="JCSQ" alias="住院检查申请单" display="2" length="18"/>
	<item id="MZSQ" alias="门诊申请单" display="2" length="18"/>
	<item id="TSTS" alias="特殊提示" display="2" type="string" length="255"/>
	<item id="LISLX" alias="LIS类型" display="2" length="2"/>
	<item id="XMFL" alias="项目分类" display="2" type="string" length="16"/>
	<item id="WJBM" alias="物价编码" display="2" type="string" length="20" />
</entry>
