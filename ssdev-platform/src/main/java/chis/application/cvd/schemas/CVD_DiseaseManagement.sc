<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.cvd.schemas.CVD_DiseaseManagement" alias="心脑血管疾病管理" sort="a.createDate">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="hszt" alias="核实状态" type="int" length="1" display="1" defaultValue="1">
		<dic>
			<item key="1" text="未核实" />
			<item key="2" text="已核实" />
		</dic>
	</item>
	
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.phoneNumber" display="1" queryable="true"/>
	<item ref="c.regionCode" display="1" queryable="true"/> 	
	<item ref="c.manaUnitId" display="1" queryable="true"/> 	
	<item ref="c.manaDoctorId" display="1" queryable="true"/> 	
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	
	<item id="mzhm" alias="门诊号" type="string" length="32"  display="2" group="就诊信息" colspan="2"/>
	<item id="zyhm" alias="住院号" type="string" length="32"  display="2" group="就诊信息" colspan="2"/>
	<item id="jzjg" alias="就诊机构" type="string" length="20" width="180" display="3" group="就诊信息" colspan="4" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="jblx" alias="疾病类型" type="int" length="2"  display="2" group="就诊信息" colspan="2">
		<dic>
			<item key="1" text="脑血管" />
			<item key="2" text="心血管" />
		</dic>
	</item>
	<item id="bglx" alias="报告类型" type="int" length="2"  display="2" group="就诊信息" colspan="2">
		<dic>
			<item key="1" text="发病" />
			<item key="2" text="死亡" />
			<item key="3" text="发病死亡" />
		</dic>
	</item>
	<item id="scfbrq" alias="首次发病日期" type="date" defaultValue="%server.date.date" display="2" group="就诊信息" colspan="2"></item>
	<item id="fbcx" alias="发病序次" type="string" length="32" display="2" group="就诊信息" colspan="2"/>
	
	<item id="bcfbrqsj" alias="本次发病日期时间" type="datetime" defaultValue="%server.date.date" width="130" display="3" group="就诊信息" colspan="2"></item>
	<item id="swrqsj" alias="死亡日期时间" type="datetime" defaultValue="%server.date.date"  display="3" group="就诊信息" colspan="2"></item>
	<item id="jzrqsj" alias="就诊日期时间" type="datetime" defaultValue="%server.date.date" display="2" group="就诊信息" colspan="2"></item>
	<item id="qzrqsj" alias="确诊日期时间" type="datetime" defaultValue="%server.date.date" display="2" group="就诊信息" colspan="2"></item>
	
	<item id="ssy" alias="收缩压(mmHg)" type="int" length="8"  display="2" group="就诊信息" colspan="2"/>
	<item id="szy" alias="舒张压(mmHg)" type="int" length="8"  display="2" group="就诊信息" colspan="2"/>
	
	<item id="icd" alias="诊断ICD10" type="string" length="20"  display="2" group="疾病信息" colspan="1"/>
	<item id="icdName" alias="诊断名称" type="string" length="80"  display="3" group="疾病信息" colspan="1"/>
	<item id="tia" alias="TIA发作" type="int" length="1"  display="2" group="疾病信息" colspan="2">
		<dic render="Radio" columns="2">
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="jbfl" alias="疾病分型" type="int" length="2"  display="2" group="疾病信息" colspan="4">
		<dic render="Radio" colWidth="120" columns="6">
			<item key="1" text="脑梗死（非腔梗）" />
			<item key="2" text="脑梗死（腔梗）" />
			<item key="3" text="脑出血" />
			<item key="4" text="蛛网膜下腔出血" />
			<item key="5" text="卒中不分型" />
			<item key="6" text="急性心肌梗塞" />
			<item key="7" text="脑出血" />
			<item key="8" text="冠心病死亡" />
			<item key="9" text="心脏性猝死" />
		</dic>
	</item>
	<item id="zdyj" alias="诊断依据" type="string" length="50"  display="2" group="疾病信息" colspan="4">
		<dic render="Checkbox" colWidth="90" columns="6">
			<item key="1" text="临床 " />
			<item key="2" text="心电图" />
			<item key="3" text="生化" />
			<item key="4" text="CT" />
			<item key="5" text="MRI" />
			<item key="6" text="尸检" />
			<item key="7" text="脑脊液" />
			<item key="8" text="其他" />
			<item key="9" text="心脏性猝死" />
		</dic>
	</item>
	<item id="bz" alias="备注" type="string" xtype="textarea" grow="true" height="40" length="100"  display="2" group="疾病信息" colspan="4"/>
	
	<item id="xxly" alias="信息来源" type="int" length="2"  display="2" group="报告信息" colspan="2">
		<dic>
			<item key="1" text="门诊" />
			<item key="2" text="住院" />
			<item key="3" text="生命统计" />
			<item key="4" text="漏报调查" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="bgys" alias="报告医师" type="string" length="20" queryable="true" defaultValue="%user.userId" display="2" group="报告信息" colspan="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="bgjg" alias="报告机构" type="string" length="32" width="320" defaultValue="%user.manageUnit.id" display="3" group="报告信息" colspan="2"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>

	<item id="lbbb" alias="漏报补报" type="int" length="1"  display="3" group="报告信息" colspan="2">
		<dic render="Radio" columns="2">
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="bgrq" alias="报告日期" type="date" defaultValue="%server.date.date"  display="1" group="报告信息" colspan="2"></item>
	<item id="lbyy" alias="漏报原因" type="int" length="2"  display="3" group="报告信息" colspan="2">
		<dic>
			<item key="1" text="医生漏报" />
			<item key="2" text="异地诊治" />
			<item key="3" text="失访" />
			<item key="4" text="不详" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="lbjg" alias="漏报医院" type="string" length="32"  display="3" group="报告信息" colspan="2"/>
	
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" defaultValue="%user.userId" fixed="true" display="2" group="报告信息" colspan="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="32" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" display="2" group="报告信息" colspan="2"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="date" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="2" group="报告信息" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="32"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="date" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "empiId" child = "empiId" />
		</relation>
	</relations>
</entry>
