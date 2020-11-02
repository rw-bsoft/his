<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YJ_ZYFORM">
	<item id="ZYHM" alias="住院号码" type="string" />
	<item id="ZYH" alias="住院号" display = "0" />
	<item id="BRXM" alias="病人姓名" fixed="true"/>
	<item id="BRXZ" alias="病人性质" fixed="true" >
		<dic id="phis.dictionary.patientProperties_GY" autoLoad="true"/>
	</item>
	<item id="BRCH" alias="病人床号" length="12" fixed="true"/>
	<item id="BRBQ" alias="病人病区" type="long" fixed="true" length="18"  >
		<dic id="phis.dictionary.department" autoLoad ="true"/>
	</item>
	<item  id="YSDM" alias="申检医生">
		<dic id="phis.dictionary.user_YJSJYS" autoLoad="true"/>
	</item>
	<item id="KSDM" alias="申检科室"  >
		<dic id="phis.dictionary.department_zyyj"  autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE"/>
	</item>
	<item id="ZXRQ" alias="检查日期" type="date"  defaultValue="%server.date.date"/>
	<item id="ZXYS" alias="检查医生">
		<dic id="phis.dictionary.user_YJJCYS" autoLoad="true"/>
	</item>
   
</entry>
