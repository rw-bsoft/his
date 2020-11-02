<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_CWSZ" alias="床位设置">
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0" pkey="true"/>
	<item id="BRCH" alias="病人床号" length="12" not-null="1" pkey="true" fixed="true"/>
	<item id="FJHM" alias="房间号码" length="10" fixed="true"/>
	<item id="CWKS" alias="床位科室" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.department_zy" autoLoad="true"/>
	</item>
	<item id="KSDM" alias="床位病区" type="long" length="18" not-null="1" fixed="true">
		<dic id="phis.dictionary.department_bq" autoLoad="true"/>
	</item>
	<item id="CWXB" alias="床位性别" type="int" length="4" not-null="1" fixed="true">
		<dic autoLoad="true">
			<item key="1" text="男"/>
			<item key="2" text="女"/>
			<item key="3" text="不限"/>
		</dic>
	</item>
	<item id="CWFY" alias="床位费用" type="double" length="6" precision="2" not-null="1" fixed="true"/>
	<item id="ICU" alias="ICU费用" type="double" length="8" precision="2" not-null="1" fixed="true"/>
	<item id="JCPB" alias="床位类别" type="int" length="1" not-null="1" dispaly="0" fixed="true">
		<dic autoLoad="true">
			<item key="0" text="普通"/>
			<item key="1" text="加床"/>
			<item key="2" text="虚床"/>
		</dic>
	</item>
	<item id="ZYH" alias="住院号" type="long" length="18" display="0"/>
	<item id="YEWYH" alias="婴儿唯一号" type="long" length="18" display="0"/>
	<item id="ZDYCW" alias="自定义床位" type="int" length="1" display="0"/>
</entry>
