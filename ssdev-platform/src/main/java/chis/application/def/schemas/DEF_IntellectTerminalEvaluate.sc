<?xml version="1.0" encoding="UTF-8"?>
<entry alias="结案总结">
	<item id="id" alias="主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="defId" alias="训练评估外键" type="string" length="16" display="0"/>
	<item id="lastScore" alias="末期分数" type="int"/>
	<item id="updateScore" alias="提高分数" type="int" fixed="true"/>
	<item id="trainEffect" alias="训练效果" length="1">
		<dic>
			<item key="1" text="有进步" />
			<item key="2" text="有效" />
			<item key="3" text="无效" />
		</dic>
	</item>
	<item id="healingTarget" alias="实现康复目标情况" length="500" colspan="3" not-null="1">
		<dic render="TreeCheck" onlyLeafCheckable="true"  checkModel ="childCascade">
			<item key="1" text="运动功能">
				<item key="11" text ="运动功能明显改善" />
				<item key="12" text ="运动功能改善" />
			</item>
			<item key="2" text="感知能力">
				<item key="21" text ="感知能力明显提高" />
				<item key="22" text ="感知能力提高" />
			</item>
			<item key="3" text="认知能力">
				<item key="31" text ="认知能力能力明显提高" />
				<item key="32" text ="认知能力能力提高" />
			</item>
			<item key="4" text="语言交往能力">
				<item key="41" text ="语言交往能力明显提高" />
				<item key="42" text ="语言交往能力提高" />
			</item>
			<item key="5" text="生活自理能力">
				<item key="51" text ="生活自理能力明显提高" />
				<item key="52" text ="生活自理能力提高" />
			</item>
			<item key="6" text="社会适应能力">
				<item key="61" text ="社会适应能力明显增强" />
				<item key="62" text ="社会适应能力增强" />
			</item>
		</dic>
	</item>
	<item id="recoverSuggestion" alias="进一步康复意见" length="100" colspan="3">
		<dic render="LovCombo">
			<item key="1" text="继续训练" />
			<item key="2" text="接受教育" />
			<item key="3" text="参加集体活动" />
			<item key="4" text="家长培训" />
			<item key="5" text="转介" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item id="evaluateUser" alias="康复指导员" type="string" length="20" defaultValue="%user.userId" colspan="3" not-null="1" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
	</item>
	<item id="inputUnit" alias="录入单位" type="string" length="20"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入人" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入时间" type="datetime"  xtype="datefield"  update="false"
		fixed="true" defaultValue="%server.date.today" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
