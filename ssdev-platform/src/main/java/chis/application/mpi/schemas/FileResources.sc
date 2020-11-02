<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="FileResources" alias="个人照片">
	<item id="fileId" alias="照片编号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="fileName" alias="照片名称" type="string"/>
	<item id="localFileName" alias="名称" type="string"/>
	<item id="length" alias="照片大小" type="long"/>
	<item id="content" alias="照片" type="binary"/>
	<item id="contentType" alias="照片类型" type="string"/>
	<item id="owner" alias="照片上传者" type="string"/>
	<item id="uploadTime" alias="更新时间" type="date"/>
	<item id="folderId"  type="string"/>
	<item id="shareRight"  type="int"/>
</entry>