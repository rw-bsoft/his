<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_JBBM_MS" tableName="GY_JBBM" alias="疾病编码库" sort=" ICD10 asc">
	<item id="JBXH" alias="疾病序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="22543"/>
		</key>
	</item>
	<item id="JBMC" alias="诊断名称" type="string" length="255" queryable="true" width="150" not-null="1"/>
	<item id="ICD10" alias="诊断编码" type="string" length="20" queryable="true" not-null="1"/>
	<item id="PYDM" alias="拼音码"  type="string" length="8" display="0" queryable="true" selected="true" />
	<item id="WBDM" alias="五笔码"  type="string" display="0" length="10"/>
	<item id="JBPB" alias="疾病判别" type="string" length="500" width="250">
		<dic id="phis.dictionary.diseaseKind" render="LovCombo" />
	</item>
	<item id="JBBGK" alias="疾病报告卡" type="string" display="0">
		<dic id="phis.dictionary.diseaseReportType"/>
	</item>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.GY_JBBM" >
			<join parent="ICD10" child="ICD10" />
		</relation>
	</relations>
</entry>
