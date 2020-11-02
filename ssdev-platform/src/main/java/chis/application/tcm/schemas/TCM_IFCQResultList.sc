<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tcm.schemas.TCM_IFCQResultList" tableName="chis.application.tcm.schemas.TCM_IFCQResult" alias="中医体质辨识问卷结果表（主）">
	<item id="IFCQRID" alias="记录主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
  
	<item id="empiId" alias="个人主索引" type="string" length="32" display="0"/>
	
	<item id="questionnaireDate" alias="问卷日期" type="date" update="false" defaultValue="%server.date.date" maxValue="%server.date.date"/>
	<item id="guideDoctorId" alias="指导医生" type="string" length="20" update="false" defaultValue="%user.userId" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="guideDoctorUnitId" alias="指导医生机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="questionnaireType" alias="问卷分类" type="string" length="1">
		<dic>
			<item key="1" text="老年人"/>
			<item key="2" text="大众"/>
		</dic>
	</item>
	<item id="status" alias="状态" type="string" length="1" defaultValue="0" display="1">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
</entry>