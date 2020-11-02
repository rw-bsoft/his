<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.tr.schemas.MDC_TumourPatientVisit" alias="肿瘤患者随访">
	<item id="visitId" alias="随访记录ID" type="string" length="16" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="phrId" alias="phrId" type="string" length="32" display="0"/>
	<item id="annulControl" alias="撤销管理" type="string" length="1" defaultValue="1" not-null="1">
		<dic render="Radio" colWidth="60" columns="2">
			<item key="1" text="未撤销"/>
			<item key="2" text="已撤销"/>
		</dic>
	</item>
	<item id="annulDate" alias="撤销日期" type="date" colspan="2"/>
	<item id="annulCause" alias="撤销原因" type="string" length="1">
		<dic>
			<item key="1" text="误诊"/>
			<item key="2" text="拒访"/>
			<item key="3" text="寄居外地"/>
			<item key="4" text="户口迁往外地"/>
			<item key="5" text="户口空挂"/>
			<item key="6" text="其他"/>
		</dic>
	</item>
	<item id="otherCause" alias="其他原因" type="string" length="200" colspan="2"/>
	<item id="haveTransfer" alias="有无转移" type="string" length="1" defaultValue="n" not-null="1">
		<dic id="chis.dictionary.haveOrNot"/>
	</item>
	<item id="transferPart" alias="转移部位" type="string" length="200" colspan="2"/>
	<item id="isRelapse" alias="是否复发" type="string" length="1" defaultValue="n" not-null="1">
		<dic id="chis.dictionary.yesOrNo" render="Radio" colWidth="50" columns="2"/>
	</item>
	<item id="relapseNumber" alias="复发次数" type="int" length="2" colspan="2"/>
	<item id="relapseDate1" alias="复发日期1" type="date"/>
	<item id="relapseDate2" alias="复发日期2" type="date"/>
	<item id="relapseDate3" alias="复发日期3" type="date"/>
	<item id="cureWay" alias="治疗方式" type="string" length="50" colspan="3" not-null="1">
		<dic render="Checkbox" columnWidth="70" columns="9">
			<item key="01" text="手术"/>
			<item key="02" text="化疗"/>
			<item key="03" text="放疗"/>
			<item key="04" text="中药"/>
			<item key="05" text="免疫"/>
			<item key="06" text="介入"/>
			<item key="07" text="止痛治疗"/>
			<item key="08" text="未治疗"/>
			<item key="99" text="其他治疗"/>
		</dic>
	</item>
	<item id="illnessCase" alias="目前疾病情况" type="string" length="1" colspan="2" not-null="1">
		<dic render="Radio" colWidth="60" columns="3">
			<item key="1" text="稳定"/>
			<item key="2" text="好转"/>
			<item key="3" text="恶化"/>
		</dic>
	</item>
	<item id="weight" alias="体重(kg)" type="int" length="2"/>
	<item id="guidanceContent" alias="指导内容" type="string" length="2" colspan="3">
		<dic render="Radio" colWidth="70" columns="7">
			<item key="01" text="督导随访"/>
			<item key="02" text="用药"/>
			<item key="03" text="饮食"/>
			<item key="04" text="康复"/>
			<item key="05" text="加床"/>
			<item key="06" text="住院"/>
			<item key="07" text="出诊"/>
		</dic>
	</item>
	<item id="CSV" alias="卡氏评分值" type="int" length="3" colspan="2" not-null="1">
		<dic id="chis.dictionary.tumourCSV"/>
	</item>
	<item id="visitDate" alias="随访日期" type="date" defaultValue="%server.date.date" not-null="1"/>
	<item id="visitDoctor" alias="随访医生" type="string" length="20" defaultValue="%user.userId" not-null="1">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="vistUnit" alias="随访机构" type="string" length="20" defaultValue="%user.manageUnit.id" fixed="true" colspan="2">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="remark" alias="备注" type="string" xtype="textarea" length="500" colspan="3"/>
	<item id="isDeath" alias="是否死亡" type="string" length="1" not-null="1" defaultValue="n">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="deathDate" alias="死亡日期" type="date"/>
	<item id="deathPlace" alias="死亡地点" type="string" length="50"/>
	<item id="deathCause" alias="根本死因代码" type="string" length="20" display="0"/>
	<item id="firstVisit" alias="是否首次随访" type="string" length="1" display="0">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	
	<item id="createUser" alias="录入人" type="string" length="20" update="false" queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime" queryable="true" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
