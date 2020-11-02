<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cdh.schemas.CDH_DebilityChildrenCheck" alias="体弱儿童检查">
	<item id="checkId" alias="体弱儿童检查编号" length="16" pkey="true"
		type="string" not-null="1" fixed="true" hidden="true"
		generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="recordId" alias="儿童档案编号" length="30" type="string"
		not-null="1" fixed="true" hidden="true" />
	<item id="empiId" alias="EMPIID" length="32" hidden="true" />
	<item id="visitId" alias="随访编号" length="16" hidden="true" />
	<item id="checkCode" alias="检查编号" length="2" hidden="true" />
	<item id="checkName" alias="检查项目名称" length="100" width="200"
		fixed="true" />
	<item id="checkResult" alias="检查结果" width="100" />
	<item id="checkResultUnit" alias="单位" length="2">
		<dic id="chis.dictionary.debilityCheckResultUnit" />
	</item>
</entry>
