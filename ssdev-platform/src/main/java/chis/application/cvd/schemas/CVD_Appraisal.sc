<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cvd.schemas.CVD_Appraisal" alias="评价结果表">
	<item id="recordId" alias="评价序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="inquireId" alias="档案编号" type="string" length="30" display="0" />
	<item id="phrId" alias="档案编号" type="string" length="30" display="0" />
	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />
	<item id="riskPrediction" alias="危险度预测" type="string" length="2000"
		xtype="textarea" colspan="2" />
	
	<item id="riskAssessment" alias="危险度评价" type="string" length="200"
		xtype="textarea" colspan="2" />
	<item id="riskPredictionResult" alias="危险度预测值" type="string"
		length="1" colspan="2" hidden="true">
		<dic id = "chis.dictionary.riskPredictionResult">
			
		</dic>
	</item>
	<item id="otherRisk" alias="危险度的因素" type="string" length="200"
		colspan="2" hidden="true">
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
	<item id="lifeStyle" alias="生活方式干预建议" type="string"
		length="524288000" display="2" xtype="htmleditor" colspan="2" />
	<item id="drugs" alias="药物干预建议" type="string" length="524288000"
		display="2" xtype="htmleditor" colspan="2" />
	<item id="modified" alias="是否修改过" type="string" length="1" display='0' >
		<dic render="LovCombo">
			<item key="0" text="未被修改" />
			<item key="1" text="已修改" />
		</dic>
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
