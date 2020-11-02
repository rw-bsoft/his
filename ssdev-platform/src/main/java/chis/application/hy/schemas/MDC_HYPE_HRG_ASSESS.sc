<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MDC_HYPE_HRG_ASSESS" alias="高血压高危评估表">
  <item id="assessId" alias="评估记录标识" type="string" length="16" not-null="1" generator="assigned" pkey="true" fixed="true" display="0">
  		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
  </item>
  <item id="empiId" alias="empiId" type="string" length="32" not-null="1" display="0"/>
  <item id="recordId" alias="高危人群表ID" type="string" length="16"/>
  <item id="assessDate" alias="评估日期" type="date"/>
  <item id="assessUser" alias="评估人员" type="string" length="20"/>
  <item id="assessUnit" alias="评估机构" type="string" length="20"/>
  <item id="assessType" alias="评估类型" type="string" length="1"/>
  <item id="isAdvancedAge" alias="高龄" type="string" length="1"/>
  <item id="isOverweightOrFat" alias="超重或肥胖" type="string" length="1"/>
  <item id="isHypeFamilyHistory" alias="高血压家族史" type="string" length="1"/>
  <item id="isSmoking" alias="是否吸烟" type="string" length="1"/>
  <item id="isLTExcessiveDrinking" alias="长期过量饮酒" type="string" length="1"/>
  <item id="isLackOfPhysicalActivity" alias="缺乏体力运动" type="string" length="1"/>
  <item id="isDyslipidemia" alias="血脂异常" type="string" length="1"/>
  <item id="isIGR" alias="糖调节异常" type="string" length="1"/>
  <item id="age" alias="年龄" type="double" length="3"/>
  <item id="constriction" alias="收缩压(mmHg)" type="double" length="3"/>
  <item id="diastolic" alias="舒张压(mmHg)" type="double" length="3"/>
  <item id="waistline" alias="腰围(cm)" type="double" length="3"/>
  <item id="weight" alias="体重(kg)" type="double" length="3"/>
  <item id="height" alias="身高(cm)" type="double" length="3"/>
  <item id="BMI" alias="BMI" type="double" length="8" precision="2"/>
  <item id="smokingNumber" alias="日吸烟量(支)" type="double" length="3"/>
  <item id="smokingYears" alias="吸烟年数" type="double" length="2"/>
  <item id="drinkTimeOfWeek" alias="周饮酒次数" type="double" length="3"/>
  <item id="dayOfDrinking" alias="日饮酒量(两)" type="double" length="3"/>
  <item id="dayOfSalt" alias="日摄盐量(g)" type="double" length="3"/>
  <item id="TC" alias="TC(mmol/L)" type="double" length="8" precision="2"/>
  <item id="TD" alias="TD(mmol/L)" type="double" length="8" precision="2"/>
  <item id="LDL-C" alias="LDL-C(mmol/L)" type="double" length="8" precision="2"/>
  <item id="HDL-C" alias="HDL-C(mmol/L)" type="double" length="8" precision="2"/>
  <item id="FBS" alias="空腹血糖值(mmol/L)" type="double" length="8" precision="2"/>
  <item id="PBS" alias="餐后血糖（mmol/L）" type="double" length="8" precision="2"/>
  
  <item id="createUnit" alias="建档单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="建档人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
</entry>
