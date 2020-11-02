<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pd.schemas.CDH_DebilityCorrectionDic" alias="体弱儿指导意见">
	<item id="recordId" alias="记录序号" type="string" length="16"
		not-null="1" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="4"/>
		</key>
	</item>
	<item id="diseaseType" alias="疾病类型" type="string" length="2" not-null="1" colspan="2">
		<dic id="chis.dictionary.debilityDiseaseType"/>
	</item>
	<item id="suggestion" alias="指导意见" type="string" length="524288000" not-null="1" width="600" xtype="textarea" colspan = "2"/>
</entry>
