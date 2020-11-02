<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YS_ZY_HZYJ" alias="会诊_会诊意见">
	<item id="JLXH" alias="记录序号" length="18" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="12" startPos="27"/>
		</key>
	</item>
	<item id="SQXH" alias="申请序号" length="18" not-null="1" display="0"/>
	<item id="BRXM" alias="病人姓名" length="8" layout="part1" fixed="true"/>
	<item id="BRXB" alias="病人性别"  length="8" layout="part1" fixed="true"/>
	<item id="BRYL" alias="病人年龄"  length="8" layout="part1" fixed="true"/>
	<item id="BRCH" alias="病人床号"  length="8" layout="part1" fixed="true" />
	<item id="ZYHM" alias="住院号码"  length="8" layout="part1" fixed="true"/>
	<item id="SQKS" alias="科室" type="string" length="8" not-null="1" layout="part1" fixed="true">
		<dic id="phis.dictionary.department_zy" autoLoad="true"  />
	</item>
	<item id="HZSJ" alias="会诊时间" type="timestamp" xtype="datetimefield" fixed="true"  layout="part1" not-null="1"/>
	<item id="SQYS" alias="申请医生" type="string" length="10" not-null="1" layout="part1" fixed="true">
		<dic id="phis.dictionary.doctor" autoLoad="true" />
	</item>
	<item id="YQDX" alias="会诊者" type="string" length="255"  layout="part1" not-null="1" fixed="true">
		<dic id="phis.dictionary.doctor_cfqx"   filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"  autoLoad="true" />
	</item>
	<item id="HZMD" alias="会诊目的" xtype="textarea" length="255" layout="part1" colspan="4" fixed="true" anchor="75%"/>
	<item id="BQZL" alias="患者治疗情况" xtype="textarea" length="255"  layout="part1" colspan="4" not-null="1" fixed="true" anchor="75%"/>
	<item id="HZYJ" alias="会诊意见" type="string" xtype="textarea"  colspan="4" length="255" layout="part2" anchor="75%"/>
	<item id="HZYJ2" alias="会诊意见2" type="string" length="255" display="0"/>
	<item id="SSYS" alias="所属医生" type="string" length="10" not-null="1" layout="part2">
		<dic id="phis.dictionary.doctor_cfqx"   filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"  autoLoad="true" />
	</item>
	<item id="KSDM" alias="代表科室" length="8" layout="part2">
		<dic id="phis.dictionary.department_zy" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" />
	</item>
	<item id="QMYS" alias="签名医生" type="string" length="10" layout="part2" display="0"/>
	<item id="SXYS" alias="书写医生" type="string" length="10" not-null="1" display="0"/>
	<item id="SXSJ" alias="书写时间" type="timestamp" not-null="1" display="0"/>
	<item id="QMSJ" alias="签名时间" type="timestamp" layout="part2" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
</entry>
