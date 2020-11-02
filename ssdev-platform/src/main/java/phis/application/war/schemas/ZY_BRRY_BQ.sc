<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BRRY" alias="病人入院" sort="a.RYRQ desc,a.ZYHM">
	<item id="ZYH" alias="住院号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1"/>
	<item id="BRID" alias="病人ID" type="long" display="0" length="18" not-null="1"/>
	<item ref="b.EMPIID" alias="病人empiId" type="long" display="0" length="18" not-null="1"/>
	<item id="BAHM" alias="病案号码" length="10" display="0"/>
	<item id="MZHM" alias="门诊号码" length="32" display="0"/>
	<item id="BRCH" alias="床号" length="12" width="80"/>
	<item id="BRXM" alias="姓名" length="20" not-null="1"/>
	<item id="AGE" alias="年龄"  virtual="true"  width="50"/>
	<item id="BRXB" alias="性别" type="int" length="4" not-null="1" width="50" renderer="brxbRender">
		<dic id="phis.dictionary.gender"/>
	</item>
	<item id="HLJB" alias="护理级别" type="string" length="4">
		<dic id="phis.dictionary.careLevel" autoLoad="true"/>
	</item>
	<item id="BRQK" alias="病人情况" type="int" length="4" renderer="brqkRender">
		<dic id="phis.dictionary.patientSituation" autoLoad="true"/>
	</item>
	<item id="CSNY" alias="出身年月"  type="date" display="0"/>
	<item id="RYRQ" alias="入院日期" type="date" not-null="1" renderer="dateFormat"/>
	<item id="CYRQ" alias="出院日期" type="date" not-null="1" renderer="dateFormat"/>
	<item id="ZSYS" alias="主任医师" length="10">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="CYPB" alias="病人状态" type="int" length="2" not-null="1" renderer="cypbRender">
		<dic id="phis.dictionary.patientStatus"/>
	</item>
	<item id="ZYHM" alias="住院号码" length="10" not-null="1"/>
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY"/>
	</item>
	<item id="ZKZT" alias="转科状态" type="int" length="4" display="0"/>
	<item id="YSDM" alias="饮食代码" length="12" display="0"/>
	<item id="BRBQ" alias="病人病区" type="long" length="18" display="0" />
	<item id="BRKS" alias="病人科室" type="long" length="18" display="0" >
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE"/>
	</item>
	<item id="MQZD" alias="目前诊断" type="string" length="255" display="0" />
	<item id="JSCS" alias="结算次数" type="int" length="3" display="0" />
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BRDA" >
			<join parent="BRID" child="BRID"></join>
		</relation>
	</relations>
</entry>
