<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_AssessRegister" alias="心血管危险因素评估登记表"
	sort="inputDate">

	<item id="inquireId" alias="登记序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" display="0"
		virtual="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" display="0"
		virtual="true" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		display="0" virtual="true" />
	<item id="personName" alias="姓名" type="string" length="20"
		display="2" virtual="true" />
	<item id="sexCode" alias="性别" type="string" length="1" display="2"
		virtual="true">
		<dic id="chis.dictionary.gender" />
	</item>
	<item id="birthday" alias="出生日期" type="date" display="2"
		virtual="true" />
	<item id="cardNo" alias="市民卡号" type="string" length="50" display="0"
		virtual="true" />
	<item id="idCard" alias="身份证号" type="string" length="30" display="2"
		virtual="true" />
	<item id="contact" alias="联系方式" type="string" length="30"
		display="0" virtual="true" />


	<item id="homeAddress" alias="家庭地址" type="string" length="50"
		colspan="0" display="0" fixed="true" virtual="true" />
	<item id="smoke" alias="是否吸烟" type="string" length="1" not-null="1"
		display="0" virtual="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="height" alias="身高(cm)" type="double" length="5"
		precision="2" enableKeyEvents="true" not-null="1" display="0"
		virtual="true" />
	<item id="weight" alias="体重(kg)" type="double" length="5"
		precision="2" enableKeyEvents="true" not-null="1" display="0"
		virtual="true" />
	<item id="waistLine" alias="腰围(cm)" type="double" length="5"
		precision="2" enableKeyEvents="true" not-null="1" display="0"
		virtual="true" />
	<item id="hipLine" alias="臀围(cm)" type="double" length="5"
		precision="2" enableKeyEvents="true" not-null="1" display="0"
		virtual="true" />
	<item id="tc" alias="总胆固醇" length="8" precision="2" type="double"
		enableKeyEvents="true" display="0" not-null="1" virtual="true" />
	<item id="ldl" alias="低密度脂蛋白" length="8" precision="2" type="double"
		display="0" virtual="true" />
	<item id="hdl" alias="高密度脂蛋白" length="8" precision="2" type="double"
		enableKeyEvents="true" not-null="1" display="0" virtual="true" />
	<item id="tg" alias="甘油三酯" length="8" precision="2" display="0"
		type="double" />
	<item id="diabetes" alias="是否糖尿病" type="string" length="1"
		not-null="1" display="0" virtual="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="fbs" alias="空腹血糖(mmol/L)" type="double" length="8"
		precision="2" display="0" not-null="1" virtual="true" />
	<item id="pbs" alias="餐后血糖(mmol/L)" type="double" length="8"
		precision="2" display="0" virtual="true" />
	<item id="rbs" alias="随机血糖(mmol/L)" type="double" length="8"
		precision="2" display="0" virtual="true" />
	<item id="hypertension" alias="是否有高血压" type="string" length="1"
		display="0" virtual="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="constriction" alias="收缩压(mmHg)" type="int" not-null="1"
		display="0" virtual="true" />
	<item id="diastolic" alias="舒张压(mmHg)" type="int" not-null="1"
		display="0" virtual="true" />
	<item id="hypertensionTreat" alias="是否高血压治疗" type="string"
		length="1" not-null="1" display="0" virtual="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="cardiovascular" alias="心血管疾病" type="string" length="128"
		colspan="4" display="0" virtual="true">
		<dic id="chis.dictionary.cardiovascular" render="LovCombo" virtual="true" />
	</item>
	<item id="riskiness" alias="危险因素" type="string" length="128"
		colspan="4" display="0" virtual="true">
		<dic id="chis.dictionary.riskiness" render="LovCombo"></dic>
	</item>
	<item id="bpIncreased" alias="血压持续升高" type="string" length="1"
		display="0" virtual="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="kidney" alias="肾部疾病" type="string" length="1" display="0"
		virtual="true">
		<dic>
			<item key="1" text="无" />
			<item key="2" text="显性肾病" />
			<item key="3" text="肾衰" />
			<item key="4" text="肾损害" />
			<item key="5" text="其它明显的肾脏疾病" />
		</dic>
	</item>
	<item id="riskAssessment" alias="危险度评价" type="string" length="200"
		xtype="textarea" virtual="true" display="0" />
	<item id="riskPredictionResult" alias="危险度预测值" type="string"
		length="1" display="2" virtual="true">
		<dic id="chis.dictionary.riskPredictionResult" />
	</item>
	<item id="beginDate" alias="评测开始日期" type="date" display="2"
		virtual="true" />
	<item id="endDate" alias="评测结束日期" type="date" display="2"
		virtual="true" />
	<item id="isLastExport" alias="只导出最后一次的记录" defaultValue="1"
		type="string" display="2" virtual="true">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="inputDate" alias="评测日期" type="date" display="0"
		virtual="true" />
	<item id="inputUnit" alias="建档机构" type="string" length="16"
		width="165" defaultValue="%user.prop.manaUnitId" virtual="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="false"
			parentKey="%user.prop.manaUnitId" />
	</item>
	<item id="inputUser" alias="建档人员" type="string" length="20"
		defaultValue="%user.userId" virtual="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.prop.manaUnitId" />
	</item>
	<item id="residenceAddress" alias="户籍地址" type="string" length="50"
		virtual="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10"
			filterMax="18" render="Tree" onlySelectLeaf="false" />
	</item>
	<item id="riskPrediction" alias="危险度预测" type="string" length="2000"
		xtype="textarea" virtual="true" display="0" />


	<item id="otherRisk" alias="危险度的因素" type="string" length="200"
		hidden="true" virtual="true" display="0">
		<dic render="LovCombo">
			<item key="01" text="过早的绝经;（早于40岁）" />
			<item key="02" text="靠近更高的年龄类别或收缩压类别" />
			<item key="03" text="肥胖（包括向心性肥胖）；（BMI≥28）" />
			<item key="04" text="静坐的生活方式；（连续静坐2小时或每天静坐超过5小时）" />
			<item key="05" text="有一级亲属过早的（男性＜55岁，女性＜65岁）发生CHD或卒中" />
			<item key="06" text="甘油三酯水平升高（＞2.0mmol/l或180mg/dl）" />
			<item key="07"
				text="HDL胆固醇水平降低（男性＜1mmol/l或40mg/dl，女性＜1.3mmol/l或50mg/dl）" />
			<item key="08"
				text="C反应蛋白、纤维蛋白原、同型半胱氨酸、载脂蛋白B或Lp(a)、空腹血糖升高或糖耐量低减" />
			<item key="09" text="微量白蛋白尿（5年内发生糖尿病的危险度增加5%）" />
			<item key="10" text="脉搏加快" />
			<item key="11" text="贫困" />
		</dic>
	</item>
	<item id="lifeStyle" alias="生活方式干预建议" type="text" length="524288000"
		xtype="htmleditor" virtual="true" display="0" />
	<item id="drugs" alias="药物干预建议" type="text" length="52428800"
		xtype="htmleditor" virtual="true" display="0" />
	<item id="answer" alias="测试结果" display="0" type="string"
		length="200">
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.prop.manaUnitId" />
        <set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="timestamp"
		defaultValue="%server.date.date" display="1">
        <set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
