<?xml version="1.0" encoding="UTF-8"?>

<entry id="MS_ZYZ_INFO" tableName="MS_BRDA" alias="患者信息">
	<item id="BRID" alias="病人ID号" type="string" length="18" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="10" startPos="1"/>
		</key>
	</item>
	<item id="MZHM" alias="门诊号码" fixed="true" type="string"  length="32"/>
	<item id="BRXM" alias="病人姓名" fixed="true" type="string" length="40"/>
	<item id="BRXB" alias="病人性别" fixed="true" type="string" length="4">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="NL" alias="病人年龄" fixed="true" type="int" virtual="true" length="40"/>
	<item id="BRXZ" alias="病人性质" fixed="true" type="string" length="18">
		<dic id="phis.dictionary.patientProperties_MZ"/>
	</item>
	<item id="ZDMC" alias="诊断名称" fixed="true" type="string"  colspan="2" length="32" virtual="true" />
	<item id="YYRQ" alias="住院日期" type="date" length="32" minValue="%server.date.date" virtual="true" />
	<item id="BQDM" alias="病区" type="long" length="18" >
		<dic id="phis.dictionary.department_bq" autoLoad="true"></dic>
	</item>
	<item id="BRCH" alias="病人床号" length="12" type="string"/>
	<item id="BRDHHM" alias="病人电话" type="string" length="20" virtual="true"/>
	<item id="LXRM" alias="联系人名" type="string" length="10" virtual="true"/>
	<item id="LXGX" alias="联系关系" type="string" length="4" virtual="true">
		<dic id="phis.dictionary.GB_T4761" />
	</item>
	<item id="LXDH" alias="联系电话" type="string" length="20" virtual="true"/>
	<item id="LXDZ" alias="联系地址" type="string" colspan="2" length="40" virtual="true" />
	<item id="YZ" alias="医嘱" type="string" length="500" colspan="4" xtype="textarea" width="200" />
	
	<item id="REMARK" alias="备注" type="string" tag="radioGroup" length="1">
		<dic>
			<item key="1" text="病重" />
			<item key="2" text="病危" />
			<item key="3" text="呼吸传染" />
			<item key="4" text="接触传染" />
			<item key="5" text="肠道传染" />
		</dic>
	</item>
	<item id="INHOSMETHOD" alias="入院途径" type="string" tag="radioGroup" length="1">
		<dic>
			<item key="1" text="急诊" />
			<item key="2" text="门诊" />
			<item key="3" text="其他医疗机构转入" />
			<item key="9" text="其他" />
		</dic>
	</item>
</entry>