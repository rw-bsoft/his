<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.npvr.schemas.EHR_NormalPopulationVisit" alias="非重点人群随访">
	<item id="id" alias="id" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item ref="b.personName" display="1"  />
	<item ref="b.sexCode" display="1"  />
	<item ref="b.birthday" display="1"  />
	<item ref="b.idCard" display="1"  />
	<item ref="b.phoneNumber" display="1"  />
	<item ref="c.regionCode" display="1"  />
	<item ref="c.regionCode_text" display="0" />
	<item ref="c.status" display="0" />
	<item ref="c.manaUnitId" display="1" />
	<item ref="c.manaDoctorId" display="1" />
	<item id="phrId" alias="档案编号" type="string" length="30" hidden="true" />
	<item id="empiId" alias="empiId" type="string" length="32" hidden="true"/>
	
	
	
	<item id="visitUser" alias="随访人员" length="20" update="false"  defaultValue="%user.userId" not-null="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" parentKey="['substring',['$','%user.manageUnit.id'],[0,12]]"/>
	</item>
	<item id="visitDate" alias="随访时间" type="date" update="false" defaultValue="%server.date.today" maxValue="%server.date.today">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="nextTime" alias="下次预约时间" type="date"/>
	
	<item id="content" alias="内容" not-null="1" length="500" xtype="textarea" colspan="3"/>
	<item id="fbs" alias="血糖(mmol/L)" type="double"/>
	<item id="pbs" alias="随机血糖(mmol/L)" type="double"/>
	<item id="con" alias="收缩压(mmHg)" type="int" minValue="10" not-null="1" maxValue="500" enableKeyEvents="true" validationEvent="false"/>
	<item id="dia" alias="舒张压(mmHg)" type="int" minValue="10" not-null="1" maxValue="500" enableKeyEvents="true" validationEvent="false"/>
	
	
	<item id="createUser" alias="登记人员" length="20" update="false" defaultValue="%user.userId" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
	
	<item id="createDate" alias="登记时间" type="datetime"  xtype="datefield" defaultValue="%server.date.today" update="false">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	<item id="createUnit" alias="登记机构" length="20" update="false" defaultValue="%user.manageUnit.id" colspan="3">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	
	<item id="lastModifyDate" alias="最后修改时间" type="timestamp" display="1" defaultValue="%server.date.datetime">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="empiId" child="empiId" />
		</relation>
	</relations>
</entry>
