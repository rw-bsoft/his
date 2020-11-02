<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="gp.application.spi.schemas.SPI_SpecialPeopleLNRSignRecord" alias="特殊人群-残疾人实际签约人数">
	<item id="PHRID" alias="健康档案号" length="30" width="130"/>
	<item id="EMPIID" alias="empiId" length="30" width="120" display="0"/>
	<item id="CJRID" alias="残疾人档案主键" length="18" width="120" display="0"/>
	<item id="incomeSource" alias="收入" length="2" width="120" display="0"/>
	<item id="PERSONNAME" alias="姓名" type="string" length="20"/>
	<item id="IDCARD" alias="身份证号" type="string" length="20" width="160" />
	<item id="BIRTHDAY" alias="出生日期" type="date" width="75"/>
	<item id="AGE" alias="年龄" type="string" length="10" width="60"/>
	<item id="SEXCODE" alias="性别" type="string" length="1" width="40"  defalutValue="9">
		<dic id="gp.dictionary.gender"/>
	</item>
	<item id="REGIONCODE" alias="网格地址" type="string" length="25" width="200" colspan="2" anchor="100%" update="false">
		<dic id="gp.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="CJRLB" alias="残障类别" type="string" length="1" display="0">
		<dic>
			<item key="1" text="肢体"/>  
			<item key="2" text="脑瘫"/>  
			<item key="3" text="智力"/> 
		</dic>
	</item>
	<item id="MANAUNITID" alias="管辖机构" type="string" not-null="true" length="20" colspan="2" anchor="100%" width="180"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="gp.@manageUnit" showWholeText="true" includeParentMinLen="6" render="Tree"/>
	</item>
	<item id="PHONENUMBER" alias="家庭电话" type="string" length="20"/>
	<item id="MANADOCTORID" alias="责任医生" type="string" length="20" not-null="true" update="false">
		<dic id="gp.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
</entry>
