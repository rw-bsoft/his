<?xml version="1.0" encoding="UTF-8"?>

<entry id="NHDZ_YK_TYPK" alias="药品信息" tableName="YK_TYPK" sort="a.YPXH desc" >
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品编码" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true"  layout="JBXX" >
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="8869" />
		</key>
	</item>
	<item id="YPMC" alias="药品通用名" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX" selected="true" queryable="true" />
	<item id="YPGG" alias="规格" type="string" length="20"  layout="JBXX"/>
	<item id="YPDW" alias="单位" type="string" length="2" layout="JBXX" not-null="1"/>
	<item id="YPSX" alias="剂型" type="long"  not-null="1" 
		length="18" layout="JBXX">
		<dic id="phis.dictionary.dosageForm" defaultIndex="0"/>
	</item>
	<item id="PYDM" alias="拼音码" type="string" length="10" not-null="1" target="YPMC" codeType="py"
		queryable="true" layout="JBXX">
	</item>
	<item id="NHBM_BSOFT" alias="农合编码" type="string" length="20" layout="JBXX" />
	<item id="NHBM_SH" alias="审核状态" type="string" length="1" layout="JBXX" queryable="true" >
		<dic>
      		<item key="0" text="未对照"/>
      		<item key="1" text="审核通过"/>
      		<item key="2" text="审核不通过"/>
      		<item key="9" text="已对照"/>
		</dic>
	</item>	
	
</entry>
