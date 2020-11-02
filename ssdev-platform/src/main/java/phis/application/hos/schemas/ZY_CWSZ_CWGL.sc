<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_CWSZ" alias="床位管理">
	<item id="ZYH" alias="" width="30" virtual="true" display="1" renderer="onRenderer"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"  not-null="1" defaultValue="%user.manageUnit.id" pkey="true"/>
	<item id="BRCH" alias="床位号码" length="12" not-null="1" queryable="true" pkey="true"/>
	<item id="FJHM" alias="房间号码" length="10"/>
	<item id="CWKS" alias="床位科室" type="long" length="18" queryable="true" not-null="1">
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="KSDM" alias="床位病区" type="long" length="18" queryable="true" not-null="1">
		<dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="CWXB" alias="性别限制" type="int" length="4" not-null="1" defaultValue="3">
		<dic>
			<item key="1" text="男"/>
			<item key="2" text="女"/>
			<item key="3" text="不限"/>
		</dic>
	</item>
	<item id="CWFY" alias="床位费" type="double" length="6" precision="2" not-null="1"/>
	<item id="ZYHM" alias="住院号码" queryable="true" virtual="true"/>
	<item id="BRXM" alias="病人姓名" virtual="true"/>
	<item id="BRXB" alias="病人性别" virtual="true">
		<dic id="phis.dictionary.gender" autoLoad="true"/>
	</item>	
	<item id="BRXZ" alias="病人性质" virtual="true">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="BRKS" alias="病人科室" virtual="true">
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.id']]" searchField="PYCODE" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院日期" virtual="true"/>
	<item id="JCPB" alias="备注" type="int" length="1" not-null="1" defaultValue="0">
		<dic>
			<item key="0" text="普通"/>
			<item key="1" text="加床"/>
			<item key="2" text="虚床"/>
		</dic>
	</item>
	<item ref="b.CYPB" alias="出院判别" type="int" length="2" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="ZYH"/>
		</relation>
	</relations>
</entry>
