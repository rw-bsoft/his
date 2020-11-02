<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_BRZD" tableName="MS_BRZD" alias="门诊诊断信息" sort="PLXH ASC">
	<item id="JLBH" alias="记录编号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase"  length="18" startPos="1000" />
		</key>
	</item>
	<item id="ZDSJ" alias="诊断时间" xtype="datetimefield" type="datetime" width="140" defaultValue="%server.date.datetime" />
	<item id="ZDLB" alias="诊断类别" defaultValue="1" type="int" length="20" fixed="true" display="0">
		<dic id="phis.dictionary.diagnosisType"/>
	</item>
	<item id="ZDMC" alias="诊断名称" not-null="1" type="string" fixed="true" length="60" width="220" renderer="zdmcRenderer"/>
	<item id="ICD10" alias="诊断编码" not-null="1" type="string" fixed="true" length="10"  width="70"/>
	
	<item id="FZBZ" alias="状态" type="int" defaultValue="0" width="50">
		<dic>
			<item key="0" text="初诊"/>
			<item key="1" text="复诊"/>
		</dic>
	</item>
	<item id="ZDYS" alias="诊断医生" type="string" length="8" display="0" defaultValue="%user.userId">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="JGID" alias="就诊机构" type="string" length="25" not-null="1" display="0"/>
	<item id="JZXH" alias="就诊序号" type="long" length="18" not-null="1" display="0"/>
	<item id="DEEP" alias="诊断级别" type='int' length="2" display="0" />
	<item id="SJZD" alias="上级诊断" type="int"  display="0"/>
	<item id="ZDXH" alias="诊断序号" not-null="1" type="long" display="0" length="10" />
	<item id="PLXH" alias="排列序号" not-null="1" type="int" display="0" length="4" />
	<item id="ZDBW" alias="部位ID/症候ID" display="0" type="int" length="12" defaultValue="0"/>
	<item id="BWMC" virtual="true" alias="部位/症候" fixed="true" width="150" not-null="1" type="string" display="0" length="12"/>
	<item id="CFLX" alias="处方类型" type="int" length="1" display="0" defaultValue="1">
		<dic id="phis.dictionary.prescriptionType" editable="false"/>
	</item>
	
	<item id="ZGQK" alias="转归情况" type="string" length="8" display="0"/>
	<item id="ZGSJ" alias="转归时间" type="date" display="0"/>
	
	<item id="FBRQ" alias="发病日期" xtype="datetimefield" width="150" type="datetime" defaultValue="%server.date.date" />
</entry>
