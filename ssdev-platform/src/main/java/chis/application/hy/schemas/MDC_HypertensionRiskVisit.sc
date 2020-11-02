<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hy.schemas.MDC_HypertensionRiskVisit" alias="糖尿病高危随访">
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
	<item id="age" alias="年龄" type="int" fixed="true" group="体格检查"  virtual="true"/>
	<item id="constriction" alias="收缩压(mmHg)" not-null="1" type="int" group="体格检查"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false" width="150"/>
	<item id="diastolic" alias="舒张压(mmHg)" not-null="1" type="int" group="体格检查"
		minValue="50" maxValue="500" enableKeyEvents="true" validationEvent="false" width="150"/>
	<item id="height" alias="身高(cm)" type="double" length="6" display="2" group="体格检查"
		minValue="100" maxValue="300"  enableKeyEvents="true" />
	<item id="weight" alias="体重(kg)" type="double" minValue="0" group="体格检查"
		maxValue="500" enableKeyEvents="true" validationEvent="false" />
	<item id="bmi" alias="BMI" length="6" type="double" fixed="true" group="体格检查"/>
	<item id="riskiness" alias="高危因素" type="string" length="64"
		colspan="3" width="450"  group="高危因素">
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
	<!--<item id="healthTeach" alias="健康指导" type="string" length="20" colspan="3" >
			<dic render="LovCombo">
				<item key="01" text="合理膳食" />
				<item key="02" text="适量运动" />
				<item key="03" text="控制体重" />
				<item key="04" text="戒烟限酒" />
				<item key="05" text="缓解精神压力" />
			</dic>
		</item>-->
	<item id="visitWay" alias="随访方式" type="string" length="1"
		not-null="true" group="随访情况">
		<dic id="chis.dictionary.visitWay" />
	</item>
	<item id="obeyDoctor" alias="遵医行为" type="string" length="1"
		canInput="true" colspan="2" group="随访情况">
		<dic>
			<item key="1" text="良好" />
			<item key="2" text="一般" />
			<item key="3" text="差" />
		</dic>
	</item>
	<item id="visitDate" alias="随访日期" type="date" not-null="1"
		queryable="true" group="随访情况" defaultValue="%server.date.today"/>
	<item id="visitDoctor" alias="随访医生" type="string" length="20"
		defaultValue="%user.userId" queryable="true" group="随访情况">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitUnit" alias="随访单位" type="string" length="20"
		defaultValue="%user.manageUnit.id" queryable="true" group="随访情况">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="visitEffect" alias="转归情况" type="string" length="1"
		defaultValue="1" group="随访情况">
		<dic>
			<item key="1" text="继续管理" />
			<item key="2" text="暂时失访" />
			<item key="3" text="终止管理" />
		</dic>
	</item>
	<item id="stopDate" alias="终止日期" type="datetime" xtype="datefield" fixed="true"
		group="随访情况"/>
	<item id="stopCause" alias="终止原因" type="string" fixed="true" length="1"
		group="随访情况">
		<dic>
			<item key="1" text="转高血压" />
			<item key="2" text="搬迁" />
			<item key="3" text="死亡" />
			<item key="4" text="拒绝" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="healthTeach" alias="健康教育" type="string" height="260" virtual="true" length="2000" width="200" colspan="8" xtype="textarea"
		group="随访情况"/>
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
