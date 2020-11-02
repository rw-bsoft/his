<?xml version="1.0" encoding="UTF-8"?>

<entry alias="病人医保卡信息" entityName="YB_YBKXX" >
	<!--其他的键根据各地医保卡返回值添加-->
 	<item id="SBXH" alias="识别序号" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
 
	<!--<item id="CARDTYPE" alias="卡类型" type="string" length="1" defaultValue="0">
		<dic>
			<item key="0" text="磁卡"/>
			<item key="1" text="保障卡"/>
		</dic>
	</item>-->
	
	<item id="BRID" alias="病人ID" type="long" colspan="1" length="18" display="0"/>
	<item id="BRXM" alias="病人姓名" type="string" colspan="1" length="28"/>
	<item id="BRXB" alias="性别" type="string" colspan="1" length="28"/>
	<item id="BRNL" alias="年龄" type="string" colspan="1" length="28"/>
	<item id="ICKH" alias="IC卡号" type="string" colspan="1" length="20"/>
	<item id="CBH" alias="参保号" type="string" colspan="1" length="20"/>
	<item id="DWMC" alias="单位名称" type="string" colspan="1" length="20"/>
	<item id="ICKZT" alias="IC卡状态" type="string" colspan="1" length="10"/>
	<item id="GZZT" alias="工作状态" type="string" colspan="1" length="20"/>
	<item id="DQMC" alias="地区名称" type="string" colspan="1" length="20"/>
	<item id="FZXMC" alias="分中心名称" type="string" colspan="1" length="20"/>
	<item id="ZHYE" alias="帐户余额" type="double" colspan="1" length="10"/>
</entry>
