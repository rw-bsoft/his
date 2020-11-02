<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="ZY_CWSZ" alias="床位信息">
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"  not-null="1" defaultValue="%user.manageUnit.id" pkey="true"/>
	<item id="BRCH" alias="床位号码" fixed="true" length="12" not-null="1" pkey="true"/>
	<item id="FJHM" alias="房间号码" fixed="true" length="10"/>
	<item id="CWKS" alias="床位科室" fixed="true" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department_zy" autoLoad="true"/>
	</item>
	<item id="KSDM" alias="床位病区" fixed="true" type="long" length="18" not-null="1">
		<dic id="phis.dictionary.department_bq" autoLoad="true"/>
	</item>
	<item id="CWXB" alias="性别限制" fixed="true" type="int" length="4" not-null="1" defaultValue="3">
		<dic>
			<item key="1" text="男"/>
			<item key="2" text="女"/>
			<item key="3" text="不限"/>
		</dic>
	</item>
	<item id="CWFY" alias="床位费" fixed="true" type="double" length="6" precision="2" not-null="1"/>
	<item id="JCPB" alias="床位类别" fixed="true" type="int" length="1" not-null="1" defaultValue="0">
		<dic>
			<item key="0" text="普通"/>
			<item key="1" text="加床"/>
			<item key="2" text="虚床"/>
		</dic>
	</item>
</entry>
