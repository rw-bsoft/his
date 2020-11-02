<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.dbs.schemas.MDC_DiabetesRiskAssessment"
	alias="糖尿病高危人群评估">
	<item id="recordId" alias="标识列" type="string" length="16"
		not-null="1" pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item ref="b.personName" display="1" queryable="true" />
	<item id="phrId" alias="个人健康档案号" type="string" length="30" hidden="true"
		fixed="true" />
	<item id="empiId" alias="empiid" type="string" length="32" hidden="true"
		fixed="true" />
	<item id="riskId" alias="高危表id" type="string" length="16" hidden="true"
		fixed="true" />
		
	<item id="estimateDate" alias="评估日期" type="date" 
		defaultValue="%server.date.today" update="false" not-null="true" maxValue="%server.date.today">
	</item>
	<item id="estimateUser" alias="评估人" type="string" length="20"
		defaultValue="%user.userId"  not-null="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="estimateUnit" alias="评估单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" fixed="true" not-null="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	
	<item id="estimateType" alias="评估类型" type="string" length="1"
		fixed="true" not-null="true">
		<dic>
			<item key="1" text="首次评估" />
			<item key="2" text="不定期评估" />
		</dic>
	</item>
	<item id="riskiness" alias="高危因素" type="string" length="64"
		colspan="4" width="450" not-null="true">
		<dic render="Checkbox" columnWidth="300" columns="2" >
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

	<item id="age" alias="年龄" type="int"  fixed="true"/>
	<item id="fbs" alias="空腹血糖(mmol/L)" type="double" length="6" precision="2"  width="150" not-null="1"/>
	<item id="pbs" alias="餐后血糖(mmol/L)" type="double" length="6" precision="2" width="150" not-null="1"/>
	<item id="height" alias="身高(cm)" type="double" length="6" display="2"
		minValue="100" maxValue="300" not-null="1" enableKeyEvents="true" />
	<item id="weight" alias="体重(kg)" type="double" length="6" display="2"
		minValue="30" maxValue="500" not-null="1" enableKeyEvents="true" />
	<item id="waistLine" alias="腰围(cm)" type="double" length="6"
		minValue="40" maxValue="200"  />
	<item id="bmi" alias="BMI" length="6" type="double" fixed="true" />
	<item id="tg" alias="TG(mmol/L)" type="double" length="10"  width="165"/>
	<item id="hdl" alias="HDL(mmol/L)" type="double" length="10"  width="165"  />

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
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20"
		defaultValue="%user.manageUnit.id" display="0">
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
