<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.ZJ_RECORD" alias="自检信息" sort="Id">
	<item id="Id" alias="自检编号" length="16" width="130"
		type="string" pkey="true" generator="assigned" not-null="1"  >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPIID" length="32"
		type="string" display="0" />
	<item id="zjrq" alias="自检日期" type="date" queryable="true"
		not-null="true" />
	<item id="yqdm" alias="仪器" type="string" width="100" length="200" queryable="true" >
		<dic>
			<item key="01" text="身高体重"/>
			<item key="02" text="人体成份"/>
			<item key="03" text="血糖"/>
			<item key="04" text="骨密度"/>
			<item key="05" text="血压"/>
			<item key="07" text="肺功能"/>
			<item key="08" text="心电图"/>
			<item key="16" text="心血管"/>
			<item key="20" text="血氧"/>
			<item key="21" text="人体脂肪测量仪"/>
			<item key="36" text="体温计"/>
		</dic>
	</item>	
	<item id="yqmc" alias="仪器名称" type="string" width="100" length="200" hidden="true" />
	<item id="xmbh" alias="自测项目" type="string" width="100" length="200" >
		<dic>
			<item key="000001" text="身高"/>
			<item key="000002" text="体重"/>
			<item key="000555" text="体温"/>
			<item key="000003" text="体质指数(BMI)"/>
			<item key="000004" text="收缩压"/>
			<item key="000005" text="舒张压"/>
			<item key="000007" text="脉博数"/>
			<item key="000012" text="心律HR"/>
			<item key="000150" text="参考诊断意见"/>
			<item key="000153" text="RV5"/>
			<item key="000154" text="SV1"/>
			<item key="000155" text="P轴"/>
			<item key="000156" text="QRS轴"/>
			<item key="000157" text="T轴"/>
			<item key="000158" text="QT间期"/>
			<item key="000159" text="QTC间期"/>
			<item key="000160" text="PR间期"/>
			<item key="000161" text="QRS时限"/>
			<item key="000108" text="肺活量(VC)"/>
			<item key="000109" text="用力肺活量(FVC)"/>
			<item key="000111" text="第一秒用力呼气量(FEV1)"/>
			<item key="000112" text="FEV1/FVC(FEV1%G)"/>
			<item key="000118" text="最大通气量(MVV)"/>
			<item key="000152" text="MMF"/>
			<item key="000021" text="Z值"/>
			<item key="000022" text="T值"/>
			<item key="000091" text="声速(SOS)"/>
			<item key="000151" text="OI"/>
			<item key="000024" text="身体水分含量"/>
			<item key="000027" text="体脂肪"/>
			<item key="000123" text="体脂百分比"/>
			<item key="000142" text="除脂体重"/>
			<item key="000028" text="血糖"/>
			<item key="000031" text="每搏心搏出量(SV)"/>
			<item key="000032" text="每分心输出量(CO)"/>
			<item key="000034" text="心脏指数(CI)"/>
			<item key="000037" text="心肌耗氧指数(HOI)"/>
			<item key="000039" text="左心搏功指数(LVWI)"/>
			<item key="000056" text="肺动脉楔压(PAWP)"/>
			<item key="000058" text="肺动脉压(PAP)"/>
			<item key="000060" text="全血粘度(N)"/>
			<item key="000201" text="血氧饱和度"/>
			<item key="000211" text="脂肪含量"/>
			<item key="000212" text="体质指数"/>
			<item key="000213" text="体型判断"/>
		</dic>
	</item>
	<item id="xmdw" alias="项目单位" type="string" width="100" length="200" />		
	<item id="jg" alias="结果" type="string" width="100" length="200" />	
	<item id="ckfw" alias="參考范围" type="string" width="100" length="200" />	
	<item id="jcrq" alias="检测日期" type="date" queryable="true"
		not-null="true" />
    <item id="orgcode" alias="检测地点" type="string" width="100" length="50" >
   	 <dic id="chis.@manageUnit" showWholeText="true" includeParentMinLen="6" render="Tree"/>
    </item>	
</entry>
