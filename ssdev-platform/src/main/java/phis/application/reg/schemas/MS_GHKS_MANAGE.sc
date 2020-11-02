<?xml version="1.0" encoding="UTF-8"?>

<entry tableName="MS_GHKS" alias="挂号科室">
	<item id="KSDM" alias="科室代码" type="long" length="18" not-null="1" display="2" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" display="2" length="8" not-null="1"/>
	<item id="KSMC" alias="科室名称" type="string" length="40"/>
	<item id="GHLB" alias="挂号类别" display="2" type="string" length="4"/>
	<item id="PYDM" alias="拼音代码" display="2" type="string" length="6" queryable="true" selected="true"/>
	<item id="WBDM" alias="五笔代码" display="2" type="string" length="6" />
	<item id="JXDM" alias="角形代码" display="2" type="string" length="6" />
	<item id="QTDM" alias="其他代码" display="2" type="string" length="6" />
	<item id="GHF" alias="挂号费" length="8" type="double" width="60" precision="2" not-null="1"/>
	<item id="ZLF" alias="诊疗费" length="8" type="double" precision="2" width="60" not-null="1"/>
	<item id="ZJMZ" alias="专家门诊" type="string"  width="60" length="1" not-null="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="GHXE" alias="限额" length="4" type="int" width="50" not-null="1"/>
	<item id="YGRS" alias="已挂" length="4" type="int" width="50" not-null="1"/>
	<item id="YYRS" alias="预约" length="4" type="int" width="50" not-null="1"/>
	<item id="GHRQ" alias="挂号日期" display="2" type="date"/>
	<item id="MZKS" alias="门诊科室" display="2" type="string" length="18"/>
	<item id="TJPB" alias="体检判别" display="2" type="string" length="1" not-null="1"/>
	<item id="TJF" alias="体验费"  display="2" type="string" length="8" precision="2" not-null="1"/>
	<item id="MZLB" alias="门诊类别" display="2" type="string" length="18" not-null="1"/>
	<item id="JZXH" alias="就诊序号" display="2" type="string" length="4"/>
	<item id="JJRGHF" alias="节假日挂号费" display="2" type="string" length="8" precision="2" not-null="1"/>
	<item id="GHRQ" alias="挂号日期" display="0" type="string"/>
	<item id="ZLSFXM" alias="诊疗收费项目" type="long" length="18"/>
</entry>
