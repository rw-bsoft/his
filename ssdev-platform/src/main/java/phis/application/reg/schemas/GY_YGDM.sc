<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_Personnel" alias="员工代码">
	<item id="ORGANIZCODE" alias="所属机构" length="50" fixed="true" width="120" display="1" >
		<dic id="phis.@manageUnit"/>
	</item>
	<item id="OFFICECODE" alias="所属科室" type="long" length="18"  display="1">
		<dic id="phis.dictionary.department" autoLoad="true"/>
	</item>
	<item id="PERSONID" alias="员工工号" type="string" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" startPos="1"/>
		</key>
	</item>
	<!--<item id="YGBH" alias="员工编号"  type="string" length="10" not-null="1" update="false"/>
	-->
	<item id="PERSONNAME" alias="员工姓名" type="string" length="50" not-null="1"/>
	<item id="GENDER" alias="员工性别" type="string" length="4" defaultValue="1">
		<dic id="phis.dictionary.gender" />
	</item>
	<item id="PHOTO"  xtype="imagefield" type="string"
		display="2" rowspan="4" />
	<item id="BIRTHDAY" alias="出生年月" display="2" type="date"/>
	<!--<item id="YGZW" alias="员工职务" type="int" length="4" defaultValue="8">
		<dic id="phis.dictionary.post" />
	</item>-->
	<!--<item id="YGJB" alias="员工级别" type="int" length="4">
		<dic id="phis.dictionary.rank" />
	</item>-->
	<item id="PYCODE" alias="拼音码"   type="string" length="10" queryable="true" target="YGXM" codeType="py"/>
	<item id="WBCODE" alias="五笔码" display="2" type="string" length="10" target="YGXM" codeType="wb"/>
	<item id="JXCODE" alias="角型码"  display="2" type="string" length="10" target="YGXM" codeType="jx"/>
	<item id="QTCODE" alias="其他码"  display="2" type="string" length="10"/>
	<item id="PRESCRIBERIGHT" alias="开处方权" display="2" type="string" length="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="NARCOTICRIGHT" alias="麻醉药权" display="2" type="string" length="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="PSYCHOTROPICRIGHT" alias="精神药权" display="2" type="string" length="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="ISEXPERT" alias="专家判别" display="2" type="int" length="1" defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="EXPERTCOST" alias="专家费用" type="double" length="11" precision="2" maxValue="999999.99" fixed="true" defaultValue="0"/>
	<item id="LOGOFF" alias="禁用标志" type="int" display="1" length="1" defaultValue="0" renderer="onRendererZFPB">
		<dic>
			<item key="0" text="正常"/>
			<item key="1" text="禁用"/>
		</dic>
	</item>
	<item id="JZSJ_SW" alias="截止时间上午" display="2"  type="date" hidden="true"/>
	<item id="JZSJ_XW" alias="截止时间下午" display="2"  type="date" hidden="true"/>
	<!--<item id="YGMM" alias="员工密码"  type="string" length="50" display="0"/>-->
	<item id="ANTIBIOTICRIGHT" alias="抗生素用权" display="0"  length="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="MOBILE" alias="手机号码" display="2"  type="string" length="20"/>
	<item id="EMAIL" alias="邮箱地址" display="0"  type="string" length="60"/>
	<!--<item id="YGQM" alias="员工签名" display="0" />
	<item id="YSJJ" alias="医生简介" display="2" type="string" colspan="2" anchor="100%"/>-->
	<item id="CARDNUM" alias="身份证号" display="2" type="string" colspan="2" anchor="100%" length="20"/>
</entry>
