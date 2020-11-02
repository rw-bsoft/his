<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRZD" tableName="JC_BRZD" alias="家床诊断信息" sort="JLBH ASC">
	<item id="JLBH" alias="记录编号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase"  length="18" startPos="1000" />
		</key>
	</item>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="JGID" alias="就诊机构" type="string" length="25" not-null="1" display="0"/>
	<item id="ZYH" alias="家床号" type="long" length="18" not-null="1" display="0"/>
	<item id="CCXH" alias="查床序号" type="long" length="18" not-null="1" display="0"/>
	<item id="ZDLB" alias=" " width="20" defaultValue="1" type="int" length="20" renderer="showZd" fixed="true" />
	<item id="ZDMC" alias="诊断名称" not-null="1" type="string" fixed="true" length="60" width="140" />
	<item id="ZDXH" alias="诊断序号" not-null="1" type="long" display="0" length="10" />
	<item id="ICD10" alias="ICD码" not-null="1" type="string" fixed="true" length="10" display="0" />
	<item id="ZDYS" alias="诊断医生" not-null="1" type="string" display="0" length="10" >
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="ZGQK" alias="转归情况" type="string" length="8" display="0"/>
	<item id="ZDSJ" alias="诊断时间" type="date" display="0"/>
</entry>
