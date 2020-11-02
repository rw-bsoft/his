<?xml version="1.0" encoding="UTF-8"?>

<entry tableName="MS_GHMX_MANAGE" alias="挂号明细表">
	<item id="SBXH" alias="识别序号" length="18"  type="long" display="0" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" length="8"  type="string" display="0" />
	<item id="BRID" alias="病人ID号" length="18"  type="long" display="0" />
	<item id="JZKH" alias="卡号" type="string" colspan="11" virtual="true" length="32" />
	<item id="MZHM" alias="门诊号码" type="string" length="32" virtual="true" display="0" />
	<item id="BRXZ" alias="病人性质" fixed="true" colspan="8" type="string" length="18">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true"/>
	</item>
	<item id="BRXM" alias="病人姓名" fixed="true" colspan="8" type="string" virtual="true" length="40"/>
	<item id="BRXB" alias="病人性别" fixed="true" colspan="8" type="string" virtual="true" length="4">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="GHSJ" alias="挂号日期" fixed="true" minValue="%server.date.date" defaultValue="%server.date.date" type="date" colspan="8"/>
	<item id="GHLB" alias="类别" fixed="true" type="string" hideLabel="true" length="1" colspan="3">
		<dic autoLoad="true">
			<item key="1" text="上午"/>
			<item key="2" text="下午"/>
		</dic>
	</item>
	<item id="ZBLB" alias="值班类别" fixed="true" type="string" display="0" length="4">
		<dic autoLoad="true">
			<item key="1" text="上午"/>
			<item key="2" text="下午"/>
		</dic>
	</item>
	<item id="KSDM" alias="科室代码" display="0" type="string" length="18"/>
	<item id="KSMC" alias="挂号科室" fixed="true" colspan="8" virtual="true" type="string" length="18"/>
	<item id="YSDM" alias="医生代码" display="0" type="string" length="10"/>
	<item id="JZYS" alias="医生" fixed="true" colspan="8" type="string" length="10"/>
	<item id="JZHM" alias="就诊号码" fixed="true" colspan="8" type="string" length="20"/>
	<item id="GHJE" alias="挂号费" type="double" colspan="11" fixed="true" defaultValue="0.00" length="10" precision="2"/>
	<item id="ZLJE" alias="诊疗费" type="double" colspan="8" fixed="true" defaultValue="0.00" length="10" precision="2"/>
	<item id="ZJFY" alias="专家费" type="double" colspan="8" fixed="true" defaultValue="0.00" length="10" precision="2"/>
	<item id="BLJE" alias="病历费" type="double" colspan="8" defaultValue="0.00" length="11" precision="2" maxValue="99999999.99"/>
	<item id="YBMC" alias="医保病种" type="string" colspan="11" defaultValue="20" iem="false">
		<dic id="phis.dictionary.ybJbbm" searchField="PYDM" autoLoad="true" />
	</item>
	<item id="NJJBYLLB" alias="医疗类别" type="string" colspan="8" defaultValue="11" length="18">
		<dic id="phis.dictionary.ybyllb_mz" searchField="PYDM" autoLoad="true"/>
	</item></entry>
