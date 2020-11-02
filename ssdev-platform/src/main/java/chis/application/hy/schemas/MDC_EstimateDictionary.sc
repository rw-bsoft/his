<?xml version="1.0" encoding="UTF-8"?>

<entry alias="高血压评估字典" sort="riskiness">
	<item id= "dicId"  alias="字典编号" type="string" length="2" not-null="1" generator="assigned" pkey="true" hidden="true"/>
	<item id="riskiness" alias="评估影响因素" type="string" length="1" width="220" fixed="true">
		<dic>
			<item key="1" text="无其他危险因素" ></item>
			<item key="2" text="有1到2个危险因素"></item>
			<item key="3" text="有≥3个危险因素或靶器官损害或糖尿病"></item>
			<item key="4" text="有并存临床情况"></item>
		</dic>
	</item>
	<item id="HL1" alias="一级血压&lt;br/&gt;(SBP140～159或DBP90～99)" type="string" length="2"  width="180">
		<dic>
			<item key="1" text="低危"/>
			<item key="2" text="中危"/>
			<item key="3" text="高危"/>
			<item key="4" text="很高危"/>
		</dic>
	</item>
	<item id="HL2" alias="二级血压&lt;br/&gt;(SBP160～179或DBP100～109)" type="string" length="2"  width="180">
		<dic>
			<item key="1" text="低危"/>
			<item key="2" text="中危"/>
			<item key="3" text="高危"/>
			<item key="4" text="很高危"/>
		</dic>
	</item>
	<item id="HL3" alias="三级血压&lt;br/&gt;(SBP≥180或  DBP≥110)" type="string" length="2" width="140">
		<dic>
			<item key="1" text="低危"/>
			<item key="2" text="中危"/>
			<item key="3" text="高危"/>
			<item key="4" text="很高危"/>
		</dic>
	</item>
	<item id="dicType" alias="字典类别" type="string" length="1" hidden="true">
		<dic>
			<item key="1" text="管理分组" ></item>
			<item key="2" text="危险分层"></item>
		</dic>
	</item>
</entry>
