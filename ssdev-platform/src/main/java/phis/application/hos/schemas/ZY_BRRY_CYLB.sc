<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_BRRY" alias="病人入院">
	<item id="JSXZ" alias="结算性质" length="20" width="60" renderer="jsxzRender"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned"/>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0" not-null="1"/>
	<item ref="b.FPHM" display="1" pkey="true"/>
	<item id="ZYHM" alias="住院号码" length="10" not-null="1"/>
	<item id="BRXM" alias="姓名" length="20" not-null="1" width="60"/>
	<item id="BRCH" alias="床号" length="12" width="40"/>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院日期" type="date" width="130" not-null="1"/>
	<item id="CYRQ" alias="出院日期" type="date" width="130"/>
	<item id="NJJBLSH" alias="南京金保流水号" type="string" width="130"/>
	<item id="NJJBYLLB" alias="南京金保医疗类别" type="string" width="130"/>
	<item id="YBMC" alias="医保病种" type="string" width="130"/>
	<item id="JSRQ" alias="结算日期" type="date" display="0"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" not-null="1" display="0"/>
	<item id="CYPB" alias="备注" type="int" length="2" not-null="1" renderer="cypbRender">
		<dic id="phis.dictionary.CYPBDic" autoLoad="true" />
	</item>
	<item id="JSLX" alias="结算类型" defaultValue="5" display="0" virtual="true" queryable="true">
		<dic id="phis.dictionary.JSLXDic" autoLoad="true" />
	</item>
	<item ref="b.JSLX" display="0"/>
	<item ref="b.JSCS" display="0"/>
	<item ref="b.KSRQ" display="0"/>
	<item ref="b.ZZRQ" display="0"/>
	<item ref="b.FYHJ" display="0"/>
	<item ref="b.ZFHJ" display="0"/>
	<item ref="b.JKHJ" display="0"/>
	<item ref="b.JSRQ" display="0"/>
	<item ref="b.CZGH" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.hos.schemas.ZY_ZYJS_CYLB" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
