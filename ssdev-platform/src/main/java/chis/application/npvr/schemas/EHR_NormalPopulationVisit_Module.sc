<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.npvr.schemas.EHR_NormalPopulationVisit" alias="正常人群随访" sort="a.id">
	<item id="id" alias="id" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" hidden="true" />
	<item id="empiId" alias="empiId" type="string" length="32" hidden="true"/>
	<item id="visitDate" alias="随访时间" type="date" update="false" defaultValue="%server.date.today" maxValue="%server.date.today" width="105">
		<set type="exp">['$','%server.date.today']</set>
	</item>	
	<item id="visitUser" alias="随访人员" length="20" update="false"  defaultValue="%user.userId" not-null="1" width="105">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="nextTime" alias="下次预约时间" type="date" minValue="%server.date.today"  hidden="true"/>
	
	<item id="content" alias="内容" not-null="1" length="500" xtype="textarea" colspan="3" hidden="true"/>
	<item id="fbs" alias="血糖" type="double"  hidden="true"/>
	<item id="pbs" alias="随机血糖" type="double"  hidden="true"/>
	<item id="con" alias="收缩压" type="int" minValue="10" not-null="1" maxValue="500" enableKeyEvents="true" validationEvent="false"  hidden="true"/>
	<item id="dia" alias="舒张压" type="int" minValue="10" not-null="1" maxValue="500" enableKeyEvents="true" validationEvent="false"  hidden="true"/>
	
	
	<item id="createUser" alias="登记人员" length="20" update="false" defaultValue="%user.userId" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
	
	<item id="createDate" alias="登记时间" type="datetime"  xtype="datefield" defaultValue="%server.date.today" hidden="true" update="false">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	<item id="createUnit" alias="登记机构" length="20" update="false" hidden="true" defaultValue="%user.manageUnit.id" colspan="3">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	
	<item id="lastModifyDate" alias="最后修改时间" type="datetime"  xtype="datefield" display="1" hidden="true" defaultValue="%server.date.datetime">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" hidden="true"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" length="20" defaultValue="%user.userId" hidden="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp" run="server">['$','%user.userId']</set>
	</item>
</entry>
