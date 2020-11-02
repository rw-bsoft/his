<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_LYFS" alias="领药方式">
	<item id="JGID" alias="机构ID" length="20" not-null="1" defaultValue="%user.manageUnit.id"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" generator="assigned" pkey="true" type="long"/>
	<item id="YKSB" alias="药库识别" length="18" not-null="1" pkey="true" type="long"/>
	<item id="LYFS" alias="领药方式" length="4" not-null="1" type="int">
		<dic id="storehouseDelivery" autoLoad="true"/>
	</item>
</entry>
