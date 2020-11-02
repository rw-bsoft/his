<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_MZ_PSJG" tableName="YS_MZ_PSJL" alias="皮试记录表"
	sort="SQSJ desc">
	<item id="YPMC" alias="药品名称" virtual="true" fixed="true" type="string"
		length="20" width="100" />
	<item id="BRXM" alias="病人姓名" virtual="true" fixed="true" type="string"
		length="20" width="100" />
	<item id="PSJG" alias="皮试结果" virtual="true" not-null="true" type="int"
		length="20" width="100">
		<dic id="phis.dictionary.skintestResult" defaultIndex="0"
			autoLoad="true" />
	</item>
	<item id="GMPH" alias="过敏批号" virtual="true" type="string" length="40"
		width="100" />
</entry>