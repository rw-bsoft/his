<?xml version="1.0" encoding="UTF-8"?>

<entry id="ZHDZ_GY_YLSF" tableName="GY_YLSF" alias="医疗收费" sort="PYDM">
	<item id="FYXH" alias="费用序号"  length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="7275"/>
		</key>
	</item>
	<item id="FYMC" alias="费用名称"  type="string" length="80" width="220" not-null="1"/>
	<item id="FYDW" alias="单位" type="string" length="4"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" target="FYMC" codeType="py" queryable="true" />
	<item id="FYGB" alias="费用归并" length="18" type="int" not-null="1">
		<dic id="phis.dictionary.feesDic" autoLoad="true"/>
	</item>
	<item id="ZFPB" alias="是否作废" display="1" type="int" length="2" not-null="1" defaultValue="0" >
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="XMBM" alias="项目编码" display="2" type="string" length="20"/>
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
