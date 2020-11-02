<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tcm.schemas.TCM_IFCQResultDetail" alias="中医体质辨识问卷结果明细">
	<item id="recordId" alias="记录主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="IFCQRID" alias="问卷主表ID" type="string" length="16" hidden="true"/>
	<item id="questionCode" alias="问卷题目编码" type="string" length="30" hidden="true"/>
	<item id="questionName" alias="问卷题目名称" type="string" length="200"/>
	<item id="questionAnswer" alias="问卷结果" type="string" length="1">
		<dic id="chis.dictionary.TCMQuestionnaireOption"/>
	</item>
</entry>
