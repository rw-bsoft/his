<?xml version="1.0" encoding="UTF-8"?>

<entry id="V_LIS_TESTRESULT" tableName="V_LIS_TESTRESULT" alias="检验结果">
	<item id="SAMPLENO" alias="样本编号" length="30" not-null="true" pkey="true" type="string" display="2" />
	<item id="TESTID" alias="检验项目编号" length="30" not-null="true" pkey="true" type="string" display="2" />
	<item id="PATIENTID" alias="病人编号" type="string" display="2" />
	<item id="VISITID" alias="流水号" type="string" display="2" />
	<item id="CHINESENAME" alias="项目名称" type="string" width="150" />
	<item id="TESTRESULT" alias="检验结果" type="string"/>
	<item id="HINT" alias="" type="string" width="20"/>
	<item id="UNIT" alias="单位" type="string"/>
	<item id="CHECKTIME" alias="报告日期" type="date"  width="150"/>

</entry>