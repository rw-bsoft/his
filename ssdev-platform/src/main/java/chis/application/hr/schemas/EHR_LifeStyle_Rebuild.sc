<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_LifeStyle" alias="个人生活习惯">
	<item id="lifeStyleId" pkey="true" alias="档案编号" type="string"
		length="16" not-null="1" fixed="true" hidden="true"
		generator="assigned">
	</item>
	<item id="empiId" alias="empiid" type="string" length="32"
		queryable="true" fixed="true" notDefaultValue="true" hidden="true" />
	<item id="smokeFlag" alias="是否吸烟" type="string" length="2" queryable="true">
		<dic>
			<item key="n" text="不吸" />
			<item key="y" text="吸" />
		</dic>
	</item>
	<item id="smokeCount" alias="每天支数" type="int" length="3"
		queryable="true" />
	<item id="smokeYears" alias="吸烟年数" type="int" length="2"
		queryable="true" />
	<item id="smokeFreqCode" alias="吸烟频率" type="string" length="2"
		queryable="true">
		<dic id="chis.dictionary.CV5101_24" />
	</item>
	<item id="drinkFlag" alias="是否饮酒" type="string" length="1"
		queryable="true">
		<dic>
			<item key="1" text="不饮酒" />
			<item key="2" text="偶尔" />
			<item key="3" text="经常（每周2~3次）" />
			<item key="4" text="每周4次以上" />
		</dic>
	</item>
	<item id="drinkCount" alias="日饮酒量(两)" type="int" length="4"
		queryable="true" />
	<item id="drinkTypeCode" alias="饮酒种类" type="string" length="64"
		queryable="true" colspan="2">
		<dic id="chis.dictionary.CV5305_02" render="LovCombo"/>
	</item>
	<item id="eateHabit" alias="饮食习惯" type="string" length="64"
		queryable="true" colspan="3">
		<dic id="chis.dictionary.eateHabit" render="LovCombo" />
	</item>
	<item id="trainFreqCode" alias="锻炼频率" type="string" length="2"
		queryable="true" >
		<dic id="chis.dictionary.CV5101_28" />
	</item>
	<item id="smokeTypeCode" alias="吸烟种类" type="string" length="2"
		queryable="true">
		<dic id="chis.dictionary.CV5305_01" />
	</item>

	<item id="smokeStartAge" alias="开始吸烟年龄" type="int" length="2"
		queryable="true" />
	<item id="smokeAddiAge" alias="每天吸烟年龄" type="int" length="2"
		queryable="true" />

	<item id="smokeEndAge" alias="戒烟开始年龄" type="int" length="2"
		queryable="true" />
	<item id="dryOutMethodCode" alias="戒烟方法" type="string" length="2"
		queryable="true">
		<dic id="chis.dictionary.CV5201_18" />
	</item>
	<item id="secondSmoke" alias="接触二手烟" type="string" length="1"
		queryable="true">
		<dic>
			<item key="1" text="几乎没有" />
			<item key="2" text="有" />
			<item key="3" text="不知道" />
		</dic>
	</item>
	<item id="secondSmokeDays" alias="每周接触天数" type="int" length="1"
		queryable="true" />
	<item id="secondSmokeSiteCode" alias="被动吸烟场所" type="string"
		length="1" queryable="true">
		<dic id="chis.dictionary.CV5101_25" />
	</item>
	<item id="tobacco" alias="烟草用量" type="string" length="10"/>
	<item id="drinkFreqCode" alias="饮酒频率" type="string" length="1"
		queryable="true">
		<dic id="chis.dictionary.CV5101_26" />
	</item>

	<item id="drinkStartAge" alias="开始饮酒年龄" type="int" length="2"
		queryable="true" />
	<item id="drinkEndAge" alias="戒酒开始年龄" type="int" length="2"
		queryable="true" colspan="2" anchor="50%" />

	<item id="foodTypeCode1" alias="饮食种类1" type="string" length="2"
		queryable="true">
		<dic id="chis.dictionary.CV5305_03" />
	</item>
	<item id="foodFreq1" alias="频率1(次/天)" type="int" length="2"
		queryable="true" />
	<item id="foodTypeCode2" alias="饮食种类2" type="string" length="2"
		queryable="true">
		<dic id="chis.dictionary.CV5305_03" />
	</item>
	<item id="foodFreq2" alias="频率2(次/天)" type="int" length="2"
		queryable="true" />
	<item id="foodTypeCode3" alias="饮食种类3" type="string" length="2"
		queryable="true">
		<dic id="chis.dictionary.CV5305_03" />
	</item>
	<item id="foodFreq3" alias="频率3(次/天)" type="int" length="2"
		queryable="true" />
	<item id="foodTypeCode4" alias="饮食种类4" type="string" length="2"
		queryable="true">
		<dic id="chis.dictionary.CV5305_03" />
	</item>
	<item id="foodFreq4" alias="频率4(次/天)" type="int" length="2"
		queryable="true" />
	<item id="saltCount" alias="一斤盐吃几天" type="int" length="3"
		queryable="true" />
	<item id="oilCount" alias="每月用油(斤)" type="int" length="2"
		queryable="true" />
	<item id="eateCount" alias="每日吃饭顿数" type="int" length="1"
		queryable="true" colspan="2" anchor="50%" />
	<item id="tabooFood" alias="饮食禁忌" type="string" length="200"/>
	<item id="trainMinute" alias="每次时长(分)" type="int" length="3"
		queryable="true" />
	<item id="trainYear" alias="坚持时间(年)" type="int" length="2"
		queryable="true" />
	<item id="trainModeCode" alias="锻炼方式" type="string" length="2"
		queryable="true">
		<dic>
			<item key="1" text="散步" />
			<item key="2" text="做操或气功" />
			<item key="3" text="太极拳" />
			<item key="4" text="跑步" />
			<item key="5" text="跳舞" />
			<item key="6" text="球类" />
			<item key="7" text="其他" />
		</dic>
	</item>
	<item id="trainSiteCode" alias="锻炼场所" type="string" length="1"
		queryable="true">
		<dic>
			<item key="1" text="家里" />
			<item key="2" text="公园" />
			<item key="3" text="健身房" />
			<item key="4" text="其他" />
		</dic>
	</item>
	<item id="workTypeCode" alias="身体活动类别" type="string" length="1"
		queryable="true">
		<dic>
			<item key="1" text="工作、农业及家务性" />
			<item key="2" text="交通性" />
			<item key="3" text="休闲性" />
		</dic>
	</item>
	<item id="workIntenCode" alias="身体活动强度" type="string" length="1"
		queryable="true">
		<dic>
			<item key="1" text="高强度活动" />
			<item key="2" text="中强度活动" />
			<item key="3" text="低强度活动" />
		</dic>
	</item>
	<item id="workFreqCode" alias="身体活动频率" type="string" length="1"
		queryable="true" colspan="2" anchor="50%">
		<dic id="chis.dictionary.CV5101_26" />
	</item>
	<item id="sitHour" alias="静态时长(时)" type="int" length="2"
		queryable="true" />
	<item id="siestaHour" alias="午睡时长(时)" type="int" length="2"
		queryable="true" />
	<item id="sackTime" alias="睡眠时长(时)" type="int" length="2"
		queryable="true" />
	<item id="psychosisCode" alias="精神情况" type="string" length="2"
		queryable="true">
		<dic id="chis.dictionary.psychosisCode" />
	</item>
	<item id="confideCode" alias="亲友倾述" type="string" length="2"
		queryable="true">
		<dic>
			<item key="01" text="不倾诉" />
			<item key="02" text="没有人可倾诉" />
			<item key="03" text="有1个" />
			<item key="04" text="有2个" />
			<item key="05" text="3个以上" />
			<item key="06" text="从无心情不好" />
			<item key="99" text="拒绝回答" />
		</dic>
	</item>
	<item id="pressureCode" alias="工作生活压力" type="string" length="2"
		queryable="true">
		<dic>
			<item key="01" text="轻松" />
			<item key="02" text="尚可" />
			<item key="03" text="有压力" />
			<item key="04" text="压力较大" />
			<item key="99" text="拒绝回答" />
		</dic>
	</item>
	<item id="temperamentCode" alias="气质类型" type="string" length="1"
		queryable="true">
		<dic>
			<item key="1" text="胆汁质" />
			<item key="2" text="多血质" />
			<item key="3" text="粘液质" />
			<item key="4" text="抑郁质" />
		</dic>
	</item>
	<item id="doingsCode" alias="社交活动" type="string" length="1"
		queryable="true">
		<dic>
			<item key="1" text="多" />
			<item key="2" text="少" />
			<item key="3" text="无" />
		</dic>
	</item>
	<item id="memo" alias="备注" type="string" length="500"
		queryable="true" colspan="4" />
	<item id="createUnit" alias="建档单位" type="string" length="20" update="false"
		width="180"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人" type="string" length="20" update="false"
		fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="date" fixed="true" defaultValue="%server.date.today" queryable="true" update="false">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="date"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.today']</set>
	</item>
</entry>