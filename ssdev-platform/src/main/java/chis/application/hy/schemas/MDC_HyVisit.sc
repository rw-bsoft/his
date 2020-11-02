<?xml version="1.0" encoding="UTF-8"?>

<entry  alias="高血压随访" sort="a.empiId">
	<item id="visitDate" alias="随访日期" not-null="true" type="date"
		defaultValue="%server.date.today" enableKeyEvents="true"
		validationEvent="false" queryable="true" />
	<item id="visitEffect" alias="转归" type="string" not-null="true" defaultValue="1" tag="radioGroup"
		length="1">
		<dic>
			<item key="1" text="继续随访" />
			<item key="2" text="暂时失访" />
			<item key="9" text="终止管理" />
		</dic>
	</item>
	<item id="groupCode" alias="管理组别" width="25" type="string">
	    <dic>
	      <item key="01" text="一组"/>
	      <item key="02" text="二组"/>
	      <item key="03" text="三组"/>
	      <item key="99" text="一般管理"/>
	    </dic>
    </item>
    <item id="riskLevel" alias="危险分层" type="string" length="1" tag="radioGroup" display="2">
		<dic id="chis.dictionary.riskLevel"/>
	</item>
    <item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int" tag="text"
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int" tag="text"
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="height" alias="身高(cm)" type="double" length="5" precision="2" minValue="0" tag="text"
		maxValue="500" enableKeyEvents="true" validationEvent="false" />
	<item id="weight" alias="体重(kg)" type="double" length="5" precision="2" minValue="0" tag="text"
		maxValue="500" enableKeyEvents="true" validationEvent="false" />	
	<item id="wy" alias="腰围(cm)" type="double" length="5" precision="2" minValue="0" tag="text"
		maxValue="500" enableKeyEvents="true" validationEvent="false" />
	<item id="sfxy" alias="是否吸烟" type="string">
		<dic id="chis.dictionary.CV5101_24" onlySelectLeaf="true"/>
	</item>
	<item id="stdl" alias="身体锻炼" type="string">
		<dic id="chis.dictionary.CV5101_28" onlySelectLeaf="true"/>
	</item>
	<item id="zfxjzs" alias="早发现家族史" type="string" >
		<dic id="chis.dictionary.yesOrNo" onlySelectLeaf="true"/>
	</item>
	<item id="complication" alias="并发症" type="string" length="64" tag="checkBox"
		defaultValue="0" colspan="2">
		<dic id="chis.dictionary.complication"  render="LovCombo" />
	</item>
	<item id="currentSymptoms" alias="目前症状" type="string" length="64" tag="checkBox"
		colspan="2" defaultValue="9">
		<dic render="LovCombo">
			<item key="9" text="无症状" />
			<item key="1" text="头痛头晕" />
			<item key="2" text="恶心呕吐" />
			<item key="3" text="眼花耳鸣" />
			<item key="4" text="呼吸困难" />
			<item key="5" text="心悸胸闷" />
			<item key="6" text="鼻衄出血不止" />
			<item key="7" text="四肢发麻" />
			<item key="8" text="下肢水肿" />
			<item key="10" text="其他" />
		</dic>
	</item>
	<item id="drugNames1" alias="药物名称2" type="string" display="2" colspan="2" virtual="true">
		<dic id="chis.dictionary.drugDirectory" render="Tree"/>
	</item>
	<item id="everyDayTime1" alias="每日/次" type="string" tag="text" display="2" virtual="true"/>
	<item id="oneDosage1" alias="每次/mg" type="string" tag="text" display="2" virtual="true"/>
	<item id="nonMedicineWay" alias="非药物疗法" type="string" length="64"
		colspan="2">
		<dic render="LovCombo">
			<item key="9" text="无" />
			<item key="1" text="限盐" />
			<item key="2" text="减少吸烟量或者戒烟" />
			<item key="3" text="减少饮酒量或戒酒" />
			<item key="4" text="减少膳食脂肪" />
			<item key="5" text="减轻体重" />
			<item key="6" text="有规律体育运动" />
			<item key="7" text="放松情绪" />
			<item key="8" text="其他措施" />
		</dic>
	</item>
	<item id="healthRecipe" alias="健康处方建议" type="string" length="64"
		colspan="2">
		<dic render="LovCombo" />
	</item>
	<item id="acceptDegree" alias="患者接受程度" type="string" length="1"
		display="0">
		<dic>
			<item key="1" text="完全接受" />
			<item key="2" text="勉强接受" />
			<item key="3" text="不接受" />
		</dic>
	</item>
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="fhz" alias="复核者" type="string" length="20"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="sfyzzk" alias="是否有纸质卡" type="string" >
		<dic id="chis.dictionary.yesOrNo" onlySelectLeaf="true"/>
	</item>
	<item id="sffs" alias="随访方式" type="string">
		<dic id="chis.dictionary.visitWay" onlySelectLeaf="true"/>
	</item>
	<item id="sfsq" alias="随访社区" type="string"/>
	<item id="sfrq" alias="复核时间" type="datetime" xtype="datefield" not-null="1"/>
</entry>
