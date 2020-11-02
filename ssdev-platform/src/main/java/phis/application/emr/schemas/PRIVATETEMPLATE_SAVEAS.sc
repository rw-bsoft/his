<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PRIVATETEMPLATE" alias="私有模板">
	<item id="PTID" alias="私有模板ID" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1"/>
		</key>
	</item>
	<item id="DISEASEID" alias="关联疾病" type="int" mode="remote" length="9" width="230"/>
	<item id="SPTTYPE" alias="模版类型" type="int" length="9">
		<dic>
			<item key="0" text="科室模板" />  
			<item key="1" text="个人模版" />  
		</dic>
	</item>
	<item id="PTNAME" alias="模板名称" xtype="textarea" length="50" not-null="1" />
</entry>
