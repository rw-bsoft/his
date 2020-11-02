<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BASY" alias="住院病案首页">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JZXH" alias="就诊序号" type="long" length="18" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="YLJGMC" alias="医疗机构名称" type="string" length="25" not-null="1"/>
	<item id="YLJGDM" alias="医疗机构组织机构代码" type="string" length="10" not-null="1"/>
	<item id="YLFYDM" alias="医疗费用支付方式代码" type="int" length="4" layout="JBXX" not-null="1">
		<dic>
			<item key="1" text="城镇职工基本医疗保险"/>
			<item key="2" text="城镇居民基本医疗保险"/>
			<item key="3" text="新型农村合作医疗"/>
			<item key="4" text="贫困救助"/>
			<item key="5" text="商业医疗保险"/>
			<item key="6" text="全公费"/>
			<item key="7" text="全自费"/>
			<item key="8" text="其他社会保险"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="ZYCS" alias="住院次数" type="int" length="2" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="JMJKKH" alias="居民健康卡号" type="string" length="32" layout="JBXX" not-null="1"/>
	<item id="YBKH" alias="医保卡号" type="string" length="32" not-null="1"/>
	<item id="MZHM" alias="门（急）诊号" type="string" length="32" not-null="1"/>
	<item id="ZYHN" alias="住院号" type="string" length="10" not-null="1"/>
	<item id="BAHM" alias="病案号" type="string" length="18" updated="true" layout="JBXX" not-null="1"/>
	<item id="BRXM" alias="患者姓名" type="string" length="50" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="BRXB" alias="性别" type="int" length="4" autoUpdate="true" layout="JBXX_radio" not-null="1"/>
	<item id="CSNY" alias="出生日期" type="date" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="BRNL" alias="年龄" type="string" length="20" layout="JBXX" not-null="1"/>
	<item id="GJDM" alias="国籍" type="string" length="4" defaultValue="CN" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.nationality"/>
	</item>
	<item id="YL" alias="月龄" type="string" length="8" layout="JBXX" not-null="1"/>
	<item id="XSECSTZ" alias="新生儿出生体重" type="string" length="5" layout="JBXX" not-null="1"/>
	<item id="XSERYTZ" alias="新生儿入院体重" type="string" length="5" layout="JBXX" not-null="1"/>
	<item id="CSD_SQS" alias="出生地-省（区、市）" type="int" length="18" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.Province"/>
	</item>
	<item id="CSD_S" alias="出生地-市" type="int" length="18" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.City"/>
	</item>
	<item id="CSD_X" alias="出生地-县" type="int" length="18" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.County"/>
	</item>
	<item id="JGDM_SQS" alias="籍贯-省（区、市）" type="int" length="18" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.Province"/>
	</item>
	<item id="JGDM_S" alias="籍贯-市" type="int" length="18" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.City"/>
	</item>
	<item id="XZZ_SQS" alias="现住址-省（区、市）" type="int" length="18" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.Province"/>
	</item>
	<item id="XZZ_S" alias="现住址-市" type="int" length="18" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.City"/>
	</item>
	<item id="XZZ_X" alias="现住址-县" type="int" length="18" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.County"/>
	</item>
	<item id="XZZ_DZ" alias="现住址-地址" type="string" length="30" layout="JBXX" not-null="1"/>
	<item id="XZZ_DH" alias="现住址-电话" type="string" length="20" layout="JBXX" not-null="1"/>
	<item id="XZZ_YB" alias="现住址-邮编" type="string" length="20" layout="JBXX" not-null="1"/>
	<item id="HKDZ_SQS" alias="户口地址-省（区、市）" type="int" length="18" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.Province"/>
	</item>
	<item id="HKDZ_S" alias="户口地址-市" type="int" length="18" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.City"/>
	</item>
	<item id="HKDZ_X" alias="户口地址-县" type="int" length="18" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.County"/>
	</item>
	<item id="HKDZ_DZ" alias="户口地址-地址" type="string" length="30" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="HKDZ_YB" alias="户口地址-邮编" type="string" length="20" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="DWDZ" alias="工作单位及地址" type="string" length="30" layout="JBXX" not-null="1"/>
	<item id="DWDH" alias="单位电话" type="string" length="20" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="DWYB" alias="单位邮编" type="string" length="20" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="LXRXM" alias="联系人姓名" type="string" length="10" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="LXRGX" alias="联系人关系" type="int" length="4" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.GB_T4761"/>
	</item>
	<item id="LXRDZ" alias="联系人地址" type="string" length="50" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="LXRDH" alias="联系人电话" type="string" length="16" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="MZDM" alias="民族" type="string" length="4" defaultValue="01" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.ethnic"/>
	</item>
	<item id="SFZJLB" alias="患者身份证件类别代码" type="int" length="4"  autoUpdate="true"   layout="JBXX_radio" not-null="1"/><!--defaultValue="0"  --> 
	<item id="SFZJHM" alias="患者身份证件号码" type="string" length="18" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="ZYDM" alias="职业类别代码" type="string" length="4" autoUpdate="true" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.jobtitle"/>
	</item>
	<item id="HYDM" alias="婚姻状况代码" type="int" length="4" layout="JBXX_radio" not-null="1"/>
	<item id="RYTJ" alias="入院途径" type="int" length="4" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.AdmissionRoute"/>
	</item>
	<item id="RYRQ" alias="入院日期时间" type="timestamp" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="RYKS" alias="入院科室" type="int" length="8" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.department_zy"/>
	</item>
	<item id="RYBF" alias="入院病房" type="int" length="8" layout="JBXX" not-null="1">
		<dic id="phis.dictionary.department_bq"/>
	</item>
	<item id="ZKKSMC" alias="转科科室名称" type="string" length="50" autoUpdate="true" layout="JBXX" not-null="1"/>
	<item id="CYRQ" alias="出院日期时间" type="timestamp" autoUpdate="true" layout="JBXX"/>
	<item id="CYKS" alias="出院科室" type="int" length="8" autoUpdate="true" layout="JBXX">
		<dic id="phis.dictionary.department_zy"/>
	</item>
	<item id="CYBQ" alias="出院病房" type="int" length="8" autoUpdate="true" layout="JBXX">
		<dic id="phis.dictionary.department_bq"/>
	</item>
	<item id="SJZYYS" alias="实际住院天数" type="int" length="4" updated="true" layout="JBXX"/>
	<item id="BLH" alias="病理号" type="string" length="18" layout="YSQM" not-null="1"/>
	<item id="GMYWBZ" alias="过敏药物标志" type="string" length="1" layout="YSQM_radio" not-null="1"/>
	<item id="GMYWMC" alias="过敏药物" type="string" length="50" layout="YSQM" not-null="1"/>
	<item id="SJBZ" alias="死亡患者尸检标志" type="string" length="1" layout="YSQM_radio" not-null="1"/>
	<item id="ABOXXDM" alias="ABO血型代码" type="int" length="4" layout="YSQM_radio" not-null="1"/>
	<item id="RHXXDM" alias="Rh（D）血型代码" type="int" length="4" layout="YSQM_radio" not-null="1"/>
	<item id="KZRQM" alias="科主任签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="ZRYSQM" alias="主任（副主任）医师签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="ZZYSQM" alias="主治医师签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="ZYYSQM" alias="住院医师签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="ZRHSQM" alias="责任护士签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="JXYSQM" alias="进修医师签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="SXYSQM" alias="实习医师签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="BABMYQM" alias="病案编码员签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="BAZL" alias="病案质量" type="int" length="4" layout="YSQM_radio"/>
	<item id="ZKYSQM" alias="质控医师签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="ZKHSQM" alias="质控护士签名" type="string" length="10" layout="YSQM">
		<dic id="phis.dictionary.user06" filter="['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="ZKRQ" alias="质控日期" type="date" layout="YSQM"/>
	<item id="LYFS" alias="离院方式" type="int" length="4" layout="YSQM" not-null="1">
		<dic>
			<item key="1" text="医嘱离院"/>
			<item key="2" text="医嘱转院"/>
			<item key="3" text="医嘱转社区卫生服务机构/乡镇卫生院"/>
			<item key="4" text="非医嘱离院"/>
			<item key="5" text="死亡"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="NJSYLJLMC" alias="拟接受医疗机构名称" type="string" length="35" layout="YSQM" not-null="1"/>
	<item id="CY31ZYBZ" alias="出院 31 天内再住院标志" type="string" length="1" layout="YSQM_radio" not-null="1"/>
	<item id="CY31ZYMD" alias="出院 31 天内再住院目的" type="string" length="50" layout="YSQM" not-null="1"/>
	<item id="RYQHMSJ" alias="颅脑损伤患者入院前昏迷时间" type="long" length="10" layout="YSQM" not-null="1"/>
	<item id="RYHHMSJ" alias="颅脑损伤患者入院后昏迷时间" type="long" length="10" layout="YSQM" not-null="1"/>
	<item id="ZDFH_MZZY" alias="诊断符合情况-门诊和出院" type="string" length="1" layout="FJXM_radio" not-null="1"/>
	<item id="ZDFH_RYCY" alias="诊断符合情况-入院和出院" type="string" length="1" layout="FJXM_radio" not-null="1"/>
	<item id="ZDFH_SQSH" alias="诊断符合情况-术前和术后" type="string" length="1" layout="FJXM_radio" not-null="1"/>
	<item id="ZDFH_LCBL" alias="诊断符合情况-临床和病理" type="string" length="1" layout="FJXM_radio" not-null="1"/>
	<item id="ZDFH_FSBL" alias="诊断符合情况-放射和病理" type="string" length="1" layout="FJXM_radio" not-null="1"/>
	<item id="QJCS" alias="抢救情况-抢救次数" type="int" length="4" layout="FJXM" not-null="1"/>
	<item id="CGCS" alias="抢救情况-成功次数" type="int" length="4" layout="FJXM" not-null="1"/>
	<item id="LCLJBZ" alias="临床路径管理标识" type="string" length="1" layout="FJXM_radio"   not-null="1"/>
    <item id="ZJBXYY" alias="不详原因" type="int" length="4" layout="JBXX_radio" not-null="1"/>
        <item id="WZBL" alias="危重病例" type="int" length="4" layout="FJXM_radio" not-null="1"/>
        <item id="YNBL" alias="疑难病例" type="int" length="4" layout="FJXM_radio" not-null="1"/>
        <item id="MDTBL" alias="MDT病例" type="int" length="4" layout="FJXM_radio" not-null="1"/>
        <item id="DBZBL" alias="单病种病例" type="int" length="4" layout="FJXM_radio" not-null="1"/>
        <item id="RJSSBL" alias="日间手术病例" type="int" length="4" layout="FJXM_radio" not-null="1"/>
        <item id="JXBL" alias="教学查房病例" type="int" length="4" layout="FJXM_radio" not-null="1"/>
           <item id="SSLB" alias="手术类别" type="int" length="4" layout="FJXM_radio" not-null="1"/>
    <item id="ZZJH" alias="重症监护" type="int" length="4"  layout="JBXX"  not-null="1">    
		<dic id="phis.dictionary.zzjh"/>
	</item>
	    <item id="JRZZJHSJ" alias="进入重症监护时间" type="timestamp" autoUpdate="true" layout="JBXX" not-null="1"/>
		<item id="ZCZZJHSJ" alias="转出重症监护时间" type="timestamp" autoUpdate="true" layout="JBXX" not-null="1"/>
		<item id="XSEEHCSTZ" alias="新生儿二孩出生体重"  type="string"  length="5"  layout="JBXX"  not-null="1"/>	
<!-- 
     <item id="ZZJH" alias="重症监护"  type="int"  length="4"  layout="FJXM" not-null="1">
		<dic >
  <item key="1" text="重症医学科（ICU）" /> 
  <item key="2" text="心脏监护室（CCU）" /> 
  <item key="3" text="呼吸监控室（RICU）" /> 
  <item key="4" text="外科监护室（SICU）" /> 
  <item key="5" text="新生儿监护室（NICU）" /> 
  <item key="6" text="儿科监护室（PICU）" /> 
  <item key="7" text="急诊监护室（EICU）" /> 
  <item key="8" text="内科监护室（MICU）" />  
  <item key="9" text="心脏外科监护室（CICU）" />  
  <item key="10" text="神经外科监护室（NSICU）" />  
  <item key="11" text="其他（指未列入上述名称的监护室）" />  
  </dic>

	</item>  -->
</entry>
