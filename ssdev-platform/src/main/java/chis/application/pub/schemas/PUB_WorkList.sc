<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pub.schemas.PUB_WorkList" alias="工作任务">
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item id="workId" type="string" length="16" pkey="true" not-null="1" fixed="true"
		display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="recordId" alias="档案编号" type="string" length="30" display="0" />
	<item id="empiId" alias="EMPIID" type="string" length="32" display="0" />
	<item id="workIcon" alias="图标" type="string" length="20" display="1" width="50" virtual="true"/>
	<item id="workType" alias="任务名称" type="string" length="2" display="1" width="300">
		<dic id="chis.dictionary.workType" />
	</item>
	<item id="count" alias="任务总数" type="string" display="1" virtual="true" width="100"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20"
		display="0">
		<dic id="manageUnit" includeParentMinLen="6"  render="Tree" parentKey="%user.manageUnit.id"
			rootVisible="true" />
	</item>
	<item id="doctorId" alias="责任医生" type="string" length="20" not-null="1"
		fixed="true" display="0">
		<dic id="user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="beginDate" alias="开始时间" type="date" display="0"/>
	<item id="endDate" alias="结束时间" type="date" display="0"/>
	<item id="otherId" alias="其他Id" type="string" display="0"/>
	<item ref="c.status"/>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="empiId" child="empiId" />
		</relation>
	</relations>
</entry>