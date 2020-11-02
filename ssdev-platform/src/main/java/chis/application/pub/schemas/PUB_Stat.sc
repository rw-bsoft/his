<?xml version="1.0" encoding="UTF-8"?>

<entry alias="辖区统计" entityName="chis.application.pub.schemas.PUB_Stat" >
	<item id="statId" type="string" length="16" pkey="true" not-null="1" fixed="true"
		>
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string"  >
		<dic id="chis.@manageUnit"/>
	</item>
	<item id="healthRecord" alias="健康档案" type="int"   />
	<item id="familyRecord" alias="家庭档案" type="int"   />
	<item id="oldPeopleRecord" alias="老年人档案" type="int"   />
	<item id="childRecord" alias="儿童档案" type="int"   />
	<item id="hypertensionRecord" alias="高血压档案" type="int"   />
	<item id="pregnantRecord" alias="孕产妇档案" type="int"   />
	<item id="diabetesRecord" alias="糖尿病档案" type="int"   />
	<item id="psychosisRecord" alias="精神病档案" type="int"   />
	<item id="computeDate" alias="统计时间" type="string"   />
	<item id="inputDate" alias="入库时间" type="timestamp"   >
		 <set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
