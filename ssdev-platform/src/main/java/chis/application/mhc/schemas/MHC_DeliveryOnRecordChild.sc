<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mhc.schemas.MHC_DeliveryOnRecordChild" alias="产妇所生儿童记录表">
	<item id="DRCID" alias="儿童记录表 主键" type="string" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key> 
	</item>
	<item id="motherEmpiId" alias="empiId" display="0" type="string" length="32"/>
	<item id="pregnantId" alias="孕妇档案标识"  display="0" type="string" length="16"/>
	<item id="childEmpiId" alias="儿童empiId" display="0" type="string" length="32"/>
	<item id="certificateNo" alias="出生证号" display="0" type="string" length="10"/>
	<item id="name" alias="姓名" length="10"/>
	<item id="sex" alias="性别" not-null="true"  type="string" length="1">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthWeight" alias="出生体重(kg)" not-null="true"  type="double" display="2" length="5" precision="2" maxValue="999.99"/>
	<item id="height" alias="出生身长(cm)" not-null="true" length="4" type="double" display="2"  precision="1" maxValue="999.9"/>
	<item id="headGirth" alias="头围(cm)" length="5" type="double" display="2"  precision="1"/>
	<item id="Bust" alias="胸围(cm)" length="5" type="double" display="2"  precision="1"/>
	<item id="deliveryDate" alias="分娩时间" not-null="true" maxValue="%server.date.datetime" type="datetime"/>
	<item id="deliveryWeeks" alias="分娩孕周" fixed="true"    type="int" width="160" />
	<item id="birthDefect" alias="出生缺陷" not-null="true" display="2"  type="string" length="1">
		<dic id="chis.dictionary.haveOrNot" />
	</item>
	<item id="birthDefectDesc" alias="出生缺陷详述" colspan="2" display="2"  length="100"/>
	<item id="apganGrade1min" alias="Apgar评分1分钟" not-null="true"  type="int" display="2"  length="2"/>
	<item id="apganGrade5min" alias="Apgar评分5分钟" not-null="true" type="int" display="2"  length="2"/>
	<item id="apganGrade10min" alias="Apgar评分10分钟" not-null="true" type="int"  display="2"  length="2"/>
	<item id="rescueSituation" alias="抢救情况" type="string" display="2"  length="1">
		<dic>
			<item key="1" text="无" />
			<item key="2" text="吸粘液" />
			<item key="3" text="气管插管" />
		</dic>
	</item>
	<item id="NeonatalComplications" alias="新生儿并发症" not-null="true" type="string" display="2"  length="1">
		<dic>
			<item key="1" text="无" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item id="neonatalComplicationsDes" alias="并发症详述" colspan="1" fixed="true" display="2"  length="100"/>
	<item id="placentaRecord" alias="胎盘记录" display="2"  length="100"/>
	<item id="FMR" alias="胎膜记录" display="2"  length="100"/>
	<item id="UCR" alias="脐带记录" display="2"  length="100"/>
	<item id="AFR" alias="羊水记录" display="2"  length="100"/>
	<item id="APFR" alias="羊水胎盘胎膜记录" colspan="3" display="2"  length="200"/>
	<item id="NGR" alias="新生儿指导记录" colspan="3" display="2"  length="100"/>
	<item id="deliveryOutcome" alias="分娩结局" not-null="true" display="2"  type="string" length="1">
		<dic>
			<item key="1" text="活产" />
			<item key="2" text="死胎" />
			<item key="3" text="死产 " />
		</dic>
	</item>
	<item id="isDie" alias="是否死亡" not-null="true" display="2"  type="string" length="1">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
		</dic>
	</item>
	<item id="dieTime" alias="死亡时间" display="2" type="datetime"   maxValue="%server.date.datetime"/>
	<item id="dieResaon" alias="死亡原因" colspan="3" display="2"  length="100"/>
	<item id="createDate" alias="录入日期" display="2" type="datetime"  xtype="datefield" fixed="true" update="false"
		defaultValue="%server.date.date">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="createUser" alias="录入员工" display="2" type="string" update="false" length="20" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="录入单位" display="2" type="string" update="false" length="22"  fixed="true" defaultValue="%user.manageUnit.id" width="150">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="22" display="2" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人"   type="string" length="20" hidden="true"
		defaultValue="%user.userId" display="2">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期"  type="datetime" hidden="true" xtype="datefield"
		defaultValue="%server.date.date" display="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
