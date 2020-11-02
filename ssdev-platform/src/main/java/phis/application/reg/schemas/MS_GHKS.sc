<?xml version="1.0" encoding="UTF-8"?>

<entry tableName="MS_GHKS" alias="挂号科室" sort="a.KSDM">
	<item id="KSDM" alias="科室代码" type="long" length="18" display="0" not-null="1" generator="assigned" pkey="true"  >
		<key>
			<rule name="increaseId" type="increase" length="10" startPos="1"/>
		</key>
	</item>
	<item id="KSMC" alias="科室名称"  width="150" type="string" length="40" not-null="1" />
	<item id="MZKS" alias="门诊科室" type="long" width="150"  length="18" not-null="1">
		<dic id="phis.dictionary.department_leaf" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE" autoLoad="true"/>
	</item>
	<item id="ZJMZ" alias="专家门诊" length="1" type="int" defaultValue="0" not-null="1">
		<dic id="phis.dictionary.confirm" autoLoad="true"/>
	</item>
	<item id="PYDM" alias="拼音码" type="string" length="10" queryable="true" selected="true" target="KSMC" codeType="py"/>
	<item id="WBDM" alias="五笔码" display="2" type="string" length="10" target="KSMC" codeType="wb"/>
	<item id="JXDM" alias="角形码" display="2" type="string" length="10" target="KSMC" codeType="jx"/>
	<item id="QTDM" alias="其他码" display="2" type="string" length="10"/>
	<item id="GHF" alias="挂号费" length="8" type="double" minValue="0" maxValue="999999.99" precision="2" defaultValue="0" not-null="1"/>
	<item id="ZLF" alias="诊疗费" length="8" type="double" minValue="0" maxValue="999999.99" precision="2"  defaultValue="0" not-null="1"/>
	<item id="GHLB" alias="挂号类别" type="int" display="0" defaultValue="1"  length="4"/>
	<item id="JGID" alias="机构ID" display="0" type="string" length="18" not-null="1" defaultValue="%user.manageUnit.id"/>
	<item id="GHXE" alias="限额" display="0" length="4" type="int" not-null="1" defaultValue="0"/>
	<item id="YGRS" alias="已挂" display="0" length="4" type="int" not-null="1" defaultValue="0"/>
	<item id="YYRS" alias="预约" display="0" length="4" type="int" not-null="1" defaultValue="0"/>
	<item id="GHRQ" alias="挂号日期" display="0" type="date"/>
	<item id="TJPB" alias="体检判别" display="0" type="int" length="1" not-null="1" defaultValue="0"/>
	<item id="TJF" alias="体验费"  display="0" length="8" type="double" precision="2" not-null="1" defaultValue="0"/>
	<item id="MZLB" alias="门诊类别" width="100" type="long" display="0" fixed="true" length="18"/>
	<item id="JZXH" alias="就诊序号" display="0" type="int" length="4" defaultValue="0"/>
	<item id="JJRGHF" alias="节假日挂号费" display="0" type="double" length="8" precision="2" not-null="1" defaultValue="0"/>
	<item id="DDXX" alias="地点信息"  type="string" length="200" />
	<item id="DDDM" alias="地点代码"  type="string" length="18" display="0"/>
	<item id="ZLSFXM" alias="诊疗收费项目" type="long" length="18"/>
</entry>
