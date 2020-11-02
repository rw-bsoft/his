<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_YLMX_DR" tableName="GY_YLMX" alias="医院医疗明细项目" isCompositeKey="true">
	<item id="JGID" alias="机构ID" length="25" not-null="1" type="string" display="0" defaultValue="%user.manageUnit.id" pkey="true"/>
	
	<item id="FYXH" alias="费用序号" length="18" display="0" type="long" not-null="1" pkey="true"/>
	<item ref="b.FYMC" alias="费用名称" type="string" fixed="true" summaryType="count" mode="remote" width="250" anchor="100%" summaryRenderer="totalYPSL" length="50" not-null="1" layout="JBXX"/>
	<item ref="b.FYGB" alias="费用归并" length="18"  display="2" not-null="1" defaultValue="0"/>
	<item ref="b.FYDW" alias="单位" type="string" length="4" fixed="true" width="40" layout="JBXX"/>
	<item ref="b.PYDM" alias="拼音码" type="string" length="10" fixed="true" queryable="true" layout="DMSX"/>
	<item id="FYDJ" alias="单价" maxValue="999999.99" minValue="-999999.99" type="double" precision="2" not-null="1" layout="JG"/>
	<item ref="b.MZSY" alias="门诊" length="1" display="0" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item ref="b.ZYSY" alias="住院" length="1" display="0" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item ref="b.YJSY" alias="医技" length="1" display="0" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item ref="b.TJFY" alias="特检" length="1" display="0" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item ref="b.TXZL" alias="特治" length="1" display="0" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"></dic>
	</item>
	<item ref="b.WBDM" alias="五笔代码" display="2" type="string" fixed="true" length="10" layout="DMSX" target="FYMC" codeType="wb"/>
	<item ref="b.JXDM" alias="角形代码" display="2" type="string" fixed="true" length="10" layout="DMSX" target="FYMC" codeType="jx"/>
	<item ref="b.BHDM" alias="笔画代码" display="2" type="string" fixed="true" length="10" layout="DMSX" target="FYMC" codeType="bh"/>
	<item ref="b.XMLX" alias="项目类型" display="2" fixed="true" length="2" layout="QT">
		<dic id="phis.dictionary.projectType" autoLoad="true"/>
	</item>
	<item ref="b.QTDM" alias="其他代码" display="2" type="string" length="10" fixed="true" layout="DMSX"/>
	
	<item id="FYKS" alias="费用科室" type="long" display="2" length="18" layout="JG">
		<dic id="phis.dictionary.department_yj" searchField="PYCODE" autoLoad="true"  filter="['eq',['s','item.properties.ORGANIZCODE']],['$','%user.manageUnit.ref']]" />
	</item>
	<item id="ZDCR" alias="自动插入" type="int" not-null="1" defaultValue="0" layout="JG">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="DZBL" alias="打折比例" display="0" type="double" length="6" precision="3" not-null="1"/>
	<item ref="b.BZJG" alias="标准价格" display="2" length="8" precision="2" fixed="true" layout="JBXX"/>
	<item id="JGBZ" alias="价格标志" type="int" length="1" layout="JBXX" defaultValue="0">
		<dic>
			<item key="0" text="未启用"/>
			<item key="1" text="启用"/>
		</dic>
	</item>
	<item ref="b.XMBM" alias="项目编码" display="2" fixed="true" type="string" length="20" layout="QT"/>
	<item id="ZFPB" alias="是否作废" length="2" not-null="1" type="int"  >
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item ref="b.WJBM" alias="物价编码" type="string" fixed="true" length="20" layout="JBXX" width="60"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.GY_YLSF_DR" />
	</relations>
</entry>
