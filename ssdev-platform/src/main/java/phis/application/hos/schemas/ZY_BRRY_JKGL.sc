<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" defaultValue="%user.manageUnit.id"/>
	<item id="BRID" alias="病人ID" type="long" length="18" not-null="1" display="0"/>
	<item id="ZYHM" alias="住院号码" length="10" readOnly="true" />
	<item id="BRCH" alias="床号" length="12" />
	<item id="BAHM" alias="病案号码" length="10" display="0"/>
	<item id="YBKH" alias="卡号" length="20" display="0"/>
	<item id="MZHM" alias="门诊号码" length="32" display="2"/>
	<item id="GFZH" alias="公费证号" length="20" display="2"/>
	<item id="BRXM" alias="姓名" length="20" not-null="1"  selected="true"/>
	<item id="SFZH" alias="身份证号" length="20" display="2"/>
	<item id="BRXB" alias="性别" type="int" length="4" defaultValue="1">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="CSNY" alias="出生年月" type="date" display="2"/>
	<item id="RYNL" alias="入院年龄" virtual="true" display="2"/>
	<item id="GJDM" alias="国籍" length="4" defaultValue="CN" display="2">
		<dic id="phis.dictionary.nationality" autoLoad="true"/>
	</item>
	<item id="JTDH" alias="家庭电话" length="16" display="2"/>
	<item id="HKYB" alias="邮政编码" length="6" display="2"/>
	<item id="GZDW" alias="工作单位" length="40" display="2"/>
	<item id="DWDH" alias="单位电话" length="16" display="2"/>
	<item id="DWYB" alias="单位邮编" length="6" display="2"/>
	<item id="LXRM" alias="联系人" length="20" display="2"/>
	<item id="LXGX" alias="联系关系" type="int" length="4" display="2">
		<dic id="phis.dictionary.GB_T4761" autoLoad="true"/>
	</item>
	<item id="LXDH" alias="联系电话" length="16" display="2"/>
	<item id="LXDZ" alias="联系地址" length="40" display="2"/>
	
	<item id="DWBH" alias="单位编号" type="int" length="6" display="2"/>
	<item id="PZHM" alias="凭证号码" length="10" display="2"/>
	<item id="SBHM" alias="社保号码" length="20" display="2"/>
	<item id="ZZTX" alias="在职退休" type="int" length="4" display="2"/>
	<item id="DBRM" alias="担保人名" length="10" display="2"/>
	<item id="DBBZ" alias="大保病种" colspan="3" type="int" length="6" display="2"/>
	<item id="DBGX" alias="担保关系" type="int" length="4" display="2"/>
	<item id="BRBQ" alias="病区" type="long" length="18" display="1" >
		<dic id="phis.dictionary.department_bq" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="BRKS" alias="科室" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department_zy" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院日期" type="datetime" not-null="1"/>
	<item id="RYQK" alias="入院情况" type="int" length="4" display="2">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="SZYS" alias="收治医生" length="10" display="2">
		<dic id="phis.dictionary.doctor_cfqx" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="ZSYS" alias="主任医师" length="10" display="2">
		<dic id="phis.dictionary.doctor_cfqx" autoLoad="true" searchField="PYCODE" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	
	
 
</entry>
