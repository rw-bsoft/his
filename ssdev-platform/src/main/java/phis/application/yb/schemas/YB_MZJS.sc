<?xml version="1.0" encoding="UTF-8"?>

<entry alias="医保门诊结算" entityName="YB_MZJS" >
	<!--其他的键根据各地医保返回值添加-->
	<item id="SBXH" alias="识别序号" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="MZXH" alias="MS_MZXX主键关联" type="long" length="18" />
	<item id="FPHM" alias="发票号码" type="string" length="13" />
	<item id="JGID" alias="机构ID" type="string" length="20" />
	<item id="ZFPB" alias="作废判别,1是作废" type="int" length="1" />
</entry>
