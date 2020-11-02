<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院">
	<item id="JSXZ" alias="结算性质" length="20" width="60"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0" not-null="1"/>
	<item id="ZYHM" alias="住院号码" length="10" not-null="1"/>
	<item id="BRCH" alias="病人床号" length="12"/>
	<item id="BRXM" alias="病人姓名" length="20" not-null="1"/>
	<item id="BRXZ" alias="病人性质" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院日期" type="date" not-null="1"/>
	<item id="CYRQ" alias="出院日期" type="date"/>
	<item id="JSRQ" alias="结算日期" type="date" display="0"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" not-null="1" display="0"/>
	<item id="CYPB" alias="备注" type="int" length="2" not-null="1">
		<dic id="phis.dictionary.CYPBDic" autoLoad="true" />
	</item>
	<item id="NJJBLSH" alias="南京金保流水号" type="string" width="130"/>
	<item id="NJJBYLLB" alias="南京金保医疗类别" type="string" width="130"/>
	<item id="YBMC" alias="医保病种" type="string" width="130"/>
</entry>
