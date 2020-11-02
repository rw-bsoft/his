<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YS_ZY_HZSQ" alias="会诊_会诊申请">
	<item id="SQSJ" alias="申请时间"  display="2"  length="2000" type="timestamp" fixed="true" layout="LB" defaultValue="%server.date.datetime"/>
	<item id="SQXH" type="long"  alias="申请单号" length="18" not-null="1" generator="assigned" pkey="true"  layout="LB"  display="2" fixed="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="27"/>
		</key>
	</item>
	<item id="BRXM" alias="病人姓名" display="2" length="8" layout="part1" fixed="true"/>
	<item id="BRXB" alias="病人性别" display="2" length="8" layout="part1" fixed="true"/>
	<item id="BRYL" alias="病人年龄" display="2" length="8" layout="part1" fixed="true"/>
	<item id="BRCH" alias="病人床号" display="2" length="8" layout="part1" fixed="true" />
	<item id="ZYHM" alias="住院号码" display="2" length="8" layout="part1" fixed="true"/>
	<item id="ZDMC" alias="当前诊断" display="2" length="8" layout="part1" fixed="true"/>
	<item id="JZHM" alias="就诊号码"  type="string" length="18" not-null="1" display="0"/>
	<item id="SQKS" alias="申请科室" type="string" length="8" not-null="1" layout="part1" fixed="true">
		<dic id="phis.dictionary.department_zy" autoLoad="true"  />
	</item>
	<item id="SQYS" alias="申请医生" type="string" length="10" not-null="1" layout="part1" fixed="true">
		<dic id="phis.dictionary.doctor" autoLoad="true" />
	</item>
	<item id="HZSJ" alias="会诊时间" type="date"   layout="part1" not-null="1" defaultValue="%server.date.datetime"/>
	
	<item id="JJBZ" alias="紧急" length="1"  layout="part1" defaultValue="0" display="2" >
		<dic id="phis.dictionary.urgent" autoLoad="true"/>
	</item>
	
	<item id="HZMD" alias="会诊目的" xtype="textarea" length="255" not-null="1" layout="part2" colspan="4" display="2"/>
	<item id="HZMD2" alias="会诊目的2" type="string" length="255" display="0"/>
	<item id="BQZL" alias="患者治疗情况" xtype="textarea" length="255"  layout="part3" colspan="4" not-null="1" display="2"/>
	<item id="YQDX" alias="会诊者" type="string" length="255"  layout="OBJ" not-null="1">
		<dic id="phis.dictionary.doctor_cfqx"   filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"  autoLoad="true" />
	</item>
	<item id="GH"  alias="工号" type="string" length="10" not-null="1" layout="OBJ" fixed="true" display="2"/>
	<!--申请医生签名-->
	<item id="SQQM" alias="申请医生签名" length="8" layout="LAST" fixed="true" display="0"/>
	<!--上级医生签名-->
	<item id="SJQM" alias="上级医生签名" length="8" layout="LAST" fixed="true" display="0"/>
	<!--打印时间-->
	<item id="DYSJ" alias="申请会诊时间" display="0" fixed="true" type="timestamp" xtype="datetimefield" defaultValue="%server.date.datetime" layout="LAST"/>
	<item id="TJBZ" alias="提交标志" length="1" not-null="1" display="1">
		<dic>
			<item key="0" text="已保存"/>
			<item key="1" text="已提交"/>
		</dic>
	</item>
	<item id="TJYS" alias="提交医生" type="string" length="10" display="0"/>
	<item id="TJSJ" alias="提交时间" type="timestamp"  display="0"/>
	<item id="ZFBZ" alias="作废标志" length="1" not-null="1" display="0"/>
	<item id="JSBZ" alias="结束标志" length="1" not-null="1" display="1">
		<dic>
			<item key="0" text="申请"/>
			<item key="1" text="结束"/>
		</dic>
	</item>
	<item id="JSSJ" alias="结束时间" type="timestamp" display="0" />
	<item id="TXRY" alias="填写人员" type="string" length="10" not-null="1" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
</entry>
