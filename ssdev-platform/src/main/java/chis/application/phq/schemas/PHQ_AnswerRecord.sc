<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.phq.schemas.PHQ_AnswerRecord" alias="肿瘤健康问卷答案记录">
  <item id="answerRecordId" alias="记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
  		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" endPos="9123372036854775807"/>
		</key>
  </item>
  <item id="empiId" alias="empiId" type="string" length="32"/>
  <item id="gcId" alias="一般情况ID" type="string" length="16"/>
  <item id="masterplateId" alias="问卷数模板编号" type="string" length="16"/>
  <item id="fieldId" alias="字段编号" type="string" length="16"/>
  <item id="fieldName" alias="字段编号" type="string" length="30"/>
  <item id="fieldValue" alias="字段值" type="string" length="200"/>
</entry>
