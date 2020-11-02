<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.inc.schemas.INC_IncompleteRecord" alias="接诊记录">
	<item id="serialNumber" type="string" alias="编号"/>
	<item id="subjectivityData" type="string" alias="主观资料" width="250" not-null="1" xtype="textarea" colspan="3" length="1000"/>
	<item id="ImpersonalityData" type="string" alias="客观资料" width="250" not-null="1" xtype="textarea" colspan="3" length="1000"/>
	<item id="assessment" alias="评估" width="250" xtype="textarea" not-null="1" colspan="3" type="string" length="1000"/>
	<item id="disposePlan" alias="处置计划" width="250" xtype="textarea" not-null="1" colspan="3" type="string" length="1000"/>
	<item id="advices" alias="健康处方" xtype="textarea" not-null="1" colspan="3" width="300" length="1000"/>
</entry>
