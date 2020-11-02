<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_TestDict" alias="知识测验项目" sort = "num,testId">
	<item id="testId" alias="项目ID号" type="string" length="16" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="testName" alias="项目名称" type="string" not-null="1" xtype="textarea" length="200" colspan="3" anchor="100%" width="1000"/>
	<!-- 
		  <item id="testItemA" alias="项目选项A" type="string" not-null="1" length="200" colspan="2" anchor="100%" width="190"/>
		  <item id="testItemB" alias="项目选项B" type="string" not-null="1" length="200" colspan="2" anchor="100%" width="190"/>
		  <item id="testItemC" alias="项目选项C" type="string" length="200" colspan="2" anchor="100%" width="190"/>
		  <item id="testItemD" alias="项目选项D" type="string" length="200" colspan="2" anchor="100%" width="190"/>
		  -->
	<item id="result" alias="标准答案" type="string" length="64" colspan="3" anchor="100%" not-null="1">
		<dic>
			<item key="1" text="A" />
			<item key="2" text="B" />
			<item key="3" text="C" />
			<item key="4" text="D" />
		</dic>
	</item>
	<item id="part" alias="段落" type="int" length="1" >
		<dic>
			<item key="1" text="第一部分" />
			<item key="2" text="第二部分" />
		</dic>
	</item>
	<item id="num" alias="序号" type="int" length="3" />
</entry>
