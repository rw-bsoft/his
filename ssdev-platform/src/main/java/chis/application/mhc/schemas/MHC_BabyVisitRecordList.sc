<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_BabyVisitRecord" alias="新生儿访视记录">
	 <item id="visitId" alias="随访序号" type="string" length="16"
    not-null="1" generator="assigned" pkey="true" hidden="true">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="babyId" alias="新生儿编号" type="string" length="16"
    hidden="true" />
	<item id="visitDate" alias="访视日期" type="date" update="false"
		fixed="true" defaultValue="%server.date.today"  >
	</item>
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId"  >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id"   />
	</item>
	
</entry>