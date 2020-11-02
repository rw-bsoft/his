<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BLYMYS" alias="页眉元素数据">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="JZXH" alias="就诊序号" type="long" length="18" not-null="1"/>
	<item id="BLLB" alias="病历类别" type="int" length="9" not-null="1"/>
	<item id="BLBH" alias="病历编号" type="long" length="18" not-null="1"/>
	<item id="DLMC" alias="段落名称" length="50" not-null="1"/>
	<item id="YSMC" alias="元素名称" length="50" not-null="1"/>
	<item id="XSMC" alias="显示名称" length="50"/>
	<item id="YSLX" alias="元素类型" type="int" length="1" not-null="1"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
</entry>
