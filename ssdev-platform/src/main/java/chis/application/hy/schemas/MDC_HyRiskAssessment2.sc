<?xml version="1.0" encoding="UTF-8"?>

<entry  alias="危险分层评估表1信息" >
	<item id="grbm" alias="个人编码" width="120" colspan="2" type="string" group="一、居民基本信息(调阅)" />
	
	<item id="wxdpgrq" alias="危险度评估日期" type="date" colspan="2" group="二、危险度评估信息"/>
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int" 
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" group="二、危险度评估信息"/>
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int"
		minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" group="二、危险度评估信息"/>
	<item id="riskLevel" alias="评估随访高血压分层" type="string" length="1"
		colspan="2" group="二、危险度评估信息">
		<dic id="chis.dictionary.riskLevel" render="Radio"/>
	</item>
	<item id="historyRiskLevel" alias="历史最高血压分级" type="string" length="1"
		colspan="2" group="二、危险度评估信息">
		<dic id="chis.dictionary.riskLevel" render="Radio"/>
	</item>
	<item id="historyRisk" alias="历史最高危险分层" colspan="2" type="string" length="1" group="二、危险度评估信息">
		<dic render="Radio">
			<item key="1" text="低危层" />
			<item key="2" text="中危层" />
			<item key="3" text="高危层" />
			<item key="4" text="很高危层" />
		</dic>
	</item>
	<item id="riskiness" alias="危险因素" type="string" defaultValue="0"
		length="64" colspan="2" display="2" group="二、危险度评估信息">
		<dic id="chis.dictionary.hyperRiskiness" render="LovCombo" />
	</item>
	<item id="targetHurt" alias="靶器官损害" type="string" defaultValue="0"
		length="64" colspan="2" group="二、危险度评估信息">
		<dic id="chis.dictionary.targetHurt"  render="LovCombo" />
	</item>
	<item id="complication" alias="并存的临床情况" type="string" length="64"
		defaultValue="0" colspan="2" group="二、危险度评估信息">
		<dic id="chis.dictionary.complication"  render="LovCombo" />
	</item>
</entry>
