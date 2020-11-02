<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_CWSZ" alias="床位设置" sort="BRCH,FJHM desc">
  <item id="ZYH" alias="" width="30" type="long" length="18" display="1" renderer="onRenderer"/>
  <item id="JGID" alias="机构ID" type="string" display="0" length="20"  not-null="1" defaultValue="%user.manageUnit.id" pkey="true"/>
  <item id="BRCH" alias="床位号码" length="8" not-null="1" queryable="true" selected="true" pkey="true"/>
  <item id="FJHM" alias="房间号码" length="10"/>
  <item id="CWKS" alias="床位科室" type="long" length="18" not-null="1">
  	<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
  </item>
  <item id="KSDM" alias="床位病区" type="long" length="18" not-null="1" queryable="true">
  	<dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
  </item>
  <item id="CWXB" alias="性别限制" type="int" length="4" not-null="1" defaultValue="3">
  	<dic  autoLoad="true">
  		<item key="1" text="男"/>
  		<item key="2" text="女"/>
  		<item key="3" text="不限"/>
  	</dic>
  </item>
  <item id="CWFY" alias="床位费" type="double" minValue="0" length="7" precision="2" not-null="1" defaultValue="0"/>
  <item id="ICU" alias="ICU费用" type="double" minValue="0" length="9" precision="2" not-null="1" defaultValue="0"/>
  <item id="JCPB" alias="备注" type="int" length="1" not-null="1" defaultValue="0">
 	<dic  autoLoad="true">
  		<item key="0" text="普通"/>
  		<item key="1" text="加床"/>
  		<item key="2" text="虚床"/>
  	</dic>
  </item>
  <item id="YEWYH" alias="婴儿唯一号" type="long" display="0" length="18"/>
  <item id="ZDYCW" alias="自定义床位" type="int" display="0" length="1"/>
  <item id="JCRQ" alias="建床日期" type="date" display="0"/>
</entry>
