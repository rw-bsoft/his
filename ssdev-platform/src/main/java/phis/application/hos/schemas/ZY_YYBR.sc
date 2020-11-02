<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_YYBR" alias="预约病人" sort="a.YYRQ">
	<item id="SBXH" alias="预约号码" type="string" length="18" not-null="1" queryable="true" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="DJRQ" alias="登记日期" type="date" queryable="true" width="180"/>
	<item id="BAHM" alias="病案号码" length="10" queryable="true"/>
	<item id="BRXM" alias="姓名" length="20" queryable="true"/>
	<item id="BRXB" alias="性别" type="int" length="4" queryable="true">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="BRXZ" alias="性质" type="long" length="18">
		<dic id="phis.dictionary.patientProperties_ZY" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="YYKS" alias="预约科室" type="long" length="18">
		<dic id="phis.dictionary.department_zy" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YYRQ" alias="预约日期" type="datetime" defaultValue="%server.date.datetime" width="180"/>
	<item id="MZHM" alias="门诊号码" length="32" not-null="1" queryable="true"  selected="true"/>
	
	
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="GFZH" alias="病人证号" length="20" display="0"/>
	<item id="CSNY" alias="出生年月" type="date" fixed="true" not-null="1" layout="part3" display="0"/>
	<item id="SFZH" alias="身份证号" length="20" fixed="true" layout="part3" vtype="idCard" enableKeyEvents="true" display="0"/>
	<item id="HYZK" alias="婚姻状况" type="int" length="4" updates="true" defaultValue="90" fixed="true" layout="part3" display="0">
		<dic id="phis.dictionary.maritals" autoLoad="true"/>
	</item>
	<item id="ZYDM" alias="职业类别" length="4" defaultValue="Y" updates="true" fixed="true" layout="part3" display="0">
		<dic id="phis.dictionary.jobtitle" onlySelectLeaf="true" autoLoad="true"/>
	</item>
	<item id="SFDM" alias="" type="int" length="1" display="0"/>
	<item id="JGDM" alias="" type="int" length="1" display="0"/>
	<item id="MZDM" alias="民族" length="4" defaultValue="01" updates="true" fixed="true" layout="part3" display="0">
		<dic id="phis.dictionary.ethnic" autoLoad="true"/>
	</item>
	<item id="GJDM" alias="国籍" length="4" defaultValue="CN" updates="true" fixed="true" layout="part3" display="0">
		<dic id="phis.dictionary.nationality" autoLoad="true"/>
	</item>
	<item id="DWBH" alias="单位编号" type="int" length="6" display="0"/>
	<item id="GZDW" alias="工作单位" length="40" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="DWDH" alias="单位电话" length="16" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="DWYB" alias="单位邮编" length="6" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="HKDZ" alias="户口地址" type="string" display="0"/>
	<item id="HKYB" alias="邮政编码" length="6" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="LXRM" alias="联系人" length="20" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="LXGX" alias="联系关系" type="int" length="4" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.GB_T4761" autoLoad="true"/>
	</item>
	<item id="LXDH" alias="联系电话" length="16" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="LXDZ" alias="联系地址" length="40" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="PZHM" alias="凭证号码" length="10" display="0"/>
	<item id="SBHM" alias="社保号码" length="20" display="0"/>
	<item id="ZZTX" alias="在职退休" type="int" display="0"/>
	<item id="DBRM" alias="担保人" length="10" display="0"/>
	<item id="DBBZ" alias="保险病种" colspan="3" type="int" length="6" display="0"/>
	<item id="DBGX" alias="担保关系" type="int" length="4" display="0"/>
	<item id="RYRQ" alias="入院日期" type="datetime" defaultValue="%server.date.datetime" display="0"/>
	<item id="JGBZ" alias="" type="int" length="1" display="0"/>
	<item id="CZGH" alias="预约医生" length="10">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="JTDH" alias="家庭电话" length="16" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="CSD_SQS" alias="出生地_省" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.Province" autoLoad="true"/>
	</item>
	<item id="CSD_S" alias="出生地_市" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.City" autoLoad="true"/>
	</item>
	<item id="CSD_X" alias="出生地_县" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.County" autoLoad="true"/>
	</item>
	<item id="JGDM_SQS" alias="籍贯_省" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.Province" autoLoad="true"/>
	</item>
	<item id="JGDM_S" alias="籍贯_市" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.City" autoLoad="true"/>
	</item>
	<item id="XZZ_SQS" alias="现住址_省" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.Province" autoLoad="true"/>
	</item>
	<item id="XZZ_S" alias="现住址_市" type="long" labelWidth="30" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.City" autoLoad="true"/>
	</item>
	<item id="XZZ_X" alias="现住址_县" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.County" autoLoad="true"/>
	</item>
	<item id="XZZ_QTDZ" alias="现住址其他" length="60" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="XZZ_DH" alias="现住址电话" length="16" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="XZZ_YB" alias="现住址邮编" length="6" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="HKDZ_SQS" alias="户口_省" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.Province" autoLoad="true"/>
	</item>
	<item id="HKDZ_S" alias="户口_市" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.City" autoLoad="true"/>
	</item>
	<item id="HKDZ_X" alias="户口_县" type="long" length="18" fixed="true" updates="true" layout="part3" display="0">
		<dic id="phis.dictionary.County" autoLoad="true"/>
	</item>
	<item id="HKDZ_QTDZ" alias="户口其他" length="60" fixed="true" updates="true" layout="part3" display="0"/>
	<item id="BRCH" alias="病人床号" length="12" display="0"/>
	<item id="BRDHHM" alias="病人电话号码" length="20" display="0"/>
</entry>
