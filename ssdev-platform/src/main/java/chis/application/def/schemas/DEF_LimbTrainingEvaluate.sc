<?xml version="1.0" encoding="UTF-8"?>
<entry alias="肢体残疾训练评估" sort="evaluateStage,id">
	<item id="id" alias="主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="defId" alias="登记号" type="string" length="16" hidden="true"/>
	
	<item id="visitDate" alias="评估日期" type="date" maxValue="%server.date.today" not-null="1"/>
	<item id="turnOver" alias="翻身" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="sit" alias="坐" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="stand" alias="站" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="shift" alias="转移" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="walk" alias="步行" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="walkupStairs" alias="上下台阶" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="takeFood" alias="进食" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="undress" alias="穿脱衣服" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="wash" alias="洗漱" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="toilet" alias="入厕" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="独立完成" />
			<item key="1" text="需要他人帮助" />
			<item key="0" text="完全依赖他人" />
		</dic>
	</item>
	<item id="exchangeActivity" alias="交流" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="能" />
			<item key="1" text="部分能" />
			<item key="0" text="不能" />
		</dic>
	</item>
	<item id="houseWork" alias="做家务" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="能" />
			<item key="1" text="部分能" />
			<item key="0" text="不能" />
		</dic>
	</item>
	<item id="groupActivity" alias="参加集体活动" type="int" display="2" not-null="1">
		<dic>
			<item key="2" text="能" />
			<item key="1" text="部分能" />
			<item key="0" text="不能" />
		</dic>
	</item>
	<item id="score" alias="整体评估分数" type="int" fixed="true" not-null="1" display="2"/>
	
	<item id="visitUser" alias="康复指导员" type="string" length="20" defaultValue="%user.userId" colspan="2" not-null="1" display="2">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
	</item>
	<item id="evaluateStage" alias="评估阶段" type="int" fixed="true" not-null="1" display="2">
		<dic>
			<item key="1" text="初次评估" />
			<item key="2" text="不定期评估" />
		</dic>
	</item>
	<item id="inputUnit" alias="录入单位" type="string" length="20"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id" display="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入人" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" display="2">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" display="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
