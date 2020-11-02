<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_BabyVisitInfo" alias="新生儿访视基本信息">
	<item id="babyId" alias="新生儿编号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="babyName" alias="婴儿姓名" type="string" length="30" />
	<item id="certificateNo" alias="婴儿出生证号" type="string" length="10"
		width="120" />
		 <item id="weight" alias="出生体重(kg)" type="bigDecimal" length="5"
    precision="2" width="120" hidden="true"/>
  <item id="length" alias="出生身长(cm)" type="bigDecimal" length="5"
    precision="2" width="120" hidden="true"/>
</entry>
