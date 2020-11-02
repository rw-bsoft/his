<?xml version="1.0" encoding="UTF-8"?>

<entry id="MS_ZYYY_YYKS" tableName="ZY_YYBR" alias="挂号科室">
	<item id="SBXH" alias="识别序号" type="long" length="18" generator="assigned" display="0"  pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="30000" />
		</key>
	</item>	
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" not-null="1"/>
	<item id="BAHM" alias="病案号码" length="10" type="string" display="0"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0" not-null="1"/>
	<item id="MZHM" alias="门诊号码" length="32" type="string" display="0"/>
	<item id="BRXZ" alias="病人性质" type="long" display="0" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="GFZH" alias="公费证号" length="20" type="string" display="0"/>
	<item id="BRXM" alias="病人姓名" length="20" type="string" display="0" not-null="1"/>
	<item id="BRXB" alias="病人性别" type="int" display="0" length="4" not-null="1">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="CSNY" alias="出生年月" type="date" display="0"/>
	<item id="SFZH" alias="身份证号" length="20" type="string" display="0"/>
	<item id="HYZK" alias="婚姻状况" type="int" length="4" display="0"/>
	<item id="ZYDM" alias="职业代码" type="string" length="4" display="0"/>
	<item id="SFDM" alias="省份代码" type="int" length="4" display="0"/>
	<item id="JGDM" alias="籍贯代码" type="int" length="4" display="0"/>
	<item id="MZDM" alias="民族代码" type="string" length="4" display="0"/>
	<item id="GJDM" alias="国籍代码" type="string" length="4" display="0"/>
	<item id="DWBH" alias="单位编号" type="int" length="6" display="0"/>
	<item id="GZDW" alias="工作单位" type="string" length="40" display="0"/>
	<item id="DWDH" alias="单位电话" type="string" length="16" display="0"/>
	<item id="DWYB" alias="单位邮编" type="string" length="6" display="0"/>
	<item id="HKDZ" alias="户口地址" type="string" length="40" display="0"/>
	<item id="HKYB" alias="户口邮编" type="string" length="6" display="0"/>
	<item id="LXRM" alias="联系人名" type="string" length="10" display="0"/>
	<item id="LXGX" alias="联系关系" type="int" length="4" display="0"/>
	<item id="LXDZ" alias="联系地址" length="50" type="string" display="0"/>
	<item id="LXDH" alias="联系电话" length="16" type="string" display="0"/>
	<item id="PZHM" alias="凭证号码" length="10" type="string" display="0"/>
	<item id="SBHM" alias="社保号码" length="20" type="string" display="0"/>
	<item id="DBRM" alias="担保人名" length="10" type="string" display="0"/>
	<item id="DBGX" alias="担保关系" type="int" length="4" display="0"/>
	<item id="ZZTX" alias="在职退休" type="int" length="4" display="0"/>
	<item id="DBBZ" alias="大保病种" type="int" length="6" display="0"/>
	<item id="YYRQ" alias="预约日期" type="date" display="0" not-null="1"/>
	<item id="YYKS" alias="科室" type="long" fixed="true" length="18" width="180">
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE"/>
	</item>
	<item id="CWZS" alias="总床位数" virtual="true"  fixed="true" width="120"/>
	<item id="KCWS" alias="空床位数" virtual="true"  fixed="true" width="120"/>
	<item id="CYRS" alias="当日待出院人数" virtual="true"  fixed="true" width="120"/>
	<item id="YYRS" alias="当日通知人数" virtual="true"  fixed="true" width="120"/>
	<item id="RYRQ" alias="入院日期" type="date" not-null="1" display="0"/>
	<item id="DJRQ" alias="登记日期" type="date" not-null="1" display="0"/>
	<item id="JGBZ" alias="标志" type="int" length="1" display="0"/>
	<item id="CZGH" alias="操作工号" length="10" type="string" display="0">
		<dic id="phis.dictionary.doctor_cfqx" autoLoad="true"/>
	</item>
	<item id="JTDH" alias="家庭电话" length="16" type="string" display="0"/>
	<item id="CSD_SQS" alias="出生地_省区市" type="long" length="18" display="0"/>
	<item id="CSD_S" alias="出生地_市" type="long" length="18" display="0"/>
	<item id="CSD_X" alias="出生地_县" type="long" length="18" display="0"/>
	<item id="JGDM_SQS" alias="籍贯代码_省区市" type="long" length="18" display="0"/>
	<item id="JGDM_S" alias="籍贯代码_市" type="long" length="18" display="0"/>
	<item id="XZZ_SQS" alias="现住址_省区市" type="long" length="18" display="0"/>
	<item id="XZZ_S" alias="现住址_市" type="long" length="18" display="0"/>
	<item id="XZZ_X" alias="现住址_县" type="long" length="18" display="0"/>
	<item id="XZZ_YB" alias="现住址_邮编" length="20" type="string" display="0"/>
	<item id="XZZ_DH" alias="现住址_电话" length="20" type="string" display="0"/>
	<item id="HKDZ_SQS" alias="户口地址_省区市" type="long" length="18" display="0"/>
	<item id="HKDZ_S" alias="户口地址_市" type="long" length="18" display="0"/>
	<item id="HKDZ_X" alias="户口地址_县" type="long" length="18" display="0"/>
	<item id="XZZ_QTDZ" alias="现住址_其他地址" length="30" type="string" display="0"/>
	<item id="HKDZ_QTDZ" alias="户口地址_其他地址" length="30" type="string" display="0"/>
	
	<item id="BQDM" alias="病区" type="long" length="18" >
		<dic id="phis.dictionary.department_bq" autoLoad="true"></dic>
	</item>
	<item id="BRCH" alias="病人床号" length="12" type="string"/>
	<item id="YZ" alias="医嘱" type="string" length="500" colspan="4" xtype="textarea" width="200" />
</entry>
