<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_OFFICE" alias="科室代码">
	<item id="ID" alias="科室代码" type="long" length="15" generator="assigned"  display="0"  pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="15" startPos="1" />
		</key>
	</item>
	<item id="ORGANIZCODE" alias="所属机构" type="string" display="0" />
	<item id="PARENTID" alias="上级科室"  type="long"  display="0" length="18" />
	<item id="OFFICENAME" alias="科室名称" not-null="true" type="string" length="50"/>
	<item id="PLSX" alias="排列顺序" type="string" not-null="1" length="5"/>
	<item id="ACCOUNTOFFICE" alias="核算科室" length="18" type="long">
		<dic id="phis.dictionary.department_leaf" filter="['eq',['$',['$','item.properties.ORGANIZCODE']],['$','%user.manageUnit.ref']]"/>
	</item>	
	<item id="OUTPATIENTCLINIC" alias="门诊科室" type="string" length="1" not-null="1" defaultValue="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="MEDICALLAB" alias="医技科室" type="string" length="1" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="HOSPITALDEPT" alias="住院科室" type="string" length="1" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="HOSPITALAREA" alias="住院病区" type="string" length="1" not-null="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="RATEDBED" alias="额定床位" type="int" length="4" hidden="true"/>
	<!-- <item id="SJBM" alias="上级编码" type="string" hidden="true"/>-->
	<item id="PYCODE" alias="拼音码" type="string" length="100" target="KSMC" codeType="py"/>
	<!-- <item id="YBDZ" alias="医保对照" type="string" length="10"  display="2"/> -->
</entry>
