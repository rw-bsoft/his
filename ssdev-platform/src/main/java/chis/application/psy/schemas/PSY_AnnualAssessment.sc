<entry  alias="精神病年度评估" sort="a.createDate asc,a.assessmentId asc">
	<item id="assessmentId" alias="评估序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="精神病档案编号" type="string" length="30" fixed="true" display="0" />
	<item id="treamentYN" alias="治疗和康复落实与否" type="string" length="1" display="2" not-null="1">
		<dic>
			<item key="1" text="落实 " />
			<item key="2" text="部分落实" />
			<item key="3" text="未落实" />
		</dic>
	</item>
	<item id="overallCondition" alias="全年病情总体变化" type="string" length="1" display="2" not-null="1">
		<dic>
			<item key="1" text="好转 " />
			<item key="2" text="无变化" />
			<item key="3" text="加重" />
		</dic>
	</item>
	<item id="workAbility" alias="劳动能力" type="string" length="1" display="2" not-null="1">
		<dic id="chis.dictionary.assessment"/>
	</item>
	<item id="personalLife" alias="个人生活料理" type="string" length="1" display="2" not-null="1">
		<dic id="chis.dictionary.assessment"/>
	</item>
	<item id="housework" alias="家务劳动" type="string" length="1" display="2" not-null="1">
		<dic id="chis.dictionary.assessment"/>
	</item>	
	<item id="produce" alias="生产劳动及工作" type="string" length="1" display="2" not-null="1">
		<dic id="chis.dictionary.assessment"/>
	</item>
	<item id="learningAbility" alias="学习能力" type="string" length="1" display="2" not-null="1">
		<dic id="chis.dictionary.assessment"/>
	</item>	
	<item id="communication" alias="社会人际交往" type="string" length="1" display="2" not-null="1">
		<dic id="chis.dictionary.assessment"/>
	</item>
	<item id="overallAssessment" alias="总体评估" type="string" length="1" display="2" not-null="1">
		<dic id="chis.dictionary.assessment"/>
	</item>		
	<item id="suggest" alias="今后治疗和康复意见" type="string" length="2000" xtype="textarea"
		colspan="3" display="2"/>	
	<item id="createUnit" alias="评估机构" type="string" length="20"
		width="320" defaultValue="%user.manageUnit.id" fixed="true" display="2" not-null="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="评估医生" type="string" length="20"
		queryable="true" defaultValue="%user.userId" fixed="true" not-null="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="评估日期" type="datetime"  xtype="datefield" queryable="true" defaultValue="%server.date.today" fixed="true" not-null="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<!--<item id="planDate" alias="计划日期" type="date" display="0"/>  -->
	<item ref="b.regionCode" display="0" queryable="true" />
	<item ref="b.manaUnitId" display="0" queryable="true" />
	<relations>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
