<?xml version="1.0" encoding="UTF-8"?>
<entry alias="定转组">
	<item id="dzzrq" alias="定转组日期" type="string"/>
	<item id="ssy" alias="收缩压" type="string" group="血压"/>
	<item id="szy" alias="舒张压" type="string" group="血压"/>
	<item id="jb" alias="级别" type="string" group="血压">
		<dic>
			<item key="1" text="一级"/>
			<item key="2" text="二级"/>
			<item key="3" text="三级"/>
		</dic>
	</item>
	<item id="wxys" alias="危险因素" colspan="2" type="string" group="危险因素">
		<dic id="chis.dictionary.hyperRiskiness" render="LovCombo"/>
	</item>
	<item id="bqgsh" alias="靶器官损害" colspan="2" type="string" group="危险因素">
		<dic id="chis.dictionary.targetHurt" render="LovCombo"/>
	</item>
	<item id="bqgsh" alias="并存的临床情况" colspan="2" type="string" group="危险因素">
		<dic id="chis.dictionary.complication" render="LovCombo"/>
	</item>
	<item id="fc" alias="分层" type="string">
		<dic>
			<item key="1" text="低危"/>
			<item key="2" text="中危"/>
			<item key="3" text="高危"/>
			<item key="4" text="很高危"/>
		</dic>
	</item>
	<item id="kzqk" alias="控制情况" type="string"/>
	<item id="fz" alias="分组" type="string">
		<dic>
			<item key="1" text="一组"/>
			<item key="2" text="二组"/>
			<item key="3" text="三组"/>
		</dic>
	</item>
	<item id="dzzqk" alias="定(转)组情况" colspan="2" type="string">
		<dic>
			<item key="1" text="初次定组"/>
			<item key="2" text="维持原组不变"/>
		</dic>
	</item>
	<item id="ysqm" alias="医生签名" colspan="2" type="string">
		<dic id="chis.dictionary.doctor" render="LovCombo"/>
	</item>
</entry>