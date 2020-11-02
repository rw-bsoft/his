<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_ZXBR" alias="诊疗医嘱执行-病人列表">
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"
		display="0" generator="assigned" pkey="true" />
	<item id="ZYHM" alias="家床号" length="10" not-null="1"  />
	<item id="BRXM" alias="姓名" length="40" not-null="1" />
	<item id="BRXZ" alias="性质" type="long" length="18" not-null="1" display="0">
		<dic id="phis.dictionary.patientProperties" />
	</item>
	<item id="FKFS" alias="缴款方式" type="int" length="6" not-null="1" display="0">
		<dic id="phis.dictionary.payment"  filter="['and',['eq',['$','item.properties.SYLX'],['s',2]],['eq',['$','item.properties.ZFBZ'],['s',0]],['eq',['$','item.properties.HBWC'],['s',0]]]" autoLoad="false"/>
	</item> 
</entry>
