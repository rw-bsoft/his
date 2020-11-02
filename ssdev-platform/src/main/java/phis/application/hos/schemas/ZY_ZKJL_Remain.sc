<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_ZKJL" alias="转科记录">
	<item id="JLXH" alias="记录序号" type="long" fixed="true" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12" startPos="27"/>
		</key>
	</item>
	<item id="BRXM" alias="病人姓名" fixed="true" type="string" length="18" not-null="1"></item>
	<item id="BRCH" alias="病人床号" fixed="true" type="string" length="18" not-null="1"></item>

	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<item id="HCLX" alias="换床类型" type="int" length="1" display="0"/>
	<item id="YSSQRQ" alias="医生申请日期" type="date" display="0"/>
	<item id="YSSQGH" alias="医生申请工号" length="10" display="0"/>
	<item id="BQSQRQ" alias="病区申请日期" type="date" fixed="true" />
	<item id="BQSQGH" alias="病区申请工号" length="10" display="0" defaultValue="%user.userId"/>
	<item id="BQZXRQ" alias="病区执行日期" type="date" display="0"/>
	<item id="BQZXGH" alias="病区执行工号" length="10" display="0"/>
	<item id="YSZXRQ" alias="医生执行日期" type="date" display="0"/>
	<item id="YSZXGH" alias="医生执行工号" length="10" display="0"/>
	
	<item id="HQCH" alias="换前床号" length="12" display="0"/>
	<item id="HHCH" alias="换后床号" length="12" display="0"/>
	<item id="HQBQ" alias="换前病区" type="long" length="18" fixed="true">
		<dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.id']]" autoLoad="true"></dic>
	</item>
	<item id="HHBQ" alias="换后病区" type="long" length="18" display="0"/>
	<item id="HQKS" alias="换前科室" type="long" length="18" fixed="true">
		<dic id="phis.dictionary.department_zy" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"  />
	</item>
	<item id="HHKS" alias="换后科室" type="long" length="18" fixed="true" >
		<dic id="phis.dictionary.department_zy"  filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"  autoLoad="true"/>

	</item>
	<item id="HQYS" alias="换前医生" length="10" display="0"/>
	<item id="HHYS" alias="换后医生" length="10" fixed="true">
		<dic id="phis.dictionary.doctor_cfqx"  autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>

	</item>
	<item id="FPCW" alias="分配床位" length="10"/>
	<item id="ZXBZ" alias="备注" type="int" length="1" fixed="true">
		<dic>
            <item key="2" text="未确认"/>
            <item key="3" text="已确认"/>
        </dic>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
</entry>
