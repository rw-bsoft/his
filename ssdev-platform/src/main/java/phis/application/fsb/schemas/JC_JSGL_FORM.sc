<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_JSGL" alias="病人入院">
	<item id="ZYHM" alias="家床号码" length="10"/>
	<item id="JCLX" alias="家床类型" type="int" length="20">
		<dic autoLoad="true">
			<item key="1" text="治疗型"/>
			<item key="2" text="康复型"/>
			<item key="3" text="舒缓照顾型"/>
		</dic>
	</item>
	<item id="BRXM" alias="姓名" type="long" fixed="true" length="18" not-null="1"/>
	<item id="BRXZ" alias="性质" type="long" fixed="true" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="KSRQ" alias="开始日期" type="date" xtype="datetimefield" fixed="true"/>
	<item id="ZZRQ" alias="终止日期" type="date" xtype="datetimefield" fixed="true"/>
	<item id="FYHJ" alias="总费用" type="double" fixed="true" precision="2" not-null="1"/>
	<item id="ZFHJ" alias="自负" type="double" fixed="true" precision="2" not-null="1"/>
	<item id="JKHJ" alias="缴款" type="double" fixed="true" precision="2" not-null="1"/>
	<item id="JSJE" alias="余款" type="double" fixed="true" precision="2" not-null="1"/>
	<item id="ZYTS" alias="天数" type="int" precision="2" fixed="true" not-null="1"/>
	<item id="JSRQ" alias="结算日期" type="date" xtype="datetimefield" fixed="true" not-null="1"/>
	
	<item id="SRKH" alias="输入的卡号(杭州医保用)"   length="40"  display="0"/>
	<item id="ZYLSH" alias="住院流水号(杭州医保用)"   length="18"  display="0"/>
	<item id="YYLSH" alias="住院交易流水号(杭州医保用)"  type="string"  length="20" display="0"/>
	<item id="GRBH" alias="个人编号(杭州市医保用)" length="20" display="0"/>
</entry>
