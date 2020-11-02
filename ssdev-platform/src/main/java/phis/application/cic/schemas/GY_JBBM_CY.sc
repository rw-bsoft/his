<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_JBBM_CY" tableName="GY_JBBM" alias="疾病编码库">
	<item id="JBXH" alias="疾病序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="22543"/>
		</key>
	</item>
	<item id="ICD9" alias="ICD编码" type="string" display="0" length="20" width="150"/>
	<item id="ICD10" alias="ICD编码" type="string" length="20" width="80" not-null="1" queryable="true"/>
	<item id="DMLB" alias="代码类别" display="0" type="int" defaultValue="10" length="2" not-null="1"/>
	<item id="JBMC" alias="疾病名称" type="string" length="255" width="180" not-null="1" queryable="true"/>
	<item id="JBLB" alias="疾病类别" display="0" type="long" length="18"/>
	<item id="PYDM" alias="拼音码"  type="string" length="8" queryable="true" selected="true" width="120"/> 	
	<item id="WBDM" alias="五笔码"  type="string" length="10" queryable="true"/>
	 <item id="JBPB" alias="疾病判别" type="string" length="500" width="250">
  		<dic id="phis.dictionary.diseaseKind" render="LovCombo" />
  	</item>
</entry>
