<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YB_MEDICINE_LIST" alias="YB_MEDICINE_LIST">
	<item id="YPXH" alias="药品内码" type="long" length="18" not-null="1" display="0">
	</item>
	<item id="YBBM" alias="医保编码" type="string" length="20" width="160"/>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="YPGG" alias="规格" type="string" length="20"  layout="JBXX"/>
	<item id="YPSX" alias="剂型" type="long"  not-null="1" 
		length="18" layout="JBXX">
		<dic id="phis.dictionary.dosageForm" defaultIndex="0"/>
	</item>
	<item id="CDMC" alias="产地简称" type="string"  width="100"  length="7" not-null="true" />
	<item id="PYDM" alias="拼音码" type="string" length="10" not-null="1" selected="true" target="YPMC" codeType="py"
		queryable="true" layout="JBXX">
	</item>
	<item id="YPCD" alias="药品产地" type="long" length="18" fixed="true"/>
</entry>
