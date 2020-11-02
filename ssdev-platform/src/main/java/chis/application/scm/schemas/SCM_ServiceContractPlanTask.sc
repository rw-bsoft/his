<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServiceContractPlanTask" alias="签约服务计划任务表">
	<item id="taskId" alias="任务编号" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
	<item id="empiId" alias="个人主索引" type="string" length="32" hidden="true" display="0"/>
	<item id="SCID" alias="签约记录编码" type="long" length="18" display="0"/>
	<item id="taskName" alias="任务名" type="string" length="200"  width="250"/>
	<item id="taskCode" alias="任务编码" type="string" length="100" display="0"/>
	<item id="moduleAppId" alias="任务模块路径" type="string" length="200" display="0">
		<dic id="chis.dictionary.moduleApp" display="0"/>
	</item>
	<item id="status" alias="任务状态" type="string" length="1" display="0" defaultValue="3">
		<dic>
			<item key="0" text="未完成"/>
			<item key="1" text="完成"/>
			<item key="2" text="已解约"/>
			<item key="3" text="未确认"/>
		</dic>
	</item>
	<item ref="c.TOTSERVICETIMES" alias="总次数" type="int"/>
	<item ref="c.SERVICETIMES" alias="服务次数" type="int" />
<!--	<item id="ServiceTime" alias="服务次数" type="int" virtual="true"/>-->
	<item id="year" alias="签约年份" type="int" length="5"/>
	<item id="visitId" alias="随访记录ID" type="string" length="16" hidden="true"/>
	<item id="planId" alias="计划记录ID" type="string" length="16" hidden="true"/>
	<item id="sort" alias="任务优先级" type="string" length="5" hidden="true"/>
    <item id="SPID" alias="所属服务包" type="string" length="20"/>
     <item id="SPIID" alias="所属服务项" type="string" length="20" hidden="true"/>
    <item id="packageName" alias="服务包" type="string" length="30" width="140"/>
	<item ref="b.createUser" display="1" fixed="true"/>
	<item ref="b.scDate" display="1" fixed="true"/>
	<item ref="b.createUnit" display="1" fixed="true"/>
	<item ref="b.createUnit" display="1" fixed="true"/>
	<item ref="b.createUnit" display="1" fixed="true"/>
     <item ref="c.SCINID" alias="增值服务主键" type="long" length="18" display="0"/>
     <item ref="c.SCIID" alias="增值服务项主键" type="long" length="18" display="0"/>
	<relations>
		<relation type="parent" entryName="chis.application.scm.schemas.SCM_SignContractRecord">
			<join parent="SCID" child="SCID" />
		</relation>
		<relation type="parent" entryName="chis.application.scm.schemas.SCM_INCREASEITEMS">
			<!--  <join parent="SCID" child="SCID" />-->
			<join parent="TASKID" child="taskId" />
		</relation>
		 <relation type="parent" entryName="chis.application.scm.schemas.SCM_SignContractPackage">
            <join parent="SPIID" child="SPIID"/>
        </relation>
	</relations>
</entry>
