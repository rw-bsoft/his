<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PCIS_CFDP02" alias="抽样02_报表">
	<item id="DPXH" alias="识别序号" length="18" type="long"  generator="assigned" pkey="true" not-null="1" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	 <item id="HJ" alias="" type="string" width="50"/>
  <item id="KSDM" alias="处方科室" length="18" type="long"  >
  	<dic id="phis.dictionary.department" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
  </item>
  <item id="DPSL" alias="点评处方数" length="2" type="int" width="100" renderer="onRenderer_dps"/>
  <item id="BHLSL" alias="不合理数量" length="2" type="int" width="100" renderer="onRenderer_bhl"/>
  <item id="BHLBL" alias="不合理处方比例" type="double" width="120"/>
</entry>
