<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.fhr.schemas.EHR_SelectModule" alias="字段数据表（子表）">
	<item id="modId" alias="选择模板" length="100">
		<dic id="chis.dictionary.selectModule"
			filter="['and',['like',['$','item.properties.inputUnit'],['concat',['substring',['$','%user.manageUnit.id'],0,9]],['s','%']],['eq',['$','item.properties.whmb'],['s','01']]]"/>
	</item>
</entry>
