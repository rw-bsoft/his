<?xml version="1.0" encoding="UTF-8"?>

<entry id="JC_YJ01_ZX" tableName="JC_YJ01" alias="家床医技表单01表">
	<item id="YJXH" alias="医技序号"  length="18"  type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20"  not-null="1" fixed="true"  display="0" />
	<item id="ZYH" alias="住院号" type="long" length="18"  display="0" fixed="true"    />
	<item id="ZYHM" alias="家床号" type="string" length="10"  fixed="true" display="1" />
	<item id="BRXM" alias="病人姓名" type="string" length="40"  display="1"  fixed="true"  />
	<item id="HJGH" alias="划价医生" type="string" length="10"  fixed="true" display="1"  >
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="KDRQ" alias="申检时间" type="date" width="130"   display="1"  fixed="true"/>
	<item id="YSDM" alias="申检医生" type="string" length="10"  fixed="true" display="1" >
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="KSDM" alias="申检科室" type="long" length="18"  fixed="true" display="1" >
		<dic id="phis.dictionary.department" autoLoad="true"   defaultValue = "%user.manageUnit.id" searchField="PYCODE"/>
	</item>
	<item id="ZXYS" alias="执行医生" type="string" length="10"  fixed="true" display="1" >
		<dic id="phis.dictionary.doctor_yjqx"  filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true"/>
	</item>
	<item id="ZXKS" alias="执行科室" type="long" length="18"  fixed="true" display="0" >
		<dic id="phis.dictionary.department" autoLoad="true"   defaultValue = "%user.manageUnit.id" searchField="PYCODE"/>
	</item>
	<item id="DJZT" alias="单据状态" type="long" length="1" not-null="1" display="0" />
	<item id="FYBQ" alias="费用病区" type="long" length="18" display="0" />
	<item id="TJHM" alias="特检号码" type="string" length="10"  fixed="true" display="0" />
	<item id="ZXRQ" alias="执行日期" type="date"   fixed="true" display="0" />
	<item id="ZXPB" alias="执行判别" type="long" length="1"  fixed="true" not-null="1" display="0" />
	<item id="BBBM" alias="标本编码" type="string" length="4"  fixed="true" display="0" />
	<item id="ZYSX" alias="注意事项" type="string" length="250" display="0" />
	<item id="ZFPB" alias="作废判别" type="long" length="1"  fixed="true" not-null="1" display="0" />
	<item id="HYMX" alias="化验明细" type="string" length="250" display="0" />
	<item id="YJPH" alias="医技片号" type="string" length="20" fixed="true" display="0" />
	<item id="SQDH" alias="申请单号" type="long" length="18" display="0" />
	<item id="BWID" alias="部位编号" type="long" length="9" display="0" />
	<item id="JBID" alias="疾病编号" type="long" length="18" display="0" />
	<item id="SQWH" alias="申请文号" type="long" length="18" display="0" />
	<item id="BRID" alias="病人ID号" display="0"/>
	
	<item id="YZXH" alias="医嘱序号" type="long" length="18" display="0"/>
</entry>
