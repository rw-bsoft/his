<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.pub.schemas.PUB_DataValidity"  alias="服务有效性认证" sort="serviceDate" version="1331867420000" filename="E:\MyProject\BZWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\pub/PUB_VisitPlan.xml">
	<item id="registerId" pkey="true" alias="记录id" type="string" length="30" not-null="1" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="serviceBusiness" alias="服务业务" type="string" length="30"  queryable="true">
		<item id="empiId" alias="empiid" type="string" length="32" hidden="true"/>
		<dic>
			<item key="1" text="个人健康档案"/>
			<item key="2" text="老年人档案"/>
			<item key="3" text="老年人随访"/>
			<item key="4" text="高血压档案"/>
			<item key="5" text="高血压评估"/>
			<item key="6" text="高血压随访"/>
			<item key="7" text="糖尿病档案"/>
			<item key="8" text="糖尿病随访"/>
			<item key="9" text="家医服务记录"/>
			<item key="10" text="健康问卷"/>
			<item key="11" text="新生儿访视"/>
			<item key="12" text="产后访视"/>
			<item key="13" text="产后42天"/>
		</dic>       
	</item>
	<item id="service" alias="服务人员" type="string" length="30">
		<dic id="chis.dictionary.user"/>
	</item>
	<item id="serviceTarget" alias="服务对象" type="string" length="30" />
	<item id="organization" alias="机构" type="string" length="50" >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  lengthLimit="9"  rootVisible = "true" render="Tree"  parentKey="%user.manageUnit.id"/>
	</item>
	<item id="serviceDate" alias="服务时间" type="date" />
	<item id="GPS" alias="GPS坐标" type="string" length="30"  hidden="true"/>
	<item id="location" alias="地址" type="string" length="255"  />
	<item id="validityFlag" alias="有效性标识" type="string" length="5"  defaultValue="0">
		<dic>
			<item key="0" text="未匹配"/>
			<item key="1" text="有效"/>
			<item key="2" text="无效"/>
		</dic>
	</item>
</entry>
