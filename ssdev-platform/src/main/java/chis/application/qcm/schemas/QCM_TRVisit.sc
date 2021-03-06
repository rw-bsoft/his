<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.qcm.schemas.QCM_TRVisit" tableName="chis.application.tr.schemas.MDC_TumourHighRiskVisit" alias="肿瘤高危随访">
	<item id="visitId" alias="随访记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item ref="b.personName" display="1" queryable="true"/>  
	<item ref="b.sexCode" display="1" queryable="true"/>  
	<item ref="b.birthday" display="1" queryable="true"/>  
	<item ref="b.idCard" display="1" queryable="true"/>  
	<item ref="b.mobileNumber" display="1" queryable="true"/>  
	<item ref="c.regionCode" display="1" queryable="true"/>  
	
	<item id="highRiskType" alias="高危类别" type="string" length="2" not-null="1" colspan="2">
		<dic id="chis.dictionary.tumourHighRiskType"/>
	</item>
	<item id="fixGroup" alias="组别" type="string" length="1" not-null="1" fixed="true" defaultValue="1" colspan="2">
		<dic id="chis.dictionary.tumourManagerGroup"/>
	</item>
	<item id="visitDate" alias="随访日期" type="date" defaultValue="%server.date.date" not-null="1" colspan="2"/>
	<item id="visitWay" alias="随访方式" type="string" length="2" not-null="1" colspan="2">
		<dic id="chis.dictionary.visitWay" />
	</item>
	<item ref="c.manaUnitId" display="1" queryable="true" />
	<item id="visitEffect" alias="转归" type="string" length="2" not-null="1" defaultValue="1" colspan="4">
		<dic render="Radio" colWidth="90" columns="4">
			<item key="1" text="继续随访" />
			<item key="2" text="暂时失访" />
			<item key="3" text="确诊" />
			<item key="9" text="终止管理" />
		</dic>
	</item>
	<item id="visitDoctor" alias="随访医生" type="string" length="20" defaultValue="%user.userId" not-null="1" colspan="2" group="随访建议">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="100" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
	
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="THRID" alias="THRID" type="string" length="16" display="0"/>
	


	<item id="lastVisitDate" alias="上次随访日期" type="date" display="0"/>
	<item id="visitDate" alias="本次随访日期" type="date" defaultValue="%server.date.date" not-null="1" colspan="2" display="0"/>
	<item id="nextDate" alias="下次预约日期" type="date" colspan="2" display="0"/>
	<item id="year" alias="管理年限" type="int" length="4" not-null="1" defaultValue="%server.date.year" colspan="2" display="0">
		<dic id="chis.dictionary.years" />
	</item>


	<item id="lostVisitReason" alias="暂时失访原因" type="string" length="100" fixed="true"  display="0" colspan="2"/>
	<item id="terminatedReason" alias="终止管理原因" type="string" length="1" display="0" colspan="2">
		<dic>
			<item key="1" text="死亡" />
			<item key="2" text="迁出" />
			<item key="3" text="失访" />
			<item key="4" text="拒绝" />
		</dic>
	</item>
	<item id="deadDate" alias="死亡日期" type="date" fixed="true" display="0" maxValue="%server.date.date" colspan="2"/>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true"  length="100" display="0" colspan="2"/>
	
	<item id="gynecologySymptom" alias="妇科症状" type="string" length="20" group="妇科症状" colspan="8" display="0">
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
	<item id="irregularVaginalBleeding" alias="阴道不规则出血" type="string" length="100" group="妇科症状" colspan="2" display="0"/>
	<item id="intermittentBackache" alias="间断性腰疼" type="string" length="100" group="妇科症状" colspan="2" display="0"/>
	<item id="vaginalDischarge" alias="阴道流液" type="string" length="100" group="妇科症状" colspan="2" display="0"/>
	<item id="urgentUrinationAnusDrop" alias="尿急以及肛门下坠" type="string" length="100" group="妇科症状" colspan="2" display="0"/>
	<item id="lowerAbdomenPain" alias="下腹疼" type="string" length="100" group="妇科症状" colspan="2" display="0"/>
	<item id="edemaOfLowerLimbs" alias="下肢浮肿" type="string" length="100" group="妇科症状" colspan="2" display="0"/>
	
	<item id="anorectalSymptom" alias="肛肠症状" type="string" length="50" group="肛肠症状" colspan="8" display="0">
		<dic render="Checkbox" columnWidth="120" columns="8">
			<item key="02" text="便频"/>
			<item key="03" text="便秘"/>
			<item key="04" text="里急后重"/>
			<item key="05" text="腹泻便秘交替出现"/>
			<item key="06" text="大便鲜红色"/>
			<item key="07" text="果酱样便"/>
			<item key="08" text="粘液便"/>
			<item key="09" text="大便变细或变型"/>
			<item key="10" text="不明原因贫血"/>
			<item key="11" text="腹部固定部位疼痛"/>
			<item key="01" text="无"/>
		</dic>
	</item>
	<item id="frequent" alias="便频" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="constipation" alias="便秘" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="tenesmus" alias="里急后重" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="DCAA" alias="腹泻便秘交替出现" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="brightRedStool" alias="大便鲜红色" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="jamSamplesThen" alias="果酱样便" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="mucousStool" alias="粘液便" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="shitAttenuateOrVariant" alias="大便变细或变型" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="unKnownCausesAnemia" alias="不明原因贫血" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	<item id="abdominalFixedSitePain" alias="腹部固定部位疼痛" type="string" length="100" group="肛肠症状" colspan="2" display="0"/>
	
	<item id="respiratorySystemSymptom" alias="呼吸系统症状" type="string" length="50" group="呼吸系统症状" colspan="8" display="0">
		<dic render="Checkbox" columnWidth="120" columns="8">
			<item key="02" text="咳嗽"/>
			<item key="03" text="咳痰"/>
			<item key="04" text="痰中带血"/>
			<item key="05" text="胸痛"/>
			<item key="06" text="胸闷"/>
			<item key="01" text="无"/>
		</dic>
	</item>
	<item id="cough" alias="咳嗽" type="string" length="100" group="呼吸系统症状" colspan="2" display="0"/>
	<item id="expectoration" alias="咳痰" type="string" length="100" group="呼吸系统症状" colspan="2" display="0"/>
	<item id="bloodySputum" alias="痰中带血" type="string" length="100" group="呼吸系统症状" colspan="2" display="0"/>
	<item id="pectoralgia1" alias="胸痛" type="string" length="100" group="呼吸系统症状" colspan="2" display="0"/>
	<item id="chestCongestion" alias="胸闷" type="string" length="100" group="呼吸系统症状" colspan="2" display="0"/>
	
	<item id="symptomsOfChest" alias="胸部症状" type="string" length="50" group="胸部症状" colspan="8" display="0">
		<dic render="Checkbox" columnWidth="120" columns="8">
			<item key="02" text="胸痛"/>
			<item key="03" text="乳房肿块"/>
			<item key="04" text="乳房部位固定疼痛"/>
			<item key="05" text="乳头糜烂"/>
			<item key="06" text="乳头溢液"/>
			<item key="07" text="双侧乳房不对称"/>
			<item key="01" text="无"/>
		</dic>
	</item>
	<item id="pectoralgia2" alias="胸痛2" type="string" length="100" group="胸部症状" colspan="2" display="0"/>
	<item id="breastLump" alias="乳房肿块" type="string" length="100" group="胸部症状" colspan="2" display="0"/>
	<item id="breastPartFixedPain" alias="乳房部位固定疼痛" type="string" length="100" group="胸部症状" colspan="2" display="0"/>
	<item id="nippleErosion" alias="乳头糜烂" type="string" length="100" group="胸部症状" colspan="2" display="0"/>
	<item id="nippleDischarge" alias="乳头溢液" type="string" length="100" group="胸部症状" colspan="2" display="0"/>
	<item id="bilateralBreastAsymmetry" alias="双侧乳房不对称" type="string" length="100" group="胸部症状" colspan="2" display="0"/>
	
	<item id="stomachSymptoms" alias="胃部症状" type="string" length="50" group="胃部症状" colspan="8" display="0">
		<dic render="Checkbox" columnWidth="120" columns="8">
			<item key="02" text="嗳气"/>
			<item key="03" text="泛酸"/>
			<item key="04" text="上腹痛"/>
			<item key="05" text="呕血黑便"/>
			<item key="01" text="无"/>
		</dic>
	</item>
	<item id="belching" alias="嗳气" type="string" length="100" group="胃部症状" colspan="2" display="0"/>
	<item id="acidRegurgitation" alias="泛酸" type="string" length="100" group="胃部症状" colspan="2" display="0"/>
	<item id="epigastricPain" alias="上腹痛" type="string" length="100" group="胃部症状" colspan="2" display="0"/>
	<item id="haematemesisMelena" alias="呕血黑便" type="string" length="100" group="胃部症状" colspan="2" display="0"/>
	
	<item id="bodySymptom" alias="全身症状" type="string" length="20" not-null="1"  colspan="8" group="全身症状" display="0">
		<dic render="Checkbox" columnWidth="100" columns="8">
			<item key="02" text="乏力 "/>
			<item key="03" text="食欲减退 "/>
			<item key="04" text="肝区疼痛 "/>
			<item key="05" text="消瘦 "/>
			<item key="06" text="发热 "/>
			<item key="01" text="无"/>
		</dic>
	</item>
	<item id="weak" alias="乏力" type="string" length="100" colspan="2" group="全身症状" display="0"/>
	<item id="anorexia" alias="食欲减退" type="string" length="100" colspan="3" group="全身症状" display="0"/>
	<item id="hepatalgia" alias="肝区疼痛" type="string" length="100" colspan="3" group="全身症状" display="0"/>
	<item id="emaciation" alias="消瘦" type="string" length="100" colspan="2" group="全身症状" display="0"/>
	<item id="fever" alias="发热" type="string" length="100" colspan="3" group="全身症状" display="0"/>
	
	<item id="diagnoseHospital" alias="就诊医院" type="string" length="2" not-null="1" colspan="8" group="就诊医院" display="0">
		<dic render="Radio" colWidth="110" columns="6">
			<item key="01" text="本区一级医院"/>
			<item key="02" text="本区二级医院"/>
			<item key="03" text="外区一级医院"/>
			<item key="04" text="外区二级医院"/>
			<item key="05" text="外区三级医院"/>
			<item key="06" text="未就诊"/>
		</dic>
	</item>
	
	<item id="superviseMyself" alias="无症状自主监测" type="string" length="1" not-null="1" colspan="2" group="无症状自主监测" display="0">
		<dic id="chis.dictionary.haveOrNot" render="Radio" colWidth="50" columns="2"/>
	</item>
	<item id="checkResultIds" alias="检查结果ID集" type="string" length="2000" group="无症状自主监测" display="0"/>
	<item id="checkResult" alias="检查结果" type="string" xtype="lookupfieldex" length="500" colspan="5" group="无症状自主监测" display="0"/>
	<item id="visitNorm" alias="随访规范" type="string" length="1" defaultValue="0" group="无症状自主监测" display="0">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	
	
	<item id="familyHistory" alias="家族史" type="string" length="1" not-null="1" defaultValue="3" colspan="3" group="危险因素情况" display="0">
		<dic render="Radio" colWidth="60" columns="3">
			<item key="1" text="有"/>
			<item key="2" text="无"/>
			<item key="3" text="不详"/>
		</dic>
	</item>
	<item id="part" alias="部位" type="string" length="20" colspan="5"  group="危险因素情况" display="0"/> 
	<item id="relationship" alias="与患者的关系" type="string" length="20" colspan="4"  group="危险因素情况" display="0">
		<dic render="Checkbox" columnWidth="80" columns="4">
			<item key="01" text="外祖父母"/>
			<item key="02" text="父母"/>
			<item key="03" text="兄妹"/>
			<item key="04" text="子女"/>
		</dic>
	</item>
	<item id="relationRemark" alias="备注" type="string" length="200" colspan="4"  group="危险因素情况" display="0"/>
	<item id="height" alias="身高(cm)" type="int" length="3" not-null="1" colspan="2"  group="危险因素情况" display="0"/>
	<item id="weight" alias="体重(kg)" type="int" length="3" not-null="1" colspan="2"  group="危险因素情况" display="0"/>
	<item id="BMI" alias="身体指数" type="double" length="6" precision="2" colspan="2" fixed="true"  group="危险因素情况" display="0"/>
	<item id="waistLine" alias="腰围(cm)" type="int" length="3" not-null="1" colspan="2" group="危险因素情况" display="0"/>
	<item id="smoke" alias="吸烟" type="string" length="1" not-null="1" colspan="4"  group="危险因素情况" display="0">
		<dic render="Radio" colWidth="80" columns="4">
			<item key="1" text="≥20支/日"/>
			<item key="2" text="≤20支/日"/>
			<item key="3" text="戒烟"/>
			<item key="4" text="不吸"/>
		</dic>
	</item>
	<item id="bakeryProduct" alias="烤制品" type="string" length="1" not-null="1" colspan="4" group="危险因素情况" display="0">
		<dic render="Radio" colWidth="90" columns="4">
			<item key="1" text="1-2次/周"/>
			<item key="2" text="3-4次/周"/>
			<item key="3" text="5-6次/周"/>
			<item key="4" text="不食"/>
		</dic>
	</item>
	<item id="drink" alias="饮酒" type="string" length="1" not-null="1" colspan="5"  group="危险因素情况" display="0">
		<dic render="Radio" colWidth="80" columns="5">
			<item key="1" text="≥5次/周"/>
			<item key="2" text="3-4次/周"/>
			<item key="3" text="≤2次/周"/>
			<item key="4" text="戒酒"/>
			<item key="5" text="不饮"/>
		</dic>
	</item>
	<item id="moodSwing" alias="较强情绪波动" type="string" length="1" not-null="1" colspan="3" group="危险因素情况" display="0">
		<dic id="chis.dictionary.yesOrNo" render="Radio" colWidth="30" columns="3"/>
	</item>
	<item id="takeExerciseCase" alias="锻炼情况" type="string" length="1" not-null="1" colspan="8"  group="危险因素情况" display="0">
		<dic render="Radio" colWidth="190" columns="4">
			<item key="1" text="不太活动(看电视、读报纸)"/>
			<item key="2" text="轻度活动(种花或家务等)"/>
			<item key="3" text="中度活动(慢跑、跳舞、羽毛球)"/>
			<item key="4" text="重度活动(举杠铃等)"/>
		</dic>
	</item>
	
	<item id="visitProposal" alias="随访建议" type="String" length="20" not-null="1" colspan="8" group="随访建议" display="0">
		<dic render="Checkbox" columnWidth="200" columns="6">
			<item key="1" text="控制体重"/>
			<item key="2" text="合理膳食"/>
			<item key="3" text="适量运动"/>
			<item key="4" text="戒烟或控制吸烟数量"/>
			<item key="5" text="调节情绪"/>
			<item key="6" text="定期监测"/>
		</dic>
	</item>
	<item id="visitReferral" alias="随访分组" type="string" length="1" not-null="1" defaultValue="1" fixed="true" colspan="2"  group="随访建议" display="0">
		<dic render="Radio" colWidth="60" columns="2">
			<item key="1" text="常规组"/>
			<item key="2" text="高危组"/>
		</dic>
	</item>
	<item id="transferTreatment" alias="是否转诊" type="string" length="1" not-null="1" defaultValue="1" colspan="2" group="随访建议" display="0">
		<dic render="Radio" colWidth="60" columns="2">
			<item key="1" text="未转诊"/>
			<item key="2" text="转诊"/>
		</dic>
	</item>
	<item id="offerOther" alias="其他" type="string" length="200" colspan="4" group="随访建议" display="0"/>

	
	
	<item id="guideDate" alias="指导日期" type="datetime" queryable="true" virtual="true" xtype="datefield" colspan="3" group="健康教育" display="0"
		defaultValue="%server.date.today">
	</item>
	<item id="guideUser" alias="指导医生" type="string" queryable="true" virtual="true" length="20" width="150" colspan="5" defaultValue="%user.userId" group="健康教育" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
	</item>
	<item id="healthTeach" alias="指导建议" type="string" height="260" virtual="true" length="2000" width="200" colspan="8" xtype="textarea" group="健康教育" display="0">
	</item>
	
	



	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo"/>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent = "empiId" child = "empiId" />
		</relation>
	</relations>
</entry>