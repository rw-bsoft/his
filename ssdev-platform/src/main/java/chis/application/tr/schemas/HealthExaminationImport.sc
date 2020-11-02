<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.HealthExaminationImport" alias="体检结果引入">
	<item id="startDate" alias="起始日期" type="date" not-null="1" />
	<item id="endDate" alias="截至日期" type="date" not-null="1" defaultValue="%server.date.date" maxValue="%server.date.date"/>
</entry>