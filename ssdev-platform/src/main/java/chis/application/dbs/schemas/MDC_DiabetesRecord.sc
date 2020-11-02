<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesRecord" alias="糖尿病档案" sort="a.createDate desc" version="1332292315384" filename="D:\Program Files\eclipse3.6\workspace\BSCHIS22\WebRoot\WEB-INF\config\schema\mdc/MDC_DiabetesRecord.xml">
	<item id="phrId" pkey="true" alias="档案编号" type="string" length="30" width="165" queryable="true" fixed="true"/>
	<item ref="b.definePhrid" display="1" queryable="true"/>
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.address" display="1" />
	<item id="checkType" type="string" display="1"  virtual = "true"  alias="是否年检"/>
	<item ref="b.mobileNumber" display="1" queryable="true"/>
	<item ref="c.regionCode" display="1" queryable="true"/>
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true" colspan="3" hidden="true"/>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" not-null="1" update="false" queryable="true" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="165" fixed="true" queryable="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="familyHistroy" alias="糖尿病家族史" type="string" length="1" display="2" defaultValue="3" not-null="1">
		<dic>
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="3" text="不知道"/>
		</dic>
	</item>
	<item id="history" alias="家族史" type="string" length="100" virtual="true" display="2" fixed="true"/>
	
	<item id="riskFactors" alias="危险因素" type="string" length="50" display="2">
		<dic render="LovCombo">
			<item key="1" text="有糖调节受损史"/>
			<item key="2" text="年龄≥45岁"/>
			<item key="3" text="超重、肥胖（BMI≥24kg/m2），男性腰围≥90cm，女性腰围≥85cm"/>
			<item key="4" text="2型糖尿病患者的一级亲属"/>
			<item key="5" text="高危种族"/>
			<item key="6" text="有巨大儿（出生体重≥4kg）生产史，妊娠糖尿病史"/>
			<item key="7" text="高血压（血压≥140/90mmHg)，或正在接受降压治疗"/>
			<item key="8" text="血脂异常（HDL-C≤0.91mmol/L（≤35mg/dl）及TG≥2.22mmol/L（≥200mg/dl)，或正在接受调脂治疗"/>
			<item key="9" text="心脑血管疾病患者"/>
			<item key="10" text="有一过性糖皮质激素诱发糖尿病病史者"/>
			<item key="11" text="BMI≥28kg/m2的多囊卵巢综合征患者"/>
			<item key="12" text="严重精神病和（或）长期接受抗抑郁症药物治疗的患者"/>
			<item key="13" text="静坐生活方式"/>
		</dic>
	</item>
	
	<item id="diagnosisDate" alias="确诊年月" type="date" display="2" defaultValue="%server.date.today" enableKeyEvents="true" not-null="1" maxValue="%server.date.today"/>
	<item id="years" alias="病程年数" type="string" length="25" virtual="true" display="2" fixed="true"/>
	<item id="diagnosisUnit" alias="确诊单位" type="string" length="25" display="2"/>
	<item id="diabetesGroup" alias="糖尿病组别" type="string" length="2" display="0" fixed="true" queryable="true" defaultValue="01">
		<dic>
			<item key="01" text="一组"/>
			<item key="02" text="二组"/>
			<item key="03" text="三组"/>
		</dic>
	</item>
	<item id="fbs" alias="建卡空腹血糖" type="double" length="6" display="2" enableKeyEvents="true"/>
	<item id="pbs" alias="建卡餐后血糖" type="double" length="6" display="2" enableKeyEvents="true"/>
	<item id="unit" alias="血糖单位" type="string" not-null="1" display="2" defaultValue="1" fixed="true">
		<dic>
			<item key="1" text="mmol/L"/>
			<item key="2" text="mg/dl"/>
		</dic>
	</item>
	<item id="height" alias="身高(cm)" type="double" length="6" display="2" minValue="100" maxValue="300" not-null="1" enableKeyEvents="true"/>
	<item id="weight" alias="体重(kg)" type="double" length="6" display="2" minValue="30" maxValue="500" not-null="1" enableKeyEvents="true"/>
	<item id="bmi" alias="BMI" type="double" display="2" length="6" fixed="true" virtual="true"/>
	<item id="diabetesType" alias="病例种类" type="string" length="1" display="3" not-null="1">
		<dic id="chis.dictionary.diabetesType"/>
	</item>
	<item id="caseSource" alias="病例来源" type="string" length="1" display="0">
		<dic id="chis.dictionary.caseSource"/>
	</item>
	
	<item id="createUser" alias="建档人员" type="string" length="20" not-null="1" update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="建档机构" type="string" length="20" update="false" fixed="true" width="165" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" not-null="1"  defaultValue="%server.date.today" enableKeyEvents="true" validationEvent="false" maxValue="%server.date.today" queryable="true"/> 

	<item id="inputUser" alias="录入人" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id"  queryable="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>

	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
			<item key="2" text="（注销）待核实"/>
		</dic>
	</item>
	<item id="endCheck" alias="终止核实情况" type="string" defaultValue="1"
		length="1"  display="0">
		<dic>
			<item key="1" text="待核实"/>
			<item key="2" text="已核实"/>
		</dic>
	</item>
	<item id="visitEffect" alias="转归" type="string" defaultValue="1"
		length="1"  display="0">
		<dic>
			<item key="1" text="继续随访" />
			<item key="2" text="暂时失访" />
			<item key="9" text="终止管理" />
		</dic>
	</item>
	<item id="noVisitReason" alias="终止原因" type="string" length="1"
		fixed="true"  display="0"> 
		<dic> 
			<item key="1" text="死亡"/>  
			<item key="2" text="迁出"/>  
			<item key="3" text="失访"/> 
			<item key="4" text="拒绝"/> 
		</dic> 
	</item>  
	<item id="visitDate" alias="终止日期" not-null="true" type="date"
		defaultValue="%server.date.today" enableKeyEvents="true"
		validationEvent="false" queryable="true"  display="0"/>
	<item id="cancellationDate" alias="档案注销日期" type="date" display="2" fixed="true"/>
	<item id="cancellationReason" alias="档案注销原因" type="string" length="1" display="2" fixed="true">
		<dic>
			<item key="1" text="死亡"/>
			<item key="2" text="迁出"/>
			<item key="3" text="失访"/>
			<item key="4" text="拒绝"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="cancellationUser" alias="注销人" type="string" length="20" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true" length="100" hidden="true" colspan="3" anchor="100%"/>
	<item id="deadDate" alias="死亡日期" type="date" fixed="true" display="0" maxValue="%server.date.today"/>
	<item id="cancellationCheckUser" alias="注销复核者" type="string" length="20" hidden="true" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="cancellationCheckDate" alias="注销复核时间" type="date" hidden="true" fixed="true"/>
	<item id="cancellationCheckUnit" alias="注销复核单位" type="string" length="20" hidden="true" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="yearEstimate" alias="年度评估" type="date" hidden="true"/>
	<item id="nextVisitDate" alias="下次随访日期" type="date" display="1" width="100" queryable="true" />
	<item id="lastFixGroupDate" alias="最近评估日期" type="date" display="1" width="100" queryable="true" />
	<item id="planTypeCode" alias="计划类型代码" type="string" length="2" hidden="true"/>
	<item id="needDoVisit" alias="标识该档案是否有随访需要做" type="boolean" display="0" virtual="true"/>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"  defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="cancellationUnit" alias="注销单位" type="string" length="20" width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item ref="c.regionCode_text" alias="网格地址" display="0"/>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId"/>
		</relation>
	</relations>
</entry>
