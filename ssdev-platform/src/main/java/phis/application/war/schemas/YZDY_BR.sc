<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YZDY_BR" alias="医嘱打印_病人列表">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="BRCH" alias="床号" type="sting" width="40"/>
	<item id="BRXM" alias="姓名" type="sting"/>
	<item id="BRXB" alias="性别" type="int" width="40" length="4">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>
	<item id="BRNL" alias="年龄" type="sting"  width="40"/>
	<item id="BQ" alias="病区" type="sting"  display="0" >
		<dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	
</entry>