<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionRisk"
	alias="高血压高危人群评估">
	<item id="riskId" alias="标识列" type="string" length="16" not-null="1"
		pkey="true" fixed="true" hidden="true" 	display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="phrId" alias="档案编号" type="string" length="30" width="160" queryable="true" display="1" fixed="true" group="建卡信息"/>
	<item ref="b.personName" display="1" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.regionCode" 	display="1" queryable="true"/>
	<item id="empiId" alias="empiid" type="string" length="32" hidden="true" display="0"
		fixed="true" />
	

	<item id="inputDate" alias="建档日期" type="date" not-null="1" fixed="true" group="建卡信息"
		defaultValue="%server.date.today" update="false">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="inputUnit" alias="建档单位" type="string" not-null="1" colspan="2" length="8" group="建卡信息"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="建档医生" type="string" not-null="1" length="20" group="建卡信息"
		defaultValue="%user.userId" fixed="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<!-- 2.4.2新增字段 b -->
	<item id="dataSource" alias="数据来源" type="string" not-null="1" length="1" group="建卡信息">
		<dic>
			<item key="1" text="老年人随访" />
			<item key="2" text="糖尿病随访" />
			<item key="3" text="健康检查表" />
			<item key="4" text="体检登记" />
			<item key="5" text="门诊就诊" />
			<item key="6" text="35岁首诊测压" />
			<item key="7" text="高血压疑似核实" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="otherDataSource" alias="其它来源" fixed="true" colspan="2" type="string" length="20" group="建卡信息"/>
	<item id="hospital" alias="经常就诊医院" type="string" length="1" group="建卡信息">
		<dic>
			<item key="1" text="本院" />
			<item key="2" text="其他一级医院" />
			<item key="3" text="本区二、三级医院" />
			<item key="9" text="其他" />
		</dic>
	</item>
	<item id="effectCase" alias="转归情况" type="string" length="1"
		group="建卡信息" fixed="true"
		defaultValue="1">
		<dic>
			<item key="1" text="继续管理" />
			<item key="2" text="暂时失访" />
			<item key="3" text="终止管理" />
		</dic>
	</item>
	<item id="stopDate" alias="终止日期" type="datetime"  colspan="2" xtype="datefield" fixed="true"
		group="建卡信息"/>
	<item id="effect" alias="终止原因" type="string" fixed="true" length="1"
		group="建卡信息">
		<dic>
			<item key="1" text="转高血压" />
			<item key="2" text="搬迁" />
			<item key="3" text="死亡" />
			<item key="4" text="拒绝" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="age" alias="年龄" type="int" fixed="true" not-null="1" group="体格检查"/>
	<item id="waistLine" alias="腰围(cm)" type="double" length="4" group="体格检查"
		minValue="40" maxValue="200" display="0" />
	<item id="height" alias="身高(cm)" type="double" length="6" display="2" group="体格检查"
		minValue="100" maxValue="300" not-null="1" enableKeyEvents="true" />
	<item id="weight" alias="体重(kg)" type="double" length="6" display="2" group="体格检查"
		minValue="30" maxValue="500" not-null="1" enableKeyEvents="true" />
	<item id="bmi" alias="BMI" length="6" type="double" not-null="1" fixed="true"  group="体格检查"/>
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int" group="体格检查"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"  width="150"/>
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int" group="体格检查"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false"  width="150"/>
	<item id="smokeCount" alias="日吸烟量" type="int" length="3"  group="体格检查"/>
	<item id="smokeYears" alias="吸烟年数" type="int" length="2"  group="体格检查"/>
	<item id="drinkTimes" alias="周饮酒次数" type="int" length="4"  group="体格检查"/>
	<item id="drinkCount" alias="日饮酒量(两)" type="int" length="4" group="体格检查" />
	<item id="saltCount" alias="日摄盐量" type="int" length="3" queryable="true"  group="体格检查"/>
	<item id="tc" alias="TC" type="double" length="10" width="165"  group="体格检查"/>
	<item id="td" alias="TD" type="double" length="10" width="165" group="体格检查"/>
	<item id="ldl" alias="LDL-C" type="double" length="10" width="165" group="体格检查" />
	<item id="hdl" alias="HDL-C" type="double" length="10" width="165" group="体格检查"/>
	<item id="fbs" alias="空腹血糖" type="double" length="6" precision="2" group="体格检查" />
	<item id="pbs" alias="餐后血糖" type="double" length="6" precision="2" group="体格检查" />
	<item id="riskiness" alias="高危因素" type="string" length="64"
		colspan="4" width="450" not-null="true" group="高危因素" >
		<dic render="Checkbox" columnWidth="700" columns="1">
			<item key="01" text="高龄" />
			<item key="02" text="超重或者肥胖(BMI>=24kg/m2)" />
			<item key="03" text="收缩压介于120~139mmHg之间和舒张压介于80~89之间" />
			<item key="04" text="高血压家族史(一,二级亲属)" />
			<item key="05" text="长期过量饮酒(每日饮2两白酒/2.5瓶啤酒/4个易拉罐啤酒/6两黄酒/1斤2两葡萄酒,且每周饮酒4次以上)" columnWidth="2"/>
			<item key="06" text="长期膳食高盐" />
			<item key="07" text="糖尿病患者" />
			<item key="08" text="缺乏体力劳动" />
			<item key="09" text="吸烟" />
			<item key="10" text="血脂异常" />
			<item key="11" text="糖调节异常" />
		</dic>
	</item>
	<item id="statusCase" alias="核实情况" type="string" length="1" display="0" defaultValue="1">
		<dic>
			<item key="0" text="待核实" />
			<item key="1" text="高危确诊" />
			<item key="2" text="疑似高血压" />
			<item key="9" text="排除" />
		</dic>
	</item>
	<item id="createFlag" alias="建档标志" type="string" length="1" display="0" not-null="1" defaultValue="1">
		<dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>
	</item>
	<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" fixed="true" display="0">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="已注销"/>
		</dic>
	</item>
	<item id="registerDate" alias="登记日期" type="date" fixed="true"
		defaultValue="%server.date.today" update="false" display="0">
		<set type="exp">["$","%server.date.today"]</set>
	</item>
	<item id="registerUser" alias="登记人" type="string" length="20"
		defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="registerUnit" alias="登记单位" type="string" length="8"
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
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
		<relation type="children"
			entryName="chis.application.mpi.schemas.MPI_DemographicInfo">
			<join parent="empiId" child="empiId" />
		</relation>
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>
