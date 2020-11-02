<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_DiseaseManagement" alias="心脑血管疾病管理" sort="createDate">
	<item id="recordId" alias="记录序号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>	
	<item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
	<item id="mzhm" alias="门诊号" type="string" length="32" not-null="1" display="2" formGroup="jzxx"/>
	<item id="zyhm" alias="住院号" type="string" length="32" not-null="1" display="2" formGroup="jzxx"/>
	<item id="jzjg" alias="就诊机构" type="string" length="32" not-null="1" display="2" formGroup="jzxx"/>
	
	<item id="jblx" alias="疾病类型" type="int" length="2" not-null="1" display="2" formGroup="jzxx">
		<dic>
			<item key="1" text="脑血管" />
			<item key="2" text="心血管" />
		</dic>
	</item>
	<item id="bglx" alias="报告类型" type="int" length="2" not-null="1" display="2" formGroup="jzxx">
		<dic>
			<item key="1" text="发病" />
			<item key="2" text="死亡" />
			<item key="3" text="发病死亡" />
		</dic>
	</item>
	<item id="fbcx" alias="发病次序" type="string" length="32" not-null="1" display="2" formGroup="jzxx"/>
	
	<item id="scfbrq" alias="首次发病日期" type="date" defaultValue="%server.date.date" not-null="1" display="2" formGroup="jzxx"></item>
	<item id="bcfbrqsj" alias="本次发病日期时间" type="date" defaultValue="%server.date.date" not-null="1" display="2" formGroup="jzxx"></item>
	<item id="swrqsj" alias="死亡日期时间" type="date" defaultValue="%server.date.date" not-null="1" display="2" formGroup="jzxx"></item>
	<item id="jzrqsj" alias="就诊日期时间" type="date" defaultValue="%server.date.date" not-null="1" display="2" formGroup="jzxx"></item>
	<item id="qzrqsj" alias="确诊日期时间" type="date" defaultValue="%server.date.date" not-null="1" display="2" formGroup="jzxx"></item>
	
	<item id="ssy" alias="收缩压" type="int" length="8" not-null="1" display="2" formGroup="jzxx"/>
	<item id="szy" alias="舒张压" type="int" length="8" not-null="1" display="2" formGroup="jzxx"/>
	
	
	<item id="icd" alias="诊断ICD10" type="string" length="20" not-null="1" display="2" formGroup="blxx"/>
	<item id="icdName" alias="诊断名称" type="string" length="80" not-null="1" display="2" formGroup="blxx"/>
	<item id="tia" alias="TIA发作" type="int" length="1" not-null="1" display="2" formGroup="blxx">
		<dic id="chis.dictionary.confirm"/>
	</item>
	<item id="jbfl" alias="疾病分型" type="int" length="2" not-null="1" display="2" formGroup="blxx" >
		<dic>
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
	<item id="zdyj" alias="诊断依据" type="int" length="2" not-null="1" display="2" colspan="2" formGroup="blxx"/>
	<item id="bz" alias="备注" type="string" xtype="textarea" height="20" length="100" not-null="1" display="2" colspan="3" formGroup="blxx"/>
	
	<item id="xxly" alias="信息来源" type="int" length="2" not-null="1" display="2" formGroup="bgxx"/>
	<item id="bgjg" alias="报告机构" type="string" length="32" not-null="1" display="2" formGroup="bgxx"/>
	<item id="bgys" alias="报告医师" type="string" length="10" not-null="1" display="2" formGroup="bgxx"/>
	<item id="bgrq" alias="报告日期" type="date" defaultValue="%server.date.date" not-null="3" display="2" formGroup="bgxx"></item>
	
	<item id="lbbb" alias="漏报补报" type="int" length="1" not-null="1" display="2" formGroup="bgxx"/>
	<item id="lbyy" alias="漏报原因" type="int" length="2" not-null="1" display="2" formGroup="bgxx"/>
	<item id="lbjg" alias="漏报医院" type="string" length="32" not-null="1" display="2" formGroup="bgxx"/>
	
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false"   defaultValue="%user.userId" fixed="true" display="2" formGroup="bgxx">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="2" formGroup="bgxx"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" widht="100"  update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="1" formGroup="bgxx">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="hszt" alias="核实状态" type="int" widht="70" length="1" not-null="1" display="1">
		<dic>
			<item key="1" text="" />
			<item key="2" text="已核实" />
		</dic>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="2" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "empiId" child = "empiId" />
		</relation>
	</relations>
</entry>
