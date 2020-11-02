<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BL04" alias="病历04表">
	<item id="FJBH" alias="附件编号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="WDBH" alias="文档编号" type="long" length="18" not-null="1"/>
	<item id="FJLX" alias="附件类型" type="int" length="4" not-null="1"/>
	<item id="FJMC" alias="附件名称" length="100" not-null="1"/>
	<item id="FJGL" alias="附件关联" type="long" length="18"/>
	<item id="WJLX" alias="文件类型" length="10"/>
	<item id="WJCD" alias="文件长度" type="long" length="18"/>
	<item id="FJNR" alias="附件内容" type="object"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
</entry>
