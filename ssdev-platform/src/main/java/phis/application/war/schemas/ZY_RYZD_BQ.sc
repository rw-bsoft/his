<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_RYZD" alias="病人诊断库" sort="a.ZDLB">
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="ZDLB" alias="诊断类别" type="int" defaultValue="2" length="6" not-null="1" pkey="true" >
		<dic id="phis.dictionary.hosDiagnosisType" autoLoad="true" />
	</item>
	<item id="ZDLB_D" alias="诊断类别" type="int" virtual="true" display="0"/>
	<item id="ZXLB" alias="中西类别" type="int" defaultValue="1" width="70">
		<dic id="phis.dictionary.hosDiagnosisExType" autoLoad="true" />
	</item>
	<item id="ZDXH" alias="诊断序号" type="long" length="18" not-null="1" pkey="true" display="0"/>
	<item id="ZDXH_D" alias="诊断序号" type="long" virtual="true" display="0"/>
	<item id="ZDMC" alias="诊断名称" width="150" mode="remote"/>
	<item id="ZDBW" alias="部位/证侯" display="0" type="int" length="12" width="100" defaultValue="0"/>
	<item id="ZHMC" alias="部位/证侯" virtual="true" mode="remotezh"/>
	<item id="ZDYS" alias="诊断医生" type="string" length="8" defaultValue="%user.userId">
		<dic id="phis.dictionary.doctor_cfqx" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" />
	</item>
	<item id="ZDSJ" alias="诊断时间" type="datetime" width="130"/>
	<item id="ICD10" alias="ICD码" fixed="true" />
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="ZGQK" alias="转规情况" type="int" length="6" >
		<dic id="phis.dictionary.outcomeSituation" autoLoad="true" />
	</item>
	<item id="TXBZ" alias="提醒标志" type="int" length="1" display="0"/>
	<item id="JBPB" alias="疾病判别" type="string" display="1" length="500" width="100" fixed="true">
  		<dic id="phis.dictionary.tumor_kind"/>
  	</item>
</entry>
