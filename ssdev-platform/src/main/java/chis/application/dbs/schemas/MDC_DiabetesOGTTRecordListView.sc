<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesOGTTRecord" alias="糖尿病高危档案" sort="a.OGTTID desc">
	<item id="OGTTID" alias="管理编号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId"  alias="档案编号" type="string" length="30" width="165" hidden="true" display="0" fixed="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true" display="0" colspan="3" hidden="true"/>
	<item id="riskFactors" alias="危险因素" type="string" length="100"  colspan="3">
		<dic id="chis.dictionary.riskFactors" render="Checkbox" columnWidth="350" columns="8"/>
	</item>
	<item id="registerDate" alias="登记日期" type="date" defaultValue="%server.date.today" queryable="true">
	</item>
	<item id="registerUser" alias="登记人" type="string" length="20"  defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="registerUnit" alias="登记单位" type="string" length="20" width="165" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="superDiagnose" alias="上级医院诊断" xtype="checkbox" type="string" length="1">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="superDiagnoseText" alias="诊断内容" display="1" type="string" length="100">
		<dic id="chis.dictionary.dbsDiagnose"/>
	</item>
	<item id="nextScreenDate" alias="下次筛查时间" display="0" type="date" defaultValue="%server.date.today" queryable="true">
	</item>
	<item id="fbs1" alias="空腹血糖1(mmol\L)" type="double" length="6" enableKeyEvents="true" group="第一次"/>
	<item id="pbs1" alias="餐后血糖1(mmol\L)" type="double" length="6" enableKeyEvents="true" group="第一次"/>
	<item id="result1" alias="结果1" type="string" length="1" fixed="true" group="第一次">
		<dic id="chis.dictionary.OGTTResult"/>
	</item>
	<item id="checkUser1" alias="核实人1" type="string" length="20" queryable="true" group="第一次">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="checkDate1" alias="核实日期1" type="date" queryable="true" group="第一次">
	</item>
	<item id="clinicSymptom1" alias="有临床症状1" type="string" length="1" group="第一次">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="fbs2" alias="空腹血糖2(mmol\L)" type="double" length="6" enableKeyEvents="true" group="第二次"/>
	<item id="pbs2" alias="餐后血糖2(mmol\L)" type="double" length="6" enableKeyEvents="true" group="第二次"/>
	<item id="result2" alias="结果2" type="string" length="1" fixed="true" group="第二次">
		<dic id="chis.dictionary.OGTTResult"/>
	</item>
	<item id="checkUser2" alias="核实人2" type="string" length="20" queryable="true" group="第二次">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="checkDate2" alias="核实日期2" type="date" queryable="true" group="第二次">
	</item>
	<item id="clinicSymptom2" alias="有临床症状2" type="string" length="1" group="第二次">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="fbs3" alias="空腹血糖3(mmol\L)" type="double" length="6" enableKeyEvents="true" group="第三次"/>
	<item id="pbs3" alias="餐后血糖3(mmol\L)" type="double" length="6" enableKeyEvents="true" group="第三次"/>
	<item id="result3" alias="结果3" type="string" length="1" fixed="true" group="第三次">
		<dic id="chis.dictionary.OGTTResult"/>
	</item>
	<item id="checkUser3" alias="核实人3" type="string" length="20"  queryable="true" group="第三次">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="checkDate3" alias="核实日期3" type="date" queryable="true" group="第三次">
	</item>
	<item id="clinicSymptom3" alias="有临床症状3" type="string" length="1" group="第三次">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="dbsCreate" alias="糖尿病建档标志" type="string" length="1" display="1" defaultValue="n">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item ref="c.regionCode" display="1" queryable="true"/>
	<item ref="c.manaDoctorId" display="1" queryable="true"/>
	<item ref="c.manaUnitId" display="1" queryable="true"/>
	<item ref="c.status" display="1" alias="健康档案状态" hidden="false" queryable="true"/>
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false" fixed="true" width="165" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入人员" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId" display="1" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="date" update="false" fixed="true" defaultValue="%server.date.today" display="1" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"  defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId"/>
		</relation>
	</relations>
</entry>
