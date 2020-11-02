<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_BRZD_CIC" tableName="MS_BRZD" alias="门诊诊断信息" sort="PLXH ASC">
	<item id="JLBH" alias="记录编号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase"  length="18" startPos="1000" />
		</key>
	</item>
	<!--<item id="ZXLB" alias="" type="int" virtual="true" fixed="true" defaultValue="1" display="1" renderer="zxzdRenderer" width="20"/>-->
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="JGID" alias="就诊机构" type="string" length="25" not-null="1" display="0"/>
	<item id="JZXH" alias="就诊序号" type="long" length="18" not-null="1" display="0"/>
	<item id="NUM" alias=" " width="20" virtual="true" display="1" renderer="numRenderer" type="string"/>
	<item id="ZXLB" alias="中西类别" type="int" virtual="true" defaultValue="1" display="1" width="70">
		<dic>
			<item key="1" text="西医"/>
			<item key="2" text="中医"/>
		</dic>
	</item>
	<item id="DEEP" alias="诊断级别" type='int' length="2" display="0" />
	<item id="SJZD" alias="上级诊断" type="int"  display="0"/>
	<item id="ZDMC" alias="诊断名称" not-null="1" type="string" mode="remote" length="60" width="150" renderer="zdmcRenderer"/>
	<item id="ZZBZ" alias="" length="1" type="int" renderer="ZzbzRenderer" width="25" display="1" fixed="false" />
	<item id="ZDXH" alias="诊断序号" not-null="1" type="long" display="0" length="10" />
	<item id="PLXH" alias="排列序号" not-null="1" type="int" display="0" length="4" />
	<item id="ICD10" alias="诊断编码" not-null="1" type="string" fixed="true" length="10" />
	<item id="ZDBW" alias="部位/证侯" display="0" type="int" length="12" width="100" defaultValue="0"/>
	<item id="MC" virtual="true" mode="remotezh" alias="部位/证侯"/>
	<item id="ZDYS" alias="诊断医生" type="string" length="8" display="2" defaultValue="%user.userId">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="ZDSJ" alias="诊断时间" type="datetime" width="130"/>
	<item id="ZDLB" alias="诊断类别" defaultValue="1" type="int" length="20" fixed="true" display="0">
		<dic id="phis.dictionary.diagnosisType"/>
	</item>
	
	<item id="CFLX" alias="处方类型" type="int" length="1" display="0" defaultValue="1">
		<dic id="phis.dictionary.prescriptionType" editable="false"/>
	</item>
	
	<item id="ZGQK" alias="转归情况" type="string" length="8" display="0"/>
	<item id="ZGSJ" alias="转归时间" type="date" display="0"/>
	<item id="FZBZ" alias="状态" type="int" defaultValue="0" width="50">
		<dic>
			<item key="0" text="初诊"/>
			<item key="1" text="复诊"/>
		</dic>
	</item>
	<item id="FBRQ" alias="发病日期" xtype="datefield" type="date" width="130" />
	<item id="JBPB" alias="疾病判别" type="string" display="1" length="500" width="100" fixed="true">
		<dic id="phis.dictionary.diseaseKind"/>
	</item>
	<item id="JBBGK" alias="疾病报告卡" type="string" display="0" length="500" width="100" fixed="true" virtual="true">
  		<dic id="phis.dictionary.diseaseReportType"/>
	</item>
</entry>
