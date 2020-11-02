<?xml version="1.0" encoding="UTF-8"?>


<entry entityName="chis.application.cvd.schemas.CVD_Category" alias="生活方式干预建议">
	<item id="categoryCode" pkey="true" alias="种类代码" type="string"
		not-null="1" generator="assigned" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="categoryName" alias="种类名称" type="string" length="50"
		not-null="1" width="280" />
</entry>
