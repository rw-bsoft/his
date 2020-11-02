<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="20" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="RYCS" alias="入院次数" not-null="1" length="5" virtual="true" defaultValue="1"  layout="part1"/>
	<item id="virtual2" alias="" virtual="true" xtype="panel" layout="part1"/>
	<item id="ZYHM" alias="住院号码" length="10" readOnly="true" not-null="1" layout="part1"/>
	<item id="BAHM" alias="病案号码" length="10" not-null="1" layout="part2"/>
	<item id="YBKH" alias="卡号" type="string" length="20" layout="part2"/>
	<item id="MZHM" alias="门诊号码" length="32" not-null="1"/>
	<item id="YBHM" alias="医保号码" length="32"/>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" updates="true" layout="part2">
		<dic id="phis.dictionary.patientProperties_ZY" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="GFZH" alias="病人证号" length="20"/>
	<item id="BRXM" alias="姓名" length="20" updates="true" not-null="1" layout="part3"/>
	<item id="SFZH" alias="身份证号" length="20" fixed="true" layout="part3" vtype="idCard" enableKeyEvents="true"/>
	<item id="BRXB" alias="性别" type="int" fixed="true" length="4" not-null="1" defaultValue="1" layout="part3">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="CSNY" alias="出生年月" type="date" fixed="true" not-null="1" layout="part3"/>
	<item id="RYNL" alias="入院年龄" type="string" fixed="true" not-null="1" layout="part3"/>
	<item id="GJDM" alias="国籍" length="4" defaultValue="CN" updates="true" fixed="true" layout="part3">
		<dic id="phis.dictionary.nationality" autoLoad="true"/>
	</item>
	<item id="MZDM" alias="民族" length="4" defaultValue="01" updates="true" fixed="true" layout="part3">
		<dic id="phis.dictionary.ethnic" autoLoad="true"/>
	</item>
	<item id="HYZK" alias="婚姻状况" type="int" length="4" updates="true" defaultValue="90" fixed="true" layout="part3">
		<dic id="phis.dictionary.maritals" autoLoad="true"/>
	</item>
	<item id="JTDH" alias="家庭电话" length="16" fixed="true" updates="true" layout="part3"/>
	<item id="CSD_SQS" alias="出生地_省" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.Province" autoLoad="true"/>
	</item>
	<item id="CSD_S" alias="出生地_市" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.City" autoLoad="true"/>
	</item>
	<item id="CSD_X" alias="出生地_县" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.County" autoLoad="true"/>
	</item>
	<item id="JGDM_SQS" alias="籍贯_省" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.Province" autoLoad="true"/>
	</item>
	<item id="JGDM_S" alias="籍贯_市" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.City" autoLoad="true"/>
	</item>
	<item id="ZYDM" alias="职业类别" length="4" defaultValue="Y" updates="true" fixed="true" layout="part3">
		<dic id="phis.dictionary.jobtitle" onlySelectLeaf="true" autoLoad="true"/>
	</item>
	<item id="XZZ_SQS" alias="现住址_省" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.Province" autoLoad="true"/>
	</item>
	<item id="XZZ_S" alias="现住址_市" type="long" labelWidth="30" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.City" autoLoad="true"/>
	</item>
	<item id="XZZ_X" alias="现住址_县" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.County" autoLoad="true"/>
	</item>
	<item id="XZZ_QTDZ" alias="现住址其他" length="60" fixed="true" updates="true" layout="part3"/>
	<item id="XZZ_DH" alias="现住址电话" length="16" fixed="true" updates="true" layout="part3"/>
	<item id="XZZ_YB" alias="现住址邮编" length="6" fixed="true" updates="true" layout="part3"/>
	<item id="HKDZ_SQS" alias="户口_省" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.Province" autoLoad="true"/>
	</item>
	<item id="HKDZ_S" alias="户口_市" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.City" autoLoad="true"/>
	</item>
	<item id="HKDZ_X" alias="户口_县" type="long" length="18" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.County" autoLoad="true"/>
	</item>
	<item id="HKDZ_QTDZ" alias="户口其他" length="60" fixed="true" updates="true" layout="part3"/>
	<item id="HKYB" alias="邮政编码" length="6" fixed="true" updates="true" layout="part3"/>
	<item id="GZDW" alias="工作单位" length="40" fixed="true" updates="true" layout="part3"/>
	<item id="DWDH" alias="单位电话" length="16" fixed="true" updates="true" layout="part3"/>
	<item id="DWYB" alias="单位邮编" length="6" fixed="true" updates="true" layout="part3"/>
	<item id="LXRM" alias="联系人" length="20" fixed="true" updates="true" layout="part3"/>
	<item id="LXGX" alias="联系关系" type="int" length="4" fixed="true" updates="true" layout="part3">
		<dic id="phis.dictionary.GB_T4761" autoLoad="true"/>
	</item>
	<item id="LXDH" alias="联系电话" length="16" fixed="true" updates="true" layout="part3"/>
	<item id="LXDZ" alias="联系地址" length="40" fixed="true" updates="true" layout="part3"/>
	<item id="BRKS" alias="病人科室" type="long" length="18" updates="true" not-null="1" layout="part7">
		<dic id="phis.dictionary.department_zy" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="BRCH" alias="病人床号" length="12" updates="true" layout="part7"/>
	<item id="RYRQ" alias="入院日期" type="datetime" updates="true" not-null="1" layout="part7" defaultValue="%server.date.datetime"/>
	<item id="RYQK" alias="入院情况" type="int" updates="true" length="4" layout="part7">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="SZYS" alias="收治医生" length="10" updates="true" layout="part7" not-null="1">
		<dic id="phis.dictionary.doctor_cfqx" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="ZSYS" alias="主任医生" length="10" updates="true" layout="part7">
		<dic id="phis.dictionary.doctor_cfqx" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YBMC" alias="医保病种" type="string" updates="true" defaultValue="20" layout="part7" >
		<dic id="phis.dictionary.ybJbbm" searchField="PYDM" autoLoad="true" />
	</item>
	<item id="ZSESHBZKH" alias="准生儿社会保障卡号" length="18" updates="true" layout="part7" />
	<!--
	<item id="RYZD" alias="入院诊断" type="string" not-null="1" layout="part7" virtual="true" mode="remote"/>
	-->
	<item ref="b.JKJE" type="double" emptyText="" length="10" precision="2" not-null="0" layout="part8"/>
	<item ref="b.JKFS" type="int" length="6" not-null="0" layout="part8"/>
	<item ref="b.ZPHM" length="20" not-null="0" layout="part8"/>
	<item ref="b.SJHM" length="20" not-null="0" layout="part8"/>
	<item ref="b.ewm" layout="part8"/>
	<item id="DWBH" alias="单位编号" type="int" length="6"/>
	<item id="PZHM" alias="凭证号码" length="10"/>
	<item id="SBHM" alias="社保号码" length="20"/>
	<item id="ZZTX" alias="在职退休" type="int"/>
	<item id="DBRM" alias="担保人" length="10"/>
	<item id="DBBZ" alias="保险病种" colspan="3" type="int" length="6"/>
	<item id="DBGX" alias="担保关系" type="int" length="4"/>
	<item id="ZLXZ" alias="诊疗小组" colspan="2" type="int" length="4"/>
	<item id="DJRQ" alias="登记日期" type="timestamp" display="0"/>
	<item id="KSRQ" alias="开始日期" type="timestamp" display="0"/>
	<item id="CYPB" alias="出院判别" type="int" display="0" length="2" not-null="1" defaultValue="0"/>
	<item id="CZGH" alias="操作工号" length="10" display="0" defaultValue="%user.userId"/>
	<item id="JSCS" alias="结算次数" type="int" display="0" length="3" not-null="1" defaultValue="0"/>
	<item id="XGPB" alias="修改判别" type="int" display="0" length="1" not-null="1" defaultValue="0"/>
	<item id="BAPB" alias="病案判别" type="int" display="0" length="1" not-null="1" defaultValue="0"/>
	<item id="DJBZ" alias="冻结标志" type="int" display="0" length="1" not-null="1" defaultValue="0"/>
	<item id="DJID" alias="冻结ID号" type="long" display="0" length="18" not-null="1" defaultValue="0"/>
	<item id="DJJE" alias="冻结金额" type="double" display="0" length="10" precision="2" not-null="1" defaultValue="0"/>
	<!-- <item id="SRKH" alias="输入的卡号(杭州市医保用)"   length="40"  display="0"/> -->
	<item id="ZYLSH" alias="住院流水号(杭州市医保用)"   length="18"  display="0"/>
	<!--<item id="GRBH" alias="个人编号(杭州市医保用)" length="20" display="0"/>
	
	<item id="BARGAININGID" alias="省医保交易流水号" type="long" length="18" display="0"/>
	<item id="ICMW" alias="省医保IC卡明文"  type="string"  length="500"  display="0"/>-->
	<item id="NJJBLSH" alias="南京金保流水号"   length="20"  display="0"/>
	<item id="NJJBYLLB" alias="南京金保医疗类别"   length="20"  display="0"/>
	<item id="YBZY" alias="医保转院"   length="20"  display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.hos.schemas.ZY_TBKK_JKFORM" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
