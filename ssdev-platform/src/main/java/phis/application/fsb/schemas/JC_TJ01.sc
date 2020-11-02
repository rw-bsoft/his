<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_TJ01" alias="家床提交记录表">
	<item id="TJXH" alias="提交序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="TJYF" alias="提交药房" type="long" length="18" not-null="1"/>
	<item id="YZLX" alias="医嘱类型" type="int" length="2" not-null="1"/>
	<item id="FYFS" alias="发药方式" type="long" length="18"/>
	<item id="XMLX" alias="项目类型" type="int" length="2" not-null="1"/>
	<item id="TJSJ" alias="提交时间" type="date"/>
	<item id="TJBQ" alias="提交病区" type="int" length="8" not-null="1"/>
	<item id="TJGH" alias="提交工号" length="10" not-null="1"/>
	<item id="FYBZ" alias="发药标志" type="int" length="1" not-null="1"/>
</entry>
