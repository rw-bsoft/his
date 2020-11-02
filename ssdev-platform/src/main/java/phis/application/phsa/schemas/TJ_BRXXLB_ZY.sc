<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="TJ_BRXXLB_ZY" alias="病人信息" sort="a.ZYHM">
	<item id="EMPIID" alias="EMPIID" type="string" length="32" display="0"/>
	<item id="BRID" alias="病人ID" type="long" length="18" display="0"/>
	<item id="ZYH" alias="住院号" type="long" length="18" display="0"/>
	<item id="BRBQ" alias="病人病区" type="long" length="18" display="0"/>
	<item id="ZYHM" alias="住院号码" length="10" width="100"/>
	<item id="BAHM" alias="病案号码" length="10" width="100"/>
	<item id="BRXZ" alias="病人性质" virtual="true" length="18" width="100">
		<dic id="phis.dictionary.patientProperties" autoLoad="true"
			searchField="PYDM" />
	</item>
	<item id="BRXM" alias="病人姓名" type="string" length="20" width="100"/>
	<item id="BRXB" alias="病人性别" type="string" length="1" width="100">
		<dic id="phis.dictionary.gender" autoLoad="true" />
	</item>
	<item id="CSNY" alias="出生日期" type="date" width="100"  />
	<item id="BRKS" alias="病人科室" type="long" length="18" width="100">
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE"/>
	</item>
	<item id="BRCH" alias="病人床号" length="12" width="100"/>
	<item id="RYRQ" alias="入院时间" type="date" width="100"/>
	<item id="CYRQ" alias="出院时间" type="date" width="100"/>
	<item id="CYPB" alias="备注" type="int" width="100">
		<dic id="phis.dictionary.CYPBDic"/>
	</item>
	
</entry>