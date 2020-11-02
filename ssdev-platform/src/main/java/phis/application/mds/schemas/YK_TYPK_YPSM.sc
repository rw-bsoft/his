<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TYPK" alias="药品信息" >
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品内码" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true"  layout="JBXX" >
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="8869" />
		</key>
	</item>
	<item id="MESS" alias="药品说明" type="string" length="524288000" xtype="htmleditor" colspan="3" display="2" anchor="100%"/>
</entry>
