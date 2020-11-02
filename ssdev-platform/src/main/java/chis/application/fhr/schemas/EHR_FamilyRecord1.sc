<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.fhr.schemas.EHR_FamilyRecord" alias="家庭健康档案" sort="familyId desc">
	<item id="familyId" pkey="true" alias="家庭编号" type="string"
		length="17" not-null="1"  fixed="true" queryable="true"  generator="assigned"  width="160">
		<key>
			<Rule name="areaCode" defaultFill="0" length="12" fillPos="after" type="string">
      		%codeCtx.regionCode
			</Rule>
			<Rule name="increaseId" defaultFill="0" type="increase" seedRel="areaCode" length="5" startPos="1"/>
		</key>
	</item>
	
	<item id="ownerName" alias="户主姓名" type="string" length="20"
		not-null="1"  >
	<!--<dic id="chis.dictionary.ownerName" render="Tree" forceSelection="false"/>-->
	</item>
	<item id="manaDoctorId" alias="责任医生" not-null="1" type="string" length="20" fixed="true" queryable="true" update="true" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"  keyNotUniquely="true" parentKey="%user.manageUnit.id" />
	</item>
	
	
	
	<item id="regionCode" alias="网格地址" not-null="1" type="string" length="25"  fixed="true"
		 queryable="true" width="200"  anchor="100%" update="false"   colspan="2">
		<dic id="chis.dictionary.areaGrid_family"  render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="regionCode_text" alias="网格地址" type="string" length="100"   not-null="1" />
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" width="180" not-null="1" queryable="true" fixed="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" sliceType = "3" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="familyAddr" alias="家庭地址" type="string" colspan="2" length="100" width="180"/>
	<item id="familyHome" alias="家庭电话" type="string" length="20" queryable="true" />
	<item id="roomTypeCode" alias="住房类型" type="string" length="6"  display="2">
		<dic id="chis.dictionary.roomTypeCode"/>
	</item>
	<item id="roomArea" alias="居住面积(㎡)"  type="double" length="8" precision="2"  display="2"/>
	<item id="totalNumber" alias="家庭总人口" type="int" maxValue="99999" display="2"/>
	<item id="liveNumber" alias="现住人口数" type="int"  maxValue="99999" display="2"/>
	<item id="aveIncome" alias="年人均收入" type="string" length="2" display="2">
		<dic id="chis.dictionary.aveIncome"/>
	</item>

	<item id="fuelType" alias="燃料类型" type="string" length="6"  display="2">
		<dic id="chis.dictionary.fuelType"/>
	</item>
	<item id="cookAirTool" alias="厨房排风设施" type="string" length="6" defaultValue="1"  display="2">
		<dic id="chis.dictionary.cookAirTool"/>
	</item>
	<item id="waterSourceCode" alias="饮水类型" type="string" length="6" defaultValue="1"  display="2">
		<dic id="chis.dictionary.waterSourceCode"/>
	</item>
	<item id="washroom" alias="厕所类别" type="string" length="2" defaultValue="1"  display="2">
		<dic id="chis.dictionary.washroom"/>
	</item>
	<item id="livestockColumn" alias="禽畜栏" type="string" length="2" display="2">
		<dic id="chis.dictionary.livestockColumn"/>
	</item>
	<item id="economicalStatus" alias="家庭经济状况" type="string" length="1"  display="2">
		<dic id="chis.dictionary.economicalStatus"/>
	</item>
	<item id="internetModeCode" alias="上网方式" type="string" length="6"  display="2">
		<dic id="chis.dictionary.internetModeCode"/>
	</item>
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
	<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" fixed="true" defaultValue="%server.date.today" queryable="true" update="false">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		display="1" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="writeOffUnit" alias="注销单位" type="string" length="20"
		width="180" hidden="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="writeOffUser" alias="注销人" type="string" length="20"
		hidden="true" >
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="writeOffDate" alias="注销日期" type="date" hidden="true" />
	<item id="status" alias="档案状态" type="string" length="1"
		hidden="true" defaultValue="0">
		<dic>
			<item key="0" text="正常"></item>
			<item key="1" text="注销"></item>
		</dic>
	</item>
	<item id="memo" alias="备注" type="string" xtype="textarea" colspan="3"
		length="500" display="2"/>
</entry>