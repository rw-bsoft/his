<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01" alias="单据选择">
	<item id="CFSB" alias="处方识别" type="string" display="0"/>
	<item id="DJMS" alias="单据描述" type="int" width="80" renderer="DJMSRenderer">
		<dic>
			<item key="0" text=""/>
			<item key="1" text="全自费"/>
			<item key="2" text="医保可报销"/>
		</dic>
	</item>
	<item id="CFLX" alias="类型" type="string" width="80">
		<dic>
			<item key="0" text="检查单"/>
			<item key="1" text="西药方"/>
			<item key="2" text="中药方"/>
			<item key="3" text="草药方"/>
		</dic>
	</item>
	<item id="KDRQ" alias="开单日期" type="string" width="100"/>
	<item id="KDKS" alias="开单科室" type="string" >
		<dic id="phis.dictionary.department" autoLoad="true"/>
	</item>
	<item id="KDYS" alias="开单医生" type="string">
		<dic id="phis.dictionary.doctor" />
	</item>
	<item id="ZXKS" alias="执行科室" type="string" display="0">
		<dic id="phis.dictionary.department_tree" render="Tree" autoLoad="true" searchField="PYCODE" parentKey="%user.manageUnit.id" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" />
	</item>
	<item id="HJJE" alias="金额" type="double" precision="2"/>
	<item id="DJLY_TEXT" alias="单据来源" type="string"/>
	<item id="XQ" alias="效期(天数)" type="int" minValue="1" queryable="true" selected="true" display="0"/>
	<item id="JZXH" alias="就诊序号" type="long" display="0"/>
</entry>
