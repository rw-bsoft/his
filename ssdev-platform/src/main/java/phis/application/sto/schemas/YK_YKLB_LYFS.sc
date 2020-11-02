<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YKLB"   alias="领药方式">
	<item id="JGID" alias="机构ID" length="20" not-null="1" defaultValue="%user.manageUnit.id" display="0"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0"/>
	<item id="YKSB" alias="药库名称" length="18" not-null="1" pkey="true" type="long" display="0"/>
	<item id="YKMC" alias="药库名称" length="30"/>
	<item id="JGMC" alias="机构名称"  virtual="true" width="180"/>
	<item id="LYFS" alias="领用方式" length="18" not-null="1" type="long">
		<dic id="phis.dictionary.storehouseDelivery" autoLoad="true"/>
	</item>
	
</entry>
