<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BLZD" alias="病历字典表">
	<item id="XMBH" alias="项目编号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="YWLB" alias="业务类别" type="int" length="1" not-null="1"/>
	<item id="XMLX" alias="项目类型"  length="12" not-null="1"/>
	<item id="XMMC" alias="项目名称"  length="30" not-null="1"/>
	<item id="PYDM" alias="拼音代码" length="6" not-null="1"/>
	<item id="XMQZ" alias="项目取值" length="255" not-null="1"/>
	<item id="PLCX" alias="排列次序" type="int" length="9"/>
</entry>