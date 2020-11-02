<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.thr.schemas.MDC_TumourHighRiskVisit" alias="肿瘤高危随访">
	<item id="visitId" alias="随访记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	
	<item ref="b.personName" display="1" queryable="true"/>
	<item ref="b.sexCode" display="1" queryable="true"/>
	<item ref="b.birthday" display="1" queryable="true"/>
	<item ref="b.idCard" display="1" queryable="true"/>
  	<item ref="c.regionCode" 	display="1" 	queryable="true"/> 
  	<item ref="c.signFlag" 	display="1" 	queryable="true"/> 
  	<item ref="d.businessType" display="0" queryable="true"/>
  	<item ref="d.planStatus" display="0" queryable="true"/>
	
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="phrId" alias="phrId" type="string" length="32" display="0"/>
	
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" colspan="2">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="fixGroup" alias="高危组别" type="string" length="1" not-null="1" colspan="2">
		<dic id="chis.dictionary.tumourManagerGroup"/>
	</item>
	<item id="visitDate" alias="本次随访日期" type="date" defaultValue="%server.date.date" not-null="1" colspan="2"/>
	<item id="nextDate" alias="下次预约日期" type="date" colspan="2"/>
	<item id="year" alias="管理年限" type="int" length="4" not-null="1" colspan="2"/>
	<item id="visitWay" alias="随访方式" type="string" length="2" not-null="1" colspan="2">
		<dic id="chis.dictionary.visitWay" />
	</item>
	<item id="visitEffect" alias="随访情况" type="string" length="2" not-null="1" defaultValue="1" colspan="4">
		<dic render="Radio" colWidth="100" columns="3">
			<item key="1" text="继续随访" />
			<item key="2" text="暂时失访" />
			<item key="3" text="拒访" />
		</dic>
	</item>
	
	<item id="gynecologySymptom" alias="妇科症状" type="string" length="20" group="妇科症状" colspan="8">
		<dic render="Checkbox" columnWidth="120" columns="8">
			<item key="02" text="阴道不规则出血"/>
			<item key="03" text="间断性腰疼"/>
			<item key="04" text="阴道流液"/>
			<item key="05" text="尿急以及肛门下坠"/>
			<item key="06" text="下腹疼"/>
			<item key="07" text="下肢浮肿"/>
			<item key="01" text="无"/>
		</dic>
	</item>
	<item id="irregularVaginalBleeding" alias="阴道不规则出血" type="string" length="100" group="妇科症状" colspan="2"/>
	<item id="intermittentBackache" alias="间断性腰疼" type="string" length="100" group="妇科症状" colspan="2"/>
	<item id="vaginalDischarge" alias="阴道流液" type="string" length="100" group="妇科症状" colspan="2"/>
	<item id="urgentUrinationAnusDrop" alias="尿急以及肛门下坠" type="string" length="100" group="妇科症状" colspan="2"/>
	<item id="lowerAbdomenPain" alias="下腹疼" type="string" length="100" group="妇科症状" colspan="2"/>
	<item id="edemaOfLowerLimbs" alias="下肢浮肿" type="string" length="100" group="妇科症状" colspan="2"/>
	
	<item id="bodySymptom" alias="全身症状" type="string" length="20" not-null="1"  colspan="8" group="全身症状">
		<dic render="Checkbox" columnWidth="100" columns="8">
			<item key="02" text="乏力 "/>
			<item key="03" text="食欲减退 "/>
			<item key="04" text="肝区疼痛 "/>
			<item key="05" text="消瘦 "/>
			<item key="06" text="发热 "/>
			<item key="01" text="无"/>
		</dic>
	</item>
	<item id="weak" alias="乏力" type="string" length="100" colspan="2" group="全身症状" />
	<item id="anorexia" alias="食欲减退" type="string" length="100" colspan="3" group="全身症状" />
	<item id="hepatalgia" alias="肝区疼痛" type="string" length="100" colspan="3" group="全身症状" />
	<item id="emaciation" alias="消瘦" type="string" length="100" colspan="2" group="全身症状" />
	<item id="fever" alias="发热" type="string" length="100" colspan="3" group="全身症状" />
	
	<item id="diagnoseHospital" alias="就诊医院" type="string" length="2" not-null="1" colspan="8" group="就诊医院">
		<dic render="Radio" colWidth="110" columns="6">
			<item key="01" text="本区一级医院"/>
			<item key="02" text="本区二级医院"/>
			<item key="03" text="外区一级医院"/>
			<item key="04" text="外区二级医院"/>
			<item key="05" text="外区三级医院"/>
			<item key="06" text="未就诊"/>
		</dic>
	</item>
	
	<item id="superviseMyself" alias="无症状自主监测" type="string" length="1" not-null="1" colspan="2" group="无症状自主监测">
		<dic id="chis.dictionary.haveOrNot" render="Radio" colWidth="50" columns="2"/>
	</item>
	<item id="checkResult" alias="检查结果" type="string" length="200" colspan="5" group="无症状自主监测"/>
	
	<item id="familyHistory" alias="家族史" type="string" length="1" not-null="1" defaultValue="3" colspan="3" group="危险因素情况">
		<dic render="Radio" colWidth="60" columns="3">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="3" text="不详"/>
		</dic>
	</item>
	<item id="part" alias="部位" type="string" length="20" colspan="5"  group="危险因素情况"/> 
	<item id="relationship" alias="与患者的关系" type="string" length="20" colspan="4"  group="危险因素情况">
		<dic render="Checkbox" columnWidth="80" columns="4">
			<item key="01" text="外祖父母"/>
			<item key="02" text="父母"/>
			<item key="03" text="兄妹"/>
			<item key="04" text="子女"/>
		</dic>
	</item>
	<item id="relationRemark" alias="备注" type="string" length="200" colspan="4"  group="危险因素情况"/>
	<item id="height" alias="身高(cm)" type="int" length="3" not-null="1" colspan="2"  group="危险因素情况"/>
	<item id="weight" alias="体重(kg)" type="int" length="3" not-null="1" colspan="2"  group="危险因素情况"/>
	<item id="BMI" alias="身体指数" type="double" length="6" precision="2" colspan="2" fixed="true"  group="危险因素情况"/>
	<item id="waistLine" alias="腰围(cm)" type="int" length="3" not-null="1" colspan="2" group="危险因素情况"/>
	<item id="smoke" alias="吸烟" type="string" length="1" not-null="1" colspan="4"  group="危险因素情况">
		<dic render="Radio" colWidth="80" columns="4">
			<item key="1" text="≥20支/日"/>
			<item key="2" text="≤20支/日"/>
			<item key="3" text="戒烟"/>
			<item key="4" text="不吸"/>
		</dic>
	</item>
	<item id="bakeryProduct" alias="烤制品" type="string" length="1" not-null="1" colspan="4" group="危险因素情况">
		<dic render="Radio" colWidth="90" columns="4">
			<item key="1" text="1-2次/周"/>
			<item key="2" text="3-4次/周"/>
			<item key="3" text="5-6次/周"/>
			<item key="4" text="不食"/>
		</dic>
	</item>
	<item id="drink" alias="饮酒" type="string" length="1" not-null="1" colspan="5"  group="危险因素情况">
		<dic render="Radio" colWidth="80" columns="5">
			<item key="1" text="≥5次/周"/>
			<item key="2" text="3-4次/周"/>
			<item key="3" text="≤2次/周"/>
			<item key="4" text="戒酒"/>
			<item key="5" text="不饮"/>
		</dic>
	</item>
	<item id="moodSwing" alias="较强情绪波动" type="string" length="1" not-null="1" colspan="3" group="危险因素情况">
		<dic id="chis.dictionary.yesOrNo" render="Radio" colWidth="30" columns="3"/>
	</item>
	<item id="takeExerciseCase" alias="锻炼情况" type="string" length="1" not-null="1" colspan="8"  group="危险因素情况">
		<dic render="Radio" colWidth="190" columns="4">
			<item key="1" text="不太活动(看电视、读报纸)"/>
			<item key="2" text="轻度活动(种花或家务等)"/>
			<item key="3" text="中度活动(慢跑、跳舞、羽毛球)"/>
			<item key="4" text="重度活动(举杠铃等)"/>
		</dic>
	</item>
	
	<item id="visitProposal" alias="随访建议" type="String" length="20" not-null="1" colspan="8" group="随访建议">
		<dic render="Checkbox" columnWidth="200" columns="6">
			<item key="1" text="控制体重"/>
			<item key="2" text="合理膳食"/>
			<item key="3" text="适量运动"/>
			<item key="4" text="戒烟或控制吸烟数量"/>
			<item key="5" text="调节情绪"/>
			<item key="6" text="定期监测"/>
		</dic>
	</item>
	<item id="visitReferral" alias="随访转诊" type="string" length="1" not-null="1" defaultValue="3" colspan="3"  group="随访建议">
		<dic render="Radio" colWidth="60" columns="3">
			<item key="1" text="常规组"/>
			<item key="2" text="高危组"/>
			<item key="3" text="未转"/>
		</dic>
	</item>
	<item id="offerOther" alias="其他" type="string" length="200" colspan="3" group="随访建议"/>
	<item id="visitDoctor" alias="随访医生" type="string" length="20" defaultValue="%user.userId" not-null="1" colspan="2" group="随访建议">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
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
		<set type="exp">['$','%server.date.date']</set>
	</item>
	<relations>
		<relation type="parent" entryName="gp.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="gp.application.hr.schemas.EHR_HealthRecord">
			<join parent = "phrId" child = "phrId" />
		</relation>
		<relation type="children" entryName="gp.application.pub.schemas.PUB_VisitPlan">
			<join parent = "empiId" child = "empiId" />
		</relation>
	</relations>
</entry>
