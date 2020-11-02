<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_DiseaseVerification" alias="心脑血管病例初访核实">
	<item id="recordId" alias="记录序号" type="string" length="16"   generator="assigned" pkey="true" hidden="true" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>

	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
	<item ref="b.phoneNumber" display="1" queryable="true"/>
	<item ref="c.regionCode" display="1" queryable="true"/> 	
	<item ref="c.manaUnitId" display="1" queryable="true"/> 	
	<item ref="c.manaDoctorId" display="1" queryable="true"/> 	
	
	<item id="precordId" alias="precordId" type="string" length="16"   display="0"/>
	<item id="empiId" alias="empiId" type="string" length="32"   display="0"/>
	
	<item id="dcys" alias="调查医师" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="2" group="核实情况" colspan="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" />
	</item>
	
	<item id="dcjg" alias="调查机构" type="string" length="32" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" display="2" group="核实情况" colspan="2"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
	</item>
	<item id="sf" alias="失访" type="string" length="1"   display="2" group="核实情况" colspan="2">
		<dic render="Radio" columns="2">
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>	
	<item id="fbdcrq" alias="发病调查日期" type="date" defaultValue="%server.date.date" display="2" group="核实情况" colspan="2"></item>
	<item id="swdcrq" alias="死亡调查日期" type="date" defaultValue="%server.date.date" display="2" group="核实情况" colspan="2"></item>

	<item id="fwsbyy" alias="访问失败原因" type="string" length="1"  display="2" group="核实情况" colspan="2">
		<dic>
			<item key="1" text="拒访" />
			<item key="2" text="多次未访到" />
			<item key="3" text="信息错误" />
			<item key="4" text="其他" />
		</dic>
	</item>	
	<item id="sfyyOther" alias="其他失访原因" type="string" length="100"  display="2" group="核实情况" colspan="2"/>


	<item id="ngs" alias="脑梗死" type="string" length="1"   display="2" group="疾病史" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="ncx" alias="脑出血" type="string" length="1"   display="2" group="疾病史" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="zzbfx" alias="卒中不分型" type="string" length="1"   display="2" group="疾病史" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="qxxngs" alias="腔隙性脑梗死" type="string" length="1"   display="2" group="疾病史" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="zwmxqcx" alias="蛛网膜下腔出血" type="string" length="1"   display="2" group="疾病史" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="xjgs" alias="心机梗死" type="string" length="1"   display="2" group="疾病史" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	
	<item id="swdd" alias="死亡地点" type="string" length="2"   display="2" group="死亡核实信息" colspan="2">
		<dic>
			<item key="1" text="医院病房" />
			<item key="2" text="急诊室" />
			<item key="3" text="家中" />
			<item key="4" text="外地" />
			<item key="5" text="家庭病床" />
			<item key="6" text="敬老院" />
			<item key="7" text="来院已死" />
			<item key="8" text="其他" />
		</dic>
	</item>
	<item id="swddOther" alias="其他地点" type="string" length="100"   display="2" group="死亡核实信息" colspan="2"/>
	
	<item id="zjsy" alias="直接死因" type="string" length="30"   display="2" group="死亡核实信息" colspan="4">
		<dic render="Checkbox" columns="8" colWidth="50">
			<item key="1" text="卒中" />
			<item key="2" text="脑疝" />
			<item key="3" text="心脏疾病" />
			<item key="4" text="肺部疾病" />
			<item key="5" text="褥疮感染" />
			<item key="6" text="全身衰竭" />
			<item key="7" text="不详" />
			<item key="8" text="其他" />
		</dic>
	</item>	
	<item id="zjsyOther" alias="其他死因" type="string" length="32"   display="2" group="死亡核实信息" colspan="4"/>
	<item id="ccfbhswyy" alias="此次发病或死亡的诱因" type="string" length="30"   display="2" group="死亡核实信息" colspan="4">
		<dic render="Checkbox" columns="10" colWidth="50">
			<item key="1" text="情绪波动" />
			<item key="2" text="饮酒" />
			<item key="3" text="吸烟" />
			<item key="4" text="饱餐" />
			<item key="5" text="劳累" />
			<item key="6" text="运动不当" />
			<item key="7" text="寒冷" />
			<item key="8" text="便秘" />
			<item key="9" text="不详" />
			<item key="10" text="其他" />
		</dic>
	</item>	
	<item id="ccfbhswyyOther" alias="其他诱因" type="string" length="32"   display="2" group="死亡核实信息" colspan="4"/>
	
	<item id="fzhchsj" alias="发作后存活时间" type="date" defaultValue="%server.date.date"   display="2" group="死亡核实信息" colspan="2"></item>
	<item id="zlly" alias="资料来源" type="string" length="1"   display="2" group="死亡核实信息" colspan="2">
		<dic>
			<item key="1" text="病史" />
			<item key="2" text="病人" />
			<item key="3" text="家属" />
			<item key="4" text="其他" />
		</dic>
	</item>	
	<item id="zllyOther" alias="资料来源其他" type="string" length="32"   display="2" group="死亡核实信息" colspan="2"/>	

	<item id="gxy" alias="高血压" type="string" length="1"   display="2" group="疾病危险因素--慢性病因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="tnb" alias="糖尿病" type="string" length="1"   display="2" group="疾病危险因素--慢性病因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="gzxz" alias="高脂血症" type="string" length="1"   display="2" group="疾病危险因素--慢性病因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="tia" alias="TIA" type="string" length="1"   display="2" group="疾病危险因素--慢性病因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="gxb" alias="冠心病" type="string" length="1"   display="2" group="疾病危险因素--慢性病因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="fc" alias="房颤" type="string" length="1"   display="2" group="疾病危险因素--慢性病因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="jdmbkhxz" alias="颈动脉斑块或狭窄" type="string" length="1"   display="2" group="疾病危险因素--慢性病因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="xzbOther" alias="其他心脏病" type="string" length="32"   display="2" group="疾病危险因素--慢性病因素" colspan="4"/>
	
	<item id="sfczxy" alias="是否存在吸烟" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	
	
	<item id="mtksxynl" alias="每天开始吸烟的年龄" type="string" length="3"   display="2" group="疾病危险因素--行为及其他因素" colspan="2"/>
	<item id="rjxyzs" alias="日均吸烟支数" type="string" length="3"   display="2" group="疾病危险因素--行为及其他因素" colspan="2"/>
	
	<item id="fpcz" alias="肥胖或超重(BMI≥24)" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="bmi" alias="BMI" type="string" length="32"   display="2" group="疾病危险因素--行为及其他因素" colspan="4"/>
	<item id="qfyd" alias="缺乏运动" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="yjs" alias="饮酒史" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="ryjl" alias="日饮酒量（相当于白酒）" type="string" length="32"   display="2" group="疾病危险因素--行为及其他因素" colspan="4" labelWidth="140"/>
	
	<item id="yjzl" alias="饮酒种类" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic>
			<item key="11" text="白酒（≥42度）" />
			<item key="12" text="白酒（＜42度）" />
			<item key="3" text="啤酒" />
			<item key="3" text="红酒" />
			<item key="4" text="黄酒" />
			<item key="9" text="其他" />
		</dic>
	</item>	
	
	<item id="glyj" alias="过量饮酒" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>	
	<item id="zzjzs" alias="卒中家族史" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>			
	<item id="gxyjzs" alias="高血压家族史" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>			
	<item id="gxbjzs" alias="冠心病家族史" type="string" length="1"   display="2" group="疾病危险因素--行为及其他因素" colspan="4">
		<dic render="Radio" columns="4">
			<item key="1" text="有" />
			<item key="2" text="无" />
			<item key="3" text="不详" />
		</dic>
	</item>			
	<item id="wxysOther" alias="其他行为危险因素" type="string" length="100"   display="2" group="疾病危险因素--行为及其他因素" colspan="4"/>
	
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="2" group="报告信息" colspan="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" display="2" group="报告信息" colspan="4"> 
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="2" group="报告信息" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
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
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
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
