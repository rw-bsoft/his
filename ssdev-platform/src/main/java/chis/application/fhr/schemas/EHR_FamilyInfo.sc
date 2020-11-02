<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.fhr.schemas.EHR_FamilyInfo" alias="家庭状况" sort="familyId desc">
	<item id="jtbh" alias="家庭编号" type="string" defaultValue="20141013141144810" fixed="true" group="基本信息"/>
	<item id="hzxm" alias="户主姓名" type="string" group="基本信息"/>
	<item id="zrys" alias="责任医生" type="string" group="基本信息">
		<dic id="chis.dictionary.doctor" onlySelectLeaf="true"/>
	</item>
	<item id="jtdh" alias="家庭电话" type="string" group="基本信息"/>
	<item id="jtdz" alias="家庭地址" type="string" colspan="2" group="基本信息"/>
	<item id="szjd" alias="所在街道" type="string" group="基本信息"/>
	<item id="sztd" alias="所在团队" type="string" group="基本信息"/>
	<item id="szjw" alias="所在居委" type="string" group="基本信息"/>
	<item id="jdr" alias="建档人" type="string" group="基本信息"/>
	<item id="jdrq" alias="建档日期" type="string" group="基本信息"/>
	<item id="jddw" alias="建档单位" type="string" group="基本信息"/>
	
	<item id="zflx" alias="住房类型" type="string" group="家庭现状">
		<dic id="chis.dictionary.roomTypeCode" onlySelectLeaf="true"/>
	</item>
	<item id="jzmj" alias="居住面积" type="string" group="家庭现状"/>
	<item id="rjsr" alias="人均收入" type="string" group="家庭现状">
		<dic id="chis.dictionary.aveIncome" onlySelectLeaf="true"/>
	</item>
	<item id="jtrk" alias="家庭人口" type="string" group="家庭现状"/>
	<item id="xzrk" alias="现住人口" type="string" group="家庭现状"/>
	<item id="rllx" alias="燃料类型" type="string" group="家庭现状">
		<dic id="chis.dictionary.fuelType" onlySelectLeaf="true"/>
	</item>
	<item id="ysly" alias="饮水来源" type="string" group="家庭现状">
		<dic id="chis.dictionary.waterSourceCode" onlySelectLeaf="true"/>
	</item>
	<item id="swfs" alias="上网方式" type="string" colspan="2" group="家庭现状">
		<dic id="chis.dictionary.internetModeCode" onlySelectLeaf="true"/>
	</item>
	<item id="bz" alias="备注" type="string" colspan="3" height="30" xtype="textarea" group="家庭现状"/>
	
	<item id="shr" alias="审核人" type="string" fixed="true" group="审核/质控"/>
	<item id="shrq" alias="审核日期" type="string" fixed="true" xtype="datefield" group="审核/质控"/>
	<item id="shjg" alias="审核结果" type="string" fixed="true" group="审核/质控"/>
	<item id="shbz" alias="审核备注" type="string" colspan="3" height="30" fixed="true" xtype="textarea" group="审核/质控"/>
	
	<item id="zkr" alias="质控人" type="string" fixed="true" group="质控/质控"/>
	<item id="zkrq" alias="质控日期" type="string" fixed="true" xtype="datefield" group="质控/质控"/>
	<item id="zkjg" alias="质控结果" type="string" fixed="true" group="质控/质控"/>
	<item id="zkbz" alias="质控备注" type="string" colspan="3" height="30" fixed="true" xtype="textarea" group="质控/质控"/>
</entry>