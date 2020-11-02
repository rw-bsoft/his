<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.fhr.schemas.PER_XCQYFW" alias="乡村医生签约" sort="a.qyhbh desc">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30" width="165" queryable="true" fixed="true"/>
	<item id="qyhbh" alias="签约户编号" type="string" length="20" not-null="1" />
	<item ref="b.personName" display="1" queryable="true"  width="60"/>
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.insuranceCode" display="1" queryable="true" width="100"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="16" not-null="1" display="2" 
		width="165" fixed="true" queryable="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="stratdate" alias="开始日期" type="date" update="true" defaultValue="%server.date.today" not-null="1" queryable="true">
	</item>
	<item id="enddate" alias="结束日期" type="date" update="true"  not-null="1" queryable="true">
	</item>
	<item id="qyxz" alias="签约性质" type="string" length="1" width="100"  not-null="1" queryable="true">
		<dic>
			<item key="1" text="新签" />
			<item key="2" text="续签" />
		</dic>
	</item>
	<item id="singingtype" alias="签约服务类型" type="string" length="1" width="100"  not-null="1" queryable="true">
		<dic render="LovCombo">
			<item key="01" text="基本服务组合" />
			<item key="02" text="诊疗优惠组合" />
			<item key="03" text="健康管理组合" />
		</dic>
	</item>
	<item id="singinguser" alias="签约乡村医生" type="string" length="20" 
		    queryable="true" not-null="1" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="qywsyys" alias="签约卫生院医生" type="string" length="20" not-null="1" />
	<item ref="b.address" display="1" queryable="true" />
	<item ref="c.regionCode" display="0" queryable="true"  width="165"/>
	<item id="empiId" alias="empiid" type="string" length="32"
		fixed="true" colspan="3" hidden="true" />
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" hidden="true"  not-null="0">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="createunit" alias="建档机构" type="string" length="16" queryable="true"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createuser" alias="建档人员" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="date" update="true" defaultValue="%server.date.datetime" queryable="true">
	</item>	
	<item id="status" alias="档案状态" type="string" length="1"
		defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="有效" />
			<item key="1" text="注销" />
			<item key="2" text="（注销）待核实" />
		</dic>
	</item>
	<item id="cancellationdate" alias="档案注销日期" type="date" display="0"
		fixed="true" />
	<item id="cancellationreason" alias="档案注销原因" type="string"
		length="1" display="0" fixed="true">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="cancellationuser" alias="注销人" type="string" length="20"
		hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="deadreason" alias="死亡原因" type="string" fixed="true"
		length="100" hidden="true" colspan="3" anchor="100%" />
	<item id="cancellationcheckuser" alias="注销复核者" type="string"
		length="20" hidden="true" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationcheckdate" alias="注销复核时间" type="date"
		hidden="true" fixed="true" />
	<item id="cancellationcheckunit" alias="注销复核单位" type="string"
		length="16" hidden="true" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="lastmodifyuser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
	<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastmodifydate" alias="最后修改日期" type="date"
		defaultValue="%server.date.today" display="0">
         <set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="lastmodifyunit" alias="修改单位" type="string" length="16"
		width="180" hidden="true" defaultValue="%user.prop.manaUnitId">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="cancellationunit" alias="注销单位" type="string" length="8"
		width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>