<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.his.schemas.HIS_MedicalHistory" alias="就诊历史" sort="a.GHSJ desc">
	<item id="JZXH" alias="就诊序号" length="18" type="long" not-null="1" hidden="true">
	</item>
	<item id="GHSJ" alias="挂号时间" type="datetime" width="140" defaultValue="%server.date.datetime"/>
	<item id="CZPB" alias="门诊类型" type="int" fixed="true" length="4" display="0">
		<dic>
			<item key="1" text="初诊"/>
			<item key="0" text="复诊"/>
		</dic>
	</item>
	<item id="KSDM" alias="科室" type="long" length="4" width="80" not-null="1">
		<dic id="chis.dictionary.department"/>
	</item>
	<item id="YSDM" alias="医生" type="string" length="10" width="80" not-null="1">
		<dic id="chis.dictionary.doctor" />
	</item>
	
	<item id="GHXH" asName="SBXH" alias="挂号序号" type="long" length="18" not-null="1" display="0"/>
	<item id="BRBH" asName="BRID" alias="病人编号" type="long" length="18" not-null="1" display="0"/>
	<item id="EMPIID" alias="empiId" type="string" length="32" display="0"/>
	<item id="JGID" alias="就诊机构" length="25" width="160">
		<dic id="chis.@manageUnit"/>
	</item>
	<item id="JZHM" alias="就诊号码"  type="string" length="12" width="120" display="0"/>
	<item id="MZHM" alias="门诊号码" type="string" length="32" width="120" not-null="1" display="0" selected="true"/>
	<item id="BRXZ" alias="性质" type="string" fixed="true" length="18" width="60" display="0">
		<dic id="chis.dictionary.patientProperties"/>
	</item>
	<item id="BRXM" alias="姓名" type="string" fixed="true" length="40" display="0"/>
	<item id="BRXB" alias="性别" type="string" fixed="true" length="4" width="40" display="0">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="CSNY" alias="出生年月" type="date" fixed="true" length="4" display="0" width="40" />
	<item id="ZYZD" alias="主要诊断" length="18" display="0"/>
	<item id="KSSJ" alias="开始时间" type="timestamp" not-null="1" width="140" display="0"/>
	<item id="JSSJ" alias="结束时间" type="timestamp" width="140" display="0"/>
	<item id="JZZT" alias="就诊状态" type="int" length="1" not-null="1" display="0"/>
	<item id="YYXH" alias="复诊预约序号" length="18" display="0"/>
	<item id="FZRQ" alias="复诊日期" type="timestamp" display="0"/>
	<item id="GHFZ" alias="挂号复诊" length="1" display="0"/>
</entry>