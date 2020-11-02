<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_AssessRegister" alias="心血管危险因素评估登记表"
	sort="inputDate">
	<item id="inquireId" alias="登记序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" display="0" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		display="0" />
	<item id="homeAddress" alias="家庭地址" type="string" length="50"
		colspan="2" display="2" />
	<item id="smoke" alias="是否吸烟" type="string" length="1" not-null="1"
		display="2">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="height" alias="身高(cm)" type="double" length="6"
		precision="2" enableKeyEvents="true" not-null="1" display="2" />
	<item id="weight" alias="体重(kg)" type="double" length="6"
		precision="2" enableKeyEvents="true" not-null="1" display="2" />
	<item id="waistLine" alias="腰围(cm)" type="double" length="5"
		precision="2" enableKeyEvents="true" not-null="1" display="2" />
	<item id="hipLine" alias="臀围(cm)" type="double" length="5"
		precision="2" enableKeyEvents="true" not-null="1" display="2" />
	<item id="tc" alias="总胆固醇(mmol/L)" length="8" precision="2" type="double"
		enableKeyEvents="true" display="2" />
	<item id="ldl" alias="低密度脂蛋白" length="8" precision="2" type="double"
		display="2" />
	<item id="hdl" alias="高密度脂蛋白" length="8" precision="2" type="double"
		enableKeyEvents="true" display="2" />
	<item id="tg" alias="甘油三酯" length="8" precision="2" display="2"
		type="double" />
	<item id="diabetes" alias="是否糖尿病" type="string" length="1"
		not-null="1" display="2">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="fbs" alias="空腹血糖(mmol/L)" type="double" length="8"
		precision="2" display="2" not-null="1" />
	<item id="pbs" alias="餐后血糖(mmol/L)" type="double" length="8"
		precision="2" display="2" />
	<item id="rbs" alias="随机血糖(mmol/L)" type="double" length="8"
		precision="2" display="2" />
	<item id="hypertension" alias="是否有高血压" type="string" length="1"
		display="2">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="constriction" alias="收缩压(mmHg)" type="int" not-null="1"
		display="2" />
	<item id="diastolic" alias="舒张压(mmHg)" type="int" not-null="1"
		display="2" />
	<item id="hypertensionTreat" alias="是否高血压治疗" type="string" length="1"
		not-null="1" display="2">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="cardiovascular" alias="心血管疾病" type="string" length="128"
		colspan="4" display="2">
		<dic id="chis.dictionary.cardiovascular" render="LovCombo" />
	</item>
	<item id="riskiness" alias="危险因素" type="string" length="128"
		colspan="4" display="2">
		<dic id="chis.dictionary.riskiness" render="LovCombo"></dic>
	</item>
	<item id="bpIncreased" alias="血压持续升高" type="string" length="1"
		colspan="2" display="2">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="kidney" alias="肾部疾病" type="string" length="1" colspan="2"
		display="2">
		<dic>
			<item key="1" text="无" />
			<item key="2" text="显性肾病" />
			<item key="3" text="肾衰" />
			<item key="4" text="肾损害" />
			<item key="5" text="其它明显的肾脏疾病" />
		</dic>
	</item>
	<item id="inputDate" alias="评测日期" type="date" defaultValue="%server.date.date" width="180"
		not-null="true">
	</item>
	<item id="inputUnit" alias="建档机构" type="string" length="20" width="180"  display="2"
		update="false" colspan="2" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="建档人员" type="string" not-null="1"    display="2"
		length="20" update="false" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="answer" alias="测试结果" display="0" type="string" length="200">
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"  
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.prop.manaUnitId" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="timestamp"
		defaultValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="isDelete" alias="是否删除" display="0" type="string" length="200"
		defaultValue="2">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="isImport" alias="是否导入" display="0" type="string" length="200"
		defaultValue="2">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="isImported" alias="是否已经导入" display="0" type="string"
		length="200" defaultValue="1">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item ref="c.regionCode_text" alias="网格地址" display="0" />
</entry>
