<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_RYZD" alias="病人诊断库">
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="ZDLB" alias="诊断类别" type="int" defaultValue="2" length="6" not-null="1" pkey="true" >
		<dic id="hosDiagnosisType" />
	</item>
	<item id="ZDXH" alias="诊断序号" type="long" length="18" not-null="1" pkey="true" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="ZGQK" alias="转规情况" type="int" length="6" >
		<dic id="outcomeSituation" />
	</item>
	<item id="TXBZ" alias="提醒标志" type="int" length="1" display="0"/>
</entry>
