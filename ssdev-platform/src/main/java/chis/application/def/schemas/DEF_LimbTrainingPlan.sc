<?xml version="1.0" encoding="UTF-8"?>
<entry alias="训练计划" >
	<item id="id" alias="主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="defId" alias="登记号" type="string" length="16" hidden="true"/>
	<item id="healingTarget" alias="康复目标" length="500" colspan="3" not-null="1" display="2">
		<dic render="TreeCheck" onlyLeafCheckable="true"  checkModel ="childCascade">
			<item key="1" text="运动功能">
				<item key="11" text ="运动功能明显改善" />
				<item key="12" text ="运动功能改善" />
			</item>
			<item key="2" text="生活自理能力">
				<item key="21" text ="生活自理能力明显提高" />
				<item key="22" text ="生活自理能力提高" />
			</item>
			<item key="3" text="社会适应能力">
				<item key="31" text ="社会适应能力明显增强" />
				<item key="32" text ="社会适应能力增强" />
			</item>
		</dic>
	</item>
	<item id="healingTraining" alias="训练项目" length="100" colspan="3" not-null="1" display="2">
		<dic render="LovCombo">
			<item key="1" text ="翻身" />
			<item key="2" text ="坐" />
			<item key="3" text ="站" />
			<item key="4" text ="转移" />
			<item key="5" text ="步行或驱动轮椅" />
			<item key="6" text ="上下台阶" />
			<item key="7" text ="进食" />
			<item key="8" text ="穿脱衣服" />
			<item key="9" text ="洗漱" />
			<item key="10" text ="入厕" />
			<item key="11" text ="交流" />
			<item key="12" text ="做家务" />
			<item key="13" text ="参加社会活动" />
		</dic>
	</item>
	<item id="healingMaterial" alias="训练指导材料" length="100" colspan="3" not-null="1" display="2">
		<dic render="LovCombo">
			<item key="1" text ="《肢体残疾系统康复训练》(中国残联编)" />
			<item key="2" text ="《康复指导丛书》(中国残联编)" />
			<item key="3" text ="康复训练普及读物(中国残联编)" />
			<item key="4" text ="肢体残疾康复训练的音像制品(中国残联编)" />
			<item key="5" text ="省残联认定的训练指导材料" />
		</dic>
	</item>
	<item id="healingPlace" alias="训练场所" length="20" not-null="1" display="2">
		<dic render="LovCombo">
			<item key="1" text ="机构" />
			<item key="2" text ="家庭" />
		</dic>
	</item>
	<item id="healingWay" alias="训练方法" length="100" colspan="2" display="2">
		<dic render="LovCombo">
			<item key="1" text ="使用器具训练" />
			<item key="2" text ="徒手训练" />
			<item key="3" text ="传统方法" />
			<item key="4" text ="理疗" />
			<item key="5" text ="社会适应训练" />
			<item key="6" text ="其他" />
		</dic>
	</item>
	<item id="healingDate" alias="制定计划日期" type="date" defaultValue="%server.date.today" not-null="1" width="120" maxValue="%server.date.today" />
	<item id="healingTrainer" alias="康复指导员" type="string" length="20"
		defaultValue="%user.userId" display="2" colspan="2" not-null="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
	</item>
	
	<item id="inputUnit" alias="录入单位" type="string" length="20"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id"  display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入人" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
