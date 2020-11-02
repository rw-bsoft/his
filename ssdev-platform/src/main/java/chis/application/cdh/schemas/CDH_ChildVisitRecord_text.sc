<?xml version="1.0" encoding="UTF-8"?>              
<entry entityName="chis.application.cdh.schemas.CDH_ChildVisitRecord" alias="新生儿家庭访视记录">
	<item id="visitId" alias="随访序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="visitDate" alias="随访日期" type="date" tag="text" > </item>
	<item id="visitDoctor" alias="随访医生" type="string" length="20" tag="text"  >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	
</entry>