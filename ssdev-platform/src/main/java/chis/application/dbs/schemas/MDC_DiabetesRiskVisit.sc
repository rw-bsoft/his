<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.dbs.schemas.MDC_DiabetesRiskVisit" alias="糖尿病高危随访">
     <item ref="b.personName" display="1" queryable="true" />
	<item id="visitId" pkey="true" alias="随访标识" type="string" length="16"
		hidden="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="phrId" alias="档案编码" type="string" length="30" fixed="true"
		colspan="2" hidden="true" display="0" />
	<item id="empiId" alias="empiId" type="string" length="32" fixed="true"
		hidden="true" display="0" />
	<item id="riskId" alias="高危表id" type="string" length="16" hidden="true"
		fixed="true" />	
		
	<item id="visitDate" alias="随访日期" type="date" not-null="1"
		queryable="true" />
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitWay" alias="随访方式" type="string" length="1"
		not-null="true">
		<dic id="chis.dictionary.visitWay" />
	</item>

	<item id="fbs" alias="空腹血糖(mmol/L)" type="double" length="6" precision="2"  not-null="1" width="150"/>
	<item id="pbs" alias="餐后血糖(mmol/L)" type="double" length="6" precision="2" not-null="1" width="150"/>
	<item id="obeyDoctor" alias="遵医行为" type="string" length="1"
		canInput="true">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>
	<item id="riskiness" alias="高危因素" type="string" length="64"
		colspan="3" width="450">
		<dic render="LovCombo">
			<item key="01" text="年龄>=45岁者" />
			<item key="02" text="超重或者肥胖" />
			<item key="03" text="高危种族" />
			<item key="04" text="静坐生活方式" />
			<item key="05" text="糖耐量异常或合并空腹血糖受损" />
			<item key="06" text="有巨大儿生产史，妊娠糖尿病史" />
			<item key="07" text="2型糖尿病患者的一级亲属" />
			<item key="08" text="血脂异常，或正在接受调脂治疗" />
			<item key="09" text="高血压，或正在接受降压治疗" />
			<item key="10" text="心脑血管疾病患者" />
			<item key="11" text="有一过性糖皮质激素诱发糖尿病病史者" />
			<item key="12" text="BMI≥28kg/㎡的多囊卵巢综合症" />
			<item key="13" text="严重精神病和(或)长期接受抗抑郁症药物治疗的患者" />
		</dic>
	</item>
	<item id="healthTeach" alias="健康指导" type="string" length="20" colspan="3" >
		<dic render="LovCombo">
			<item key="01" text="合理膳食" />
			<item key="02" text="适量运动" />
			<item key="03" text="控制体重" />
			<item key="04" text="戒烟限酒" />
			<item key="05" text="缓解精神压力" />
		</dic>
	</item>
	
	<item id="inputUnit" alias="登记单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" display="1">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
			<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="登记日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" display="1">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="inputUser" alias="登记人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>

	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		display="0" width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="children" entryName="chis.application.mpi.schemas.MPI_DemographicInfo">
			<join parent="empiId" child="empiId" />
		</relation>
	</relations>
</entry>
