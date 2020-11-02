<?xml version="1.0" encoding="UTF-8"?>

<entry  alias="高血压随访质控" entityName="chis.application.quality.schemas.QUALITY_ZK_SJ"  >
	<item id="CODERNO" alias="唯一id" type="string" display="0"
		length="16" not-null="1" pkey="true"  >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	 <item id="hypertensionGroup" alias="组别" type="string" length="2"
		fixed="true" >
		<dic id="chis.dictionary.QualityControl_ZKZB" />
	</item>
	<item id="personName"   alias="姓名" fixed="true"/>
	<item id="sexCode" alias="性别" type="string" length="1" width="40"   fixed="true">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生年月"   fixed="true"/>
	 <item id="ZDF" alias="得分"   fixed="true"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		display="0" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id"  />
	</item>
</entry>
