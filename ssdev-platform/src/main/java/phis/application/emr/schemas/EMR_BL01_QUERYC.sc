<?xml version="1.0" encoding="UTF-8"?>

<entry alias="病历01表" entityName="EMR_BL01_QUERY">
	<item id="TJID" alias="条件序号" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="TJMC" alias="条件名称" length="50" not-null="1" width="180"/>
	<item id="YHID" alias="用户编号" length="30"  display="0"/>
	<item id="BRID" alias="病人号码" type="long" length="18" display="0"/>
	<item id="BLLX" alias="病历类型" type="int" length="1" display="0" >
		<dic>
			<item key="0" text="病历"/>
			<item key="1" text="病程"/>
		</dic>
	</item>
	<item id="BRZD" alias="病人诊断" length="255"  display="0"/>
	<item id="XTSJKS" alias="创建时间开始"  type="date" display="0"/>
	<item id="XTSJJS" alias="创建时间结束" type="date"  display="0"/>
	<item id="MBLB" alias="模板类别" type="int" length="9" display="0">
		<dic id="phis.dictionary.caseType"/>
	</item>
	<item id="BRXM" alias="病人姓名" length="30"  display="0"/>
	<item id="BLZT" alias="病历状态" type="int" length="1"  display="0" defaultValue="5">
		<dic>
			<item key="0" text="书写" />
			<item key="1" text="完成" />
			<item key="2" text="封存(归档)" />
			<item key="9" text="删除" />
			<item key="5" text="非删除" />
		</dic>
	</item>
	<item id="BLLB" alias="病历类别" type="int" length="9" display="0">
		<dic id="phis.dictionary.caseFramework"/>
	</item>
	<item id="JLSJKS" alias="记录时间开始"  display="0" type="date" />
	<item id="JLSJJS" alias="记录时间结束"  display="0" type="date" />
	<item id="MBBH" alias="指定模板" length="9" type="int" display="0"/>
	<item id="MBMC" alias="模板名称" length="255"  display="0"/>
	<item id="BRKS" alias="病人科室" type="long" length="18"  display="0">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="SXYS" alias="书写医生" type="long" length="18"  display="0">
		<dic id="phis.dictionary.doctor" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" sliceType="1"/>
	</item>
	<item id="BLMC" alias="病历名称" length="100" width="200" display="0"/>
	<item id="WCSJKS" alias="完成时间开始" display="0" type="date"/>
	<item id="WCSJJS" alias="完成时间结束" display="0" type="date"/>
</entry>
