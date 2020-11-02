<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.qcm.schemas.QCM_HypertensionQualityScore" alias="高血压质控评分表">
	<item id="QSID" alias="主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="qualityId" alias="质控主表ID" type="string" length="16" hidden="true"/>
	<item id="empiId" alias="empiId" type="string" length="32" hidden="true"/>
	<item id="visitId" alias="原随访ID" type="string" length="16" hidden="true"/>
	<item id="qualityVisitId" alias="质控随访ID" type="string" length="16" hidden="true"/>
	<item id="itemId" alias="字段ID" type="string" length="50" hidden="true"/>
	<item id="itemAlias" alias="字段名称" type="string" length="100" width="100" align="center"/>
	<item id="visitValue" alias="原随访值" type="string" length="100" width="200" align="center"/>
	<item id="qualityValue" alias="质控值" type="string" length="100" width="200" align="center"/>
	<item id="score" alias="得分" type="int" length="3" align="center"/>
</entry>
