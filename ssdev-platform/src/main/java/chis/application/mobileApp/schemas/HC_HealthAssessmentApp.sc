<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.HC_HealthAssessment" alias="健康评价表">
	<item id="assessmentId" alias="评价编号" length="16" type="string" pkey="true" generator="assigned" not-null="1" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="healthCheck" alias="年检编号" length="16" type="string" display="0"/>
  
	<item id="recognize" alias="中医体质辨识" type="string" colspan="2">
		<dic  render="TreeCheck" onlyLeafCheckable="true"  checkModel ="childCascade">
			<item key="1" text="平和质">
				<item key="11" text ="平和质" />
				<item key="12" text ="基本是平和质" />
			</item>
			<item key="2" text="气虚质">
				<item key="21" text ="气虚质" />
				<item key="22" text ="气虚质倾向" />
			</item>
			<item key="3" text="阳虚质">
				<item key="31" text ="阳虚质" />
				<item key="32" text ="阳虚质倾向" />
			</item>
			<item key="4" text="阴虚质">
				<item key="41" text ="阴虚质" />
				<item key="42" text ="阴虚质倾向" />
			</item>
			<item key="5" text="痰湿质">
				<item key="51" text ="痰湿质" />
				<item key="52" text ="痰湿质倾向" />
			</item>
			<item key="6" text="湿热质">
				<item key="61" text ="湿热质" />
				<item key="62" text ="湿热质倾向" />
			</item>
			<item key="7" text="血瘀质">
				<item key="71" text ="血瘀质" />
				<item key="72" text ="血瘀质倾向" />
			</item>
			<item key="8" text="气郁质">
				<item key="81" text ="气郁质" />
				<item key="82" text ="气郁质倾向" />
			</item>
			<item key="9" text="特秉质">
				<item key="91" text ="特秉质" />
				<item key="92" text ="特秉质倾向" />
			</item>
		</dic>
	</item>
  
	<item id="abnormality" alias="健康评价" length="1"  type="string" colspan="2">
		<dic>
			<item key = "1" text="体检无异常" />
			<item key = "2" text="有异常" />
		</dic>
	</item>
	<item id="abnormality1" alias="异常1" length="500"  type="string" colspan="2" fixed="true"/>
	<item id="abnormality2" alias="异常2" length="500"  type="string" colspan="2" fixed="true"/>
	<item id="abnormality3" alias="异常3" length="500"  type="string" colspan="2" fixed="true"/>
	<item id="abnormality4" alias="异常4" length="500"  type="string" colspan="2" fixed="true"/>
	<item id="mana" alias="健康指导" length="10"  type="string" colspan="2">
		<dic render="LovCombo">
			<item key = "1" text="纳入慢性病患者健康管理" />
			<item key = "2" text="建议复查" />
			<item key = "3" text="建议转诊" />
		</dic>
	</item>
	<item id="riskfactorsControl" alias="危险因素控制" length="100"  type="string" colspan="2">
		<dic render="LovCombo">
			<item key = "1" text="戒烟" />
			<item key = "2" text="健康饮酒" />
			<item key = "3" text="饮食" />
			<item key = "4" text="锻炼" />
			<item key = "5" text="减体重" />
			<item key = "6" text="建议接种疫苗" />
			<item key = "7" text="其他" />
		</dic>
	</item>
	
	<item id="targetWeight" alias="目标体重(kg)" length="3"  type="int" colspan="2" fixed="true"/>
	<item id="vaccine" alias="建议接种疫苗" length="50"  type="string" colspan="2" fixed="true"/>
	<item id="pjOther" alias="其他控制" length="500"  type="string" colspan="4" fixed="true"/>
	<item queryable="true" id="createUser" alias="录入员工" length="20" update="false" display="1"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item queryable="true" id="createUnit" alias="录入单位" length="20" update="false"  display="1"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item queryable="true" id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"  display="1"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
