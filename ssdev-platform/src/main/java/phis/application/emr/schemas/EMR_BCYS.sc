<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BCYS" alias="病程医生(虚拟)">
	<item id="YSDM" alias="医生" defaultValue="%user.userId" not-null="1">
		<dic id="phis.dictionary.doctor" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="JLSJ" alias="时间" type="datetime" not-null="1" length="30" />
	<item id="YL" alias="预览" xtype="textarea" height="30" colspan="2" fixed="true"/>
</entry>
