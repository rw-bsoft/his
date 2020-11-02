<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY_ZT" alias="病人入院" tableName="ZY_BRRY">
  <item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="BRID" alias="病人ID" type="long" length="18" not-null="1"/>
  <item id="ZYHM" alias="住院号码" length="10" not-null="1"/>
  <item id="BAHM" alias="病案号码" length="10"/>
  <item id="MZHM" alias="门诊号码" length="32"/>
  <item id="BRXZ" alias="病人性质" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
  <item id="GFZH" alias="公费证号" length="20"/>
  <item id="BRXM" alias="病人姓名" length="20" not-null="1"/>
  <item id="BRXB" alias="病人性别" type="int" length="4" not-null="1" queryable="true">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
  </item>
  <item id="CSNY" alias="出生年月" type="date"/>
  <item id="SFZH" alias="身份证号" length="20"/>
  <item id="HYZK" alias="婚姻状况" type="int" length="4"/>
  <item id="ZYDM" alias="职业代码" type="int" length="4"/>
  <item id="SFDM" alias="省份代码" type="int" length="4"/>
  <item id="JGDM" alias="籍贯代码" type="int" length="4"/>
  <item id="MZDM" alias="民族代码" type="int" length="4"/>
  <item id="GJDM" alias="国籍代码" type="int" length="4"/>
  <item id="DWBH" alias="单位编号" type="int" length="6"/>
  <item id="GZDW" alias="工作单位" length="40"/>
  <item id="DWDH" alias="单位电话" length="16"/>
  <item id="DWYB" alias="单位邮编" length="6"/>
  <item id="HKDZ" alias="户口地址" length="40"/>
  <item id="HKYB" alias="户口邮编" length="6"/>
  <item id="LXRM" alias="联系人名" length="10"/>
  <item id="LXGX" alias="联系关系" type="int" length="4"/>
  <item id="LXDZ" alias="联系地址" length="40"/>
  <item id="LXDH" alias="联系电话" length="16"/>
  <item id="PZHM" alias="凭证号码" length="10"/>
  <item id="SBHM" alias="社保号码" length="20"/>
  <item id="DBRM" alias="担保人名" length="10"/>
  <item id="DBGX" alias="担保关系" type="int" length="4"/>
  <item id="ZZTX" alias="在职退休" type="int" length="4"/>
  <item id="DBBZ" alias="大保病种" type="int" length="6"/>
  <item id="BRKS" alias="病人科室" type="long" length="18" >
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" />
	</item>
  <item id="BRBQ" alias="病人病区" type="long" length="18"/>
  <item id="BRCH" alias="病人床号" length="12"/>
  <item id="DJRQ" alias="登记日期" type="date" not-null="1"/>
  <item id="RYRQ" alias="入院日期" type="date" not-null="1"/>
  <item id="CYRQ" alias="出院日期" type="date"/>
  <item id="CYPB" alias="出院判别" type="int" length="2" not-null="1"/>
  <item id="CYFS" alias="出院方式" type="int" length="1"/>
  <item id="CZGH" alias="操作工号" length="10"/>
  <item id="RYQK" alias="入院情况" type="int" length="4"/>
  <item id="BRQK" alias="病人情况" type="int" length="4"/>
  <item id="HLJB" alias="护理级别" type="int" length="4"/>
  <item id="YSDM" alias="饮食代码" type="int" length="4"/>
  <item id="BRXX" alias="病人血型" type="int" length="4"/>
  <item id="HZKS" alias="会诊科室" type="long" length="18"/>
  <item id="JCKS" alias="借床科室" type="long" length="18"/>
  <item id="MZYS" alias="门诊医师" length="10"/>
  <item id="ZYYS" alias="住院医师" length="10"/>
  <item id="ZSYS" alias="主任医师" length="10"/>
  <item id="ZZYS" alias="主治医师" length="10"/>
  <item id="QZRQ" alias="确诊日期" type="date"/>
  <item id="KSRQ" alias="开始日期" type="date"/>
  <item id="JSRQ" alias="结算日期" type="date"/>
  <item id="JSCS" alias="结算次数" type="int" length="3" not-null="1"/>
  <item id="JZRQ" alias="结帐日期" type="date"/>
  <item id="HZRQ" alias="汇总日期" type="date"/>
  <item id="XGPB" alias="修改判别" type="int" length="1" not-null="1"/>
  <item id="BAPB" alias="病案判别" type="int" length="1" not-null="1"/>
  <item id="BRGL" alias="病人关联" type="long" length="18"/>
  <item id="BRKL" alias="病人口令" length="6"/>
  <item id="BRQM" alias="病人全名" length="32"/>
  <item id="JTDH" alias="家庭电话" length="16"/>
  <item id="YBKH" alias="医保卡号" length="20"/>
  <item id="JZKH" alias="就诊卡号" length="40"/>
  <item id="SZYS" alias="收治医生" length="10"/>
  <item id="ZLXZ" alias="诊疗小组" type="int" length="4"/>
  <item id="SPJE" alias="审批金额" type="double" length="12" precision="2"/>
  <item id="SJZY" alias="数据转移" type="int" length="1"/>
  <item id="DJBZ" alias="冻结标志" type="int" length="1" not-null="1"/>
  <item id="DJID" alias="冻结ID号" type="long" length="18" not-null="1"/>
  <item id="DJJE" alias="冻结金额" type="double" length="10" precision="2" not-null="1"/>
  <item id="YSJS" alias="YSJS" type="int" length="1"/>
  <item id="ZKZT" alias="转科状态" type="int" length="1"/>
  <item id="ZDHZPB" alias="重点患者判别" type="int" length="4"/>
  <item id="BZ" alias="备注" length="250"/>
  <item id="CSD_SQS" alias="出生地_省区市" type="long" length="18"/>
  <item id="CSD_S" alias="出生地_市" type="long" length="18"/>
  <item id="CSD_X" alias="出生地_县" type="long" length="18"/>
  <item id="JGDM_SQS" alias="籍贯代码_省区市" type="long" length="18"/>
  <item id="JGDM_S" alias="籍贯代码_市" type="long" length="18"/>
  <item id="XZZ_SQS" alias="现住址_省区市" type="long" length="18"/>
  <item id="XZZ_S" alias="现住址_市" type="long" length="18"/>
  <item id="XZZ_X" alias="现住址_县" type="long" length="18"/>
  <item id="XZZ_YB" alias="现住址_邮编" length="20"/>
  <item id="XZZ_DH" alias="现住址_电话" length="20"/>
  <item id="HKDZ_SQS" alias="户口地址_省区市" type="long" length="18"/>
  <item id="HKDZ_S" alias="户口地址_市" type="long" length="18"/>
  <item id="HKDZ_X" alias="户口地址_县" type="long" length="18"/>
  <item id="XZZ_QTDZ" alias="现住址_其他地址" length="60"/>
  <item id="HKDZ_QTDZ" alias="户口地址_其他地址" length="60"/>
  <item id="DRBZ" alias="调入标志" type="int" length="1"/>
  <item id="RYNL" alias="入院年龄" type="string" length="20"/>
  <item id="ZYCS" alias="住院次数，用于病案归档" type="int" length="2"/>
  <item id="RYZD" alias="入院诊断" type="string" length="255"/>
  <item id="MQZD" alias="目前诊断" type="string" length="255"/>
  <item id="ZYZD" alias="主要诊断名称" type="string" length="255"/>
</entry>
