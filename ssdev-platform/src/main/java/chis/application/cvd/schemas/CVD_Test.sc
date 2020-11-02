<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_Test" alias="心血管疾病知识测验" sort="num,testId">
	<item id="recordId" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="inquireId" alias="登记序号" type="string" length="16"
		hidden="true" />
	<item id="phrId" alias="档案编号" type="string" length="30"
		hidden="true" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" hidden="true" />
	<item id="testId" alias="项目ID号" type="string" length="16"
		hidden="true" />
	<item id="testName" alias="测验项目名称" type="string" length="200"
		width="800" fixed="true" />
	<!-- 
		<item ref="b.testItemA" display="1" width="100"/>
		<item ref="b.testItemB" display="1"  width="100"/>
		<item ref="b.testItemC" display="1"  width="100"/>
		<item ref="b.testItemD" display="1"  width="100"/>
		 -->
	<item id="testResult" alias="测验结果" type="string" length="64" width="100">
		<dic>
			<item key="1" text="A" />
			<item key="2" text="B" />
			<item key="3" text="C" />
			<item key="4" text="D" />
		</dic>
	</item>
	<item id="result" alias="标准答案" type="string" length="200" fixed="true" width="100">
		<dic>
			<item key="1" text="A" />
			<item key="2" text="B" />
			<item key="3" text="C" />
			<item key="4" text="D" />
		</dic>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.prop.manaUnitId" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="timestamp"
		defaultValue="%server.date.date" hidden="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="num" alias="序号" type="int" length="3" hidden="true"/>
	<relations>
		<relation type="parent" entryName="chis.application.cvd.schemas.CVD_TestDict" />
	</relations>
	 
</entry>
