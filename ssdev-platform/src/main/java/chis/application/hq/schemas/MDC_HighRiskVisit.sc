<?xml version="1.0" encoding="UTF-8"?>

<entry  alias="高危随访" sort="a.empiId">
	<item id="visitId" alias="随访标识" type="string" display="0" length="16" not-null="1" pkey="true" fixed="true" generator="assigned">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" not-null="1" display="0" />
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.phoneNumber" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" />
	<item id="empiId" alias="EMPIID" type="string" length="32" not-null="1" display="0" />
	<item id="visitDate" alias="随访日期" not-null="true" type="date" defaultValue="%server.date.today" enableKeyEvents="true"
		validationEvent="false" queryable="true" />
	<item id="visitWay" alias="随访方式" type="string" length="1" not-null="true">
		<dic id="chis.dictionary.visitWay" />
	</item>
	<item id="visitEffect" alias="转归" type="string" defaultValue="1" length="1">
		<dic>
			<item key="1" text="特征项数增加" />
			<item key="2" text="特征项数不变" />
			<item key="3" text="特征项数降低" />
			<item key="4" text="转为慢性病患者" />
			<item key="5" text="迁出" />
			<item key="6" text="死亡" />
			<item key="7" text="其它" />
		</dic>
	</item>
	<item id="noVisitReason" alias="原因" type="string" length="100" fixed="true" display="0"> 
		<dic> 
			<item key="1" text="死亡"/>  
			<item key="2" text="迁出"/>  
			<item key="3" text="失访"/> 
			<item key="4" text="拒绝"/> 
		</dic> 
	</item>   
	<item id="interventions" alias="干预措施" type="string" length="64" colspan="1" defaultValue="1">
		<dic render="LovCombo">
			<item key="1" text="定期监测血压" />
			<item key="2" text="减少吸烟量或戒烟" />
			<item key="3" text="定期监测血糖" />
			<item key="4" text="定期监测血脂" />
			<item key="5" text="减轻或控制体重" />
			<item key="6" text="合理膳食，减少钠盐摄入" />
			<item key="7" text="适量运动" />
			<item key="8" text="缓解心理压力" />
		</dic>
	</item>
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="isSmoke" alias="是否吸烟" type="string" length="1" >
		<dic id="chis.dictionary.oneyeszerono"/>
	</item>
	<item id="smokeCount" alias="日吸烟量" type="int" />
	<item id="fbs" alias="空腹血糖(mmol/L)" type="double" length="8" minValue="1" maxValue="20" />
	<item id="tc" alias="总胆固醇(mmol/L)" type="double" precision="2" length="8" minValue="1" maxValue="20" />
	<item id="waistLine" alias="腰围(cm)" type="double" length="40" minValue="40" maxValue="200" />
	<item id="nextDate" alias="下次预约时间" type="date" />
	<item id="visitDoctor" alias="随访医生" type="string" length="20" defaultValue="%user.userId" not-null="1" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitUnit" alias="随访机构" type="string" length="20" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.manageUnit.id" colspan="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入员" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false" 
		defaultValue="%server.date.today" queryable="true" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item ref="c.regionCode_text" alias="网格地址" display="0" />
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
